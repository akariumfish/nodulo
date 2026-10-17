package util;

import java.lang.annotation.*;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ServiceLoader;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

public class U {
	
	
	
	

	public static void TEST() {
		
		
		
		
		
		// Thread safe collection :
		List<String> syncCollection = Collections.synchronizedList(Arrays.asList("a", "b", "c"));
		List<String> uppercasedCollection = new ArrayList<>();
		
		Runnable listOperations = () -> { // Lambda expression
		    synchronized (syncCollection) {
		        syncCollection.forEach((e) -> {
		            uppercasedCollection.add(e.toUpperCase());
		        });
		    }
		};
		
		
		
	}
	
	
	
	
	public interface Indexed <K extends Indexed<K>> {
		public int index();
		public void setIndex(int i);
		public Indexor<K> indexor();
		public void setIndexor(Indexor<K> i);
		public K find(int id);
	}

	public interface Indexor <K extends Indexed<K>> {
		public K find(int id);
	}

	public interface Makeable { public void make(Object...a); public void clear(); }

	
	
	
	
	
	
	
	
	
	public interface Supplier <K> { K alloc(); K[] array(int l); Collec<K> collec(); }

	public interface Collec <K> extends Iterator<K> {
		public int len();
		public void add(K k);
		public K first();
		public K last();
		public K removeFirst();
		public K removeLast();
		public K removeIndex(int i);
		public K removeValue(K k);
	}

	public static class Pool <K> {
		final Collec<K> used, free;
		final Supplier<K> supplier;
		Pool(Supplier<K> _supplier) { supplier = _supplier; 
			used = supplier.collec(); free = supplier.collec(); }
		K take() { 
			used.add(free.len() > 0 ? free.removeLast() : supplier.alloc()); 
			return used.last(); }
		void put(K k) { free.add(used.removeValue(k)); }
		int _used() { return used.len(); }
		int _free() { return free.len(); }
	}
	
	public static class PoolCollec <K> implements Collec<K> {
		final Supplier<K> supplier;
		PoolCollec(Supplier<K> _supplier) { supplier = _supplier; }

		@Override public boolean hasNext() { return false; }
		@Override public K next() { return null; }
		@Override public int len() { return 0; }
		@Override public void add(K k) { }
		@Override public K first() { return null; }
		@Override public K last() { return null; }
		@Override public K removeFirst() { return null; }
		@Override public K removeLast() { return null; }
		@Override public K removeIndex(int i) { return null; }
		@Override public K removeValue(K k) { return null; }
		
	}
	
	public static class QuadF extends Pool<float[]>{

		static { new QuadF(); }
		
		QuadF() { super(new Supplier<float[]>() {
			@Override public float[] alloc() { return new float[4]; }
			@Override public float[][] array(int l) { return new float[l][4]; }
			@Override public Collec<float[]> collec() { 
				return new PoolCollec<float[]>(this); } }); 
			pool = this; }
		
		static QuadF pool;
		static float[] obtain() { return pool.take(); }
		static void free(float[] k) { pool.put(k); }
		static int used() { return pool._used(); }
		static int free() { return pool._free(); }
	}
	
	
	
	

//	@Override public Iterator<nRun> iterator() { return new RunIterator(); }
//		
//	class RunIterator implements Iterator<nRun> {
//		private int cnt = 0;
//		@Override public boolean hasNext() { return cnt < size(); }
//		@Override public nRun next() { cnt++; return get(cnt-1); }
//	}
//	
	

	
	
	
	
	
	abstract class Twin<K> {
		public abstract K newObj(Object...arg);
		private final K objA, objB;
		public Twin(Object...arg) { objA = newObj(arg); objB = newObj(arg); }
		private boolean flip = false, left_lock = false, right_lock = false;
		public K left() { left_lock = true; return flip ? objA : objB; }
		public K right() { right_lock = true; return flip ? objB : objA; }
		public void free_left() { left_lock = false; }
		public void free_right() { right_lock = false; }
		public boolean isFree() { return !right_lock && !left_lock; }
		public void turn() { if (isFree()) flip = !flip; }
	}
	
	abstract class Run {
		public abstract void run();
		private final Object[] conf;
		private Object[] args;
		public Run(Object..._conf) { conf = _conf; }
		public void exec(Object...a) { args = a; run(); }
		@SuppressWarnings("unchecked")
		public <K> K arg(int i, Class<K> cl) { return (K)args[i]; }
		@SuppressWarnings("unchecked")
		public <K> K conf(int i, Class<K> cl) { return (K)conf[i]; }
	}
	
	
	
	
	
	
	
	

	//-------------------------------------------------
	//--					SETUP						--
	//-------------------------------------------------

	//in Main do new U() to run static block

	static { build(); }

	public static void build() {
//		TEST(); //TODO to del
		
		//TODO //Prepare Primitive
//		Tag.build(); //Prepare tags (unique int id)
//		Txt.build(); //Load Texts
		//TODO //Load Config
		for (Setup p : ServiceLoader.load(Setup.class)) p.alloc();
	}
	
	public static interface Setup { public void alloc(); }

	// to run static block at setup
	public static class SetupModule implements Setup {
		public SetupModule() {}
		@Override public void alloc() { new SetupModule(); }
		static {
			
		}
	}
	
	public static Applet app;
	
