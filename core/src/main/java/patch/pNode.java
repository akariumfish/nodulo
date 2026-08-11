package patch;

import data.*;
import gui.*;
import util.*;
import aa_nodulo.*;
import app.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;


public class pNode {

	public static final int LINK_HIGHLIGHT_TEMP = 10;

	public static float BRIC_GRID_SIZE = 30f;
	public static float BRIC_GRID_OVER = 3f;

	public static float SCALE_MIN_BIG = 0.15f;
	public static float DEF_SCALE_MIN = 0.5f;
	public static float DEF_SCALE_MAX = 20f;

	public static float LINKED_PLUG_REDUC_FACT = 30f;
	
	public static void build() {
		
		BRIC_GRID_SIZE = nGUI.book.RS;
		BRIC_GRID_OVER = BRIC_GRID_SIZE / 10f;
		
		build_book();
		
		build_standard();
		
		build_coms();

		build_nodes();

	}
	

	public static void build_nodes() { //PlaneApplet app

		float RS = PlaneApplet.app.gui.book.RS;
		

		

		newNodeModel("boolean", "var")
		.process() 
			.openSec()
				.param("run", new nRun() {public void run() {
					if (instance.getVar("auto", Boolean.class)) {
						PlaneApplet.app.addDelayEvent(1, new nRun(instance) {public void run() {
							pInstance inst = (pInstance)builder;
							pInstance co = inst.get("get_co", pInstance.class, "out");
							co.run("send", inst.getVar("var", Boolean.class)); }}); } }})
				.param("height", 2f, "scale_min", 0.0f) 
				.run(getRun(CT.RUNP_VAR_BOO_SWITCH), "var", "state", (int)8)
			.closeSec()
			.commande(getCom(CT.COM_ADD_ROW))
			.openSec()
				.param("def", true) 
				.run(getRun(CT.RUNP_VAR_BOO_SWITCH), "auto", "auto", (int)6) 
			.closeSec()
			.openSec()
				.param("run", new nRun() {public void run() {
					pInstance co = instance.get("get_co", pInstance.class, "out");
					co.run("send", instance.getVar("var", Boolean.class)); }})
				.run(getRun(CT.RUNP_ADD_TRIGG), "send", "send", (int)6)
			.closeSec()
			.getStand()
		.openSec()
			.param("event_receive", new nRun() {public void run() {
				Object r = arg(0,Object.class);
				pInstance node = instance.object("node", pInstance.class);
				boolean change = false;
				if (r instanceof Boolean) {
					if (!(node.getVar("var", Boolean.class) == (Boolean)r)) change = true;
					node.setVar("var", (Boolean)r);
				}
				if (change && node.getVar("auto", Boolean.class)) {
					pInstance co = node.get("get_co", pInstance.class, "out");
					co.run("send", node.getVar("var", Boolean.class));
				}
			}})
			.param("keys", new String[] {"var","boo"}, "filters", new String[] {"var","boo"})
			.run(getRun(CT.RUNS_ADD_CO_IN), "in")
		.closeSec()
		.openSec()	
			.param("offer", new nRun() {public Object get() {
				pInstance node = instance.object("node", pInstance.class);
				return node.getVar("var", Boolean.class);
			}})
			.param("keys", new String[] {"var","boo"}, "filters", new String[] {"var","boo"})
			.run(getRun(CT.RUNS_ADD_CO_OUT), "out")
		.closeSec()
		;

		newNodeModel("not")
		.process()
		.run(getRun(CT.RUNP_ADD_LABEL), "!", (int)10)
		.getStand()
		.openSec().param("event_receive", new nRun() {public void run() {
			pInstance node = instance.object("node", pInstance.class);
			Object r = arg(0,Object.class); 
			pInstance co = node.get("get_co", pInstance.class, "out");
			if (r instanceof Boolean) {
				boolean b = (Boolean)r; b = !b; co.run("send", b); }
		}}, "text", "!")
		.param("keys", new String[] {"var","boo"}, "filters", new String[] {"var","boo"}) 
		.run(getRun(CT.RUNS_ADD_CO_IN), "in").closeSec()
		.param("offer", new nRun() {public Object get() {
			pInstance node = instance.object("node", pInstance.class);
			pInstance co = node.get("get_co", pInstance.class, "in");
			Object r = co.get("obtain");
			if (r instanceof Boolean) {
				boolean b = (Boolean)r; b = !b; return b; }
			return null;
		}})
		.param("keys", new String[] {"var","boo"}, "filters", new String[] {"var","boo"}) 
		.run(getRun(CT.RUNS_ADD_CO_OUT), "out");
		
		
		
		
		
		
		
		newNodeModel("bang").process()
		.openSec()
			.param("run", new nRun() {public void run() {
				pInstance co = instance.get("get_co", pInstance.class, "out");
				co.run("send", true); }}, "height", 2f, "scale_min", 0.0f)
			.run(getRun(CT.RUNP_ADD_TRIGG), "bang", "bang", (int)8)
		.closeSec()
		.getStand()
		.param("keys", new String[] {"bang"}, "filters", new String[] {}) 
		.run(getRun(CT.RUNS_ADD_CO_OUT), "out")
		;
		

		newNodeModel("gate")
		.openSec()
			.param("event_link", new nRun() {public void run() {
				pInstance node = instance.object("node", pInstance.class);
				pInstance co_out = node.get("get_co", pInstance.class, "out");
				if (node.hasVar("state") && node.getVar("state", Boolean.class)) 
					co_out.run("run_event_link_from_node");
			}})
			.param("event_receive", new nRun() {public void run() {
				pInstance node = instance.object("node", pInstance.class);
				if (node.hasVar("state") && node.getVar("state", Boolean.class)) {
//					Object r = arg(0,Object.class);
					pInstance co = node.get("get_co", pInstance.class, "out");
					co.run("send", args); } }})
			.param("keys", new String[] {"all"}, "filters", new String[] {})  
			.run(getRun(CT.RUNS_ADD_CO_IN), "in")
		.closeSec()
		.openSec()
			.param("event_receive", new nRun() {public void run() {
				pInstance node = instance.object("node", pInstance.class);
				Object r = arg(0,Object.class); 
				if (r instanceof Boolean) { boolean b = (Boolean)r; node.setVar("state",b); }
//				else { node.setVar("state", !node.getVar("state",Boolean.class)); }
			}})
			.param("keys", new String[] {"var","boo"}, "filters", new String[] {"var","boo"}) 
			.run(getRun(CT.RUNS_ADD_CO_IN), "set")
		.closeSec()
		.openSec()
			.param("event_link", new nRun() {public void run() {
				pInstance node = instance.object("node", pInstance.class);
				pInstance co_in = node.get("get_co", pInstance.class, "in");
				if (node.hasVar("state") && node.getVar("state", Boolean.class)) 
					co_in.run("run_event_link_from_node");
			}})
			.param("offer", new nRun() {public Object get() {
				pInstance node = instance.object("node", pInstance.class);
				if (node.hasVar("state") && node.getVar("state", Boolean.class)) {
					pInstance co = node.get("get_co", pInstance.class, "in");
					return co.get("obtain");
				} else return null;
			}})
			.param("key", "all")
			.param("keys", new String[] {"all"}, "filters", new String[] {})
			.run(getRun(CT.RUNS_ADD_CO_OUT), "out")
		.closeSec()
		.process()
		.openSec()
			.param("run", new nRun() {public void run() {
//				pInstance bric = instance.object("bric", pInstance.class);
				pInstance co_in = instance.get("get_co", pInstance.class, "in");
				pInstance co_out = instance.get("get_co", pInstance.class, "out");
				if (co_in != null) co_in.run("run_event_link_from_bric"); 
				if (co_out != null) co_out.run("run_event_link_from_bric");
			}})
			.param("text", "gate", "width", (int)8, "height", 2f, "def", true) 
			.run(getRun(CT.RUNP_VAR_BOO_SWITCH), "state")  
		.closeSec()
		;

		
		
		
		
		

		newNodeModel("to").process()
		.useInit().commande(new nRun() {public void run() {
			instance.patch.node_to.add(instance);
		}})
		.useClear().commande(new nRun() {public void run() {
			instance.patch.node_to.remove(instance);
		}}).useInit()
		.openSec()
			.param("text", "this: ", "width", (int)8, "def", "to")
			.run(pNode.getRun(CT.RUNP_VAR_STR_LAB_FIELD), "this_ref")
		.closeSec()
		.commande(getCom(CT.COM_ADD_ROW))
		.openSec()
			.param("text", "target: ", "width", (int)8)
			.run(pNode.getRun(CT.RUNP_VAR_STR_LAB_FIELD), "target_ref")
		.closeSec()
		.openSec()
			.param("run", new nRun() {public void run() {
				nWidget trigg_w = instance.get("get_mapped_widget", nWidget.class, 
						"trigg_dropm_targ");
				if (trigg_w == null) return;
				ArrayList<String> targs = new ArrayList<String>();
				for (pInstance t : instance.patch.node_from) {
					String r = t.getVar("this_ref", String.class);
					if (r != null) targs.add(r);
				}
//				instance.patch.patch_dropmenu.metode("clear_entrys");
				for (String rf : targs) {
					
					nGUI.add_dropmenu_entry(rf, new nRun(instance) { public void run() {
						((pInstance)builder).setVar("target_ref", rf); }}); 
					
//					nWidget w1 = (nWidget)instance.patch.patch_dropmenu
//							.metodeGet("add_entry_custom", rf, RS*6f, RS*2f/3f);
//					w1.addEventTrigger(new nRun(instance) { public void run() {
//						((pInstance)builder).setVar("target_ref", rf); 
//					}}); 
				}
//				instance.patch.patch_dropmenu.metode("open", trigg_w); 
				nGUI.open_dropmenu(trigg_w);
			}})
			.run(pNode.getRun(CT.RUNP_ADD_TRIGG), "trigg_dropm_targ", "Pk", (int)2)
		.closeSec()
		.getStand()
//		.param("offer", new nRun() {public Object get() {
//			pInstance node = instance.object("node", pInstance.class);
//			String targ_ref = node.getVar("target_ref", String.class);
//			for (pInstance t : instance.patch.node_from) {
//				String r = t.getVar("this_ref", String.class);
//				if (r != null && r.equals(targ_ref)) {
//					pInstance co = t.get("get_co", pInstance.class, "out");
//					if (co != null) return co.get("obtain");
//				}
//			}
//			return null;
//		}})
//		.param("offer_all_nodes", new nRun() {public Object get() {
//			pInstance node = instance.object("node", pInstance.class);
//			String targ_ref = node.getVar("target_ref", String.class);
//			for (pInstance t : instance.patch.node_from) {
//				String r = t.getVar("this_ref", String.class);
//				if (r != null && r.equals(targ_ref)) {
//					pInstance co = t.get("get_co", pInstance.class, "out");
//					if (co != null) {
////						app.log("this_ref "+r);
//						return co.get("obtain_all_nodes", Object.class);
//					}
//				}
//			}
//			return null;
//		}})
//		.param("offer_node", new nRun() {public Object get() {
//			pInstance node = instance.object("node", pInstance.class);
//			String targ_ref = node.getVar("target_ref", String.class);
//			for (pInstance t : instance.patch.node_from) {
//				String r = t.getVar("this_ref", String.class);
//				if (r != null && r.equals(targ_ref)) {
//					pInstance co = t.get("get_co", pInstance.class, "out");
//					if (co != null) {
//						return co.get("obtain_node");
//					}
//				}
//			}
//			return null;
//		}})
		.param("event_receive", new nRun() {public void run() {
			pInstance node = instance.object("node", pInstance.class);
			Object r = arg(0,Object.class); 
			String targ_ref = node.getVar("target_ref", String.class);
			for (pInstance t : instance.patch.node_from) {
				String rf = t.getVar("this_ref", String.class);
				if (rf != null && rf.equals(targ_ref)) {
					pInstance co = t.get("get_co", pInstance.class, "out");
					if (co != null) co.run("send", r);
				}
			}
		}})
		.param("keys", new String[] {"all"}, "filters", new String[] {}) 
		.run(getRun(CT.RUNS_ADD_CO_IN), "in")
		;
		
		newNodeModel("from").process()
		.useInit().commande(new nRun() {public void run() {
			instance.patch.node_from.add(instance);
		}})
		.useClear().commande(new nRun() {public void run() {
			instance.patch.node_from.remove(instance);
		}}).useInit()
		.openSec()
			.param("text", "this: ", "width", (int)8, "def", "from")
			.run(pNode.getRun(CT.RUNP_VAR_STR_LAB_FIELD), "this_ref")
		.closeSec()
		.commande(getCom(CT.COM_ADD_ROW))
		.openSec()
			.param("text", "target: ", "width", (int)8)
			.run(pNode.getRun(CT.RUNP_VAR_STR_LAB_FIELD), "target_ref")
		.closeSec()
		.openSec()
			.param("run", new nRun() {public void run() {
				nWidget trigg_w = instance.get("get_mapped_widget", nWidget.class, 
						"trigg_dropm_targ");
				if (trigg_w == null) return;
				ArrayList<String> targs = new ArrayList<String>();
				for (pInstance t : instance.patch.node_to) {
					String r = t.getVar("this_ref", String.class);
					if (r != null) targs.add(r);
				}
//				instance.patch.patch_dropmenu.metode("clear_entrys");
				for (String rf : targs) {
					nGUI.add_dropmenu_entry(rf, new nRun(instance) { public void run() {
						((pInstance)builder).setVar("target_ref", rf); }}); 
					
//					nWidget w1 = (nWidget)instance.patch.patch_dropmenu
//							.metodeGet("add_entry_custom", rf, RS*6f, RS*2f/3f);
//					w1.addEventTrigger(new nRun(instance) { public void run() {
//						((pInstance)builder).setVar("target_ref", rf); 
//					}}); 
				}
//				instance.patch.patch_dropmenu.metode("open", trigg_w); 
				nGUI.open_dropmenu(trigg_w);
			}})
			.run(pNode.getRun(CT.RUNP_ADD_TRIGG), "trigg_dropm_targ", "Pk", (int)2)
		.closeSec()
		.getStand()
		.openSec()
		.param("offer", new nRun() {public Object get() {
			pInstance node = instance.object("node", pInstance.class);
			String targ_ref = node.getVar("target_ref", String.class);
			for (pInstance t : instance.patch.node_to) {
				String r = t.getVar("this_ref", String.class);
				if (r != null && r.equals(targ_ref)) {
					pInstance co = t.get("get_co", pInstance.class, "in");
					if (co != null) return co.get("obtain");
				}
			}
			return null;
		}})
		.param("offer_all_nodes", new nRun() {public Object get() {
			pInstance node = instance.object("node", pInstance.class);
			String targ_ref = node.getVar("target_ref", String.class);
			for (pInstance t : instance.patch.node_to) {
				String r = t.getVar("this_ref", String.class);
				if (r != null && r.equals(targ_ref)) {
					pInstance co = t.get("get_co", pInstance.class, "in");
					if (co != null) {
//						app.log("this_ref "+r);
						return co.get("obtain_all_nodes", Object.class);
					}
				}
			}
			return null;
		}})
		.param("offer_node", new nRun() {public Object get() {
			pInstance node = instance.object("node", pInstance.class);
			String targ_ref = node.getVar("target_ref", String.class);
			for (pInstance t : instance.patch.node_to) {
				String r = t.getVar("this_ref", String.class);
				if (r != null && r.equals(targ_ref)) {
					pInstance co = t.get("get_co", pInstance.class, "in");
					if (co != null) {
						return co.get("obtain_node");
					}
				}
			}
			return null;
		}})
//		.param("event_receive", new nRun() {public void run() {
//			pInstance node = instance.object("node", pInstance.class);
//			Object r = arg(0,Object.class); 
//			String targ_ref = node.getVar("target_ref", String.class);
//			for (pInstance t : instance.patch.node_to) {
//				String rf = t.getVar("this_ref", String.class);
//				if (rf != null && rf.equals(targ_ref)) {
//					pInstance co = t.get("get_co", pInstance.class, "in");
//					if (co != null) co.run("send", r);
//				}
//			}
//		}})
		.param("keys", new String[] {"all"}, "filters", new String[] {}) 
		.run(getRun(CT.RUNS_ADD_CO_OUT), "out")
		.closeSec()
		;
		
		
		
		
		
		
		
		
		


	}
	
	
	public static void build_standard() {

		float RS = nGUI.book.RS;

		
		pStandard.newStandard("node", "inst")
		.addCollecInst("cos", "ent")
		.addCollecInst("plugs", "ent")
		.addData("pos", new Vector2())
		.addData("selected", false)
		.setInitRun(new nRun() {public void run() {
			pPatch patch = instance.patch;
			pSheet sheet = instance.sheet;
			if (!patch.nodes.contains(instance)) patch.nodes.add(instance);
			if (!sheet.nodes.contains(instance)) sheet.nodes.add(instance);
			
			nWidgetGroup group = patch.app.gui.addWidgetGroup("patch_node");
			instance.addObject("group", group);
			nInterface interf = (nInterface)group.metodeGet("get_interf");
			interf.group.get("ref").setPassif();
			instance.addObject("interf", interf);
			
			group.get("back").setText(instance.pool_index + " " + 
					node_models.getKey(instance.stand));

			instance.addObject("co_cnt", (int)0);
			instance.addObject("plug_cnt", (int)0);

			if (instance.getDataBoo("selected")) instance.setData("selected", false);

			nMap<nWidget> widget_map = new nMap<nWidget>();
			instance.addObject("widget_map", widget_map);
			
		}})
		.setLoadRun(new nRun() {public void run() {
			nWidgetGroup group = instance.object("group", nWidgetGroup.class);
			group.metode("link_to_instance", instance);
		}})
		.setClearRun(new nRun() {public void run() {
			ArrayList<pInstance> co_inst = instance.collecInstAll("cos");
			if (co_inst != null) for (pInstance e : co_inst) e.clear(); 
			ArrayList<pInstance> plug_inst = instance.collecInstAll("plugs");
			if (plug_inst != null) for (pInstance e : plug_inst) e.clear();
			
			pPatch patch = instance.patch;
			if (instance.getDataBoo("selected")) patch.select_nodes.remove(instance); 
			patch.nodes.remove(instance);
			instance.sheet.nodes.remove(instance);
			if (instance.hasObject("group")) 
				instance.object("group", nWidgetGroup.class).clear();
			
		}})
		.newRun("clear_trigg", new nRun() {public void run() {
			instance.clear();
		}})
		.newRun("move_to_cam", new nRun() {public void run() {
			nWidgetGroup group = instance.object("group", nWidgetGroup.class);
			group.metode("move_to_cam");
		}})
		.newRun("find_place", new nRun() {public void run() {
			nWidgetGroup group = instance.object("group", nWidgetGroup.class);
//			group.metode("find_place");
			PlaneApplet.app.addDelayEvent(6,new nRun() { public void run() {
				group.metode("find_place"); }});
		}})
		.newRun("attract_plugged", new nRun() {public void run() {
//			if (instance.getData("show", Boolean.class)) {
				for (pInstance e : instance.collecInstAll("plugs")) 
						if (!e.getVar("attracted", Boolean.class) && 
								e.getInst("plugged") != null && 
								!e.getInst("plugged").getVar("attracted", Boolean.class)) {
					Vector2 pos = e.get("getCenter", Vector2.class);
					Vector2 pos2 = e.getInst("plugged").get("getCenter", Vector2.class);
					Vector2 f = new Vector2(pos).sub(pos2);
					if (f.len() > 0f) {
	//					f.scl(0.5f);
						pInstance tile = e.getInst("plugged").getInst("node");
						if (tile != null) { tile.object("group", nWidgetGroup.class)
							.metode("move",f); }
					}
					e.setVar("attracted", true); 
					e.getInst("plugged").setVar("attracted", true); 
					e.getInst("plugged").getInst("node").run("attract_plugged"); 
				}
//			}
		}})
		.newRun("move", new nRun() {public void run() {
//			app.log(" bric move run "+args.length);
			if (args.length == 1 && args[0] != null && args[0].getClass() == Vector2.class) {
				Vector2 p = arg(0, Vector2.class);
				instance.object("group", nWidgetGroup.class).metode("move",p);
			} else if (args.length == 2 && args[0] != null && args[1] != null) {
				float x = arg(0, Float.class);
				float y = arg(1, Float.class);
				instance.object("group", nWidgetGroup.class).metode("move",new Vector2(x,y));
			}
		}})
		.newRun("go_to", new nRun() {public void run() {
//			app.log(" bric move run "+args.length);
			if (args.length == 1 && args[0] != null && args[0].getClass() == Vector2.class) {
				Vector2 p = arg(0, Vector2.class);
				instance.object("group", nWidgetGroup.class).metode("go_to",p);
			} else if (args.length == 2 && args[0] != null && args[1] != null) {
				float x = arg(0, Float.class);
				float y = arg(1, Float.class);
				instance.object("group", nWidgetGroup.class).metode("go_to",new Vector2(x,y));
			}
		}})
		.newRun("get_plug", new nRun() {public Object get() {
			String plug_ref = arg(0, String.class);
			if (plug_ref == null) return null;
			for (pInstance c : instance.collecInstAll("plugs")) {
				if (c.getDataStr("ref") != null && 
						c.getDataStr("ref").equals(plug_ref)) {
					return c; } }
			return null;
		}})
		.newRun("plug_obtain", new nRun() {public Object get() {
			String plug_ref = arg(0, String.class);
			if (plug_ref == null) return null;
			for (pInstance c : instance.collecInstAll("plugs")) {
				if (c.getDataStr("ref") != null && 
						c.getDataStr("ref").equals(plug_ref)) {
					return c.get("obtain"); } }
			return null;
		}})
		.newRun("plug_obtain_node", new nRun() {public Object get() {
			String plug_ref = arg(0, String.class);
			if (plug_ref == null) return null;
			for (pInstance c : instance.collecInstAll("plugs")) {
				if (c.getDataStr("ref") != null && 
						c.getDataStr("ref").equals(plug_ref) && 
						c.getInst("plugged") != null) {
					return c.getInst("plugged").getInst("node"); } }
			return null;
		}})
		.newRun("get_co", new nRun() {public Object get() {
			String co_ref = arg(0, String.class);
			if (co_ref == null) return null;
			for (pInstance c : instance.collecInstAll("cos")) {
				if (c.getDataStr("ref") != null && 
						c.getDataStr("ref").equals(co_ref)) {
					return c; } }
			return null;
		}})
		.newRun("co_obtain", new nRun() {public Object get() {
			String co_ref = arg(0, String.class);
			if (co_ref == null) return null;
			for (pInstance c : instance.collecInstAll("cos")) {
				if (c.getDataStr("ref") != null && 
						c.getDataStr("ref").equals(co_ref)) {
					return c.get("obtain"); } }
			return null;
		}})
		.newRun("get_mapped_widget", new nRun() {public Object get() {
			nMap<nWidget> widget_map = instance.object("widget_map", nMap.class);
			if (widget_map != null) return widget_map.get(arg(0, String.class));
			return null;
		}})
		.newRun("select", new nRun() {public void run() {
			pPatch patch = instance.patch;
			if (patch.select_sheet != null && patch.select_sheet != instance.sheet) return;
			instance.sheet.select_sheet();
			instance.object("group", nWidgetGroup.class).get("selline").setOutline(true);
			if (!patch.select_nodes.contains(instance)) patch.select_nodes.add(instance);
			instance.setData("selected", true);
		}})
		.newRun("unselect", new nRun() {public void run() {
			pPatch patch = instance.patch;
			while (patch.select_nodes.contains(instance)) 
				patch.select_nodes.remove(instance);
			instance.object("group", nWidgetGroup.class).get("selline").setOutline(false);
			instance.setData("selected", false);
//			if (patch.select_nodes.size() == 0) instance.sheet.unselect_sheet();
		}})
		.newRun("pop_node", new nRun() {public Object get() { 
			String co_ref = arg(0,String.class);
			String node_model = arg(1,String.class);
			String node_co = arg(2,String.class);
			pSheet sheet = instance.sheet;
			pInstance t = sheet.newNode(node_model);
			if (t != null) {
				pInstance inst_co = instance.get("get_co", pInstance.class, co_ref);
				pInstance t_co = t.get("get_co", pInstance.class, node_co);
				if (inst_co != null && t_co != null) {
					inst_co.run("link_to", t_co);
					
					PlaneApplet.app.addDelayEvent(4,new nRun(instance) {public void run() {
						pInstance inst = (pInstance)builder;
						Vector2 p1 = inst_co.get("getCenter", Vector2.class);
						Vector2 p2 = t_co.get("getCenter", Vector2.class);
						p1 = p1.sub(p2);
						if (inst_co.getData("side", String.class).equals("left")) 
							p1 = p1.add(-RS*2f,0f);
						else p1 = p1.add(RS*2f,0f);
						t_co.getInst("node").run("move", p1);
					}});
					
				}
				return t;
			}
			return null;
		}})
		.newRun("pop_plug_node", new nRun() {public Object get() { 
			String plug_ref = arg(0,String.class);
			String node_model = arg(1,String.class);
			String node_plug = arg(2,String.class);
			pSheet sheet = instance.sheet;
			pInstance t = sheet.newNode(node_model);
			if (t != null) {
				pInstance inst_plug = instance.get("get_plug", pInstance.class, plug_ref);
				pInstance t_plug = t.get("get_plug", pInstance.class, node_plug);
				if (inst_plug != null && t_plug != null) {
					inst_plug.run("link_to", t_plug);
				}
				return t;
			}
			return null;
		}})
		;
		
		
		

		
		
		newRunTool("add_node_co", CT.RUNP_ADD_CO, new nRun() {public void run() {
			pProcess proc = arg(0,pProcess.class);
			String ref = arg(1,String.class);
			String side = arg(2,String.class);
			if (ref == null || side == null) return;
			if (proc == null) return;
			proc.commande(new nRun() { public void run() {
				int co_cnt = instance.object("co_cnt", Integer.class);
				pInstance in = instance.collecInstGet("cos",co_cnt);
				if (in == null) { 
					for (pInstance p : instance.collecInstAll("cos")) {
						if (p.getData("ref", String.class).equals(ref)) {
							Utl.logn("ERROR : pNode "+instance.pool_ref+" add_co : "+
									ref+" allready exist");
							return;
						} }
					in = instance.sheet.newInstance("co", instance, ref, side);
					in.is_new = true;
				} 
				in.run("init_run", param);
				in.is_new = false;
				instance.setObject("co_cnt", (int)(co_cnt+1));
			}});
		}});
		
		pStandard.newStandard("co", "ent")
		.addData("ref", "")
		.addData("side", "")
		.addInst("node", "inst")
		.addCollecInst("co_linked", "ent")
		.addCollecInst("links", "link")
		.addData("max_link", (int)0)
		.addData("hightlight_count", (int)0)
		.setCreateRun(new nRun() {public void run() {

//			app.log("co create_run "+instance.pool_ref);
			
			pInstance node = instance.getInst("node");
			if (node == null) node = arg(0,pInstance.class);
			if (node == null) return;
			instance.setInst("node", node);
			
			String co_ref = instance.getData("ref", String.class);
			if (co_ref == null || co_ref.length() == 0) 
				co_ref = arg(1,String.class);
			instance.setData("ref", co_ref);

			String co_side = instance.getData("side", String.class);
			if (co_side == null || co_side.length() == 0) 
				co_side = arg(2,String.class);
			instance.setData("side", co_side);
			
		}})
		.newRun("init_run", new nRun() {public void run() {
			
			pInstance node = instance.getInst("node");
			pPatch patch = instance.patch;
			patch.cos.add(instance);
			instance.sheet.cos.add(instance);
			if (!node.collecInstContains("cos",instance))
				node.collecInstAdd("cos",instance);
			instance.addObject("node", node);

			ArrayList<String> keys = new ArrayList<String>();
			ArrayList<String> filters = new ArrayList<String>();
			if (hasParam("key", String.class)) 
				keys.add(getParam("key", String.class));
			if (hasParam("filter", String.class)) 
				filters.add(getParam("filter", String.class));
			if (hasParam("keys", String[].class)) {
				String[] k = getParam("keys", String[].class);
				for (String s : k) keys.add(s); }
			if (hasParam("filters", String[].class)) {
				String[] k = getParam("filters", String[].class);
				for (String s : k) filters.add(s); }
			instance.addObject("keys", keys);
			instance.addObject("filters", filters);

			nWidgetGroup group = node.object("group", nWidgetGroup.class);
			nWidget w = (nWidget)group.metodeGet("add_widget",(int)2);
			w.copyLookFrom(PlaneApplet.app.gui.book.getModel("NC_base"));
			w.setInfo(instance.getData("ref", String.class));
			nWidget title = (nWidget)group.metodeGet("add_widget",(int)2);
			title.copyFrom(PlaneApplet.app.gui.book.getModel("NC_title"));
			title.setParent(w);
			title.setSize(RS*2f,2f*RS/3f);
			String title_txt = instance.getDataStr("ref");
			if (hasParam("text", String.class)) {
				title_txt = Utl.copy(getParam("text", String.class)); }
			if (title_txt.length() >= 7) title_txt = title_txt.substring(0, 7);
			title.setText(title_txt);
			if (instance.getData("side", String.class).equals("right"))
				title.setPos(0,RS/2f).setRectOrigin(nAlign.RIGHT,nAlign.CENTER);
			else title.setPos(RS,RS/2f).setRectOrigin(nAlign.LEFT,nAlign.CENTER);

			nWidget costack = (nWidget)group.metodeGet("get_side_stack", 
					instance.getData("side", String.class));
			w.setParent(costack);
			costack.setGlueSpace(RS*2f);
			
			w.setTrigger().setRightTrigger();
			
			nRun run_trigg = new nRun(instance) {public void run() {
				pInstance inst = (pInstance)builder;
				pPatch patch = inst.patch;
				pSheet sheet = inst.sheet;
				if (patch.linking_node_co == null) {
					patch.linking_node_co = inst;
					for (pInstance c : sheet.cos) if (c != inst && 
							inst.get("co_is_compatible", Boolean.class, c)) {
						c.run("light_up");
					} else c.run("light_down");
				} else if (patch.linking_node_co != inst && 
						patch.linking_node_co.sheet == inst.sheet) {
					inst.run("link_to", patch.linking_node_co);
					patch.linking_node_co = null;
					for (pInstance c : patch.cos) c.run("light_down");
				}
			}};
			nRun run_trigg_r = new nRun(instance) {public void run() {
				pInstance inst = (pInstance)builder;
				pInstance node = inst.getInst("node");
				
//				inst.patch.patch_dropmenu.metode("clear_entrys");
				
				CoDef this_pd = null;
				String this_ref = inst.getData("ref", String.class);
				for (CoDef p : getNodeModelCos(node.stand.ref)) 
					if (p.ref.equals(this_ref)) { this_pd = p; break; }
				if (this_pd == null) {
					Utl.logn("ERROR co run trigg r");
				}
				for (String node_model : buildable_node_models.allKey()) {
					pStandard node_model_stan = buildable_node_models.get(node_model);
					for (CoDef pd : getNodeModelCos(node_model_stan)) {
						if (key_filter_compatibility(this_pd.keys, this_pd.filters, 
								pd.keys, pd.filters)) {
							String r = pd.ref;
							nWidget w1 = nGUI.add_dropmenu_entry(node_model + " - " + r); 
//							nWidget w1 = (nWidget)inst.patch.patch_dropmenu
//									.metodeGet("add_entry_custom", node_model + " - " + r,
//											RS*8f, RS*3f/3f);
							w1.addEventTrigger(new nRun(node, this_ref, node_model, r) { 
									public void run() {
								((pInstance)args[0]).get("pop_node", 
										((String)args[1]), ((String)args[2]), 
										((String)args[3]));  
							}}); 
						}
					}
				}
//				inst.patch.patch_dropmenu.metode("open", w); 
				nGUI.open_dropmenu(w);
			}};
			
			w.addEventTrigger(run_trigg);
			w.addEventTriggerRight(run_trigg_r);

			instance.addObject("co_widget", w);
			
			if (hasParam("event_link")) {
				instance.addObject("event_link", getParam("event_link", nRun.class)); }
			if (hasParam("event_receive")) {
				instance.addObject("event_receive", getParam("event_receive", nRun.class)); }
			if (hasParam("offer")) {
				instance.addObject("offer", getParam("offer", nRun.class)); }		
			if (hasParam("offer_node")) {
				instance.addObject("offer_node", getParam("offer_node", nRun.class)); }		
			if (hasParam("offer_all_nodes")) {
				instance.addObject("offer_all_nodes", getParam("offer_all_nodes", nRun.class)); }		
			
		}})
		.setLoadRun(new nRun() {public void run() {
			
		}})
		.setClearRun(new nRun() {public void run() {
			pPatch patch = instance.patch;
			patch.cos.remove(instance);
			instance.sheet.cos.remove(instance);
			pInstance node = instance.getInst("node");
			if (node != null) {
				node.collecInstRemove("cos",instance); }
		}})
		.newRun("highlight_self", new nRun() {public void run() {
			instance.setData("hightlight_count", pNode.LINK_HIGHLIGHT_TEMP);
		}})
		.newRun("highlight_links", new nRun() {public void run() {
			instance.setData("hightlight_count", pNode.LINK_HIGHLIGHT_TEMP);
			for (pInstance l : instance.collecInstAll("links")) l.run("highlight");
		}})
		.newRun("getCenter", Vector2.class, new nRun() {public Object get() {
			if (!instance.hasObject("co_widget")) return new Vector2();
			nWidget w = instance.object("co_widget", nWidget.class);
			Vector2 v = new Vector2(w.getPosRelativeToParent(instance.sheet.sheet_ref));
			v.add(w.getLocalSX()/2f, w.getLocalSY()/2f);
			return v;
		}})
		.newRun("frame", new nRun() {public void run() {
			if (instance.getDataInt("hightlight_count") > 0) 
				instance.addDataInt("hightlight_count", (int)-1); 
//			nWidget w = instance.object("co_widget", nWidget.class);
//			if (instance.getDataInt("hightlight_count") > 0) w.setOutline(true);
//			else w.setOutline(false);
		}})
		.newRun("draw", new nRun() {public void run() {
			Vector2 pos = instance.get("getCenter", Vector2.class);
			PlaneApplet.app.noFill();
			PlaneApplet.app.stroke(255,255,0,255, 5f);
			if (instance.getDataInt("hightlight_count") > 0) PlaneApplet.app.circle(pos.x, pos.y, 5f);
		}})
		.newRun("light_up", new nRun() {public void run() {
			if (instance.hasObject("co_widget"))
				instance.object("co_widget", nWidget.class)
				.setOutlineWeight(RS/3f)
				.set_color_outline(Utl.color(255,200,0,255));
		}})
		.newRun("light_down", new nRun() {public void run() {
			if (instance.hasObject("co_widget"))
				instance.object("co_widget", nWidget.class)
				.setOutlineWeight(RS/10f)
				.set_color_outline(Utl.color(120,180,255,255));
		}})
		.newRun("co_is_compatible", Boolean.class, new nRun() {public Object get() {
			pInstance c = arg(0, pInstance.class);
			if (c != null && c != instance) {
				int instml = instance.getData("max_link", Integer.class);
				int cml = c.getData("max_link", Integer.class);
				if (instml > 0 && instml >= instance.collecInstAll("co_linked").size()) return false;
				if (cml > 0 && cml >= c.collecInstAll("co_linked").size()) return false;
				ArrayList<String> keys = Utl.duplic(instance.object("keys", ArrayList.class));
				ArrayList<String> filters = Utl.duplic(instance.object("filters", ArrayList.class));
				String key = instance.object("key", String.class);
				String filter = instance.object("filter", String.class);
				if (keys == null) keys = new ArrayList<String>();
				if (filters == null) filters = new ArrayList<String>();
				if (key != null) keys.add(key); if (filter != null) filters.add(filter);
				ArrayList<String> keys2 = Utl.duplic(c.object("keys", ArrayList.class));
				ArrayList<String> filters2 = Utl.duplic(c.object("filters", ArrayList.class));
				String key2 = c.object("key", String.class);
				String filter2 = c.object("filter", String.class);
				if (keys2 == null) keys2 = new ArrayList<String>();
				if (filters2 == null) filters2 = new ArrayList<String>();
				if (key2 != null) keys2.add(key2); if (filter2 != null) filters2.add(filter2);
				boolean ok = true;
				for (String f : filters) ok = ok && (Utl.contains(keys2, f) || Utl.contains(keys2, "all"));
				for (String f : filters2) ok = ok && (Utl.contains(keys, f) || Utl.contains(keys, "all"));
				return ok;
			}
			return false;
		}})
		.newRun("link_to", new nRun() {public void run() {
			pInstance c = arg(0, pInstance.class);
			if (c != null && c != instance) {
				boolean change = false;
				ArrayList<pInstance> co_linked = instance.collecInstAll("co_linked");
				ArrayList<pInstance> c_co_linked = c.collecInstAll("co_linked");
				if (!instance.get("co_is_compatible", Boolean.class, c)) {
					if (co_linked.contains(c)) { change = true; instance.collecInstRemove("co_linked", c); }
					if (c_co_linked.contains(instance)) { change = true; c.collecInstRemove("co_linked", instance); }
					if (change) {
						instance.run("run_event_link_from_link", instance);
						c.run("run_event_link_from_link", c);
					}
					return; }
				
				if (!co_linked.contains(c)) { change = true; instance.collecInstAdd("co_linked", c); }
				if (!c_co_linked.contains(instance)) { change = true; c.collecInstAdd("co_linked", instance); }
				
				if (change) {

					pInstance l = instance.sheet.newInstance("node_link", instance, c); 
					
					instance.run("run_event_link_from_link", instance);
					c.run("run_event_link_from_link", c);
					
				}
			}
		}}).runArgs("co", pInstance.class)
		.newRun("run_event_link_from_link", new nRun() {public void run() {
			if (instance.hasObject("event_link")) 
				instance.object("event_link", nRun.class).do_run(instance);
		}})
		.newRun("run_event_link_from_node", new nRun() {public void run() {
			ArrayList<pInstance> co_linked = instance.collecInstAll("co_linked");
			for (pInstance in : co_linked) in.run("run_event_link_from_link", in);
		}})
		.newRun("unlink_from", new nRun() {public void run() {
			pInstance c = arg(0, pInstance.class);
			if (c != null && c != instance) {
				
				ArrayList<pInstance> links = instance.collecInstAll("links");
				for (pInstance l : links) {
					if (l.get("get_other_end", pInstance.class, instance) == c) { l.clear(); }
					else if (l.get("get_other_end", pInstance.class, c) == instance) { l.clear(); }
				}
					
				boolean change = false;
				ArrayList<pInstance> co_linked = instance.collecInstAll("co_linked");
				ArrayList<pInstance> c_co_linked = c.collecInstAll("co_linked");
				if (co_linked.contains(c)) {
					instance.collecInstRemove("co_linked", c);
					change = true;
				}
				if (c_co_linked.contains(instance)) {
					c.collecInstRemove("co_linked", instance);
					change = true;
				}

				if (change) {
					instance.run("run_event_link_from_link", instance);
					c.run("run_event_link_from_link", c);
				}
			}
		}}).runArgs("co", pInstance.class)
		.newRun("send", new nRun() {public void run() {
			instance.run("highlight_links");
			ArrayList<pInstance> co_linked = instance.collecInstAll("co_linked");
			if (args == null || args.length == 0) { for (pInstance l : co_linked) {
				l.run("receive"); } return; }
			Object[] a = new Object[args.length];
			for (int i = 0 ; i < args.length ; i++) a[i] = Utl.copy(args[i]);
			for (pInstance l : co_linked) l.run("receive", a);
		}}).runArgs("send", Object.class)
		.newRun("receive", new nRun() {public void run() {
			instance.run("highlight_self");
			if (instance.hasObject("event_receive")) {
				if (args.length == 0)// || c == null)
					instance.object("event_receive", nRun.class).do_run(instance, param, Utl.duplic(args));
				else instance.object("event_receive", nRun.class).do_run(instance, param, Utl.duplic(args));//c);
			}
		}}).runArgs("received", Object.class)
		.newRun("obtain", Object.class, new nRun() {public Object get() {
			instance.run("highlight_links");
			ArrayList<pInstance> co_linked = instance.collecInstAll("co_linked");
			for (pInstance l : co_linked) { return l.get("provide"); }
			return null;
		}})
		.newRun("obtain_all", Object.class, new nRun() {public Object get() {
			instance.run("highlight_links");
			ArrayList<pInstance> co_linked = instance.collecInstAll("co_linked");
			ArrayList<Object> prov = new ArrayList<Object>();
			for (pInstance l : co_linked) { 
				Object o =  l.get("provide"); 
				if (o != null) prov.add(o);
			}
			return prov;
		}})
		.newRun("provide", Object.class, new nRun() {public Object get() {
			instance.run("highlight_self");
			if (instance.hasObject("offer")) 
				return Utl.copy(instance.object("offer", nRun.class).do_get(instance, param));
			return null;
		}})
		.newRun("obtain_node", pInstance.class, new nRun() {public Object get() {

//			app.log("obtain_bric "+instance.pool_ref);
			
			instance.run("highlight_links");
			ArrayList<pInstance> co_linked = instance.collecInstAll("co_linked");
			for (pInstance l : co_linked) { 
				pInstance br = l.get("provide_node", pInstance.class); 
				if (br != null) { return br; }
			}
			return null;
		}})
		.newRun("obtain_all_nodes", ArrayList.class, new nRun() {public Object get() {

//			app.log("obtain_all_brics "+instance.pool_ref);
			
			instance.run("highlight_links");
			ArrayList<pInstance> co_linked = instance.collecInstAll("co_linked");
			ArrayList<pInstance> nodes = new ArrayList<pInstance>();
			for (pInstance l : co_linked) { 
				if (l.hasObject("offer_all_nodes")) {
					Object op_reg = l.object("offer_all_nodes", nRun.class).do_get(l);
					if (op_reg != null) {
						ArrayList<Object> reg_prov = (ArrayList)op_reg;
						for (Object or : reg_prov) if (or instanceof pInstance) {
							pInstance reg = (pInstance)or;
							if (reg != null) { nodes.add(reg); } }
					}
				} else {
					pInstance br = l.get("provide_node", pInstance.class); 
					if (br != null) { nodes.add(br); } }
				}
			return nodes;
		}})
		.newRun("provide_node", pInstance.class, new nRun() {public Object get() {

//			app.log("provide_bric "+instance.pool_ref);
			
			instance.run("highlight_self");
			
			if (instance.hasObject("offer_node")) 
				return instance.object("offer_node", nRun.class)
						.do_get(instance, param, pInstance.class);
			
			return instance.object("node", pInstance.class);
		}})
		.newRun("highlight_self", new nRun() {public void run() {
			instance.setData("hightlight_count", pNode.LINK_HIGHLIGHT_TEMP);
		}})
		.newRun("highlight_links", new nRun() {public void run() {
			instance.setData("hightlight_count", pNode.LINK_HIGHLIGHT_TEMP);
			for (pInstance l : instance.collecInstAll("links")) l.run("highlight");
		}})
		;
		
		
		
		
		
		
		

		pStandard.newStandard("node_link", "link")
		.addData("hightlight_count", (int)0)
		.addCollecInst("cos", "ent")
		.setCreateRun(new nRun() {public void run() {

//			app.log("link create "+instance.pool_ref);
			
			pInstance co1 = arg(0,pInstance.class);
			pInstance co2 = arg(1,pInstance.class);
			if (co1 == null || co2 == null) return;
			instance.collecInstAdd("cos", co1);
			instance.collecInstAdd("cos", co2);
			co1.collecInstAdd("links", instance);
			co2.collecInstAdd("links", instance);
			
		}}) //.runArgs("co1", pInstance.class, "co2", pInstance.class)
		.setInitRun(new nRun() {public void run() {

//			app.log("link init "+instance.pool_ref);
			
			pPatch patch = instance.patch;
			patch.node_links.add(instance);
			instance.sheet.node_links.add(instance);
			
		}})
		.setLoadRun(new nRun() {public void run() {

//			app.log("link load "+instance.pool_ref);
			
			ArrayList<pInstance> cos = instance.collecInstAll("cos");
			if (cos.size() == 2) {
				pInstance co1 = cos.get(0);
				pInstance co2 = cos.get(1);
				ArrayList<pInstance> co_linked = co1.collecInstAll("co_linked");
				ArrayList<pInstance> c_co_linked = co2.collecInstAll("co_linked");
				boolean change = false;
				
				if (!co1.get("co_is_compatible", Boolean.class, co2)) {
					boolean found1 = false;
					for (pInstance l : co1.collecInstAll("links")) {
						if (l == instance) found1 = true; }
					boolean found2 = false;
					for (pInstance l : co1.collecInstAll("links")) {
						if (l == instance) found2 = true; }
					if (found1) co1.collecInstRemove("links", instance);
					if (found2) co2.collecInstRemove("links", instance);
					if (found1 || found2) change = true;
					if (co_linked.contains(co2)) { change = true; co1.collecInstRemove("co_linked", co2); }
					if (c_co_linked.contains(co1)) { change = true; co2.collecInstRemove("co_linked", co1); }
					if (change) {
						instance.run("run_event_link_from_link", instance);
						co2.run("run_event_link_from_link", co2);
					}
					return; 
				}
				
				boolean found1 = false;
				for (pInstance l : co1.collecInstAll("links")) {
					if (l == instance) found1 = true; }
				boolean found2 = false;
				for (pInstance l : co1.collecInstAll("links")) {
					if (l == instance) found2 = true; }
				if (!found1) co1.collecInstAdd("links", instance);
				if (!found2) co2.collecInstAdd("links", instance);
				if (!(found1 && found2)) change = true;
				if (!co_linked.contains(co2)) { change = true; co1.collecInstAdd("co_linked", co2); }
				if (!c_co_linked.contains(co1)) { change = true; co2.collecInstAdd("co_linked", co1); }

				if (change) {
					co1.run("run_event_link_from_link", co1);
					co2.run("run_event_link_from_link", co2);
				}
			}
		}})
		.setClearRun(new nRun() {public void run() {
			pPatch patch = instance.patch;
			patch.node_links.remove(instance);
			instance.sheet.node_links.remove(instance);
			ArrayList<pInstance> cos = instance.collecInstAll("cos");
			if (cos.size() != 2) return;
			cos.get(0).collecInstRemove("links", instance);
			cos.get(1).collecInstRemove("links", instance);
		}})
		.newRun("highlight", new nRun() {public void run() {
			instance.setData("hightlight_count", pNode.LINK_HIGHLIGHT_TEMP);
		}})
		.newRun("get_other_end", pInstance.class, new nRun() {public Object get() {
			pInstance co = arg(0,pInstance.class);
			ArrayList<pInstance> cos = instance.collecInstAll("cos");
			if (co == null || cos.size() != 2) return null;
			pInstance co1 = cos.get(0); pInstance co2 = cos.get(1);
			if (co == co1) return co2; else if (co == co2) return co1;
			return null;
		}}).runArgs("co", pInstance.class)
		.newRun("draw", new nRun() {public void run() {
			Vector2 mouse = arg(0,Vector2.class);

			if (instance.getDataInt("hightlight_count") > 0) 
				instance.addDataInt("hightlight_count", (int)-1); 

			if (instance.collecInstAll("cos") == null) return;

			ArrayList<pInstance> cos = instance.collecInstAll("cos");
			if (cos.size() != 2) return;

			pInstance co1 = cos.get(0);
			pInstance co2 = cos.get(1);
			
			Vector2 pos = co1.get("getCenter", Vector2.class);
			Vector2 pos2 = co2.get("getCenter", Vector2.class);
			
			PlaneApplet app = PlaneApplet.app;
			
			app.noFill();
//			app.stroke(255,255,0,255, 5f);
//			if (instance.getDataInt("hightlight_count") > 0) {
//				app.circle(pos.x, pos.y, 5);
//				app.circle(pos2.x, pos2.y, 5); }
			
			float d = Utl.distanceSegmentPoint(pos, pos2, mouse);
			
			float touch_d = 15;
			float touch_w = 20;
			float notouch_w = 15;
			
			if (d <= touch_d) { app.stroke(255,155,0,255, touch_w); } 
			else if (instance.getDataInt("hightlight_count") > 0) 
				app.stroke(255,150,90,255, notouch_w);
			else app.stroke(80,100,240,255, notouch_w);
			
			app.line(pos, pos2);
			
			if (d <= 18 && app.input.getClick("MouseRight")) {
				co1.run("unlink_from", co2); } 
			
		}}).runArgs("mouse", Vector2.class)
		;
		
		
		
		
		
		
		
		newRunTool("add_node_plug", CT.RUNP_ADD_PLUG, new nRun() {public void run() {
			pProcess proc = arg(0,pProcess.class);
			String ref = arg(1,String.class);
			String side = arg(2,String.class);
			if (ref == null || side == null) return;
			if (proc == null) return;
			proc.commande(new nRun() { public void run() {
				int plug_cnt = instance.object("plug_cnt", Integer.class);
				pInstance in = instance.collecInstGet("plugs",plug_cnt);
				if (in == null) { 
					for (pInstance p : instance.collecInstAll("plugs")) {
						if (p.getData("ref", String.class).equals(ref)) {
							Utl.logn("ERROR : pTile "+instance.pool_ref+" add_plug : "+
									ref+" allready exist");
							return;
						} }
					in = instance.sheet.newInstance("node_plug", instance, ref, side);
					in.is_new = true;
				} 
				in.run("init_run", param);
				in.is_new = false;
				instance.setObject("plug_cnt", plug_cnt+1);
			}});
		}});
		
		pStandard.newStandard("node_plug", "ent")
		.addData("ref", "")
		.addData("side", "")
		.addInst("node", "inst")
		.addInst("plugged", "ent")
		.addData("hightlight_count", (int)0)
		.addData("hide", false)
		.setCreateRun(new nRun() {public void run() {

//			app.log("plug create_run "+instance.pool_ref);
			
			pInstance node = instance.getInst("node");
			if (node == null) node = arg(0,pInstance.class);
			if (node == null) return;
			instance.setInst("node", node);
			
			String plug_ref = instance.getData("ref", String.class);
			if (plug_ref == null || plug_ref.length() == 0) 
				plug_ref = arg(1,String.class);
			instance.setData("ref", plug_ref);

			String plug_side = instance.getData("side", String.class);
			if (plug_side == null || plug_side.length() == 0) 
				plug_side = arg(2,String.class);
			instance.setData("side", plug_side);
			
		}})
		.newRun("init_run", new nRun() {public void run() {
			
			pInstance node = instance.getInst("node");
			pPatch patch = instance.patch;
			patch.node_plugs.add(instance);
			instance.sheet.node_plugs.add(instance);
			instance.addObject("patch", patch);
			if (!node.collecInstContains("plugs",instance))
				node.collecInstAdd("plugs",instance);
			instance.addObject("node", node);
			
			instance.setVar("attracted", false);
			
			nWidgetGroup group = node.object("group", nWidgetGroup.class);
			nWidget w = (nWidget)group.metodeGet("add_widget",(int)2);
			w.copyLookFrom(PlaneApplet.app.gui.book.getModel("NP_base"));
			w.setInfo(instance.getData("ref", String.class));
			
			w.setParent((nWidget)group.metodeGet("get_side_stack", 
					instance.getData("side", String.class)));
			
			w.setTrigger();//.setRightTrigger();

			if (hasParam("hide") && getParam("hide", Boolean.class)) {
				w.setPassif()
				.setOutline(false)
				.set_color_background(Utl.color(0,0));
				instance.setData("hide", true);
			}
			
//			nRun run_trigg = new nRun(instance) {public void run() {
////				pInstance inst = (pInstance)builder;
////				pPatch patch = inst.patch;
////				if (patch.linking_plug == null) {
////					patch.linking_plug = inst;
////					
////				} else if (patch.linking_plug != inst) {
////					inst.run("link_to", patch.linking_plug);
////					patch.linking_plug = null;
////				}
//			}};
			nRun run_trigg_r = new nRun(instance) {public void run() {
				pInstance inst = (pInstance)builder;
				pInstance node = inst.getInst("node");
				
//				inst.patch.patch_dropmenu.metode("clear_entrys");
				
				PlugDef this_pd = null;
				String this_ref = inst.getData("ref", String.class);
				for (PlugDef p : getNodeModelPlugs(node.stand.ref)) 
					if (p.ref.equals(this_ref)) { this_pd = p; break; }
				if (this_pd == null) {
					Utl.logn("ERROR node plug run trigg r");
				}
				for (String node_model : node_models.allKey()) {
					pStandard node_model_stan = node_models.get(node_model);
					for (PlugDef pd : getNodeModelPlugs(node_model_stan)) {
						if (key_filter_compatibility(this_pd.keys, this_pd.filters, 
								pd.keys, pd.filters)) {
							String r = pd.ref;
							nWidget w1 = nGUI.add_dropmenu_entry(node_model + " - " + r); 
//							nWidget w1 = (nWidget)inst.patch.patch_dropmenu
//									.metodeGet("add_entry_custom", node_model + " - " + r,
//											RS*8f, RS*3f/3f);
							w1.addEventTrigger(new nRun(node, this_ref, node_model, r) { 
									public void run() {
								((pInstance)args[0]).get("pop_plug_node", 
										((String)args[1]), ((String)args[2]), 
										((String)args[3]));  
								}}); 
						}
					}
				}
//				inst.patch.patch_dropmenu.metode("open", w); 
				nGUI.open_dropmenu(w);
			}};
			
//			w.addEventTrigger(run_trigg);
//			w.addEventTriggerRight(run_trigg_r);
			
			w.addEventTrigger(run_trigg_r);

			instance.addObject("plug_widget", w);
			
			if (hasParam("event_link")) {
				instance.addObject("event_link", getParam("event_link", nRun.class)); }
			if (hasParam("event_receive")) {
				instance.addObject("event_receive", getParam("event_receive", nRun.class)); }
			if (hasParam("offer")) {
				instance.addObject("offer", getParam("offer", nRun.class)); }		
			
		}})
		.setLoadRun(new nRun() {public void run() {
			if (instance.getInst("plugged") != null) {
				if (instance.getData("side", String.class).equals("left") || 
						instance.getData("side", String.class).equals("right")) 
					instance.getInst("plugged")
						.object("plug_widget", nWidget.class)
						.setSX(RS/LINKED_PLUG_REDUC_FACT).setPassif();
				else instance.getInst("plugged")
					.object("plug_widget", nWidget.class)
					.setSY(RS/LINKED_PLUG_REDUC_FACT).setPassif(); }
		}})
		.setClearRun(new nRun() {public void run() {
			pPatch patch = instance.patch;
			patch.node_plugs.remove(instance);
			instance.sheet.node_plugs.remove(instance);
			pInstance node = instance.getInst("node");
			if (node != null) {
				node.collecInstRemove("plugs",instance); }
			instance.run("unlink");
		}})
		.newRun("highlight_self", new nRun() {public void run() {
			instance.setData("hightlight_count", pNode.LINK_HIGHLIGHT_TEMP);
		}})
		.newRun("getCenter", Vector2.class, new nRun() {public Object get() {
			if (!instance.hasObject("plug_widget")) return new Vector2();
			nWidget w = instance.object("plug_widget", nWidget.class);
//			w.force_calc();
			Vector2 v = new Vector2(w.getPosRelativeToParent(instance.sheet.sheet_ref));
			v.add(w.getLocalSX()/2f, w.getLocalSY()/2f);
			return v;
		}})
		.newRun("frame", new nRun() {public void run() {
			instance.setVar("attracted", false);
			if (instance.getDataInt("hightlight_count") > 0) 
				instance.addDataInt("hightlight_count", (int)-1); 
			nWidget w = instance.object("plug_widget", nWidget.class);
			if (!instance.getDataBoo("hide") && 
					instance.getDataInt("hightlight_count") > 0) w.setOutline(true);
			else w.setOutline(false);
		}})
		.newRun("unlink", new nRun() {public void run() {
			if (instance.getInst("plugged") != null) {
				pInstance c = instance.getInst("plugged");
				c.setInst("plugged", "");
				instance.setInst("plugged", "");
				if (instance.hasObject("event_link")) 
					instance.object("event_link", nRun.class).do_run(instance);
				if (c.hasObject("event_link")) 
					c.object("event_link", nRun.class).do_run(c);
				nWidget w = instance.object("plug_widget", nWidget.class);
				w.setSize(RS,RS);
				if (!instance.getDataBoo("hide")) w.setTrigger();
				w = c.object("plug_widget", nWidget.class);
				w.setSize(RS,RS);
				if (!c.getDataBoo("hide")) w.setTrigger();
			}
		}})
		.newRun("link_to", new nRun() {public void run() {
			pInstance c = arg(0, pInstance.class);
			if (instance.getInst("plugged") == null && c != instance && 
					c != null && c.getInst("plugged") == null) {
				c.setInst("plugged", instance);
				instance.setInst("plugged", c);
				if (instance.hasObject("event_link")) 
					instance.object("event_link", nRun.class).do_run(instance);
				if (c.hasObject("event_link")) 
					c.object("event_link", nRun.class).do_run(c);
				if (instance.getData("side", String.class).equals("left") || 
						instance.getData("side", String.class).equals("right")) {
					nWidget w = instance.object("plug_widget", nWidget.class);
					w.setSX(RS/LINKED_PLUG_REDUC_FACT).setPassif();
					w = c.object("plug_widget", nWidget.class);
					w.setSX(RS/LINKED_PLUG_REDUC_FACT).setPassif();
					
				} else {
					nWidget w = instance.object("plug_widget", nWidget.class);
					w.setSY(RS/LINKED_PLUG_REDUC_FACT).setPassif();
					w = c.object("plug_widget", nWidget.class);
					w.setSY(RS/LINKED_PLUG_REDUC_FACT).setPassif();
				}
			}
		}}).runArgs("plug", pInstance.class)
		.newRun("send", new nRun() {public void run() {
			pInstance plugged = instance.getInst("plugged");
			if (plugged == null) return;
			instance.run("highlight_self");
			if (args == null || args.length == 0) { plugged.run("receive"); }
			else {
				Object[] a = new Object[args.length];
				for (int i = 0 ; i < args.length ; i++) a[i] = Utl.copy(args[i]);
				plugged.run("receive", a); }
		}}).runArgs("send", Object.class)
		.newRun("receive", new nRun() {public void run() {
			if (instance.hasObject("event_receive")) {
				instance.run("highlight_self");
				instance.object("event_receive", nRun.class)
					.do_run(instance, param, Utl.duplic(args)); }
		}})
		.newRun("obtain", Object.class, new nRun() {public Object get() {
			pInstance plugged = instance.getInst("plugged");
			if (plugged != null) { 
				instance.run("highlight_self"); 
				return plugged.get("provide"); }
			return null;
		}})
		.newRun("provide", Object.class, new nRun() {public Object get() {
			if (instance.hasObject("offer")) {
				instance.run("highlight_self");
				return Utl.copy( instance.object("offer", nRun.class)
						.do_get(instance, param) ); }
			return null;
		}})
		;
		
		
		
		
	}
	
	
	
