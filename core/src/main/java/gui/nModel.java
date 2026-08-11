package gui;

import java.util.HashMap;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;

import util.Utl;
import app.App;
import app.nLauncher;

public class nModel extends nLauncher {
	
	public nWidget custom_init(nWidget w) { return w; }
	
//	public App app;

	public nModel() {
		super();
//		app = a;
		
		init_default();
	}

	public nModel(App a) {
		super();
//		app = a;
		
		init_default();
	}

//	public nModel(Applet a) {
//		super();
////		app = a;
//		
//		init_default();
//	}
	
	public nWidget asWidget() { return (nWidget)this; }
	
	

	protected boolean helper = false;
	protected String helper_ref = "";
	

	protected boolean vfx = false;

	//rect in personal space
	protected Vector2 setPos = new Vector2();
	protected Vector2 setSize = new Vector2();

	protected boolean do_draw = true;

	protected boolean visible = true;

	protected boolean scale_limit = false;
	protected boolean scale_limit_nodraw = false;
	protected float scale_min = 0, scale_max = 0;

	protected boolean drawstackPriority = false;
	
	protected boolean showOrigin = false;

	protected boolean boundChild = false; //resize to bound around children with boundParent
	protected boolean boundParent = false; //influence parent size
	protected float boundOutspace = 10; 
	protected boolean isStacked = false; 
	protected nAlign stackAxis = nAlign.VERTICAL; 
	protected nAlign stackDirection = nAlign.UP; 
	protected nAlign stackAlign = nAlign.CENTER; 
	protected nAlign rectOriginX = nAlign.LEFT; 
	protected nAlign rectOriginY = nAlign.BOTTOM; 
	protected float stackSpacing = 10;

	protected nAlign glue_side = nAlign.RIGHT; 
	protected float glue_space = 0;
	protected nAlign glue_align = nAlign.CENTER; 

	public boolean isHovered = false;
	public boolean isClicked = false;
	public boolean isRightClicked = false;
	protected boolean switchState = false;
	protected boolean isGrabbed = false;
	public boolean isSelected = false;
	
	protected boolean triggerMode = false, switchMode = false;
	protected boolean triggerRightMode = false;
	
	protected boolean hoverable = false; //active hovering test by nGUI 

	protected boolean hoverable_zone = false;
	protected boolean stop_hover_child_zone = false;
	
	public boolean isGrabbable() { return grabbable; }
	protected boolean grabbable = false, constrainX = false, constrainY = false, constrainD = false;
	protected float constrainDlength = 0;
	protected boolean isSelectable = false, isField = false;
	protected boolean grabb_root = false;
	protected boolean act_as_root = false;

	protected boolean isSlider = false;
	protected float sliderFact = 0.1f;
	protected nAlign sliderAxis = nAlign.HORIZONTAL;
	protected float sliderMin = 0f, sliderMax = 1f;
	protected float slider_granulo = 0;
	protected boolean slider_log = false;
	
	protected String text = "";
	protected boolean show_text = true, showCursor = false;
	protected int cursorPos = 0;
	
	protected nAlign textAlignX = nAlign.CENTER, textAlignY = nAlign.CENTER;
	protected boolean auto_line_return = false;
	protected int set_line_length = 0;
	protected float textFont = 10f;
	
	public boolean showOutline = false;
	protected boolean hoverOutline = false;
	protected boolean constantOutlineWeight = false;
	protected boolean outlineAfterChild = false;
	
	public float outlineWeight = 1;
	
	public  Color color_background, color_pressed, color_hovered, 
		color_standby, color_sliderback, color_outline, color_outline_selected, 
		color_shadow, color_switch_on, color_switch_off, color_text;

	protected boolean maskChildren = false;

	protected boolean backgroundRender = false;
	

	protected boolean warpChildren = false;
	protected Vector2 warpTranslate = new Vector2();
	protected float warpScale = 1.0f;
	protected float warpRot = 0.0f;
	
	
	public enum Shape { RECT, CIRCLE, DIAMOND }
	
	protected Shape shape = Shape.RECT;
	
	protected boolean has_shadow = false, press_reduce_shadow = true;
	protected float shadow_thick = 0.0f;
	
	protected boolean has_info = false;
	public String info_txt = "";
	
