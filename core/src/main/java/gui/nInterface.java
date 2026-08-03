package gui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.utils.Pool.Poolable;

import app.Applet;
import data.*;
import util.Utl;
import util.nRun;

public class nInterface  implements Poolable {
	

	public HashMap<String, nRun> metodes;
	public HashMap<String, Object> objects;
	

	public nInterface addObject(String ref, Object r) {
		if (r != null) objects.put(ref, r); return this; }
	public nInterface removeObject(String ref, Object r) {
		if (r != null) objects.remove(ref, r); return this; }
	public Object getObject(String ref) {
		return objects.get(ref); }
	public nInterface setObject(String ref, Object r) {
		if (r != null) {
			Object old = getObject(ref);
			if (old != null) removeObject(ref, old);
			objects.put(ref, r); 
		}
		return this; 
	}
	
	public nInterface addMetode(String ref, nRun r) {
		metodes.put(ref, r); return this; }
	public nInterface metode(String ref) {
		metodes.get(ref).run(); return this; }
	public nInterface metode(String ref, Object o) {
		metodes.get(ref).run(o); return this; }
	public nInterface metode(String ref, Object o1, Object o2) {
		metodes.get(ref).run(o1, o2); return this; }
	public Object metodeGet(String ref) {
		return metodes.get(ref).get(); }
	public Object metodeGet(String ref, Object o) {
		return metodes.get(ref).get(o); }
	
	

	public HashMap<String, String> params;
	private void init_params() {
		params.clear();
		for (Map.Entry<String, String> me : handler.params_def.entrySet()) 
			params.put(me.getKey(), me.getValue());
	}
	
	

	public Applet app;
//	public nInterfaceHandler handler;
	public nGUI handler;
	
	public nGUI gui = null;
	public nWidgetGroup group = null;
	
	public boolean is_pop = false;

	public nInterface() {

		metodes = new HashMap<String, nRun>();
		objects = new HashMap<String, Object>();
		
		params = new HashMap<String, String>();
		
	}

	// called after obtained from pool
//	public nInterface init(nInterfaceHandler g) {
	public nInterface init(nGUI g) {
		
		handler = g; 
		app = g.app;
		
		gui = g;
		group = null;
		is_pop = false;
		line_nb = 0; col_nb = 0; row_nb = 0; ent_nb = 0;
		current_line = null; current_col = null; current_row = null;
		clearing = false;
		context_bloc = null;
		command_addition_index = 0;
		eventNewCommand.clear(); 
		build_commands.clear();
		
		init_params();
		
		return this;
	}
	
	public boolean clearing = true;

	// to call to delete this
	public void clear() {
		if (!clearing) {
			clearing = true;
			
			if (is_pop) clear_ui();

			metodes.clear(); 
			objects.clear(); 
			is_pop = false;
			eventNewCommand.clear(); 
			build_commands.clear();
			params.clear();
			context_bloc = null;
			command_addition_index = 0;
			line_nb = 0; col_nb = 0; row_nb = 0; ent_nb = 0;
			current_line = null; current_col = null; current_row = null;
			

			handler.interf_pool.free(this);
		}
		
	}

	//called when freed by the pool
	public void reset() { }
	
	
	

	
	// pop it in a gui
	public nInterface pop(nGUI g) {
		if (!is_pop) {
			gui = g;
			is_pop = true;
			build_ui();
		}
		return this;
	}
	// pop it in a widget
	public nInterface pop(nWidget g) {
		if (!is_pop) {
			pop(g.gui);
			ref.setParent(g);
			g.addEventClear(new nRun() { public void run() { clear(); }});
		}
		return this;
	}
	// pop it in a widgetgroup with a back
	public nInterface pop(nWidgetGroup g) {
		if (!is_pop) {
			pop(g.gui);
			ref.setParent(g.get("back"));
			g.addEventClear(new nRun() { public void run() { clear(); }});
		}
		return this;
	}
	// pop model in a widgetgroup with a back
	public nInterface pop(nWidgetGroup g, nInterfModel m) {
		if (!is_pop) {
			pop(g);
			build_from_model(m); }
		return this;
	}
	
	
	public enum Code { 	LINE, COL, ROW, 
				COL_SEP, COL_LAB, COL_TRI, COL_SWT_R, COL_SWT_B, 
				ROW_LAB, ROW_TRI, ROW_TRI_M, ROW_SWT_R, ROW_SWT_B, 
				COL_FLD_S, ROW_FLD, ROW_FLD_S, ROW_WTC, ROW_FLD_F,  //field, can link str / flt
				COL_INC_I, COL_INC_F, COL_FAC_F, 
				ROW_TRI_BLD,
				ROW_SLD, ROW_SLD_F, ROW_SLD_I, 
				PARAM, PARAM_DEF,
				SCROLLIST, PICKLIST, TREELIST,
				CONTEXT; }
	
	public static final String[] codeKey = { 	
			"LINE", "COL", "ROW", 
			"COL_SEP", "COL_LAB", "COL_TRI", "COL_SWT_R", "COL_SWT_B", 
			"ROW_LAB", "ROW_TRI", "ROW_TRI_M", "ROW_SWT_R", "ROW_SWT_B", 
			"COL_FLD_S", "ROW_FLD", "ROW_FLD_S", "ROW_WTC", "ROW_FLD_F", 
			"COL_INC_I", "COL_INC_F", "COL_FAC_F", 
			"ROW_TRI_BLD" , 
			"ROW_SLD", "ROW_SLD_F", "ROW_SLD_I",
			"PARAM", "PARAM_DEF",
			"SCROLLIST", "PICKLIST", "TREELIST", 
			"CONTEXT" };

