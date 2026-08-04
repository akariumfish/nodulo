package data;

import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

import app.Applet;
import gui.nAlign;
import gui.nInterface;
import gui.nWidget;
import gui.nWidgetGroup;
import util.Utl;
import util.nRun;

public class sDataGUI {

	
	

	public static void pop_saveas(Applet app) {
		
		nInterface interf = app.menu.get_popWindow();

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
			app.menu.close_popwindow();
		}};
		
		refresh_w.addEventTrigger(new nRun() { public void run() {
			run_list_files.run(); }});
		new_w.addEventTrigger(new nRun() { public void run() {
			run_new_file.run(); }});
		load_w.addEventTrigger(new nRun() { public void run() {
			run_save_file.run(); }});
		
		app.addEventNextFrame(new nRun() { public void run() {
			app.menu.pop_popwindow("Save"); }});
	}
	public static void pop_loadfrom(Applet app) {

		nInterface interf = app.menu.get_popWindow();

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
				if (fl.extension().equals(app.data.file_ext_txt)) {
					interf.add_list_entry(fl.name());
				}
			}
		}};
		run_list_files.run();

		nRun run_load_file = new nRun() { public void run() {
			String file_name = (String)file_list.metodeGet("get_pick");
			if (file_name.length() == 0 || !Utl.file_exist(file_name)) return;
			app.data.val_root_savepath.set(file_name);
			app.data.setting_load();
			app.menu.close_popwindow();
			app.data.re_full_load();
		}};
		
		refresh_w.addEventTrigger(new nRun() { public void run() {
			run_list_files.run(); }});
		load_w.addEventTrigger(new nRun() { public void run() {
			run_load_file.run(); }});
		
		app.addEventNextFrame(new nRun() { public void run() {
			app.menu.pop_popwindow("Load"); }});
	}
	
	

	public static void pop_setting(Applet app) {
		
		float RS = app.gui.book.RS;

		nInterface interf = app.menu.get_popWindow();
		interf.add_row();
		interf.add_row_label(10, "Settings");

		interf.add_col_separator();
		interf.add_col_separator();
		
		interf.setContext(app.data.setting_bloc);

		interf.add_row();
		interf.add_row_label(6, "Database Savepath:");
		interf.add_row_label(4, "");
		interf.add_row();
		interf.add_row_field_str(8, "", "val_datab_savepath");
		nRun run_pick_data = new nRun() { public void run(Object o) {
			String file_name = (String)o;
			app.data.val_datab_savepath.set(file_name); }};
		interf.add_row_trigg(2, "Pick", new nRun() { public void run() {
			pop_pickfile(app, app.data.data_ext_txt, run_pick_data); }});

		interf.add_col_separator();
		interf.add_col_separator();

		interf.add_row();
		interf.add_row_label(6, "Root Savepath:");
		interf.add_row_label(4, "");
		interf.add_row();
		interf.add_row_field_str(8, "", "val_root_savepath");
		nRun run_pick_root = new nRun() { public void run(Object o) {
			String file_name = (String)o;
			app.data.val_root_savepath.set(file_name); }};
		interf.add_row_trigg(2, "Pick", new nRun() { public void run() {
			pop_pickfile(app, app.data.file_ext_txt, run_pick_root);
		}});

		interf.add_col_separator();
		interf.add_col_separator();

		interf.add_row();
		interf.add_row_label(6, "");
		interf.add_row_trigg(4, "Save Settings", new nRun() { public void run() {
			app.data.space_save(app.data.setting_space, 
					app.data.setting_savepath, true); }});
		
		
		interf.add_col();
		interf.add_row();
		interf.add_row_label(10, "Setting Value :");

		interf.add_row();
		nWidgetGroup vallist = interf.add_scrollist(8, 4);
		
		nRun run_update_vllist = new nRun() { public void run() {
			interf.change_current_list(vallist);
			for (Map.Entry<String,sValue> me : 
				app.data.setting_space.root.values.entrySet()) {
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
		app.data.setting_space.root.addEventChangeThisFrame(run_update_vllist);
		run_update_vllist.run();
		
		app.addEventNextFrame(new nRun() { public void run() {
			app.menu.pop_popwindow("Setting"); }});
	}
	

	public static void pop_pickfile(Applet app, String extention, nRun run_pick) {

		nInterface interf = app.menu.get_popWindow();

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
			app.menu.close_popwindow();
		}};

		file_list.metode("set_pick_event", run_pick_file);
		
		app.addEventNextFrame(new nRun() { public void run() {
			app.menu.pop_popwindow("Pick File"); }});
	}
	

	
}