	protected int float_rez = 2;
	
//	private boolean alignX = false, stackX = false, alignY = false, stackY = false;
//	private boolean centerX = false, centerY = false;
//	private boolean placeLeft = false, placeRight = false, placeUp = false, placeDown = false;

	
	public void init_default() {
		helper = false;
		helper_ref = "";
		
		vfx = false;
		backgroundRender = false;
		
		do_draw = true;
		visible = true; showOrigin = false;
		drawstackPriority = false;
		
		setPos.set(0,0);
		setSize.set(0,0);

		scale_min = 0; scale_max = 0; scale_limit = false; scale_limit_nodraw = false;
		
		boundChild = false; boundParent = false; isStacked = false; 
		boundOutspace = 10; stackSpacing = 10;
		stackAxis = nAlign.VERTICAL; stackDirection = nAlign.UP; 
		stackAlign = nAlign.CENTER;
		rectOriginX = nAlign.LEFT; rectOriginY = nAlign.BOTTOM; 
		glue_side = nAlign.RIGHT; 
		glue_space = 0;
		glue_align = nAlign.CENTER; 
		triggerMode = false; switchMode = false; hoverable = false; 
		triggerRightMode = false;
		hoverable_zone = false;
		stop_hover_child_zone = false;
		grabbable = false; constrainX = false; constrainY = false; constrainD = false;
		constrainDlength = 0;
		isHovered = false; isClicked = false; isRightClicked = false;
		grabb_root = false;
		act_as_root = false;
		switchState = false; isGrabbed = false; isSelected = false;
		isSelectable = false; isField = false;
		isSlider = false; sliderAxis = nAlign.HORIZONTAL; 
		sliderFact = 0.1f;
		sliderMin = 0f; sliderMax = 1f;
		slider_granulo = 0f;
		slider_log = false;
		text = ""; show_text = true; showCursor = false;
		
		textAlignX = nAlign.CENTER; textAlignY = nAlign.CENTER;
		auto_line_return = false;
		set_line_length = 0; textFont = 16f; 
		showOutline = false; hoverOutline = false;
		constantOutlineWeight = false; 
		outlineAfterChild = false;
		outlineWeight = 1;
		cursorPos = 0; 
		
		color_background = getColor(Utl.color(80,80,80,255));
		color_pressed = getColor(Utl.color(20,20,255,255));
		color_hovered = getColor(Utl.color(0,0,210,255));
		color_standby = getColor(Utl.color(0,0,120,255));
		color_sliderback = getColor(Utl.color(50,50,50,255));
		color_outline = getColor(Utl.color(200,200,200,255));
		color_outline_selected = getColor(Utl.color(200,200,0,255));
		color_shadow = getColor(Utl.color(0,0,0,100));
		color_switch_on = getColor(Utl.color(0,70,255,255));
		color_switch_off = getColor(Utl.color(0,0,40,255));
		color_text = getColor(Utl.color(240));
		
		maskChildren = false;
		
		warpChildren = false;
		warpTranslate.set(0,0);
		warpScale = 1.0f;
		warpRot = 0.0f;
		
		shape = Shape.RECT;
		
		has_shadow = false; press_reduce_shadow = true;
		shadow_thick = 4.0f;
		
		has_info = false;
		info_txt = "";
		
		float_rez = 2;
	}
	
