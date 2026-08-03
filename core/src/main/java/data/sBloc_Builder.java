package data;

import java.util.ArrayList;

import app.Applet;
import util.Utl;
import util.nRun;


public class sBloc_Builder {
	sData data;
	public String ref;
	public boolean isSolo = false;
//	public sValueBloc preset_bloc;
//	public nWidget root_interf_w = null;
	public sBloc_Builder(sData p, String t) { 
		if (p.bloc_builders.get(t) != null) {
			p.app.logn("ERROR : sBloc_Builder "+t+" allready exist"); return; }
		data = p; ref = t; data.bloc_builders.put(t, this); 
//		preset_bloc = data.data_space.newRootBloc(ref);
	}
	
	public void run_init(Object o) {
		if (init_run != null) init_run.run(o);
		nRun.runEvents(init_events);
		nRun.runEvents(init_events, o); }
	public void run_load(Object o) {
		if (load_run != null) load_run.run(o);
		nRun.runEvents(load_events);
		nRun.runEvents(load_events, o); }
	public void run_clear(Object o) {
		if (clear_run != null) clear_run.run(o);
		nRun.runEvents(clear_events);
		nRun.runEvents(clear_events, o); }
	
	
	
	
	
	
	
	public sBloc_Builder copy(sBloc_Builder r) { 
		
		builder_add_run = r.builder_add_run;
		init_run = r.init_run;
		load_run = r.load_run;
		clear_run = r.clear_run;

		for (nRun n : r.init_events) init_events.add(n);
		for (nRun n : r.load_events) load_events.add(n);
		for (nRun n : r.clear_events) clear_events.add(n);
		
		isSolo = r.isSolo;
		
		for (RunDef rd : r.rundefs) new RunDef(rd);

		return this; 
	}
	
	
	nRun builder_add_run = null, init_run, load_run = null, clear_run;
	
	public sBloc_Builder setInitRun(nRun r) { init_run = r; return this; }
	public sBloc_Builder setLoadRun(nRun r) { load_run = r; return this; }
	public sBloc_Builder setClearRun(nRun r) { clear_run = r; return this; }
	public sBloc_Builder setBuilderAddRun(nRun r) { builder_add_run = r; return this; }
	
	ArrayList<nRun> init_events = new ArrayList<nRun>();
	ArrayList<nRun> load_events = new ArrayList<nRun>();
	ArrayList<nRun> clear_events = new ArrayList<nRun>();

	public sBloc_Builder addEventInit(nRun r) { init_events.add(r); return this; }
	public void removeEventInit(nRun r) { init_events.remove(r); }
	public sBloc_Builder addEventLoad(nRun r) { load_events.add(r); return this; }
	public void removeEventLoad(nRun r) { load_events.remove(r); }
	public sBloc_Builder addEventClear(nRun r) { clear_events.add(r); return this; }
	public void removeEventClear(nRun r) { clear_events.remove(r); }
	
	public sBloc_Builder setSolo(boolean r) { isSolo = r; return this; } 
	
	public sBloc_Builder runAdding(sValueBloc r) { 
		if (builder_add_run != null) builder_add_run.run(r); return this; }
	
	public boolean isBuildableIn(sValueBloc b) {
		if (b.getBlocBuilder(ref) != null && (!isSolo || b.getBloc(ref) == null)) 
			return true; else return false; }
	
	
	
	
	
	
	

//	private RunDef new_rundef = null;
	public sBloc_Builder newRun(String r, nRun run) {
		RunDef rd = new RunDef(r, null, run);
//		new_rundef = rd; 
		return this;
	}
	public sBloc_Builder replaceRun(String r, nRun run) {
		RunDef old = getRunDef(r);
		if (old != null) rundefs.remove(old);
		RunDef rd = new RunDef(r, null, run);
//		if (old != null) rd.param = new pPar(old.param);
		return this;
	}
	public sBloc_Builder newRun(String r, Class<?> ct, nRun run) {
		RunDef rd = new RunDef(r, ct, run);
//		new_rundef = rd; 
		return this;
	}
	public sBloc_Builder replaceRun(String r, Class<?> ct, nRun run) {
		RunDef old = getRunDef(r);
		if (old != null) rundefs.remove(old);
		RunDef rd = new RunDef(r, ct, run);
//		if (old != null) rd.param = new pPar(old.param);
		return this;
	}
//	public pStandard runArgs(Object ... args) {
//		if (new_rundef != null) {
//			if (args == null) return this; 
//			if (args.length%2 != 0) return this; 
//			String k = null; int cnt = 0;
//			String[] ar = new String[args.length/2]; 
//			Class<?>[] ac = new Class<?>[args.length/2];
//			for (Object a : args) {
//				if (k == null && (a instanceof String)) { k = (String)a; }
//				if (k != null && (a instanceof Class<?>)) { 
//					ar[cnt] = k; ac[cnt] = (Class<?>)a;
//					k = null; cnt++; } 
//			}
//			new_rundef.set_arg(ar,ac);
//		}
//		return this;
//	}

	public RunDef getRunDef(String r) {
		for (RunDef d : rundefs) if (d.ref.equals(r)) return d;
		return null; }
	
	public ArrayList<RunDef> rundefs = new ArrayList<RunDef>(); 
	
	// crun in context.runs 
	public class RunDef {
		public String ref;
		public nRun run;
//		public pPar param;
//		public String comment = "";
//		public Class<?> return_class;
//		public int args_nb = 0;
//		public String[] args_ref;
//		public Class<?>[] args_class;

		public RunDef(RunDef r) {
			ref = Utl.copy(r.ref); 
//			return_class = r.return_class; 
			run = r.run;
//			args_ref = new String[r.args_ref.length];
//			args_class = new Class<?>[r.args_class.length]; 
//			args_nb = r.args_nb;
//			for (int i = 0 ; i < args_nb ; i++) {
//				args_ref[i] = Applet.copy(r.args_ref[i]);
//				args_class[i] = r.args_class[i];
//			}
//			param = new pPar(r.param);
			rundefs.add(this);
		}
		public RunDef(String r, Class<?> ct, nRun rn) {
			ref = r; 
//			return_class = ct; 
			run = rn;
//			args_ref = new String[0];
//			args_class = new Class<?>[0]; 
//			args_nb = 0;
			rundefs.add(this);
//			param = new pPar(current_param);
		}
//		public RunDef set_arg(String[] ar, Class<?>[] ac) {
//			if ((ar != null) != (ac != null)) return this;
//			if (ar != null && ac != null && ar.length != ac.length) return this;
//			if (ar != null && ac != null) {
//				args_ref = new String[ar.length];
//				System.arraycopy( ar, 0, args_ref, 0, ar.length );
//				args_class = new Class<?>[ac.length];
//				System.arraycopy( ac, 0, args_class, 0, ac.length );
//			} else {
//				args_ref = new String[0];
//				args_class = new Class<?>[0]; }
//			args_nb = ar.length;
//			return this;
//		}
		
//		public boolean test_args(Object ... v) {
//			Object[] args = v;//Applet.toArray(v);
//			if (args.length != args_nb) return false;
//			for (int i = 0 ; i < args_nb ; i++) {
//				if (args[i].getClass() != args_class[i]) return false;
//			}
//			return true;
//		}
	}

	
	
	
}
