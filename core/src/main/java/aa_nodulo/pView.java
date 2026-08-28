package aa_nodulo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.noodle.nodulo.GdxApp;

import aa_term.CommandExecutor;
import aa_term.ConsoleDoc;
import aa_term.LogLevel;
import app.App;
import data.*;
import gui.*;
import util.Utl;
import util.nMap;
import util.nPool;
import util.nRun;
import patch.pInstance;
import patch.pNode;
import patch.pNode.CT;

public class pView {

//		app.setPref("default", "DEF_VIEW_WIN_POS", new Vector2(370f,445f));
//		app.setPref("default", "DEF_VIEW_WIN_SZ", new Vector2(910f,350f));
//		app.setPref("focus_space", "DEF_VIEW_WIN_POS", new Vector2(370f,445f));
//		app.setPref("focus_space", "DEF_VIEW_WIN_SZ", new Vector2(910f,350f));
//		app.setPref("release", "DEF_VIEW_WIN_POS", new Vector2(20f,445f));
//		app.setPref("release", "DEF_VIEW_WIN_SZ", new Vector2(1260f,350f));
//		app.setPref("release_FS", "DEF_VIEW_WIN_POS", new Vector2(690f,1030f));
//		app.setPref("release_FS", "DEF_VIEW_WIN_SZ", new Vector2(1210f,950f));

