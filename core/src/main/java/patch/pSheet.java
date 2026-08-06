package patch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import data.*;
import gui.*;
import util.*;
import aa_nodulo.*;
import app.*;

public class pSheet {


	public static void build() {
		
		build_book();
		
	}
	
	public static class SheetModel {
		public String ref;
		public sBloc_Builder builder;
		public nRun setup_run;
		public boolean def_collapse = false;
		public nMap<pMacro.Macro> macros = new nMap<pMacro.Macro>();
		public SheetModel(String r) { ref = r; }
		public sBloc_Builder getBuilder() { return builder; }
		public SheetModel setSetupRun(nRun n) { 
			setup_run = n; sheet_setup_runs.put(ref, n); return this; }
		public pMacro.Macro addMacro(String r) {
			pMacro.Macro mac = new pMacro.Macro("sheet_"+ref+"_macro_"+r);
			macros.put(r,mac);
			return mac; }
		public void addMacro(String r, String mc) {
			pMacro.Macro mac = pMacro.getMacro(mc);
			if (mac == null) return;
			macros.put(r,mac); }
		public void addMacro(String r, pMacro.Macro mac) {
			if (mac == null) return;
			macros.put(r,mac); }
	}
	public static nMap<SheetModel> sheet_models = new nMap<SheetModel>();

	public static SheetModel getSheetModel(String r) {
		return sheet_models.get(r); }
	public static sBloc_Builder getSheetBuilder(String r) {
		return sheet_builders.get(r); }
	
	public static nMap<sBloc_Builder> sheet_builders = new nMap<sBloc_Builder>();
	public static nMap<pMacro.Macro> sheet_macros = new nMap<pMacro.Macro>();
	public static nMap<nRun> sheet_setup_runs = new nMap<nRun>();
	public static SheetModel newSheet(sData data, String ref) { return newSheet(data, ref, false); }
	public static SheetModel newSheet(sData data, String ref, boolean def_collapse) {

		sBloc_Builder new_builder = new sBloc_Builder(data, "sheet_builder_"+ref)
			.setInitRun(new nRun() { public void run(Object o) {
				sValueBloc b = (sValueBloc)o; newObject(b, ref); }})
			.setLoadRun(new nRun() { public void run(Object o) {
				sValueBloc b = (sValueBloc)o; 
				pSheet sheet = b.object("sheet", pSheet.class);
				if (sheet_setup_runs.get(ref) != null) 
					sheet_setup_runs.get(ref).do_run(sheet);
				sheet.do_sheet_load(); }})
			.setClearRun(new nRun() { public void run(Object o) {
				sValueBloc b = (sValueBloc)o; b.run("clearing");  }})
			;
		
		if (sheet_models.hasKey(ref)) {
			SheetModel sm = sheet_models.get(ref);
			sm.def_collapse = def_collapse;
			return sm; }
		
		pPatch.addEventInit(new nRun() { public void run(Object o) {
			sValueBloc b = (sValueBloc)o; b.addBlocBuilder(new_builder); 
			if (b.is_new_bloc) b.buildBloc("sheet_builder_"+ref, ref);
		}});

		sheet_builders.put(ref, new_builder);
		SheetModel sm = new SheetModel(ref);
		sm.builder = new_builder;
		sm.def_collapse = def_collapse;
		sheet_models.put(ref, sm);
		return sm;
	}

	public static pMacro.Macro setDefMacro(String r) {
		if (sheet_macros.hasKey(r)) {
			sheet_macros.remove(r);
			pMacro.all_macros.remove("sheet_"+r+"_def_macro"); 
		}
		pMacro.Macro mac = new pMacro.Macro("sheet_"+r+"_def_macro");
		sheet_macros.put(r,mac);
		return mac; }
	public static void setDefMacro(String r, String mc) {
		pMacro.Macro mac = pMacro.getMacro(mc);
		if (mac == null) return;
		if (sheet_macros.hasKey(r)) { sheet_macros.remove(r); }
		sheet_macros.put(r,mac); }
	public static void setDefMacro(String r, pMacro.Macro mac) {
		if (sheet_macros.hasKey(r)) { sheet_macros.remove(r); }
		sheet_macros.put(r,mac); }
	public static void setDefCollapse(String r, boolean c) {
		if (sheet_models.hasKey(r)) { sheet_models.get(r).def_collapse = c; } }
	
	
	
	
	public static void dispose(PlaneApplet app) { pool.dispose(); }
	public static final nPool<pSheet> pool = new nPool<pSheet>() {
		protected pSheet newObject() { return new pSheet(); } };
	public static pSheet newObject(sValueBloc b, String model) {
		return pool.obtain().init(b, model); }
	
	
	
	
	


