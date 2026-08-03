package patch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import app.Applet;
import app.nMap;
import app.nPool;
import app.nRun;

import data.*;
import data.sPool.State;
import gui.*;
import patch.pMacro.Macro;
import plane.pAtom;
import plane.pBody;
import plane.pCollec;
import plane.pParam;
import plane.pPlane;
import plane.pProperty;
import plane.pSpace;
import plane.pSystem;
import plane.pView;

public class pPatch extends pSystem {

	public static sBloc_Builder builder = null;
	
	public static void build(Applet app) {

		builder = builder(app, "patch", pPatch.class, new nRun() { public void run(Object o) {
			sValueBloc b = (sValueBloc)o; newObject(b); }});

		app.addPrefRun(new nRun() { public void run() {
			app.setPref("default", "DEF_PATCH_WIN_POS", new Vector2(370f,915f));
			app.setPref("default", "DEF_PATCH_WIN_SZ", new Vector2(910f,430f));
			app.setPref("focus_space", "DEF_PATCH_WIN_POS", new Vector2(370f,915f));
			app.setPref("focus_space", "DEF_PATCH_WIN_SZ", new Vector2(910f,430f));
			app.setPref("release", "DEF_PATCH_WIN_POS", new Vector2(20f,915f));
			app.setPref("release", "DEF_PATCH_WIN_SZ", new Vector2(1260f,430f));
			app.setPref("release_FS", "DEF_PATCH_WIN_POS", new Vector2(20f,1030f));
			app.setPref("release_FS", "DEF_PATCH_WIN_SZ", new Vector2(660f,950f));
		}});

		build_database_book(app);

		build_book(app);
		
		pStandard.build(app);
		pCommande.build(app);
		pProcess.build(app);

		pMacro.build(app);

		
		pSheet.build(app);

		build_sheet(app);

		pNodeSpace.build_sheet(app);
		
		
		pNode.build(app);
		
		pNodeUI.build(app);

		pNodeSpace.build_nodes(app);

		pNodeAction.build(app);

		pTile.build(app);
		
		pTileHead.build_nodes(app);
		pTileHead.build_tiles(app);
		
		pFunc.build(app);

		pAnk.build(app);

	}
	public static void dispose() { 
		pool.dispose(); 
		pFunc.dispose();
	}
	public static final nPool<pPatch> pool = new nPool<pPatch>() {
		protected pPatch newObject() { return new pPatch(); } };
	public static pPatch newObject(sValueBloc b) {
		return pool.obtain().init(b); }
	
	
	
	public static void build_sheet(Applet app) {
		pSheet.SheetModel func_sheet_model = pSheet.newSheet("function", false);
		
		pSheet.setDefMacro("function", "FUNCTION_SETUP");
//
		func_sheet_model.addMacro("function", "function");
		func_sheet_model.addMacro("FUNCTION_SETUP", "FUNCTION_SETUP");
		
		pSheet.SheetModel main_sheet_model = pSheet.newSheet("main");

		new Macro("main_sheet_def")
		.addMacro("sel_body", pMacro.getMacro("sel_body"), 	0f, 	0f)
		.addMacro("exec", pMacro.getMacro("executor"), 	1500f, 	0f)
		.addNode("from", "from", 		-600f, 	0f)
		.addSetVar("this_ref", "from1").addSetVar("target_ref", "to1").getMacro()
		.addLink("sel_body_sel_body", "in", "from", "out")
		.addLink("sel_body_register", "co_register", "exec_exec", "co_reg")
		;

		pSheet.setDefMacro("main", "main_sheet_def");
		
		main_sheet_model.addMacro("main_sheet_def", "main_sheet_def");
		main_sheet_model.addMacro("SETUP", "SETUP");
		main_sheet_model.addMacro("sel_body", "sel_body");

		pPlane.newStartupModel("patch_exemple")
		.setSetupRun(new nRun() { public void run() {
			pSheet.setDefMacro("main", "SETUP");
		}})
		;
		
	}
	
	
	
	
	
	
	
	public void save_insts_to_tab(ArrayList<pInstance> insts, sTab tab) {
		
//		app.log("Copy");
		
		if (tab == null) return; 
		tab.setWidth(0);
		if (insts == null || insts.size() == 0) return;
		for (pInstance b : insts) b.do_save();
		ArrayList<pInstance> ent = new ArrayList<pInstance>();
		ArrayList<pInstance> link = new ArrayList<pInstance>();
		ArrayList<pColl> col = new ArrayList<pColl>();
		for (pInstance b : insts) {
			for (pColl c : b.collec_list) col.add(c);
			
			for (pInstance e : b.collecInstAll("plugs")) {
				ent.add(e); for (pColl c : e.collec_list) col.add(c); } 
			for (pInstance c : b.collecInstAll("cos")) {
				for (pInstance l : c.collecInstAll("links")) {
					link.add(l);
					for (pColl cl : l.collec_list) col.add(cl);
				} 
			} 
		}
		
		int tab_width = 1 + insts.size() + ent.size() + link.size() + col.size();
		tab.setWidth(tab_width);
		tab.setRowHeight(0,4+tab_width);
		tab.set(0,0,insts.size());
		tab.set(0,1,ent.size());
		tab.set(0,2,link.size());
		tab.set(0,3,col.size());
		
		int cnt = 4;
		for (pInstance b : insts) {
			tab.set(0,cnt,b.pool_ref); cnt++; }
		for (pInstance b : ent) {
			tab.set(0,cnt,b.pool_ref); cnt++; }
		for (pInstance b : link) {
			tab.set(0,cnt,b.pool_ref); cnt++; }
		for (pColl b : col) {
			tab.set(0,cnt,b.pool_ref); cnt++; }
		
		cnt = 1;
		for (pInstance b : insts) { 
			tab.setRowHeight(cnt, b.data_size()); 
			b.to_tab(tab,cnt); cnt++; }
		for (pInstance b : ent) { 
			tab.setRowHeight(cnt, b.data_size()); 
			b.to_tab(tab,cnt); cnt++; }
		for (pInstance b : link) { 
			tab.setRowHeight(cnt, b.data_size()); 
			b.to_tab(tab,cnt); cnt++; }
		for (pColl b : col) { 
			tab.setRowHeight(cnt, b.data_size()); 
			b.to_tab(tab,cnt); cnt++; }
		
	}

