package gui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.noodle.nodulo.GdxApp;

import aa_nodulo.PlaneApplet;
import app.App;
import data.*;
import util.Utl;
import util.nMap;
import util.nRun;

public class nMenu {

	
	public PlaneApplet app;
	public nGUI gui;
	public sData data;

	public nToolBox toolbox;

	nWidget info_back;
	nWidgetGroup dropmenu_file;//, dropmenu_tool;//dropmenu_build, 
	public nWidget save_path_viewer, close, fullscreen, hidebar, hideinfo, fx;
	
	public sBoo val_hide_bar, val_hide_info, val_fx;
	
	private boolean menu_bar_visible = false;
	public Rectangle freeview = new Rectangle();
	
	ArrayList<nRun> freeviewEvent = new ArrayList<nRun>();

	public void addFreeviewEvent(nRun n) { freeviewEvent.add(n); updateFreeview(); }
	public void removeFreeviewEvent(nRun n) { freeviewEvent.remove(n); }
	
	public void updateFreeview() {
		Vector2 p = new Vector2(0,0);
		Vector2 s = new Vector2(app.gdx.getscreenwidth(), app.gdx.getscreenheight());
		if (toolbox.val_toolbox_open.get()) {
			s.x -= toolbox.tool_group.get("back").getSX();
			p.x += toolbox.tool_group.get("back").getSX();}
		if (menu_bar_visible) { s.y -= gui.menu_back.getSY(); }
		freeview.set(p.x,p.y,s.x,s.y);
		nRun.runEvents(freeviewEvent);
	}
	
	public nWidget menu_right;
	
