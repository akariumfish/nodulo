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
import com.noodle.nodulo.GdxApp;

import app.App;
import app.nDrawer;
import box2d.pBox2d;
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
import patch.pFunc;
import patch.pInstance;
import patch.pNode;
import patch.pNodeSpace;
import patch.pPar;
import patch.pProcess;
import patch.pStandard;
import patch.pTileHead;
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

			stand.addInitRun(new nRun() {public void run() {
				instance.run("empty_geom");
			}});
			stand.newRun("empty_geom", new nRun() {public void run() {
				Color fill = nGUI.book.getModel("CL_def_graph").color_background;
				Color line = nGUI.book.getModel("CL_def_graph").color_outline;
				float thick = nGUI.book.getModel("CL_def_graph").outlineWeight;
				instance.setVar("fill_color", Utl.rgbToInt(fill));
				instance.setVar("line_color", Utl.rgbToInt(line));
				instance.setVar("line_thick", thick);
				pParam par = instance.object("param", pParam.class);
				if (par == null) return; 
				par.collecEmpty("point");
				par.collecEmpty("color");
				par.collecEmpty("faceA");
				par.collecEmpty("faceB");
				par.collecEmpty("faceC");
				par.collecEmpty("faceT");
				par.collecEmpty("faceL");
//				par.collecEmpty("lineA");
//				par.collecEmpty("lineB");
//				par.collecEmpty("lineT");
//				par.collecEmpty("light");
//				par.collecEmpty("light_dist");
//				par.collecEmpty("aura");
//				par.collecEmpty("aura_dist");
//				par.collecEmpty("halo");
//				par.collecEmpty("halo_rad");
			}});
			stand.newRun("set_fill_color", new nRun() {public void run() {
				Color col = arg(0, Color.class);
				instance.setVar("fill_color", Utl.rgbToInt(col));
			}});
			stand.newRun("add_point", new nRun() {public void run() {
				float x = arg(0, Float.class);
				float y = arg(1, Float.class);
				pParam par = instance.object("param", pParam.class);
				if (par == null) return; 
				par.collecAdd("point", new Vector2(x,y)); 
				par.collecAdd("color", instance.getVar("fill_color")); 
			}});