	public void move_inst_group_to_cam(ArrayList<pInstance> list) {
		Vector2 center = new Vector2();
		for (pInstance b : list) if (b.stand != null) { center.add(b.getDataVec("pos")); }
		center.x /= list.size(); center.y /= list.size();
		sVec cam_pos = view.object("val_cam_pos", sVec.class);
		center.add(-cam_pos.x(), -cam_pos.y());
		center.x = center.x - center.x%pNode.BRIC_GRID_SIZE + pNode.BRIC_GRID_SIZE;
		center.y = center.y - center.y%pNode.BRIC_GRID_SIZE + pNode.BRIC_GRID_SIZE;
		for (pInstance b : list) if (b.stand != null) { b.addDataVec("pos", center); }
	}
	
//	public void build_inst_database(sTab db) {
//		db.setWidth(pNode.node_models.size());
//		int i = 0;
//		for (pStandard model : pNode.node_models.all()) {
//			model.def_to_tab(db,i); i++; }
//	}

	public void save_contents() {
		for (pSheet s : sheets.all()) s.get_svalues();
		
		for (pSheet s : sheets.all()) s.collec_pool.save();
		for (pSheet s : sheets.all()) s.ent_pool.save();
		for (pSheet s : sheets.all()) s.tile_pool.save();
		for (pSheet s : sheets.all()) s.inst_pool.save();
		for (pSheet s : sheets.all()) s.link_pool.save();
	}
	
	public void load_contents() {
		for (pSheet s : sheets.all()) s.link_pool.freeAll();
		for (pSheet s : sheets.all()) s.inst_pool.freeAll();
		for (pSheet s : sheets.all()) s.tile_pool.freeAll();
		for (pSheet s : sheets.all()) s.ent_pool.freeAll();
		for (pSheet s : sheets.all()) s.collec_pool.freeAll();
		
		for (pSheet s : sheets.all()) s.get_svalues();
		
		for (pSheet s : sheets.all()) s.collec_pool.load();
		for (pSheet s : sheets.all()) s.ent_pool.load_1();
		for (pSheet s : sheets.all()) s.inst_pool.load_1();
		for (pSheet s : sheets.all()) s.link_pool.load_1();
		for (pSheet s : sheets.all()) s.ent_pool.load_2();
		for (pSheet s : sheets.all()) s.inst_pool.load_2();
		for (pSheet s : sheets.all()) s.link_pool.load_2();
		for (pSheet s : sheets.all()) s.ent_pool.load_3();
		for (pSheet s : sheets.all()) s.inst_pool.load_3();
		for (pSheet s : sheets.all()) s.link_pool.load_3();

		for (pSheet s : sheets.all()) 
			for (pInstance b : Applet.duplic(s.inst_pool.all())) b.do_point_after_load();
		for (pSheet s : sheets.all()) 
			for (pInstance b : Applet.duplic(s.ent_pool.all())) b.do_point_after_load();
		for (pSheet s : sheets.all()) 
			for (pInstance b : Applet.duplic(s.link_pool.all())) b.do_point_after_load();

		app.addDelayEvent(1, new nRun() { public void run() {
			for (pInstance b : Applet.duplic(cos)) b.run("run_event_link"); 
			for (pInstance b : Applet.duplic(plugs)) b.run("run_event_link");
			for (pInstance b : Applet.duplic(node_plugs)) b.run("run_event_link");  }});

	}
	
	public nMap<pSheet> sheets = new nMap<pSheet>();
	
//	sTab val_inst_database;
	sInt val_inst_nb, val_inst_free, val_ent_nb, val_ent_free, val_collec_nb, val_collec_free, 
		val_link_nb, val_link_free, val_tile_nb, val_tile_free, val_virt_nb, val_virt_free;
	
	sTab val_tab_copy_stack;
	
	

	public void clear_all_inst() {
		for (pSheet s : sheets.all()) s.inst_pool.freeAll();
		for (pSheet s : sheets.all()) s.tile_pool.freeAll();
		for (pSheet s : sheets.all()) s.ent_pool.freeAll(); 
		for (pSheet s : sheets.all()) s.collec_pool.freeAll(); 
		for (pSheet s : sheets.all()) s.link_pool.freeAll(); 
	}

	ArrayList<pInstance> nodes = new ArrayList<pInstance>();
	ArrayList<pInstance> cos = new ArrayList<pInstance>();
	ArrayList<pInstance> node_links = new ArrayList<pInstance>();
	ArrayList<pInstance> node_plugs = new ArrayList<pInstance>();

	ArrayList<pInstance> tiles = new ArrayList<pInstance>();
	ArrayList<pInstance> plugs = new ArrayList<pInstance>();

	ArrayList<pInstance> node_to = new ArrayList<pInstance>();
	ArrayList<pInstance> node_from = new ArrayList<pInstance>();

	ArrayList<pInstance> select_nodes = new ArrayList<pInstance>();
	pSheet select_sheet = null;
	
	public void select_all() {
		for (pInstance b : Applet.duplic(nodes)) b.run("select");
	}
	public void unselect_all() {
		for (pInstance b : Applet.duplic(select_nodes)) b.run("unselect");
	}
	public void clear_select() {
		for (pInstance b : Applet.duplic(select_nodes)) b.clear();
	}
	