	public static boolean key_filter_compatibility(String[] keys, String[] filters, 
			String[] keys2, String[] filters2) {
		boolean ok = true;
		for (String f : filters) if (!f.equals("all")) ok = ok && 
				(Utl.contains(keys2, f) || Utl.contains(keys2, "all"));
		for (String f : filters2) if (!f.equals("all")) ok = ok && 
				(Utl.contains(keys, f) || Utl.contains(keys, "all"));
		return ok;
	}
	
	
	
	public static void build_coms() {
		
		float RS = PlaneApplet.app.gui.book.RS;

		newRunTool("run_var_boo_switch", CT.RUNP_VAR_BOO_SWITCH, 
				new nRun() {public void run() {
			pProcess proc = arg(0,pProcess.class);
			String ref = arg(1,String.class);
			String text = ""+ref; 
			if (args.length > 2) text = arg(2,String.class);
			int w = 4; 
			if (args.length > 3) w = arg(3,Integer.class);
			if (proc == null) return;
			proc.run(getRun(CT.RUNP_OBTAIN_VAR), ref, 
					getParamOrDef("def", Boolean.class, false))
			.openSec()
				.param("ref", "switch_"+ref, "var_link_ref", ref)	
				.param("text", getParamOrDef("text", String.class, text), 
						"width", getParamOrDef("width", Integer.class, w)) 
				.commande(getCom(CT.COM_ADD_SWITCH))
			.closeSec()
			;
		}});

		newRunTool("run_var_vec_field", CT.RUNP_VAR_VEC_FIELD, 
				new nRun() {public void run() {
			pProcess proc = arg(0,pProcess.class);
			String ref = arg(1,String.class);
			int w = 2; 
			if (args.length > 2) w = arg(2,Integer.class);
			if (proc == null) return;
			proc.run(getRun(CT.RUNP_OBTAIN_VAR), ref, 
					getParamOrDef("def", Vector2.class, new Vector2()))
			.openSec()
				.param("ref", "field_"+ref, "var_link_ref", ref, "var_link_vec_axe", "x")
				.param("var_link_class", Vector2.class.getName())	
				.param("width", getParamOrDef("width", Integer.class, w)) 
				.commande(getCom(CT.COM_ADD_FIELD))
			.closeSec()
			.openSec()
				.param("ref", "field_"+ref, "var_link_ref", ref, "var_link_vec_axe", "y")
				.param("var_link_class", Vector2.class.getName())	
				.param("width", getParamOrDef("width", Integer.class, w)) 
				.commande(getCom(CT.COM_ADD_FIELD))
			.closeSec()
			;
		}});

		newRunTool("run_var_vec_lab_field", CT.RUNP_VAR_VEC_LAB_FIELD, 
				new nRun() {public void run() {
			pProcess proc = arg(0,pProcess.class);
			String ref = arg(1,String.class);
			String text = ""+ref; 
			if (args.length > 2) text = arg(2,String.class);
			int w = 3; 
			if (args.length > 3) w = arg(3,Integer.class);
			if (proc == null) return;
			proc.run(getRun(CT.RUNP_OBTAIN_VAR), ref, 
					getParamOrDef("def", Vector2.class, new Vector2()))
			.run(getRun(CT.RUNP_ADD_LABEL), text, getParamOrDef("width", Integer.class, w))
			.openSec()
				.param("ref", "field_"+ref, "var_link_ref", ref, "var_link_vec_axe", "x")
				.param("var_link_class", Vector2.class.getName())	
				.param("width", getParamOrDef("width", Integer.class, w)) 
				.commande(getCom(CT.COM_ADD_FIELD))
			.closeSec()
			.openSec()
				.param("ref", "field_"+ref, "var_link_ref", ref, "var_link_vec_axe", "y")
				.param("var_link_class", Vector2.class.getName())	
				.param("width", getParamOrDef("width", Integer.class, w)) 
				.commande(getCom(CT.COM_ADD_FIELD))
			.closeSec()
			;
		}});

		newRunTool("run_var_int_field", CT.RUNP_VAR_INT_FIELD, 
				new nRun() {public void run() {
			pProcess proc = arg(0,pProcess.class);
			String ref = arg(1,String.class);
			int w = 4; 
			if (args.length > 2) w = arg(2,Integer.class);
			if (proc == null) return;
			proc.run(getRun(CT.RUNP_OBTAIN_VAR), ref, 
					getParamOrDef("def", Integer.class, (int)0));
			proc.openSec()
				.param("ref", "field_"+ref, "var_link_ref", ref)
				.param("var_link_class", Integer.class.getName())	
				.param("width", getParamOrDef("width", Integer.class, w)) 
				.commande(getCom(CT.COM_ADD_FIELD))
			.closeSec()
			;
		}});

		newRunTool("run_var_int_lab_field", CT.RUNP_VAR_INT_LAB_FIELD, 
				new nRun() {public void run() {
			pProcess proc = arg(0,pProcess.class);
			String ref = arg(1,String.class);
			String text = ""+ref; 
			if (args.length > 2) text = arg(2,String.class);
			int w = 4; 
			if (args.length > 3) w = arg(3,Integer.class);
			boolean row = false; 
			if (args.length > 4) row = arg(4,Boolean.class);
			if (proc == null) return;
			proc.run(getRun(CT.RUNP_OBTAIN_VAR), ref, 
					getParamOrDef("def", Integer.class, (int)0))
			.run(getRun(CT.RUNP_ADD_LABEL), text, 
					getParamOrDef("width", Integer.class, w));
			if (row) proc.commande(getCom(CT.COM_ADD_ROW)); 
			proc.openSec()
				.param("ref", "field_"+ref, "var_link_ref", ref)
				.param("var_link_class", Integer.class.getName())	
				.param("width", getParamOrDef("width", Integer.class, w)) 
				.commande(getCom(CT.COM_ADD_FIELD))
			.closeSec()
			;
		}});

		newRunTool("run_var_str_watch", CT.RUNP_VAR_STR_WATCH, 
				new nRun() {public void run() {
			pProcess proc = arg(0,pProcess.class);
			String ref = arg(1,String.class);
			int w = 4; 
			if (args.length > 2) w = arg(2,Integer.class);
			if (proc == null) return;
			proc.run(getRun(CT.RUNP_OBTAIN_VAR), ref, 
					getParamOrDef("def", String.class, ""))
			.openSec()
				.param("ref", "watch_"+ref, "var_link_ref", ref)
				.param("var_link_class", String.class.getName())	
				.param("width", getParamOrDef("width", Integer.class, w)) 
				.commande(getCom(CT.COM_ADD_WATCH))
			.closeSec()
			;
		}});

		newRunTool("run_var_str_field", CT.RUNP_VAR_STR_FIELD, 
				new nRun() {public void run() {
			pProcess proc = arg(0,pProcess.class);
			String ref = arg(1,String.class);
			int w = 4; 
			if (args.length > 2) w = arg(2,Integer.class);
			if (proc == null) return;
			proc.run(getRun(CT.RUNP_OBTAIN_VAR), ref, 
					getParamOrDef("def", String.class, ""))
			.openSec()
				.param("ref", "field_"+ref, "var_link_ref", ref)
				.param("var_link_class", String.class.getName())	
				.param("width", getParamOrDef("width", Integer.class, w)) 
				.commande(getCom(CT.COM_ADD_FIELD))
			.closeSec()
			;
		}});

		newRunTool("run_var_str_lab_field", CT.RUNP_VAR_STR_LAB_FIELD, 
				new nRun() {public void run() {
			pProcess proc = arg(0,pProcess.class);
			String ref = arg(1,String.class);
			String text = ""+ref; 
			if (args.length > 2) text = arg(2,String.class);
			int w = 4; 
			if (args.length > 3) w = arg(3,Integer.class);
			if (proc == null) return;
			proc.run(getRun(CT.RUNP_OBTAIN_VAR), ref, 
					getParamOrDef("def", String.class, ""))
			.run(getRun(CT.RUNP_ADD_LABEL), text, getParamOrDef("width", Integer.class, w))
			.openSec()
				.param("ref", "field_"+ref, "var_link_ref", ref)
				.param("var_link_class", String.class.getName())	
				.param("width", getParamOrDef("width", Integer.class, w)) 
				.commande(getCom(CT.COM_ADD_FIELD))
			.closeSec()
			;
		}});

		newRunTool("run_var_flt_field", CT.RUNP_VAR_FLT_FIELD, 
				new nRun() {public void run() {
			pProcess proc = arg(0,pProcess.class);
			String ref = arg(1,String.class);
			int w = 4; 
			if (args.length > 2) w = arg(2,Integer.class);
			if (proc == null) return;
			proc.run(getRun(CT.RUNP_OBTAIN_VAR), ref, 
					getParamOrDef("def", Float.class, 0f))
			.openSec()
				.param("ref", "field_"+ref, "var_link_ref", ref)
				.param("var_link_class", Float.class.getName())	
				.param("width", getParamOrDef("width", Integer.class, w)) 
				.commande(getCom(CT.COM_ADD_FIELD))
			.closeSec()
			;
		}});

		newRunTool("run_var_flt_lab_field", CT.RUNP_VAR_FLT_LAB_FIELD, 
				new nRun() {public void run() {
			pProcess proc = arg(0,pProcess.class);
			String ref = arg(1,String.class);
			String text = ""+ref; 
			if (args.length > 2) text = arg(2,String.class);
			int w = 4; 
			if (args.length > 3) w = arg(3,Integer.class);
			if (proc == null) return;
			proc.run(getRun(CT.RUNP_OBTAIN_VAR), ref, 
					getParamOrDef("def", Float.class, 0f))
			.run(getRun(CT.RUNP_ADD_LABEL), text, getParamOrDef("width", Integer.class, w))
			.openSec()
				.param("ref", "field_"+ref, "var_link_ref", ref)
				.param("var_link_class", Float.class.getName())	
				.param("width", getParamOrDef("width", Integer.class, w)) 
				.commande(getCom(CT.COM_ADD_FIELD))
			.closeSec()
			;
		}});

		newRunTool("run_var_flt_field_slide", CT.RUNP_VAR_FLT_FIELD_SLIDE, 
				new nRun() {public void run() {
			pProcess proc = arg(0,pProcess.class);
			String ref = arg(1,String.class);
			int w = 2; 
			if (args.length > 2) w = arg(2,Integer.class);
			int w2 = 2; 
			if (args.length > 3) w2 = arg(3,Integer.class);
			if (proc == null) return;
			proc.run(getRun(CT.RUNP_OBTAIN_VAR), ref, 
					getParamOrDef("def", Float.class, 0f))
			.openSec()
				.param("ref", "field_"+ref, "var_link_ref", ref)
				.param("var_link_class", Float.class.getName())	
				.param("width", getParamOrDef("width", Integer.class, w)) 
				.commande(getCom(CT.COM_ADD_FIELD))
			.closeSec()
			.openSec()
				.param("ref", "slide_"+ref, "var_link_ref", ref)
				.param("var_link_class", Float.class.getName())	
				.param("width", getParamOrDef("width", Integer.class, w2)) 
				.commande(getCom(CT.COM_ADD_SLIDE))
			.closeSec()
			;
		}});

		newRunTool("run_var_flt_lab_field_slide", CT.RUNP_VAR_FLT_LAB_FIELD_SLIDE, 
				new nRun() {public void run() {
			pProcess proc = arg(0,pProcess.class);
			String ref = arg(1,String.class);
			String text = ""+ref; 
			if (args.length > 2) text = arg(2,String.class);
			int w = 2; 
			if (args.length > 3) w = arg(3,Integer.class);
			boolean row = false; 
			if (args.length > 4) row = arg(4,Boolean.class);
			if (proc == null) return;
			proc.run(getRun(CT.RUNP_OBTAIN_VAR), ref, 
					getParamOrDef("def", Float.class, 0f))
			.openSec()
				.param("ref", "label_"+ref, "text", text)	
				.param("width", getParamOrDef("width", Integer.class, w)) 
				.commande(getCom(CT.COM_ADD_WIDGET))
			.closeSec()
			.openSec()
				.param("ref", "field_"+ref, "var_link_ref", ref)
				.param("var_link_class", Float.class.getName())	
				.param("width", getParamOrDef("width", Integer.class, w)) 
				.commande(getCom(CT.COM_ADD_FIELD))
			.closeSec();
			if (row) proc.commande(getCom(CT.COM_ADD_ROW)); 
			if (row) w *= 2.0f;
			proc.openSec()
				.param("ref", "slide_"+ref, "var_link_ref", ref)
				.param("var_link_class", Float.class.getName())	
				.param("width", getParamOrDef("width", Integer.class, w)) 
				.commande(getCom(CT.COM_ADD_SLIDE))
			.closeSec()
			;
		}});

		
		
		
		
		
		
		
		newRunTool("run_add_co_out", CT.RUNS_ADD_CO_OUT, 
				new nRun() {public void run() {
			
			pStandard stand = arg(0,pStandard.class);
			String ref = arg(1,String.class);
			if (stand == null || ref == null) return;
			
			String text = ""+ref; 
			if (args.length > 2) text = arg(2,String.class);
			pProcess proc = stand.process();
			
			ArrayList<String> keys = new ArrayList<String>();
			ArrayList<String> filters = new ArrayList<String>();
			if (hasParam("key", String.class)) 
				keys.add(getParam("key", String.class));
			if (hasParam("filter", String.class)) 
				filters.add(getParam("filter", String.class));
			if (hasParam("keys", String[].class)) {
				String[] k = getParam("keys", String[].class);
				for (String s : k) keys.add(s); }
			if (hasParam("filters", String[].class)) {
				String[] k = getParam("filters", String[].class);
				for (String s : k) filters.add(s); }

			if (!Utl.contains(keys, "out")) keys.add("out");
			if (!Utl.contains(filters, "in")) filters.add("in");
			
			String[] keys_arr = new String[keys.size()];
			String[] filters_arr = new String[filters.size()];

			for (int i = 0 ; i < keys.size() ; i++) keys_arr[i] = keys.get(i);
			for (int i = 0 ; i < filters.size() ; i++) filters_arr[i] = filters.get(i);
			CoDef pd = new CoDef(ref, keys_arr, filters_arr);
			node_model_cos.get(stand.ref).add(pd);
			
			proc.openSec()
				.param("ref", ref, "text", text)	
				.param("keys", keys_arr, "filters", filters_arr)
				.run(getRun(CT.RUNP_ADD_CO), ref, "right")
			.closeSec();
		}});

		newRunTool("run_add_co_in", CT.RUNS_ADD_CO_IN, 
				new nRun() {public void run() {
			pStandard stand = arg(0,pStandard.class);
			String ref = arg(1,String.class);
			if (stand == null || ref == null) return;
			String text = ""+ref; 
			if (args.length > 2) text = arg(2,String.class);
			pProcess proc = stand.process();

			ArrayList<String> keys = new ArrayList<String>();
			ArrayList<String> filters = new ArrayList<String>();
			if (hasParam("key", String.class)) 
				keys.add(getParam("key", String.class));
			if (hasParam("filter", String.class)) 
				filters.add(getParam("filter", String.class));
			if (hasParam("keys", String[].class)) {
				String[] k = getParam("keys", String[].class);
				for (String s : k) keys.add(s); }
			if (hasParam("filters", String[].class)) {
				String[] k = getParam("filters", String[].class);
				for (String s : k) filters.add(s); }

			if (!Utl.contains(keys, "in")) keys.add("in");
			if (!Utl.contains(filters, "out")) filters.add("out");
			
			String[] keys_arr = new String[keys.size()];
			String[] filters_arr = new String[filters.size()];

			for (int i = 0 ; i < keys.size() ; i++) keys_arr[i] = keys.get(i);
			for (int i = 0 ; i < filters.size() ; i++) filters_arr[i] = filters.get(i);
			CoDef pd = new CoDef(ref, keys_arr, filters_arr);
			node_model_cos.get(stand.ref).add(pd);
			
			proc.openSec()
				.param("ref", ref, "text", text)
				.param("keys", keys_arr, "filters", filters_arr)	
				.run(getRun(CT.RUNP_ADD_CO), ref, "left")
			.closeSec();
		}});

		
		
		
		
		
		
		
		newRunTool("add_chain_start_plug", CT.RUNS_ADD_CHAIN_START_PLUG, new nRun() {public void run() {
			pStandard stand = arg(0,pStandard.class);
			String ref = arg(1,String.class);
			String side = arg(2,String.class);
			if (stand == null || ref == null) return;
			
			ArrayList<String> keys = new ArrayList<String>();
			ArrayList<String> filters = new ArrayList<String>();
			if (hasParam("key", String.class)) 
				keys.add(getParam("key", String.class));
			if (hasParam("filter", String.class)) 
				filters.add(getParam("filter", String.class));
			if (hasParam("keys", String[].class)) {
				String[] k = getParam("keys", String[].class);
				for (String s : k) keys.add(s); }
			if (hasParam("filters", String[].class)) {
				String[] k = getParam("filters", String[].class);
				for (String s : k) filters.add(s); }

			if (!Utl.contains(keys, "chain")) keys.add("chain");
			if (!Utl.contains(filters, "chain")) filters.add("chain");
			
			String[] keys_arr2 = new String[keys.size() + 1];
			String[] filters_arr2 = new String[filters.size() + 1];
			for (int i = 0 ; i < keys.size() ; i++) keys_arr2[i] = keys.get(i);
			for (int i = 0 ; i < filters.size() ; i++) filters_arr2[i] = filters.get(i);
			keys_arr2[keys.size()] = "out";
			filters_arr2[filters.size()] = "in";
			PlugDef pd2 = new PlugDef(ref+"_out", keys_arr2, filters_arr2);
			node_model_plugs.get(stand.ref).add(pd2);

			stand.newRun("get_chain_last", new nRun() {public Object get() {
				pInstance last = instance;
				boolean as_next = instance.get("plug_obtain_node", pInstance.class, ref+"_out") != null;
				while (as_next) {
					if (last.get("plug_obtain_node", pInstance.class, ref+"_out") != null) {
						last = last.get("plug_obtain_node", pInstance.class, ref+"_out");
						as_next = true;
					} else { as_next = false; }
				}
				return last;
			}})
			.newRun("get_all_chain", new nRun() {public Object get() {
				ArrayList<pInstance> arr = new ArrayList<pInstance>();
				arr.add(instance);
				pInstance last = instance;
				boolean as_next = instance.get("plug_obtain_node", pInstance.class, ref+"_out") != null;
				while (as_next) {
					if (last.get("plug_obtain_node", pInstance.class, ref+"_out") != null) {
						last = last.get("plug_obtain_node", pInstance.class, ref+"_out");
						as_next = true;
						arr.add(last);
					} else { as_next = false; }
				}
				return arr;
			}})
			.replaceRun("clear_trigg", new nRun() {public void run() {
				ArrayList<pInstance> chains = new ArrayList<pInstance>();
				pInstance last = instance;
				boolean as_next = instance.get("plug_obtain_node", pInstance.class, ref+"_out") != null;
				while (as_next) {
					if (last.get("plug_obtain_node", pInstance.class, ref+"_out") != null) {
						last = last.get("plug_obtain_node", pInstance.class, ref+"_out");
						chains.add(last);
						as_next = true;
					} else { as_next = false; }
				}
				for (int i = chains.size() - 1 ; i >= 0 ; i--) {
					chains.get(i).clear(); }
				instance.clear();
			}});
			
			stand.run(getRun(CT.RUNS_ADD_ATTRACT_PLUGGED));
			
			pProcess proc = stand.process();
			proc.openSec()
			.param("event_link", new nRun() {public void run() {
				pInstance node = instance.getInst("node");
				if (node == null) return;
				pInstance next_node = node.get("plug_obtain_node", pInstance.class, ref+"_out");
				if (next_node != null) next_node.run("set_chain_head", node);
			}})
			.param("keys", keys_arr2, "filters", filters_arr2)
			.run(getRun(CT.RUNP_ADD_PLUG), ref+"_out", side).closeSec();
			
		}});
		
		newRunTool("add_chain_plug", CT.RUNS_ADD_CHAIN_PLUGS, new nRun() {public void run() {
			pStandard stand = arg(0,pStandard.class);
			String ref = arg(1,String.class);
			String side = arg(2,String.class);
			if (stand == null || ref == null) return;
			
			ArrayList<String> keys = new ArrayList<String>();
			ArrayList<String> filters = new ArrayList<String>();
			if (hasParam("key", String.class)) 
				keys.add(getParam("key", String.class));
			if (hasParam("filter", String.class)) 
				filters.add(getParam("filter", String.class));
			if (hasParam("keys", String[].class)) {
				String[] k = getParam("keys", String[].class);
				for (String s : k) keys.add(s); }
			if (hasParam("filters", String[].class)) {
				String[] k = getParam("filters", String[].class);
				for (String s : k) filters.add(s); }

			if (!Utl.contains(keys, "chain")) keys.add("chain");
			if (!Utl.contains(filters, "chain")) filters.add("chain");
			
			String[] keys_arr1 = new String[keys.size() + 1];
			String[] filters_arr1 = new String[filters.size() + 1];
			for (int i = 0 ; i < keys.size() ; i++) keys_arr1[i] = keys.get(i);
			for (int i = 0 ; i < filters.size() ; i++) filters_arr1[i] = filters.get(i);
			keys_arr1[keys.size()] = "in";
			filters_arr1[filters.size()] = "out";
			PlugDef pd1 = new PlugDef(ref+"_in", keys_arr1, filters_arr1);
			node_model_plugs.get(stand.ref).add(pd1);

			String[] keys_arr2 = new String[keys.size() + 1];
			String[] filters_arr2 = new String[filters.size() + 1];
			for (int i = 0 ; i < keys.size() ; i++) keys_arr2[i] = keys.get(i);
			for (int i = 0 ; i < filters.size() ; i++) filters_arr2[i] = filters.get(i);
			keys_arr2[keys.size()] = "out";
			filters_arr2[filters.size()] = "in";
			PlugDef pd2 = new PlugDef(ref+"_out", keys_arr2, filters_arr2);
			node_model_plugs.get(stand.ref).add(pd2);
			
			stand.addInst("chain_head", "inst")
			.newRun("get_chain_head", new nRun() {public Object get() {
				return instance.getInst("chain_head");
			}})
			.newRun("set_chain_head", new nRun() {public void run() {
				pInstance head = arg(0,pInstance.class);
				if (head == null) return;
				instance.setInst("chain_head", head);
				pInstance next_node = instance.get("plug_obtain_node", pInstance.class, ref+"_out");
				if (next_node != null) next_node.run("set_chain_head", head);
			}})
			.replaceRun("clear_trigg", new nRun() {public void run() {
				pInstance prev_node = instance.get("plug_obtain_node", 
						pInstance.class, ref+"_in");
				pInstance next_node = instance.get("plug_obtain_node", 
						pInstance.class, ref+"_out");
				if (prev_node != null && next_node != null) {
					pInstance in_plug = instance.get("get_plug", 
							pInstance.class, ref+"_in");
					pInstance out_plug = instance.get("get_plug", 
							pInstance.class, ref+"_out");
					if (in_plug != null && out_plug != null) {
						pInstance next_in_plug = next_node.get("get_plug", 
								pInstance.class, ref+"_in");
						pInstance prev_out_plug = prev_node.get("get_plug", 
								pInstance.class, ref+"_out");
						if (next_in_plug != null && prev_out_plug != null) {
							next_in_plug.run("unlink", out_plug);
							prev_out_plug.run("unlink", in_plug);
							next_in_plug.run("link_to", prev_out_plug);
						}
					}
				}
				instance.clear();
			}});
			
			pProcess proc = stand.process();
			proc.openSec()
			.param("event_link", new nRun() {public void run() {
				pInstance node = instance.getInst("node");
				if (node == null) return;
				pInstance head = node.get("get_chain_head", pInstance.class);
				node.run("set_chain_head", head);
			}}).param("keys", keys_arr2, "filters", filters_arr2)
			.run(getRun(CT.RUNP_ADD_PLUG), ref+"_out", side).closeSec();
			if (side.equals("bottom")) side = "top";
			else if (side.equals("top")) side = "bottom";
			else if (side.equals("left")) side = "right";
			else if (side.equals("right")) side = "left";
			proc.openSec().param("keys", keys_arr1, "filters", filters_arr1)
			.run(getRun(CT.RUNP_ADD_PLUG), ref+"_in", side).closeSec();
			
		}});
		
		newRunTool("add_obtain_plug", CT.RUNS_ADD_OBTAIN_PLUG, new nRun() {public void run() {
			pStandard stand = arg(0,pStandard.class);
			String ref = arg(1,String.class);
			if (stand == null || ref == null) return;
			
			ArrayList<String> keys = new ArrayList<String>();
			ArrayList<String> filters = new ArrayList<String>();
			if (hasParam("key", String.class)) 
				keys.add(getParam("key", String.class));
			if (hasParam("filter", String.class)) 
				filters.add(getParam("filter", String.class));
			if (hasParam("keys", String[].class)) {
				String[] k = getParam("keys", String[].class);
				for (String s : k) keys.add(s); }
			if (hasParam("filters", String[].class)) {
				String[] k = getParam("filters", String[].class);
				for (String s : k) filters.add(s); }

			if (!Utl.contains(keys, "obtain")) keys.add("obtain");
			if (!Utl.contains(filters, "offer")) filters.add("offer");
			
			String[] keys_arr = new String[keys.size()];
			String[] filters_arr = new String[filters.size()];

			for (int i = 0 ; i < keys.size() ; i++) keys_arr[i] = keys.get(i);
			for (int i = 0 ; i < filters.size() ; i++) filters_arr[i] = filters.get(i);
			PlugDef pd = new PlugDef(ref, keys_arr, filters_arr);
			node_model_plugs.get(stand.ref).add(pd);
			
			stand.process().openSec().param("keys", keys_arr, "filters", filters_arr)
			.run(getRun(CT.RUNP_ADD_PLUG), ref, "left").closeSec();
			
		}});

		newRunTool("add_offer_plug", CT.RUNS_ADD_OFFER_PLUG, new nRun() {public void run() {
			pStandard stand = arg(0,pStandard.class);
			String ref = arg(1,String.class);
			if (stand == null || ref == null) return;

			ArrayList<String> keys = new ArrayList<String>();
			ArrayList<String> filters = new ArrayList<String>();
			if (hasParam("key", String.class)) 
				keys.add(getParam("key", String.class));
			if (hasParam("filter", String.class)) 
				filters.add(getParam("filter", String.class));
			if (hasParam("keys", String[].class)) {
				String[] k = getParam("keys", String[].class);
				for (String s : k) keys.add(s); }
			if (hasParam("filters", String[].class)) {
				String[] k = getParam("filters", String[].class);
				for (String s : k) filters.add(s); }

			if (!Utl.contains(keys, "offer")) keys.add("offer");
			if (!Utl.contains(filters, "obtain")) filters.add("obtain");
			
			String[] keys_arr = new String[keys.size()];
			String[] filters_arr = new String[filters.size()];

			for (int i = 0 ; i < keys.size() ; i++) keys_arr[i] = keys.get(i);
			for (int i = 0 ; i < filters.size() ; i++) filters_arr[i] = filters.get(i);
			PlugDef pd = new PlugDef(ref, keys_arr, filters_arr);
			node_model_plugs.get(stand.ref).add(pd);
			
			pProcess proc = stand.process();
			proc.openSec();
			if (hasParam("offer")) {
				nRun pr = getParam("offer", nRun.class);
				proc.param("offer", pr); }
			proc.param("keys", keys_arr, "filters", filters_arr)
			.run(getRun(CT.RUNP_ADD_PLUG), ref, "right").closeSec();
		}});
		
		newRunTool("run_add_attract_plugged", CT.RUNS_ADD_ATTRACT_PLUGGED, 
				new nRun() {public void run() {
			pStandard stand = arg(0,pStandard.class);
			pProcess proc = stand.process();
			proc.commande(new nRun() {public void run() { 
				nRun run_frame = new nRun(instance) {public void run() { 
					pInstance inst = (pInstance)builder;
					inst.run("attract_plugged");
				}};
				instance.addObject("run_frame", run_frame);
				instance.patch.addEventFrame(run_frame);
			}})
			.useClear().commande(new nRun() {public void run() { 
				instance.patch.removeEventFrame(
						instance.object("run_frame", nRun.class));
			}}).useInit();
		}});

		
		
		
		
		
		
		
		
		newRunTool("run_add_trigg", CT.RUNP_ADD_TRIGG, 
				new nRun() {public void run() {
			pProcess proc = arg(0,pProcess.class);
			String ref = arg(1,String.class);
			String text = arg(2,String.class);
			int w = arg(3,Integer.class);
			if (proc == null) return;
			proc.openSec()
				.param("ref", ref, "text", text, "width", w)	
				.commande(getCom(CT.COM_ADD_TRIGG))
			.closeSec()
			;
		}});

		newRunTool("run_add_label", CT.RUNP_ADD_LABEL, 
				new nRun() {public void run() {
			pProcess proc = arg(0,pProcess.class);
			String ref = arg(1,String.class);
			int w = arg(2,Integer.class);
			if (proc == null) return;
			proc.openSec()
				.param("ref", "label_"+ref, "text", ref, "width", w)	
				.commande(getCom(CT.COM_ADD_WIDGET))
			.closeSec()
			;
		}});

		newRunTool("run_obtain_var", CT.RUNP_OBTAIN_VAR, new nRun() {public void run() {
			pProcess proc = arg(0,pProcess.class);
			String ref = arg(1,String.class);
			Object def = arg(2,Object.class);
			if (ref == null || def == null) return;
			if (proc == null) return;
			proc.commande(new nRun() { public void run() {
				if (!instance.hasVar(ref)) { instance.addVar(ref, def); } }});
		}});

		
		
		
		
		
		
		newComTool("row", CT.COM_ADD_ROW, new nRun() {public void run() {
			instance.object("interf", nInterface.class).add_row(); 
		}});
	
		newComTool("col", CT.COM_ADD_COL, new nRun() {public void run() {
			instance.object("interf", nInterface.class).add_col(); 
			instance.object("interf", nInterface.class).add_row(); 
		}});
	
		newComTool("add_widget", CT.COM_ADD_WIDGET, nWidget.class, new nRun() {public Object get() {
			
//			app.log("com_add_widget "+instance.pool_ref);
	
			int width = getParamOrDef("width", Integer.class, (int)2);
			float height = getParamOrDef("height", Float.class, 1f);
			String text = getParamOrDef("text", String.class, "");
			String info = getParamOrDef("info", String.class, "");

			float scale_min = getParamOrDef("scale_min", Float.class, DEF_SCALE_MIN);
			float scale_max = getParamOrDef("scale_max", Float.class, DEF_SCALE_MAX);

			boolean big_scale_min = getParamOrDef("scale_min_big", Boolean.class, false);
			if (big_scale_min) scale_min = SCALE_MIN_BIG;
			
			nInterface interf = instance.object("interf", nInterface.class);
			
			nWidget w = interf.add_row_label(1,text);

			w.copyColorFrom(nGUI.book.getModel("ref"));
			
			w.setBoundParent(true);
			w.setScaleLimitNoDraw(scale_min, scale_max);
			w.setText(text);
			w.setInfo(info);
			w.setSX(w.getLocalSX()*width/2f);
			w.setSY(w.getLocalSY()*height);
//			w.set_color_background(Utl.color((int)(255*w.color_background.r), 
//					(int)(255*w.color_background.g), 
//					(int)(255*w.color_background.b),
//					(int)255));
			
			if (hasParam("custom_drawer")) {
				nRun pr = getParam("custom_drawer", nRun.class);
				nDrawable dr = new nDrawable(instance,param) {public void drawing() { 
					pr.do_run((pInstance)args[0],(pPar)args[1]); }};
				if (dr != null) w.setCustomDrawer(dr);
			}
			if (hasParam("logic_event")) {
				nRun pr = getParam("logic_event", nRun.class);
				nRun dr = new nRun(instance,param) {public void run() { 
					pr.do_run((pInstance)args[0],(pPar)args[1]); }};
				if (dr != null) w.addEventLogic(dr);
			}

			if (hasParam("ref")) { 
				nMap<nWidget> widget_map = instance.object("widget_map", nMap.class);
				if (widget_map != null) widget_map.put(getParam("ref", String.class),w);
			}
			
			return w;
		}}).set_return(nWidget.class);

		newComTool("add_trigg", CT.COM_ADD_TRIGG, nWidget.class, new nRun() {public Object get() {
	
	//		app.log("com_add_trigg");
			
			nWidget w = getCom(CT.COM_ADD_WIDGET,instance,param,nWidget.class);
			w.setTrigger();
			if (hasParam("run", nRun.class)) {
				nRun run = getParam("run", nRun.class);
				w.addEventTrigger(new nRun(instance,param,run) {public void run() {
					((nRun)args[2]).do_run((pInstance)args[0],(pPar)args[1]); }});
			}
			return w;
		}});
	
		newComTool("add_switch", CT.COM_ADD_SWITCH, nWidget.class, new nRun() { public Object get() {
			nWidget w = getCom(CT.COM_ADD_WIDGET,instance,param,nWidget.class);
			w.setSwitch();
			if (hasParam("var_link_ref", String.class)) { 
				if (!instance.hasVar(getParam("var_link_ref", String.class))) return w;
				String var_ref = getParam("var_link_ref", String.class);
				w.addEventSwitch(new nRun(instance) {public void run() {
					pInstance target = (pInstance)builder;
					target.setVar(var_ref, w.isOn());	}});
				nRun sw_run = new nRun(instance) {public void run() {
					pInstance target = (pInstance)builder;
					if (target.getVar(var_ref, Boolean.class) == null) return;
					boolean data = target.getVar(var_ref, Boolean.class);
					if (w.isOn() != data) w.setSwitchState(data); }};
				w.addEventLogic(sw_run); sw_run.run();
			}
			if (hasParam("run", nRun.class)) {
				nRun run = getParam("run", nRun.class);
				w.addEventSwitch(new nRun(instance,param,run) {public void run() {
					((nRun)args[2]).do_run((pInstance)args[0],(pPar)args[1]); }});
			}
			return w;
		}});
	
		newComTool("add_field", CT.COM_ADD_FIELD, nWidget.class, new nRun() {public Object get() {

//			app.log("com_add_field "+instance.pool_ref);
	
			nWidget w = getCom(CT.COM_ADD_WIDGET,instance,param,nWidget.class);
			w.setField(true)
			.copyLookFrom(nGUI.book.getModel("text_field"));
			if (hasParam("run_right", nRun.class)) {
				w.setRightTrigger();
				w.copyLookFrom(PlaneApplet.app.gui.book.getModel("CL_right_trigg"));
				nRun run = getParam("run_right", nRun.class);
				w.addEventTriggerRight(new nRun(instance,param,run) {public void run() {
					((nRun)args[2]).do_run((pInstance)args[0],(pPar)args[1]); }});
			}
			int float_rez = (int)(1.2f * w.getLocalSX() / w.getFont()) - 3;
			if (hasParam("var_link_ref", String.class) && 
					hasParam("var_link_class", String.class)) { 
				if (!instance.hasVar(getParam("var_link_ref", String.class))) return w;
				String var_ref = getParam("var_link_ref", String.class);
				String var_cls = getParam("var_link_class", String.class);
				String var_axe = getParam("var_link_vec_axe", String.class);
				w.addEventFieldChange(new nRun(instance) {public void run() {
					pInstance target = (pInstance)builder;
					if (var_cls.equals(Float.class.getName())) {
						target.setVar(var_ref, Utl.tofloat(w.getText()));
					} else if (var_cls.equals(Integer.class.getName())) {
						target.setVar(var_ref, Utl.toint(w.getText()));
					} else if (var_cls.equals(String.class.getName())) {
						target.setVar(var_ref, w.getText());
					} else if (var_cls.equals(Vector2.class.getName()) && var_axe != null) {
						Vector2 v = new Vector2(target.getVar( var_ref, Vector2.class));
						float n = Utl.tofloat(w.getText());
						if (var_axe.equals("x"))
							v.x = n; else v.y = n;
						target.setVar(var_ref, v);
					} 
				}});
				nRun sl_run = new nRun(instance, float_rez) {public void run() {
					pInstance target = (pInstance)args[0];
					int frez = (int)args[1];
					String text = w.getText();
//					if (hasParam("text")) text = getParam("text", String.class);
					if (var_cls.equals(Float.class.getName())) {
						text = "" + Utl.trimFlt(target.getVar(var_ref, Float.class), frez);
					} else if (var_cls.equals(Integer.class.getName())) {
						text = "" + target.getVar(var_ref, Integer.class);
					} else if (var_cls.equals(String.class.getName())) {
						text = "" + target.getVar(var_ref, String.class);
					} else if (var_cls.equals(Vector2.class.getName()) && var_axe != null) {
						Vector2 v = new Vector2(target.getVar(var_ref, Vector2.class));
						if (var_axe.equals("x")) text = "" + Utl.trimFlt(v.x, frez); 
						else text = "" + Utl.trimFlt(v.y, frez);
					} 
					if (
						//!w.isSelected && 
						!text.equals(w.getText())) w.setText(text);
				}};
				w.addEventLogic(sl_run); sl_run.run();
			}
			if (hasParam("run", nRun.class)) {
				nRun run = getParam("run", nRun.class);
				w.addEventFieldChange(new nRun(instance,param,run) {public void run() {
					((nRun)args[2]).do_run((pInstance)args[0],(pPar)args[1]); }});
			}
			return w;
		}});
	
		newComTool("add_watch", CT.COM_ADD_WATCH, nWidget.class, new nRun() { public Object get() {
			nWidget w = getCom(CT.COM_ADD_WIDGET,instance,param,nWidget.class);
			int float_rez = (int)(1.2f * w.getLocalSX() / w.getFont()) - 3;
			if (hasParam("run_right", nRun.class)) {
				w.setRightTrigger();
				w.copyLookFrom(PlaneApplet.app.gui.book.getModel("CL_right_trigg"));
				nRun run = getParam("run_right", nRun.class);
				w.addEventTriggerRight(new nRun(instance,param,run) {public void run() {
					((nRun)args[2]).do_run((pInstance)args[0],(pPar)args[1]); }});
			}
			if (hasParam("var_link_ref", String.class) && 
					hasParam("var_link_class", String.class)) { 
				if (!instance.hasVar(getParam("var_link_ref", String.class))) return w;
				String var_ref = getParam("var_link_ref", String.class);
				String var_cls = getParam("var_link_class", String.class);
				String var_txt = getParam("text", String.class);
				nRun sl_run = new nRun(instance, float_rez) {public void run() {
					pInstance target = (pInstance)args[0];
					int frez = (int)args[1];
					String text = "";
					if (var_txt != null) text = Utl.copy(var_txt);
					if (var_cls.equals(Float.class.getName())) {
						text += Utl.trimFlt(target.getVar(var_ref, Float.class), frez);
					} else if (var_cls.equals(Integer.class.getName())) {
						text += target.getVar(var_ref, Integer.class);
					} else if (var_cls.equals(Boolean.class.getName())) {
						text += target.getVar(var_ref, Boolean.class);
					} else if (var_cls.equals(String.class.getName())) {
						text += target.getVar(var_ref, String.class);
					} else if (var_cls.equals(Vector2.class.getName())) {
						text += Utl.tostr(target.getData(var_ref, Vector2.class));
					}
					w.setText(text);
				}};
				w.addEventLogic(sl_run); sl_run.run();
			}
			return w;
		}});
	
		newComTool("add_slide", CT.COM_ADD_SLIDE, nWidget.class, new nRun() {public Object get() {
			
	//		app.log("com_add_slide");
	
			int width = getParamOrDef("width", Integer.class, (int)2);
			float height = getParamOrDef("height", Float.class, 1f);
			float min = getParamOrDef("min", Float.class, 0f);
			float max = getParamOrDef("max", Float.class, 1f);

			float scale_min = getParamOrDef("scale_min", Float.class, DEF_SCALE_MIN);
			float scale_max = getParamOrDef("scale_max", Float.class, DEF_SCALE_MAX);
			
			nInterface interf = instance.object("interf", nInterface.class);
			nWidget w = interf.add_row_slide(1,min,max);

			w.copyColorFrom(PlaneApplet.app.gui.book.getModel("ref"));
			
			w.setSX(w.getLocalSX()*width/2f);
			w.setSY(w.getLocalSY()*height);
			w.setScaleLimitNoDraw(scale_min, scale_max);
//			w.set_color_background(Utl.color((int)(255*w.color_background.r), 
//					(int)(255*w.color_background.g), 
//					(int)(255*w.color_background.b),
//					(int)255));
			
			if (hasParam("granulo", Float.class)) 
				w.setSliderGranulo(getParam("granulo", Float.class));

			if (hasParam("logic_event")) {
				nRun pr = getParam("logic_event", nRun.class);
				nRun dr = new nRun(instance,param) {public void run() { 
					pr.do_run((pInstance)args[0],(pPar)args[1]); }};
				if (dr != null) w.addEventLogic(dr);
			}

			if (hasParam("ref", String.class)) { 
				nMap<nWidget> widget_map = instance.object("widget_map", nMap.class);
				if (widget_map != null) widget_map.put(getParam("ref", String.class),w);
			}
			
			if (hasParam("var_link_ref", String.class) && 
					hasParam("var_link_class", String.class)) { 
				if (!instance.hasVar(getParam("var_link_ref", String.class))) return w;
				String var_ref = getParam("var_link_ref", String.class);
				String var_cls = getParam("var_link_class", String.class);
				String var_axe = getParam("var_link_vec_axe", String.class);
				w.addEventSliderChange(new nRun(instance) {public void run() {
					pInstance target = (pInstance)builder;
					if (var_cls.equals(Float.class.getName())) {
						target.setVar(var_ref, w.getSliderValInMinMax());
					} else if (var_cls.equals(Integer.class.getName())) {
						target.setVar(var_ref, Utl.toint(w.getSliderValInMinMax()));
					} else if (var_cls.equals(Vector2.class.getName()) && var_axe != null) {
						Vector2 v = new Vector2(target.getVar(var_ref, Vector2.class));
						float n = w.getSliderValInMinMax();
						if (var_axe.equals("x")) v.x = n; else v.y = n;
						target.setVar(var_ref, v);
					} 
				}});
				nRun sl_run = new nRun(instance) {public void run() {
					if (w.isSliderGrabbed) return; 
					pInstance target = (pInstance)builder;
					float vl = w.getSliderValInMinMax();
					if (var_cls.equals(Float.class.getName())) {
						vl = target.getVar(var_ref, Float.class);
					} else if (var_cls.equals(Integer.class.getName())) {
						vl = target.getVar(var_ref, Integer.class);
					} else if (var_cls.equals(Vector2.class.getName()) && var_axe != null) {
						Vector2 v = new Vector2(target.getVar(var_ref, Vector2.class));
						if (var_axe.equals("x")) vl = v.x; else vl = v.y;
					} 
					if (vl != w.getSliderValInMinMax()) w.setSliderValInRange(vl);
				}};
				w.addEventLogic(sl_run); sl_run.run();
			}
			if (hasParam("run", nRun.class)) {
				nRun run = getParam("run", nRun.class);
				w.addEventSliderChange(new nRun(instance,param,run) {public void run() {
					((nRun)args[2]).do_run((pInstance)args[0],(pPar)args[1]); }});
			}
			return w;
		}}).set_return(nWidget.class);
			
	}
	
	

	