	public nMenu(PlaneApplet a) {
		app = a;
		app.menu = this;
		gui = a.gui;
		data = a.data;
		
		float RS = nGUI.book.RS;
		nWidget menu_back = gui.menu_back;
		
		menu_right = gui.addWidget("ref")
				.setParent(menu_back)
				.setBoundChild(true)
				.setStackAxis(nAlign.HORIZONTAL) // HORIZONTAL   VERTICAL
				.setStackDirection(nAlign.LEFT) // RIGHT   LEFT   UP   DOWN
				.setRectOrigin(nAlign.LEFT,nAlign.BOTTOM) // TOP   BOTTOM
				.setBoundOutspace(0)
				.setStackSpacing(RS/10f)
				.setPassif()
				.set_color_background(Utl.color(0,0))
				.asWidget()
				;
		
		close = gui.addWidget("ref")
//				.setRect(GdxApp.WIDTH - RS*4f - RS/6f, RS/6f, RS*4f, RS)
				.setSize(4f*RS, RS)
				.setBoundParent(true)
				.setStacked(true)
				.setFont(20)
				.setTrigger()
				.setText("Exit")
				.asWidget()
				.setParent(menu_right)
				.addEventTrigger(new nRun() { public void run() {
					gui.pop_exit(); }})
				;

		fullscreen = gui.addWidget("ref")
//				.setRect(GdxApp.WIDTH - 14f*RS/6f, RS/6f, RS, RS)
				.setSize(RS, RS)
				.setBoundParent(true)
				.setStacked(true)
				.setFont(20)
				.setSwitch()
				.setText("Fs")
				.asWidget()
				.setParent(menu_right)
				.setLink(app.input.val_fullscreen)
				;

		val_hide_bar = app.data.root_bloc.newBoo("val_hide_bar", true);

		hidebar = gui.addWidget("ref")
//				.setRect(GdxApp.WIDTH - 21f*RS/6f, RS/6f, RS, RS)
				.setSize(RS, RS)
				.setBoundParent(true)
				.setStacked(true)
				.setFont(20)
				.setSwitch()
				.setText("M")
				.asWidget()
				.setParent(menu_right)
				.setLink(val_hide_bar)
				;

		val_hide_info = app.data.root_bloc.newBoo("val_hide_info", false);

		hideinfo = gui.addWidget("ref")
//				.setRect(GdxApp.WIDTH - 28f*RS/6f, RS/6f, RS, RS)
				.setSize(RS, RS)
				.setBoundParent(true)
				.setStacked(true)
				.setFont(20)
				.setSwitch()
				.setText("I")
				.asWidget()
				.setParent(menu_right)
				.setLink(val_hide_info)
				;

		val_fx = app.data.root_bloc.newBoo("val_fx", app.config.START_FX);
		val_fx.addEventChangeLastFrame(new nRun() { public void run() {
			app.use_fx(val_fx.get()); }});
		
		fx = gui.addWidget("ref")
//				.setRect(GdxApp.WIDTH - 35f*RS/6f, RS/6f, RS, RS)
				.setSize(RS, RS)
				.setBoundParent(true)
				.setStacked(true)
				.setFont(20)
				.setSwitch()
				.setText("FX")
				.asWidget()
				.setParent(menu_right)
				.setLink(val_fx)
				;
		
		save_path_viewer = gui.addWidget("ref")
//				.setRect(GdxApp.WIDTH - RS*20f, RS/6f, RS*10f, RS)
				.setSize(RS*10f, RS)
				.setBoundParent(true)
				.setStacked(true)
				.setPassif()
				.set_color_background(Utl.color(0,0))
				.asWidget()
				.setParent(menu_right)
				.setLink(app.data.val_root_savepath)
				.setFont(20).asWidget()
				;

		info_back = gui.addWidget("info_back")
				.setPos(GdxApp.WIDTH - 190,0)
				.asWidget()
				;

		
		nWidget d = add_menu_trigg("File");
		dropmenu_file = gui.addWidgetGroup("dropmenu");
		d.addEventTrigger(new nRun() { public void run() {
			dropmenu_file.metode("open", d); }});
//		nWidget d2 = add_menu_trigg("Build");
//		dropmenu_build = gui.addWidgetGroup("dropmenu");
//		d2.addEventTrigger(new nRun() { public void run() {
//			dropmenu_build.metode("open", d2); }});
//		nWidget d3 = add_menu_trigg("Tool");
//		dropmenu_tool = gui.addWidgetGroup("dropmenu");
//		d3.addEventTrigger(new nRun() { public void run() {
//			dropmenu_tool.metode("open", d3); }});
		
		
		
		toolbox = new nToolBox(this);
		

		nRun run_hb_frame = new nRun() { public void run() {
			if (app.input.mouse.y > menu_back.getLocalY() - menu_back.getLocalSY()) {
				menu_back.show();
				if (!menu_bar_visible) {
					menu_bar_visible = true;
					updateFreeview();
				}
			} else {
				menu_back.hide();
				if (menu_bar_visible) {
					menu_bar_visible = false;
					updateFreeview();
				}
			}
//			if (app.input.mouse.y < bar_back.getLocalY() + bar_back.getLocalSY()) {
//				bar_back.show();
//			} else {
//				bar_back.hide();
//			}
		}};
		nRun run_hi_frame = new nRun() { public void run() {
			if (app.input.mouse.x > info_back.getLocalX() && 
					app.input.mouse.y < info_back.getLocalY() + info_back.getSY()) {
				info_back.show();
			} else {
				info_back.hide();
			}
		}};
		nRun run_h = new nRun() { public void run() {
			if (val_hide_bar.get()) {
				if (!app.runFrameStart.contains(run_hb_frame))
					app.addRunFrameStart(run_hb_frame);
			} else {
				app.removeRunFrameStart(run_hb_frame);
				menu_back.show();
				if (!menu_bar_visible) {
					menu_bar_visible = true;
					updateFreeview();
				}
//				bar_back.show();
			}
			if (val_hide_info.get()) {
				if (!app.runFrameStart.contains(run_hi_frame))
					app.addRunFrameStart(run_hi_frame);
			} else {
				app.removeRunFrameStart(run_hi_frame);
				info_back.show();
			}
		}};
		app.addEventNextFrame(new nRun() { public void run() {
			run_h.run();
			val_hide_bar.addEventChangeLastFrame(run_h);
			val_hide_info.addEventChangeLastFrame(run_h); }});

		app.gdx.addEventScreen(new nRun() { public void run() {
			info_back.setPos(app.gdx.getscreenwidth() - 190,0); 
			menu_right.force_calc_child();
			menu_right.setPos(app.gdx.getscreenwidth() - menu_right.getSX(),RS/6f);
//			close.setRect(app.gdx.getscreenwidth() - RS*4f - RS/6f, RS/6f, RS*4f, RS);
//			fullscreen.setRect(app.gdx.getscreenwidth() - 14f*RS/6f, RS/6f, RS, RS);
//			save_path_viewer.setRect(app.gdx.getscreenwidth() - RS*20f, RS/6f, RS*10f, RS);
//			hidebar.setRect(app.gdx.getscreenwidth() - 21f*RS/6f, RS/6f, RS, RS);
//			hideinfo.setRect(app.gdx.getscreenwidth() - 28f*RS/6f, RS/6f, RS, RS);
//			fx.setRect(app.gdx.getscreenwidth() - 35f*RS/6f, RS/6f, RS, RS);
			
			updateFreeview();
		}});
		
//		add_shortcut_target("Fullscreen", 'M', new nRun() { public void run() {
//			app.gdx.switchscreen(); }});
		

		add_info_text("fps:", app.input.val_framerate);
		if (!app.config.RELEASE) {
			add_info_text("mouse:", app.input.val_mouse_pos);
			add_info_text("javHeap:", app.input.val_javaHeap);
//			add_info_text("natHeap:", app.input.val_nativeHeap);
		}

		
		
		
//		add_file_menu_trigg("open last", new nRun() { public void run() {
//			app.data.re_full_load(); }});
//		add_file_menu_trigg("open ...", new nRun() { public void run() {
//			pop_loadfrom(); }});
//
//		add_file_menu_separator();

		add_file_menu_trigg("save", new nRun() { public void run() { 
			app.data.full_save(); }});
//		add_file_menu_trigg("save to", new nRun() { public void run() {
////			pop_saveas(); 
//		}});

		add_file_menu_separator();
		
//		add_file_menu_trigg("Settings", new nRun() { public void run() {
////			pop_setting(); 
//		}});
		
		add_file_menu_trigg("Shortcut", new nRun() { public void run() {
			gui.pop_shortcut(); }});

//		add_file_menu_trigg("Book Explo", new nRun() { public void run() {
//			pop_book_explo(); 
//		}});

		//TODO a bouger dans titleScreen
//		add_file_menu_trigg("About", new nRun() { public void run() {
//			pop_about(); }});

		add_file_menu_separator();
		
		add_file_menu_trigg("Exit", new nRun() { public void run() {
			app.gdx.close_app(); }});

	}
	