	public boolean mouse_is_hover_view() {
		return view.get("background").mouseOverZone; }
	public Vector2 mouse_in_view() {
		Vector2 m = new Vector2(app.input.mouse);
		m.set(view_backref.revertWarp(m)); return m; }

	nMap<pInstance> common_functions = new nMap<pInstance>();
	nMap<pInstance> common_branchs = new nMap<pInstance>();
	
	public int tile_id_counter = 0;
	
	
	

	
	
	
	
	
	
	public pPatch() { super(); }
	public pPatch init(sValueBloc b) { return (pPatch) super.init(b); }
	

	nWidgetGroup view;
	nWidget view_backref, patch_ref, select_grab, patch_pop_close;
	
	String pop_close_user = "";
	public boolean pop_close_user(String n) { return pop_close_user.equals(n) || (pop_close_user.length() == 0); }    
	
	nInterface tool_interf = null;

	sValueBloc patch_content_bloc;
	
	pInstance linking_node_co = null;

	nRun run_frame_end;
	
	public pPatch patch;
	
	public nWidgetGroup patch_dropmenu = null;
	public nWidgetGroup patch_pop = null;
	
	public void system_clear() {

		unselect_all();
		
		app.removeRunFrame(run_frame_end);
		
		pTileHead.func_counter = 0;
		
	}
	
	private void set_viewspace(float posx, float posy, float sx, float sy, float scale) {
		sVec val_pos = view.object("val_pos", sVec.class);
		val_pos.set(posx,posy);
		view.metode("set_size", new Vector2(sx,sy));
		view.metode("event_corner_drag");
		sFlt val_cam_scale = view.object("val_cam_scale", sFlt.class);
		val_cam_scale.set(scale);
		sBoo val_border = view.object("val_border", sBoo.class);
		val_border.set(false);
	}
	
	public void system_init() {
		
		patch = this;
		
		bloc.addObject("patch", this);
		
		plane.storeSystemType(bloc.ref, this.getClass());

		plane.addEventSave(new nRun() { public void run() {
			save_contents(); 
//			if (plane.getSystem(pSpace.class) != null) 
//				plane.getSystem(pSpace.class).save_contents(); 
		}});

		plane.addEventEmpty(new nRun() { public void run() {
			clear_all_inst(); 
			if (plane.getSystem(pSpace.class) != null) 
				plane.getSystem(pSpace.class).clear_all_obj();
			pTileHead.func_counter = 0;
		}});

		plane.addEventLoad(new nRun() { public void run() {
			app.addDelayEvent(1, new nRun() { public void run() {
				app.logn("patch "+bloc.ref+" load_contents");
				load_contents();
				app.addDelayEvent(1, new nRun() { public void run() {
//					if (plane.getSystem(pSpace.class) != null) 
//						plane.getSystem(pSpace.class).load_contents();
					if (plane.getSystem(pSpace.class) != null) 
						plane.getSystem(pSpace.class).start_space();
				}});
			}});
		}});

		run_frame_end = new nRun() { public void run(Object o) {
			float b = (float)o; frame_end(b); }};
		
		app.addRunFrame(run_frame_end);

//		val_inst_database = bloc.obtainTab("val_inst_database");
//		bloc.data.databases.put("patch_inst", val_inst_database);
//		build_inst_database(val_inst_database);
		
		patch_dropmenu = app.gui.addWidgetGroup("dropmenu");
		
		view = app.gui.addWidgetGroup("viewspace");
		bloc.addObject("viewspaceGroup", view);
		view.addWidgetGroup("patch_dropmenu", patch_dropmenu);
		view.metode("link_to_bloc", bloc);
		
		view.metode("set_title", plane.bloc.ref+" patch");
		
		if (!app.getPref("STARTUP_LOAD", Boolean.class)) {
			
			set_viewspace(
				app.getPref("DEF_PATCH_WIN_POS_x", Float.class),
				app.getPref("DEF_PATCH_WIN_POS_y", Float.class),
				app.getPref("DEF_PATCH_WIN_SZ_x", Float.class),
				app.getPref("DEF_PATCH_WIN_SZ_y", Float.class),
				app.getPref("DEF_PATCH_ZOOM", Float.class));
			
//			if (app.getPref("RELEASE", Boolean.class)) {
//				if (app.getPref("start_fullscreen", Boolean.class)) {
//					set_viewspace(
//							20f,1030f, 
//							660f,950f, 
//							app.getPref("DEF_PATCH_ZOOM", Float.class));
//				} else {
//					set_viewspace(
//							20f,915f, 
//							1260f,430f, 
//							app.getPref("DEF_PATCH_ZOOM", Float.class));
//				}
//			} else {
//				if (app.start_solo) set_viewspace(
//						370f,915f, 
//						910f,430f, 
//						app.getPref("DEF_PATCH_ZOOM", Float.class));
//				else set_viewspace(
//						370f,815f, 
////						app.getPref("DEF_PATCH_WIN_POS_x", Float.class),
////						app.getPref("DEF_PATCH_WIN_POS_y", Float.class),
//						510f,330f, 
////						app.getPref("DEF_PATCH_WIN_SZ_x", Float.class),
////						app.getPref("DEF_PATCH_WIN_SZ_y", Float.class),
//						app.getPref("DEF_PATCH_ZOOM", Float.class));
//			}
			sVec val_cam_pos = view.object("val_cam_pos", sVec.class);
			val_cam_pos.set(app.getPref("DEF_PATCH_POS", Vector2.class));
		}

//		view.metode("run_collapse");o
		
		app.menu.add_info_text("patch zoom: ", view.object("val_cam_scale", sFlt.class));
		
		view_backref = view.get("backref");
		patch_pop = app.gui.addWidgetGroup("patch_pop");
		patch_pop.metode("set_patch", this);
		view.addWidgetGroup("patch_pop", patch_pop);
		
		build_tools();

		nRun run_del = new nRun() { public void run() {
			clear_select(); }};
		add_toolbar_trigg("Del", run_del);
		app.menu.add_shortcut_target("Patch - Del", 'O', run_del);
		nRun run_sel = new nRun() { public void run() {
			if (select_nodes.size() == nodes.size()) unselect_all();
			else select_all(); 
		}};
		add_toolbar_trigg("Sel", run_sel);
		app.menu.add_shortcut_target("Patch - Select", 'I', run_sel);
		nRun run_cut = new nRun() { public void run() {
			ArrayList<pInstance> sel = new ArrayList<pInstance>();
			for (pInstance n : select_nodes) sel.add(n);
			save_insts_to_tab(sel, val_tab_copy_stack); 
			clear_select(); }};
		add_toolbar_trigg("Ct", run_cut);
		app.menu.add_shortcut_target("Patch - Cut", 'X', run_cut);
		nRun run_copy = new nRun() { public void run() {
			ArrayList<pInstance> sel = new ArrayList<pInstance>();
			for (pInstance n : select_nodes) sel.add(n);
			save_insts_to_tab(sel, val_tab_copy_stack); 
		}};
		add_toolbar_trigg("Cp", run_copy);
		app.menu.add_shortcut_target("Patch - Copy", 'C', run_copy);
		nRun run_paste = new nRun() { public void run() {
//			ArrayList<pInstance> paste = build_insts_from_tab(val_tab_copy_stack); 
//			unselect_all();
//			for (pInstance b : paste) { b.run("select"); }
//			move_inst_group_to_cam(paste);
		}};
		add_toolbar_trigg("Pst", run_paste);
		app.menu.add_shortcut_target("Patch - Paste", 'V', run_paste);
		
//		app.addDelayEvent(2, new nRun() { public void run() {			
//			bloc.run("add_menu"); 
//			if (bloc.is_new_bloc) {
//				sValueBloc menu_bloc = bloc.getBloc("blocmenu");
////				menu_bloc.getValue("val_tab_sel", sInt.class).set(1);
//				menu_bloc.getValue("val_collapse", sBoo.class).set(true);
//			}
//		}});

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
		val_virt_nb = bloc.obtainInt("val_virt_nb");
		val_virt_free = bloc.obtainInt("val_virt_free");
		
		val_tab_copy_stack = bloc.obtainTab("val_tab_copy_stack");
		
//		nRun run_clear_inst = new nRun() { public void run() {
//			clear_all_inst(); }};
//		bloc.addMetode("clear_instance", run_clear_inst, "view_in_menu");
	
	}
	public void system_load() {
		load_contents();
		if (!app.getPref("RELEASE", Boolean.class)) tool_setup(true);
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
		interf.add_row();
		interf.add_row_watch(5, "Virt: ", "val_virt_nb");
		interf.add_row_watch(5, " / ", "val_virt_free");
		
	}