	public static class Applet {
		
		Module[] mod;
		public Applet() {
			app = this;
			ServiceLoader<Module> services = ServiceLoader.load(Module.class);
			int i = 0; for (Module m : services) i++;
			mod = new Module[i];
			i = 0; for (Module m : services) { mod[i] = m.alloc(); i++; }
			
		}
		
	}
	
	public static abstract class Module {
		public abstract <K extends Module> K alloc(); 
		public abstract void make(); public abstract void finish(); 
	}

//	//-------------------------------------------------
//	//--					TAGS							--
//	//-------------------------------------------------
//
//	public static class TaggedModule implements Tag.Creator {
//		@Tag.Tagged public static final int TAGGED_FIELD = Tag.newTag();
//		
//		public TaggedModule() {}
//		@Override public void loadClass() { new TaggedModule(); }
//		@Override public Class<?> getTaggedClass() { return TaggedModule.class; }
//	}
//
//	// for automatic unique global final int key
//	public static class Tag {
//
//		public static interface Creator { 
//			public void loadClass(); // should instanciate one to run statics
//			public Class<?> getTaggedClass(); }
//
//		@Documented @Target(ElementType.FIELD) @Inherited @Retention(RetentionPolicy.RUNTIME) 
//		public @interface Tagged {}
//		
//		public static int newTag() { cnt++; return cnt-1; }
//		
//		private static int cnt = 0;
//		private static IntStringMap map;
//		public static void build() {
//			
//			//TODO map = new Map()
//			if (map == null) return;
//			ServiceLoader<Creator> services = ServiceLoader.load(Creator.class);
//			for (Creator p : services) p.loadClass();
//			for (Creator p : services) {
//				Class<?> clazz = p.getTaggedClass();
//				for (Field f : clazz.getFields()) {
//					Annotation annotation = f.getDeclaredAnnotation(Tagged.class);
//					if (annotation != null ) {
//						try { map.put(f.getInt(null), f.getName()); } 
//						catch (IllegalArgumentException | IllegalAccessException e) {
//							e.printStackTrace(); }
//					}
//				}
//			}
//		}
//		public static int key(String tag) { return map.key(tag); }
//		public static String tag(int key) { return map.val(key); }
//	}
//	
//	//TODO for storing Tags
//	public interface IntStringMap {
//		public void put (int key, String val);
//		public int key(String v);
//		public String val(int k);
//		public boolean hasKey(int k);
//		public boolean hasVal(String v);
//		public int size();
//	}
	
	//-------------------------------------------------
	//--					TEXTS						--
	//-------------------------------------------------

	public static class Txt {
		
		public static final String TEXT_FILE_PATH = "alltext.txt";

		private static FileHandle fl;
		private static String tmp = "";
		
		public static void build() {
			fl = Gdx.files.local(TEXT_FILE_PATH);
			if (!fl.exists()) { fl.writeString("void void\n", false); }
			String file = fl.readString();
			String[] line = file.split("[\\n]");
			for (String l : line) {
				String[] words = l.split("[\\s]");
				if (words.length == 0) continue;
				tmp = ""; for (int i = 1 ; i < words.length ; i++) {
					tmp += words[i]; if (i < words.length - 1) tmp += " "; }
				map.put(words[0], Utl.copy(tmp));
			}
		}
		
		private static final nMap<String> map = new nMap<String>();
		
		public static String t(String ref, String def) {
			if (!map.hasKey(ref)) {
				fl.writeString(ref+" "+def+"\n",true);
				map.put(ref,def);
				return def; }
			return map.get(ref);
		}

		public static String t(String ref) {
			if (!map.hasKey(ref)) { Utl.logn("ERROR : T.t( "+ref+" ) dont exist"); return ""; }
			return map.get(ref);
		}
		

//		private static void replace(String...data) {
//			if (data == null) return; tmp = "";
//			for (String s : data) tmp += s+'\n';
//			fl.writeString(tmp,false);
//		}
	//
//		private static void append(String...data) {
//			if (data == null) return; tmp = "";
//			for (String s : data) tmp += s+'\n';
//			fl.writeString(tmp,true);
//		}

	}
	
	
	
	
	
	

	//-------------------------------------------------
	//--					CONSTANTS					--
	//-------------------------------------------------

	public static final float PI = (float)Math.PI;
	public static final float HPI = (float)Math.PI / 2f;
	public static final float TPI = (float)Math.PI / 3f;
	public static final float TDPI = (float)Math.PI * 2f / 3f;
	public static final float DPI = (float)Math.PI * 2f;
	
	public static float revAngle(float r) { return (r + PI) < PI ? (r + PI) : (r - PI); }
	
	
	public static float abs(float r) { return Math.abs(r); }
	
	
	
	

	

	//-------------------------------------------------
	//--					METHODS						--
	//-------------------------------------------------

	/**
	 * Get the method name for a depth in call stack. <br />
	 * Utility function
	 * @param depth depth in the call stack (0 means current method, 1 means call method, ...)
	 * @return method name
	 */
	public static String getMethodName(final int depth)
	{
	  final StackTraceElement[] ste = Thread.currentThread().getStackTrace();
	  if (ste.length <= 2 + depth) return ""; 
	  return ste[2 + depth].getMethodName(); 
	}
	
	
	
	
	
	
	
	
	
	
}
