package zz_patch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.math.Vector2;

import data.*;
import gui.nInterfCommand;
import gui.nInterface.Code;
import util.Utl;
import util.nMap;
import util.nRun;
import zz_applet.Applet;
import zz_plane.pPlane;

public class pStandard {
	
	public static nMap<pStandard> standards;
	public static Applet app;
	
	public static void build(Applet a) {
		app = a; 
		standards = new nMap<pStandard>(); 
	}

	public static pStandard newAbstractStandard() {
		int c = 0; String r = "abstract_"; String ref = r+c;
		while (standards.hasKey(ref)) { c++; ref = r+c; }
		pStandard p = new pStandard(ref, ""); return p; }
	public static pStandard newStandard(String ref, String pool_ref) {
		if (standards.hasKey(ref)) {
			app.logn("ERROR: cant create standard, <"+ref+"> allready exist"); return null; }
		pStandard p = new pStandard(ref, pool_ref); return p; }
	public static pStandard get(String r) {
		return standards.get(r); }
	
	
	
	
	
	
	
	
	

	public String ref;

	public String pool_ref;
	
	
	
	public pStandard(String r, String pr) {
		ref = r; 
		pool_ref = pr; 
		standards.put(r, this);
		data_used = new int[Utl.data_type_nb];
		for (int i = 0 ; i < Utl.data_type_nb ; i++) data_used[i] = 0;
		def_param = new pPar();
		current_param = def_param;
		current_sec = null;
	}
	
	public pStandard initInstanceObj(pInstance cont) {
		if (cont.obj_is_init) return this;
		for (ObjDef od : objdefs) {
			Object o = od.newobj.do_get(cont, od.param);
			cont.setObject(od.ref,o); }
		cont.obj_is_init = true;
		return this; }

	public pStandard createInstance(pInstance cont, Object ... args) {
//		if (args != null) app.log("stand "+ref+" create instance with "+args.length+" args");
//		else app.log("stand "+ref+" create instance with 0 args");
		initInstanceObj(cont);
		create_run.do_run(cont, args);
		nRun.runList(create_runs, cont, args);
		return this; }

	public pStandard initInstance(pInstance cont) {
		initInstanceObj(cont);
		init_run.do_run(cont);
		nRun.runList(init_runs, cont);
		for (pProcess p : procs.all()) p.useInit().exec(cont, proc_pars.get(p.ref));
		return this; }

	public pStandard loadInstance(pInstance cont) {
		load_run.do_run(cont);
		nRun.runList(load_runs, cont);
		for (pProcess p : procs.all()) p.useLoad().exec(cont, proc_pars.get(p.ref));
		return this; }

	public pStandard saveInstance(pInstance cont) {
		nRun.runList(save_runs, cont);
		for (pProcess p : procs.all()) p.useSave().exec(cont, proc_pars.get(p.ref));
		return this; }

	public pStandard clearInstance(pInstance cont) {
		clear_run.do_run(cont);
		nRun.runList(clear_runs,cont);
		for (pProcess p : procs.all()) p.useClear().exec(cont, proc_pars.get(p.ref));
		return this; }

	public ArrayList<nRun> create_runs = new ArrayList<nRun>();
	public ArrayList<nRun> init_runs = new ArrayList<nRun>();
	public ArrayList<nRun> load_runs = new ArrayList<nRun>();
	public ArrayList<nRun> save_runs = new ArrayList<nRun>();
	public ArrayList<nRun> clear_runs = new ArrayList<nRun>();
	public pStandard addCreateRun(nRun r) { create_runs.add(r); return this; }
	public pStandard addInitRun(nRun r) { init_runs.add(r); return this; }
	public pStandard addLoadRun(nRun r) { load_runs.add(r); return this; }
	public pStandard addSaveRun(nRun r) { save_runs.add(r); return this; }
	public pStandard addClearRun(nRun r) { clear_runs.add(r); return this; }

	public nRun create_run = new nRun() {};
	public nRun init_run = new nRun() {};
	public nRun load_run = new nRun() {};
	public nRun clear_run = new nRun() {};

	public pStandard setCreateRun(nRun r) { create_run = r; return this; }
	public pStandard setInitRun(nRun r) { init_run = r; return this; }
	public pStandard setLoadRun(nRun r) { load_run = r; return this; }
	public pStandard setClearRun(nRun r) { clear_run = r; return this; }
	
	

