package app;

import java.util.ArrayList;

import patch.pInstance;
import patch.pPar;

public abstract class nRun {
	
	public boolean to_clear = false;
	public Object builder = null; 
	public Object[] args = null;

	public pInstance instance = null;
	public pPar param = null;

	public nRun() {} 
	public nRun(Object p) { builder = p; args = new Object[] { p }; } 
	public nRun(Object ... o) { args = o; }
	
	
	// to override
	public void run() {}
	public void run(Object v) {}
	public void run(Object v1, Object v2) {}
	public void run(Object v1, Object v2, Object v3) {}
	public Object get() { return null; }
	public Object get(Object v) { return null; }
	public Object get(Object v1, Object v2) { return null; }
	public Object get(Object v1, Object v2, Object v3) { return null; }

	// to get arg
	public <T> T arg(int index, Class<T> ct) { return arg(index, args, ct); }
	
	//to access param
	public boolean hasParam(String k) { if (param != null) return param.has(k); return false; }
	public <T> boolean hasParam(String k, Class<T> cl) { 
		return (param != null && param.has(k) && getParam(k, cl) != null); }
	public Object getParam(String k) { if (param != null) return Applet.copy(param.get(k)); return null; }
	public <T> T getParam(String k, Class<T> cl) { if (param != null) return Applet.copy(param.get(k, cl)); return null; }
	public <T> T getParamOrDef(String k, Class<T> cl, T def) { if (param != null && param.has(k)) return Applet.copy(param.get(k, cl)); return def; }
	
	
	
	
	
	
	public <T> T arg(int index, Object[] args, Class<T> ct) {
		if (index >= args.length || index < 0 || args[index] == null 
//				|| (args[index]).getClass() != ct
				) return null;
		return (T)(args[index]); }
	
	
	
	

	protected void tel(String r, Object d) {
		if (answer != null) answer.do_run(instance, r, d); }
	protected Object ask(String r) {
		if (answer == null) return null; return answer.do_get(instance, r); }
	protected <T> T ask(String r, Class<T> cl) {
		if (answer == null) return null; return answer.do_get(instance, cl, r); }
	public void setAnswer(nRun a) { answer = a; }
	private nRun answer = null;
	
	
	
	public void do_run(Object ... v) { 
		instance = null; param = null; answer = null; args = v; /*args = Applet.toArray(v)*/; run(); }
	public void do_run(pPar p, Object ... v) { 
		instance = null; param = p; answer = null; args = v; /*args = Applet.toArray(v)*/; run(); }
	public void do_run(pInstance c, Object ... v) { 
		instance = c; param = null; answer = null; args = v; /*args = Applet.toArray(v)*/; run(); }
	public void do_run(pInstance c, pPar p, Object ... v) { 
//		if (v != null) Applet.app.log("prun.do_run() : v length = "+v.length);
//		if (v != null && v.length == 1 && v[0] instanceof Object[]) v = (Object[])v[0];
		instance = c; param = p; answer = null; args = v;
//		if (v != null) Applet.app.log("prun.do_run() : args length = "+args.length);
		/*args = Applet.toArray(v)*/; 
		run(); }

	public void do_run(nRun answ, Object ... v) { 
		instance = null; param = null; answer = answ; args = v; run(); }
	public void do_run(pPar p, nRun answ, Object ... v) { 
		instance = null; param = p; answer = answ; args = v; run(); }
	public void do_run(pInstance c, nRun answ, Object ... v) { 
		instance = c; param = null; answer = answ; args = v; run(); }
	public void do_run(pInstance c, pPar p, nRun answ, Object ... v) { 
		instance = c; param = p; answer = answ; args = v;
		run(); }
	
	public Object do_get(Object ... v) { 
		instance = null; param = null; answer = null; args = v; /*args = Applet.toArray(v)*/; return get(); }
	public <T> T do_get(Class<T> ct, Object ... v) { 
		instance = null; param = null; answer = null; args = v; Object o = get(); if (o != null) return (T)o; return null; }
	public Object do_get(pInstance c, Object ... v) { 
		instance = c; param = null; answer = null; args = v; /*args = Applet.toArray(v)*/; return get(); }
	public <T> T do_get(pInstance c, Class<T> ct, Object ... v) { 
		instance = c; param = null; answer = null; args = v; Object o = get(); if (o != null) return (T)o; return null; }
	public Object do_get(pPar p, Object ... v) { 
		instance = null; param = p; answer = null; args = v; /*args = Applet.toArray(v)*/; return get(); }
	public <T> T do_get(pPar p, Class<T> ct, Object ... v) { 
		instance = null; param = p; answer = null; args = v; Object o = get(); if (o != null) return (T)o; return null; }
	public Object do_get(pInstance c, pPar p, Object ... v) { 
		instance = c; param = p; answer = null; args = v; /*args = Applet.toArray(v)*/; return get(); }
	public <T> T do_get(pInstance c, pPar p, Class<T> ct, Object ... v) { 
		instance = c; param = p; answer = null; args = v; Object o = get(); if (o != null) return (T)o; return null; }
	