	public nModel copyFrom(nModel m) {
		helper = m.helper;
		helper_ref = m.helper_ref;
		
		vfx = m.vfx;
		backgroundRender = m.backgroundRender;
		do_draw = m.do_draw;
		visible = m.visible; 
		showOrigin = m.showOrigin;
		drawstackPriority = m.drawstackPriority;
		
		setPos.set(m.setPos);
		setSize.set(m.setSize);
		
		scale_min = m.scale_min; scale_max = m.scale_max; scale_limit = m.scale_limit;
		scale_limit_nodraw = m.scale_limit_nodraw;
		
		boundChild = m.boundChild; boundParent = m.boundParent; isStacked = m.isStacked; 
		boundOutspace = m.boundOutspace; stackSpacing = m.stackSpacing;
		stackAxis = m.stackAxis; stackDirection = m.stackDirection; 
		stackAlign = m.stackAlign;
		rectOriginX = m.rectOriginX; rectOriginY = m.rectOriginY; 
		glue_side = m.glue_side; glue_space = m.glue_space;
		glue_align = m.glue_align;
		triggerMode = m.triggerMode; switchMode = m.switchMode; hoverable = m.hoverable;
		triggerRightMode = m.triggerRightMode;
		hoverable_zone = m.hoverable_zone;
		stop_hover_child_zone = m.stop_hover_child_zone;
		grabbable = m.grabbable; constrainX = m.constrainX; constrainY = m.constrainY; constrainD = m.constrainD;
		constrainDlength = m.constrainDlength;
		isHovered = m.isHovered; isClicked = m.isClicked;
		grabb_root = m.grabb_root;
		act_as_root = m.act_as_root;
		switchState = m.switchState; isGrabbed = m.isGrabbed; isSelected = m.isSelected;
		isSelectable = m.isSelectable; isField = m.isField;
		isSlider = m.isSlider; sliderAxis = m.sliderAxis; 
		sliderFact = m.sliderFact; 
		sliderMin = m.sliderMin; sliderMax = m.sliderMax;
		slider_granulo = m.slider_granulo;
		slider_log = m.slider_log;
		text = Utl.copy(m.text); show_text = m.show_text; showCursor = m.showCursor;
		
		textAlignX = m.textAlignX; textAlignY = m.textAlignY;
		auto_line_return = m.auto_line_return;
		set_line_length = m.set_line_length; textFont = m.textFont; 
		showOutline = m.showOutline; hoverOutline = m.hoverOutline;
		constantOutlineWeight = m.constantOutlineWeight; 
		outlineAfterChild = m.outlineAfterChild;
		outlineWeight = m.outlineWeight;
		cursorPos = m.cursorPos; 
		
		color_background = getColor(m.color_background);
		color_pressed = getColor(m.color_pressed);
		color_hovered = getColor(m.color_hovered);
		color_standby = getColor(m.color_standby);
		color_sliderback = getColor(m.color_sliderback);
		color_outline = getColor(m.color_outline);
		color_outline_selected = getColor(m.color_outline_selected);
		color_shadow = getColor(m.color_shadow);
		color_switch_on = getColor(m.color_switch_on);
		color_switch_off = getColor(m.color_switch_off);
		color_text = getColor(m.color_text);
		
		maskChildren = m.maskChildren;
		
		warpChildren = m.warpChildren;
		warpTranslate.set(m.warpTranslate);
		warpScale = m.warpScale;
		warpRot = m.warpRot;
		
		shape = m.shape;
		
		has_shadow = m.has_shadow; press_reduce_shadow = m.press_reduce_shadow;
		shadow_thick = m.shadow_thick;

		has_info = m.has_info;
		info_txt = Utl.copy(m.info_txt);
		
		float_rez = m.float_rez;
		
		return this;
	}
	
	public nModel copySizeFrom(nModel m) {
		setSize.set(m.setSize);
		return this;
	}

	public nModel copyColorFrom(nModel m) {
		color_background = getColor(m.color_background);
		color_pressed = getColor(m.color_pressed);
		color_hovered = getColor(m.color_hovered);
		color_standby = getColor(m.color_standby);
		color_sliderback = getColor(m.color_sliderback);
		color_outline = getColor(m.color_outline);
		color_outline_selected = getColor(m.color_outline_selected);
		color_shadow = getColor(m.color_shadow);
		color_switch_on = getColor(m.color_switch_on);
		color_switch_off = getColor(m.color_switch_off);
		color_text = getColor(m.color_text);
		return this;
	}
	
	public nModel copyLookFrom(nModel m) {
		color_background = getColor(m.color_background);
		color_pressed = getColor(m.color_pressed);
		color_hovered = getColor(m.color_hovered);
		color_standby = getColor(m.color_standby);
		color_sliderback = getColor(m.color_sliderback);
		color_outline = getColor(m.color_outline);
		color_outline_selected = getColor(m.color_outline_selected);
		color_shadow = getColor(m.color_shadow);
		color_switch_on = getColor(m.color_switch_on);
		color_switch_off = getColor(m.color_switch_off);
		color_text = getColor(m.color_text);
		
		showOutline = m.showOutline; hoverOutline = m.hoverOutline;
		constantOutlineWeight = m.constantOutlineWeight; 
		outlineAfterChild = m.outlineAfterChild;
		outlineWeight = m.outlineWeight;
		
		shape = m.shape;

		has_shadow = m.has_shadow; press_reduce_shadow = m.press_reduce_shadow;
		shadow_thick = m.shadow_thick;
		
		return this;
	}
	