	public pStandard run(nRun p, Object ... args) {
		if (p == null) return this;
		int l = 1;
		if (args != null) l += args.length;
		Object[] a = new Object[l]; a[0] = this;
		for (int i = 0 ; i < args.length ; i++) a[i+1] = args[i];
		p.do_run(current_param, a);
		return this; }
	

	
	
	
	public nMap<pProcess> procs = new nMap<pProcess>();

	public nMap<pPar> proc_pars = new nMap<pPar>();
	
	private int new_proc_cnt = 0;
	public pProcess process() {  
		pProcess p = pProcess.newProcess(ref+"_proc_"+new_proc_cnt);
		new_proc_cnt++;
		p.creator_stand = this;
		procs.put(p.ref, p); proc_pars.put(p.ref, new pPar(current_param)); return p; }
	public pProcess process(String r) { 
		pProcess p = pProcess.newProcess(ref+"_"+r+"_proc");
		p.creator_stand = this;
		procs.put(p.ref, p); proc_pars.put(p.ref, new pPar(current_param)); return p; }
	public pProcess process(pProcess r, pPar par) { 
		pProcess p = pProcess.newProcess(ref+"_"+r+"_proc");
		p.creator_stand = this;
		p.append(r);
		procs.put(p.ref, p); proc_pars.put(p.ref, new pPar(par)); return p; }
	
	

	public pPar def_param = new pPar();
	public pPar current_param = def_param;

	public pStandard param(Object ... args) {
		if (args == null) return this; 
		if (args.length%2 != 0) return this; 
		String k = null;
		for (Object a : args) {
			if (k == null && (a instanceof String)) { k = (String)a; }
			else if (k != null) { current_param.set(k,a); k = null; }  }
		return this;
	} 
	private void setcurrentsec(Section s) {
		current_sec = s;
		if (s != null) current_param = s.param;
		else current_param = def_param;
	} 
	
	public ArrayList<Section> all_sections = new ArrayList<Section>();
	public ArrayList<Section> sections = new ArrayList<Section>();
	public Section current_sec = null; 

	public pStandard openSec() {
		if (current_sec == null) {
			Section s = new Section(this);
			setcurrentsec(s);
		} else {
			Section s = new Section(current_sec);
			setcurrentsec(s); }
		return this;
	}
	public pStandard closeSec() {
		if (current_sec != null) {
			if (current_sec.sur_sec instanceof pStandard) setcurrentsec(null);
			else setcurrentsec((Section)current_sec.sur_sec);
		}
		return this;
	}
	
	public class Section {
		public Object sur_sec = null;
		public pStandard standard = null;
		public ArrayList<Section> sections = new ArrayList<Section>();
		public pPar param;
		public Section(pStandard s) { 
			standard = s; sur_sec = s;
			s.sections.add(this);
			standard.all_sections.add(this);
			param = new pPar(s.def_param);
		}
		public Section(Section s) { 
			standard = s.standard; sur_sec = s;
			s.sections.add(this);
			standard.all_sections.add(this);
			param = new pPar(s.param);
		}
		public Section() {}
		public void clear() {
			for (Section s : sections) s.clear();
			sections.clear();
			param.clear(); param = null;
			sur_sec = null; standard = null;
		}
	}
	
	
	
	private RunDef new_rundef = null;
	public pStandard newRun(String r, nRun run) {
		RunDef rd = new RunDef(r, null, run);
		new_rundef = rd; return this;
	}
	public pStandard replaceRun(String r, nRun run) {
		RunDef old = getRunDef(r);
		if (old != null) rundefs.remove(old);
		RunDef rd = new RunDef(r, null, run);
		if (old != null) rd.param = new pPar(old.param);
		return this;
	}
	public pStandard newRun(String r, Class<?> ct, nRun run) {
		RunDef rd = new RunDef(r, ct, run);
		new_rundef = rd; return this;
	}
	public pStandard replaceRun(String r, Class<?> ct, nRun run) {
		RunDef old = getRunDef(r);
		if (old != null) rundefs.remove(old);
		RunDef rd = new RunDef(r, ct, run);
		if (old != null) rd.param = new pPar(old.param);
		return this;
	}
	public pStandard runArgs(Object ... args) {
		if (new_rundef != null) {
			if (args == null) return this; 
			if (args.length%2 != 0) return this; 
			String k = null; int cnt = 0;
			String[] ar = new String[args.length/2]; 
			Class<?>[] ac = new Class<?>[args.length/2];
			for (Object a : args) {
				if (k == null && (a instanceof String)) { k = (String)a; }
				if (k != null && (a instanceof Class<?>)) { 
					ar[cnt] = k; ac[cnt] = (Class<?>)a;
					k = null; cnt++; } 
			}
			new_rundef.set_arg(ar,ac);
		}
		return this;
	}