	public void frame(float delta) {

		val_inst_nb.set(0);
		val_inst_free.set(0);
		val_tile_nb.set(0);
		val_tile_free.set(0);
		val_ent_nb.set(0);
		val_ent_free.set(0);
		val_collec_nb.set(0);
		val_collec_free.set(0);
		val_link_nb.set(0);
		val_link_free.set(0);
		val_virt_nb.set(pFunc.virtual_pool.size());
		val_virt_free.set(pFunc.virtual_pool.capacity());
		
		for (int prio = pSheet.max_frame_prio ; prio >= 0 ; prio--)
			for (pSheet sht : sheets.all()) 
				if (sht.val_prio_frame.get() == prio) sht.frame(delta);
		
		nRun.runEvents(eventFrameRun);
		nRun.runEvents(eventFrameRun, delta);
		
	}

	public void frame_end(float delta) {
		
		pFunc.freeAllVirtual();
		
		for (pSheet sheet : sheets.all()) 
			sheet.sheet_bound.metode("bound_up");
		if (!select_grab.isGrabbed()) {
			float st_x = 0;
			Vector2 p = null, s = null;
			for (pSheet sheet : sheets.all()) {
				p = sheet.sheet_bound_bound.getParentPos();
				s = sheet.sheet_bound_bound.getBoundedSize();
				sheet.sheet_ref.setPos(st_x-p.x,-p.y-s.y/2f);
				st_x += s.x + 8f*app.gui.book.RS;
			}
			st_x /= 2.0f;
			for (pSheet sheet : sheets.all()) { sheet.sheet_ref.addPos(-st_x, 0); }
			for (pSheet sheet : sheets.all()) {
				p = sheet.sheet_ref.getLocalPos().sub(sheet.sheet_ref_prev_pos).scl(-1f);
				sheet.sheet_ref_prev_pos.set(sheet.sheet_ref.getLocalPos()); 
				if (patch.select_sheet == sheet) {
//					if (select_grab.isGrabbed()) {
//						select_grab.move_grabbing_origin(p.x,p.y);
//						select_grab.addPos(p.x,p.y);
//					}
					view.metode("add_cam_pos", p); }
			}
		}
	}
	
	ArrayList<nRun> eventFrameRun = new ArrayList<nRun>();
	
	public pPatch addEventFrame(nRun r) { eventFrameRun.add(r); return this; }
	public pPatch removeEventFrame(nRun r) { eventFrameRun.remove(r); return this; }
	public pPatch clearEventFrame() { eventFrameRun.clear(); return this; }

	
	
	
	
	

	nWidgetGroup build_list = null;
	nInterface bar_interf = null;
	