//			stand.newRun("add_line", new nRun() {public void run() {
//				int p1 = arg(0, Integer.class);
//				int p2 = arg(1, Integer.class);
//				pParam par = instance.object("param", pParam.class);
//				if (par == null) return; 
//				par.collecAdd("lineA", p1);
//				par.collecAdd("lineB", p2);
//				par.collecAdd("lineT", instance.getVar("line_thick")); 
//			}});
			stand.newRun("add_face", new nRun() {public void run() {
				int p1 = arg(0, Integer.class);
				int p2 = arg(1, Integer.class);
				int p3 = arg(2, Integer.class);
				pParam par = instance.object("param", pParam.class);
				if (par == null) return; 
				par.collecAdd("faceA", p1);
				par.collecAdd("faceB", p2);
				par.collecAdd("faceC", p3);
				par.collecAdd("faceT", instance.getVar("line_thick")); 
				par.collecAdd("faceL", instance.getVar("line_color")); 
			}});
			stand.newRun("set_def", new nRun() {public void run() {
				instance.run("empty_geom");
				pParam par = instance.object("param", pParam.class);
				if (par == null) return; 
				Vector2 v = new Vector2(80,0); 
				Vector2 v2 = new Vector2(80,0); 
				v.rotateRad((float)(2f*Math.PI/3f)); 
				v2.rotateRad((float)(2f*Math.PI/3f)); 
				v2.rotateRad((float)(2f*Math.PI/3f)); 
				instance.run("add_point", v.x, v.y); 
				instance.run("add_point", 80f, 0f); 
				instance.run("add_point", v2.x, v2.y);
				instance.run("add_face", (int)0, (int)1, (int)2);
			}});
			stand.newRun("set_trig", new nRun() {public void run() {
				float r = arg(0, Float.class);
				instance.run("empty_geom");
				pParam par = instance.object("param", pParam.class);
				if (par == null) return; 
				Vector2 v = new Vector2(r,0); 
				Vector2 v2 = new Vector2(r,0); 
				v.rotateRad((float)(2f*Math.PI/3f)); 
				v2.rotateRad((float)(2f*Math.PI/3f)); 
				v2.rotateRad((float)(2f*Math.PI/3f)); 
				instance.run("add_point", v.x, v.y); 
				instance.run("add_point", r, 0f); 
				instance.run("add_point", v2.x, v2.y);
				instance.run("add_face", (int)0, (int)1, (int)2);
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
				instance.run("add_point", v.x, v.y); 
				instance.run("add_point", r, 0f); 
				instance.run("add_point", v2.x, v2.y);
				instance.run("add_face", 
						col_size+(int)0, col_size+(int)1, col_size+(int)2);
			}});
			stand.newRun("new_trig", new nRun() {public void run() {
				float x = arg(0, Float.class);
				float y = arg(1, Float.class);
				float rad = arg(2, Float.class);
				float rot = arg(3, Float.class);
				pParam par = instance.object("param", pParam.class);
				if (par == null) return; 
				Vector2 p = new Vector2(x,y); 
				Vector2 v = new Vector2(rad,0); 
				Vector2 v1 = new Vector2(rad,0); 
				Vector2 v2 = new Vector2(rad,0); 
				v.rotateRad((float)(2f*Math.PI/3f));
				v.rotateRad(rot).add(p);
				v1.rotateRad(rot).add(p);
				v2.rotateRad((float)(2f*Math.PI/3f)); 
				v2.rotateRad((float)(2f*Math.PI/3f));
				v2.rotateRad(rot).add(p); 
				int col_size = par.getCollecSize("point");
				instance.run("add_point", v.x, v.y); 
				instance.run("add_point", v1.x, v1.y); 
				instance.run("add_point", v2.x, v2.y);
				instance.run("add_face", 
						col_size+(int)0, col_size+(int)1, col_size+(int)2);
			}});
			stand.newRun("new_face", new nRun() {public void run() {
				float x1 = arg(0, Float.class);
				float y1 = arg(1, Float.class);
				float x2 = arg(2, Float.class);
				float y2 = arg(3, Float.class);
				float x3 = arg(4, Float.class);
				float y3 = arg(5, Float.class);
				pParam par = instance.object("param", pParam.class);
				if (par == null) return;  
				int col_size = par.getCollecSize("point");
				instance.run("add_point", x1, y1); 
				instance.run("add_point", x2, y2); 
				instance.run("add_point", x3, y3);
				instance.run("add_face", 
						col_size+(int)0, col_size+(int)1, col_size+(int)2);
			}});
			
			
			
			
			stand.newRun("pop_editor", new nRun() {public void run() {

				instance.obtainVar("sel_point", (int)-1);
				
				float pvs = 3f;
				float pvw = RS*7f*pvs;
				float pvh = RS*7f*pvs;
				
				nInterface interf = PlaneApplet.app.gui.get_popWindow();
				

				interf.add_col();
				interf.add_row();
				interf.add_row_label((int)(7*pvs) + 8,"EDITOR");

				interf.add_row();
				interf.add_row_label((int)(7*pvs) + 4, "");
				nWidget empty_geom_widg = interf.add_row_trigg(4,"EMPTY");
				
				

				interf.add_line();
				interf.add_col();
				interf.add_row();
				interf.add_row_label(2,"Pnt");

				interf.add_row();
				nWidgetGroup pointpick = 
						interf.add_picklist(2,(int)(7*pvs));
				
				nRun sel_point = new nRun() { public void run() {
					if (args.length != 1) return;
					int id = arg(0, Integer.class);
					if(instance.getVar("sel_point", Integer.class) == id) 
						return;
					instance.setVar("sel_point", id); 
					pointpick.metode("set_pick", ""+id);
				}};
				
				pointpick.metode("set_pick_event", new nRun(instance) { public void run(Object o) {
					pInstance inst = (pInstance)builder;
					String t = (String)o;
					sel_point.do_run(inst, Utl.toint(t));
				}});
				interf.add_row();

				interf.add_col();
				interf.add_row();
				interf.add_row_label((int)(7*pvs)," ");

				interf.add_row();
				nWidget preview = interf.add_row_label(9,"");

//				interf.set_param("entry_height","0.8");
//				interf.add_row();
//				nWidgetGroup pointlist = interf.add_treelist(20,5);
//				interf.set_param("entry_height","1");

				interf.add_row();
				
				interf.add_col();
				interf.add_row();
				nRun pointlist_run = new nRun(instance) { public void run() {
					pInstance inst = (pInstance)builder;
					pParam geom = inst.object("param", pParam.class);
					if (geom == null) return; 
					ArrayList<Vector2> point = geom.getCollecData("point", Vector2.class);
					interf.change_current_list(pointpick);
					int i = 0;
					for (Vector2 v : point) {
						interf.add_list_entry(""+i);
//						interf.add_list_entry("point "+i+" : "+v.x+" "+v.y);
//						interf.go_up_tree();
						i++;
					}
//					ArrayList<Integer> faceA = geom.getCollecData("faceA", Integer.class);
//					ArrayList<Integer> faceB = geom.getCollecData("faceB", Integer.class);
//					ArrayList<Integer> faceC = geom.getCollecData("faceC", Integer.class);
//					if (faceA.size() != faceB.size() || faceA.size() != faceC.size() || 
//							faceC.size() != faceB.size()) return;
//
//					ArrayList<Integer> lineA = geom.getCollecData("lineA", Integer.class);
//					ArrayList<Integer> lineB = geom.getCollecData("lineB", Integer.class);
//					if (lineA.size() != lineB.size()) return;
//					for (i = 0 ; i < faceA.size() ; i++) {
//						int p1 = faceA.get(i), p2 = faceB.get(i), p3 = faceC.get(i);
//						interf.add_list_entry("face "+i+" : "+p1+" "+p2+" "+p3);
//						interf.go_up_tree();
//					}
//					for (i = 0 ; i < lineA.size() ; i++) {
//						int p1 = lineA.get(i), p2 = lineB.get(i);
//						interf.add_list_entry("line "+i+" : "+p1+" "+p2);
//						interf.go_up_tree();
//					}

					if(inst.getVar("sel_point", Integer.class) == null) 
						return;
					pointpick.metode("set_pick", ""+
						inst.getVar("sel_point", Integer.class));
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
				interf.add_row_trigg(3,"+PNT", new nRun(instance) { public void run() {
					((pInstance)builder).run("add_point", 0f, 0f);
					pointlist_run.do_run((pInstance)builder);
				}});
				interf.add_row_trigg(3,"+TRIG", new nRun(instance) { public void run() {
					((pInstance)builder).run("add_trig", 60f);
					pointlist_run.do_run((pInstance)builder);
				}});
				
				interf.add_row();
				interf.add_row_label(6, "");
				interf.add_row();
				interf.add_row_trigg(6,"DEL PNT", new nRun(instance) { public void run() {
//					((pInstance)builder).run("add_point", 0f, 0f);
//					pointlist_run.do_run((pInstance)builder);
				}});
				
				empty_geom_widg.addEventTrigger(new nRun(instance) { public void run() {
					((pInstance)builder).run("empty_geom");
					pointlist_run.do_run((pInstance)builder);
				}});
				
				
				


				
				pointlist_run.do_run(instance);

				preview.setSize(pvw,pvh);
				nRun pr = new nRun() {public void run() { 
					PlaneApplet app = PlaneApplet.app;
					app.fill(0); app.rect(0,0,pvw,pvh);
					app.push(); app.translate(pvw/2f,pvh/2f); app.scale(pvs);
					
					app.stroke(255,0,0,255,2f); app.line(0,-100,0,100);
					app.stroke(0,255,0,255,2f); app.line(-100,0,100,0);
					app.pop();
					
					pParam geom = instance.object("param", pParam.class);
					if (geom == null) return; 
					app.push(); app.translate(pvw/2f,pvh/2f); app.scale(pvs);
					
					pGeom.draw_geom(app, null, geom);
					
					ArrayList<Vector2> point = geom.getCollecData("point", Vector2.class);
					
					app.fill(220); app.noStroke();
					for (Vector2 v : point) {
							app.circle(v.x, v.y, 1.5f);
					}
					
					int sel_point = instance.getVar("sel_point", Integer.class);
					if (sel_point >= 0 && sel_point < point.size()) {
						Vector2 v = point.get(sel_point);
						app.fill(255,180,0,255);
						app.circle(v.x, v.y, 3f);
					}

					Vector2 mouse = new Vector2(app.input.mouse);
					mouse.sub(preview.getPos()).sub(pvw/2f,pvh/2f).scl(1f/pvs);
					for (Vector2 v : point) {
						Vector2 l = new Vector2(v).sub(mouse);
//						app.fill(255,180,0,255);
//						app.circle(n.x, n.y, 10f);
//						app.circle(mouse.x, mouse.y, 10f);
						if (l.len() <= 8f) {
							app.fill(255,255,0,255);
							app.circle(v.x, v.y, 2f);
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
					mouse.sub(preview.getPos()).sub(pvw/2f,pvh/2f).scl(1f/pvs);
					int i = 0;
					for (Vector2 v : point) {
						Vector2 l = new Vector2(v).sub(mouse);
						if (l.len() <= 8f && App.ap.input.mouseLeft.trigClick) {
							sel_point.do_run(inst, i);
							break; }
						i++; 
					}
					if (i >= point.size() && 
							preview.globalrect.contains(App.ap.input.mouse) && 
							App.ap.input.mouseLeft.trigClick)
						sel_point.do_run(inst, (int)-1);
				}});
				
				App.ap.addEventNextFrame(new nRun() { public void run() {
					PlaneApplet.app.gui.pop_popwindow("Geom"); 
				}});
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
					instance.run("pop_editor"); }})
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
				pGeom.draw_geom(app, null, par); 
				app.pop();
			}}) 
			.commande(pNode.getCom(CT.COM_ADD_WIDGET))
			.closeSec()
			;
		}};
		
		
		
		
		
		// PROPERTY DEF
		
		

		pProperty space_prop = pProperty.newGeneralProperty("space");

		space_prop.addInitRun(new nRun() {public void run() {
			Utl.plane.space.space_param = contextParam(); }});

		space_prop.addClearRun(new nRun() {public void run() {
			Utl.plane.space.space_param = null; }});
		
		nRun space_prop_run = new nRun() { public void run() {
			pStandard stand = arg(0,pStandard.class);
			if (stand == null) return;
			stand.addInitRun(new nRun() {public void run() {
				int i = 0;
				String r = ""+i;
				while (instance.patch.function_props.hasKey(r)) {
					i++; r = ""+i; }
				instance.patch.function_props.put(r,instance);
				instance.setVar("inst_ref",r);
			}});
			stand.addClearRun(new nRun() {public void run() {
				instance.patch.function_props.remove(
						instance.getVar("inst_ref", String.class),instance);
			}});
			stand.append(pTileHead.exec_context);
			stand.openSec()
				.param("keys", new String[]{"reg", "register", "in"}, 
						"filters", new String[]{"out"}) 
				.run(pNode.getRun(pNode.CT.RUNS_ADD_CO_IN), "co_reg")
			.closeSec();
		}};

		space_prop
		.addData("setup_func", "func_setup")
		.addData("init_func", "func_start")
		.addData("inst_ref", "")
		.addData("tilemap", "Map2.tmx")
		.addNodeRun(space_prop_run)
		;

		space_prop.newRun("start",new nRun() {public void run() {
			pParam sp = contextParam();
			String funcref = sp.getStr("init_func");
			pInstance func = Utl.plane.patch.common_functions.get(funcref);
			String inst_ref = sp.getStr("inst_ref");
			pInstance inst = Utl.plane.patch.function_props.get(inst_ref);
			if (func == null) return;
			Object[] script = func.get("get_instruction_script", Object[].class);
			if (script == null) return;
			pFunc.func_script_run(inst, script, null); 
		}});

		space_prop.newRun("setup",new nRun() {public void run() {
//			Utl.logn("setup");
			pParam sp = contextParam();
			String map = sp.getStr("tilemap");
			Utl.plane.getSystem(pBox2d.class).loadMap(map);
			String funcref = sp.getStr("setup_func");
			pInstance func = Utl.plane.patch.common_functions.get(funcref);
			String inst_ref = sp.getStr("inst_ref");
			pInstance inst = Utl.plane.patch.function_props.get(inst_ref);
			if (func == null) return;
			Object[] script = func.get("get_instruction_script", Object[].class);
			if (script == null) return;
			pFunc.func_script_run(inst, script, null); 
		}});

		

		pProperty coordinate = pProperty.newGeneralProperty("coordinate");
