package patch;

import java.util.ArrayList;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.noodle.nodulo.GdxApp;

import aa_nodulo.PlaneApplet;
import aa_nodulo.pGeom;
import aa_nodulo.pProperty;
import aa_nodulo.pSpace;
import aa_nodulo.pTime;
import aa_nodulo.pView;
import aa_term.CommandExecutor;
import aa_term.ConsoleDoc;
import aa_term.LogLevel;
import aa_term.SystemExecutor;
import aa_term.pTerm;
import app.App;
import box2d.pBox2d;
import data.*;
import gui.*;
import util.Utl;
import util.nMap;
import util.nRun;

public class pPatch {
	
	public static boolean has_build_statics = false;
	
	public static void build(sData data, nGUI gui) {
		
//		app.addPrefRun(new nRun() { public void run() {
//			app.setPref("default", "DEF_PATCH_WIN_POS", new Vector2(370f,915f));
//			app.setPref("default", "DEF_PATCH_WIN_SZ", new Vector2(910f,430f));
//			app.setPref("focus_space", "DEF_PATCH_WIN_POS", new Vector2(370f,915f));
//			app.setPref("focus_space", "DEF_PATCH_WIN_SZ", new Vector2(910f,430f));
//			app.setPref("release", "DEF_PATCH_WIN_POS", new Vector2(20f,915f));
//			app.setPref("release", "DEF_PATCH_WIN_SZ", new Vector2(1260f,430f));
//			app.setPref("release_FS", "DEF_PATCH_WIN_POS", new Vector2(20f,1030f));
//			app.setPref("release_FS", "DEF_PATCH_WIN_SZ", new Vector2(660f,950f));
//		}});

		if (!has_build_statics) pSpace.build();
//		pGround.build(data); 
		pGeom.build(data); 
		pBox2d.build(data); 
		
		if (!has_build_statics) {
			pProperty.complete_generals();
//			build_database_book();
			build_book();
			pMacroBook.build();
			pSheet.build();
		}

//		build_database_builder(data);		
		build_sheet(data);
		pNodeSpace.build_sheet(data, has_build_statics);
		
		if (!has_build_statics) {
			pNode.build();
			pNodeUI.build();
			pNodeSpace.build_nodes();
			pNodeAction.build();
			pTile.build();
			pTileHead.build_nodes();
			pTileHead.build_tiles();
			pFunc.build();
			pAnk.build();
			pTime.build_node(); 
			pView.build_nodes(); 
		}
		
		has_build_statics = true;
	}
	public static void dispose() { 
//		pFunc.dispose();
	}
	
	
	
	public static void build_sheet(sData data) {
		pSheet.SheetModel func_sheet_model = pSheet.newSheet(data, "function", false);
		
		pSheet.SheetModel main_sheet_model = pSheet.newSheet(data, "main");
		
	}
	

	public static void build_setup() {
		
		PlaneApplet.newStartupModel("exemple")
		.setSetupRun(new nRun() { public void run() {
			
			pSheet.setDefMacro("main", "main_exemple");
			pSheet.setDefMacro("function", "func_exemple");
			pSheet.setDefMacro("blueprint", "PARAM_SETUP");
			pSheet.setDefCollapse("main", false);
			pSheet.setDefCollapse("function", false);
			pSheet.setDefCollapse("blueprint", false);
			
		}})
		;
		
	}
	
	
	
	
	
	
	
	
	public void save_insts_to_tab(ArrayList<pInstance> insts, sTab tab) {
		
//		app.log("Copy");
		
		if (tab == null) return; 
//		tab.setWidth(0);
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
				ent.add(c); for (pColl cl : c.collec_list) col.add(cl);
				for (pInstance l : c.collecInstAll("links")) {
					link.add(l); for (pColl cl : l.collec_list) col.add(cl); } 
			} 
		}
		
		int tab_width = 1 + insts.size() + ent.size() + link.size() + col.size();
		tab.setWidth(tab_width);
		tab.setRowHeight(0,4+tab_width);
		tab.set(0,0,col.size());
		tab.set(0,1,ent.size());
		tab.set(0,2,insts.size());
		tab.set(0,3,link.size());
		
		int cnt = 4;
		for (pColl b : col) {
			tab.set(0,cnt,b.pool_ref); cnt++; }
		for (pInstance b : ent) {
			tab.set(0,cnt,b.pool_ref); cnt++; }
		for (pInstance b : insts) {
			tab.set(0,cnt,b.pool_ref); cnt++; }
		for (pInstance b : link) {
			tab.set(0,cnt,b.pool_ref); cnt++; }
		
		cnt = 1;
		for (pColl b : col) { 
			tab.setRowHeight(cnt, b.data_size()); 
			b.to_tab(tab,cnt); cnt++; }
		for (pInstance b : ent) { 
			tab.setRowHeight(cnt, b.data_size()); 
			b.to_tab(tab,cnt); cnt++; }
		for (pInstance b : insts) { 
			tab.setRowHeight(cnt, b.data_size()); 
			b.to_tab(tab,cnt); cnt++; }
		for (pInstance b : link) { 
			tab.setRowHeight(cnt, b.data_size()); 
			b.to_tab(tab,cnt); cnt++; }
		
	}
	public void move_inst_group(ArrayList<pInstance> list, Vector2 pos) {
		if (list.size() == 0) return;
		Vector2 center = new Vector2();
		for (pInstance b : list) if (b.stand != null) { center.add(b.getDataVec("pos")); }
		center.x /= list.size(); center.y /= list.size();
		center.scl(-1f);
		Vector2 p = new Vector2(pos);
		pSheet sheet = list.get(0).sheet;
		p.sub(sheet.sheet_ref.getLocalPos());
		center.add(p);
		if (center.x > 0)
			center.x = center.x - center.x%pNode.BRIC_GRID_SIZE;// + pNode.BRIC_GRID_SIZE;
		else center.x = center.x + center.x%pNode.BRIC_GRID_SIZE;// - pNode.BRIC_GRID_SIZE;
		if (center.y > 0)
			center.y = center.y - center.y%pNode.BRIC_GRID_SIZE;// + pNode.BRIC_GRID_SIZE;
		else center.y = center.y + center.y%pNode.BRIC_GRID_SIZE;// - pNode.BRIC_GRID_SIZE;
		for (pInstance b : list) if (b.stand != null) { b.addDataVec("pos", center); }
	}
	
	public void move_inst_group_to_cam(ArrayList<pInstance> list) {
		if (list.size() == 0) return;
		sVec cam_pos = view.object("val_cam_pos", sVec.class);
		move_inst_group(list, new Vector2(cam_pos.get()));//.scl(-1f));
	}
	
