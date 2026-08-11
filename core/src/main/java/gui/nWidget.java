package gui;

import java.util.ArrayList;
import java.util.Map;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.utils.ScissorStack;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.noodle.nodulo.GdxApp;

import app.App;
import app.nDrawer;
import data.*;
import util.Utl;
import util.nClearable;
import util.nRun;
import util.nTransform;

public class nWidget extends nModel implements Poolable, nClearable {
	
	
	
	public void print_state(int tabs) {
		String t = "";
		for (int i = 0 ; i < tabs ; i++) t += " ";
		t += "-"+widget_id+" "+groupKey;
		if (group != null) t += " in "+group.ref;
		t += " : "+text+" "+maskedrect.toString();
		int dec = 60 - t.length();
		for (int i = 0 ; i < dec ; i++) t += " ";
		t += ".";
		if (mouseOver) t += "mouseOver";
		Utl.logn(t);
		for (nWidget r : childs) r.print_state(tabs+1); 
	}
	
	
	

	public nWidget runModelCustomInit(nModel m) { return m.custom_init(this); }
	

	public nGUI gui;
	public nDrawer.Drawer app;
	
	public nDrawable drawer, custom_drawer = null;
	public  nWidget setDrawer(nDrawable d) { drawer = d; return this; }
	public  nWidget setCustomDrawer(nDrawable d) { custom_drawer = d; return this; }
	
	public static int WIDGET_COUNTER = 0;
	public int widget_id = 0;
	
	// called when pool is filled
	public nWidget(nGUI g) {
		super();
		app = g.drawer;
		gui = g; 
		widget_id = WIDGET_COUNTER;
		WIDGET_COUNTER++;
		drawer = new nDrawable() { public void drawing() { draw(); }};
		launcher_ref = "nWidget ?";
	}

	// called after obtained from pool
	public nWidget init() {

		init_default();
		
		drawer = new nDrawable() { public void drawing() { draw(); }};
		
		vw_int = null; vw_flt = null; vw_boo = null; vw_str = null; vw_vec = null;
		vw_tab = null;
		vl_vec = null; vl_boo = null; vl_str = null; 
		vl_int = null; vl_flt = null; vlslide_flt = null; vlfld_flt = null;
		vlslide_int = null; vlfld_int = null;
		
//		vwp_stack_index = null;
		
		watcher_pre_text = ""; watcher_post_text = "";
		vl_flt_incr = 0; vl_flt_fact = 1;
		vl_int_incr = 0; vl_int_fact = 1;
		
		boundrect_calc_done = false; parentrect_calc_done = false; 
		childstackorigin_calc_done = false;
		
		boundedSize.set(0,0);
		boundedPos.set(0,0);
		parentPos.set(0,0);
		globalPos.set(0,0);
		globalscale = 1;
		globalrot = 0;
		
		globalrect.set(0,0,0,0); 
		maskingrect.set(0,0,0,0);
		maskedrect.set(0,0,0,0);
		warptransform.reset();
		
		childStackSize.set(0,0); stackOffset.set(0,0);
		mouseOver = false; mouseOverZone = false; 
		sliderVal = 1; mouseclickLocalX = 0; mouseclickLocalY = 0;
		mouseclickGlobalX = 0; mouseclickGlobalY = 0;
		cursorCount = 0; cursorCycle = 80;
		prev_select_outline = false;
		isSliderGrabbed = false;
		
		copy_size_x = false;
		copy_size_y = false;
		copy_size_min = 0;
		copy_size_inc = 0;
		copy_size_cible = null;
		
		custom_drawer = null;

		is_clearing = false;
		
		glue_cible = null;
		extraBounds.clear();
		
		if (!gui.orphan_widgets.contains(this)) gui.orphan_widgets.add(this);
		return this;
	}
	
	//called when freed by the pool
	@Override
	public void reset() { }
	
	boolean is_clearing = true;
	
	ArrayList<nWidget> tmpw = new ArrayList<nWidget>();
	
	public void clear() { 
		if (!is_clearing) {
			runEventList("eventClearRun"); 
			is_clearing = true; 
	
			if (vw_int != null) vw_int.unlinkWidget(this);
			if (vw_flt != null) vw_flt.unlinkWidget(this);
			if (vw_boo != null) vw_boo.unlinkWidget(this);
			if (vw_str != null) vw_str.unlinkWidget(this); 
			if (vw_vec != null) vw_vec.unlinkWidget(this); 
			if (vw_tab != null) vw_tab.unlinkWidget(this); 
			if (vl_vec != null) vl_vec.unlinkWidget(this);
			if (vl_boo != null) vl_boo.unlinkWidget(this);
			if (vl_flt != null) vl_flt.unlinkWidget(this);
			if (vl_int != null) vl_int.unlinkWidget(this);
			if (vlslide_flt != null) vlslide_flt.unlinkWidget(this);
			if (vlslide_int != null) vlslide_int.unlinkWidget(this);
			if (vl_str != null) vl_str.unlinkWidget(this);
			if (vlfld_flt != null) vlfld_flt.unlinkWidget(this);
			if (vlfld_int != null) vlfld_int.unlinkWidget(this);
//			if (vwp_stack_index != null) vwp_stack_index.unlinkWidget(this);
	
			tmpw.clear();
			for (nWidget w : childs) tmpw.add(w);
			for (nWidget w : tmpw) w.clear();
			tmpw.clear();
			
			childs.clear(); 	
	
			if (parent != null) { parent.childs.remove(this); } 
			else gui.orphan_widgets.remove(this);
			parent = null;
	
			quitGroup();
			
			extraBounds.clear();
			glue_cible = null;
			
			eventMouseEnterRun.clear();
			eventMouseLeaveRun.clear();
			eventPressRun.clear();
			eventReleaseRun.clear();
			eventTriggerRightRun.clear();
			eventSwitchOnRun.clear();
			eventSwitchRun.clear();
			eventSwitchOffRun.clear();
			eventTriggerRun.clear();
			eventGrabRun.clear();
			eventDragRun.clear();
			eventLiberateRun.clear();
			eventFieldChangeRun.clear();
			eventFieldEnterRun.clear();
			eventFieldUnselectRun.clear();
			eventSliderChangeRun.clear();
			eventClearRun.clear();
			eventVisibilityRun.clear();
			eventLogicRun.clear();
			eventSelectRun.clear();
			eventUnselectRun.clear();

			gui.widget_pool.free(this);
	
		}
	}
	
	private nWidgetGroup group = null;
	String groupKey = "";
	public nWidget parent = null;
	private ArrayList<nWidget> childs = new ArrayList<nWidget>();

	public nWidget setParent(nWidget p) { 
		if (p != null && p != parent && !p.childs.contains(this)) {
			if (parent != null) parent.childs.remove(this); 
			else gui.orphan_widgets.remove(this);
			parent = p; p.childs.add(this); } return this; }

//	public nWidget setSiblingIndex(int p) { 
//		if (parent != null && getSiblingIndex() != p) {
//			parent.childs.remove(this); 
//			if (p > parent.childs.size()) p = parent.childs.size();
//			parent.childs.add(p, this); 
//			if (vwp_stack_index != null) vwp_stack_index.set(p);
//			int i = 0;
//			for (nWidget w : parent.childs) {
//				if (w.vwp_stack_index != null) w.vwp_stack_index.set(i);
//				i++; }
//		} 
//		if (parent == null && getSiblingIndex() != p) {
//			gui.orphan_widgets.remove(this); 
//			if (p > gui.orphan_widgets.size()) p = gui.orphan_widgets.size();
//			gui.orphan_widgets.add(p, this); 
//			if (vwp_stack_index != null) vwp_stack_index.set(p);
//			int i = 0;
//			for (nWidget w : gui.orphan_widgets) {
//				if (w.vwp_stack_index != null) w.vwp_stack_index.set(i);
//				i++; }
//		} 
//		return this; 
//	}
//	public int getSiblingIndex() { 
//		if (parent != null) { return parent.childs.indexOf(this); } 
//		else { return gui.orphan_widgets.indexOf(this); } }
	
	public nWidget clearParent() { 
		if (parent != null) { 
			gui.orphan_widgets.add(this);
			parent.childs.remove(this); parent = null; } return this; }
	public int getChildNb() { return childs.size(); }
	public ArrayList<nWidget> getChilds() { return childs; }
	public nWidget root() {
		if (act_as_root) return this;
		else if (parent != null) return parent.root();
		else return this; }

	private ArrayList<nWidget> extraBounds = new ArrayList<nWidget>();
	
	public nWidget addExtraBound(nWidget p) { 
		extraBounds.add(p); return this; }
	
	private nWidget glue_cible = null;
	public nWidget setGlueCible(nWidget p) { 
		glue_cible = p; return this; }

	public nWidget setGroup(nWidgetGroup g, String k) { 
		quitGroup(); 
		String base_ref = k;
		int c = 1; while (g.widgets.get(k) != null) { k = base_ref + "-" + c; c++; }
		if (!base_ref.equals(k)) Utl.logn("Error when adding widget to group " + 
				g.ref + ": '"+base_ref+"' allready used, '"+k+"' used instead");
		g.widgets.put(k, this); 
		group = g; groupKey = k; 
		return this; }
	public nWidget quitGroup() { 
		if (group != null) {
			group.widgets.remove(groupKey, this); 
			group = null; groupKey = ""; 
		} 
		return this;
	}
	public nWidgetGroup getGroup() { return group; }
	
	// VAR

	public Rectangle getParentRect() { 
		return new Rectangle(parentPos.x, parentPos.y, boundedSize.x, boundedSize.y);
	}

	public Vector2 getParentPos() { 
		return new Vector2(parentPos); 
	}

	public Vector2 getParentCenter() { 
		return new Vector2(parentPos.x + boundedSize.x / 2f, 
				parentPos.y + boundedSize.y / 2f); 
	}
	public Vector2 getBoundedSize() { return Utl.copy(boundedSize); }

	public Vector2 getParentPos(nAlign x, nAlign y) { 
		Vector2 pos = new Vector2(parentPos);
		if (x == nAlign.CENTER) pos.x += boundedSize.x / 2f;
		else if (x == nAlign.RIGHT) pos.x += boundedSize.x;
		if (y == nAlign.CENTER) pos.y += boundedSize.y / 2f;
		else if (y == nAlign.UP) pos.y += boundedSize.y;
		return pos; 
	}

	public Rectangle getRectRelativeToParent(nWidget w) { 
		Vector2 pos = new Vector2(parentPos.x, parentPos.y);
		nWidget par = parent;
		while (par != null && par != w) {
			pos.add(par.parentPos);
			par = par.parent;
		}
		Rectangle r = new Rectangle(pos.x, pos.y, boundedSize.x, boundedSize.y);
		return r; 
	}

	public Vector2 getPosRelativeToParent(nWidget w) { 
		Vector2 pos = new Vector2(parentPos.x, parentPos.y);
		nWidget par = parent;
		while (par != null && par != w) {
			pos.add(par.parentPos);
			par = par.parent;
		}
		return pos; 
	}

	public Vector2 getPosRelativeToParent(nWidget w, nAlign x, nAlign y) { 
		Vector2 pos = new Vector2(parentPos.x, parentPos.y);
		nWidget par = parent;
		while (par != null && par != w) {
			pos.add(par.parentPos);
			par = par.parent;
		}
		if (x == nAlign.CENTER) pos.x += boundedSize.x / 2f;
		else if (x == nAlign.RIGHT) pos.x += boundedSize.x;
		if (y == nAlign.CENTER) pos.y += boundedSize.y / 2f;
		else if (y == nAlign.UP) pos.y += boundedSize.y;
		return pos; 
	}

	public Vector2 getGlobalCenter() { 
		return new Vector2(globalrect.x + globalrect.width / 2f, 
				globalrect.y + globalrect.height / 2f); 
	}