	public nModel copyFunctionFrom(nModel m) {
		helper = m.helper;
		helper_ref = m.helper_ref;

		vfx = m.vfx;
		backgroundRender = m.backgroundRender;
		
		do_draw = m.do_draw;
		visible = m.visible; 
		showOrigin = m.showOrigin;
		drawstackPriority = m.drawstackPriority;
		
		setPos.set(m.setPos);

		scale_min = m.scale_min; scale_max = m.scale_max; scale_limit = m.scale_limit;
		scale_limit_nodraw = m.scale_limit_nodraw;
		
		boundChild = m.boundChild; boundParent = m.boundParent; isStacked = m.isStacked; 
		boundOutspace = m.boundOutspace; stackSpacing = m.stackSpacing;
		stackAxis = m.stackAxis; stackDirection = m.stackDirection; 
		stackAlign = m.stackAlign;
		rectOriginX = m.rectOriginX; rectOriginY = m.rectOriginY; 
		glue_side = m.glue_side; glue_space = m.glue_space;
		glue_align = m.glue_align;
		triggerMode = m.triggerMode; switchMode = m.switchMode; hoverable = m.hoverable; 
		triggerRightMode = m.triggerRightMode;
		hoverable_zone = m.hoverable_zone;
		stop_hover_child_zone = m.stop_hover_child_zone;
		grabbable = m.grabbable; constrainX = m.constrainX; constrainY = m.constrainY; constrainD = m.constrainD;
		constrainDlength = m.constrainDlength;
		isHovered = m.isHovered; isClicked = m.isClicked;
		grabb_root = m.grabb_root;
		act_as_root = m.act_as_root;
		switchState = m.switchState; isGrabbed = m.isGrabbed; isSelected = m.isSelected;
		isSelectable = m.isSelectable; isField = m.isField;
		isSlider = m.isSlider; sliderAxis = m.sliderAxis; 
		sliderFact = m.sliderFact;
		sliderMin = m.sliderMin; sliderMax = m.sliderMax;
		slider_granulo = m.slider_granulo;
		slider_log = m.slider_log;
		text = Utl.copy(m.text); show_text = m.show_text; showCursor = m.showCursor;
		
		textAlignX = m.textAlignX; textAlignY = m.textAlignY;
		auto_line_return = m.auto_line_return;
		set_line_length = m.set_line_length; textFont = m.textFont; 
		showOutline = m.showOutline; hoverOutline = m.hoverOutline;
		constantOutlineWeight = m.constantOutlineWeight; 
		outlineAfterChild = m.outlineAfterChild;
		outlineWeight = m.outlineWeight;
		cursorPos = m.cursorPos; 
		
		maskChildren = m.maskChildren;

		warpChildren = m.warpChildren;
		warpTranslate.set(m.warpTranslate);
		warpScale = m.warpScale;
		warpRot = m.warpRot;

		has_shadow = m.has_shadow; press_reduce_shadow = m.press_reduce_shadow;
		shadow_thick = m.shadow_thick;

		has_info = m.has_info;
		info_txt = Utl.copy(m.info_txt);

		float_rez = m.float_rez;
		
		return this;
	}

	public nModel setHelper(String b) { this.helper = true; this.helper_ref = b; return this; }
	public nModel setNoHelper() { this.helper = false; return this; }

	public nModel setVFX() { this.vfx = true; return this; }

	public nModel setBackgroundRender() { this.backgroundRender = true; return this; }
	
	public nModel setDraw(boolean b) { this.do_draw = b; return this; }
	
	public nModel setInfo(boolean b) { this.has_info = b; return this; }
	public nModel setInfoText(String b) { this.info_txt = b; return this; }
	public nModel setInfo(String b) { 
		if (b == null || b.length() == 0) return this; 
		this.has_info = true; this.info_txt = b; return this; }
	
	public nModel setFloatRez(int b) { float_rez = b; return this; }