	public void build_tools() {

		nWidgetGroup tools = app.gui.addWidgetGroup("viewspace_tool");
		bloc.addObject("patch_tool", tools);
		tools.metode("set_py", 10f);
		tools.metode("set_title", "Common Bric Builder");
		tools.metode("link_tool_to_bloc", bloc, "build");
		tools.metode("add_to_viewspace_front", view);
		if (app.getPref("PATCH_TOOL_AUTOCOLLAPSE", Boolean.class)) tools.metode("set_auto_hide");
		tool_interf = (nInterface)tools.metodeGet("get_interf");

		tool_interf.set_param("entry_height","0.7");

		tool_interf.add_row();
		tool_interf.add_row();
		build_list = tool_interf.add_scrollist(10,10);
		
		update_patch_tool();
		
		nWidgetGroup bar = app.gui.addWidgetGroup("viewspace_tool");
		bloc.addObject("bar_viewspace_tool", bar);
		bar.metode("set_px", 350f);
		bar.metode("set_pop_up");
		bar.metode("set_title", "ToolBar");
		bar.metode("link_tool_to_bloc", bloc, "bar");
		bar.metode("add_to_viewspace_front", view);
		bar_interf = (nInterface)bar.metodeGet("get_interf");
		
		bar_interf.set_param("entry_height","1.5");
		
		bar_interf.add_row();
		
	}
	

	public nWidget add_toolbar_trigg(String t, nRun r) {
		nWidget w1 = bar_interf.add_row_trigg(2, t);
		w1.setFont(20);
		w1.addEventTrigger(r);
		return w1;
	}
	public nWidget add_toolbar_switch(String t, nRun r) {
		nWidget w1 = bar_interf.add_row_switch(2, t);
		w1.setFont(20);
		w1.addEventSwitch(r);
		return w1;
	}
	
