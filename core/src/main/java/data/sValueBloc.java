package data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool.Poolable;
import app.Applet;
import app.MetodeMap;
import app.nClearable;
import app.nLauncher;
import app.nMap;
import app.nRun;

import gui.nWidget;
import gui.nWidgetGroup;
import patch.pPar;
import patch.pStandard;




public class sValueBloc extends nLauncher implements nClearable, Poolable {
	
	
	
	
//	public HashMap<String, sRun> runs = new HashMap<String, sRun>();
//	
//	public sRun newRun(String n)			 			   { return data.newRun(this, n); }
//
//	public sRun getRun(String r) { return runs.get(r); }
	
	
	
	
	
	

	
	
	

	public void def_run(String ref, Object ... v) {
		if (builder == null) return;
		sBloc_Builder.RunDef rd = builder.getRunDef(ref);
		if (rd == null) return;
//		if (!rd.test_args(v)) return;
//		if (v != null) Applet.app.log("inst.run() : v length = "+v.length);
		rd.run.do_run(v); }
	
	public void def_run(String ref, pPar par, Object ... v) {
		if (builder == null) return;
		sBloc_Builder.RunDef rd = builder.getRunDef(ref);
		if (rd == null) return;
//		if (!rd.test_args(v)) return;
		rd.run.do_run(par,v); }
	
	public Object def_get(String ref, Object ... v) {
		if (builder == null) return null;
		sBloc_Builder.RunDef rd = builder.getRunDef(ref);
		if (rd == null) return null;
//		if (!rd.test_args(v)) return null;
		return rd.run.do_get(v); }
	
	public <T> T def_get(String ref, Class<T> cl, Object ... v) {
		if (builder == null) return null;
		sBloc_Builder.RunDef rd = builder.getRunDef(ref);
		if (rd == null) return null;
//		if (cl != rd.return_class) return null;
//		if (!rd.test_args(v)) return null;
		return rd.run.do_get(cl,v); }
	
	
	
	
	
	
	
	

	public final ArrayList<sBloc_Builder> bloc_builders = new ArrayList<sBloc_Builder>();
	public final ArrayList<String> bloc_builders_types = new ArrayList<String>();
	

	public void addBlocBuilder(sBloc_Builder b) { bloc_builders.add(b);
		bloc_builders_types.add(b.ref); b.runAdding(this); }
	public void addBlocBuilder(String r) {
		sBloc_Builder b = data.getBuilder(r);
		if (b != null) addBlocBuilder(b); }
	
	public sBloc_Builder getBlocBuilder(String r) {
		for (sBloc_Builder b : bloc_builders) if (b.ref.equals(r)) return b;
		return null; }
	
	public sValueBloc buildBloc(sBloc_Builder b, String ref) {
		if (b.isSolo) {
			boolean found = false;
			for (Map.Entry<String, sValueBloc> me : blocs.entrySet()) {
				sValueBloc vb = ((sValueBloc)me.getValue());
				if (vb.builder == b) found = true; }
			if (!found) {
//				if (b.root_interf_w != null) b.root_interf_w.hide();
				sValueBloc bl = data.bloc_pool.obtain().init(b, this, ref);
				bl.load_finish();
				bl.runEventList("eventsLoadEnd");
				bl.is_loading = false;
				return bl;
			} else return null;
		} else {
			sValueBloc bl = data.bloc_pool.obtain().init(b, this, ref);
			bl.load_finish();
			bl.runEventList("eventsLoadEnd");
			bl.is_loading = false;
			return bl;
		}
	}
	public sValueBloc buildBloc(String builder, String ref) {
		sBloc_Builder b = getBlocBuilder(builder);
		if (b != null) return buildBloc(b, ref); 
		else return null; }
	public sValueBloc buildBloc(String builder) {
		sBloc_Builder b = getBlocBuilder(builder);
		if (b != null) return buildBloc(b, builder); 
		else return null; }
	
	// used by newBloc(Save_Bloc)
	public sValueBloc loadBloc(sBloc_Builder b, String ref) {
		if (b.isSolo) {
			boolean found = false;
			for (Map.Entry<String, sValueBloc> me : blocs.entrySet()) {
				sValueBloc vb = ((sValueBloc)me.getValue());
				if (vb.builder == b) found = true;
			}
			if (!found) {
//				if (b.root_interf_w != null) b.root_interf_w.hide();
				return data.bloc_pool.obtain().init_start(b, this, ref);
			} else return null;
		} else return data.bloc_pool.obtain().init_start(b, this, ref);
	}
	public sValueBloc loadBloc(String builder, String ref) {
		sBloc_Builder b = getBlocBuilder(builder);
		if (b != null) return loadBloc(b, ref); 
		else return null; }
	
	
	
	
	public final ArrayList<String> flags = new ArrayList<String>();
	public sValueBloc addFlag(String f) {
		for (String s : flags) if (s.equals(f)) return this;
		flags.add(f); return this; }
	public boolean isFlag(String f) {
		for (String s : flags) if (s.equals(f)) { return true; } return false; }
	
	public sValueBloc getAllBlocWithFlag(String flag, ArrayList<sValueBloc> flagged_bloc) {
		getAllBlocWithFlag(flag, flagged_bloc, true); return this; }
	public sValueBloc getAllBlocWithFlag(String flag, 
			ArrayList<sValueBloc> flagged_bloc, boolean search_childs) {
		for (Map.Entry<String, sValueBloc> me : blocs.entrySet()) {
			sValueBloc vb = ((sValueBloc)me.getValue());
			if (vb.isFlag(flag)) flagged_bloc.add(vb);
			if (search_childs) vb.getAllBlocWithFlag(flag, flagged_bloc, true); }
		return this; 
	}
	public sValueBloc getAllValueWithFlag(String flag, ArrayList<sValue> flagged_val) {
		getAllValueWithFlag(flag, flagged_val, true); return this; }
	public sValueBloc getAllValueWithFlag(String flag, 
			ArrayList<sValue> flagged_val, boolean search_childs) {
		for (Map.Entry<String, sValue> me : values.entrySet()) {
			sValue vb = ((sValue)me.getValue());
			if (vb.isFlag(flag)) flagged_val.add(vb); }
		if (search_childs) for (Map.Entry<String, sValueBloc> me : blocs.entrySet()) {
			sValueBloc vb = ((sValueBloc)me.getValue());
			vb.getAllValueWithFlag(flag, flagged_val, true); }
		return this; 
	}
	