	public static void build_nodes() {		
		
		
		
		pNode.newNodeModel("pview")
		.process()
		.useInit().commande(new nRun() {public void run() {
			pView pview = PlaneApplet.app.view;
			if (!pview.in_patch) {
				sVec val_pos = pview.view.object("val_pos", sVec.class);
				Vector2 old_pos = new Vector2(val_pos.get());
				pview.view.setObject("old_pos", old_pos);
				val_pos.set(0,0);
				App.ap.addDelayEvent(2, new nRun(instance) { public void run() {
					pInstance inst = (pInstance)builder;
					pView pview = PlaneApplet.app.view;
					sVec val_pos = pview.view.object("val_pos", sVec.class);
					val_pos.set(0,0);
					nWidget vref = pview.view.get("ref");
					vref.unlink(val_pos);
					vref.unlink(pview.view.object("val_stack_index", sInt.class));
					vref.unlink(val_pos);
					vref.unlink(pview.view.object("val_stack_index", sInt.class));
					vref.setParent(
							inst.object("group", nWidgetGroup.class).get("back"));
					pview.in_patch = true;
					vref.setBoundParent(true);
					vref.setStacked(true);
					vref.setRectOrigin(nAlign.LEFT,nAlign.BOTTOM); // TOP   BOTTOM
					pview.view.get("headback").hide();
				}});
				instance.object("group", nWidgetGroup.class).get("back")
						.addEventClear(new nRun(instance) {public void run() {
					pInstance inst = (pInstance)builder;
					pView pview = PlaneApplet.app.view;
					nWidget vref = pview.view.get("ref");
					vref.clearParent();
					pview.in_patch = false;
					vref.setBoundParent(false);
					vref.setStacked(false);
					vref.setRectOrigin(nAlign.LEFT,nAlign.TOP); // TOP   BOTTOM
					pview.view.get("headback").show();
					sVec val_pos = pview.view.object("val_pos", sVec.class);
					Vector2 old_pos = pview.view.object("old_pos", Vector2.class);
					val_pos.set(old_pos);
					vref.setLink(val_pos);
//					vref.setStackIndexLink(pview.view.object("val_stack_index", sInt.class));
				}});
			}
		}})
		.useClear().commande(new nRun() {public void run() {
			pView pview = PlaneApplet.app.view;
			nWidget vref = pview.view.get("ref");
			vref.clearParent();
			pview.in_patch = false;
			vref.setBoundParent(false);
			vref.setStacked(false);
			vref.setRectOrigin(nAlign.LEFT,nAlign.TOP); // TOP   BOTTOM
			pview.view.get("headback").show();
			sVec val_pos = pview.view.object("val_pos", sVec.class);
			Vector2 old_pos = pview.view.object("old_pos", Vector2.class);
			val_pos.set(old_pos);
			vref.setLink(val_pos);
//			vref.setStackIndexLink(pview.view.object("val_stack_index", sInt.class));
		}}).useInit()
//		.run(pNode.getRun(pNode.CT.RUNP_ADD_LABEL), "ViewSpace", (int)20)
		.commande(pNode.getCom(CT.COM_ADD_ROW))
		;
		
		
		
		
		
		
//		pBric.newBricModel("cam").process()
//		.openSec()
//			.param("event_receive", new nRun() {public void run() {
//				Vector2 r = arg(0,Vector2.class); 
//				pView view = instance.patch.plane.getSystem(pView.class);
//				view.val_cam_pos_target.set(-r.x,-r.y);
//				view.got_cam_pos_target = true;
//			}})
//			.param("keys", new String[] {"var","vec"}, "filters", new String[] {"var","vec"}) 
//			.run(pBric.getRun(Code.RUN_CO_IN), "pos", "pos", (int)6)
//		.closeSec()
//		.commande(pBric.getCom(Code.ADD_ROW))
//		.openSec()
//			.param("event_receive", new nRun() {public void run() {
//				float r = arg(0,Float.class); 
//				pView view = instance.patch.plane.getSystem(pView.class);
//				view.val_cam_scale_target.set(r);
//				view.got_cam_scale_target = true;
//			}})
//			.param("keys", new String[] {"var","flt"}, "filters", new String[] {"var","flt"}) 
//			.run(pBric.getRun(Code.RUN_CO_IN), "ref", "scale", (int)6)
//		.closeSec()
//		.commande(pBric.getCom(Code.ADD_ROW))
//		.openSec()
//			.param("event_receive", new nRun() {public void run() {
//				float r = arg(0,Float.class); 
//				pView view = instance.patch.plane.getSystem(pView.class);
//				view.val_cam_rot_target.set(-r);
//				view.got_cam_rot_target = true;
//			}})
//			.param("keys", new String[] {"var","flt"}, "filters", new String[] {"var","flt"}) 
//			.run(pBric.getRun(Code.RUN_CO_IN), "rot", "rot", (int)6)
//		.closeSec()
//		.commande(pBric.getCom(Code.ADD_COL))
//		.openSec()
//		.param("run", new nRun() {public void run() {
//			pView view = instance.patch.plane.getSystem(pView.class);
//			view.val_center_ratio_target.set(new Vector2(
//					instance.getVar("cr_x", Float.class), 
//					instance.getVar("cr_y", Float.class)));
//			view.got_center_ratio_target = true;
//		}})
//		.param("def", 0.5f, "min", 0f, "max", 1f, "granulo", 0.025f)
//		.run(pBric.getRun(Code.RUN_VAR_FLT_LAB_FLD_SLD), "cr_x", "center_ratio_x", (int)8).closeSec()
//		.commande(pBric.getCom(Code.ADD_ROW))
//		.openSec()
//		.param("run", new nRun() {public void run() {
//			pView view = instance.patch.plane.getSystem(pView.class);
//			view.val_center_ratio_target.set(new Vector2(
//					instance.getVar("cr_x", Float.class), 
//					instance.getVar("cr_y", Float.class)));
//			view.got_center_ratio_target = true;
//		}})
//		.param("def", 0.5f, "min", 0f, "max", 1f, "granulo", 0.025f)
//		.run(pBric.getRun(Code.RUN_VAR_FLT_LAB_FLD_SLD), "cr_y", "center_ratio_y", (int)8).closeSec()
//		;
	}
	
	
	
	
	
	public pView(PlaneApplet a) { 
		app = a;
		tick_run = new nRun() { public void run(Object o) { tick((float)o); }}; 
		init(); }

	public boolean mouse_is_hover_view() {
		return view.get("background").mouseOverZone; }
	public Vector2 mouse_in_view() {
		Vector2 m = new Vector2(app.input.mouse);
		m.set(view_ref.revertWarp(m)); return m; }
	public Vector2 to_view(Vector2 v) {
		Vector2 m = new Vector2(v);
		m.set(view_ref.revertWarp(m)); return m; }
	public Vector2 to_screen(Vector2 v) {
		Vector2 m = new Vector2(v);
		m.set(view_ref.revertWarp(m)); return m; }
	
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

