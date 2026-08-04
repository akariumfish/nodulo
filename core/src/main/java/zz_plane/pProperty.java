package zz_plane;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.math.Vector2;

import util.Utl;
import data.MetodeMap;
import data.sTab;
import data.sValueBloc;
import util.nMap;
import util.nRun;
import zz_applet.Applet;
import zz_patch.pPar;
import zz_patch.pPatch;
import zz_patch.pProcess;
import zz_patch.pStandard;

public class pProperty {

	public static nMap<pProperty> body_propertys = new nMap<pProperty>(); 
	public static nMap<pProperty> body_common_propertys = new nMap<pProperty>(); 
	public static Applet app;

//	public static nMap<pProperty> addable_props = new nMap<pProperty>();
//	public static nMap<pProperty> def_added_props = new nMap<pProperty>();
	
	public static pProperty get(String r) {
		return body_propertys.get(r);
	}
	
	public static pProperty newProperty(String r) { return newProperty(r,false); }
	
	public static pProperty newProperty(String r, boolean addable) {
		if (body_propertys.hasKey(r)) {
			app.logn("ERROR: cant add property, <"+r+"> allready exist");
			return null; }
		pProperty p = new pProperty(r);
//		if (addable) p.addable();
		return p;
	}
	
	public static pProperty general_prop_abstract;
	public static pProperty user_prop_abstract;
	
	public static void build(Applet a) {
		app = a;
		
		
		general_prop_abstract = new pProperty("general_prop_abstract")
		.addNodeRun(new nRun() { public void run() {	
			pStandard stand = arg(0,pStandard.class);
			if (stand == null) return;
			
			
		}})
		
		;

		user_prop_abstract = new pProperty("user_prop_abstract")
		
		;
		
		
	}
	

	public static nMap<pProperty> general_propertys = new nMap<pProperty>(); 

	public static pProperty newGeneralProperty(String r) {
		if (body_propertys.hasKey(r)) {
			app.logn("ERROR: cant add property, <"+r+"> allready exist");
			return null; }
		pProperty p = new pProperty(r);
		p.copy(general_prop_abstract);
		p.setCommon();
		p.is_general = true;
		general_propertys.put(r,p);
		return p;
	}

	
	public pProperty getGeneral() { return general; }

	public pProperty newLocalProperty(String r) {
		if (!is_general) return null;
		pProperty p = newProperty(r,false);
		p.copy(user_prop_abstract);
		need_props.add(p);
		p.general = this;
		return p; 
	}
	public pProperty newOptionalLocalProperty(String r) {
		if (!is_general) return null;
		pProperty p = newProperty(r,false);
		p.copy(user_prop_abstract);
		option_props.add(p);
		p.general = this;
		addData("use_"+r, false);
		return p; 
	}
	public ArrayList<pProperty> need_props = new ArrayList<pProperty>();
	public ArrayList<pProperty> option_props = new ArrayList<pProperty>();
	
	public pProperty general = null;
	public boolean is_general = false;
	
