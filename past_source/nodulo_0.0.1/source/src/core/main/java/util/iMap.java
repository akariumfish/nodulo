package util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class iMap <T> {
	HashMap<Integer, T> map = new HashMap<Integer, T>();
	ArrayList<T> all = new ArrayList<T>();
	
	public Set<Map.Entry<Integer,T>> entrySet() { return map.entrySet(); }
	public ArrayList<T> all() { return all; }
	public Set<Integer> allKey() { return map.keySet(); }
	public ArrayList<T> tmp_all() { 
		ArrayList<T> tmp = new ArrayList<T>();
		for (T t : all) tmp.add(t); return tmp; }
	public int size() { return map.size(); }
	public void put(int s, T r) { 
		if (map.get(s) != null) {
//			Applet.logg("WARNING : nMap.put : the key <"+s+"> is already used"); 
		}
		map.put(s, r); all.add(r); }
	public void putOne(int s, T r) { 
		if (map.get(s) != null) return;
		map.put(s, r); all.add(r); }
	public void replace(int s, T r) { map.remove(s); all.remove(r); map.put(s, r); all.add(r); }
	public T get(int s) { return map.get(s); }
	public Integer getKey(T s) { 
		for (Map.Entry<Integer,T> me : map.entrySet()) {
			if (me.getValue() == s) return me.getKey(); } return null; }
	public boolean hasKey(int s) { return map.containsKey(s); }
	public boolean hasVal(T s) { return map.containsValue(s); }
	public void remove(int s) { if (map.get(s) == null) return; 
		T r = map.get(s); all.remove(r); map.remove(s,r); }
	public void remove(int s, T r) { all.remove(r); map.remove(s, r); }
	public void remove(T r) { 
		if (getKey(r) != null) map.remove(getKey(r), r); 
		all.remove(r); 
		if (getKey(r) != null) remove(r); 
	}
	public void clear() { map.clear(); all.clear(); }

//	public boolean containsKey(String s) { return map.containsKey(s); }
//	public boolean containsValue(T s) { return map.containsValue(s); }
}
