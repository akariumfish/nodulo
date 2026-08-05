package util;

import java.util.ArrayList;
import java.util.HashMap;

import app.GdxApp;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public abstract class nScripted {
	
	private static HashMap<Class<? extends nScripted>,nMap<Method>> 
		scripted_methods = new HashMap<Class<? extends nScripted>,nMap<Method>>();
	
	public static void addScriptedMetode(Class<? extends nScripted> cl, String met_name, 
			Class<?>... params) {
		if (scripted_methods.get(cl) == null) {
			nMap<Method> mets = new nMap<Method>();
			scripted_methods.put(cl,mets); }
		nMap<Method> mets = scripted_methods.get(cl);
		try {
			Method m = cl.getDeclaredMethod(met_name, params);
			if (mets.hasKey(m.getName())) {
				Utl.logn("ERROR : nScripted.addScriptedMetode :"
						+ " allready a method with name "+m.getName()); return; }
			mets.put(m.getName(), m);
		} catch (NoSuchMethodException e) {
			Utl.logn("ERROR : nScripted.addScriptedMetode :"
					+ " NoSuchMethodException "+met_name); 
//			e.printStackTrace();
		} catch (SecurityException e) {
			Utl.logn("ERROR : nScripted.addScriptedMetode :"
					+ " SecurityException "+met_name); 
//			e.printStackTrace();
		}
	}
	
	private ScriptBuilder script_builder = new ScriptBuilder();
	protected void com(Object... arg) {
		String metode_name = Utl.getMethodName(1);
		script_builder.com(C.MET);
		script_builder.com(metode_name);
		if (arg != null) for (Object a : arg) script_builder.com(a);
	}
	
	public Object[] getScript() {
		Object[] ob = script_builder.get();
		return ob;
	}
	
	protected abstract void empty();
	public void clear() { empty(); script_builder.coms.clear(); }
	
	public static <T extends nScripted> void buildScript(T scripted, Object[] scr) {
		if (scripted == null) {
			Utl.logn("ERROR : nScripted.buildScript : "
					+ "no target"); return; }
		if (scripted_methods.get(scripted.getClass()) == null) {
			Utl.logn("ERROR : nScripted.buildScript : "
					+ "unscriptable target"); return; }
		if (scr == null || scr.length == 0) {
			Utl.logn("ERROR : nScripted.buildScript : "
					+ "no script"); return; }
		nMap<Method> metodes = scripted_methods.get(scripted.getClass());
		ArrayList<Object> script = to_array(scr);
		scripted.empty();
		if (popC(script) != C.INT) {
			Utl.logn("ERROR : nScripted.buildScript : "
					+ "no script size"); return; }
		int tot_size = pop(script, Integer.class);
		if (script.size() + 2 != tot_size) { Utl.logn("ERROR : "
				+ "nScripted.buildScript : script.size() != tot_size"
				+ " " + script.size() +" "+ tot_size); 
				return; }

		while (script.size() > 1) {
			if (popC(script) != C.CODE) {
				Utl.logn("ERROR : nScripted.buildScript : "
						+ "no start code"); return; }
			if (popC(script) != C.MET) {
				Utl.logn("ERROR : nScripted.buildScript : "
						+ "no start code 2"); return; }
			if (popC(script) != C.STR) {
				Utl.logn("ERROR : nScripted.buildScript : "
						+ "no code for method ref"); return; }
			String method_ref = pop(script, String.class);
			if (method_ref == null) {
				Utl.logn("ERROR : nScripted.buildScript : "
						+ "no method ref"); return; }
			Method metode = metodes.get(method_ref);
			if (metode == null) {
				Utl.logn("ERROR : nScripted.buildScript : "
						+ "cant found method "+method_ref); return; }
			int arg_nb = metode.getParameterCount();
			Class<?>[] arg_type = metode.getParameterTypes();
			Object[] args = new Object[arg_nb];
			for (int i = 0 ; i < arg_nb ; i++) {
				if (popC(script) != int_to_code.get(
						Utl.type_class_index.get(arg_type[i]))) {
					Utl.logn("ERROR : nScripted.buildScript : "
							+ "bad arg class"); return; }
				args[i] = pop(script);
			}
			try {
				metode.invoke(scripted, args);
			} catch (IllegalAccessException e) {
				Utl.logn("ERROR : nScripted.buildScript : "
						+ "IllegalAccessException");
//				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				Utl.logn("ERROR : nScripted.buildScript : "
						+ "IllegalArgumentException");
//				e.printStackTrace();
			} catch (InvocationTargetException e) {
				Utl.logn("ERROR : nScripted.buildScript : "
						+ "InvocationTargetException");
//				e.printStackTrace();
			}
		}
	}
	
	
	
	
	
	
	
	private enum C { FLT, INT, BOO, STR, VEC, CODE, MET };
	private static final C[] types_codes = new C[] { C.FLT, C.INT, C.BOO, C.STR, C.VEC };
	
	private static final HashMap<Integer,C> int_to_code = new HashMap<Integer,C>();
	private static final HashMap<C,Integer> code_to_int = new HashMap<C,Integer>();
	private static final HashMap<C,Class<?>> code_to_class = new HashMap<C,Class<?>>();
	
	static void build_codes() {
		int cnt = 0;
		for (C c : C.values()) { int_to_code.put(cnt,c); code_to_int.put(c,cnt); cnt++; }
		cnt = 0;
		for (Class<?> cl : Utl.data_type) { code_to_class.put(types_codes[cnt], cl); }
	}
	
	private static class ScriptBuilder {
		ArrayList<Object[]> coms = new ArrayList<Object[]>();
		public ScriptBuilder() {}
		public ScriptBuilder com(Object d) {
			if (!Utl.type_is_used(d.getClass())) return this;
			Object[] l = new Object[2];
			l[0] = code_to_int.get(types_codes[Utl.type_class_index.get(d.getClass())]);
			l[1] = Utl.copy(d); 
			coms.add(l); return this; }
		public ScriptBuilder com(C c) {
			coms.add(new Object[] { code_to_int.get(C.CODE), code_to_int.get(c) }); return this; }
//		public ScriptBuilder com(Object[] d) { if (d != null) coms.add(Utl.duplic(d)); return this; }//if (d != null) 
		public Object[] get() {
			int l = 2; for (Object[] ol : coms) l += ol.length;//if (ol != null) 
			Object[] list = new Object[l];
			list[0] = code_to_int.get(types_codes[Utl.type_class_index.get(Integer.class)]);
			list[1] = l;
			l = 2; 
			for (Object[] ol : coms) {//if (ol != null) 
				int l2 = 0; for (Object o : ol) { list[l+l2] = Utl.copy(o); l2++; }
				l += ol.length; }
			return list;
		}
	}
	
	private static ArrayList<Object> to_array(Object[] list) {
		ArrayList<Object> arr = new ArrayList<Object>();
		for (Object o : list) arr.add(o);
		return arr; }
	