	public static nMap<pStandard> node_models = new nMap<pStandard>();
	public static nMap<pStandard> buildable_node_models = new nMap<pStandard>();

	public static nMap<String> node_group = new nMap<String>();
	public static HashMap<pStandard,String> stand_to_ref = new HashMap<pStandard,String>();

	public static pStandard newChainnedNodeModel(String r) {
		return newNodeModel(r, "chainned", false); }
	
	public static pStandard newNodeModel(String r) {
		return newNodeModel(r, "base", true); }
	public static pStandard newNodeModel(String r, String g) {
		return newNodeModel(r, g, true); }
	public static pStandard newNodeModel(String r, boolean buildable) {
		return newNodeModel(r, "base", buildable); }
	public static pStandard newNodeModel(String r, String g, boolean buildable) {
		if (node_models.hasKey(r)) return null;
		pStandard p = pStandard.newStandard("node_model_"+r, "inst");
		node_models.put(r,p);
		node_group.put(r,g);
		stand_to_ref.put(p,r);
		if (buildable) buildable_node_models.put(r,p);
		ArrayList<CoDef> cd = new ArrayList<CoDef>();
		node_model_cos.put("node_model_"+r,cd);
		ArrayList<PlugDef> pd = new ArrayList<PlugDef>();
		node_model_plugs.put("node_model_"+r,pd);
		p.copy(pStandard.get("node"));
		return p;
	}
	public static pStandard getNodeModel(String r) { return node_models.get(r); }
	public static String getNodeModel(pStandard r) { return stand_to_ref.get(r); }
	
	
	