	public sValueBloc getAllBlocWithFlags(ArrayList<String> flag, ArrayList<sValueBloc> flagged_bloc) {
		getAllBlocWithFlags(flag, flagged_bloc, true); return this; }
	public sValueBloc getAllBlocWithFlags(ArrayList<String> flag, 
			ArrayList<sValueBloc> flagged_bloc, boolean search_childs) {
		for (Map.Entry<String, sValueBloc> me : blocs.entrySet()) {
			sValueBloc vb = ((sValueBloc)me.getValue());
			boolean asFlags = true;
			for (String f : flag) asFlags = asFlags && vb.isFlag(f);
			if (asFlags) flagged_bloc.add(vb);
			if (search_childs) vb.getAllBlocWithFlags(flag, flagged_bloc, true); }
		return this; 
	}
	public sValueBloc getAllValueWithFlags(ArrayList<String> flag, ArrayList<sValue> flagged_val) {
		getAllValueWithFlags(flag, flagged_val, true); return this; }
	public sValueBloc getAllValueWithFlags(ArrayList<String> flag, 
			ArrayList<sValue> flagged_val, boolean search_childs) {
		for (Map.Entry<String, sValue> me : values.entrySet()) {
			sValue vb = ((sValue)me.getValue());
			boolean asFlags = true;
			for (String f : flag) asFlags = asFlags && vb.isFlag(f);
			if (asFlags) flagged_val.add(vb); }
		if (search_childs) for (Map.Entry<String, sValueBloc> me : blocs.entrySet()) {
			sValueBloc vb = ((sValueBloc)me.getValue());
			vb.getAllValueWithFlags(flag, flagged_val, true); }
		return this; 
	}
	
	
	public MetodeMap metodes;
	public nMap<Object> objects;
	
	public sValueBloc addObject(String ref, Object r) {
		if (r != null) objects.put(ref, r); return this; }
	public sValueBloc removeObject(String ref, Object r) {
		if (r != null) objects.remove(ref, r); return this; }
	public sValueBloc setObject(String ref, Object r) {
		Object old = object(ref); if (old != null) removeObject(ref, old);
		addObject(ref, r); return this; }
	public boolean hasObject(String ref) { return objects.get(ref) != null; }
	public Object object(String ref) { return objects.get(ref); }
	public <T> T object(String ref, Class<T> cl) { 
		Object o = objects.get(ref);
		if (o != null && cl.isAssignableFrom(o.getClass())) 
			return (T)o; else return null; }
	public int objectInt(String ref) { return (int)objects.get(ref); }
	public float objectFlt(String ref) { return (float)objects.get(ref); }
	public boolean objectBoo(String ref) { return (boolean)objects.get(ref); }
	public String objectStr(String ref) { return (String)objects.get(ref); }
	
	public sValueBloc addMetode(String ref, nRun r) { metodes.put(ref, r); return this; }
	public sValueBloc addMetode(String ref, nRun r, String f1) { metodes.put(ref, r, f1); return this; }
	public sValueBloc addMetode(String ref, nRun r, String f1, String f2) { metodes.put(ref, r, f1, f2); return this; }
	public sValueBloc addMetode(String ref, nRun r, String f1, String f2, String f3) { metodes.put(ref, r, f1, f2, f3); return this; }
	public sValueBloc removeMetode(String ref, nRun r) { metodes.remove(ref, r); return this; }
	public nRun getMetode(String ref) { return metodes.get(ref); }
	public boolean getMetodeFlag(String ref, String flag) { return metodes.isFlag(ref, flag); }
	
	public sValueBloc run(String ref) {
		if (metodes.get(ref) != null) metodes.get(ref).run(); return this; }
	public sValueBloc run(String ref, Object o) {
		if (metodes.get(ref) != null) metodes.get(ref).run(o); return this; }
	public sValueBloc run(String ref, Object o1, Object o2) {
		if (metodes.get(ref) != null) metodes.get(ref).run(o1, o2); return this; }
	public Object get(String ref) {
		if (metodes.get(ref) != null) return metodes.get(ref).get(); return null; }
	public <T> T get(String ref, Class<T> ct) {
		if (metodes.get(ref) != null) return (T)metodes.get(ref).get(); return null; }
	public Object get(String ref, Object o) {
		if (metodes.get(ref) != null) return metodes.get(ref).get(o); return null; }
	public <T> T get(String ref, Class<T> ct, Object o) {
		if (metodes.get(ref) != null) return (T)metodes.get(ref).get(o); return null; }
	
	public int getInt(String ref) { return (int)get(ref); }
	public int getInt(String ref, Object o) { return (int)get(ref, o); }
	public float getFlt(String ref) { return (float)get(ref); }
	public float getFlt(String ref, Object o) { return (float)get(ref, o); }
	public boolean getBoo(String ref) { return (boolean)get(ref); }
	public boolean getBoo(String ref, Object o) { return (boolean)get(ref, o); }
	public String getStr(String ref) { return (String)get(ref); }
	public String getStr(String ref, Object o) { return (String)get(ref, o); }
	
	
	public  sValueBloc doEvent(boolean t) { doevent = t; return this; }

