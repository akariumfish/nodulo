package util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class iMap <K> {
	HashMap<Integer, K> map = new HashMap<Integer, K>();
	ArrayList<K> all = new ArrayList<K>();
	
	public Set<Map.Entry<Integer,K>> entrySet() { return map.entrySet(); }
	public ArrayList<K> all() { return all; }
	public Set<Integer> allKey() { return map.keySet(); }
	public ArrayList<K> tmp_all() { 
		ArrayList<K> tmp = new ArrayList<K>();
		for (K t : all) tmp.add(t); return tmp; }
	public int size() { return map.size(); }
	public void put(int s, K r) { 
		if (map.get(s) != null) {
//			Applet.logg("WARNING : nMap.put : the key <"+s+"> is already used"); 
		}
		map.put(s, r); all.add(r); }
	public void putOne(int s, K r) { 
		if (map.get(s) != null) return;
		map.put(s, r); all.add(r); }
	public void replace(int s, K r) { map.remove(s); all.remove(r); map.put(s, r); all.add(r); }
	public K get(int s) { return map.get(s); }
	public Integer getKey(K s) { 
		for (Map.Entry<Integer,K> me : map.entrySet()) {
			if (me.getValue() == s) return me.getKey(); } return null; }
	public boolean hasKey(int s) { return map.containsKey(s); }
	public boolean hasVal(K s) { return map.containsValue(s); }
	public void remove(int s) { if (map.get(s) == null) return; 
		K r = map.get(s); all.remove(r); map.remove(s,r); }
	public void remove(int s, K r) { all.remove(r); map.remove(s, r); }
	public void remove(K r) { 
		if (getKey(r) != null) map.remove(getKey(r), r); 
		all.remove(r); 
		if (getKey(r) != null) remove(r); 
	}
	public void clear() { map.clear(); all.clear(); }

//	public boolean containsKey(String s) { return map.containsKey(s); }
//	public boolean containsValue(T s) { return map.containsValue(s); }
}
