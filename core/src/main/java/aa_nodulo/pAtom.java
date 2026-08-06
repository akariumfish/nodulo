package aa_nodulo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import app.App;
import data.sBloc_Builder;
import data.sBoo;
import data.sData;
import data.sFlt;
import data.sInt;
import data.sValue;
import data.sValueBloc;
import gui.nDrawable;
import gui.nInterface;
import gui.nWidget;
import gui.nWidgetGroup;
import util.nMap;
import util.nPool;
import util.nRun;
import patch.*;
import patch.pMacro.Macro;
import patch.pMacro.MacroScript;
import patch.pNode.CT;

public class pAtom extends pSystem {

	public static sBloc_Builder builder = null;
	
	public static void build(sData data) {

		if (builder == null) build_prop();
		
		builder = builder(data, "atom", pAtom.class, new nRun() { public void run(Object o) {
			sValueBloc b = (sValueBloc)o; newObject(b); }});
		
	}

	public static void dispose(PlaneApplet app) { pool.dispose(); }
	public static final nPool<pAtom> pool = new nPool<pAtom>() {
		protected pAtom newObject() { return new pAtom(); } };
	public static pAtom newObject(sValueBloc b) {
		return pool.obtain().init(b); }
	
	
	

	private static boolean has_build = false;
	