	public pProperty copy(pProperty s) {

		mode_runtime = s.mode_runtime;
		mode_nosync = s.mode_nosync;
		mode_fullsync = s.mode_fullsync;
		mode_localval = s.mode_localval;
		mode_common = s.mode_common;
		
		grouping_flag = Utl.copy(s.grouping_flag);

		for (nRun n : s.node_run) node_run.add(n);

		for (nRun n : s.body_init_run) body_init_run.add(n);
//		for (nRun n : s.body_clear_run) body_clear_run.add(n);
		
		for (String r : s.used_key) used_key.add(r);
		
		for (Map.Entry<String,Integer> me : s.collec_vals.entrySet()) 
			collec_vals.put(me.getKey(), me.getValue());
		for (String r : s.collec_bodys) collec_bodys.add(r);
		for (Map.Entry<String,String> me : s.collec_datas.entrySet()) 
			collec_datas.put(me.getKey(), me.getValue());
		for (Map.Entry<String,String> me : s.collec_props.entrySet()) 
			collec_props.put(me.getKey(), me.getValue());
		collec_used += s.collec_used;
		
		for (Map.Entry<String,Integer> me : s.ref_vals.entrySet()) 
			ref_vals.put(me.getKey(), me.getValue());
		for (Map.Entry<String,String> me : s.ref_props.entrySet()) 
			ref_props.put(me.getKey(), me.getValue());
		ref_used += s.ref_used;

		for (Map.Entry<String,Integer> me : s.body_vals.entrySet()) 
			body_vals.put(me.getKey(), me.getValue());
		body_used += s.body_used;

		for (int i = 0 ; i < s.data_used.length ; i++) data_used[i] += s.data_used[i];

		for (Map.Entry<Class<?>, nMap<Integer>> me : s.data_vals.entrySet()) {
			Class<?> ct = me.getKey();
			if (data_vals.get(ct) != null) {
				for (Map.Entry<String,Integer> map_me : me.getValue().entrySet()) 
					data_vals.get(ct).put(map_me.getKey(), map_me.getValue());
			} else {
				nMap<Integer> map = new nMap<Integer>();
				for (Map.Entry<String,Integer> map_me : me.getValue().entrySet()) 
					map.put(map_me.getKey(), map_me.getValue());
				data_vals.put(ct,map);
			}
		}
		
		for (Map.Entry<Class<?>, nMap<Object>> me : s.data_defs.entrySet()) {
			Class<?> ct = me.getKey();
			if (data_defs.get(ct) != null) {
				for (Map.Entry<String,Object> map_me : me.getValue().entrySet()) 
					data_defs.get(ct).put(map_me.getKey(), Utl.copy(map_me.getValue()));
			} else {
				nMap<Object> map = new nMap<Object>();
				for (Map.Entry<String,Object> map_me : me.getValue().entrySet()) 
					map.put(map_me.getKey(), Utl.copy(map_me.getValue()));
				data_defs.put(ct,map);
			}
		}

		for (Map.Entry<String,Class<?>> me : s.data_class.entrySet()) 
			data_class.put(me.getKey(), me.getValue());

		for (Map.Entry<String, nMap<Object>> me : s.settings.entrySet()) {
			String ct = me.getKey();
			if (settings.get(ct) != null) {
				for (Map.Entry<String,Object> map_me : me.getValue().entrySet()) 
					settings.get(ct).put(map_me.getKey(), Utl.copy(map_me.getValue()));
			} else {
				nMap<Object> map = new nMap<Object>();
				for (Map.Entry<String,Object> map_me : me.getValue().entrySet()) 
					map.put(map_me.getKey(), Utl.copy(map_me.getValue()));
				settings.put(ct,map);
			}
		}

		return this;
	}

	
	
	
	
	public String ref;
	
	public pProperty(String r) {
		ref = r; 
		body_propertys.put(r, this);
		
		data_used = new int[Utl.data_type_nb];
		for (int i = 0 ; i < Utl.data_type_nb ; i++) data_used[i] = 0;
	}

	public boolean mode_runtime = false;
	public boolean mode_nosync = false;
	public boolean mode_fullsync = false;
	public boolean mode_localval = false;
	public boolean mode_common = false;

//	public pProperty addable() { return addable(false); }
//	public pProperty addable(boolean def_add) { 
//		if (addable_props.hasKey(ref)) {
//			app.logn("ERROR: cant make property addable, <"+ref+"> allready exist");
//			return null; }
//		addable_props.put(ref,this);
//		if (def_add) def_added_props.put(ref,this);
//		return this; 
//	}
	public pProperty setRuntime() { mode_runtime = true; return this; }
	public pProperty setNoSync() { mode_nosync = true; return this; }
	public pProperty setFullSync() { mode_fullsync = true; return this; }
	public pProperty setLocalVal() { mode_localval = true; return this; }
	public pProperty setCommon() { mode_common = true; 
		body_common_propertys.add(this); return this; }

	public String grouping_flag = "";

	public pProperty setGroupFlag(String r) { grouping_flag = r; return this; }
	
	public ArrayList<nRun> node_run = new ArrayList<nRun>();
	public pProperty addNodeRun(nRun r) { node_run.add(r); return this; }

	public ArrayList<nRun> body_init_run = new ArrayList<nRun>();
//	public ArrayList<nRun> body_clear_run = new ArrayList<nRun>();
	public pProperty addBodyInitRun(nRun r) { body_init_run.add(r); return this; }
//	public pProperty addBodyClearRun(nRun r) { body_clear_run.add(r); return this; }

//	public ArrayList<nRun> frame_runs = new ArrayList<nRun>();
//	public ArrayList<nRun> tick_runs = new ArrayList<nRun>();
//	public pProperty addFrameRun(nRun r) { frame_runs.add(r); return this; }
//	public pProperty addTickRun(nRun r) { tick_runs.add(r); return this; }
	
	
	