	public Object do_get(nRun answ, Object ... v) { 
		instance = null; param = null; answer = answ; args = v; /*args = Applet.toArray(v)*/; return get(); }
	public <T> T do_get(Class<T> ct, nRun answ, Object ... v) { 
		instance = null; param = null; answer = answ; args = v; Object o = get(); if (o != null) return (T)o; return null; }
	public Object do_get(pInstance c, nRun answ, Object ... v) { 
		instance = c; param = null; answer = answ; args = v; /*args = Applet.toArray(v)*/; return get(); }
	public <T> T do_get(pInstance c, Class<T> ct, nRun answ, Object ... v) { 
		instance = c; param = null; answer = answ; args = v; Object o = get(); if (o != null) return (T)o; return null; }
	public Object do_get(pPar p, nRun answ, Object ... v) { 
		instance = null; param = p; answer = answ; args = v; /*args = Applet.toArray(v)*/; return get(); }
	public <T> T do_get(pPar p, Class<T> ct, nRun answ, Object ... v) { 
		instance = null; param = p; answer = answ; args = v; Object o = get(); if (o != null) return (T)o; return null; }
	public Object do_get(pInstance c, pPar p, nRun answ, Object ... v) { 
		instance = c; param = p; answer = answ; args = v; /*args = Applet.toArray(v)*/; return get(); }
	public <T> T do_get(pInstance c, pPar p, Class<T> ct, nRun answ, Object ... v) { 
		instance = c; param = p; answer = answ; args = v; Object o = get(); if (o != null) return (T)o; return null; }

	
	
	

	public static void runList(ArrayList<nRun> e, Object ... v) { 
		for (nRun r : Applet.duplic(e)) r.do_run(v); }
	public static void runList(ArrayList<nRun> e, pInstance cont, Object ... v) { 
		for (nRun r : Applet.duplic(e)) r.do_run(cont,v); }
	public static void runList(ArrayList<nRun> e, pPar par, Object ... v) { 
		for (nRun r : Applet.duplic(e)) r.do_run(par,v); }
	public static void runList(ArrayList<nRun> e, pInstance cont, pPar par, Object ... v) { 
		for (nRun r : Applet.duplic(e)) r.do_run(cont,par,v); }
	
	
	
	
	
	
	public static void runEvents(ArrayList<nRun> e) { 
		ArrayList<nRun> e2 = new ArrayList<nRun>();
		for (nRun r : e) e2.add(r); 
		for (nRun r : e2) if (r != null) r.run();
//		for (int i = e.size() - 1 ; i >= 0 ; i--) if (i < e.size()) e.get(i).run(); 
	}
	public static void runEvents(ArrayList<nRun> e, Object v) { 
		ArrayList<nRun> e2 = new ArrayList<nRun>();
		for (nRun r : e) e2.add(r); for (nRun r : e2) r.run(v);
//		for (int i = e.size() - 1 ; i >= 0 ; i--) if (i < e.size()) e.get(i).run(v); 
	}
	public static void runEvents(ArrayList<nRun> e, Object v1, Object v2) { 
		ArrayList<nRun> e2 = new ArrayList<nRun>();
		for (nRun r : e) e2.add(r); for (nRun r : e2) r.run(v1,v2);
//		for (int i = e.size() - 1 ; i >= 0 ; i--) if (i < e.size()) e.get(i).run(v1,v2); 
	}
	public static void runEvents(ArrayList<nRun> e, Object v1, Object v2, Object v3) { 
		ArrayList<nRun> e2 = new ArrayList<nRun>();
		for (nRun r : e) e2.add(r); for (nRun r : e2) r.run(v1,v2,v3);
//		for (int i = e.size() - 1 ; i >= 0 ; i--) if (i < e.size()) e.get(i).run(v1,v2,v3); 
	}
	
	public static void clearEvents(ArrayList<nRun> e) { 
		ArrayList<nRun> e2 = new ArrayList<nRun>();
		for (nRun r : e) if (r.to_clear) e2.add(r);
		for (nRun r : e2) e.remove(r);
	}
	
	
	
	
	
	
	

	
	
	
	
	
	// to use in sRun to access run Arg
//	public sRun.Context srun_context = null; 
//	public <T> T getArg(String ref, Class<T> ct) {
//		if (srun_context != null) return srun_context.getArg(ref, ct); else return null; }
//	public <T> void setArg(String ref, T val) {
//		if (srun_context != null) srun_context.setArg(ref, val); }

//	public abstract class Context {
//		public abstract <T> T getVar(String ref, Class<T> ct); 
//		public abstract <T> void setVar(String ref, T val); 
//		public abstract sValueBloc getBloc(String ref); 
//		public abstract <T> T getObj(String ref, Class<T> ct); 
//		public abstract <T> void setObj(String ref, T val); 
//
//		public abstract void run(String ref); 
//		public abstract void run(String ref, Object o); 
//		public abstract Object get(String ref); 
//		public abstract Object get(String ref, Object o); 
//		
//	}
//	
//	// to use in pBric to access bric var
//	private Context bric_context = null; 
//	public void setContext(Context c) { bric_context = c; }
//	public void copyContext(nRunnable c) { bric_context = c.bric_context; }
//	public <T> T getVar(String ref, Class<T> ct) {
//		if (bric_context != null) return bric_context.getVar(ref, ct); else return null; }
//	public <T> void setVar(String ref, T val) {
//		if (bric_context != null) bric_context.setVar(ref, val); }
//	public sValueBloc getBloc(String ref) {
//		if (bric_context != null) return bric_context.getBloc(ref); else return null; }
//	public <T> T getObj(String ref, Class<T> ct) {
//		if (bric_context != null) return bric_context.getObj(ref, ct); else return null; }
//	public <T> void setObj(String ref, T val) {
//		if (bric_context != null) bric_context.setObj(ref, val); }
//	
//	public void contRun(String ref) {
//		if (bric_context != null) bric_context.run(ref); }
//	public Object contGet(String ref) {
//		if (bric_context != null) return bric_context.get(ref); return null; }
//	public Object contGet(String ref, Object o) {
//		if (bric_context != null) return bric_context.get(ref, o); return null; }
//	
}
