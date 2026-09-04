package aa_new;

import java.util.ArrayList;
import java.util.Map;

import com.badlogic.gdx.math.Vector2;

import util.Utl;
import util.nMap;
import util.nRun;
import util.nRuns;

public class Property extends Utl.OrderedPriorizableImplement {

	
	public final Plane plane;
	public final String ref;
	
	public int pool_start_cap = 20;
	public int pool_expend_cap = 20;
	
	public cPool<Component> pool;
	
	public nMap<Component> registry;
	public boolean registering = false;
	
	public Property(Plane p, String r) {
		plane = p;
		ref = Utl.copy(r);
		
		
	}
	
	public void finish() {
		Property this_prop = this;
		pool = new cPool<Component>(pool_start_cap, pool_expend_cap) {
			public Component newObject() { return new Component(this_prop); }
			public Component[] newArray(int i) { return new Component[i]; } };
	}

	public void empty() {
		if (registry != null) registry.clear();
		if (pool != null) pool.freeAll();
	}

	public void dispose() {
		if (registry != null) registry.clear();
		if (pool != null) pool.dispose();
	}
	
	public Property registered() {
		registry = new nMap<Component>();
		registering = true;
		return this;
	}
	
	
	public Component obtain() {
		Component c = pool.obtain();	
		return c;
	}
	public Component obtain(String r) {
		Component c = obtain(); register(r,c); return c; }

	public Component get(int i) {
		return pool.get(i);
	}

	public Component get(String r) {
		return registry.get(r);
	}

	public void register(String r, Component c) {
		registry.remove(r);
		registry.put(r,c);
	}

	
	
	
	
	

	public Property copy(String s) {
		copy(plane.getProperty(s));
		return this;
	}
	public Property copy(Property s) {
		if (s == null) return this;
		
		prio = s.prio; pre = s.pre; mid = s.mid; post = s.post;
		
		for (nRun n : s.init_runs) addInitRun(n);
		for (nRun n : s.finish_runs) addFinishRun(n);
		for (nRun n : s.clear_runs) addClearRun(n);
		for (nRun n : s.frame_runs) addFrameRun(n);
		for (nRun n : s.tick_runs) addTickRun(n);
		for (nRun n : s.draw_runs) addDrawRun(n);
		
		for (RunDef r : s.rundefs) new RunDef(r);
		
		for (Entry r : s.entrys.all()) new Entry(this,r);
		
		return this;
	}

	
	
	Object[][] getDataArray() {
		Object[][] dt = new Object[12][];
		int cnt = 0; 
		if (int_cnt > 0) { dt[cnt] = new Integer[int_cnt]; 
		for (int i = 0 ; i < int_cnt ; i++) dt[cnt][i] = new Integer(0); }
		cnt++; 
		if (flt_cnt > 0) { dt[cnt] = new Float[flt_cnt]; 
		for (int i = 0 ; i < flt_cnt ; i++) dt[cnt][i] = new Float(0); }
		cnt++; 
		if (boo_cnt > 0) { dt[cnt] = new Boolean[boo_cnt]; 
		for (int i = 0 ; i < boo_cnt ; i++) dt[cnt][i] = new Boolean(false); }
		cnt++; 
		if (str_cnt > 0) { dt[cnt] = new String[str_cnt]; 
		for (int i = 0 ; i < str_cnt ; i++) dt[cnt][i] = new String(); }
		cnt++; 
		if (vec_cnt > 0) { dt[cnt] = new Vector2[vec_cnt]; 
		for (int i = 0 ; i < vec_cnt ; i++) dt[cnt][i] = new Vector2(); }
		cnt++; 
		if (com_cnt > 0) { dt[cnt] = new Integer[com_cnt]; 
		for (int i = 0 ; i < com_cnt ; i++) dt[cnt][i] = new Integer(0); }
		cnt++; 
		if (int_arr_cnt > 0) { 
			dt[cnt] = new ArrayList[int_arr_cnt]; 
			for (int i = 0 ; i < int_arr_cnt ; i++) dt[cnt][i] = new ArrayList<Integer>(); }
		cnt++;
		if (flt_arr_cnt > 0) { 
			dt[cnt] = new ArrayList[flt_arr_cnt]; 
			for (int i = 0 ; i < flt_arr_cnt ; i++) dt[cnt][i] = new ArrayList<Float>(); }
		cnt++;
		if (boo_arr_cnt > 0) { 
			dt[cnt] = new ArrayList[boo_arr_cnt]; 
			for (int i = 0 ; i < boo_arr_cnt ; i++) dt[cnt][i] = new ArrayList<Boolean>(); }
		cnt++;
		if (str_arr_cnt > 0) { 
			dt[cnt] = new ArrayList[str_arr_cnt]; 
			for (int i = 0 ; i < str_arr_cnt ; i++) dt[cnt][i] = new ArrayList<String>(); }
		cnt++;
		if (vec_arr_cnt > 0) { 
			dt[cnt] = new ArrayList[vec_arr_cnt]; 
			for (int i = 0 ; i < vec_arr_cnt ; i++) dt[cnt][i] = new ArrayList<Vector2>(); }
		cnt++;
		if (com_arr_cnt > 0) { 
			dt[cnt] = new ArrayList[com_arr_cnt]; 
			for (int i = 0 ; i < com_arr_cnt ; i++) dt[cnt][i] = new ArrayList<Integer>(); }
		cnt++;
		return dt;
	}

