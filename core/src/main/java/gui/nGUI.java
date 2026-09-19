package gui;

import java.util.ArrayList;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.noodle.nodulo.GdxApp;

import aa_nodulo.PlaneApplet;
import app.nDrawer;
import app.App;
import app.Runner;
import app.nInput;
import data.sBoo;
import data.sData;
import data.sFlt;
import data.sInt;
import data.sStr;
import data.sValueBloc;
import data.sVec;
import util.Utl;
import util.nMap;
import util.nPool;
import util.nRun;
//import zz_applet.nMenu;
//import zz_applet.nPref;

public class nGUI {
	
	
	public void print_state() {
		Utl.logn("Printing nGUI state :");
		Utl.logn(" - Widgets :");
		for (nWidget r : orphan_widgets) r.print_state(0); 
		Utl.logn(" - WidgetGroups :");
		for (nWidgetGroup r : widgetgroup_pool.all()) r.print_state();
	}
	
	

	public nInterface addInterface() {
		return interf_pool.obtain().init(this);
	}
	
	public nInterface addInterface(sValueBloc context, String model) {
		nInterface n = addInterface();
		n.setContext(context);
		nInterfModel m = null;
		for (nInterfModel s : all_models) if (s.ref.equals(model)) {m = s; break; }
		if (m != null) n.build_from_model(m);
		return n;
	}
	
	// get new widget from pool
	public nWidget addWidget() {
		nWidget w = widget_pool.obtain().init();
//		app.log("obtained widget "+w.widget_id);
		return w; }
	
	// get new widget from model
	public nWidget addWidget(String ref) {
		return book.buildWidget(ref, this); }
	
	// get new widget from size color and function model
	public nWidget addWidget(String size_ref, 
			String col_ref, String func_ref) {
		return book.buildWidget(size_ref, col_ref, func_ref, this); }
		
	
	// with params
	public nWidget addWidget(String ref, float x, float y) {
		return addWidget(ref).setPosition(x,y).asWidget(); }
	public nWidget addWidget(String ref, String t) {
		return addWidget(ref).setText(t).asWidget(); }
	
	public nWidget addWidget(String size_ref, 
			String col_ref, String func_ref, float x, float y) {
		return addWidget(size_ref, col_ref, func_ref)
				.setPosition(x,y).asWidget(); }
	public nWidget addWidget(String size_ref, 
			String col_ref, String func_ref, String t) {
		return addWidget(size_ref, col_ref, func_ref)
				.setText(t).asWidget(); }
	
	//widgetgroup builder
	public nWidgetGroup addWidgetGroup() {
		nWidgetGroup w = gui.widgetgroup_pool.obtain().init();
		return w; }
	
	public nWidgetGroup addWidgetGroup(String ref) {
		nWidgetGroup g = book.buildGroup(ref, gui);
		g.ref = ref; return g;
	}
	
	
	//easy builder
	public static nWidgetGroup add_Window(nGUI gui, String title) {
		nWidgetGroup g = gui.addWidgetGroup("window");
		g.get("head").setText(title);
		return g;
	}
	public static nWidgetGroup add_Window(nGUI gui, String title, float px, float py) {
		nWidgetGroup g = gui.addWidgetGroup("window");
		g.get("head").setText(title).setPos(px,py);
		return g;
	}
	
	
	
	public void clearPoolsFreeObjects() {
		widget_pool.clearFreeObjs();
		widgetgroup_pool.clearFreeObjs();
		interf_pool.clearFreeObjs();
	}
	
	

	public nDrawer.DrawContext context;
	public nDrawer.Drawer drawer;
	public sData data;
	public Runner runner;
	public nInput in;
	
	public OrthographicCamera cam;
	public Vector2 mouse_vec;
    public float scale = 1;
    public nGUI gui;
    public static nModelBook book;
    
    public Rectangle viewrect;
    
    public sInt val_widget_nb = null, val_free_widget_nb = null, 
    		val_wgroup_nb = null, val_free_wgroup_nb = null;
    
    
    
    public void add_info_svalues_in(sValueBloc b) {
		val_widget_nb = b.newInt("val_widget_nb");
		val_free_widget_nb = b.newInt("val_free_widget_nb");
		val_wgroup_nb = b.newInt("val_wgroup_nb");
		val_free_wgroup_nb = b.newInt("val_free_wgroup_nb");
    }

