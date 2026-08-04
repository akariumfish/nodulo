package data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import data.sPool.State;
import util.Utl;
import util.nMap;
import zz_applet.Applet;

public abstract class sPool <T extends sPoolable> {
	
	
	public enum State { FREE, LOADING, CREATING, INIT, LOAD, USED, CLEARING, DELAYED };
	
	
	
	public abstract T newObject();
	public abstract T[] newArray(int l);
	
	
	
	public static <T extends sPoolable> void draw_pool(Applet app, sPool<T> pool, float x, float y, float w, float h) {
		if (pool.tab == null) return;
//		pool.save();
		for (int i = 0 ; i < pool.tab.width() ; i++) {
			for (int j = 0 ; j < pool.tab.height(i) ; j++) {
				app.text(pool.tab.getGenericString(i,j), i * w, -j * h, h*0.5f);
			}
		}
	}

	
	
	private int get_index(T t) {
		for (int i = 0 ; i < objects.length ; i++)
			if (objects[i] == t) return i;
		return -1;
	}
	
	public Applet app;
	public sTab tab;
	public String name;

	public int capacity = 0;
	public int prefered_capacity = 20;
	public int expending_capacity = 20;
	public int max = 2000;
	
	public T[] objects = null;
	public boolean[] use = null;
	public int last_use = 0;
	
	private void calc_last_use() { last_use = 0;
		for (int i = 0 ; i < use.length ; i++) if (use[i]) last_use = i; }

//	public long delay = 500;
//	private HashMap<T, Long> delayedObjects;
	
	public nMap<T> all_used = new nMap<T>();
	
	public sPool (Applet a, sTab t, String n) {//pSpace s,
		app = a; tab = t; name = n; //space = s; 
//		delayedObjects = new HashMap<T, Long>();
		if (t != null && t.width() > prefered_capacity) capacity = t.width();
		else capacity = prefered_capacity;
		resize();
	}

	public void freeAll() {
		for (T b : Utl.duplic(all_used.all())) b.clear();
		if (all_used.size() > 0) {
			for (T b : Utl.duplic(all_used.all())) b.clear();
			if (all_used.size() > 0)
				app.logn("ERROR: sPool freeAll did not free all objects");
		}

	}

	public void clear() {
		freeAll();
		capacity = prefered_capacity;
		resize();
	}

	public void resize() {
		if (use != null) {
			for (int i = 0 ; i < use.length ; i++) 
				if (use[i]) free(objects[i]);
			for (int i = 0 ; i < use.length ; i++) { 
				objects[i].clear(); 
				objects[i] = null; 
			}
		}
		objects = newArray(capacity);
		use = new boolean[capacity];
		for (int i = 0 ; i < capacity ; i++) {
			use[i] = false;
			objects[i] = newObject();
			objects[i].setup(this, i);
		}
	}

	public void expendTo(int r) {
		if (r < capacity) return;
		int expend = r - capacity;
		int mod = expend%expending_capacity;
		int comp = expending_capacity - mod;
		expend += comp;
		
		if (capacity + expend > max) {
			app.logn("ERROR : sPool is full");
			return;
		}
		T[] ps = newArray(capacity + expend);
		boolean[] us = new boolean[capacity + expend];
		for (int i = 0 ; i < capacity ; i++) {
			us[i] = use[i];
			ps[i] = objects[i]; }
		for (int i = capacity ; i < capacity + expend ; i++) {
			us[i] = false;
			ps[i] = newObject();
			ps[i].setup(this, i); }
		use = us; objects = ps; capacity += expend;
	}

	public void expend() {
		if (capacity + expending_capacity > max) {
			app.logn("ERROR : sPool is full");
			return;
		}
		T[] ps = newArray(capacity + expending_capacity);
		boolean[] us = new boolean[capacity + expending_capacity];
		for (int i = 0 ; i < capacity ; i++) {
			us[i] = use[i];
			ps[i] = objects[i]; }
		for (int i = capacity ; i < capacity + expending_capacity ; i++) {
			us[i] = false;
			ps[i] = newObject();
			ps[i].setup(this, i); }
		use = us; objects = ps; capacity += expending_capacity;
	}

	public void save() {
		if (tab == null) return;
		for (int i = 0 ; i < objects.length ; i++) if (use[i]) {
			objects[i].do_save(); }
		calc_last_use();
		if (all_used.size() > 0) {
//			int max_bod_size = 0;
//			for (T b : all_used.all()) 
//				max_bod_size = Math.max(max_bod_size, b.data_size());
			tab.setWidth(last_use + 1);
//			tab.resize(last_use + 1, max_bod_size);
			for (int i = 0 ; i < tab.width() ; i++) {
				if (use[i]) {
					objects[i].do_save();
					tab.setRowHeight(i,objects[i].data_size());
					objects[i].to_tab(tab, i);
				} else {
					tab.setRowHeight(i,0);
//					tab.set(i,0,false);
				}
			}
		} else {
			tab.setWidth(0);
//			tab.resize(0,0);
		}
	}
	

	public T load_tab_row(sTab t, int r) {
		T p = null;
		for (int i = 0 ; i < capacity ; i++) if (!use[i] && objects[i].state == State.FREE) {
			p = objects[i]; use[i] = true; break; }
		if (p == null) {
			int free_id = capacity;
			expend();
			if (free_id >= capacity) return null;
			p = objects[free_id];
			use[free_id] = true; }
		all_used.put(p.pool_ref, p);
		p.load_poolable();
		p.from_tab(t, r);
		p.do_init();
		p.do_load();
		return p;
	}