	public static final Code[] codeArray = { 	
			Code.LINE, Code.COL, Code.ROW, 
			Code.COL_SEP, Code.COL_LAB, Code.COL_TRI, Code.COL_SWT_R, Code.COL_SWT_B, 
			Code.ROW_LAB, Code.ROW_TRI, Code.ROW_TRI_M, Code.ROW_SWT_R, Code.ROW_SWT_B, 
			Code.COL_FLD_S, Code.ROW_FLD, Code.ROW_FLD_S, Code.ROW_WTC, Code.ROW_FLD_F, 
			Code.COL_INC_I, Code.COL_INC_F, Code.COL_FAC_F, 
			Code.ROW_TRI_BLD, 
			Code.ROW_SLD, Code.ROW_SLD_F, Code.ROW_SLD_I, 
			Code.PARAM, Code.PARAM_DEF, 
			Code.SCROLLIST, Code.PICKLIST, Code.TREELIST,
			Code.CONTEXT };
	
	public static final String[][] codeArgs = { 	
			{"","","",""}, {"","","",""}, {"","","",""}, 
			{"","","",""}, {"T","","",""}, {"T","V","",""}, {"T","","",""}, {"T","V","",""}, 
			{"W","T","",""}, {"W","T","",""}, {"W","T","M",""}, {"W","T","",""}, {"W","T","V",""}, 
			{"T","V","",""}, {"W","T","",""}, {"W","T","V",""}, {"W","T","V",""}, {"W","T","V",""}, 
			{"T","V","F",""}, {"T","V","F",""}, {"T","V","F",""}, 
			{"W","T","B",""}, 
			{"W","F","F",""}, {"W","F","F","V"}, {"W","F","F","V"}, 
			{"P","T","",""}, {"P","","",""}, 
			{"","","",""}, {"","","",""}, {"","","",""}, 
			{"","","",""} };

	public static final boolean[] codeShow = { 	// shown in command droplist
			true, true, true, 
			true, false, false, false, false, 
			true, false, true, false, true, 
			false, false, true, true, true, 
			true, true, true, 
			true,
			false, true, true,
			true, true, 
			false, false, false, 
			false };
	
	public static String codeToStr(Code c) {
		for (int i = 0 ; i < codeArray.length ; i++) 
			if (codeArray[i] == c) return codeKey[i];
		return "";
	}
	public static Code strToCode(String s) {
		for (int i = 0 ; i < codeArray.length ; i++) 
			if (codeKey[i].equals(s)) return codeArray[i];
		return null;
	}
	
	public ArrayList<nInterfCommand> build_commands  = new ArrayList<nInterfCommand>();
	public ArrayList<nRun> eventNewCommand  = new ArrayList<nRun>();
	public nInterface addEventNewCommand(nRun r) { eventNewCommand.add(r); return this; }
	public nInterface removeEventNewCommand(nRun r) { eventNewCommand.remove(r); return this; }
	
	public void newCommand(Code code) {
		nInterfCommand c = new nInterfCommand(code);
		build_commands.add(build_commands.size() - command_addition_index, c); 
		if (command_addition_index > 0) rebuild_from_command_list();
		nRun.runEvents(eventNewCommand); }
	public void newCommand(Code code, String arg) {
		nInterfCommand c = new nInterfCommand(code, arg);
		build_commands.add(build_commands.size() - command_addition_index, c); 
		if (command_addition_index > 0) rebuild_from_command_list();
		nRun.runEvents(eventNewCommand); }
	public void newCommand(Code code, String arg1, String arg2) {
		nInterfCommand c = new nInterfCommand(code, arg1, arg2);
		build_commands.add(build_commands.size() - command_addition_index, c); 
		if (command_addition_index > 0) rebuild_from_command_list();
		nRun.runEvents(eventNewCommand); }
	public void newCommand(Code code, String arg1, String arg2, String arg3) {
		nInterfCommand c = new nInterfCommand(code, arg1, arg2, arg3);
		build_commands.add(build_commands.size() - command_addition_index, c); 
		if (command_addition_index > 0) rebuild_from_command_list();
		nRun.runEvents(eventNewCommand); }
	public void newCommand(Code code, String arg1, String arg2, String arg3, String arg4) {
		nInterfCommand c = new nInterfCommand(code, arg1, arg2, arg3, arg4);
		build_commands.add(build_commands.size() - command_addition_index, c); 
		if (command_addition_index > 0) rebuild_from_command_list();
		nRun.runEvents(eventNewCommand); }

	public void clearCommands() {
		build_commands.clear(); 
		command_addition_index = 0;
		init_params();
		nRun.runEvents(eventNewCommand); }