	@Override
	public void build_lauchables() {

		eventsAddVal = newEventList("eventsAddVal", new ArrayList<nRun>());
		eventsAddBloc = newEventList("eventsAddBloc", new ArrayList<nRun>());
		eventsDelVal = newEventList("eventsDelVal", new ArrayList<nRun>());
		eventsDelBloc = newEventList("eventsDelBloc", new ArrayList<nRun>());
		eventsDelete = newEventList("eventsDelete", new ArrayList<nRun>());
		eventsChangeThisFrame = newEventList("eventsChangeThisFrame", new ArrayList<nRun>());
		eventsSelect = newEventList("eventsSelect", new ArrayList<nRun>());
		eventsUnselect = newEventList("eventsUnselect", new ArrayList<nRun>());
		eventsSave = newEventList("eventsSave", new ArrayList<nRun>());
		eventsLoadEnd = newEventList("eventsLoadEnd", new ArrayList<nRun>());
		eventsLoadParam = newEventList("eventsLoadParam", new ArrayList<nRun>());
		
	}
	
	ArrayList<nRun> eventsAddVal;
	ArrayList<nRun> eventsAddBloc;
	ArrayList<nRun> eventsDelVal;
	ArrayList<nRun> eventsDelBloc;
	ArrayList<nRun> eventsDelete;
	ArrayList<nRun> eventsChangeThisFrame;
	ArrayList<nRun> eventsSelect;
	ArrayList<nRun> eventsUnselect;
	ArrayList<nRun> eventsSave;
	ArrayList<nRun> eventsLoadEnd;
	ArrayList<nRun> eventsLoadParam;
	
	public sValueBloc addEventAddValue_Builder(nRun r) { r.builder = this; eventsAddVal.add(r); return this; }
	public sValueBloc addEventAddBloc_Builder(nRun r) { r.builder = this; eventsAddBloc.add(r); return this; }
	public sValueBloc addEventDelValue_Builder(nRun r) { r.builder = this; eventsDelVal.add(r); return this; }
	public sValueBloc addEventDelBloc_Builder(nRun r) { r.builder = this; eventsDelBloc.add(r); return this; }
	public sValueBloc addEventDelete_Builder(nRun r) { r.builder = this; eventsDelete.add(r); return this; }
	public sValueBloc addEventDelete(nRun r) { eventsDelete.add(r); return this; }
	public sValueBloc removeEventDelete(nRun r) { eventsDelete.remove(r); return this; }
	public sValueBloc addEventChangeThisFrame(nRun r) { eventsChangeThisFrame.add(r); return this; }
	public sValueBloc removeEventChangeThisFrame(nRun r) { eventsChangeThisFrame.remove(r); return this; }
	public sValueBloc addEventSelect(nRun r) { eventsSelect.add(r); return this; }
	public sValueBloc removeEventSelect(nRun r) { eventsSelect.remove(r); return this; }
	public sValueBloc addEventUnselect(nRun r) { eventsUnselect.add(r); return this; }
	public sValueBloc removeEventUnselect(nRun r) { eventsUnselect.remove(r); return this; }
	public sValueBloc addEventSave(nRun r) { eventsSave.add(r); return this; }
	public sValueBloc removeEventSave(nRun r) { eventsSave.remove(r); return this; }
	public sValueBloc addEventLoadEnd(nRun r) { eventsLoadEnd.add(r); return this; }
	public sValueBloc removeEventLoadEnd(nRun r) { eventsLoadEnd.remove(r); return this; }
	public sValueBloc addEventLoadParam(nRun r) { eventsLoadParam.add(r); return this; }
	public sValueBloc removeEventLoadParam(nRun r) { eventsLoadParam.remove(r); return this; }
	
	
	


	public Applet app;
	public sData data; public sValueBloc parent = null, last_created_bloc = null; 
	public sValue last_created_value = null;
	public String ref = "", base_ref = "", type = "def", use = "";
	public int id = -1;
	public nMap<sValue> values = new nMap<sValue>();
	public nMap<sValueBloc> blocs = new nMap<sValueBloc>();
	public String adress; boolean doevent = true;
	
	public sValueBloc thisBloc;
			
	public sBloc_Builder builder = null;
	public String build_ref = "";
	
	public boolean clearing = false;

	public boolean open_in_dataview = false;
	public nWidgetGroup box_in_dataview = null;

	public boolean is_selected = false;

	public boolean is_loading = false;
	
	public boolean is_new_bloc = false;
	
	public boolean is_selected() { return is_selected; }
	public void select_bloc() {
		if (!is_selected) {
//			app.log("select bloc "+ref);
			is_selected = true;
			if (data.selected_bloc != null) {
				data.selected_bloc.unselect_bloc();
			}
			data.selected_bloc = this;
			runEventList("eventsSelect");
		}
	}
	public void unselect_bloc() {
		if (is_selected) { 
//			app.log("unselect bloc "+ref);
			is_selected = false;
			data.selected_bloc = null;
			runEventList("eventsUnselect");
		}
	}

	// constructor called by pool to fill it
	sValueBloc() {
		thisBloc = this;
		metodes = new MetodeMap();
		objects = new nMap<Object>();
	}

	// common init
	private void init_setup(sValueBloc b, String r) {  
		is_loading = true;
		data = b.data; parent = b; app = data.app;
		
		
		int pref_id = data.autoid.find_id(r);
		if (pref_id == -1) {
			base_ref = Applet.copy(r);
			if (b.blocs.get(r) == null) {
				ref = Applet.copy(r);
				id = -1;
			} else {
				id = data.autoid.get_new_id();
				ref = data.autoid.make_full_ref(r, id);
			}
		} else {
			base_ref = data.autoid.find_ref(r);
			id = data.autoid.get_prefered_id(pref_id);
			if (id != pref_id) {
				//naming conflict !
				app.logn("WARNING : sValueBloc naming cnflict, "+r+" not usable");
			}
			ref = data.autoid.make_full_ref(base_ref, id);
			if (!ref.equals(r)) {
				//naming error !
				app.logn("ERROR : incoherent result while naming bloc, r != ref for r: "+r+
						" and ref: "+ref);
			}
		}
		if (b.blocs.get(ref) != null) {
			app.logn("ERROR : bloc naming bug "+ref+" allready exist");
			app.crash();
		}
		
//		base_ref = r;
//		int c = 1; while (b.blocs.get(r) != null) { r = base_ref + "-" + c; c++; }
//		ref = r;
		  
		if (parent == data) adress = "" + sData.adress_token + ref;
		else adress = b.adress + sData.adress_token + ref;
		if (!sData.refIsValid(ref)) app.logn("ERROR Invalid valbloc ref");
		b.blocs.put(ref, this); 
		for (sBloc_Builder bb : data.common_bloc_builders) addBlocBuilder(bb); 
		create_common_metodes(); }
	