//	public void build_inst_database(sTab db) {
//		db.setWidth(pNode.node_models.size());
//		int i = 0;
//		for (pStandard model : pNode.node_models.all()) {
//			model.def_to_tab(db,i); i++; }
//	}

	public void save_contents() {
		
		for (pSheet s : sheets.all()) s.save_sheet_contents();
		
//		Utl.logn("save"); log_debug();
		
	}
	
//	public void log_debug() {
//		for (pSheet s : sheets.all()) if (s.bloc.ref.equals("function")) {
//			Utl.logn("SHEET "+s.bloc.ref);
//			for (pColl c : s.collec_pool.all()) c.log_debug();
//		}
//	}
	
	public void load_contents() {
		for (pSheet s : sheets.all()) s.link_pool.freeAll();
		for (pSheet s : sheets.all()) s.inst_pool.freeAll();
		for (pSheet s : sheets.all()) s.tile_pool.freeAll();
		for (pSheet s : sheets.all()) s.ent_pool.freeAll();
		for (pSheet s : sheets.all()) s.collec_pool.freeAll();
		
//		for (pSheet s : sheets.all()) s.get_svalues();
		
//		Utl.logn("free"); log_debug();
		
//		for (pSheet s : sheets.all()) s.collec_pool.load();
		for (pSheet s : sheets.all()) s.collec_pool.load_1();

//		Utl.logn("load 1"); log_debug();
		
		for (pSheet s : sheets.all()) s.ent_pool.load_1();
		for (pSheet s : sheets.all()) s.inst_pool.load_1();
		for (pSheet s : sheets.all()) s.link_pool.load_1();
		for (pSheet s : sheets.all()) s.collec_pool.load_2();

//		Utl.logn("load 2"); log_debug();
		
		for (pSheet s : sheets.all()) s.ent_pool.load_2();
		for (pSheet s : sheets.all()) s.inst_pool.load_2();
		for (pSheet s : sheets.all()) s.link_pool.load_2();
		for (pSheet s : sheets.all()) s.collec_pool.load_3();
		for (pSheet s : sheets.all()) s.ent_pool.load_3();
		for (pSheet s : sheets.all()) s.inst_pool.load_3();
		for (pSheet s : sheets.all()) s.link_pool.load_3();

		for (pSheet s : sheets.all()) 
			for (pInstance b : Utl.duplic(s.inst_pool.all())) b.do_point_after_load();
		for (pSheet s : sheets.all()) 
			for (pInstance b : Utl.duplic(s.ent_pool.all())) b.do_point_after_load();
		for (pSheet s : sheets.all()) 
			for (pInstance b : Utl.duplic(s.link_pool.all())) b.do_point_after_load();

		app.addDelayEvent(1, new nRun() { public void run() {
			for (pInstance b : Utl.duplic(cos)) b.run("run_event_link_from_node");  
		}});

//		Utl.logn("end"); log_debug();
		
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
	public void select_all(pSheet s) {
		if (s != null) {
			unselect_all();
			App.ap.addDelayEvent(6, new nRun(s) { public void run() {
				pSheet s = (pSheet)builder;
				s.select_sheet();
				for (pInstance b : Utl.duplic(s.nodes)) b.run("select");
			}});
			
		}
	}
	public void select_all() {
		if (select_sheet != null)
			for (pInstance b : Utl.duplic(select_sheet.nodes)) b.run("select");
	}
	public void unselect_all() {
		for (pInstance b : Utl.duplic(select_nodes)) b.run("unselect");
	}
	public void clear_select() {
		for (pInstance b : Utl.duplic(select_nodes)) b.clear();
	}

	public boolean mouse_is_hover_back() {
		return view.get("background").mouseOver; }
	public boolean mouse_is_hover_view() {
		return view.get("background").mouseOverZone; }
	public Vector2 mouse_in_view() {
		Vector2 m = new Vector2(app.input.mouse);
		m.set(patch_ref.revertWarp(m)); return m; }

	public nMap<pInstance> common_functions = new nMap<pInstance>();
	public nMap<pInstance> common_branchs = new nMap<pInstance>();
	
	public int tile_id_counter = 0;
	
	
	

	
	
	
	
	
	
	public pPatch(PlaneApplet a) { 
		app = a; init();
	}
	

	public PlaneApplet app = null;

	public sValueBloc bloc = null;

	public boolean use_net_frame = false;
	
	nWidgetGroup view;
	nWidget view_backref, patch_ref, select_grab, patch_pop_close;
	
	String pop_close_user = "";
	public boolean pop_close_user(String n) { return pop_close_user.equals(n) || (pop_close_user.length() == 0); }    
	
	nInterface tool_interf = null;

	sValueBloc patch_content_bloc;
	
	sBoo val_wallp;
	
	pInstance linking_node_co = null;

	nRun run_frame_end;
	
	public pPatch patch;
	
//	public nWidgetGroup patch_dropmenu = null;
	public nWidgetGroup patch_pop = null;
	
	nRun run_del,run_sel,run_cut,run_copy,run_paste;
	
	public void clear() {

		unselect_all();
		
		app.removeRunFrame(run_frame_end);
		
		pTileHead.func_counter = 0;
		
		bloc.clear();
		
	}
	
	static ArrayList<nRun> eventInit = new ArrayList<nRun>();

	public static void addEventInit(nRun n) { eventInit.add(n); }
	
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
	
	public void init() {
		
		bloc = app.data.root_bloc.obtainBloc("patch_bloc");
		bloc.addObject("tick", this);

		bloc.addMetode("clearing", new nRun() { public void run() {
			clear(); }}); 


		use_net_frame = app.config.start_as_client;
		
		patch = this;
		
		bloc.addObject("patch", this);
		
		app.addEventSave(new nRun() { public void run() {
			save_contents(); 
			app.space.save_contents(); 
		}});

		app.addEventEmpty(new nRun() { public void run() {
			clear_all_inst(); 
			app.space.clear_all_obj();
			pTileHead.func_counter = 0;
		}});

		app.addEventLoad(new nRun() { public void run() {
			app.addDelayEvent(1, new nRun() { public void run() {
//				Utl.logn("patch "+bloc.ref+" load_contents");
				app.space.load_contents();
				load_contents();
//				app.addDelayEvent(1, new nRun() { public void run() {
////					app.space.load_contents();
//					app.space.start_space();
//				}});
			}});
		}});

		run_frame_end = new nRun() { public void run(Object o) {
			float b = (float)o; frame_end(b); }};
		
		app.addRunFrame(run_frame_end);

//		val_inst_database = bloc.obtainTab("val_inst_database");
//		bloc.data.databases.put("patch_inst", val_inst_database);
//		build_inst_database(val_inst_database);
		
//		patch_dropmenu = app.gui.addWidgetGroup("dropmenu");
		
		view = app.gui.addWidgetGroup("viewspace");
		bloc.addObject("viewspaceGroup", view);
//		view.addWidgetGroup("patch_dropmenu", patch_dropmenu);
		view.metode("link_to_bloc", bloc);
		
		view.metode("set_title", app.gdx.window_title+" patch");
		view.get("close").setPassif().setDraw(false).setSize(0,0);
		
		if (!app.config.STARTUP_LOAD) {
			if (app.config.RELEASE) {
				if (GdxApp.START_FULLSCREEN) 
					set_viewspace(20f,1030f,660f,950f,app.config.DEF_PATCH_ZOOM);
				else set_viewspace(20f,915f,1260f,430f,app.config.DEF_PATCH_ZOOM);
			} else {
				if (app.config.start_solo) {
					if (GdxApp.START_FULLSCREEN) 
						set_viewspace(20f,1030f,660f,950f,app.config.DEF_PATCH_ZOOM);
					else set_viewspace(
							app.config.DEF_PATCH_WIN_POS.x,
							app.config.DEF_PATCH_WIN_POS.y,
							app.config.DEF_PATCH_WIN_SZ.x,
							app.config.DEF_PATCH_WIN_SZ.y,
							app.config.DEF_PATCH_ZOOM);
				} else set_viewspace(370f,815f,510f,330f,app.config.DEF_PATCH_ZOOM);
			}
			sVec val_cam_pos = view.object("val_cam_pos", sVec.class);
			val_cam_pos.set(app.config.DEF_PATCH_POS);
			if (app.config.PATCH_START_COLLAPSED) view.metode("run_collapse");
		}
		sBoo val_grid = view.object("val_grid", sBoo.class);
		val_grid.set(true);
		
		
		app.gui.add_info_text("patch zoom: ", view.object("val_cam_scale", sFlt.class));
		
		view_backref = view.get("backref");
		patch_pop = app.gui.addWidgetGroup("patch_pop");
		patch_pop.metode("set_patch", this);
		view.addWidgetGroup("patch_pop", patch_pop);
		val_wallp = view.object("val_wallp", sBoo.class);
		
//		patch_pop.get("fx").setVFX();
		
//		build_tools();

		run_del = new nRun() { public void run() {
			clear_select(); }};
//		add_toolbar_trigg("Del", run_del);
		app.gui.add_shortcut_target("Patch - Del", 'O', run_del);
		run_sel = new nRun() { public void run() {
			if (select_nodes.size() == nodes.size()) unselect_all();
			else select_all(); 
		}};
//		add_toolbar_trigg("Sel", run_sel);
		app.gui.add_shortcut_target("Patch - Select", 'I', run_sel);
		run_cut = new nRun() { public void run() {
			ArrayList<pInstance> sel = new ArrayList<pInstance>();
			for (pInstance n : select_nodes) sel.add(n);
			save_insts_to_tab(sel, val_tab_copy_stack); 
			clear_select(); }};
//		add_toolbar_trigg("Ct", run_cut);
		app.gui.add_shortcut_target("Patch - Cut", 'X', run_cut);
		run_copy = new nRun() { public void run() {
			ArrayList<pInstance> sel = new ArrayList<pInstance>();
			for (pInstance n : select_nodes) sel.add(n);
			save_insts_to_tab(sel, val_tab_copy_stack); 
		}};
//		add_toolbar_trigg("Cp", run_copy);
		app.gui.add_shortcut_target("Patch - Copy", 'C', run_copy);
		run_paste = new nRun() { public void run() {
			if (select_sheet != null && mouse_is_hover_view()) {
				do_paste(select_sheet, mouse_in_view()); }
		}};
//		add_toolbar_trigg("Pst", run_paste);
		app.gui.add_shortcut_target("Patch - Paste", 'V', run_paste);
		
		val_inst_nb = app.data.system_bloc.obtainInt("val_inst_nb");
		val_inst_free = app.data.system_bloc.obtainInt("val_inst_free");
		val_tile_nb = app.data.system_bloc.obtainInt("val_tile_nb");
		val_tile_free = app.data.system_bloc.obtainInt("val_tile_free");
		val_ent_nb = app.data.system_bloc.obtainInt("val_ent_nb");
		val_ent_free = app.data.system_bloc.obtainInt("val_ent_free");
		val_collec_nb = app.data.system_bloc.obtainInt("val_collec_nb");
		val_collec_free = app.data.system_bloc.obtainInt("val_collec_free");
		val_link_nb = app.data.system_bloc.obtainInt("val_link_nb");
		val_link_free = app.data.system_bloc.obtainInt("val_link_free");
		val_virt_nb = app.data.system_bloc.obtainInt("val_virt_nb");
		val_virt_free = app.data.system_bloc.obtainInt("val_virt_free");
		
		val_tab_copy_stack = app.data.system_bloc.obtainTab("val_tab_patch_copy_stack");
		
		
	}
	public void system_load() {
//		load_contents();
		if (!app.config.RELEASE) tool_setup(false);
		
		app.term.addExecutor(new SystemExecutor("patch", bloc) {
			
		});
		
		app.addDelayEvent(1, new nRun() { public void run() {	
			if (!app.config.STARTUP_LOAD && app.config.PATCH_START_WALLPAPER) 
				val_wallp.set(true);	 }});
		
		nRun.runEvents(eventInit, bloc);
	}

	public void tool_setup(boolean open) {
		
		app.addDelayEvent(1, new nRun(this) { public void run() {
			nWidgetGroup sec = app.gui.toolbox
					.addSection("patch", open);
			nInterface interf = app.gui.addInterface();
			interf.pop(sec);
			interf.setContext(bloc);
			tool_init(interf);
		}});
		
	}
	
	public void tool_init(nInterface interf) {
		interf.setContext(app.data.system_bloc);
		
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
//		
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
				st_x += s.x + 8f*nGUI.book.RS;
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

	
	
	
	
	

//	nWidgetGroup build_list = null;
//	nInterface bar_interf = null;
	
//	public void build_tools() {
//
//		nWidgetGroup tools = app.gui.addWidgetGroup("viewspace_tool");
//		bloc.addObject("patch_tool", tools);
//		tools.metode("set_py", 10f);
//		tools.metode("set_title", "Common Bric Builder");
//		tools.metode("link_tool_to_bloc", bloc, "build");
//		tools.metode("add_to_viewspace_front", view);
//		if (app.config.PATCH_TOOL_AUTOCOLLAPSE) tools.metode("set_auto_hide");
//		tool_interf = (nInterface)tools.metodeGet("get_interf");
//
//		tool_interf.set_param("entry_height","0.7");
//
//		tool_interf.add_row();
//		tool_interf.add_row();
//		build_list = tool_interf.add_scrollist(10,10);
//		
//		update_patch_tool();
//		
//		nWidgetGroup bar = app.gui.addWidgetGroup("viewspace_tool");
//		bloc.addObject("bar_viewspace_tool", bar);
//		bar.metode("set_px", 350f);
//		bar.metode("set_pop_up");
//		bar.metode("set_title", "ToolBar");
//		bar.metode("link_tool_to_bloc", bloc, "bar");
//		bar.metode("add_to_viewspace_front", view);
//		bar_interf = (nInterface)bar.metodeGet("get_interf");
//		
//		bar_interf.set_param("entry_height","1.5");
//		
//		bar_interf.add_row();
//		
//	}
//	
//
//	public nWidget add_toolbar_trigg(String t, nRun r) {
//		nWidget w1 = bar_interf.add_row_trigg(2, t);
//		w1.setFont(20);
//		w1.addEventTrigger(r);
//		return w1;
//	}
//	public nWidget add_toolbar_switch(String t, nRun r) {
//		nWidget w1 = bar_interf.add_row_switch(2, t);
//		w1.setFont(20);
//		w1.addEventSwitch(r);
//		return w1;
//	}
//	
//	public void update_patch_tool() {
//		
//		tool_interf.change_current_list(build_list);
//		tool_interf.set_param("entry_height","0.7");
//
//		int i = 0;
//		nWidget ent = null;
////		for (String mr : pMacro.macro_runs.allKey()) {
////			if (i%2 == 0) {
////				nWidget w = tool_interf.add_list_entry("");
////				w.setSY(app.gui.book.RS/5f);
////				ent = tool_interf.add_list_entry("");
////				ent.setBoundChild(true).setBoundOutspace(0);
////			} else {
////				nWidget w = tool_interf.get_row_entry_widget(1);
////				w.setParent(ent);
////				w.setSX(app.gui.book.RS/2f);
////			}
////			nWidget w = tool_interf.get_row_button_widget(4);
////			w.setText(mr);
////			w.setParent(ent);
////			w.setTrigger();
////			w.addEventTrigger(new nRun(mr, patch) { public void run() {
////				pMacro.runMacro(arg(0, String.class), arg(1, pPatch.class)); 
////			}});
////			i++;
////		}
////		if (i%2 != 0) {
////			nWidget w = tool_interf.get_row_entry_widget(5);
////			w.setParent(ent); 
////		}
////		tool_interf.add_list_entry("");
//		
//
//		ArrayList<String> grouplist = new ArrayList<String>();
//		
//		for (Map.Entry<String, pStandard> me : pNode.node_models.entrySet()) {
//			if (!Utl.contains(grouplist, pNode.node_group.get(me.getKey())))
//				grouplist.add(pNode.node_group.get(me.getKey()));
//		}
//		for (String sg : grouplist) {
//			i = 0;
//			ent = null;
//			for (String mr : pNode.buildable_node_models.allKey()) 
//					if (sg.equals(pNode.node_group.get(mr))) {
//				if (i%2 == 0) {
//					nWidget w = tool_interf.add_list_entry("");
//					w.setSY(app.gui.book.RS/5f);
//					ent = tool_interf.add_list_entry("");
//					ent.setBoundChild(true).setBoundOutspace(0);
//				} else {
//					nWidget w = tool_interf.get_row_entry_widget(1);
//					w.setParent(ent);
//					w.setSX(app.gui.book.RS/2f);
//				}
//				nWidget w = tool_interf.get_row_button_widget(4);
//				w.setText(mr);
//				w.setParent(ent);
//				w.setTrigger();
//				w.addEventTrigger(new nRun(mr) { public void run() {
//					if (select_sheet != null) {
//						pInstance n = select_sheet.newNode((String)builder);  
//						n.run("move_to_cam"); n.run("find_place"); }
//				}});
//				i++;
//			}
//			if (i%2 != 0) {
//				nWidget w = tool_interf.get_row_entry_widget(5);
//				w.setParent(ent); 
//			}
//			tool_interf.add_list_entry("");
//		}
//		
//	}
	public void new_node_dropmenu(pSheet sheet, Vector2 p) {
		ArrayList<String> grouplist = new ArrayList<String>();
		grouplist.add("base");
		for (Map.Entry<String, pStandard> me : pNode.node_models.entrySet()) {
			if (!Utl.contains(grouplist, pNode.node_group.get(me.getKey())))
				grouplist.add(pNode.node_group.get(me.getKey()));
		}
		nGUI.clear_dropmenu();
		for (String sg : grouplist) {
			nGUI.add_dropmenu_entry(sg, new nRun() { public void run() {
				Vector2 pos = new Vector2(p);
				App.ap.addDelayEvent(8, new nRun() { public void run() {
					new_node_dropmenu_group(sheet, sg, pos); }});
			}}); 
		}
		nGUI.open_dropmenu();
	}
	public void new_node_dropmenu_group(pSheet sheet, String sg, Vector2 p) {
		nGUI.clear_dropmenu();
		for (String mr : pNode.buildable_node_models.allKey()) 
			if (sg.equals(pNode.node_group.get(mr))) {
				nGUI.add_dropmenu_entry(mr, new nRun() { public void run() {
					Vector2 pos = new Vector2(p);
					App.ap.addDelayEvent(8, new nRun() { public void run() {
						new_node(sheet,mr,pos); }});
				}}); 
			}
		nGUI.open_dropmenu();
	}
	public void new_node(pSheet sheet, String model, Vector2 pos) {
		if (sheet != null) {
			sheet.select_sheet();
			pInstance n = sheet.newNode(model);
			ArrayList<pInstance> list = new ArrayList<pInstance>();
			list.add(n);
			move_inst_group(list, pos);
		}
	}
	public void do_paste(pSheet sheet, Vector2 pos) {
		if (sheet == null) return;
		sheet.select_sheet();
		ArrayList<pInstance> paste = 
				sheet.build_insts_from_tab(val_tab_copy_stack); 
		unselect_all();
		for (pInstance b : paste) { b.run("select"); }
		if (pos == null) move_inst_group_to_cam(paste);
		else { move_inst_group(paste, pos); }
	}
	
	
	public static void build_book() {
		nModelBook book = nGUI.book;
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
		.set_color_outline(Utl.color(255,0,0,255))
		.set_color_background(Utl.color(180,0,0,255))
//		.setScaleLimit(pNode.DEF_SCALE_MIN, pNode.DEF_SCALE_MAX)
		;

		
		
		
		book.newModel("PP_ref")
		.copyFrom(book.getModel("ref"))
		.setPassif()
		.set_color_background(Utl.color(0,0))
		.setDraw(false)
		.setRectOrigin(nAlign.LEFT,nAlign.BOTTOM) // TOP CENTER  BOTTOM
		.setBoundOutspace(0)
		.setStackSpacing(0)
		;

		book.newModel("PP_selectzone")
		.set_color_background(Utl.color(0,0))
		.setPassif()
		.setRectOrigin(nAlign.LEFT,nAlign.BOTTOM) // TOP   BOTTOM
		.set_color_outline(Utl.color(200,200,0,255))
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
		.set_color_pressed(Utl.color(255,80))
		.set_color_hovered(Utl.color(255,180))
		.set_color_standby(Utl.color(255,120))
		.set_color_outline(Utl.color(0,240,230,255))
		.setShape(nModel.Shape.DIAMOND)
		;

		book.newModel("PP_fx")
//		.setRect(0,0,RS*15f,RS*10f)
		.set_color_background(Utl.color(0, 0, 0, 0))
//		.setBoundParent(true)
//		.setBoundChild(true)
//		.setBoundOutspace(0f)
//		.setStackSpacing(0f)
		.setPassif()
		.setDraw(false)
		;
		
		book.newModelGroup("patch_pop", new nModelGroup() { 
			public nWidgetGroup build(nGUI gui) {
				nWidgetGroup g = gui.addWidgetGroup();

				nWidget fx = g.addWidget("fx", "PP_fx");
				nWidget ref = g.addWidget("ref", "P_ref")
						.setParent(fx);
				
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
					fx.setParent(patch.view_backref);
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
									App.ap.input.mouseLeft.trigClick) {
								Vector2 m = new Vector2(App.ap.input.mouse);
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
							Vector2 m = new Vector2(App.ap.input.mouse);
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

						if (App.ap.input.mouseLeft.trigUClick) {
							g.setObject("selzone_clic", false);
							selectzone.hide();
						}
						if (g.object("selzone_clic", Boolean.class)) {
							for (pInstance br : patch.nodes) 
									if (patch.select_sheet == null || 
										patch.select_sheet == br.sheet){
								nWidget w = br.object("group", nWidgetGroup.class).get("selline");
								Rectangle wr = new Rectangle(w.getRectRelativeToParent(ref));
								if (Utl.intersect(wr, selzone)) {
									br.run("select");
								} else {
									br.run("unselect");
								}
							}
						}
						
						if (viewsp_bg.mouseOver && !g.object("selzone_clic", Boolean.class) && 
								App.ap.input.mouseRight.trigClick) {
							Vector2 m = new Vector2(App.ap.input.mouse);
							m.set(ref.warptransform.revert(m));
							pSheet clic_sheet = null;
							for (pSheet s : patch.sheets.all()) {
								nWidget sbb = s.sheet_bound_bound;
								Rectangle wr = new Rectangle(
										sbb.getRectRelativeToParent(ref));
								if (wr.contains(m)) { clic_sheet = s; break; } }
							if (clic_sheet != null) {
								if (patch.select_sheet != null && 
										patch.select_sheet != clic_sheet) {
									patch.select_sheet.unselect_sheet();
									clic_sheet.select_sheet();
								}
								Vector2 mp = new Vector2(patch.mouse_in_view());
								nGUI.clear_dropmenu();
								nGUI.add_dropmenu_entry("new", new nRun(clic_sheet) { public void run() {
									pSheet s = (pSheet)builder;
									App.ap.addDelayEvent(8, new nRun(s) { public void run() {
										pSheet s = (pSheet)builder;
										patch.new_node_dropmenu(s,mp); }}); }}); 
								if (patch.select_nodes.size() != clic_sheet.nodes.size()) 
									nGUI.add_dropmenu_entry("select all", new nRun(clic_sheet) { public void run() {
										pSheet s = (pSheet)builder;
										patch.select_all(s); }});
								if (patch.select_nodes.size() > 0) {
									nGUI.add_dropmenu_entry("unselect all", new nRun() { public void run() {
										patch.unselect_all(); 
										if (patch.select_sheet != null) 
											patch.select_sheet.unselect_sheet(); }}); 
									nGUI.add_dropmenu_entry("copy", new nRun() { public void run() {
										patch.run_copy.run(); }}); 
									nGUI.add_dropmenu_entry("cut", new nRun() { public void run() {
										patch.run_cut.run(); }}); 
									nGUI.add_dropmenu_entry("delete", new nRun() { public void run() {
										patch.run_del.run(); }}); 
								} 
								if (patch.val_tab_copy_stack.width() > 0)
									nGUI.add_dropmenu_entry("paste", new nRun(clic_sheet) { public void run() {
										pSheet s = (pSheet)builder;
										App.ap.addDelayEvent(8, new nRun(s) { public void run() {
											pSheet s = (pSheet)builder;
											patch.do_paste(s,mp); }}); 
										}}); 
								nGUI.open_dropmenu();
							}
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
	
	
	
	
//	public static void build_database_builder(sData data) {
//		
//		nModelBook book = nGUI.book;
//		float RS = book.RS;
//		
//		sBloc_Builder database_editor_builder = new sBloc_Builder(data, "database_editor")
//			.setInitRun(new nRun() { public void run(Object o) {
//				sValueBloc b = (sValueBloc)o;
//				nWidgetGroup win = PlaneApplet.app.gui.addWidgetGroup("database_editor");
//				b.addObject("database_view_win", win);
//				win.metode("link_window_to_bloc", b);
//				win.addEventClear(new nRun() { public void run() {
//					b.clear(); }});
//				b.addEventDelete(new nRun() { public void run() {
//					win.clear(); }});
//				
//				win.metode("run_tofront");
//				
//				if (b.is_new_bloc) {
//					sVec val_pos = win.object("val_pos", sVec.class);
//					val_pos.set(20f,445f);
//				}
//				
//			}})
//			.setClearRun(new nRun() { public void run(Object o) {
//				sValueBloc b = (sValueBloc)o; 
//			}});
//
//		data.addRootBlocBuilder(database_editor_builder);
//	}
//	
//	public static void build_database_book() {
//		
//		nModelBook book = nGUI.book;
//		float RS = book.RS;
//		
//		book.newModelGroup("database_editor", new nModelGroup() { 
//			public nWidgetGroup build(nGUI gui) {
//				nWidgetGroup g = gui.addWidgetGroup("complex_window");
//
//				nInterface interf = gui.addInterface()
//						.pop(g);
//				
//				g.metode("set_title", "database editor");
//				
//				interf.add_row();
//				interf.add_row_label(10," Select Database : ");
//				interf.add_row();
//				nWidgetGroup db_list = interf.add_picklist(8,4);
//				
//				interf.add_row();
//				interf.add_row_label(4,"");
//				nWidget load_w = interf.add_row_trigg(4,"EDIT");
//				load_w.addEventTrigger(new nRun() { public void run() {
//					g.metode("edit_tab"); }});
//
//				interf.add_col_separator();
//				interf.add_col_separator();
//
//				interf.add_row();
//				nWidget tab_lb = interf.add_row_label(8,"sTab : ");
//				interf.add_row();
//				nWidget tab_h_lb = interf.add_row_label(8,"width :");
//				interf.add_row();
//				nWidget row_lb = interf.add_row_label(8,"     viewing row : ");
//				
//				interf.add_row();
//				interf.add_row_trigg(2,"<<", new nRun() { public void run() {
//					if (!g.hasObject("row")) return;
//					int row = g.object("row", Integer.class);
//					row -= 1; g.metode("view_tab_row", row);
//				}});
//				interf.add_row_label(4,"Row");
//				interf.add_row_trigg(2,">>", new nRun() { public void run() {
//					if (!g.hasObject("row")) return;
//					int row = g.object("row", Integer.class);
//					row += 1; g.metode("view_tab_row", row);
//				}});
//
//				interf.add_row();
//				interf.add_row_trigg(3,"Add Row", new nRun() { public void run() {
//					sTab tab = g.object("tab", sTab.class);
//					if (tab == null) return;
//					int row = tab.width() + 1;
//					tab.setWidth(row);
//					tab_h_lb.setText("width :"+tab.width());
//					g.metode("view_tab_row", row-1);
//				}});
//				interf.add_row_label(2,"");
//				interf.add_row_trigg(3,"Del Row", new nRun() { public void run() {
//					
//				}});
//
//				interf.add_col_separator();
//
//
//				interf.add_col();
//
//				interf.add_row();
//				interf.add_row_label(14," Database Editor : ");
//				
//				interf.add_row();
//				nWidget cell_lb = interf.add_row_label(8,"0 Cell in this row");
//				interf.add_row_label(1,"");
//				interf.add_row_trigg(4,"Add Cell", new nRun() { public void run() {
//					sTab tab = g.object("tab", sTab.class);
//					if (tab == null) return;
//					if (!g.hasObject("row")) return;
//					int row = g.object("row", Integer.class);
//					int h = tab.height(row);
//					h += 1; tab.setRowHeight(row, h); 
//					g.metode("view_tab_row", row);
//				}});
//				interf.add_row_label(1,"");
////				interf.add_row_trigg(2,"Del", new nRunnable() { public void run() {
////					sTab tab = g.object("tab", sTab.class);
////					if (tab == null) return;
////					if (!g.hasObject("row")) return;
////					int row = g.object("row", Integer.class);
////					int h = tab.height(row);
////					h -= 1; if (h < 0) h = 0;
////					tab.setColHeight(row, h); 
////					g.metode("view_tab_row", row);
////				}});
//
//				interf.add_col_separator();
//
//				interf.add_row();
//				nWidgetGroup edit_list = interf.add_scrollist(14,12);
//
//				ArrayList<nWidget> ent_arr = new ArrayList<nWidget>();
//				ArrayList<nWidgetGroup> dm_arr = new ArrayList<nWidgetGroup>();
//				ArrayList<nRun> run_arr = new ArrayList<nRun>();
//				
//				g.addMetode("clear_edit_list", new nRun() { public void run() {
//					
//					interf.change_current_list(edit_list);
//					
//					row_lb.setText("");
//					cell_lb.setText("");
//
//					for (nWidget w : ent_arr) w.clear();
//					
//					if (g.hasGroup("dropmenu_type")) {
//						nWidgetGroup w = g.getGroup("dropmenu_type");
//						dm_arr.remove(w);
//						g.removeGroup(w); w.clear();
//					}
//					
//					for (nWidgetGroup w : dm_arr) w.clear();
//					
//					sTab tab = g.object("tab", sTab.class);
//					if (tab == null) return;
//					for (nRun w : run_arr) tab.removeEventChangeLastFrame(w); 
//					
//					ent_arr.clear(); dm_arr.clear(); run_arr.clear();
//				}});
//				
//				g.addEventClear(new nRun() { public void run() {
//					g.metode("clear_edit_list"); }});
//				
//				g.addMetode("view_tab_row", new nRun() { public void run(Object o) {
//					int row = (Integer)o;
//					sTab tab = g.object("tab", sTab.class);
//					
//					g.metode("clear_edit_list"); 
//					
//					if (tab == null) return;
//					if (row >= tab.width() || row < 0) { 
//						if (tab.width() > 0) g.metode("view_tab_row", 0); 
//						return; 
//					} 
//
//					g.setObject("row", row); 
//					row_lb.setText("viewing row : "+row); 
//
//					cell_lb.setText(tab.height(row)+" Cell in this row"); 
//					
//					for (int i = 0 ; i < tab.height(row) ; i++) {
//						
//						nWidget ent = interf.add_list_entry("");
//						ent.setStackAxis(nAlign.HORIZONTAL).setStackSpacing(2f);
//						
//						nWidget lab = interf.get_row_entry_widget(2);
//						nWidget fld = interf.get_row_entry_widget(8);
//						nWidget typ = interf.get_row_entry_widget(1);
//						nWidget del = interf.get_row_entry_widget(1);
//						
//						lab.setParent(ent).setText(""+i).setStacked(true);
//						fld.setParent(ent).setStacked(true);
//						fld.setField(true).copyLookFrom(interf.gui.book.getModel("text_field"));
//						typ.setParent(ent).setText("").setTrigger().setStacked(true);
//						del.setParent(ent).setText("x").setTrigger().setStacked(true);
//						
//						if (tab.getObj(row,i) == null) typ.setText("null");
//						else if (!Utl.type_is_used(tab.getObj(row,i).getClass())) 
//							typ.setText("??");
//						else typ.setText(Utl.type_class_type.get(
//								tab.getObj(row,i).getClass()));
//						
//						ent_arr.add(lab); ent_arr.add(fld); 
//						ent_arr.add(typ); ent_arr.add(del); 
//						
//						nWidgetGroup dropmenu_type = null;
//						if (g.hasGroup("dropmenu_type"))
//							dropmenu_type = g.getGroup("dropmenu_type");
//						else {
//							dropmenu_type = gui.addWidgetGroup("dropmenu");
//							g.addWidgetGroup("dropmenu_type", dropmenu_type);
//							dm_arr.add(dropmenu_type);
//						}
//								
//						
//						typ.addEventTrigger(new nRun(i) { public void run() {
//							nWidgetGroup dm_type = null;
//							if (g.hasGroup("dropmenu_type"))
//								dm_type = g.getGroup("dropmenu_type");
//							else {
//								dm_type = gui.addWidgetGroup("dropmenu");
//								g.addWidgetGroup("dropmenu_type", dm_type);
//								dm_arr.add(dm_type);
//							}
//							dm_type.metode("clear_entrys");
//							for (String sc : Utl.type_short_names) {
//								nWidget w1 = (nWidget)dm_type
//										.metodeGet("add_entry_custom", sc, 
//												RS*6f, RS*2f/3f);
//								w1.addEventTrigger(new nRun() { public void run() {
//									typ.setText(sc); }}); 
//							}
//							dm_type.metode("open", typ); 
//						}});
//						
//						fld.addEventFieldChange(new nRun(i) { public void run() {
//							tab.set(row, (int)builder, 
//									Utl.from_string(fld.getText(), 
//									Utl.type_type_class.get(typ.getText()))); 
//						}});
//
//						del.addEventTrigger(new nRun(i) { public void run() {
//							
//						}});
//						
//						nRun tab_up = new nRun(i) { public void run() {
//							fld.setText(Utl.to_string(tab.get(row,(int)builder,
//									Utl.type_type_class.get(typ.getText())))); }};
//						tab.addEventChangeLastFrame(tab_up);
//						tab_up.run();
//						run_arr.add(tab_up);
//					}
//				}});
//				
//				g.addMetode("list_db", new nRun() { public void run() {
//					interf.change_current_list(db_list);
//					for(Map.Entry<String,sTab> me : App.ap.data.databases.entrySet()) {
//						String r = me.getKey(); sTab tab = me.getValue();
//						interf.add_list_entry(r);
//					}
//				}});
//				g.metode("list_db");
//				
//				g.addMetode("edit_tab", new nRun() { public void run() {
//					String db_name = (String)db_list.metodeGet("get_pick");
//					if (db_name == null) return;
//					sTab tab = App.ap.data.databases.get(db_name);
//					if (tab == null) return;
//					g.setObject("tab", tab);
//					tab_lb.setText("sTab : "+tab.ref);
//					tab_h_lb.setText("width :"+tab.width());
//					g.metode("view_tab_row", 0);
//				}});
//
//				return g;
//			} 
//		} );
//		
//	}
}