	private void build_from_command(nInterfCommand com) {
		if (com.code == Code.LINE) add_line();
		else if (com.code == Code.COL) add_col();
		else if (com.code == Code.COL_SEP) add_col_separator();
		else if (com.code == Code.COL_LAB) add_col_label(com.arg(0));
		else if (com.code == Code.ROW) add_row();
		else if (com.code == Code.ROW_LAB) add_row_label(com.argInt(0), com.arg(1));
		
		// !! add runnable
		else if (com.code == Code.COL_TRI) add_col_trigg(com.arg(0), null); 
		else if (com.code == Code.COL_SWT_R) add_col_switch_run(com.arg(0), null); 
		else if (com.code == Code.ROW_TRI) add_row_trigg(com.argInt(0), com.arg(1), null);
		else if (com.code == Code.ROW_TRI_M) add_row_trigg_met(com.argInt(0), com.arg(1), com.arg(2));
		else if (com.code == Code.ROW_TRI_BLD) add_row_trigg_build(com.argInt(0), com.arg(1), com.arg(2));
		else if (com.code == Code.ROW_SWT_R) add_row_switch_run(com.argInt(0), com.arg(1), null);
		
		// sval
		else if (com.code == Code.COL_SWT_B) add_col_switch_boo(com.arg(0), com.arg(1));
		else if (com.code == Code.ROW_SWT_B) add_row_switch_boo(com.argInt(0), com.arg(1), com.arg(2));
		
		else if (com.code == Code.COL_FLD_S) add_col_field_str(com.arg(0), com.arg(1));
		else if (com.code == Code.ROW_FLD) add_row_field(com.argInt(0), com.arg(1));
		else if (com.code == Code.ROW_FLD_S) add_row_field_str(com.argInt(0), com.arg(1), com.arg(2));
		else if (com.code == Code.ROW_WTC) add_row_watch(com.argInt(0), com.arg(1), com.arg(2));
		else if (com.code == Code.ROW_FLD_F) add_row_field_flt(com.argInt(0), com.arg(1), com.arg(2));
		else if (com.code == Code.COL_INC_I) add_col_incr_int(com.arg(0), com.arg(1), com.arg(2));
		else if (com.code == Code.COL_INC_F) add_col_incr_flt(com.arg(0), com.arg(1), com.arg(2));
		else if (com.code == Code.COL_FAC_F) add_col_fact_flt(com.arg(0), com.arg(1), com.arg(2));
		
		else if (com.code == Code.ROW_SLD) add_row_slide(com.argInt(0), com.argFlt(1), com.argFlt(2));
		else if (com.code == Code.ROW_SLD_F) add_row_slide_flt(com.argInt(0), com.argFlt(1), com.argFlt(2), com.arg(3));
		else if (com.code == Code.ROW_SLD_I) add_row_slide_int(com.argInt(0), com.argFlt(1), com.argFlt(2), com.arg(3));

		else if (com.code == Code.PARAM) set_param(com.arg(0), com.arg(1));
		else if (com.code == Code.PARAM_DEF) set_param_def(com.arg(0));

		else if (com.code == Code.SCROLLIST) add_scrollist(com.argInt(0), com.argInt(1));
		else if (com.code == Code.PICKLIST) add_picklist(com.argInt(0), com.argInt(1));
		else if (com.code == Code.TREELIST) add_treelist(com.argInt(0), com.argInt(1));
		
		else if (com.code == Code.CONTEXT) cmd_context(com.arg(0));
		
	}
	
	private void build_from_command_list(ArrayList<nInterfCommand> coms) {
		for (nInterfCommand c : coms) build_from_command(c);
	}

	public void build_from_model(nInterfModel model) {
		empty();
//		app.addDelayEvent(0, new nRunnable() { public void run() {
			build_from_command_list(model.build_commands);
//		}});
	}

	public void build_from_sTab(sTab tab) {
		empty();
//		app.addDelayEvent(0, new nRunnable() { public void run() {
			ArrayList<nInterfCommand> build_commands  = new ArrayList<nInterfCommand>();
			for (int i = 0 ; i < tab.width() ; i++) {
				nInterface.Code c = nInterface.strToCode(tab.getStr(i, 0));
				nInterfCommand n = new nInterfCommand(c, nInterfCommand.MAX_ARGS);
				for (int j = 0 ; j < nInterfCommand.MAX_ARGS ; j++) {
					n.args[j] = tab.getStr(i, j + 1);
				}
				build_commands.add(n);
			}
			build_from_command_list(build_commands);
//		}});
	}
	
	public void rebuild_from_command_list() {
		if (is_pop) {
			ArrayList<nInterfCommand> coms = new ArrayList<nInterfCommand>();
			for (nInterfCommand c : build_commands) {
				nInterfCommand n = new nInterfCommand(c);
				coms.add(n); }
			int add_ind = command_addition_index;
			empty();
			app.addDelayEvent(6, new nRun() { public void run() {
				build_from_command_list(coms); 
				command_addition_index = add_ind; }});
		}
	}

	public nInterfModel create_model(String r) {
		nInterfModel m = new nInterfModel(r, build_commands);
		return m;
	}
	
	
	
	
	public sValueBloc context_bloc = null;
	
	public void setContext(sValueBloc c) {
		context_bloc = c;
	}
	public int command_addition_index = 0;
	
	public void setAdditionIndex(int c) {
		command_addition_index = c;
	}
	
	public void cmd_context(String adress) {
		newCommand(Code.CONTEXT, adress);
		if (!app.data.blocAdressExist(adress)) return;
		sValueBloc c = app.data.getBlocFromAdress(adress);
		setContext(c);
	}
	
	

	public nWidget ref = null;
	
	public void clear_ui() {
		if (group != null) group.clear();
		group = null;
		ref = null;
	}

	public void build_ui() {
		group = gui.addWidgetGroup("interface");
		ref = group.get("ref");
		group.addWidget("filler", "INT_filler").setParent(ref);
	}
	
	
	
	
	int line_nb = 0, col_nb = 0, row_nb = 0, ent_nb = 0, list_ent_nb = 0; 
	public nWidget current_line = null, current_col = null, current_row = null;
	public nWidgetGroup current_list = null;
	public nWidget current_tree_entry = null;
	