	//called when obtained from pool
	public sValueBloc init(sValueBloc b, String r) { 
		init_setup(b, r);
		is_new_bloc = true;
		parent.event_new_child(this);
//		view_bloc_in_dataview();
		return this; }

	//called when obtained by a builder 
	public sValueBloc init(sBloc_Builder build, sValueBloc b, String r) { 
		init_setup(b, r);
		is_new_bloc = true;
		builder = build;
		build_ref = builder.ref;
		builder.run_init(this);
		parent.event_new_child(this);
//		view_bloc_in_dataview();
		return this; }

	//called when loaded with a builder 
	public sValueBloc init_start(sBloc_Builder build, sValueBloc b, String r) { 
		init_setup(b, r);
		is_new_bloc = false;
		builder = build;
		build_ref = builder.ref;
		return this; }
	public sValueBloc init_finish() { 
		builder.run_init(this);
		parent.event_new_child(this);
		return this; }
	public sValueBloc load_finish() { 
		if (builder != null) builder.run_load(this);
		return this; }

	public void event_new_child(sValueBloc b) {
		if (doevent && data.doevent) {
			last_created_bloc = b; 
			runEventList("eventsAddBloc"); 
			nRun.runEvents(eventsChangeThisFrame, "add_bloc", b); 
			runEventList("eventsChangeThisFrame"); 
			data.signal_change();
		}
	}

	// view this bloc in dataview
	public void view_bloc_in_dataview() {
		app.addDelayEvent(1, new nRun() { public void run() {
			data.collapse_all_in_dataview();
			open_in_dataview = true;
			open_parents_in_dataview();
			select_bloc();
			if (data.dataview_bloc != null) {
				app.addDelayEvent(2, new nRun() { public void run() {
					data.dataview_bloc.run("populate_run");
					app.addDelayEvent(2, new nRun() { public void run() {
						data.dataview_bloc.run("center_on_bloc", thisBloc);
					}}); 
				}});
			}
		}});
	}
	
	// create common metodes
	public void create_common_metodes() {
		addMetode("view_bloc_in_dataview", new nRun() { public void run(Object o) {
			view_bloc_in_dataview(); }});
		addMetode("clear_bloc", new nRun() { public void run(Object o) {
			clear(); }});
	}
	
	// recursive open all parents
	public void open_parents_in_dataview() {
		if (parent != null && parent != this) { 
			parent.open_in_dataview = true;
			parent.open_parents_in_dataview(); } }
	
	//called when freed by pool
	@Override
	public void reset() {
		
//		empty();
		
//		if (widgGroup_bloc_viewer != null) widgGroup_bloc_viewer.clear();
//		widgGroup_bloc_viewer = null;
	}
	
	public void clear() {
		if (!clearing) {

			clearing = true;

			if (is_selected) unselect_bloc();
			
			if (builder != null) { 
				builder.run_clear(this);
//				if (builder.isSolo && builder.root_interf_w != null) 
//					builder.root_interf_w.setTrigger();
			}
	
			if (doevent && data.doevent) {
				runEventList("eventsDelete");
				data.signal_change();
			}
	
			if (parent != null && parent.doevent && data.doevent && !parent.clearing) {
				parent.runEventList("eventsDelBloc"); 
				nRun.runEvents(parent.eventsChangeThisFrame, "del_bloc", this); 
				parent.runEventList("eventsChangeThisFrame"); 
				data.signal_change();
			}

			clear_all_bloc();
			clear_all_vals();

			metodes.clear(); objects.clear(); 
			
//			runs.clear(); 
			bloc_builders.clear(); bloc_builders_types.clear(); 
	
			if (parent != null) parent.blocs.remove(ref, this);
			
			data.autoid.free_id(id);
			id = -1;
			
			last_created_bloc = null; 
			last_created_value = null;
			base_ref = ""; ref = ""; adress = ""; 
			parent = null;
			type = "def";
			use = "";
			builder = null; build_ref = "";
			doevent = true;
			clearing = false;
			open_in_dataview = false;
			box_in_dataview = null;
			is_selected = false;

			flags.clear();

//			runs.clear(); 
			bloc_builders.clear(); bloc_builders_types.clear(); 

			eventsAddVal.clear();
			eventsAddBloc.clear();
			eventsDelVal.clear();
			eventsDelBloc.clear();
			eventsDelete.clear();
			eventsChangeThisFrame.clear();
			eventsSelect.clear();
			eventsUnselect.clear();
			eventsSave.clear();
			eventsLoadEnd.clear();

			data.bloc_pool.free(this);
			
		}
	}
	
	ArrayList<sValue> tmpval = new ArrayList<sValue>();
	ArrayList<sValueBloc> tmpblc = new ArrayList<sValueBloc>();
	public void empty() {
		clear_all_bloc();
		clear_all_vals();
		
		metodes.clear(); objects.clear(); 
	}
	public void clear_all_bloc() {
		
//		if (parent != null) app.log("bloc "+ref+" in "+parent.ref+" clear all bloc");
//		else app.log("bloc "+ref+" in root clear all bloc");
		
		tmpblc.clear();
		for (Map.Entry<String,sValueBloc> b : blocs.entrySet()) tmpblc.add((sValueBloc)b.getValue());
		for (int i = tmpblc.size()-1 ; i >= 0 ; i--) tmpblc.get(i).clear();
		tmpblc.clear();
		blocs.clear(); 
	}
	public void clear_all_vals() {
		tmpval.clear();
		for (Map.Entry<String,sValue> b : values.entrySet()) tmpval.add((sValue)b.getValue());
		for (int i = tmpval.size()-1 ; i >= 0 ; i--) tmpval.get(i).clear();
		tmpval.clear(); values.clear(); 
	}