	public nMap<String> params_def;
	void new_param(String ref, String def) { params_def.put(ref, def); }

	private void setup_params() {
		params_def = new nMap<String>();
		
		new_param("entry_width", "2.0");
		new_param("entry_height", "1.0");
		if (PlaneApplet.app != null && PlaneApplet.app.config.RELEASE) 
			new_param("entry_colors", "CL_release");
		else new_param("entry_colors", "CL_def");
		new_param("spacing", "2.0");
		new_param("row_entry_model", "INT_row_entry_");
		new_param("row_entry_button_model", "INT_row_entry_");
		new_param("text_align_X", "CENTER");
		new_param("text_align_Y", "CENTER");
	}
	public final ArrayList<Rectangle> scissors = new ArrayList<Rectangle>();
	
    public final ArrayList<nWidget> orphan_widgets = new ArrayList<nWidget>();
	
	public final nPool<nWidget> widget_pool = new nPool<nWidget>(1000) {
		protected nWidget newObject() { return new nWidget(gui); } };

	public final nPool<nWidgetGroup> widgetgroup_pool = new nPool<nWidgetGroup>(200) {
		protected nWidgetGroup newObject() { return new nWidgetGroup(gui); } };
			
	public boolean field_used = false;

	public final ArrayList<nInterfModel> all_models  = new ArrayList<nInterfModel>();
	
	public final nPool<nInterface> interf_pool = new nPool<nInterface>(100) {
		protected nInterface newObject() { return new nInterface(); } };

	public sInt val_interf_nb = null, val_free_interf_nb = null;
	
	public boolean do_help = false;
	public Color helper_light;

	public nWidgetGroup group_infopop = null;
	public nWidgetGroup group_popWindow = null;
	public nWidgetGroup group_dropmenu = null;

	public nWidget menu_back;
	public nWidget menu_ref;

	public nToolBox toolbox;

	nWidget info_back;

	public sBoo val_hide_bar, val_hide_info, val_fx;
	
	public nWidget menu_right;
	
	private boolean menu_bar_visible = false;
	public Rectangle freeview = new Rectangle();
	
	ArrayList<nRun> freeviewEvent = new ArrayList<nRun>();

	public void addFreeviewEvent(nRun n) { freeviewEvent.add(n); updateFreeview(); }
	public void removeFreeviewEvent(nRun n) { freeviewEvent.remove(n); }
	
	public void updateFreeview() {
		Vector2 p = new Vector2(0,0);
		Vector2 s = new Vector2(App.ap.gdx.getscreenwidth(), 
				App.ap.gdx.getscreenheight());
		if (toolbox.val_toolbox_open.get()) {
			s.x -= toolbox.tool_group.get("back").getSX();
			p.x += toolbox.tool_group.get("back").getSX();}
		if (menu_bar_visible) { s.y -= gui.menu_back.getSY(); }
		freeview.set(p.x,p.y,s.x,s.y);
		nRun.runEvents(freeviewEvent);
	}
	
