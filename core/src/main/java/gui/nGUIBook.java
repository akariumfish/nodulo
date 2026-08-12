package gui;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.noodle.nodulo.GdxApp;

import aa_nodulo.PlaneApplet;
import app.App;
import data.sBoo;
import data.sFlt;
import data.sInt;
import data.sValueBloc;
import data.sVec;
import util.Utl;
import util.nRun;
import patch.pNode;

public class nGUIBook {


	
	
	

	public static void build_color(nModelBook book) {
		
		float RS = book.RS;
		
		//      -----  BUILDING MODEL  -----
		
		book.newModel("SZ1-1").setSize(1*RS, 1*RS);
		book.newModel("SZ1-2").setSize(1*RS, 2*RS);
		

		book.newModel("CL_def")
		.set_color_background(Utl.color(80,80,80,255))
		.set_color_pressed(Utl.color(20,20,255,255))
		.set_color_hovered(Utl.color(0,0,210,255))
		.set_color_standby(Utl.color(0,0,120,255))
		.set_color_sliderback(Utl.color(50,50,50,255))
		.set_color_outline(Utl.color(200,200,200,255))
		.set_color_outline_selected(Utl.color(200,200,0,255))
		.set_color_shadow(Utl.color(0,0,0,100))
		.set_color_switch_on(Utl.color(0,70,255,255))
		.set_color_switch_off(Utl.color(0,0,40,255))
		.set_color_text(Utl.color(200,255))
		.setFont(18)
		;

		
		book.newModel("CL_release")
		.set_color_background(Utl.color(230))
		.set_color_pressed(Utl.color(20,20,255,255))
		.set_color_hovered(Utl.color(230,230,180,255))
		.set_color_standby(Utl.color(185,202,225))
		.set_color_sliderback(Utl.color(220))
		.set_color_outline(Utl.color(22,20,15,100))
		.set_color_outline_selected(Utl.color(90,50,0,255))
		.set_color_shadow(Utl.color(0,0,0,100))
		.set_color_switch_on(Utl.color(0,170,255,255))
		.set_color_switch_off(Utl.color(150,150,165))
		.set_color_text(Utl.color(0))
		.setFont(18)
		;

		book.newModel("CL_def_VS_back")
		.copyFrom(book.getModel("CL_def"))
		.set_color_background(Utl.color(0))
		.set_color_outline(Utl.color(150))
		;
		book.newModel("CL_release_VS_back")
		.copyFrom(book.getModel("CL_release"))
		.set_color_background(Utl.color(235))
		.set_color_outline(Utl.color(22,20,15,100))
		;

		book.newModel("CL_def_DM_back")
		.copyFrom(book.getModel("CL_def"))
		.set_color_background(Utl.color(70))
		.setOutline(false)
		;
		book.newModel("CL_release_DM_back")
		.copyFrom(book.getModel("CL_release"))
		.setOutline(true)
		;

		book.newModel("CL_def_DM_entry")
		.copyFrom(book.getModel("CL_def"))
		.set_color_pressed(Utl.color(50,255))
		.set_color_hovered(Utl.color(140,255))
		.set_color_standby(Utl.color(90,255))
		;
		book.newModel("CL_release_DM_entry")
		.copyFrom(book.getModel("CL_release"))
		.set_color_pressed(Utl.color(20,20,255,255))
		.set_color_hovered(Utl.color(200,190,10,255))
		.set_color_standby(Utl.color(115,132,160))
		;
		
		book.newModel("CL_def_CW")
		.copyFrom(book.getModel("CL_def"))
		.setOutlineWeight(3)
		;
		book.newModel("CL_release_CW")
		.copyFrom(book.getModel("CL_release"))
		.setOutlineWeight(3)
		;

		book.newModel("CL_def_CW_head")
		.copyFrom(book.getModel("CL_def"))
		.setFont(20)
		.setOutline(true)
		.setOutlineWeight(1)
		.set_color_pressed(Utl.color(10))
		.set_color_hovered(Utl.color(120))
		.set_color_standby(Utl.color(40))
		.set_color_outline(Utl.color(0,0,190))
		;
		book.newModel("CL_release_CW_head")
		.copyFrom(book.getModel("CL_release"))
		.setFont(20)
		.setOutline(true)
		.setOutlineWeight(1)
		.set_color_pressed(Utl.color(120,120,255,255))
		.set_color_hovered(Utl.color(250,230,180))
		.set_color_standby(Utl.color(215,232,255))
		;

		book.newModel("CL_def_FLD")
		.copyFrom(book.getModel("CL_def"))
		.set_color_background(Utl.color(10))
		.set_color_outline(Utl.color(50,50,255))
		.set_color_outline_selected(Utl.color(200,200,0))
		.setOutline(true)
		.setOutlineWeight(2)
		.setField(true)
		;
		book.newModel("CL_release_FLD")
		.copyFrom(book.getModel("CL_release"))
		.set_color_background(Utl.color(210,220,240))
		.set_color_outline(Utl.color(50,50,255))
		.set_color_outline_selected(Utl.color(200,200,0))
		.setOutline(true)
		.setOutlineWeight(2)
		.setField(true)
		.set_color_text(Utl.color(0,0,90))
		;


		book.newModel("CL_def_RT")
		.set_color_hovered(Utl.color(120,255))
		.set_color_standby(Utl.color(70,255))
		.set_color_outline(Utl.color(30,255))
		.set_color_text(Utl.color(205,255))
		.setOutline(true)
		.setOutlineWeight(RS/10f)
		;

		book.newModel("CL_release_RT")
		.copyFrom(book.getModel("CL_release_CW_head"))
		.set_color_standby(Utl.color(255,232,215))
//		.set_color_background(Utl.color(210,220,240))
//		.set_color_hovered(Utl.color(120,255))
//		.set_color_standby(Utl.color(70,255))
//		.set_color_outline(Utl.color(30,255))
//		.set_color_text(Utl.color(205,255))
		.setOutline(true)
		.setOutlineWeight(RS/10f)
		;
		
		
		
		
		

		book.newModel("CL_def_graph")
		.set_color_background(Utl.color(120))
		.set_color_outline(Utl.color(210))
		.setOutlineWeight(6)
		;
		book.newModel("CL_release_graph")
		.set_color_background(Utl.color(0,20))
		.set_color_outline(Utl.color(0,20,150))
		.setOutlineWeight(8)
		;

	}

	public static void build_theme_color(nModelBook book) {
		
		boolean RELEASE = PlaneApplet.RELEASE;
		
//		-----  BASE MODEL  -----
		
		if (RELEASE) {
//			app.gdx.drawer.color_back = new Color(book.getModel("CL_release").color_background);
			book.newModel("ref").copyFrom(book.getModel("CL_release"));
			book.newModel("CL_VS_back").copyFrom(book.getModel("CL_release_VS_back"));
//			app.gdx.drawer.buffer_clear_color = new Color(book.getModel("CL_VS_back").color_background); 
			book.newModel("CL_DM_back").copyFrom(book.getModel("CL_release_DM_back"));
			book.newModel("CL_DM_entry").copyFrom(book.getModel("CL_release_DM_entry"));
			book.newModel("CL_CW").copyFrom(book.getModel("CL_release_CW"));
			book.newModel("CW_head_color").copyFrom(book.getModel("CL_release_CW_head"));
			book.newModel("text_field").copyFrom(book.getModel("CL_release_FLD"));
			book.newModel("CL_graph").copyFrom(book.getModel("CL_release_graph"));
			book.newModel("CL_right_trigg").copyFrom(book.getModel("CL_release_RT"));
		} else {
//			app.gdx.drawer.color_back = Utl.color(70);
			book.newModel("ref").copyFrom(book.getModel("CL_def"));
			book.newModel("CL_VS_back").copyFrom(book.getModel("CL_def_VS_back"));
//			app.gdx.drawer.buffer_clear_color = new Color(book.getModel("CL_VS_back").color_background); 
			book.newModel("CL_DM_back").copyFrom(book.getModel("CL_def_DM_back"));
			book.newModel("CL_DM_entry").copyFrom(book.getModel("CL_def_DM_entry"));
			book.newModel("CL_CW").copyFrom(book.getModel("CL_def_CW"));
			book.newModel("CW_head_color").copyFrom(book.getModel("CL_def_CW_head"));
			book.newModel("text_field").copyFrom(book.getModel("CL_def_FLD"));
			book.newModel("CL_graph").copyFrom(book.getModel("CL_def_graph"));
			book.newModel("CL_right_trigg").copyFrom(book.getModel("CL_def_RT"));
		}
		
//		book.newModel("bp")
//		.copyFrom(book.getModel("ref"))
//		.copySizeFrom(book.getModel("SZ1-1"))
//		.setTrigger()
//		;
	}
	
