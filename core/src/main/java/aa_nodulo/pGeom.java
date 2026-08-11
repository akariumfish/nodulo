package aa_nodulo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import app.App;
import data.sBloc_Builder;
import data.sBoo;
import data.sData;
import data.sFlt;
import data.sValueBloc;
import gui.nDrawable;
import gui.nGUI;
import gui.nInterface;
import gui.nWidget;
import gui.nWidgetGroup;
import util.Utl;
import util.nMap;
import util.nPool;
import util.nRun;
import patch.pInstance;
import patch.pNode;
import patch.pNodeSpace;
import patch.pPar;
import patch.pProcess;
import patch.pStandard;
import patch.pNode.CT;

public class pGeom extends pSystem {

	public static sBloc_Builder builder = null;
	
	public static void build(sData data) {

		if (builder == null) build_prop();
		
		builder = builder(data, "geom", pGeom.class, true, new nRun() { public void run(Object o) {
			sValueBloc b = (sValueBloc)o; newObject(b); }});
		
	}

	public static void dispose(PlaneApplet app) { pool.dispose(); }
	public static final nPool<pGeom> pool = new nPool<pGeom>() {
		protected pGeom newObject() { return new pGeom(); } };
	public static pGeom newObject(sValueBloc b) {
		return pool.obtain().init(b); }
	
