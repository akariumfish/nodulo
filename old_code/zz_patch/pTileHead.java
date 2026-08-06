package zz_patch;

import java.util.ArrayList;
import java.util.Map;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import gui.nInterface;
import gui.nWidget;
import gui.nWidgetGroup;
import util.Utl;
import util.nRun;
import zz_applet.Applet;
import zz_patch.pTile.CT;
import zz_patch.pTile.PlugDef;
import zz_plane.pSpace;
import zz_plane.pTime;

public class pTileHead {
	
	public static int func_counter = 0;

	public static void build_nodes(Applet app) {

//		build_stack_editor(app);
		
		float RS = nGUI.book.RS;
		

		pStandard exec_context = pStandard.newStandard("exec_context_abstract", "inst");
		
		exec_context
		.newRun("obtain_all_reg_of_model", new nRun() {public Object get() { 
			String model_ref = arg(0, String.class);
			ArrayList<String> allkey = new ArrayList<String>();
			if (model_ref == null) return allkey;
			pInstance co_reg = instance.get("get_co", pInstance.class, "co_reg");
			if (co_reg == null) return allkey;
			Object op_reg = co_reg.get("obtain_all_nodes", Object.class);
			if (op_reg == null) return allkey;
			ArrayList<Object> reg_prov = (ArrayList)op_reg;
			for (Object or : reg_prov) if (or instanceof pInstance) {
				pInstance reg = (pInstance)or;
				if (reg != null) {
					if (reg.stand.ref.equals("node_model_"+model_ref) && 
							reg.hasVar("reg_ref")) {
						allkey.add(reg.getVar("reg_ref", String.class));
					} else if (reg.stand.ref.equals("node_model_register")) {
						ArrayList<String> ak = reg.get("obtain_all_reg_of_model", 
								ArrayList.class, model_ref);
						if (ak != null) {
							for (String nk : ak) 
								if (!Utl.contains(allkey, nk)) allkey.add(nk);
						}
					}
				}
			}
			return allkey;
		}})
		.newRun("obtain_reg", new nRun() {public Object get() { 
			String reg_ref = arg(0, String.class);
			if (reg_ref == null) return null;
			pInstance co_reg = instance.get("get_co", pInstance.class, "co_reg");
			if (co_reg == null) return null;
			Object op_reg = co_reg.get("obtain_all_nodes", Object.class);
			if (op_reg == null) return null;
			ArrayList<Object> reg_prov = (ArrayList)op_reg;
			for (Object or : reg_prov) if (or instanceof pInstance) {
				pInstance reg = (pInstance)or;
				if (reg != null) {
					if (reg.hasVar("reg_ref") && 
							reg.getVar("reg_ref", String.class).equals(reg_ref)) {
						return reg;
					} else if (reg.stand.ref.equals("node_model_register")) {
						pInstance reg_reg = reg.get("obtain_reg", pInstance.class, reg_ref);
						if (reg_reg != null) return reg_reg;
					}
				}
			}
			return null;
		}})
//		.newRun("add_temp", new nRun() {public void run() { 
//			String r = arg(0, String.class); Object d = arg(1, Object.class);
//			if (r == null || d == null) return;
//			instance.obtainVar("temp_var_"+r, d);
////			instance.setVar("temp_var_"+r, d);
////			instance.obtainVar("temp_var_"+r+"_init", d);
//			instance.setVar("temp_var_"+r+"_init", d);
//			if (!instance.hasObject("all_temp_var_list")) {
//				instance.addObject("all_temp_var_list", new ArrayList<String>()); }
//			ArrayList<String> all_temp = instance.object("all_temp_var_list", 
//					ArrayList.class);
//			if (!Utl.contains(all_temp, r)) all_temp.add(r);
//			instance.setObject("all_temp_var_list", all_temp);
//		}})
//		.newRun("del_all_temp", new nRun() {public void run() { 
//			if (!instance.hasObject("all_temp_var_list")) {
//				instance.addObject("all_temp_var_list", new ArrayList<String>()); }
//			ArrayList<String> all_temp = instance.object("all_temp_var_list", 
//					ArrayList.class);
//			for (String r : all_temp) {
//				if (r == null) continue;
//				if (instance.hasVar("temp_var_"+r)) instance.removeVar("temp_var_"+r);
//				if (instance.hasVar("temp_var_"+r+"_init")) 
//					instance.removeVar("temp_var_"+r+"_init"); }
//			all_temp.clear();
//			instance.setObject("all_temp_var_list", all_temp);
//		}})
//		.newRun("init_all_temp", new nRun() {public void run() { 
//			if (!instance.hasObject("all_temp_var_list")) {
//				instance.addObject("all_temp_var_list", new ArrayList<String>()); }
//			ArrayList<String> all_temp = instance.object("all_temp_var_list", 
//					ArrayList.class);
//			for (String r : all_temp) {
//				if (r == null || !instance.hasVar("temp_var_"+r) 
//						|| !instance.hasVar("temp_var_"+r+"_init")) continue;
//				instance.setVar("temp_var_"+r, instance.getVar("temp_var_"+r+"_init"));
//			}
//		}})
//		.newRun("init_temp", new nRun() {public void run() { 
//			String r = arg(0, String.class); 
//			if (r == null || !instance.hasVar("temp_var_"+r) 
//					|| !instance.hasVar("temp_var_"+r+"_init")) return;
//			instance.setVar("temp_var_"+r, instance.getVar("temp_var_"+r+"_init"));
//		}})
//		.newRun("set_temp", new nRun() {public void run() { 
//			String r = arg(0, String.class); Object d = arg(1, Object.class);
//			if (r == null || d == null) return;
//			if (instance.hasVar("temp_var_"+r)) instance.setVar("temp_var_"+r, d);
////			else instance.run("add_temp", r, d);
//		}})
//		.newRun("get_temp", new nRun() {public Object get() { 
//			String r = arg(0, String.class); 
//			if (r == null || !instance.hasVar("temp_var_"+r)) return null;
//			return instance.getVar("temp_var_"+r);
//		}})
		;
		
		
		pNode.newNodeModel("executor", "tile")
		.append(exec_context)
		.newRun("run_stack", new nRun() {public void run() { 
			if (!instance.getVar("activate", Boolean.class)) return;
			String target_ref = instance.getVar("target_ref", String.class);
			pInstance func = instance.patch.common_functions.get(target_ref);
			if (func == null || target_ref.length() == 0) {
				pInstance co_func = instance.get("get_co", pInstance.class, "co_func");
				if (co_func == null) return;
				func = co_func.get("obtain_node", pInstance.class);
			}
			if (func == null) return;
			Object[] script = func.get("get_instruction_script", Object[].class);
			if (script == null) return;
			pFunc.func_script_run(instance, script, null); 
		}})
		.process()
		.openSec()
		.param("def", true, "height", 1.5f) 
		.run(pNode.getRun(pNode.CT.RUNP_VAR_BOO_SWITCH), 
				"activate", "activate", (int)8)
		.closeSec()
		.commande(pNode.getCom(pNode.CT.COM_ADD_ROW))
		.openSec().param("height", 0.1f)
		.run(pNode.getRun(pNode.CT.RUNP_ADD_LABEL), "", (int)20).closeSec()
		.commande(pNode.getCom(pNode.CT.COM_ADD_ROW))
		.openSec()
			.param("width", (int)12)
			.run(pNode.getRun(pNode.CT.RUNP_VAR_STR_WATCH), "target_ref")
		.closeSec()
		.openSec()
			.param("run", new nRun() {public void run() {
				nWidget trigg_w = instance.get("get_mapped_widget", nWidget.class, 
						"trigg_dropm");
				if (trigg_w == null) return;
				instance.patch.patch_dropmenu.metode("clear_entrys");
				for (String par : instance.patch.common_functions.allKey()) {
					nWidget w1 = (nWidget)instance.patch.patch_dropmenu
							.metodeGet("add_entry_custom", par, RS*6f, RS*2f/3f);
					w1.addEventTrigger(new nRun(instance) { public void run() {
						((pInstance)builder).setVar("target_ref", par); 
					}}); }
				instance.patch.patch_dropmenu.metode("open", trigg_w); 
			}})
			.run(pNode.getRun(pNode.CT.RUNP_ADD_TRIGG), "trigg_dropm", "Pk", (int)2)
		.closeSec()
		.getStand()
		.openSec()
			.param("event_receive", new nRun() {public void run() { 
				pInstance node = instance.object("node", pInstance.class);
				node.run("run_stack"); }})
			.param("keys", new String[]{"bang", "func"}, "filters", new String[]{"bang"}) 
			.run(pNode.getRun(pNode.CT.RUNS_ADD_CO_IN), "co_run")
		.closeSec()
		.openSec()
			.param("keys", new String[]{"reg", "register", "in"}, 
					"filters", new String[]{"out"}) 
			.run(pNode.getRun(pNode.CT.RUNS_ADD_CO_IN), "co_reg")
		.closeSec()
		.openSec()
			.param("keys", new String[]{"func"}, 
					"filters", new String[]{"func"}) 
			.run(pNode.getRun(pNode.CT.RUNS_ADD_CO_IN), "co_func")
		.closeSec()
		;
		
		
		
		
		
		
		pStandard stack_head = pStandard.newStandard("stack_head_abstract", "inst")
		.addInst("head_tile", "tile")
		.addCollec("script_com", String.class)
		.addCollec("script_arg", String.class)
		.addCollec("script_arg_class", String.class)
		.addCollecInst("tiles", "tile")
		.newRun("get_instruction_script", new nRun() {public Object get() { 
			if (instance.getInst("head_tile") != null) { 
				if (!instance.hasObject("run_script")) {
					Object[] script = instance.getInst("head_tile")
							.get("get_instruction_script", Object[].class);
					if (script != null) instance.addObject("run_script", script); }
				if (instance.hasObject("run_script")) 
					return instance.object("run_script"); }
			return null;
		}})
//		.newRun("get_next_id", new nRun() {public Object get() {
//			instance.obtainVar("tile_stack_next_id", (int)0);
//			instance.setVar("tile_stack_next_id", 
//					(int)(instance.getVar("tile_stack_next_id", Integer.class)+(int)1));
//			return instance.getVar("tile_stack_next_id", Integer.class);
//		}})
//		.newRun("reset_next_id", new nRun() {public void run() { 
//			instance.setVar("tile_stack_next_id", (int)0);
//		}})
		.newRun("build_script", new nRun() {public void run() {
			if (args.length < 2) return;
			Script script = arg(0,Script.class);
			boolean gui = arg(1,Boolean.class);
			pInstance head = instance.getInst("head_tile");
			if (head == null) return;
			if (!gui) {
				instance.get("get_instruction_script");
				ArrayList<pInstance> all_tile = 
						head.get("get_all_tile", ArrayList.class);
				for (pInstance s : Utl.duplic(all_tile)) s.clear(); }
			head.setData("gui", gui);
			instance.setObject("is_script", !gui);
			instance.setVar("script", !gui);
			if (gui) { app.addDelayEvent(1, new nRun() { public void run() {
				head.run("build_script", script); }}); }
		}})
		.newRun("build_saved_script", new nRun() {public void run() { 
			Script script = new Script();
			int com_nb = instance.getCollecSize("script_com");
			for (int i = 0 ; i < com_nb ; i++) {
				String com = instance.collecGet("script_com", i, String.class);
				String str_arg1 = instance.collecGet("script_arg", (i*3), String.class);
				String str_arg2 = instance.collecGet("script_arg", (i*3)+1, String.class);
				String str_arg3 = instance.collecGet("script_arg", (i*3)+2, String.class);
				String str_arg_class1 = instance.collecGet("script_arg_class", (i*3), String.class);
				String str_arg_class2 = instance.collecGet("script_arg_class", (i*3)+1, String.class);
				String str_arg_class3 = instance.collecGet("script_arg_class", (i*3)+2, String.class);
				Object arg1 = null, arg2 = null, arg3 = null;
				if (str_arg_class1 != null && str_arg_class1.length() > 0) {
					Class<?> arg_class1 = Utl.type_ref_class.get(str_arg_class1);
					arg1 = Utl.from_string(str_arg1, arg_class1); }
				if (str_arg_class2 != null && str_arg_class2.length() > 0) {
					Class<?> arg_class2 = Utl.type_ref_class.get(str_arg_class2);
					arg2 = Utl.from_string(str_arg2, arg_class2); }
				if (str_arg_class3 != null && str_arg_class3.length() > 0) {
					Class<?> arg_class3 = Utl.type_ref_class.get(str_arg_class3);
					arg3 = Utl.from_string(str_arg3, arg_class3); }
				if (arg3 != null) script.commande(com,arg1,arg2,arg3);
				else if (arg2 != null) script.commande(com,arg1,arg2);
				else if (arg1 != null) script.commande(com,arg1);
				else script.commande(com);
			}
			instance.run("build_script", script, instance.getVar("script", Boolean.class));
		}})
		.newRun("update_script", new nRun() {public void run() { 
			if (instance.getInst("head_tile") == null) return;
			if (instance.getVar("script", Boolean.class) && 
					!instance.object("is_script", Boolean.class)) {
				pInstance head = instance.getInst("head_tile");
				head.run("calc_script");
				Script script = head.object("script", Script.class);
				instance.run("build_script", script, false);
			} else if (!instance.getVar("script", Boolean.class) && 
					instance.object("is_script", Boolean.class)) {
				pInstance head = instance.getInst("head_tile");
				Script script = head.object("script", Script.class);
				instance.run("build_script", script, true);
			}
		}})
		.process()
		.commande(new nRun() {public void run() { 
			instance.addObject("is_script", false);
			nRun run_frame = new nRun(instance) {public void run() { 
				pInstance inst = (pInstance)builder;
				inst.setVar("tile_co_node_cnt", (int)0);
				
				if (inst.getInst("head_tile") != null && 
						!inst.object("is_script", Boolean.class)) {
					
//					inst.run("reset_next_id");
					
					inst.removeObject("run_script");
					
					pInstance head_tile = inst.getInst("head_tile");
					nWidgetGroup group = inst.object("group", nWidgetGroup.class);
					Vector2 p = group.get("ref").getLocalPos();
					Vector2 p2 = head_tile.object("group", nWidgetGroup.class).get("ref").getLocalPos();
					p.add(0,RS*0.1f);
					Vector2 f = new Vector2(p).sub(p2);
					if (f.len() > 0f) head_tile.object("group", nWidgetGroup.class).metode("move",f);
					head_tile.run("attract_plugged");
					
					head_tile.run("all_flag_recursion");
					Rectangle stack_bb = head_tile.get("get_bounding_box", Rectangle.class);
					Rectangle this_rect = group.get("selline")
							.getRectRelativeToParent(inst.sheet.sheet_ref);
					ArrayList<Rectangle> arr = new ArrayList<Rectangle>();
					arr.add(this_rect); arr.add(stack_bb);
					Rectangle bb = Utl.get_bounding_rect(arr);
					Vector2 r_pos = group.get("ref").getPosRelativeToParent(inst.sheet.sheet_ref);
					Vector2 sl_pos = new Vector2(bb.x,bb.y).sub(r_pos);
					group.get("selline").setRect(sl_pos.x,sl_pos.y,bb.width,bb.height);
				}
			}};
			instance.addObject("run_frame_stack_head", run_frame);
			instance.patch.addEventFrame(run_frame);
		}})
		.useSave().commande(new nRun() {public void run() { 
			if (instance.getInst("head_tile") != null) { 
				pInstance head = instance.getInst("head_tile");
				head.run("calc_script");
				Script script = head.object("script", Script.class);
				instance.getCollec("script_com").empty();
				instance.getCollec("script_arg").empty();
				instance.getCollec("script_arg_class").empty();
				for (ScriptCom sc : script.coms) {
					instance.getCollec("script_com").add(sc.com);
					String arg = "", arg_class = "";
					if (sc.arg1 != null) {
						arg = Utl.to_string(sc.arg1);
						arg_class = sc.arg1.getClass().getName(); } 
					instance.getCollec("script_arg").add(arg);
					instance.getCollec("script_arg_class").add(arg_class);
					arg = ""; arg_class = "";
					if (sc.arg2 != null) {
						arg = Utl.to_string(sc.arg2);
						arg_class = sc.arg2.getClass().getName(); }
					instance.getCollec("script_arg").add(arg);
					instance.getCollec("script_arg_class").add(arg_class);
					arg = ""; arg_class = "";
					if (sc.arg3 != null) {
						arg = Utl.to_string(sc.arg3);
						arg_class = sc.arg3.getClass().getName(); }
					instance.getCollec("script_arg").add(arg);
					instance.getCollec("script_arg_class").add(arg_class);
				}
			}
		}})
		.useClear().commande(new nRun() {public void run() { 
			instance.patch.removeEventFrame(
					instance.object("run_frame_stack_head", nRun.class));
			pInstance head = instance.getInst("head_tile");
			if (head == null) return;
			ArrayList<pInstance> all_tile = 
					head.get("get_all_tile", ArrayList.class);
			for (pInstance s : Utl.duplic(all_tile)) s.clear();
		}}).useInit()
		.getStand()
		;
		
		

		pNode.newNodeModel("branch", "tile")
		.append(stack_head)
		.process()
		.useLoad().commande(new nRun() {public void run() { 
			if (instance.getInst("head_tile") == null) {
				pSheet sheet = instance.sheet;
				pInstance t = sheet.newTile("branch_start");
				instance.setInst("head_tile", t);
				t.setInst("tile_node", instance);
				if (!instance.collecInstContains("tiles", t)) 
					instance.collecInstAdd("tiles", t);
			}
			if (instance.getInst("head_tile") != null) { 
				if (!instance.is_new) instance.run("build_saved_script"); }
			if (!instance.hasVar("branch_ref")) {
				instance.obtainVar("branch_ref", "branch_"+func_counter);
				func_counter++; }
			instance.patch.common_branchs.put(
					instance.getVar("branch_ref", String.class), instance);
		}})
		.useClear().commande(new nRun() {public void run() { 
			instance.patch.common_branchs.remove(instance);
		}}).useInit()
		.run(pNode.getRun(pNode.CT.RUNP_ADD_LABEL), "", (int)16)
		.commande(pNode.getCom(pNode.CT.COM_ADD_ROW))
		.openSec()
		.param("run", new nRun() {public void run() { 
			instance.patch.common_branchs.remove(instance);
			instance.patch.common_branchs.put(
					instance.getVar("branch_ref", String.class), instance);
		}})
		.run(pNode.getRun(pNode.CT.RUNP_VAR_STR_FIELD), "branch_ref", (int)16)
		.closeSec()
		.commande(pNode.getCom(pNode.CT.COM_ADD_ROW))
		.openSec()
		.param("run", new nRun() {public void run() { 
			instance.run("update_script"); }})
		.param("def", false, "height", 3f, "scale_min_big", true) 
		.run(pNode.getRun(pNode.CT.RUNP_VAR_BOO_SWITCH), 
				"script", "script", (int)16).closeSec()
		.getStand()
		;
		
		
		
		
		

		pNode.newNodeModel("function", "tile")
		.append(stack_head)
		.append(exec_context)
		.process()
		.useLoad().commande(new nRun() {public void run() { 
			if (instance.getInst("head_tile") == null) {
				pSheet sheet = instance.sheet;
				pInstance t = sheet.newTile("stack_start");
				instance.setInst("head_tile", t);
				t.setInst("tile_node", instance);
				if (!instance.collecInstContains("tiles", t)) 
					instance.collecInstAdd("tiles", t);
			}
			if (instance.getInst("head_tile") != null) { 
				if (!instance.is_new) instance.run("build_saved_script"); }
			if (!instance.hasVar("func_ref")) {
				instance.obtainVar("func_ref", "function_"+func_counter);
				func_counter++; }
			instance.patch.common_functions.put(
					instance.getVar("func_ref", String.class), instance);
		}})
		.useClear().commande(new nRun() {public void run() { 
			instance.patch.common_functions.remove(instance);
		}}).useInit()
		.run(pNode.getRun(pNode.CT.RUNP_ADD_LABEL), "", (int)16)
		.commande(pNode.getCom(pNode.CT.COM_ADD_ROW))
		.openSec()
		.param("run", new nRun() {public void run() { 
			instance.patch.common_functions.remove(instance);
			instance.patch.common_functions.put(
					instance.getVar("func_ref", String.class), instance);
		}})
		.run(pNode.getRun(pNode.CT.RUNP_VAR_STR_FIELD), "func_ref", (int)16)
		.closeSec()
		.commande(pNode.getCom(pNode.CT.COM_ADD_ROW))
		.openSec().param("height", 0.1f)
		.run(pNode.getRun(pNode.CT.RUNP_ADD_LABEL), "", (int)32).closeSec()
		.commande(pNode.getCom(pNode.CT.COM_ADD_ROW))
		.openSec()
		.param("run", new nRun() {public void run() { 
			instance.run("update_script"); }})
		.param("def", false, "height", 3f, "scale_min_big", true) 
		.run(pNode.getRun(pNode.CT.RUNP_VAR_BOO_SWITCH), 
				"script", "script", (int)16).closeSec()
		.getStand()
//		.openSec()
//		.param("keys", new String[] {"tile_pop"}, "filters", new String[] {"tile_pop"}) 
//		.run(pNode.getRun(pNode.CT.RUNS_ADD_CHAIN_START_PLUG), "tile_pop", "bottom")
//		.closeSec()
		;
		

		
		
		
		
		
		
		pNode.newNodeModel("register", "tile").process()
		.openSec() 
		.run(pNode.getRun(pNode.CT.RUNP_ADD_LABEL), "REG", (int)16).closeSec()
		.getStand()
		.newRun("obtain_all_reg_of_model", new nRun() {public Object get() { 
			String model_ref = arg(0, String.class);
			ArrayList<String> allkey = new ArrayList<String>();
			if (model_ref == null) return allkey;
			pInstance co_reg = instance.get("get_co", pInstance.class, "co_reg");
			if (co_reg == null) return allkey;
			Object op_reg = co_reg.get("obtain_all_nodes", Object.class);
			if (op_reg == null) return allkey;
			ArrayList<Object> reg_prov = (ArrayList)op_reg;
			for (Object or : reg_prov) if (or instanceof pInstance) {
				pInstance reg = (pInstance)or;
				if (reg != null) {
					if (reg.stand.ref.equals("node_model_"+model_ref) && 
							reg.hasVar("reg_ref")) {
						allkey.add(reg.getVar("reg_ref", String.class));
					} else if (reg.stand.ref.equals("node_model_register")) {
						ArrayList<String> ak = reg.get("obtain_all_reg_of_model", 
								ArrayList.class, model_ref);
						if (ak != null) {
							for (String nk : ak) 
								if (!Utl.contains(allkey, nk)) allkey.add(nk);
						}
					}
				}
			}
			return allkey;
		}})
		.newRun("obtain_reg", new nRun() {public Object get() { 
			String reg_ref = arg(0, String.class);
			ArrayList<String> allkey = new ArrayList<String>();
			if (reg_ref == null) return null;
			pInstance co_reg = instance.get("get_co", pInstance.class, "co_reg");
			if (co_reg == null) return null;
			Object op_reg = co_reg.get("obtain_all_nodes", Object.class);
			if (op_reg == null) return null;
			ArrayList<Object> reg_prov = (ArrayList)op_reg;
			for (Object or : reg_prov) if (or instanceof pInstance) {
				pInstance reg = (pInstance)or;
				if (reg != null) {
					if (reg.hasVar("reg_ref") && 
							reg.getVar("reg_ref", String.class).equals(reg_ref)) {
						return reg;
					} else if (reg.stand.ref.equals("node_model_register")) {
						pInstance reg_reg = reg.get("obtain_reg", pInstance.class, reg_ref);
						if (reg_reg != null) return reg_reg;
					}
				}
			}
			return null;
		}})
		.openSec()
		.param("keys", new String[]{"reg", "in"}, "filters", new String[]{"reg", "out"}) 
			.run(pNode.getRun(pNode.CT.RUNS_ADD_CO_IN), "co_reg")
		.closeSec()
		.openSec()
		.param("keys", new String[]{"register", "out"}, 
				"filters", new String[]{"register", "in"}) 
		.run(pNode.getRun(pNode.CT.RUNS_ADD_CO_OUT), "co_register").closeSec()
		;

		pNode.newNodeModel("reg_in", "tile").process()
		.openSec()//.param("width", (int)6) 
		.run(pNode.getRun(pNode.CT.RUNP_VAR_STR_FIELD), "reg_ref", (int)10).closeSec()
		.getStand()
		.openSec().param("keys", new String[] {"all"}, "filters", new String[] {}) 
		.run(pNode.getRun(pNode.CT.RUNS_ADD_CO_IN), "co_in").closeSec()
		.openSec()
		.param("keys", new String[]{"reg", "out"}, "filters", new String[]{"reg", "in"}) 
		.run(pNode.getRun(pNode.CT.RUNS_ADD_CO_OUT), "co_reg").closeSec()
		;

		pNode.newNodeModel("reg_out", "tile").process()
		.openSec()//.param("width", (int)6) 
		.run(pNode.getRun(pNode.CT.RUNP_VAR_STR_FIELD), "reg_ref", (int)10).closeSec()
		.getStand()
		.openSec().param("keys", new String[] {"all"}, "filters", new String[] {}) 
		.run(pNode.getRun(pNode.CT.RUNS_ADD_CO_OUT), "co_out").closeSec()
		.openSec()
		.param("keys", new String[]{"reg", "out"}, "filters", new String[]{"reg", "in"}) 
		.run(pNode.getRun(pNode.CT.RUNS_ADD_CO_OUT), "co_reg").closeSec()
		;
		
		
		
	}
	
