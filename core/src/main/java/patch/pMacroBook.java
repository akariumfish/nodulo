package patch;

import com.badlogic.gdx.math.Vector2;

import aa_nodulo.PlaneApplet;
import aa_nodulo.pBody;
import patch.pMacro.Macro;
import patch.pMacro.MacroScript;
import util.nMap;
import util.nRun;

public class pMacroBook {


	private static void build_test(PlaneApplet app) {

//		new MacroScript("test")
//		.com("add_set_param", "ctrl_move", "accelerate")
//		.com("add_get_reg_in_at", "body", "body")
//		.com("add_get_input_at", "data", "keycross_press")
//		.com("add_set_param", "ctrl_move", "acc_pos")
//		.com("add_get_reg_in_at", "body", "body")
//		.com("add_at", "data", "rot", "out")
//		.com("add_flt_at", "rot", 0f)//-((float)Math.PI) / 2f)
//		.com("add_get_input_at", "in", "keycross_dir")
//		.com("get_last")
//		.com("add_set_param", "ctrl_move", "decelerate")
//		.com("add_get_reg_in_at", "body", "body")
//		.com("add_at", "data", "not", "out")
//		.com("add_get_input_at", "in", "keycross_press")
//		.com("get_last")
//		.com("add_set_output", "cam_pos")
//		.com("add_branch_at", "data", "get_body_param")
//		.com("add_arr", "arg", "ref", "pos").com("get_last")
//		;
//
//		Macro func_test = new Macro("func_test")
//			.addNode("get_body_param", "branch", 				0f,		-300f)
//			.addSetVar("branch_ref", "get_body_param").getMacro()
//			.addTileScript("get_body_param", "get_body_param")
//			.addNode("tile", "function", 		0f, 	0f)
//			.addSetVar("func_ref", "func_test")
//			.addTileScript("test").getMacro()
//			;
//		
//		Macro test = new Macro("TEST")
//			.addNode("sel_body", "sel_body", 	780f, 	60f).getMacro()
//			.addNode("from", "from", 		450f, 	60f)
//			.addSetVar("this_ref", "from1").addSetVar("target_ref", "to1").getMacro()
//			.addLink("sel_body", "in", "from", "out")
//			.addNode("register", "register", 	1380f, 	-300f).getMacro()
//			.addNode("ank", "ank", 				0f, 		-450f).getMacro()
//			.addNode("reg_in_ank", "reg_in", 	450f,	-450f).addSetVar("reg_ref", "pointmouse").getMacro()
//			.addNode("reg_in_bod", "reg_in", 	1200f,	-60f).addSetVar("reg_ref", "body").getMacro()
//			.addMacro("exec", pMacro.getMacro("executor"), 		1800f, 	60f)
//			.addSetVar("exec_exec", "target_ref", "func_test")
//			.addLink("sel_body", "co_sel_bod", "reg_in_bod", "co_in")
//			.addLink("ank", "co_mouse", "reg_in_ank", "co_in")
//			.addLink("reg_in_bod", "co_reg", "register", "co_reg")
//			.addLink("reg_in_ank", "co_reg", "register", "co_reg")
//			.addLink("register", "co_register", "exec_exec", "co_reg")
//			.addRun(new nRun() { public void run() {
//				nMap<pInstance> list = arg(0, nMap.class);
//			}})
//			;
//		
//
//
//		PlaneApplet.newStartupModel("TEST")
//		.setSetupRun(new nRun() { public void run() {
//			
//			pSheet.setDefMacro("main", test);
//			pSheet.setDefMacro("function", func_test);
//			pSheet.setDefCollapse("main", false);
//			pSheet.setDefCollapse("function", false);
//			pSheet.setDefCollapse("common_param", false);
//			pSheet.setDefCollapse("init_space", false);
//			
//		}})
//		;
//		
//		
	}
	
	
	
	
	
	
	
	
	
	
	public static void build(PlaneApplet app) {
		
		build_tile_scripts(app);
		
		
		Macro executor = new Macro("executor")
		.addNode("exec", "executor", 	0f, 	0f).getMacro()
		.addNode("time", "time", 		-450f, 	0f)
			.addSetVar("tick", true).addSetVar("state", true).getMacro()
		.addLink("exec", "co_run", "time", "out")
		;

		new Macro("FUNCTION_SETUP")
		.addNode("set_body_param", "function", 	-1500f,	-600f)
			.addSetVar("func_ref", "set_body_param").addTileScript("set_body_param").getMacro()
		.addNode("get_body_param", "branch", 	0f,		-600f)
			.addSetVar("branch_ref", "get_body_param").addTileScript("get_body_param").getMacro()
		.addNode("auto_move", "function", 		-4500f, 	0f)
			.addTileScript("auto_move")
			.addSetVar("func_ref", "auto_move").getMacro()
		.addNode("auto_shoot", "function", 		-3000f, 	0f)
			.addTileScript("auto_shoot")
			.addSetVar("func_ref", "auto_shoot").getMacro()
		.addNode("shoot", "function", 			-1500f, 	0f)
			.addTileScript("shoot").addSetVar("func_ref", "shoot").getMacro()
		.addNode("move1", "function", 			0f, 		0f)
			.addTileScript("move1").addSetVar("func_ref", "move1").getMacro()
		.addNode("move_target", "function", 		1500f, 	0f)
			.addTileScript("move_target").addSetVar("func_ref", "move_target").getMacro()
		.addNode("cam", "function", 				3000f, 	0f)
			.addTileScript("cam").addSetVar("func_ref", "cam").getMacro()
		.addNode("main", "function", 			1500f, 	-600f)
			.addTileScript("main_stack").addSetVar("func_ref", "main").getMacro()
		.addRun(new nRun() { public void run() {
			nMap<pInstance> list = arg(0, nMap.class);
			
			app.addDelayEvent(3, new nRun(list) { public void run() {
				nMap<pInstance> list = (nMap)builder;

//				list.get("set_body_param").setVar("script", true);
//				list.get("get_body_param").setVar("script", true);
//				list.get("main").setVar("script", true);
//				list.get("shoot").setVar("script", true);
//				list.get("move1").setVar("script", true);
//				list.get("move_target").setVar("script", true);
//				list.get("cam").setVar("script", true);
//				list.get("auto_shoot").setVar("script", true);
//				list.get("auto_move").setVar("script", true);
				
			}});
			
		}})
		;
		
		new Macro("sel_body")
		.addNode("sel_body", "sel_body", 	0f, 		60f).getMacro()
		.addNode("ank", "ank", 				0f, 		-450f).getMacro()
		.addNode("register", "register", 	900f, 	-300f).getMacro()
		.addNode("reg_in_bod", "reg_in", 	450f,	-60f).addSetVar("reg_ref", "body").getMacro()
		.addNode("reg_in_ank", "reg_in", 	450f,	-450f).addSetVar("reg_ref", "pointmouse").getMacro()
		.addLink("sel_body", "co_sel_bod", "reg_in_bod", "co_in")
		.addLink("reg_in_bod", "co_reg", "register", "co_reg")
		.addLink("reg_in_ank", "co_reg", "register", "co_reg")
		.addLink("sel_body", "co_ank", "ank", "co_this")
		.addLink("ank", "co_mouse", "reg_in_ank", "co_in")
		.addRun(new nRun() { public void run() {
			nMap<pInstance> list = arg(0, nMap.class);
		}})
		;

		Macro wall_blueprint = new Macro("wall_blueprint")
		.addNode("blueprint", "blueprint", 	0f, 		0f).getMacro()
		.addNode("coordinate", "coordinate", -600f, 	300f).addSetVar("add_ctrl_pop", true).getMacro()
		.addNode("graph", "graph", 			-600f, 	0f)
			.addSetVar("line", true).addSetVar("fill", true).getMacro()
		.addNode("geom", "geom", 			-600f, 	-300f).getMacro()
		.addLink("coordinate", "param", "blueprint", "param_in")
		.addLink("graph", "param", "blueprint", "param_in")
		.addLink("geom", "param", "blueprint", "param_in")
		.addRun(new nRun() { public void run() {
			nMap<pInstance> list = arg(0, nMap.class);
			pInstance geom = list.get("geom");
			pInstance graph = list.get("graph");
			graph.run("pop_param_data", "fill");
			
			list.get("blueprint").setVar("name", "wall_print");
			
			list.get("geom").patch.app.addDelayEvent(2, new nRun() { public void run() {
				geom.run("set_rect", 100f);
			}});
		}})
		;

		Macro bullet_blueprint = new Macro("bullet_blueprint")
		.addNode("blueprint", "blueprint", 	0f, 		0f).getMacro()
		.addNode("coordinate", "coordinate", -600f, 	600f).addSetVar("add_ctrl_pop", true).getMacro()
		.addNode("moveable", "moveable", 	-600f, 	-300f).addSetVar("add_ctrl_move", true).getMacro()
		.addNode("graph", "graph", 			-600f, 	-600f).addSetVar("line", true).getMacro()
		.addNode("geom", "geom", 			-600f, 	-900f).getMacro()
		.addNode("damagezone", "damagezone", -600f, 	0f).getMacro()
//		.addNode("to", "to", 				900f, 	0f)
//		.addSetVar("this_ref", "bullet_ref").addSetVar("target_ref", "").getMacro()
		.addLink("coordinate", "param", "blueprint", "param_in")
		.addLink("moveable", "param", "blueprint", "param_in")
		.addLink("graph", "param", "blueprint", "param_in")
		.addLink("geom", "param", "blueprint", "param_in")
		.addLink("damagezone", "param", "blueprint", "param_in")
//		.addLink("to", "in", "blueprint", "ref")
		.addRun(new nRun() { public void run() {
			nMap<pInstance> list = arg(0, nMap.class);
			pInstance geom = list.get("geom");
			pInstance graph = list.get("graph");
			
			list.get("blueprint").setVar("name", "bullet_print");
			
			list.get("geom").patch.app.addDelayEvent(2, new nRun() { public void run() {
				geom.run("set_trig", 30f); 
			}});
		}})
		;

		Macro body_blueprint = new Macro("body_blueprint")
		.addNode("blueprint", "blueprint", 	0f, 		0f).getMacro()
		.addNode("coordinate", "coordinate", -600f, 	600f).addSetVar("use_ctrl_pop", true).getMacro()
		.addNode("interactif", "interactif", -600f, 	300f).getMacro()
		.addNode("ownable", "ownable", 		-600f, 	0f).addSetVar("acquire", true).getMacro()
		.addNode("moveable", "moveable", 	-600f, 	-300f).addSetVar("use_ctrl_move", true).getMacro()
		.addNode("graph", "graph", 			-600f, 	-600f).addSetVar("line", true).getMacro()
		.addNode("geom", "geom", 			-600f, 	-900f).getMacro()
		.addLink("coordinate", "param", "blueprint", "param_in")
		.addLink("interactif", "param", "blueprint", "param_in")
		.addLink("moveable", "param", "blueprint", "param_in")
		.addLink("graph", "param", "blueprint", "param_in")
		.addLink("geom", "param", "blueprint", "param_in")
		.addLink("ownable", "param", "blueprint", "param_in")
		.addRun(new nRun() { public void run() {
			nMap<pInstance> list = arg(0, nMap.class);
			pInstance geom = list.get("geom");
			pInstance graph = list.get("graph");
			graph.run("pop_param_data", "fill");
			
			list.get("blueprint").setVar("name", "body_print");
			
			list.get("geom").patch.app.addDelayEvent(2, new nRun() { public void run() {
				geom.run("set_def"); 
			}});
		}})
		;

		
		

		new Macro("PARAM_SETUP")
		.addMacro("body_blueprint", body_blueprint, 0f, 0f)
		.addMacro("wall_blueprint", wall_blueprint, 0f, 1800f)
		.addMacro("bullet_blueprint", bullet_blueprint, 0f, 3600f)
		.addRun(new nRun() { public void run() {
			nMap<pInstance> list = arg(0, nMap.class);
		}})
		;

		Macro constructor = new Macro("constructor")
		.addNode("const", "constructor", 	0f, 	0f).getMacro()
		.addNode("ank", "ank", 					0f, 	-330f).getMacro()
		.addLink("const", "co_ank", "ank", "co_this")
		.addRun(new nRun() { public void run() {
			nMap<pInstance> list = arg(0, nMap.class);
			list.get("ank").setVar("view_ank", false);
		}})
		;
		
		Macro node_body_ctrl = new Macro("body_ctrl")
		.addNode("sel_body", "sel_body", 	0f, 		60f).getMacro() 
		.addNode("ank", "ank", 				0f, 		-450f).getMacro()
		.addNode("reg_in_loc", "reg_in", 	450f,	-900f).addSetVar("reg_ref", "target_loc").getMacro()
		.addNode("register", "register", 	1200f, 	-300f).getMacro()
		.addNode("reg_in_bod", "reg_in", 	450f,	-60f).addSetVar("reg_ref", "body").getMacro()
		.addNode("reg_in_targ", "reg_in", 	600f,	-90f).addSetVar("reg_ref", "target").getMacro()
		.addNode("reg_in_ank", "reg_in", 	450f,	-450f).addSetVar("reg_ref", "pointmouse").getMacro()
//		.addNode("reg_in_bp", "reg_in", 		1050f, 	-510f).addSetVar("reg_ref", "blueprint").getMacro()
		.addNode("reg_in_m1", "reg_in", 		750f, 	-300f).addSetVar("reg_ref", "move_mode_1").getMacro()
		.addNode("reg_in_m2", "reg_in", 		750f, 	-390f).addSetVar("reg_ref", "move_mode_2").getMacro()
		.addMacro("execM", executor, 		2250f, 	0f)
		.addSetVar("execM_exec", "target_ref", "main")
		.addNode("execF", "executor", 		2250f, 	-600f).addSetVar("target_ref", "cam").getMacro()
		.addNode("time", "time", 		1800f, 	-900f)
		.addSetVar("frame", true).addSetVar("state", true).getMacro()
		.addLink("execF", "co_run", "time", "out")
		.addNode("ui", "UI", 				120f, 	-210f)
		.addRunPop("ui_switch", "pop_plug_node", "UI_widg_out", "UI_switch", "UI_widg_in").getMacro()
		.addSetVar("ui_ui_switch", "state", false).addSetVar("ui_ui_switch", "widg_text", "view")
		.addNode("not", "not", 				510f, 	-210f).getMacro()
//		.addNode("from", "from", 			1350f, 	-750f)
//		.addSetVar("this_ref", "").addSetVar("target_ref", "bullet_ref").getMacro()
		.addLink("sel_body", "co_sel_bod", "reg_in_bod", "co_in")
//		.addLink("from", "out", "reg_in_bp", "co_in")
		.addLink("reg_in_bod", "co_reg", "register", "co_reg")
		.addLink("reg_in_targ", "co_reg", "register", "co_reg")
		.addLink("reg_in_loc", "co_reg", "register", "co_reg")
//		.addLink("reg_in_bp", "co_reg", "register", "co_reg")
		.addLink("reg_in_ank", "co_reg", "register", "co_reg")
		.addLink("reg_in_m1", "co_reg", "register", "co_reg")
		.addLink("reg_in_m2", "co_reg", "register", "co_reg")
		.addLink("register", "co_register", "execF", "co_reg")
		.addLink("register", "co_register", "execM_exec", "co_reg")
		.addLink("sel_body", "co_ank", "ank", "co_this")
		.addLink("ank", "co_mouse", "reg_in_ank", "co_in")
		.addLink("ui_ui_switch", "out", "reg_in_m2", "co_in")
		.addLink("ui_ui_switch", "out", "not", "in")
		.addLink("not", "out", "reg_in_m1", "co_in")
		.addRun(new nRun() { public void run() {
			nMap<pInstance> list = arg(0, nMap.class);
			
			pInstance bod_get_body = list.get("sel_body").get("pop_plug_node", pInstance.class, 
					"sel_bod_out", "bod_get_body", "sel_bod_in");
			bod_get_body.setVar("param_ref", "ctrl_seek");
			bod_get_body.setVar("body_ref", "target");
			pMacro.link_brics_cos(bod_get_body, "co_bod", list.get("reg_in_targ"), "co_in");
			
			pInstance ank_bod_pointer = list.get("ank").get("pop_plug_node", pInstance.class, 
					"ank_tool_out", "ank_bod_pointer", "ank_tool_in");
			pMacro.link_brics_cos(bod_get_body, "co_bod", ank_bod_pointer, "body");
			pMacro.link_brics_cos(list.get("reg_in_loc"), "co_in", ank_bod_pointer, "loc_pos");
			
			pInstance ank_zone = ank_bod_pointer.get("pop_plug_node", pInstance.class, 
					"ank_tool_out", "ank_zone", "ank_tool_in");
			ank_zone.setVar("show", false).setVar("radius", 2000f);
			
		}})
		;

		Macro node_shooter = new Macro("shooter")
		.addMacro("const", constructor)
		.addSetVar("const_const", "print_name", "body_print")
		.addNode("sel_body", "sel_body", 	780f, 	60f).getMacro()
		.addNode("register", "register", 	1380f, 	-300f).getMacro()
		.addNode("reg_in_bod", "reg_in", 	1200f,	-60f).addSetVar("reg_ref", "body").getMacro()
//		.addNode("reg_in_bp", "reg_in", 		1800f, 	-510f).addSetVar("reg_ref", "blueprint").getMacro()
		.addNode("executor", "executor", 	2700f, 	60f).addSetVar("target_ref", "auto_move").getMacro()
		.addNode("time", "time", 		2100f, 	60f)
			.addSetVar("tick", true).addSetVar("state", true).getMacro()
		.addLink("executor", "co_run", "time", "out")
		.addNode("executor2", "executor", 	2700f, 	-300f).addSetVar("target_ref", "auto_shoot").getMacro()
		.addNode("time2", "time", 		2100f, 	-300f)
			.addSetVar("tick", true).addSetVar("state", true).addSetVar("delay", 20f).getMacro()
		.addLink("executor2", "co_run", "time2", "out")
//		.addNode("from", "from", 			2100f, 	-750f)
//		.addSetVar("this_ref", "").addSetVar("target_ref", "bullet_ref").getMacro()
		.addLink("sel_body", "co_sel_bod", "reg_in_bod", "co_in")
//		.addLink("from", "out", "reg_in_bp", "co_in")
		.addLink("reg_in_bod", "co_reg", "register", "co_reg")
//		.addLink("reg_in_bp", "co_reg", "register", "co_reg")
		.addLink("register", "co_register", "executor", "co_reg")
		.addLink("register", "co_register", "executor2", "co_reg")
		.addLink("const_const", "co_body", "sel_body", "in")
		.addRun(new nRun() { public void run() {
			nMap<pInstance> list = arg(0, nMap.class);
			app.addDelayEvent(8, new nRun() { public void run() {
				list.get("const_const").get("new_body", pBody.class);
			}});
		}})
		;

		new Macro("SETUP")
		.addMacro("const", constructor, -300f, 1500f)
		.addSetVar("const_const", "print_name", "wall_print")
		.addMacro("shooter", node_shooter, -3600f, 1500f)
		.addMacro("shooter2", node_shooter, -3600f, 2100f)
		.addSetVar("shooter_const_ank", "ank_pos", new Vector2(450,200))
		.addSetVar("shooter2_const_ank", "ank_pos", new Vector2(450,-200))
		.addMacro("body_ctrl", node_body_ctrl, 2400f, 1500f)
		.addNode("from", "from", 		1800f, 	1500f)
		.addSetVar("this_ref", "from1").addSetVar("target_ref", "to1").getMacro()
		.addLink("body_ctrl_sel_body", "in", "from", "out")
		.addNode("action", "action", 		1200f, 	2250f).getMacro()
		.addNode("bang", "bang", 		600f, 	2250f).getMacro()
		.addLink("action", "body", "body_ctrl_sel_body", "co_sel_bod")
		.addLink("action", "run", "bang", "out")
//		.addNode("pview", "pview", 		0f, 	2100f).getMacro()
		.addRun(new nRun() { public void run() {
			nMap<pInstance> list = arg(0, nMap.class);

			pInstance action = list.get("action");
			
			app.addDelayEvent(2, new nRun() { public void run() {
				action.run("def_ctrl", "move");
				action.run("pop_ctrl_chan", "accelerate");
				action.run("pop_ctrl_chan", "acc_pos");
				action.run("pop_ctrl_key", "accelerate", (int)1, true);
				action.run("pop_ctrl_key", "accelerate", (int)19, true);
				action.run("pop_ctrl_key", "accelerate", (int)1, false);
				action.run("pop_ctrl_key", "accelerate", (int)18, false);
				action.run("pop_ctrl_key", "acc_pos", (int)1, new Vector2(5,0)); 
				action.run("pop_ctrl_key", "acc_pos", (int)38, new Vector2(5,0)); 
			}});
			
//			list.get("body_ctrl_tile").get("pop_plug_node", "tile_pop_out", 
//					"stack_editor", "tile_pop_in");
			
			app.addDelayEvent(16, new nRun(list) { public void run() {
				nMap<pInstance> list = (nMap)builder;
				
				pInstance ank2 = list.get("const_ank");
				for (int i = 0 ; i < 4 ; i++) {
					float s = 400f * (float)Math.sqrt(2f);
					ank2.setVar("ank_pos", new Vector2(-850,-s+(i*s)));
					pBody b2 = list.get("const_const")
							.get("new_body", pBody.class);
					b2.setFlt("scale", "scale", 4f);
				}
				
			}});
			
		}})
		;
		
		
		//  DONT WORK
//		new Macro("CLNT_STP")
////		.addMacro("body_ctrl", node_body_ctrl)
//		.addRun(new nRun() { public void run() {
//			nMap<pInstance> list = arg(0, nMap.class);
//			
//			app.addDelayEvent(12, new nRun() { public void run() {
////				
////				list.get("body_ctrl_tile_tile").setVar("script", true);
////				list.get("body_ctrl_tile2_tile").setVar("script", true);
////				list.get("body_ctrl_tile3_tile").setVar("script", true);
////				list.get("body_ctrl_tileF_tile").setVar("script", true);
////				
//			}});
//		}})
//		;

		
		
		build_test(app);
		
	}
	

	
	
	
	
	
	private static void build_tile_scripts(PlaneApplet app) {


		new MacroScript("get_body_param") 
		.com("add_mem_at", "in", "data") 
		.com("add_get_pass_at", "mem", (int)1)
		.com("add_mem_at", "obj", "param")
		.com("add_get_reg_in_at", "obj", "body")
		.com("add_get_pass_at", "mem", (int)0)
		.com("get_last")
		;
		new MacroScript("set_body_param") 
		.com("add_set_mem")
			.com("add_get_pass_at", "data", (int)2) 
			.com("add_get_pass_at", "mem", (int)1)
			.com("add_mem_at", "obj", "param")
				.com("add_get_reg_in_at", "obj", "body")
				.com("add_get_pass_at", "mem", (int)0)
			.com("get_last")
		.com("get_last")
		;
		
		
		
		
		new MacroScript("main_stack") 
		.com("add_if")
			.com("add_boo_at", "test", true)
			.com("add_func", "move1")
			.com("add_func", "move_target")
			.com("add_func", "shoot")
		.com("add_close")
		;
		
		new MacroScript("move1")
		.com("add_if")
			.com("add_get_reg_in_at", "test", "move_mode_1")
			
//			.com("add_set_param", "ctrl_move", "accelerate")
//			.com("add_get_reg_in_at", "body", "body")
//			.com("add_get_input_at", "data", "keycross_press")

			.com("add_func", "set_body_param")
			.com("add_arr_at", "arg", "ctrl_move", "accelerate")
			.com("add_arr_at", "array")
			.com("add_get_input_at", "entry", "keycross_press")
			.com("get_last").com("get_last").com("get_last")
			
			.com("add_set_param", "ctrl_move", "acc_pos")
			.com("add_get_reg_in_at", "body", "body")
			.com("add_get_input_at", "data", "keycross_dir")
			.com("add_set_param", "ctrl_move", "target_rot")
			.com("add_get_reg_in_at", "body", "body")
			.com("add_get_input_at", "data", "mouse_hover_view")
			.com("add_set_param", "ctrl_move", "decelerate")
			.com("add_get_reg_in_at", "body", "body")
			.com("add_not_at", "data")
			.com("add_get_input_at", "in", "keycross_press")
			.com("get_last")
			.com("add_set_param", "ctrl_move", "decelerate_rot")
			.com("add_get_reg_in_at", "body", "body")
			.com("add_not_at", "data")
			.com("add_get_input_at", "in", "mouse_hover_view")
			.com("get_last")
			.com("add_set_param", "ctrl_move", "trg_rot")
			.com("add_get_reg_in_at", "body", "body")
			.com("add_dir_at", "data")
			.com("add_get_reg_in_at", "in", "pointmouse") 
			.com("get_last")
			.com("add_set_param", "ctrl_move", "global_ref")
			.com("add_get_reg_in_at", "body", "body")
			.com("add_boo_at", "data", true)
			.com("add_set_output", "cam_pos")
			.com("add_branch_at", "data", "get_body_param")
			.com("add_arr", "arg", "ref", "pos").com("get_last")
			.com("add_set_output", "cam_rot")
			.com("add_flt_at", "data", 0f)
//			.com("add_send_run")
		.com("add_close")
		;

		new MacroScript("move_target") 
		.com("add_if")
			.com("add_not_at", "test")
			.com("add_get_reg_in_at", "in", "move_mode_2")
			.com("get_last")
			.com("add_return")
		.com("add_close")
		
//		.com("add_if")
//			.com("add_branch_at", "test", "get_body_param")
//			.com("add_arr", "arg", "ctrl_seek", "got_target").com("get_last")
//			.com("add_set_param", "ctrl_move", "trg_rot")
//			.com("add_get_reg_in_at", "body", "body")
//			.com("add_dir_at", "data")
//			.com("add_get_reg_in_at", "in", "target_loc")
//			.com("get_last")
//			.com("add_set_param", "ctrl_move", "target_rot")
//			.com("add_get_reg_in_at", "body", "body")
//			.com("add_get_input_at", "data", "mouse_hover_view")
//			.com("add_set_param", "ctrl_move", "decelerate_rot")
//			.com("add_get_reg_in_at", "body", "body")
//			.com("add_not_at", "data")
//			.com("add_get_input_at", "in", "mouse_hover_view")
//			.com("get_last")
//		.com("add_close")
//		.com("add_if")
//			.com("add_not_at", "test")
//			.com("add_branch_at", "in", "get_body_param")
//			.com("add_arr", "arg", "ctrl_seek", "got_target").com("get_last")
//			.com("get_last")
			.com("add_set_param", "ctrl_move", "accelerate_rot")
			.com("add_get_reg_in_at", "body", "body")
			.com("add_get_input_at", "data", "keyrot_press")
			.com("add_set_param", "ctrl_move", "decelerate_rot")
			.com("add_get_reg_in_at", "body", "body")
			.com("add_not_at", "data")
			.com("add_get_input_at", "in", "keyrot_press")
			.com("get_last")
			.com("add_set_param", "ctrl_move", "acc_rot")
			.com("add_get_reg_in_at", "body", "body")
			.com("add_mult_at", "data")
			.com("add_flt_at", "fact", 0.02f)
			.com("add_get_input_at", "in", "keyrot_dir")
			.com("get_last")
//		.com("add_close")
		
		.com("add_set_param", "ctrl_move", "accelerate")
		.com("add_get_reg_in_at", "body", "body")
		.com("add_get_input_at", "data", "keycross_press")
		.com("add_set_param", "ctrl_move", "acc_pos")
		.com("add_get_reg_in_at", "body", "body")
		.com("add_rot_at", "data")
		.com("add_flt_at", "rot", -((float)Math.PI) / 2f)
		.com("add_get_input_at", "in", "keycross_dir")
		.com("get_last")
		.com("add_set_param", "ctrl_move", "decelerate")
		.com("add_get_reg_in_at", "body", "body")
		.com("add_not_at", "data")
		.com("add_get_input_at", "in", "keycross_press")
		.com("get_last")
		.com("add_set_param", "ctrl_move", "global_ref")
		.com("add_get_reg_in_at", "body", "body")
		.com("add_boo_at", "data", false)
		
		.com("add_set_output", "cam_pos")
		.com("add_branch_at", "data", "get_body_param")
		.com("add_arr", "arg", "ref", "pos").com("get_last")
		.com("add_set_output", "cam_rot")
		.com("add_add_at", "data")
		.com("add_flt_at", "fact", -((float)Math.PI) / 2f)
		.com("add_branch_at", "in", "get_body_param")
		.com("add_arr", "arg", "ref", "rot").com("get_last")
		.com("get_last")
//		.com("add_send_run")
		;

		new MacroScript("cam") 
		.com("add_set_output", "cam_pos")
		.com("add_branch_at", "data", "get_body_param")
		.com("add_arr", "arg", "ref", "pos").com("get_last")
//		.com("add_send_run")
		;

		new MacroScript("shoot") 
		.com("add_set_param", "ctrl_pop", "pop")
		.com("add_get_reg_in_at", "body", "body")
		.com("add_get_input_at", "data", "mouse_right_click")
		.com("add_set_param", "ctrl_pop", "throw")
		.com("add_get_reg_in_at", "body", "body")
		.com("add_get_input_at", "data", "mouse_right_unclick")
		.com("add_set_param", "ctrl_pop", "blueprint_par")
		.com("add_get_reg_in_at", "body", "body")
		.com("add_str_at", "data", "bullet_print")
//		.com("add_get_reg_in_at", "data", "blueprint")
		
		.com("add_set_param", "ctrl_pop", "pop_pos")
		.com("add_get_reg_in_at", "body", "body")
		.com("add_mag_at", "data")
		.com("add_flt_at", "mag", 150f)
		.com("add_get_reg_in_at", "in", "pointmouse")
		.com("get_last")
		.com("add_set_param", "ctrl_pop", "acc_pos")
		.com("add_get_reg_in_at", "body", "body")
		.com("add_mag_at", "data")
		.com("add_flt_at", "mag", 16f)
		.com("add_get_reg_in_at", "in", "pointmouse")
		.com("get_last")
		.com("add_set_param", "ctrl_time", "activate")
		.com("add_get_reg_in_at", "body", "body")
		.com("add_get_input_at", "data", "key_space_state")
		.com("add_set_param", "ctrl_move", "trg_pos")
		.com("add_get_reg_in_at", "body", "body")
		.com("add_get_input_at", "data", "mouse_pos")
		.com("add_set_param", "ctrl_move", "teleport")
		.com("add_get_reg_in_at", "body", "body")
		.com("add_and_at", "data")
		.com("add_get_input_at", "in1", "key_shift_state")
		.com("add_get_input_at", "in2", "mouse_left_click")
		.com("get_last")
//		.com("add_send_run")
		;
		
		float speed = 1;

		new MacroScript("auto_move") 
		.com("add_if")
			.com("add_esup_at", "test")
			.com("add_vec_y_at", "in1")
			.com("add_get_param_at", "in", "ctrl_move", "acc_pos")
			.com("add_get_reg_in_at", "body", "body")
			.com("get_last")
			.com("get_last")
			.com("add_flt_at", "in2", 0f)
			.com("get_last")
			.com("add_set_param", "ctrl_move", "accelerate")
			.com("add_get_reg_in_at", "body", "body")
			.com("add_boo_at", "data", true)
			.com("add_set_param", "ctrl_move", "acc_pos")
			.com("add_get_reg_in_at", "body", "body")
			.com("add_vec_at", "data", new Vector2(0,speed))
		.com("add_close")
		.com("add_if")
			.com("add_esup_at", "test")
			.com("add_vec_y_at", "in1")
			.com("add_branch_at", "in", "get_body_param")
			.com("add_arr", "arg", "ref", "pos").com("get_last")
			.com("get_last")
			.com("add_flt_at", "in2", 60f)
			.com("get_last")
			.com("add_set_param", "ctrl_move", "acc_pos")
			.com("add_get_reg_in_at", "body", "body")
			.com("add_vec_at", "data", new Vector2(0,-speed))
		.com("add_close")
		.com("add_if")
			.com("add_inf_at", "test")
			.com("add_vec_y_at", "in1")
			.com("add_branch_at", "in", "get_body_param")
			.com("add_arr", "arg", "ref", "pos").com("get_last")
			.com("get_last")
			.com("add_flt_at", "in2", 0f)
			.com("get_last")
			.com("add_set_param", "ctrl_move", "accelerate")
			.com("add_get_reg_in_at", "body", "body")
			.com("add_boo_at", "data", true)
			.com("add_set_param", "ctrl_move", "acc_pos")
			.com("add_get_reg_in_at", "body", "body")
			.com("add_vec_at", "data", new Vector2(0,-speed))
		.com("add_close")
		.com("add_if")
			.com("add_einf_at", "test")
			.com("add_vec_y_at", "in1")
			.com("add_branch_at", "in", "get_body_param")
			.com("add_arr", "arg", "ref", "pos").com("get_last")
			.com("get_last")
			.com("add_flt_at", "in2", -60f)
			.com("get_last")
			.com("add_set_param", "ctrl_move", "acc_pos")
			.com("add_get_reg_in_at", "body", "body")
			.com("add_vec_at", "data", new Vector2(0,speed))
		.com("add_close")
		;
		
		new MacroScript("auto_shoot") 
		.com("add_set_param", "ctrl_pop", "pop")
		.com("add_get_reg_in_at", "body", "body")
		.com("add_boo_at", "data", true)
		.com("add_set_param", "ctrl_pop", "keep")
		.com("add_get_reg_in_at", "body", "body")
		.com("add_boo_at", "data", false)
		.com("add_set_param", "ctrl_pop", "throw")
		.com("add_get_reg_in_at", "body", "body")
		.com("add_boo_at", "data", true)
		.com("add_set_param", "ctrl_pop", "acc_pos")
		.com("add_get_reg_in_at", "body", "body")
		.com("add_vec_at", "data", new Vector2(20,0))
		.com("add_set_param", "ctrl_pop", "blueprint_par")
		.com("add_get_reg_in_at", "body", "body")
		.com("add_str_at", "data", "bullet_print")
//		.com("add_get_reg_in_at", "data", "blueprint")
		
//		.com("add_send_run")
		;
		
		
		

		
	}

}