	public static void build_prop() {
		
		float RS = nGUI.book.RS;
		
		
		
		
		
		nRun geom_prop_run = new nRun() {public void run() {
			pStandard stand = arg(0,pStandard.class);
			if (stand == null) return;
			
			stand.newRun("set_def", new nRun() {public void run() {
				pParam par = instance.object("param", pParam.class);
				if (par == null) return; 
				par.collecEmpty("point");
				par.collecEmpty("faceA");
				par.collecEmpty("faceB");
				par.collecEmpty("faceC");
				Vector2 v = new Vector2(80,0); 
				Vector2 v2 = new Vector2(80,0); 
				v.rotateRad((float)(2f*Math.PI/3f)); 
				v2.rotateRad((float)(2f*Math.PI/3f)); 
				v2.rotateRad((float)(2f*Math.PI/3f)); 
				par.collecAdd("point", v); 
				par.collecAdd("point", new Vector2(80,0)); 
				par.collecAdd("point", v2);
				par.collecAdd("faceA", (int)0);
				par.collecAdd("faceB", (int)1);
				par.collecAdd("faceC", (int)2);
			}});
			stand.newRun("set_trig", new nRun() {public void run() {
				float r = arg(0, Float.class);
				pParam par = instance.object("param", pParam.class);
				if (par == null) return; 
				par.collecEmpty("point");
				par.collecEmpty("faceA");
				par.collecEmpty("faceB");
				par.collecEmpty("faceC");
				Vector2 v = new Vector2(r,0); 
				Vector2 v2 = new Vector2(r,0); 
				v.rotateRad((float)(2f*Math.PI/3f)); 
				v2.rotateRad((float)(2f*Math.PI/3f)); 
				v2.rotateRad((float)(2f*Math.PI/3f)); 
				par.collecAdd("point", v); 
				par.collecAdd("point", new Vector2(r,0)); 
				par.collecAdd("point", v2);
				par.collecAdd("faceA", (int)0);
				par.collecAdd("faceB", (int)1);
				par.collecAdd("faceC", (int)2);
			}});
			stand.newRun("add_trig", new nRun() {public void run() {
				float r = arg(0, Float.class);
				pParam par = instance.object("param", pParam.class);
				if (par == null) return; 
				Vector2 v = new Vector2(r,0); 
				Vector2 v2 = new Vector2(r,0); 
				v.rotateRad((float)(2f*Math.PI/3f)); 
				v2.rotateRad((float)(2f*Math.PI/3f)); 
				v2.rotateRad((float)(2f*Math.PI/3f)); 
				int col_size = par.getCollecSize("point");
				par.collecAdd("point", v); 
				par.collecAdd("point", new Vector2(r,0)); 
				par.collecAdd("point", v2);
				par.collecAdd("faceA", col_size+(int)0);
				par.collecAdd("faceB", col_size+(int)1);
				par.collecAdd("faceC", col_size+(int)2);
			}});
			stand.newRun("set_rect", new nRun() {public void run() {
				float r = arg(0, Float.class);
				pParam par = instance.object("param", pParam.class);
				if (par == null) return; 
				par.collecEmpty("point");
				par.collecEmpty("faceA");
				par.collecEmpty("faceB");
				par.collecEmpty("faceC");
				Vector2 v = new Vector2(r,0); 
				Vector2 v2 = new Vector2(r,0);
				Vector2 v3 = new Vector2(r,0);
				Vector2 v4 = new Vector2(r,0); 
				v.rotateRad((float)(3f*Math.PI/4f)); 
				v2.rotateRad((float)(1f*Math.PI/4f)); 
				v3.rotateRad((float)(-1f*Math.PI/4f));
				v4.rotateRad((float)(-3f*Math.PI/4f)); 
				par.collecAdd("point", v); 
				par.collecAdd("point", v2);
				par.collecAdd("point", v3);
				par.collecAdd("point", v4);
				par.collecAdd("faceA", (int)0);
				par.collecAdd("faceB", (int)1);
				par.collecAdd("faceC", (int)2);
				par.collecAdd("faceA", (int)2);
				par.collecAdd("faceB", (int)3);
				par.collecAdd("faceC", (int)0);
			}});
			
			
			stand.process()
			.commande(pNode.getCom(CT.COM_ADD_ROW))
			.openSec()
			.param("text", "size", "width", (int)8, 
					"def", 80f, "min", 1f, "max", 120f, "granulo", 1f)
			.run(pNode.getRun(pNode.CT.RUNP_VAR_FLT_LAB_FIELD_SLIDE), "size")
			.closeSec()
			.commande(pNode.getCom(CT.COM_ADD_ROW))
			.openSec()
				.param("run", new nRun() {public void run() {
					instance.run("set_def"); }})
				.run(pNode.getRun(CT.RUNP_ADD_TRIGG), "set_def", "set_def", (int)6)
			.closeSec()
			.openSec()
				.param("run", new nRun() {public void run() {
					instance.run("set_trig", instance.getVar("size", Float.class)); }})
				.run(pNode.getRun(CT.RUNP_ADD_TRIGG), "set_trig", "set_trig", (int)6)
			.closeSec()
			.openSec()
				.param("run", new nRun() {public void run() {
					instance.run("set_rect", instance.getVar("size", Float.class)); }})
				.run(pNode.getRun(CT.RUNP_ADD_TRIGG), "set_rect", "set_rect", (int)6)
			.closeSec()
			.commande(pNode.getCom(CT.COM_ADD_ROW))
			.openSec()
				.param("run", new nRun() {public void run() {
					
					instance.obtainVar("sel_point", (int)-1);
					
					nInterface interf = PlaneApplet.app.gui.get_popWindow();

					interf.add_col();
					interf.add_row();
					interf.add_row_label(9," Geom Editor ");

					interf.add_row();
					nWidget preview = interf.add_row_label(9,"");

					interf.set_param("entry_height","0.8");
					interf.add_row();
					nWidgetGroup pointlist = interf.add_treelist(9,5);
					interf.set_param("entry_height","1");

					interf.add_row();
					
					interf.add_col();
					interf.add_row();
					nRun pointlist_run = new nRun(instance) { public void run() {
						pInstance inst = (pInstance)builder;
						pParam geom = inst.object("param", pParam.class);
						if (geom == null) return; 
						ArrayList<Vector2> point = geom.getCollecData("point", Vector2.class);
						interf.change_current_list(pointlist);
						int i = 0;
						for (Vector2 v : point) {
							interf.add_list_entry("point "+i+" : "+v.x+" "+v.y);
							interf.go_up_tree();
							i++;
						}
						ArrayList<Integer> faceA = geom.getCollecData("faceA", Integer.class);
						ArrayList<Integer> faceB = geom.getCollecData("faceB", Integer.class);
						ArrayList<Integer> faceC = geom.getCollecData("faceC", Integer.class);
						if (faceA.size() != faceB.size() || faceA.size() != faceC.size() || 
								faceC.size() != faceB.size()) return;

						ArrayList<Integer> lineA = geom.getCollecData("lineA", Integer.class);
						ArrayList<Integer> lineB = geom.getCollecData("lineB", Integer.class);
						if (lineA.size() != lineB.size()) return;
						for (i = 0 ; i < faceA.size() ; i++) {
							int p1 = faceA.get(i), p2 = faceB.get(i), p3 = faceC.get(i);
							interf.add_list_entry("face "+i+" : "+p1+" "+p2+" "+p3);
							interf.go_up_tree();
						}
						for (i = 0 ; i < lineA.size() ; i++) {
							int p1 = lineA.get(i), p2 = lineB.get(i);
							interf.add_list_entry("line "+i+" : "+p1+" "+p2);
							interf.go_up_tree();
						}
					
					}};
					
					nRun move_run = new nRun(instance) { public void run() {
						pInstance inst = (pInstance)builder;
						float mx = arg(0,Float.class), my = arg(1,Float.class);
						int sel_point = inst.getVar("sel_point", Integer.class);
						pParam geom = inst.object("param", pParam.class);
						if (geom == null) return; 
						ArrayList<Vector2> point = geom.getCollecData("point", Vector2.class);
						if (sel_point >= 0 && sel_point < point.size()) {
							Vector2 v = new Vector2(point.get(sel_point));
							v.add(mx,my);
							geom.collecSet("point", sel_point, v); 
							pointlist_run.do_run(inst); }
					}};
					nRun add_run = new nRun(instance) { public void run() {
						pInstance inst = (pInstance)builder;
						inst.run("add_trig", 60f);
					}};
					interf.add_row_trigg(3,"U", new nRun(instance) { public void run() {
						move_run.do_run((pInstance)builder, 0f, 10f); }});
					interf.add_row();
					interf.add_row_trigg(3,"L", new nRun(instance) { public void run() {
						move_run.do_run((pInstance)builder, -10f, 0f); }});
					interf.add_row_trigg(3,"R", new nRun(instance) { public void run() {
						move_run.do_run((pInstance)builder, 10f, 0f); }});
					interf.add_row();
					interf.add_row_trigg(3,"D", new nRun(instance) { public void run() {
						move_run.do_run((pInstance)builder, 0f, -10f); }});

					interf.add_row();
					interf.add_row_trigg(3,"CCW", new nRun(instance) { public void run() {
						pInstance inst = (pInstance)builder;
						int sel_point = inst.getVar("sel_point", Integer.class);
						pParam geom = inst.object("param", pParam.class);
						if (geom == null) return; 
						ArrayList<Vector2> point = geom.getCollecData("point", Vector2.class);
						if (sel_point >= 0 && sel_point < point.size()) {
							Vector2 v = new Vector2(point.get(sel_point))
									.rotateRad(((float)Math.PI)/12f);
							geom.collecSet("point", sel_point, v);
							pointlist_run.do_run(inst); 
						} }});
					interf.add_row_trigg(3,"CW", new nRun(instance) { public void run() {
						pInstance inst = (pInstance)builder;
						int sel_point = inst.getVar("sel_point", Integer.class);
						pParam geom = inst.object("param", pParam.class);
						if (geom == null) return; 
						ArrayList<Vector2> point = geom.getCollecData("point", Vector2.class);
						if (sel_point >= 0 && sel_point < point.size()) {
							Vector2 v = new Vector2(point.get(sel_point))
									.rotateRad(-((float)Math.PI)/12f);
							geom.collecSet("point", sel_point, v);
							pointlist_run.do_run(inst); 
						} }});

					interf.add_row();
					interf.add_row_trigg(3,"mag -", new nRun(instance) { public void run() {
						pInstance inst = (pInstance)builder;
						int sel_point = inst.getVar("sel_point", Integer.class);
						pParam geom = inst.object("param", pParam.class);
						if (geom == null) return; 
						ArrayList<Vector2> point = geom.getCollecData("point", Vector2.class);
						if (sel_point >= 0 && sel_point < point.size()) {
							Vector2 v = new Vector2(point.get(sel_point));
							float l = v.len(); float l2 = l - 10f;
							if (l > 0 && l2 > 0) v.scl(l2 / l);
							geom.collecSet("point", sel_point, v);
							pointlist_run.do_run(inst); 
						} }});
					interf.add_row_trigg(3,"mag +", new nRun(instance) { public void run() {
						pInstance inst = (pInstance)builder;
						int sel_point = inst.getVar("sel_point", Integer.class);
						pParam geom = inst.object("param", pParam.class);
						if (geom == null) return; 
						ArrayList<Vector2> point = geom.getCollecData("point", Vector2.class);
						if (sel_point >= 0 && sel_point < point.size()) {
							Vector2 v = new Vector2(point.get(sel_point));
							float l = v.len(); float l2 = l + 10f;
							if (l > 0) v.scl(l2 / l); else v.set(10,0);
							geom.collecSet("point", sel_point, v);
							pointlist_run.do_run(inst); 
						} }});
					

					interf.add_row();
					interf.add_row_label(6, "");
					interf.add_row();
					interf.add_row_label(1, "");
					interf.add_row_trigg(4,"ADD", new nRun(instance) { public void run() {
						add_run.do_run((pInstance)builder); 
					}});
					interf.add_row_label(1, "");

					
					pointlist_run.do_run(instance);
					
					preview.setSY(RS*7f);
					nRun pr = new nRun() {public void run() { 
						PlaneApplet app = PlaneApplet.app;
						app.fill(40); app.rect(0,0,210,210);
						app.push(); app.translate(105,105);
						app.stroke(255,0,0,255,2f); app.line(0,-80,0,80);
						app.stroke(0,255,0,255,2f); app.line(-80,0,80,0);
						app.pop();
						
						pParam geom = instance.object("param", pParam.class);
						if (geom == null) return; 
						app.push(); app.translate(105,105);
						
						ArrayList<Vector2> point = geom.getCollecData("point", Vector2.class);

						ArrayList<Integer> faceA = geom.getCollecData("faceA", Integer.class);
						ArrayList<Integer> faceB = geom.getCollecData("faceB", Integer.class);
						ArrayList<Integer> faceC = geom.getCollecData("faceC", Integer.class);
						if (faceA.size() != faceB.size() || faceA.size() != faceC.size() || 
								faceC.size() != faceB.size()) return;

						ArrayList<Integer> lineA = geom.getCollecData("lineA", Integer.class);
						ArrayList<Integer> lineB = geom.getCollecData("lineB", Integer.class);
						if (lineA.size() != lineB.size()) return;
					
						app.stroke(255,255,255,255,3f);
						app.noFill();

						for (int i = 0 ; i < faceA.size() ; i++) {
							int p1 = faceA.get(i), p2 = faceB.get(i), p3 = faceC.get(i);
							if (p1 < 0 || p1 >= point.size() || 
									p2 < 0 || p2 >= point.size() || 
									p3 < 0 || p3 >= point.size()) continue;
							app.polygon(point.get(p1), 
									point.get(p2), 
									point.get(p3));
						}
						for (int i = 0 ; i < lineA.size() ; i++) {
							int p1 = lineA.get(i), p2 = lineB.get(i);
							if (p1 < 0 || p1 >= point.size() || p2 < 0 || p2 >= point.size()) continue;
							app.line(point.get(p1), point.get(p2));
						}
						app.fill(220); app.noStroke();
						for (Vector2 v : point) {
								app.circle(v.x, v.y, 3f);
						}
						
						int sel_point = instance.getVar("sel_point", Integer.class);
						if (sel_point >= 0 && sel_point < point.size()) {
							Vector2 v = point.get(sel_point);
							app.fill(255,180,0,255);
							app.circle(v.x, v.y, 10f);
						}

						Vector2 mouse = new Vector2(app.input.mouse);
						mouse.sub(preview.getPos()).sub(105,105);
						for (Vector2 v : point) {
							Vector2 l = new Vector2(v).sub(mouse);
//							app.fill(255,180,0,255);
//							app.circle(n.x, n.y, 10f);
//							app.circle(mouse.x, mouse.y, 10f);
							if (l.len() <= 10f) {
								app.fill(255,255,0,255);
								app.circle(v.x, v.y, 10f);
							}
						}
						
						app.pop();
					}};
					preview.setCustomDrawer(new nDrawable(instance) {public void drawing() { 
						pr.do_run((pInstance)builder); }});
					preview.addEventLogic(new nRun(instance) {public void run() { 
						pInstance inst = (pInstance)builder;
						pParam geom = inst.object("param", pParam.class);
						if (geom == null) return; 
						ArrayList<Vector2> point = geom.getCollecData("point", Vector2.class);
						Vector2 mouse = new Vector2(App.ap.input.mouse);
						mouse.sub(preview.getPos()).sub(105,105);
						int i = 0;
						for (Vector2 v : point) {
							Vector2 l = new Vector2(v).sub(mouse);
							if (l.len() <= 10f && App.ap.input.mouseLeft.trigClick) {
								inst.setVar("sel_point", i); 
								break; }
							i++; 
						}
						if (i >= point.size() && 
								preview.globalrect.contains(App.ap.input.mouse) && 
								App.ap.input.mouseLeft.trigClick)
							inst.setVar("sel_point", (int)-1);
					}});
					
					App.ap.addEventNextFrame(new nRun() { public void run() {
						PlaneApplet.app.gui.pop_popwindow("Load"); 
					}});
				}})
				.run(pNode.getRun(CT.RUNP_ADD_TRIGG), "editor", "editor", (int)8)
			.closeSec()
			.commande(pNode.getCom(CT.COM_ADD_ROW))
			.openSec()
			.param("ref", "preview", "scale_min", 0.0f)
			.param("text", "", "width", (int)6, "height", 6f)
			.param("custom_drawer", new nRun() {public void run() { 
				PlaneApplet app = PlaneApplet.app;
				app.fill(40); app.rect(0,0,180,180);
				app.push(); app.translate(90,90);
				app.stroke(255,0,0,255,2f); app.line(0,-80,0,80);
				app.stroke(0,255,0,255,2f); app.line(-80,0,80,0);
				app.pop();

				pParam par = instance.object("param", pParam.class);
				if (par == null) return; 				
				app.push(); app.translate(90,90);
				pGeom.draw_geom_graph(app, null, par, null); 
				app.pop();
			}}) 
			.commande(pNode.getCom(CT.COM_ADD_WIDGET))
			.closeSec()
			;
		}};
		
		
		
		
		
		// PROPERTY DEF
		

		pProperty coordinate = pProperty.newGeneralProperty("coordinate");
		coordinate
		.addData("limit", true)
		.addData("limit_dist", 20000f)
		;
		
		coordinate.newLocalProperty("ref")
		.addData("pos", new Vector2())
		.addData("rot", 0f)
		;
		coordinate.newLocalProperty("scale")
		.addData("scale", 1f)
		;
		
		
		
		pProperty geom = pProperty.newGeneralProperty("geom")
		.setGroupFlag("draw")
		.addCollec("point", Vector2.class)
		.addCollec("lineA", Integer.class)
		.addCollec("lineB", Integer.class)
		.addCollec("faceA", Integer.class)
		.addCollec("faceB", Integer.class)
		.addCollec("faceC", Integer.class)
		.addNodeRun(geom_prop_run)
		;

		geom.newLocalProperty("info_shape")
		.setRuntime()
		.setLocalVal()
		.addData("aabb_pos", new Vector2())
		.addData("aabb_size", new Vector2())
		.addData("area", 1f)
		;
		
		
		
		
		
		Color fill = nGUI.book.getModel("CL_graph").color_background;
		Color line = nGUI.book.getModel("CL_graph").color_outline;
		float thick = nGUI.book.getModel("CL_graph").outlineWeight;
		
		pProperty graph = pProperty.newGeneralProperty("graph")
		.setGroupFlag("draw")
		.addData("line", true)
		.addData("fill", true)
		.addData("thick", thick, "min", 1f, "max", 12f, "granulo", 1f)
		.addData("line_r", (int)(line.r*255), "def", (int)(line.r*255), "min", 0f, "max", 255f, "granulo", 1f, "hide", true)
		.addData("line_g", (int)(line.g*255), "def", (int)(line.g*255), "min", 0f, "max", 255f, "granulo", 1f, "hide", true)
		.addData("line_b", (int)(line.b*255), "def", (int)(line.b*255), "min", 0f, "max", 255f, "granulo", 1f, "hide", true)
		.addData("line_a", (int)(line.a*255), "def", (int)(line.a*255), "min", 0f, "max", 255f, "granulo", 1f, "hide", true)
		.addData("fill_r", (int)(fill.r*255), "def", (int)(fill.r*255), "min", 0f, "max", 255f, "granulo", 1f, "hide", true)
		.addData("fill_g", (int)(fill.g*255), "def", (int)(fill.g*255), "min", 0f, "max", 255f, "granulo", 1f, "hide", true)
		.addData("fill_b", (int)(fill.b*255), "def", (int)(fill.b*255), "min", 0f, "max", 255f, "granulo", 1f, "hide", true)
		.addData("fill_a", (int)(fill.a*255), "def", (int)(fill.a*255), "min", 0f, "max", 255f, "granulo", 1f, "hide", true)
		;


		
		

		
		
		pProperty moveable = pProperty.newGeneralProperty("moveable");
		
		moveable.addBodyInitRun(new nRun() {public void run() {
			pBody bod = arg(0,pBody.class);
			pGeom geo = PlaneApplet.app.getSystem(pGeom.class);
			if (geo == null || bod == null) return;
			geo.init_body(bod);
		}});
		
		moveable.newLocalProperty("move")
		.setRuntime()
		.setNoSync()
		.addData("acc_pos", new Vector2())
		.addData("acc_rot", 0f)
		.addData("tp_pos", new Vector2())
		.addData("tp_rot", 0f)
		.addData("slow_pos", 0f)
		.addData("slow_rot", 0f)
		.addData("friction", 0.0001f)
		;

		moveable.newLocalProperty("var_move")
		.setRuntime()
		.setNoSync()
		.addData("prev_pos", new Vector2())
		.addData("prev_rot", 0f)
		.addData("pos_move", new Vector2())
		.addData("rot_move", 0f)
		.addData("pos_speed", 0f)
		.addData("rot_speed", 0f)
		;

		
		
		
		
		
		pFamily.newFamily("movable")
		.addProp("ref")
		.addProp("move")
		.addProp("var_move")
		;
		
		
		
		
		
		
		pFamily.newFamily("drawable")
		.addProp("ref")
		.addProp("geom")
		.addProp("graph")
		;
		pFamily.newFamily("aabb")
		.addProp("ref")
		.addProp("geom")
		.addProp("info_shape")
		;
		pFamily.newFamily("aabb_drawable")
		.addProp("info_shape")
		;
		
		
		
		

		
		pProperty interactif = pProperty.newGeneralProperty("interactif");

		interactif.addClearRun(new nRun() {public void run() {
			pBody bod = arg(0,pBody.class);
			pGeom geo = bod.space.app.getSystem(pGeom.class);
			if (geo == null || bod == null) return;
			
		}});

		interactif.newLocalProperty("mouse")
		.setLocalVal()
		.addData("mousepos", new Vector2())
		;

		interactif.newLocalProperty("highlightable")
		.setLocalVal()
		.addData("lighted", false)
		.addData("light_red", (int)255)
		.addData("light_green", (int)255)
		.addData("light_blue", (int)0)
		;

		interactif.newLocalProperty("clickable")
		.setLocalVal()
		.addData("hover", false)
		.addData("press", false)
		.addData("click", false)
		;

		pFamily.newFamily("aabb_clickable")
		.addProp("info_shape")
		.addProp("clickable")
		;

		pFamily.newFamily("mouse")
		.addProp("ref")
		.addProp("mouse")
		;

		
		
		pProperty ownable = pProperty.newGeneralProperty("ownable")
		.addData("acquire", false);

		ownable.addBodyInitRun(new nRun() {public void run() {
			pBody bod = arg(0,pBody.class);
			pGeom geo = PlaneApplet.app.getSystem(pGeom.class);
			if (geo == null || bod == null) return;
			if (bod.getBoo("ownable", "acquire")) {
				bod.setBoo("owner", "owned", true);
				bod.setStr("owner", "owner", PlaneApplet.app.config.player_ref);
			}
		}});
		
		ownable.newLocalProperty("owner") 
		.setFullSync()
		.addData("owned", false)
		.addData("owner", "")
		;

		
		
		
		


		pProperty logic = pProperty.newGeneralProperty("logic")
		.addData("run", true)
		.addData("frame", false)
		.addData("tick", true)
		.addData("delay", (int)0)
		.addData("func_ref", "")
		;

		pFamily.newFamily("logic")
		.addProp("logic")
		;

		
		
		
		
		

//		nRun run_ctrl_seek = new nRun() { public void run(Object o) { 
//			pBody bod = (pBody)o; if (bod == null) return;
//			if (bod.hasParam("ref") && bod.hasParam("ctrl_seek")) {
//				float dist = bod.getFlt("ctrl_seek","dist");
//				if (bod.getBoo("ctrl_seek","activate")) {
//					bod.setBoo("ctrl_seek", "got_target", false);
//					Vector2 pos = bod.getVec("ref","pos");
//					HashMap<pBody, Float> close_bod = new HashMap<pBody, Float>();
//					for (pBody b : bod.space.body_pool.all()) {
//						if (b == bod || !b.hasParam("ref")) continue;
//						Vector2 p = new Vector2(b.getVec("ref","pos")).sub(pos);
//						float d = p.len(); if (d <= dist) { close_bod.put(b,d); } }
//					pBody closest = null;
//					float min_dist = dist + 1f;
//					for (Map.Entry<pBody, Float> me : close_bod.entrySet()) {
//						if (me.getValue() < min_dist) {
//							closest = me.getKey(); min_dist = me.getValue(); } }
//					if (closest != null) {
//						bod.param("ctrl_seek").setBody("target", closest);
//						bod.setBoo("ctrl_seek", "got_target", true); }
//				}
//			}
//		}};
//
//		pGeom.newControlProp("seek",run_ctrl_seek)
//		.setFullSync()
//		.addData("activate", false)
//		.addData("got_target", false)
//		.addBody("target")
//		.addData("dist", 1000f, "min", 100f, "max", 4000f, "granulo", 100f)
//		;
//
//		nRun run_ctrl_time = new nRun() { public void run(Object o) { 
//			pBody bod = (pBody)o; if (bod == null) return;
//			pTime time = bod.space.app.time;
//			if (bod.hasParam("matter") && bod.hasParam("ctrl_time")) {
//				float strength = bod.getFlt("ctrl_time","strength");
//				if (bod.getBoo("ctrl_time","activate") && 
//						time.get_tickrate_fact() != strength) {
//					time.set_tickrate_fact(strength); } }
//		}};
//
//		pGeom.newControlProp("time",run_ctrl_time)
//		.setFullSync()
//		.addData("activate", false)
//		.addData("strength", 3f, "min", 0.25f, "max", 4f)
//		;

		
		nRun run_ctrl_move = new nRun() { public void run(Object o) { 
			pBody bod = (pBody)o; if (bod == null) return;
			pGeom geo = PlaneApplet.app.getSystem(pGeom.class);
			if (bod.hasParam("ref") && bod.hasParam("ctrl_move")) {
				if (bod.getBoo("ctrl_move","accelerate")) {
					Vector2 acc_pos = bod.getVec("ctrl_move","acc_pos"); 
					if (!bod.getBoo("ctrl_move","global_ref")) 
						acc_pos.rotateRad(bod.getFlt("ref","rot"));
					if (bod.getBoo("ctrl_move","target_pos")) acc_pos.set(0,0);
					geo.speed_body(bod,acc_pos.x,acc_pos.y,0);
				}
				if (bod.getBoo("ctrl_move","accelerate_rot")) {
					float acc_rot = bod.getFlt("ctrl_move","acc_rot"); 
					if (bod.getBoo("ctrl_move","target_rot")) acc_rot = 0f; 
					geo.speed_body(bod,0,0,acc_rot);
				}
				if (bod.getBoo("ctrl_move","decelerate")) {
					float dec_pos = bod.getFlt("ctrl_move","dec_pos");
					if (bod.getBoo("ctrl_move","target_pos")) dec_pos = 0f;
					geo.slow_body(bod,dec_pos,0);
				}
				if (bod.getBoo("ctrl_move","decelerate_rot")) {
					float dec_rot = bod.getFlt("ctrl_move","dec_rot");
					if (bod.getBoo("ctrl_move","target_rot")) dec_rot = 0f;
					geo.slow_body(bod,0,dec_rot);
				}
				if (bod.getBoo("ctrl_move","target_pos")) {
					Vector2 trg_pos = bod.getVec("ctrl_move","trg_pos");
					float max_speed = bod.getFlt("ctrl_move","max_speed");
					geo.move_to_target(bod, trg_pos, max_speed);
				}
				if (bod.getBoo("ctrl_move","target_rot")) {
					float trg_rot = bod.getFlt("ctrl_move","trg_rot");
					float max_rot = bod.getFlt("ctrl_move","max_rot");
					geo.rot_to_target(bod, trg_rot, max_rot);
				}
				if (bod.getBoo("ctrl_move","teleport")) {
					bod.setBoo("ctrl_move","teleport", false);
					Vector2 pos = bod.getVec("ref","pos");
					Vector2 trg_pos = bod.getVec("ctrl_move","trg_pos").sub(pos);
					float trg_rot = trg_pos.angleRad() - bod.getFlt("ref","rot");
					geo.tp_body(bod, trg_pos.x, trg_pos.y, trg_rot);
				}
			}
		}};
		
		pGeom.newControlProp("move",moveable,run_ctrl_move,"ref")
		.setFullSync()
		.addData("global_ref", true)
		.addData("accelerate", false)
		.addData("accelerate_rot", false)
		.addData("acc_pos", new Vector2())
		.addData("acc_rot", 0f, "min", -0.1f, "max", 0.1f)
		.addData("decelerate", false)
		.addData("decelerate_rot", false)
		.addData("dec_pos", 5f, "min", 0f, "max", 20f)
		.addData("dec_rot", 0.08f, "min", 0f, "max", 0.1f)
		.addData("max_rot", 0.1f, "min", 0f, "max", 0.15f)
		.addData("target_pos", false)
		.addData("target_rot", false)
		.addData("teleport", false)
		.addData("trg_pos", new Vector2())
		.addData("trg_rot", 0f, "min", -(float)Math.PI, "max", (float)Math.PI)
		;




		nRun run_ctrl_pop = new nRun() { public void run(Object o) { 
			pBody bod = (pBody)o; if (bod == null) return;
			pGeom geo = PlaneApplet.app.getSystem(pGeom.class);
			if (geo != null && bod.hasParam("ref") && bod.hasParam("ctrl_pop")) {

				Vector2 pop_pos = bod.getVec("ctrl_pop","pop_pos");
				float pop_rot = bod.getFlt("ctrl_pop","pop_rot");
				Vector2 acc_pos = bod.getVec("ctrl_pop","acc_pos");
				float acc_rot = bod.getFlt("ctrl_pop","acc_rot");
				
				if (bod.getBoo("ctrl_pop","pop")) {
					
					String bluep_par = bod.getStr("ctrl_pop","blueprint_par");
//					pParam bluep = bod.space.param_pools.get("blueprint").get(bluep_par);
					
					pParam bluep = null;
					for (pParam p : bod.space.param_pools.get("blueprint").all()) 
						if (p.getStr("name").equals(bluep_par)) { bluep = p; break; }
					
					if (bluep != null) { 
						pBody pop = pNodeSpace.new_body(bluep);
						if (pop == null) return;
						
						Vector2 bp = bod.getVec("ref", "pos");
						Vector2 p = new Vector2(pop_pos.x,pop_pos.y);
						p.add(bp);
						pop.setVec("ref", "pos", new Vector2(p.x,p.y));
						pop.setFlt("ref", "rot", bod.getFlt("ref", "rot") + pop_rot);

						pop.setFlt("ref", "rot", acc_pos.angleRad());
						
						pNodeSpace.init_body(pop, bluep);
						
						geo.speed_body(bod,pop,acc_pos.x,acc_pos.y,acc_rot);
						bod.param("ctrl_pop").setBody("last", pop);
					} 
				}
				if (bod.getBoo("ctrl_pop","throw")) {
					if (bod.param("ctrl_pop").getBody("last") != null) {
						pBody last = bod.param("ctrl_pop").getBody("last");
						geo.speed_body(bod,last,acc_pos.x,acc_pos.y,acc_rot);
					}
				}
				if (!bod.getBoo("ctrl_pop","pop") && !bod.getBoo("ctrl_pop","throw") && 
						bod.getBoo("ctrl_pop","keep") && 
						bod.param("ctrl_pop").getBody("last") != null) {
					pBody keep = bod.param("ctrl_pop").getBody("last");
					Vector2 bod_pos = bod.getVec("ref","pos");
					Vector2 kp_pos = keep.getVec("ref","pos");
					float bod_rot = bod.getFlt("ref","rot");
					Vector2 trg_pos = new Vector2(pop_pos).add(bod_pos);//.rotateRad(bod_rot)
					float trg_rot = bod_rot + pop_rot;
					float keep_pos = bod.getFlt("ctrl_pop","keep_pos");
					float keep_rot = bod.getFlt("ctrl_pop","keep_rot");
					geo.move_to_target(keep, trg_pos, keep_pos);
					geo.rot_to_target(keep, trg_rot, keep_rot);
					float keep_dist = bod.getFlt("ctrl_pop","keep_dist");
					float dist = kp_pos.sub(bod_pos).len();
					if (dist > keep_dist) bod.param("ctrl_pop").setBody("last", "");
				}

				bod.setBoo("ctrl_pop","pop", false);
				if (bod.getBoo("ctrl_pop","throw") && 
						bod.param("ctrl_pop").getBody("last") != null) {
					bod.param("ctrl_pop").setBody("last", ""); }
				bod.setBoo("ctrl_pop","throw", false);

			}
		}};

		pGeom.newControlProp("pop",coordinate,run_ctrl_pop,"ref") 
		.setFullSync()
		.addData("pop", false)
		.addData("blueprint_par", "")
		.addData("pop_pos", new Vector2(150,0))
		.addData("pop_rot", 0f, "min", -(float)Math.PI, "max", (float)Math.PI)
		.addData("acc_pos", new Vector2(0,0))
		.addData("acc_rot", 0f, "min", 0f, "max", 0.1f)
		.addBody("last")
		.addData("throw", false)
		.addData("keep", true)
		.addData("keep_dist", 500f, "min", 0f, "max", 1000f)
		.addData("keep_pos", 20f, "min", 0f, "max", 20f)
		.addData("keep_rot", 0.1f, "min", 0f, "max", 0.1f)
		;
		
		
		
	}

