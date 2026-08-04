package gui;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import app.nDrawer;
import app.App;
import app.Runner;
import app.nInput;
import data.sData;
import data.sInt;
import data.sValueBloc;
import util.Utl;
import util.nMap;
import util.nPool;
import util.nRun;
import zz_applet.nMenu;
import zz_applet.nPref;

public class nGUI {
	
	
//	public void print_state() {
//		log.logn("Printing nGUI state :");
//		log.logn(" - Widgets :");
//		for (nWidget r : orphan_widgets) r.print_state(0); 
//		log.logn(" - WidgetGroups :");
//		for (nWidgetGroup r : widgetgroup_pool.all()) r.print_state();
//	}
	
	

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
	public Utl.Logger log;
	public sData data;
	public Runner runner;
	public nInput in;
	
	public OrthographicCamera cam;
	public Vector2 mouse_vec;
    public float scale = 1;
    public nGUI gui;
    public nModelBook book;
    
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
//		if (app.getPref("RELEASE", Boolean.class)) 
//			new_param("entry_colors", "CL_release");
//		else 
		new_param("entry_colors", "CL_def");
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

	public nGUI(App app) { this(app,app.gdx,app,app.gdx,app.input,app.data); }
	public nGUI(Runner _app, nDrawer.DrawContext c, nDrawer.Drawer dr, 
			Utl.Logger l, nInput i, sData d) {
		in = i; data = d; log = l; context = c; drawer = dr;
		runner = _app;
		cam = c.getCamera();
		gui = this;
		book = new nModelBook();
		mouse_vec = in.mouse;
		viewrect = c.getScreenRect();
		
		helper_light = Utl.color(240,230,220,150);

		val_interf_nb = data.setting_bloc.newInt("val_interf_nb", "", 0);
		val_free_interf_nb = data.setting_bloc.newInt("val_free_interf_nb", "", 0);
		
		runner.addRunFrameStart(new nRun() { public void run() {
			val_interf_nb.set(interf_pool.all().size());
			val_free_interf_nb.set(interf_pool.getFree());
		}});
		
		setup_params();
		
		add_info_svalues_in(data.setting_bloc);
	}

	public void dispose() {
		widget_pool.dispose();
		widgetgroup_pool.dispose();
		
		orphan_widgets.clear();
		drawing_stack.clear();
		
//		in = null; app = null; gui = null; book = null; mouse_vec = null;
	}

	//used only for hovering
	public final ArrayList<nWidget> drawing_stack = new ArrayList<nWidget>();
	
	public void frame() {
		if (val_widget_nb != null) val_widget_nb.set(widget_pool.all().size());
		if (val_free_widget_nb != null) val_free_widget_nb.set(widget_pool.getFree());
		if (val_wgroup_nb != null) val_wgroup_nb.set(widgetgroup_pool.all().size());
		if (val_free_wgroup_nb != null) val_free_wgroup_nb.set(widgetgroup_pool.getFree());
		
//		//clearing marked widgets
//		for (int i = all_widgets.size() - 1 ; i >= 0 ; i--) {
//			all_widgets.get(i).test_clearing(); }
		
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
	
	private ArrayList<nWidget> tmp_widg = new ArrayList<nWidget>();

	public void draw() {
		
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

	
	
	
	
	
	
}
