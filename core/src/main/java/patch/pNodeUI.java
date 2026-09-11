package patch;

import gui.*;
import patch.pNode.CT;
import util.*;
import aa_nodulo.*;

import com.badlogic.gdx.math.Vector2;

public class pNodeUI {
	
	public static void build() {

		build_paint();
		
		pNode.newNodeModel("UI").process()
			.useLoad().commande(new nRun() {public void run() { 
				pView view = PlaneApplet.app.view;
				nWidgetGroup bar = PlaneApplet.app.gui.addWidgetGroup("viewspace_tool");
				instance.addObject("viewspace_tool", bar);
				bar.metode("set_px", 100f);
				bar.metode("set_py", 20f);
				bar.metode("set_pop_up");
				bar.metode("set_title", "Node UI");
				bar.metode("add_to_viewspace_front", view.view);
				nInterface bar_interf = (nInterface)bar.metodeGet("get_interf");
				instance.addObject("interf", bar_interf);
			}})
			.useClear().commande(new nRun() {public void run() { 
				nWidgetGroup bar = instance.object("viewspace_tool", nWidgetGroup.class);
				if (bar != null) bar.clear();
			}}).useInit()
			.openSec()
				.param("run", new nRun() {public void run() {
					nWidgetGroup bar = instance.object("viewspace_tool", nWidgetGroup.class);
					if (bar == null) return;
					Vector2 pos = instance.getVar("view_pos", Vector2.class);
					bar.metode("set_px", pos.x);
					bar.metode("set_py", pos.y);
				}})
				.param("text", "pos", "width", (int)6, "def", new Vector2(100,20)) 
				.run(pNode.getRun(CT.RUNP_VAR_VEC_LAB_FIELD), "view_pos")  
				.commande(pNode.getCom(CT.COM_ADD_ROW))
			.closeSec()
			.getStand()
		.openSec()
		.param("keys", new String[] {"UI"}, "filters", new String[] {"UI"}) 
		.run(pNode.getRun(CT.RUNS_ADD_CHAIN_START_PLUG), "UI_widg", "bottom")
		.closeSec()
		;

		
		
		
		
		pNode.newChainnedNodeModel("UI_trigg")
		.process()
			.commande(new nRun() {public void run() {
				instance.obtainVar("widg_size", new Vector2(3,1));
				PlaneApplet.app.addDelayEvent(1, new nRun(instance) {public void run() {
					pInstance inst = (pInstance)builder;
					pInstance head = inst.get("get_chain_head", pInstance.class);
					if (head == null) return;
					nInterface interf = head.object("interf", nInterface.class);
					if (interf == null) return;
					nWidget tr = interf.add_row_trigg(3, 
							inst.getVar("widg_text", String.class));
					inst.addObject("trigg", tr);
					Vector2 size = inst.getVar("widg_size", Vector2.class);

					float RS = nGUI.book.RS;
					
					tr.setSX(RS*size.x);
					tr.setSY(RS*size.y);
					tr.addEventTrigger(new nRun(inst) {public void run() { 
						pInstance inst2 = (pInstance)builder;
						pInstance co_out = inst2.get("get_co", pInstance.class, "out");
						if (co_out != null) co_out.run("send");
					}});
				}});
			}})
			.openSec()
				.param("run", new nRun() {public void run() {
					nWidget tr = instance.object("trigg", nWidget.class);
					if (tr == null) return;
					Vector2 size = instance.getVar("widg_size", Vector2.class);

					float RS = nGUI.book.RS;
					
					tr.setSX(RS*size.x);
					tr.setSY(RS*size.y);
				}})
				.param("text", "size", "width", (int)6, "def", new Vector2(3,1)) 
				.run(pNode.getRun(CT.RUNP_VAR_VEC_LAB_FIELD), "widg_size")  
				.commande(pNode.getCom(CT.COM_ADD_ROW))
			.closeSec()
			.openSec()
				.param("run", new nRun() {public void run() {
					nWidget tr = instance.object("trigg", nWidget.class);
					if (tr == null) return;
					String text = instance.getVar("widg_text", String.class);
					tr.setText(text);
				}})
				.param("text", "text", "width", (int)6, "def", "") 
				.run(pNode.getRun(CT.RUNP_VAR_STR_LAB_FIELD), "widg_text")  
			.closeSec()
			.getStand()
		.openSec()
		.param("keys", new String[] {"all"}, "filters", new String[] {}) 
		.run(pNode.getRun(CT.RUNS_ADD_CO_OUT), "out")
		.closeSec()
		.openSec()
		.param("keys", new String[] {"UI"}, "filters", new String[] {"UI"}) 
		.run(pNode.getRun(CT.RUNS_ADD_CHAIN_PLUGS), "UI_widg", "bottom")
		.closeSec()
		;

		
		
		
		
		pNode.newChainnedNodeModel("UI_switch")
		.process()
			.commande(new nRun() {public void run() {
				instance.obtainVar("state", false);
				instance.obtainVar("widg_size", new Vector2(3,1));
				PlaneApplet.app.addDelayEvent(1, new nRun(instance) {public void run() {
					pInstance inst = (pInstance)builder;
					pInstance head = inst.get("get_chain_head", pInstance.class);
					if (head == null) return;
					nInterface interf = head.object("interf", nInterface.class);
					if (interf == null) return;
					nWidget tr = interf.add_row_switch(3, 
							inst.getVar("widg_text", String.class));
					inst.addObject("widg", tr);
					Vector2 size = inst.getVar("widg_size", Vector2.class);

					float RS = nGUI.book.RS;
					
					tr.setSX(RS*size.x);
					tr.setSY(RS*size.y);
					tr.addEventSwitch(new nRun(inst) {public void run() { 
						pInstance inst2 = (pInstance)builder;
						inst2.setVar("state", tr.isOn());
						pInstance co_out = inst2.get("get_co", pInstance.class, "out");
						if (co_out != null) co_out.run("send", tr.isOn());
					}});
					nRun logic_run = new nRun(inst) {public void run() { 
						pInstance inst2 = (pInstance)builder;
						tr.setSwitchState(inst2.getVar("state", Boolean.class));
					}};
					tr.addEventLogic(logic_run); logic_run.run();
				}});
			}})
			.openSec()
				.param("run", new nRun() {public void run() {
					nWidget tr = instance.object("widg", nWidget.class);
					if (tr == null) return;
					Vector2 size = instance.getVar("widg_size", Vector2.class);

					float RS = nGUI.book.RS;
					
					tr.setSX(RS*size.x);
					tr.setSY(RS*size.y);
				}})
				.param("text", "size", "width", (int)6, "def", new Vector2(3,1)) 
				.run(pNode.getRun(CT.RUNP_VAR_VEC_LAB_FIELD), "widg_size")  
				.commande(pNode.getCom(CT.COM_ADD_ROW))
			.closeSec()
			.openSec()
				.param("run", new nRun() {public void run() {
					nWidget tr = instance.object("widg", nWidget.class);
					if (tr == null) return;
					String text = instance.getVar("widg_text", String.class);
					tr.setText(text);
				}})
				.param("text", "text", "width", (int)6, "def", "") 
				.run(pNode.getRun(CT.RUNP_VAR_STR_LAB_FIELD), "widg_text")  
			.closeSec()
			.getStand()
		.openSec()
		.param("offer", new nRun() {public Object get() {
			pInstance node = instance.object("node", pInstance.class);
			return node.getVar("state", Boolean.class); }})
		.param("keys", new String[] {"all"}, "filters", new String[] {}) 
		.run(pNode.getRun(CT.RUNS_ADD_CO_OUT), "out")
		.closeSec()
		.openSec()
		.param("keys", new String[] {"UI"}, "filters", new String[] {"UI"}) 
		.run(pNode.getRun(CT.RUNS_ADD_CHAIN_PLUGS), "UI_widg", "bottom")
		.closeSec()
		;

		
		
		
		
		pNode.newChainnedNodeModel("UI_label")
		.newRun("update_label_text", new nRun() {public void run() {
			nWidget tr = instance.object("widg", nWidget.class);
			if (tr == null) return;
			String text = instance.getVar("widg_text", String.class);
			Object input_obj = instance.object("input_obj");
			if (input_obj == null) tr.setText(text);
			else tr.setText(text + " " + Utl.to_string(input_obj));
		}})
		.process()
			.commande(new nRun() {public void run() {
				instance.obtainVar("widg_size", new Vector2(3,1));
				PlaneApplet.app.addDelayEvent(1, new nRun(instance) {public void run() {
					pInstance inst = (pInstance)builder;
					pInstance head = inst.get("get_chain_head", pInstance.class);
					if (head == null) return;
					nInterface interf = head.object("interf", nInterface.class);
					if (interf == null) return;
					nWidget tr = interf.add_row_label(3, 
							inst.getVar("widg_text", String.class));
					inst.addObject("widg", tr);
					Vector2 size = inst.getVar("widg_size", Vector2.class);

					float RS = nGUI.book.RS;
					
					tr.setSX(RS*size.x);
					tr.setSY(RS*size.y);
					nRun logic_run = new nRun(inst) {public void run() { 
						pInstance inst2 = (pInstance)builder;
						if (!inst2.getVar("auto_obtain", Boolean.class)) return;
						pInstance co = inst2.get("get_co", pInstance.class, "in");
						Object op = co.get("obtain", Object.class);
						if (op == null) return;
						inst2.setObject("input_obj", op);
						inst2.run("update_label_text"); 
					}};
					tr.addEventLogic(logic_run); logic_run.run();
				}});
			}})
			.openSec()
				.param("run", new nRun() {public void run() {
					nWidget tr = instance.object("widg", nWidget.class);
					if (tr == null) return;
					Vector2 size = instance.getVar("widg_size", Vector2.class);

					float RS = nGUI.book.RS;
					
					tr.setSX(RS*size.x);
					tr.setSY(RS*size.y);
				}})
				.param("text", "size", "width", (int)6, "def", new Vector2(3,1)) 
				.run(pNode.getRun(CT.RUNP_VAR_VEC_LAB_FIELD), "widg_size")  
				.commande(pNode.getCom(CT.COM_ADD_ROW))
			.closeSec()
			.openSec()
				.param("run", new nRun() {public void run() {
					instance.run("update_label_text"); }})
				.param("text", "text", "width", (int)6, "def", "") 
				.run(pNode.getRun(CT.RUNP_VAR_STR_LAB_FIELD), "widg_text")  
			.closeSec()
			.openSec()
				.param("text", "Auto", "width", (int)4, "def", true) 
				.run(pNode.getRun(pNode.CT.RUNP_VAR_BOO_SWITCH), "auto_obtain")
			.closeSec()
			.getStand()
		.openSec()
		.param("event_receive", new nRun() {public void run() {
			Object r = arg(0,Object.class);
			pInstance node = instance.object("node", pInstance.class);
			node.setObject("input_obj", r);
			node.run("update_label_text"); 
		}})
		.param("keys", new String[] {"all"}, "filters", new String[] {}) 
		.run(pNode.getRun(CT.RUNS_ADD_CO_IN), "in")
		.closeSec()
		.openSec()
		.param("keys", new String[] {"UI"}, "filters", new String[] {"UI"}) 
		.run(pNode.getRun(CT.RUNS_ADD_CHAIN_PLUGS), "UI_widg", "bottom")
		.closeSec()
		;
		
		
				
	}
	
	
	public static void build_paint() {

		float RS = nGUI.book.RS;
		
		pNode.newNodeModel("text")
		.newRun("set_font", new nRun() {public void run() { 
			float f = arg(0,Float.class);
			nWidget text = instance.get("get_mapped_widget", nWidget.class, 
					"field_text");
			if (text != null) {
				instance.setVar("text_font", f);
				text.setFont(f*RS*9f/10f); 
				int l = 1 + text.line_number(instance.getVar("text", String.class));
				text.setSY(l*f*RS*9f/10f); }
		}})
		.newRun("set_width", new nRun() {public void run() { 
			float f = arg(0,Float.class);
			nWidget text = instance.get("get_mapped_widget", nWidget.class, 
					"field_text");
			if (text != null) {
				instance.setVar("text_width", f);
				text.setSX(f*20f*RS); 
				int l = 1 + text.line_number(instance.getVar("text", String.class));
				text.setSY(l*instance.getVar("text_font", Float.class)*RS*9f/10f); }
		}})
		.process()
		.useLoad().commande(new nRun() {public void run() { 
			instance.obtainVar("text_font", 4f);
			instance.obtainVar("text_width", 2f);
			nWidgetGroup group = instance.object("group", nWidgetGroup.class);
			group.get("back").set_color_background(Utl.color(0,0)).setText("");
			nWidget text = instance.get("get_mapped_widget", nWidget.class, 
					"field_text");
			if (text != null) {
				text.set_color_background(Utl.color(0,0))
				.set_color_standby(Utl.color(0,0))
				.set_color_hovered(Utl.color(0,0))
				.set_color_pressed(Utl.color(0,0))
				.setTextAlignment(nAlign.LEFT,nAlign.CENTER)
				.setTextAutoReturn(true);
				instance.run("set_font",instance.getVar("text_font", Float.class));
				instance.run("set_width",instance.getVar("text_width", Float.class));
			}

			nRun run_frame = new nRun(instance) {public void run() { 
				pInstance inst = (pInstance)builder;
				nWidget text = inst.get("get_mapped_widget", nWidget.class, 
						"field_text");
				int l = 1 + text.line_number(inst.getVar("text", String.class));
				text.setSY(l*inst.getVar("text_font", Float.class)*RS*9f/10f);
			}};

			instance.addObject("run_frame_paint", run_frame);
			instance.patch.addEventFrame(run_frame);
		}})
		.useClear().commande(new nRun() {public void run() { 
			instance.patch.removeEventFrame(
					instance.object("run_frame_paint", nRun.class));
			
		}}).useInit()
		.openSec()
			.param("run_right", new nRun() {public void run() {
				nGUI.clear_dropmenu();
				nGUI.add_dropmenu_entry("set font : small", new nRun(instance) { public void run() {
					((pInstance)builder).run("set_font", 1f); }});
				nGUI.add_dropmenu_entry("set font : medium", new nRun(instance) { public void run() {
					((pInstance)builder).run("set_font", 2f); }});
				nGUI.add_dropmenu_entry("set font : large", new nRun(instance) { public void run() {
					((pInstance)builder).run("set_font", 4f); }});
				nGUI.add_dropmenu_entry("set font : big", new nRun(instance) { public void run() {
					((pInstance)builder).run("set_font", 8f); }}); 
				nGUI.add_dropmenu_entry("set width : small", new nRun(instance) { public void run() {
					((pInstance)builder).run("set_width", 1f); }});
				nGUI.add_dropmenu_entry("set width : medium", new nRun(instance) { public void run() {
					((pInstance)builder).run("set_width", 2f); }});
				nGUI.add_dropmenu_entry("set width : large", new nRun(instance) { public void run() {
					((pInstance)builder).run("set_width", 6f); }});
				nGUI.open_dropmenu();
			}})
			.param("ref", "text", "scale_min", 0f, 
					"def", "write here ...") 
			.run(pNode.getRun(CT.RUNP_VAR_STR_FIELD), "text")  
		.closeSec()
		;
		
//		pNode.newNodeModel("paint")
//		.process()
//		.useLoad().commande(new nRun() {public void run() { 
////			nWidgetGroup group = instance.object("group", nWidgetGroup.class);
////			group.get("back").set_color_background(Utl.color(0,0)).setText("");
//			
//		}})
//		.useClear().commande(new nRun() {public void run() { 
//			
//		}}).useInit()
//		.openSec()
//			.run(pNode.getRun(pNode.CT.RUNP_ADD_LABEL), "", (int)10)
//		.closeSec()
//		;
		
	}
	
}