	public static nMap<ArrayList<CoDef>> node_model_cos = new nMap<ArrayList<CoDef>>();

	public static ArrayList<CoDef> getNodeModelCos(String r) { 
		return node_model_cos.get(r); }

	public static ArrayList<CoDef> getNodeModelCos(pStandard r) { 
		return node_model_cos.get(r.ref); }
	
	static class CoDef {
		String ref;
		String[] keys, filters;
		public CoDef(String r, String[] k, String[] f) { ref = r; keys = k; filters = f; }
	}
	
	
	public static nMap<ArrayList<PlugDef>> node_model_plugs = new nMap<ArrayList<PlugDef>>();

	public static ArrayList<PlugDef> getNodeModelPlugs(String r) { 
		return node_model_plugs.get(r); }

	public static ArrayList<PlugDef> getNodeModelPlugs(pStandard r) { 
		return node_model_plugs.get(r.ref); }
	
	static class PlugDef {
		String ref;
		String[] keys, filters;
		public PlugDef(String r, String[] k, String[] f) { ref = r; keys = k; filters = f; }
	}
	
	
	
	
	public static void runCom(CT cd, pInstance cont, pPar par, Object ... args) {
		pCommande c = com_tools.get(tool_refs.get(cd));
		c.run(cont,par,args); }