	public nInterface empty() {
		clearCommands();
		if (is_pop) {

			for (nWidget w : group.widgets.tmp_all()) if (w != ref) w.clear();
			for (nWidgetGroup w : group.widgetgroups.tmp_all()) w.clear();
			
//			gui.app.widgetMap.clearAllExcluding(group.widgets, ref);
//			gui.app.widgetgroupMap.clearAll(group.widgetgroups);

			group.widgets.clear(); 
			group.widgets.put("ref", ref); 
			group.widgetgroups.clear();
			group.metodes.clear(); 
			group.objects.clear(); 
			
			group.addWidget("filler", "INT_filler").setParent(ref);
			
			line_nb = 0; col_nb = 0; row_nb = 0; ent_nb = 0; list_ent_nb = 0;
			current_line = null; current_col = null; current_row = null;
			current_list = null;
			current_tree_entry = null;
		}
		return this;
	}
	
	
	

	public nWidgetGroup add_treelist(int width, int height) {
		if (current_row == null) { add_row(); }
		newCommand(Code.TREELIST, Utl.tostr(width), Utl.tostr(height));
		if (is_pop && current_row != null) {
			nWidget ent = add_row_entry(width);
			ent.setSY(ent.getLocalSY() * height);
			
			nWidgetGroup list = gui.addWidgetGroup("treelist");
			int n = 0, cnt = 0;
			String bs = "list_";
			String ns = bs+n;
			while (cnt < 100 && group.hasGroup(ns)) { n++; ns = bs+n; cnt++; }
			group.addWidgetGroup(ns,list);
			list.get("ref").setParent(ent);
			ent.force_calc();
			list.metode("set_height", ent.getSY());
			ent.setBoundChild(true);
			
			current_list = list;
			list_ent_nb = 0;
			
			return list;
		}
		return null;
	}

	public nWidgetGroup add_picklist(int width, int height) {
		if (current_row == null) { add_row(); }
		newCommand(Code.PICKLIST, Utl.tostr(width), Utl.tostr(height));
		if (is_pop && current_row != null) {
			nWidget ent = add_row_entry(width);
			ent.setSY(ent.getLocalSY() * height);
			
			nWidgetGroup list = gui.addWidgetGroup("picklist");
			int n = 0, cnt = 0;
			String bs = "list_";
			String ns = bs+n;
			while (cnt < 100 && group.hasGroup(ns)) { n++; ns = bs+n; cnt++; }
			group.addWidgetGroup(ns,list);
			list.get("ref").setParent(ent);
			ent.force_calc();
			list.metode("set_height", ent.getSY());
			ent.setBoundChild(true);
			
			current_list = list;
			list_ent_nb = 0;
			
			return list;
		}
		return null;
	}

	public nWidgetGroup add_scrollist(int width, int height) {
		if (current_row == null) { add_row(); }
		newCommand(Code.SCROLLIST, Utl.tostr(width), Utl.tostr(height));
		if (is_pop && current_row != null) {
			nWidget ent = add_row_entry(width);
			ent.setSY(ent.getLocalSY() * height);
			
			nWidgetGroup list = gui.addWidgetGroup("scrollist");
			int n = 0, cnt = 0;
			String bs = "list_";
			String ns = bs+n;
			while (cnt < 100 && group.hasGroup(ns)) { n++; ns = bs+n; cnt++; }
			group.addWidgetGroup(ns,list);
			list.get("ref").setParent(ent);
			ent.force_calc();
			list.metode("set_height", ent.getSY());
			list.metode("set_width", ent.getSX());
			ent.setBoundChild(true);
			
			current_list = list;
			list_ent_nb = 0;
			
			return list;
		}
		return null;
	}
	
	public nWidget add_list_entry(String txt) {
		if (current_list != null) {
			if (current_list.model_ref.equals("scrollist")) {
				nWidget w = gui.addWidget("list_entry", txt);
				float height = Utl.tofloat(params.get("entry_height"));
				w.setSY(w.getLocalSY() * height);
				current_list.metodeGet("add_widget_as_entry", 
						current_list.addWidget("list_entry_"+txt+"_"+list_ent_nb, w));
				list_ent_nb++;
				return w;
			} else if (current_list.model_ref.equals("picklist")) {
				nWidget w = (nWidget)current_list.metodeGet("add_pick", txt);
				list_ent_nb++;
				return w;
			} else if (current_list.model_ref.equals("treelist")) {
				if (current_tree_entry == null) {
					nWidget w = (nWidget)current_list.metodeGet("add_entry", txt);
//					w.setSwitch();
					nWidget wh = (nWidget)current_list.metodeGet("add_sub_entry", 
							w, txt+" ");
					wh.set_color_background(Utl.color(0,0));
					wh.setPassif();
					current_tree_entry = w;
					return w;
				} else {
					current_tree_entry.setSwitch();
					nWidget w = (nWidget)current_list.metodeGet("add_sub_entry", 
							current_tree_entry, txt);
//					w.setSwitch();
					nWidget wh = (nWidget)current_list.metodeGet("add_sub_entry", 
							w, txt+" ");
					wh.set_color_background(Utl.color(0,0));
					wh.setPassif();
					current_tree_entry = w;
					return w;
				}
			}
		}
		return null;
	}

	public void go_up_tree() {
		if (current_list != null && current_list.model_ref.equals("treelist") && 
				current_tree_entry != null) {
			if (current_tree_entry.parent != 
					current_list.getGroup("list").get("back"))
				current_tree_entry = current_tree_entry.parent;
			else current_tree_entry = null;
		}
	}
	
	public void change_current_list(nWidgetGroup l) {
		if (current_list != null && current_list.model_ref.equals("treelist")) {
			current_tree_entry = null;
		}
		current_list = l;
		list_ent_nb = 0;
		if (current_list.model_ref.equals("scrollist"))
			current_list.metode("clear_entrys");
		else if (current_list.model_ref.equals("picklist"))
			current_list.metode("clear_pick");
		else if (current_list.model_ref.equals("treelist")) {
			current_list.metode("clear_tree");
			current_tree_entry = null;
		}
	}
	
	
	
	
	
	
	public nInterface set_param(String ref, String data) {
		newCommand(Code.PARAM, ref, data);
		params.replace(ref, data);
		return this;
	}
	public nInterface set_param_def(String ref) {
		newCommand(Code.PARAM_DEF, ref);
		params.replace(ref, handler.params_def.get(ref));
		return this;
	}
	