	public static void build_setup() {
		if (has_build) return;
		has_build = true;
		
		PlaneApplet.newStartupModel("atom_game")
		.setSetupRun(new nRun() { public void run() {
			
			pSheet.setDefMacro("main", "main_sheet_atom");
			pSheet.setDefMacro("init_space", "init_space_atom");
			pSheet.setDefMacro("common_param", "common_param_atom");
			pSheet.setDefMacro("function", "common_func_atom");

			pSheet.setDefCollapse("init_space", true);
			pSheet.setDefCollapse("common_param", true);
			pSheet.setDefCollapse("function", true);
		}})
		;
		
	}
	
	
	public static void build_game() {

		new MacroScript("atom_ctrl") 
		.com("add_set_param", "ref", "rot")
		.com("add_get_reg_in_at", "body", "body")
		.com("add_flt_at", "data", (float)Math.PI)

		.com("add_set_param", "ctrl_atom", "accel_right")
		.com("add_get_reg_in_at", "body", "body")
		.com("add_get_input_at", "data", "keycross_left_state")
		.com("add_set_param", "ctrl_atom", "accel_left")
		.com("add_get_reg_in_at", "body", "body")
		.com("add_get_input_at", "data", "keycross_right_state")
		
		.com("add_set_param", "ctrl_atom", "decelerate")
		.com("add_get_reg_in_at", "body", "body")
		.com("add_not_at", "data")
		.com("add_get_input_at", "in", "keycross_press")
		.com("get_last")

		.com("add_set_output", "cam_scale")
		.com("add_flt_at", "data", 0.15f)
		.com("add_set_output", "cam_rot")
		.com("add_flt_at", "data", (float)(Math.PI / 2f))
		.com("add_set_output", "cam_pos")
		.com("add_vec_at", "data", new Vector2(0,0))

		.com("add_set_param", "ctrl_atom", "pop")
		.com("add_get_reg_in_at", "body", "body")
		.com("add_get_input_at", "data", "key_space_click")
		.com("add_set_param", "ctrl_atom", "blueprint_par")
		.com("add_get_reg_in_at", "body", "body")
		.com("add_str_at", "data", "bullet_print")
//		.com("add_get_reg_in_at", "data", "blueprint")
		
		
//		.com("add_send_run")
		;
		
		float limit = 40;

		new MacroScript("atom_move") 
		.com("add_if")
			.com("add_and_at", "test")
			.com("add_not_at", "in1")
			.com("add_branch_at", "in", "get_body_param")
			.com("add_arr", "arg", "ctrl_atom", "accel_left").com("get_last")
			.com("get_last")
			.com("add_not_at", "in2")
			.com("add_branch_at", "in", "get_body_param")
			.com("add_arr", "arg", "ctrl_atom", "accel_right").com("get_last")
			.com("get_last")
			.com("get_last")
			.com("add_set_param", "ctrl_atom", "accel_left")
			.com("add_get_reg_in_at", "body", "body")
			.com("add_boo_at", "data", true)
		.com("add_close")
		.com("add_if")
			.com("add_esup_at", "test")
			.com("add_vec_y_at", "in1")
			.com("add_branch_at", "in", "get_body_param")
			.com("add_arr", "arg", "ref", "pos").com("get_last")
			.com("get_last")
			.com("add_flt_at", "in2", limit)
			.com("get_last")
			.com("add_set_param", "ctrl_atom", "accel_left")
			.com("add_get_reg_in_at", "body", "body")
			.com("add_boo_at", "data", false)
			.com("add_set_param", "ctrl_atom", "accel_right")
			.com("add_get_reg_in_at", "body", "body")
			.com("add_boo_at", "data", true)
		.com("add_close")
		.com("add_if")
			.com("add_einf_at", "test")
			.com("add_vec_y_at", "in1")
			.com("add_branch_at", "in", "get_body_param")
			.com("add_arr", "arg", "ref", "pos").com("get_last")
			.com("get_last")
			.com("add_flt_at", "in2", -limit)
			.com("get_last")
			.com("add_set_param", "ctrl_atom", "accel_left")
			.com("add_get_reg_in_at", "body", "body")
			.com("add_boo_at", "data", true)
			.com("add_set_param", "ctrl_atom", "accel_right")
			.com("add_get_reg_in_at", "body", "body")
			.com("add_boo_at", "data", false)
		.com("add_close")
		;

		new MacroScript("atom_shoot") 
		.com("add_set_param", "ctrl_atom", "pop")
		.com("add_get_reg_in_at", "body", "body")
		.com("add_boo_at", "data", true)
		.com("add_set_param", "ctrl_atom", "blueprint_par")
		.com("add_get_reg_in_at", "body", "body")
		.com("add_str_at", "data", "bullet_print")
//		.com("add_get_reg_in_at", "data", "blueprint")
		
//		.com("add_send_run")
		;
		
		
		
		
		
		// MACRO

		Macro common_func_atom = new Macro("common_func_atom")
		.addNode("set_body_param", "function", 	-1500f,	-600f)
		.addSetVar("func_ref", "set_body_param").addTileScript("set_body_param").getMacro()
		.addNode("get_body_param", "branch", 	0f,		-600f)
		.addSetVar("branch_ref", "get_body_param").addTileScript("get_body_param").getMacro()
		.addTileScript("get_body_param", "get_body_param")
		.addNode("func_p", "function", 		1200f, 	0f).addSetVar("func_ref", "atom_ctrl").getMacro()
		.addTileScript("func_p", "atom_ctrl")
		.addNode("func_m", "function", 		0f, 	0f).addSetVar("func_ref", "atom_move").getMacro()
		.addTileScript("func_m", "atom_move")
		.addNode("func_s", "function", 		-1200f, 	0f).addSetVar("func_ref", "atom_shoot").getMacro()
		.addTileScript("func_s", "atom_shoot")
		.addRun(new nRun() { public void run() {
			nMap<pInstance> list = arg(0, nMap.class);
			App.ap.addDelayEvent(3, new nRun() { public void run() {
//				list.get("func_p").setVar("script", true);
//				list.get("func_m").setVar("script", true);
//				list.get("func_s").setVar("script", true);
			}});
		}})
		;

		Macro atom_player_tile = new Macro("atom_player_tile")
		.addMacro("tile", pMacro.getMacro("executor"), 		0f, 	0f)
		.addSetVar("tile_exec", "target_ref", "atom_ctrl")
		;

		Macro atom_shooter_tile = new Macro("atom_shooter_tile")
		.addMacro("tile", pMacro.getMacro("executor"), 		0f, 	0f)
		.addSetVar("tile_exec", "target_ref", "atom_move")
		.addMacro("tile2", pMacro.getMacro("executor"), 		0f, 600f)
		.addSetVar("tile2_exec", "target_ref", "atom_shoot")
		.addSetVar("tile2_time", "delay", 15f)
		;
		
		Macro main_sheet_atom = new Macro("main_sheet_atom")
		.addMacro("atom_player_tile", atom_player_tile, 	600f, 	0f)
		.addMacro("atom_shooter_tile", atom_shooter_tile, 	-600f, 	0f)
		.addNode("from1", "from", 		0f, -60f)
		.addSetVar("this_ref", "fp1").addSetVar("target_ref", "tp1").getMacro()
		.addNode("from2", "from", 		-1200f, -60f)
		.addSetVar("this_ref", "fp2").addSetVar("target_ref", "tp2").getMacro()
		.addLink("atom_player_tile_tile_exec", "co_reg", "from1", "out")
		.addLink("atom_shooter_tile_tile_exec", "co_reg", "from2", "out")
		.addLink("atom_shooter_tile_tile2_exec", "co_reg", "from2", "out")
		.addRun(new nRun() { public void run() {
			nMap<pInstance> list = arg(0, nMap.class);
			
		}})
		;

		Macro atom_blueprint = new Macro("atom_blueprint")
		.addNode("blueprint", "blueprint", 	0f, 		0f).getMacro()
		.addNode("coordinate", "coordinate", -600f, 	600f)
			.addSetVar("use_ctrl_atom", true).getMacro()
		.addNode("interactif", "interactif", -600f, 	300f).getMacro()
		.addNode("ownable", "ownable", 		-600f, 	0f).addSetVar("acquire", true).getMacro()
		.addNode("moveable", "moveable", 	-600f, 	-300f).getMacro()
		.addNode("graph", "graph", 			-600f, 	-600f).addSetVar("line", true).getMacro()
		.addNode("hittable", "hittable", 	-600f, 	-900f).getMacro()
		.addNode("geom", "geom", 			-600f, 	-1200f).getMacro()
		.addLink("coordinate", "param", "blueprint", "param_in")
		.addLink("interactif", "param", "blueprint", "param_in")
		.addLink("moveable", "param", "blueprint", "param_in")
		.addLink("graph", "param", "blueprint", "param_in")
		.addLink("geom", "param", "blueprint", "param_in")
		.addLink("ownable", "param", "blueprint", "param_in")
		.addLink("hittable", "param", "blueprint", "param_in")
		.addRun(new nRun() { public void run() {
			nMap<pInstance> list = arg(0, nMap.class);
			pInstance geom = list.get("geom");
			pInstance graph = list.get("graph");
			graph.run("pop_param_data", "fill");

			list.get("blueprint").setVar("name", "atom_print");
			
			list.get("geom").patch.app.addDelayEvent(2, new nRun() { public void run() {
				geom.run("set_def"); }});
		}})
		;

		Macro common_param_atom = new Macro("common_param_atom")
		.addMacro("atom_blueprint", atom_blueprint, 0f, 0f)
		.addMacro("bullet_blueprint", pMacro.getMacro("bullet_blueprint"), 0f, 2700f)
		.addRun(new nRun() { public void run() {
			nMap<pInstance> list = arg(0, nMap.class);
			list.get("atom_blueprint_blueprint").setVar("name", "atom_print");
		}})
		;

		

		Macro atom_player = new Macro("atom_player")
		.addNode("sel_body", "sel_body", 	0f, 		60f).getMacro()
		.addNode("ank", "ank", 				0f, 		-450f).getMacro()
		.addNode("register", "register", 	900f, 	-300f).getMacro()
		.addNode("reg_in_bod", "reg_in", 	450f,	-60f).addSetVar("reg_ref", "body").getMacro()
		.addNode("reg_in_ank", "reg_in", 	450f,	-450f).addSetVar("reg_ref", "pointmouse").getMacro()
//		.addNode("reg_in_bp", "reg_in", 		1050f, 	-510f).addSetVar("reg_ref", "blueprint").getMacro()
//		.addNode("from", "from", 			1350f, 	-750f)
//		.addSetVar("this_ref", "").addSetVar("target_ref", "bullet_ref").getMacro()
		.addLink("sel_body", "co_sel_bod", "reg_in_bod", "co_in")
//		.addLink("from", "out", "reg_in_bp", "co_in")
		.addLink("reg_in_bod", "co_reg", "register", "co_reg")
//		.addLink("reg_in_bp", "co_reg", "register", "co_reg")
		.addLink("reg_in_ank", "co_reg", "register", "co_reg")
		.addLink("sel_body", "co_ank", "ank", "co_this")
		.addLink("ank", "co_mouse", "reg_in_ank", "co_in")
		.addRun(new nRun() { public void run() {
			nMap<pInstance> list = arg(0, nMap.class);
		}})
		;
		
		Macro atom_shooter = new Macro("atom_shooter")
		.addNode("sel_body", "sel_body", 	180f, 	60f).getMacro()
		.addNode("register", "register", 	780f, 	-300f).getMacro()
		.addNode("reg_in_bod", "reg_in", 	600f,	-60f).addSetVar("reg_ref", "body").getMacro()
//		.addNode("reg_in_bp", "reg_in", 		1200f, 	-510f).addSetVar("reg_ref", "blueprint").getMacro()
//		.addNode("from", "from", 			750f, 	-750f)
//		.addSetVar("this_ref", "").addSetVar("target_ref", "bullet_ref").getMacro()
		.addLink("sel_body", "co_sel_bod", "reg_in_bod", "co_in")
//		.addLink("from", "out", "reg_in_bp", "co_in")
		.addLink("reg_in_bod", "co_reg", "register", "co_reg")
//		.addLink("reg_in_bp", "co_reg", "register", "co_reg")
		.addRun(new nRun() { public void run() {
			nMap<pInstance> list = arg(0, nMap.class);
		}})
		;

		Macro init_space_atom = new Macro("init_space_atom")
		.addMacro("atom_player", pMacro.getMacro("atom_player"), 	1200f, 	-150f)
		.addMacro("atom_shooter", pMacro.getMacro("atom_shooter"), 	1200f, 	-1200f)
		.addMacro("construct1", "constructor", 	0f, 	-150f)
		.addSetVar("construct1_const", "print_name", "atom_print")
		.addMacro("construct2", "constructor", 	0f, 	-1200f)
		.addSetVar("construct2_const", "print_name", "atom_print")
		.addNode("space_init", "space_init", 		0f, 120f).getMacro()
		.addNode("to1", "to", 		3000f, -150f)
		.addSetVar("this_ref", "tp1").addSetVar("target_ref", "fp1").getMacro()
		.addNode("to2", "to", 		3000f, -1200f)
		.addSetVar("this_ref", "tp2").addSetVar("target_ref", "fp2").getMacro()
		.addLink("atom_player_register", "co_register", "to1", "in")
		.addLink("construct1_const", "co_run", "space_init", "start_run")
		.addLink("atom_shooter_register", "co_register", "to2", "in")
		.addLink("construct2_const", "co_run", "space_init", "start_run")
		.addLink("atom_player_sel_body", "in", "construct1_const", "co_body")
		.addLink("atom_shooter_sel_body", "in", "construct2_const", "co_body")
		.addRun(new nRun() { public void run() {
			nMap<pInstance> list = arg(0, nMap.class);
			
			list.get("construct1_ank").setVar("view_ank", false);
			list.get("construct1_ank").setVar("ank_pos", new Vector2(900,0));
			list.get("construct2_ank").setVar("view_ank", false);
			list.get("construct2_ank").setVar("ank_pos", new Vector2(-900,0));
			
			sValue v = PlaneApplet.app.view
				.bloc.getValue("val_grid");
			if (v != null) ((sBoo)v).set(false);
			v = PlaneApplet.app.getSystem(pAtom.class)
					.bloc.getValue("val_draw");
				if (v != null) ((sBoo)v).set(true);
			v = PlaneApplet.app.getSystem(pAtom.class)
					.bloc.getValue("val_play");
				if (v != null) ((sBoo)v).set(true);
		}})
		;

		
		
		pSheet.getSheetModel("main").addMacro("main_sheet_atom", main_sheet_atom);
		pSheet.getSheetModel("main").addMacro("atom_shooter_tile", atom_shooter_tile);
		pSheet.getSheetModel("main").addMacro("atom_player_tile", atom_player_tile);
		pSheet.getSheetModel("init_space").addMacro("init_space_atom", init_space_atom);
		pSheet.getSheetModel("init_space").addMacro("atom_shooter", atom_shooter);
		pSheet.getSheetModel("init_space").addMacro("atom_player", atom_player);
		pSheet.getSheetModel("common_param").addMacro("common_param_atom", common_param_atom);
		pSheet.getSheetModel("common_param").addMacro("atom_blueprint", atom_blueprint);
		
	}

	
	
	
	public static void build_prop() {
		
		pProperty hittable = pProperty.newGeneralProperty("hittable")
		;
		hittable.newLocalProperty("hitzone")
		.addData("hitpoint", (int)50)
		;

		pProperty.newGeneralProperty("damagezone")
		.addData("damage", (int)1)
		;

		pFamily.newFamily("hitzone")
		.addProp("ref")
		.addProp("hitzone")
		;
		pFamily.newFamily("damagezone")
		.addProp("damagezone")
		;
		
		
		
		
		

		nRun run_ctrl_atom = new nRun() { public void run(Object o) { 
			pBody bod = (pBody)o; if (bod == null) return;
			pGeom geo = PlaneApplet.app.getSystem(pGeom.class);
			pAtom ato = PlaneApplet.app.getSystem(pAtom.class);
			if (geo != null && bod.hasParam("ref") && bod.hasParam("var_move") && 
					bod.hasParam("move") && bod.hasParam("ctrl_atom")) {
				float accel = ato.val_accel.get();
				float base_decel = ato.val_base_decel.get();
				geo.slow_body(bod,base_decel,0);
				if (bod.getBoo("ctrl_atom","accel_left")) {
					geo.speed_body(bod,0,accel,0); }
				if (bod.getBoo("ctrl_atom","accel_right")) {
					geo.speed_body(bod,0,-accel,0); }
				if (bod.getBoo("ctrl_atom","decelerate")) {
					geo.slow_body(bod,ato.val_decel.get(),0);
				}
				float speed = bod.getFlt("var_move","pos_speed");
				float max_speed = ato.val_max_speed.get();
				if (speed > max_speed)
					geo.slow_body(bod,speed-max_speed,0);

				Vector2 pop_pos = new Vector2(ato.val_bullet_pop_dist.get(), 0);
				Vector2 acc_pos = new Vector2(ato.val_bullet_speed.get(), 0);
				
				if (bod.getBoo("ctrl_atom","pop")) {
					String bluep_par = bod.getStr("ctrl_atom","blueprint_par");
//					pParam bluep = bod.space.param_pools.get("blueprint").get(bluep_par);
					
					pParam bluep = null;
					for (pParam p : bod.space.param_pools.get("blueprint").all()) 
						if (p.getStr("name").equals(bluep_par)) { bluep = p; break; }
					
					if (bluep != null) { 
						pBody pop = pNodeSpace.new_body(bluep);
						if (pop == null) return;

						Vector2 bp = bod.getVec("ref", "pos");
						Vector2 p = new Vector2(pop_pos.x,pop_pos.y);
						p.rotateRad(bod.getFlt("ref", "rot"));
						p.add(bp);
						pop.setVec("ref", "pos", new Vector2(p.x,p.y));
						pop.setFlt("ref", "rot", pop_pos.angleRad());
						
						pNodeSpace.init_body(pop, bluep);

						Vector2 ac = new Vector2(acc_pos);
						ac.rotateRad(bod.getFlt("ref", "rot"));
						geo.speed_body(bod,pop,ac.x,ac.y,0f);
					} 
					bod.setBoo("ctrl_atom","pop", false);
				}
			}
		}};

		pGeom.newControlProp("atom",pProperty.get("coordinate"),run_ctrl_atom,"ref") 
		.setFullSync()
		.addData("pop", false)
		.addData("blueprint_par", "")
		.addData("accel_left", false)
		.addData("accel_right", false)
		.addData("decelerate", false)
		;

		
	}


	
	
	
	
	
	