	public void frame_start() {
		tmpblc.clear();
		for (Map.Entry<String, sValueBloc> b : blocs.entrySet()) tmpblc.add((sValueBloc)b.getValue());
		for (int i = tmpblc.size()-1 ; i >= 0 ; i--) tmpblc.get(i).frame_start();
		tmpblc.clear();
		tmpval.clear();
		for (Map.Entry<String,sValue> b : values.entrySet()) tmpval.add((sValue)b.getValue());
		for (int i = tmpval.size()-1 ; i >= 0 ; i--) tmpval.get(i).frame_start();
		tmpval.clear();
	}
	public void frame_end() {
		tmpblc.clear();
		for (Map.Entry<String, sValueBloc> b : blocs.entrySet()) tmpblc.add((sValueBloc)b.getValue());
		for (int i = tmpblc.size()-1 ; i >= 0 ; i--) tmpblc.get(i).frame_end();
		tmpblc.clear();
		tmpval.clear();
		for (Map.Entry<String,sValue> b : values.entrySet()) tmpval.add((sValue)b.getValue());
		for (int i = tmpval.size()-1 ; i >= 0 ; i--) tmpval.get(i).frame_end();
		tmpval.clear();
	}
	

	public  sValueBloc getBloc(String r) { return blocs.get(r); }
	public  sValueBloc getLastBloc() { return last_created_bloc; }
	public  sValue getValue(String r) { return values.get(r); }
	public <T> T getValue(String ref, Class<T> cl) { 
		Object o = values.get(ref);
		if (o != null && cl.isAssignableFrom(o.getClass())) 
			return (T)o; else return null; }	
	
	public  sValueBloc newBloc(String n) { return data.newBloc(this, n); } 
	
	public sValue newVal(String n, String s, Object v) { return data.newVal(this, v, n, s); }
	
	public  sInt newInt(String n, String s, int v)       { return data.newInt(this, v, n, s); }
	public  sFlt newFlt(String n, String s, float v)     { return data.newFlt(this, v, n, s); }
	public  sBoo newBoo(String n, String s, boolean v)   { return data.newBoo(this, v, n, s); }
	public  sBoo newBoo(String n, String s, boolean v, char ct)   { return data.newBoo(this, v, n, s, ct); }
	public sInt newInt(int v, String n, String s)       { return data.newInt(this, v, n, s); }
	public sFlt newFlt(float v, String n, String s)     { return data.newFlt(this, v, n, s); }
	public sBoo newBoo(boolean v, String n, String s)   { return data.newBoo(this, v, n, s); }
	public sBoo newBoo(boolean v, String n, String s, char ct)   { return data.newBoo(this, v, n, s, ct); }
	public sStr newStr(String n, String s, String v)    { return data.newStr(this, v, n, s); }
	public sVec newVec(String n, String s, Vector2 v)   { return data.newVec(this, n, s).set(v); }
	public sVec newVec(String n, String s)              { return data.newVec(this, n, s); }
	public sArr newArr(String n, String s) 			   { return data.newArr(this, n, s); }
	public sTab newTab(String n, String s) 			   { return data.newTab(this, n, s); }
	
	public sInt newInt(String ref) { return newInt(ref, ref, 0); }
	public sFlt newFlt(String ref) { return newFlt(ref, ref, 0); }
	public sBoo newBoo(String ref) { return newBoo(ref, ref, false); }
	public sStr newStr(String ref) { return newStr(ref, ref, ""); }
	public sVec newVec(String ref) { return newVec(ref, ref, new Vector2()); }
	public sTab newTab(String ref) { return newTab(ref, ref); }

	public sInt newInt(String ref, int val) { return newInt(ref, ref, val); }
	public sFlt newFlt(String ref, float val) { return newFlt(ref, ref, val); }
	public sBoo newBoo(String ref, boolean val) { return newBoo(ref, ref, val); }
	public sStr newStr(String ref, String val) { return newStr(ref, ref, val); }
	public sVec newVec(String ref, Vector2 val) { return newVec(ref, ref, val); }
	
	
	
	public sInt obtainInt(String ref) { return obtainInt(ref, ref, 0); }
	public sFlt obtainFlt(String ref) { return obtainFlt(ref, ref, 0); }
	public sBoo obtainBoo(String ref) { return obtainBoo(ref, ref, false); }
	public sStr obtainStr(String ref) { return obtainStr(ref, ref, ""); }
	public sVec obtainVec(String ref) { return obtainVec(ref, ref, new Vector2()); }
	public sArr obtainArr(String ref) { return obtainArr(ref, ref); }
	public sTab obtainTab(String ref) { return obtainTab(ref, ref); }

	public sInt obtainInt(String ref, int val) { return obtainInt(ref, ref, val); }
	public sFlt obtainFlt(String ref, float val) { return obtainFlt(ref, ref, val); }
	public sBoo obtainBoo(String ref, boolean val) { return obtainBoo(ref, ref, val); }
	public sStr obtainStr(String ref, String val) { return obtainStr(ref, ref, val); }
	public sVec obtainVec(String ref, Vector2 val) { return obtainVec(ref, ref, val); }
	
	public sValue obtainVal(String ref, Object val) { return obtainVal(ref, ref, val); }

	public sValueBloc obtainBloc(String ref) {
		sValueBloc v = blocs.get(ref);
		if (v != null) { return v; }
		else return newBloc(ref); }
	public sValueBloc obtainBloc(String ref, String build) {
		sValueBloc v = blocs.get(ref);
		if (v != null) { return v; }
		else return buildBloc(build, ref); }
	
