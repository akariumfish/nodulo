package patch;

import java.util.ArrayList;

import com.badlogic.gdx.math.Vector2;

import aa_nodulo.PlaneApplet;
import aa_nodulo.pBody;
import app.App;
import patch.pMacro.Macro;
import patch.pMacro.MacroScript;
import util.nMap;
import util.nRun;

public class pMacroBook {

	
	private static ArrayList<String> funcs = new ArrayList<String>();
	private static MacroScript newMacroScript(String r) {
		funcs.add(r);
		return new MacroScript(r);
	}
	private static Macro addMacroScript(Macro m) {
		int n = 0;
		for (String r : funcs) {
			m.addNode(r, "function", 	(n * -1200f) -2400f, 0f)
			.addSetVar("func_ref", "func_"+r)
			.addTileScript(r).getMacro();
			n++;
		}
		return m;
	}
	public static void build() {
		
		build_tile_scripts();
		
		
		Macro bullet_blueprint = new Macro("bullet_blueprint")
		.addNode("blueprint", "blueprint", 	0f, 		0f).getMacro()
//		.addNode("interactif", "interactif", -1200f, 	300f).getMacro()
		.addNode("physic", "physic", 		-600f, 	900f)
		.addSetVar("dynamic", true)
		.addSetVar("use_ctrl_box", true)
		.addSetVar("light", true)
		.addSetVar("contact_break", true)
		.addSetVar("sensor", true)
		.getMacro()
		.addNode("coordinate", "coordinate", -600f, 	600f).getMacro()
		.addNode("geom", "geom", 			-600f, 	-300f)
			.addSetVar("halo", true).getMacro()
		.addNode("hitzone", "hitzone", -600f, 	0f).getMacro()
		.addLink("coordinate", "param", "blueprint", "param_in")
		.addLink("geom", "param", "blueprint", "param_in")
		.addLink("hitzone", "param", "blueprint", "param_in")
		.addLink("physic", "param", "blueprint", "param_in")
//		.addLink("interactif", "param", "blueprint", "param_in")
		.addRun(new nRun() { public void run() {
			nMap<pInstance> list = arg(0, nMap.class);
			pInstance geom = list.get("geom");
			
			list.get("blueprint").setVar("name", "bullet_print");
			
			list.get("geom").patch.app.addDelayEvent(2, new nRun() { public void run() {
				geom.run("set_trig", 20f); 
			}});
		}})
		;

		Macro mob_blueprint = new Macro("mob_blueprint")
		.addNode("blueprint", "blueprint", 	0f, 		0f).getMacro()
		.addNode("coordinate", "coordinate", -600f, 	600f)
		.addSetVar("use_ctrl_mob", true).getMacro()
		.addNode("interactif", "interactif", -600f, 	300f).getMacro()
		.addNode("geom", "geom", 			-600f, 	0f).getMacro()
		.addNode("physic", "physic", 		-600f, 	900f)
		.addSetVar("dynamic", true)
		.addSetVar("use_ctrl_box", true)
		.addSetVar("aura", true)
		.getMacro()
		.addNode("hitpoint", "hitpoint", 	-600f, 	1050f).getMacro()
		.addLink("coordinate", "param", "blueprint", "param_in")
		.addLink("interactif", "param", "blueprint", "param_in")
		.addLink("geom", "param", "blueprint", "param_in")
		.addLink("physic", "param", "blueprint", "param_in")
		.addLink("hitpoint", "param", "blueprint", "param_in")
		.addRun(new nRun() { public void run() {
			nMap<pInstance> list = arg(0, nMap.class);
			pInstance geom = list.get("geom");
			
			list.get("blueprint").setVar("name", "mob_print");
			
			list.get("geom").patch.app.addDelayEvent(2, new nRun() { public void run() {
				geom.run("empty_geom");
				geom.run("new_trig",0f,0f,80f,0f);
				geom.run("new_trig",0f,0f,30f,(float)Math.PI);
			}});
		}})
		;

		Macro body_blueprint = new Macro("body_blueprint")
		.addNode("blueprint", "blueprint", 	0f, 		0f).getMacro()
		.addNode("coordinate", "coordinate", -600f, 	600f)
			.addSetVar("use_ctrl_pop", true)
			.addSetVar("use_ctrl_time", true)
			.getMacro()
		.addNode("interactif", "interactif", -600f, 	300f).getMacro()
		.addNode("logic", "logic", 			-600f, 	-300f)
			.addSetVar("use_ctrl_func", true)
			.addSetVar("tick_func_ref", "func_avatar")
			.getMacro()
		.addNode("geom", "geom", 			-600f, 	-600f).getMacro()
		.addNode("physic", "physic", 		-600f, 	900f)
			.addSetVar("dynamic", true)
			.addSetVar("use_ctrl_box", true)
			.addSetVar("aura", true)
			.addSetVar("view_light", true)
		.getMacro()
		.addNode("hitpoint", "hitpoint", 	-600f, 	1050f)
			.addSetVar("avatar", true)
		.getMacro()
		.addLink("coordinate", "param", "blueprint", "param_in")
		.addLink("interactif", "param", "blueprint", "param_in")
		.addLink("geom", "param", "blueprint", "param_in")
		.addLink("physic", "param", "blueprint", "param_in")
		.addLink("hitpoint", "param", "blueprint", "param_in")
		.addLink("logic", "param", "blueprint", "param_in")
		
		.addNode("ui", "UI", 								-1800f,		-300f)
			.addRunPop("ui_switch", "pop_plug_node", 
					"UI_widg_out", "UI_switch", "UI_widg_in").getMacro()
			.addSetVar("ui_ui_switch", "state", true)
			.addSetVar("ui_ui_switch", "widg_text", "mode")
		.addNode("reg_in_mode", "reg_in", 					-1200f,	-300f)
			.addSetVar("reg_ref", "mode").getMacro()
		.addLink("reg_in_mode", "co_reg", "logic", "co_reg")
		.addLink("ui_ui_switch", "out", "reg_in_mode", "co_in")
		
		.addRun(new nRun() { public void run() {
			nMap<pInstance> list = arg(0, nMap.class);
			pInstance geom = list.get("geom");
			
			list.get("blueprint").setVar("name", "body_print");
			
			list.get("geom").patch.app.addDelayEvent(2, new nRun() { public void run() {
				geom.run("empty_geom");
				geom.run("new_face",-40f,20f,80f,20f,-40f,40f);
				geom.run("new_face",-40f,-20f,80f,-20f,-40f,-40f);
				geom.run("new_trig",-15f,50f,35f,0f);
				geom.run("new_trig",-15f,-50f,35f,0f);
				geom.run("new_trig",0f,0f,80f,0f);
				geom.run("new_trig",-10f,0f,35f,0f);
			}});
			
			App.ap.addDelayEvent(60, new nRun() { public void run() {
				if (PlaneApplet.app.config.POP_BODY_EDITOR) 
					geom.run("pop_editor");
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

		
		
		Macro func_exemple = addMacroScript(new Macro("func_exemple"))
			.addNode("avatar", "function", 						0f, 		0f)
			.addSetVar("func_ref", "func_avatar")
			.addTileScript("avatar").getMacro()
			.addNode("startup", "function", 						-1200f, 		0f)
			.addSetVar("func_ref", "func_start")
			.addTileScript("startup").getMacro()
			.addRun(new nRun() { public void run() {
				nMap<pInstance> list = arg(0, nMap.class);
				
//				list.get("set_body_param").setVar("script", true);
//				list.get("get_body_param").setVar("script", true);
//				list.get("startup").setVar("script", true);
//				list.get("pop_mob").setVar("script", true);
//				list.get("avatar").setVar("script", true);
				
			}})
			;


//		Macro executor = new Macro("executor")
//		.addNode("exec", "executor", 	0f, 	0f).getMacro()
//		.addNode("time", "time", 		-450f, 	0f)
//			.addSetVar("tick", true).addSetVar("state", true).getMacro()
//		.addLink("exec", "co_run", "time", "out")
//		;
//
//		Macro exec_actor = new Macro("exec_actor")
//		.addNode("exec", "executor", 	300f, 	150f).getMacro()
//		.addNode("time", "time", 		-300f, 	150f)
//			.addSetVar("tick", true).addSetVar("state", true).getMacro()
//		.addNode("actor", "actor", 		-300f,	-150f).getMacro()
//		.addLink("exec", "co_run", "time", "out")
//		.addLink("actor", "co_register", "exec", "co_reg")
//		;

		
		Macro main_exemple = new Macro("main_exemple")
			
			.addNode("startup_run", "space_init", 	150f, 	1500f).getMacro()
			.addNode("startup_exec", "executor", 	600f, 	1500f)
			.addSetVar("target_ref", "func_start").getMacro()
			.addLink("startup_exec", "co_run", "startup_run", "start_run")
			
			.addNode("text", "text", 				-300f,		1500f).getMacro()
			.addRun(new nRun() { public void run() {
				nMap<pInstance> list = arg(0, nMap.class);
				
			}})
			;
		

	}
	

	
	
	
	
	
	private static void build_tile_scripts() {


		new MacroScript("startup")

		.com("add_func","func_pop_body")
			.com("add_arr_at", "arg")
				.com("add_new_at","entry","body_print")
			.com("get_last")

		.com("add_func","func_pop_mob")
			.com("add_arr_at", "arg")
				.com("add_new_at","entry","mob_print")
				.com("add_arr_at", "array")
					.com("add_vec_at", "entry", new Vector2(800,-900))
					.com("add_arr_at", "array")
						.com("add_vec_at", "entry", new Vector2(800,900))
					.com("get_last")
				.com("get_last")
			.com("get_last")

		.com("add_func","func_pop_mob")
			.com("add_arr_at", "arg")
				.com("add_new_at","entry","mob_print")
				.com("add_arr_at", "array")
					.com("add_vec_at", "entry", new Vector2(1800,-900))
					.com("add_arr_at", "array")
						.com("add_vec_at", "entry", new Vector2(1800,900))
					.com("get_last")
				.com("get_last")
			.com("get_last")
			
		;

		newMacroScript("pop_body")
		;

		newMacroScript("pop_mob")
		.com("add_set_param", "ctrl_mob", "spawn")
			.com("add_get_pass_at", "body", (int)0)
			.com("add_get_pass_at", "data", (int)1)
		.com("add_set_param", "ctrl_mob", "target")
			.com("add_get_pass_at", "body", (int)0)
			.com("add_get_pass_at", "data", (int)2)
		;

		new MacroScript("avatar")

		.com("add_set_param", "ctrl_box", "accel_move")
		.com("add_get_pass_at", "body", (int)0)
		.com("add_get_input_at", "data", "keycross_press")

		.com("add_set_param", "ctrl_box", "accel_dir")
		.com("add_get_pass_at", "body", (int)0)
		.com("add_get_input_at", "data", "keycross_dir")

		.com("add_set_param", "ctrl_box", "decel_move")
		.com("add_get_pass_at", "body", (int)0)
		.com("add_at", "data", "not", "out")
		.com("add_get_input_at", "in", "keycross_press")
		.com("get_last")

		.com("add_if")
			.com("add_not_at", "test")
			.com("add_get_reg_in_at", "in", "mode")
			.com("get_last")

			.com("add_set_param", "ctrl_box", "rot_to_target")
			.com("add_get_pass_at", "body", (int)0)
			.com("add_get_input_at", "data", "mouse_hover_view")
			
			.com("add_set_param", "ctrl_box", "decel_rot")
			.com("add_get_pass_at", "body", (int)0)
			.com("add_at", "data", "not", "out")
			.com("add_get_input_at", "in", "mouse_hover_view")
			.com("get_last")
			
			.com("add_set_param", "ctrl_box", "rot_target")
			.com("add_get_pass_at", "body", (int)0)
			.com("add_dir_at", "data")
			.com("add_get_param_at", "in", "mouse", "mousepos")
			.com("add_get_pass_at", "body", (int)0).com("get_last")
			.com("get_last")
			
			.com("add_set_param", "ctrl_box", "global_ref")
			.com("add_get_pass_at", "body", (int)0)
			.com("add_boo_at", "data", true)
	
			.com("add_set_output", "cam_pos")
			.com("add_get_param_at", "data", "ref", "pos")
			.com("add_get_pass_at", "body", (int)0).com("get_last")
			
			.com("add_set_output", "cam_rot")
			.com("add_flt_at", "data", 0f)
			
		.com("add_close")
		.com("add_if")
			.com("add_get_reg_in_at", "test", "mode")

			.com("add_set_param", "ctrl_box", "accel_cw")
			.com("add_get_pass_at", "body", (int)0)
			.com("add_get_input_at", "data", "keycross_cw_state")

			.com("add_set_param", "ctrl_box", "accel_ccw")
			.com("add_get_pass_at", "body", (int)0)
			.com("add_get_input_at", "data", "keycross_ccw_state")

			.com("add_set_param", "ctrl_box", "decel_rot")
			.com("add_get_pass_at", "body", (int)0)
			.com("add_at", "data", "not", "out")
			.com("add_get_input_at", "in", "keyrot_press")
			.com("get_last")

			.com("add_set_param", "ctrl_box", "global_ref")
			.com("add_get_pass_at", "body", (int)0)
			.com("add_boo_at", "data", false)
			
			.com("add_set_output", "cam_pos")
				.com("add_add_vec_at", "data")
					.com("add_rot_at", "fact")
						.com("add_axe_vec_at", "in")
						.com("add_flt_at", "y", 0f)
							.com("add_mult_at", "x")
								.com("add_mult_at", "in")
									.com("add_get_input_at", "in", "cam_height")
									.com("add_flt_at", "fact", 1f/3f)
									.com("get_last")
								.com("add_get_input_at", "fact", "cam_scale_inv")
								.com("get_last")
							.com("get_last")
						.com("add_get_param_at", "rot", "ref", "rot")
						.com("add_get_pass_at", "body", (int)0).com("get_last")
						.com("get_last")
					.com("add_get_param_at", "in", "ref", "pos")
					.com("add_get_pass_at", "body", (int)0).com("get_last")
				.com("get_last")
			
			.com("add_set_output", "cam_rot")
				.com("add_add_at", "data")
					.com("add_flt_at", "fact", -((float)Math.PI) / 2f)
					.com("add_get_param_at", "in", "ref", "rot")
					.com("add_get_pass_at", "body", (int)0).com("get_last")
				.com("get_last")
			
		.com("add_close")

		.com("add_set_param", "ctrl_time", "activate")
		.com("add_get_pass_at", "body", (int)0)
		.com("add_get_input_at", "data", "key_space_state")

		.com("add_set_param", "ctrl_pop", "pop")
		.com("add_get_pass_at", "body", (int)0)
		.com("add_get_input_at", "data", "mouse_right_click")
		
		.com("add_set_param", "ctrl_pop", "throw")
		.com("add_get_pass_at", "body", (int)0)
		.com("add_get_input_at", "data", "mouse_right_unclick")
		
		.com("add_set_param", "ctrl_pop", "blueprint_par")
		.com("add_get_pass_at", "body", (int)0)
		.com("add_str_at", "data", "bullet_print")
		
		.com("add_set_param", "ctrl_pop", "pop_pos")
		.com("add_get_pass_at", "body", (int)0)
		.com("add_mag_at", "data")
		.com("add_flt_at", "mag", 180f)
		.com("add_get_param_at", "in", "mouse", "mousepos")
		.com("add_get_pass_at", "body", (int)0).com("get_last")
		.com("get_last")
		
		.com("add_set_param", "ctrl_pop", "acc_pos")
		.com("add_get_pass_at", "body", (int)0)
		.com("add_mag_at", "data")
		.com("add_flt_at", "mag", 1f)
		.com("add_get_param_at", "in", "mouse", "mousepos")
		.com("add_get_pass_at", "body", (int)0).com("get_last")
		.com("get_last")
		
		.com("add_set_param", "ctrl_time", "activate")
		.com("add_get_pass_at", "body", (int)0)
		.com("add_get_input_at", "data", "key_space_state")

		.com("add_set_param", "ctrl_move", "trg_pos")
		.com("add_get_pass_at", "body", (int)0)
		.com("add_get_input_at", "data", "mouse_pos")	
		
		.com("add_set_param", "ctrl_move", "teleport")
		.com("add_get_pass_at", "body", (int)0)
		.com("add_and_at", "data")
		.com("add_get_input_at", "in1", "key_shift_state")
		.com("add_get_input_at", "in2", "mouse_left_click")
		.com("get_last")

		;

		
	}

}
