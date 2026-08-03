package app;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class MetodeMap {
	HashMap<String, nRun> metodes = new HashMap<String, nRun>();
	HashMap<String, Flag> flags = new HashMap<String, Flag>();
	
	class Flag {
		ArrayList<String> flags = new ArrayList<String>(); }

	public Set<Map.Entry<String,nRun>> entrySet() {
		return metodes.entrySet(); }
	
	public int size() { return metodes.size(); }
	
	public void put(String s, nRun r, String f1) {
		put(s, r); flag(s, f1); }
	public void put(String s, nRun r, String f1, String f2) {
		put(s, r); flag(s, f1); flag(s, f2); }
	public void put(String s, nRun r, String f1, String f2, String f3) {
		put(s, r); flag(s, f1); flag(s, f2); flag(s, f3); }
	
	public void put(String s, nRun r) {
		metodes.put(s, r); Flag f = new Flag(); flags.put(s, f); }
	
	public void flag(String metode, String flag) { // add flag to metode
		flags.get(metode).flags.add(flag); }
	
	public boolean isFlag(String metode, String flag) {
		for (String s : flags.get(metode).flags) if (s.equals(flag)) return true;
		return false; }
	
	public nRun get(String s) { return metodes.get(s); }
	public boolean has(String s) { return metodes.get(s) != null; }
	
	public void remove(String s) { metodes.remove(s); flags.get(s).flags.clear(); flags.remove(s); }
	public void remove(String s, nRun r) { metodes.remove(s, r); flags.get(s).flags.clear(); flags.remove(s); }
	
	public void clear() {
		metodes.clear();
		for (Map.Entry<String, Flag> f : flags.entrySet()) 
			f.getValue().flags.clear();
		flags.clear(); }
	
}