	public void select_sheet() {
		if (patch.select_sheet != null && patch.select_sheet != this) 
			patch.select_sheet.unselect_sheet();
		patch.select_sheet = this;
		selected = true;
		sheet_bound_bound.set_color_outline(Utl.color(0,200,200,255));
	}
	public void unselect_sheet() {
		if (patch.select_sheet == this) {
			selected = false;
			sheet_bound_bound.set_color_outline(Utl.color(0,60,200,255));
			patch.unselect_all(); 
			patch.select_sheet = null;
		}
	}

	nWidgetGroup sheet_bound;
	nWidget sheet_ref, sheet_bound_bound, sheet_link_draw;
	Vector2 sheet_ref_prev_pos = new Vector2();
	
	sValueBloc sheet_content_bloc;
	
	public pPatch patch;
	public pSheet sheet;
	
	public sValueBloc bloc = null;
	public PlaneApplet plane = null;
	public PlaneApplet app = null;

	public String sheet_model;
	public SheetModel model;
	public sBoo val_active_frame, val_collapse;
	public sInt val_prio_frame;
	public static int max_frame_prio = 0;
	
	public boolean selected = false;
	
	public nRun run_collapse;
	
	public pSheet() {}
	public pSheet init(sValueBloc b, String m) {
		bloc = b; app = PlaneApplet.app;
		sheet_model = Utl.copy(m);
		model = sheet_models.get(m);
		plane = app;//b.parent.object("plane", pPlane.class);
		bloc.addObject("plane", plane);
		bloc.addMetode("clearing", new nRun() { public void run() {
			clear(); }}); 
		sheet = this;
		patch = bloc.parent.object("patch", pPatch.class);
		bloc.addObject("sheet", this);
		patch.sheets.put(bloc.ref,this);

		val_active_frame = bloc.obtainBoo("val_active_frame", true);
		val_prio_frame = bloc.obtainInt("val_prio_frame", 5);
		max_frame_prio = Math.max(max_frame_prio, val_prio_frame.get());
		val_prio_frame.addEventChangeLastFrame(new nRun() { public void run() {
			max_frame_prio = Math.max(max_frame_prio, val_prio_frame.get()); }});
		
		val_collapse = bloc.obtainBoo("val_collapse", 
				model.def_collapse || 
				app.config.PATCH_SHEET_COLLAPSE);
		
		sheet_bound = app.gui.addWidgetGroup("sheet_bound");

		sheet_ref = sheet_bound.get("ref"); 
		sheet_bound_bound = sheet_bound.get("bound");
		
		sheet_bound.metode("set_sheet", this);
		patch.view.addWidgetGroup(bloc.ref+"_sheet_bound", sheet_bound);

		sheet_link_draw = app.gui.addWidget("S_link"); 
		sheet_link_draw.setParent(sheet_bound.get("fx"));
//		sheet_bound.get("fx").setVFX();
		sheet_bound.addWidget("sheet_link_draw", sheet_link_draw);
		sheet_link_draw.setCustomDrawer(new nDrawable() { public void drawing() {
			Vector2 m = new Vector2(app.input.mouse);
			m.set(sheet_link_draw.revertWarp(m));
			for (pInstance c : cos) c.run("draw"); 
			for (pInstance c : Utl.duplic(node_links)) c.run("draw", m); 
			if (patch.linking_node_co != null && patch.linking_node_co.sheet == sheet) {
				Vector2 pos = patch.linking_node_co.get("getCenter", Vector2.class);
				if (pos == null) return;
				app.noFill(); app.stroke(255, 2);
				app.line(pos, m); 
				if (app.input.getClick("MouseRight")) {
					patch.linking_node_co = null;
					for (pInstance c : patch.cos) c.run("light_down"); }
			}
		}});
		sheet_link_draw.toFront();

		val_inst_nb = bloc.obtainInt("val_inst_nb");
		val_inst_free = bloc.obtainInt("val_inst_free");
		val_tile_nb = bloc.obtainInt("val_tile_nb");
		val_tile_free = bloc.obtainInt("val_tile_free");
		val_ent_nb = bloc.obtainInt("val_ent_nb");
		val_ent_free = bloc.obtainInt("val_ent_free");
		val_collec_nb = bloc.obtainInt("val_collec_nb");
		val_collec_free = bloc.obtainInt("val_collec_free");
		val_link_nb = bloc.obtainInt("val_link_nb");
		val_link_free = bloc.obtainInt("val_link_free");
		
		sheet_content_bloc = app.data.root_bloc.obtainBloc("sheet_"+bloc.ref+"_content");
		val_inst_tab = sheet_content_bloc.obtainTab("val_inst_tab");
		val_ent_tab = sheet_content_bloc.obtainTab("val_ent_tab");
		val_collec_tab = sheet_content_bloc.obtainTab("val_collec_tab");
		val_link_tab = sheet_content_bloc.obtainTab("val_link_tab");
		
		inst_pool = new sPool<pInstance>(app, val_inst_tab, "inst") {
			public pInstance newObject() { return new pInstance(sheet); }
			public pInstance[] newArray(int i) { return new pInstance[i]; } };

		tile_pool = new sPool<pInstance>(app, null, "tile") {
			public pInstance newObject() { return new pInstance(sheet); }
			public pInstance[] newArray(int i) { return new pInstance[i]; } };

		ent_pool = new sPool<pInstance>(app, val_ent_tab, "ent") {
			public pInstance newObject() { return new pInstance(sheet); }
			public pInstance[] newArray(int i) { return new pInstance[i]; } };

		link_pool = new sPool<pInstance>(app, val_link_tab, "link") {
			public pInstance newObject() { return new pInstance(sheet); }
			public pInstance[] newArray(int i) { return new pInstance[i]; } };

		pool_map.put("inst", inst_pool);
		pool_map.put("tile", tile_pool);
		pool_map.put("ent", ent_pool);
		pool_map.put("link", link_pool);
			
		collec_pool = new sPool<pColl>(app, val_collec_tab, "collec") {
			public pColl newObject() { return new pColl(); }
			public pColl[] newArray(int i) { return new pColl[i]; } };
		
		return this;
	}