	public Vector2 boundedSize = new Vector2();
	private Vector2 boundedPos = new Vector2();
	private Vector2 parentPos = new Vector2();
	public Rectangle globalrect = new Rectangle(); //used by draw, real rect coords
	Vector2 globalPos = new Vector2(); //ref pos for childrens
	public float globalscale = 1;
	public float globalrot = 0;
	public Rectangle maskingrect = new Rectangle(); // place for mask
	public Rectangle maskedrect = new Rectangle(); // rect once masked, used by hovering test
	public nTransform warptransform = new nTransform(); // rect once masked, used by hovering test
	
//	private Vector2 childStackOrigin = new Vector2();
	private Vector2 childStackSize = new Vector2();
	private Vector2 stackOffset = new Vector2(); //offset pile
	
	public boolean mouseOver = false; //set by nGUI	
	public boolean mouseOverZone = false; //set by nGUI
	public boolean mouseOverChildZone = false; //set by nGUI
	private float sliderVal = 1;
	private int cursorCount = 0;
	private int cursorCycle = 80;
	private float grabbedLocalX = 0, grabbedLocalY = 0;
	private float mouseclickLocalX = 0, mouseclickLocalY = 0;
	private float mouseclickGlobalX = 0, mouseclickGlobalY = 0;
	private float mv = 0;
	
	private boolean prev_select_outline = false;
	public boolean isSliderGrabbed = false;

	private boolean copy_size_x = false;
	private boolean copy_size_y = false;
	private float copy_size_min = 0;
	private float copy_size_inc = 0;
	private nWidget copy_size_cible = null;
	
	
	
	public void logic_update() {
		if (mouseOver 
//				&& !(fine_view && getSX()*gui.scale < 2 && getSY()*gui.scale < 2) && 
//				!(!fine_view && getSX()*gui.scale < 10 && getSY()*gui.scale < 10)
				) {
			if (!isHovered) {
				runEventList("eventMouseEnterRun");
			}
			isHovered = true;
			
			if (has_info) gui.pop_infopop(this);
			
		} else {
			if (isHovered) runEventList("eventMouseLeaveRun"); 
			isHovered = false;
		}
		if (helper && gui.do_help && gui.in.getClick("MouseRight") && 
				isHovered && !isRightClicked) {
//			app.menu.popHelp(helper_ref);
		}
		if (triggerRightMode) {
			if (gui.in.getUnClick("MouseRight")) {
				isRightClicked = false;
			}
			if (gui.in.getClick("MouseRight") && isHovered && !isRightClicked) {
				isRightClicked = true;
				runEventList("eventTriggerRightRun"); 
			}

		}
		
		
		if (triggerMode || switchMode) {
			if (gui.in.getUnClick("MouseLeft")) {
				if (isClicked) runEventList("eventReleaseRun"); 
				isClicked = false;
			}
			if (gui.in.getClick("MouseLeft") && isHovered && !isClicked) {
				isClicked = true;
				if (triggerMode) {
					runEventList("eventTriggerRun"); 
					if (vl_flt != null) { 
						vl_flt.add(vl_flt_incr); 
						vl_flt.mult(vl_flt_fact); 
					}
					if (vl_int != null) { 
						vl_int.add(vl_int_incr); 
						vl_int.mult(vl_int_fact); 
					}
				}
				if (switchMode) { 
					if (switchState) { setOff(); } else { setOn(); } 
					if (vl_boo != null) { vl_boo.set(switchState); }
				}
			}

		}
		if (isClicked) runEventList("eventPressRun");
		if (grabbable) {
			if (isHovered) {
				if (gui.in.getClick("MouseLeft")) {
					
					calc_grabbing_origin();
					
					//gui.in.cam.GRAB = false; //deactive le deplacement camera
					//gui.szone.ON = false;
					isGrabbed = true;
					runEventList("eventGrabRun");
				}
			}
			if (isGrabbed && gui.in.getUnClick("MouseLeft")) {
				isGrabbed = false;
				//gui.in.cam.GRAB = true;
				//gui.szone.ON = true;
				runEventList("eventLiberateRun");
			}
			if (isGrabbed && isClicked) {
				
				float nx = gui.mouse_vec.x + mouseclickLocalX, 
						ny = gui.mouse_vec.y + mouseclickLocalY;
				nx /= globalscale; ny /= globalscale;
				nx += grabbedLocalX; ny += grabbedLocalY; 
				if (constrainD) {
					Vector2 p = new Vector2(nx, ny);
					if (p.len() > constrainDlength) p.setLength(constrainDlength);
					nx = p.x; ny = p.y;
				}
				if (constrainX) nx = 0;
				if (constrainY) ny = 0;
				float dx = 0, dy = 0;
				if (grabb_root) {
					dx = nx - root().getLocalX(); dy = ny - root().getLocalY();
					root().setPosition(nx, ny); 
				} else {
					dx = nx - getLocalX(); dy = ny - getLocalY();
					setPosition(nx, ny);
				}
				Vector2 dp = new Vector2(dx,dy);
//				runEventList("eventDragRun");
				runEventList("eventDragRun", dp);
				
				if (!constrainX && !constrainY && !constrainD) calc_grabbing_origin();
			}
		}
		if (isSelectable) {
			if (isHovered && !isSelected && gui.in.getClick("MouseLeft")) {
				select();
//			if (isHovered && gui.in.getClick("MouseLeft")) {
//				isSelected = !isSelected;
//				if (isSelected) {
//					prev_select_outline = showOutline;
//					showOutline = true;
//					if (isField) showCursor = true;
//					gui.in.do_shortcut = false;
//					gui.field_used = true;
//				} else {
//					showOutline = prev_select_outline;
//					if (isField) showCursor = false;
//					gui.in.do_shortcut = true;
//					gui.field_used = false;
//				}
			} else if (gui.in.getClick("MouseLeft") && isSelected) { //!isHovered && 
				unselect();
//				showOutline = prev_select_outline;
//				if (isField) { 
//					showCursor = false;
//					nRunnable.runEvents(eventFieldUnselectRun);
//				}
//				isSelected = false;
//				gui.field_used = false;
//				gui.in.do_shortcut = true;
			}
		}
		if (isField && isSelected) {
			if (isHovered && gui.in.getClick("MouseLeft")) {
				float loc_mouse_x = gui.mouse_vec.x - getX();
				float tfnt = textFont * globalscale;
				if (textAlignX == nAlign.LEFT)        {
					int cpos = (int)(loc_mouse_x / tfnt);
					if (text.length() > cpos) cursorPos = cpos;
				} else if (textAlignX == nAlign.CENTER) {
					float txt_w = text.length() * tfnt;
					float txt_left_side = getSX() / 2f - txt_w / 2f;
					loc_mouse_x -= txt_left_side;
					int cpos = (int)(loc_mouse_x / tfnt); 
					if (text.length() > cpos) cursorPos = cpos;
				} else if (textAlignX == nAlign.RIGHT) {
					int cpos = (int)((getSX() - loc_mouse_x) / tfnt);
					if (text.length() > cpos) cursorPos = text.length() - cpos;
				}
				if (cursorPos < 0) cursorPos = 0;
				if (cursorPos > text.length()) cursorPos = text.length();
			}
			if (gui.in.getClick("Left")) cursorPos = Math.max(0, cursorPos-1);
			else if (gui.in.getClick("Right")) cursorPos = Math.min(cursorPos+1, text.length());
			else if (gui.in.getClick("Backspace") && cursorPos > 0) {
				String str = text.substring(0, cursorPos-1);
				String end = text.substring(cursorPos, text.length());
				text = str + end;
				cursorPos--;
				if (vl_str != null) vl_str.set(text);
				if (vlfld_flt != null) vlfld_flt.set(Utl.tofloat(text));
				if (vlfld_int != null) vlfld_int.set(Utl.toint(text));
				runEventList("eventFieldChangeRun");
			}
			else if (gui.in.getClick("Enter")) {
				runEventList("eventFieldEnterRun");
				// it break the saving!!!
				//String str = label.substring(0, cursorPos);
				//String end = label.substring(cursorPos, label.length());
				//label = str + '\n' + end;
				//cursorPos++;
				//runEvents(eventFieldChangeRun);
			}
			else if (gui.in.getClick("Backspace")) {}
			else if (gui.in.getClick("Char")) {
				String str = text.substring(0, cursorPos);
				String end = text.substring(cursorPos, text.length());
				char k = gui.in.getLastKey();
				if (k != 0) text = str + k + end;
				else text = str + end;
				cursorPos++;
				if (vl_str != null) vl_str.set(text);
				if (vlfld_flt != null) vlfld_flt.set(Utl.tofloat(text));
				if (vlfld_int != null) vlfld_int.set(Utl.toint(text));
				runEventList("eventFieldChangeRun");
			}
		}
		if (isSlider) {
			if (isHovered && gui.in.getClick("MouseLeft")) {
				isSliderGrabbed = true;
				mouseclickLocalX = gui.mouse_vec.x;
				mouseclickLocalY = gui.mouse_vec.y;
				mv = sliderVal;
			}
			if (!gui.in.getState("MouseLeft")) isSliderGrabbed = false;
			if (isSliderGrabbed) {
				float tmp = sliderVal;
				if (sliderAxis == nAlign.VERTICAL) {
					float nx = gui.mouse_vec.x - mouseclickLocalX, ny = gui.mouse_vec.y - mouseclickLocalY;
					float a = ny / (globalrect.height - globalrect.height * sliderFact);
					sliderVal = mv + a;
				} else {
					float nx = gui.mouse_vec.x - mouseclickLocalX, ny = gui.mouse_vec.y - mouseclickLocalY;
					float a = nx / (globalrect.width - globalrect.width * sliderFact);
					sliderVal = mv + a;
				}
				sliderVal = MathUtils.clamp(sliderVal, 0f, 1f);
				if (vlslide_flt != null) vlslide_flt.set(slideValToMinmax(sliderVal));
				if (vlslide_int != null) vlslide_int.set(slideValToMinmax(sliderVal));
				if (tmp != sliderVal) runEventList("eventSliderChangeRun");
			}
		}

		svalue_linking_update();
		
		runEventList("eventLogicRun");
	}
	public void move_grabbing_origin(float x, float y) {
		mouseclickLocalX -= x * globalscale;
		mouseclickLocalY -= y * globalscale;
		grabbedLocalX += x;
		grabbedLocalY += y;
	}
	public void calc_grabbing_origin() {
		mouseclickGlobalX = gui.mouse_vec.x;
		mouseclickGlobalY = gui.mouse_vec.y;
		
		if (grabb_root) {
			mouseclickLocalX = - gui.mouse_vec.x;// root().getLocalX()
			mouseclickLocalY = - gui.mouse_vec.y;// root().getLocalY()
			grabbedLocalX = root().getLocalX(); grabbedLocalY = root().getLocalY(); 
		} else {
			mouseclickLocalX = - gui.mouse_vec.x;// getLocalX()
			mouseclickLocalY = - gui.mouse_vec.y;// getLocalY()
			grabbedLocalX = getLocalX(); grabbedLocalY = getLocalY();
		}
	}
	