	//Ctrl action
	public void move_to_target(pBody bod, Vector2 trg_pos, float max_speed) {
		pGeom geo = app.getSystem(pGeom.class);
		Vector2 pos = bod.getVec("ref","pos");
		Vector2 mov = new Vector2(trg_pos).sub(pos);
		float mov_len = mov.len();
		float speed = bod.getFlt("var_move","pos_speed");
		if (speed > max_speed) { geo.slow_body(bod,speed-max_speed,0); }
		if (mov_len > max_speed) {
			mov.nor().scl(max_speed);
			geo.slow_body(bod,mov_len-max_speed,0);
			geo.speed_body(bod,mov.x,mov.y,0);
		} else if (mov_len >= 0) {
//			mov.scl(1f/2f);
			geo.slow_body(bod,speed,0);
			geo.speed_body(bod,mov.x,mov.y,0);
		}
		
	}
	public void rot_to_target(pBody bod, float trg_rot, float max_rot) {
		pGeom geo = app.getSystem(pGeom.class);
		float rot = bod.getFlt("ref","rot");
		float rot_speed = bod.getFlt("var_move", "rot_speed");
		float m = Utl.mapToCircularValuesDist(rot, trg_rot, max_rot, 
				-((float)Math.PI), ((float)Math.PI));
		m *= Utl.mapToCircularValuesDir(rot, trg_rot, max_rot, 
				-((float)Math.PI), ((float)Math.PI)); 
		geo.speed_body(bod,0,0,m);
		if (rot_speed > max_rot || rot_speed < -max_rot || 
				m < max_rot || m > -max_rot) {
			geo.slow_body(bod,0,max_rot); }
	}
	public void speed_body(pBody b, float x, float y, float r) {
		speed_body(b,b,x,y,r); }
	public void speed_body(pBody from, pBody targ, float x, float y, float r) {
		targ.addVec("move", "acc_pos", x, y);
		targ.addFlt("move", "acc_rot", r); 
	}
	public void tp_body(pBody b, float x, float y, float r) {
		tp_body(b,b,x,y,r); }
	public void tp_body(pBody from, pBody targ, float x, float y, float r) {
		targ.addVec("move", "tp_pos", x, y);
		targ.addFlt("move", "tp_rot", r); 
	}
	public void slow_body(pBody b, float p, float r) {
		slow_body(b,b,p,r); }
	public void slow_body(pBody from, pBody targ, float p, float r) {
		targ.addFlt("move", "slow_pos", p);
		targ.addFlt("move", "slow_rot", r); 
	}

	
	
	