	public static <T> T getCom(CT cd, pInstance cont, pPar par, Class<T> ct, Object ... args) {
		pCommande c = com_tools.get(tool_refs.get(cd));
		return c.get(cont,par,ct,args); }

	public static void newRunTool(String r, CT cd, nRun rn) {
		if (run_tools.hasKey(r)) return;
		run_tools.put(r,rn); tool_codes.put(r,cd); tool_refs.put(cd,r); 
	}

	public static pCommande newComTool(String r, CT cd, nRun rn) {
		if (com_tools.hasKey(r)) return com_tools.get(r);
		pCommande c = pCommande.newCommande("com_node_tool_"+r,rn);
		com_tools.put(r,c); tool_codes.put(r,cd); tool_refs.put(cd,r); 
		return c; }

	public static pCommande newComTool(String r, CT cd, Class<?> ct, nRun rn) {
		if (com_tools.hasKey(r)) return com_tools.get(r);
		pCommande c = pCommande.newCommande("com_node_tool_"+r,ct,rn);
		com_tools.put(r,c); tool_codes.put(r,cd); tool_refs.put(cd,r); 
		return c; }

	public static pProcess newProcTool(String r, CT cd) {
		if (proc_tools.hasKey(r)) return proc_tools.get(r);
		pProcess c = pProcess.newProcess("proc_node_tool_"+r);
		proc_tools.put(r,c); tool_codes.put(r,cd); tool_refs.put(cd,r); 
		return c; }
	