	public nModel setGlueSide(nAlign b) { glue_side = b; return this; }
	public nModel setGlueSpace(float b) { glue_space = b; return this; }
	public nModel setGlueAlign(nAlign b) { glue_align = b; return this; }

	public Vector2 getLocalPos() { return Utl.copy(setPos); }
	public float getLocalX() { return setPos.x; }
	public float getLocalY() { return setPos.y; }
	public float getLocalSX() { return setSize.x; }
	public float getLocalSY() { return setSize.y; }
	
	public nModel setScaleLimit(float a, float b) { scale_limit = true; scale_min = a; scale_max = b; return this; }
	public nModel setScaleLimitNoDraw(float a, float b) { scale_limit = true; scale_limit_nodraw = true; scale_min = a; scale_max = b; return this; }
	
	public nModel setPX(float v) { 
		if (v != setPos.x) { setPos.x = v; positionChange(); return this; } return this; }
	public nModel setPY(float v) { 
		if (v != setPos.y) { setPos.y = v; positionChange(); return this; } return this; }
	public nModel setSX(float v) { 
		if (v != setSize.x) { setSize.x = v; sizeChange(); return this; } return this; }
	public nModel setSY(float v) { 
		if (v != setSize.y) { setSize.y = v; sizeChange(); return this; } return this; }
	
	public void sizeChange() { if (isSlider) calcSliderAxe(); }
	public void positionChange() {}
	
	public nModel setRect(float x, float y, float sx, float sy) { setPX(x); setPY(y); setSX(sx); setSY(sy); return this; }
	
	public nModel setPos(float x, float y) { setPosition(x,y); return this; }
	public nModel addPos(float x, float y) { setPosition(setPos.x + x,setPos.y + y); return this; }
	public nModel setPos(Vector2 v) { setPosition(v.x,v.y); return this; }
	public nModel setPosition(float x, float y) { setPX(x); setPY(y); return this; }
	public nModel setPosition(double x, double y) { setPX((float)x); setPY((float)y); return this; }
	public nModel setPosition(Vector2 p) { setPX(p.x); setPY(p.y); return this; }
	public nModel setSize(float x, float y) { setSX(x); setSY(y); return this; }
	public nModel setSize(double x, double y) { setSX((float)x); setSY((float)y); return this; }
	public nModel setPX(double v) { return setPX((float)v); }
	public nModel setPY(double v) { return setPY((float)v); }
	public nModel setSX(double v) { return setSX((float)v); }
	public nModel setSY(double v) { return setSY((float)v); }

	public nModel setShape(Shape b) { this.shape = b; return this; }

	public nModel setShadow(boolean b) { this.has_shadow = b; return this; }
	public nModel setShadowPressReduce(boolean b) { this.press_reduce_shadow = b; return this; }
	public nModel setShadowThick(float b) { this.shadow_thick = b; return this; }
	
	public nModel showOrigin(boolean b) { this.showOrigin = b; return this; }
	
	public nModel setBoundParent(boolean b) { this.boundParent = b; return this; }
	public nModel setBoundChild(boolean b) { this.boundChild = b; return this; }
	public nModel setBoundOutspace(float b) { this.boundOutspace = b; return this; }
	public nModel setStackSpacing(float b) { this.stackSpacing = b; return this; }
	public nModel setStacked(boolean i) { this.isStacked = i; return this; }
	public nModel setStack(nAlign a, nAlign d) { stackAxis = a; stackDirection = d; return this; }
	public nModel setStack(nAlign a, nAlign d, nAlign al) { stackAxis = a; stackDirection = d; stackAlign = al; return this; }
	public nModel setStackAxis(nAlign s) { stackAxis = s; return this; }
	public nModel setStackDirection(nAlign s) { stackDirection = s; return this; }
	public nModel setStackAlign(nAlign s) { stackAlign = s; return this; }
	
	
	public nModel setRectOrigin(nAlign sx, nAlign sy) { rectOriginX = sx; rectOriginY = sy; return this; }

	public nModel setVisibility(boolean s) { visible = s; return this; }
	public nModel switchVisibility() { setVisibility(!visible); return this; }
	public nModel show() { setVisibility(true); return this; }
	public nModel hide() { setVisibility(false); return this; }
	
	public nModel setDrawstackPriority(boolean s) { drawstackPriority = s; return this; }
	