	public void select() {
		if (isSelectable && !isSelected) {
			isSelected = true;
			prev_select_outline = showOutline;
			showOutline = true;
			if (isField) { 
				showCursor = true;
				gui.in.do_shortcut = false;
				gui.field_used = true;
			}
			runEventList("eventSelectRun");
		}
	}
	public void unselect() {
		if (isSelectable && isSelected) {
			isSelected = false;
			showOutline = prev_select_outline;
			if (isField) { 
				showCursor = false;
				gui.field_used = false;
				gui.in.do_shortcut = true;
				runEventList("eventFieldUnselectRun");
			}
			runEventList("eventUnselectRun");
		}
	}
	
	
	private Rectangle drawrect = new Rectangle();
	private Rectangle shadowrect = new Rectangle();
	public void draw() {
		
		drawrect.set(globalrect);
		
		if (has_shadow) {
			shadowrect.set(globalrect);
			float shad_t = shadow_thick * globalscale;

			drawrect.width -= shad_t;
			drawrect.height -= shad_t;
			drawrect.y += shad_t;
			shadowrect.width -= shad_t;
			shadowrect.height -= shad_t;
			shadowrect.x += shad_t;
			
			if (press_reduce_shadow && isClicked) { 
				drawrect.x += shad_t / 2f;
				drawrect.y -= shad_t / 2f;
			} 
			
			app.fill(color_shadow);
			app.noStroke();
			if (shape == Shape.RECT) {
				app.rect(shadowrect);
			} else if (shape == Shape.CIRCLE) {
				app.circle(shadowrect);
			} else if (shape == Shape.DIAMOND) {
				app.diamond(shadowrect);
			}
		}
		
		if (isSlider) 								{ app.fill(color_sliderback); }
		else if ((triggerMode || switchMode || triggerRightMode) && isClicked) 	{ 
			app.fill(color_pressed); } 
		else if ((triggerMode || switchMode || triggerRightMode) && isHovered) 	{ 
			app.fill(color_hovered); } 
		else if (switchMode && switchState)			{ app.fill(color_switch_on); } 
		else if (switchMode && !switchState) 		{ app.fill(color_switch_off); } 
		else if (triggerMode || triggerRightMode) 	{ app.fill(color_standby); }
		else 										{ app.fill(color_background); }
		app.noStroke();
		//	        app.ellipseMode(PConstants.CORNER);

		//	        if (shapeRound) app.ellipse(getX(), getY(), getSX(), getSY());
		//	        else if (shapeLosange) {app.quad(getX() + getSX()/2, getY(), 
		//	                                     getX() + getSX()  , getY() + getSY()/2, 
		//	                                     getX() + getSX()/2, getY() + getSY(), 
		//	                                     getX()            , getY() + getSY()/2  );}
		//	        else 	if (!app.DEBUG_NOFILL) 
		if (shape == Shape.RECT) {
			app.rect(drawrect);
		} else if (shape == Shape.CIRCLE) {
			app.circle(drawrect);
		} else if (shape == Shape.DIAMOND) {
			app.diamond(drawrect);
		}
		
		if (isSlider) {
			
			app.fill(Utl.color(20));
			app.noStroke();
			if (sliderAxis == nAlign.HORIZONTAL) {
				app.rect(drawrect.x, drawrect.y + drawrect.height / 3.0f,
						drawrect.width, drawrect.height / 3.0f);
			} else {
				app.rect(drawrect.x + drawrect.width / 3.0f, drawrect.y,
						drawrect.width / 3.0f, drawrect.height);
			}
			if (isClicked) 		{ app.fill(color_pressed); } 
			else if (isHovered) 	{ app.fill(color_hovered); } 
			else 				{ app.fill(color_standby); }
			if (sliderAxis == nAlign.HORIZONTAL) {
				float x = drawrect.x + sliderVal * (drawrect.width - drawrect.width * sliderFact);
				float y = drawrect.y;
				float w = drawrect.width * sliderFact;
				float h = drawrect.height;
				app.rect(x,y,w,h);
			} else {
				float x = drawrect.x;
				float y = drawrect.y + sliderVal * (drawrect.height - drawrect.height * sliderFact);
				float w = drawrect.width;
				float h = drawrect.height * sliderFact;
				app.rect(x,y,w,h);
			}
		}

		// OUTLINE    OUTLINE
		if (!outlineAfterChild) draw_outline();

		if (helper && gui.do_help) {
			app.noStroke(); app.fill(gui.helper_light);
			float RS = gui.book.RS;
			app.rect(drawrect.x - RS/16f,drawrect.y - RS/16f,
					drawrect.width + RS/8f, drawrect.height + RS/8f);
		}
		
		        // TEXT    TEXT
		if (show_text && text != null && text.length() > 0) {// && gui.scale >= 0.3
			String l = text;
			if (showCursor) {
				String str = text.substring(0, cursorPos);
				String end = text.substring(cursorPos, text.length());
				if (cursorCount < cursorCycle / 2) l = str + "|" + end;
				else l = str + " " + end;
				cursorCount++;
				if (cursorCount > cursorCycle) cursorCount = 0;
			}
			if (l.length() > 0) {
				//		          app.fill(look.textColor); 
				app.textAlign(textAlignX, textAlignY);
				float tx = getX();
				float ty = getY();
				if (textAlignY == nAlign.CENTER)         
					ty += (getSY() / 2.0f)
//					- (textFont * globalscale / 6.0f)
					;
				else if (textAlignY == nAlign.BOTTOM) 
					ty += getSY()
//					- (textFont / 10f)
					;
				if (textAlignX == nAlign.LEFT)        tx += textFont * globalscale / 4.0f;
				else if (textAlignX == nAlign.CENTER) tx += getSX() / 2f;
				else if (textAlignX == nAlign.RIGHT) 
					tx += getSX() - app.textWidth('m',textFont * globalscale)*l.length();
				//	
				float line_max_char = ((getSX() - textFont * globalscale * 2f) / 
						app.textWidth('m',textFont * globalscale));
				if (set_line_length > 0) line_max_char = set_line_length;
				if (!auto_line_return || l.length() < line_max_char) 
					app.text(l, tx, ty, textFont * globalscale, color_text); // * globalscale
				else {
					int max_line_char = (int) (((getSX() - textFont * globalscale * 2f) / 
							(app.textWidth("m",textFont * globalscale))));// * globalscale
					if (set_line_length > 0) max_line_char = set_line_length;
					if (max_line_char <= 0) max_line_char = 1;
					int next_return = l.length();
					int printed_char = 0;
					int line_cnt = 0;
					while (printed_char < l.length()) {
						next_return = l.length();
						for (int i = printed_char ; i < l.length() ; i++) 
							if (l.charAt(i) == '\n') {
								next_return = i; break; }
						int line_end = Math.min(
								Math.min(l.length(), next_return + 1), printed_char + max_line_char + 1);
						if (line_end == next_return + 1) line_end--;
//						if (line_end - printed_char < 2) line_end++;
						String line_string = l.substring(printed_char, line_end);
						if (line_end == next_return) printed_char++;
						printed_char += line_string.length();
						line_cnt++;
						app.text(line_string, tx, ty - (line_cnt*textFont * globalscale), 
								textFont * globalscale, color_text);// * globalscale
					}
				}
			}
		}
		if (has_crash) {
			app.noFill();
			app.stroke(255,0,0,100,5f);
			app.rect(globalrect);
			app.line(globalrect.x, globalrect.y, 
					globalrect.x + globalrect.width, 
					globalrect.y + globalrect.height );
			app.line(globalrect.x + globalrect.width,globalrect.y,
					globalrect.x, globalrect.y + globalrect.height);
		}
		
	}
	
	public int line_number(String l) {
		int max_line_char = (int) (((getSX() - textFont * globalscale * 2f) / 
				(app.textWidth("m",textFont * globalscale))));
		if (set_line_length > 0) max_line_char = set_line_length;
		if (max_line_char <= 0) max_line_char = 1;
		int next_return = l.length();
		int printed_char = 0;
		int line_cnt = 0;
		while (printed_char < l.length()) {
			next_return = l.length();
			for (int i = printed_char ; i < l.length() ; i++) 
				if (l.charAt(i) == '\n') {
					next_return = i; break; }
			int line_end = Math.min(
					Math.min(l.length(), next_return + 1), printed_char + max_line_char + 1);
			if (line_end == next_return + 1) line_end--;
//			if (line_end - printed_char < 2) line_end++;
			String line_string = l.substring(printed_char, line_end);
			if (line_end == next_return) printed_char++;
			printed_char += line_string.length();
			line_cnt++;
		}
		return line_cnt;
	}
	
	private void draw_outline() {
		app.noFill();
		if (/*isField &&*/ isSelected) app.stroke(color_outline_selected);
		else if (showOutline || (hoverOutline && isHovered)) app.stroke(color_outline);
		else app.noStroke();
		float wf = 1.0F * globalscale;
		if (constantOutlineWeight) { wf = 1f * globalscale / gui.scale; app.strokeWeight(outlineWeight / gui.scale); }
		//else app.strokeWeight(outlineWeight + 2); // debug +2 to see outline around edges
		else app.strokeWeight(outlineWeight * globalscale);
		
		//	        
		//	        
		//	        if (shapeRound) app.ellipse(getX() + wf*look.outlineWeight/2, getY() + wf*look.outlineWeight/2, 
		//	             getSX() - wf*look.outlineWeight, getSY() - wf*look.outlineWeight);
		//	        else if (shapeLosange) {app.quad(getX() + getSX()/2, getY() + wf*look.outlineWeight/2, 
		//	                                     getX() + getSX() - wf*look.outlineWeight/2, getY() + getSY()/2, 
		//	                                     getX() + getSX()/2, getY() + getSY() - wf*look.outlineWeight/2, 
		//	                                     getX() + wf*look.outlineWeight/2, getY() + getSY()/2  );}
		//	        else 
//		app.rect(getX() + wf*outlineWeight/2f, getY() + wf*outlineWeight/2f, 
//				getSX() - wf*outlineWeight, getSY() - wf*outlineWeight);
		
		if (shape == Shape.RECT) {
			app.rect(drawrect.x + wf*outlineWeight/2f, drawrect.y + wf*outlineWeight/2f, 
					drawrect.width - wf*outlineWeight, drawrect.height - wf*outlineWeight);
		} else if (shape == Shape.CIRCLE) {
			Rectangle rct = new Rectangle();
			rct.set(drawrect.x + wf*outlineWeight/2f, drawrect.y + wf*outlineWeight/2f, 
					drawrect.width - wf*outlineWeight, drawrect.height - wf*outlineWeight);
			app.circle(rct);
		} else if (shape == Shape.DIAMOND) {
			Rectangle rct = new Rectangle();
			rct.set(drawrect.x + wf*outlineWeight/2f, drawrect.y + wf*outlineWeight/2f, 
					drawrect.width - wf*outlineWeight, drawrect.height - wf*outlineWeight);
			app.diamond(rct);
		}
		
	}
	
	public void draw_debug() {
//		if (showOrigin) {
//			float px = globalPos.x, py = globalPos.y;
//			app.fill(Utl.color(255,0,0));
//			app.noStroke();
//			app.rect(px-2,py-2,4,4);
//		}
		
//		app.stroke(Utl.color(255));
//		app.strokeWeight(2);
//		app.rect(maskedrect);
		
//		float wf = 1.0F;
//		if (mouseOver) {
//			app.stroke(Utl.color(255));
//			app.strokeWeight(4);
//			app.rect(getX() + wf*outlineWeight/2, getY() + wf*outlineWeight/2, 
//					getSX() - wf*outlineWeight, getSY() - wf*outlineWeight);
//		}
//		if (mouseOverZone) {
//			app.stroke(Utl.color(255,0,0));
//			app.strokeWeight(2);
//			app.rect(getX() + wf*outlineWeight/2, getY() + wf*outlineWeight/2, 
//					getSX() - wf*outlineWeight, getSY() - wf*outlineWeight);
//		}
		
		if (mouseOver) highlight_parent();
		for (nWidget w : childs) if (w.visible) w.draw_debug();
	}
	
	public void highlight_parent() {
		app.stroke(Utl.color(255,0,0));
		app.strokeWeight(2);
		app.rect(getX(), getY(), getSX(), getSY());
		if (parent != null) parent.highlight_parent();
	}
	
	public nWidget buildDrawStackAndMask(ArrayList<nWidget> s, Rectangle mask) { 
		if (visible) {
			s.add(this);
			boolean isInMask = Utl.intersect(mask, globalrect, maskedrect);
			if (!isInMask) maskedrect.set(0,0,0,0);
			if (maskChildren)
				for (nWidget w : childs) w.buildDrawStackAndMask(s, maskedrect);
			else 
				for (nWidget w : childs) w.buildDrawStackAndMask(s, mask);
		}
		return this;
	}