	public pAtom() { super(); 
		tick_run = new nRun() { public void run(Object o) { tick((float)o); }};
		net_tick_run = new nRun() { public void run(Object o) { net_tick((float)o); }};
		draw_run = new nDrawable() { public void drawing() { draw(); }}; }

	nRun tick_run, net_tick_run;
	nDrawable draw_run;
	
	public pAtom init(sValueBloc b) { return (pAtom) super.init(b); }
	
	pTime time;
	pGeom geo;
	pSpace space;
	
	sBoo val_draw, val_play;
	sFlt val_accel, val_decel, val_max_speed, val_base_decel, val_bullet_speed, 
		val_bullet_pop_dist;
//	sInt val_max_hp, val_bullet_damage;
	
	public void system_init() {
		bloc.addObject("atom", this);

		app.storeSystemType(bloc.ref, this.getClass());

		useNetFrame();
		val_draw = bloc.obtainBoo("val_draw", false);
		val_play = bloc.obtainBoo("val_play", false);
		val_play.set(false);
		val_accel = bloc.obtainFlt("val_accel", 1f);
		val_decel = bloc.obtainFlt("val_decel", 0.4f);
		val_max_speed = bloc.obtainFlt("val_max_speed", 20f);
		val_base_decel = bloc.obtainFlt("val_base_decel", 0.1f);
		val_bullet_speed = bloc.obtainFlt("val_bullet_speed", 25f);
		val_bullet_pop_dist = bloc.obtainFlt("val_bullet_pop_dist", 150f);
//		val_max_hp = bloc.obtainInt("val_max_hp", (int)50);
//		val_bullet_damage = bloc.obtainInt("val_bullet_damage", (int)1);
		
	}
	public void system_load() {

		time = app.time;
		geo = app.getSystem(pGeom.class);
		space = app.space;
		time.addEventTick(tick_run);
		time.addEventNetTick(net_tick_run);
		app.view.addDrawable(draw_run);
		
		tool_setup(true);
		
	}
	public void system_clear() {
		time.removeEventTick(tick_run);
		time.removeEventNetTick(net_tick_run);
		app.view.removeDrawable(draw_run);
	}
	
