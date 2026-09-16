package patch;

import data.*;
import gui.*;
import patch.pNode.CT;
import util.*;
import aa_nodulo.*;
import app.*;

import java.util.ArrayList;
import java.util.Map;

import com.badlogic.gdx.math.Vector2;

public class pNodeAction {
	
	public static void build() {

		float RS = nGUI.book.RS;
		
//
//
//		pStandard stand_action = pNode.newNodeModel("action", "action")
//		.newRun("def_ctrl", new nRun() {public void run() { 
//			String ctrl_ref = arg(0,String.class);
//			if (ctrl_ref == null) return;
//			pProperty ctrl = pProperty.get("ctrl_"+ctrl_ref);
//			if (ctrl == null) return;
//			instance.setVar("ctrl_used", ctrl_ref);
//			instance.setVar("step", (int)0);
//			instance.setObject("ctrl", ctrl);
//			for (int i = 0 ; i < Utl.data_type_nb ; i++) {
//				nMap<Integer> map = ctrl.data_vals.get(Utl.data_type[i]);
//				if (map != null) for (Map.Entry<String, Integer> mr : map.entrySet()) {
//					String dt_ref = mr.getKey();
//					Object dt = ctrl.getDataValDef(dt_ref, Utl.data_type[i]);
//					instance.setVar(dt_ref, dt);
//					instance.setVar(dt_ref+"_key", false);
//				}
//			}
//			instance.setVar("got_ctrl", true);
//		}})
//		.newRun("apply_step", new nRun() {public void run() { 
//			if (instance.getVar("play", Boolean.class)) {
//				instance.setVar("step", (int)(instance.getVar("step", Integer.class) + 1));
//				float period = instance.getVar("period", Float.class);
//				if (instance.getVar("step", Integer.class) >= period) {
//					instance.setVar("step", (int)0);
//					if (!instance.getVar("loop", Boolean.class)) {
//						instance.setVar("play", false);
//					}
//				}
//				
//				pInstance co = instance.get("get_co", pInstance.class, "body");
//				Object op = co.get("obtain_all", Object.class);
//				if (op == null) return;
//				pBody bod = null;
//				ArrayList<Object> prov = (ArrayList)op;
//				for (Object o : prov) if (o instanceof pBody) {
//					bod = (pBody)o; }
//				if (bod == null) return;
//				
//				pProperty ctrl = instance.object("ctrl", pProperty.class);
//				if (ctrl == null || !bod.hasParam(ctrl.ref)) return;
//				
//				int step = instance.getVar("step", Integer.class);
//				
//				pInstance last = instance;
//				boolean as_next = instance.get("plug_obtain_node", pInstance.class, 
//						"ctrl_chan_out") != null;
//				int wcnt = 0;
//				while (as_next && wcnt < 200) {
//					if (last.get("plug_obtain_node", pInstance.class, 
//							"ctrl_chan_out") != null) {
//						last = last.get("plug_obtain_node", pInstance.class, 
//							"ctrl_chan_out");
//						String data_used = last.getVar("data_used", String.class);
//						if (data_used == null) return;
//						Object ov = last.get("get_var_at_step", step);
//						bod.param(ctrl.ref).set(data_used, ov);
//						instance.setVar(data_used+"_last_data", ov);
//						as_next = true;
//					} else { as_next = false; }
//					wcnt++;
//				}
//			}
//		}})
//		.newRun("do_prev_tick", new nRun() {public void run() { 
//			if (instance.getVar("auto", Boolean.class)) {
//				instance.run("apply_step"); }
//		}})
//		.newRun("do_tick", new nRun() {public void run() { 
//			
//		}})
//		.newRun("get_chan", new nRun() {public Object get() { 
//			String dt_ref = arg(0,String.class);
//			if (dt_ref == null) return null;
//			pInstance last = instance;
//			boolean as_next = instance.get("plug_obtain_node", pInstance.class, 
//					"ctrl_chan_out") != null;
//			int wcnt = 0;
//			while (as_next && wcnt < 200) {
//				if (last.get("plug_obtain_node", pInstance.class, "ctrl_chan_out") != null) {
//					last = last.get("plug_obtain_node", pInstance.class, "ctrl_chan_out");
//					String data_used = last.getVar("data_used", String.class);
//					if (data_used == null) continue;
//					if (data_used.equals(dt_ref)) return last;
//				} else { as_next = false; }
//				wcnt++;
//			}
//			return null;
//		}})
//		.newRun("pop_ctrl_chan", new nRun() {public void run() {
//			String dt_ref = arg(0,String.class);
//			if (dt_ref == null) return;
//			pInstance last = instance.get("get_chain_last", pInstance.class);
//			pInstance n = last.get("pop_plug_node", pInstance.class,
//					"ctrl_chan_out", "ctrl_chan", "ctrl_chan_in");
//			n.run("set_data", dt_ref);
//			n.get("pop_ctrl_key");
//		}})
//		.newRun("pop_ctrl_key", new nRun() {public void run() {
//			if (args.length <= 2) return;
//			String dt_ref = arg(0,String.class);
//			int len = arg(1,Integer.class);
//			Object val = args[2];
//			if (dt_ref == null || val == null) return;
//			pInstance last = instance.get("get_chan", pInstance.class, dt_ref);
//			if (last == null) return;
//			pInstance key = last.get("pop_ctrl_key", pInstance.class);
//			if (key == null) return;
//			key.setVar("len", len); key.setVar("val", val);
//		}})
//		;
//		
//		stand_action.process().commande(new nRun() {public void run() {
//			PlaneApplet.app.time.addPrevTickBric(instance);
//			PlaneApplet.app.time.addTickBric(instance);
//			
//			nRun run_frame = new nRun(instance) {public void run() { 
//				pInstance inst = (pInstance)builder;
//				
//			}};
//			instance.addObject("run_frame", run_frame);
//			instance.patch.addEventFrame(run_frame);
//			instance.obtainVar("got_ctrl", false);
//			PlaneApplet.app.addDelayEvent(1, new nRun(instance) {public void run() {
//				pInstance inst = (pInstance)builder;
//				if (!inst.getVar("got_ctrl", Boolean.class))
//					inst.run("def_ctrl", inst.getVar("ctrl_used", String.class));
//			}});
//		}})
//		.useClear().commande(new nRun() {public void run() { 
//			PlaneApplet.app.time.removePrevTickBric(instance);
//			PlaneApplet.app.time.removeTickBric(instance);
//			instance.patch.removeEventFrame(
//					instance.object("run_frame", nRun.class));
//		}}).useInit()
//		.openSec()
//		.run(pNode.getRun(CT.RUNP_VAR_STR_FIELD), "ctrl_used", (int)6)
//		.closeSec()
//		.openSec()
//			.param("run", new nRun() {public void run() {
////				nWidget triggP_w = instance.get("get_mapped_widget", nWidget.class, 
////						"trigg_dropm_ctrl");
////				if (triggP_w == null) return;
////				instance.patch.patch_dropmenu.metode("clear_entrys");
////				for (String rf : pGeom.control_props.allKey()) {
////					nWidget w1 = (nWidget)instance.patch.patch_dropmenu
////							.metodeGet("add_entry_custom", rf, RS*6f, RS*2f/3f);
////					w1.addEventTrigger(new nRun(instance) { public void run() {
////						((pInstance)builder).run("def_ctrl", rf); 
////					}}); 
////				}
////				instance.patch.patch_dropmenu.metode("open", triggP_w);
//			}})
//			.run(pNode.getRun(CT.RUNP_ADD_TRIGG), "trigg_dropm_ctrl", "Pk", (int)2)
//		.closeSec()
//		.commande(pNode.getCom(CT.COM_ADD_ROW))
//		.openSec()
//		.param("run", new nRun() {public void run() {
//			instance.setVar("step", (int)0); }})
//		.param("def", 40f, "min", 1f, "max", 60f, "granulo", 1f)
//		.run(pNode.getRun(pNode.CT.RUNP_VAR_FLT_LAB_FIELD_SLIDE), 
//				"period", "period", (int)5, true)
//		.closeSec()
//		.commande(pNode.getCom(CT.COM_ADD_ROW))
//		.openSec()
//			.param("run", new nRun() {public void run() {
//				nWidget triggP_w = instance.get("get_mapped_widget", nWidget.class, 
//						"trigg_dropm_data");
//				if (triggP_w == null) return;
////				instance.patch.patch_dropmenu.metode("clear_entrys");
////
////				pProperty ctrl = instance.object("ctrl", pProperty.class);
////				if (ctrl == null) return;
////				for (int i = 0 ; i < Utl.data_type_nb ; i++) {
////					nMap<Integer> map = ctrl.data_vals.get(Utl.data_type[i]);
////					if (map != null) for (Map.Entry<String, Integer> mr : map.entrySet()) {
////						String dt_ref = mr.getKey();
////						if (instance.getVar(dt_ref+"_key", Boolean.class)) continue;
////						nWidget w1 = (nWidget)instance.patch.patch_dropmenu
////								.metodeGet("add_entry_custom", dt_ref, RS*6f, RS*2f/3f);
////						w1.addEventTrigger(new nRun(instance) { public void run() {
////							pInstance inst = (pInstance)builder;
////							inst.run("pop_ctrl_chan", dt_ref);
////						}}); 
////					}
////				}
////				instance.patch.patch_dropmenu.metode("open", triggP_w);
//			}})
//			.run(pNode.getRun(CT.RUNP_ADD_TRIGG), "trigg_dropm_data", "pop_ctrl_key", (int)10)
//		.closeSec()
//		.commande(pNode.getCom(CT.COM_ADD_COL))
//		.openSec()
//		.param("ref", "timeline")
//		.param("text", "", "width", (int)20, "height", 4f)
//		.param("custom_drawer", new nRun() {public void run() { 
//			PlaneApplet app = PlaneApplet.app;
//			
//			float sx = 300, sy = 120;
//			app.fill(40); app.noStroke(); 
//			app.rect(0,0,sx,sy);
//			
//			pProperty ctrl = instance.object("ctrl", pProperty.class);
//			if (ctrl == null) return;
//			String txt = "";
//			pInstance last = instance;
//			boolean as_next = instance.get("plug_obtain_node", pInstance.class, 
//					"ctrl_chan_out") != null;
//			int wcnt = 0;
//			while (as_next && wcnt < 200) {
//				if (last.get("plug_obtain_node", pInstance.class, "ctrl_chan_out") != null) {
//					last = last.get("plug_obtain_node", pInstance.class, "ctrl_chan_out");
//					String data_used = last.getVar("data_used", String.class);
//					if (data_used == null) return;
//					Class<?> dt_class = ctrl.data_class.get(data_used);
//					if (dt_class == null) continue;
//					txt += Utl.to_string(instance.getVar(
//							data_used+"_last_data", dt_class)) + "\n";
//				} else { as_next = false; }
//				wcnt++;
//			}
//			app.textAlign(nAlign.CENTER, nAlign.CENTER);
//			app.text(txt,sx/2f,sy/2f,12f);
//			int step = instance.getVar("step", Integer.class);
//			float period = instance.getVar("period", Float.class);
//			float f = step / period;
//			app.stroke(255,3f); app.line(sx * f, 0, sx * f, sy);
//			
//		}}) 
//		.commande(pNode.getCom(CT.COM_ADD_WIDGET))
//		.closeSec()
//		.commande(pNode.getCom(CT.COM_ADD_COL))
//		.openSec()
//		.run(pNode.getRun(CT.RUNP_VAR_INT_LAB_FIELD), "step", "step", (int)3, true)
//		.closeSec()
//		.commande(pNode.getCom(CT.COM_ADD_COL))
//		.openSec()
//		.param("def", false)
//		.run(pNode.getRun(CT.RUNP_VAR_BOO_SWITCH), "play", "play", (int)3)
//		.closeSec()
//		.commande(pNode.getCom(CT.COM_ADD_ROW))
//		.openSec()
//		.param("def", true)
//		.run(pNode.getRun(CT.RUNP_VAR_BOO_SWITCH), "auto", "auto", (int)3)
//		.closeSec()
//		.commande(pNode.getCom(CT.COM_ADD_ROW))
//		.openSec()
//		.param("def", false)
//		.run(pNode.getRun(CT.RUNP_VAR_BOO_SWITCH), "loop", "loop", (int)3)
//		.closeSec()
//		;
//		
//		stand_action
//		.openSec()
//		.param("keys", new String[] {"body"}, "filters", new String[] {"body"}) 
//		.run(pNode.getRun(CT.RUNS_ADD_CO_IN), "body")
//		.closeSec()
//		.openSec()
//		.param("event_receive", new nRun() {public void run() {
//			instance.object("node", pInstance.class).run("apply_step");
//		}})
//		.param("keys", new String[] {"bang"}, "filters", new String[] {"bang"}) 
//		.run(pNode.getRun(CT.RUNS_ADD_CO_IN), "tick")
//		.closeSec()
//		.openSec()
//		.param("event_receive", new nRun() {public void run() {
//			instance.object("node", pInstance.class).setVar("play", true);
//		}})
//		.param("keys", new String[] {"bang"}, "filters", new String[] {"bang"}) 
//		.run(pNode.getRun(CT.RUNS_ADD_CO_IN), "run")
//		.closeSec()
//		.openSec()
//		.param("hide", true, "keys", new String[] {"ctrl_chan"}, "filters", new String[] {"ctrl_chan"}) 
//		.run(pNode.getRun(CT.RUNS_ADD_CHAIN_START_PLUG), "ctrl_chan", "bottom")
//		.closeSec()
//		;
//		
//		
//		
//		
//		pStandard stand_ctrl_chan = pNode.newChainnedNodeModel("ctrl_chan")
//		.newRun("set_data", new nRun() {public void run() {
//			String data_used = arg(0, String.class);
//			if (data_used == null) return;
//			instance.setVar("data_used", data_used);
//			pInstance head = instance.get("get_chain_head", pInstance.class);
//			if (head == null) return;
//			pProperty ctrl = head.object("ctrl", pProperty.class);
//			if (ctrl == null) return;
//			Class<?> dt_class = ctrl.data_class.get(data_used);
//			if (dt_class == null) return;
//			head.setVar(data_used+"_key", true);
//			head.obtainVar(data_used+"_last_data", ctrl.getDataValDef(data_used, dt_class));
//			instance.setVar("got_data", true);
//		}})
//		.newRun("pop_ctrl_key", new nRun() {public Object get() {
//			pInstance head = instance.get("get_chain_head", pInstance.class);
//			if (head == null) return null;
//			pProperty ctrl = head.object("ctrl", pProperty.class);
//			if (ctrl == null) return null;
//			Class<?> dt_class = ctrl.data_class
//					.get(instance.getVar("data_used", String.class));
//			if (dt_class == null) return null;
//			pInstance last = instance.get("get_chain_last", pInstance.class);
//			pInstance n = null;
//			if (dt_class == Float.class) 
//				n = last.get("pop_plug_node", pInstance.class,
//					"ctrl_key_out", "ctrl_key_flt", "ctrl_key_in");
//			else if (dt_class == Boolean.class) 
//				n = last.get("pop_plug_node", pInstance.class,
//					"ctrl_key_out", "ctrl_key_boo", "ctrl_key_in");
//			else if (dt_class == Integer.class) 
//				n = last.get("pop_plug_node", pInstance.class,
//					"ctrl_key_out", "ctrl_key_int", "ctrl_key_in");
//			else if (dt_class == String.class) 
//				n = last.get("pop_plug_node", pInstance.class,
//					"ctrl_key_out", "ctrl_key_str", "ctrl_key_in");
//			else if (dt_class == Vector2.class) 
//				n = last.get("pop_plug_node", pInstance.class,
//					"ctrl_key_out", "ctrl_key_vec", "ctrl_key_in");
//			if (n != null) {
//				int r = (int)(100f + Math.random() * 150f);
//				int g = (int)(100f + Math.random() * 150f);
//				int b = (int)(100f + Math.random() * 150f);
//				n.obtainVar("r", r); n.obtainVar("g", g); n.obtainVar("b", b);
//			}
//			return n;
//		}})
//		.newRun("get_var_at_step", new nRun() {public Object get() {
//			if (args.length == 0) return null;
//			int step = arg(0, Integer.class);
//			pInstance head = instance.get("get_chain_head", pInstance.class);
//			if (head == null) return null;
//			float period = head.getVar("period", Float.class);
//			if (step >= period) return null;
//			pProperty ctrl = head.object("ctrl", pProperty.class);
//			if (ctrl == null) return null;
//			String data_ref = instance.getVar("data_used", String.class);
//			Class<?> dt_class = ctrl.data_class
//					.get(data_ref);
//			if (dt_class == null) return null;
//
//			pInstance last = instance;
//			pInstance seg_last = null;
//			boolean as_next = instance.get("plug_obtain_node", pInstance.class, 
//					"ctrl_key_out") != null;
//			int wcnt = 0;
//			while (as_next && wcnt < 200) {
//				if (last.get("plug_obtain_node", pInstance.class, "ctrl_key_out") != null) {
//					seg_last = last;
//					last = last.get("plug_obtain_node", pInstance.class, "ctrl_key_out");
//					int len = last.getVar("len", Integer.class);
//					if (len < step) { step -= len; as_next = true; }
//					else as_next = false;
//				} else { as_next = false; }
//				wcnt++;
//			}
//			if (last == instance) {
//				if (dt_class == Float.class) { return 0f; }
//				else if (dt_class == Integer.class) { return (int)0; }
//				else if (dt_class == String.class) { return ""; }
//				else if (dt_class == Vector2.class) { return new Vector2(); } 
//				else if (dt_class == Boolean.class) { return false; } 
//				else return null;
//			} else {
//				int len = last.getVar("len", Integer.class);
//				float fct = (float)step / (float)len;
//				if (step >= len || seg_last == instance || seg_last == null) {
//					if (dt_class == Float.class) { return last.getVar("val", Float.class); }
//					else if (dt_class == Integer.class) { return last.getVar("val", Integer.class); }
//					else if (dt_class == String.class) { return last.getVar("val", String.class); }
//					else if (dt_class == Vector2.class) { return last.getVar("val", Vector2.class); } 
//					else if (dt_class == Boolean.class) { return last.getVar("val", Boolean.class); }
//					else return null;
//				} else {
//					if (dt_class == Float.class) { 
//						float pv = seg_last.getVar("val", Float.class);
//						float v = last.getVar("val", Float.class);
//						return pv + (v - pv) * fct; 
//					} else if (dt_class == Integer.class) { 
//						int pv = seg_last.getVar("val", Integer.class);
//						int v = last.getVar("val", Integer.class);
//						return pv + (int)((v - pv) * fct); 
//					} else if (dt_class == String.class) { 
//						return seg_last.getVar("val", String.class);
//					} else if (dt_class == Vector2.class) { 
//						return seg_last.getVar("val", Vector2.class);
//					} else if (dt_class == Boolean.class) { 
//						return seg_last.getVar("val", Boolean.class);
//					} else return null;
//				}
//			}
//		}})
//		;
//		stand_ctrl_chan.process()
//		.commande(new nRun() {public void run() {
//			instance.obtainVar("got_data", false);
//			PlaneApplet.app.addDelayEvent(1, new nRun(instance) {public void run() {
//				pInstance inst = (pInstance)builder;
//				if (!inst.getVar("got_data", Boolean.class))
//					inst.run("set_data", inst.getVar("data_used", String.class));
//			}});
//		}})
//		.openSec()
//		.run(pNode.getRun(CT.RUNP_VAR_STR_FIELD), "data_used", (int)10)
//		.closeSec()
//		.commande(pNode.getCom(CT.COM_ADD_COL))
//		.openSec()
//		.param("ref", "timeline")
//		.param("text", "", "width", (int)20)
//		.param("custom_drawer", new nRun() {public void run() { 
//			PlaneApplet app = PlaneApplet.app;
//			
//			float sx = 300, sy = 30;
//			app.fill(40); app.noStroke(); 
//			app.rect(0,0,sx,sy);
//			pInstance head = instance.get("get_chain_head", pInstance.class);
//			if (head == null) return;
//			pProperty ctrl = head.object("ctrl", pProperty.class);
//			if (ctrl == null) return;
//			String data_ref = instance.getVar("data_used", String.class);
//			Class<?> dt_class = ctrl.data_class
//					.get(data_ref);
//			if (dt_class == null) return;
//			float period = head.getVar("period", Float.class);
//			
//			int prev = 0;
//			pInstance last = instance;
//			boolean as_next = instance.get("plug_obtain_node", pInstance.class, 
//					"ctrl_key_out") != null;
//			int wcnt = 0;
//			while (as_next && wcnt < 200) {
//				if (last.get("plug_obtain_node", pInstance.class, "ctrl_key_out") != null) {
//					last = last.get("plug_obtain_node", pInstance.class, "ctrl_key_out");
//					int len = last.getVar("len", Integer.class);
//					if (prev + len > period) len = (int)period - prev;
//					int r = last.getVar("r", Integer.class);
//					int g = last.getVar("g", Integer.class);
//					int b = last.getVar("b", Integer.class);
//					app.fill(r,g,b,120); app.noStroke();
//					app.rect(sx * prev / period, sy / 4f,sx * len / period, sy / 2f);
//					if (dt_class == String.class || dt_class == Vector2.class) {
//						Object ov = last.getVar("val", dt_class);
//						app.textAlign(nAlign.CENTER, nAlign.CENTER);
//						app.text(Utl.to_string(ov), 
//								sx * prev / period + (sx * len / period) / 2f, 
//								sy / 2f, 10f);
//					}
//					if (len + prev >= period) { as_next = false; }
//					else { as_next = true; prev += len; }
//				} else { as_next = false; }
//				wcnt++;
//			}
//
//			for (int i = 0 ; i < period ; i++) { 
//				Object ov = instance.get("get_var_at_step", i);
//				if (ov == null) continue;
//				if (dt_class == Float.class && ov.getClass() == Float.class) {
//					float min = 0f, max = 1f; 
//					if (ctrl.get_setting(data_ref, "min") != null) 
//						min = (float)ctrl.get_setting(data_ref, "min");
//					if (ctrl.get_setting(data_ref, "max") != null) 
//						max = (float)ctrl.get_setting(data_ref, "max");
//					app.fill(255,180); app.noStroke();
//					float v = (float)ov;
//					if (v < min || v > max) continue;
//					float p = (v-min) / (max-min);
//					app.rect(sx * i / period, sy * p,sx / period, 1f);
//				} else if (dt_class == Integer.class && ov.getClass() == Integer.class) {
//					float min = 0f, max = 1f; 
//					if (ctrl.get_setting(data_ref, "min") != null) 
//						min = (float)ctrl.get_setting(data_ref, "min");
//					if (ctrl.get_setting(data_ref, "max") != null) 
//						max = (float)ctrl.get_setting(data_ref, "max");
//					app.fill(255,180); app.noStroke();
//					int v = (int)ov;
//					if (v < min || v > max) continue;
//					float p = (v-min) / (max-min);
//					app.rect(sx * i / period, sy * p,sx / period, 1f);
//				} else if (dt_class == Boolean.class && ov.getClass() == Boolean.class) {
//					app.fill(255,200,0,180); app.noStroke();
//					boolean v = (boolean)ov;
//					if (v) app.rect(sx * i / period, 0f, sx / period, sy);
//				}
//			}
//			
//			
//			int step = head.getVar("step", Integer.class);
//			float f = step / period;
//			app.stroke(255,3f); app.line(sx * f, 0, sx * f, sy);
//			
//		}}) 
//		.commande(pNode.getCom(CT.COM_ADD_WIDGET))
//		.closeSec()
//		.commande(pNode.getCom(CT.COM_ADD_COL))
//		.openSec()
//		.param("run", new nRun() {public void run() {
//			instance.get("pop_ctrl_key"); }})
//		.run(pNode.getRun(CT.RUNP_ADD_TRIGG), "add_key", ">", (int)6)
//		.closeSec()
//		;
//		
//		stand_ctrl_chan.openSec()
//		.param("hide", true, "keys", new String[] {"ctrl_chan"}, "filters", new String[] {"ctrl_chan"}) 
//		.run(pNode.getRun(CT.RUNS_ADD_CHAIN_PLUGS), "ctrl_chan", "bottom")
//		.closeSec()
//		.openSec()
//		.param("hide", true, "keys", new String[] {"ctrl_key"}, "filters", new String[] {"ctrl_key"}) 
//		.run(pNode.getRun(CT.RUNS_ADD_CHAIN_START_PLUG), "ctrl_key", "right")
//		.closeSec()
//		;
//		
//		
//		
//		pStandard stand_ctrl_key_abstract = pStandard.newAbstractStandard();
//		stand_ctrl_key_abstract.process()
//		.openSec().param("run", new nRun() {public void run() {
//			instance.setVar("len", instance.getVar("len", Integer.class)+(int)1); }})
//		.run(pNode.getRun(CT.RUNP_ADD_TRIGG), "add_len", "+", (int)1).closeSec()
//		.openSec().param("def", (int)1)
//		.run(pNode.getRun(CT.RUNP_VAR_INT_FIELD), "len", (int)3).closeSec()
//		.openSec().param("run", new nRun() {public void run() {
//			instance.setVar("len", instance.getVar("len", Integer.class)-(int)1); }})
//		.run(pNode.getRun(CT.RUNP_ADD_TRIGG), "sup_len", "-", (int)1).closeSec()
//		;
//		
//		
//		pNode.newChainnedNodeModel("ctrl_key_flt")
//		.openSec().param("hide", true, "keys", new String[] {"ctrl_key"}, "filters", new String[] {"ctrl_key"}) 
//		.run(pNode.getRun(CT.RUNS_ADD_CHAIN_PLUGS), "ctrl_key", "right").closeSec()
//		.process()
//		.run(pNode.getRun(CT.RUNP_ADD_LABEL), "", (int)1)
//		.openSec().run(pNode.getRun(CT.RUNP_VAR_FLT_FIELD), "val", (int)4).closeSec()
//		.getStand().append(stand_ctrl_key_abstract)
//		;
//
//		pNode.newChainnedNodeModel("ctrl_key_boo")
//		.openSec().param("hide", true, "keys", new String[] {"ctrl_key"}, "filters", new String[] {"ctrl_key"}) 
//		.run(pNode.getRun(CT.RUNS_ADD_CHAIN_PLUGS), "ctrl_key", "right").closeSec()
//		.process()
//		.run(pNode.getRun(CT.RUNP_ADD_LABEL), "", (int)1)
//		.openSec().run(pNode.getRun(CT.RUNP_VAR_BOO_SWITCH), 
//				"val", "", (int)4).closeSec()
//		.getStand().append(stand_ctrl_key_abstract)
//		;
//
//		pNode.newChainnedNodeModel("ctrl_key_int")
//		.openSec().param("hide", true, "keys", new String[] {"ctrl_key"}, "filters", new String[] {"ctrl_key"}) 
//		.run(pNode.getRun(CT.RUNS_ADD_CHAIN_PLUGS), "ctrl_key", "right").closeSec()
//		.process()
//		.run(pNode.getRun(CT.RUNP_ADD_LABEL), "", (int)1)
//		.openSec().run(pNode.getRun(CT.RUNP_VAR_INT_FIELD), "val", (int)4).closeSec()
//		.getStand().append(stand_ctrl_key_abstract)
//		;
//
//		pNode.newChainnedNodeModel("ctrl_key_str")
//		.openSec().param("hide", true, "keys", new String[] {"ctrl_key"}, "filters", new String[] {"ctrl_key"}) 
//		.run(pNode.getRun(CT.RUNS_ADD_CHAIN_PLUGS), "ctrl_key", "right").closeSec()
//		.process()
//		.run(pNode.getRun(CT.RUNP_ADD_LABEL), "", (int)1)
//		.openSec().run(pNode.getRun(CT.RUNP_VAR_STR_FIELD), "val", (int)4).closeSec()
//		.getStand().append(stand_ctrl_key_abstract)
//		;
//
//		pNode.newChainnedNodeModel("ctrl_key_vec")
//		.openSec().param("hide", true, "keys", new String[] {"ctrl_key"}, "filters", new String[] {"ctrl_key"}) 
//		.run(pNode.getRun(CT.RUNS_ADD_CHAIN_PLUGS), "ctrl_key", "right").closeSec()
//		.process()
//		.run(pNode.getRun(CT.RUNP_ADD_LABEL), "", (int)1)
//		.openSec().run(pNode.getRun(CT.RUNP_VAR_VEC_FIELD), "val", (int)4).closeSec()
//		.getStand().append(stand_ctrl_key_abstract)
//		;
//		
//		
//		
//		
	}
	
}