	public boolean getDrawVisibility() { return visible && 
			((globalscale >= scale_min && globalscale <= scale_max) || 
					!scale_limit); }
	
	public boolean getVisibility() { return visible && 
			((globalscale >= scale_min && globalscale <= scale_max) || 
					!scale_limit || (scale_limit && scale_limit_nodraw)); }
	
	public nWidget drawMasked() { 
//		if (backgroundRender && !gui.back_is_rendering) {
//			
//			return this;
//		}

		if (
//				!(backgroundRender && !gui.back_is_rendering) && 
				vfx) app.fx();
		
		
		if (getVisibility()) { 

			if (
//					!(backgroundRender && !gui.back_is_rendering) && 
					getDrawVisibility() && do_draw) drawer.drawing();
			
			if (custom_drawer != null && getDrawVisibility()) {
				
//				app.log(nTransform.toString(warptransform));
				
				app.push();
				app.transf(warptransform);
				
//				if (!(backgroundRender && !gui.back_is_rendering)) 
					runLaunchMetode("custom_drawer_drawing");

				app.pop();
			}

			boolean pop = false;
			if (
//					!(backgroundRender && !gui.back_is_rendering) && 
					maskChildren) {
			    ScissorStack.calculateScissors(
			    		gui.cam, app.getTransformMatrix(), 
			    		maskedrect, maskingrect);
				app.flush();
				pop = ScissorStack.pushScissors(maskingrect); //return false if mask area =0
				if (pop) gui.scissors.add(maskingrect);
//				Utl.logn("a"+gui.scissors.size());
			}

			if (pop || !maskChildren) 
				for (nWidget w : childs) {
					w.drawMasked();
//					if (w.backgroundRender) {
//						if (gui.back_is_rendering) w.drawMasked();
//						else app.alpha_rect(maskedrect);
//					}
//					else w.drawMasked();
				}

			if (
//					!(backgroundRender && !gui.back_is_rendering) && 
					maskChildren) {
				app.flush();
				if (pop) ScissorStack.popScissors();
				if (pop) gui.scissors.remove(gui.scissors.get(gui.scissors.size() - 1));
//				Utl.logn("b"+gui.scissors.size());
			}

			if (
//					!(backgroundRender && !gui.back_is_rendering) && 
					outlineAfterChild) draw_outline();

		}

		if (
//				!(backgroundRender && !gui.back_is_rendering) && 
				vfx) app.noFx();
//		if (vfx) app.noFx((int)maskingrect.x, (int)maskingrect.y, 
//				(int)maskingrect.width, (int)maskingrect.height);
		
		return this;
	}
	
	
	private boolean boundrect_calc_done = false, parentrect_calc_done = false,
			childstackorigin_calc_done = false, globalrect_calc_done = false;

	//recalc globalrect right now
	public void force_calc() {
		undone_calc();
		globalrect_calc();
	}
	public void force_calc_child() {
		undone_calc_child();
		globalrect_calc();
	}
	
	public void undone_calc() {
		boundrect_calc_done = false;
		parentrect_calc_done = false;
		globalrect_calc_done = false;
		childstackorigin_calc_done = false;
	}
	public void undone_calc_child() {
		boundrect_calc_done = false;
		parentrect_calc_done = false;
		globalrect_calc_done = false;
		childstackorigin_calc_done = false;
		for(nWidget w : childs) w.undone_calc_child();
	}
	
	public Vector2 revertWarp(Vector2 v) {
		v.set(warptransform.revert(v));
		return v;
	}

	public void globalrect_calc() {
		if (!globalrect_calc_done) {
			
			parentrect_calc();
			
			if (parent != null && !parent.globalrect_calc_done) 
				parent.globalrect_calc();

			warptransform.reset();
			if (parent != null) warptransform.set(parent.warptransform);
			globalscale = warptransform.getScale();
			globalrot = warptransform.getRotation();
			
			globalPos.set(parentPos);

			globalPos.set(warptransform.transform(globalPos));

			warptransform.push();
			warptransform.translate(parentPos);
			
			if (parent != null) {
//				if (parent.rectOriginX == nAlign.CENTER) globalPos.x -= parent.globalrect.width / 2.0f;
//				else if (parent.rectOriginX == nAlign.RIGHT) globalPos.x -= parent.globalrect.width;
//				if (parent.rectOriginY == nAlign.CENTER) globalPos.y -= parent.globalrect.height / 2.0f;
//				else if (parent.rectOriginY == nAlign.TOP) globalPos.y -= parent.globalrect.height;
			}
			
			globalrect.set(globalPos.x,globalPos.y,
					boundedSize.x * globalscale,
					boundedSize.y * globalscale);

			if (warpChildren) {
				warptransform.push();
				warptransform.scale(warpScale);
				warptransform.rotate(warpRot);
				warptransform.translate(warpTranslate);
			}
			
			globalrect_calc_done = true;
			for (nWidget w : childs) if (w.visible) w.globalrect_calc();
			if (warpChildren) {
				warptransform.pop();
			}
			warptransform.pop();
//			if (backgroundRender) gui.backgroundRender = this;
		}
	}
	
	public void boundrect_calc() {
		if (!boundrect_calc_done) {

			//if size copy get cible size
			if (copy_size_x) setSize.x = copy_size_inc + 
					Math.max(copy_size_min, copy_size_cible.boundedSize.x);
			if (copy_size_y) setSize.y = copy_size_inc + 
					Math.max(copy_size_min, copy_size_cible.boundedSize.y);
			
			if (glue_cible != null) {
				if (glue_side == nAlign.LEFT) {
					setPos.x = glue_cible.parentPos.x - boundedSize.x - glue_space;
					if (glue_align == nAlign.TOP)
						setPos.y = glue_cible.parentPos.y + 
								(glue_cible.boundedSize.y - boundedSize.y);
					else if (glue_align == nAlign.CENTER)
						setPos.y = glue_cible.parentPos.y + 
								(glue_cible.boundedSize.y - boundedSize.y) / 2f;
					else if (glue_align == nAlign.BOTTOM)
						setPos.y = glue_cible.parentPos.y;
				} else if (glue_side == nAlign.RIGHT) {
					setPos.x = glue_cible.parentPos.x + glue_cible.boundedSize.x + glue_space;
					if (glue_align == nAlign.TOP)
						setPos.y = glue_cible.parentPos.y + 
								(glue_cible.boundedSize.y - boundedSize.y);
					else if (glue_align == nAlign.CENTER)
						setPos.y = glue_cible.parentPos.y + 
								(glue_cible.boundedSize.y - boundedSize.y) / 2f;
					else if (glue_align == nAlign.BOTTOM)
						setPos.y = glue_cible.parentPos.y;
				} else if (glue_side == nAlign.BOTTOM) {
					setPos.y = glue_cible.parentPos.y - boundedSize.y - glue_space;
					if (glue_align == nAlign.RIGHT)
						setPos.x = glue_cible.parentPos.x + 
								(glue_cible.boundedSize.x - boundedSize.x);
					else if (glue_align == nAlign.CENTER)
						setPos.x = glue_cible.parentPos.x + 
								(glue_cible.boundedSize.x - boundedSize.x) / 2f;
					else if (glue_align == nAlign.LEFT)
						setPos.x = glue_cible.parentPos.x;
				} else if (glue_side == nAlign.TOP) {
					setPos.y = glue_cible.parentPos.y + glue_cible.boundedSize.y + glue_space;
					if (glue_align == nAlign.RIGHT)
						setPos.x = glue_cible.parentPos.x + 
								(glue_cible.boundedSize.x - boundedSize.x);
					else if (glue_align == nAlign.CENTER)
						setPos.x = glue_cible.parentPos.x + 
								(glue_cible.boundedSize.x - boundedSize.x) / 2f;
					else if (glue_align == nAlign.LEFT)
						setPos.x = glue_cible.parentPos.x;
				}
			}
			
			if (boundChild) {
				for (nWidget w : childs) if (w.visible && w.boundParent) w.parentrect_calc();
				
				for (nWidget w : extraBounds) if (w.visible && w.boundParent) w.parentrect_calc();
				
				
				
				//calc boundrect from child parentrect
				float bx = - boundOutspace, 
						by = - boundOutspace, 
						bx2 = setSize.x + boundOutspace, 
						by2 = setSize.y + boundOutspace;

				for (nWidget w : childs) if (w.getVisibility() && w.boundParent) {
					bx = Math.min(bx, w.parentPos.x - boundOutspace);
					by = Math.min(by, w.parentPos.y - boundOutspace); }
				for (nWidget w : extraBounds) if (w.getVisibility() && w.boundParent) {
					bx = Math.min(bx, w.parentPos.x - boundOutspace);
					by = Math.min(by, w.parentPos.y - boundOutspace); }
				for (nWidget w : childs) if (w.getVisibility() && w.boundParent) {
					bx2 = Math.max(bx2, 
							w.parentPos.x + w.boundedSize.x + boundOutspace);
					by2 = Math.max(by2, 
							w.parentPos.y + w.boundedSize.y + boundOutspace); }
				for (nWidget w : extraBounds) if (w.getVisibility() && w.boundParent) {
					bx2 = Math.max(bx2, 
							w.parentPos.x + w.boundedSize.x + boundOutspace);
					by2 = Math.max(by2, 
							w.parentPos.y + w.boundedSize.y + boundOutspace); }
				for (nWidget w : childs) if (w.getVisibility()) {
					w.parentPos.x += boundOutspace;
					w.parentPos.y += boundOutspace; }
//				for (nWidget w : extraBounds) if (w.visible) {
//					w.parentPos.x += boundOutspace;
//					w.parentPos.y += boundOutspace; }
				
				boundedPos.set(bx, by);
				boundedSize.set(bx2 - bx, by2 - by);
				if (!isStacked) {
					boundedPos.x += setPos.x + 0*boundOutspace;
					boundedPos.y += setPos.y + 0*boundOutspace;
				}
			} else {
				boundedSize.set(setSize.x, setSize.y);
			}
			boundrect_calc_done = true;
		}
	}
	
	public void parentrect_calc() {
		if (!parentrect_calc_done) {
			boundrect_calc();
			
			if (isStacked && parent != null) {
				
				parentPos.x = 0; parentPos.y = 0;

				parent.child_stack_size_calc();
//				parentPos.x += parent.childStackOrigin.x;
//				parentPos.y += parent.childStackOrigin.y;
				
				stackOffset.set(0,0);
				for (nWidget w : parent.childs) if (w.getVisibility() && w.isStacked) {
					if (w == this) break;
					if (parent.stackAxis == nAlign.VERTICAL) {
						if (parent.stackDirection == nAlign.UP) {
							stackOffset.add(0, w.boundedSize.y + parent.stackSpacing);
						} else {
							stackOffset.add(0, -w.boundedSize.y - parent.stackSpacing);
						}
					} else {
						if (parent.stackDirection == nAlign.RIGHT)  {
							stackOffset.add(w.boundedSize.x + parent.stackSpacing, 0);
						} else {
							stackOffset.add(-w.boundedSize.x - parent.stackSpacing, 0);
						}
					}
				}
				
				parentPos.x += stackOffset.x;
				parentPos.y += stackOffset.y;
				
				if (parent.stackAxis == nAlign.VERTICAL) {
					if (parent.stackDirection == nAlign.DOWN) {
						parentPos.y += parent.childStackSize.y;
						parentPos.y -= boundedSize.y;
					} 
					if (parent.stackAlign == nAlign.CENTER) {
						parentPos.x += parent.childStackSize.x / 2f - 
								boundedSize.x / 2f;
					} else if (parent.stackAlign == nAlign.RIGHT) {
						parentPos.x += parent.childStackSize.x - boundedSize.x;
					}
				} else {
					if (parent.stackDirection == nAlign.LEFT)  {
						parentPos.x += parent.childStackSize.x;
						parentPos.x -= boundedSize.x;
					} 
					if (parent.stackAlign == nAlign.CENTER) {
						parentPos.y += parent.childStackSize.y / 2f - 
								boundedSize.y / 2f;
					} else if (parent.stackAlign == nAlign.TOP) {
						parentPos.y += parent.childStackSize.y - boundedSize.y;
					}
				}
				
				
			} else {
				parentPos.set(setPos);
			}
			
			Vector2 bb = new Vector2(boundedSize);
			bb = bb.rotateRad(-globalrot);
			
			if (rectOriginX == nAlign.CENTER) parentPos.x -= bb.x / 2.0f;
			else if (rectOriginX == nAlign.RIGHT) parentPos.x -= bb.x;
			if (rectOriginY == nAlign.CENTER) parentPos.y -= bb.y / 2.0f;
			else if (rectOriginY == nAlign.TOP) parentPos.y -= bb.y;

			parentrect_calc_done = true;
		}
	}
	
