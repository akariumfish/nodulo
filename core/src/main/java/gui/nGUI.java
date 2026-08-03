package gui;

import java.util.ArrayList;
import java.util.HashMap;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import app.Applet;
import app.nInput;
import app.nMap;
import app.nMenu;
import app.nPool;
import app.nRun;

import data.sInt;
import data.sValueBloc;

public class nGUI {
	
	
	public void print_state() {
		app.logn("Printing nGUI state :");
		app.logn(" - Widgets :");
		for (nWidget r : orphan_widgets) r.print_state(0); 
		app.logn(" - WidgetGroups :");
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
	
	
	

	public Applet app;
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
		if (app.getPref("RELEASE", Boolean.class)) 
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
	
	public nGUI(Applet _app, OrthographicCamera c, Vector2 m, Rectangle r) {
		in = _app.input; 
		app = _app;
		cam = c;
		gui = this;
		book = new nModelBook(app);
		mouse_vec = m;
		viewrect = r;
		
		helper_light = app.color(240,230,220,150);

		val_interf_nb = app.data.setting_bloc.newInt("val_interf_nb", "", 0);
		val_free_interf_nb = app.data.setting_bloc.newInt("val_free_interf_nb", "", 0);
		
		app.addEventFrame(new nRun() { public void run() {
			val_interf_nb.set(interf_pool.all().size());
			val_free_interf_nb.set(interf_pool.getFree());
		}});
		
		build_interf_book();

		setup_params();
		
		add_info_svalues_in(app.data.setting_bloc);
	}

	public void build(nMenu menu) {
		if (app.getPref("RELEASE", Boolean.class)) return;
		menu.add_info_text("widget:", val_widget_nb);
		menu.add_info_text("free widget:", val_free_widget_nb);
		menu.add_info_text("wgroup:", val_wgroup_nb);
		menu.add_info_text("free wgroup:", val_free_wgroup_nb);
		menu.add_info_text("interf:", val_interf_nb);
		menu.add_info_text("free interf:", val_free_interf_nb);
		
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
		if (!app.input.mouse_has_been_catched) {
			nWidget w;
			nWidget zone = null;
			boolean found = false, found_zone = false;
			for (int i = drawing_stack.size() - 1 ; i >= 0 ; i--) {
				w = drawing_stack.get(i);
				if ((w.hoverable || w.hoverable_zone) && 
						w.maskedrect.contains(mouse_vec)) {
					if (w.hoverable && !found) {
						w.mouseOver = true; found = true; 
						app.input.mouse_has_been_catched = true; 
					} else w.mouseOver = false;
					if (w.hoverable_zone && !found_zone) {
						w.mouseOverZone = true; found_zone = true; 
						app.input.mouse_has_been_catched = true; 
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
		
		app.drawer.flush();
		
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

	
	
	
	
	
	
	
	public void build_interf_book() {
		float RS = book.RS;
		
		
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
		.set_color_background(app.color(0,0))
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
		.set_color_background(app.color(0,0))
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
		.set_color_background(app.color(0,0))
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
		.set_color_outline(app.color(20))
		.set_color_background(app.color(0,0))
		;

		book.newModel("INT_col_head")
		.copyColorFrom(book.getModel("ref"))
		.setSize(RS*10f, RS/3f*2f)
		.setBoundParent(true)
		.setStacked(true)
//		.set_color_pressed(app.color(20))
//		.set_color_hovered(app.color(120))
//		.set_color_standby(app.color(60))
		.setSwitch()
		;

		book.newModel("INT_filler")
		.copyFrom(book.getModel("INT_col"))
		.setSize(RS*2,RS*2)
		.set_color_background(app.color(0,0))
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
		.set_color_background(app.color(0,0))
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
			.set_color_background(app.color(0,0))
			;
		}
		
		book.newModelGroup("interface", new nModelGroup(app) { 
			public nWidgetGroup build(nGUI gui) {
				nWidgetGroup g = gui.addWidgetGroup();
				nWidget ref = g.addWidget("ref", gui.addWidget("INT_back"));
				
				return g;
			} 
		} );
	}
}