	public static nMap<nRun> run_tools = new nMap<nRun>();
	public static nMap<pCommande> com_tools = new nMap<pCommande>();
	public static nMap<pProcess> proc_tools = new nMap<pProcess>();
	public static nMap<CT> tool_codes = new nMap<CT>();
	public static HashMap<CT,String> tool_refs = new HashMap<CT,String>();

	public static nRun getRun(CT cd) { return run_tools.get(tool_refs.get(cd)); }
	public static pCommande getCom(CT cd) { return com_tools.get(tool_refs.get(cd)); }
	public static pProcess getProc(CT cd) { return proc_tools.get(tool_refs.get(cd)); }
	
	public enum CT { 
		COM_ADD_WIDGET, COM_ADD_COL, COM_ADD_ROW, 
		COM_ADD_TRIGG, COM_ADD_SWITCH, COM_ADD_WATCH, COM_ADD_FIELD, COM_ADD_SLIDE, 
		RUNP_OBTAIN_VAR,
		RUNS_ADD_ATTRACT_PLUGGED,
		RUNP_ADD_PLUG,
		RUNS_ADD_OBTAIN_PLUG, RUNS_ADD_OFFER_PLUG, 
		RUNS_ADD_CHAIN_START_PLUG, RUNS_ADD_CHAIN_PLUGS, 
		RUNP_ADD_CO, RUNS_ADD_CO_IN, RUNS_ADD_CO_OUT, 
		RUNP_ADD_LABEL, RUNP_ADD_TRIGG,
		RUNP_VAR_STR_WATCH, 
		RUNP_VAR_BOO_SWITCH, RUNP_VAR_STR_FIELD, RUNP_VAR_INT_FIELD, RUNP_VAR_VEC_FIELD,
		RUNP_VAR_FLT_FIELD,
		RUNP_VAR_FLT_FIELD_SLIDE, 
		RUNP_VAR_FLT_LAB_FIELD, RUNP_VAR_STR_LAB_FIELD, RUNP_VAR_INT_LAB_FIELD,
		RUNP_VAR_VEC_LAB_FIELD,
		RUNP_VAR_FLT_LAB_FIELD_SLIDE
	};
	
	
	
	
	