	static class ScriptCom {
		public String com;
		public Object arg1 = null, arg2 = null, arg3 = null;
		public ScriptCom(String c) { com = c; }
		public ScriptCom(String c, Object a1) { com = c; arg1 = a1; }
		public ScriptCom(String c, Object a1, Object a2) { com = c; arg1 = a1; arg2 = a2; }
		public ScriptCom(String c, Object a1, Object a2, Object a3) { com = c; arg1 = a1; arg2 = a2; arg3 = a3; }
		public String to_string() { 
			String s = ""+com+" ( "+Utl.to_string(arg1)+
					" , "+Utl.to_string(arg2)+
					" , "+Utl.to_string(arg3)+" ) \n";
			return s; }
	}
	static class Script {
		public ArrayList<ScriptCom> coms = new ArrayList<ScriptCom>();
		public Script() {}
		public void clear() { coms.clear(); }
		public Script commande(String c) {
			ScriptCom sc = new ScriptCom(c); coms.add(sc); return this; }
		public Script commande(String c, Object a1) {
			ScriptCom sc = new ScriptCom(c,a1); coms.add(sc); return this; }
		public Script commande(String c, Object a1, Object a2) {
			ScriptCom sc = new ScriptCom(c,a1,a2); coms.add(sc); return this; }
		public Script commande(String c, Object a1, Object a2, Object a3) {
			ScriptCom sc = new ScriptCom(c,a1,a2,a3); coms.add(sc); return this; }
		public String to_string() { 
			String s = "";
			for (ScriptCom sc : coms) s += sc.to_string();
			return s;
		}
	}
	
	
	