	public void child_stack_size_calc() {
		if (!childstackorigin_calc_done) {
			//calc child boundrect
			for (nWidget w : childs) if (w.visible && w.isStacked) w.boundrect_calc();
			
			//calc child stack origin from child boundrect w/h
			childStackSize.set(0,0);
			if (stackAxis == nAlign.VERTICAL) { 
				for (nWidget w : childs) if (w.getVisibility() && w.isStacked) {
					childStackSize.x = Math.max(childStackSize.x, w.boundedSize.x);
					childStackSize.y += w.boundedSize.y + stackSpacing; }
				if (childStackSize.y > 0) childStackSize.y -= stackSpacing;
			} else {
				for (nWidget w : childs) if (w.getVisibility() && w.isStacked) {
					childStackSize.y = Math.max(childStackSize.y, w.boundedSize.y);
					childStackSize.x += w.boundedSize.x + stackSpacing; }
				if (childStackSize.x > 0) childStackSize.x -= stackSpacing;
			}
			
//			childStackOrigin.set(0,0);
			
//			if (boundChild) {
//				if (rectOriginX == nAlign.LEFT) 
//					childStackOrigin.x += boundOutspace;
//				else if (rectOriginX == nAlign.CENTER) 
//					childStackOrigin.x += 0;
//				else if (rectOriginX == nAlign.RIGHT) 
//					childStackOrigin.x -= 0*boundOutspace;
//				if (rectOriginY == nAlign.BOTTOM) 
//					childStackOrigin.y += boundOutspace;
//				else if (rectOriginY == nAlign.CENTER) 
//					childStackOrigin.y += 0;
//				else if (rectOriginY == nAlign.TOP) 
//					childStackOrigin.y -= 0*boundOutspace;
//			}
			
			childstackorigin_calc_done = true;
		}
	}
	
	
	
	
	
	//      GETTER / SETTER

	public float getX() { return globalrect.x; }
	public float getY() { return globalrect.y; }
	public Vector2 getPos() { return new Vector2(globalrect.x, globalrect.y); }
	public Vector2 getPos(nAlign x, nAlign y) { 
		Vector2 pos = new Vector2(globalrect.x, globalrect.y);
		if (x == nAlign.CENTER) pos.x += globalrect.width / 2f;
		else if (x == nAlign.RIGHT) pos.x += globalrect.width;
		if (y == nAlign.CENTER) pos.y += globalrect.height / 2f;
		else if (y == nAlign.UP) pos.y += globalrect.height;
		return pos; 
	}
	public float getSX() { return globalrect.width; }
	public float getSY() { return globalrect.height; }
	

	public float getSliderVal() { return sliderVal; }
	public float getSliderValInMinMax() { return slideValToMinmax(sliderVal); }
	public float getSliderValInRange(float min, float max) { 
		return min + sliderVal * (max - min); }
	public int getSliderValInRangeInteger(int min, int max) { 
		return Utl.toint(min + sliderVal * (max - min)); }
	
	public nWidget setSliderVal(float v) {   // between 0 and 1
		sliderVal = MathUtils.clamp(v, 0f, 1f);
		if (vlslide_flt != null) vlslide_flt.set(slideValToMinmax(sliderVal));
		if (vlslide_int != null) vlslide_int.set(slideValToMinmax(sliderVal));
		if (v != sliderVal) runEventList("eventSliderChangeRun");
		return this;
	}

	public nWidget setSliderValInRange(float v) {   // between sliderMin and Max
		sliderVal = MathUtils.clamp(slideMinmaxToVal(v), 0f, 1f);
		if (vlslide_flt != null) vlslide_flt.set(slideValToMinmax(sliderVal));
		if (vlslide_int != null) vlslide_int.set(slideValToMinmax(sliderVal));
		if (slideMinmaxToVal(v) != sliderVal) runEventList("eventSliderChangeRun");
		return this;
	}
	
	public void positionChange() {
		if (vl_vec != null) { vl_vec.set(setPos.x, setPos.y); }
	}
	
	//  model setter override
	public nWidget setText(String s) { 
		super.setText(s); 
		runEventList("eventFieldChangeRun");
		return this; }
	
	
	public nWidget setVisibility(boolean s) { 
		if (s != visible) runEventList("eventVisibilityRun");
		super.setVisibility(s); return this; }
	
	

	public nWidget stopSizeCopy() { 
		copy_size_cible = null; copy_size_x = false; copy_size_y = false; return this; }
	public nWidget setSizeCopyX(nWidget c) { 
		copy_size_cible = c; copy_size_x = true; return this; }
	public nWidget setSizeCopyY(nWidget c) { 
		copy_size_cible = c; copy_size_y = true; return this; }
	public nWidget setSizeCopyMin(float c) { 
		copy_size_min = c;; return this; }
	public nWidget setSizeCopyIncr(float c) { 
		copy_size_inc = c;; return this; }
	

	public nWidget toBack() {
		if (parent != null) { parent.childs.remove(this); parent.childs.add(0, this); } 
		else { gui.orphan_widgets.remove(this); gui.orphan_widgets.add(0, this); }
//		if (parent != null) {
//			int i = 0;
//			for (nWidget w : parent.childs) {
//				if (w.vwp_stack_index != null) w.vwp_stack_index.set(i); i++; } } 
//		if (parent == null) {
//			int i = 0;
//			for (nWidget w : gui.orphan_widgets) {
//				if (w.vwp_stack_index != null) w.vwp_stack_index.set(i); i++; } } 
		return this; }
	public nWidget toFront() {
		if (parent != null) { parent.childs.remove(this); parent.childs.add(this); } 
		else { gui.orphan_widgets.remove(this); gui.orphan_widgets.add(this); }
//		if (parent != null) {
//			int i = 0;
//			for (nWidget w : parent.childs) {
//				if (w.vwp_stack_index != null) w.vwp_stack_index.set(i); i++; } } 
//		if (parent == null) {
//			int i = 0;
//			for (nWidget w : gui.orphan_widgets) {
//				if (w.vwp_stack_index != null) w.vwp_stack_index.set(i); i++; } } 
		return this; }

	public boolean isOn() { return switchState; }
	public boolean getSwitchState() { return switchState; }
	public boolean isClicked() { return isClicked; }
	public boolean isRightClicked() { return isRightClicked; }
	public boolean isHovered() { return isHovered; }
	
	

		  
	public nWidget setSwitchState(boolean s) { if (s) setOn(); else setOff(); return this; }
	public nWidget setOn() { 
		if (!switchState) {
			switchState = true;
			if (vl_boo != null) { vl_boo.set(switchState); }
			runEventList("eventSwitchOnRun");
			runEventList("eventSwitchRun");
//			for (nWidget b : excludes) b.setOff(); 
		}
		return this;
	}
//	public void forceOn() {
//		switchState = true;
//		nRunnable.runEvents(eventSwitchOnRun);
////		for (nWidget b : excludes) b.setOff(); 
//	}

	public nWidget setOff() {
		if (switchState) {
			switchState = false;
			if (vl_boo != null) { vl_boo.set(switchState); }
			runEventList("eventSwitchOffRun"); 
			runEventList("eventSwitchRun"); } 
		return this; }
//	public void forceOff() {
//		switchState = false;
//		nRunnable.runEvents(eventSwitchOffRun); }

	
	
	


	@Override
	public void build_lauchables() {
		eventMouseEnterRun = newEventList("eventMouseEnterRun", new ArrayList<nRun>());
		eventMouseLeaveRun = newEventList("eventMouseLeaveRun", new ArrayList<nRun>());
		eventPressRun = newEventList("eventPressRun", new ArrayList<nRun>());
		eventReleaseRun = newEventList("eventReleaseRun", new ArrayList<nRun>());
		eventTriggerRightRun = newEventList("eventTriggerRightRun", new ArrayList<nRun>());
		eventSwitchRun = newEventList("eventSwitchRun", new ArrayList<nRun>());
		eventSwitchOnRun = newEventList("eventSwitchOnRun", new ArrayList<nRun>());
		eventSwitchOffRun = newEventList("eventSwitchOffRun", new ArrayList<nRun>());
		eventTriggerRun = newEventList("eventTriggerRun", new ArrayList<nRun>());
		eventGrabRun = newEventList("eventGrabRun", new ArrayList<nRun>());
		eventDragRun = newEventList("eventDragRun", new ArrayList<nRun>());
		eventLiberateRun = newEventList("eventLiberateRun", new ArrayList<nRun>());
		eventFieldChangeRun = newEventList("eventFieldChangeRun", new ArrayList<nRun>());
		eventFieldEnterRun = newEventList("eventFieldEnterRun", new ArrayList<nRun>());
		eventFieldUnselectRun = newEventList("eventFieldUnselectRun", new ArrayList<nRun>());
		eventSliderChangeRun = newEventList("eventSliderChangeRun", new ArrayList<nRun>());
		eventClearRun = newEventList("eventClearRun", new ArrayList<nRun>());
		eventVisibilityRun = newEventList("eventVisibilityRun", new ArrayList<nRun>());
		eventLogicRun = newEventList("eventLogicRun", new ArrayList<nRun>());
		eventSelectRun = newEventList("eventSelectRun", new ArrayList<nRun>());
		eventUnselectRun = newEventList("eventUnselectRun", new ArrayList<nRun>());
		newLaunchMetode("custom_drawer_drawing", new nRun() { public void run() { custom_drawer_drawing(); }});
	}
	
	private void custom_drawer_drawing() { custom_drawer.drawing(); }