	public nGUI(App app) {
		in = app.input; data = app.data; context = app.gdx; drawer = app;
		runner = app;
		cam = context.getCamera();
		gui = this;
		mouse_vec = in.mouse;
		viewrect = context.getScreenRect();
		
		helper_light = Utl.color(240,230,220,150);

		val_interf_nb = data.system_bloc.newInt("val_interf_nb", "", 0);
		val_free_interf_nb = data.system_bloc.newInt("val_free_interf_nb", "", 0);
		
		runner.addRunFrameStart(new nRun() { public void run() {
			val_interf_nb.set(interf_pool.all().size());
			val_free_interf_nb.set(interf_pool.getFree());
		}});
		
		setup_params();
		
		add_info_svalues_in(data.system_bloc);

		boolean has_static = true;
		if (book == null) {
			has_static = false;
			book = new nModelBook();
			nGUIBook.build_color(book);
		}
		
		nGUIBook.build_theme_color(book); 
		
		if (!has_static) {
			nGUIBook.build_all_book(book);
			nGUIBook.build_menu_book();
			nToolBox.build_book();
		}

		if (PlaneApplet.app != null && PlaneApplet.app.config.RELEASE) {
			App.ap.gdx.drawer.color_back = new Color(
					nGUI.book.getModel("CL_release").color_background);
			App.ap.gdx.drawer.buffer_clear_color = new Color(
					nGUI.book.getModel("CL_VS_back").color_background); 
		} else {
			App.ap.gdx.drawer.color_back = Utl.color(70);
			App.ap.gdx.drawer.buffer_clear_color = new Color(
					nGUI.book.getModel("CL_VS_back").color_background); 
		}
	
		group_infopop = addWidgetGroup("info_pop");
		group_popWindow = addWidgetGroup("pop_window");
		group_dropmenu = addWidgetGroup("dropmenu");
		

		app.addRunFrameStart(new nRun() { public void run() {
			update_shortcut(); }});

		float RS = book.RS;
		
		build_help();
		
		menu_back = gui.addWidget("menu_back")
				.setRect(0,GdxApp.HEIGHT - 4f*RS/3f,GdxApp.WIDTH,4f*RS/3f)
				.asWidget()
				;
		
		menu_ref = gui.addWidget("menu_ref")
				.asWidget()
				.setParent(menu_back)
				;
		
		
		bar_entrys = new ArrayList<nWidget>();
		
		bar_ref = gui.addWidget("taskbar_ref")
				.setRect(RS*12f,0,RS*6f,RS)
				.asWidget()
				.setParent(menu_back)
				;


		info_back = gui.addWidget("info_back")
				.setPos(GdxApp.WIDTH - 190,0)
				.asWidget()
				;

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

		val_hide_bar = app.data.root_bloc.newBoo("val_hide_bar", true);

		val_hide_info = app.data.root_bloc.newBoo("val_hide_info", false);

		val_fx = app.data.root_bloc.newBoo("val_fx", 
				PlaneApplet.app != null && !PlaneApplet.app.config.START_FX);
		val_fx.addEventChangeLastFrame(new nRun() { public void run() {
			app.use_fx(val_fx.get()); }});
		
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
			menu_back.setRect(0,app.gdx.getscreenheight() - 4f*RS/3f,app.gdx.getscreenwidth(),4f*RS/3f); 
			updateFreeview();
		}});
		app.gdx.addEventScreen(new nRun() { public void run() {
			}});

		add_info_text("fps:", app.input.val_framerate);
		if (PlaneApplet.app != null && !PlaneApplet.app.config.RELEASE) {
			add_info_text("mouse:", app.input.val_mouse_pos);
			add_info_text("javHeap:", app.input.val_javaHeap);
//			add_info_text("natHeap:", app.input.val_nativeHeap);
			if (GdxApp.USE_GLPROFILER) {
				add_info_text("glCall:", app.input.val_glCalls);
				add_info_text("textBind:", app.input.val_textureBindings);
				add_info_text("drawCall:", app.input.val_drawCalls);
				add_info_text("shadeSw:", app.input.val_shaderSwitch);
				add_info_text("batchCll:", app.input.val_batchCalls);
			}
		}
		
	}

	public void dispose() {
		widget_pool.dispose();
		widgetgroup_pool.dispose();
		
		orphan_widgets.clear();
		drawing_stack.clear();
		
//		in = null; app = null; gui = null; book = null; mouse_vec = null;
	}

	public void pop_infopop(nWidget pop) {
		group_infopop.metode("pop", pop); }

	public nInterface get_popWindow() {
		return (nInterface)group_popWindow.metodeGet("get_interf"); }
	public void pop_popwindow(String title) {
		group_popWindow.metode("pop", title); }
	public void close_popwindow() {
		group_popWindow.metode("close"); }
	
	public static nWidgetGroup clear_dropmenu() {
		return App.ap.gui.group_dropmenu.metode("clear_entrys"); }
	public static nWidget add_dropmenu_entry(String t) {
		return add_dropmenu_entry(t, book.RS*6f, book.RS*2f/3f); }
	public static nWidget add_dropmenu_entry(String t, nRun n) {
		return add_dropmenu_entry(t, book.RS*6f, book.RS*2f/3f, n); }
	public static nWidget add_dropmenu_entry(String t, float x, float y) {
		return (nWidget)(App.ap.gui.group_dropmenu.metodeGet("add_entry_custom", t, x, y)); }
	public static nWidget add_dropmenu_entry(String t, float x, float y, nRun n) {
		return ((nWidget)(App.ap.gui.group_dropmenu
				.metodeGet("add_entry_custom", t, x, y)))
				.addEventTrigger(n); }
	public static void open_dropmenu() {
		App.ap.gui.group_dropmenu.metode("open"); }
	public static void open_dropmenu(nWidget w) {
		App.ap.gui.group_dropmenu.metode("open", w); }
	
	//used only for hovering
	public final ArrayList<nWidget> drawing_stack = new ArrayList<nWidget>();
	public nWidget backgroundRender = null;
	public boolean back_is_rendering = false;
	public void frame() {
		
		if (val_widget_nb != null) val_widget_nb.set(widget_pool.all().size());
		if (val_free_widget_nb != null) val_free_widget_nb.set(widget_pool.getFree());
		if (val_wgroup_nb != null) val_wgroup_nb.set(widgetgroup_pool.all().size());
		if (val_free_wgroup_nb != null) val_free_wgroup_nb.set(widgetgroup_pool.getFree());
		
//		//clearing marked widgets
//		for (int i = all_widgets.size() - 1 ; i >= 0 ; i--) {
//			all_widgets.get(i).test_clearing(); }

		backgroundRender = null;
		
		// deactive position calc flags
		for (nWidget r : widget_pool.all()) {
			r.undone_calc();
		}
		//calculate global coords
		for (nWidget r : orphan_widgets) if (r.visible) {
			r.globalrect_calc();
		}

		//empty drawing stack
		drawing_stack.clear();

		//build draw stack, calc masked rect
		for (nWidget r : orphan_widgets) if (r.visible && !r.drawstackPriority) {
			r.buildDrawStackAndMask(drawing_stack, viewrect); }
		for (nWidget r : orphan_widgets) if (r.visible && r.drawstackPriority) {
			r.buildDrawStackAndMask(drawing_stack, viewrect); }
		
		//search hovered in draw stack
		for (int i = widget_pool.all().size() - 1 ; i >= 0 ; i--) {
			widget_pool.all().get(i).mouseOver = false;
			widget_pool.all().get(i).mouseOverZone = false;
			widget_pool.all().get(i).mouseOverChildZone = false; }
		if (!in.mouse_has_been_catched) {
			nWidget w;
			nWidget zone = null;
			boolean found = false, found_zone = false;
			for (int i = drawing_stack.size() - 1 ; i >= 0 ; i--) {
				w = drawing_stack.get(i);
				if ((w.hoverable || w.hoverable_zone) && 
						w.maskedrect.contains(mouse_vec)) {
					if (w.hoverable && !found) {
						w.mouseOver = true; found = true; 
						in.mouse_has_been_catched = true; 
					} else w.mouseOver = false;
					if (w.hoverable_zone && !found_zone) {
						w.mouseOverZone = true; found_zone = true; 
						in.mouse_has_been_catched = true; 
						zone = w;
					} else w.mouseOverZone = false;
				} else {
					w.mouseOver = false; w.mouseOverZone = false;
				}
			}
			while (zone != null) {
				zone.mouseOverChildZone = true;
				if (zone.stop_hover_child_zone) zone = null;
				else zone = zone.parent;
			}
		}
		//interaction logic
		tmp_widg.clear();
		for (nWidget w : widget_pool.all()) tmp_widg.add(w);
		for (nWidget w : tmp_widg) if (!w.is_clearing) w.logic_update();
		tmp_widg.clear();
		
	}

	public void draw_start() {
		
	}
	private ArrayList<nWidget> tmp_widg = new ArrayList<nWidget>();

	public void draw() {
		
//		back_is_rendering = true;
//		
//		if (backgroundRender != null) backgroundRender.drawMasked();
//		
//		back_is_rendering = false;
		
		for (nWidget r : orphan_widgets) if (r.visible && !r.drawstackPriority) {
			r.drawMasked();
		}
		for (nWidget r : orphan_widgets) if (r.visible && r.drawstackPriority) {
			r.drawMasked();
		}
		
		context.getDrawer().flush();
		
//		for (nWidget r : all_widgets) {
//			r.draw_debug();
//		}
		
//		for (nWidget r : orphan_widgets) if (r.visible && !r.drawstackPriority) {
//			r.draw_debug();
//		}
//		for (nWidget r : orphan_widgets) if (r.visible && r.drawstackPriority) {
//			r.draw_debug();
//		}
		
	}


	public nWidget add_menu_trigg(String t) {
		return addWidget("menu_trigg",t)
		.setParent(menu_ref)
		;
	}
	public nWidget add_menu_trigg(String t, nRun r) {
		return addWidget("menu_trigg",t)
		.addEventTrigger(r)
		.setParent(menu_ref)
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
			if (in.do_shortcut && !field_used && 
					(in.getClick(me.getValue()) || 
							in.getUnClick(me.getValue())) && 
					shortcut_run.get(me.getKey()) != null) {
				shortcut_run.get(me.getKey()).run();
				shortcut_run.get(me.getKey()).run(in.getState(me.getValue()));
			}
				
			if (in.do_shortcut && !field_used && 
					shortcut_state_run.get(me.getKey()) != null) 
				shortcut_state_run.get(me.getKey()).run(in.getState(me.getValue()));
		}
	}
	

	nWidget bar_ref; //bar_back, 
	public ArrayList<nWidget> bar_entrys;
	
	public nWidget add_taskbar_entry() {
		
		nWidget w = gui.addWidget("taskbar_entry")
		.setParent(bar_ref);
		
		for(nWidget n : bar_entrys) n.setOff();
		
		w.addEventSwitchOn(new nRun() { public void run() {
			for(nWidget n : bar_entrys) if (n != w) n.setOff(); }});
		
		bar_entrys.add(w);
		return w;
	}
	public void remove_taskbar_entry(nWidget w) {
		bar_entrys.remove(w); }
	
	


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
		
		runner.addEventNextFrame(new nRun() { public void run() {
			gui.pop_popwindow("Shortcut"); }});
	}

	

	public void pop_exit() {

		nInterface interf = gui.get_popWindow();
		interf.add_row();
		interf.add_row_label(10, "");
		interf.add_row();
		interf.add_row_trigg(5, "Exit", new nRun() {public void run() { 
			GdxApp.app.close_app(); }});
		interf.add_row_label(1, "");
		interf.add_row_trigg(5, "Title", new nRun() {public void run() { 
			gui.close_popwindow(); GdxApp.app.to_title(); }});
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
				if (fl.extension().equals(data.file_ext_txt)) { 
					nWidget le = interf.add_list_entry(fl.name());
					if (data.val_root_savepath.get().equals(fl.name())) le.setOn();
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
			data.val_root_savepath.set(file_name);
			run_list_files.run();
		}};
		
		nRun run_save_file = new nRun() { public void run() {
			String file_name = (String)file_list.metodeGet("get_pick");
			if (file_name.length() == 0 || !Utl.file_exist(file_name)) return;
			data.val_root_savepath.set(file_name);
			data.full_save();
			gui.close_popwindow();
		}};
		
		refresh_w.addEventTrigger(new nRun() { public void run() {
			run_list_files.run(); }});
		new_w.addEventTrigger(new nRun() { public void run() {
			run_new_file.run(); }});
		load_w.addEventTrigger(new nRun() { public void run() {
			run_save_file.run(); }});
		
		runner.addEventNextFrame(new nRun() { public void run() {
			gui.pop_popwindow("Save"); }});
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
	

	//TODO a refaire avec scene2d.ui dans un autre Screen
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
				txt_w.setSY(drawer.textHeight() * txt_w.line_number(txt) / 1.1f);
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
				txt_w.setSY(drawer.textHeight() * txt_w.line_number(txt) / 1.1f);
				txt_w.setText(txt);
				txt_w.setTextAutoReturn(true)
				.setTextAlignment(nAlign.LEFT, nAlign.BOTTOM);
				txt_w.force_calc();
				txt_w = interf.add_list_entry("");
				txt = link_text;
				txt_w.force_calc();
				txt_w.setSY(drawer.textHeight() * txt_w.line_number(txt) / 1.1f);
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
		
		gui.do_help = PlaneApplet.app != null && PlaneApplet.app.config.START_HELP;
		
		gui.add_shortcut_target("Help - view interest", 'H', null, 
				new nRun() {public void run(Object o) {
			boolean b = (boolean)o; 
			HELP_VIEW = b;// && val_help.get(); 
			do_help = HELP_VIEW; }});
		
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