	public class Entry {
		int id = 0, class_id = 0, val_id = 0; 
		String ref;
		Class<?> content;
		boolean isArray = false;
		nMap<Object> settings = new nMap<Object>();
		nRuns change_runs = new nRuns();
		Property prop;
		public Entry(Property p, String r, Class<?> c, boolean arr) { 
			prop = p;
			ref = Utl.copy(r); 
			content = c; 
			id = entrys.size(); 
			entrys.put(r, this); 
			isArray = arr;
			getClassId();
		}
		public Entry(Property p, Entry e) { 
			prop = p;
			ref = Utl.copy(e.ref); 
			content = e.content; 
			id = entrys.size(); 
			entrys.put(ref, this); 
			isArray = e.isArray;
			getClassId();
			for (nRun n : e.change_runs) change_runs.add(n);
			for (Map.Entry<String,Object> n : e.settings.entrySet()) 
				settings.put(n.getKey(),n.getValue());
		}
		Object get(Object[][] dt) {
			return dt[class_id][val_id];
		}
		void set(Object[][] dt, Object o) {
			dt[class_id][val_id] = o;
		}
		private void getClassId() {
			if (isArray) {
				if (content == Integer.class) { class_id = 6; val_id = int_arr_cnt; int_arr_cnt++; }
				else if (content == Float.class) { class_id = 7; val_id = flt_arr_cnt; flt_arr_cnt++; }
				else if (content == Boolean.class) { class_id = 8; val_id = boo_arr_cnt; boo_arr_cnt++; }
				else if (content == String.class) { class_id = 9; val_id = str_arr_cnt; str_arr_cnt++; }
				else if (content == Vector2.class) { class_id = 10; val_id = vec_arr_cnt; vec_arr_cnt++; }
				else if (content == Component.class) { class_id = 11; val_id = com_arr_cnt; com_arr_cnt++; }
			} else {
				if (content == Integer.class) { class_id = 0; val_id = int_cnt; int_cnt++; }
				else if (content == Float.class) { class_id = 1; val_id = flt_cnt; flt_cnt++; }
				else if (content == Boolean.class) { class_id = 2; val_id = boo_cnt; boo_cnt++; }
				else if (content == String.class) { class_id = 3; val_id = str_cnt; str_cnt++; }
				else if (content == Vector2.class) { class_id = 4; val_id = vec_cnt; vec_cnt++; }
				else if (content == Component.class) { class_id = 5; val_id = com_cnt; com_cnt++; }
			}
		}
		public Entry addSetting(String r, Object s) { settings.put(r,s); return this; }
		public Entry addChangeRun(nRun n) { change_runs.add(n); return this; }
		public Property getProp() { return prop; }
	}
	public Entry getEntry(String r) { return entrys.get(r); }
	public nMap<Entry> entrys = new nMap<Entry>();

	public int int_cnt = 0;
	public int flt_cnt = 0;
	public int boo_cnt = 0;
	public int str_cnt = 0;
	public int vec_cnt = 0;
	public int com_cnt = 0;
	public int int_arr_cnt = 0;
	public int flt_arr_cnt = 0;
	public int boo_arr_cnt = 0;
	public int str_arr_cnt = 0;
	public int vec_arr_cnt = 0;
	public int com_arr_cnt = 0;

	public Entry addEntry(String ref, Class<?> cont, boolean array) {
		if (ref == null || cont == null) { Utl.logn("ERROR Poperty.addEntry : got null arg "); return null; }
		if (entrys.hasKey(ref)) { Utl.logn("ERROR Poperty.addEntry : ref allready exist : "+ref); return null; }
		return new Entry(this, ref, cont, array);
	}