//		coordinate
//		.addData("limit", true)
//		.addData("limit_dist", 20000f)
//		;
		
		coordinate.newLocalProperty("ref")
		.addData("pos", new Vector2())
		.addData("rot", 0f)
		.addData("scale", 1f)
		.addData("aabb_pos", new Vector2())
		.addData("aabb_size", new Vector2())
		;
		
		
		
		pProperty geom = pProperty.newGeneralProperty("geom")
		.setGroupFlag("draw")
		.addData("name","")
		.addData("halo", false)
		.addCollec("point", Vector2.class)
		.addCollec("color", Integer.class)
//		.addCollec("color3", Integer.class)
//		.addCollec("lineA", Integer.class)
//		.addCollec("lineB", Integer.class)
//		.addCollec("lineT", Float.class)
		.addCollec("faceA", Integer.class)
		.addCollec("faceB", Integer.class)
		.addCollec("faceC", Integer.class)
		.addCollec("faceT", Float.class)
		.addCollec("faceL", Integer.class)
//		.addCollec("circleC", Integer.class)
//		.addCollec("circleR", Float.class)
//		.addCollec("circleT", Float.class)
		
//		.addCollec("light", Integer.class)
//		.addCollec("light_dist", Float.class)
//		.addCollec("aura", Integer.class)
//		.addCollec("aura_dist", Float.class)
//		.addCollec("halo", Integer.class)
//		.addCollec("halo_rad", Float.class)
		
		.addNodeRun(geom_prop_run)
		;

		geom.newRun("get_flt_array",new nRun() {public Object get() {
			pParam geom = contextParam();
			if (geom == null) return null;
			ArrayList<Vector2> point = geom.getCollecData("point", Vector2.class);
			ArrayList<Integer> color = geom.getCollecData("color", Integer.class);
			ArrayList<Integer> faceA = geom.getCollecData("faceA", Integer.class);
			ArrayList<Integer> faceB = geom.getCollecData("faceB", Integer.class);
			ArrayList<Integer> faceC = geom.getCollecData("faceC", Integer.class);
			if (faceA.size() != faceB.size() || faceA.size() != faceC.size() || 
					color.size() != point.size()) return null;
			Float[] pl = new Float[9 * faceA.size()];
			for (int i = 0 ; i < faceA.size() ; i++) {
				int p1 = faceA.get(i), p2 = faceB.get(i), p3 = faceC.get(i);
				if (p1 < 0 || p1 >= point.size() || 
						p2 < 0 || p2 >= point.size() || 
						p3 < 0 || p3 >= point.size()) continue;
				pl[i*9] = point.get(p1).x; 
				pl[i*9+1] = point.get(p1).y;
				pl[i*9+2] = Utl.intToColor(color.get(p1)).toFloatBits(); 
				pl[i*9+3] = point.get(p2).x;
				pl[i*9+4] = point.get(p2).y; 
				pl[i*9+5] = Utl.intToColor(color.get(p2)).toFloatBits(); 
				pl[i*9+6] = point.get(p3).x; 
				pl[i*9+7] = point.get(p3).y; 
				pl[i*9+8] = Utl.intToColor(color.get(p3)).toFloatBits();
			}
			return pl;
		}});

		geom.newRun("pop_shape",new nRun() {public Object get() {
			pParam geom = contextParam();
			pBox2d box = PlaneApplet.app.getSystem(pBox2d.class);
			if (box == null || args.length < 3) return null;
			String shape = arg(0,String.class);
			Vector2 pos = arg(1,Vector2.class);
			float rot = arg(2,Float.class);
			return box.popShape(shape,geom,pos,rot);
		}});

		

		pProperty hitpoint = pProperty.newGeneralProperty("hitpoint")
		.addData("avatar", false)
		;
		hitpoint.newLocalProperty("hp")
		.addData("hp", (int)5)
		;

		pProperty.newGeneralProperty("hitzone")
		.addData("damage", (int)1)
		;
		
		pFamily.newFamily("hitpoint")
		.addProp("hp")
		;
		
		
		
		pFamily.newFamily("drawable")
		.addProp("ref")
		.addProp("geom")
		;
		pFamily.newFamily("aabb")
		.addProp("ref")
		.addProp("geom")
		;
		
		
		
		

		
		pProperty interactif = pProperty.newGeneralProperty("interactif");

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
		.addProp("ref")
		.addProp("clickable")
		;

		pFamily.newFamily("mouse")
		.addProp("ref")
		.addProp("mouse")
		;

		
		

		
		
		
		
		

		
		

		nRun run_ctrl_mob = new nRun() { public void run(Object o) { 
			pBody bod = (pBody)o; if (bod == null) return;
			pGeom geo = PlaneApplet.app.getSystem(pGeom.class);
			pBox2d box = PlaneApplet.app.getSystem(pBox2d.class);
			if (box == null || !bod.hasParam("box_body") || !bod.hasParam("ref") || 
					!bod.hasParam("ctrl_mob") || geo == null) return;
			int spawning = bod.getInt("ctrl_mob","spawning");
			if (spawning > 0) {
				bod.setInt("ctrl_mob","spawning",(int)(spawning-1));
				return;
			} else if (spawning == 0) {
				bod.setInt("ctrl_mob","spawning",(int)(spawning-1));
				Vector2 pos = bod.getVec("ctrl_mob","spawn_pos");
				float rot = bod.getFlt("ctrl_mob","spawn_rot");
				box.move_body(bod,pos.x,pos.y,rot); 
				return;
			} 
			int spawnid = bod.getInt("ctrl_mob","spawn_id");
			
			if (bod.getInt("ctrl_mob","collision_tmp") > 0) {
				bod.setInt("ctrl_mob","collision_tmp", 
						bod.getInt("ctrl_mob","collision_tmp") - (int)1);
			} 
			if (bod.getBoo("ctrl_mob","activate") && (
					spawnid == 1 || spawnid == 3)) {
				float spawnrot = bod.getFlt("ctrl_mob","spawn_rot");
				float speed = bod.getFlt("ctrl_mob","speed");
				boolean direction = bod.getBoo("ctrl_mob","direction");
				Vector2 m = new Vector2(speed,0).rotateRad(spawnrot + (float)Math.PI / 2f);
				if (!direction) m.scl(-1f); 
				box.accel_body(bod,true,m.x,m.y,speed*2f); 
				box.rot_body_toward(bod,spawnrot,speed/20f,speed/10f); 
			}
			if (bod.getBoo("ctrl_mob","shoot")) {
				if (bod.getInt("ctrl_mob","shoot_counter") < 
						bod.getInt("ctrl_mob","shoot_delay")) {
					int step = 1;
					if (spawnid == 3 || spawnid == 4) step = 2;
					bod.setInt("ctrl_mob","shoot_counter", 
							bod.getInt("ctrl_mob","shoot_counter") + step);
				} else {
					bod.setInt("ctrl_mob","shoot_counter", 0);
					String bullet_par = bod.getStr("ctrl_mob","bullet_par");
					Vector2 pos = bod.getVec("ref","pos");
					float rot = bod.getFlt("ref","rot");
					Vector2 m = new Vector2(180,0).rotateRad(rot).add(pos);
					box.shootBullet(bullet_par,m,rot);
				}
			}
		}};

		pGeom.newControlProp("mob",coordinate,run_ctrl_mob)
		.setFullSync()
		.addData("activate", true)
		.addData("direction", true)
		.addData("collision_tmp", (int)0)
		.addData("speed", 50f)
		.addData("spawning", (int)2)
		.addData("shoot", true)
		.addData("bullet_par", "bullet_mob")
		.addData("shoot_counter", (int)0)
		.addData("shoot_delay", (int)30)
		.addData("spawn_pos", new Vector2())
		.addData("spawn_rot", 0f)
		.addData("spawn_id", (int)0)
		;
		
		/*
		 * SPAWN IDS :
		 * 
		 * 0 : avatar
		 * 1 : move + shoot
		 * 2 : shoot
		 * 3 : move + quick shoot
		 * 4 : quick shoot
		 * 
		 * */

		
		
		

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
		nRun run_ctrl_time = new nRun() { public void run(Object o) { 
			pBody bod = (pBody)o; if (bod == null) return;
			pTime time = bod.space.app.time;
			if (bod.hasParam("matter") && bod.hasParam("ctrl_time")) {
				float strength = bod.getFlt("ctrl_time","strength");
				if (bod.getBoo("ctrl_time","activate") && 
						time.get_tickrate_fact() != strength) {
					time.set_tickrate_fact(strength); } }
		}};

		pGeom.newControlProp("time",coordinate,run_ctrl_time)
		.setFullSync()
		.addData("activate", false)
		.addData("strength", 3f, "min", 0.25f, "max", 4f)
		;

		


		
		
		nRun run_ctrl_bullet = new nRun() { public void run(Object o) { 
			pBody bod = (pBody)o; if (bod == null) return;
			pGeom geo = PlaneApplet.app.getSystem(pGeom.class);
			pBox2d box = PlaneApplet.app.getSystem(pBox2d.class);
			if (geo != null && box != null && 
					bod.hasParam("ref") && bod.hasParam("ctrl_bullet")) {

				if (bod.getBoo("ctrl_bullet","pop")) {
					bod.setInt("ctrl_bullet","shoot_counter", 0);
					String bullet_par = bod.getStr("ctrl_bullet","bullet_par");
					Vector2 pos = bod.getVec("ref","pos");
					float rot = bod.getFlt("ref","rot");
					Vector2 m = new Vector2(150,0).rotateRad(rot).add(pos);
					box.shootBullet(bullet_par,m,rot);
					bod.setBoo("ctrl_bullet","pop", false);
				} 
				else if (bod.getBoo("ctrl_bullet","shoot")) {
					if (bod.getInt("ctrl_bullet","shoot_counter") < 
							bod.getInt("ctrl_bullet","shoot_delay")) {
						bod.setInt("ctrl_bullet","shoot_counter", 
								bod.getInt("ctrl_bullet","shoot_counter") + (int)1);
					} else {
						bod.setInt("ctrl_bullet","shoot_counter", 0);
						String bullet_par = bod.getStr("ctrl_bullet","bullet_par");
						Vector2 pos = bod.getVec("ref","pos");
						float rot = bod.getFlt("ref","rot");
						Vector2 m = new Vector2(180,0).rotateRad(rot).add(pos);
						box.shootBullet(bullet_par,m,rot);
					}
				}

			}
		}};

		pGeom.newControlProp("bullet",coordinate,run_ctrl_bullet,"ref") 
		.setFullSync()
		.addData("pop", false)
		.addData("shoot", false)
		.addData("bullet_par", "bullet_def")
		.addData("shoot_counter", (int)0)
		.addData("shoot_delay", (int)8)