	//events
	ArrayList<nRun> eventMouseEnterRun;
	ArrayList<nRun> eventMouseLeaveRun;
	ArrayList<nRun> eventPressRun;
	ArrayList<nRun> eventReleaseRun;
	ArrayList<nRun> eventTriggerRightRun;
	ArrayList<nRun> eventSwitchRun;
	ArrayList<nRun> eventSwitchOnRun;
	ArrayList<nRun> eventSwitchOffRun;
	ArrayList<nRun> eventTriggerRun;
	ArrayList<nRun> eventGrabRun;
	ArrayList<nRun> eventDragRun;
	ArrayList<nRun> eventLiberateRun;
	ArrayList<nRun> eventFieldChangeRun;
	ArrayList<nRun> eventFieldEnterRun;
	ArrayList<nRun> eventFieldUnselectRun;
	ArrayList<nRun> eventSliderChangeRun;
	ArrayList<nRun> eventClearRun;
	ArrayList<nRun> eventVisibilityRun;
	ArrayList<nRun> eventLogicRun;
	ArrayList<nRun> eventSelectRun;
	ArrayList<nRun> eventUnselectRun;
	
//	ArrayList<nRunnable> eventMouseEnterRun = new ArrayList<nRunnable>();
//	ArrayList<nRunnable> eventMouseLeaveRun = new ArrayList<nRunnable>();
//	ArrayList<nRunnable> eventPressRun = new ArrayList<nRunnable>();
//	ArrayList<nRunnable> eventReleaseRun = new ArrayList<nRunnable>();
//	ArrayList<nRunnable> eventTriggerRightRun = new ArrayList<nRunnable>();
//	ArrayList<nRunnable> eventSwitchRun = new ArrayList<nRunnable>();
//	ArrayList<nRunnable> eventSwitchOnRun = new ArrayList<nRunnable>();
//	ArrayList<nRunnable> eventSwitchOffRun = new ArrayList<nRunnable>();
//	ArrayList<nRunnable> eventTriggerRun = new ArrayList<nRunnable>();
//	ArrayList<nRunnable> eventGrabRun = new ArrayList<nRunnable>();
//	ArrayList<nRunnable> eventDragRun = new ArrayList<nRunnable>();
//	ArrayList<nRunnable> eventLiberateRun = new ArrayList<nRunnable>();
//	ArrayList<nRunnable> eventFieldChangeRun = new ArrayList<nRunnable>();
//	ArrayList<nRunnable> eventFieldEnterRun = new ArrayList<nRunnable>();
//	ArrayList<nRunnable> eventFieldUnselectRun = new ArrayList<nRunnable>();
//	ArrayList<nRunnable> eventSliderChangeRun = new ArrayList<nRunnable>();
//	ArrayList<nRunnable> eventClearRun = new ArrayList<nRunnable>();
//	ArrayList<nRunnable> eventVisibilityRun = new ArrayList<nRunnable>();
//	ArrayList<nRunnable> eventLogicRun = new ArrayList<nRunnable>();
//	ArrayList<nRunnable> eventSelectRun = new ArrayList<nRunnable>();
//	ArrayList<nRunnable> eventUnselectRun = new ArrayList<nRunnable>();
	
	public nWidget addEventSelect(nRun r) { eventSelectRun.add(r); return this; }
	public nWidget removeEventSelect(nRun r) { eventSelectRun.remove(r); return this; }
	public nWidget addEventUnselect(nRun r) { eventUnselectRun.add(r); return this; }
	public nWidget removeEventUnselect(nRun r) { eventUnselectRun.remove(r); return this; }
	
	public nWidget addEventLogic(nRun r) { eventLogicRun.add(r); return this; }
	public nWidget removeEventLogic(nRun r) { eventLogicRun.remove(r); return this; }
	public nWidget clearEventLogic() { eventLogicRun.clear(); return this; }
	public nWidget addEventVisibility(nRun r) { eventVisibilityRun.add(r); return this; }
	public nWidget removeEventVisibility(nRun r) { eventVisibilityRun.remove(r); return this; }
	public nWidget clearEventVisibility() { eventVisibilityRun.clear(); return this; }
	public nWidget addEventMouseEnter(nRun r) { eventMouseEnterRun.add(r); return this; }
	public nWidget addEventMouseLeave(nRun r) { eventMouseLeaveRun.add(r); return this; }
	public nWidget addEventTriggerRight(nRun r)         { eventTriggerRightRun.add(r); return this; }
	public nWidget addEventPressed(nRun r)      { eventPressRun.add(r); return this; }
	public nWidget addEventRelease(nRun r)    { eventReleaseRun.add(r); return this; }
	public nWidget addEventTrigger(nRun r)         { eventTriggerRun.add(r); return this; }
	public nWidget removeEventTrigger(nRun r)      { eventTriggerRun.remove(r); return this; }
	public nWidget clearEventTrigger()                 { eventTriggerRun.clear(); return this; }
	public nWidget addEventTrigger_Builder(nRun r) { eventTriggerRun.add(r); r.builder = this; return this; }
	public nWidget addEventSwitch(nRun r)   { eventSwitchRun.add(r); return this; }
	public nWidget removeEventSwitch(nRun r)       { eventSwitchRun.remove(r); return this; }
	public nWidget addEventSwitchOn(nRun r)   { eventSwitchOnRun.add(r); return this; }
	public nWidget addEventSwitchOn_Builder(nRun r)   { r.builder = this; eventSwitchOnRun.add(r); return this; }
	public nWidget addEventSwitchOff(nRun r)  { eventSwitchOffRun.add(r); return this; }
	public nWidget clearEventSwitchOn()   { eventSwitchOnRun.clear(); return this; }
	public nWidget clearEventSwitchOff()  { eventSwitchOffRun.clear(); return this; }
	public nWidget addEventGrab(nRun r)       { eventGrabRun.add(r); return this; }
	public nWidget addEventDrag(nRun r)       { eventDragRun.add(r); return this; }
	public nWidget removeEventDrag(nRun r)       { eventDragRun.remove(r); return this; }
	public nWidget addEventLiberate(nRun r)   { eventLiberateRun.add(r); return this; }
	public nWidget removeEventLiberate(nRun r)   { eventLiberateRun.remove(r); return this; }
	public nWidget addEventFieldChange(nRun r) { eventFieldChangeRun.add(r); return this; }
	public nWidget removeEventFieldChange(nRun r) { eventFieldChangeRun.remove(r); return this; }
	public nWidget clearEventFieldChange() { eventFieldChangeRun.clear(); return this; }
	public nWidget addEventFieldEnter(nRun r) { eventFieldEnterRun.add(r); return this; }
	public nWidget addEventFieldUnselect(nRun r) { eventFieldUnselectRun.add(r); return this; }
	public nWidget addEventSliderChange(nRun r) { eventSliderChangeRun.add(r); return this; }
	public nWidget removeEventSliderChange(nRun r) { eventSliderChangeRun.remove(r); return this; }
	public nWidget clearEventSliderChange() { eventSliderChangeRun.clear(); return this; }
	public nWidget addEventClear(nRun r) { eventClearRun.add(r); return this; }
	public nWidget removeEventClear(nRun r) { eventClearRun.remove(r); return this; }
	public nWidget clearEventClear() { eventClearRun.clear(); return this; }
	
	



	// sValue linking
	private sInt vw_int = null;
	private sFlt vw_flt = null;
	private sBoo vw_boo = null;
	private sStr vw_str = null;
	private sTab vw_tab = null;
	private String watcher_pre_text = "", watcher_post_text = "";
	private sVec vw_vec = null, vl_vec = null;
	private sBoo vl_boo = null;
	private sFlt vl_flt = null;
	private sInt vl_int = null;
	private float vl_flt_incr = 0, vl_flt_fact = 1;
	private int vl_int_incr = 0, vl_int_fact = 1;
	private sFlt vlslide_flt = null, vlfld_flt = null;
	private sInt vlslide_int = null, vlfld_int = null;
	private sStr vl_str = null;

	//widget property link
//	private sInt vwp_stack_index = null;
	
	public void svalue_linking_update() {
		if (vw_int != null) { text = watcher_pre_text + vw_int.get() + watcher_post_text; }
		if (vw_flt != null) { text = watcher_pre_text + Utl.trimFlt(vw_flt.get(), float_rez) + watcher_post_text; }
		if (vw_boo != null) { text = watcher_pre_text + vw_boo.get() + watcher_post_text; }
		if (vw_str != null) { text = watcher_pre_text + vw_str.get() + watcher_post_text; }
		if (vw_vec != null) { text = watcher_pre_text + vw_vec.x() + ":" + vw_vec.y() + watcher_post_text; }
		if (vw_tab != null) { text = watcher_pre_text + watcher_post_text; }
		if (vl_vec != null) { setPosition(vl_vec.get()); }
		if (vl_boo != null) { setSwitchState(vl_boo.get()); }
		if (vl_flt != null) { ; }
		if (vl_int != null) { ; }
		if (vlslide_flt != null && !isSliderGrabbed && 
				sliderVal != slideMinmaxToVal(vlslide_flt.get())) {
			sliderVal = slideMinmaxToVal(vlslide_flt.get());
			runEventList("eventSliderChangeRun");
		}
		if (vlslide_int != null && !isSliderGrabbed && 
				sliderVal != slideMinmaxToVal(vlslide_int.get())) {
			sliderVal = slideMinmaxToVal(vlslide_int.get());
			runEventList("eventSliderChangeRun");
		}
		if (vl_str != null) { setText(vl_str.get()); }
		if (vlfld_flt != null) { setText(Utl.trimFlt(vlfld_flt.get(), float_rez)); }
		if (vlfld_int != null) { setText(Utl.tostr(vlfld_int.get())); }
//		if (vwp_stack_index != null && 
//				getSiblingIndex() != vwp_stack_index.get()) { 
//			setSiblingIndex(vwp_stack_index.get()); }
	}
	
	public void link(sValue v) {
		v.linkWidget(this);
	}
	
	public void unlink(sValue v) {
		if (vw_int == v) { v.unlinkWidget(this); vw_int = null; }
		if (vw_flt == v) { v.unlinkWidget(this); vw_flt = null; }
		if (vw_boo == v) { v.unlinkWidget(this); vw_boo = null; }
		if (vw_str == v) { v.unlinkWidget(this); vw_str = null; }
		if (vw_tab == v) { v.unlinkWidget(this); vw_tab = null; }
		if (vl_vec == v) { v.unlinkWidget(this); vl_vec = null; }
		if (vw_vec == v) { v.unlinkWidget(this); vw_vec = null; }
		if (vl_boo == v) { v.unlinkWidget(this); vl_boo = null; }
		if (vl_flt == v) { v.unlinkWidget(this); vl_flt = null; }
		if (vl_int == v) { v.unlinkWidget(this); vl_int = null; }
		if (vlslide_flt == v) { v.unlinkWidget(this); vlslide_flt = null; }
		if (vlslide_int == v) { v.unlinkWidget(this); vlslide_int = null; }
		if (vl_str == v) { v.unlinkWidget(this); vl_str = null; }
		if (vlfld_flt == v) { v.unlinkWidget(this); vlfld_flt = null; }
		if (vlfld_int == v) { v.unlinkWidget(this); vlfld_int = null; }
//		if (vwp_stack_index == v) { v.unlinkWidget(this); vwp_stack_index = null; }
	}
	public nWidget setWatcher(sValue v) { 
		if (v.isBoo()) setWatcher((sBoo)v);
		else if (v.isInt()) setWatcher((sInt)v);
		else if (v.isFlt()) setWatcher((sFlt)v);
		else if (v.isStr()) setWatcher((sStr)v);
		else if (v.isVec()) setWatcher((sVec)v);
		else if (v.isTab()) setWatcher((sTab)v);
		return this; }
	public nWidget setWatcher(String pr, sValue v) { 
		if (v.isBoo()) setWatcher(pr, (sBoo)v, "");
		else if (v.isInt()) setWatcher(pr, (sInt)v, "");
		else if (v.isFlt()) setWatcher(pr, (sFlt)v, "");
		else if (v.isStr()) setWatcher(pr, (sStr)v, "");
		else if (v.isVec()) setWatcher(pr, (sVec)v, "");
		else if (v.isTab()) setWatcher(pr, (sTab)v, "");
		return this; }
	public nWidget setWatcher(String pr, sValue v, String po) { 
		if (v.isBoo()) setWatcher(pr, (sBoo)v, po);
		else if (v.isInt()) setWatcher(pr, (sInt)v, po);
		else if (v.isFlt()) setWatcher(pr, (sFlt)v, po);
		else if (v.isStr()) setWatcher(pr, (sStr)v, po);
		else if (v.isVec()) setWatcher(pr, (sVec)v, po);
		else if (v.isTab()) setWatcher(pr, (sTab)v, po);
		return this; }
	