	public void do_sheet_load() { 
		if (
//				!app.config.STARTUP_LOAD && 
				app.config.PATCH_BUILD) {
			app.addDelayEvent(8, new nRun() { public void run() {
				if (sheet_macros.get(sheet_model) != null) {
					sheet_macros.get(sheet_model).add(sheet, false); 
//					app.addDelayEvent(10, new nRun() { public void run() {
//						run_collapse.run(); }}); 
				} 
			}}); 
		}
			
////		if (plane.bloc.is_new_bloc) system_load();
////		else {
//			plane.bloc.addEventLoadEnd(new nRun() { public void run() {
//				system_load();
//			}});
////		}
	}
	public void system_load() {
		if (!app.config.RELEASE) tool_setup(false);

//		load_sheet_contents();

		run_collapse.run();
		
	}
	
	public void clear() {

//		unselect_all();
		
//		view_ref.clear();

		inst_pool.clear();
		tile_pool.clear();
		ent_pool.clear();
		link_pool.clear();
		collec_pool.clear();

		patch.sheets.remove(bloc.ref,this);
		
		bloc.clear();
		bloc = null; plane = null; 
		
	}
	

	public void tool_init(nInterface interf) {
		interf.setContext(bloc);
		
		interf.add_row();
		interf.add_row_watch(5, "inst: ", "val_inst_nb");
		interf.add_row_watch(5, " / ", "val_inst_free");
		interf.add_row();
		interf.add_row_watch(5, "tile: ", "val_tile_nb");
		interf.add_row_watch(5, " / ", "val_tile_free");
		interf.add_row();
		interf.add_row_watch(5, "entry: ", "val_ent_nb");
		interf.add_row_watch(5, " / ", "val_ent_free");
		interf.add_row();
		interf.add_row_watch(5, "collec: ", "val_collec_nb");
		interf.add_row_watch(5, " / ", "val_collec_free");
		interf.add_row();
		interf.add_row_watch(5, "link: ", "val_link_nb");
		interf.add_row_watch(5, " / ", "val_link_free");
		
	}
	public void tool_setup(boolean open) {
		app.addDelayEvent(1, new nRun(this) { public void run() {
			nWidgetGroup sec = app.menu.toolbox
					.addSection("Sheet "+sheet_model, open);
			nInterface interf = app.gui.addInterface();
			interf.pop(sec);
			interf.setContext(bloc);
			tool_init(interf);
		}});
	}