	public RunDef getRunDef(String r) {
		for (RunDef d : rundefs) if (d.ref.equals(r)) return d;
		return null; }
	
	public ArrayList<RunDef> rundefs = new ArrayList<RunDef>(); 
	
	// crun in context.runs 
	public class RunDef {
		public String ref;
		public nRun run;
		public pPar param;
		public String comment = "";
		public Class<?> return_class;
		public int args_nb = 0;
		public String[] args_ref;
		public Class<?>[] args_class;

		public RunDef(RunDef r) {
			ref = Utl.copy(r.ref); return_class = r.return_class; run = r.run;
			args_ref = new String[r.args_ref.length];
			args_class = new Class<?>[r.args_class.length]; 
			args_nb = r.args_nb;
			for (int i = 0 ; i < args_nb ; i++) {
				args_ref[i] = Utl.copy(r.args_ref[i]);
				args_class[i] = r.args_class[i];
			}
			param = new pPar(r.param);
			rundefs.add(this);
		}
		public RunDef(String r, Class<?> ct, nRun rn) {
			ref = r; return_class = ct; run = rn;
			args_ref = new String[0];
			args_class = new Class<?>[0]; 
			args_nb = 0;
			rundefs.add(this);
			param = new pPar(current_param);
		}
		public RunDef set_arg(String[] ar, Class<?>[] ac) {
			if ((ar != null) != (ac != null)) return this;
			if (ar != null && ac != null && ar.length != ac.length) return this;
			if (ar != null && ac != null) {
				args_ref = new String[ar.length];
				System.arraycopy( ar, 0, args_ref, 0, ar.length );
				args_class = new Class<?>[ac.length];
				System.arraycopy( ac, 0, args_class, 0, ac.length );
			} else {
				args_ref = new String[0];
				args_class = new Class<?>[0]; }
			args_nb = ar.length;
			return this;
		}
		
		public boolean test_args(Object ... v) {
			Object[] args = v;//Utl.toArray(v);
			if (args.length != args_nb) return false;
			for (int i = 0 ; i < args_nb ; i++) {
				if (args[i].getClass() != args_class[i]) return false;
			}
			return true;
		}
	}

	
	
	
	

	private ObjDef new_objdef = null;
	
	public pStandard newObj(String r, nRun newobj) { //, Class<?> ct
		ObjDef rd = new ObjDef(r, newobj);
		new_objdef = rd; return this;
	}

	public ObjDef getObjDef(String r) {
		for (ObjDef d : objdefs) if (d.ref.equals(r)) return d;
		return null; }
	
	public ArrayList<ObjDef> objdefs = new ArrayList<ObjDef>(); 
	
	// object in context.objects 
	public class ObjDef {
		public String ref;
		public String comment = "";
		public pPar param;
//		public Class<?> obj_class;
		public nRun newobj;
		public ObjDef(ObjDef o) { //, Class<?> ct
			ref = Utl.copy(o.ref); newobj = o.newobj; //obj_class = ct; 
			objdefs.add(this);
			param = new pPar(o.param);
		}
		public ObjDef(String r, nRun nwob) { //, Class<?> ct
			ref = r; newobj = nwob; //obj_class = ct; 
			objdefs.add(this);
			param = new pPar(current_param);
		}
	}

	
	
	
	
	
	
	
	
	
	public ArrayList<String> used_key = new ArrayList<String>();
	public boolean add_key(String k) {
		for (String s : used_key) if (s.equals(k)) return true; 
		used_key.add(k); return false; }
	public boolean has_key(String k) {
		for (String s : used_key) if (s.equals(k)) return true;
		return false; }
	
	
	
	

	public nMap<Integer> collec_vals = 
			new nMap<Integer>();
	public ArrayList<String> collec_insts = 
			new ArrayList<String>();
	public nMap<String> collec_inst_pools = 
			new nMap<String>();
	public nMap<String> collec_datas = 
			new nMap<String>();
	public nMap<pPar> collec_pars = new nMap<pPar>();
	