	public void restart_game() {
		space.start_space();
		val_play.set(true);
	}
	
	public void tool_init(nInterface interf) {

		interf.setContext(bloc);
		interf.set_param("entry_height","2");
		interf.add_row();
		interf.add_row_label(1, "");
		interf.add_row_trigg(8, "restart", new nRun() { public void run() { 
			restart_game(); }});
		interf.add_row_label(1, "");

		interf.set_param("entry_height","0.5");
		interf.add_row();
		interf.add_row_label(10, "");

		interf.set_param("entry_height","2");
		interf.setContext(time.bloc);
		interf.add_row();
		interf.add_row_label(2, "");
		interf.add_row_switch_boo(6, "pause", "val_pause");
		interf.add_row_label(2, "");

		interf.set_param("entry_height","0.5");
		interf.add_row();
		interf.add_row_label(10, "");

		interf.set_param("entry_height","1");
		interf.add_row();
		interf.add_row_label(4, "accel");
		interf.add_row_slide_flt(6, 0.1f, 2f, "val_accel");
		interf.add_row();
		interf.add_row_label(4, "decel");
		interf.add_row_slide_flt(6, 0.1f, 2f, "val_decel");
		interf.add_row();
		interf.add_row_label(4, "max_speed");
		interf.add_row_slide_flt(6, 1f, 30f, "val_max_speed");
		interf.add_row();
		interf.add_row_label(4, "base_decel");
		interf.add_row_slide_flt(6, 0.1f, 2f, "val_base_decel");
		interf.add_row();
		interf.add_row_label(4, "bullet_speed");
		interf.add_row_slide_flt(6, 4f, 40f, "val_bullet_speed");
		
	}

