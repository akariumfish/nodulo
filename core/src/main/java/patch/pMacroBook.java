package patch;

import com.badlogic.gdx.math.Vector2;

import aa_nodulo.PlaneApplet;
import aa_nodulo.pBody;
import app.App;
import patch.pMacro.Macro;
import patch.pMacro.MacroScript;
import util.nMap;
import util.nRun;

public class pMacroBook {

	
	
	public static void build() {
		
		build_tile_scripts();
		
		
		Macro executor = new Macro("executor")
		.addNode("exec", "executor", 	0f, 	0f).getMacro()
		.addNode("time", "time", 		-450f, 	0f)
			.addSetVar("tick", true).addSetVar("state", true).getMacro()
		.addLink("exec", "co_run", "time", "out")
		;

		Macro exec_actor = new Macro("exec_actor")
		.addNode("exec", "executor", 	300f, 	150f).getMacro()
		.addNode("time", "time", 		-300f, 	150f)
			.addSetVar("tick", true).addSetVar("state", true).getMacro()
		.addNode("actor", "actor", 		-300f,	-150f).getMacro()
		.addLink("exec", "co_run", "time", "out")
		.addLink("actor", "co_register", "exec", "co_reg")
		;

		Macro bullet_blueprint = new Macro("bullet_blueprint")
		.addNode("blueprint", "blueprint", 	0f, 		0f).getMacro()
//		.addNode("interactif", "interactif", -1200f, 	300f).getMacro()
//		.addNode("ownable", "ownable", 		-600f, 	0f).addSetVar("acquire", true).getMacro()
		.addNode("physic", "physic", 		-600f, 	900f)
		.addSetVar("use_dynamic", true)
		.addSetVar("use_ctrl_box", true)
		.addSetVar("use_light", true)
		.addSetVar("use_contact_break", true)
		.getMacro()
		.addNode("coordinate", "coordinate", -600f, 	600f)
//			.addSetVar("add_ctrl_pop", true)
			.getMacro()
		.addNode("graph", "graph", 			-600f, 	-600f)
			.addSetVar("line", true).addSetVar("halo", true)
			.addSetVar("thick", 1f).getMacro()
		.addNode("geom", "geom", 			-600f, 	-900f).getMacro()
		.addNode("damagezone", "damagezone", -600f, 	0f).getMacro()
		.addLink("coordinate", "param", "blueprint", "param_in")
		.addLink("graph", "param", "blueprint", "param_in")
		.addLink("geom", "param", "blueprint", "param_in")
		.addLink("damagezone", "param", "blueprint", "param_in")
		.addLink("physic", "param", "blueprint", "param_in")
//		.addLink("interactif", "param", "blueprint", "param_in")
//		.addLink("ownable", "param", "blueprint", "param_in")
		.addRun(new nRun() { public void run() {
			nMap<pInstance> list = arg(0, nMap.class);
			pInstance geom = list.get("geom");
			pInstance graph = list.get("graph");
			
			list.get("blueprint").setVar("name", "bullet_print");
			
			list.get("geom").patch.app.addDelayEvent(2, new nRun() { public void run() {
				geom.run("set_trig", 20f); 
			}});
		}})
		;

		Macro mob_blueprint = new Macro("mob_blueprint")
		.addNode("blueprint", "blueprint", 	0f, 		0f).getMacro()
		.addNode("coordinate", "coordinate", -600f, 	600f).addSetVar("use_ctrl_pop", true).getMacro()
		.addNode("interactif", "interactif", -600f, 	300f).getMacro()
		.addNode("ownable", "ownable", 		-600f, 	0f).addSetVar("acquire", true).getMacro()
		.addNode("graph", "graph", 			-600f, 	-600f)
			.addSetVar("line", true).addSetVar("fill", true).getMacro()
		.addNode("geom", "geom", 			-600f, 	-900f).getMacro()
		.addNode("physic", "physic", 		-600f, 	900f)
//		.addSetVar("use_kinematic", true)
		.addSetVar("use_dynamic", true)
		.addSetVar("use_ctrl_box", true)
		.addSetVar("use_cone_light", true)
//		.addSetVar("def_kinematic_rad", 70f)
//		.addSetVar("def_light_dist", 80f)
//		.addSetVar("def_light_r", (int)200)
//		.addSetVar("def_light_g", (int)200)
//		.addSetVar("def_light_b", (int)0)
//		.addSetVar("def_light_a", (int)200)
		.getMacro()
		.addNode("hittable", "hittable", 	-600f, 	1050f)
//		.addSetVar("use_avatar", true)
		.getMacro()
		.addLink("coordinate", "param", "blueprint", "param_in")
		.addLink("interactif", "param", "blueprint", "param_in")
		.addLink("graph", "param", "blueprint", "param_in")
		.addLink("geom", "param", "blueprint", "param_in")
		.addLink("ownable", "param", "blueprint", "param_in")
		.addLink("physic", "param", "blueprint", "param_in")
		.addLink("hittable", "param", "blueprint", "param_in")
		.addRun(new nRun() { public void run() {
			nMap<pInstance> list = arg(0, nMap.class);
			pInstance geom = list.get("geom");
			pInstance graph = list.get("graph");
			graph.run("pop_param_data", "fill");
			
			list.get("blueprint").setVar("name", "mob_print");
			
			list.get("geom").patch.app.addDelayEvent(2, new nRun() { public void run() {
				geom.run("set_def"); 
			}});
		}})
		;

		Macro body_blueprint = new Macro("body_blueprint")
		.addNode("blueprint", "blueprint", 	0f, 		0f).getMacro()
		.addNode("coordinate", "coordinate", -600f, 	600f).addSetVar("use_ctrl_pop", true).getMacro()
		.addNode("interactif", "interactif", -600f, 	300f).getMacro()
		.addNode("ownable", "ownable", 		-600f, 	0f).addSetVar("acquire", true).getMacro()
		.addNode("graph", "graph", 			-600f, 	-600f)
			.addSetVar("line", true).addSetVar("fill", true).getMacro()
		.addNode("geom", "geom", 			-600f, 	-900f).getMacro()
		.addNode("physic", "physic", 		-600f, 	900f)
//		.addSetVar("use_kinematic", true)
		.addSetVar("use_dynamic", true)
		.addSetVar("use_ctrl_box", true)
		.addSetVar("use_cone_light", true)
		.addSetVar("use_view_light", true)
//		.addSetVar("def_kinematic_rad", 70f)
//		.addSetVar("def_light_dist", 80f)
//		.addSetVar("def_light_r", (int)200)
//		.addSetVar("def_light_g", (int)200)
//		.addSetVar("def_light_b", (int)0)
//		.addSetVar("def_light_a", (int)200)
		.getMacro()
		.addNode("hittable", "hittable", 	-600f, 	1050f)
//		.addSetVar("use_avatar", true)
		.getMacro()
		.addLink("coordinate", "param", "blueprint", "param_in")
		.addLink("interactif", "param", "blueprint", "param_in")
		.addLink("graph", "param", "blueprint", "param_in")
		.addLink("geom", "param", "blueprint", "param_in")
		.addLink("ownable", "param", "blueprint", "param_in")
		.addLink("physic", "param", "blueprint", "param_in")
		.addLink("hittable", "param", "blueprint", "param_in")
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
		.addMacro("mob_blueprint", mob_blueprint, 0f, -3000f)
		.addMacro("body_blueprint", body_blueprint, 0f, -600f)
		.addMacro("bullet_blueprint", bullet_blueprint, 0f, 1800f)
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


		Macro empty = new Macro("empty")
			;
		
		Macro func_exemple = new Macro("func_exemple")
			.addNode("set_body_param", "function", 				-1200f,	600f)
			.addSetVar("func_ref", "set_body_param").addTileScript("set_body_param").getMacro()
			.addNode("get_body_param", "branch", 				0f,		600f)
			.addSetVar("branch_ref", "get_body_param").getMacro()
			.addTileScript("get_body_param", "get_body_param")
			.addNode("avatar", "function", 						0f, 		0f)
			.addSetVar("func_ref", "func_avatar")
			.addTileScript("avatar").getMacro()
			.addNode("auto_move", "function", 					-1500f, 	0f)
				.addTileScript("auto_move")
				.addSetVar("func_ref", "auto_move").getMacro()
			.addNode("auto_shoot", "function", 					-3000f, 	0f)
				.addTileScript("auto_shoot")
				.addSetVar("func_ref", "auto_shoot").getMacro()
			.addRun(new nRun() { public void run() {
				nMap<pInstance> list = arg(0, nMap.class);
				
				list.get("set_body_param").setVar("script", true);
				list.get("get_body_param").setVar("script", true);
//				list.get("avatar").setVar("script", true);
//				list.get("auto_move").setVar("script", true);
//				list.get("auto_shoot").setVar("script", true);
				
			}})
			;
		
		Macro main_exemple = new Macro("main_exemple")
			.addMacro("exac", pMacro.getMacro("exec_actor"), 		600f,	150f)
				.addSetVar("exac_exec", "target_ref", "func_avatar")
				.addSetVar("exac_actor", "pop_pos", new Vector2(0,200)) 
				.addSetVar("exac_actor", "print_name", "body_print")
			.addNode("ui", "UI", 								0f,		-300f)
				.addRunPop("ui_switch", "pop_plug_node", 
						"UI_widg_out", "UI_switch", "UI_widg_in").getMacro()
				.addSetVar("ui_ui_switch", "state", false)
				.addSetVar("ui_ui_switch", "widg_text", "mode")
			.addNode("reg_in_mode", "reg_in", 					600f,	-300f)
				.addSetVar("reg_ref", "mode").getMacro()
			.addLink("reg_in_mode", "co_reg", "exac_exec", "co_reg")
			.addLink("ui_ui_switch", "out", "reg_in_mode", "co_in")
			.addMacro("exac_shoot1", pMacro.getMacro("exec_actor"), 		-900f,	600f)
				.addSetVar("exac_shoot1_exec", "target_ref", "auto_move")
				.addSetVar("exac_shoot1_actor", "pop_pos", new Vector2(400,900)) 
				.addSetVar("exac_shoot1_actor", "print_name", "mob_print")
			.addMacro("exec_shoot1", pMacro.getMacro("executor"), 		-600f,	1050f)
				.addSetVar("exec_shoot1_exec", "target_ref", "auto_shoot")
				.addSetVar("exec_shoot1_time", "delay", (int)30)
				.addLink("exac_shoot1_actor", "co_register", "exec_shoot1_exec", "co_reg")
			.addMacro("exac_shoot2", pMacro.getMacro("exec_actor"), 		-900f,	-1050f)
				.addSetVar("exac_shoot2_exec", "target_ref", "auto_move")
				.addSetVar("exac_shoot2_actor", "pop_pos", new Vector2(800,-900)) 
				.addSetVar("exac_shoot2_actor", "print_name", "mob_print")
			.addMacro("exec_shoot2", pMacro.getMacro("executor"), 		-600f,	-600f)
				.addSetVar("exec_shoot2_exec", "target_ref", "auto_shoot")
				.addSetVar("exec_shoot2_time", "delay", (int)30)
				.addLink("exac_shoot2_actor", "co_register", "exec_shoot2_exec", "co_reg")
			
			.addNode("text", "text", 						-300f,		1500f).getMacro()
			.addRun(new nRun() { public void run() {
				nMap<pInstance> list = arg(0, nMap.class);
				
			}})
			;
		

	}
	

	
	
	
	
	
	private static void build_tile_scripts() {
		

		new MacroScript("avatar")

		.com("add_set_param", "ctrl_box", "accel_move")
		.com("add_get_reg_in_at", "body", "body")
		.com("add_get_input_at", "data", "keycross_press")

		.com("add_set_param", "ctrl_box", "accel_dir")
		.com("add_get_reg_in_at", "body", "body")
		.com("add_get_input_at", "data", "keycross_dir")

		.com("add_set_param", "ctrl_box", "decel_move")
		.com("add_get_reg_in_at", "body", "body")
		.com("add_at", "data", "not", "out")
		.com("add_get_input_at", "in", "keycross_press")
		.com("get_last")

		.com("add_if")
			.com("add_not_at", "test")
			.com("add_get_reg_in_at", "in", "mode")
			.com("get_last")

			.com("add_set_param", "ctrl_box", "rot_to_target")
			.com("add_get_reg_in_at", "body", "body")
			.com("add_get_input_at", "data", "mouse_hover_view")
			
			.com("add_set_param", "ctrl_box", "decel_rot")
			.com("add_get_reg_in_at", "body", "body")
			.com("add_at", "data", "not", "out")
			.com("add_get_input_at", "in", "mouse_hover_view")
			.com("get_last")
			
			.com("add_set_param", "ctrl_box", "rot_target")
			.com("add_get_reg_in_at", "body", "body")
			.com("add_dir_at", "data")
			.com("add_branch_at", "in", "get_body_param")
			.com("add_arr", "arg", "mouse", "mousepos").com("get_last")
			.com("get_last")
			
			.com("add_set_param", "ctrl_box", "global_ref")
			.com("add_get_reg_in_at", "body", "body")
			.com("add_boo_at", "data", true)
	
			.com("add_set_output", "cam_pos")
			.com("add_branch_at", "data", "get_body_param")
			.com("add_arr", "arg", "ref", "pos").com("get_last")
			
			.com("add_set_output", "cam_rot")
			.com("add_flt_at", "data", 0f)
			
		.com("add_close")
		.com("add_if")
			.com("add_get_reg_in_at", "test", "mode")

			.com("add_set_param", "ctrl_box", "accel_cw")
			.com("add_get_reg_in_at", "body", "body")
			.com("add_get_input_at", "data", "keycross_cw_state")

			.com("add_set_param", "ctrl_box", "accel_ccw")
			.com("add_get_reg_in_at", "body", "body")
			.com("add_get_input_at", "data", "keycross_ccw_state")

			.com("add_set_param", "ctrl_box", "decel_rot")
			.com("add_get_reg_in_at", "body", "body")
			.com("add_at", "data", "not", "out")
			.com("add_get_input_at", "in", "keyrot_press")
			.com("get_last")

			.com("add_set_param", "ctrl_box", "global_ref")
			.com("add_get_reg_in_at", "body", "body")
			.com("add_boo_at", "data", false)

//			.com("add_set_output", "cam_pos")
//			.com("add_branch_at", "data", "get_body_param")
//			.com("add_arr", "arg", "ref", "pos").com("get_last")
			
			.com("add_set_output", "cam_pos")
				.com("add_add_vec_at", "data")
					.com("add_rot_at", "fact")
						.com("add_axe_vec_at", "in")
						.com("add_flt_at", "y", 0f)
							.com("add_mult_at", "x")
								.com("add_flt_at", "in", 150f)
								.com("add_get_input_at", "fact", "cam_scale_inv")
								.com("get_last")
							.com("get_last")
						.com("add_branch_at", "rot", "get_body_param")
						.com("add_arr", "arg", "ref", "rot").com("get_last")
						.com("get_last")
					.com("add_branch_at", "in", "get_body_param")
					.com("add_arr", "arg", "ref", "pos").com("get_last")
				.com("get_last")
			
			.com("add_set_output", "cam_rot")
				.com("add_add_at", "data")
					.com("add_flt_at", "fact", -((float)Math.PI) / 2f)
					.com("add_branch_at", "in", "get_body_param")
					.com("add_arr", "arg", "ref", "rot").com("get_last")
				.com("get_last")
			
		.com("add_close")
		
		.com("add_set_param", "ctrl_pop", "pop")
		.com("add_get_reg_in_at", "body", "body")
		.com("add_get_input_at", "data", "mouse_right_click")
		
		.com("add_set_param", "ctrl_pop", "throw")
		.com("add_get_reg_in_at", "body", "body")
		.com("add_get_input_at", "data", "mouse_right_unclick")
		
		.com("add_set_param", "ctrl_pop", "blueprint_par")
		.com("add_get_reg_in_at", "body", "body")
		.com("add_str_at", "data", "bullet_print")
		
		.com("add_set_param", "ctrl_pop", "pop_pos")
		.com("add_get_reg_in_at", "body", "body")
		.com("add_mag_at", "data")
		.com("add_flt_at", "mag", 180f)
		.com("add_branch_at", "in", "get_body_param")
		.com("add_arr", "arg", "mouse", "mousepos").com("get_last")
		.com("get_last")
		
		.com("add_set_param", "ctrl_pop", "acc_pos")
		.com("add_get_reg_in_at", "body", "body")
		.com("add_mag_at", "data")
		.com("add_flt_at", "mag", 1f)
		.com("add_branch_at", "in", "get_body_param")
		.com("add_arr", "arg", "mouse", "mousepos").com("get_last")
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

		;



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
		
		
		float speed = 1;

		new MacroScript("auto_move") 
		.com("add_if")
			.com("add_and_at", "test")
				.com("add_not_at", "in1")
					.com("add_branch_at", "in", "get_body_param")
					.com("add_arr", "arg", "ctrl_box", "accel_up").com("get_last")
					.com("get_last")
				.com("add_not_at", "in2")
					.com("add_branch_at", "in", "get_body_param")
					.com("add_arr", "arg", "ctrl_box", "accel_down").com("get_last")
					.com("get_last")
				.com("get_last")
			.com("add_set_param", "ctrl_box", "accel_up")
			.com("add_get_reg_in_at", "body", "body")
			.com("add_boo_at", "data", true)
		.com("add_close")
		.com("add_if")
			.com("add_esup_at", "test")
				.com("add_vec_y_at", "in1")
					.com("add_branch_at", "in", "get_body_param")
					.com("add_arr", "arg", "ref", "pos").com("get_last")
					.com("get_last")
				.com("add_flt_at", "in2", 900f)
			.com("get_last")
			.com("add_set_param", "ctrl_box", "accel_down")
				.com("add_get_reg_in_at", "body", "body")
				.com("add_boo_at", "data", true)
			.com("add_set_param", "ctrl_box", "accel_up")
				.com("add_get_reg_in_at", "body", "body")
				.com("add_boo_at", "data", false)
		.com("add_close")
		.com("add_if")
			.com("add_einf_at", "test")
				.com("add_vec_y_at", "in1")
					.com("add_branch_at", "in", "get_body_param")
					.com("add_arr", "arg", "ref", "pos").com("get_last")
					.com("get_last")
				.com("add_flt_at", "in2", -900f)
				.com("get_last")
			.com("add_set_param", "ctrl_box", "accel_up")
				.com("add_get_reg_in_at", "body", "body")
				.com("add_boo_at", "data", true)
			.com("add_set_param", "ctrl_box", "accel_down")
				.com("add_get_reg_in_at", "body", "body")
				.com("add_boo_at", "data", false)
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
		.com("add_vec_at", "data", new Vector2(1,0))
		.com("add_set_param", "ctrl_pop", "blueprint_par")
		.com("add_get_reg_in_at", "body", "body")
		.com("add_str_at", "data", "bullet_print")
		;
		
		
		

		
	}

}