	public static nMap<pProperty> control_props = new nMap<pProperty>();
	public static nMap<nRun> control_ticks = new nMap<nRun>();
	
	public static pProperty newControlProp(String ref, pProperty gene, 
			nRun tick_run, String ... fams) {
		pProperty prop = gene.newOptionalLocalProperty("ctrl_"+ref);
		control_props.put(ref,prop);
		control_ticks.put(ref,tick_run);
		pFamily fam = pFamily.newFamily("ctrl_"+ref)
		.addProp("ctrl_"+ref);
		if (fams != null) for (String f : fams) fam.addProp(f);
		return prop;
	}
	

	nRun run_tool_paramlist_update;
	public void build_paramlist_tools() {
		
		nWidgetGroup sec = app.gui.toolbox.addSection("Selected Body", true);
		nInterface interf = app.gui.addInterface();
		interf.pop(sec);
		interf.setContext(bloc);
		
		interf.set_param("entry_height","0.6");

		interf.add_row();
		interf.add_row_label(5, "Selected Body:");
		nWidget body_label = interf.add_row_label(5, "");
		interf.add_row();
		nWidgetGroup paramlist = interf.add_treelist(8,8);
		
		interf.add_col_separator();
		
		run_tool_paramlist_update = new nRun() { public void run() {
			interf.change_current_list(paramlist);
			body_label.setText("");
			if (sel_body != null) {
				body_label.setText(sel_body.pool_ref);
				for (Map.Entry<String,pParam> me : sel_body.params.entrySet()) {
					pParam par = me.getValue();
					interf.add_list_entry(me.getKey() + " : " + par.pool_ref);
					
					for (int i = 0 ; i < Utl.data_type_nb ; i++) {
						nMap<Integer> map = par.prop.data_vals.get(Utl.data_type[i]);
						if (map != null)
								for (Map.Entry<String, Integer> mr : 
								map.entrySet()) {
							String dt_ref = mr.getKey();
							String dt = Utl.to_string(par.get(dt_ref, Utl.data_type[i]));
							nWidget w = interf.add_list_entry("   "+Utl.type_short_names[i]+" " + dt_ref + " = " +dt);
							w.addEventLogic(new nRun(i, dt_ref, par) { public void run() {
								int i = (int)args[0];
								String dt_ref = (String)args[1];
								pParam par = (pParam)args[2];
								String dt = Utl.to_string(par.get(dt_ref, Utl.data_type[i]));
								w.setText("   "+Utl.type_short_names[i]+" " + dt_ref + " = " +dt);
							}});
							interf.go_up_tree();
						}
					}
					for (Map.Entry<String, Integer> mr : par.prop.ref_vals.entrySet()) {
						String ref_ref = mr.getKey();
						String txt = "   ref: " + ref_ref;
						pParam pr = par.getRef(ref_ref);
						if (pr != null) txt += " " +pr.pool_ref;
						interf.add_list_entry(txt);
						interf.go_up_tree();
					}
					for (Map.Entry<String, Integer> mr : par.prop.body_vals.entrySet()) {
						String ref_ref = mr.getKey();
						String txt = "   body: " + ref_ref;
						pBody pr = par.getBody(ref_ref);
						if (pr != null) txt += " " +pr.pool_ref;
						interf.add_list_entry(txt);
						interf.go_up_tree();
					}
					for (Map.Entry<String, Integer> mr : par.prop.collec_vals.entrySet()) {
						String cl_ref = mr.getKey();
						pCollec cl = par.getCollec(cl_ref);
						interf.add_list_entry("   collec: " + cl_ref);
						interf.go_up_tree();
						interf.add_list_entry("     " + cl.pool_ref);
						interf.go_up_tree();
					}
					
					interf.go_up_tree();
				}
			}
		}};
	}

