package util;

import java.util.ArrayList;

import aa_nodulo.pParam;
import patch.pInstance;
import patch.pPar;

public abstract class nRun implements Utl.Priorizable, Utl.Ordered {
	
	
	
	
	
	
	
	
	
	
	
	
	
	private class RunStackEntry {
		int prio = 0; boolean pre = false, mid = true, post = false; }
	
	private RunStackEntry stack_entry = null;
	
	public int getSortingPriority() { return stack_entry.prio; }
	public boolean isOrderedPre() { return stack_entry.pre; }
	public boolean isOrderedMid() { return stack_entry.mid; }
	public boolean isOrderedPost() { return stack_entry.post; }

	public nRun setSorted() {
		if (stack_entry == null) stack_entry = new RunStackEntry(); return this; }
	public nRun setPrio(int p) { setSorted(); stack_entry.prio = p; return this; }
	public nRun setPre() { setSorted(); stack_entry.pre = true; stack_entry.mid = false; return this; }
	public nRun setPost() { setSorted(); stack_entry.post = true; stack_entry.mid = false; return this; }
	public nRun setPreMid() { setSorted(); stack_entry.pre = true; return this; }
	public nRun setMidPost() { setSorted(); stack_entry.post = true; return this; }
	public nRun setPreMidPost() { setSorted(); stack_entry.post = true; stack_entry.pre = true; return this; }
	
	
	
	
	
	
	
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
	public Object getParam(String k) { if (param != null) return Utl.copy(param.get(k)); return null; }
	public <T> T getParam(String k, Class<T> cl) { if (param != null) return Utl.copy(param.get(k, cl)); return null; }
	public <T> T getParamOrDef(String k, Class<T> cl, T def) { if (param != null && param.has(k)) return Utl.copy(param.get(k, cl)); return def; }
	
	
	
	
	
	
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
	
	
	
	
	
	public Object context = null;
	public <T> T context(Class<T> ct) { return (T)context; }
	public pParam contextParam() { return (pParam)context; }
	
	
	
	
	
	public void do_run(Object ... v) {  // context = null;
		instance = null; param = null; answer = null; args = v; run(); }
	public void do_run(pPar p, Object ... v) { 
		instance = null; param = p; answer = null; args = v; run(); }
	public void do_run(pInstance c, Object ... v) { 
		instance = c; param = null; answer = null; args = v; run(); }
	public void do_run(pInstance c, pPar p, Object ... v) { 
		instance = c; param = p; answer = null; args = v;
		run(); }

	public void do_run(nRun answ, Object ... v) { 
		instance = null; param = null; answer = answ; args = v; run(); }
	public void do_run(pPar p, nRun answ, Object ... v) { 
		instance = null; param = p; answer = answ; args = v; run(); }
	public void do_run(pInstance c, nRun answ, Object ... v) { 
		instance = c; param = null; answer = answ; args = v; run(); }
	public void do_run(pInstance c, pPar p, nRun answ, Object ... v) { 
		instance = c; param = p; answer = answ; args = v;
		run();  }
	
	public Object do_get(Object ... v) { 
		instance = null; param = null; answer = null; args = v; return get(); }
	public <T> T do_get(Class<T> ct, Object ... v) { 
		instance = null; param = null; answer = null; args = v; Object o = get(); if (o != null) return (T)o; return null; }
	public Object do_get(pInstance c, Object ... v) { 
		instance = c; param = null; answer = null; args = v; return get(); }
	public <T> T do_get(pInstance c, Class<T> ct, Object ... v) { 
		instance = c; param = null; answer = null; args = v; Object o = get(); if (o != null) return (T)o; return null; }
	public Object do_get(pPar p, Object ... v) { 
		instance = null; param = p; answer = null; args = v; return get(); }
	public <T> T do_get(pPar p, Class<T> ct, Object ... v) { 
		instance = null; param = p; answer = null; args = v; Object o = get(); if (o != null) return (T)o; return null; }
	public Object do_get(pInstance c, pPar p, Object ... v) { 
		instance = c; param = p; answer = null; args = v; return get(); }
	public <T> T do_get(pInstance c, pPar p, Class<T> ct, Object ... v) { 
		instance = c; param = p; answer = null; args = v; Object o = get(); if (o != null) return (T)o; return null; }
	
	public Object do_get(nRun answ, Object ... v) { 
		instance = null; param = null; answer = answ; args = v; return get(); }
	public <T> T do_get(Class<T> ct, nRun answ, Object ... v) { 
		instance = null; param = null; answer = answ; args = v; Object o = get(); if (o != null) return (T)o; return null; }
	public Object do_get(pInstance c, nRun answ, Object ... v) { 
		instance = c; param = null; answer = answ; args = v; return get(); }
	public <T> T do_get(pInstance c, Class<T> ct, nRun answ, Object ... v) { 
		instance = c; param = null; answer = answ; args = v; Object o = get(); if (o != null) return (T)o; return null; }
	public Object do_get(pPar p, nRun answ, Object ... v) { 
		instance = null; param = p; answer = answ; args = v; return get(); }
	public <T> T do_get(pPar p, Class<T> ct, nRun answ, Object ... v) { 
		instance = null; param = p; answer = answ; args = v; Object o = get(); if (o != null) return (T)o; return null; }
	public Object do_get(pInstance c, pPar p, nRun answ, Object ... v) { 
		instance = c; param = p; answer = answ; args = v; return get(); }
	public <T> T do_get(pInstance c, pPar p, Class<T> ct, nRun answ, Object ... v) { 
		instance = c; param = p; answer = answ; args = v; Object o = get(); if (o != null) return (T)o; return null; }

	
	
	

	public static void runList(ArrayList<nRun> e, Object ... v) { 
		for (nRun r : Utl.duplic(e)) if (r != null) r.do_run(v); }
	public static void runList(ArrayList<nRun> e, pInstance cont, Object ... v) { 
		for (nRun r : Utl.duplic(e)) if (r != null) r.do_run(cont,v); }
	public static void runList(ArrayList<nRun> e, pPar par, Object ... v) { 
		for (nRun r : Utl.duplic(e)) if (r != null) r.do_run(par,v); }
	public static void runList(ArrayList<nRun> e, pInstance cont, pPar par, Object ... v) { 
		for (nRun r : Utl.duplic(e)) if (r != null) r.do_run(cont,par,v); }
	
	
	public static void runEvents(ArrayList<nRun> e) { 
		for (nRun r : Utl.duplic(e)) if (r != null) r.run(); }
	public static void runEvents(ArrayList<nRun> e, Object v) { 
		for (nRun r : Utl.duplic(e)) if (r != null) r.run(v); }
	public static void runEvents(ArrayList<nRun> e, Object v1, Object v2) { 
		for (nRun r : Utl.duplic(e)) if (r != null) r.run(v1,v2); }
	public static void runEvents(ArrayList<nRun> e, Object v1, Object v2, Object v3) { 
		for (nRun r : Utl.duplic(e)) if (r != null) r.run(v1,v2,v3); }
	
	
	
	
}