	public void frame(float delta) {

		for (pInstance c : tiles) c.run("frame"); 
		for (pInstance c : plugs) c.run("frame"); 
		for (pInstance c : node_plugs) c.run("frame"); 
		for (pInstance c : cos) c.run("frame"); 
		
		nRun.runEvents(eventFrameRun);
		nRun.runEvents(eventFrameRun, delta);
		
		val_inst_nb.set(inst_pool.size());
		val_inst_free.set(inst_pool.capacity());
		val_tile_nb.set(tile_pool.size());
		val_tile_free.set(tile_pool.capacity());
		val_ent_nb.set(ent_pool.size());
		val_ent_free.set(ent_pool.capacity());
		val_collec_nb.set(collec_pool.size());
		val_collec_free.set(collec_pool.capacity());
		val_link_nb.set(link_pool.size());
		val_link_free.set(link_pool.capacity());
		patch.val_inst_nb.add(inst_pool.size());
		patch.val_inst_free.add(inst_pool.capacity());
		patch.val_tile_nb.add(tile_pool.size());
		patch.val_tile_free.add(tile_pool.capacity());
		patch.val_ent_nb.add(ent_pool.size());
		patch.val_ent_free.add(ent_pool.capacity());
		patch.val_collec_nb.add(collec_pool.size());
		patch.val_collec_free.add(collec_pool.capacity());
		patch.val_link_nb.add(link_pool.size());
		patch.val_link_free.add(link_pool.capacity());
		
		sheet_link_draw.toFront();
	}
	
	ArrayList<nRun> eventFrameRun = new ArrayList<nRun>();
	
	public pSheet addEventFrame(nRun r) { eventFrameRun.add(r); return this; }
	public pSheet removeEventFrame(nRun r) { eventFrameRun.remove(r); return this; }
	public pSheet clearEventFrame() { eventFrameRun.clear(); return this; }

	
	
	
	
	


	public nMap<sPool<pInstance>> pool_map = new nMap<sPool<pInstance>>();
	public sPool<pInstance> inst_pool;
	public sPool<pInstance> tile_pool;
	public sPool<pInstance> ent_pool;
	public sPool<pInstance> link_pool;
	public sPool<pColl> collec_pool;
	sTab val_inst_tab, val_ent_tab, val_link_tab, val_collec_tab, val_inst_database;
	sInt val_inst_nb, val_inst_free, val_ent_nb, val_ent_free, val_collec_nb, val_collec_free, 
		val_link_nb, val_link_free, val_tile_nb, val_tile_free;
	sTab val_tab_copy_stack;
	

	ArrayList<pInstance> nodes = new ArrayList<pInstance>();
	ArrayList<pInstance> cos = new ArrayList<pInstance>();
	ArrayList<pInstance> node_links = new ArrayList<pInstance>();
	ArrayList<pInstance> node_plugs = new ArrayList<pInstance>();

	ArrayList<pInstance> tiles = new ArrayList<pInstance>();
	ArrayList<pInstance> plugs = new ArrayList<pInstance>();


	public pInstance newTile(String s, Object ... args) { 
		if (pTile.tile_models.get(s) == null) {
			Utl.logn("ERROR : pSheet.newTile : invalide tile model : "+s);
			return null; }
		pInstance n = newInstance("tile_model_"+s, args);
//		run_collapse.run();
		return n; } 