	ArrayList<nRun> eventEmptyClic = new ArrayList<nRun>();
	ArrayList<nRun> eventBodyClic = new ArrayList<nRun>();
	ArrayList<nRun> eventBodyClear = new ArrayList<nRun>();

	public pGeom addEventEmptyClic(nRun r) { 
		if (!eventEmptyClic.contains(r)) eventEmptyClic.add(r); return this; }
	public pGeom removeEventEmptyClic(nRun r) { eventEmptyClic.remove(r); return this; }
	public pGeom addEventBodyClic(nRun r) { 
		if (!eventBodyClic.contains(r)) eventBodyClic.add(r); return this; }
	public pGeom removeEventBodyClic(nRun r) { eventBodyClic.remove(r); return this; }
	public pGeom addEventBodyClear(nRun r) { 
		if (!eventBodyClear.contains(r)) eventBodyClear.add(r); return this; }
	public pGeom removeEventBodyClear(nRun r) { eventBodyClear.remove(r); return this; }
	
	public pGeom() { super(); 
		tick_run = new nRun() { public void run(Object o) { tick((float)o); }};
		net_tick_run = new nRun() { public void run(Object o) { net_tick((float)o); }};
		draw_run = new nDrawable() { public void drawing() { draw(); }}; 
		draw_aabb_run = new nDrawable() { public void drawing() { draw_aabb(); }}; }

	nRun tick_run, net_tick_run;
	nDrawable draw_run, draw_aabb_run;
	
	public pGeom init(sValueBloc b) { return (pGeom) super.init(b); }
	
	public pSpace space;
	
	public sBoo val_do_draw, val_do_aabb_draw, val_do_click_draw, val_do_hover_draw, 
		val_do_calc, val_do_ctrl, val_do_collision, val_do_move; 
	public sBoo val_do_limit;
	public sFlt val_limit_dist;

	nRun clear_run, empty_clic_run, clic_run;
	public pBody sel_body = null;
	nRun run_body_select;