	public void update_patch_tool() {
		
		tool_interf.change_current_list(build_list);
		tool_interf.set_param("entry_height","0.7");

		int i = 0;
		nWidget ent = null;
//		for (String mr : pMacro.macro_runs.allKey()) {
//			if (i%2 == 0) {
//				nWidget w = tool_interf.add_list_entry("");
//				w.setSY(app.gui.book.RS/5f);
//				ent = tool_interf.add_list_entry("");
//				ent.setBoundChild(true).setBoundOutspace(0);
//			} else {
//				nWidget w = tool_interf.get_row_entry_widget(1);
//				w.setParent(ent);
//				w.setSX(app.gui.book.RS/2f);
//			}
//			nWidget w = tool_interf.get_row_button_widget(4);
//			w.setText(mr);
//			w.setParent(ent);
//			w.setTrigger();
//			w.addEventTrigger(new nRun(mr, patch) { public void run() {
//				pMacro.runMacro(arg(0, String.class), arg(1, pPatch.class)); 
//			}});
//			i++;
//		}
//		if (i%2 != 0) {
//			nWidget w = tool_interf.get_row_entry_widget(5);
//			w.setParent(ent); 
//		}
//		tool_interf.add_list_entry("");
		

		ArrayList<String> grouplist = new ArrayList<String>();
		
		for (Map.Entry<String, pStandard> me : pNode.node_models.entrySet()) {
			if (!Applet.contains(grouplist, pNode.node_group.get(me.getKey())))
				grouplist.add(pNode.node_group.get(me.getKey()));
		}
		for (String sg : grouplist) {
			i = 0;
			ent = null;
			for (String mr : pNode.buildable_node_models.allKey()) 
					if (sg.equals(pNode.node_group.get(mr))) {
				if (i%2 == 0) {
					nWidget w = tool_interf.add_list_entry("");
					w.setSY(app.gui.book.RS/5f);
					ent = tool_interf.add_list_entry("");
					ent.setBoundChild(true).setBoundOutspace(0);
				} else {
					nWidget w = tool_interf.get_row_entry_widget(1);
					w.setParent(ent);
					w.setSX(app.gui.book.RS/2f);
				}
				nWidget w = tool_interf.get_row_button_widget(4);
				w.setText(mr);
				w.setParent(ent);
				w.setTrigger();
				w.addEventTrigger(new nRun(mr) { public void run() {
					if (select_sheet != null) {
						pInstance n = select_sheet.newNode((String)builder);  
						n.run("move_to_cam"); n.run("find_place"); }
				}});
				i++;
			}
			if (i%2 != 0) {
				nWidget w = tool_interf.get_row_entry_widget(5);
				w.setParent(ent); 
			}
			tool_interf.add_list_entry("");
		}
		
	}
	
	
	public static void build_book(Applet app) {
		nModelBook book = app.gui.book;
		float RS = book.RS;
		
		book.newModel("P_ref")
		.copyFrom(book.getModel("ref"))
//		.setBoundChild(true)
//		.setBoundParent(true)
//		.setStacked(true)
//		.setStackAxis(nAlign.VERTICAL) // HORIZONTAL   VERTICAL
//		.setStackDirection(nAlign.UP) // RIGHT   LEFT   UP   DOWN
		.setRectOrigin(nAlign.LEFT,nAlign.BOTTOM) // TOP   BOTTOM
		.setBoundOutspace(0)
		.setStackSpacing(0)
		;
		
		book.newModel("P_pop_close")
		.setSize(RS,RS)
		.setRectOrigin(nAlign.RIGHT,nAlign.BOTTOM) // TOP   BOTTOM
		.setInfo("Delete")
		.setText("X")
		.setFont(24)
		.setTrigger()
		.setHoverableZone(true)
		.setOutline(true)
		.setOutlineWeight(RS/10f)
		.set_color_outline(app.color(255,0,0,255))
		.set_color_background(app.color(180,0,0,255))
		.setScaleLimit(pNode.DEF_SCALE_MIN, pNode.DEF_SCALE_MAX)
		;

		
		
		
		book.newModel("PP_ref")
		.copyFrom(book.getModel("ref"))
		.setPassif()
		.set_color_background(app.color(0,0))
		.setDraw(false)
		.setRectOrigin(nAlign.LEFT,nAlign.BOTTOM) // TOP CENTER  BOTTOM
		.setBoundOutspace(0)
		.setStackSpacing(0)
		;

		book.newModel("PP_selectzone")
		.set_color_background(app.color(0,0))
		.setPassif()
		.setRectOrigin(nAlign.LEFT,nAlign.BOTTOM) // TOP   BOTTOM
		.set_color_outline(app.color(200,200,0,255))
		.setOutline(true)
		.setOutlineWeight(RS/15f)
		.setOutlineConstant(true)
		;
		
		float select_grab_base_size = RS*1.2f;

		book.newModel("PP_select_grab")
		.setSize(select_grab_base_size,select_grab_base_size)
		.setRectOrigin(nAlign.CENTER,nAlign.CENTER) // TOP   BOTTOM
		.setGrabbable()
		.setHoverableZone(true)
		.setOutline(true)
		.setOutlineWeight(RS/6f)
		.setOutlineConstant(true)
		.set_color_pressed(app.color(255,80))
		.set_color_hovered(app.color(255,180))
		.set_color_standby(app.color(255,120))
		.set_color_outline(app.color(0,240,230,255))
		.setShape(nModel.Shape.DIAMOND)
		;
		
		book.newModelGroup("patch_pop", new nModelGroup(app) { 
			public nWidgetGroup build(nGUI gui) {
				nWidgetGroup g = gui.addWidgetGroup();

				nWidget ref = g.addWidget("ref", "P_ref");
				nWidget select_grab = g.addWidget("select_grab", "PP_select_grab");
				nWidget pop_close = g.addWidget("pop_close", "P_pop_close");
				pop_close.hide();
				nWidget selectzone = g.addWidget("selectzone", "PP_selectzone");
				selectzone.hide();
				g.addObject("selzone_clic", false);
				
				Vector2 selzone_start = new Vector2();
				Rectangle selzone = new Rectangle();
				
				g.addMetode("set_patch", new nRun() { public void run(Object o) {
					pPatch patch = (pPatch)o;
					ref.setParent(patch.view_backref);
					patch.patch_ref = ref;
					patch.select_grab = select_grab;
					patch.patch_pop_close = pop_close;
					selectzone.setParent(ref);
					select_grab.setParent(ref).hide();
					pop_close.hide();

					nRun run_selzone = new nRun() { public void run() {
						
						nWidget viewsp_bg = patch.view.get("background");
						
						if (viewsp_bg.mouseOverZone) {
							if (!g.object("selzone_clic", Boolean.class) && 
									app.input.mouseLeft.trigClick) {
								Vector2 m = new Vector2(app.input.mouse);
								m.set(ref.warptransform.revert(m));
								selzone_start.set(m);
								g.setObject("selzone_clic", true);
								selectzone.setRect(m.x,m.y,0,0);
								selectzone.force_calc();
								selectzone.toFront().show();
								patch.unselect_all(); 
								if (patch.select_sheet != null) 
									patch.select_sheet.unselect_sheet();
								for (pSheet s : patch.sheets.all()) {
									nWidget sbb = s.sheet_bound_bound;
									Rectangle wr = new Rectangle(
											sbb.getRectRelativeToParent(ref));
									if (wr.contains(m)) {
										s.select_sheet(); break; } 
								}
							}
						}
						
						if (g.object("selzone_clic", Boolean.class)) {
							Vector2 m = new Vector2(app.input.mouse);
							m.set(ref.warptransform.revert(m));
							m.add(-selzone_start.x, -selzone_start.y);
							Vector2 m2 = new Vector2(selzone_start);
							if (m.x < 0) { m.x *= -1; m2.x -= m.x; }
							if (m.y < 0) { m.y *= -1; m2.y -= m.y;  }
							selectzone.setRect(m2.x, m2.y, m.x, m.y);
							selzone.set(m2.x, m2.y, m.x, m.y);
						} 
						if (patch.select_nodes.size() > 0) {
							patch.select_grab.toFront().show();
							float s = select_grab_base_size / ref.warptransform.getScale();
							patch.select_grab.setSize(s,s);
							if (!patch.select_grab.isGrabbed()) {
								Vector2 m = new Vector2();
								for (pInstance br : patch.select_nodes) {
									m.add(br.object("group", nWidgetGroup.class)
											.metodeGetVec("get_center")); 
									m.add(br.sheet.sheet_ref
											.getPosRelativeToParent(ref)); 
								}
								m.scl(1f / (patch.select_nodes.size()));
								patch.select_grab.setPos(m.x, m.y);
							}
						} else patch.select_grab.hide();

						if (app.input.mouseLeft.trigUClick) {
							g.setObject("selzone_clic", false);
							selectzone.hide();
						}
						if (g.object("selzone_clic", Boolean.class)) {
							for (pInstance br : patch.nodes) 
									if (patch.select_sheet == null || 
										patch.select_sheet == br.sheet){
								nWidget w = br.object("group", nWidgetGroup.class).get("selline");
								Rectangle wr = new Rectangle(w.getRectRelativeToParent(ref));
								if (Applet.intersect(wr, selzone)) {
									br.run("select");
								} else {
									br.run("unselect");
								}
							}
//							if (patch.select_nodes.size() == 0 && 
//									patch.select_sheet != null) 
//								patch.select_sheet.unselect_sheet();
						}
						
					}};
					
					patch.addEventFrame(run_selzone);
					
					g.addObject("drag_pile", new Vector2());
					nRun event_grab = new nRun() { public void run(Object o) {
						g.setObject("drag_pile", new Vector2());
					}};
					nRun event_drag = new nRun() { public void run(Object o) {
						Vector2 dp = (Vector2)o;
						
//						app.log(""+dp.toString());
						
						Vector2 drag_pile = g.object("drag_pile", Vector2.class);
						Vector2 move = new Vector2(); 
						drag_pile.add(dp);
						while (drag_pile.x >= pNode.BRIC_GRID_SIZE + pNode.BRIC_GRID_OVER) {
							drag_pile.x -= pNode.BRIC_GRID_SIZE; 
							move.x += pNode.BRIC_GRID_SIZE; }
						while (drag_pile.y >= pNode.BRIC_GRID_SIZE + pNode.BRIC_GRID_OVER) {
							drag_pile.y -= pNode.BRIC_GRID_SIZE;
							move.y += pNode.BRIC_GRID_SIZE; }
						while (drag_pile.x <= -(pNode.BRIC_GRID_SIZE + pNode.BRIC_GRID_OVER)) {
							drag_pile.x += pNode.BRIC_GRID_SIZE;
							move.x -= pNode.BRIC_GRID_SIZE; }
						while (drag_pile.y <= -(pNode.BRIC_GRID_SIZE + pNode.BRIC_GRID_OVER)) {
							drag_pile.y += pNode.BRIC_GRID_SIZE;
							move.y -= pNode.BRIC_GRID_SIZE; }
						g.setObject("drag_pile", drag_pile);

						for (pInstance br : patch.select_nodes) {
							br.addDataVec("pos", move);
						}
						
					}};
					patch.select_grab = select_grab;
					patch.select_grab.addEventGrab(event_grab);
					patch.select_grab.addEventDrag(event_drag);

					

				}});
				
				
				return g;
			} 
		} );
		
	}
	
	
	
	
	public static sBloc_Builder database_editor_builder;
	public static void build_database_book(Applet app) {
		
		nModelBook book = app.gui.book;
		float RS = book.RS;
		
		database_editor_builder = new sBloc_Builder(app.data, "database_editor")
			.setInitRun(new nRun() { public void run(Object o) {
				sValueBloc b = (sValueBloc)o;
				nWidgetGroup win = app.gui.addWidgetGroup("database_editor");
				b.addObject("database_view_win", win);
				win.metode("link_window_to_bloc", b);
				win.addEventClear(new nRun() { public void run() {
					b.clear(); }});
				b.addEventDelete(new nRun() { public void run() {
					win.clear(); }});
				
				win.metode("run_tofront");
				
				if (b.is_new_bloc) {
					sVec val_pos = win.object("val_pos", sVec.class);
					val_pos.set(20f,445f);
				}
				
			}})
			.setClearRun(new nRun() { public void run(Object o) {
				sValueBloc b = (sValueBloc)o; 
			}});

		app.data.addRootBlocBuilder(database_editor_builder);
		
		book.newModelGroup("database_editor", new nModelGroup(app) { 
			public nWidgetGroup build(nGUI gui) {
				nWidgetGroup g = gui.addWidgetGroup("complex_window");

				nInterface interf = gui.addInterface()
						.pop(g);
				
				g.metode("set_title", "database editor");
				
				interf.add_row();
				interf.add_row_label(10," Select Database : ");
				interf.add_row();
				nWidgetGroup db_list = interf.add_picklist(8,4);
				
				interf.add_row();
				interf.add_row_label(4,"");
				nWidget load_w = interf.add_row_trigg(4,"EDIT");
				load_w.addEventTrigger(new nRun() { public void run() {
					g.metode("edit_tab"); }});

				interf.add_col_separator();
				interf.add_col_separator();

				interf.add_row();
				nWidget tab_lb = interf.add_row_label(8,"sTab : ");
				interf.add_row();
				nWidget tab_h_lb = interf.add_row_label(8,"width :");
				interf.add_row();
				nWidget row_lb = interf.add_row_label(8,"     viewing row : ");
				
				interf.add_row();
				interf.add_row_trigg(2,"<<", new nRun() { public void run() {
					if (!g.hasObject("row")) return;
					int row = g.object("row", Integer.class);
					row -= 1; g.metode("view_tab_row", row);
				}});
				interf.add_row_label(4,"Row");
				interf.add_row_trigg(2,">>", new nRun() { public void run() {
					if (!g.hasObject("row")) return;
					int row = g.object("row", Integer.class);
					row += 1; g.metode("view_tab_row", row);
				}});

				interf.add_row();
				interf.add_row_trigg(3,"Add Row", new nRun() { public void run() {
					sTab tab = g.object("tab", sTab.class);
					if (tab == null) return;
					int row = tab.width() + 1;
					tab.setWidth(row);
					tab_h_lb.setText("width :"+tab.width());
					g.metode("view_tab_row", row-1);
				}});
				interf.add_row_label(2,"");
				interf.add_row_trigg(3,"Del Row", new nRun() { public void run() {
					
				}});

				interf.add_col_separator();


				interf.add_col();

				interf.add_row();
				interf.add_row_label(14," Database Editor : ");
				
				interf.add_row();
				nWidget cell_lb = interf.add_row_label(8,"0 Cell in this row");
				interf.add_row_label(1,"");
				interf.add_row_trigg(4,"Add Cell", new nRun() { public void run() {
					sTab tab = g.object("tab", sTab.class);
					if (tab == null) return;
					if (!g.hasObject("row")) return;
					int row = g.object("row", Integer.class);
					int h = tab.height(row);
					h += 1; tab.setRowHeight(row, h); 
					g.metode("view_tab_row", row);
				}});
				interf.add_row_label(1,"");
//				interf.add_row_trigg(2,"Del", new nRunnable() { public void run() {
//					sTab tab = g.object("tab", sTab.class);
//					if (tab == null) return;
//					if (!g.hasObject("row")) return;
//					int row = g.object("row", Integer.class);
//					int h = tab.height(row);
//					h -= 1; if (h < 0) h = 0;
//					tab.setColHeight(row, h); 
//					g.metode("view_tab_row", row);
//				}});

				interf.add_col_separator();

				interf.add_row();
				nWidgetGroup edit_list = interf.add_scrollist(14,12);

				ArrayList<nWidget> ent_arr = new ArrayList<nWidget>();
				ArrayList<nWidgetGroup> dm_arr = new ArrayList<nWidgetGroup>();
				ArrayList<nRun> run_arr = new ArrayList<nRun>();
				
				g.addMetode("clear_edit_list", new nRun() { public void run() {
					
					interf.change_current_list(edit_list);
					
					row_lb.setText("");
					cell_lb.setText("");

					for (nWidget w : ent_arr) w.clear();
					
					if (g.hasGroup("dropmenu_type")) {
						nWidgetGroup w = g.getGroup("dropmenu_type");
						dm_arr.remove(w);
						g.removeGroup(w); w.clear();
					}
					
					for (nWidgetGroup w : dm_arr) w.clear();
					
					sTab tab = g.object("tab", sTab.class);
					if (tab == null) return;
					for (nRun w : run_arr) tab.removeEventChangeLastFrame(w); 
					
					ent_arr.clear(); dm_arr.clear(); run_arr.clear();
				}});
				
				g.addEventClear(new nRun() { public void run() {
					g.metode("clear_edit_list"); }});
				
				g.addMetode("view_tab_row", new nRun() { public void run(Object o) {
					int row = (Integer)o;
					sTab tab = g.object("tab", sTab.class);
					
					g.metode("clear_edit_list"); 
					
					if (tab == null) return;
					if (row >= tab.width() || row < 0) { 
						if (tab.width() > 0) g.metode("view_tab_row", 0); 
						return; 
					} 

					g.setObject("row", row); 
					row_lb.setText("viewing row : "+row); 

					cell_lb.setText(tab.height(row)+" Cell in this row"); 
					
					for (int i = 0 ; i < tab.height(row) ; i++) {
						
						nWidget ent = interf.add_list_entry("");
						ent.setStackAxis(nAlign.HORIZONTAL).setStackSpacing(2f);
						
						nWidget lab = interf.get_row_entry_widget(2);
						nWidget fld = interf.get_row_entry_widget(8);
						nWidget typ = interf.get_row_entry_widget(1);
						nWidget del = interf.get_row_entry_widget(1);
						
						lab.setParent(ent).setText(""+i).setStacked(true);
						fld.setParent(ent).setStacked(true);
						fld.setField(true).copyLookFrom(app.gui.book.getModel("text_field"));
						typ.setParent(ent).setText("").setTrigger().setStacked(true);
						del.setParent(ent).setText("x").setTrigger().setStacked(true);
						
						if (tab.getObj(row,i) == null) typ.setText("null");
						else if (!Applet.type_is_used(tab.getObj(row,i).getClass())) 
							typ.setText("??");
						else typ.setText(Applet.type_class_type.get(
								tab.getObj(row,i).getClass()));
						
						ent_arr.add(lab); ent_arr.add(fld); 
						ent_arr.add(typ); ent_arr.add(del); 
						
						nWidgetGroup dropmenu_type = null;
						if (g.hasGroup("dropmenu_type"))
							dropmenu_type = g.getGroup("dropmenu_type");
						else {
							dropmenu_type = gui.addWidgetGroup("dropmenu");
							g.addWidgetGroup("dropmenu_type", dropmenu_type);
							dm_arr.add(dropmenu_type);
						}
								
						
						typ.addEventTrigger(new nRun(i) { public void run() {
							nWidgetGroup dm_type = null;
							if (g.hasGroup("dropmenu_type"))
								dm_type = g.getGroup("dropmenu_type");
							else {
								dm_type = gui.addWidgetGroup("dropmenu");
								g.addWidgetGroup("dropmenu_type", dm_type);
								dm_arr.add(dm_type);
							}
							dm_type.metode("clear_entrys");
							for (String sc : Applet.type_short_names) {
								nWidget w1 = (nWidget)dm_type
										.metodeGet("add_entry_custom", sc, 
												RS*6f, RS*2f/3f);
								w1.addEventTrigger(new nRun() { public void run() {
									typ.setText(sc); }}); 
							}
							dm_type.metode("open", typ); 
						}});
						
						fld.addEventFieldChange(new nRun(i) { public void run() {
							tab.set(row, (int)builder, 
									Applet.from_string(fld.getText(), 
									Applet.type_type_class.get(typ.getText()))); 
						}});

						del.addEventTrigger(new nRun(i) { public void run() {
							
						}});
						
						nRun tab_up = new nRun(i) { public void run() {
							fld.setText(Applet.to_string(tab.get(row,(int)builder,
									Applet.type_type_class.get(typ.getText())))); }};
						tab.addEventChangeLastFrame(tab_up);
						tab_up.run();
						run_arr.add(tab_up);
					}
				}});
				
				g.addMetode("list_db", new nRun() { public void run() {
					interf.change_current_list(db_list);
					for(Map.Entry<String,sTab> me : app.data.databases.entrySet()) {
						String r = me.getKey(); sTab tab = me.getValue();
						interf.add_list_entry(r);
					}
				}});
				g.metode("list_db");
				
				g.addMetode("edit_tab", new nRun() { public void run() {
					String db_name = (String)db_list.metodeGet("get_pick");
					if (db_name == null) return;
					sTab tab = app.data.databases.get(db_name);
					if (tab == null) return;
					g.setObject("tab", tab);
					tab_lb.setText("sTab : "+tab.ref);
					tab_h_lb.setText("width :"+tab.width());
					g.metode("view_tab_row", 0);
				}});

				return g;
			} 
		} );
		
	}
}