	public pView addDrawable(nDrawable r) { addDrawable(0,r); return this; }
	public pView addDrawable(int prio, nDrawable r) { 
		drawRun.add(r); drawPrio.put(r, prio); max_prio = Math.max(max_prio, prio); return this; }
	public pView removeDrawable(nDrawable r) { drawRun.remove(r); return this; }
	public pView addPreDrawable(nDrawable r) { addDrawable(0,r); return this; }
	public pView addPreDrawable(int prio, nDrawable r) { 
		preDrawRun.add(r); drawPrio.put(r, prio); max_prio = Math.max(max_prio, prio); return this; }
	public pView removePreDrawable(nDrawable r) { preDrawRun.remove(r); return this; }
	public pView addPostDrawable(nDrawable r) { addDrawable(0,r); return this; }
	public pView addPostDrawable(int prio, nDrawable r) { 
		postDrawRun.add(r); drawPrio.put(r, prio); max_prio = Math.max(max_prio, prio); return this; }
	public pView removePostDrawable(nDrawable r) { postDrawRun.remove(r); return this; }
	public pView clearDrawable() { drawRun.clear(); preDrawRun.clear(); postDrawRun.clear(); return this; }

	public PlaneApplet app = null;

	public sValueBloc bloc = null;

	public nWidgetGroup view;
	public nWidget view_ref;
	nInterface bar_interf = null;
	ArrayList<nDrawable> drawRun = new ArrayList<nDrawable>();
	ArrayList<nDrawable> preDrawRun = new ArrayList<nDrawable>();
	ArrayList<nDrawable> postDrawRun = new ArrayList<nDrawable>();
	HashMap<nDrawable, Integer> drawPrio = new HashMap<nDrawable, Integer>();
	int max_prio = 0;
	
	nRun tick_run;

	public sVec val_center_ratio_target;
	public boolean got_center_ratio_target = false;
	public sVec val_cam_pos_target;
	public boolean got_cam_pos_target = false;
	public sFlt val_cam_scale_target;
	public boolean got_cam_scale_target = false;
	public sFlt val_cam_rot_target;
	public boolean got_cam_rot_target = false;

	public boolean in_patch = false;
	
	public sVec val_pos;

	public sVec val_view_size;

	public sVec val_cam_pos;

	public sVec val_limit_pos, val_mouse_in_view;
	public sFlt val_cam_scale, val_cam_rot, val_zoom_min, val_zoom_max;
	public sBoo val_do_limit, val_wallp;

	public sVec val_corner_LB, val_corner_RB, val_corner_LT, val_corner_RT;
	
	public void set_limit_pos(float lx, float ly) {
		val_limit_pos.set(lx,ly); val_do_limit.set(true); }
	public void set_limit_zoom(float min, float max) {
		val_zoom_min.set(min); val_zoom_max.set(max); val_do_limit.set(true); }
	
	private void set_viewspace(float posx, float posy, float sx, float sy, float scale) {
		
		val_pos.set(posx,posy);
		view.metode("set_size", new Vector2(sx,sy));
		view.metode("event_corner_drag");
		val_cam_scale.set(scale);
		sBoo val_border = view.object("val_border", sBoo.class);
		val_border.set(false);
	}
	