	public void system_init() {
		bloc.addObject("geom", this);

		app.storeSystemType(bloc.ref, this.getClass());

		useNetFrame();
		
		val_do_draw = bloc.obtainBoo("val_do_draw", true);
		val_do_aabb_draw = bloc.obtainBoo("val_do_aabb_draw", false);
		val_do_hover_draw = bloc.obtainBoo("val_do_hover_draw", false);
		val_do_click_draw = bloc.obtainBoo("val_do_click_draw", true);
		val_do_calc = bloc.obtainBoo("val_do_calc", true);
		val_do_ctrl = bloc.obtainBoo("val_do_ctrl", true);
		val_do_collision = bloc.obtainBoo("val_do_collision", true);
		val_do_move = bloc.obtainBoo("val_do_move", true);

		val_do_limit = bloc.obtainBoo("val_do_limit", true);
		val_limit_dist = bloc.obtainFlt("val_limit_dist", 10000f);

//		plane.addEventToolInit(new nRun() { public void run(Object o) {
//			nInterface interf = (nInterface)o;
//		}});

		pFamily.getFamily("aabb_clickable").setClearRun(new nRun() { public void run(Object o) {
			pBody bod = (pBody)o; nRun.runEvents(eventBodyClear, bod); }});
	}
	public void system_load() {

		app.time.addEventTick(tick_run);
		app.time.addEventNetTick(net_tick_run);
		app.view.addDrawable(5, draw_run);
		app.view.addDrawable(20, draw_aabb_run);
		space = app.space;

//		if (!app.RELEASE) 
			tool_setup(false);
		
//		plane.addEventSave(new nRun() { public void run() {
//			
//		}});
		run_body_select = new nRun() { public void run() {
			run_tool_paramlist_update.run();
		}};

		clic_run = new nRun() { public void run(Object o) {
			if (o == null || !(o instanceof pBody)) return;
			pBody bod = (pBody)o;
			select_body(bod);
		}};
		empty_clic_run = new nRun() { public void run() {
//			sel_body = null;
//			run_body_select.run();
		}};
		clear_run = new nRun() { public void run(Object o) {
			if (o == null || !(o instanceof pBody)) return;
			pBody bod = (pBody)o;
			if (bod == sel_body) {
				sel_body = null;
				run_body_select.run();
			}
		}};

		addEventEmptyClic(empty_clic_run);
		addEventBodyClic(clic_run);
		addEventBodyClear(clear_run);

//		if (!app.RELEASE) 
			build_paramlist_tools();
		
	}
	public void select_body(pBody b) {
		if (b == null || b == sel_body) return;
		sel_body = b;
		run_body_select.run();
	}
	public void system_clear() {
		app.time.removeEventTick(tick_run);
		app.time.removeEventNetTick(net_tick_run);
		app.view.removeDrawable(draw_run);
		app.view.removeDrawable(draw_aabb_run);
	}
	
	public void tool_init(nInterface interf) {

		interf.setContext(bloc);
		interf.add_row();
		interf.add_row_switch_boo(4, "draw", "val_do_draw");
		interf.add_row_label(2, "");
		interf.add_row_switch_boo(4, "aabb_draw", "val_do_aabb_draw");
		interf.add_row();
		interf.add_row_switch_boo(4, "hover_draw", "val_do_hover_draw");
		interf.add_row_label(2, "");
		interf.add_row_switch_boo(4, "clic_draw", "val_do_click_draw");
		interf.add_row();
		interf.add_row_switch_boo(4, "aabb_calc", "val_do_calc");
		interf.add_row_label(2, "");
		interf.add_row_switch_boo(4, "do_ctrl", "val_do_ctrl");
		interf.add_row();
		interf.add_row_switch_boo(4, "do_collision", "val_do_collision");
		interf.add_row_label(2, "");
		interf.add_row_switch_boo(4, "do_move", "val_do_move");
		interf.add_row();
		interf.add_row_switch_boo(4, "do_limit", "val_do_limit");
		interf.add_row_label(6, "");
		interf.add_row();
		interf.add_row_label(4, "limit_dist");
		interf.add_row_slide_flt(6, 1000f, 20000f, "val_limit_dist");
		
	}

	public void frame(float delta) { 
		for (pBody b : space.familyMember("mouse")) {
			if (app.view.mouse_is_hover_view()) 
				b.setVec("mouse", "mousepos", 
						new Vector2(app.view.mouse_in_view())
						.sub(b.getVec("ref", "pos")));
			else b.setVec("mouse", "mousepos", 0, 0);
		}
		for (pBody b : space.familyMember("aabb_clickable")) {
			b.setBoo("clickable", "hover", false);
			b.setBoo("clickable", "click", false);
			b.setBoo("clickable", "press", false); }
		
		if (val_do_draw.get() && app.view.mouse_is_hover_view()) {
			boolean found = false;
			for (pBody b : space.familyMember("aabb_clickable")) 
					if (!found && test_clic(b)) {
				found = true;
				b.setBoo("clickable", "hover", true);
				if (app.input.getState("MouseLeft")) 
					b.setBoo("clickable", "press", true);
				if (app.input.getClick("MouseLeft")) {
					b.setBoo("clickable", "click", true);
					nRun.runEvents(eventBodyClic, b); }
			}
			if (app.input.getClick("MouseLeft") && !found) nRun.runEvents(eventEmptyClic);
		}
	}
	
	public void tick(float delta) {
		calc_ref();
		if (val_do_ctrl.get()) {
			for (String ref : control_props.allKey()) {
				for (pBody b : Utl.duplic(space.familyMember("ctrl_"+ref))) {
					control_ticks.get(ref).run(b);
				}
			}
			for (pBody b : space.familyMember("logic")) do_logic(b);
		}
		if (val_do_move.get()) {
			for (pBody b : space.familyMember("movable")) calc_move(b);
		}
		collisions.clear();
		if (val_do_collision.get()) {
			ArrayList<pBody> aabbs = space.familyMember("aabb");
			for (int i = 0 ; i < aabbs.size() ; i++)
				for (int j = i+1 ; j < aabbs.size() ; j++) if (i != j) {
					test_collide(aabbs.get(i), aabbs.get(j)); }
		}
		if (val_do_limit.get()) {
			float l = val_limit_dist.get(); 
			for (pBody b : space.body_pool.temp_all()) {
				if (b.hasParam("ref") && b.getVec("ref","pos").len() > l) b.clear();
			}
		}
	}

	public void net_frame(float delta) { 
		frame(delta);
	}
	public void net_tick(float delta) { 
		calc_ref();
	}
	public void draw() { 
		if (val_do_draw.get() && val_do_limit.get()) {
			app.noFill();
			app.stroke(150,0,0,200,90f);
			app.circle(0,0,val_limit_dist.get());
		}
		
		if (val_do_draw.get())
			for (pBody b : space.familyMember("drawable")) draw_body(app, b);
	
	}
	public void draw_aabb() { 
		if (val_do_click_draw.get())
			for (pBody b : space.familyMember("aabb_clickable")) draw_clic_aabb(b);
		else if (val_do_hover_draw.get())
			for (pBody b : space.familyMember("aabb_clickable")) draw_hover_aabb(b);
		else if (val_do_aabb_draw.get())
			for (pBody b : space.familyMember("aabb_drawable")) draw_aabb(b);
		
//		app.stroke(255,255,255,60,4f); app.fill(255,255,255,60);
//		for (Polygon p : dmg_zones) app.polygon(p);
//		dmg_zones.clear();
	}

	public void init_body(pBody b) {
		if (b.param("ref") == null || b.param("var_move") == null) return;
		b.setVec("var_move", "prev_pos", Utl.copy(b.getVec("ref", "pos")));
		b.setFlt("var_move", "prev_rot", b.getFlt("ref", "rot"));
		b.setVec("var_move", "pos_move", new Vector2());
		b.setFlt("var_move", "rot_move", 0f);
		b.setFlt("var_move", "pos_speed", 0f);
		b.setFlt("var_move", "rot_speed", 0f);
	}
	

	public static Vector2 toRef(pBody b, Vector2 v) {
		if (b == null) return v;
		pParam ref = b.param("ref");
		if (ref == null) return v;
		Vector2 p = new Vector2(v);
		p.rotateRad(ref.getFlt("rot"));
		if (b.hasParam("scale"))
			p.scl(b.getFlt("scale", "scale")); 
		p.add(ref.getVec("pos"));
		return p; }
	
	private void calc_ref() {
		if (val_do_calc.get())
			for (pBody b : space.familyMember("aabb")) calc_info_shape(b);
		
		for (pBody b : space.familyMember("movable")) {
			Vector2 prevpos = Utl.copy(b.getVec("var_move", "prev_pos"));
			b.setVec("var_move", "pos_move", Utl.copy(b.getVec("ref", "pos")).sub(prevpos));
			b.setVec("var_move", "prev_pos", Utl.copy(b.getVec("ref", "pos")));
			b.setFlt("var_move", "pos_speed", b.getVec("var_move", "pos_move").len());
			float pi = (float)Math.PI;
			float rot = b.getFlt("ref", "rot"); 
			while (rot > pi) rot -= 2f*pi; while (rot < -pi) rot += 2f*pi;
			b.setFlt("ref", "rot", rot);
			float prevrot = b.getFlt("var_move", "prev_rot");
			float rs = b.getFlt("ref", "rot") - prevrot;
			while (rs > pi) rs -= 2f*pi; while (rs < -pi) rs += 2f*pi;
			b.setFlt("var_move", "rot_move", rs);
			b.setFlt("var_move", "rot_speed", (float)Math.abs(rs));
			b.setFlt("var_move", "prev_rot", b.getFlt("ref", "rot"));
		}
	}
	