	public pInstance newNode(String s, Object ... args) { 
		if (pNode.node_models.get(s) == null) {
			Utl.logn("ERROR : pSheet.newNode : invalide node model : "+s);
			return null; }
		pInstance n = newInstance("node_model_"+s, args);
//		run_collapse.run();
		return n; } 
	
	public pInstance newInstance(String s, Object ... args) {
		pStandard st = pStandard.get(s); 
		if (st == null || pool_map.get(st.pool_ref) == null) return null;
		pInstance p = pool_map.get(st.pool_ref).obtain();
		p.init(st, args);
		return p; } 

	public pColl newCollec(pStandard s, String r) { 
		pColl c = collec_pool.obtain();
		c.init(this, s, r); 
		return c; }

	public void clear_all_inst() {
		inst_pool.freeAll();
		tile_pool.freeAll();
		ent_pool.freeAll(); 
		collec_pool.freeAll(); 
		link_pool.freeAll(); 
	}

	

	public void get_svalues() {
		sheet_content_bloc = app.data.root_bloc.obtainBloc("sheet_"+bloc.ref+"_content");
		val_inst_tab = sheet_content_bloc.obtainTab("val_inst_tab");
		val_ent_tab = sheet_content_bloc.obtainTab("val_ent_tab");
		val_collec_tab = sheet_content_bloc.obtainTab("val_collec_tab");
		val_link_tab = sheet_content_bloc.obtainTab("val_link_tab");
		inst_pool.tab = val_inst_tab;
		ent_pool.tab = val_ent_tab;
		collec_pool.tab = val_collec_tab;
		link_pool.tab = val_link_tab;
	}
	
	public void save_sheet_contents() {
		sheet_content_bloc = app.data.root_bloc.obtainBloc("sheet_"+bloc.ref+"_content");
		val_inst_tab = sheet_content_bloc.obtainTab("val_inst_tab");
		val_ent_tab = sheet_content_bloc.obtainTab("val_ent_tab");
		val_collec_tab = sheet_content_bloc.obtainTab("val_collec_tab");
		val_link_tab = sheet_content_bloc.obtainTab("val_link_tab");
		inst_pool.tab = val_inst_tab;
		ent_pool.tab = val_ent_tab;
		collec_pool.tab = val_collec_tab;
		link_pool.tab = val_link_tab;
		
		collec_pool.save();
		ent_pool.save();
		tile_pool.save();
		inst_pool.save();
		link_pool.save();
	}
	
	public void load_sheet_contents() {
		link_pool.freeAll();
		inst_pool.freeAll();
		tile_pool.freeAll();
		ent_pool.freeAll();
		collec_pool.freeAll();
		
		sheet_content_bloc = app.data.root_bloc.obtainBloc("sheet_"+bloc.ref+"_content");
		val_inst_tab = sheet_content_bloc.obtainTab("val_inst_tab");
		val_ent_tab = sheet_content_bloc.obtainTab("val_ent_tab");
		val_collec_tab = sheet_content_bloc.obtainTab("val_collec_tab");
		val_link_tab = sheet_content_bloc.obtainTab("val_link_tab");
		inst_pool.tab = val_inst_tab;
		ent_pool.tab = val_ent_tab;
		collec_pool.tab = val_collec_tab;
		link_pool.tab = val_link_tab;
		
		collec_pool.load();
		ent_pool.load_1();
		inst_pool.load_1();
		link_pool.load_1();
		ent_pool.load_2();
		inst_pool.load_2();
		link_pool.load_2();
		ent_pool.load_3();
		inst_pool.load_3();
		link_pool.load_3();

		for (pInstance b : Utl.duplic(inst_pool.all())) b.do_point_after_load();
		for (pInstance b : Utl.duplic(ent_pool.all())) b.do_point_after_load();
		for (pInstance b : Utl.duplic(link_pool.all())) b.do_point_after_load();

		app.addDelayEvent(1, new nRun() { public void run() {
			for (pInstance b : Utl.duplic(cos)) b.run("run_event_link"); 
			for (pInstance b : Utl.duplic(plugs)) b.run("run_event_link");
			for (pInstance b : Utl.duplic(node_plugs)) b.run("run_event_link");  }});

		run_collapse.run();
	}
	

