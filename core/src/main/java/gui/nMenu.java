package gui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import aa_nodulo.PlaneApplet;
import app.App;
import app.GdxApp;
import data.*;
import util.Utl;
import util.nMap;
import util.nRun;

public class nMenu {

	
	public PlaneApplet app;
	public nGUI gui;
	public sData data;

	public nToolBox toolbox;

	public nWidget menu_back;
	public nWidget menu_ref;
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
		if (menu_bar_visible) { s.y -= menu_back.getSY(); }
		freeview.set(p.x,p.y,s.x,s.y);
		nRun.runEvents(freeviewEvent);
	}
	
	public nMenu(PlaneApplet a) {
		app = a;
		app.menu = this;
		gui = a.gui;
		data = a.data;
		
		build_help();
		
		float RS = nGUI.book.RS;
		
		menu_back = gui.addWidget("menu_back")
				.setRect(0,GdxApp.HEIGHT - 4f*RS/3f,GdxApp.WIDTH,4f*RS/3f)
				.asWidget()
				;
		
		menu_ref = gui.addWidget("menu_ref")
				.asWidget()
				.setParent(menu_back)
				;
		
		close = gui.addWidget("ref")
				.setRect(GdxApp.WIDTH - 7f*RS/6f, RS/6f, RS, RS)
				.setFont(20)
				.setTrigger()
				.setText("X")
				.asWidget()
				.setParent(menu_back)
				.addEventTrigger(new nRun() { public void run() {
					pop_exit(); }})
				;

		fullscreen = gui.addWidget("ref")
				.setRect(GdxApp.WIDTH - 14f*RS/6f, RS/6f, RS, RS)
				.setFont(20)
				.setSwitch()
				.setText("Fs")
				.asWidget()
				.setParent(menu_back)
				.setLink(app.input.val_fullscreen)
				;

		val_hide_bar = app.data.setting_bloc.newBoo("val_hide_bar", true);

		hidebar = gui.addWidget("ref")
				.setRect(GdxApp.WIDTH - 21f*RS/6f, RS/6f, RS, RS)
				.setFont(20)
				.setSwitch()
				.setText("M")
				.asWidget()
				.setParent(menu_back)
				.setLink(val_hide_bar)
				;

		val_hide_info = app.data.setting_bloc.newBoo("val_hide_info", false);

		hideinfo = gui.addWidget("ref")
				.setRect(GdxApp.WIDTH - 28f*RS/6f, RS/6f, RS, RS)
				.setFont(20)
				.setSwitch()
				.setText("I")
				.asWidget()
				.setParent(menu_back)
				.setLink(val_hide_info)
				;

		val_fx = app.data.setting_bloc.newBoo("val_fx", app.config.START_FX);
		val_fx.addEventChangeLastFrame(new nRun() { public void run() {
			app.use_fx(val_fx.get()); }});
		
		fx = gui.addWidget("ref")
				.setRect(GdxApp.WIDTH - 35f*RS/6f, RS/6f, RS, RS)
				.setFont(20)
				.setSwitch()
				.setText("FX")
				.asWidget()
				.setParent(menu_back)
				.setLink(val_fx)
				;
		
		save_path_viewer = gui.addWidget("ref")
				.setRect(GdxApp.WIDTH - RS*16f, RS/6f, RS*10f, RS)
				.setPassif()
				.set_color_background(Utl.color(0,0))
				.asWidget()
				.setParent(menu_back)
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
		

//		bar_entrys = new ArrayList<nWidget>();
//		
//		bar_back = gui.addWidget("taskbar_back")
//				.setRect(0,0,GdxApp.WIDTH,RS+10)
//				.setDrawstackPriority(true)
//				.asWidget()
//				;
//		bar_ref = gui.addWidget("taskbar_ref")
//				.asWidget()
//				.setParent(bar_back)
//				;

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
			menu_back.setRect(0,app.gdx.getscreenheight() - 4f*RS/3f,app.gdx.getscreenwidth(),4f*RS/3f); 
			info_back.setPos(app.gdx.getscreenwidth() - 190,0); 
			close.setRect(app.gdx.getscreenwidth() - 7f*RS/6f, RS/6f, RS, RS);
			fullscreen.setRect(app.gdx.getscreenwidth() - 14f*RS/6f, RS/6f, RS, RS);
			save_path_viewer.setRect(app.gdx.getscreenwidth() - RS*16f, RS/6f, RS*10f, RS);
			hidebar.setRect(app.gdx.getscreenwidth() - 21f*RS/6f, RS/6f, RS, RS);
			hideinfo.setRect(app.gdx.getscreenwidth() - 28f*RS/6f, RS/6f, RS, RS);
			fx.setRect(app.gdx.getscreenwidth() - 35f*RS/6f, RS/6f, RS, RS);
//			bar_back.setRect(0,0,app.gdx.getscreenwidth(),RS+10); 
//			if (app.gdx.isfullscreen()) close.show(); else close.hide();
			updateFreeview();
		}});
		