	public static void build_tiles(Applet app) {

		pStandard stack_start_abstract = pStandard.newStandard("stack_start_abstract", "inst");
		stack_start_abstract
		.newRun("build_script", new nRun() {public void run() {
			Script script = arg(0,Script.class);
			instance.setObject("current_build", instance);
			ArrayList<pInstance> last_build = instance.object("last_build", ArrayList.class);
			last_build.clear();
			instance.setObject("last_build", last_build);
			Script new_script = new Script();
			instance.setObject("script", new_script);
			for (ScriptCom sc : script.coms) {
				if (sc.arg3 != null) instance.get(sc.com, pInstance.class, sc.arg1, sc.arg2, sc.arg3);
				else if (sc.arg2 != null) instance.get(sc.com, pInstance.class, sc.arg1, sc.arg2);
				else if (sc.arg1 != null) instance.get(sc.com, pInstance.class, sc.arg1);
				else instance.get(sc.com, pInstance.class);
			}
			script.clear();
		}})
		.newRun("calc_script_iter", new nRun() {public void run() {
			pInstance to_scan = arg(0,pInstance.class);
			Script script = instance.object("script", Script.class);
			ArrayList<pInstance> added_tile = 
					instance.object("added_tile", ArrayList.class);
			if (!added_tile.contains(to_scan)) {
				added_tile.add(to_scan);
				instance.setObject("added_tile", added_tile);
				for (pInstance p : to_scan.collecInstAll("plugs")) 
						if (p.getInst("plugged") != null) {
					pInstance n = p.getInst("plugged").getInst("tile");
					added_tile = instance.object("added_tile", ArrayList.class);
					if (!added_tile.contains(n)) {
						script.commande("add_at", p.getDataStr("ref"), 
								pTile.tile_models.getKey(n.stand), 
								p.getInst("plugged").getDataStr("ref"));

						for (Map.Entry<String,Object> me : n.var_vals.entrySet()) {
							script.commande("set_var", me.getKey(), 
									Utl.copy(me.getValue())); }
						
						instance.run("calc_script_iter", n);
						script.commande("get_last");
					}
				}
			}
			instance.setObject("script", script);
		}})
		.newRun("calc_script", new nRun() {public void run() {
			ArrayList<pInstance> added_tile = 
					instance.object("added_tile", ArrayList.class);
			added_tile.clear();
			instance.setObject("added_tile", added_tile);
			Script script = new Script();
			instance.setObject("script", script);
			instance.run("calc_script_iter", instance); 
		}})
		.newRun("get_all_tile_iter", new nRun() {public void run() {
			pInstance to_scan = arg(0,pInstance.class);
			ArrayList<pInstance> added_tile = 
					instance.object("added_tile", ArrayList.class);
			if (!added_tile.contains(to_scan)) {
				added_tile.add(to_scan);
				instance.setObject("added_tile", added_tile);
				for (pInstance p : to_scan.collecInstAll("plugs")) 
						if (p.getInst("plugged") != null) {
					pInstance n = p.getInst("plugged").getInst("tile");
					instance.run("get_all_tile_iter", n); } }
		}})
		.newRun("get_all_tile", new nRun() {public Object get() {
			ArrayList<pInstance> added_tile = 
					instance.object("added_tile", ArrayList.class);
			added_tile.clear();
			instance.setObject("added_tile", added_tile);
			instance.run("get_all_tile_iter", instance);
			added_tile = instance.object("added_tile", ArrayList.class);
			added_tile.remove(instance); 
			return Utl.duplic(added_tile);
		}})
		.newRun("get_last", new nRun() {public Object get() {
			ArrayList<pInstance> last_build = instance.object("last_build", ArrayList.class);
			if (last_build.size() == 0) return instance;
			Script script = instance.object("script", Script.class);
			script.commande("get_last");
			instance.setObject("script", script);
			
			pInstance current_build = last_build.remove(last_build.size()-1);
			instance.setObject("last_build", last_build);
			instance.setObject("current_build", current_build);
			return instance;
		}})
		.newRun("set_var", new nRun() {public Object get() {
			if (args.length != 2) return instance;
			String var_ref = arg(0, String.class);
			Object data = arg(1, Object.class);

			Script script = instance.object("script", Script.class);
			script.commande("set_var", var_ref, data);
			instance.setObject("script", script);
			
			pInstance current_build = instance.object("current_build", pInstance.class);
			current_build.setVar(var_ref, data);
			return instance;
		}})
		.newRun("add_at", new nRun() {public Object get() {
			if (args.length != 3) return instance;
			String this_plug_ref = arg(0, String.class);
			String model_ref = arg(1, String.class);
			String model_plug_ref = arg(2, String.class);
			
			Script script = instance.object("script", Script.class);
			script.commande("add_at", this_plug_ref, model_ref, model_plug_ref);
			instance.setObject("script", script);
			
			pSheet sheet = instance.sheet;
			pInstance t = sheet.newTile(model_ref, instance.getDataBoo("gui"));
			t.setInst("tile_node", instance.getInst("tile_node"));
			if (!instance.getInst("tile_node").collecInstContains("tiles", t)) 
				instance.getInst("tile_node").collecInstAdd("tiles", t);
			pInstance current_build = instance.object("current_build", pInstance.class);
			if (current_build == null) current_build = instance;
			ArrayList<pInstance> last_build = instance.object("last_build", ArrayList.class);
			last_build.add(current_build);
			instance.setObject("last_build", last_build);
			pInstance inst_plug = current_build.get("get_plug", pInstance.class, this_plug_ref);
			pInstance t_plug = t.get("get_plug", pInstance.class, model_plug_ref);
			if (inst_plug != null && t_plug != null) {
				inst_plug.run("link_to", t_plug);
			}
			instance.setObject("current_build", t);
			return instance;
		}})
		;
		
		
		
		
		
		
		pTile.newUnpoppableTileModel("branch_start") 
		.append(stack_start_abstract)
		.process()
		.commande(new nRun() {public void run() { 
			instance.addObject("current_build", instance);
			ArrayList<pInstance> last_build = new ArrayList<pInstance>();
			instance.addObject("last_build", last_build);
			Script script = new Script();
			instance.addObject("script", script);
			ArrayList<pInstance> added_tile = new ArrayList<pInstance>();
			instance.addObject("added_tile", added_tile);
		}})
		.run(pTile.getRun(CT.OBTAIN_VAR), "watch", "")
		.openSec()
		.param("ref", "watch_watch", "var_link_ref", "watch", 
				"var_link_class", String.class.getName())
		.param("width", (int)16)
		.commande(pTile.getCom(CT.ADD_WATCH))
		.closeSec()
		.getStand()
		.run(pTile.getRun(pTile.CT.ADD_OBTAIN_PLUG), "in", "all")
		;
		
		
		
		
		
		
		pTile.newUnpoppableTileModel("stack_start")
		.append(stack_start_abstract)
		.process()
		.commande(new nRun() {public void run() { 
			instance.addObject("current_build", instance);
			ArrayList<pInstance> last_build = new ArrayList<pInstance>();
			instance.addObject("last_build", last_build);
			Script script = new Script();
			instance.addObject("script", script);
			ArrayList<pInstance> added_tile = new ArrayList<pInstance>();
			instance.addObject("added_tile", added_tile);
		}})
		.openSec()
		.run(pTile.getRun(CT.ADD_PLUG), "run_out", "top")
		.closeSec()
		.getStand()
		;

		PlugDef pd = new PlugDef("run_out", new String[] {"run","out"}, 
				new String[] {"run","in"});
		pTile.tile_model_plugs.get("tile_model_stack_start").add(pd);
		
	}
	
	
	
	

