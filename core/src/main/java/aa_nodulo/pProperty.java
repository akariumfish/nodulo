package aa_nodulo;

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
import util.nRuns;
import util.nSortedArray;
import app.App;
import patch.pPar;
import patch.pPatch;
import patch.pProcess;
import patch.pStandard;
import patch.pStandard.RunDef;

public class pProperty extends Utl.OrderedPriorizableImplement {

	public static nMap<pProperty> body_propertys = new nMap<pProperty>(); 
	public static nMap<pProperty> body_common_propertys = new nMap<pProperty>(); 
	
	public static pProperty get(String r) {
		return body_propertys.get(r);
	}
	
	public static pProperty newProperty(String r) { return newProperty(r,false); }
	
	public static pProperty newProperty(String r, boolean addable) {
		if (body_propertys.hasKey(r)) {
			Utl.logn("ERROR: cant add property, <"+r+"> allready exist");
			return null; }
		pProperty p = new pProperty(r);
//		if (addable) p.addable();
		return p;
	}
	
//	public static pProperty general_prop_abstract;
//	public static pProperty user_prop_abstract;
//	
//	public static void build() {
//		
//		
//		general_prop_abstract = new pProperty("general_prop_abstract")
//		.addNodeRun(new nRun() { public void run() {	
//			pStandard stand = arg(0,pStandard.class);
//			if (stand == null) return;
//			
//			
//		}})
//		
//		;
//
//		user_prop_abstract = new pProperty("user_prop_abstract")
//		
//		;
//		
//		
//	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

	public static nMap<pProperty> general_propertys = new nMap<pProperty>(); 

	public static pProperty newGeneralProperty(String r) {
		if (body_propertys.hasKey(r)) {
			Utl.logn("ERROR: cant add property, <"+r+"> allready exist");
			return null; }
		pProperty p = new pProperty(r);
//		p.copy(general_prop_abstract);
		p.setCommon();
		p.is_general = true;
		general_propertys.put(r,p);
		return p;
	}
	
	public static void complete_generals() {
		for (pProperty gene : general_propertys.all()) {
			for (pProperty need : gene.need_props) {
				for (Map.Entry<Class<?>, nMap<Integer>> me : need.data_vals.entrySet()) {
					Class<?> ct = me.getKey();
					if (me.getValue() != null) {
						for (Map.Entry<String,Integer> map_me : me.getValue().entrySet()) {
							Object df = need.data_defs.get(ct).get(map_me.getKey());
							String rf = "def_"+need.ref+"_"+map_me.getKey();
							if (df != null) gene.addData(rf, df);
							else gene.addData(rf, Utl.new_object(ct));
						}		
					}
				}
			}
			for (pProperty opt : gene.option_props) {
				for (Map.Entry<Class<?>, nMap<Integer>> me : opt.data_vals.entrySet()) {
					Class<?> ct = me.getKey();
					if (me.getValue() != null) {
						for (Map.Entry<String,Integer> map_me : me.getValue().entrySet()) {
							Object df = opt.data_defs.get(ct).get(map_me.getKey());
							String rf = "def_"+opt.ref+"_"+map_me.getKey();
							if (df != null) gene.addData(rf, df);
							else gene.addData(rf, Utl.new_object(ct));
						}		
					}
				}
			}
		}
	}

	
	public pProperty getGeneral() { return general; }

	public pProperty newLocalProperty(String r) {
		if (!is_general) return null;
		pProperty p = newProperty(r,false);
//		p.copy(user_prop_abstract);
		need_props.add(p);
		p.general = this;
		return p; 
	}
	public pProperty newOptionalLocalProperty(String r) { 
		return newOptionalLocalProperty(r,false); }
	public pProperty newOptionalLocalProperty(String r, boolean autoadd) {
		if (!is_general) return null;
		pProperty p = newProperty(r,false);
//		p.copy(user_prop_abstract);
		option_props.add(p);
		p.general = this;
		addData("use_"+r, autoadd);
		return p; 
	}
	public ArrayList<pProperty> need_props = new ArrayList<pProperty>();
	public ArrayList<pProperty> option_props = new ArrayList<pProperty>();
	
	public pProperty general = null;
	public boolean is_general = false;
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