	public static void build_all_book(nModelBook book) {
		
		float RS = book.RS;

		
		
		//      -----  INTERFACE  -----

		book.newModel("INT_back")
		.copyColorFrom(book.getModel("ref"))
		.setRect(0,0,0,0)
		.setBackground()
		.setBoundChild(true)
		.setBoundParent(true)
		.setStacked(true)
		.setOutline(false)
		.setStack(nAlign.VERTICAL,nAlign.DOWN) // HORIZONTAL VERTICAL RIGHT LEFT UP DOWN
		.setRectOrigin(nAlign.LEFT,nAlign.BOTTOM)
		.setBoundOutspace(0 * RS / 6f)
		.setStackSpacing(RS / 15f)
		.set_color_background(Utl.color(0,0))
		.setDraw(false)
		;
		book.newModel("INT_col_line")
		.copyColorFrom(book.getModel("ref"))
		.setRect(0,0,0,0)
		.setBoundParent(true)
		.setBoundChild(true)
		.setStacked(true)
		.setOutline(false)
		.setBoundOutspace(0)
		.setStackSpacing(RS / 15f)
		.setStack(nAlign.HORIZONTAL, nAlign.RIGHT) // HORIZONTAL VERTICAL RIGHT LEFT UP DOWN
		.set_color_background(Utl.color(0,0))
		.setDraw(false)
		;
		book.newModel("INT_col")
		.copyColorFrom(book.getModel("ref"))
		.setRect(0,0,0,0)
		.setBoundParent(true)
		.setBoundChild(true)
		.setStacked(true)
		.setOutline(false)
		.setBoundOutspace(0)
		.setStackSpacing(RS / 15f)
		.setStack(nAlign.VERTICAL, nAlign.DOWN) // HORIZONTAL VERTICAL RIGHT LEFT UP DOWN
		.set_color_background(Utl.color(0,0))
		.setDraw(false)
		;
		book.newModel("INT_col_back") 
		.copyColorFrom(book.getModel("ref"))
		.setRect(0,0,0,0)
		.setBoundParent(true)
		.setBoundChild(true)
		.setStacked(true)
		.setOutline(false)
		.setBoundOutspace(RS / 10f)
		.setStackSpacing(RS / 15f)
		.setStack(nAlign.VERTICAL, nAlign.DOWN) // HORIZONTAL VERTICAL RIGHT LEFT UP DOWN
		.setOutline(true)
		.setOutlineWeight(RS / 10f)
		.set_color_outline(Utl.color(20))
		.set_color_background(Utl.color(0,0))
		;

		book.newModel("INT_col_head")
		.copyColorFrom(book.getModel("ref"))
		.setSize(RS*10f, RS/3f*2f)
		.setBoundParent(true)
		.setStacked(true)
//		.set_color_pressed(Utl.color(20))
//		.set_color_hovered(Utl.color(120))
//		.set_color_standby(Utl.color(60))
		.setSwitch()
		;

		book.newModel("INT_filler")
		.copyFrom(book.getModel("INT_col"))
		.setSize(RS*2,RS*2)
		.set_color_background(Utl.color(0,0))
		.setDraw(false)
		;

		book.newModel("INT_row")
		.copyFrom(book.getModel("INT_col"))
		.setStackSpacing(RS / 15f)
		.setStack(nAlign.HORIZONTAL, nAlign.RIGHT) // HORIZONTAL VERTICAL RIGHT LEFT UP DOWN
		;

		book.newModel("INT_col_entry")
		.copyColorFrom(book.getModel("ref"))
		.setSize(RS*10f, RS)
		.setBoundParent(true)
		.setStacked(true)
		.setBoundOutspace(0)
		.set_color_background(Utl.color(0,0))
		;

		book.newModel("INT_col_separator")
		.copyFrom(book.getModel("INT_col_entry"))
		.setSY(RS/6f)
		;
		
		
		// bigger size made n demand in Interface.get_row_entry_widget && Interface.get_row_button_widget
		for (int i = 1 ; i <= 40 ; i++) {
			book.newModel("INT_row_entry_"+i)
			.copyColorFrom(book.getModel("ref"))
			.setSize(RS * i / 2f, RS)
			.setBoundParent(true)
			.setStacked(true)
			.setBoundOutspace(0)
			.set_color_background(Utl.color(0,0))
			;
		}
		
		book.newModelGroup("interface", new nModelGroup() { 
			public nWidgetGroup build(nGUI gui) {
				nWidgetGroup g = gui.addWidgetGroup();
				nWidget ref = g.addWidget("ref", gui.addWidget("INT_back"));
				
				return g;
			} 
		} );
		
		//      -----  INFO POP  -----

		book.newModel("IP_ref")
		.set_color_background(Utl.color(100))
		.setSize(RS * 6f, RS)
//		.setHoverableZone(true)
		.setRectOrigin(nAlign.LEFT,nAlign.CENTER) // TOP   BOTTOM
		.setBoundOutspace(0)
		.setStackSpacing(0)
		.setOutline(true)
		.setOutlineWeight(2)
		.set_color_outline(Utl.color(80))
		.setOutlineAfterChild(true)
		;

		book.newModelGroup("info_pop", new nModelGroup() { 
			public nWidgetGroup build(nGUI gui) {
				nWidgetGroup g = gui.addWidgetGroup();
				g.ref = "info_pop";
				
				nWidget ref = g.addWidget("ref", "IP_ref");
				 
				nRun run_tofront = new nRun() { public void run() {
					ref.show(); ref.toFront(); }};

				nRun run_hide = new nRun() { public void run() {
					ref.hide(); }};

				run_hide.run();
				
				nRun run_testfocus = new nRun() { public void run() {
					if (ref.getVisibility()) { 
						ref.setPos(App.ap.input.mouse); 
						if (App.ap.input.mouse.x > GdxApp.WIDTH / 2f + RS) 
							ref.setRectOrigin(nAlign.RIGHT,nAlign.CENTER);
						if (App.ap.input.mouse.x < GdxApp.WIDTH / 2f - RS) 
							ref.setRectOrigin(nAlign.LEFT,nAlign.CENTER);
						boolean over = false;
						Object o = g.object("widg");
						if (o != null && o instanceof nWidget) {
							over = ((nWidget)o).mouseOver;
						}
						over = over || ref.mouseOverZone;
						if (!over) run_hide.run();
					}
				}};
				ref.addEventLogic(run_testfocus);
				
				g.addMetode("pop", new nRun() { public void run(Object o) {
					nWidget v = ((nWidget)o); 
					g.setObject("widg", v);
					ref.setText(v.info_txt);
					ref.force_calc();
//					ref.setPos(v.getX()+v.getSX()/2f, v.getY()+v.getSY()/2f); 
					ref.setSX(App.ap.textWidth(v.info_txt,v.textFont) + RS);
					run_tofront.run(); 
				} });

				return g;
			} 
		} );

		
		
		//      -----  POP WINDOW  -----
		
		book.newModelGroup("pop_window", new nModelGroup() { 
			public nWidgetGroup build(nGUI gui) {
				nWidgetGroup g = gui.addWidgetGroup();
				g.ref = "pop_window";
				
				nWidget ref = g.addWidget("ref", "CW_ref");
				 
				nWidget back = g.addWidget("back", "CW_back")
						.setParent(ref);
				nWidget headback = g.addWidget("headback", "CW_headback")
						.setParent(ref);
				nWidget head = g.addWidget("head", "CW_head")
						.setParent(headback);
				nWidget close = g.addWidget("close", "CW_close")
						.setParent(headback); 
				
				head.setSizeCopyX(back);
				head.setSizeCopyMin(RS);
				head.setSizeCopyIncr(-2*RS);
				
				nInterface interf = head.gui.addInterface()
						.pop(back);

				g.addObject("interf", interf);
				
				nRun run_tofront = new nRun() { public void run() {
					ref.show(); ref.toFront(); }};

				nRun run_hide = new nRun() { public void run() {
					interf.clearCommands();
					ref.hide(); }};

				run_hide.run();
				
				close.addEventTrigger(new nRun() { public void run() {
					run_hide.run(); }});

				nRun run_testfocus = new nRun() { public void run() {
					if (ref.getVisibility()) { 
						if (ref.mouseOverZone) run_tofront.run();
//						if (App.ap.input.mouseLeft.trigClick && !ref.mouseOverZone)
//							run_hide.run();		
					}
				}};
				ref.addEventLogic(run_testfocus);
				
				g.addMetode("pop", new nRun() { public void run(Object o) {
					String v = ((String)o); head.setText(v);
					ref.force_calc();
					ref.setPos(GdxApp.WIDTH / 2f - ref.getSX() / 2f, 
							GdxApp.HEIGHT / 2f + ref.getSY() / 2f); 
					run_tofront.run(); 
				} });

				g.addMetode("close", new nRun() { public void run() {
					run_hide.run();	} });

				g.addMetode("get_interf", new nRun() { public Object get() {
					run_hide.run();
					interf.clearCommands();
					interf.rebuild_from_command_list();
					return interf; } });
				
				return g;
			} 
		} );
		
		
		
		//      -----  COMPLEX WINDOW  -----
		//.copyFrom(book.getModel("CL_CW"))
		book.newModel("CW_ref")
		.setStopHoverChildZone(true)
		.setBoundChild(true)
		.setHoverableZone(true)
		.setStackAxis(nAlign.VERTICAL) // HORIZONTAL   VERTICAL
		.setStackDirection(nAlign.UP) // RIGHT   LEFT   UP   DOWN
		.setRectOrigin(nAlign.LEFT,nAlign.TOP) // TOP   BOTTOM
		.setBoundOutspace(0)
		.setStackSpacing(0)
		.setOutline(true)
		.setOutlineWeight(book.getModel("CL_CW").outlineWeight)
		.set_color_outline(book.getModel("CL_CW").color_outline)
		.setOutlineAfterChild(true)
		;
 
		
		book.newModel("CW_headback")
		.copyFrom(book.getModel("CW_head_color"))
//		.setRect(0,0,0,0)
//		.setBackground()
		.setBoundParent(true)
		.setBoundChild(true)
		.setStacked(true)
		.setStackAxis(nAlign.HORIZONTAL) // HORIZONTAL   VERTICAL
		.setStackDirection(nAlign.RIGHT) // RIGHT   LEFT   UP   DOWN
		.setRectOrigin(nAlign.LEFT,nAlign.BOTTOM) // TOP   BOTTOM
		.setBoundOutspace(0)
		.setStackSpacing(0)
		.setOutline(true)
		.setOutlineWeight(1)
//		.set_color_outline(Utl.color(0,0,190))
		.setOutlineAfterChild(true)
		;
		book.newModel("CW_head")
		.copyFrom(book.getModel("CW_head_color"))
		.setRect(0,0,RS,RS)
		.setGrabbable()
		.setGrabbRoot(true)
		.setBoundParent(true)
		.setStacked(true)
		.setRectOrigin(nAlign.LEFT,nAlign.BOTTOM) // TOP   BOTTOM
		;
		book.newModel("CW_header_button")
		.copyFrom(book.getModel("CW_head_color"))
		.setRect(0,0,RS,RS)
		.setBoundParent(true)
		.setStacked(true)
		.setRectOrigin(nAlign.LEFT,nAlign.BOTTOM) // TOP   BOTTOM
		.setHoverableZone(true)
		;
		book.newModel("CW_header_trigg")
		.copyFrom(book.getModel("CW_header_button"))
		.setTrigger()
		;
		book.newModel("CW_collapse")
		.copyFrom(book.getModel("CW_header_trigg"))
		.setText("_")
		;
		book.newModel("CW_close")
		.copyFrom(book.getModel("CW_header_trigg"))
		.setText("X")
		;
		
		book.newModel("CW_back")
		.setRect(0,0,0,0)
		.setBackground()
		.setBoundParent(true)
		.setBoundChild(true)
		.setStacked(true)
		.setStackAxis(nAlign.VERTICAL) // HORIZONTAL   VERTICAL
		.setStackDirection(nAlign.DOWN) // RIGHT   LEFT   UP   DOWN
		.setRectOrigin(nAlign.LEFT,nAlign.BOTTOM) // TOP   BOTTOM
		.setBoundOutspace(5)
		.setStackSpacing(2)
		;
		
		book.newModel("CW_col_entry")
		.setSize(RS*10f, RS)
		.setBoundParent(true)
		.setStacked(true)
		.setBoundOutspace(0)
		;

		// -----------  WINDOW BAR -----------
		
		book.newModel("taskbar_back")
		.copyFrom(book.getModel("ref"))
		.setBackground()
		.setOutline(true)
		.setDrawstackPriority(true)
		;
		book.newModel("taskbar_ref")
		.copyFrom(book.getModel("ref"))
		.setBoundChild(true)
		.setStackAxis(nAlign.HORIZONTAL) // HORIZONTAL   VERTICAL
		.setStackDirection(nAlign.RIGHT) // RIGHT   LEFT   UP   DOWN
		.setRectOrigin(nAlign.LEFT,nAlign.BOTTOM) // TOP   BOTTOM
		.setBoundOutspace(5)
		.setStackSpacing(5)
		.set_color_background(Utl.color(0, 0))
		;

		book.newModel("taskbar_entry")
		.copyFrom(book.getModel("ref"))
		.setRect(0,0,RS*5,RS)
		.setBoundParent(true)
		.setStacked(true)
		.setFont(22)
		.setSwitch()
		;
		
		book.newModelGroup("complex_window", new nModelGroup() { 
			public nWidgetGroup build(nGUI gui) {
				nWidgetGroup g = gui.addWidgetGroup();
				g.ref = "complex_window";
//				app.log("new complex_window : w:"+g.widgets.size()+" g:"+g.widgetgroups.size());

				nWidget ref = g.addWidget("ref", "CW_ref");
				 
				nWidget back = g.addWidget("back", "CW_back")
						.setParent(ref);
				nWidget headback = g.addWidget("headback", "CW_headback")
						.setParent(ref);
				nWidget head = g.addWidget("head", "CW_head")
						.setParent(headback);
				nWidget collapse = g.addWidget("collapse", "CW_collapse") 
						.setParent(headback);
				nWidget close = g.addWidget("close", "CW_close")
						.setParent(headback); 

				nWidget bar_swtch = App.ap.gui.add_taskbar_entry();
				g.addWidget("bar_swtch", bar_swtch);
				
				close.addEventTrigger(new nRun() { public void run() {
					g.clear(); }});
				
				head.setSizeCopyX(back);
				head.setSizeCopyMin(RS);
				head.setSizeCopyIncr(-2*RS);
				
				nRun run_tofront = new nRun() { public void run() {
					if (g.hasObject("no_tofront")) return;
					for(nWidget n : App.ap.gui.bar_entrys) 
						if (n != bar_swtch) n.setOff();
					bar_swtch.setOn(); 
					ref.show(); ref.toFront(); 
					if (g.hasObject("val_collapse")) 
						g.object("val_collapse", sBoo.class).set(false); 
					if (g.hasObject("event_tofront")) 
						g.object("event_tofront", nRun.class).run();
				}};
				
//				run_tofront.run();

				bar_swtch.addEventSwitchOn(run_tofront);
				bar_swtch.setOn();
				
				nRun run_collapse = new nRun() { public void run() {
					ref.hide(); bar_swtch.setOff(); 
					if (g.hasObject("val_collapse")) 
						g.object("val_collapse", sBoo.class).set(true); 
				}};
				
				collapse.addEventTrigger(run_collapse);
				
				nRun run_testfocus = new nRun() { public void run() {
					if (ref.getVisibility() && 
							ref.mouseOverZone && 
							App.ap.input.mouseLeft.trigClick) {
						run_tofront.run(); }  }};
				ref.addEventLogic(run_testfocus);
				
				g.addEventClear(new nRun() { public void run() {
					App.ap.gui.remove_taskbar_entry(bar_swtch); }});

				g.addMetode("run_tofront", run_tofront);
				g.addMetode("run_collapse", run_collapse);
				g.addMetode("init_pos", new nRun() { public void run() {
					head.setPos(book.getNewWindowPos()); 
					run_tofront.run(); } });
				
				g.addMetode("set_title", new nRun() { public void run(Object o) {
					String v = ((String)o);
					bar_swtch.setText(v);
					head.setText(v); }});
				
				g.addMetode("set_tofront_event", new nRun() { public void run(Object o) {
					nRun v = ((nRun)o);
					if (g.hasObject("event_tofront")) g.setObject("event_tofront", v);
					else g.addObject("event_tofront", v);
				}});

				g.addMetode("no_link_to_bloc", new nRun() { public void run() {
					
					ref.setPos(book.getNewWindowPos());
					
				}});
				
				g.addMetode("link_window_to_bloc", new nRun() {
					public void run(Object o) {
						sValueBloc v = ((sValueBloc)o);
						
						sVec val_pos = (v.getValue("val_pos") == null) ? 
								v.obtainVec("val_pos", book.getNewWindowPos()) :
								v.obtainVec("val_pos");
						ref.setLink(val_pos);
						g.addObject("val_pos", val_pos);

						sBoo val_collapse = v.obtainBoo("val_collapse", false);
						g.addObject("val_collapse", val_collapse);
						if (val_collapse.get()) run_collapse.run(); 
						else run_tofront.run();
						
						val_collapse.addEventChangeThisFrame(
								new nRun() { public void run() {
							if (val_collapse.get()) run_collapse.run(); 
							else run_tofront.run(); }});
						
//						sInt val_stack_index = v.obtainInt("val_stack_index", 
//								ref.getSiblingIndex());
//						ref.setStackIndexLink(val_stack_index);
//						g.addObject("val_stack_index", val_stack_index);
						
						close.addEventTrigger(new nRun() { public void run() {
							v.clear(); }});
						v.addEventDelete(new nRun() { public void run() {
							g.clear(); }});
					}
				});
				
				return g;
			} 
		} );
		
		
		
		
		
		
		
		
		
		//      -----  RESIZABLE WINDOW  -----

		book.newModel("RW_space")
		.setRect(0,RS,RS*15f,RS*10f)
//		.set_color_background(Utl.color(0, 0, 0, 0))
		.set_color_outline(Utl.color(100,150,100))
		.set_color_outline_selected(Utl.color(200,200,0))
		.setOutline(true)
		.setOutlineWeight(RS / 6f)
		.setOutlineAfterChild(true)
		.setBackground()
		.setMask(true)
		.setBoundParent(true)
		.setStacked(true)
		;
		
		book.newModel("RW_corner")
		.copyFrom(book.getModel("ref"))
		.setSize(2*RS/3,2*RS/3)
		.setGrabbable()
		.set_color_pressed(Utl.color(200,255,200,220))
		.set_color_hovered(Utl.color(150,210,150,220))
		.set_color_standby(Utl.color(100,150,100,220))
		;
		
		book.newModelGroup("resizable_window", new nModelGroup() { 
			public nWidgetGroup build(nGUI gui) {
				nWidgetGroup g = gui.addWidgetGroup("complex_window");

				nWidget ref = g.get("ref");
				nWidget back = g.get("back");
				
				back.setBoundOutspace(0).setStackSpacing(0);

				nWidget space = g.addWidget("space", gui.addWidget("RW_space")
						.setParent(back)
						);
				
				nWidget cornerTR = g.addWidget("cornerTR", gui.addWidget("RW_corner")
						.setParent(back)
						.setPos(space.getLocalSX(), space.getLocalSY())
						.setRectOrigin(nAlign.RIGHT, nAlign.TOP)
						.asWidget()
						);
				nWidget cornerTL = g.addWidget("cornerTL", gui.addWidget("RW_corner")
						.setParent(back)
						.setPos(0, space.getLocalSY())
						.setRectOrigin(nAlign.LEFT, nAlign.TOP)
						.asWidget()
						);
				nWidget cornerDR = g.addWidget("cornerDR", gui.addWidget("RW_corner")
						.setParent(back)
						.setPos(space.getLocalSX(), 0)
						.setRectOrigin(nAlign.RIGHT, nAlign.BOTTOM)
						.asWidget()
						);
				nWidget cornerDL = g.addWidget("cornerDL", gui.addWidget("RW_corner")
						.setParent(back)
						.setPos(0, 0)
						.setRectOrigin(nAlign.LEFT, nAlign.BOTTOM)
						.asWidget()
						);

				cornerTR.addEventDrag(new nRun() { public void run() {
					ref.addPos(0, cornerTR.getLocalY() - space.getLocalSY());
					cornerDR.setPX(cornerTR.getLocalX());
					cornerTL.setPY(cornerTR.getLocalY());
					cornerTR.calc_grabbing_origin();
					space.setSize(cornerTR.getLocalX() - cornerTL.getLocalX(), 
							cornerTR.getLocalY() - cornerDR.getLocalY());
					Object vcp = g.object("val_view_size");
					if (vcp != null) 
						((sVec)vcp).set(space.getLocalSX(), space.getLocalSY());
				}});
				cornerTL.addEventDrag(new nRun() { public void run() {
					ref.addPos(cornerTL.getLocalX(), 
							cornerTL.getLocalY() - space.getLocalSY());
					cornerTR.addPos(-cornerTL.getLocalX(), 0);
					cornerDL.addPos(-cornerTL.getLocalX(), 0);
					cornerDR.addPos(-cornerTL.getLocalX(), 0);
					cornerTL.addPos(-cornerTL.getLocalX(), 0);
					cornerDL.setPX(cornerTL.getLocalX());
					cornerTR.setPY(cornerTL.getLocalY());
					cornerTL.calc_grabbing_origin();
					space.setSize(cornerTR.getLocalX() - cornerTL.getLocalX(), 
							cornerTR.getLocalY() - cornerDR.getLocalY());
					Object vcp = g.object("val_view_size");
					if (vcp != null) 
						((sVec)vcp).set(space.getLocalSX(), space.getLocalSY());
				}});
				cornerDR.addEventDrag(new nRun() { public void run() {
					cornerTL.addPos(0, -cornerDR.getLocalY());
					cornerDL.addPos(0, -cornerDR.getLocalY());
					cornerTR.addPos(0, -cornerDR.getLocalY());
					cornerDR.addPos(0, -cornerDR.getLocalY());
					cornerTR.setPX(cornerDR.getLocalX());
					cornerDL.setPY(cornerDR.getLocalY());
					cornerDR.calc_grabbing_origin();
					space.setSize(cornerTR.getLocalX() - cornerTL.getLocalX(), 
							cornerTR.getLocalY() - cornerDR.getLocalY());
					Object vcp = g.object("val_view_size");
					if (vcp != null) 
						((sVec)vcp).set(space.getLocalSX(), space.getLocalSY());
				}});
				cornerDL.addEventDrag(new nRun() { public void run() {
					ref.addPos(cornerDL.getLocalX(), 0);
					cornerTR.addPos(-cornerDL.getLocalX(), -cornerDL.getLocalY());
					cornerTL.addPos(-cornerDL.getLocalX(), -cornerDL.getLocalY());
					cornerDR.addPos(-cornerDL.getLocalX(), -cornerDL.getLocalY());
					cornerDL.setPos(0,0);
					cornerTL.setPX(cornerDL.getLocalX());
					cornerDR.setPY(cornerDL.getLocalY());
					cornerDL.calc_grabbing_origin();
					space.setSize(cornerTR.getLocalX() - cornerTL.getLocalX(), 
							cornerTR.getLocalY() - cornerDR.getLocalY());
					Object vcp = g.object("val_view_size");
					if (vcp != null) 
						((sVec)vcp).set(space.getLocalSX(), space.getLocalSY());
				}});

				g.addMetode("set_size", new nRun() {
					public void run(Object o) {
						Vector2 v = ((Vector2)o);
						space.setSize(v.x, v.y);
						cornerTR.setPos(space.getLocalSX(), space.getLocalSY());
						cornerTL.setPos(0, space.getLocalSY());
						cornerDR.setPos(space.getLocalSX(), 0);
						if (g.hasObject("val_view_size")) 
							g.object("val_view_size", sVec.class).set(v.x, v.y);
					}
				});
				
				g.addMetode("link_resize_to_bloc", new nRun() {
					public void run(Object o) {
						sValueBloc v = ((sValueBloc)o);
						
						sVec val_size = v.obtainVec("val_view_size", 
								new Vector2(space.getLocalSX(), space.getLocalSY()));
						g.addObject("val_view_size", val_size);
						nRun run_valsize_ch = 
								new nRun() { public void run() {
							space.setSize(val_size.get().x, val_size.get().y);
							cornerTR.setPos(space.getLocalSX(), space.getLocalSY());
							cornerTL.setPos(0, space.getLocalSY());
							cornerDR.setPos(space.getLocalSX(), 0);
						}};
						run_valsize_ch.run();
						val_size.addEventChangeThisFrame(run_valsize_ch);
					}
				});
				
				
				return g;
			} 
		} );
		
		
		

		//      -----  VIEWSPACE  -----
		
		book.newModel("VP_ref")
		.set_color_background(Utl.color(0, 0, 0, 0))
		.setWarp(true)
		;
		book.newModel("VP_background")
//		.set_color_background(Utl.color(0, 0, 0, 255))
		.set_color_background(book.getModel("CL_VS_back").color_background) 
		.setBackground()
		.setHoverableZone(true)
		;
		book.newModel("VP_frontref")
		.set_color_background(Utl.color(0, 0, 0, 0))
		.setBoundChild(true)
		.setStack(nAlign.VERTICAL, nAlign.UP) //HORIZONTAL VERTICAL RIGHT LEFT UP DOWN
		.setStackSpacing(RS/2f)
		.setBoundOutspace(0)
		;

		book.newModel("VP_fx")
//		.setRect(0,0,RS*15f,RS*10f)
		.set_color_background(Utl.color(0, 0, 0, 0))
		.setBoundParent(true)
		.setBoundChild(true)
		.setBoundOutspace(0f)
		.setStackSpacing(0f)
		.setPassif()
		.setDraw(false)
		;
		
		book.newModel("VP_center", new nModel() { public nWidget custom_init(nWidget w) { 
			w.setCustomDrawer(new nDrawable() { public void drawing() {
				App app = App.ap;
				int line_nb = 20;
				int line_sp = (int)pNode.BRIC_GRID_SIZE * 20;

				float scalefact = 1f;///w.globalscale;
				app.stroke(book.getModel("CL_VS_back").color_outline, 1f * scalefact);
				
				for (int i = 0 ; i < line_nb ; i++)
					for (int j = 0 ; j < line_nb ; j++) {
						if (j%2 == 0) app.stroke(book.getModel("CL_VS_back").color_outline, 
								0.5f * scalefact);
						else app.stroke(book.getModel("CL_VS_back").color_outline, 
								1f * scalefact);
						
						app.line(line_sp * line_nb, j*line_sp,
								-line_sp * line_nb, j*line_sp);
						app.line(line_sp * line_nb, -j*line_sp,
								-line_sp * line_nb, -j*line_sp);
						app.line(i*line_sp, line_sp * line_nb,
								i*line_sp, -line_sp * line_nb);
						app.line(-i*line_sp, line_sp * line_nb,
								-i*line_sp, -line_sp * line_nb);
					}

				app.stroke(140, 0, 0, 255, 5f/w.globalscale);

				app.line(line_sp * line_nb, 0,
						-line_sp * line_nb, 0);
				
				app.stroke(0, 140, 0, 255, 5f/w.globalscale);
				
				app.line(0, line_sp * line_nb,
						0, -line_sp * line_nb);

				app.fill(180,0,0,255);
				app.noStroke();
				app.rect(-6/w.globalscale, -6/w.globalscale, 12/w.globalscale, 12/w.globalscale);

			}});
			return w; 
		}})
//		.set_color_background(Utl.color(255,0,0,255))
//		.setRect(0,0,20,20)
		;
		
		book.newModel("VP_draw")
		;
		
		book.newModelGroup("viewspace", new nModelGroup() { 
			public nWidgetGroup build(nGUI gui) {
				nWidgetGroup g = gui.addWidgetGroup("resizable_window");
				
				nWidget back = g.get("back");
				nWidget space = g.get("space");

				nWidget wallpaper = g.addWidget("wallpaper", 
						gui.addWidget("CW_header_button"));
				wallpaper.setSwitchState(false).setSwitch().setText("F")
				.set_color_pressed(Utl.color(80))
				.set_color_hovered(Utl.color(120))
				.set_color_standby(Utl.color(40));

				nWidget grid = g.addWidget("grid", 
						gui.addWidget("CW_header_button"));
				grid.setSwitchState(true).setSwitch().setText("G")
				.set_color_pressed(Utl.color(80))
				.set_color_hovered(Utl.color(120))
				.set_color_standby(Utl.color(40));

				nWidget border = g.addWidget("border", 
						gui.addWidget("CW_header_button"));
				border.setSwitchState(true).setSwitch().setText("B")
				.set_color_pressed(Utl.color(80))
				.set_color_hovered(Utl.color(120))
				.set_color_standby(Utl.color(40));

				g.get("head").setSizeCopyIncr(-4*RS);

				g.get("collapse").clearParent();
//				g.get("close").clearParent();
				border.setParent(g.get("headback"));
				grid.setParent(g.get("headback"));
				wallpaper.setParent(g.get("headback"));
				g.get("collapse").setParent(g.get("headback"));
//				g.get("close").setParent(g.get("headback"));
				
				nRun run_border_view = new nRun() { public void run() {
					if (border.isOn()) {
						g.get("cornerTL").show();
						g.get("cornerTR").show();
						g.get("cornerDL").show();
						g.get("cornerDR").show();
						space.set_color_outline(Utl.color(100,150,100))
						.setOutlineWeight(RS / 6f);
					} else {
						g.get("cornerTL").hide();
						g.get("cornerTR").hide();
						g.get("cornerDL").hide();
						g.get("cornerDR").hide();
						space.set_color_outline(Utl.color(0,0,190))
						.setOutlineWeight(RS / 15f);
					}
				}};
				border.addEventSwitch(run_border_view);
				
//				back.hide();
				
				g.metode("set_size", new Vector2(800,600));

				nWidget backrender = g.addWidget("backrender", gui.addWidget("VP_fx")
						.setParent(space)
						);
				
				nWidget fx = g.addWidget("fx", gui.addWidget("VP_fx")
						.setParent(backrender)
						);
				
				nWidget bg = g.addWidget("background", gui.addWidget("VP_background")
						.setParent(fx)
						);

				nWidget backref = g.addWidget("backref", gui.addWidget("VP_ref")
						.setParent(fx)
						);
				nWidget center = g.addWidget("center", gui.addWidget("VP_center")
						.setParent(backref)
						);
				
				nWidget draw = g.addWidget("draw", gui.addWidget("VP_draw")
						.setParent(backref)
						);
				
				nWidget frontref = g.addWidget("frontref", gui.addWidget("VP_frontref")
						.setParent(space)
						);

				nRun run_grid_view = new nRun() { public void run() {
					if (grid.isOn()) center.show(); else center.hide(); }};
				grid.addEventSwitch(run_grid_view);

				Vector2 center_ratio = new Vector2(0.5f,0.5f);
				g.addObject("center_ratio", center_ratio);
				
				
				nRun run_drag = new nRun() { public void run() {
					Vector2 center_ratio = g.object("center_ratio", Vector2.class);
					bg.setSize(space.getLocalSX(), space.getLocalSY());
					backref.setSize(space.getLocalSX(), space.getLocalSY());
					backref.setPos(space.getLocalSX() * center_ratio.x, 
							space.getLocalSY() * center_ratio.y);
				}};

				g.get("cornerTL").addEventDrag(run_drag);
				g.get("cornerTR").addEventDrag(run_drag);
				g.get("cornerDL").addEventDrag(run_drag);
				g.get("cornerDR").addEventDrag(run_drag);
				
				run_drag.run();
				
				g.addMetode("event_corner_drag", run_drag);
				
				if (g.hasObject("val_view_size")) 
					g.object("val_view_size", sVec.class).addEventChangeThisFrame(run_drag);
				
				Vector2 cam_pos = new Vector2();
				g.addObject("cam_pos", cam_pos);
				float cam_scale = 1.0f;
				g.addObject("cam_scale", cam_scale);
				float cam_rot = 0.0f;
				g.addObject("cam_rot", cam_rot);
				g.addObject("cam_grabbed", false);
				Vector2 cam_grab_ref = new Vector2();
				g.addObject("cam_grab_ref", cam_grab_ref);
				
				nRun r = new nRun() { public void run() {
					float SCROLL_FCT = 1.1f;
					boolean isGrabbed = (boolean)g.object("cam_grabbed");
					if (bg.mouseOverZone) {
						if (App.ap.input.mouseWheelUp) {
							float s = (float)g.object("cam_scale");
							s *= SCROLL_FCT;
							g.setObject("cam_scale", s);
							backref.setWarpScale(s);
							Object vcs = g.object("val_cam_scale");
							if (vcs != null) ((sFlt)vcs).set(s);
							g.metode("run_tofront");
						}
						if (App.ap.input.mouseWheelDown) {
							float s = (float)g.object("cam_scale");
							s /= SCROLL_FCT;
							g.setObject("cam_scale", s);
							backref.setWarpScale(s);
							Object vcs = g.object("val_cam_scale");
							if (vcs != null) ((sFlt)vcs).set(s);
							g.metode("run_tofront");
						}
						if (App.ap.input.mouseLeft.trigClick) {
							g.metode("run_tofront");
						}
					} 
					if (bg.mouseOverZone || backref.mouseOverChildZone) {
						if (!isGrabbed && App.ap.input.mouseCenter.trigClick) {
							Vector2 cp = (Vector2)g.object("cam_pos");
							g.setObject("cam_grab_ref", 
									new Vector2(gui.mouse_vec.x, gui.mouse_vec.y));
							g.setObject("cam_grabbed", true);
							isGrabbed = true;
							g.metode("run_tofront");
						}
					}
					if (isGrabbed) {
						if (App.ap.input.mouseCenter.state) {
							Vector2 cp = (Vector2)g.object("cam_pos");
							Vector2 gr = (Vector2)g.object("cam_grab_ref");
							float s = (float)g.object("cam_scale");
							float nx = cp.x + (gui.mouse_vec.x - gr.x)/s, 
									ny = cp.y + (gui.mouse_vec.y - gr.y)/s;
							g.setObject("cam_grab_ref", 
									new Vector2(gui.mouse_vec.x, gui.mouse_vec.y));
							g.setObject("cam_pos", new Vector2(nx, ny));
							backref.setWarpTranslate(nx, ny);
							Object vcp = g.object("val_cam_pos");
							if (vcp != null) ((sVec)vcp).set(nx, ny);
						}
						if (!App.ap.input.mouseCenter.state) {
							g.setObject("cam_grabbed", false);
							isGrabbed = false;
						}
					}
				}};
				
				bg.addEventLogic(r);
				

				nRun run_freeview = new nRun() { public void run() {
					if (wallpaper.isOn()) {
						Vector2 p = new Vector2(App.ap.gui.freeview.x, 
								App.ap.gui.freeview.y + 
								App.ap.gui.freeview.height);
						Vector2 s = new Vector2(
								App.ap.gui.freeview.width, 
								App.ap.gui.freeview.height - RS);
						sVec val_pos = g.object("val_pos", sVec.class);
						if (val_pos.x() != p.x || val_pos.y() != p.y) val_pos.set(p.x,p.y);
						g.metode("set_size", s);
						g.metode("event_corner_drag");
						g.get("ref").toBack();
					}
				}};
				
				nRun run_wallpaper = new nRun() { public void run() {
					if (wallpaper.isOn()) {
						g.addObject("no_tofront", "");
						sVec val_pos = g.object("val_pos", sVec.class);
						Vector2 old_pos = new Vector2(val_pos.get());
						g.addObject("old_pos", old_pos);
//						val_pos.set(0f,App.ap.gdx.getscreenheight() - 4f*RS/3f);
						sVec val_view_size = g.object("val_view_size", sVec.class);
						Vector2 old_size = new Vector2(val_view_size.get());
						g.addObject("old_size", old_size);
//						g.metode("set_size", new Vector2(App.ap.gdx.getscreenwidth(), 
//								App.ap.gdx.getscreenheight() - 7f*RS/3f));
//						g.metode("event_corner_drag");
						App.ap.gui.addFreeviewEvent(run_freeview);
						sBoo val_border = g.object("val_border", sBoo.class);
						val_border.set(false);
						border.hide();
						g.get("collapse").hide();
						g.get("head").setSizeCopyIncr(-2*RS);
						g.get("ref").toBack();
						space.setOutline(false);
						g.get("ref").setOutline(false);
					} else {
						if (g.hasObject("no_tofront")) 
							g.removeObject("no_tofront");
						border.show();
						g.get("collapse").show();
						g.get("head").setSizeCopyIncr(-4*RS);
						space.setOutline(true);
						g.get("ref").setOutline(true);

						App.ap.gui.removeFreeviewEvent(run_freeview);
						
						if (g.hasObject("old_pos")) {
							Vector2 old_pos = g.object("old_pos", Vector2.class);
							sVec val_pos = g.object("val_pos", sVec.class);
							val_pos.set(old_pos);
							g.removeObject("old_pos");
						}
						if (g.hasObject("old_size")) {
							Vector2 old_size = g.object("old_size", Vector2.class);
							g.metode("set_size", new Vector2(old_size));
							g.removeObject("old_size");
						}
					}
					g.metode("event_corner_drag");
				}};
				wallpaper.addEventSwitch(run_wallpaper);
				
				g.addMetode("add_widget_to_view", new nRun() {
					public void run(Object o) {
						nWidget v = ((nWidget)o);
						v.setParent(backref);
					}
				});
				g.addMetode("add_widget_to_front", new nRun() {
					public void run(Object o) {
						nWidget v = ((nWidget)o);
						v.setParent(frontref)
						.setStacked(true)
						.setBoundParent(true)
						;
					}
				});

				g.addMetode("add_cam_pos", new nRun() {
					public void run(Object o) {
						Vector2 v = ((Vector2)o);
						Object vcp = g.object("val_cam_pos");
						if (vcp != null) ((sVec)vcp).add(v);
						g.setObject("cam_pos", new Vector2(((sVec)vcp).get()));
						backref.setWarpTranslate(((sVec)vcp).get()); 
						g.get("ref").force_calc_child();
					}
				});

				g.addMetode("set_cam_pos", new nRun() {
					public void run(Object o) {
						Vector2 v = ((Vector2)o);
						Object vcp = g.object("val_cam_pos");
						if (vcp != null) ((sVec)vcp).set(v);
						g.setObject("cam_pos", new Vector2(v));
						backref.setWarpTranslate(v); 
						g.get("ref").force_calc_child();
					}
				});

				g.addMetode("set_cam_scale", new nRun() {
					public void run(Object o) {
						float v = ((float)o);
						Object vcp = g.object("val_cam_scale");
						if (vcp != null) ((sFlt)vcp).set(v);
						g.setObject("cam_scale", v);
						backref.setWarpScale(v); 
						g.get("ref").force_calc_child();
					}
				});
				
				g.addMetode("set_cam_rot", new nRun() {
					public void run(Object o) {
						float v = ((float)o);
						Object vcp = g.object("val_cam_rot");
						if (vcp != null) ((sFlt)vcp).set(v);
						g.setObject("cam_rot", v);
						backref.setWarpRot(v); 
						g.get("ref").force_calc_child();
					}
				});

				g.addMetode("set_center_ratio", new nRun() { public void run(Object o) {
					Vector2 v = ((Vector2)o);
					g.setObject("center_ratio", v);
					g.metode("event_corner_drag");
				}});
				
				g.addMetode("link_to_bloc", new nRun() {
					public void run(Object o) {
						sValueBloc v = ((sValueBloc)o);
						
						g.metode("link_window_to_bloc", v);
						g.metode("link_resize_to_bloc", v);
						run_drag.run();
						
						sVec val_cam_pos = v.obtainVec("val_cam_pos", 
								(Vector2)g.object("cam_pos"));
						sFlt val_cam_scale = v.obtainFlt("val_cam_scale", 
								(float)g.object("cam_scale"));
						sFlt val_cam_rot = v.obtainFlt("val_cam_rot", 
								(float)g.object("cam_rot"));
						
						g.addObject("val_cam_pos", val_cam_pos);
						g.addObject("val_cam_scale", val_cam_scale);
						g.addObject("val_cam_rot", val_cam_rot);
						
						nRun run_cam_pos_change = new nRun() { public void run() {
							g.setObject("cam_pos", new Vector2(val_cam_pos.get()));
							backref.setWarpTranslate(val_cam_pos.get()); }};
						nRun run_cam_scale_change = new nRun() { public void run() {
							g.setObject("cam_scale", val_cam_scale.get());
							backref.setWarpScale(val_cam_scale.get()); }};
						nRun run_cam_rot_change = new nRun() { public void run() {
							g.setObject("cam_rot", val_cam_rot.get());
							backref.setWarpRot(val_cam_rot.get()); }};
//						val_cam_pos.addEventChangeThisFrame(run_cam_pos_change);
//						val_cam_scale.addEventChangeThisFrame(run_cam_scale_change);
						val_cam_pos.addEventChangeLastFrame(run_cam_pos_change);
						val_cam_scale.addEventChangeLastFrame(run_cam_scale_change);
						val_cam_rot.addEventChangeLastFrame(run_cam_rot_change);
						
						g.setObject("cam_pos", new Vector2(val_cam_pos.get()));
						backref.setWarpTranslate(val_cam_pos.get());
						g.setObject("cam_scale", val_cam_scale.get());
						backref.setWarpScale(val_cam_scale.get());
						g.setObject("cam_rot", val_cam_rot.get());
						backref.setWarpRot(val_cam_rot.get());
						
						nWidget close = g.get("close");
						if (close != null) 
							close.addEventTrigger(new nRun() { public void run() {
								v.clear(); }});

						v.addEventDelete(new nRun() { public void run() {
							g.clear(); }});
						
						sBoo val_border = v.obtainBoo("val_border", true);
						g.addObject("val_border", val_border);
						border.setLink(val_border);
						run_border_view.run();
						sBoo val_grid = v.obtainBoo("val_grid", true);
						g.addObject("val_grid", val_grid);
						grid.setLink(val_grid);
						run_grid_view.run();
						sBoo val_wallp = v.obtainBoo("val_wallp", false);
						g.addObject("val_wallp", val_wallp);
						wallpaper.setLink(val_wallp);
						run_wallpaper.run();
					}
				});
				
				return g;
			} 
		} );


		
		
		
		
		
		//      -----  VIEWSPACE TOOL  -----

		book.newModel("VT_ref")
		.setBoundChild(true)
		.setHoverableZone(true)
		.setStackAxis(nAlign.HORIZONTAL) // HORIZONTAL   VERTICAL
		.setStackDirection(nAlign.RIGHT) // RIGHT   LEFT   UP   DOWN
//		.setRectOrigin(nAlign.LEFT,nAlign.TOP) // TOP   BOTTOM
		.setBoundOutspace(0)
		.setStackSpacing(0)
		.setOutline(true)
		.setOutlineWeight(1)
		.set_color_outline(Utl.color(0,0,190))
		.setOutlineAfterChild(true)
		;
		
		book.newModel("VT_open")
		.setRect(0,0,RS,RS)
		.setBoundParent(false)
		.setStacked(true)
		.setSwitch()
		.setText("<T")
		.setHoverableZone(true)
		;
		
		book.newModel("VT_back")
		.setRect(0,0,0,0)
		.setBackground()
		.setBoundParent(true)
		.setBoundChild(true)
		.setStacked(true)
		.setStackAxis(nAlign.VERTICAL) // HORIZONTAL   VERTICAL
		.setStackDirection(nAlign.DOWN) // RIGHT   LEFT   UP   DOWN
		.setRectOrigin(nAlign.LEFT,nAlign.BOTTOM) // TOP   BOTTOM
		.setBoundOutspace(2)
		.setStackSpacing(0)
		.setHoverableZone(true)
		;
		
		book.newModelGroup("viewspace_tool", new nModelGroup() { 
			public nWidgetGroup build(nGUI gui) {
				nWidgetGroup g = gui.addWidgetGroup();

				nWidget ref = g.addWidget("ref", "VT_ref");
				 
				nWidget back = g.addWidget("back", "VT_back")
						.setParent(ref);
				nWidget open = g.addWidget("open", "VT_open")
						.setParent(ref); 

				open.setSizeCopyY(back);
				open.setSizeCopyMin(2f*RS); 
				
				nRun run_open = new nRun() { public void run() {
					back.show(); open.setSwitchState(true);
					if (g.hasObject("val_tool_open")) 
						g.object("val_tool_open", sBoo.class).set(true); }};
				nRun run_close = new nRun() { public void run() {
					back.hide(); open.setSwitchState(false);
					if (g.hasObject("val_tool_open")) 
						g.object("val_tool_open", sBoo.class).set(false); }};
				
				open.addEventSwitchOn(run_open);
				open.addEventSwitchOff(run_close);
				
				nInterface interf = open.gui.addInterface()
						.pop(back);
				g.addObject("interf",interf);
				
				
				nRun run_frame = new nRun() { public void run() {
					if (!g.hasObject("val_autohide") || 
							(g.hasObject("val_autohide") && 
							!g.object("val_autohide", sBoo.class).get())) {
						if (open.isOn()) {
							if (back.mouseOverChildZone || open.mouseOverChildZone) open.show();
							else open.hide();
						} else open.show();
					} else if (g.hasObject("val_autohide") && 
							g.object("val_autohide", sBoo.class).get() && 
							g.hasObject("viewspace")) {
						open.hide();
						nWidgetGroup v = g.object("viewspace", nWidgetGroup.class);
						if (v.get("ref").mouseOverChildZone) {
							Vector2 m = new Vector2(App.ap.input.mouse).sub(ref.getPos());
							if (open.isOn() && !back.mouseOverChildZone) run_close.run(); 
							else if (!open.isOn() && m.len() < RS * 4f) run_open.run();
						} else run_close.run(); 
					}
				}};
				App.ap.addRunFrameStart(run_frame);

				g.addEventClear(new nRun() { public void run() {
					App.ap.removeRunFrameStart(run_frame); }});
				
				g.addMetode("get_interf", new nRun() {
					public Object get() { return interf; } });
				
				g.addMetode("add_to_viewspace_front", new nRun() {
					public void run(Object o) {
						nWidgetGroup v = ((nWidgetGroup)o);
						ref.setParent(v.get("frontref"));
						int c = 0;
						while (v.hasGroup("viewspace_tool_"+c)) c++;
						v.addWidgetGroup("viewspace_tool_"+c, g);
						g.addObject("viewspace", v);
						if (g.hasObject("val_tool_open")) {
							if (g.object("val_tool_open", sBoo.class).get()) run_open.run(); 
							else run_close.run(); 
						} else {
							run_open.run(); 
						}
					}
				});

				g.addMetode("collapse", new nRun() { public void run() {
					run_close.run(); }});
				
				g.addMetode("set_pop_up", new nRun() { public void run() {
					ref.setStackAxis(nAlign.VERTICAL) // HORIZONTAL   VERTICAL
					.setStackDirection(nAlign.UP) // RIGHT   LEFT   UP   DOWN
					;
					open.setSY(2f*RS/3f);
					open.stopSizeCopy();
					open.setSizeCopyX(back);
					open.setSizeCopyMin(RS);
				}});

				g.addMetode("set_px", new nRun() {
					public void run(Object o) {
						float v = ((float)o);
						ref.setPX(v);
					}
				});

				g.addMetode("set_py", new nRun() {
					public void run(Object o) {
						float v = ((float)o);
						ref.setPY(v);
					}
				});

				g.addMetode("set_title", new nRun() { public void run(Object o) {
					String s = ((String)o);
//					open.setText(s);
					open.setInfo(s);
				}});

				g.addMetode("set_auto_hide", new nRun() { public void run() {
					if (g.hasObject("val_autohide"))
						g.object("val_autohide", sBoo.class).set(true);
				}});

				g.addMetode("link_tool_to_bloc", new nRun() {
					public void run(Object o1, Object o2) {
						sValueBloc v = ((sValueBloc)o1);
						String s = ((String)o2);
						
						sBoo val_open = v.obtainBoo("val_tool_open_"+s, true);
						g.addObject("val_tool_open", val_open);
						sBoo val_autohide = v.obtainBoo("val_autohide_"+s, false);
						g.addObject("val_autohide", val_autohide);
						
						nRun run_val_open = new nRun() { public void run() {
							if (val_open.get()) run_open.run(); 
							else run_close.run(); }};
						
						val_open.addEventChangeThisFrame(run_val_open);
						run_val_open.run();
						
						v.addEventDelete(new nRun() { public void run() {
							g.clear(); }});
					}
				});
				
				return g;
			} 
		} );

		
		
		
		
		
		//      -----  SCROLLABLE LIST  -----
		
		book.newModel("scrollist_slider")
		.copyFrom(book.getModel("ref"))
		.setRect(0,0,RS,RS*8)
		.setSlider()
		.setOutline(true)
		;
		book.newModel("scrollist_ref")
		.copyFrom(book.getModel("ref"))
		.setBoundChild(true)
		.setBoundParent(true)
		.setStacked(true)
		.setStackSpacing(0)
		.setBoundOutspace(0)
		.setHoverableZone(true)
		;
		book.newModel("scrollist_space")
		.copyFrom(book.getModel("ref"))
		.setRect(RS,0,RS*9,RS*8)
		.setBackground()
		.setOutline(true)
		.setOutlineAfterChild(true)
		.setOutlineWeight(RS/30f)
		.set_color_background(Utl.color(0,0,0,0))
		.setMask(true)
		;
		book.newModel("list_back")
		.copyFrom(book.getModel("ref"))
//		.setRect(0,0,RS*8,RS*12)
//		.setBackground()
		.setBoundChild(true)
		.setStackSpacing(0)
		.setBoundOutspace(0)
		.setStack(nAlign.VERTICAL, nAlign.DOWN) //HORIZONTAL VERTICAL RIGHT LEFT UP DOWN
//		.setOutline(true)
		.set_color_background(Utl.color(0,0,0,0))
		;
		book.newModel("list_entry")
		.copyFrom(book.getModel("ref"))
		.setRect(0,0,RS*9,RS)
		.setStacked(true)
		.setBoundParent(true)
		.setStack(nAlign.HORIZONTAL, nAlign.RIGHT) //HORIZONTAL VERTICAL RIGHT LEFT UP DOWN
		.setStackSpacing(0)
//		.setOutline(true)
//		.setOutlineWeight(1)
		;
		book.newModel("list_trigger")
		.copyFrom(book.getModel("list_entry"))
		.setTrigger()
		;
		book.newModel("list_entry_obj")
		.copyFrom(book.getModel("ref"))
		.setRect(0,0,RS,RS)
		.setStacked(true)
		.setTextAlignment(nAlign.LEFT, nAlign.CENTER)
		;
		book.newModel("list_entry_label_1")
		.copyFrom(book.getModel("list_entry_obj"))
		.setSX(RS)
		.set_color_background(Utl.color(0,0,0,0))
		;
		for (int i = 2 ; i <= 9 ; i++)
			book.newModel("list_entry_label_"+i)
			.copyFrom(book.getModel("list_entry_label_1")).setSX(RS*i);

		book.newModel("list_entry_trigg_1")
		.copyFrom(book.getModel("list_entry_obj"))
		.setSX(RS)
		.setTrigger()
		;
		for (int i = 2 ; i <= 9 ; i++)
			book.newModel("list_entry_trigg_"+i)
			.copyFrom(book.getModel("list_entry_trigg_1")).setSX(RS*i);

		book.newModel("list_entry_switch_1")
		.copyFrom(book.getModel("list_entry_obj"))
		.setSX(RS)
		.setSwitch()
		;
		for (int i = 2 ; i <= 9 ; i++)
			book.newModel("list_entry_switch_"+i)
			.copyFrom(book.getModel("list_entry_switch_1")).setSX(RS*i);
		
		book.newModelGroup("list", new nModelGroup() { 
			public nWidgetGroup build(nGUI gui) {
				nWidgetGroup g = gui.addWidgetGroup();
				
				nWidget w1 = g.addWidget("back", "list_back");
				g.addMetode("add_entry", new nRun() {
					public Object get(Object o) {
						return g.addWidget("entry_"+g.get("back").getChildNb(), 
								gui.addWidget("list_entry", (String)o)
								.setParent(w1)
								);
					}
				});
				g.addMetode("add_widget_as_entry", new nRun() {
					public Object get(Object o) {
						return ( ((nWidget)o)
								.setParent(w1)
								.setStacked(true)
								.setBoundParent(true)
								);
					}
				});

				g.addMetode("clear_entrys", new nRun() {
					public void run() {
						nWidget back = g.get("back");
						if (back != null)
							for (int i = back.getChilds().size() - 1 ; i >= 0 ; i--) {
							nWidget w = back.getChilds().get(i);
							g.removeWidget(w);
							w.clear();
						}
					}
				});
				return g;
			} 
		} );
		
		book.newModelGroup("scrollist", new nModelGroup() { 
			public nWidgetGroup build(nGUI gui) {
				nWidgetGroup g = gui.addWidgetGroup();
				
				nWidget ref = g.addWidget("ref", gui.addWidget("scrollist_ref"));
				
				nWidget space = g.addWidget("space", gui.addWidget("scrollist_space")
						.setParent(ref)
						);
				space.setBoundParent(true);
				
				nWidgetGroup list = gui.addWidgetGroup("list");
				list.get("back").setParent(space);
				g.addWidgetGroup("list",list);
				
				//slider as moved, recalc back position
				nRun slidechangerun = new nRun() { public void run() {
					if (list.get("back") != null) {
						nWidget sl = g.get("slider");
						nWidget sp = g.get("space");
						nWidget bc = list.get("back");
						float y = bc.getSY() - sp.getSY();
						if (y > 0) y = y * sl.getSliderVal();
						else y = 0;
						bc.setPY(-y);
					}
				}};
				
				nWidget slider = g.addWidget("slider", gui.addWidget("scrollist_slider")
						.setParent(ref)
						.addEventSliderChange(slidechangerun)
						);
				slider.setBoundParent(true);
						
				nRun r = new nRun() { public void run() {
					if (ref.mouseOverZone) {
						float m = RS / (2f * (list.get("back").getSY() - space.getSY()));
						if (m > 0) {
							if (App.ap.input.mouseWheelUp) {
								slider.setSliderVal(slider.getSliderVal() + m);		
								slidechangerun.run();
							}
							if (App.ap.input.mouseWheelDown) {
								slider.setSliderVal(slider.getSliderVal() - m);		
								slidechangerun.run();
							}
						}
					}
				}};
				
				ref.addEventLogic(r);
				
				//calc slider size
				nRun slidecalcrun = new nRun() { public void run() {
					if (list.get("back") != null) {
						float f = ref.getSY() / list.get("back").getSY();
	//					f = 1/f;
						if (f > 1) f = 1;
						slider.setSliderCursorSize(f);
	//					slider.setSliderVal(0f);	
						slidechangerun.run();
					}
				}};
				g.addMetode("slide_calc", slidecalcrun);
				
				g.addMetode("add_entry", new nRun() {
					public Object get(Object o) {
						Object ob = list.metodeGet("add_entry", o);
						App.ap.addEventNextFrame(slidecalcrun);
						return ob; } } );
				g.addMetode("add_widget_as_entry", new nRun() {
					public Object get(Object o) {
						Object ob = list.metodeGet("add_widget_as_entry", o);
						App.ap.addEventNextFrame(slidecalcrun);
						((nWidget)ob).setSX(space.getLocalSX());
						return ob; } } );
				

				g.addMetode("clear_entrys", new nRun() {
					public void run() {
						list.metode("clear_entrys");
						App.ap.addEventNextFrame(slidecalcrun); } } );

				g.addMetode("set_height", new nRun() {
					public void run(Object o) {
						float h = (Float)o;
						space.setSY(h);
						slider.setSY(h);
						App.ap.addEventNextFrame(slidecalcrun);
					} } );
				g.addMetode("set_width", new nRun() {
					public void run(Object o) {
						float h = (Float)o;
						space.setSX(h-RS);
						App.ap.addEventNextFrame(slidecalcrun);
					} } );
				return g;
			} 
		} );

		
		
		//      -----  PICK LIST  -----
		
		book.newModel("picklist_entry")
		.copyFrom(book.getModel("ref"))
		.setRect(0,0,RS*9,RS)
		.setStacked(true)
		.setBoundParent(true)
		.setStack(nAlign.HORIZONTAL, nAlign.RIGHT) //HORIZONTAL VERTICAL RIGHT LEFT UP DOWN
		.setStackSpacing(0)
		.setOutline(true)
		.setOutlineWeight(1)
		.setSwitch()
		;
		
		book.newModelGroup("picklist", new nModelGroup() { 
			public nWidgetGroup build(nGUI gui) {
				nWidgetGroup g = gui.addWidgetGroup("scrollist");
				
				ArrayList<nWidget> pick_list = new ArrayList<nWidget>();
				g.addObject("pick_list", pick_list);
				
				g.addEventClear(new nRun() { public void run() {
					g.metode("clear_pick"); } } );
				
				g.addMetode("add_pick", new nRun() { public Object get(Object o) {
					String t = (String)o;
					nWidget ent = (nWidget)g.metodeGet("add_widget_as_entry", 
							g.addWidget("pick_entry_"+t, 
								gui.addWidget("picklist_entry", t) )
					);
					pick_list.add(ent);
					ent.addEventSwitchOn(new nRun() { public void run() {
						for (nWidget w : pick_list) if (w != ent) w.setOff();
						if (g.hasObject("pick_event")) {
							nRun pick_event = g.object("pick_event", nRun.class);
							if (pick_event != null) pick_event.run(ent.getText());
						}
					}});
					return ent; 
				} } );

				g.addMetode("get_pick", new nRun() { public Object get() {
					for (nWidget w : pick_list) if (w.isOn()) return w.getText();
					return ""; 
				} } );
				
				g.addMetode("clear_pick", new nRun() { public void run() {
					pick_list.clear();
					g.metode("clear_entrys");
				} } );

				g.addMetode("set_pick_event", new nRun() { public void run(Object o) {
					nRun t = (nRun)o;
					g.addObject("pick_event", t);
				} } );
				
				return g;
			} 
		} );

		
		
		//      -----  TREE LIST  -----

		book.newModel("tree_entry")
		.copyFrom(book.getModel("list_entry"))
		.setRect(0,0,RS*9,RS*0.6f)
		.setStacked(true)
		.setBoundParent(true)
		.setBoundChild(true)
		.setStackSpacing(0)
		.setBoundOutspace(0)
		.setStack(nAlign.VERTICAL, nAlign.DOWN) //HORIZONTAL VERTICAL RIGHT LEFT UP DOWN
		.setTextAlignment(nAlign.LEFT, nAlign.CENTER)
		;
		
		book.newModelGroup("treelist", new nModelGroup() { 
			public nWidgetGroup build(nGUI gui) {
				nWidgetGroup g = gui.addWidgetGroup("scrollist");
				
				g.addEventClear(new nRun() { public void run() {
					g.metode("clear_tree"); } } );

				g.addMetode("clear_tree", new nRun() { public void run() {
					g.setObject("entry_nb", 0);
					g.metode("clear_entrys");
				} } );
				
				g.addObject("entry_nb", 0);
				
				g.addMetode("add_entry", new nRun() { public Object get(Object o) {
					String t = (String)o;
					int ent_nb = g.object("entry_nb", Integer.class);
					nWidget ent = (nWidget)g.metodeGet("add_widget_as_entry", 
							g.addWidget("entry_"+ent_nb, 
								gui.addWidget("tree_entry", t) )
					);
					g.setObject("entry_nb", ent_nb+1);
					ent.addEventSwitchOn(new nRun() { public void run() {
						for (nWidget w : ent.getChilds()) w.show();
						ent.setTextVisibility(false);
						ent.force_calc();
						g.metode("slide_calc"); }});
					ent.addEventSwitchOff(new nRun() { public void run() {
						for (nWidget w : ent.getChilds()) w.hide();  
						ent.setTextVisibility(true);
						ent.force_calc();
						g.metode("slide_calc"); }});
					return ent; 
				} } );

				g.addMetode("add_sub_entry", new nRun() { public Object get(Object o1, Object o2) {
					nWidget p = (nWidget)o1;
					String t = (String)o2;
					int ent_nb = g.object("entry_nb", Integer.class);
					nWidget ent = g.addWidget("entry_"+ent_nb, 
							gui.addWidget("tree_entry", t) );
					g.setObject("entry_nb", ent_nb+1);
					ent.setParent(p);
					ent.hide();
//					g.metode("slide_calc");
					ent.addEventSwitchOn(new nRun() { public void run() {
						for (nWidget w : ent.getChilds()) w.show(); 
						ent.setTextVisibility(false);
						ent.force_calc();
						g.metode("slide_calc"); }});
					ent.addEventSwitchOff(new nRun() { public void run() {
						for (nWidget w : ent.getChilds()) w.hide();  
						ent.setTextVisibility(true);
						ent.force_calc();
						g.metode("slide_calc"); }});
					return ent; 
				} } );
				
				return g;
			} 
		} );

		
		
		//      -----  DROPMENU  -----
		book.newModel("DM_ref")
//		.setRect(0,0,0,0)
//		.setBackground()
//		.set_color_background(Utl.color(0,0))
//		.setOutline(true)
//		.setOutlineWeight(RS / 30f)
//		.setOutlineAfterChild(true)
//		.set_color_outline(Utl.color(180,255))
//		.setBoundChild(true)
//		.setStack(nAlign.HORIZONTAL, nAlign.RIGHT)
//		.setRectOrigin(nAlign.LEFT,nAlign.TOP) // TOP   BOTTOM
//		.setBoundOutspace(0)
//		.setStackSpacing(RS/15f)
		.setVisibility(false)
		.setHoverableZone(true)
		.setDrawstackPriority(true)
		;
		book.newModel("DM_back")
//		.setRect(0,0,0,0)
//		.set_color_background(Utl.color(0,0))
//		.set_color_background(Utl.color(70,255)) 
		.set_color_background(book.getModel("CL_DM_back").color_background) 
		.set_color_outline(book.getModel("CL_DM_back").color_outline)
		.setOutline(book.getModel("CL_DM_back").showOutline) 
		.setBoundParent(true)
		.setBoundChild(true)
		.setStacked(true)
		.setStack(nAlign.VERTICAL, nAlign.DOWN)
		.setRectOrigin(nAlign.LEFT,nAlign.BOTTOM) // TOP   BOTTOM
		.setBoundOutspace(0)
		.setStackSpacing(RS/15f)
		;

		book.newModel("DM_zone")
		.setRect(-2f*RS,2f*RS,0,0)
		.set_color_background(Utl.color(0,0))
		.setDraw(false)
//		.setBoundParent(true)
		.setBoundChild(true)
//		.setStacked(true)
		.setStack(nAlign.HORIZONTAL, nAlign.RIGHT)
		.setRectOrigin(nAlign.LEFT,nAlign.TOP) // TOP   BOTTOM
		.setBoundOutspace(RS*2f)
//		.setStackSpacing(RS/15f)
//		.setOutline(true)
		;
		
		book.newModel("DM_entry")
		.copyFrom(book.getModel("CL_DM_entry"))
		.setRect(0,0,RS*10f,RS*5f/6f)
		.setBoundParent(true)
		.setStacked(true)
		.setTrigger()
		;
		
		book.newModel("DM_separator")
		.setRect(0,0,RS*1f,RS/6f)
		.setBoundParent(true)
		.setStacked(true)
		.set_color_background(book.getModel("CL_DM_back").color_background)
		;
		
		int DM_max_ent = 12;
		
		book.newModelGroup("dropmenu", new nModelGroup() { 
			public nWidgetGroup build(nGUI gui) {
				nWidgetGroup g = gui.addWidgetGroup();

				nWidget ref = g.addWidget("ref", gui.addWidget("DM_ref")
						);
				nWidget zone = g.addWidget("zone", gui.addWidget("DM_zone")
						.setParent(ref)
						);
				g.addWidget("back", gui.addWidget("DM_back")
						.setParent(zone)
						);
				
				ArrayList<nWidget> entrys = new ArrayList<nWidget>();
				g.addObject("entrys", entrys);
				
				g.setObject("right_side", false);
				
				g.addObject("cnt", (int)0);
				g.addMetode("cnt", new nRun() { public Object get() {
					int i = g.object("cnt", Integer.class);
					g.setObject("cnt", i+(int)1);
					return i;
				} });
				g.addObject("bcnt", (int)0);
				g.addMetode("bcnt", new nRun() { public Object get() {
					int i = g.object("bcnt", Integer.class);
					g.setObject("bcnt", i+(int)1);
					return i;
				} });
				
				
				nRun r = new nRun() { public void run() {
					
//					if (g.object("right_side", Boolean.class)) {
//						nWidget back = g.get("back0");
//						if (back == null) back = g.get("back");
//						if (back != null && back.globalPos.x < 0) 
//							zone.addPos(-back.globalPos.x,0);
//					} else {
//						nWidget back = g.get("back");
//						if (back != null && 
//								back.globalPos.x + back.globalrect.width > Applet.WIDTH) 
//							zone.addPos(back.globalPos.x + 
//									back.globalrect.width - Applet.WIDTH,0);
//					}
					
					boolean over = false;
					if (zone.maskedrect.contains(ref.gui.mouse_vec)) {
						over = true; }
					if (g.get("openner") != null && 
						g.get("openner").maskedrect.contains(
								g.get("openner").gui.mouse_vec)) {
						over = true; }
					if (!over) g.metode("close");
				}}; 
				g.addMetode("open", new nRun() {
					public void run() { run(null); }
					public void run(Object o) {
						if (o == null) {
							if (g.get("openner") != null) g.removeWidget("openner");

							ref.setVisibility(true);
							
							App.ap.addEventNextFrame(new nRun() { public void run() {
								Vector2 np = new Vector2(App.ap.input.mouse);
								if (np.x > GdxApp.WIDTH / 2f) {
									zone.setRectOrigin(nAlign.RIGHT,nAlign.TOP);
									zone.setPos(2f*RS,2f*RS);
									ref.toFront().setPos(np.x+5, np.y+5);
									g.setObject("right_side", true);
								} else {
									zone.setRectOrigin(nAlign.LEFT,nAlign.TOP);
									zone.setPos(-2f*RS,2f*RS);
									ref.toFront().setPos(np.x-5, np.y+5);
									g.setObject("right_side", false); }
								ref.force_calc_child();
								App.ap.addRunFrameStart(r);
							}});
						} else if (o instanceof nWidget) {
							nWidget op = (nWidget)o;
							
							if (g.get("openner") != null) g.removeWidget("openner");
							g.addWidget("openner", op);
	
							ref.setVisibility(true);
							
							App.ap.addEventNextFrame(new nRun() { public void run() {
							
								Vector2 np = op.maskedrect
										.getPosition(new Vector2());
	//								ref.toFront().setPos(np.x, np.y+5);
								if (np.x > GdxApp.WIDTH / 2f) {
									zone.setRectOrigin(nAlign.RIGHT,nAlign.TOP);
									zone.setPos(2f*RS,2f*RS);
									ref.toFront().setPos(np.x + op.maskedrect.width, 
											np.y+5);
									g.setObject("right_side", true);
								} else {
									zone.setRectOrigin(nAlign.LEFT,nAlign.TOP);
									zone.setPos(-2f*RS,2f*RS);
									ref.toFront().setPos(np.x, np.y+5);
									g.setObject("right_side", false); }
									
								App.ap.addRunFrameStart(r);
							}});
						}
					}
				});
				g.addMetode("close", new nRun() {
					public void run() {
						App.ap.removeRunFrameStart(r);
						App.ap.addEventNextFrame(new nRun() { public void run() {
							ref.setVisibility(false);
							g.removeWidget("openner");
							App.ap.addEventNextFrame(new nRun() { public void run() {
								g.metode("clear_entrys"); }});
						}});
					}
				});
				g.addMetode("add_separator", new nRun() {
					public void run() {
//						g.metode("close");
						nWidget back = g.get("back");
						if (back.getChildNb() > DM_max_ent) {
							g.removeWidget("back");
							nWidget back2 = g.addWidget("back", gui.addWidget("DM_back")
									.setParent(zone)
									);
							g.addWidget("back"+g.metodeGetInt("bcnt"), back);
							back = back2;
						}
						nWidget ent = gui.addWidget("DM_separator")
								.setParent(back);
						entrys.add(ent);
						g.addWidget("entry_"+g.metodeGetInt("cnt"), ent);
					}
				});
				g.addMetode("add_entry", new nRun() {
					public Object get(Object o) {
//						g.metode("close");
						nWidget back = g.get("back");
						if (back.getChildNb() > DM_max_ent) {
							g.removeWidget("back");
							nWidget back2 = g.addWidget("back", gui.addWidget("DM_back")
									.setParent(zone)
									);
							g.addWidget("back"+g.metodeGetInt("bcnt"), back);
							back = back2;
						}
						nWidget ent = gui.addWidget("DM_entry", (String)o)
								.setParent(back)
								.addEventTrigger(new nRun() { public void run() {
									App.ap.addDelayEvent(5, new nRun() { public void run() {
										g.metode("close"); 
									}}); }});
						entrys.add(ent);
						
						return g.addWidget("entry_"+g.metodeGetInt("cnt"), 
								ent);
					}
				});
				g.addMetode("add_entry_custom", new nRun() {
					public Object get(Object o, Object o2, Object o3) {
//						g.metode("close");
						nWidget back = g.get("back");
						if (back.getChildNb() > DM_max_ent) {
							g.removeWidget("back");
							nWidget back2 = g.addWidget("back", gui.addWidget("DM_back")
									.setParent(zone)
									);
							g.addWidget("back"+g.metodeGetInt("bcnt"), back);
							back = back2;
						}
						nWidget ent = gui.addWidget("DM_entry", (String)o)
								.setParent(back)
								.addEventTrigger(new nRun() { public void run() {
									App.ap.addDelayEvent(4, new nRun() { public void run() {
										g.metode("close"); 
									}}); }})
								.setSize((Float)o2, (Float)o3).asWidget();
						entrys.add(ent);
						return g.addWidget("entry_"+g.metodeGetInt("cnt"), 
								ent);
					}
				});
				g.addMetode("clear_entrys", new nRun() {
					public void run() {
						for (int i = entrys.size() - 1 ; i >= 0 ; i--) {
							nWidget w = entrys.get(i);
							g.removeWidget(w);
							w.clear();
						}
						entrys.clear();
						for (int i = zone.getChilds().size() - 1 ; i >= 0 ; i--) {
							nWidget w = zone.getChilds().get(i);
							g.removeWidget(w);
							w.clear();
						}
						g.addWidget("back", gui.addWidget("DM_back")
								.setParent(zone)
								);
						g.setObject("cnt", (int)0);
						g.setObject("bcnt", (int)0);
					}
				});
				
				return g;
			} 
		} );
		
		
		
	}
	