	public static void build_stack_editor(Applet app) {

		float RS = nGUI.book.RS;

		pNode.newChainnedNodeModel("stack_editor")
		.process()
			.useLoad().commande(new nRun() {public void run() {
				nInterface interf = instance.object("interf", nInterface.class);

				interf.add_col();
				interf.add_row();
				interf.add_row_label(9, "Stack Editor");
				
				interf.set_param("entry_height","0.7");
				interf.add_row();
				nWidgetGroup list = interf.add_treelist(9,8);
				interf.set_param("entry_height","1");
				nRun list_run = new nRun(instance) { public void run() {
					pInstance inst = (pInstance)builder;
					if (inst.hasObject("doing_list_run") && 
							inst.object("doing_list_run", Boolean.class)) return;
					inst.setObject("doing_list_run", true);
					app.addDelayEvent(1,new nRun(inst) { public void run() {
						pInstance inst = (pInstance)builder;
						inst.setObject("doing_list_run", false);
						pInstance head = inst.get("get_chain_head", pInstance.class);
						if (head == null || head.getInst("head_tile") == null) return;
						pInstance head_tile = head.getInst("head_tile");
						head_tile.run("calc_script");
						Script script = head_tile.object("script", Script.class);
						if (script == null) return;
						interf.change_current_list(list);
						for (ScriptCom sc : script.coms) {
							interf.add_list_entry(sc.to_string());
							interf.go_up_tree();
						}
					}});
				}};
				
				interf.add_row();
				interf.add_row_label(9, "");
				interf.add_row();
				interf.add_row_label(2, "");
				nWidget add_at_w = interf.add_row_trigg(5,"add_at");
				interf.add_row_label(2, "");
				interf.add_row();
				nWidget add_at_out_pk_w = interf.add_row_trigg(2,"out");
				nWidget add_at_out_targ_w = interf.add_row_label(7, "");
				interf.add_row();
				nWidget add_at_mod_pk_w = interf.add_row_trigg(1,"mod");
				nWidget add_at_mod_targ_w = interf.add_row_label(4, "");
				nWidget add_at_in_targ_w = interf.add_row_label(4, "");
				
				nRun run_pk = new nRun() { public void run() {
					nWidget trigg_w = arg(0, nWidget.class);
					nWidget lab_w = arg(1, nWidget.class);
					ArrayList<String> arr = arg(2, ArrayList.class);
					instance.patch.patch_dropmenu.metode("clear_entrys"); 
					for (String par : arr) {
						nWidget w1 = (nWidget)instance.patch.patch_dropmenu
								.metodeGet("add_entry_custom", par, RS*6f, RS*2f/3f);
						w1.addEventTrigger(new nRun(par) { public void run() {
							String targ = arg(0, String.class);
							lab_w.setText(targ); }}); }
					instance.patch.patch_dropmenu.metode("open", trigg_w); 
				}};
				nRun run_add_at_out_pk = new nRun(instance) { public void run() {
					pInstance inst = (pInstance)builder;
					pInstance head = inst.get("get_chain_head", pInstance.class);
					if (head == null || head.getInst("head_tile") == null) return;
					pInstance head_tile = head.getInst("head_tile");
					pInstance current_build = 
							head_tile.object("current_build", pInstance.class);
					ArrayList<String> arr = new ArrayList<String>();
					for (pInstance c : current_build.collecInstAll("plugs")) {
						if (c.getInst("plugged") == null) {
							arr.add(c.getVar("ref", String.class));
						}
					}
					run_pk.do_run(inst,add_at_out_pk_w, add_at_out_targ_w, arr);
				}};
				add_at_out_pk_w.addEventTrigger(run_add_at_out_pk);

				nRun run_add_at_mod_pk = new nRun(instance) { public void run() {
					pInstance inst = (pInstance)builder;
					pInstance head = inst.get("get_chain_head", pInstance.class);
					if (head == null || head.getInst("head_tile") == null) return;
					pInstance head_tile = head.getInst("head_tile");
					pInstance current_build = 
							head_tile.object("current_build", pInstance.class);
					String out_ref = add_at_out_targ_w.getText();
					pInstance out_plug = current_build.get("get_plug", 
							pInstance.class, out_ref);
					if (out_plug == null) {
						add_at_out_targ_w.setText("");
						add_at_mod_targ_w.setText("");
						add_at_in_targ_w.setText("");
						return;
					}
					
					ArrayList<String> arr = new ArrayList<String>();
					PlugDef out_pd = null;
					for (PlugDef p : pTile.getTileModelPlugs(current_build.stand.ref)) 
						if (p.ref.equals(out_ref)) { out_pd = p; break; }
					if (out_pd == null) {
						Utl.logn("ERROR plug run trigg r"); }
					for (String tile_model : pTile.tile_models.allKey()) 
						if (!Utl.contains(pTile.not_poppable_models, tile_model)) {
						pStandard tile_model_stan = pTile.tile_models.get(tile_model);
						for (PlugDef pd : pTile.getTileModelPlugs(tile_model_stan)) {
							if (pTile.key_filter_compatibility(out_pd.keys, out_pd.filters, 
									pd.keys, pd.filters)) {
								String r = pd.ref;
								arr.add(tile_model+" "+r);
							}
						}
					}

					instance.patch.patch_dropmenu.metode("clear_entrys"); 
					for (String par : arr) {
						nWidget w1 = (nWidget)instance.patch.patch_dropmenu
								.metodeGet("add_entry_custom", par, RS*6f, RS*2f/3f);
						w1.addEventTrigger(new nRun(par) { public void run() {
							String targ = arg(0, String.class);
							String[] tg = Utl.split(targ, ' ');
							if (!(tg.length == 2)) return;
							add_at_mod_targ_w.setText(tg[0]);
							add_at_in_targ_w.setText(tg[1]);
						}}); }
					instance.patch.patch_dropmenu.metode("open", add_at_mod_pk_w); 
					
				}};
				add_at_mod_pk_w.addEventTrigger(run_add_at_mod_pk);
				
				nRun run_add_at = new nRun(instance) { public void run() {
					pInstance inst = (pInstance)builder;
					pInstance head = inst.get("get_chain_head", pInstance.class);
					if (head == null || head.getInst("head_tile") == null) return;
					pInstance head_tile = head.getInst("head_tile");
					String out_ref = add_at_out_targ_w.getText();
					String mod_ref = add_at_mod_targ_w.getText();
					String in_ref = add_at_in_targ_w.getText();
					head_tile.get("add_at", pInstance.class, out_ref, mod_ref, in_ref);
					add_at_out_targ_w.setText("");
					add_at_mod_targ_w.setText("");
					add_at_in_targ_w.setText("");
					list_run.do_run(inst);
				}};
				add_at_w.addEventTrigger(run_add_at);
				
				
//				interf.add_row();
//				interf.add_row_trigg(5,"set_var", new nRun(instance) { public void run() {
//					pInstance inst = (pInstance)builder;
//					pInstance head = inst.get("get_chain_head", pInstance.class);
//					if (head == null || head.getInst("head_tile") == null) return;
//					pInstance head_tile = head.getInst("head_tile");
//					head_tile.get("set_var", pInstance.class);
//					add_at_out_targ_w.setText("");
//					add_at_mod_targ_w.setText("");
//					add_at_in_targ_w.setText("");
//					list_run.do_run(inst);
//				}});
//				interf.add_row_label(4, "");
				interf.add_row();
				interf.add_row_trigg(4,"get_last", new nRun(instance) { public void run() {
					pInstance inst = (pInstance)builder;
					pInstance head = inst.get("get_chain_head", pInstance.class);
					if (head == null || head.getInst("head_tile") == null) return;
					pInstance head_tile = head.getInst("head_tile");
					head_tile.get("get_last", pInstance.class);
					add_at_out_targ_w.setText("");
					add_at_mod_targ_w.setText("");
					add_at_in_targ_w.setText("");
					list_run.do_run(inst);
				}});
				interf.add_row_label(1, "");
				interf.add_row_trigg(4,"del_last_com", new nRun(instance) { public void run() {
					pInstance inst = (pInstance)builder;
					pInstance head = inst.get("get_chain_head", pInstance.class);
					if (head == null || head.getInst("head_tile") == null) return;
					pInstance head_tile = head.getInst("head_tile");
					head_tile.run("calc_script");
					Script script = head_tile.object("script", Script.class);
					if (script == null) return;
					if (script.coms.size() > 0) script.coms.remove(
							script.coms.get(script.coms.size() - 1));
					head.run("build_script", script, !head.getVar("script", Boolean.class));
					add_at_out_targ_w.setText("");
					add_at_mod_targ_w.setText("");
					add_at_in_targ_w.setText("");
					list_run.do_run(inst);
				}});

				interf.add_row();
				interf.add_row_label(9, "");
				interf.add_row();
				interf.add_row_label(1, "");
				interf.add_row_trigg(7,"rebuild", new nRun(instance) { public void run() {
					pInstance inst = (pInstance)builder;
					pInstance head = inst.get("get_chain_head", pInstance.class);
					if (head == null || head.getInst("head_tile") == null) return;
					pInstance head_tile = head.getInst("head_tile");
					head_tile.run("calc_script");
					Script script = head_tile.object("script", Script.class);
					head.run("build_script", script, !head.getVar("script", Boolean.class));
					list_run.do_run(inst);
				}});
				interf.add_row_label(1, "");
				
				
				list_run.do_run(instance);
				
			}})
			.getStand()
		.openSec()
		.param("keys", new String[] {"tile_pop"}, "filters", new String[] {"tile_pop"}) 
		.run(pNode.getRun(pNode.CT.RUNS_ADD_CHAIN_PLUGS), "tile_pop", "bottom")
		.closeSec()
		;
		
		
	}
	
	
	
	
}
