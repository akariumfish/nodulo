package gui;

import java.util.ArrayList;

import com.badlogic.gdx.math.Vector2;

import aa_nodulo.PlaneApplet;
import app.App;
import app.GdxApp;
import data.*;
import util.Utl;
import util.nRun;

public class nToolBox {

	public PlaneApplet app;
	public nMenu menu;
	sData data;
	nModelBook book;
	nGUI gui;
	public float RS;
	
	public sValueBloc toolbox_bloc;
	public sBoo val_toolbox_open;
	public sFlt val_toolbox_scroll;
//	public sInt val_toolbox_stackindex;
	
	public nToolBox(nMenu a) {
		menu = a; app = a.app; book = nGUI.book; RS = book.RS;
		gui = app.gui; data = app.data;
		
		toolbox_bloc = data.setting_bloc.obtainBloc("toolbox_bloc");

		val_toolbox_open = toolbox_bloc.obtainBoo("val_toolbox_open", "toolbox open", 
				app.config.TOOLBOX_OPEN);
		val_toolbox_scroll = toolbox_bloc.obtainFlt("val_toolbox_scroll", "toolbox scroll", 1);
//		val_toolbox_stackindex = toolbox_bloc.obtainInt("val_toolbox_stackindex", "toolbox stackindex", 0);

		build_toolbox();
	}

	
	
	
	
	
	nWidgetGroup tool_group;
	
	public nWidgetGroup addSection(String t, boolean open) {
		nWidgetGroup ent = app.gui.addWidgetGroup("toolbox_section");
		sBoo val_sec_open = toolbox_bloc.obtainBoo("val_sec_open_"+t, "sec open", open);
		ent.metode("add_to_toolbox", tool_group);
		ent.metode("set_title", t);
		ent.metode("set_val_open", val_sec_open);
		return ent; }
	
//	public sBoo newBoo(String ref, String shrt, boolean val) {
//		sValue v = toolbox_bloc.getValue(ref);
//		if (v != null) { return (sBoo)v; }
//		else return toolbox_bloc.newBoo(ref, shrt, val); }
//	public sFlt newFlt(String ref, String shrt, float val) {
//		sValue v = toolbox_bloc.getValue(ref);
//		if (v != null) { return (sFlt)v; }
//		else return toolbox_bloc.newFlt(ref, shrt, val); }
//	public sInt newInt(String ref, String shrt, int val) {
//		sValue v = toolbox_bloc.getValue(ref);
//		if (v != null) { return (sInt)v; }
//		else return toolbox_bloc.newInt(ref, shrt, val); }
	
	public void addSeparator(nWidgetGroup sec) {
		String k = "separator"; String base_ref = k;
		int c = 1; while (sec.widgets.get(k) != null) { k = base_ref + "-" + c; c++; }
		sec.metodeGet("add_widget_as_entry", 
				sec.addWidget(k, "toolbox_section_separator")); }
	public nWidget addWidget(nWidgetGroup sec, String ref, String model) {
		nWidget w = (nWidget)sec.metodeGet("add_widget_as_entry", 
				sec.addWidget(ref, model));
		return w; }
	public nWidget addWidget(nWidgetGroup sec, String ref, String model, String text) {
		nWidget w = addWidget(sec, ref, model);
		w.setText(text);
		return w; }
	public nWidget addWidget(nWidgetGroup sec, String ref, nWidget w) {
		sec.addWidget(ref, w); return w; }
	
	
	
	ArrayList<nRun> eventOpen = new ArrayList<nRun>();

	public void addEventOpen(nRun n) { eventOpen.add(n); }
	public void removeEventOpen(nRun n) { eventOpen.remove(n); }
	