	public void frame(float delta) { 
		
	}
	
	ArrayList<pBody> to_clr = new ArrayList<pBody>();
	ArrayList<Polygon> dmg_zones = new ArrayList<Polygon>();
	
	public void tick(float delta) {

		if (val_play.get()) {
			to_clr.clear();
			for (pGeom.Collision c : geo.collisions) {
				if (space.familyContains("hitzone", c.bod1) && 
					space.familyContains("damagezone", c.bod2)) {
				do_collide(c.bod1, c.bod2, c.overlap, to_clr); 
				dmg_zones.add(c.overlap); }
				else if (space.familyContains("damagezone", c.bod1) && 
						space.familyContains("hitzone", c.bod2)) {
					do_collide(c.bod2, c.bod1, c.overlap, to_clr); 
					dmg_zones.add(c.overlap); }
			}
			for (pBody b : to_clr) b.clear();
			to_clr.clear();
			space.update_families();

			for (pBody b : space.familyMember("hitzone")) {
				if (!b.hasParam("ref")) continue;
				Vector2 p = b.getVec("ref", "pos");
				if (p.y > 600f) b.addVec("ref", "pos", 0, -p.y+600f);
				if (p.y < -600f) b.addVec("ref", "pos", 0, -p.y-600f);
			}
		}	
	}