	public void def_to_tab(sTab t, int c) {
		t.set(c, 0, true);
		
		int cnt = 1;

		for (int i = 0 ; i < Utl.data_type_nb ; i++) {
			for (int j = 0 ; j < data_used[i] ; j++) {
				String dtrf = null;
				for (Map.Entry<String,Integer> me : data_vals.get(Utl.data_type[i]).entrySet()) {
					if (me.getValue() == j) dtrf = me.getKey(); }
				if (dtrf == null) continue;
				if (i == Utl.type_class_index.get(Vector2.class)) {  
					Vector2 v = (Vector2)data_defs.get(Utl.data_type[i]).get(dtrf);
					t.set(c, cnt+(j*2), v.x);
					t.set(c, cnt+(j*2)+1, v.y);
				} else if (i == Utl.type_class_index.get(Float.class)) {  
					float v = (float)data_defs.get(Utl.data_type[i]).get(dtrf);
					t.set(c, cnt+j, v);
				} else if (i == Utl.type_class_index.get(Integer.class)) {  
					int v = (int)data_defs.get(Utl.data_type[i]).get(dtrf);
					t.set(c, cnt+j, v);
				} else if (i == Utl.type_class_index.get(Boolean.class)) { 
					boolean v = (boolean)data_defs.get(Utl.data_type[i]).get(dtrf);
					t.set(c, cnt+j, v);
				} else if (i == Utl.type_class_index.get(String.class)) { 
					String v = (String)data_defs.get(Utl.data_type[i]).get(dtrf);
					t.set(c, cnt+j, v);
				}
			}
			cnt += data_used[i] * Utl.type_data_size.get(Utl.data_type[i]);
		}
		
		for (int j = 0 ; j < collec_used ; j++) {
			t.set(c, cnt+j, "");
		}
		cnt += collec_used;

		for (int j = 0 ; j < ref_used ; j++) {
			t.set(c, cnt+j, "");
		}
		cnt += ref_used;

		for (int j = 0 ; j < body_used ; j++) {
			t.set(c, cnt+j, "");
		}
		cnt += body_used;
		
	}
	public int data_size() {
		int cnt = 1;
		for (int i = 0 ; i < Utl.data_type_nb ; i++) 
			cnt += data_used[i] * Utl.type_data_size.get(Utl.data_type[i]);
		cnt += collec_used;
		cnt += ref_used;
		cnt += body_used;
		return cnt;
	}
	
	
	
	
	
	public ArrayList<String> used_key = new ArrayList<String>();
	public boolean add_key(String k) {
		for (String s : used_key) if (s.equals(k)) {
			return true;
		}
		used_key.add(k);
		return false;
	}
	
	 
	
	
	
	
	
	public nMap<Integer> collec_vals = 
			new nMap<Integer>();
	public nMap<String> collec_props = 
			new nMap<String>();
	public ArrayList<String> collec_bodys = 
			new ArrayList<String>();
	public nMap<String> collec_datas = 
			new nMap<String>();
	
	int collec_used = 0;

	public pProperty addCollec(String ref_in_param, Class<?> ct) {
		addCollec(ref_in_param);
		collec_datas.put(ref_in_param, ct.getName());
		return this;
	}

	public pProperty addCollecRef(String ref_in_param, String prop_ref) {
		addCollec(ref_in_param);
		collec_props.put(ref_in_param, prop_ref);
		return this;
	}

	public pProperty addCollecBody(String ref_in_param) {
		addCollec(ref_in_param);
		collec_bodys.add(ref_in_param);
		return this;
	}

	private pProperty addCollec(String ref) {
		if (add_key(ref)) {
			app.logn("ERROR : pProperty "+this.ref+" has allready the key "+ref);
			return this;
		}
		collec_vals.put(ref, collec_used);
		collec_used++; 
		return this;
	}

	public int getCollecUsed() {
		return collec_used; }

	public int getCollecValId(String r) {
		if (collec_vals.get(r) != null) return collec_vals.get(r); else return -1; }

	public pProperty getCollecProp(String r) {
		if (collec_props.get(r) != null) return pProperty.get(collec_props.get(r)); 
		else return null; }