	private void build_toolbox() {
		tool_group = gui.addWidgetGroup("toolbox");
		tool_group.metode("set_toolbox", this);
//		tool_group.metode("set_val_open", val_toolbox_open);

		if (app.config.TOOLBOX_OPEN)
			app.addDelayEvent(30, new nRun() { public void run() {
				tool_group.get("back").toFront(); }});
		
		
		menu.add_menu_trigg("-", new nRun() { public void run() {
			val_toolbox_open.swtch();
			app.addDelayEvent(1, new nRun() { public void run() {
				menu.updateFreeview(); }}); 
		}}).toBack().setSize(30,30);
		
		nRun run_toolbox_open = new nRun() { public void run() {
			if (val_toolbox_open.get()) {
				tool_group.get("back").show().asWidget().toFront();
				
			} else tool_group.get("back").hide();
		}};
		
		val_toolbox_open.addEventChangeThisFrame(run_toolbox_open);
		run_toolbox_open.run();
		
		app.addDelayEvent(1, new nRun() { public void run() {
			run_toolbox_open.run(); }});
		
		

		app.gdx.addEventScreen(new nRun() { public void run() {
			tool_group.get("back").setRect(0,0,RS*12,app.gdx.getscreenheight()); 
			tool_group.getGroup("list").get("space").setSize(tool_group.get("back").getLocalSX() - RS, 
					tool_group.get("back").getLocalSY());
			tool_group.getGroup("list").get("slider").setSize(RS,tool_group.get("back").getLocalSY());
		}});
		
		
//		
//		bar_entrys = new ArrayList<nWidget>();
//		
//		bar_back = gui.addWidget("taskbar_back")
//				.setRect(0,0,Applet.WIDTH,RS+10)
//				.setDrawstackPriority(true)
//				.asWidget()
//				;
//		bar_ref = gui.addWidget("taskbar_ref")
//				.asWidget()
//				.setParent(bar_back)
//				;
//
//		app.addEventScreen(new nRunnable() { public void run() {
//			bar_back.setRect(0,0,app.getscreenwidth(),RS+10); 
//		}});
//
//		
//
//		nWidget testw = gui.addWidget("info_text")
//		.setParent(bar_ref);
//		testw.setSX(250);
//
//		testw.addEventLogic(new nRunnable() { public void run() {
//			if (data.selected_bloc != null) 
//				testw.setText("selected bloc:"+data.selected_bloc.ref); 
//			else testw.setText("selected bloc: none"); }});
//		

	}
	
//	public void build_quicktool() {
//		toolbox_quicktool_sec = addSection(" QUICK TOOLS ", true);
//	}
//	
//	public nWidgetGroup toolbox_quicktool_sec;
//	
//	nWidget bar_back, bar_ref;
//	ArrayList<nWidget> bar_entrys;
//	
//	public nWidget add_taskbar_entry() {
//		
//		nWidget w = gui.addWidget("taskbar_entry")
//		.setParent(bar_ref);
//		
//		for(nWidget n : bar_entrys) n.setOff();
//		
//		w.addEventSwitchOn(new nRunnable() { public void run() {
//			for(nWidget n : bar_entrys) if (n != w) n.setOff(); }});
//		
//		bar_entrys.add(w);
//		return w;
//	}
//	public void remove_taskbar_entry(nWidget w) {
//		bar_entrys.remove(w); }
//	
	
	
	
	
	
	
	