	public void net_frame(float delta) { 
		frame(delta);
	}
	public void net_tick(float delta) { 
		
	}
	public void draw() { 
		if (val_draw.get()) {
			for (pBody b : space.familyMember("hitzone")) {
				if (!b.hasParam("hitzone") || !b.hasParam("ref")) continue;
				Vector2 p = b.getVec("ref", "pos");
				int hp = b.getInt("hitzone", "hitpoint");
				app.text(""+hp, p.x, p.y, 24);
			}
			
			app.stroke(255,255,255,120,4f); app.fill(255,255,255,90);
			for (Polygon p : dmg_zones) app.polygon(p);
			dmg_zones.clear();
		
			app.stroke(255,255,255,120,10f); app.noFill();
			app.rect(-800f,-1000f,1600f,2000f);
		}
	}


	public void do_collide(pBody b1, pBody b2, Polygon coll, 
			ArrayList<pBody> to_clr) {
//		app.log("intersect");
		float coll_area = coll.area();
		if (coll_area <= 0) return;
//		
//		if (val_do_solidity.get() && (b1.getFlt("material", "solidity") > 0 || 
//				b2.getFlt("material", "solidity") > 0)) {
////			app.log("solid");
//			Vector2 center = coll.getCentroid(new Vector2());
//			Vector2 p1 = b1.getVec("ref", "pos");
//			Vector2 p2 = b2.getVec("ref", "pos");
//			Vector2 l1 = new Vector2(p1).sub(center);
//			Vector2 l2 = new Vector2(p2).sub(center);
////			float m1 = b1.getFlt("matter", "mass");
////			float m2 = b2.getFlt("matter", "mass");
////			if (m1 > 0 && m2 > 0) {
//				float f1 = b1.getFlt("material", "solidity") / 100f;
//				float f2 = b2.getFlt("material", "solidity") / 100f;
////				f1 *= m1 / m2; f2 *= m2 / m1;
//				l1.scl(f2); l2.scl(f1); 
//				b1.addVec("move", "acc_pos", l1);
//				b2.addVec("move", "acc_pos", l2);
////			}
//		}
		
		if (!b1.hasParam("hitzone") || !b2.hasParam("damagezone")) return;
		
		int damage = b2.getInt("damagezone", "damage");
		b1.setInt("hitzone", "hitpoint", b1.getInt("hitzone", "hitpoint") - damage);
		
		if (b1.getInt("hitzone", "hitpoint") <= 0) to_clr.add(b1);
		to_clr.add(b2);
	}
	
	
	
	


}