//		.addData("pop_pos", new Vector2(150,0))
//		.addData("pop_rot", 0f, "min", -(float)Math.PI, "max", (float)Math.PI)
		;
		

		
		
		
		
//		nRun run_ctrl_pop = new nRun() { public void run(Object o) { 
//			pBody bod = (pBody)o; if (bod == null) return;
//			pGeom geo = PlaneApplet.app.getSystem(pGeom.class);
//			pBox2d box = PlaneApplet.app.getSystem(pBox2d.class);
//			if (geo != null && bod.hasParam("ref") && bod.hasParam("ctrl_pop")) {
//
//				Vector2 pop_pos = bod.getVec("ctrl_pop","pop_pos");
//				float pop_rot = bod.getFlt("ctrl_pop","pop_rot");
//				Vector2 acc_pos = bod.getVec("ctrl_pop","acc_pos");
//				float acc_rot = bod.getFlt("ctrl_pop","acc_rot");
//				
//				if (bod.getBoo("ctrl_pop","pop")) {
//					
//					String bluep_par = bod.getStr("ctrl_pop","blueprint_par");
//					
//					pParam bluep = null;
//					for (pParam p : bod.space.param_pools.get("blueprint").all()) 
//						if (p.getStr("name").equals(bluep_par)) { bluep = p; break; }
//					
//					if (bluep != null) { 
//						pBody pop = pNodeSpace.new_body(bluep);
//						if (pop == null) return;
//						
//						Vector2 bp = bod.getVec("ref", "pos");
//						Vector2 p = new Vector2(pop_pos.x,pop_pos.y);
//						p.add(bp);
//						pop.setVec("ref", "pos", new Vector2(p.x,p.y));
//						pop.setFlt("ref", "rot", bod.getFlt("ref", "rot") + pop_rot);
//
//						pop.setFlt("ref", "rot", acc_pos.angleRad());
//						
//						pNodeSpace.init_body(pop, bluep);
//						
//						if (box != null) {
//							if (pop.hasParam("ctrl_box")) {
//								pop.setBoo("ctrl_box","accel_move", true);
//								pop.setVec("ctrl_box","accel_dir", acc_pos.x,acc_pos.y);
////								pop.setFlt("ctrl_box","move_strength", acc_pos.len());
////								pop.setFlt("ctrl_box","max_speed", 1000f);
//							}
//						}
//						bod.param("ctrl_pop").setBody("last", pop);
//					} 
//				}
//				bod.setBoo("ctrl_pop","pop", false);
//				if (bod.getBoo("ctrl_pop","throw") && 
//						bod.param("ctrl_pop").getBody("last") != null) {
//					bod.param("ctrl_pop").setBody("last", ""); }
//				bod.setBoo("ctrl_pop","throw", false);
//
//			}
//		}};
//
//		pGeom.newControlProp("pop",coordinate,run_ctrl_pop,"ref") 
//		.setFullSync()
//		.addData("pop", false)
//		.addData("blueprint_par", "")
//		.addData("pop_pos", new Vector2(150,0))
//		.addData("pop_rot", 0f, "min", -(float)Math.PI, "max", (float)Math.PI)
//		.addData("acc_pos", new Vector2(0,0))
//		.addData("acc_rot", 0f, "min", 0f, "max", 0.1f)
//		.addBody("last")
//		.addData("throw", false)
//		.addData("keep", true)
//		.addData("keep_dist", 500f, "min", 0f, "max", 1000f)
//		.addData("keep_pos", 20f, "min", 0f, "max", 20f)
//		.addData("keep_rot", 0.1f, "min", 0f, "max", 0.1f)
//		;
		
		

		nRun func_prop_run = new nRun() { public void run() {
			pStandard stand = arg(0,pStandard.class);
			if (stand == null) return;
			stand.addInitRun(new nRun() {public void run() {
				int i = 0;
				String r = ""+i;
				while (instance.patch.function_props.hasKey(r)) {
					i++; r = ""+i; }
				instance.patch.function_props.put(r,instance);
				instance.setVar("inst_ref",r);
			}});
			stand.addClearRun(new nRun() {public void run() {
				instance.patch.function_props.remove(
						instance.getVar("inst_ref", String.class),instance);
			}});
			stand.append(pTileHead.exec_context);
			stand.openSec()
				.param("keys", new String[]{"reg", "register", "in"}, 
						"filters", new String[]{"out"}) 
				.run(pNode.getRun(pNode.CT.RUNS_ADD_CO_IN), "co_reg")
			.closeSec();
		}};


		pProperty logic = pProperty.newGeneralProperty("logic")