	int collec_used = 0;

	private pStandard addCollec(String ref) {
		if (add_key(ref)) {
			app.logn("ERROR : pStandard "+this.ref+" has allready the key "+ref);
			return this;
		}
		collec_vals.put(ref, collec_used);
		collec_used++; 
		collec_pars.put(ref, new pPar(current_param));
		return this;
	}

	public pStandard addCollec(String ref, Object ... args) {
		addCollec(ref);
		if (args == null) return this; 
		if (args.length%2 != 0) return this; 
		String k = null;
		for (Object a : args) {
			if (k == null && (a instanceof String)) { k = (String)a; }
			else if (k != null) { 
				current_param.set(k,a); 
				collec_pars.replace(ref, new pPar(current_param));
				k = null; 
			}  
		}
		return this;
	}

	public pStandard addCollec(String ref_in_param, Class<?> ct) {
		addCollec(ref_in_param);
		collec_datas.put(ref_in_param, ct.getName());
		return this;
	}

	public pStandard addCollec(String ref, Class<?> ct, Object ... args) {
		addCollec(ref, ct);
		if (args == null) return this; 
		if (args.length%2 != 0) return this; 
		String k = null;
		for (Object a : args) {
			if (k == null && (a instanceof String)) { k = (String)a; }
			else if (k != null) { 
				current_param.set(k,a); 
				collec_pars.replace(ref, new pPar(current_param));
				k = null; 
			}  
		}
		return this;
	}

	public pStandard addCollecInst(String ref_in_param, String pool_ref) {
		addCollec(ref_in_param);
		collec_insts.add(ref_in_param);
		collec_inst_pools.put(ref_in_param, pool_ref);
		return this;
	}

	public pStandard addCollecInst(String ref_in_param, String pool_ref, Object ... args) {
		addCollec(ref_in_param, args);
		collec_insts.add(ref_in_param);
		collec_inst_pools.put(ref_in_param, pool_ref);
		return this;
	}

	public int getCollecUsed() {
		return collec_used; }

	public int getCollecValId(String r) {
		if (collec_vals.get(r) != null) return collec_vals.get(r); else return -1; }

	public boolean isCollecInst(String r) {
		return Utl.contains(collec_insts, r); }

	public String getCollecData(String r) {
		if (collec_datas.get(r) != null) return collec_datas.get(r); 
		else return null; }

	public String getCollecRefFromId(int i) {
		for (Map.Entry<String, Integer> me : collec_vals.entrySet()) {
			if (me.getValue() == i) return me.getKey(); }
		return ""; }
	
	
	
	public nMap<Integer> inst_vals = 
			new nMap<Integer>();
	public nMap<String> inst_pools = 
			new nMap<String>();
	public nMap<pPar> inst_pars = new nMap<pPar>();
	int inst_used = 0;

	public pStandard addInst(String ref_in_param, String pool_ref) {
		if (add_key(ref_in_param)) {
			app.logn("ERROR : pStandard "+this.ref+" has allready the key "+ref_in_param);
			return this;
		}
		inst_vals.put(ref_in_param, inst_used);
		inst_used++; 
		inst_pars.put(ref_in_param, new pPar(current_param));
		inst_pools.put(ref_in_param, pool_ref);
		return this;
	}

	public int getInstUsed() {
		return inst_used; }

	public int getInstValId(String r) {
		if (inst_vals.get(r) != null) return inst_vals.get(r); else return -1; }

	
	

	

	public HashMap<Class<?>, nMap<Integer>> data_vals = 
			new HashMap<Class<?>, nMap<Integer>>();
	public HashMap<Class<?>, nMap<Object>> data_defs = 
			new HashMap<Class<?>, nMap<Object>>();
	public nMap<pPar> data_pars = new nMap<pPar>();

	public int[] data_used;

	public pStandard addData(String ref, Object def) {
		addData(ref, def.getClass());
		nMap<Object> vals_def = data_defs.get(def.getClass());
		vals_def.put(ref, def);
		return this;
	}
	public pStandard addData(String ref, Class<?> ct) {
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
		data_pars.put(ref, new pPar(current_param));
		return this;
	}