	public ArrayList<pInstance> build_insts_from_tab(sTab tab) {
		
//		app.log("Paste");

		ArrayList<pInstance> insts = new ArrayList<pInstance>();
		
		if (tab == null || tab.width() < 2) return insts; 

		ArrayList<pInstance> ents = new ArrayList<pInstance>();
		ArrayList<pInstance> links = new ArrayList<pInstance>();
		ArrayList<pColl> cols = new ArrayList<pColl>();
		
		HashMap<String,String> old_new = new HashMap<String,String>();
		
		int inst_nb = tab.getInt(0,0);
		int ent_nb = tab.getInt(0,1);
		int link_nb = tab.getInt(0,2);
		int col_nb = tab.getInt(0,3);

		int cnt = 4;
		
		for (int i = 0 ; i < inst_nb ; i++) {
			pInstance b = inst_pool.obtain_uninit();
			String old_name = tab.getStr(0,cnt); cnt++;
			old_new.put(old_name,b.pool_ref);
			insts.add(b); }
		for (int i = 0 ; i < ent_nb ; i++) {
			pInstance b = ent_pool.obtain_uninit();
			String old_name = tab.getStr(0,cnt); cnt++;
			old_new.put(old_name,b.pool_ref);
			ents.add(b); }
		for (int i = 0 ; i < link_nb ; i++) {
			pInstance b = link_pool.obtain_uninit();
			String old_name = tab.getStr(0,cnt); cnt++;
			old_new.put(old_name,b.pool_ref);
			links.add(b); }
		for (int i = 0 ; i < col_nb ; i++) {
			pColl b = collec_pool.obtain_uninit();
			String old_name = tab.getStr(0,cnt); cnt++;
			old_new.put(old_name,b.pool_ref);
			cols.add(b); }
		
		cnt = 1;
		for (int i = 0 ; i < inst_nb ; i++) {
			insts.get(i).from_tab(tab, cnt, old_new); cnt++; }
		for (int i = 0 ; i < ent_nb ; i++) {
			ents.get(i).from_tab(tab, cnt, old_new); cnt++; }
		for (int i = 0 ; i < link_nb ; i++) {
			links.get(i).from_tab(tab, cnt, old_new); cnt++; }
		for (int i = 0 ; i < col_nb ; i++) {
			cols.get(i).from_tab(tab, cnt, old_new); cnt++; }

		for (pColl b : cols) if (b.stand == null) { cols.remove(b); b.clear(); }
		for (pInstance b : links) if (b.stand == null) { links.remove(b); b.clear(); }
		for (pInstance b : ents) if (b.stand == null) { ents.remove(b); b.clear(); }
		for (pInstance b : insts) if (b.stand == null) { insts.remove(b); b.clear(); }
		
		for (pColl b : cols) { b.do_init(); }
		for (pInstance b : ents) { b.do_init(); }
		for (pInstance b : insts) { b.do_init(); }
		for (pInstance b : links) { b.do_init(); }
		for (pColl b : cols) { b.do_load(); }
		for (pInstance b : ents) { b.do_load(); }
		for (pInstance b : insts) { b.do_load(); }
		for (pInstance b : links) { b.do_load(); }

		for (pInstance b : ents) b.do_point_after_load();
		for (pInstance b : insts) b.do_point_after_load();
		for (pInstance b : links) b.do_point_after_load();

		run_collapse.run();
		
		return insts;
	}
	
	

	
	
	
	
	
	
	
	
	

	
	
