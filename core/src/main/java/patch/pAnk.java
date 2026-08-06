package patch;

import java.util.ArrayList;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import data.*;
import gui.*;
import patch.pNode.CT;
import util.*;
import aa_nodulo.*;
import app.*;

public class pAnk {
	
	
	public static void build() {
		
		build_book();
		
		build_nodes();
		
	}
	
	public static void build_nodes() {
		
		pNode.newNodeModel("ank")
		.addInitRun(new nRun() {public void run() {
			
			instance.obtainVar("ank_pos", new Vector2());
			instance.obtainVar("view_ank", true); 
			instance.obtainVar("grab", true); 
			instance.obtainVar("title", ""); 
			
			nWidgetGroup group = PlaneApplet.app.gui.addWidgetGroup("space_ank");
			instance.addObject("space_ank", group);
			
			group.get("grab").setInfo(instance.getVar("title", String.class));
			
			if (instance.getVar("grab", Boolean.class)) group.get("grab").setGrabbable();
			else group.get("grab").setHoverable();
			if (instance.getVar("view_ank", Boolean.class)) group.get("ref").show();
			else group.get("ref").hide();
			
			group.get("grab").setCustomDrawer(new nDrawable() { public void drawing() {
				ArrayList<pInstance> arr = instance.get("get_all_chain", ArrayList.class);
				for (pInstance t : arr) if (t.hasObject("run_draw")) {
					t.object("run_draw", nRun.class).do_run(t); }
//				app.noStroke(); app.fill(255); app.circle(0,0,5);
			}});
			
			PlaneApplet.app.time.addPrevTickBric(instance);
			PlaneApplet.app.time.addTickBric(instance);
		}})
		.addLoadRun(new nRun() {public void run() {
			PlaneApplet.app.addDelayEvent(1, new nRun(instance) { public void run() {
				nWidgetGroup group = ((pInstance)builder).object("space_ank", nWidgetGroup.class);
				group.metode("link_to_node", ((pInstance)builder));
			}});
		}})
		.addClearRun(new nRun() {public void run() {
			if (instance.hasObject("space_ank"))
				instance.object("space_ank", nWidgetGroup.class).clear();
			PlaneApplet.app.time.removePrevTickBric(instance);
			PlaneApplet.app.time.removeTickBric(instance);
		}})
		.newRun("do_prev_tick", new nRun() {public void run() { 
			if (instance.hasObject("space_ank")) {
				nWidgetGroup group = instance.object("space_ank", nWidgetGroup.class);
				group.get("ref").setPos(instance.getVar("ank_pos", Vector2.class)); 
				group.get("grab").setInfo(instance.getVar("title", String.class));
				float s = ANK_GRAB_BASE_SIZE / group.get("ref").warptransform.getScale();
				group.get("grab").setSize(s,s);
				if (instance.getVar("view_ank", Boolean.class)) group.get("ref").show();
				else group.get("ref").hide();
				if (instance.getVar("grab", Boolean.class)) 
					group.get("grab").setGrabbable();
				else group.get("grab").setHoverable();
			}
		}})
		.newRun("do_tick", new nRun() {public void run() { 
			if (instance.hasObject("space_ank")) {
				nWidgetGroup group = instance.object("space_ank", nWidgetGroup.class);
				group.get("ref").setPos(instance.getVar("ank_pos", Vector2.class)); 
				group.get("grab").setInfo(instance.getVar("title", String.class));
				if (instance.getVar("view_ank", Boolean.class)) group.get("ref").show();
				else group.get("ref").hide();
				if (instance.getVar("grab", Boolean.class)) 
					group.get("grab").setGrabbable();
				else group.get("grab").setHoverable();
			}
		}})
		.newRun("recalc", new nRun() {public void run() { 
			if (instance.hasObject("space_ank")) {
				nWidgetGroup group = instance.object("space_ank", nWidgetGroup.class);
				group.get("ref").setPos(instance.getVar("ank_pos", Vector2.class)); 
				group.get("grab").setInfo(instance.getVar("title", String.class));
				if (instance.getVar("view_ank", Boolean.class)) group.get("ref").show();
				else group.get("ref").hide();
				if (instance.getVar("grab", Boolean.class)) 
					group.get("grab").setGrabbable();
				else group.get("grab").setHoverable();
			}
		}})
		.process()
		.openSec().param("width", (int)8)
		.run(pNode.getRun(pNode.CT.RUNP_VAR_STR_LAB_FIELD), "title")
		.closeSec()
		.commande(pNode.getCom(pNode.CT.COM_ADD_ROW))
		.openSec()
		.param("text", "pos", "width", (int)8)
		.run(pNode.getRun(pNode.CT.RUNP_VAR_VEC_LAB_FIELD), "ank_pos")
		.closeSec()
		.commande(pNode.getCom(pNode.CT.COM_ADD_ROW))
		.openSec().param("text", "view_ank", "width", (int)8)
		.run(pNode.getRun(pNode.CT.RUNP_VAR_BOO_SWITCH), "view_ank", "view_ank", (int)8)
		.closeSec()
		.commande(pNode.getCom(pNode.CT.COM_ADD_ROW))
		.openSec().param("text", "grab", "width", (int)8)
		.run(pNode.getRun(pNode.CT.RUNP_VAR_BOO_SWITCH), "grab", "grab", (int)8)
		.closeSec()
		.getStand()
		.openSec()
		.param("offer", new nRun() { public Object get() {
			return instance.object("node", pInstance.class);
		}})
		.param("keys", new String[] {"ank"}, "filters", new String[] {"ank"})
//		.param("text", "this", "width", (int)8) 
		.run(pNode.getRun(pNode.CT.RUNS_ADD_CO_OUT), "co_this")
		.closeSec()
		.openSec()
		.param("offer", new nRun() {public Object get() {
			pInstance node = instance.object("node", pInstance.class);
			if (node == null) return new Vector2();
			return node.getVar("ank_pos", Vector2.class);
		}})
		.param("keys", new String[] {"var","vec"}, "filters", new String[] {"var","vec"})
		.run(pNode.getRun(pNode.CT.RUNS_ADD_CO_OUT), "co_pos", "co_pos", (int)4)
		.closeSec()
		.openSec()
		.param("offer", new nRun() {public Object get() {
			pInstance node = instance.object("node", pInstance.class);
			if (node == null) return new Vector2();
			Vector2 m = PlaneApplet.app.view.mouse_in_view();
			m.sub(node.getVar("ank_pos", Vector2.class));
			return m;
		}})
		.param("keys", new String[] {"var","vec"}, "filters", new String[] {"var","vec"})
		.run(pNode.getRun(pNode.CT.RUNS_ADD_CO_OUT), "co_mouse", "mouse_in_ref", (int)4)
		.closeSec()
		.openSec()
		.param("hide", true, "keys", new String[] {"ank_tool"}, 
				"filters", new String[] {"ank_tool"}) 
		.run(pNode.getRun(pNode.CT.RUNS_ADD_CHAIN_START_PLUG), "ank_tool", "bottom")
		.closeSec()
		;
		
		
		
		pNode.newChainnedNodeModel("ank_bod_pointer")
		.process()
		.commande(new nRun() {public void run() {
			nRun run_draw = new nRun(instance) {public void run() {
				 pInstance inst = (pInstance)builder;
				 if (!inst.getVar("show", Boolean.class)) return;
				pInstance co_loc = inst.get("get_co", pInstance.class, "loc_pos");
				Vector2 loc_pos = co_loc.get("provide", Vector2.class);
				if (loc_pos == null) return;
				PlaneApplet.app.stroke(255,255,0,255,3f);
				PlaneApplet.app.line(0,0,loc_pos.x,loc_pos.y);
			}};
			instance.addObject("run_draw", run_draw);
		}})
		.openSec() 
		.run(pNode.getRun(CT.RUNP_VAR_BOO_SWITCH), "show", "show", (int)6)
		.closeSec()
		.getStand()
		.openSec()
		.param("keys", new String[] {"body"}, "filters", new String[] {"body"})
//		.param("text", "this", "width", (int)8) 
		.run(pNode.getRun(pNode.CT.RUNS_ADD_CO_IN), "body")
		.closeSec()
		.openSec()
		.param("offer", new nRun() {public Object get() {
			pInstance node = instance.object("node", pInstance.class);
			if (node == null) return new Vector2();
			pInstance head = node.get("get_chain_head", pInstance.class);
			if (head == null) return new Vector2();
			Vector2 ank_pos = head.getVar("ank_pos", Vector2.class);
			if (ank_pos == null) return new Vector2();
			pInstance co_bod = node.get("get_co", pInstance.class, "body");
			Object op_bod = co_bod.get("obtain_all", Object.class);
			if (op_bod != null) { 
				ArrayList<Object> prov_bod = (ArrayList)op_bod;
				for (Object o : prov_bod) if (o instanceof pBody) {
					pBody bod = (pBody)o;
					if (bod != null && bod.hasParam("ref")) {
						Vector2 pos = new Vector2(bod.getVec("ref", "pos"));
						Vector2 loc_pos = new Vector2(pos).sub(ank_pos);
						return loc_pos;
					}
				}
			}
			return new Vector2();
		}})
		.param("keys", new String[] {"var","vec"}, "filters", new String[] {"var","vec"})
		.run(pNode.getRun(pNode.CT.RUNS_ADD_CO_OUT), "loc_pos", "loc_pos", (int)4)
		.closeSec()
		.openSec().param("hide", true, "keys", new String[] {"ank_tool"}, 
				"filters", new String[] {"ank_tool"}) 
		.run(pNode.getRun(CT.RUNS_ADD_CHAIN_PLUGS), "ank_tool", "bottom").closeSec()
		;
		
		pNode.newChainnedNodeModel("ank_zone")
		.process()
		.commande(new nRun() {public void run() {
			nRun run_draw = new nRun(instance) {public void run() {
				 pInstance inst = (pInstance)builder;
				 if (!inst.getVar("show", Boolean.class)) return;
				 float rad = inst.getVar("radius", Float.class);
				 PlaneApplet.app.stroke(255,255,0,255,3f); PlaneApplet.app.noFill();
				 PlaneApplet.app.circle(0,0,rad);
			}};
			instance.addObject("run_draw", run_draw);
		}})
		.openSec()
			.param("text", "radius: ", "width", (int)5, "min", 1f, "max", 4000f, "granulo", 1f)
			.run(pNode.getRun(CT.RUNP_VAR_FLT_LAB_FIELD_SLIDE), "radius")
		.closeSec()
		.commande(pNode.getCom(CT.COM_ADD_ROW))
		.openSec()
			.param("def", true)
			.run(pNode.getRun(CT.RUNP_VAR_BOO_SWITCH), "show", "show", (int)6)
		.closeSec()
		.getStand()
		.closeSec()
		.openSec().param("hide", true, "keys", new String[] {"ank_tool"}, 
				"filters", new String[] {"ank_tool"}) 
		.run(pNode.getRun(CT.RUNS_ADD_CHAIN_PLUGS), "ank_tool", "bottom").closeSec()
		;
		
	}

	
	