	public int getDataUsed(Class<?> ct) {
		return data_used[Utl.type_class_index.get(ct)]; }
	
	public int getDataValId(String r, Class<?> ct) {
		if (data_vals.get(ct) != null && data_vals.get(ct).get(r) != null)
			return data_vals.get(ct).get(r); 
		else return -1; }
	public Object getDataValDef(String r, Class<?> ct) {
		return data_defs.get(ct).get(r); }
	
	public pStandard addData(String ref, Object def, Object ... args) {
		addData(ref, def); 
		if (args == null) return this; 
		if (args.length%2 != 0) return this; 
		String k = null;
		for (Object a : args) {
			if (k == null && (a instanceof String)) k = (String)a;
			if (k != null) { 
				current_param.set(k,a); 
				data_pars.replace(ref, new pPar(current_param));
				k = null; 
			} 
		}
		return this; }
	public pStandard addData(String ref, Class<?> ct, Object ... args) {
		addData(ref, ct);
		if (args == null) return this; 
		if (args.length%2 != 0) return this; 
		String k = null;
		for (Object a : args) {
			if (k == null && (a instanceof String)) k = (String)a;
			if (k != null) { 
				current_param.set(k,a); 
				data_pars.replace(ref, new pPar(current_param));
				k = null; 
			}  
		}
		return this; }
	
	
	

	public pStandard append(pStandard s) {
		create_runs.add(s.create_run);
		init_runs.add(s.init_run);
		load_runs.add(s.load_run);
		clear_runs.add(s.clear_run);

		for (nRun r : s.create_runs) create_runs.add(r);
		for (nRun r : s.init_runs) init_runs.add(r);
		for (nRun r : s.load_runs) load_runs.add(r);
		for (nRun r : s.save_runs) save_runs.add(r);
		for (nRun r : s.clear_runs) clear_runs.add(r);
		
		for (Map.Entry<String,pProcess> me : s.procs.entrySet()) 
			process(me.getValue(), s.proc_pars.get(me.getKey()));
		
		for (RunDef r : s.rundefs) new RunDef(r);
		for (ObjDef r : s.objdefs) new ObjDef(r);
		
		for (String r : s.used_key) used_key.add(r);
		
		for (Map.Entry<String,Integer> me : s.collec_vals.entrySet()) 
			collec_vals.put(me.getKey(), me.getValue());
		for (String r : s.collec_insts) collec_insts.add(r);
		for (Map.Entry<String,String> me : s.collec_datas.entrySet()) 
			collec_datas.put(me.getKey(), me.getValue());

		for (Map.Entry<String,String> me : s.collec_inst_pools.entrySet()) 
			collec_inst_pools.put(me.getKey(), me.getValue());
		
		for (Map.Entry<String,pPar> me : s.collec_pars.entrySet()) 
			collec_pars.put(me.getKey(), new pPar(me.getValue()));
		
		collec_used += s.collec_used;
		inst_used += s.inst_used;
		
		for (Map.Entry<String,Integer> me : s.inst_vals.entrySet()) 
			inst_vals.put(me.getKey(), me.getValue());

		for (Map.Entry<String,String> me : s.inst_pools.entrySet()) 
			inst_pools.put(me.getKey(), me.getValue());
		
		for (Map.Entry<String,pPar> me : s.inst_pars.entrySet()) 
			inst_pars.put(me.getKey(), new pPar(me.getValue()));
		
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
		
		for (Map.Entry<String,pPar> me : s.data_pars.entrySet()) 
			data_pars.put(me.getKey(), new pPar(me.getValue()));
		
		return this;
	}

