package util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;

import app.Applet;

public abstract class nQuickPool<T> {

	private final Array<T> freeObjects;
	private int free_nb = 0;

	ArrayList<T> allObjects = new ArrayList<T>();

	public nQuickPool() {
		freeObjects = new Array<T>();
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

	public int capacity() {
		return freeObjects.size; }

	public T get(int i) {
		return allObjects.get(i); }


	public T obtain () {
		T t = freeObjects.size == 0 ? newObject() : freeObjects.pop();
		allObjects.add(t);
		if (t instanceof nPool.Poolable) ((nPool.Poolable)t).pool_init();
		return t;
	}
	
	protected void reset (T object) {
		if (object instanceof nPool.Poolable) ((nPool.Poolable)object).reset();
	}

	protected void discard (T object) {
		reset(object);
	}

	public void free(T t) {
		reset(t); 
		freeObjects.add(t); 
		allObjects.remove(t);
	}
	
	public void freeAll() {
		for (T t : Utl.duplic(allObjects)) {
			reset(t); freeObjects.add(t); }
		allObjects.clear();
	}
	
	public void dispose() {
		freeAll();
		Array<T> freeObjects = this.freeObjects;
		for (int i = 0, n = freeObjects.size; i < n; i++)
			discard(freeObjects.get(i));
		freeObjects.clear();
	}

}
