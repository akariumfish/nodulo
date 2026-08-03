package util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;

import app.Applet;

public abstract class nPool<T> {

	/** The maximum number of objects that will be pooled. */
	public final int max;
	/** The highest number of free objects. Can be reset any time. */
	public int peak;
	
	public long delay = 500;
	private HashMap<T, Long> delayedObjects;
	
	private final Array<T> freeObjects;

	ArrayList<T> allObjects = new ArrayList<T>();
	
	/** Creates a pool with an initial capacity of 16 and no maximum. */
	public nPool () {
		this(16, Integer.MAX_VALUE);
	}

	/** Creates a pool with the specified initial capacity and no maximum. */
	public nPool (int initialCapacity) {
		this(initialCapacity, Integer.MAX_VALUE);
	}

	/** @param initialCapacity The initial size of the array supporting the pool. No objects are created/pre-allocated. Use
	 *           {@link #fill(int)} after instantiation if needed.
	 * @param max The maximum number of free objects to store in this pool. */
	public nPool (int initialCapacity, int max) {
		freeObjects = new Array(false, initialCapacity);
		delayedObjects = new HashMap<T, Long>();
		this.max = max;
//		fill(initialCapacity);
	}

	abstract protected T newObject ();

	public ArrayList<T> all() {
		return allObjects; }
	public ArrayList<T> tmp_all() {
		ArrayList<T> arr = new ArrayList<T>();
		for (T t : allObjects) arr.add(t);
		return arr; }

	public int size() {
		return allObjects.size(); }

	public T get(int i) {
		return allObjects.get(i); }

	private T obtainDelayed() {
		long time = System.currentTimeMillis();
		T t = null;
		for (Map.Entry<T,Long> me : delayedObjects.entrySet()) 
			if (time - me.getValue() > delay) {
				t = me.getKey(); break; }
		if (t != null) {
			long l = delayedObjects.get(t);
			delayedObjects.remove(t,l); }
		return t;
	}

	/** Returns an object from this pool. The object may be new (from {@link #newObject()}) or reused (previously
	 * {@link #free(Object) freed}). */
	public T obtain () {
		T t = obtainDelayed();
		if (t == null) t = freeObjects.size == 0 ? newObject() : freeObjects.pop();
		allObjects.add(t);
		if (t instanceof Poolable) ((Poolable)t).pool_init();
		return t;
	}
	
	/** Adds the specified number of new free objects to the pool. Usually called early on as a pre-allocation mechanism but can be
	 * used at any time.
	 *
	 * @param size the number of objects to be added */
	public void fill (int size) {
		for (int i = 0; i < size; i++)
			if (freeObjects.size + delayedObjects.size() < max) 
				freeObjects.add(newObject());
		peak = Math.max(peak, freeObjects.size + delayedObjects.size());
	}

	/** Called when an object is freed to clear the state of the object for possible later reuse. The default implementation calls
	 * {@link Poolable#pool_reset()} if the object is {@link Poolable}. */
	protected void reset (T object) {
		if (object instanceof Poolable) ((Poolable)object).reset();
	}

	/** Called when an object is discarded. This is the case when an object is freed, but the maximum capacity of the pool is
	 * reached, and when the pool is {@link #clearFreeObjs() cleared} */
	protected void discard (T object) {
		reset(object);
	}

	/** Puts the specified object in the pool, making it eligible to be returned by {@link #obtain()}. If the pool already contains
	 * {@link #max} free objects, the specified object is {@link #discard(Object) discarded}, it is not reset and not added to the
	 * pool.*/
	public void free (T object) {
		if (object == null) throw new IllegalArgumentException("object cannot be null.");
		if (freeObjects.contains(object, true)) return; 
//		for (Map.Entry<T,Long> me : delayedObjects.entrySet()) 
//			if (me.getKey() == object) return;
		if (delayedObjects.containsKey(object)) return;
		
		if (allObjects.contains(object)) {
			while (allObjects.contains(object)) allObjects.remove(object);
			if (freeObjects.size + delayedObjects.size() < max) {
//				freeObjects.add(object);
				long time = System.currentTimeMillis();
				delayedObjects.put(object, time);
				peak = Math.max(peak, freeObjects.size + delayedObjects.size());
				reset(object);
			} else
				discard(object);
		}
	}
	
	public void freeAll() {
		for (T t : Utl.duplic(allObjects)) free(t);
//		for (int i = allObjects.size() - 1; i >= 0 ; i--)
//			free(allObjects.get(i));
		allObjects.clear();
	}
	
	/** Removes and discards all free objects from this pool. */
	public void clearFreeObjs () {
		Array<T> freeObjects = this.freeObjects;
		for (int i = 0, n = freeObjects.size; i < n; i++)
			discard(freeObjects.get(i));
		freeObjects.clear();
		for (Map.Entry<T,Long> me : delayedObjects.entrySet()) 
			discard(me.getKey());
		delayedObjects.clear();
	}

	public void dispose() {
		freeAll();
		clearFreeObjs(); // reset then clear free objects
	}

	/** The number of objects available to be obtained. */
	public int getFree () {
		return freeObjects.size + delayedObjects.size();
	}

	/** Objects implementing this interface will have {@link #pool_reset()} called when passed to {@link Pool#free(Object)}. */
	static public interface Poolable {
		/** Resets the object for reuse. Object references should be nulled and fields may be set to default values. */
		public void reset ();
		public void pool_init ();
	}

}