	public nWidget get_row_entry_widget(int width) { 
		width = (int)(width * Utl.tofloat(params.get("entry_width")));
		
		if (gui.book.getModel(params.get("row_entry_model")+width) == null) {
			gui.book.newModel("INT_row_entry_"+width)
			.copyColorFrom(gui.book.getModel("ref"))
			.setSize(gui.book.RS * width / 2f, gui.book.RS)
			.setBoundParent(true)
			.setStacked(true)
			.setBoundOutspace(0)
			.set_color_background(Utl.color(0,0))
			;
		}
		
		nWidget ent = group.addWidget("ent_"+ent_nb, 
				params.get("row_entry_model")+width);
		ent_nb++;
		float height = Utl.tofloat(params.get("entry_height"));
		ent.setSY(ent.getLocalSY() * height);
		if (!params.get("entry_colors")
				.equals(handler.params_def.get("entry_colors"))) {
			ent.copyColorFrom(gui.book.getModel(params.get("entry_colors")));
		}
		if (params.get("text_align_X").equals("CENTER")) ent.setTextAlignmentX(nAlign.CENTER);
		else if (params.get("text_align_X").equals("LEFT")) ent.setTextAlignmentX(nAlign.LEFT);
		else if (params.get("text_align_X").equals("RIGHT")) ent.setTextAlignmentX(nAlign.RIGHT);
		if (params.get("text_align_Y").equals("CENTER")) ent.setTextAlignmentY(nAlign.CENTER);
		else if (params.get("text_align_Y").equals("TOP")) ent.setTextAlignmentY(nAlign.TOP);
		else if (params.get("text_align_Y").equals("BOTTOM")) ent.setTextAlignmentY(nAlign.BOTTOM);
		return ent;
	}

	public nWidget get_row_button_widget(int width) { 
		width = (int)(width * Utl.tofloat(params.get("entry_width")));
		

		if (gui.book.getModel(params.get("row_entry_button_model")+width) == null) {
			gui.book.newModel("INT_row_entry_"+width)
			.copyColorFrom(gui.book.getModel("ref"))
			.setSize(gui.book.RS * width / 2f, gui.book.RS)
			.setBoundParent(true)
			.setStacked(true)
			.setBoundOutspace(0)
			.set_color_background(Utl.color(0,0))
			;
		}
		
		nWidget ent = group.addWidget("ent_"+ent_nb, 
				params.get("row_entry_button_model")+width);
		ent_nb++;
		float height = Utl.tofloat(params.get("entry_height"));
		ent.setSY(ent.getLocalSY() * height);
		if (!params.get("entry_colors")
				.equals(handler.params_def.get("entry_colors"))) {
			ent.copyColorFrom(gui.book.getModel(params.get("entry_colors")));
		}
		if (params.get("text_align_X").equals("CENTER")) ent.setTextAlignmentX(nAlign.CENTER);
		else if (params.get("text_align_X").equals("LEFT")) ent.setTextAlignmentX(nAlign.LEFT);
		else if (params.get("text_align_X").equals("RIGHT")) ent.setTextAlignmentX(nAlign.RIGHT);
		if (params.get("text_align_Y").equals("CENTER")) ent.setTextAlignmentY(nAlign.CENTER);
		else if (params.get("text_align_Y").equals("TOP")) ent.setTextAlignmentY(nAlign.TOP);
		else if (params.get("text_align_Y").equals("BOTTOM")) ent.setTextAlignmentY(nAlign.BOTTOM);
		return ent;
	}
	
	public nWidget add_row_entry(int width) { 
		if (current_row == null) { add_row(); }
		if (is_pop && current_row != null) {
			nWidget ent = get_row_entry_widget(width);
			ent.setParent(current_row);
			return ent;
		}
		return null;
	}

	public nWidget add_row_entry_button(int width) { 
		if (current_row == null) { add_row(); }
		if (is_pop && current_row != null) {
			nWidget ent = get_row_button_widget(width);
			ent.setParent(current_row);
			return ent;
		}
		return null;
	}
	
	
	
	
	
	
	

	public nWidgetGroup add_collapse_col(String title) {
		if (current_line == null) { add_line(); }
		newCommand(Code.COL);
		if (is_pop && current_line != null) {
			
			nWidgetGroup cg = gui.addWidgetGroup();
			group.addWidgetGroup("collapse_group_"+col_nb, cg);
			
			nWidget col = group.addWidget("col_"+col_nb, "INT_col");
			col_nb++;
			col.setStackSpacing(Utl.tofloat(params.get("spacing")));

			nWidget ent = group.addWidget("ent_"+ent_nb, "INT_col_back");
			ent_nb++;
			ent.setParent(current_line);
			nWidget ent2 = group.addWidget("ent_"+ent_nb, "INT_col_head");
			ent_nb++;
			ent2.setParent(ent).setText(title).setOn();
			col.setParent(ent);
			ent2.addEventSwitch(new nRun() { public void run() {
				col.setVisibility(ent2.isOn()); }});
			
			current_col = col;

			cg.addWidget("back", ent);
			cg.addWidget("head", ent2);
			cg.addWidget("col", col);
			return cg;
		}
		return null;
	}
	