	public nWidget setWatcher(sInt v) { link(v); vw_int = v; return this; }
	public nWidget setWatcher(String pr, sInt v, String po) { 
		watcher_pre_text = pr; watcher_post_text = po; setWatcher(v); return this; }
	public nWidget setWatcher(sFlt v) { link(v); vw_flt = v; return this; }
	public nWidget setWatcher(String pr, sFlt v, String po) { 
		watcher_pre_text = pr; watcher_post_text = po; setWatcher(v); return this; }
	public nWidget setWatcher(sBoo v) { link(v); vw_boo = v; return this; }
	public nWidget setWatcher(String pr, sBoo v, String po) { 
		watcher_pre_text = pr; watcher_post_text = po; setWatcher(v); return this; }
	public nWidget setWatcher(sStr v) { link(v); vw_str = v; return this; }
	public nWidget setWatcher(String pr, sStr v, String po) { 
		watcher_pre_text = pr; watcher_post_text = po; setWatcher(v); return this; }
	public nWidget setWatcher(sVec v) { link(v); vw_vec = v; return this; }
	public nWidget setWatcher(String pr, sVec v, String po) { 
		watcher_pre_text = pr; watcher_post_text = po; setWatcher(v); return this; }
	
	// unfinished
	public nWidget setWatcher(sTab v) { link(v); vw_tab = v; return this; }
	public nWidget setWatcher(String pr, sTab v, String po) { 
		watcher_pre_text = pr; watcher_post_text = po; setWatcher(v); return this; }

	public nWidget setLink(sVec v) { link(v); vl_vec = v; setPosition(v.x(),v.y()); return this; }
	public nWidget setLink(sBoo v) { link(v); vl_boo = v; setSwitchState(vl_boo.get()); return this; }
	public nWidget setLink(sFlt v, float incr, float fact) { 
		link(v); vl_flt = v; vl_flt_incr = incr; vl_flt_fact = fact; return this; }
	public nWidget setLink(sInt v, int incr, int fact) { 
		link(v); vl_int = v; vl_int_incr = incr; vl_int_fact = fact; return this; }

	public nWidget setLinkSlider(sInt v) { 
		link(v); vlslide_int = v; return this; }

	public nWidget setLinkSlider(sFlt v) { 
		link(v); vlslide_flt = v; return this; }

	public nWidget setLinkField(sFlt v) { 
		link(v); vlfld_flt = v; return this; }

	public nWidget setLinkField(sInt v) { 
		link(v); vlfld_int = v; return this; }
	
	public nWidget setLink(sStr v) { link(v); vl_str = v; setText(vl_str.get()); return this; }
	
//	public nWidget setStackIndexLink(sInt v) { link(v); vwp_stack_index = v; setSiblingIndex(v.get()); return this; }
	












	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

	//   DUMP




	//	private String infoText;
	
	//
	//	private boolean fine_view = false;
	//	private boolean always_view = false;
	//
	//	private boolean temp_passif = false;

	//	private boolean hide = false, drawerHideState = true, hoverHideState = true, show_text = true;
	//	private boolean shapeRound = false, shapeLosange = false, showInfo = false;
	//	private boolean tempPassifPreState = false;
	//	private int layer = 0;
	
	//	ArrayList<nRunnable> eventPositionChange = new ArrayList<nRunnable>();
	//	ArrayList<nRunnable> eventShapeChange = new ArrayList<nRunnable>();
	//	ArrayList<nRunnable> eventLayerChange = new ArrayList<nRunnable>();
	//	ArrayList<nRunnable> eventTopLayer = new ArrayList<nRunnable>();
	//	ArrayList<nRunnable> eventVisibilityChange = new ArrayList<nRunnable>();

	
//	ArrayList<nRunnable> eventClear = new ArrayList<nRunnable>();
//	ArrayList<nRunnable> eventFrameRun = new ArrayList<nRunnable>();
//








	//	void init(nGUI g) {
	//		frame_run = new nRunnable() { public void run() { frame(); } };
	//		gui.addEventFrame(frame_run);
	//	    localrect = new Rect();
	//	    globalrect = new Rect();
	//	    phantomrect = new Rect();
	//		changePosition();
	//	    hover = new Hoverable(g.hoverable_pile, globalrect);
	//	    hover.active = true;
	//	    hoverHideState = hover.active; 
	//		label = new String();
	//	    look = new nLook(gui.app);
	// draw
	//	    drawer = new Drawable(g.drawing_pile) { public void drawing() {
	//	      if (isViewable()) {
	//	        
	//	      }
	//	    } } ;
	//	}

	//	boolean has_been_cleared = false;
	//	public void clear() {
	//		nGUI.all_widgets.remove(this);
	//if (ndrawer != null) ndrawer.widgets.remove(this);
	//		for (nWidget w : childs) w.clear();
	//		nRunnable.runEvents(eventClear);
	//		if (gui != null) gui.removeEventFrame(frame_run);
	//		frame_run.to_clear = true;
	//		    if (look != null) look.clear(); 
	//		    look = null;
	//		    if (drawer != null) drawer.clear(); if (hover != null) hover.clear();
	//		    drawer = null; hover = null;
	//		eventPositionChange.clear(); eventShapeChange.clear(); eventLayerChange.clear(); 
	//		eventVisibilityChange.clear(); eventClear.clear(); eventFrameRun.clear(); 
	//		eventGrabRun.clear(); eventDragRun.clear(); eventLiberateRun.clear(); eventFieldChangeRun.clear();
	//		eventMouseEnterRun.clear(); eventMouseLeaveRun.clear(); 
	//		eventPressRun.clear(); eventReleaseRun.clear(); eventTriggerRun.clear(); 
	//		eventTriggerRightRun.clear(); 
	//		eventSwitchOnRun.clear(); eventSwitchOffRun.clear(); eventFieldChangeRun.clear();
	//		has_been_cleared = true;
	//	}

	//	void frame() {

	//	}

	

	//	public nWidget copy(nWidget w) {
	//eventFrameRun.clear(); for (Runnable r : w.eventFrameRun) eventFrameRun.add(r);

	//ArrayList<Runnable> eventPositionChange = new ArrayList<Runnable>();
	//ArrayList<Runnable> eventShapeChange = new ArrayList<Runnable>();
	//ArrayList<Runnable> eventLayerChange = new ArrayList<Runnable>();
	//ArrayList<Runnable> eventVisibilityChange = new ArrayList<Runnable>();
	//ArrayList<Runnable> eventClear = new ArrayList<Runnable>();
	//ArrayList<Runnable> eventFrameRun = new ArrayList<Runnable>();
	//ArrayList<Runnable> eventGrabRun = new ArrayList<Runnable>();
	//ArrayList<Runnable> eventDragRun = new ArrayList<Runnable>();
	//ArrayList<Runnable> eventLiberateRun = new ArrayList<Runnable>();
	//ArrayList<Runnable> eventMouseEnterRun = new ArrayList<Runnable>();
	//ArrayList<Runnable> eventMouseLeaveRun = new ArrayList<Runnable>();
	//ArrayList<Runnable> eventPressRun = new ArrayList<Runnable>();
	//ArrayList<Runnable> eventReleaseRun = new ArrayList<Runnable>();
	//ArrayList<Runnable> eventTriggerRun = new ArrayList<Runnable>();
	//ArrayList<Runnable> eventSwitchOnRun = new ArrayList<Runnable>();
	//ArrayList<Runnable> eventSwitchOffRun = new ArrayList<Runnable>();
	//ArrayList<Runnable> eventFieldChangeRun = new ArrayList<Runnable>();
	//		triggerMode = w.triggerMode; switchMode = w.switchMode;
	//		grabbable = w.grabbable; constrainX = w.constrainX; constrainY = w.constrainY;
	//		isSelectable = w.isSelectable; isField = w.isField; 
	//		showCursor = w.showCursor; hoverOutline = w.hoverOutline; showOutline = w.showOutline;
	//		alignX = w.alignX; stackX = w.stackX; alignY = w.alignY; stackY = w.stackY; centerX = w.centerX; centerY = w.centerY;
	//		placeLeft = w.placeLeft; placeRight = w.placeRight; placeUp = w.placeUp; placeDown = w.placeDown;
	//		hide = w.hide; drawerHideState = w.drawerHideState; hoverHideState = w.hoverHideState;
	//		constantOutlineWeight = w.constantOutlineWeight;
	//		    textAlignX = w.textAlignX; textAlignY = w.textAlignY; 
	//		show_text = w.show_text;
	//		shapeRound = w.shapeRound; shapeLosange = w.shapeLosange; 
	//		showInfo = w.showInfo; 
	//		    infoText = RConst.str_copy(w.infoText);
	//		auto_line_return = w.auto_line_return; set_line_length = w.set_line_length;
	//		constrainDlength = w.constrainDlength; constrainD = w.constrainD;
	//		fine_view = w.fine_view; always_view = w.always_view;
	//		    look.copy(w.look);
	//		    setLayer(w.layer);
	//		    setPosition(w.localrect.x, w.localrect.y);
	//		    setSize(w.localrect.size.x, w.localrect.size.y);
	//		changePosition();
	//		    if (hover != null && w.hover != null) hover.active = w.hover.active;
	//		    if (w.parent != null) setParent(w.parent);
	//		    if (hover != null && (isSelectable || grabbable || triggerMode || switchMode) && !hide) hover.active = true;
	//		return this;
	//	}








	//pos size getter / setter
	
//	private void changePosition() { 
	//	    globalrect.pos.x = getX(); 
	//	    globalrect.pos.y = getY(); 
	//	    globalrect.size.x = getSX(); 
	//	    globalrect.size.y = getSY(); 
	//	    phantomrect.pos.x = getX(); 
	//	    phantomrect.pos.y = getY(); 
	//	    phantomrect.size.x = getLocalSX(); 
	//	    phantomrect.size.y = getLocalSY(); 
	//		nRunnable.runEvents(eventPositionChange); 
	//		for (nWidget w : childs) w.changePosition(); 
	//	}


	//	public nWidget setPosition(float x, float y) { setPX(x); setPY(y); return this; }
	//	public nWidget setPosition(double x, double y) { setPX((float)x); setPY((float)y); return this; }
	//	public nWidget setPosition(Vector2 p) { setPX(p.x); setPY(p.y); return this; }
	//	public nWidget setSize(float x, float y) { setSX(x); setSY(y); return this; }
	//	public nWidget setSize(double x, double y) { setSX((float)x); setSY((float)y); return this; }
	//
	//	public nWidget setPX(double v) { return setPX((float)v); }
	//	public nWidget setPY(double v) { return setPY((float)v); }
	//	public nWidget setSX(double v) { return setSX((float)v); }
	//	public nWidget setSY(double v) { return setSY((float)v); }