	public boolean isCollecBody(String r) {
		return Utl.contains(collec_bodys, r); }

	public String getCollecData(String r) {
		if (collec_datas.get(r) != null) return collec_datas.get(r); 
		else return null; }

	public String getCollecRefFromId(int i) {
		for (Map.Entry<String, Integer> me : collec_vals.entrySet()) {
			if (me.getValue() == i) return me.getKey(); }
		return ""; }
	
	
	
	
	
	
	public nMap<Integer> ref_vals =  
			new nMap<Integer>();
	public HashMap<String, String> ref_props = 
			new HashMap<String, String>();
	int ref_used = 0;

	public pProperty addRef(String ref_in_param, String prop_ref) {
		if (add_key(ref_in_param)) {
			app.logn("ERROR : pProperty "+this.ref+" has allready the key "+ref_in_param);
			return this;
		}
		ref_vals.put(ref_in_param, ref_used);
		ref_props.put(ref_in_param, prop_ref);
		ref_used++; 
		return this;
	}

	public int getRefUsed() {
		return ref_used; }

	public int getRefValId(String r) {
		if (ref_vals.get(r) != null) return ref_vals.get(r); else return -1; }

	public pProperty getRefProp(String r) {
		if (ref_props.get(r) != null) return pProperty.get(ref_props.get(r)); else return null; }
	
	
	
	
	
	
	public nMap<Integer> body_vals =  
			new nMap<Integer>();
	int body_used = 0;

	public pProperty addBody(String ref_in_param) {
		if (add_key(ref_in_param)) {
			app.logn("ERROR : pProperty "+this.ref+" has allready the key "+ref_in_param);
			return this;
		}
		body_vals.put(ref_in_param, body_used);
		body_used++; 
		return this;
	}

	public int getBodyUsed() {
		return body_used; }

	public int getBodyValId(String r) {
		if (body_vals.get(r) != null) return body_vals.get(r); else return -1; }

	
	
	
	
	
	 
	public HashMap<Class<?>, nMap<Integer>> data_vals = 
			new HashMap<Class<?>, nMap<Integer>>();
	public HashMap<Class<?>, nMap<Object>> data_defs = 
			new HashMap<Class<?>, nMap<Object>>();
	
	public nMap<Class<?>> data_class = new nMap<Class<?>>();
	
	public int[] data_used;

	public pProperty addData(String ref, Object def) {
		addData(ref, def.getClass());
		nMap<Object> vals_def = data_defs.get(def.getClass());
		vals_def.put(ref, def);
		return this;
	}
	public pProperty addData(String ref, Class<?> ct) {
		if (add_key(ref)) {
			app.logn("ERROR : pProperty "+this.ref+" has allready the key "+ref);
			return this; 
		}
		nMap<Object> vals_def = data_defs.get(ct);
		if (vals_def == null) { 
			vals_def = new nMap<Object>();
			data_defs.put(ct, vals_def);
		}
		nMap<Integer> vals_id = data_vals.get(ct);
		if (vals_id == null) { 
			vals_id = new nMap<Integer>();
			data_vals.put(ct, vals_id);
		}
		int du = data_used[Utl.type_class_index.get(ct)];
		vals_id.put(ref, du);
		du++; data_used[Utl.type_class_index.get(ct)] = du;
		data_class.put(ref,ct);
		return this;
	}

	public int getDataUsed(Class<?> ct) {
		return data_used[Utl.type_class_index.get(ct)]; }
	
	public int getDataValId(String r, Class<?> ct) {
		if (data_vals.get(ct) != null && data_vals.get(ct).get(r) != null)
			return data_vals.get(ct).get(r); 
		else return -1; }
	public Object getDataValDef(String r, Class<?> ct) {
		return Utl.copy(data_defs.get(ct).get(r)); }
	public Class<?> getDataValClass(String r) {
		return data_class.get(r); }

	
	
	
	
	public Object get_setting(String ref, String s) {
		nMap<Object> data_set = settings.get(ref);
		if (data_set == null) return null;
		return Utl.copy(data_set.get(s));
	}
	
	public HashMap<String, nMap<Object>> settings = 
			new HashMap<String, nMap<Object>>();
	
	public pProperty addCollec(String ref, Class<?> ct, String s1, Object o1) {
		addCollec(ref, ct);
		put_setting(ref, s1, o1); 
		return this;
	}