	public nModel setWarp(boolean s) { warpChildren = s; return this; }
	public nModel setWarpTranslate(float x, float y) { warpTranslate.set(x,y); return this; }
	public nModel setWarpTranslate(Vector2 v) { warpTranslate.set(v); return this; }
	public nModel setWarpScale(float s) { warpScale = s; return this; }
	public nModel setWarpRot(float s) { warpRot = s; return this; }
	
	public nModel setMask(boolean s) { maskChildren = s; return this; }
	
	public nModel setHoverableZone(boolean s) { hoverable_zone = s; return this; }
	public nModel setStopHoverChildZone(boolean s) { stop_hover_child_zone = s; return this; }
	
	public nModel setPassif() { 
		triggerMode = false; 
		switchMode = false; 
		switchState = false; 
		grabbable = false; 
		isField = false;
		isClicked = false;
		hoverable = false;
		return this; }
	public nModel setBackground() { 
		triggerMode = false; 
		switchMode = false; 
		switchState = false; 
		grabbable = false; 
		isField = false; 
		isClicked = false;
		hoverable = true;
		return this; }
	public nModel setTrigger() { 
		triggerMode = true; switchMode = false; switchState = false; 
		hoverable = true; return this; }
	public nModel setRightTrigger() { 
		triggerRightMode = true; hoverable = true; return this; }
	public nModel setSwitch() { 
		triggerMode = false; switchMode = true; switchState = false; 
		hoverable = true; 
		has_shadow = true;
		return this; }

	public nModel setFont(float s) { textFont = s; return this; }
	public float getFont() { return textFont; }
	public nModel setTextAlignment(nAlign sx, nAlign sy) { textAlignX = sx; textAlignY = sy; return this; }
	public nModel setTextAlignmentX(nAlign sx) { textAlignX = sx; return this; }
	public nModel setTextAlignmentY(nAlign sy) { textAlignY = sy; return this; }
	public nModel setTextVisibility(boolean s) { show_text = s; return this; }
	public nModel setTextAutoReturn(boolean s) { auto_line_return = s; return this; }
	public nModel setTextLineLength(int s) { set_line_length = s; return this; }
	public nModel setTextLineNoLength() { set_line_length = 0; return this; }
	
	public nModel setHoverable() { triggerMode = false; grabbable = false; hoverable = true; /*isStacked = false;*/ return this; }
	
	public nModel setGrabbable() { triggerMode = true; grabbable = true; hoverable = true; isStacked = false; return this; }
	public nModel setGrabbRoot(boolean b) { grabb_root = b; return this; }
	public nModel setActAsRoot(boolean b) { act_as_root = b; return this; }
	public nModel setFixed() { grabbable = false; hoverable = false; return this; }
	public nModel setConstrainX(boolean b) { constrainX = b; return this; }
	public nModel setConstrainY(boolean b) { constrainY = b; return this; }
	public nModel setConstrainDistance(float b) { if (b == 0) constrainD = false; else { constrainDlength = b; constrainD = true; } return this; }
	public nModel setSelectable(boolean o) { isSelectable = o; hoverOutline = o; hoverable = true; return this; }
	public nModel setField(boolean o) { isField = o; setSelectable(o); return this; }

	public nModel setOutline(boolean o) { showOutline = o; return this; }
	public nModel setOutlineWeight(float l) { outlineWeight = l; return this; }
	public nModel setOutlineWeight(double l) { outlineWeight = (float)l; return this; }
	public nModel setOutlineConstant(boolean l) { constantOutlineWeight = l; return this; }
	public nModel setOutlineAfterChild(boolean l) { outlineAfterChild = l; return this; }
	
	public nModel setHoveredOutline(boolean o) { hoverOutline = o; return this; }

	public nModel setText(String s) { if (s != null) { text = s; if (cursorPos > text.length()) cursorPos = text.length(); } return this; }
	public nModel changeText(String s) { text = s; if (cursorPos > text.length()) cursorPos = text.length(); return this; }
	public String getText() { return text; }

	public boolean isGrabbed() { return isGrabbed; }
	public boolean isField() { return isField; }