//	private static Object peek(ArrayList<Object> arr) { 
//		if (arr == null || arr.size() == 0) return null; return arr.get(0); }
//	private static <T> T peek(ArrayList<Object> arr, Class<T> cl) { 
//		if (arr == null || arr.size() == 0) return null; return (T)arr.get(0); }
//	private static C peekC(ArrayList<Object> arr) { 
//		if (arr == null || arr.size() == 0) return null; return int_to_code.get((int)arr.get(0)); }
//	private static Object peek(ArrayList<Object> arr, int n) { 
//		if (arr == null) return null; return arr.get(n); }
//	private static <T> T peek(ArrayList<Object> arr, int n, Class<T> cl) { 
//		if (arr == null || n >= arr.size()) return null; return (T)arr.get(n); }
//	private static C peekC(ArrayList<Object> arr, int n) { 
//		if (arr == null) return null; return int_to_code.get((int)arr.get(n)); }
	
	private static Object pop(ArrayList<Object> arr) { 
		if (arr == null || arr.size() == 0) return null; return arr.remove(0); }
	private static <T> T pop(ArrayList<Object> arr, Class<T> cl) { 
		if (arr == null || arr.size() == 0) return null; return (T)arr.remove(0); }
	private static C popC(ArrayList<Object> arr) { 
		if (arr == null || arr.size() == 0) return null; return int_to_code.get((int)arr.remove(0)); }
//	private static void pop(ArrayList<Object> arr, int n, ArrayList<Object> l) { 
//		if (arr == null || arr.size() == 0) return;
//		for (int i = 0 ; i < n ; i++) l.add(pop(arr)); }
	
	
	
}