	public sValue obtainVal(String ref, String shrt, Object val) {
		sValue v = values.get(ref);
		if (v != null) { return v; }
		else return newVal(ref, shrt, val); }
	
	public sInt obtainInt(String ref, String shrt, int val) {
		sValue v = values.get(ref);
		if (v != null) { return (sInt)v; }
		else return newInt(ref, shrt, val); }
	public sFlt obtainFlt(String ref, String shrt, float val) {
		sValue v = values.get(ref);
		if (v != null) { return (sFlt)v; }
		else return newFlt(ref, shrt, val); }
	public sBoo obtainBoo(String ref, String shrt, boolean val) {
		sValue v = values.get(ref);
		if (v != null) { return (sBoo)v; }
		else return newBoo(ref, shrt, val); }
	public sStr obtainStr(String ref, String shrt, String val) {
		sValue v = values.get(ref);
		if (v != null) { return (sStr)v; }
		else return newStr(ref, shrt, val); }
	public sVec obtainVec(String ref, String shrt, Vector2 val) {
		sValue v = values.get(ref);
		if (v != null) { return (sVec)v; }
		else return newVec(ref, shrt, val); }
	public sArr obtainArr(String ref, String shrt) {
		sValue v = values.get(ref);
		if (v != null) { return (sArr)v; }
		else return newArr(ref, shrt); }
	public sTab obtainTab(String ref, String shrt) {
		sValue v = values.get(ref);
		if (v != null) { return (sTab)v; }
		else return newTab(ref, shrt); }
	
	public String getHierarchy(boolean print_ref) {
		String struct = "<bloc_"+type;
		if (print_ref) struct += "_"+ref;
		struct += ":"+"values<";
		for (Map.Entry<String,sValue> me : values.entrySet()) { 
			sValue v = (sValue)me.getValue(); 
			struct += "<val_"+v.type;
			if (print_ref) struct += "_"+v.ref;
			struct += ">";
		} 
		struct += ">blocs<";
		for (Map.Entry<String,sValueBloc> me : blocs.entrySet()) { 
			sValueBloc vb = (sValueBloc)me.getValue(); 
			struct += vb.getHierarchy(print_ref);
			struct += "-";
		} 
		struct += ">>";
		return struct;
	}
	String getValueHierarchy(boolean print_ref) {
		String struct = "<bloc_"+type;
		if (print_ref) struct += "_"+ref;
		struct += ":"+"values<";
		for (Map.Entry<String,sValue> me : values.entrySet()) { 
			sValue v = (sValue)me.getValue(); 
			struct += "<val_"+v.type;
			if (print_ref) struct += "_"+v.ref;
			struct += ">";
		} 
		struct += ">>";
		return struct;
	}



	
	
	
	
	

	void runIterator(nIterator<sValue> i) { 
		for (Map.Entry<String,sValue> mev : values.entrySet()) {
			sValue v = ((sValue)mev.getValue());
			i.run(v);
		}
		for (Map.Entry<String, sValueBloc> me : blocs.entrySet()) {
			sValueBloc vb = ((sValueBloc)me.getValue());
			vb.runIterator(i);
		}
	}
	public void runValueIterator(nIterator<sValue> i) { 
		for (Map.Entry<String,sValue> mev : values.entrySet()) {
			sValue v = ((sValue)mev.getValue());
			i.run(v);
		}
	}
	public void runBlocIterator(nIterator<sValueBloc> i) { 
		for (Map.Entry<String, sValueBloc> me : blocs.entrySet()) {
			sValueBloc vb = ((sValueBloc)me.getValue());
			i.run(vb); } }
	int runIterator_Counted(nIterator<sValue> i) { return runIterator_Counted(i, 0); }
	int runIterator_Counted(nIterator<sValue> i, int c) { 
		for (Map.Entry<String,sValue> mev : values.entrySet()) {
			sValue v = ((sValue)mev.getValue());
			i.run(v, c); c++;
		}
		for (Map.Entry<String, sValueBloc> me : blocs.entrySet()) {
			sValueBloc vb = ((sValueBloc)me.getValue());
			c = vb.runIterator_Counted(i, c);
		}
		return c;
	}
	void runIterator_Filter(String t, nIterator<sValue> i) { 
		for (Map.Entry<String,sValue> mev : values.entrySet()) {
			sValue v = ((sValue)mev.getValue());
			if (v.type.equals(t)) i.run(v);
		}
		for (Map.Entry<String, sValueBloc> me : blocs.entrySet()) {
			sValueBloc vb = ((sValueBloc)me.getValue());
			vb.runIterator_Filter(t, i);
		}
	}
	int runIterator_Filter_Counted(String t, nIterator<sValue> i) { return runIterator_Filter_Counted(t, i, 0); }
	int runIterator_Filter_Counted(String t, nIterator<sValue> i, int c) { 
		for (Map.Entry<String,sValue> mev : values.entrySet()) {
			sValue v = ((sValue)mev.getValue());
			if (v.type.equals(t)) { i.run(v, c); c++; }
		}
		for (Map.Entry<String, sValueBloc> me : blocs.entrySet()) {
			sValueBloc vb = ((sValueBloc)me.getValue());
			c = vb.runIterator_Filter_Counted(t, i, c);
		}
		return c;
	}
	sValue searchValue(String t) { 
		sValue e = values.get(t);
		if (e != null) return e;
		for (Map.Entry<String, sValueBloc> me : blocs.entrySet()) {
			e = ( (sValueBloc)(me.getValue()) ).searchValue(t);
			if (e != null) return e; }
		return null;
	}
	int getCountOfType(String t) { return getCountOfType(t, 0); }
	int getCountOfType(String t, int c) {
		for (Map.Entry<String,sValue> mev : values.entrySet()) {
			sValue v = ((sValue)mev.getValue());
			if (v.type.equals(t)) c++;
		}
		for (Map.Entry<String, sValueBloc> me : blocs.entrySet()) {
			sValueBloc vb = ((sValueBloc)me.getValue());
			c = vb.getCountOfType(t, c);
		}
		return c;
	}
	
	
	
	
	
	
	
	
	
	