	public pStandard copy(pStandard s) {
		create_run = s.create_run;
		init_run = s.init_run;
		load_run = s.load_run;
		clear_run = s.clear_run;

		for (nRun r : s.create_runs) create_runs.add(r);
		for (nRun r : s.init_runs) init_runs.add(r);
		for (nRun r : s.load_runs) load_runs.add(r);
		for (nRun r : s.save_runs) save_runs.add(r);
		for (nRun r : s.clear_runs) clear_runs.add(r);
		
		for (Map.Entry<String,pProcess> me : s.procs.entrySet()) 
			procs.put(me.getKey(), me.getValue());
		for (Map.Entry<String,pPar> me : s.proc_pars.entrySet()) 
			proc_pars.put(me.getKey(), new pPar(me.getValue()));
		
		def_param = new pPar(s.def_param);
		current_param = s.current_param;
		
		for (RunDef r : s.rundefs) new RunDef(r);
		for (ObjDef r : s.objdefs) new ObjDef(r);
		
		for (String r : s.used_key) used_key.add(r);
		
		for (Map.Entry<String,Integer> me : s.collec_vals.entrySet()) 
			collec_vals.put(me.getKey(), me.getValue());
		for (String r : s.collec_insts) collec_insts.add(r);
		for (Map.Entry<String,String> me : s.collec_datas.entrySet()) 
			collec_datas.put(me.getKey(), me.getValue());

		for (Map.Entry<String,String> me : s.collec_inst_pools.entrySet()) 
			collec_inst_pools.put(me.getKey(), me.getValue());
		
		for (Map.Entry<String,pPar> me : s.collec_pars.entrySet()) 
			collec_pars.put(me.getKey(), new pPar(me.getValue()));
		
		collec_used = s.collec_used;
		inst_used = s.inst_used;
		
		for (Map.Entry<String,Integer> me : s.inst_vals.entrySet()) 
			inst_vals.put(me.getKey(), me.getValue());

		for (Map.Entry<String,String> me : s.inst_pools.entrySet()) 
			inst_pools.put(me.getKey(), me.getValue());
		
		for (Map.Entry<String,pPar> me : s.inst_pars.entrySet()) 
			inst_pars.put(me.getKey(), new pPar(me.getValue()));
		
		data_used = new int[s.data_used.length];
		for (int i = 0 ; i < s.data_used.length ; i++) data_used[i] = s.data_used[i];

		for (Map.Entry<Class<?>, nMap<Integer>> me : s.data_vals.entrySet()) {
			Class<?> ct = me.getKey();
			nMap<Integer> map = new nMap<Integer>();
			for (Map.Entry<String,Integer> map_me : me.getValue().entrySet()) 
				map.put(map_me.getKey(), map_me.getValue());
			data_vals.put(ct,map);
		}
		
		for (Map.Entry<Class<?>, nMap<Object>> me : s.data_defs.entrySet()) {
			Class<?> ct = me.getKey();
			nMap<Object> map = new nMap<Object>();
			for (Map.Entry<String,Object> map_me : me.getValue().entrySet()) 
				map.put(map_me.getKey(), Utl.copy(map_me.getValue()));
			data_defs.put(ct,map);
		}
		
		for (Map.Entry<String,pPar> me : s.data_pars.entrySet()) 
			data_pars.put(me.getKey(), new pPar(me.getValue()));
		
		
		all_sections = new ArrayList<Section>();
		sections = new ArrayList<Section>();
		current_sec = null;
		
		HashMap<Section,Section> secmap = new HashMap<Section,Section>();
//		HashMap<Section,Section> secmap2 = new HashMap<Section,Section>();
		for (Section r : s.all_sections) {
			Section ns = new Section();
			ns.standard = this;
			ns.standard.all_sections.add(ns);
			ns.param = new pPar(r.param);
			secmap.put(r,ns);
//			secmap2.put(ns,r);
		}
		for (Section r : s.all_sections) {
			Section ns = secmap.get(r);
			
			if (r.sur_sec == r.standard) ns.sur_sec = this;
			else {
				Section ss = secmap.get(r.sur_sec);
				ns.sur_sec = ss;
				ss.sections.add(ns);
			}
		}
		for (Section r : s.all_sections) 
			if (r == s.current_sec) current_sec = secmap.get(r);
		
		return this;
	}

	public int data_size() {
		int cnt = 2;
//		cnt++;
		for (int i = 0 ; i < Utl.data_type_nb ; i++) 
			cnt += data_used[i] * Utl.type_data_size.get(Utl.data_type[i]);
		cnt += collec_used;
		cnt += inst_used;
		cnt += 1;// + var_vals.size() * 3;
		return cnt;
	}
	public void def_to_tab(sTab t, int c) {

		t.setRowHeight(c, data_size());
		int cnt = 0;
		
		t.set(c, cnt, true); cnt++; 
		t.set(c, cnt, ref); cnt++; 
		
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

		for (int j = 0 ; j < inst_used ; j++) {
			t.set(c, cnt+j, "");
		}
		cnt += inst_used;
		
		t.set(c, cnt, 0); cnt++; // no var
	}

	
	
	
}