	//  public nWidget setPX(float v) { 
	//	    if (v != localrect.x) { localrect.x = v; changePosition(); return this; } return this; }
	//  public nWidget setPY(float v) { 
	//	    if (v != localrect.y) { localrect.y = v; changePosition(); return this; } return this; }
	//  public nWidget setSX(float v) { 
	//	    if (v != localrect.size.x) { 
	//	    		localrect.size.x = v; 
	//	    		globalrect.size.x = getSX(); 
	//	    		if (stackX && placeLeft) globalrect.pos.x = getX(); 
	//			changePosition();
	//			for (nWidget w : childs) 
	//				if (((w.stackX || w.alignX) && w.placeRight) || ((stackX || alignX) && placeLeft)) w.changePosition(); 
	//	      	nRunnable.runEvents(eventShapeChange); 
	//	      	return this; 
	//	    } 
	//	    return this; 
	//  }
	//  public nWidget setSY(float v) { 
	//	    if (v != localrect.size.y) { 
	//	    		localrect.size.y = v; 
	//	    		globalrect.size.y = getSY(); 
	//	    		if (stackY && placeUp) globalrect.pos.y = getY(); 
	//	    		changePosition();
	//	    		for (nWidget w : childs) 
	//	    			if (((w.stackY || w.alignY) && w.placeDown) || ((stackY || alignY) && placeUp)) w.changePosition(); 
	//	    		nRunnable.runEvents(eventShapeChange); 
	//	    		return this; 
	//	    } 
	//	    return this; 
	//  }
	//  public float getX() { 
	//    if (parent != null) {
	//      if (alignX) {
	//        if (placeRight) return parent.getX() + parent.getSX() + localrect.x - getSX();
	//        else if (placeLeft) return parent.getX() + localrect.x;
	//      } else if (stackX) {
	//        if (placeRight) return parent.getX() + parent.getSX() + localrect.x;
	//        else if (placeLeft) return parent.getX() + localrect.x - getSX();
	//      } else if (centerX) return parent.getX() + localrect.x - getSX()/2;
	//      else return localrect.x + parent.getX();
	//    } 
	//    if (alignX) {
	//      if (placeRight) return localrect.x - getSX();
	//      else if (placeLeft) return localrect.x;
	//    } else if (stackX) {
	//      if (placeRight) return localrect.x;
	//      else if (placeLeft) return localrect.x - getSX();
	//    } else if (centerX) return localrect.x - getSX()/2;
	//    return localrect.x;
	//  }
	//  public float getY() { 
	//    if (parent != null) {
	//      if (alignY) {
	//        if (placeDown) return parent.getY() + parent.getSY() + localrect.y - getSY();
	//        else if (placeUp) return parent.getY() + localrect.y;
	//      } else if (stackY) {
	//        if (placeDown) return parent.getY() + parent.getSY() + localrect.y;
	//        else if (placeUp) return parent.getY() + localrect.y - getSY();
	//      } else if (centerY) return parent.getY() + localrect.y - getSY()/2;
	//      else return localrect.y + parent.getY();
	//    } 
	//    if (alignY) {
	//      if (placeDown) return localrect.y - getSY();
	//      else if (placeUp) return localrect.y;
	//    } else if (stackY) {
	//      if (placeDown) return localrect.y;
	//      else if (placeUp) return localrect.y - getSY();
	//    } else if (centerY) return localrect.y - getSY()/2;
	//    return localrect.y;
	//  }
	//  public float getLocalX() { return localrect.x; }
	//  public float getLocalY() { return localrect.y; }
	//  public float getSX() { if (!hide) return localrect.size.x; else return 0; }
	//  public float getSY() { if (!hide) return localrect.size.y; else return 0; }
	//  public float getLocalSX() { return localrect.size.x; }
	//  public float getLocalSY() { return localrect.size.y; }
	//  





	
	
	
	
	
	
	//  Other



	//	private String identity = ""; 
	//	private boolean identity_flag = false; 
	//	public  nWidget setIdentity(String s) { identity = s; return this; }
	//	public  nWidget setIdentityFlag(boolean s) { identity_flag = s; return this; }
	//	public  String getIdentity() { boolean[] last = new boolean[0]; return getIdentity(0, last); }
	//	public  String getIdentity(int rec_count, boolean[] last) { 
	//		String id = this + " widgetid:" + identity + " childs:";
	//		if (identity_flag) {
	//			for (int i = 0 ; i < 10 - rec_count ; i++) id += "    ";
	//			id += ">>> FLAG <<<";
	//		}
	//		id += " "+'\n';
	//		for (nWidget w : childs) {
	//			for (int i = 0 ; i < rec_count - 1 ; i++) 
	//				if (last[i]) id += "    "; else id += "  | ";
	//			boolean[] last2 = new boolean[rec_count+1];
	//			for (int i = 0 ; i < rec_count ; i++) last2[i] = last[i];
	//			last2[rec_count] = childs.get(childs.size() - 1) == w;
	//			id += "  |_" + w.getIdentity(rec_count+1, last2);
	//			last2[rec_count] = false;
	//		}
	//		return id; 
	//	}
	//	  
	////  public  nWidget setDrawer(nDrawer d) { ndrawer = d; return this; }
	////  public  nDrawer getDrawer() { return ndrawer; }
	////  public  nShelf getShelf() { return ndrawer.shelf; }
	////  public  nShelfPanel getShelfPanel() { return ndrawer.shelf.shelfPanel; }
	//
	//  public  nWidget addEventTopLayer(nRunnable r)      { eventTopLayer.add(r); return this; }
	//  public  nWidget removeEventTopLayer(nRunnable r)      { eventTopLayer.remove(r); return this; }
	//  
	//  public  nWidget addEventPositionChange(nRunnable r)   { eventPositionChange.add(r); return this; }
	//  public  nWidget addEventShapeChange(nRunnable r)      { eventShapeChange.add(r); return this; }
	//  public  nWidget addEventLayerChange(nRunnable r)      { eventLayerChange.add(r); return this; }
	//  public  nWidget addEventVisibilityChange(nRunnable r) { eventVisibilityChange.add(r); return this; }
	//  
	//  public nWidget addEventClear(nRunnable r)      { eventClear.add(r); return this; }
	//  
	//  public nWidget addEventFrame(nRunnable r)      { eventFrameRun.add(r); return this; }
	//  public nWidget addEventFrame_Builder(nRunnable r) { eventFrameRun.add(r); r.builder = this; return this; }
	// 
	//  public nWidget setDrawable(Drawable d) { 
	//    gui.drawing_pile.drawables.remove(drawer); 
	//    drawer.clear();
	//    drawer = d; 
	//    if (drawer != null) {
	//      drawer.setLayer(layer); 
	////      gui.drawing_pile.drawables.add(d); 
	//    }
	//    return this; 
	//  }
	//
	//  public Drawable getDrawable() { 
	//    return drawer; 
	//  }
	//  public nWidget setLayer(int l) { 
	//    layer = l; 
	//    if (drawer != null) drawer.setLayer(layer); 
	//    if (hover != null) hover.setLayer(layer); 
	//    nRunnable.runEvents(eventLayerChange); 
	//    return this; 
	//  }
	//
	//  
	//  public nWidget toLayerTop() {
	//    if (drawer != null) drawer.toLayerTop();
	//    if (hover != null) hover.toLayerTop();
	//    nRunnable.runEvents(eventTopLayer); 
	//    return this;
	//  }

	//
	//  public nWidget setFineView(boolean d) { fine_view = d; return this; }
	//  
	//  
	//  
	//  public nWidget setInfo(String s) { if (s != null && s.length() > 0) { infoText = s; showInfo = true; } return this; }
	//  public nWidget setNoInfo() { showInfo = false; return this; }
	//  
	//  public nWidget setLook(nLook l) { look.copy(l); return this; }
	//  public nWidget setLook(nTheme t, String r) { look.copy(t.getLook(r)); return this; }
	//  public nWidget setLook(String r) { look.copy(gui.theme.getLook(r)); return this; }

	//  public nWidget tempPassif(boolean b) { 
	//    if (hover != null && b != temp_passif) {
	//    		if (!b) hover.active = tempPassifPreState; 
	//    		else { tempPassifPreState = hover.active; hover.active = false; }
	//    }
	//	temp_passif = b;
	//    return this;
	//  }
	//  
	//  boolean totalhide = false;
	//  public nWidget totalhide() { 
	//	  totalhide = true;
	//    return this; 
	//  }
	//  
	//  public nWidget hide() { 
	//    if (!hide) {
	//      hide = true; 
	//      changePosition(); 
	//      if (drawer != null) { drawerHideState = drawer.get_view(); drawer.hide(); }
	//      if (hover != null) { hoverHideState = hover.active; hover.active = false; }
	//      nRunnable.runEvents(eventVisibilityChange); 
	//      for (nWidget w : childs) w.hide(); 
	//    }
	//    return this; 
	//  }
	//  public nWidget show() { 
	//    if (hide) {
	//      hide = false; 
	//      changePosition(); 
	//      if (drawer != null) drawer.set_view(drawerHideState); 
	//      if (hover != null) hover.active = hoverHideState; 
	//      nRunnable.runEvents(eventVisibilityChange); 
	//      for (nWidget w : childs) w.show(); 
	//    }
	//    return this; 
	//  }
	//  public nWidget show_childs() { 
	//	    for (nWidget w : childs) w.show(); 
	//	    return this; 
	//	  }
	//  public nWidget hide_childs() { 
	//	    for (nWidget w : childs) w.hide(); 
	//	    return this; 
	//	  }



	//  public nGUI getGUI() { return gui; }
	//  public Rect getRect() { return globalrect; }
	//  public Rect getPhantomRect() { return phantomrect; } //rect exist enven when hided ; for hided collisions
	//  public int getLayer() { return layer; }
	//  public String getText() { return label.substring(0, label.length()); }
	//  
	//  
	//  public nWidget setAlwaysView(boolean b) { 
	//	  always_view = b;
	//	  return this; }

//	public boolean isHided() { return hide; }
	//  public nWidget setHoverablePhantomSpace(float f) { if (hover != null) hover.phantom_space = f; return this; }


	//carefull!! dont work if excluded cleared before this
	//  private ArrayList<nWidget> excludes = new ArrayList<nWidget>();
	//  public nWidget addExclude(nWidget b) { excludes.add(b); return this; }
	//  public nWidget removeExclude(nWidget b) { excludes.remove(b); return this; }



	//  public nWidget setRound(boolean c) { shapeRound = c; return this; }
	//  public nWidget setLosange(boolean c) { shapeLosange = c; return this; }
	//  
	//  public nWidget setStandbyColor(int c) { look.standbyColor = c; return this; }
	//  public nWidget setHoveredColor(int c) { look.hoveredColor = c; return this; }
	//  public nWidget setClickedColor(int c) { look.pressColor = c; return this; }
	//  public nWidget setLabelColor(int c)   { look.textColor = c; return this; }
	//  public nWidget setOutlineColor(int c) { look.outlineColor = c; return this; }
	//  public nWidget setOutlineSelectedColor(int c) { look.outlineSelectedColor = c; return this; }
	//  
	//  public nWidget alignUp()    { alignY = true;  stackY = false; placeUp   = true;  placeDown = false;  centerY = false; changePosition(); return this; }
	//  public nWidget alignDown()  { alignY = true;  stackY = false; placeUp   = false; placeDown = true;   centerY = false; changePosition(); return this; }
	//  public nWidget alignLeft()  { alignX = true;  stackX = false; placeLeft = true;  placeRight = false; centerY = false; changePosition(); return this; }
	//  public nWidget alignRight() { alignX = true;  stackX = false; placeLeft = false; placeRight = true;  centerY = false; changePosition(); return this; }
	//  public nWidget stackUp()    { alignY = false; stackY = true;  placeUp   = true;  placeDown = false;  centerX = false; changePosition(); return this; }
	//  public nWidget stackDown()  { alignY = false; stackY = true;  placeUp   = false; placeDown = true;   centerX = false; changePosition(); return this; }
	//  public nWidget stackLeft()  { alignX = false; stackX = true;  placeLeft = true;  placeRight = false; centerX = false; changePosition(); return this; }
	//  public nWidget stackRight() { alignX = false; stackX = true;  placeLeft = false; placeRight = true;  centerX = false; changePosition(); return this; }
	//  public nWidget centerX()    { alignX = false; stackX = false; placeLeft = false; placeRight = false; centerX = true;  changePosition(); return this; }
	//  public nWidget centerY()    { alignY = false; stackY = false; placeUp   = false; placeDown  = false; centerY = true;  changePosition(); return this; }
	//  public nWidget center()     { centerX(); centerY(); return this; }
	//  


	//  public boolean isViewable() {
	//	  return !totalhide &&( always_view || (Rect.rectCollide(getRect(), gui.view) && 
	//    		  !(fine_view && getSX()*gui.scale < gui.min_fineview_size && 
	//    				  getSY()*gui.scale < gui.min_fineview_size ) && 
	//    		  !(!fine_view && getSX()*gui.scale < gui.min_view_size && 
	//    				  getSY()*gui.scale < gui.min_view_size)) );
	//  }

	
}