	public static void build_book() {
		nModelBook book = nGUI.book;
		float RS = book.RS;
		
//		book.newModel("S_ref")
////		.setBoundChild(true)
////		.setBoundParent(true)
////		.setStacked(true)
////		.setStackAxis(nAlign.VERTICAL) // HORIZONTAL   VERTICAL
////		.setStackDirection(nAlign.UP) // RIGHT   LEFT   UP   DOWN
//		.setRectOrigin(nAlign.LEFT,nAlign.BOTTOM) // TOP   BOTTOM
//		.setBoundOutspace(0)
//		.setStackSpacing(0)
//		;

		book.newModel("S_link")
		.setRectOrigin(nAlign.LEFT,nAlign.BOTTOM) // TOP   BOTTOM
		;

		
		
		
		book.newModel("SB_ref")
//		.setPassif()
//		.setBoundParent(true)
		.set_color_background(Utl.color(0,0))
		.setDraw(false)
		.setRectOrigin(nAlign.LEFT,nAlign.BOTTOM) // TOP CENTER  BOTTOM
		.setBoundOutspace(0)
		.setStackSpacing(0)
		;

		book.newModel("SB_bound")
		.set_color_background(Utl.color(0,0))
		.setPassif()
		.setRectOrigin(nAlign.LEFT,nAlign.BOTTOM) // TOP   BOTTOM
		.set_color_outline(Utl.color(0,60,200,255))
		.setOutline(true)
		.setOutlineWeight(RS/15f)
		.setOutlineConstant(true)
		;


		book.newModel("SB_topbar")
		.setRect(0,0,RS,RS*6f)
		.setGlueSide(nAlign.TOP)
		.setGlueAlign(nAlign.CENTER)
		.setRectOrigin(nAlign.LEFT,nAlign.BOTTOM) // TOP CENTER  BOTTOM
		.setBoundOutspace(0)
		.setStackSpacing(0)
		.setFont(128)
		;
		book.newModel("SB_bar_button")
		.setRect(0,0,RS*6f,RS*6f)
		.setGlueSide(nAlign.RIGHT)
		.setGlueAlign(nAlign.CENTER)
		.setRectOrigin(nAlign.LEFT,nAlign.BOTTOM) // TOP CENTER  BOTTOM
		.setBoundOutspace(0)
		.setStackSpacing(0)
		.setFont(128)
		;

		book.newModel("SB_botbar")
		.setRect(0,0,RS,RS)
		.setGlueSide(nAlign.BOTTOM)
		.setGlueAlign(nAlign.CENTER)
		.setBoundChild(true)
		.setStackAxis(nAlign.HORIZONTAL) // HORIZONTAL   VERTICAL
		.setStackDirection(nAlign.RIGHT) // RIGHT   LEFT   UP   DOWN
		.setRectOrigin(nAlign.LEFT,nAlign.BOTTOM) // TOP CENTER  BOTTOM
		.setBoundOutspace(0)
		.setStackSpacing(0)
		;

		book.newModel("SB_botinterf_back")
		.setBoundChild(true)
		.setBoundParent(true)
		.setStacked(true)
		.setRectOrigin(nAlign.LEFT,nAlign.BOTTOM) // TOP CENTER  BOTTOM
		.setBoundOutspace(0)
		.setStackSpacing(0)
		;

		book.newModel("SB_fx")
//		.setRect(0,0,RS*15f,RS*10f)
		.set_color_background(Utl.color(0, 0, 0, 0))
//		.setBoundParent(true)
//		.setBoundChild(true)
//		.setBoundOutspace(0f)
//		.setStackSpacing(0f)
		.setPassif()
		.setDraw(false)
		;
		
		
		book.newModelGroup("sheet_bound", new nModelGroup() { 
			public nWidgetGroup build(nGUI gui) {
				nWidgetGroup g = gui.addWidgetGroup();

				nWidget ref = g.addWidget("ref", "SB_ref");
				
				nWidget fx = g.addWidget("fx", gui.addWidget("SB_fx")
						.setParent(ref)
						);
				
				nWidget bound = g.addWidget("bound", "SB_bound")
									.setParent(ref);
				
				nWidget topbar = g.addWidget("topbar", "SB_topbar")
						.setParent(ref);
				topbar.setSizeCopyX(bound).setGlueCible(bound);
				
				nWidget collapse = g.addWidget("collapse", "SB_bar_button")
						.setParent(ref);
				collapse.setGlueCible(topbar);
				collapse.setText("_").setSwitch();
				
				nWidget botbar = g.addWidget("botbar", "SB_botbar")
						.setParent(ref);
				botbar.setGlueCible(bound);

				nWidget botback = g.addWidget("botback", "SB_botinterf_back")
						.setParent(botbar);
				
				nInterface interf = PlaneApplet.app.gui.addInterface();
				interf.pop(botback);
				
				g.addMetode("set_sheet", new nRun() { public void run(Object o) {
					pSheet sheet = (pSheet)o;
					g.addObject("sheet", sheet);
					ref.setParent(sheet.patch.patch_ref);
					topbar.setText(sheet.sheet_model);
					collapse.setLink(sheet.val_collapse);
					if (sheet.val_collapse.get()) collapse.setOn(); else collapse.setOff();
					sheet.run_collapse = new nRun() { public void run() {
						if (sheet.val_collapse.get()) { 
							for (nWidget c : sheet.sheet_ref.getChilds()) 
								if (c != bound && c != topbar && 
									c != collapse && c != botbar) c.hide(); 
						} else { 
							for (nWidget c : sheet.sheet_ref.getChilds()) 
//								if (c != bound && c != topbar && 
//								c != collapse && c != botbar) 
									c.show(); }
					}};
					sheet.val_collapse.addEventChangeLastFrame(sheet.run_collapse);
					sheet.run_collapse.run();
					
					interf.setContext(sheet.bloc);
					interf.add_row();
					interf.add_row_label(10, "Sheet");
					
					interf.set_param("entry_height","0.7");
					interf.add_row();
					nWidgetGroup build_list = interf.add_scrollist(10,5);
					
					interf.change_current_list(build_list);
					interf.set_param("entry_height","0.7");

					int i = 0;
					nWidget ent = null;
					for (String mr : sheet.model.macros.allKey()) {
						if (i%2 == 0) {
							nWidget w = interf.add_list_entry("");
							w.setSY(nGUI.book.RS/5f);
							ent = interf.add_list_entry("");
							ent.setBoundChild(true).setBoundOutspace(0);
						} else {
							nWidget w = interf.get_row_entry_widget(1);
							w.setParent(ent);
							w.setSX(nGUI.book.RS/2f);
						}
						nWidget w = interf.get_row_button_widget(4);
						w.setText(mr);
						w.setParent(ent);
						w.setTrigger();
						w.addEventTrigger(new nRun(mr, sheet) { public void run() {
							arg(1, pSheet.class).model.macros.get(arg(0, String.class))
								.add(arg(1, pSheet.class), true);
						}});
						i++;
					}
					if (i%2 != 0) {
						nWidget w = interf.get_row_entry_widget(5);
						w.setParent(ent); 
					}
					interf.add_list_entry("");
				}});
				
				g.addMetode("bound_up", new nRun() { public void run() {
					pSheet sheet = g.object("sheet", pSheet.class);
					if (sheet == null) return;

					float boundOutspace = RS*10f;
					if (sheet.val_collapse.get()) {
						bound.setPos(- boundOutspace * 3f, - boundOutspace);
						bound.setSize(boundOutspace * 6f, boundOutspace * 2f);
						return;
					}
					float bx = - boundOutspace, by = - boundOutspace, 
							bx2 = boundOutspace, by2 = boundOutspace;
					nWidget r = sheet.sheet_ref;
					Vector2 p = null;
					if (sheet.nodes.size() > 0) {
						pInstance n = sheet.nodes.get(0);
						nWidget w = n.object("group", nWidgetGroup.class).get("selline");
						p = w.getPosRelativeToParent(r);
						bx = p.x - boundOutspace; by = p.y - boundOutspace; 
						bx2 = p.x + w.boundedSize.x + boundOutspace; 
						by2 = p.y + w.boundedSize.y + boundOutspace;
					}
					for (pInstance n : sheet.nodes) {
						nWidget w = n.object("group", nWidgetGroup.class).get("selline");
						p = w.getPosRelativeToParent(r);
						bx = Math.min(bx, p.x - boundOutspace);
						by = Math.min(by, p.y - boundOutspace); }
					for (pInstance n : sheet.nodes) {
						nWidget w = n.object("group", nWidgetGroup.class).get("selline");
						p = w.getPosRelativeToParent(r);
						bx2 = Math.max(bx2, p.x + w.boundedSize.x + boundOutspace);
						by2 = Math.max(by2, p.y + w.boundedSize.y + boundOutspace); }
					bound.setPos(bx, by);
					bound.setSize(bx2 - bx, by2 - by);
					
				}});
				
				
				return g;
			} 
		} );
		
	}
	
}