	public void load_from_bloc(File_Bloc sb) {
		if (sb.hasData("__bloc_type") && sb.hasData("__bloc_ref") && 
				sb.hasData("__bloc_bas") && sb.hasData("__bloc_use") && 
				sb.hasData("__bloc_build") && 
				sb.hasData("__bloc_collapseview") && 
				sb.getString("__bloc_type").equals("def")) {
			
//			String b = sb.getData("__bloc_bas");
//			String u = sb.getData("__bloc_use");
//			String build = sb.getData("__bloc_build");
//			use = u;
//			flags.clear();
//			load_flags_to_bloc(sb, this);
			
			open_in_dataview = sb.getBoolean("__bloc_collapseview");
		
			ArrayList<File_Bloc> unfound = new ArrayList<File_Bloc>();
			for (File_Bloc b : sb.getBlocList()) unfound.add(b);
			
			for (Map.Entry<String,sValueBloc> b : blocs.entrySet()) { 
				sValueBloc s = (sValueBloc)b.getValue(); 
				//data.app.vlogln("test vb "+ s.ref);
				File_Bloc child_blocs = sb.getBloc(s.ref);
				if (child_blocs != null) {
					//data.app.vlogln("got save bloc ");
					s.load_from_bloc(child_blocs);
					unfound.remove(child_blocs);
				} 
			}
			
			for (Map.Entry<String,sValue> b : values.entrySet()) { 
				sValue s = (sValue)b.getValue(); 
				//data.app.vlogln("test vb "+ s.ref);
				File_Bloc child_blocs = sb.getBloc(s.ref);
				if (child_blocs != null) {
					//data.app.vlogln("got save bloc ");
					s.load_from_bloc(child_blocs);
					unfound.remove(child_blocs);
				}
			}
			
			for (File_Bloc u : unfound) {
				if (u.hasData("__bloc_type") && 
						u.getString("__bloc_type").equals("val")) {
					newValue(u);
				} else if (u.hasData("__bloc_type") && 
						u.getString("__bloc_type").equals("def")) {
					newBloc(u);
				}
			}
		}
	}

	public void load_params_from_bloc(File_Bloc sb) {
		//data.app.vlogln("svb load " + ref + "  /svb " + sb.blocs.size() + " /sv " + sb.datas.size());

		for (Map.Entry<String,sValueBloc> b : blocs.entrySet()) { 
			sValueBloc s = (sValueBloc)b.getValue(); 
			//data.app.vlogln("test vb "+ s.ref);
			File_Bloc child_blocs = sb.getBloc(s.ref);
			if (child_blocs != null) {
				//data.app.vlogln("got save bloc ");
				s.load_params_from_bloc(child_blocs);
			}
		}

		for (Map.Entry<String,sValue> b : values.entrySet()) { 
			sValue s = (sValue)b.getValue(); 
			//data.app.vlogln("test vb "+ s.ref);
			File_Bloc child_blocs = sb.getBloc(s.ref);
			if (child_blocs != null) {
				//data.app.vlogln("got save bloc ");
				s.load_from_bloc(child_blocs);
			}
		}

		runEventList("eventsLoadParam");

	}

	public void load_values_from_bloc(File_Bloc sb) {
		//data.app.vlogln("svb load " + ref + "  /svb " + sb.blocs.size() + " /sv " + sb.datas.size());

		for (Map.Entry<String,sValue> b : values.entrySet()) { 
			sValue s = (sValue)b.getValue(); 
			//data.app.vlogln("test vb "+ s.ref);
			File_Bloc child_blocs = sb.getBloc(s.ref);
			if (child_blocs != null) {
				//data.app.vlogln("got save bloc ");
				s.load_from_bloc(child_blocs);
			}
		}
	}
	
	public void bloc_data_to_save_bloc(File_Bloc sb) {
		sb.newData("__bloc_type", type);
		sb.newData("__bloc_ref", ref);
		sb.newData("__bloc_bas", base_ref);
		sb.newData("__bloc_use", use);
		sb.newData("__bloc_build", build_ref);
		sb.newData("__bloc_flag_nb", (int)flags.size());
		sb.newData("__bloc_collapseview", open_in_dataview);
		for (int i = 0 ; i < flags.size() ; i++) {
			sb.newData("__bloc_flag_"+i, flags.get(i));
		}
	}
	
	public void preset_value_to_save_bloc(File_Bloc sb) {
		//data.app.dlog("valuebloc " + ref + " saving to savebloc > clearing savebloc >");
		sb.clear();
		////data.app.dlogln(" saving ref typ >");
		
		bloc_data_to_save_bloc(sb);

		////data.app.dlogln("saving under values >");
		for (Map.Entry<String,sValue> me : values.entrySet()) { 
			sValue s = (sValue)me.getValue(); 
			File_Bloc sbv = sb.newBloc((String)me.getKey());
			sbv.newData("__bloc_type", "val");
			s.save_to_bloc(sbv); } 

		////data.app.dlogln("done saving " + ref + " to savebloc");
	}



	public int preset_to_save_bloc(File_Bloc sb) { 
		sb.clear();
		return preset_to_save_bloc(sb, 0); }
	int preset_to_save_bloc(File_Bloc sb, int cnt) {
		//data.app.dlog("valuebloc " + ref + " saving to savebloc > val counter: " + cnt + " > clearing savebloc >");
//		sb.clear();
		////data.app.dlogln(" saving ref typ >");
		
		runEventList("eventsSave");

		bloc_data_to_save_bloc(sb);

		////data.app.dlogln("saving under blocs >");
		for (Map.Entry<String,sValueBloc> me : blocs.entrySet()) { 
			sValueBloc svb = (sValueBloc)me.getValue(); 
			if (svb == this) continue;
			File_Bloc sb2 = sb.newBloc(svb.ref);
			cnt = svb.preset_to_save_bloc(sb2, cnt); 
		} 
		////data.app.dlogln("saving under values >");
		for (Map.Entry<String,sValue> me : values.entrySet()) { 
			sValue s = (sValue)me.getValue(); 
			File_Bloc sbv = sb.newBloc((String)me.getKey());
			sbv.newData("__bloc_type", "val");
			cnt++;
			s.save_to_bloc(sbv); } 

		////data.app.dlogln("done saving " + ref + " to savebloc");
		return cnt;
	}