	static void build_book() {
		
		nModelBook book = nGUI.book;
		float RS = book.RS;
		
		// -----------  WINDOW BAR -----------
		
//		book.newModel("taskbar_back")
//		.copyFrom(book.getModel("ref"))
//		.setBackground()
//		.setOutline(true)
//		.setDrawstackPriority(true)
//		;
//		book.newModel("taskbar_ref")
//		.copyFrom(book.getModel("ref"))
//		.setBoundChild(true)
//		.setStackAxis(nAlign.HORIZONTAL) // HORIZONTAL   VERTICAL
//		.setStackDirection(nAlign.RIGHT) // RIGHT   LEFT   UP   DOWN
//		.setRectOrigin(nAlign.LEFT,nAlign.BOTTOM) // TOP   BOTTOM
//		.setBoundOutspace(5)
//		.setStackSpacing(5)
//		.set_color_background(Utl.color(0, 0))
//		;
//
//		book.newModel("taskbar_entry")
//		.copyFrom(book.getModel("ref"))
//		.setRect(0,0,RS*5,RS)
//		.setBoundParent(true)
//		.setStacked(true)
//		.setFont(22)
//		.setSwitch()
//		;
		
		
		// -----------   TOOLBOX   -----------
		
		book.newModel("tool_back")
		.copyFrom(book.getModel("ref"))
		.setBackground()
		.setRect(0,0,RS*12,GdxApp.HEIGHT - 40)
		;
		
		book.newModel("toolbox_section_space")
		.copyFrom(book.getModel("ref"))
		.setStacked(true)
		.setBoundParent(true)
		.setBoundChild(true)
		.setBoundOutspace(0)
		.set_color_background(Utl.color(0,0,0,0))
		.setOutline(true)
		.setOutlineWeight(3f)
//		.set_color_outline(Utl.color(180,180,180,255))
		.setOutlineAfterChild(true)
		;
		book.newModel("toolbox_section_back")
		.copyFrom(book.getModel("ref"))
		.setRect(0,RS,RS*11f - 10,RS)
		.setStack(nAlign.VERTICAL, nAlign.DOWN) //HORIZONTAL VERTICAL RIGHT LEFT UP DOWN
		.setBoundParent(true)
		.setBoundChild(true)
		.setBoundOutspace(RS/6f)
		.setStackSpacing(0)
		.set_color_background(Utl.color(0,0,0,0))
		;
		book.newModel("toolbox_section_head")
		.copyFrom(book.getModel("CW_head_color"))
		.setRect(0,0,RS*11 - 10,RS)
		.setBoundParent(true)
//		.set_color_background(Utl.color(40,40,40,255))
		;
		book.newModel("toolbox_section_collapse")
		.copyFrom(book.getModel("CW_head_color"))
		.setRect(RS*10 - 10,0,RS,RS)
		.setBoundParent(true)
		.setText("-")
		.setTrigger()
		.setOutline(true)
		.setOutlineWeight(1)
		.set_color_outline(Utl.color(0,0,200,255))
		;
		
		book.newModel("toolbox_separator")
		.copyFrom(book.getModel("ref"))
		.setStacked(true)
		.setBoundParent(true)
		.set_color_background(Utl.color(0,0,0,0))
		.setRect(0,0,RS*11f - 1,RS/6f)
		;
		
		book.newModel("toolbox_section_entry")
		.copyFrom(book.getModel("ref"))
		.setRect(0,0,RS*11f - RS/3f - 1,RS)
		.setBoundParent(true)
		.setStacked(true)
		.setBoundChild(true)
		.setBoundOutspace(0)
		.setStack(nAlign.HORIZONTAL, nAlign.RIGHT) //HORIZONTAL VERTICAL RIGHT LEFT UP DOWN
		.setStackSpacing(10)
		;

		book.newModel("toolbox_section_separator")
		.copyFrom(book.getModel("toolbox_section_entry"))
		.setSY(RS/4f)
		.set_color_background(Utl.color(0,0,0,0))
		;
		
		book.newModel("toolbox_section_trigg")
		.copyFrom(book.getModel("toolbox_section_entry"))
		.setTrigger()
		;
		
		book.newModel("toolbox_section_switch")
		.copyFrom(book.getModel("toolbox_section_entry"))
		.setSwitch()
		;
		
		book.newModelGroup("toolbox_section", new nModelGroup() { 
			public nWidgetGroup build(nGUI gui) {
				nWidgetGroup g = gui.addWidgetGroup();
				
				nWidget space = g.addWidget("space", "toolbox_section_space");
				nWidget back = g.addWidget("back", "toolbox_section_back");
				nWidget head = g.addWidget("head", "toolbox_section_head");
				nWidget collapse = g.addWidget("collapse", "toolbox_section_collapse");
				nWidget sep = g.addWidget("sep", "toolbox_separator");

				head.setParent(space);
				back.setParent(space);
				collapse.setParent(space);
				
				nRun run_collapse = new nRun() { public void run() {
					Object o = g.object("val_open");
					if (o != null) ((sBoo)o).swtch();
					else back.switchVisibility();
				}};
				collapse.addEventTrigger(run_collapse) ;
				
				back.addEventVisibility(new nRun() { public void run() {
					App.ap.addEventNextFrame(new nRun() { public void run() {
						nWidgetGroup tool = (nWidgetGroup)g.object("toolbox");
						tool.getGroup("list").metode("slide_calc");
					}});
				}});
				
				g.addMetode("tool_scroll_calc", new nRun() {
					public void run() {
						App.ap.addDelayEvent(2, new nRun() { public void run() {
							nWidgetGroup tool = (nWidgetGroup)g.object("toolbox");
							tool.getGroup("list").metode("slide_calc");
						}});
					} } );
				
				g.addMetode("set_title", new nRun() {
					public void run(Object o) {
						String t = (String)o;
						head.setText(t);
					} } );
				
				g.addMetode("set_val_open", new nRun() { public void run(Object o) {
					sBoo t = (sBoo)o;
					g.addObject("val_open", t);
					if (!t.get()) back.hide();
					t.addEventChangeLastFrame(new nRun() { public void run() {
						if (t.get()) back.show(); else back.hide();
					}});
				} } );
				
				g.addMetode("add_to_toolbox", new nRun() {
					public void run(Object o) {
						nWidgetGroup tool = (nWidgetGroup)o;
						tool.metodeGet("add_widget_as_entry", space);
						tool.metodeGet("add_widget_as_entry", sep);
						g.addObject("toolbox", tool);
						App.ap.addDelayEvent(2, new nRun() { public void run() {
							tool.getGroup("list").metode("slide_calc");
						}});
					} } );
				
				g.addMetode("add_widget_as_entry", new nRun() {
					public Object get(Object o) {
						Object ob = ((nWidget)o).setParent(back);
						App.ap.addDelayEvent(2, new nRun() { public void run() {
							((nWidgetGroup)g.object("toolbox"))
								.getGroup("list").metode("slide_calc");
						}});
						return ob; } } );
				
				return g;
			} 
		} );
		
		book.newModelGroup("toolbox", new nModelGroup() { 
			public nWidgetGroup build(nGUI gui) {
				nWidgetGroup g = gui.addWidgetGroup();
				
				nWidget tool_back = g.addWidget("back", "tool_back");
//				tool_back.toTop();
				
				nWidgetGroup list = gui.addWidgetGroup("scrollist");
				g.addWidgetGroup("list",list);
				
				tool_back.addEventVisibility(new nRun() { public void run() {
					App.ap.addEventNextFrame(new nRun() { public void run() {
						list.metode("slide_calc"); }}); }});
				
				nWidget list_ref = list.get("ref").setParent(tool_back);
				
				nRun r = new nRun() { public void run() {
					if (list_ref.mouseOverZone && App.ap.input.mouseLeft.trigClick) {
						tool_back.toFront(); }  }};
				list_ref.addEventLogic(r);
				
				list.get("space").setSize(tool_back.getLocalSX() - RS, 
							tool_back.getLocalSY());
				list.get("slider").setSize(RS,tool_back.getLocalSY());
				
				g.addMetode("add_widget_as_entry", new nRun() {
					public Object get(Object o) {
						Object ob = list.metodeGet("add_widget_as_entry", o);
						return ob; } } );
				
				g.addMetode("set_toolbox", new nRun() {public void run(Object o) {
					list.get("slider").setLinkSlider(((nToolBox)o).val_toolbox_scroll);
				}});
				
				return g;
			} 
		} );
		
		
	}
	
}