	public void init() {
		bloc = app.data.root_bloc.obtainBloc("view_bloc");
		bloc.addObject("view", this);

		bloc.addMetode("clearing", new nRun() { public void run() {
			clear(); }}); 
		
		view = app.gui.addWidgetGroup("viewspace");
		bloc.addObject("viewspaceGroup", view);
		view.metode("link_to_bloc", bloc);
//		view.get("fx").setVFX();
		
		view.metode("set_title", "space view");
		
		val_pos = view.object("val_pos", sVec.class);
		val_view_size = view.object("val_view_size", sVec.class);
		val_cam_pos = view.object("val_cam_pos", sVec.class);
		val_cam_scale = view.object("val_cam_scale", sFlt.class);
		val_cam_rot = view.object("val_cam_rot", sFlt.class);
		val_wallp = view.object("val_wallp", sBoo.class);
		
		val_mouse_in_view = bloc.obtainVec("val_mouse_in_view");
		
		val_corner_LB = bloc.obtainVec("val_corner_LB");
		val_corner_RB = bloc.obtainVec("val_corner_RB");
		val_corner_LT = bloc.obtainVec("val_corner_LT");
		val_corner_RT = bloc.obtainVec("val_corner_RT");
		
		if (!app.config.STARTUP_LOAD) {
			if (app.config.RELEASE) {
				if (GdxApp.START_FULLSCREEN) 
					set_viewspace(690f,1030f, 1210f,950f, app.config.DEF_VIEW_ZOOM);
				else set_viewspace(20f,445f, 1260f,350f, app.config.DEF_VIEW_ZOOM);
			} else {
				if (app.config.start_solo) {
					if (GdxApp.START_FULLSCREEN) 
						set_viewspace(690f,1030f, 1210f,950f, app.config.DEF_VIEW_ZOOM);
					else set_viewspace(
							app.config.DEF_VIEW_WIN_POS.x,
							app.config.DEF_VIEW_WIN_POS.y,
							app.config.DEF_VIEW_WIN_SZ.x,
							app.config.DEF_VIEW_WIN_SZ.y,
							app.config.DEF_VIEW_ZOOM);
				} else set_viewspace(370f,445f, 510f,270f, app.config.DEF_VIEW_ZOOM);
			}
			if (app.config.VIEW_START_COLLAPSED) view.metode("run_collapse");
		}
		
		sBoo val_grid = view.object("val_grid", sBoo.class);
		val_grid.set(app.config.VIEW_START_GRID);
		

		

		app.gui.add_info_text("space zoom: ", view.object("val_cam_scale", sFlt.class));

		app.gui.add_info_text("Smouse: ", val_mouse_in_view);
		
		nWidget view_backref = view.get("backref");
		
		view_ref = app.gui.addWidget("ref");
		view_ref.setParent(view_backref);

		
		//TODO
//		view.get("backrender").setBackgroundRender();
		
		
		
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
		
//		nWidget close = view.get("close");
//		close.addEventTrigger(new nRunnable() { public void run() {
//			bloc.run("clearing"); }});

		view.get("close").setPassif().setDraw(false).setSize(0,0);
		
		val_center_ratio_target = bloc.obtainVec("val_center_ratio_target");
		val_cam_pos_target = bloc.obtainVec("val_cam_pos_target");
		val_cam_scale_target = bloc.obtainFlt("val_cam_scale_target", 1f);
		val_cam_rot_target = bloc.obtainFlt("val_cam_rot_target", 0f);

		val_limit_pos = bloc.obtainVec("val_limit", new Vector2(11000,11000));
		val_do_limit = bloc.obtainBoo("val_do_limit", false);
		val_zoom_min = bloc.obtainFlt("val_zoom_min", 0.05f);
		val_zoom_max = bloc.obtainFlt("val_zoom_max", 2f);

		view.get("backref").setCustomDrawer(new nDrawable() { public void drawing() {
			
			ArrayList<nDrawable> alldraw = Utl.duplic(preDrawRun);
			
			for (int prio = 0 ; prio <= max_prio ; prio++)
				for (nDrawable d : preDrawRun) 
					if (drawPrio.get(d) == prio) { d.drawing(); alldraw.remove(d); } 

			for (nDrawable d : alldraw) d.drawing(); 
		
		}});
		
		view.get("draw").setCustomDrawer(new nDrawable() { public void drawing() {
			
			ArrayList<nDrawable> alldraw = Utl.duplic(drawRun);
			
			for (int prio = 0 ; prio <= max_prio ; prio++)
				for (nDrawable d : drawRun) 
					if (drawPrio.get(d) == prio) { d.drawing(); alldraw.remove(d); } 
			
			for (nDrawable d : alldraw) d.drawing(); 

			alldraw = Utl.duplic(postDrawRun);
			
			for (int prio = 0 ; prio <= max_prio ; prio++)
				for (nDrawable d : postDrawRun) 
					if (drawPrio.get(d) == prio) { d.drawing(); alldraw.remove(d); } 
			
			for (nDrawable d : alldraw) d.drawing(); 
			
		}});
		
	}
	public void system_load() {

		app.addDelayEvent(1, new nRun() { public void run() {
			
			app.time.addEventTick(tick_run);
			
			if (!app.config.STARTUP_LOAD && app.config.VIEW_START_WALLPAPER) 
				val_wallp.set(true);
//			if (!app.start_solo) {
//				plane.getSystem(pNet.class).net
//					.addSyncVal(view.object("val_cam_pos", sVec.class));
//				plane.getSystem(pNet.class).net
//					.addSyncVal(view.object("val_cam_scale", sFlt.class));				
//			}
		}});
		
		app.term.addExecutor(new CommandExecutor("view") {

			@ConsoleDoc(description = "Cam scale", 
					paramDescriptions = {"scale"}) 
			public void scale(float r) {
				val_cam_scale_target.set(r);
				got_cam_scale_target = true;
				console.log("cam scale = "+r, LogLevel.SUCCESS);
			}

		});
	}