	public void load() {
		freeAll();
		if (tab == null) return;
		if (tab.width() > prefered_capacity) capacity = tab.width();
		else capacity = prefered_capacity;
		resize();
		for (int i = 0 ; i < tab.width() ; i++) {
			if (tab.height(i) > 0 && tab.getBool(i, 0)) {
				use[i] = true;
				all_used.put(objects[i].pool_ref, objects[i]);
				objects[i].load_poolable();
				objects[i].from_tab(tab, i);
			}
		}
		for (int i = 0 ; i < tab.width() ; i++)  
			if (use[i]) {
				objects[i].do_init();
			}
		for (int i = 0 ; i < tab.width() ; i++) 
			if (use[i]) {
				objects[i].do_load();
			}
		
	}

	public void load_1() {
		freeAll();
		if (tab == null) return;
		if (tab.width() > prefered_capacity) capacity = tab.width();
		else capacity = prefered_capacity;
		resize();
		for (int i = 0 ; i < tab.width() ; i++) {
			if (tab.height(i) > 0 && tab.getBool(i, 0)) {
				use[i] = true;
				all_used.put(objects[i].pool_ref, objects[i]);
				objects[i].load_poolable();
				objects[i].from_tab(tab, i);
			}
		}
	}

	public void load_2() {
		for (int i = 0 ; i < tab.width() ; i++)  
			if (use[i]) {
				objects[i].do_init();
			}
		
	}

	public void load_3() {
		for (int i = 0 ; i < tab.width() ; i++) 
			if (use[i]) {
				objects[i].do_load();
			}
		
	}
	
	
	

	public ArrayList<T> temp_all() {
		ArrayList<T> tmp = new ArrayList<T>();
		for (T t : all_used.all()) tmp.add(t); return tmp;  }
	
	public ArrayList<T> all() {
		return all_used.all(); }
	
	public Set<String> allKey() {
		return all_used.allKey(); }

	public int size() { return all_used.size(); }
	public int capacity() { return capacity; }

	public T get(String r) {
		return all_used.get(r);
	}

	public T get(int r) {
		return all_used.get(r);
	}

	public T getObjectAt(int r) {
		if (r > objects.length) expendTo(r);
		return objects[r];
	}

	public T get_any() {
		if (all_used.size() > 0) return all_used.get(0);
		return null;
	}

//	private T obtainDelayed() {
//		long time = System.currentTimeMillis();
//		T t = null;
//		for (Map.Entry<T,Long> me : delayedObjects.entrySet()) 
//			if (time - me.getValue() > delay) {
//				t = me.getKey(); break; }
//		if (t != null) {
//			int ind = get_index(t);
//			if (ind == -1 || use[ind]) {
//				app.log("WARNING : sPool obtainDelayed() : object in use in delayed map");
//				return null;
//			}
//			long l = delayedObjects.get(t);
//			delayedObjects.remove(t,l); 
//			use[ind] = true; }
//		return t;
//	}

	public T obtain_uninit() {
		T p = null;//obtainDelayed();
		if (p == null) for (int i = 0 ; i < capacity ; i++) 
			if (!use[i] && objects[i].state == State.FREE) {
				p = objects[i]; use[i] = true; break; }
		if (p == null) {
			int free_id = capacity;
			expend();
			if (free_id >= capacity) return null;
			p = objects[free_id];
			use[free_id] = true; }
		all_used.put(p.pool_ref, p);
		p.load_poolable();
		return p;
	}

	public T obtain(int id) {
		if (id >= capacity) expendTo(id);
		T p = null;
		if (use[id]) {
			app.logn("WARNING : sPool obtain(int) : object allready in use "+objects[id].pool_ref);
			free(objects[id]);
		}
		p = objects[id];  use[id] = true;
		all_used.put(p.pool_ref, p);
		p.create_poolable();
		return p;
	}

	public T obtain() {
		T p = null;//obtainDelayed();
		if (p == null) for (int i = 0 ; i < capacity ; i++) 
			if (!use[i] && objects[i].state == State.FREE) {
				p = objects[i]; use[i] = true; break; }
		if (p == null) {
			int free_id = capacity;
			expend();
			if (free_id >= capacity) return null;
			p = objects[free_id];
			use[free_id] = true; }
		all_used.put(p.pool_ref, p);
		p.create_poolable();
		return p;
	}

	public void free(sPoolable p) {
		if (p.state != State.FREE && p.state != State.CLEARING) {// && p.state != State.DELAYED
			if (p.pool == this) {
				if (!(objects[p.pool_index] == p)) 
					app.logn("ERROR : sPool try to free an object not from his list");
				else {
					if (!(all_used.hasVal((T)p) && all_used.get(p.pool_ref) == p)) {
						if (all_used.hasVal((T)p) && !(all_used.get(p.pool_ref) == p))
							app.logn("ERROR : sPool try to free an object badly referenced");
					} else {
						p.state = State.CLEARING;
						p.clear();
						all_used.remove(p.pool_ref, (T)p);
						use[p.pool_index] = false;
						p.state = State.FREE;
//						if (!delayedObjects.containsKey(p)) {
//							p.state = State.DELAYED;
//							long time = System.currentTimeMillis();
//							delayedObjects.put((T)p, time);
//						}
					}
				}
			} else {
				app.logn("ERROR : sPool try to free an object with another pool");
			}
		}
	}
}