	public pProperty addCollec(String ref, Class<?> ct, String s1, Object o1, String s2, Object o2) {
		addCollec(ref, ct);
		put_setting(ref, s1, o1); put_setting(ref, s2, o2); 
		return this;
	}

	public pProperty addCollec(String ref, Class<?> ct, String s1, Object o1, String s2, Object o2, String s3, Object o3) {
		addCollec(ref, ct);
		put_setting(ref, s1, o1); put_setting(ref, s2, o2); put_setting(ref, s3, o3); 
		return this;
	}

	public pProperty addCollec(String ref, Class<?> ct, String s1, Object o1, String s2, Object o2, String s3, Object o3, String s4, Object o4) {
		addCollec(ref, ct);
		put_setting(ref, s1, o1); put_setting(ref, s2, o2); put_setting(ref, s3, o3); put_setting(ref, s4, o4); 
		return this;
	}
	
	public pProperty addCollec(String ref, Class<?> ct, String s1, Object o1, String s2, Object o2, String s3, Object o3, String s4, Object o4, String s5, Object o5) {
		addCollec(ref, ct);
		put_setting(ref, s1, o1); put_setting(ref, s2, o2); put_setting(ref, s3, o3); put_setting(ref, s4, o4); put_setting(ref, s5, o5);
		return this;
	}
	
	

	public pProperty addData(String ref, Object def, String s1, Object o1) {
		addData(ref, def);
		put_setting(ref, s1, o1); 
		return this; }
	public pProperty addData(String ref, Class<?> ct, String s1, Object o1) {
		addData(ref, ct);
		put_setting(ref, s1, o1); 
		return this; }

	public pProperty addData(String ref, Object def, String s1, Object o1, String s2, Object o2) {
		addData(ref, def);
		put_setting(ref, s1, o1); put_setting(ref, s2, o2); 
		return this; }
	public pProperty addData(String ref, Class<?> ct, String s1, Object o1, String s2, Object o2) {
		addData(ref, ct);
		put_setting(ref, s1, o1); put_setting(ref, s2, o2); 
		return this; }

	public pProperty addData(String ref, Object def, String s1, Object o1, String s2, Object o2, String s3, Object o3) {
		addData(ref, def);
		put_setting(ref, s1, o1); put_setting(ref, s2, o2); put_setting(ref, s3, o3); 
		return this; }
	public pProperty addData(String ref, Class<?> ct, String s1, Object o1, String s2, Object o2, String s3, Object o3) {
		addData(ref, ct);
		put_setting(ref, s1, o1); put_setting(ref, s2, o2); put_setting(ref, s3, o3); 
		return this; }

	public pProperty addData(String ref, Object def, String s1, Object o1, String s2, Object o2, String s3, Object o3, String s4, Object o4) {
		addData(ref, def);
		put_setting(ref, s1, o1); put_setting(ref, s2, o2); put_setting(ref, s3, o3); put_setting(ref, s4, o4); 
		return this; }
	public pProperty addData(String ref, Class<?> ct, String s1, Object o1, String s2, Object o2, String s3, Object o3, String s4, Object o4) {
		addData(ref, ct);
		put_setting(ref, s1, o1); put_setting(ref, s2, o2); put_setting(ref, s3, o3); put_setting(ref, s4, o4); 
		return this; }

	public pProperty addData(String ref, Object def, String s1, Object o1, String s2, Object o2, String s3, Object o3, String s4, Object o4, String s5, Object o5) {
		addData(ref, def);
		put_setting(ref, s1, o1); put_setting(ref, s2, o2); put_setting(ref, s3, o3); put_setting(ref, s4, o4); put_setting(ref, s5, o5);
		return this; }
	public pProperty addData(String ref, Class<?> ct, String s1, Object o1, String s2, Object o2, String s3, Object o3, String s4, Object o4, String s5, Object o5) {
		addData(ref, ct);
		put_setting(ref, s1, o1); put_setting(ref, s2, o2); put_setting(ref, s3, o3); put_setting(ref, s4, o4); put_setting(ref, s5, o5);
		return this; }
	
	private void put_setting(String ref, String s, Object o) {
		nMap<Object> data_set = settings.get(ref);
		if (data_set == null) { 
			data_set = new nMap<Object>();
			settings.put(ref, data_set);
		}
		data_set.put(s,Utl.copy(o));
	}
	
	

	
}
