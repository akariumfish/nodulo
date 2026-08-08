package patch;

import java.util.ArrayList;
import java.util.HashMap;

import data.*;
import gui.*;
import util.*;
import aa_nodulo.*;
import app.*;

public class pColl extends sPoolable {
	
	private static final int start_data_nb = 4;
	
	public String get_convert_str(String s, HashMap<String,String> map) {
		if (map != null && map.get(s) != null) s = Utl.copy(map.get(s)); return s; }
	public void from_tab(sTab t, int c) { from_tab(t, c, null); }
	public void from_tab(sTab t, int c, HashMap<String,String> map) {
		
		empty();
		
		int cnt = 0;
		
		boolean is_used = t.getBool(c, 0);
		if (!is_used) return;
		
		String stand_ref = t.getStr(c, 1);
		stand = pStandard.get(stand_ref); 
		if (stand == null) return;
		ref_in_stand = Utl.copy(t.getStr(c, 2));
		
		length = t.getInt(c, 3); 
		
		cnt += start_data_nb;
		
		for (int i = 0 ; i < length ; i++) {
			String data = t.getStr(c, cnt+i);
			data = get_convert_str(data, map);
			datas.add(Utl.copy(data));
		}
		cnt += length;
		
	}
//	public void from_tab(sTab t, int c) {
//		
//		empty();
//		
//		int cnt = 0;
//		
//		boolean is_used = t.getBool(c, 0);
//		if (!is_used) return;
//		
//		String stand_ref = t.getStr(c, 1);
//		stand = pStandard.get(stand_ref); 
//		if (stand == null) return;
//		ref_in_stand = t.getStr(c, 2);
//		
//		length = t.getInt(c, 3); 
//		
//		cnt += start_data_nb;
//		
//		for (int i = 0 ; i < length ; i++) {
//			String data = t.getStr(c, cnt+i);
//			datas.add(data);
//		}
//		cnt += length;
//		
//	}
	public void to_tab(sTab t, int c) {
		int cnt = 0;
		
		t.set(c, 0, true); 
		if (stand != null) t.set(c, 1, stand.ref);
		else t.set(c, 1, "");
		t.set(c, 2, ref_in_stand); 
		t.set(c, 3, (int)datas.size()); 
		
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


	public pPatch patch = null;
	public pSheet sheet = null;
	
	
	public ArrayList<String> datas = new ArrayList<String>();
	public int length = 0;
	
	public pStandard stand;
	public String ref_in_stand;
	public int fill = 0;
	
	public pColl() {}
	
	public pColl init(pSheet s, pStandard p, String r) { 
		sheet = s; patch = s.patch; ref_in_stand = r; stand = p; //def(); 
		return this; }
	
	public void empty() {
		datas.clear();
		length = 0;
	}

	public void def() {
		datas.clear();
		length = 0;
		if (stand != null && stand.collec_pars.get(ref_in_stand) != null) {
			Object so = stand.collec_pars.get(ref_in_stand).get("fill");
			fill = 0;
			if (so != null && so instanceof Integer) fill = (int)so;
			String type = stand.getCollecData(ref_in_stand);
			if (type != null) for (int j = 0 ; j < fill ; j++) {
				String a = Utl.to_string(Utl.new_object(Utl.type_name_class.get(type)));
				add(a);
			}
		}
	}

	public void clear_action() {
		empty(); 
	}
	
	
	
	
	public int size() { return datas.size(); }

	public boolean contains(String s) { return Utl.contains(datas, s); }

	public pColl add(String s) {
		datas.add(Utl.copy(s)); length++; return this; }

	public pColl set(int i, String s) {
		datas.set(i,Utl.copy(s)); return this; }
	
	public pColl remove(String s) {
		for (int i = 0 ; i < datas.size() ; i++) { 
			String d = datas.get(i);
			if (d.equals(s)) {
				datas.remove(d); length--; 
				return this; }
		}
		return this; }
	
	public String get(int i) { 
		if (i < 0) {
//			plane.app.log("ERROR : cCollec "+pool_ref+" .get(int) : "+i+" < 0");
			return ""; }
		if (i >= length) {
//			plane.app.log("ERROR : cCollec "+pool_ref+" .get(int) : "+i+" >= length: "+length);
			return ""; }
		return Utl.copy(datas.get(i)); }
	public ArrayList<String> get() { 
		ArrayList<String> arr = new ArrayList<String>();
		for (String s : datas) arr.add(Utl.copy(s));
		return arr; }
	
	
	public void log_debug() {
		Utl.log(pool_ref+" ");
		if (stand != null) Utl.log(stand.ref+" "+ref_in_stand+" ");
		for (String s : datas) Utl.log(s+" ");
		Utl.logn();
	}

}