	public nInterface add_line() {
		newCommand(Code.LINE);
		if (is_pop) {
			if (current_line == null && group.get("filler") != null) {
				group.get("filler").clear();
			}
			current_line = group.addWidget("line_"+line_nb, "INT_col_line");
			current_line.setStackSpacing(Utl.tofloat(params.get("spacing")));
			line_nb++;
			current_line.setParent(ref);
		}
		return this;
	}
	
	public nWidget add_col() {
		if (current_line == null) { add_line(); }
		newCommand(Code.COL);
		if (is_pop && current_line != null) {
			current_col = group.addWidget("col_"+col_nb, "INT_col");
			current_col.setStackSpacing(Utl.tofloat(params.get("spacing")));
			col_nb++;
			current_col.setParent(current_line);
		}
		return current_col;
	}
	
	public nWidget add_row() {
		if (current_col == null) { add_col(); }
		newCommand(Code.ROW);
		if (is_pop && current_col != null) {
			current_row = group.addWidget("row_"+row_nb, "INT_row");
			current_row.setStackSpacing(Utl.tofloat(params.get("spacing")));
			row_nb++;
			current_row.setParent(current_col);
			return current_row;
		}
		return null;
	}
	
	
	public nWidget add_col_entry() {
		if (current_col == null) { add_col(); }
		if (is_pop && current_col != null) {
			nWidget ent = group.addWidget("ent_"+ent_nb, "INT_col_entry");
			ent_nb++;
			ent.setParent(current_col);
			return ent;
		}
		return null;
	}
	

	
	public nWidget add_col_separator() {
		if (current_col == null) { add_col(); }
		newCommand(Code.COL_SEP);
		if (is_pop && current_col != null) {
			nWidget ent = group.addWidget("ent_"+ent_nb, "INT_col_separator");
			ent_nb++;
			ent.setParent(current_col);
			return ent;
		}
		return null;
	}
	public nWidget add_col_label(String text) {
		if (current_col == null) { add_col(); }
		newCommand(Code.COL_LAB, text);
		if (is_pop && current_col != null) {
			nWidget ent = add_col_entry();
			ent.setText(text);
			return ent;
		}
		return null;
	}
	public nWidget add_col_trigg(String text, nRun r) {
		if (current_col == null) { add_col(); }
		newCommand(Code.COL_TRI, text);
		if (is_pop && current_col != null) {
			nWidget ent = add_col_entry(); 
			ent.addEventTrigger(r)
			.setTrigger()
			.setText(text);
			return ent;
		}
		return null;
	}
	public nWidget add_col_switch_run(String text) {
		if (current_col == null) { add_col(); }
		newCommand(Code.COL_SWT_R, text);
		if (is_pop && current_col != null) {
			nWidget ent = add_col_entry();
			ent.setSwitch()
			.setText(text);
			return ent;
		}
		return null;
	}
	public nWidget add_col_switch_run(String text, nRun r) {
		if (current_col == null) { add_col(); }
		newCommand(Code.COL_SWT_R, text);
		if (is_pop && current_col != null) {
			nWidget ent = add_col_entry();
			ent.addEventSwitch(r)
			.setSwitch()
			.setText(text);
			return ent;
		}
		return null;
	}
	public nWidget add_col_switch_boo(String text, String val_ref) {
		if (current_col == null) { add_col(); }
		newCommand(Code.COL_SWT_B, text, val_ref);
		if (is_pop && current_col != null) {
			nWidget ent = add_col_entry();
			ent.setSwitch()
			.setText(text);
			if (context_bloc != null) {
				sValue v = context_bloc.getValue(val_ref);
				if (v != null) ent.setLink((sBoo)v); }
			return ent;
		}
		return null;
	}
	

	public nWidget add_row_label(int width, String text) {
		if (current_row == null) { add_row(); }
		newCommand(Code.ROW_LAB, Utl.tostr(width), text);
		if (is_pop && current_row != null) {
			nWidget ent = add_row_entry(width);
			ent.setText(text);
			return ent;
		}
		return null;
	}
	public nWidget add_row_trigg(int width, String text) {
		if (current_row == null) { add_row(); }
		newCommand(Code.ROW_TRI, Utl.tostr(width), text);
		if (is_pop && current_row != null) {
			nWidget ent = add_row_entry_button(width);
			ent.setTrigger()
			.setText(text);
			return ent;
		}
		return null;
	}
	public nWidget add_row_trigg(int width, String text, nRun r) {
		if (current_row == null) { add_row(); }
		newCommand(Code.ROW_TRI, Utl.tostr(width), text);
		if (is_pop && current_row != null) {
			nWidget ent = add_row_entry_button(width);
			ent.addEventTrigger(r)
			.setTrigger()
			.setText(text);
			return ent;
		}
		return null;
	}
	public nWidget add_row_trigg_build(int width, String text, String buildref) {
		if (current_row == null) { add_row(); }
		newCommand(Code.ROW_TRI_BLD, Utl.tostr(width), text, buildref);
		if (is_pop && current_row != null) {
			nWidget ent = add_row_entry_button(width);
			if (context_bloc != null) {
				ent.addEventTrigger(new nRun() { public void run() {
					context_bloc.buildBloc(buildref, buildref); }});  }
			ent.setTrigger()
			.setText(text);
			return ent;
		}
		return null;
	}
	public nWidget add_row_trigg_met(int width, String text, String metoderef) {
		if (current_row == null) { add_row(); }
		newCommand(Code.ROW_TRI_M, Utl.tostr(width), text, metoderef);
		if (is_pop && current_row != null) {
			nWidget ent = add_row_entry_button(width);
			if (context_bloc != null) {
				nRun v = context_bloc.getMetode(metoderef);
				if (v != null) ent.addEventTrigger(v); }
			ent.setTrigger()
			.setText(text);
			return ent;
		}
		return null;
	}
	public nWidget add_row_switch(int width, String text) {
		if (current_row == null) { add_row(); }
		newCommand(Code.ROW_SWT_R, Utl.tostr(width), text);
		if (is_pop && current_row != null) {
			nWidget ent = add_row_entry_button(width);
			ent.setSwitch()
			.setText(text);
			return ent;
		}
		return null;
	}
	public nWidget add_row_switch_run(int width, String text, nRun r) {
		if (current_row == null) { add_row(); }
		newCommand(Code.ROW_SWT_R, Utl.tostr(width), text);
		if (is_pop && current_row != null) {
			nWidget ent = add_row_entry_button(width);
			ent.addEventSwitch(r)
			.setSwitch()
			.setText(text);
			return ent;
		}
		return null;
	}
	