	public static void build_book() {
		nModelBook book = nGUI.book;
		float RS = book.RS;
		

		book.newModel("NC_base")
		.setBoundParent(true)
		.setStacked(true)
		.setOutline(true)
		.setOutlineWeight(RS/10f)
		.set_color_pressed(Utl.color(20,20,255,255))
		.set_color_hovered(Utl.color(0,0,210,255))
		.set_color_standby(Utl.color(0,0,120,255))
		.set_color_outline(Utl.color(120,180,255,255))
		.setShape(nModel.Shape.CIRCLE)
		;
		book.newModel("NC_title")
		.copyColorFrom(book.getModel("ref"))
		.setOutline(true)
		.setOutlineWeight(RS/8f)
//		.setOutlineConstant(true)
		.set_color_outline(Utl.color(0))
//		.setOutline(true)
//		.setOutlineWeight(RS/10f)
//		.set_color_pressed(Utl.color(20,20,255,255))
//		.set_color_hovered(Utl.color(0,0,210,255))
//		.set_color_standby(Utl.color(0,0,120,255))
//		.set_color_outline(Utl.color(120,180,255,255))
		;
		

		book.newModel("NP_base")
		.setBoundParent(true)
		.setStacked(true)
		.setOutline(true)
		.setOutlineWeight(RS/10f)
		.set_color_pressed(Utl.color(20,20,255,255))
		.set_color_hovered(Utl.color(0,0,210,255))
		.set_color_standby(Utl.color(0,0,120,255))
		.set_color_outline(Utl.color(120,180,255,255))
		.setShape(nModel.Shape.DIAMOND)
		;
		
		
		book.newModel("PN_ref")
		.copyColorFrom(book.getModel("ref"))
		.setActAsRoot(true)
		.setBackground()
		.setBoundChild(true)
		.setHoverableZone(true)
		.setStackAxis(nAlign.VERTICAL) // HORIZONTAL   VERTICAL
		.setStackDirection(nAlign.UP) // RIGHT   LEFT   UP   DOWN
		.setRectOrigin(nAlign.CENTER,nAlign.TOP) // TOP   BOTTOM
		.setBoundOutspace(2)
		.setStackSpacing(0)
//		.setOutline(true)
//		.setOutlineWeight(2)
//		.set_color_outline(Utl.color(20,20,120))
//		.setOutlineAfterChild(true)
		.set_color_background(Utl.color(0,0))
		.setDraw(false)
		;

		book.newModel("PN_selline")
		.setPassif()
		.setRectOrigin(nAlign.LEFT,nAlign.BOTTOM) // TOP   BOTTOM
		.setOutline(false)
		.setOutlineWeight(RS/15f)
		.setOutlineConstant(true)
		.set_color_outline(Utl.color(200,100,0))
		.set_color_background(Utl.color(0,0))
		;

		nModel PN_back = book.newModel("PN_back")
		.copyColorFrom(book.getModel("ref"))
		.setHoverable()
		.setBoundParent(true)
		.setBoundChild(true)
		.setStacked(true)
		.setStackAxis(nAlign.HORIZONTAL) // HORIZONTAL   VERTICAL
		.setStackDirection(nAlign.RIGHT) // RIGHT   LEFT   UP   DOWN
		.setRectOrigin(nAlign.LEFT,nAlign.BOTTOM) // TOP   BOTTOM
		.setBoundOutspace(RS/4f)
		.setStackSpacing(RS/15f)
//		.setOutline(true)
		.setOutlineWeight(RS/4f)
//		.setOutlineConstant(true)
		.setTextAlignment(nAlign.LEFT, nAlign.CENTER)
		.setFont(RS*5f/4f)
		.setMask(true)
		;
		if (PlaneApplet.app.config.RELEASE) 
			PN_back.setOutline(true)
			.set_color_outline(Utl.color(0));
		

		book.newModel("PN_costack")
		.setBoundChild(true)
		.setBoundOutspace(0)
		.setStackSpacing(0)
		.set_color_background(Utl.color(0,0))
		.setDraw(false)
		.setPassif()
		;
		
		book.newModelGroup("patch_node", new nModelGroup() { 
			public nWidgetGroup build(nGUI gui) {
				nWidgetGroup g = gui.addWidgetGroup();

				nWidget ref = g.addWidget("ref", "PN_ref");
				
				nWidget back = g.addWidget("back", "PN_back");
				back.setParent(ref);
				
//				back.setHelper("help_1");

				nWidget selline = g.addWidget("selline", "PN_selline")
						.setParent(ref);

				nInterface interf = PlaneApplet.app.gui.addInterface() 
						.pop(back);
				g.addObject("interf", interf);
				
				g.addMetode("get_interf", new nRun() {
					public Object get() { return interf; } });
				
				g.addObject("widg_nb", (int)0);
				g.addMetode("add_widget", new nRun() { public Object get(Object o) { 
					int width = (int)o;
					int widg_nb = g.object("widg_nb", Integer.class);
					nWidget ent = g.addWidget("ent_"+widg_nb, "INT_row_entry_"+width);
					widg_nb++;
					g.setObject("widg_nb", widg_nb);
					return ent; 
				} });

				g.addMetode("move", new nRun() { public void run(Object o) { 
					Vector2 p = (Vector2)o;
					pInstance node = g.object("node", pInstance.class);
					p.add(node.getDataVec("pos"));
					node.setData("pos", p);
					ref.setPos(node.getDataVec("pos"));
					ref.force_calc();
				}});

				g.addMetode("go_to", new nRun() { public void run(Object o) { 
					Vector2 p = (Vector2)o;
					pInstance node = g.object("node", pInstance.class);
					node.setData("pos", p);
					ref.setPos(node.getDataVec("pos"));
					ref.force_calc();
				}});

				g.addMetode("get_center", new nRun() { public Object get() { 
					return ref.getParentCenter();
				}});

				g.addMetode("move_to_cam", new nRun() { public void run() { 
					pInstance node = g.object("node", pInstance.class);
					Vector2 new_pos = new Vector2();
					sVec viewspace_cam_pos = node.patch.view
							.object("val_cam_pos", sVec.class);
					new_pos.set(-viewspace_cam_pos.x(), 
							-viewspace_cam_pos.y());
					new_pos.sub(node.sheet.sheet_ref.getLocalPos());
					if(new_pos.x > 0) new_pos.x -= new_pos.x%pNode.BRIC_GRID_SIZE;
					else new_pos.x += Math.abs(new_pos.x)%pNode.BRIC_GRID_SIZE;
					if(new_pos.y > 0) new_pos.y -= new_pos.y%pNode.BRIC_GRID_SIZE;
					else new_pos.y += Math.abs(new_pos.y)%pNode.BRIC_GRID_SIZE;
					node.setData("pos", new_pos);
					ref.setPos(new_pos);
				}});

				g.addMetode("get_side_stack", new nRun() { public Object get(Object o) { 
					String side = (String)o;
					if (side.equals("left")) {
						if (g.get("costack_left") == null) {
							nWidget costack_left = g.addWidget("costack_left", "PN_costack");
							costack_left.setParent(ref)
							.setGlueCible(back)
							.setGlueSide(nAlign.LEFT)
							.setGlueAlign(nAlign.TOP)
							.setStackAxis(nAlign.VERTICAL) // HORIZONTAL   VERTICAL
							.setStackDirection(nAlign.DOWN) // RIGHT   LEFT   UP   DOWN
							;
						}
						return g.get("costack_left");
					} else if (side.equals("right")) {
						if (g.get("costack_right") == null) {
							nWidget costack_right = g.addWidget("costack_right", "PN_costack");
							costack_right.setParent(ref)
							.setGlueCible(back)
							.setGlueSide(nAlign.RIGHT)
							.setGlueAlign(nAlign.TOP)
							.setStackAxis(nAlign.VERTICAL) // HORIZONTAL   VERTICAL
							.setStackDirection(nAlign.DOWN) // RIGHT   LEFT   UP   DOWN
							;
						}
						return g.get("costack_right");
					} else if (side.equals("bottom")) {
						if (g.get("costack_bottom") == null) {
							nWidget costack_bottom = g.addWidget("costack_bottom", "PN_costack");
							costack_bottom.setParent(ref)
							.setGlueCible(back)
							.setGlueSide(nAlign.BOTTOM)
							.setGlueAlign(nAlign.CENTER)
							.setStackAxis(nAlign.HORIZONTAL) // HORIZONTAL   VERTICAL
							.setStackDirection(nAlign.RIGHT) // RIGHT   LEFT   UP   DOWN
							;
						}
						return g.get("costack_bottom");
					} else if (side.equals("top")) {
						if (g.get("costack_top") == null) {
							nWidget costack_top = g.addWidget("costack_top", "PN_costack");
							costack_top.setParent(ref)
							.setGlueCible(back)
							.setGlueSide(nAlign.TOP)
							.setGlueAlign(nAlign.CENTER)
							.setStackAxis(nAlign.HORIZONTAL) // HORIZONTAL   VERTICAL
							.setStackDirection(nAlign.RIGHT) // RIGHT   LEFT   UP   DOWN
							;
						}
						return g.get("costack_top");
					} 
					return null; 
				} });

				g.addMetode("link_to_instance", new nRun() {
					public void run(Object o) {
						pInstance node = ((pInstance)o);
						g.addObject("node", node);
						pPatch patch = node.patch;
						
						ref.setParent(node.sheet.sheet_ref);
						
						if (node.sheet.val_collapse.get()) ref.hide();

						nRun pop_close_run = new nRun() { public void run() {
							if (patch.pop_close_user.equals(node.sheet.bloc.ref+
									"_"+node.pool_ref))
								patch.pop_close_user = "";
							g.setObject("mouseOver", false);
							patch.patch_pop_close.removeEventTrigger(
									g.object("pop_close_run", nRun.class)); 
							patch.patch_pop_close.clearParent().hide();
							node.run("clear_trigg"); }};
						g.addObject("pop_close_run", pop_close_run);

						nRun g_close_run = new nRun() { public void run() {
							if (patch.pop_close_user.equals(node.sheet.bloc.ref+
									"_"+node.pool_ref))
								patch.pop_close_user = "";
							patch.patch_pop_close.removeEventTrigger(
									g.object("pop_close_run", nRun.class)); 
						}};
						g.addEventClear(g_close_run);
						
						g.addObject("mouseOver", false);
						ref.addEventLogic(new nRun() { public void run() {
							if (back.mouseOver && PlaneApplet.app.input.mouseLeft.trigClick) {
								if (!node.getDataBoo("selected")) node.run("select");
								else node.run("unselect"); }
							ref.setPos(node.getDataVec("pos"));
							selline.setPos(-3f*RS-RS/4f, -RS/4f);
							selline.setSize(ref.boundedSize.x + 6f*RS + RS/2f,
									ref.boundedSize.y + RS/2f); 
							boolean over = g.object("mouseOver", Boolean.class);
							boolean new_over = ref.mouseOverChildZone || 
									(over && patch.patch_pop_close.mouseOverZone && 
									patch.pop_close_user(node.sheet.bloc.ref+
											"_"+node.pool_ref));
							if (new_over && !over) {
								patch.pop_close_user = Utl.copy(node.sheet.bloc.ref+
										"_"+node.pool_ref);
								patch.patch_pop_close.setParent(back)
								.addEventTrigger(pop_close_run);
								patch.patch_pop_close.show();
							} else if (!new_over && over) {
								patch.patch_pop_close.removeEventTrigger(pop_close_run);
								patch.patch_pop_close.hide();
							}
							if (ref.mouseOverChildZone || 
									(patch.patch_pop_close.mouseOverZone && 
									patch.pop_close_user(node.sheet.bloc.ref+
											"_"+node.pool_ref))) {
								patch.patch_pop_close.setPos(back.boundedSize.x, 
										back.boundedSize.y - RS/6f); 
								patch.patch_pop_close.show();
								g.setObject("mouseOver", true);
							}
							else g.setObject("mouseOver", false);
						}});

					}
				});

//				g.addMetode("find_place", new nRun() { public void run() {
//					
//					pInstance node = g.object("node", pInstance.class);
////					pPatch patch = node.patch;
//					
//					Vector2 new_pos = new Vector2(node.getDataVec("pos"));
//					
//					boolean found = false;
//					int loop_cnt = 200;
//					ref.setPos(new_pos);
//					ref.force_calc_child();
//					
//					int mdir = 0;
//					int msidel = 1;
//					int msidec = 0;
//					
//					while (!found && loop_cnt > 0) {
//						found = true;
//						loop_cnt--;
////						Rectangle r1 = new Rectangle();
////						r1.x = ref.getLocalX(); r1.y = ref.getLocalY();
////						r1.width = ref.boundedSize.x; r1.height = ref.boundedSize.y;
//						
//						Rectangle r1 = selline.getRectRelativeToParent(ref.parent);
//						
//						for (pInstance ob : node.sheet.nodes) 
//								if (found && ob != node) {
//							nWidget ref2 = ob.object("group", nWidgetGroup.class)
//									.get("ref");
//							ref2.force_calc_child();
//							
//							Rectangle r2 = ob.object("group", nWidgetGroup.class)
//									.get("selline").getRectRelativeToParent(ref2.parent);
//							
////							Rectangle r2 = new Rectangle();
////							r2.x = ref2.getLocalX(); r2.y = ref2.getLocalY();
////							r2.width = ref2.boundedSize.x; r2.height = ref2.boundedSize.y;
//							
//							if (Utl.intersect(r1,r2)) {
//								found = false; 
//								break;
//							}
//						}
//						if (!found) {
//							float xm = ref.boundedSize.x + 20;
//							float ym = ref.boundedSize.y + 20;
//							xm = xm - xm%pNode.BRIC_GRID_SIZE + pNode.BRIC_GRID_SIZE;
//							ym = ym - ym%pNode.BRIC_GRID_SIZE + pNode.BRIC_GRID_SIZE;
//							if (mdir == 0) { ref.setPX(ref.getLocalX() + xm); }
//							if (mdir == 1) { ref.setPY(ref.getLocalY() + ym); }
//							if (mdir == 2) { ref.setPX(ref.getLocalX() - xm); }
//							if (mdir == 3) { ref.setPY(ref.getLocalY() - ym); }
//							msidec++;
//							if (msidec == msidel) {
//								msidec = 0;
//								if (mdir%2 == 1 ) msidel++;//&& msidel != 1
//								if (mdir == 1 && msidel == 1) msidel++;
//								mdir += 1; if (mdir == 4) mdir = 0;
//							}
//							
//							ref.force_calc_child();
//							new_pos.set(ref.getLocalX(), ref.getLocalY());
//							node.setData("pos", new_pos);
//						}
//					}
//					
//				}});
				
				return g;
			} 
		} );
		
	}
	
	
}