//		.addData("init_func_ref", "")
		.addData("tick_func_ref", "")
		.addData("inst_ref", "")
		.addNodeRun(func_prop_run)
		;

		nRun run_ctrl_func = new nRun() { public void run(Object o) { 
			pBody bod = (pBody)o; if (bod == null) return;
			if (bod.hasParam("logic") && bod.hasParam("ctrl_func")) {
				String func_ref = bod.getStr("logic","tick_func_ref"); 
				String inst_ref = bod.getStr("logic","inst_ref");
				pInstance func = PlaneApplet.app.patch.common_functions.get(func_ref);
				pInstance inst = PlaneApplet.app.patch.function_props.get(inst_ref);
				if (func == null || inst == null) return;
				Object[] script = func.get("get_instruction_script", Object[].class);
				if (script == null) return;
				Object[] ar = new Object[] { bod };
				pFunc.func_script_run(inst, script, ar); 
			}
		}};
		pGeom.newControlProp("func",logic,run_ctrl_func) 
		;
		
		
		
		
		
		
		
		
	}
	

	public pParam getGeom(String name) {
		for (pParam p : app.space.param_pools.get("geom").all())
			if (p.getStr("name").equals(name)) return p;
		return null;
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
		
		nWidgetGroup sec = app.gui.toolbox.addSection("Selected Body", false);
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
		val_do_calc, val_do_ctrl; 
//	public sBoo val_do_limit;
//	public sFlt val_limit_dist;

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

//		val_do_limit = bloc.obtainBoo("val_do_limit", true);
//		val_limit_dist = bloc.obtainFlt("val_limit_dist", 10000f);

//		plane.addEventToolInit(new nRun() { public void run(Object o) {
//			nInterface interf = (nInterface)o;
//		}});

		pFamily.getFamily("aabb_clickable").setClearRun(new nRun() { public void run(Object o) {
			pBody bod = (pBody)o; nRun.runEvents(eventBodyClear, bod); }});
		
		app.addEventToolInit(new nRun() { public void run(Object o) {
			nInterface interf = (nInterface)o;
			if (!app.NET_CTRL) {
//				interf.setContext(bloc);
				interf.cmd_context(bloc.adress);

				interf.set_param("entry_height","0.5");
				interf.add_row();
				interf.add_row_label(10, "");
				
				interf.set_param("entry_height","2");
				interf.add_row();
				interf.add_row_label(1, "");
				interf.add_row_trigg(8, "restart", new nRun() { public void run() { 
					restart_game(); }});
				interf.add_row_label(1, "");

				interf.set_param("entry_height","0.5");
				interf.add_row();
				interf.add_row_label(10, "");
				
			}
		}});
	}
	public void system_load() {

		app.time.addEventTick(tick_run);
		app.time.addEventNetTick(net_tick_run);
		
//		app.view.addDrawable(5, draw_run);
		app.view.addDrawable(20, draw_aabb_run);
		space = app.space;

//		if (!app.RELEASE) 
			tool_setup(false);
		app.addDelayEvent(1,new nRun() { public void run() {
			app.getSystem(pBox2d.class).addDrawable(5, draw_run);
		}});		
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

		app.outputs.put("start_game", new nRun() { public void run() {
			start_game(); }});
		

//		app.addDelayEvent(62, new nRun() { public void run() {			
//			restart_game();
//		}});

	}
	public void select_body(pBody b) {
		if (b == null || b == sel_body) return;
		sel_body = b;
		run_body_select.run();
	}
	public void system_clear() {
		app.time.removeEventTick(tick_run);
		app.time.removeEventNetTick(net_tick_run);
		if (app.getSystem(pBox2d.class) != null) 
			app.getSystem(pBox2d.class).removeDrawable(draw_run);
//		app.view.removeDrawable(draw_run);
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
		interf.add_row_label(6, "");
//		interf.add_row();
//		interf.add_row_switch_boo(4, "do_limit", "val_do_limit");
//		interf.add_row_label(6, "");
//		interf.add_row();
//		interf.add_row_label(4, "limit_dist");
//		interf.add_row_slide_flt(6, 1000f, 20000f, "val_limit_dist");
		
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

	ArrayList<pBody> to_clr = new ArrayList<pBody>();
	
	public void tick(float delta) {
		calc_ref();
		if (val_do_ctrl.get()) {
			for (String ref : control_props.allKey()) {
				for (pBody b : Utl.duplic(space.familyMember("ctrl_"+ref))) {
					control_ticks.get(ref).run(b);
				}
			}
		}
//		if (val_do_limit.get()) {
//			float l = val_limit_dist.get(); 
//			for (pBody b : space.body_pool.temp_all()) {
//				if (b.hasParam("ref") && b.getVec("ref","pos").len() > l) b.clear();
//			}
//		}
	}

	public void net_frame(float delta) { 
		frame(delta);
	}
	public void net_tick(float delta) { 
		calc_ref();
	}
	public void draw() { 
		if (val_do_draw.get())
			for (pBody b : space.familyMember("drawable")) draw_body(app, b);
		
	}
	public void draw_aabb() { 
		if (val_do_click_draw.get())
			for (pBody b : space.familyMember("aabb_clickable")) draw_clic_aabb(b);
		else if (val_do_hover_draw.get())
			for (pBody b : space.familyMember("aabb_clickable")) draw_hover_aabb(b);
		else if (val_do_aabb_draw.get())
			for (pBody b : space.familyMember("aabb")) draw_aabb(b);

		
//		GdxApp.app.drawer.test_light(app.view);
		
	}

	public static Vector2 toRef(pBody b, Vector2 v) {
		if (b == null) return v;
		pParam ref = b.param("ref");
		if (ref == null) return v;
		Vector2 p = new Vector2(v);
		p.rotateRad(ref.getFlt("rot"));
		p.scl(b.getFlt("ref", "scale")); 
		p.add(ref.getVec("pos"));
		return p; }
	
	private void calc_ref() {
		if (val_do_calc.get())
			for (pBody b : space.familyMember("aabb")) calc_info_shape(b);
	}
	
	
	public boolean test_clic(pBody b) {
		Vector2 pos = b.getVec("ref", "aabb_pos");
		Vector2 size = b.getVec("ref", "aabb_size");
		Vector2 m = new Vector2();
		m.set(app.view.mouse_in_view());
		Rectangle aabb = new Rectangle(pos.x, pos.y, size.x, size.y);
		return aabb.contains(m);
	}

	

	public void draw_body(PlaneApplet app, pBody b) {
		if (!b.hasParam("ref") || !b.hasParam("geom")) return;
		draw_geom(app, b, b.param("geom"));
		if (!b.hasParam("hp")) return;
		Vector2 p = b.getVec("ref", "pos");
		int hp = b.getInt("hp", "hp");
		app.text(""+hp, p.x, p.y, 24);
	}

	public static void draw_geom(nDrawer.Drawer app, pBody b, pParam geom) {
		if (geom == null) return;
		ArrayList<Vector2> point = geom.getCollecData("point", Vector2.class);
		if (point == null) return;
		
		ArrayList<Integer> color = geom.getCollecData("color", Integer.class);
		if (color == null || color.size() != point.size()) return;
		
		ArrayList<Integer> faceA = geom.getCollecData("faceA", Integer.class);
		ArrayList<Integer> faceB = geom.getCollecData("faceB", Integer.class);
		ArrayList<Integer> faceC = geom.getCollecData("faceC", Integer.class);
		ArrayList<Float> faceT = geom.getCollecData("faceT", Float.class);
		ArrayList<Integer> faceL = geom.getCollecData("faceL", Integer.class);
		if (faceA == null || faceB == null || faceC == null || 
				faceT == null || faceL == null || 
				faceA.size() != faceB.size() || faceA.size() != faceC.size() || 
				faceA.size() != faceT.size() || faceA.size() != faceL.size()) return;

//		ArrayList<Integer> lineA = geom.getCollecData("lineA", Integer.class);
//		ArrayList<Integer> lineB = geom.getCollecData("lineB", Integer.class);
//		if (lineA == null || lineB == null || 
//				lineA.size() != lineB.size()) return;
		
//		if (graph == null) { 
			Vector2 ps1 = new Vector2();
			Vector2 ps2 = new Vector2();
			Vector2 ps3 = new Vector2();
			for (int i = 0 ; i < faceA.size() ; i++) {
				int p1 = faceA.get(i), p2 = faceB.get(i), p3 = faceC.get(i);
				if (p1 < 0 || p1 >= point.size() || 
						p2 < 0 || p2 >= point.size() || 
						p3 < 0 || p3 >= point.size()) continue;
				ps1.set(toRef(b, point.get(p1)));
				ps2.set(toRef(b, point.get(p2)));
				ps3.set(toRef(b, point.get(p3)));
				app.face(ps1.x,ps1.y,ps2.x,ps2.y,ps3.x,ps3.y, 
						Utl.intToColor(color.get(p1)), 
						Utl.intToColor(color.get(p2)), 
						Utl.intToColor(color.get(p3)));
				app.stroke(Utl.intToColor(faceL.get(i)),faceT.get(i));
				app.noFill();
				app.polygon(ps1,ps2,ps3);
			}
//			for (int i = 0 ; i < lineA.size() ; i++) {
//				int p1 = lineA.get(i), p2 = lineB.get(i);
//				if (p1 < 0 || p1 >= point.size() || p2 < 0 || p2 >= point.size()) continue;
//				app.line(toRef(b, point.get(p1)), toRef(b, point.get(p2)));
//			}
//			return;
//		}
//		boolean fill = graph.getBoo("fill");
//		boolean line = graph.getBoo("line");
//		float thick = graph.getFlt("thick");
//		Color col_fill = Utl.color(
//				graph.getInt("fill_r"), 
//				graph.getInt("fill_g"), 
//				graph.getInt("fill_b"), 
//				graph.getInt("fill_a") );
//		Color col_line = Utl.color(
//				graph.getInt("line_r"), 
//				graph.getInt("line_g"), 
//				graph.getInt("line_b"), 
//				graph.getInt("line_a") );
//		
//		for (int i = 0 ; i < faceA.size() ; i++) {
//			int p1 = faceA.get(i), p2 = faceB.get(i), p3 = faceC.get(i);
//			if (p1 < 0 || p1 >= point.size() || 
//					p2 < 0 || p2 >= point.size() || 
//					p3 < 0 || p3 >= point.size()) continue;
//			if (fill) {
//				app.fill(col_fill);
//				app.noStroke();
//				app.polygon(toRef(b, point.get(p1)), 
//						toRef(b, point.get(p2)), 
//						toRef(b, point.get(p3)));
//			}
//			if (line) {
//				app.stroke(col_line, thick);
//				app.noFill();
//				app.polygon(toRef(b, point.get(p1)), 
//						toRef(b, point.get(p2)), 
//						toRef(b, point.get(p3)));
//			}
//		}
////		if (line) {
////			app.stroke(col_line, thick);
////			app.noFill();
////			for (int i = 0 ; i < lineA.size() ; i++) {
////				int p1 = lineA.get(i), p2 = lineB.get(i);
////				if (p1 < 0 || p1 >= point.size() || p2 < 0 || p2 >= point.size()) continue;
////				app.line(toRef(b, point.get(p1)), toRef(b, point.get(p2)));
////			}
////		} 
		boolean halo = geom.getBoo("halo");
		if (halo && b != null) {
			app.halo(b.getVec("ref", "pos"), 12, 
					Utl.color(255,0,0,0), Utl.color(255,100,100,255));
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
		if (!b.hasParam("ref") || !b.hasParam("geom")) return;
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
			b.setVec("ref", "aabb_pos", pos);
			b.setVec("ref", "aabb_size", size);
		} else {
			b.setVec("ref", "aabb_pos", b.getVec("ref", "pos"));
			b.setVec("ref", "aabb_size", new Vector2());
		}
	}

	public void draw_aabb(pBody b) {
		if (!b.hasParam("ref")) return;
		Vector2 pos = b.getVec("ref", "aabb_pos");
		Vector2 size = b.getVec("ref", "aabb_size");
		app.stroke(255, 0, 255, 127, 2f); 
		app.noFill();
		Vector2 p1 = new Vector2(pos.x+size.x,pos.y);
		Vector2 p2 = new Vector2(pos.x,pos.y+size.y);
		Vector2 p3 = new Vector2(pos.x+size.x,pos.y+size.y);
		app.polygon(pos, p1, p3, p2);
	}

	public void draw_hover_aabb(pBody b) {
		if (!b.hasParam("ref")) return;
		Vector2 pos = b.getVec("ref", "aabb_pos");
		Vector2 size = b.getVec("ref", "aabb_size");
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
		if (!b.hasParam("ref")) return;
		Vector2 pos = b.getVec("ref", "aabb_pos");
		Vector2 size = b.getVec("ref", "aabb_size");
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
	
	
	

	public void start_game() {
		space.start_space();
	}
	
	
	public void game_over() {
		app.time.set_pause(true);
	}
	

	public void restart_game() {
		space.start_space();
//		val_play.set(true);
	}
	


}