	public nWidget add_row_switch_boo(int width, String text, String val_ref) {
		if (current_row == null) { add_row(); }
		newCommand(Code.ROW_SWT_B, Utl.tostr(width), text, val_ref);
		if (is_pop && current_row != null) {
			nWidget ent = add_row_entry_button(width);
			ent.setSwitch()
			.setText(text);
			if (context_bloc != null) {
				sValue v = context_bloc.getValue(val_ref);
				if (v != null) ent.setLink((sBoo)v); }
			return ent;
		}
		return null;
	}
	
	
	public nWidget add_col_field_str(String text, String val_ref) {
		if (current_col == null) { add_col(); }
		if (current_col != null) newCommand(Code.COL_FLD_S, text, val_ref);
		if (is_pop && current_col != null) {
			nWidget ent = add_col_entry();
			ent.setField(true)
			.copyLookFrom(gui.book.getModel("text_field"))
			.setText(text);
			if (context_bloc != null) {
				sValue v = context_bloc.getValue(val_ref);
				if (v != null) ent.setLink((sStr)v); }
			return ent;
		}
		return null;
	}
	public nWidget add_row_field(int width, String text) {
		if (current_row == null) { add_row(); }
		if (current_row != null) newCommand(Code.ROW_FLD, Utl.tostr(width), text);
		if (is_pop && current_row != null) {
			nWidget ent = add_row_entry(width);
			ent.setField(true)
			.copyLookFrom(gui.book.getModel("text_field"))
			.setText(text);
			return ent;
		}
		return null;
	}
	public nWidget add_row_field_str(int width, String text, String val_ref) {
		if (current_row == null) { add_row(); }
		if (current_row != null) newCommand(Code.ROW_FLD_S, Utl.tostr(width), text, val_ref);
		if (is_pop && current_row != null) {
			nWidget ent = add_row_entry(width);
			ent.setField(true)
			.copyLookFrom(gui.book.getModel("text_field"))
			.setText(text);
			if (context_bloc != null) {
				sValue v = context_bloc.getValue(val_ref);
				if (v != null) ent.setLink((sStr)v); }
			return ent;
		}
		return null;
	}
	
	public nWidget add_row_field_flt(int width, String text, String val_ref) {
		if (current_row == null) { add_row(); }
		if (current_row != null) newCommand(Code.ROW_FLD_F, Utl.tostr(width), text, val_ref);
		if (is_pop && current_row != null) {
			nWidget ent = add_row_entry(width);
			ent.setField(true)
			.copyLookFrom(gui.book.getModel("text_field"))
			.setText(text);
			if (context_bloc != null) {
				sValue v = context_bloc.getValue(val_ref);
				if (v != null) ent.setLinkField((sFlt)v); }
			return ent;
		}
		return null;
	}
	
	public nWidget add_row_watch(int width, String text, String val_ref) {
		if (current_row == null) { add_row(); }
		if (current_row != null) newCommand(Code.ROW_WTC, Utl.tostr(width), text, val_ref);
		if (is_pop && current_row != null) {
			nWidget ent = add_row_entry(width);
			if (context_bloc != null) {
				sValue v = context_bloc.getValue(val_ref);
				if (v != null) ent.setWatcher(text, v);
			}
			return ent;
		}
		return null;
	}
	

	public nWidget add_col_incr_int(String text, String val_ref, int fact) {
		return add_col_incr_int(text, val_ref, Utl.tostr(fact)); }
	public nWidget add_col_incr_int(String text, String val_ref, String inc_fact) {
		if (current_col == null) { add_col(); }
		if (current_col != null) newCommand(Code.COL_INC_I, text, val_ref, inc_fact);
		if (is_pop && current_col != null) {

			nWidget ent = group.addWidget("row_"+row_nb, "INT_row");
			row_nb++;
			ent.setParent(current_col);
			 
			nWidget add2 = get_row_entry_widget(1);
			add2.setParent(ent); add2.setTrigger().setText("+" + 10*Utl.tofloat(inc_fact));
			nWidget add1 = get_row_entry_widget(1);
			add1.setParent(ent); add1.setTrigger().setText("+" + inc_fact);
			
			nWidget wtc = get_row_entry_widget(6);
			wtc.setParent(ent); wtc.setText(text);
			
			nWidget sub1 = get_row_entry_widget(1);
			sub1.setParent(ent); sub1.setTrigger().setText("" + -1*Utl.tofloat(inc_fact));
			nWidget sub2 = get_row_entry_widget(1);
			sub2.setParent(ent); sub2.setTrigger().setText("" + -10*Utl.tofloat(inc_fact));
			if (context_bloc != null) {
				sValue v = context_bloc.getValue(val_ref);
				if (v != null) {
					int inc = Utl.toint(inc_fact);
					add1.setLink((sInt)v, inc, 1); 
					add2.setLink((sInt)v, 10*inc, 1); 
					wtc.setWatcher(text, v);
					sub1.setLink((sInt)v, -inc, 1); 
					sub2.setLink((sInt)v, -10*inc, 1); 
				}
			}
			return ent;
		}
		return null;
	}
	
