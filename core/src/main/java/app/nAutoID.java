package app;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import com.badlogic.gdx.utils.Pool;

public class nAutoID {
	
	public static final char sep = '_';
	
	private HashMap<Integer, Integer> used = new HashMap<Integer, Integer>();
	private Random rng = new Random();
	private int rng_bound = 100, rng_extend = 100;
	
	public nAutoID() {}
	
	
	public String make_full_ref(String ref, int id) { return ref + sep + id; }
	
	
	public int find_id(String s) { // extract id from String : text_#ID#
		int cnt = 0; for (int i = 0; i < s.length(); i++) if (s.charAt(i) == sep) cnt=i;
		if (cnt+1 >= s.length()) return -1;
		String s_end = s.substring(cnt+1, s.length());
		try { int id = Integer.parseInt(s_end); return id; } 
		catch (NumberFormatException e) { return -1; } }
	public String find_ref(String s) { // extract ref from String : #ref#_ID
		int cnt = 0; for (int i = 0; i < s.length(); i++) if (s.charAt(i) == sep) cnt=i;
		if (cnt+1 >= s.length()) return s;
		String s_end = s.substring(cnt+1, s.length());
		try { 
			int id = Integer.parseInt(s_end); 
			String s_strt = s.substring(0,cnt); 
			return s_strt; } 
		catch (NumberFormatException e) { return s; } }
	

	public boolean id_exist(int e) { return used.get(e) != null; }

	public void free_id(int i) { 
		if (id_exist(i)) used.remove(i); }
	
	public int get_new_id() {
		int n = get_random_int();
		while (id_exist(n)) n = get_random_int();
		used.put(n,n);
		return n; }

	public int get_prefered_id(int i) {
		if (!id_exist(i)) { used.put(i,i); return i; }
		return get_new_id(); }
	
	private int get_random_int() {
		if (used.size() > 2f * rng_bound / 3f) rng_bound += rng_extend;
		return rng.nextInt(rng_bound); }
	
}