	public nWidget add_file_menu_trigg(String t, nRun r) {
		nWidget w1 = (nWidget)dropmenu_file.metodeGet("add_entry", t);
		w1.addEventTrigger(r);
		return w1;
	}
	public void add_file_menu_separator() {
		dropmenu_file.metode("add_separator");
	}
	
	public nWidget add_menu_trigg(String t) {
		return gui.addWidget("menu_trigg",t)
		.setParent(gui.menu_ref)
		;
	}
	public nWidget add_menu_trigg(String t, nRun r) {
		return gui.addWidget("menu_trigg",t)
		.addEventTrigger(r)
		.setParent(gui.menu_ref)
		;
	}

	public nWidget add_info_text(String t) {
		return gui.addWidget("info_text")
		.setText(t)
		.setParent(info_back)
		;
	}
	public nWidget add_info_text(String t, sBoo v) {
		return gui.addWidget("info_text")
		.setWatcher(t, v, "")
		.setParent(info_back)
		;
	}
	public nWidget add_info_text(String t, sInt v) {
		return gui.addWidget("info_text")
		.setWatcher(t, v, "")
		.setParent(info_back)
		;
	}
	public nWidget add_info_text(String t, sFlt v) {
		return gui.addWidget("info_text")
		.setWatcher(t, v, "")
		.setParent(info_back)
		;
	}
	public nWidget add_info_text(String t, sVec v) {
		return gui.addWidget("info_text")
		.setWatcher(t, v, "")
		.setParent(info_back)
		;
	}
	public nWidget add_info_text(String t, sStr v) {
		return gui.addWidget("info_text")
		.setWatcher(t, v, "")
		.setParent(info_back)
		;
	}
	
	
	
}
