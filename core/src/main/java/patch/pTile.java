package patch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import app.Applet;
import app.nMap;
import app.nRun;
import app.nTransform;

import data.*;
import gui.*;
import patch.pNode.CT;
import plane.pBody;
import plane.pParam;
import plane.pProperty;
import plane.pSpace;
import plane.pTime;

public class pTile {
	
	public static void build(Applet app) {
		
		build_book(app);
		
		build_standard(app);
		
		build_coms(app);

	}

	
	
	
	
	public static void build_standard(Applet app) {

		float RS = app.gui.book.RS;

		
		pStandard.newStandard("tile", "tile")
		.addInst("tile_node", "inst")
		.addCollecInst("plugs", "ent")
		.addData("pos", new Vector2())
		.addData("gui", true)
		.addData("hightlight_count", (int)0)
		.setCreateRun(new nRun() {public void run() {

//			app.log("tile create_run "+instance.pool_ref);
			
			if (args == null || args.length == 0) return;
			boolean gui = arg(0,Boolean.class);
			instance.setData("gui", gui);
			
		}})
		.setInitRun(new nRun() {public void run() {
			pPatch patch = instance.patch;
			patch.tiles.add(instance);
			instance.sheet.tiles.add(instance);
			
			if (instance.getDataBoo("gui")) {
				nWidgetGroup group = patch.app.gui.addWidgetGroup("patch_tile");
				instance.addObject("group", group);
				if (tile_models_short.get(tile_models.getKey(instance.stand)) != null) 
					group.get("back").setText(//instance.pool_index + " " + 
							tile_models_short.get(tile_models.getKey(instance.stand))); 
				else group.get("back").setText(instance.pool_index + " " + 
						tile_models.getKey(instance.stand)); 
			}
			instance.addObject("plug_cnt", (int)0);
			instance.addObject("bounding_box_flag", false);
			
			nMap<nWidget> widget_map = new nMap<nWidget>();
			instance.addObject("widget_map", widget_map);
			
		}})
		.setLoadRun(new nRun() {public void run() {
			if (instance.getDataBoo("gui")) {
				nWidgetGroup group = instance.object("group", nWidgetGroup.class);
				group.metode("link_to_instance", instance);
			}
			pInstance node = instance.getInst("tile_node");
			if (node != null && !node.collecInstContains("tiles", instance)) 
				node.collecInstAdd("tiles", instance);
		}})
		.setClearRun(new nRun() {public void run() {
			pInstance node = instance.getInst("tile_node");
			if (node != null) node.collecInstRemove("tiles", instance);
			ArrayList<pInstance> plug_inst = instance.collecInstAll("plugs");
			if (plug_inst != null) 
				for (pInstance e : Applet.duplic(plug_inst)) e.clear(); 
			
			pPatch patch = instance.patch;
			patch.tiles.remove(instance);
			instance.sheet.tiles.remove(instance);
			if (instance.hasObject("group")) 
				instance.object("group", nWidgetGroup.class).clear();
			
		}})
		.newRun("highlight", new nRun() {public void run() {
			instance.setData("hightlight_count", pNode.LINK_HIGHLIGHT_TEMP);
		}})
		.newRun("frame", new nRun() {public void run() {
			if (instance.getDataInt("hightlight_count") > 0) 
				instance.addDataInt("hightlight_count", (int)-1); 
			if (instance.getDataBoo("gui")) {
				nWidgetGroup group = instance.object("group", nWidgetGroup.class);
				if (instance.getDataInt("hightlight_count") > 0) 
					group.get("back").setOutline(true);
				else group.get("back").setOutline(false); 
			}
		}})
		.newRun("clear_trigg", new nRun() {public void run() {
			instance.clear();
		}})
		.newRun("attract_plugged", new nRun() {public void run() {
			if (instance.getData("gui", Boolean.class)) {
				for (pInstance e : instance.collecInstAll("plugs")) 
						if (!e.getVar("attracted", Boolean.class) && 
								e.getInst("plugged") != null && 
								!e.getInst("plugged").getVar("attracted", Boolean.class)) {
					Vector2 pos = e.get("getCenter", Vector2.class);
					Vector2 pos2 = e.getInst("plugged").get("getCenter", Vector2.class);
					Vector2 f = new Vector2(pos).sub(pos2);
					if (f.len() > 0f) {
	//					f.scl(0.5f);
						pInstance tile = e.getInst("plugged").getInst("tile");
						if (tile != null) { tile.object("group", nWidgetGroup.class)
							.metode("move",f); }
					}
					e.setVar("attracted", true); 
					e.getInst("plugged").setVar("attracted", true); 
					e.getInst("plugged").getInst("tile").run("attract_plugged"); 
				}
			}
		}})
		.newRun("all_flag_recursion", new nRun() {public void run() {  
			pInstance node = instance.getInst("tile_node");
//			if (node == null) return;
			for (pInstance t : node.collecInstAll("tiles")) {
				if (!t.hasObject("recursion_flag")) 
					t.addObject("recursion_flag", true);
				t.setObject("recursion_flag", true);
			}
		}})
		.newRun("flag_do_recursion", new nRun() {public void run() {  
			if (!instance.hasObject("recursion_flag")) 
				instance.addObject("recursion_flag", true);
			if (instance.object("recursion_flag", Boolean.class)) return; 
			instance.setObject("recursion_flag", true);
			for (pInstance c : instance.collecInstAll("plugs")) 
					if (c.getInst("plugged") != null) {
				pInstance n = c.getInst("plugged").getInst("tile");
				if (n == null) continue; else n.run("flag_do_recursion"); }
		}})
		.newRun("get_bounding_box", new nRun() {public Object get() { 
			if (!instance.hasObject("recursion_flag")) 
				instance.addObject("recursion_flag", true);
			if (!instance.getData("gui", Boolean.class)) return null; 
			if (!instance.object("recursion_flag", Boolean.class)) return null; 
			instance.setObject("recursion_flag", false);
			nWidgetGroup group = instance.object("group", nWidgetGroup.class);
			Rectangle this_rect = group.get("back")
					.getRectRelativeToParent(instance.sheet.sheet_ref);
			 
//			app.debugTransf(instance.sheet.sheet_bound_bound.warptransform);
//			app.debugRect(this_rect); 
			
			if (instance.collecInstSize("plugs") == 0) { return this_rect; }
			ArrayList<Rectangle> arr = new ArrayList<Rectangle>();
			arr.add(this_rect);
			for (pInstance c : instance.collecInstAll("plugs")) 
					if (c.getInst("plugged") != null) {
				pInstance n = c.getInst("plugged").getInst("tile");
				if (n == null) continue;
				Rectangle r = n.get("get_bounding_box", Rectangle.class); 
				if (r != null) arr.add(r); }
			Rectangle bb = Applet.get_bounding_rect(arr);
			
//			app.debugRect(bb);
			
			arr.clear(); 
			return bb;
		}})
		.newRun("get_all_plugged", new nRun() {public Object get() { 
			if (!instance.hasObject("recursion_flag")) 
				instance.addObject("recursion_flag", true);
			if (!instance.object("recursion_flag", Boolean.class)) return null; 
			instance.setObject("recursion_flag", false);
			ArrayList<pInstance> arr = new ArrayList<pInstance>();
			arr.add(instance);
			for (pInstance c : instance.collecInstAll("plugs")) 
					if (c.getInst("plugged") != null) {
				pInstance n = c.getInst("plugged").getInst("tile");
				if (n == null) continue; 
				ArrayList<pInstance> narr = n.get("get_all_plugged", ArrayList.class);
				if (narr != null) for (pInstance a : narr) if (!arr.contains(a)) arr.add(a); 
			}
			return arr;
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
		.newRun("get_mapped_widget", new nRun() {public Object get() {
			nMap<nWidget> widget_map = instance.object("widget_map", nMap.class);
			if (widget_map != null) return widget_map.get(arg(0, String.class));
			return null;
		}})
		.newRun("pop_tile", new nRun() {public Object get() { 
			String plug_ref = arg(0,String.class);
			String tile_model = arg(1,String.class);
			String tile_plug = arg(2,String.class);
			pSheet sheet = instance.sheet;
			pInstance t = sheet.newTile(tile_model, instance.getDataBoo("gui"));
			if (t != null) {
				pInstance node = instance.getInst("tile_node");
				t.setInst("tile_node", node);
				if (!node.collecInstContains("tiles", t)) 
					node.collecInstAdd("tiles", t);
				pInstance inst_plug = instance.get("get_plug", pInstance.class, plug_ref);
				pInstance t_plug = t.get("get_plug", pInstance.class, tile_plug);
				if (inst_plug != null && t_plug != null) {
					inst_plug.run("link_to", t_plug);
				}
				return t;
			}
			return null;
		}})
		;
		
		
		

		newRunTool("add_plug", CT.ADD_PLUG, new nRun() {public void run() {
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
							app.logn("ERROR : pTile "+instance.pool_ref+" add_plug : "+
									ref+" allready exist");
							return;
						} }
					in = instance.sheet.newInstance("plug", instance, ref, side);
					in.is_new = true;
					in.setData("gui", instance.getDataBoo("gui"));
				} 
				in.run("init_run", param);
				in.is_new = false;
				instance.setObject("plug_cnt", plug_cnt+1);
			}});
		}});
		
		pStandard.newStandard("plug", "ent")
		.addData("ref", "")
		.addData("side", "")
		.addInst("tile", "tile")
		.addInst("plugged", "ent")
		.addData("hightlight_count", (int)0)
		.addData("gui", true)
		.setCreateRun(new nRun() {public void run() {

//			app.log("plug create_run "+instance.pool_ref);
			
			pInstance tile = instance.getInst("tile");
			if (tile == null) tile = arg(0,pInstance.class);
			if (tile == null) return;
			instance.setInst("tile", tile);
			
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
			
			pInstance tile = instance.getInst("tile");
			pPatch patch = instance.patch;
			patch.plugs.add(instance);
			instance.sheet.plugs.add(instance);
//			instance.addObject("patch", patch);
			if (!tile.collecInstContains("plugs",instance))
				tile.collecInstAdd("plugs",instance);
			instance.addObject("tile", tile);
			
			instance.setVar("attracted", false);
			
			if (instance.getDataBoo("gui")) {

				nWidgetGroup group = tile.object("group", nWidgetGroup.class);
				nWidget w = (nWidget)group.metodeGet("add_widget",(int)2);
				w.copyLookFrom(app.gui.book.getModel("PL_base"));
				
				w.setInfo(instance.getData("ref", String.class));
				
				w.setParent((nWidget)group.metodeGet("get_side_stack", 
						instance.getData("side", String.class)));
				
				w.setTrigger();//.setRightTrigger();
				
				nRun run_trigg_r = new nRun(instance) {public void run() {
					pInstance inst = (pInstance)builder;
					inst.run("trigger_clic");
				}};
				
	//			w.addEventTriggerRight(run_trigg_r);
				
				w.addEventTrigger(run_trigg_r);
	
				instance.addObject("plug_widget", w);
			}
			
			if (hasParam("event_receive")) {
				instance.addObject("event_receive", getParam("event_receive", nRun.class)); }
			if (hasParam("offer")) {
				instance.addObject("offer", getParam("offer", nRun.class)); }		
			
		}})
		.setLoadRun(new nRun() {public void run() {
			if (instance.getDataBoo("gui") && instance.getInst("plugged") != null && 
					instance.getInst("plugged").object("plug_widget", nWidget.class) != null && 
					instance.object("plug_widget", nWidget.class) != null) {
				if (instance.getData("side", String.class).equals("left")) {
					instance.getInst("plugged")
					.object("plug_widget", nWidget.class)
					.copyFrom(app.gui.book.getModel("PL_plugged_H_R"));
					instance.object("plug_widget", nWidget.class)
					.copyFrom(app.gui.book.getModel("PL_plugged_H_L"));
				} else if (instance.getData("side", String.class).equals("right")) {
					instance.getInst("plugged")
					.object("plug_widget", nWidget.class)
					.copyFrom(app.gui.book.getModel("PL_plugged_H_L"));
					instance.object("plug_widget", nWidget.class)
					.copyFrom(app.gui.book.getModel("PL_plugged_H_R"));
				} else {
					instance.getInst("plugged")
					.object("plug_widget", nWidget.class)
					.copyFrom(app.gui.book.getModel("PL_plugged_V"));
					instance.object("plug_widget", nWidget.class)
					.copyFrom(app.gui.book.getModel("PL_plugged_V"));
				}
			} else if (instance.object("plug_widget", nWidget.class) != null) {
				if (instance.getData("side", String.class).equals("left")) {
					instance.object("plug_widget", nWidget.class)
					.copyFrom(app.gui.book.getModel("PL_open_H"));
				} else if (instance.getData("side", String.class).equals("right")) {
					instance.object("plug_widget", nWidget.class)
					.copyFrom(app.gui.book.getModel("PL_open_H"));
				} else {
					instance.object("plug_widget", nWidget.class)
					.copyFrom(app.gui.book.getModel("PL_open_V"));
				}
			}
		}})
		.setClearRun(new nRun() {public void run() {
			pPatch patch = instance.patch;
			patch.plugs.remove(instance);
			instance.sheet.plugs.remove(instance);
			pInstance tile = instance.getInst("tile");
			if (tile != null) {
				tile.collecInstRemove("plugs",instance); }
			if (instance.getInst("plugged") != null) {
				if (instance.getInst("plugged")
						.object("plug_widget", nWidget.class) != null) { 
					if (instance.getData("side", String.class).equals("left") || 
							instance.getData("side", String.class).equals("right")) 
						instance.getInst("plugged")
							.object("plug_widget", nWidget.class)
							.copyFrom(app.gui.book.getModel("PL_open_H"));
					else instance.getInst("plugged")
						.object("plug_widget", nWidget.class)
						.copyFrom(app.gui.book.getModel("PL_open_V")); }
				instance.getInst("plugged").setInst("plugged", ""); }
		}})
		.newRun("trigger_clic", new nRun() {public void run() {
			pInstance tile = instance.getInst("tile");
			
			nWidget w = instance.object("plug_widget", nWidget.class);
			if (w == null) return;
			
			instance.patch.patch_dropmenu.metode("clear_entrys");
			
			PlugDef this_pd = null;
			String this_ref = instance.getData("ref", String.class);
			for (PlugDef p : getTileModelPlugs(tile.stand.ref)) 
				if (p.ref.equals(this_ref)) { this_pd = p; break; }
			if (this_pd == null) {
				app.logn("ERROR plug run trigg r");
			}
			if (instance.getInst("plugged") != null) {
				pInstance plugged_plug = instance.getInst("plugged");
				pInstance plug_tile = plugged_plug.getInst("tile");
				if (plug_tile == null) return;
				PlugDef plug_pd = null;
				String plug_ref = plugged_plug.getData("ref", String.class);
				for (PlugDef p : getTileModelPlugs(plug_tile.stand.ref)) 
					if (p.ref.equals(plug_ref)) { plug_pd = p; break; }
				if (plug_pd == null) {
					app.logn("ERROR plug run trigg r");
				}
				for (String tile_model : tile_models.allKey()) 
						if (!Applet.contains(not_poppable_models, tile_model)) {
					pStandard tile_model_stan = tile_models.get(tile_model);
					for (PlugDef pd : getTileModelPlugs(tile_model_stan)) {
						if (key_filter_compatibility(this_pd.keys, this_pd.filters, 
								pd.keys, pd.filters)) {
							String r = pd.ref;
							for (PlugDef pd2 : getTileModelPlugs(tile_model_stan)) {
								if (pd2 != pd && key_filter_compatibility(plug_pd.keys, 
										plug_pd.filters, pd2.keys, pd2.filters)) {
									String r2 = pd2.ref;
									nWidget w1 = (nWidget)instance.patch.patch_dropmenu
											.metodeGet("add_entry_custom", 
													tile_model + " : " + r + "<->" + r2,
													RS*8f, RS*3f/3f);
									w1.addEventTrigger(new nRun(tile, plug_tile, 
											this_ref, plug_ref, 
											tile_model, r, r2) { 
											public void run() {
										pInstance tile = ((pInstance)args[0]);
										pInstance plug_tile = ((pInstance)args[1]);
										String this_ref = ((String)args[2]);
										String plug_ref = ((String)args[3]);
										String tile_model = ((String)args[4]);
										String to_this_ref = ((String)args[5]);
										String to_plug_ref = ((String)args[6]);
										pInstance this_plug = tile.get("get_plug", 
												pInstance.class, this_ref);
										pInstance plugged_plug = plug_tile
												.get("get_plug", pInstance.class, plug_ref);
										this_plug.run("unlink");
										pInstance n = tile.get("pop_tile", pInstance.class, 
												this_ref, tile_model, to_this_ref);
										pInstance n_plug = n.get("get_plug", 
												pInstance.class, to_plug_ref);
										n_plug.run("link_to", plugged_plug);
									}}); 
								}
							}
						}
					}
				}
			} else {
				for (String tile_model : tile_models.allKey()) 
					if (!Applet.contains(not_poppable_models, tile_model)) {
					pStandard tile_model_stan = tile_models.get(tile_model);
					for (PlugDef pd : getTileModelPlugs(tile_model_stan)) {
						if (key_filter_compatibility(this_pd.keys, this_pd.filters, 
								pd.keys, pd.filters)) {
							String r = pd.ref;
							nWidget w1 = (nWidget)instance.patch.patch_dropmenu
									.metodeGet("add_entry_custom", tile_model + " - " + r,
											RS*8f, RS*3f/3f);
							w1.addEventTrigger(new nRun(tile, this_ref, tile_model, r) { 
									public void run() {
								((pInstance)args[0]).get("pop_tile", 
										((String)args[1]), ((String)args[2]), 
										((String)args[3]));  }}); 
						}
					}
				}
			}
			instance.patch.patch_dropmenu.metode("open", w); 
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
			if (w != null) {
				if (instance.getDataInt("hightlight_count") > 0) w.setOutline(true);
				else w.setOutline(false); }
			if (instance.getDataBoo("gui") && instance.getData("side", String.class)
					.equals("right")) {
//				instance.object("plug_widget", nWidget.class)
//					.setInfo(instance.getData("ref", String.class)); 
			}
		}})
		.newRun("unlink", new nRun() {public void run() {
			if (instance.getInst("plugged") != null) {
				if (instance.getInst("plugged")
						.object("plug_widget", nWidget.class) != null) { 
					if (instance.getData("side", String.class).equals("left") || 
							instance.getData("side", String.class).equals("right")) 
						instance.getInst("plugged")
							.object("plug_widget", nWidget.class)
							.copyFrom(app.gui.book.getModel("PL_open_H"));
					else instance.getInst("plugged")
						.object("plug_widget", nWidget.class)
						.copyFrom(app.gui.book.getModel("PL_open_V")); }
				instance.getInst("plugged").setInst("plugged", ""); 
				if (instance.object("plug_widget", nWidget.class) != null) { 
					if (instance.getData("side", String.class).equals("left") || 
							instance.getData("side", String.class).equals("right")) 
						instance.object("plug_widget", nWidget.class)
						.copyFrom(app.gui.book.getModel("PL_open_H"));
					else instance.object("plug_widget", nWidget.class)
					.copyFrom(app.gui.book.getModel("PL_open_V")); }
				instance.setInst("plugged", ""); }
		}})
		.newRun("link_to", new nRun() {public void run() {
			pInstance c = arg(0, pInstance.class);
			if (instance.getInst("plugged") == null && c != instance && 
					c != null && c.getInst("plugged") == null) {
				c.setInst("plugged", instance);
				instance.setInst("plugged", c);
				if (instance.getDataBoo("gui")) {
					if (instance.getData("side", String.class).equals("left")) {
						nWidget w = instance.object("plug_widget", nWidget.class);
						if (w != null) w.copyFrom(app.gui.book.getModel("PL_plugged_H_L"));
						w = c.object("plug_widget", nWidget.class);
						if (w != null) w.copyFrom(app.gui.book.getModel("PL_plugged_H_R"));
					} else if (instance.getData("side", String.class).equals("right")) {
						nWidget w = instance.object("plug_widget", nWidget.class);
						if (w != null) w.copyFrom(app.gui.book.getModel("PL_plugged_H_R"));
						w = c.object("plug_widget", nWidget.class);
						if (w != null) w.copyFrom(app.gui.book.getModel("PL_plugged_H_L"));
					} else {
						nWidget w = instance.object("plug_widget", nWidget.class);
						if (w != null) w.copyFrom(app.gui.book.getModel("PL_plugged_V"));
						w = c.object("plug_widget", nWidget.class);
						if (w != null) w.copyFrom(app.gui.book.getModel("PL_plugged_V"));
					}
				}
			}
		}}).runArgs("co", pInstance.class)
		.newRun("send", new nRun() {public void run() {
			pInstance plugged = instance.getInst("plugged");
			if (plugged == null) return;
			instance.run("highlight_self");
			if (args == null || args.length == 0) { plugged.run("receive"); }
			else {
				Object[] a = new Object[args.length];
				for (int i = 0 ; i < args.length ; i++) a[i] = Applet.copy(args[i]);
				plugged.run("receive", a); }
		}}).runArgs("send", Object.class)
		.newRun("receive", new nRun() {public void run() {
			if (instance.hasObject("event_receive")) {
				instance.run("highlight_self");
				instance.object("event_receive", nRun.class)
					.do_run(instance, param, Applet.duplic(args)); }
		}})
		.newRun("obtain", Object.class, new nRun() {public Object get() {
			pInstance plugged = instance.getInst("plugged");
			if (plugged != null) { 
				instance.run("highlight_self"); 
//				if (instance.getDataBoo("gui")) {
//					Object r = plugged.get("provide");
//					String info = "";
//					if (r != null) info += Applet.to_string(r);
//					instance.object("plug_widget", nWidget.class).setInfo(info);
//					return r;
//				} else return plugged.get("provide");
				return plugged.get("provide");
			}
			return null;
		}})
		.newRun("provide", Object.class, new nRun() {public Object get() {
			if (instance.hasObject("offer")) {
				instance.run("highlight_self");
				Object r = Applet.copy( instance.object("offer", nRun.class)
						.do_get(instance, param) );
				if (instance.getDataBoo("gui")) {
					String info = "";
					if (r != null) info += Applet.to_string(r);
					instance.object("plug_widget", nWidget.class).setInfo(info);
				}
				return r; }
			return null;
		}})
		;
		
		
	}
	
	
	
	public static boolean key_filter_compatibility(String[] keys, String[] filters, 
			String[] keys2, String[] filters2) {
		boolean ok = true;
		for (String f : filters) if (!f.equals("all")) ok = ok && 
				(Applet.contains(keys2, f) || Applet.contains(keys2, "all"));
		for (String f : filters2) if (!f.equals("all")) ok = ok && 
				(Applet.contains(keys, f) || Applet.contains(keys, "all"));
		return ok;
	}
	
	
	
	public static void build_coms(Applet app) {
		
		float RS = app.gui.book.RS;

		newRunTool("add_obtain_plug", CT.ADD_OBTAIN_PLUG, new nRun() {public void run() {
			pStandard stand = arg(0,pStandard.class);
			String ref = arg(1,String.class);
			if (stand == null || ref == null) return;

			pProcess proc = stand.process();

			boolean first_obtain_plug = true;
//			boolean sec_obtain_plug = true;
			//use the existance of a rundef as a flag to indicate an obtain plug allready exist
			for (pStandard.RunDef rd : stand.rundefs) {
				if (rd.ref.equals("FLAG_first_obtain_plug_run")) first_obtain_plug = false; 
//				if (rd.ref.equals("FLAG_sec_obtain_plug_run")) sec_obtain_plug = false; 
			}
			
			if (first_obtain_plug) {
				stand.newRun("FLAG_first_obtain_plug_run", new nRun() { public void run() {}});
				proc.useInit().commande(new nRun() { public void run() {
					if (!instance.getData("gui", Boolean.class)) return;
					nRun run_frame = new nRun(instance) { public void run() {
						pInstance tile = (pInstance)builder;
						ArrayList<pInstance> plug_arr = new ArrayList<pInstance>();
						ArrayList<Rectangle> bb_arr = new ArrayList<Rectangle>();
						for (pInstance plg : tile.collecInstAll("plugs")) 
								if (plg.getData("side", String.class).equals("left")) {
							if (plg.getInst("plugged") != null) {
								pInstance plugged = plg.getInst("plugged");
								pInstance plug_tile = plugged.object("tile", 
										pInstance.class);
								plug_tile.run("all_flag_recursion");
								tile.setObject("recursion_flag", false);
								Rectangle bb = plug_tile.get("get_bounding_box",  
										Rectangle.class);
								if (bb != null) { bb_arr.add(bb); plug_arr.add(plg); }
								else {
									bb = plg.object("plug_widget", nWidget.class)
											.getRectRelativeToParent(tile.sheet.sheet_ref);
//									bb.height += 2f;
									bb_arr.add(bb); 
									plug_arr.add(plg); 
								}
							} else {
								Rectangle bb = plg.object("plug_widget", nWidget.class)
										.getRectRelativeToParent(tile.sheet.sheet_ref);
//								bb.height += 2f;
								bb_arr.add(bb); 
								plug_arr.add(plg); 
							}
						}
						for (int i = 0 ; i < bb_arr.size() ; i++) {
							pInstance plug = plug_arr.get(i);
							Rectangle bb = bb_arr.get(i);
							plug.object("plug_widget", nWidget.class)
							.setSY(bb.height);
						}
					}};
					instance.patch.addEventFrame(run_frame);
					instance.addObject(ref+"_plug_run_frame", run_frame);
				}}).useClear().commande(new nRun() { public void run() {
					if (instance.hasObject(ref+"_plug_run_frame"))
						instance.patch.removeEventFrame(
								instance.object(ref+"_plug_run_frame", nRun.class));
					;
				}}).useInit();
			} 
//			else if (sec_obtain_plug) {
//				stand.newRun("FLAG_sec_obtain_plug_run", new nRun() { public void run() {}});
//				
//			}
			
			proc.openSec().run(getRun(CT.ADD_PLUG), ref, "left").closeSec();
			
			if (args.length > 2) {
				String key = arg(2,String.class);
				PlugDef pd = new PlugDef(ref, new String[] {"obtain", key}, new String[] {"offer", key});
				tile_model_plugs.get(stand.ref).add(pd);
			} else {
				PlugDef pd = new PlugDef(ref, new String[] {"obtain"}, new String[] {"offer"});
				tile_model_plugs.get(stand.ref).add(pd);
			}
		}});

		newRunTool("add_offer_plug", CT.ADD_OFFER_PLUG, new nRun() {public void run() {
			pStandard stand = arg(0,pStandard.class);
			String ref = arg(1,String.class);
			if (stand == null || ref == null) return;
			pProcess proc = stand.process();
			proc.openSec();
			if (hasParam("offer")) {
				nRun pr = getParam("offer", nRun.class);
				proc.param("offer", pr); }
			proc.run(getRun(CT.ADD_PLUG), ref, "right").closeSec();
			if (args.length > 2) {
				String key = arg(2,String.class);
				PlugDef pd = new PlugDef(ref, 
						new String[] {"offer", key}, new String[] {"obtain", key});
				tile_model_plugs.get(stand.ref).add(pd);
			} else {
				PlugDef pd = new PlugDef(ref, new String[] {"offer"}, new String[] {"obtain"});
				tile_model_plugs.get(stand.ref).add(pd);
			}
		}});
		
		newRunTool("add_run_plugs", CT.ADD_RUN_PLUGS, new nRun() {public void run() {
			pStandard stand = arg(0,pStandard.class);
			if (stand == null) return;
			
			stand
//			.addInst("jump_run", "tile")
			.replaceRun("clear_trigg", new nRun() {public void run() {
				pInstance run_out = instance.get("get_plug", pInstance.class, "run_out");
				pInstance run_in = instance.get("get_plug", pInstance.class, "run_in");
				if (run_in == null || run_out == null) return;
				pInstance run_out_plugged = run_out.getInst("plugged");
				pInstance run_in_plugged = run_in.getInst("plugged");
				if (run_in_plugged != null && run_out_plugged != null) {
					pInstance in_tile = run_in_plugged
							.object("tile", pInstance.class);
					pInstance out_tile = run_out_plugged
							.object("tile", pInstance.class);
					if (in_tile != null && out_tile != null) {
						run_in_plugged.run("unlink");
						run_out_plugged.run("unlink");
						run_in_plugged.run("link_to", run_out_plugged);
					}
				}
				instance.run("all_flag_recursion");
				if (run_out_plugged != null) {
					run_out_plugged.object("tile", pInstance.class)
						.setObject("recursion_flag", false); }
				if (run_in_plugged != null) {
					run_in_plugged.object("tile", pInstance.class)
						.setObject("recursion_flag", false); }
				instance.setObject("recursion_flag", true);
				ArrayList<pInstance> narr = instance.get("get_all_plugged", ArrayList.class);
				if (narr != null) for (pInstance a : narr) a.clear();
				
				instance.clear();
			}})
			;
			
			pProcess proc = stand.process();
//			proc.run(getRun(CT.OBTAIN_VAR), "pass_run", true);
//			proc.run(getRun(CT.OBTAIN_VAR), "jump_run", false);

			proc.useInit().commande(new nRun() { public void run() {
				if (!instance.getData("gui", Boolean.class)) return;
				nRun run_frame = new nRun(instance) { public void run() {
					pInstance tile = (pInstance)builder;
					pInstance run_out = tile.get("get_plug", pInstance.class, "run_out");
					pInstance run_in = tile.get("get_plug", pInstance.class, "run_in");
					if (run_in == null || run_out == null) return;
					pInstance run_out_plugged = run_out.getInst("plugged");
					pInstance run_in_plugged = run_in.getInst("plugged");
					tile.run("all_flag_recursion");
					if (run_out_plugged != null) {
						run_out_plugged.object("tile", pInstance.class)
							.setObject("recursion_flag", false); }
					if (run_in_plugged != null) {
						run_in_plugged.object("tile", pInstance.class)
							.setObject("recursion_flag", false); }
					tile.setObject("recursion_flag", true);
					Rectangle bb = tile.get("get_bounding_box", Rectangle.class);
					if (bb == null) return;
					
					
//					app.debugTransf(tile.sheet.sheet_bound_bound.warptransform);
//					app.debugRect(bb);
					
					
					nWidgetGroup group = tile.object("group", nWidgetGroup.class);
					Vector2 ref_pos = group.get("ref").getPosRelativeToParent(tile.sheet.sheet_ref);
					Vector2 back_pos = group.get("back").getPosRelativeToParent(tile.sheet.sheet_ref);
					float sy = group.get("back").getLocalSY();
					float bottom_space = ref_pos.y - bb.y;
					float top_space = bb.y + bb.height - back_pos.y - sy - RS * 7f / 8f;
					tile.setObject("bot_space", bottom_space);
					if (run_out_plugged != null) {
						pInstance out_tile = run_out_plugged
								.object("tile", pInstance.class); 
						if (out_tile.hasObject("bot_space")) {
							float out_bot_sp = out_tile.object("bot_space", Float.class);
							float top_sp = out_bot_sp + top_space + RS/8f;
							float cur_sp = run_out.object("plug_widget", nWidget.class)
									.getLocalSY();
							if (Math.abs(top_sp - cur_sp) > 2) {
								run_out.object("plug_widget", nWidget.class)
									.setSY(top_sp);
								run_out_plugged.object("plug_widget", nWidget.class)
									.setSY(top_sp);
							}
						}
					}
				}};
				instance.patch.addEventFrame(run_frame);
				instance.addObject("run_plug_run_frame", run_frame);
			}}).useClear().commande(new nRun() { public void run() {
				if (instance.hasObject("run_plug_run_frame"))
					instance.patch.removeEventFrame(
							instance.object("run_plug_run_frame", nRun.class));
				;
			}}).useInit();
			
			if (hasParam("run_event")) {
				nRun pr = getParam("run_event", nRun.class);
				proc.commande(new nRun(pr) { public void run() {
					instance.addObject("run_event", (nRun)builder); }});
			}
			
			proc.openSec().run(getRun(CT.ADD_PLUG), "run_in", "bottom").closeSec();
			proc.openSec().run(getRun(CT.ADD_PLUG), "run_out", "top").closeSec();

			PlugDef pd = new PlugDef("run_in", new String[] {"run","in"}, new String[] {"run","out"});
			tile_model_plugs.get(stand.ref).add(pd);
			PlugDef pd2 = new PlugDef("run_out", new String[] {"run","out"}, new String[] {"run","in"});
			tile_model_plugs.get(stand.ref).add(pd2);
		}});
		

		newRunTool("run_obtain_var", CT.OBTAIN_VAR, new nRun() {public void run() {
			pProcess proc = arg(0,pProcess.class);
			String ref = arg(1,String.class);
			Object def = arg(2,Object.class);
			if (ref == null || def == null) return;
			if (proc == null) return;
			proc.commande(new nRun() { public void run() {
				if (!instance.hasVar(ref)) { instance.addVar(ref, def); } }});
		}});
		
		newComTool("add_widget", CT.ADD_WIDGET, nWidget.class, new nRun() {public Object get() {
			
//			app.log("com_add_widget "+instance.pool_ref);
			
			if (!instance.getDataBoo("gui")) return null;
	
			int width = getParamOrDef("width", Integer.class, (int)2);
			float height = getParamOrDef("height", Float.class, 1f);
			String text = getParamOrDef("text", String.class, "");
			String info = getParamOrDef("info", String.class, "");
			
			float scale_min = getParamOrDef("scale_min", Float.class, pNode.DEF_SCALE_MIN);
			float scale_max = getParamOrDef("scale_max", Float.class, pNode.DEF_SCALE_MAX);
			
			nWidgetGroup group = instance.object("group", nWidgetGroup.class);
			nWidget w = (nWidget)group.metodeGet("add_widget",(int)2);

			w.copyColorFrom(app.gui.book.getModel("ref"));
			
			w.setText(text);
			w.setInfo(info);
			w.setParent(group.get("back"));
			w.setSX(w.getLocalSX()*width/2f);
			w.setSY(w.getLocalSY()*height);
			w.setScaleLimitNoDraw(scale_min, scale_max);
//			w.set_color_background(app.color((int)(255*w.color_background.r), 
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

		newComTool("add_trigg", CT.ADD_TRIGG, nWidget.class, new nRun() {public Object get() {
	
	//		app.log("com_add_trigg");
			
			if (!instance.getDataBoo("gui")) return null;
	
			nWidget w = getCom(CT.ADD_WIDGET,instance,param,nWidget.class);
			w.setTrigger();
			if (hasParam("run", nRun.class)) {
				nRun run = getParam("run", nRun.class);
				w.addEventTrigger(new nRun(instance,param,run) {public void run() {
					((nRun)args[2]).do_run((pInstance)args[0],(pPar)args[1]); }});
			}
			return w;
		}});
	
		newComTool("add_switch", CT.ADD_SWITCH, nWidget.class, new nRun() { public Object get() {
			
			if (!instance.getDataBoo("gui")) return null;
			
			nWidget w = getCom(CT.ADD_WIDGET,instance,param,nWidget.class);
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
	
		newComTool("add_field", CT.ADD_FIELD, nWidget.class, new nRun() {public Object get() {

//			app.log("com_add_field "+instance.pool_ref);

			if (!instance.getDataBoo("gui")) return null;
			
			nWidget w = getCom(CT.ADD_WIDGET,instance,param,nWidget.class);
			w.setField(true)
			.copyLookFrom(app.gui.book.getModel("text_field"));
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
						target.setVar(var_ref, Applet.tofloat(w.getText()));
					} else if (var_cls.equals(Integer.class.getName())) {
						target.setVar(var_ref, Applet.toint(w.getText()));
					} else if (var_cls.equals(String.class.getName())) {
						target.setVar(var_ref, w.getText());
					} else if (var_cls.equals(Vector2.class.getName()) && var_axe != null) {
						Vector2 v = new Vector2(target.getVar( var_ref, Vector2.class));
						float n = Applet.tofloat(w.getText());
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
						text = "" + Applet.trimFlt(target.getVar(var_ref, Float.class), frez);
					} else if (var_cls.equals(Integer.class.getName())) {
						text = "" + target.getVar(var_ref, Integer.class);
					} else if (var_cls.equals(String.class.getName())) {
						text = "" + target.getVar(var_ref, String.class);
					} else if (var_cls.equals(Vector2.class.getName()) && var_axe != null) {
						Vector2 v = new Vector2(target.getVar(var_ref, Vector2.class));
						if (var_axe.equals("x")) text = "" + Applet.trimFlt(v.x, frez); 
						else text = "" + Applet.trimFlt(v.y, frez);
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
	
		newComTool("add_watch", CT.ADD_WATCH, nWidget.class, new nRun() { public Object get() {

			if (!instance.getDataBoo("gui")) return null;
			
			nWidget w = getCom(CT.ADD_WIDGET,instance,param,nWidget.class);
			int float_rez = (int)(1.2f * w.getLocalSX() / w.getFont()) - 3;
			if (hasParam("run_right", nRun.class)) {
				w.setRightTrigger();
				w.copyLookFrom(app.gui.book.getModel("CL_right_trigg"));
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
					if (var_txt != null) text = Applet.copy(var_txt);
					if (var_cls.equals(Float.class.getName())) {
						text += Applet.trimFlt(target.getVar(var_ref, Float.class), frez);
					} else if (var_cls.equals(Integer.class.getName())) {
						text += target.getVar(var_ref, Integer.class);
					} else if (var_cls.equals(Boolean.class.getName())) {
						text += target.getVar(var_ref, Boolean.class);
					} else if (var_cls.equals(String.class.getName())) {
						text += target.getVar(var_ref, String.class);
					} else if (var_cls.equals(Vector2.class.getName())) {
						text += Applet.tostr(target.getData(var_ref, Vector2.class));
					}
					w.setText(text);
				}};
				w.addEventLogic(sl_run); sl_run.run();
			}
			return w;
		}});
	
		newComTool("add_slide", CT.ADD_SLIDE, nWidget.class, new nRun() {public Object get() {
			
	//		app.log("com_add_slide");

			if (!instance.getDataBoo("gui")) return null;
			
			int width = getParamOrDef("width", Integer.class, (int)2);
			float height = getParamOrDef("height", Float.class, 1f);
			float min = getParamOrDef("min", Float.class, 0f);
			float max = getParamOrDef("max", Float.class, 1f);

			float scale_min = getParamOrDef("scale_min", Float.class, pNode.DEF_SCALE_MIN);
			float scale_max = getParamOrDef("scale_max", Float.class, pNode.DEF_SCALE_MAX);
			
			nWidgetGroup group = instance.object("group", nWidgetGroup.class);
			nWidget w = (nWidget)group.metodeGet("add_widget",(int)2);

			w.copyColorFrom(app.gui.book.getModel("ref"));
			
			w.setParent(group.get("back"));
			
			w.setSlider()
			.setSliderRange(min, max);
			w.setSX(w.getLocalSX()*width/2f);
			w.setSY(w.getLocalSY()*height);
			w.setScaleLimitNoDraw(scale_min, scale_max);
//			w.set_color_background(app.color((int)(255*w.color_background.r), 
//					(int)(255*w.color_background.g), 
//					(int)(255*w.color_background.b),
//					(int)255));
			
			if (hasParam("granulo", Float.class)) 
				w.setSliderGranulo(getParam("granulo", Float.class));

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
						target.setVar(var_ref, Applet.toint(w.getSliderValInMinMax()));
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
	
	

	
	

	public static nMap<pStandard> tile_models = new nMap<pStandard>();
	public static nMap<String> tile_models_short = new nMap<String>();
	public static ArrayList<String> not_poppable_models = new ArrayList<String>();
	public static nMap<ArrayList<PlugDef>> tile_model_plugs = new nMap<ArrayList<PlugDef>>();

	public static ArrayList<PlugDef> getTileModelPlugs(String r) { 
		return tile_model_plugs.get(r); }

	public static ArrayList<PlugDef> getTileModelPlugs(pStandard r) { 
		return tile_model_plugs.get(r.ref); }
	
	static class PlugDef {
		String ref;
		String[] keys, filters;
		public PlugDef(String r, String[] k, String[] f) { ref = r; keys = k; filters = f; }
	}

	public static pStandard newUnpoppableTileModel(String r) {
		not_poppable_models.add(r);
		return newTileModel(r); }
	public static pStandard newTileModel(String r) {
		pStandard p = pStandard.newStandard("tile_model_"+r, "tile");
		tile_models.put(r,p);
		ArrayList<PlugDef> pd = new ArrayList<PlugDef>();
		tile_model_plugs.put("tile_model_"+r,pd);
		p.copy(pStandard.get("tile"));
		return p;
	}
	public static pStandard getTileModel(String r) { return tile_models.get(r); }
	
	
	
	public static void runCom(CT cd, pInstance cont, pPar par, Object ... args) {
		pCommande c = com_tools.get(tool_refs.get(cd));
		c.run(cont,par,args); }

	public static <T> T getCom(CT cd, pInstance cont, pPar par, Class<T> ct, Object ... args) {
		pCommande c = com_tools.get(tool_refs.get(cd));
		return c.get(cont,par,ct,args); }

	public static void newRunTool(String r, CT cd, nRun rn) {
		run_tools.put(r,rn); tool_codes.put(r,cd); tool_refs.put(cd,r); 
	}

	public static pCommande newComTool(String r, CT cd, nRun rn) {
		pCommande c = pCommande.newCommande("com_tile_tool_"+r,rn);
		com_tools.put(r,c); tool_codes.put(r,cd); tool_refs.put(cd,r); 
		return c; }

	public static pCommande newComTool(String r, CT cd, Class<?> ct, nRun rn) {
		pCommande c = pCommande.newCommande("com_tile_tool_"+r,ct,rn);
		com_tools.put(r,c); tool_codes.put(r,cd); tool_refs.put(cd,r); 
		return c; }

	public static pProcess newProcTool(String r, CT cd) {
		pProcess c = pProcess.newProcess("proc_tile_tool_"+r);
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
		OBTAIN_VAR,
		ADD_PLUG, ADD_RUN_PLUGS, ADD_OBTAIN_PLUG, ADD_OFFER_PLUG, 
		ADD_WIDGET, 
		ADD_TRIGG, ADD_SWITCH, ADD_WATCH, ADD_FIELD, ADD_SLIDE
	};
	
	
	
	
	
	
	
	
	
	public static void build_book(Applet app) {
		nModelBook book = app.gui.book;
		float RS = book.RS;
		

		book.newModel("PL_base")
		.setBoundParent(true)
		.setStacked(true)
		.setOutline(true)
		.setOutlineWeight(RS/10f)
		.set_color_pressed(app.color(20,20,255,255))
		.set_color_hovered(app.color(0,0,210,255))
		.set_color_standby(app.color(0,0,120,255))
		.set_color_outline(app.color(120,180,255,255))
		.set_color_background(app.color(40))
		.setShape(nModel.Shape.DIAMOND)
		.setTrigger()
		.setSize(RS,RS)
		;
		book.newModel("PL_hide")
		.setBoundParent(true)
		.setStacked(true)
		.setOutline(false)
		.setOutlineWeight(0.1f)
		.set_color_pressed(app.color(20,20,255,255))
		.set_color_hovered(app.color(0,0,210,255))
		.set_color_standby(app.color(0,0,120,255))
		.set_color_outline(app.color(120,180,255,255))
		.set_color_background(app.color(0,0))
		.setShape(nModel.Shape.DIAMOND)
		.setPassif()
		;
		book.newModel("PL_open_H")
		.copyFrom(book.getModel("PL_base"))
		;
		book.newModel("PL_plugged_H_L")
		.copyFrom(book.getModel("PL_hide"))
		.setSX(RS/2f)
		;
		book.newModel("PL_plugged_H_R")
		.copyFrom(book.getModel("PL_base"))
		.setSX(RS/2f)
		;
		book.newModel("PL_open_V")
		.copyFrom(book.getModel("PL_base"))
		;
		book.newModel("PL_plugged_V")
		.copyFrom(book.getModel("PL_base"))
		;
		
		
		

		book.newModel("PT_ref")
//		.copyFrom(book.getModel("ref"))
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
//		.set_color_outline(app.color(20,20,120))
//		.setOutlineAfterChild(true)
		.set_color_background(app.color(0,0))
		.setDraw(false)
		;

		book.newModel("PT_selline")
		.setPassif()
		.setRectOrigin(nAlign.LEFT,nAlign.BOTTOM) // TOP   BOTTOM
		.setOutline(false)
		.setOutlineWeight(RS/15f)
		.setOutlineConstant(true)
		.set_color_outline(app.color(200,200,0))
		.set_color_background(app.color(0,0))
		;

		nModel PT_back = book.newModel("PT_back")
		.copyColorFrom(book.getModel("ref"))
		.setBoundParent(true)
		.setBoundChild(true)
		.setStacked(true)
		.setStackAxis(nAlign.HORIZONTAL) // HORIZONTAL   VERTICAL
		.setStackDirection(nAlign.RIGHT) // RIGHT   LEFT   UP   DOWN
		.setRectOrigin(nAlign.LEFT,nAlign.BOTTOM) // TOP   BOTTOM
//		.setBoundOutspace(2)
//		.setStackSpacing(2)
////		.setOutline(true)
//		.setOutlineWeight(RS/30f)
//		.setOutlineConstant(true)
////		.set_color_background(app.color(50))
		
		.setBoundOutspace(RS/4f)
		.setStackSpacing(RS/15f)
//		.setOutline(true)
		.setOutlineWeight(RS/4f)
//		.setOutlineConstant(true)
		.setTextAlignment(nAlign.LEFT, nAlign.CENTER)
		.setFont(RS*5f/4f)
		.setMask(true)
		;
		if (app.getPref("RELEASE", Boolean.class)) 
			PT_back.setOutline(true)
			.set_color_outline(app.color(0));

		book.newModel("PT_plugstack")
		.setBoundChild(true)
//		.setBoundParent(true)
//		.setRectOrigin(nAlign.LEFT,nAlign.BOTTOM) // TOP   BOTTOM
		.setBoundOutspace(0)
		.setStackSpacing(0)
		.set_color_background(app.color(0,0))
		.setPassif()
		;
		book.newModelGroup("patch_tile", new nModelGroup(app) { 
			public nWidgetGroup build(nGUI gui) {
				nWidgetGroup g = gui.addWidgetGroup();

				nWidget ref = g.addWidget("ref", "PT_ref");
				
				nWidget back = g.addWidget("back", "PT_back");
				back.setParent(ref);

				nWidget selline = g.addWidget("selline", "PT_selline") 
						.setParent(ref);

				g.addMetode("get_side_stack", new nRun() { public Object get(Object o) { 
					String side = (String)o;
					if (side.equals("left")) {
						if (g.get("plugstack_left") == null) {
							nWidget plugstack_left = g.addWidget("plugstack_left", "PT_plugstack");
							plugstack_left.setParent(ref)
							.setGlueCible(back)
							.setGlueSide(nAlign.LEFT)
							.setStackAxis(nAlign.VERTICAL) // HORIZONTAL   VERTICAL
							.setStackDirection(nAlign.DOWN) // RIGHT   LEFT   UP   DOWN
							;
						}
						return g.get("plugstack_left");
					} else if (side.equals("right")) {
						if (g.get("plugstack_right") == null) {
							nWidget plugstack_right = g.addWidget("plugstack_right", "PT_plugstack");
							plugstack_right.setParent(ref)
							.setGlueCible(back)
							.setGlueSide(nAlign.RIGHT)
							.setStackAxis(nAlign.VERTICAL) // HORIZONTAL   VERTICAL
							.setStackDirection(nAlign.DOWN) // RIGHT   LEFT   UP   DOWN
							;
						}
						return g.get("plugstack_right");
					} else if (side.equals("top")) {
						if (g.get("plugstack_top") == null) {
							nWidget plugstack_top = g.addWidget("plugstack_top", "PT_plugstack");
							plugstack_top.setParent(ref)
							.setGlueCible(back)
							.setGlueSide(nAlign.TOP)
							.setStackAxis(nAlign.HORIZONTAL) // HORIZONTAL   VERTICAL
							.setStackDirection(nAlign.RIGHT) // RIGHT   LEFT   UP   DOWN
							;
						}
						return g.get("plugstack_top");
					} else if (side.equals("bottom")) {
						if (g.get("plugstack_bottom") == null) {
							nWidget plugstack_bottom = g.addWidget("plugstack_bottom", "PT_plugstack");
							plugstack_bottom.setParent(ref)
							.setGlueCible(back)
							.setGlueSide(nAlign.BOTTOM)
							.setStackAxis(nAlign.HORIZONTAL) // HORIZONTAL   VERTICAL
							.setStackDirection(nAlign.RIGHT) // RIGHT   LEFT   UP   DOWN
							;
						}
						return g.get("plugstack_bottom");
					}
					return null; 
				} });

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
					pInstance tile = g.object("tile", pInstance.class);
					p.add(tile.getDataVec("pos"));
					tile.setData("pos", p);
					ref.setPos(tile.getDataVec("pos"));
					ref.force_calc();
				}});

				g.addMetode("go_to", new nRun() { public void run(Object o) { 
					Vector2 p = (Vector2)o;
					pInstance tile = g.object("tile", pInstance.class);
					tile.setData("pos", p);
					ref.setPos(tile.getDataVec("pos"));
					ref.force_calc();
				}});
				
				g.addMetode("link_to_instance", new nRun() {
					public void run(Object o) {
						pInstance tile = ((pInstance)o);
						g.addObject("tile", tile);
						pPatch patch = tile.patch;
						
						ref.setParent(tile.sheet.sheet_ref);

						Vector2 new_pos = new Vector2();
						if (tile.is_new) { 
							sVec viewspace_cam_pos = patch.view
									.object("val_cam_pos", sVec.class);
							new_pos.set(-viewspace_cam_pos.x(), 
									-viewspace_cam_pos.y());
							if(new_pos.x > 0) new_pos.x -= new_pos.x%pNode.BRIC_GRID_SIZE;
							else new_pos.x += Math.abs(new_pos.x)%pNode.BRIC_GRID_SIZE;
							if(new_pos.y > 0) new_pos.y -= new_pos.y%pNode.BRIC_GRID_SIZE;
							else new_pos.y += Math.abs(new_pos.y)%pNode.BRIC_GRID_SIZE;
							tile.setData("pos", new_pos);
							ref.setPos(new_pos);
						}

						nRun pop_close_run = new nRun() { public void run() {
							if (patch.pop_close_user.equals(tile.sheet.bloc.ref+
									"_"+tile.pool_ref))
								patch.pop_close_user = "";
							g.setObject("mouseOver", false);
							patch.patch_pop_close.removeEventTrigger(
									g.object("pop_close_run", nRun.class)); 
							patch.patch_pop_close.clearParent().hide();
							tile.run("clear_trigg"); }};
						g.addObject("pop_close_run", pop_close_run);
						
						nRun g_close_run = new nRun() { public void run() {
							if (patch.pop_close_user.equals(tile.sheet.bloc.ref+
									"_"+tile.pool_ref))
								patch.pop_close_user = "";
							patch.patch_pop_close.removeEventTrigger(
									g.object("pop_close_run", nRun.class)); 
						}};
						g.addEventClear(g_close_run);
						
						g.addObject("mouseOver", false);
						ref.addEventLogic(new nRun() { public void run() {
							ref.setPos(tile.getDataVec("pos"));
							selline.setPos(0,0);
							selline.setSize(ref.boundedSize.x, 
									ref.boundedSize.y);
//							selline.setPos(-RS/4f, -RS/4f);
//							selline.setSize(ref.boundedSize.x + RS/2f, 
//									ref.boundedSize.y + RS/2f);
							boolean over = g.object("mouseOver", Boolean.class);
							boolean new_over = ref.mouseOverChildZone || 
									(over && patch.patch_pop_close.mouseOverZone && 
									patch.pop_close_user(tile.sheet.bloc.ref+
											"_"+tile.pool_ref));
							if (new_over && !over) {
								patch.pop_close_user = Applet.copy(tile.sheet.bloc.ref+
										"_"+tile.pool_ref);
								patch.patch_pop_close.setParent(back)
								.addEventTrigger(pop_close_run);
								patch.patch_pop_close.show();
							} else if (!new_over && over) {
								patch.patch_pop_close.removeEventTrigger(pop_close_run);
								patch.patch_pop_close.hide();
							}
							if (ref.mouseOverChildZone || 
									(patch.patch_pop_close.mouseOverZone && 
									patch.pop_close_user(tile.sheet.bloc.ref+
											"_"+tile.pool_ref))) {
								patch.patch_pop_close.setPos(back.boundedSize.x, 
										back.boundedSize.y - RS/30f); 
								patch.patch_pop_close.show();
								g.setObject("mouseOver", true);
							}
							else g.setObject("mouseOver", false);
						}});

						if (tile.sheet.val_collapse.get()) ref.hide();

					}
				});
				
				return g;
			} 
		} );
		
	}
	
}