	public nModel setSlider() { 
		triggerMode = true; isSlider = true; hoverable = true; 
		calcSliderAxe();
		return this; }
	public nModel setSliderCursorSize(float f) { sliderFact = f; return this; }
	public nModel setSliderMin(float f) { sliderMin = f; return this; }
	public nModel setSliderMax(float f) { sliderMax = f; return this; }
	public nModel setSliderGranulo(float f) { slider_granulo = f; return this; }
	public nModel setSliderLog(boolean f) { slider_log = f; return this; }
	public nModel setSliderRange(float min, float max) { sliderMin = min; sliderMax = max; return this; }
	public nModel calcSliderAxe() { 
		if (getLocalSX() > getLocalSY()) sliderAxis = nAlign.HORIZONTAL;
		else sliderAxis = nAlign.VERTICAL; 
		return this; }
	protected float slideValToMinmax(float v) { 
		if (v == 0.0f) return sliderMin;
		else if (v == 1.0f) return sliderMax;
		if (slider_log) {
			v = Utl.linear_to_log(v, 3);
		}
		float r = (sliderMax - sliderMin) * v;
		if (slider_granulo > 0) r -= r % slider_granulo; 
		return sliderMin + r; 
	}
	protected float slideMinmaxToVal(float v) { 
		v = (v - sliderMin) / (sliderMax - sliderMin);
		if (slider_log) {
			v = Utl.log_to_linear(v, 3);
		}
		return v; }
	
	public nModel set_color_background(Color o) { color_background = getColor(o); return this; }
	public nModel set_color_pressed(Color o) { color_pressed = getColor(o); return this; }
	public nModel set_color_hovered(Color o) { color_hovered = getColor(o); return this; }
	public nModel set_color_standby(Color o) { color_standby = getColor(o); return this; }
	public nModel set_color_sliderback(Color o) { color_sliderback = getColor(o); return this; }
	public nModel set_color_outline(Color o) { color_outline = getColor(o); return this; }
	public nModel set_color_outline_selected(Color o) { color_outline_selected = getColor(o); return this; }
	
	public nModel set_color_shadow(Color o) { color_shadow = getColor(o); return this; }
	public nModel set_color_switch_on(Color o) { color_switch_on = getColor(o); return this; }
	public nModel set_color_switch_off(Color o) { color_switch_off = getColor(o); return this; }
	public nModel set_color_text(Color o) { color_text = getColor(o); return this; }

	@Override
	public void build_lauchables() {
		// TODO Auto-generated method stub
		
	}
	
	private static HashMap<Integer,Color> colors = new HashMap<Integer,Color>();
	private Color getColor(Color c) {
		int id = Utl.rgbToInt((int)(255.0f*c.r), (int)(255.0f*c.g), 
				(int)(255.0f*c.b), (int)(255.0f*c.a));
		if (colors.get(id) != null) return colors.get(id);
		colors.put(id,c);
		return c;
	}
	
	

//	public nWidget alignUp()    { alignY = true;  stackY = false; placeUp   = true;  placeDown = false;  centerY = false; return this; }
//	public nWidget alignDown()  { alignY = true;  stackY = false; placeUp   = false; placeDown = true;   centerY = false; return this; }
//	public nWidget alignLeft()  { alignX = true;  stackX = false; placeLeft = true;  placeRight = false; centerY = false; return this; }
//	public nWidget alignRight() { alignX = true;  stackX = false; placeLeft = false; placeRight = true;  centerY = false; return this; }
//	public nWidget stackUp()    { alignY = false; stackY = true;  placeUp   = true;  placeDown = false;  centerX = false; return this; }
//	public nWidget stackDown()  { alignY = false; stackY = true;  placeUp   = false; placeDown = true;   centerX = false; return this; }
//	public nWidget stackLeft()  { alignX = false; stackX = true;  placeLeft = true;  placeRight = false; centerX = false; return this; }
//	public nWidget stackRight() { alignX = false; stackX = true;  placeLeft = false; placeRight = true;  centerX = false; return this; }
//	public nWidget centerX()    { alignX = false; stackX = false; placeLeft = false; placeRight = false; centerX = true;  return this; }
//	public nWidget centerY()    { alignY = false; stackY = false; placeUp   = false; placeDown  = false; centerY = true;  return this; }
//	public nWidget center()     { centerX(); centerY(); return this; }
	
}