	public nWidget add_col_incr_flt(String text, String val_ref, float fact) {
		return add_col_incr_flt(text, val_ref, Utl.tostr(fact)); }
	public nWidget add_col_incr_flt(String text, String val_ref, String inc_fact) {
		if (current_col == null) { add_col(); }
		if (current_col != null) newCommand(Code.COL_INC_F, text, val_ref, inc_fact);
		if (is_pop && current_col != null) {

			nWidget ent = group.addWidget("row_"+row_nb, "INT_row");
			row_nb++;
			ent.setParent(current_col);
			
			nWidget add2 = get_row_entry_widget(1);
			add2.setParent(ent); add2.setTrigger().setText("+" + 10*Utl.tofloat(inc_fact));
			nWidget add1 = get_row_entry_widget(1);
			add1.setParent(ent); add1.setTrigger().setText("+" + inc_fact);
			
			nWidget wtc = get_row_entry_widget(6);
			wtc.setParent(ent); wtc.setText(text);
			
			nWidget sub1 = get_row_entry_widget(1);
			sub1.setParent(ent); sub1.setTrigger().setText("" + -1*Utl.tofloat(inc_fact));
			nWidget sub2 = get_row_entry_widget(1);
			sub2.setParent(ent); sub2.setTrigger().setText("" + -10*Utl.tofloat(inc_fact));
			if (context_bloc != null) {
				sValue v = context_bloc.getValue(val_ref);
				if (v != null) {
					float inc = Utl.tofloat(inc_fact);
					add1.setLink((sFlt)v, inc, 1.0f); 
					add2.setLink((sFlt)v, 10*inc, 1.0f); 
					wtc.setWatcher(text, v);
					sub1.setLink((sFlt)v, -inc, 1.0f); 
					sub2.setLink((sFlt)v, -10*inc, 1.0f); 
				}
			}
			return ent;
		}
		return null;
	}
	
	public nWidget add_col_fact_flt(String text, String val_ref, float fact) {
		return add_col_fact_flt(text, val_ref, Utl.tostr(fact)); }
	public nWidget add_col_fact_flt(String text, String val_ref, String fact) {
		if (current_col == null) { add_col(); }
		if (current_col != null) newCommand(Code.COL_FAC_F, text, val_ref, fact);
		if (is_pop && current_col != null) {

			nWidget ent = group.addWidget("row_"+row_nb, "INT_row");
			row_nb++;
			ent.setParent(current_col);
			
			nWidget add2 = get_row_entry_widget(1);
			add2.setParent(ent); add2.setTrigger().setText("" + fact);
			nWidget add1 = get_row_entry_widget(1);
			add1.setParent(ent); add1.setTrigger().setText("xx" + fact);
			
			nWidget wtc = get_row_entry_widget(6);
			wtc.setParent(ent); wtc.setText("<x  "+text+"  />");
			
			nWidget sub1 = get_row_entry_widget(1);
			sub1.setParent(ent); sub1.setTrigger().setText("xx" + fact);
			nWidget sub2 = get_row_entry_widget(1);
			sub2.setParent(ent); sub2.setTrigger().setText("" + fact);
			if (context_bloc != null) {
				sValue v = context_bloc.getValue(val_ref);
				if (v != null) {
					float fct = Utl.tofloat(fact);
					add1.setLink((sFlt)v, 0f, (float)Math.sqrt(fct)); 
					add2.setLink((sFlt)v, 0f, fct); 
					wtc.setWatcher("<x "+text, v, " />");
					sub1.setLink((sFlt)v, 0f, 1f/(float)Math.sqrt(fct)); 
					sub2.setLink((sFlt)v, 0f, 1f/fct); 
				}
			}
			return ent;
		}
		return null;
	}

	public nWidget add_row_slide(int width, float min, float max) { 
		if (current_row == null) { add_row(); }
		if (current_row != null) newCommand(Code.ROW_SLD, Utl.tostr(width), Utl.tostr(min), Utl.tostr(max));
		if (is_pop && current_row != null) {
			nWidget ent = add_row_entry(width);
			ent.setSlider()
			.setSliderRange(min, max);
			return ent;
		}
		return null;
	}

	public nWidget add_row_slide_flt(int width, float min, float max, String val_ref) { 
		if (current_row == null) { add_row(); }
		if (current_row != null) newCommand(Code.ROW_SLD_F, Utl.tostr(width), Utl.tostr(min), Utl.tostr(max), val_ref);
		if (is_pop && current_row != null) {
			nWidget ent = add_row_entry(width);
			ent.setSlider()
			.setSliderRange(min, max);
			if (context_bloc != null) {
				sValue v = context_bloc.getValue(val_ref);
				if (v != null) ent.setLinkSlider((sFlt)v); }
			return ent;
		}
		return null;
	}

	public nWidget add_row_slide_int(int width, float min, float max, String val_ref) {
		if (current_row == null) { add_row(); }
		if (current_row != null) newCommand(Code.ROW_SLD_I, Utl.tostr(width), Utl.tostr(min), Utl.tostr(max), val_ref);
		if (is_pop && current_row != null) {
			nWidget ent = add_row_entry(width);
			ent.setSlider()
			.setSliderRange(min, max);
			if (context_bloc != null) {
				sValue v = context_bloc.getValue(val_ref);
				if (v != null) ent.setLinkSlider((sInt)v); }
			return ent;
		}
		return null;
	}
	
	
	
}