//		add_shortcut_target("Fullscreen", 'M', new nRun() { public void run() {
//			app.gdx.switchscreen(); }});
		

		app.addRunFrameStart(new nRun() { public void run() {
			update_shortcut(); }});
		
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
		add_file_menu_trigg("save to", new nRun() { public void run() {
			pop_saveas(); }});

		add_file_menu_separator();
		
		add_file_menu_trigg("Settings", new nRun() { public void run() {
			pop_setting(); }});
		
		add_file_menu_trigg("Shortcut", new nRun() { public void run() {
			pop_shortcut(); }});

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
		.setParent(menu_ref)
		;
	}
	public nWidget add_menu_trigg(String t, nRun r) {
		return gui.addWidget("menu_trigg",t)
		.addEventTrigger(r)
		.setParent(menu_ref)
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
	
	

	public nMap<Character> shortcut_key = new nMap<Character>();
	public nMap<nRun> shortcut_run = new nMap<nRun>();
	public nMap<nRun> shortcut_state_run = new nMap<nRun>();
	public void add_shortcut_target(String ref, char def, nRun run) {
		shortcut_key.put(ref, def);
		if (run != null) shortcut_run.put(ref, run);
	}
	public void add_shortcut_target(String ref, char def, nRun run, nRun srun) {
		shortcut_key.put(ref, def);
		if (run != null) shortcut_run.put(ref, run);
		if (srun != null) shortcut_state_run.put(ref, srun);
	}

	public void remove_shortcut_target(String ref, char def, nRun run) {
		shortcut_key.remove(ref);
		shortcut_run.remove(ref);
		shortcut_state_run.remove(ref);
	}

	public void update_shortcut() {
		for (Map.Entry<String,Character> me : shortcut_key.entrySet()) {
			if (app.input.do_shortcut && !app.gui.field_used && 
					(app.input.getClick(me.getValue()) || 
							app.input.getUnClick(me.getValue())) && 
					shortcut_run.get(me.getKey()) != null) {
				shortcut_run.get(me.getKey()).run();
				shortcut_run.get(me.getKey()).run(app.input.getState(me.getValue()));
			}
				
			if (app.input.do_shortcut && !app.gui.field_used && 
					shortcut_state_run.get(me.getKey()) != null) 
				shortcut_state_run.get(me.getKey()).run(app.input.getState(me.getValue()));
		}
	}
	
	

//	nWidget bar_back, bar_ref;
//	public ArrayList<nWidget> bar_entrys;
//	
//	public nWidget add_taskbar_entry() {
//		
//		nWidget w = gui.addWidget("taskbar_entry")
//		.setParent(bar_ref);
//		
//		for(nWidget n : bar_entrys) n.setOff();
//		
//		w.addEventSwitchOn(new nRun() { public void run() {
//			for(nWidget n : bar_entrys) if (n != w) n.setOff(); }});
//		
//		bar_entrys.add(w);
//		return w;
//	}
//	public void remove_taskbar_entry(nWidget w) {
//		bar_entrys.remove(w); }
	
	

	public void pop_shortcut() {

		nInterface interf = gui.get_popWindow();

		interf.add_row();
		interf.add_row_label(10,"Shortcut : ");
		
		interf.add_row();
		nWidgetGroup list = interf.add_picklist(8,4);
		
		interf.change_current_list(list);
		for (Map.Entry<String,Character> me : shortcut_key.entrySet()) {
			String ref = me.getKey();
			nWidget w = interf.add_list_entry(ref);
			w.setTextAlignment(nAlign.LEFT, nAlign.CENTER);
			
			float RS = nGUI.book.RS;
			nWidget fld_w = interf.get_row_button_widget(4);
			fld_w.setParent(w)
			.setField(true)
			.setText(""+me.getValue())
			.setStacked(false)
			.setRect(13f*RS/2f, 0, 4f*RS/2f, RS);
			fld_w.addEventFieldChange(new nRun() { public void run() {
				char c = shortcut_key.get(ref);
				String fs = fld_w.getText();
				if (fs.length() > 0) c = fs.charAt(0);
				shortcut_key.remove(ref);
				shortcut_key.put(ref, c);
				fld_w.setText(""+c);
			}});
		}
		
		app.addEventNextFrame(new nRun() { public void run() {
			gui.pop_popwindow("Shortcut"); }});
	}

	

	public void pop_exit() {

		nInterface interf = gui.get_popWindow();
		interf.add_row();
		interf.add_row_label(10, "");
		interf.add_row();
		interf.add_row_trigg(5, "Exit", new nRun() {public void run() { 
			app.gdx.close_app(); }});
		interf.add_row_label(1, "");
		interf.add_row_trigg(5, "Title", new nRun() {public void run() { 
			gui.close_popwindow(); app.gdx.to_title(); }});
		interf.add_row_label(1, "");
		interf.add_row_trigg(5, "Cancel", new nRun() {public void run() { 
			gui.close_popwindow(); }});
		interf.add_row();
		interf.add_row_label(10, "");
		gui.pop_popwindow("Exit ?");
		
	}

	public void pop_saveas() {
		
		nInterface interf = gui.get_popWindow();

		interf.add_row();
		interf.add_row_label(7," Select File : ");
		nWidget refresh_w = interf.add_row_trigg(3,"REFRESH");
		interf.add_row();
		nWidgetGroup file_list = interf.add_picklist(8,4);
		interf.add_row();
		interf.add_row_label(2,"New :");
		nWidget new_f_w = interf.add_row_field(6,"");
		nWidget new_w = interf.add_row_trigg(2,"NEW");
		
		interf.add_row();
		interf.add_row_label(6,"");
		nWidget load_w = interf.add_row_trigg(4,"SAVE TO");

		interf.add_col_separator();

		nRun run_list_files = new nRun() { public void run() {
			interf.change_current_list(file_list);
			FileHandle[] files = Gdx.files.local("/").list();
			for(FileHandle fl : files) {
				if (fl.extension().equals(app.data.file_ext_txt)) { 
					nWidget le = interf.add_list_entry(fl.name());
					if (app.data.val_root_savepath.get().equals(fl.name())) le.setOn();
				}
			}
		}};
		run_list_files.run();

		nRun run_new_file = new nRun() { public void run() {
			String file_name = new_f_w.getText();
			if (file_name.length() == 0) return;
			file_name += sData.file_extension;
			FileHandle fl = Gdx.files.local(file_name);
			if (!fl.exists()) fl.writeString(" ", false);
			app.data.val_root_savepath.set(file_name);
			run_list_files.run();
		}};
		
		nRun run_save_file = new nRun() { public void run() {
			String file_name = (String)file_list.metodeGet("get_pick");
			if (file_name.length() == 0 || !Utl.file_exist(file_name)) return;
			app.data.val_root_savepath.set(file_name);
			app.data.full_save();
			gui.close_popwindow();
		}};
		
		refresh_w.addEventTrigger(new nRun() { public void run() {
			run_list_files.run(); }});
		new_w.addEventTrigger(new nRun() { public void run() {
			run_new_file.run(); }});
		load_w.addEventTrigger(new nRun() { public void run() {
			run_save_file.run(); }});
		
		app.addEventNextFrame(new nRun() { public void run() {
			gui.pop_popwindow("Save"); }});
	}
	public void pop_loadfrom() {

		nInterface interf = gui.get_popWindow();

		interf.add_row();
		interf.add_row_label(7," Select File : ");
		nWidget refresh_w = interf.add_row_trigg(3,"REFRESH");
		interf.add_row();
		nWidgetGroup file_list = interf.add_picklist(8,4);
		
		interf.add_row();
		interf.add_row_label(6,"");
		nWidget load_w = interf.add_row_trigg(4,"LOAD");

		interf.add_col_separator();

		nRun run_list_files = new nRun() { public void run() {
			interf.change_current_list(file_list);
			FileHandle[] files = Gdx.files.local("/").list();
			for(FileHandle fl : files) {
				if (fl.extension().equals(data.file_ext_txt)) {
					interf.add_list_entry(fl.name());
				}
			}
		}};
		run_list_files.run();

		nRun run_load_file = new nRun() { public void run() {
			String file_name = (String)file_list.metodeGet("get_pick");
			if (file_name.length() == 0 || !Utl.file_exist(file_name)) return;
			data.val_root_savepath.set(file_name);
			data.setting_load();
			gui.close_popwindow();
			data.re_full_load();
		}};
		
		refresh_w.addEventTrigger(new nRun() { public void run() {
			run_list_files.run(); }});
		load_w.addEventTrigger(new nRun() { public void run() {
			run_load_file.run(); }});
		
		App.ap.addEventNextFrame(new nRun() { public void run() {
			gui.pop_popwindow("Load"); }});
	}
	
	

	public void pop_setting() {
		
		float RS = nGUI.book.RS;

		nInterface interf = gui.get_popWindow();
		interf.add_row();
		interf.add_row_label(10, "Settings");

		interf.add_col_separator();
		interf.add_col_separator();
		
		interf.setContext(data.setting_bloc);

		interf.add_row();
		interf.add_row_label(6, "Database Savepath:");
		interf.add_row_label(4, "");
		interf.add_row();
		interf.add_row_field_str(8, "", "val_datab_savepath");
		nRun run_pick_data = new nRun() { public void run(Object o) {
			String file_name = (String)o;
			data.val_datab_savepath.set(file_name); }};
		interf.add_row_trigg(2, "Pick", new nRun() { public void run() {
			pop_pickfile(sData.data_ext_txt, run_pick_data); }});

		interf.add_col_separator();
		interf.add_col_separator();

		interf.add_row();
		interf.add_row_label(6, "Root Savepath:");
		interf.add_row_label(4, "");
		interf.add_row();
		interf.add_row_field_str(8, "", "val_root_savepath");
		nRun run_pick_root = new nRun() { public void run(Object o) {
			String file_name = (String)o;
			data.val_root_savepath.set(file_name); }};
		interf.add_row_trigg(2, "Pick", new nRun() { public void run() {
			pop_pickfile(sData.file_ext_txt, run_pick_root);
		}});

		interf.add_col_separator();
		interf.add_col_separator();

		interf.add_row();
		interf.add_row_label(6, "");
		interf.add_row_trigg(4, "Save Settings", new nRun() { public void run() {
			data.space_save(data.setting_space, 
					data.setting_savepath, true); }});
		
		
		interf.add_col();
		interf.add_row();
		interf.add_row_label(10, "Setting Value :");

		interf.add_row();
		nWidgetGroup vallist = interf.add_scrollist(8, 4);
		
		nRun run_update_vllist = new nRun() { public void run() {
			interf.change_current_list(vallist);
			for (Map.Entry<String,sValue> me : 
				data.setting_space.root.values.entrySet()) {
				sValue val = me.getValue();
				String key = me.getKey();
				nWidget w = interf.add_list_entry(
						key + " : " + val.getString());
				w.setTextAlignment(nAlign.LEFT, nAlign.CENTER);
				
				if (val.isBoo()) {
					nWidget bp_w = interf.get_row_button_widget(3);
					bp_w.setParent(w)
					.setLink((sBoo)val)
					.setStacked(false)
					.setText("I/O")
					.setRect(15f*RS/2f, 0, 3f*RS/2f, RS)
					.setSwitch();
				}
			}
		}};
		data.setting_space.root.addEventChangeThisFrame(run_update_vllist);
		run_update_vllist.run();
		
		App.ap.addEventNextFrame(new nRun() { public void run() {
			gui.pop_popwindow("Setting"); }});
	}
	

	public void pop_pickfile(String extention, nRun run_pick) {

		nInterface interf = gui.get_popWindow();

		interf.add_col_separator();

		interf.add_row();
		interf.add_row_label(10," Select File : ");
		interf.add_row();
		nWidgetGroup file_list = interf.add_picklist(8,4);
		FileHandle[] files = Gdx.files.local("/").list();
		for(FileHandle fl : files) {
			if (fl.extension().equals(extention)) {
				interf.add_list_entry(fl.name());
			}
		}
		
		interf.add_col_separator();

		nRun run_pick_file = new nRun() { public void run(Object o) {
			String file_name = (String)o;
			if (file_name.length() == 0 || !Utl.file_exist(file_name)) return;
			run_pick.run(file_name);
			gui.close_popwindow();
		}};

		file_list.metode("set_pick_event", run_pick_file);
		
		App.ap.addEventNextFrame(new nRun() { public void run() {
			gui.pop_popwindow("Pick File"); }});
	}
	

	//TODO a refaire avec sceneéd.ui dans un autre Screen
//	public void pop_book_explo() {
//		nInterface interf = gui.get_popWindow();
//
//		interf.add_row();
//		interf.add_row_label(10, "Book Exploration");
//		interf.add_row();
//		interf.add_row_label(10, "Model nb: "+app.gui.book.models.size() + 
//				"  ModelGroup nb: "+app.gui.book.modelgroups.size());
//
//		interf.add_row();
//		interf.add_row_label(10, "nWidget Models :");
//		interf.add_row();
//		nWidgetGroup list = interf.add_treelist(10, 12);
//		
//		interf.change_current_list(list);
//
//		ArrayList<String> mod = new ArrayList<String>();
//		for (Map.Entry<String,nModel> me : app.gui.book.models.entrySet()) {
//			mod.add(me.getKey());
//		}
//		Collections.sort(mod);
//		for (String s : mod) {
//			interf.add_list_entry(s);
//			interf.go_up_tree();
//		}
//
//		interf.add_col();
//		
//		interf.add_row();
//		interf.add_row_label(10, "nWidgetGroup ModelGroups :");
//		interf.add_row();
//		nWidgetGroup list2 = interf.add_treelist(10, 14);
//		
//		interf.change_current_list(list2);
//
//		mod.clear();
//		for (Map.Entry<String,nModelGroup> me : app.gui.book.modelgroups.entrySet()) {
//			mod.add(me.getKey());
//		}
//		Collections.sort(mod);
//		for (String s : mod) {
//			interf.add_list_entry(s);
//			interf.go_up_tree();
//		}
//		
//		gui.pop_popwindow("Book Explo");
//	}
	

	//TODO a refaire avec sceneéd.ui dans un autre Screen
//	public void pop_about() {
//		
//		String about = "Eeeeeeeeeeeeeee\n" + 
//				"eeeeeeeeeeeeeee\n" + 
//				"eeeeeeeeeeeeeee\n" + 
//				"eeeeeeeeeeeeeee\n" + 
//				"eeeeeeeeeeeeeee\n" + 
//				"eeeeeeeeeeeeeee\n" + 
//				"eeeeeeeeeeeeeee\n" + 
//				"eeeeeeeeeeeeeee\n" + 
//				"eeeeeeeeeeeeeee\n" + 
//				"eeeeeeeeeeeeeee\n" + 
//				"eeeeeeeeeeeeeee\n" + 
//				"eeeeeeeeeeeeeee\n" + 
//				"Eeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee"
//				+ "eeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee"
//				+ "eeeeeeeeeeeeeeeeeeeeE\n" + 
//				"eeeeeeeeeeeeeee\n" + 
//				"eeeeeeeeeeeeeee\n" + 
//				"eeeeeeeeeeeeeee\n" + 
//				"eeeeeeeeeeeeeee\n" + 
//				"eeeeeeeeeeeeeee\n" + 
//				"eeeeeeeeeeeeeee\n" + 
//				"eeeeeeeeeeeeeee\n" + 
//				"eeeeeeeeeeeeeeE\n" + 
//				"E" ;
//		nInterface interf = gui.get_popWindow();
//
//		interf.add_row();
//		
//		nWidgetGroup list = interf.add_scrollist(8, 4);
//		
//		nWidget txt_w = interf.add_list_entry("");
//		txt_w.force_calc();
//		txt_w.setSY(app.textHeight() * txt_w.line_number(about) / 1.1f);
//		txt_w.setText(about);
//		txt_w.setTextAutoReturn(true)
//		.setTextAlignment(nAlign.LEFT, nAlign.BOTTOM);
//		txt_w.force_calc();
//		list.metode("slide_calc");
//		
//		gui.pop_popwindow("  About  ");
//	}
	
	
	
	
	
	
	public void pop_help_page(Help help) {
		
		nInterface interf = gui.get_popWindow();

		interf.add_row();
		
		nWidgetGroup list = interf.add_scrollist(24, 12);

		nWidget txt_w = interf.add_list_entry("");
		String txt = "";
		boolean link = false;
		String link_text = null;
		String link_targ = null;
		for (String t : help.text) {
			if (t.equals("_____LINE")) {
				txt_w.force_calc();
				txt_w.setSY(app.textHeight() * txt_w.line_number(txt) / 1.1f);
				txt_w.setText(txt);
				txt_w.setTextAutoReturn(true)
				.setTextAlignment(nAlign.LEFT, nAlign.BOTTOM);
				txt_w.force_calc();
				txt_w = interf.add_list_entry("");
				txt = "";
				link = false;
				link_text = null;
				link_targ = null;
			} else if (link_text != null && link_targ == null && link) {
				link_targ = t;
				txt_w.force_calc();
				txt_w.setSY(app.textHeight() * txt_w.line_number(txt) / 1.1f);
				txt_w.setText(txt);
				txt_w.setTextAutoReturn(true)
				.setTextAlignment(nAlign.LEFT, nAlign.BOTTOM);
				txt_w.force_calc();
				txt_w = interf.add_list_entry("");
				txt = link_text;
				txt_w.force_calc();
				txt_w.setSY(app.textHeight() * txt_w.line_number(txt) / 1.1f);
				txt_w.setText(txt);
				txt_w.setTextAutoReturn(true)
				.setTextAlignment(nAlign.LEFT, nAlign.BOTTOM);
				txt_w.setTrigger();
				txt_w.addEventTrigger(new nRun(Utl.copy(link_targ)) {public void run() {
					popHelp((String)builder); }});
				txt_w.force_calc();
				txt_w = interf.add_list_entry("");
				txt = "";
				link = false;
				link_text = null;
				link_targ = null;
			} else if (link && link_text == null) {
				link_text = t;
			} else if (t.equals("_____LINK")) {
				link = true;
			} else {
				txt += t;
			}
		}
		list.metode("slide_calc");
		
		gui.pop_popwindow("  Help : "+help.ref);
	}
	
	public static class Help {
		public String ref;
		public ArrayList<String> text = new ArrayList<String>();
		public Help(String r, String t) {
			ref = r; text.add(t);
			helps.put(r,this);
		}
		public Help text(String t) { text.add(t); return this; }
		public Help link(String link_txt, String link_targ) { 
			text.add("_____LINK"); text.add(link_txt); text.add(link_targ); return this; }
		public Help line() { text.add("_____LINE"); return this; }
	}
	
	public static final nMap<Help> helps = new nMap<Help>();
	
	public static Help newHelp(String r, String t) {
		Help h = new Help(r,t);
		return h;
	}
	public void popHelp(String r) {
		pop_help_page(helps.get(r));
	}
	

	public boolean HELP_VIEW = false;
	public void build_help() {
		
		gui.do_help = app.config.START_HELP;
		
		add_shortcut_target("Help - view interest", 'H', null, 
				new nRun() {public void run(Object o) {
			boolean b = (boolean)o; 
			HELP_VIEW = b;// && val_help.get(); 
			app.gui.do_help = HELP_VIEW; }});
		
	}


	
	static void build_book() {
		nModelBook book = nGUI.book;
		float RS = book.RS;
		
		
		book.newModel("title_screen_back")
		.copyFrom(book.getModel("ref"))
		.setBackground()
		.setDrawstackPriority(true)
		;

		book.newModel("title_screen_stack")
		.copyFrom(book.getModel("ref"))
		.setPassif()
		.set_color_background(Utl.color(0,0,0,0))
		.setBoundChild(true)
		.setStackAxis(nAlign.VERTICAL) // HORIZONTAL   VERTICAL
		.setStackDirection(nAlign.DOWN) // RIGHT   LEFT   UP   DOWN
		.setRectOrigin(nAlign.CENTER,nAlign.CENTER) // TOP   BOTTOM
		.setBoundOutspace(RS/6f)
		.setStackSpacing(RS/5f)
		;
		
		book.newModel("title_screen_title")
		.copyFrom(book.getModel("ref"))
		.setRect(0,0,24f*RS,6f*RS)
		.setBoundParent(true)
		.setStacked(true)
		.setFont(90)
		.set_color_text(Utl.color(220,220,218))
		.set_color_background(Utl.color(12,10,10))
		.set_color_outline(Utl.color(200))
		.setOutline(true)
		.setOutlineWeight(RS/4f)
		;
		book.newModel("title_screen_trigg")
		.copyFrom(book.getModel("ref"))
		.setRect(0,0,8f*RS,2f*RS)
		.setBoundParent(true)
		.setStacked(true)
		.setFont(30)
		.setTrigger()
		;
		
		
		
		
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