	public void do_logic(pBody b) {
		if (!b.hasParam("logic")) return;
		String func_ref = b.getStr("logic", "func_ref");
		pInstance func = app.patch.common_functions.get(func_ref);
		if (func == null) return;
		
		//TODO
		
	}

	public void calc_move(pBody b) {
		if (!b.hasParam("ref") || !b.hasParam("var_move") || !b.hasParam("move")) return;
		
		Vector2 acc_pos = b.getVec("move", "acc_pos");
		float acc_rot = b.getFlt("move", "acc_rot");
		b.setVec("move", "acc_pos", 0f, 0f);
		b.setFlt("move", "acc_rot", 0f);

		Vector2 tp_pos = b.getVec("move", "tp_pos");
		float tp_rot = b.getFlt("move", "tp_rot");
		b.setVec("move", "tp_pos", 0f, 0f);
		b.setFlt("move", "tp_rot", 0f);
		b.addVec("ref", "pos", tp_pos);
		b.addFlt("ref", "rot", tp_rot); 
		b.addVec("var_move", "prev_pos", tp_pos);
		b.addFlt("var_move", "prev_rot", tp_rot);

		float slow_pos = b.getFlt("move", "slow_pos");
		float slow_rot = b.getFlt("move", "slow_rot");
		b.setFlt("move", "slow_pos", 0f);
		b.setFlt("move", "slow_rot", 0f);

		Vector2 inert = b.getVec("var_move", "pos_move");
		float rinert = b.getFlt("var_move", "rot_move");
		
		if (slow_rot < 0) slow_rot *= -1f;
		if (slow_pos < 0) slow_pos *= -1f;
		if (slow_rot > 0 && rinert != 0) {
			if (rinert > 0) { rinert -= slow_rot; if (rinert < 0) rinert = 0; }
			if (rinert < 0) { rinert += slow_rot; if (rinert > 0) rinert = 0; }
		}
		float pos_speed = b.getFlt("var_move", "pos_speed");
		if (slow_pos > 0 && pos_speed > 0) {
			if (slow_pos > pos_speed) slow_pos = pos_speed;
			inert.scl(1f - (slow_pos / pos_speed));
		}
		
		inert.add(acc_pos); rinert += acc_rot;

		float friction = 1f - b.getFlt("move", "friction");
		
		inert.scl(friction);
		b.addVec("ref", "pos", inert);
		b.addFlt("ref", "rot", rinert); 
			
	}
	
	
	
	
	
	
	
	public boolean test_clic(pBody b) {
		Vector2 pos = b.getVec("info_shape", "aabb_pos");
		Vector2 size = b.getVec("info_shape", "aabb_size");
		Vector2 m = new Vector2();
		m.set(app.view.mouse_in_view());
		Rectangle aabb = new Rectangle(pos.x, pos.y, size.x, size.y);
		return aabb.contains(m);
	}

	ArrayList<Polygon> dmg_zones = new ArrayList<Polygon>();
	
	public ArrayList<Collision> collisions = new ArrayList<Collision>();
	class Collision {
		public pBody bod1,bod2; public Polygon overlap;
		public Collision(pBody b1, pBody b2, Polygon p) {
			bod1 = b1; bod2 = b2; overlap = p; } }
	
	public void test_collide(pBody b1, pBody b2) {
		if (!b1.hasParam("ref") || !b2.hasParam("ref") || 
				!b1.hasParam("info_shape") || !b2.hasParam("info_shape") || 
				!b1.hasParam("geom") || !b2.hasParam("geom")) return;

		Vector2 p1 = b1.getVec("info_shape", "aabb_pos");
		Vector2 s1 = b1.getVec("info_shape", "aabb_size");
		Rectangle aabb1 = new Rectangle(p1.x, p1.y, s1.x, s1.y);
		Vector2 p2 = b2.getVec("info_shape", "aabb_pos");
		Vector2 s2 = b2.getVec("info_shape", "aabb_size");
		Rectangle aabb2 = new Rectangle(p2.x, p2.y, s2.x, s2.y);
		if (Utl.intersect(aabb1, aabb2)) {
			ArrayList<Polygon> polys1 = get_geom_polys(b1);
			ArrayList<Polygon> polys2 = get_geom_polys(b2);
//			app.log(b1.pool_ref+" "+polys1.size()+" "+b2.pool_ref+" "+polys2.size());
			for (Polygon pol1 : polys1)
				for (Polygon pol2 : polys2)
					test_collide(b1,b2, pol1, pol2);
			}
	}
	
	public void test_collide(pBody b1, pBody b2, Polygon poly1, Polygon poly2) {
		

		poly1.setOrigin(0f,0f);
		poly1.setPosition(b1.getVec("ref", "pos").x, b1.getVec("ref", "pos").y);
		poly1.setRotation(MathUtils.radiansToDegrees * b1.getFlt("ref", "rot"));
//		poly1.setScale(b1.getFlt("ref", "scale"), b1.getFlt("ref", "scale"));
		poly2.setOrigin(0f,0f);
		poly2.setPosition(b2.getVec("ref", "pos").x, b2.getVec("ref", "pos").y);
		poly2.setRotation(MathUtils.radiansToDegrees * b2.getFlt("ref", "rot"));
//		poly2.setScale(b2.getFlt("ref", "scale"), b2.getFlt("ref", "scale"));

		Polygon poly_overlap = new Polygon();
		boolean intersect = Intersector.intersectPolygons(poly1,poly2,poly_overlap);

//		app.fill(255); 
//		app.stroke(0,0,255,255,3f);
//		if (intersect) app.polygon(poly_overlap);
		
		if (intersect) {
			collisions.add(new Collision(b1,b2,poly_overlap));
			dmg_zones.add(poly_overlap);
		}
	}
	
	

	public void draw_body(PlaneApplet app, pBody b) {
		if (!b.hasParam("ref") || !b.hasParam("graph") || 
				!b.hasParam("geom")) return;
		draw_geom_graph(app, b, b.param("geom"), b.param("graph"));
	}

	public static void draw_geom_graph(PlaneApplet app, pBody b, pParam geom, pParam graph) {
		if (geom == null) return;
		ArrayList<Vector2> point = geom.getCollecData("point", Vector2.class);
		if (point == null) return;
		
		ArrayList<Integer> faceA = geom.getCollecData("faceA", Integer.class);
		ArrayList<Integer> faceB = geom.getCollecData("faceB", Integer.class);
		ArrayList<Integer> faceC = geom.getCollecData("faceC", Integer.class);
		if (faceA == null || faceB == null || faceC == null || 
				faceA.size() != faceB.size() || faceA.size() != faceC.size() || 
				faceC.size() != faceB.size()) return;

		ArrayList<Integer> lineA = geom.getCollecData("lineA", Integer.class);
		ArrayList<Integer> lineB = geom.getCollecData("lineB", Integer.class);
		if (lineA == null || lineB == null || 
				lineA.size() != lineB.size()) return;
		
		if (graph == null) {
			app.stroke(255,255,255,255,3f);
			app.noFill();

			for (int i = 0 ; i < faceA.size() ; i++) {
				int p1 = faceA.get(i), p2 = faceB.get(i), p3 = faceC.get(i);
				if (p1 < 0 || p1 >= point.size() || 
						p2 < 0 || p2 >= point.size() || 
						p3 < 0 || p3 >= point.size()) continue;
				app.polygon(toRef(b, point.get(p1)), 
						toRef(b, point.get(p2)), 
						toRef(b, point.get(p3)));
			}
			for (int i = 0 ; i < lineA.size() ; i++) {
				int p1 = lineA.get(i), p2 = lineB.get(i);
				if (p1 < 0 || p1 >= point.size() || p2 < 0 || p2 >= point.size()) continue;
				app.line(toRef(b, point.get(p1)), toRef(b, point.get(p2)));
			}
			return;
		}
		boolean fill = graph.getBoo("fill");
		if (fill) {
			Color col_fill = Utl.color(
					graph.getInt("fill_r"), 
					graph.getInt("fill_g"), 
					graph.getInt("fill_b"), 
					graph.getInt("fill_a") );
			app.fill(col_fill);
			app.noStroke();
			
			for (int i = 0 ; i < faceA.size() ; i++) {
				int p1 = faceA.get(i), p2 = faceB.get(i), p3 = faceC.get(i);
				if (p1 < 0 || p1 >= point.size() || 
						p2 < 0 || p2 >= point.size() || 
						p3 < 0 || p3 >= point.size()) continue;
				app.polygon(toRef(b, point.get(p1)), 
						toRef(b, point.get(p2)), 
						toRef(b, point.get(p3)));
			}
		} 
		boolean line = graph.getBoo("line");
		if (line) {
			float thick = graph.getFlt("thick");
			Color col_line = Utl.color(
					graph.getInt("line_r"), 
					graph.getInt("line_g"), 
					graph.getInt("line_b"), 
					graph.getInt("line_a") );
			app.stroke(col_line, thick);
			app.noFill();

			for (int i = 0 ; i < faceA.size() ; i++) {
				int p1 = faceA.get(i), p2 = faceB.get(i), p3 = faceC.get(i);
				if (p1 < 0 || p1 >= point.size() || 
						p2 < 0 || p2 >= point.size() || 
						p3 < 0 || p3 >= point.size()) continue;
				app.polygon(toRef(b, point.get(p1)), 
						toRef(b, point.get(p2)), 
						toRef(b, point.get(p3)));
			}
			for (int i = 0 ; i < lineA.size() ; i++) {
				int p1 = lineA.get(i), p2 = lineB.get(i);
				if (p1 < 0 || p1 >= point.size() || p2 < 0 || p2 >= point.size()) continue;
				app.line(toRef(b, point.get(p1)), toRef(b, point.get(p2)));
			}
		} 
	}

	