	public Entry addEntry(String ref, Class<?> cont) {
		return addEntry(ref, cont, false);
	}

	public Entry addArray(String ref, Class<?> cont) {
		return addEntry(ref, cont, true);
	}

	public Entry addData(String r, Object def) {
		return addEntry(ref, def.getClass(), false).addSetting("def", def); 
	} 

	public Entry addRefArray(String ref, String prop) {
		return addEntry(ref, Component.class, true).addSetting("prop", prop);
	}

	public Entry addRef(String ref, String prop) {
		return addEntry(ref, Component.class, false).addSetting("prop", prop); 
	} 
	
//	public Entry addData(String r, Object def, Object ... settings) {
//		Object[] stngs = new Object[2 + (settings != null ? settings.length : 0)];
//		stngs[0] = "def"; stngs[1] = def;
//		if (settings != null) for (int i = 0 ; i < settings.length ; i++) 
//			stngs[i+2] = settings[i];
//		return addEntry(ref, def.getClass(), false, stngs); 
//	} 
//	public Property addData(String r, Class<?> cont, Object ... settings) {
//		addEntry(ref, cont, false, settings);
//		return this; 
//	} 
//	public Property addRef(String r, String comp_prop_ref, Object ... settings) {
//		Object[] stngs = new Object[2 + (settings != null ? settings.length : 0)];
//		stngs[0] = "comp_prop_ref"; stngs[1] = comp_prop_ref;
//		if (settings != null) for (int i = 0 ; i < settings.length ; i++) 
//			stngs[i+2] = settings[i];
//		addEntry(ref, Component.class, false, stngs);
//		return this; 
//	} 
//
//	public Property addArray(String r, Class<?> cont, Object ... settings) {
//		addEntry(ref, cont, true, settings);
//		return this; 
//	} 
//	public Property addRefArray(String r, String comp_prop_ref, Object ... settings) {
//		Object[] stngs = new Object[2 + (settings != null ? settings.length : 0)];
//		stngs[0] = "comp_prop_ref"; stngs[1] = comp_prop_ref;
//		if (settings != null) for (int i = 0 ; i < settings.length ; i++) 
//			stngs[i+2] = settings[i];
//		addEntry(ref, Component.class, true, stngs);
//		return this; 
//	} 
	
	
	
	

	
	
	
	public Property setPrio(int p) { prio = p; return this; }
	public Property setPre() { pre = true; mid = false; return this; }
	public Property setPost() { post = true; mid = false; return this; }
	public Property setPreMid() { pre = true; return this; }
	public Property setMidPost() { post = true; return this; }
	public Property setPreMidPost() { post = true; pre = true; return this; }
	

	public void run_init(Component p) 		{ init_runs.do_run(p); }
	public void run_finish(Component p) 	{ finish_runs.do_run(p); }
	public void run_clear(Component p) 		{ clear_runs.do_run(p); }
	public void run_frame(Component p) 		{ frame_runs.do_run(p); }
	public void run_tick(Component p) 		{ tick_runs.do_run(p); }
	public void run_draw(Component p) 		{ draw_runs.do_run(p); }
	
	public nRuns init_runs = new nRuns().setAutoSorted();
	public nRuns finish_runs = new nRuns().setAutoSorted();
	public nRuns clear_runs = new nRuns().setPrioritized().setAutoSorted();
	public nRuns frame_runs = new nRuns().setOrdered().setPrioritized().setAutoSorted();
	public nRuns tick_runs = new nRuns().setOrdered().setPrioritized().setAutoSorted();	
	public nRuns draw_runs = new nRuns().setOrdered().setPrioritized().setAutoSorted();	

	public Property addInitRun(nRun n) 		{ init_runs.add(n); return this; }
	public Property addFinishRun(nRun r) 	{ finish_runs.add(r); return this; }
	public Property addClearRun(nRun r) 		{ clear_runs.add(r); return this; }
	public Property addFrameRun(nRun r) 		{ plane.with_frame_run.addOne(this); frame_runs.add(r); return this; }
	public Property addTickRun(nRun n) 		{ plane.with_tick_run.addOne(this); tick_runs.add(n); return this; }
	public Property addDrawRun(nRun n) 		{ plane.with_draw_run.addOne(this); draw_runs.add(n); return this; }
	
	
	
	
	
	
	
	
	
	public Property newRun(String r, nRun run) { new RunDef(r, run); return this; }
	public Property replaceRun(String r, nRun run) {
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

	
	
	
	
}