	static void build_menu_book() {
		nModelBook book = nGUI.book;
		float RS = book.RS;
		
		
//		book.newModel("title_screen_back")
//		.copyFrom(book.getModel("ref"))
//		.setBackground()
//		.setDrawstackPriority(true)
//		;
//
//		book.newModel("title_screen_stack")
//		.copyFrom(book.getModel("ref"))
//		.setPassif()
//		.set_color_background(Utl.color(0,0,0,0))
//		.setBoundChild(true)
//		.setStackAxis(nAlign.VERTICAL) // HORIZONTAL   VERTICAL
//		.setStackDirection(nAlign.DOWN) // RIGHT   LEFT   UP   DOWN
//		.setRectOrigin(nAlign.CENTER,nAlign.CENTER) // TOP   BOTTOM
//		.setBoundOutspace(RS/6f)
//		.setStackSpacing(RS/5f)
//		;
//		
//		book.newModel("title_screen_title")
//		.copyFrom(book.getModel("ref"))
//		.setRect(0,0,24f*RS,6f*RS)
//		.setBoundParent(true)
//		.setStacked(true)
//		.setFont(90)
//		.set_color_text(Utl.color(220,220,218))
//		.set_color_background(Utl.color(12,10,10))
//		.set_color_outline(Utl.color(200))
//		.setOutline(true)
//		.setOutlineWeight(RS/4f)
//		;
//		book.newModel("title_screen_trigg")
//		.copyFrom(book.getModel("ref"))
//		.setRect(0,0,8f*RS,2f*RS)
//		.setBoundParent(true)
//		.setStacked(true)
//		.setFont(30)
//		.setTrigger()
//		;
		
		
		
		
		//      -----  MAIN MENU  -----
		book.newModel("menu_back")
		.copyFrom(book.getModel("CW_head_color"))
		.setBackground()
		.setOutline(true)
		.setDrawstackPriority(true)
		;
		book.newModel("menu_ref")
		.copyFrom(book.getModel("ref"))
		.setBoundChild(true)
		.setStackAxis(nAlign.HORIZONTAL) // HORIZONTAL   VERTICAL
		.setStackDirection(nAlign.RIGHT) // RIGHT   LEFT   UP   DOWN
		.setRectOrigin(nAlign.LEFT,nAlign.BOTTOM) // TOP   BOTTOM
		.setBoundOutspace(5)
		.setStackSpacing(5)
		.set_color_background(Utl.color(0, 0))
		;
		
		book.newModel("info_back")
		.copyFrom(book.getModel("ref"))
		.setPassif()
		.set_color_background(Utl.color(0,0,0,0))
		.setBoundChild(true)
		.setOutline(true)
		.setStackAxis(nAlign.VERTICAL) // HORIZONTAL   VERTICAL
		.setStackDirection(nAlign.UP) // RIGHT   LEFT   UP   DOWN
		.setRectOrigin(nAlign.LEFT,nAlign.BOTTOM) // TOP   BOTTOM
		.setBoundOutspace(RS/6f)
		.setStackSpacing(RS/15f)
		.setDrawstackPriority(true)
		;
		
		book.newModel("menu_trigg")
		.copyFrom(book.getModel("CW_head_color"))
		.setRect(0,0,8f*RS/3f,RS)
		.setBoundParent(true)
		.setStacked(true)
		.setFont(20)
		.setTrigger()
		;
		
		book.newModel("info_text")
		.copyFrom(book.getModel("ref"))
		.setRect(0,0,6f*RS,2f*RS/3f)
		.set_color_background(Utl.color(0,0,0,0))
		.setOutline(false)
		.setBoundParent(true)
		.setStacked(true)
		.setPassif()
		;
	}
	
	
	
	
}
