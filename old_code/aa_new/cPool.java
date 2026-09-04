package aa_new;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import util.Utl;
import util.iMap;
import app.App;
import data.sTab;

public abstract class cPool <T extends cPoolable> {
	
	
	public enum State { FREE, USED, CLEARING };
	
	
	
	public abstract T newObject();
	public abstract T[] newArray(int l);
	
	
	
	public int capacity = 0;
	public int prefered_capacity = 20;
	public int expending_capacity = 20;
	public int max = 2000;
	
	public T[] objects = null;
	public boolean[] use = null;

	public iMap<T> all_used = new iMap<T>();
	
	public cPool (int start_capacity, int expend_capacity) {
		prefered_capacity = start_capacity;
		expending_capacity = expend_capacity;
		capacity = prefered_capacity;
		build();
	}

	public void freeAll() {
		for (T b : Utl.duplic(all_used.all())) b.clear();
		if (all_used.size() > 0) {
			for (T b : Utl.duplic(all_used.all())) b.clear();
			if (all_used.size() > 0)
				Utl.logn("ERROR: sPool freeAll did not free all objects");
		}

	}

	public void clear() {
		freeAll();
		capacity = prefered_capacity;
		build();
	}

	public void dispose() {
		capacity = 0;
		build();
	}

	public void build() {
		if (use != null) {
			freeAll();
			for (int i = 0 ; i < use.length ; i++) { 
				objects[i].dispose(); 
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

	public void expend() {
		if (capacity + expending_capacity > max) {
			Utl.logn("ERROR : sPool is full");
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

	
	

	public ArrayList<T> temp_all() {
		ArrayList<T> tmp = new ArrayList<T>();
		for (T t : all_used.all()) tmp.add(t); return tmp;  }
	
	public ArrayList<T> all() {
		return all_used.all(); }
	
	public Set<Integer> allKey() {
		return all_used.allKey(); }

	public int size() { return all_used.size(); }
	public int capacity() { return capacity; }

	public T get(int r) {
		return objects[r];
	}

	public T obtain() {
		T p = null;
		for (int i = 0 ; i < capacity ; i++) 
			if (!use[i] && objects[i].state == State.FREE) {
				p = objects[i]; use[i] = true; break; }
		if (p == null) {
			int free_id = capacity;
			expend();
			if (free_id >= capacity) return null;
			p = objects[free_id];
			use[free_id] = true; }
		all_used.put(p.id, p);
		p.do_init();
		return p;
	}

	public void free(cPoolable p) {
		if (p.state != State.FREE && p.state != State.CLEARING) {
			if (p.pool == this) {
				if (!(objects[p.id] == p)) 
					Utl.logn("ERROR : cPool try to free an object not from his list");
				else {
					if ((!(all_used.hasVal((T)p) && all_used.get(p.id) == p)) ||
						(all_used.hasVal((T)p) && !(all_used.get(p.id) == p))) {
							Utl.logn("ERROR : cPool try to free "
									+ "an object badly referenced");
					} else {
						p.state = State.CLEARING;
						p.clear();
						all_used.remove(p.id, (T)p);
						use[p.id] = false;
						p.state = State.FREE;
					}
				}
			} else {
				Utl.logn("ERROR : cPool try to free an object with another pool");
			}
		}
	}
	
	
	
	
	
	
//	public int last_use = 0;
//	private void calc_last_use() { last_use = 0;
//		for (int i = 0 ; i < use.length ; i++) if (use[i]) last_use = i; }
//
//	public void save() {
//		for (int i = 0 ; i < objects.length ; i++) if (use[i]) {
//			objects[i].do_save(); }
//		
//		if (tab == null) return;
//		
//		calc_last_use();
//		if (all_used.size() > 0) {
////			int max_bod_size = 0;
////			for (T b : all_used.all()) 
////				max_bod_size = Math.max(max_bod_size, b.data_size());
//			tab.setWidth(last_use + 1);
////			tab.resize(last_use + 1, max_bod_size);
//			for (int i = 0 ; i < tab.width() ; i++) {
//				if (use[i]) {
//					objects[i].do_save();
//					tab.setRowHeight(i,objects[i].data_size());
//					objects[i].to_tab(tab, i);
//				} else {
//					tab.setRowHeight(i,0);
////					tab.set(i,0,false);
//				}
//			}
//		} else {
//			tab.setWidth(0);
////			tab.resize(0,0);
//		}
//	}
//	
//
//	public T load_tab_row(sTab t, int r) {
//		T p = null;
//		for (int i = 0 ; i < capacity ; i++) if (!use[i] && objects[i].state == State.FREE) {
//			p = objects[i]; use[i] = true; break; }
//		if (p == null) {
//			int free_id = capacity;
//			expend();
//			if (free_id >= capacity) return null;
//			p = objects[free_id];
//			use[free_id] = true; }
//		all_used.put(p.id, p);
//		p.load_poolable();
//		p.from_tab(t, r);
//		p.do_init();
//		p.do_finish();
//		return p;
//	}
//
//	public void load() {
//		freeAll();
//		if (tab == null) return;
//		if (tab.width() > prefered_capacity) capacity = tab.width();
//		else capacity = prefered_capacity;
//		resize();
//		for (int i = 0 ; i < tab.width() ; i++) {
//			if (tab.height(i) > 0 && tab.getBool(i, 0)) {
//				use[i] = true;
//				all_used.put(objects[i].id, objects[i]);
//				objects[i].load_poolable();
//				objects[i].from_tab(tab, i);
//			}
//		}
//		for (int i = 0 ; i < tab.width() ; i++)  
//			if (use[i]) {
//				objects[i].do_init();
//			}
//		for (int i = 0 ; i < tab.width() ; i++) 
//			if (use[i]) {
//				objects[i].do_finish();
//			}
//		
//	}
//
//	public void load_1() {
//		freeAll();
//		if (tab == null) return;
//		if (tab.width() > prefered_capacity) capacity = tab.width();
//		else capacity = prefered_capacity;
//		resize();
//		for (int i = 0 ; i < tab.width() ; i++) {
//			if (tab.height(i) > 0 && tab.getBool(i, 0)) {
//				use[i] = true;
//				all_used.put(objects[i].id, objects[i]);
//				objects[i].load_poolable();
//				objects[i].from_tab(tab, i);
//			}
//		}
//	}
//
//	public void load_2() {
//		for (int i = 0 ; i < tab.width() ; i++)  
//			if (use[i]) {
//				objects[i].do_init();
//			}
//		
//	}
//
//	public void load_3() {
//		for (int i = 0 ; i < tab.width() ; i++) 
//			if (use[i]) {
//				objects[i].do_finish();
//			}
//		
//	}
//	
}