	public pProperty copy(String s) {
		copy(pProperty.get(s));
		return this;
	}
	public pProperty copy(pProperty s) {
		if (s == null) return this;
		
		mode_runtime = s.mode_runtime;
		mode_nosync = s.mode_nosync;
		mode_fullsync = s.mode_fullsync;
		mode_localval = s.mode_localval;
		mode_common = s.mode_common;
		
		grouping_flag = Utl.copy(s.grouping_flag);

		for (nRun n : s.node_run) node_run.add(n);

		for (nRun n : s.body_clear_runs) body_clear_runs.add(n);

		for (nRun n : s.body_init_runs) body_init_runs.add(n);
//		for (nRun n : s.body_clear_run) body_clear_run.add(n);
		
//		for (nRun n : s.init_runs) addInitRun(n);
//		for (nRun n : s.clear_runs) addClearRun(n);
		for (nRun n : s.frame_runs) addFrameRun(n);
		for (nRun n : s.tick_runs) addTickRun(n);
//		for (nRun n : s.draw_runs) addDrawRun(n);
		
		
		for (Map.Entry<String,Integer> me : s.collec_vals.entrySet()) {
			String k = me.getKey();
			if (Utl.contains(s.collec_bodys, k)) {
				addCollecBody(k);
			} else if (s.collec_props.hasKey(k)) {
				addCollecRef(k, s.collec_props.get(k));
			} else if (s.collec_datas.hasKey(k)) {
				addCollec(k,Utl.type_ref_class.get(s.collec_datas.get(k)));
			}
		}
		
		for (Map.Entry<String,Integer> me : s.body_vals.entrySet()) {
			String k = me.getKey();
			addBody(k);
		}

		for (Map.Entry<String,Integer> me : s.ref_vals.entrySet()) {
			String k = me.getKey();
			addRef(k,s.ref_props.get(k));
		}
		
		for (Map.Entry<Class<?>, nMap<Integer>> me : s.data_vals.entrySet()) {
			Class<?> ct = me.getKey();
			for (Map.Entry<String,Integer> map_me : me.getValue().entrySet()) {
				String k = map_me.getKey();
				addData(k,s.data_defs.get(ct).get(k));
			}
		}
		
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
	
	public nRuns body_init_runs = new nRuns();
	public nRuns body_clear_runs = new nRuns();

	public pProperty addBodyInitRun(nRun n) { body_init_runs.add(n); return this; }
	public pProperty addBodyClearRun(nRun r) { body_clear_runs.add(r); return this; }
	
	
	

	public pProperty setPrio(int p) { prio = p; return this; }
	public pProperty setPre() { pre = true; mid = false; return this; }
	public pProperty setPost() { post = true; mid = false; return this; }
	public pProperty setPreMid() { pre = true; return this; }
	public pProperty setMidPost() { post = true; return this; }
	public pProperty setPreMidPost() { post = true; pre = true; return this; }
	
	
	
	
	//     TODO                    this is not used :

//	public static void run_create(pSpace space) 		{ for (pProperty prop : with_create_run) 	for (pParam p : space.param_pools.get(prop.ref).all()) prop.create_runs.do_run(p); }
//	public static void run_load(pSpace space) 		{ for (pProperty prop : with_load_run) 		for (pParam p : space.param_pools.get(prop.ref).all()) prop.load_runs.do_run(p); }
//	public static void run_save(pSpace space) 		{ for (pProperty prop : with_save_run) 		for (pParam p : space.param_pools.get(prop.ref).all()) prop.save_runs.do_run(p); }
//	public static void run_init(pSpace space) 		{ for (pProperty prop : with_init_run) 		for (pParam p : space.param_pools.get(prop.ref).all()) prop.init_runs.do_run(p); }
//	public static void run_finalize(pSpace space) 	{ for (pProperty prop : with_finalize_run) 	for (pParam p : space.param_pools.get(prop.ref).all()) prop.finalize_runs.do_run(p); }
//	public static void run_clear(pSpace space) 		{ for (pProperty prop : with_clear_run) 		for (pParam p : space.param_pools.get(prop.ref).all()) prop.clear_runs.do_run(p); }
	public static void run_frame(pSpace space) 		{ for (pProperty prop : with_frame_run) 		for (pParam p : space.param_pools.get(prop.ref).all()) prop.frame_runs.do_run(p); }
	public static void run_tick(pSpace space) 		{ for (pProperty prop : with_tick_run) 		for (pParam p : space.param_pools.get(prop.ref).all()) prop.tick_runs.do_run(p); }
//	public static void run_draw(pSpace space) 		{ for (pProperty prop : with_draw_run) 		for (pParam p : space.param_pools.get(prop.ref).all()) prop.draw_runs.do_run(p); }
	
//	public void run_create(ArrayList<pParam> l) 		{ for (pParam p : l) create_runs.do_run(p); }
//	public void run_load(ArrayList<pParam> l) 		{ for (pParam p : l) load_runs.do_run(p); }
//	public void run_save(ArrayList<pParam> l) 		{ for (pParam p : l) save_runs.do_run(p); }
//	public void run_init(ArrayList<pParam> l) 		{ for (pParam p : l) init_runs.do_run(p); }
//	public void run_finalize(ArrayList<pParam> l) 	{ for (pParam p : l) finalize_runs.do_run(p); }
//	public void run_clear(ArrayList<pParam> l) 		{ for (pParam p : l) clear_runs.do_run(p); }
	public void run_frame(ArrayList<pParam> l) 		{ for (pParam p : l) frame_runs.do_run(p); }
	public void run_tick(ArrayList<pParam> l) 		{ for (pParam p : l) tick_runs.do_run(p); }
//	public void run_draw(ArrayList<pParam> l) 		{ for (pParam p : l) draw_runs.do_run(p); }

//	public void run_create(pParam p) 	{ create_runs.do_run(p); }
//	public void run_load(pParam p) 		{ load_runs.do_run(p); }
//	public void run_save(pParam p) 		{ save_runs.do_run(p); }
	public void run_init(pParam p) 		{ init_runs.setContext(p); init_runs.do_run(p); }
//	public void run_finalize(pParam p) 	{ finalize_runs.do_run(p); }
	public void run_clear(pParam p) 		{ clear_runs.setContext(p); clear_runs.do_run(p); }
	public void run_frame(pParam p) 		{ frame_runs.setContext(p); frame_runs.do_run(p); }
	public void run_tick(pParam p) 		{ tick_runs.setContext(p); tick_runs.do_run(p); }
//	public void run_draw(pParam p) 		{ draw_runs.do_run(p); }
	
//	public nRuns create_runs = new nRuns().setAutoSorted();
//	public nRuns load_runs = new nRuns().setOrdered().setPrioritized().setAutoSorted();
//	public nRuns save_runs = new nRuns().setOrdered().setPrioritized().setAutoSorted();
	public nRuns init_runs = new nRuns().setAutoSorted();
//	public nRuns finalize_runs = new nRuns().setAutoSorted();
	public nRuns clear_runs = new nRuns().setPrioritized().setAutoSorted();
	public nRuns frame_runs = new nRuns().setOrdered().setPrioritized().setAutoSorted();
	public nRuns tick_runs = new nRuns().setOrdered().setPrioritized().setAutoSorted();	
//	public nRuns draw_runs = new nRuns().setOrdered().setPrioritized().setAutoSorted();	

//	public pProperty addCreateRun(nRun r) 	{ with_create_run.addOne(this); create_runs.add(r); return this; }
//	public pProperty addLoadRun(nRun r) 		{ with_load_run.addOne(this); load_runs.add(r); return this; }
//	public pProperty addSaveRun(nRun r) 		{ with_save_run.addOne(this); save_runs.add(r); return this; }
	public pProperty addInitRun(nRun n) 		{ with_init_run.addOne(this); init_runs.add(n); return this; }
//	public pProperty addFinalizeRun(nRun r) 	{ with_finalize_run.addOne(this); finalize_runs.add(r); return this; }
	public pProperty addClearRun(nRun r) 	{ with_clear_run.addOne(this); clear_runs.add(r); return this; }
	public pProperty addFrameRun(nRun r) 	{ with_frame_run.addOne(this); frame_runs.add(r); return this; }
	public pProperty addTickRun(nRun n) 		{ with_tick_run.addOne(this); tick_runs.add(n); return this; }
//	public pProperty addDrawRun(nRun n) 		{ with_draw_run.addOne(this); draw_runs.add(n); return this; }
	
//	private static nSortedArray<pProperty> with_create_run = new nSortedArray<pProperty>();
//	private static nSortedArray<pProperty> with_load_run = new nSortedArray<pProperty>().setOrdered().setPrioritized().setAutoSorted();
//	private static nSortedArray<pProperty> with_save_run = new nSortedArray<pProperty>().setOrdered().setPrioritized().setAutoSorted();
	private static nSortedArray<pProperty> with_init_run = new nSortedArray<pProperty>().setPrioritized().setAutoSorted();
//	private static nSortedArray<pProperty> with_finalize_run = new nSortedArray<pProperty>().setPrioritized().setAutoSorted();
	private static nSortedArray<pProperty> with_clear_run = new nSortedArray<pProperty>().setPrioritized().setAutoSorted();
	private static nSortedArray<pProperty> with_frame_run = new nSortedArray<pProperty>().setOrdered().setPrioritized().setAutoSorted();
	private static nSortedArray<pProperty> with_tick_run = new nSortedArray<pProperty>().setOrdered().setPrioritized().setAutoSorted();
//	private static nSortedArray<pProperty> with_draw_run = new nSortedArray<pProperty>().setOrdered().setPrioritized().setAutoSorted();
	
	
	

	
	
	
	
	

	public pProperty newRun(String r, nRun run) { new RunDef(r, run); return this; }
	public pProperty replaceRun(String r, nRun run) {
		RunDef old = getRunDef(r);
		if (old != null) rundefs.remove(old);
		new RunDef(r, run); return this; }

	public RunDef getRunDef(String r) {
		for (RunDef d : rundefs) if (d.ref.equals(r)) return d; return null; }
	
	public ArrayList<RunDef> rundefs = new ArrayList<RunDef>(); 
	
	public class RunDef {
		public String ref;
		public nRun run;
		public RunDef(RunDef r) {
			ref = Utl.copy(r.ref); 
			run = r.run;
			rundefs.add(this);
		}
		public RunDef(String r, nRun rn) {//Class<?> ct, 
			ref = r; run = rn;
			rundefs.add(this);
		}
	}

	
	
	
	
//	public class Entry {
//		public int id = 0; 
//		public String ref;
//		public Class<?> content;
//		public Entry(String r, Class<?> c) { 
//			ref = Utl.copy(r); content = c; id = entrys.size(); entrys.put(r, this); 
//			if (c == Integer.class) int_entrys.put(r, id); 
//			else if (c == Float.class) flt_entrys.put(r, id); 
//			else if (c == Boolean.class) boo_entrys.put(r, id); 
//			else if (c == String.class) str_entrys.put(r, id); 
//			else if (c == Vector2.class) vec_entrys.put(r, id); 
//			else if (c == pParam.class) par_entrys.put(r, id); 
//			else if (c == pBody.class) bod_entrys.put(r, id);
//			else { Utl.logn("ERROR pProperty.Entry : unknown type"); }
//		}
//	}
//
//	public nMap<Entry> entrys = new nMap<Entry>();
//	
//	public nMap<Integer> getEntryMapOfType(Class<?> c) {
//		if (c == Integer.class) return int_entrys; 
//		else if (c == Float.class) return flt_entrys; 
//		else if (c == Boolean.class) return boo_entrys; 
//		else if (c == String.class) return str_entrys; 
//		else if (c == Vector2.class) return vec_entrys; 
//		else if (c == pParam.class) return par_entrys; 
//		else if (c == pBody.class) return bod_entrys;
//		else {
//			Utl.logn("ERROR pProperty getEntryMapOfType : unknown type : "+ (c != null ? c.getName() : "null"));
//			return null;
//		}
//	}
//	public nMap<Integer> int_entrys = new nMap<Integer>();
//	public nMap<Integer> flt_entrys = new nMap<Integer>();
//	public nMap<Integer> boo_entrys = new nMap<Integer>();
//	public nMap<Integer> str_entrys = new nMap<Integer>();
//	public nMap<Integer> vec_entrys = new nMap<Integer>();
//	public nMap<Integer> par_entrys = new nMap<Integer>();
//	public nMap<Integer> bod_entrys = new nMap<Integer>();
//	
//	public Entry N_addEntry(String ref, Class<?> cont) {
//		if (ref == null || cont == null) { Utl.logn("ERROR pPoperty.addEntry : got null arg "); return null; }
//		if (entrys.hasKey(ref)) { Utl.logn("ERROR pPoperty.addEntry : ref allready exist : "+ref); return null; }
//		return new Entry(ref, cont);
//	}
//	
//	public pProperty N_addData(String r, Class<?> cont, Object ... settings) {
//		N_addEntry(ref, cont);
//		return this; 
//	} 
//	public pProperty N_addParam(String r, String param_prop_ref, Object ... settings) {
//		N_addEntry(ref, pParam.class);
//		return this; 
//	} 
//	public pProperty N_addBody(String r, Object ... settings) {
//		N_addEntry(ref, pBody.class);
//		return this; 
//	} 
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
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
			Utl.logn("ERROR : pProperty "+this.ref+" has allready the key "+ref);
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
			Utl.logn("ERROR : pProperty "+this.ref+" has allready the key "+ref_in_param);
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
			Utl.logn("ERROR : pProperty "+this.ref+" has allready the key "+ref_in_param);
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
			Utl.logn("ERROR : pProperty "+this.ref+" has allready the key "+ref);
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

	private void put_setting(String ref, String s, Object o) {
		nMap<Object> data_set = settings.get(ref);
		if (data_set == null) { 
			data_set = new nMap<Object>();
			settings.put(ref, data_set);
		}
		data_set.put(s,Utl.copy(o));
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
	

	
}
