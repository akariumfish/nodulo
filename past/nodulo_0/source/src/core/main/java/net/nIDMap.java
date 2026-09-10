package net;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class nIDMap <T> {
	HashMap<Integer, T> map = new HashMap<Integer, T>();
	ArrayList<T> all = new ArrayList<T>();
	
	public Set<Map.Entry<Integer,T>> entrySet() { return map.entrySet(); }
	public ArrayList<T> all() { return all; }
	public Set<Integer> allKey() { return map.keySet(); }
	public ArrayList<T> tmp_all() { 
		ArrayList<T> tmp = new ArrayList<T>();
		for (T t : all) tmp.add(t); return tmp; }
	public int getFreeId() { 
		int id = 0, cnt = 200;
		while (hasKey(id) && cnt > 0) { id++; cnt--; }
		if (cnt == 0) return -1; else return id; }
	public int size() { return map.size(); }
	public void put(int s, T r) { map.put(s, r); all.add(r); }
	public void replace(int s, T r) { map.remove(s); all.remove(r); map.put(s, r); all.add(r); }
//	int add_cnt = 0;
//	public void add(T r) { String s = "__no_name_"+add_cnt; add_cnt++; put(s, r); }
	public T getAtRef(int s) { return map.get(s); }
	public T getAtIndex(int s) { return all.get(s); }
	public boolean hasKey(int s) { return map.get(s) != null; }
	public boolean hasVal(T s) { return map.containsValue(s); }
	public void remove(int s) { all.remove(map.get(s)); map.remove(s); }
	public void remove(int s, T r) { all.remove(r); map.remove(s, r); }
	public void remove(T r) { all.remove(r); map.remove(r); }
	public void clear() { /*add_cnt = 0;*/ map.clear(); all.clear(); }

	public boolean containsKey(int s) { return map.containsKey(s); }
	public boolean containsValue(T s) { return map.containsValue(s); }
}
