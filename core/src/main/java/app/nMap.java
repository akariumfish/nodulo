package app;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class nMap <T> {
	HashMap<String, T> map = new HashMap<String, T>();
	ArrayList<T> all = new ArrayList<T>();
	
	public Set<Map.Entry<String,T>> entrySet() { return map.entrySet(); }
	public ArrayList<T> all() { return all; }
	public Set<String> allKey() { return map.keySet(); }
	public ArrayList<T> tmp_all() { 
		ArrayList<T> tmp = new ArrayList<T>();
		for (T t : all) tmp.add(t); return tmp; }
	public int size() { return map.size(); }
	public void put(String s, T r) { 
		if (map.get(s) != null) {
//			Applet.logg("WARNING : nMap.put : the key <"+s+"> is already used"); 
		}
		map.put(s, r); all.add(r); }
	public void replace(String s, T r) { map.remove(s); all.remove(r); map.put(s, r); all.add(r); }
	int add_cnt = 0;
	public void add(T r) { String s = "__no_name_"+add_cnt; add_cnt++; put(s, r); }
	public T get(String s) { return map.get(s); }
	public T get(int s) { return all.get(s); }
	public String getKey(T s) { 
		for (Map.Entry<String,T> me : map.entrySet()) {
			if (me.getValue() == s) return me.getKey(); } return null; }
	public boolean hasKey(String s) { return map.get(s) != null; }
	public boolean hasVal(T s) { return map.containsValue(s); }
	public void remove(String s) { if (map.get(s) == null) return; 
		T r = map.get(s); all.remove(r); map.remove(s,r); }
	public void remove(String s, T r) { all.remove(r); map.remove(s, r); }
	public void remove(T r) { 
		if (getKey(r) != null) map.remove(getKey(r), r); 
		all.remove(r); 
		if (getKey(r) != null) remove(r); 
	}
	public void clear() { add_cnt = 0; map.clear(); all.clear(); }

	public boolean containsKey(String s) { return map.containsKey(s); }
	public boolean containsValue(T s) { return map.containsValue(s); }
}