	public static ArrayList<Polygon> get_geom_polys(pBody b) {
		ArrayList<Polygon> polys = new ArrayList<Polygon>();
		if (!b.hasParam("ref") || !b.hasParam("geom")) return polys;
		
		pParam geom = b.param("geom");
		ArrayList<Vector2> point = geom.getCollecData("point", Vector2.class);
		if (point.size() > 0) {
			ArrayList<Integer> faceA = geom.getCollecData("faceA", Integer.class);
			ArrayList<Integer> faceB = geom.getCollecData("faceB", Integer.class);
			ArrayList<Integer> faceC = geom.getCollecData("faceC", Integer.class);
			if (faceA.size() != faceB.size() || faceA.size() != faceC.size() || 
					faceC.size() != faceB.size()) return polys;
			
			Vector2 v1 = new Vector2(), v2 = new Vector2(), v3 = new Vector2();
			for (int i = 0 ; i < faceA.size() ; i++) {
				int p1 = faceA.get(i), p2 = faceB.get(i), p3 = faceC.get(i);
				if (p1 < 0 || p1 >= point.size() || 
						p2 < 0 || p2 >= point.size() || 
						p3 < 0 || p3 >= point.size()) continue;
				v1.set(point.get(p1));
				v2.set(point.get(p2));
				v3.set(point.get(p3));
				Polygon poly = new Polygon();
				float[] vert = new float[6];
				vert[0] = v1.x; vert[1] = v1.y;
				vert[2] = v2.x; vert[3] = v2.y;
				vert[4] = v3.x; vert[5] = v3.y;
				poly.setVertices(vert);
				polys.add(poly);
			}
		}
		return polys;
	}


	public static void calc_info_shape(pBody b) {
		if (!b.hasParam("ref") || !b.hasParam("info_shape") || 
				!b.hasParam("geom")) return;
		pParam geom = b.param("geom");
		ArrayList<Vector2> point = geom.getCollecData("point", Vector2.class);
		if (point == null) return;
		if (point.size() > 0) {
			Vector2 pos = new Vector2(toRef(b, point.get(0)));
			Vector2 size = new Vector2();
			for (Vector2 pv : point) {
				Vector2 v = toRef(b, pv);
				if (v.x < pos.x) pos.x = v.x;
				if (v.y < pos.y) pos.y = v.y;
			}
			for (Vector2 pv : point) {
				Vector2 v = toRef(b, pv);
				v.sub(pos);
				if (v.x > size.x) size.x = v.x;
				if (v.y > size.y) size.y = v.y;
			}
			b.setVec("info_shape", "aabb_pos", pos);
			b.setVec("info_shape", "aabb_size", size);
			
			ArrayList<Integer> faceA = geom.getCollecData("faceA", Integer.class);
			ArrayList<Integer> faceB = geom.getCollecData("faceB", Integer.class);
			ArrayList<Integer> faceC = geom.getCollecData("faceC", Integer.class);
			if (faceA.size() != faceB.size() || faceA.size() != faceC.size() || 
					faceC.size() != faceB.size()) return;
			float area = 0;
			Polygon poly = new Polygon();
			float[] vert = new float[6];
			Vector2 v1 = new Vector2(), v2 = new Vector2(), v3 = new Vector2();
			for (int i = 0 ; i < faceA.size() ; i++) {
				int p1 = faceA.get(i), p2 = faceB.get(i), p3 = faceC.get(i);
				if (p1 < 0 || p1 >= point.size() || 
						p2 < 0 || p2 >= point.size() || 
						p3 < 0 || p3 >= point.size()) continue;
				v1.set(toRef(b, point.get(p1)));
				v2.set(toRef(b, point.get(p2)));
				v3.set(toRef(b, point.get(p3)));
				vert[0] = v1.x; vert[1] = v1.y;
				vert[2] = v2.x; vert[3] = v2.y;
				vert[4] = v3.x; vert[5] = v3.y;
				poly.setVertices(vert);
				area += poly.area();
			}
			b.setFlt("info_shape", "area", area);
			
		} else {
			b.setVec("info_shape", "aabb_pos", b.getVec("ref", "pos"));
			b.setVec("info_shape", "aabb_size", new Vector2());
			b.setFlt("info_shape", "area", 1f);
		}
	}

	public void draw_aabb(pBody b) {
		if (!b.hasParam("info_shape")) return;
		Vector2 pos = b.getVec("info_shape", "aabb_pos");
		Vector2 size = b.getVec("info_shape", "aabb_size");
		app.stroke(255, 0, 255, 127, 2f); 
		app.noFill();
		Vector2 p1 = new Vector2(pos.x+size.x,pos.y);
		Vector2 p2 = new Vector2(pos.x,pos.y+size.y);
		Vector2 p3 = new Vector2(pos.x+size.x,pos.y+size.y);
		app.polygon(pos, p1, p3, p2);
	}

	public void draw_hover_aabb(pBody b) {
		if (!b.hasParam("info_shape")) return;
		Vector2 pos = b.getVec("info_shape", "aabb_pos");
		Vector2 size = b.getVec("info_shape", "aabb_size");
		if (b.hasParam("highlightable") && b.getBoo("highlightable", "lighted")) {
			int lr = b.getInt("highlightable", "light_red");
			int lg = b.getInt("highlightable", "light_green");
			int lb = b.getInt("highlightable", "light_blue");
			if (b.hasParam("clickable") && b.getBoo("clickable", "press")) 
				app.stroke(lr, lg, lb, 255, 1f);
			else if (b.hasParam("clickable") && b.getBoo("clickable", "hover")) 
				app.stroke(lr, lg, lb, 255, 6f);
			else app.stroke(lr, lg, lb, 255, 2f);
		}
		else if (b.hasParam("clickable") && b.getBoo("clickable", "press"))
			app.stroke(255, 0, 255, 255, 1f); 
		else if (b.hasParam("clickable") && b.getBoo("clickable", "hover"))
			app.stroke(255, 0, 255, 255, 6f); 
		else app.stroke(255, 0, 255, 127, 2f); 
		app.noFill();
		Vector2 p1 = new Vector2(pos.x+size.x,pos.y);
		Vector2 p2 = new Vector2(pos.x,pos.y+size.y);
		Vector2 p3 = new Vector2(pos.x+size.x,pos.y+size.y);
		app.polygon(pos, p1, p3, p2);
	}
	
	public void draw_clic_aabb(pBody b) {
		if (!b.hasParam("info_shape")) return;
		Vector2 pos = b.getVec("info_shape", "aabb_pos");
		Vector2 size = b.getVec("info_shape", "aabb_size");
		if (b.hasParam("highlightable") && b.getBoo("highlightable", "lighted")) {
			int lr = b.getInt("highlightable", "light_red");
			int lg = b.getInt("highlightable", "light_green");
			int lb = b.getInt("highlightable", "light_blue");
			if (b.hasParam("clickable") && b.getBoo("clickable", "press")) 
				app.stroke(lr, lg, lb, 255, 1f);
			else if (b.hasParam("clickable") && b.getBoo("clickable", "hover")) 
				app.stroke(lr, lg, lb, 255, 6f);
			else return; 
		}
		else if (b.hasParam("clickable") && b.getBoo("clickable", "press"))
			app.stroke(255, 0, 255, 255, 1f); 
		else if (b.hasParam("clickable") && b.getBoo("clickable", "hover"))
			app.stroke(255, 0, 255, 255, 6f); 
		else return; 
		app.noFill();
		Vector2 p1 = new Vector2(pos.x+size.x,pos.y);
		Vector2 p2 = new Vector2(pos.x,pos.y+size.y);
		Vector2 p3 = new Vector2(pos.x+size.x,pos.y+size.y);
		app.polygon(pos, p1, p3, p2);
	}
	
	
	
	
	
	
	


}