	public void clear() {

		if (app.time != null) app.time.removeEventTick(tick_run);
		
		if (bloc != null) bloc.clear();
		
	}

	private final Vector2 tmp1 = new Vector2();
	private final Vector2 tmp2 = new Vector2();
	public void frame(float d) {
		if (mouse_is_hover_view()) 
			val_mouse_in_view.set(mouse_in_view());
		else val_mouse_in_view.set(0,0);

		tmp1.set(val_view_size.x(),0)
			.scl(val_cam_scale.get() / 2f)
			.rotateRad(val_cam_rot.get());
		tmp2.set(0,val_view_size.y())
			.scl(val_cam_scale.get() / 2f)
			.rotateRad(val_cam_rot.get());
		val_corner_LB.set(-tmp1.x-tmp2.x,-tmp1.y-tmp2.y);
		val_corner_RB.set(tmp1.x-tmp2.x,tmp1.y-tmp2.y);
		val_corner_LT.set(-tmp1.x+tmp2.x,-tmp1.y+tmp2.y);
		val_corner_RT.set(tmp1.x+tmp2.x,tmp1.y+tmp2.y);
		
		if (got_center_ratio_target) {
			view.metode("set_center_ratio", val_center_ratio_target.get());
			got_center_ratio_target = false;
		}
		if (got_cam_pos_target) {
			view.metode("set_cam_pos", val_cam_pos_target.get());
			got_cam_pos_target = false;
		}
		if (got_cam_scale_target) {
			view.metode("set_cam_scale", val_cam_scale_target.get());
			got_cam_scale_target = false;
		}
		if (got_cam_rot_target) {
			view.metode("set_cam_rot", val_cam_rot_target.get());
			got_cam_rot_target = false;
		}
		if (val_do_limit.get()) {
			float min_zoom = (2f * val_limit_pos.x()) / val_view_size.x();
			min_zoom = Math.min(min_zoom, (2f * val_limit_pos.y()) / val_view_size.y());
			min_zoom = 1f / min_zoom;
			min_zoom = Math.max(min_zoom, val_zoom_min.get());
			if (val_cam_scale.get() > val_zoom_max.get()) 
				view.metode("set_cam_scale", val_zoom_max.get());
			if (val_cam_scale.get() < min_zoom) 
				view.metode("set_cam_scale", min_zoom);
			
			float sc = 1f / val_cam_scale.get();
			float decx = 0, decy = 0;
			float cx = 0, cy = 0;
			if (val_cam_pos.x() > 0 && 
					val_cam_pos.x() + sc * val_view_size.x() / 2f > val_limit_pos.x()) { cx++;
				decx += val_cam_pos.x() + sc * val_view_size.x() / 2f - val_limit_pos.x(); }
			if (val_cam_pos.x() < 0 && 
					val_cam_pos.x() - sc * val_view_size.x() / 2f < -val_limit_pos.x()) { cx++;
				decx += ((val_cam_pos.x() - sc * val_view_size.x() / 2f) + val_limit_pos.x()); }
			if (val_cam_pos.y() > 0 && 
					val_cam_pos.y() + sc * val_view_size.y() / 2f > val_limit_pos.y()) { cy++;
				decy += val_cam_pos.y() + sc * val_view_size.y() / 2f - val_limit_pos.y(); }
			if (val_cam_pos.y() < 0 && 
					val_cam_pos.y() - sc * val_view_size.y() / 2f < -val_limit_pos.y()) { cy++;
				decy += ((val_cam_pos.y() - sc * val_view_size.y() / 2f) + val_limit_pos.y()); }
			if (cx > 0) decx = decx/cx; if (cy > 0) decy = decy/cy;
			view.metode("set_cam_pos", 
					new Vector2(val_cam_pos.x() - decx, val_cam_pos.y() - decy));
			
			
		}
	}

	public void tick(float d) {
		if (got_cam_pos_target) {
			view.metode("set_cam_pos", val_cam_pos_target.get());
			got_cam_pos_target = false;
		}
		if (got_cam_scale_target) {
			view.metode("set_cam_scale", val_cam_scale_target.get());
			got_cam_scale_target = false;
		}
		if (got_cam_rot_target) {
			view.metode("set_cam_rot", val_cam_rot_target.get());
			got_cam_rot_target = false;
		}
	}
	
}
