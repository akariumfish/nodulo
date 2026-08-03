package plane;

import java.util.ArrayList;

import app.Applet;

import data.*;

public class pCollec extends sPoolable {

	public void signalChange() {
		if (prop != null && !prop.mode_nosync && space != null) space.signalChange(this); 
	}
	
	private static final int start_data_nb = 2;

//	private float getFloat(Object[] c, int i) { return (float)c[i]; }
	private int getInt(Object[] c, int i) { return (int)c[i]; }
	private boolean getBool(Object[] c, int i) { return (boolean)c[i]; }
	private String getStr(Object[] c, int i) { return (String)c[i]; }
	public void init_from_array(Object[] c) {
		
		empty();
		
		int cnt = 0;
		
		boolean is_used = getBool(c, 0);
		if (!is_used) return;
		
		length = getInt(c, 1); 
		
		cnt += start_data_nb;
		
		for (int i = 0 ; i < length ; i++) {
			String data = getStr(c, cnt+i);
			datas.add(data);
		}
		cnt += length;
		
	}
	public void load_from_array(Object[] c) {
		
		int cnt = 0;
		
		length = getInt(c, 1); 
		
		cnt += start_data_nb;
		
		datas.clear();
		for (int i = 0 ; i < length ; i++) {
			String data = getStr(c, cnt+i);
			datas.add(data);
		}
		cnt += length;
		
	}
	private void set(Object[] c, int i, Object data) { c[i] = Applet.copy(data); }
	public void to_array(Object[] c) {
		int cnt = 0;
		
		set(c, 0, true); 
		set(c, 1, (int)datas.size()); 
		
		cnt += start_data_nb;
		
		int ci = 0;
		for (String s : datas) {
			set(c, cnt+ci, s);
			ci++;
		}
		cnt += length;
		
	}
	public void from_tab(sTab t, int c) {
		
		empty();
		
		int cnt = 0;
		
		boolean is_used = t.getBool(c, 0);
		if (!is_used) return;
		
		length = t.getInt(c, 1); 
		
		cnt += start_data_nb;
		
		for (int i = 0 ; i < length ; i++) {
			String data = t.getStr(c, cnt+i);
			datas.add(data);
		}
		cnt += length;
		
	}
	public void to_tab(sTab t, int c) {
		int cnt = 0;
		
		t.set(c, 0, true); 
		t.set(c, 1, (int)datas.size()); 
		
		cnt += start_data_nb;
		
		int ci = 0;
		for (String s : datas) {
			t.set(c, cnt+ci, s);
			ci++;
		}
		cnt += length;
		
	}
	public int data_size() {
		int cnt = 0;
		cnt += start_data_nb;
		cnt += length;
		return cnt;
	}

	
	public pSpace space = null;
	
	
	public ArrayList<String> datas = new ArrayList<String>();
	public int length = 0;
	
	public pProperty prop;
	public String ref_in_prop;
	public int fill = 0;
	
	public pCollec() {}
	
	public pCollec init(pSpace s, pProperty p, String r) { 
		space = s; prop = p; ref_in_prop = r; empty(); 
		return this; }
	
	public void empty() {
		datas.clear();
		length = 0;
		if (prop != null) {
			Object so = prop.get_setting(ref_in_prop, "fill");
			fill = 0;
			if (so != null) fill = (int)so;
			String type = prop.getCollecData(ref_in_prop);
			for (int j = 0 ; j < fill ; j++) {
				String a = Applet.to_string(Applet.new_object(Applet.type_ref_class.get(type)));
				add(a); length++; 
			}
		}
		signalChange();
	}

	public void clear_action() {
		empty();
		if (space != null && !Applet.has(space.delCollecs, this)) space.delCollecs.add(this);
	}
	
	
	
	
	public int size() { return datas.size(); }

	public pCollec add(String s) {
		datas.add(s); length++; 
		signalChange(); return this; }

	public pCollec set(int i, String s) {
		if (i >= length) return this;
		boolean change = !datas.get(i).equals(s);
		datas.set(i,s); 
		if (change) signalChange(); 
		return this; }
	
	public pCollec remove(String s) {
		for (int i = 0 ; i < datas.size() ; i++) { 
			String d = datas.get(i);
			if (d.equals(s)) {
				datas.remove(d); length--; 
				signalChange(); 
				return this; }
		}
		return this; }
	
	public String get(int i) { 
		if (i >= length) {
			space.app.logn("ERROR : pCollec "+pool_ref+" .get("+i+") where "+i+" > length");
			return ""; }
		return datas.get(i); }
	public ArrayList<String> get() { 
		ArrayList<String> arr = new ArrayList<String>();
		for (String s : datas) arr.add(Applet.copy(s));
		return arr; }
	
	
	

}