	sValue newValue(File_Bloc sb) {
		//logln(ref+" newValue from SB "+sb.name);
		//logln("   type "+sb.getData("__bloc_type"));
		sValue nv = null;
		if (sb.hasData("__bloc_type") && sb.getString("__bloc_type").equals("val")) {
			String n = sb.getString("ref");
			String s = sb.getString("shr");
			String t = sb.getString("typ");
			if (t.equals("int")) { nv = data.newInt(this, 0, n, s);      nv.load_from_bloc(sb); }
			if (t.equals("flt")) { nv = data.newFlt(this, 0, n, s);      nv.load_from_bloc(sb); }
			if (t.equals("boo")) { nv = data.newBoo(this, false, n, s);  nv.load_from_bloc(sb); }
			if (t.equals("str")) { nv = data.newStr(this, "", n, s);     nv.load_from_bloc(sb); }
			if (t.equals("vec")) { nv = data.newVec(this, n, s);         nv.load_from_bloc(sb); }
			if (t.equals("arr")) { nv = data.newArr(this, n, s);      	 nv.load_from_bloc(sb); }
			if (t.equals("tab")) { nv = data.newTab(this, n, s);      	 nv.load_from_bloc(sb); }
		}
		return nv;
	}
	
	private void load_flags_to_bloc(File_Bloc sb, sValueBloc b) {
		if (sb.hasData("__bloc_flag_nb")) {
			int flag_nb = sb.getInt("__bloc_flag_nb");
			for (int i = 0 ; i < flag_nb ; i++) {
				String f = sb.getString("__bloc_flag_"+i);
				addFlag(f);
			}
		}
	}

	public sValueBloc newBloc(File_Bloc sb) {
		////data.app.dlogln("newbloc");
		if (sb.hasData("__bloc_type") && sb.hasData("__bloc_ref") && 
				sb.hasData("__bloc_bas") && sb.hasData("__bloc_use") && 
				sb.hasData("__bloc_build") && 
				sb.hasData("__bloc_collapseview") && 
				sb.getString("__bloc_type").equals("def")) {
			////data.app.dlogln("got it");
			String b = sb.getString("__bloc_bas");
			String r = sb.getString("__bloc_ref");
			String u = sb.getString("__bloc_use");
			String build = sb.getString("__bloc_build");
			sValueBloc vb = null;
			boolean isbuild = false;
			if (!data.USE_BUILDER || build.length() == 0) vb = data.newBloc(this, r); 
			else { isbuild = true;
				sBloc_Builder bld = getBlocBuilder(build);
				if (bld == null) { isbuild = false; vb = data.newBloc(this, r); }
				else {
					vb = loadBloc(bld, r); //find build, obtain bloc, do init_start 
					if (vb == null) {
						// bloc_builder is solo and 
						// bloc with this builder allready exist
						// abandon 
						return null;
					}
				}
			}
			vb.use = u;
			vb.open_in_dataview = sb.getBoolean("__bloc_collapseview");
			load_flags_to_bloc(sb, vb);
			for (File_Bloc csb : sb.getBlocList()) {
				String type = csb.getString("__bloc_type");
				if (type != null && type.equals("val")) { vb.newValue(csb); } }
			if (isbuild) vb.init_finish();
			for (File_Bloc csb : sb.getBlocList()) {
				String type = csb.getString("__bloc_type");
				if      (type != null && type.equals("def")) { vb.newBloc(csb); } }
			if (isbuild) vb.load_finish();
			vb.runEventList("eventsLoadEnd");
			vb.is_loading = false;
			return vb;
		}
		return null;
	}

	public sValueBloc newBloc(File_Bloc sb, String n) {
		////data.app.dlogln("newbloc");
		if (sb.hasData("__bloc_type") && sb.hasData("__bloc_ref") && 
				sb.hasData("__bloc_bas") && sb.hasData("__bloc_use") && 
				sb.hasData("__bloc_build") && 
				sb.hasData("__bloc_collapseview") && 
				sb.getString("__bloc_type").equals("def")) {
			////data.app.dlogln("got it");
			//String b = sb.getData("__bloc_bas");
			String u = sb.getString("__bloc_use");
			String build = sb.getString("__bloc_build");
			sValueBloc vb = null;
			boolean isbuild = false;
			if (!data.USE_BUILDER || build.length() == 0) vb = data.newBloc(this, n);
			else { isbuild = true;
				sBloc_Builder bld = getBlocBuilder(build);
				if (bld == null) { isbuild = false; vb = data.newBloc(this, n); }
				else {
					vb = loadBloc(bld, n); //find build, obtain bloc, do init_start
					if (vb == null) {
						// bloc_builder is solo and 
						// bloc with this builder allready exist
						// abandon 
						return null;
					}
				}
			}
			vb.use = u;
			vb.open_in_dataview = sb.getBoolean("__bloc_collapseview");
			load_flags_to_bloc(sb, vb);
			for (File_Bloc csb : sb.getBlocList()) {
				String type = csb.getString("__bloc_type");
				if      (type != null && type.equals("def")) { vb.newBloc(csb); } 
				else if (type != null && type.equals("val")) { vb.newValue(csb); }
			}
			if (isbuild) vb.init_finish();
			if (isbuild) vb.load_finish();
			vb.runEventList("eventsLoadEnd");
			vb.is_loading = false;
			return vb;
		}
		return null;
	}
	
	
} 
