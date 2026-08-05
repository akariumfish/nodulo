package patch;

import data.*;
import gui.*;
import patch.pNode.CT;
import util.*;
import aa_nodulo.*;
import app.*;

import com.badlogic.gdx.math.Vector2;

public class pNodeUI {
	
	public static void build(PlaneApplet app) {

		float RS = app.gui.book.RS;
		
		pNode.newNodeModel("UI").process()
			.useLoad().commande(new nRun() {public void run() { 
				pView view = app.view;
				nWidgetGroup bar = app.gui.addWidgetGroup("viewspace_tool");
				instance.addObject("viewspace_tool", bar);
				bar.metode("set_px", 100f);
				bar.metode("set_py", 100f);
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
				.param("text", "pos", "width", (int)6, "def", new Vector2(100,100)) 
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
				app.addDelayEvent(1, new nRun(instance) {public void run() {
					pInstance inst = (pInstance)builder;
					pInstance head = inst.get("get_chain_head", pInstance.class);
					if (head == null) return;
					nInterface interf = head.object("interf", nInterface.class);
					if (interf == null) return;
					nWidget tr = interf.add_row_trigg(3, 
							inst.getVar("widg_text", String.class));
					inst.addObject("trigg", tr);
					Vector2 size = inst.getVar("widg_size", Vector2.class);
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
				app.addDelayEvent(1, new nRun(instance) {public void run() {
					pInstance inst = (pInstance)builder;
					pInstance head = inst.get("get_chain_head", pInstance.class);
					if (head == null) return;
					nInterface interf = head.object("interf", nInterface.class);
					if (interf == null) return;
					nWidget tr = interf.add_row_switch(3, 
							inst.getVar("widg_text", String.class));
					inst.addObject("widg", tr);
					Vector2 size = inst.getVar("widg_size", Vector2.class);
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
		
		
		
		
		
		
		
		
		
	}
	
}