	public static float ANK_GRAB_BASE_SIZE = 1f;
	
	public static void build_book() {
		
		nModelBook book = nGUI.book;
		float RS = book.RS;

		ANK_GRAB_BASE_SIZE = RS * 5f / 8f;

		book.newModel("AN_ref")
		.set_color_background(Utl.color(0,0))
		.setDraw(false)
		.setActAsRoot(true)
		.setRectOrigin(nAlign.LEFT,nAlign.BOTTOM) // TOP   BOTTOM
		;
		
		float s = ANK_GRAB_BASE_SIZE;
		book.newModel("AN_grab")
		.setRect(0,0,s,s)
		.setGrabbable()
		.setGrabbRoot(true)
		.setHoverableZone(true)
		.setRectOrigin(nAlign.CENTER,nAlign.CENTER) // TOP   BOTTOM
		.set_color_background(Utl.color(0,0))
		.set_color_pressed(Utl.color(210,60))
		.set_color_hovered(Utl.color(210,120))
		.set_color_standby(Utl.color(0,0))
		.set_color_outline(Utl.color(200,200,0,255))
		.setOutline(true)
		.setOutlineConstant(true)
		.setOutlineWeight(ANK_GRAB_BASE_SIZE/5f)
		.setShape(nModel.Shape.DIAMOND)
		;
		
		
		book.newModelGroup("space_ank", new nModelGroup() { 
			public nWidgetGroup build(nGUI gui) {
				nWidgetGroup g = gui.addWidgetGroup();

				nWidget ref = g.addWidget("ref", "AN_ref");
				nWidget grab = g.addWidget("grab", "AN_grab")
						.setParent(ref);
				
				g.addMetode("link_to_node", new nRun() {
					public void run(Object o) {
						pInstance node = ((pInstance)o);
						g.addObject("node", node);
						
						g.addEventClear(new nRun() { public void run() {
							node.clear(); }});
						
						ref.setParent(PlaneApplet.app.view.view_ref);

						ref.setPos(node.getVar("ank_pos", Vector2.class));

						grab.addEventDrag(new nRun() { public void run() {
							node.setVar("ank_pos", ref.getParentPos()); }});
						
					}
				});
				
				return g;
			} 
		} );
		
		
	}

	
	
	
	
	
}
