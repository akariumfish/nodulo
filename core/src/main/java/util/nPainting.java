package util;

import com.badlogic.gdx.math.Vector2;

import app.GdxApp;
import app.nDrawer;

public class nPainting extends nScripted {
	
	public static void register() {
		addScriptedMetode(nPainting.class, "pop");
		addScriptedMetode(nPainting.class, "push");
		addScriptedMetode(nPainting.class, "_trans", Vector2.class);
		addScriptedMetode(nPainting.class, "_rot", Float.class);
		addScriptedMetode(nPainting.class, "_scale", Float.class);
		addScriptedMetode(nPainting.class, "_trig", Vector2.class, Vector2.class, Vector2.class);
		addScriptedMetode(nPainting.class, "_rect", Vector2.class, Vector2.class);
		addScriptedMetode(nPainting.class, "_line", Vector2.class, Vector2.class);
		addScriptedMetode(nPainting.class, "_point", Vector2.class);
		addScriptedMetode(nPainting.class, "_circle", Vector2.class, Float.class);
		addScriptedMetode(nPainting.class, "noFill");
		addScriptedMetode(nPainting.class, "noStroke");
		addScriptedMetode(nPainting.class, "_fill", 
				Integer.class, Integer.class, Integer.class, Integer.class);
		addScriptedMetode(nPainting.class, "_stroke", 
				Integer.class, Integer.class, Integer.class, Integer.class, Float.class);
		addScriptedMetode(nPainting.class, "_text", String.class, Vector2.class, Float.class);
	}

	public nPainting() { }
	
	
//	public class nPicto {
//		public String ref;
//		public Object[] script;
//		public nPicto(String r, Object[] s) { ref = r; script = s; }
//		public nPicto(Object[] s) { script = s; }
//	}
//	
//	public nPicto getPicto() { return new nPicto(super.getScript()); }
//	public nPicto getPicto(String r) { return new nPicto(r,super.getScript()); }
	
	public Object[] getScript() { return super.getScript(); }
	
	public void buildScript(Object[] scr) { nScripted.buildScript(this, scr); }
	
	
	public void draw(nDrawer drawer) { 
		drawer.push(); 
		drawer.translate(paint_ref); 
		drawer.scale(paint_scale);
		drawer.rotate(paint_rot);
		drawer.noFill(); drawer.stroke(255,5f);
		for (Elem d : elements.all()) d.draw(drawer);
		drawer.pop();
	}

	
	public void clear() { super.clear(); }
	

	public void transform(Vector2 v, float s, float r) { 
		paint_ref.set(v); paint_scale = s; paint_rot = r; }
	public void untransform() { 
		paint_ref.set(0,0); paint_scale = 1f; paint_rot = 0f; }

	

	public void pop() { com(); elements.obtain().init_pop(); }
	public void push() { com(); elements.obtain().init_push(); }

	public void transf(Vector2 v1) { _trans(v1); }
	public void scale(float f1) { _scale(f1); }
	public void rot(float f1) { _rot(f1); }
	
	public void rect(float x, float y, float w, float h) { 
		_rect(new Vector2(x,y),new Vector2(w,h)); }
	public void rect(Vector2 v1, Vector2 v2) { _rect(v1,v2); }
	
	public void line(float x1, float y1, float x2, float y2) { 
		_line(new Vector2(x1,y1),new Vector2(x2,y2)); }
	public void line(Vector2 v1, Vector2 v2) { _line(v1,v2); }
	
	public void point(float x, float y) { _point(new Vector2(x,y)); }
	public void point(Vector2 v1) { _point(v1); }
	
	public void text(String t, Vector2 p, float f) { _text(t,p,f); }
	public void text(String t, float x, float y, float f) { _text(t,new Vector2(x,y),f); }

	public void trig(Vector2 v1, Vector2 v2, Vector2 v3) { _trig(v1,v2,v3); }
	public void circle(Vector2 v1, float f1) { _circle(v1,f1); }
	
	public void fill(int _l) { 
		_fill(_l,_l,_l,(int)255); }
	public void fill(int _l, int _a) { 
		_fill(_l,_l,_l,_a); }
	public void fill(int _r, int _g, int _b, int _a) { 
		_fill(_r,_g,_b,_a); }
	
	public void stroke(int _l, float _w) { 
		_stroke(_l,_l,_l,(int)255,_w); }
	public void stroke(int _l, int _a, float _w) { 
		_stroke(_l,_l,_l,_a,_w); }
	public void stroke(int _r, int _g, int _b, int _a, float _w) { 
		_stroke(_r,_g,_b,_a,_w); }

	public void noFill() { com(); elements.obtain().init_nofill(); }
	public void noStroke() { com(); elements.obtain().init_nostrok(); }

	
	
	
	
	
	
	
	

	protected void empty() { elements.freeAll(); }

	protected void _trans(Vector2 v1) { com(v1); 
		elements.obtain().init_trans(v1); }
	protected void _rot(Float f1) { com(f1); 
		elements.obtain().init_rot(f1); }
	protected void _scale(Float f1) { com(f1); 
		elements.obtain().init_scale(f1); }



	protected void _rect(Vector2 v1, Vector2 v2) { com(v1,v2); 
		elements.obtain().init_rect(v1,v2); }
	protected void _line(Vector2 v1, Vector2 v2) { com(v1,v2); 
		elements.obtain().init_line(v1,v2); }
	protected void _point(Vector2 v1) { com(v1); 
		elements.obtain().init_point(v1); }
	protected void _trig(Vector2 v1, Vector2 v2, Vector2 v3) { com(v1,v2,v3); 
		elements.obtain().init_trig(v1,v2,v3); }
	protected void _circle(Vector2 v1, Float f1) { com(v1,f1); 
		elements.obtain().init_circle(v1,f1); }

	protected void _text(String t, Vector2 p, Float f) { com(t,p,f); 
		elements.obtain().init_text(t,p,f); }
	
	protected void _fill(Integer _r, Integer _g, Integer _b, Integer _a) { com(_r,_g,_b,_a); 
		elements.obtain().init_fill(_r,_g,_b,_a); }
	protected void _stroke(Integer _r, Integer _g, Integer _b, Integer _a, Float _w) { com(_r,_g,_b,_a,_w); 
		elements.obtain().init_strok(_r,_g,_b,_a,_w); }
	
	

	private Vector2 paint_ref = new Vector2();
	private float paint_scale = 1.0f, paint_rot = 0.0f;
	
	private nPool<Elem> elements = new nPool<Elem>() {
		protected Elem newObject() { return new Elem(); } };
	
	
	
	// TODO     Action to add:
	// QUAD, QUINT, HEXA, OCTO, 
	
	enum Act { LINE, POINT, RECT, FILL, NOFILL, STROK, NOSTROK, TEXT, 
		TRIG, CIRCLE, PUSH, POP, TRANSLATE, SCALE, ROTATE }
	private class Elem {
		Act act;
		Vector2 v1,v2,v3;
		int r,g,b,a;
		float f1,f2;
		String t;
		public Elem() {}
		public void init_scale(float f) { act = Act.SCALE; f1 = f; }
		public void init_rot(float f) { act = Act.ROTATE; f1 = f; }
		public void init_trans(Vector2 _v1) { act = Act.TRANSLATE; v1 = Utl.copy(_v1); }
		public void init_push() { act = Act.PUSH; }
		public void init_pop() { act = Act.POP; }
		public void init_circle(Vector2 _v1, float s) { 
			act = Act.CIRCLE; v1 = Utl.copy(_v1); f1 = s; }
		public void init_trig(Vector2 _v1, Vector2 _v2, Vector2 _v3) { 
			act = Act.TRIG; v1 = Utl.copy(_v1); v2 = Utl.copy(_v2); v3 = Utl.copy(_v3); }
		public void init_text(String _t, Vector2 _v, float _f) { 
			act = Act.TEXT; t = Utl.copy(_t); v1 = Utl.copy(_v); f1 = _f; }
		public void init_nostrok() { act = Act.NOSTROK; }
		public void init_nofill() { act = Act.NOFILL; }
		public void init_strok(int _r, int _g, int _b, int _a, float _f) { 
			act = Act.STROK; r = _r; g = _g; b = _b; a = _a; f1 = _f; }
		public void init_fill(int _r, int _g, int _b, int _a) { 
			act = Act.FILL; r = _r; g = _g; b = _b; a = _a; }
		public void init_rect(Vector2 _v1, Vector2 _v2) { 
			act = Act.RECT; v1 = Utl.copy(_v1); v2 = Utl.copy(_v2); }
		public void init_line(Vector2 _v1, Vector2 _v2) { 
			act = Act.LINE; v1 = Utl.copy(_v1); v2 = Utl.copy(_v2); }
		public void init_point(Vector2 _v1) { act = Act.POINT; v1 = Utl.copy(_v1); }
		public void draw(nDrawer drawer) {
			if (act == Act.RECT) { drawer.rect(v1.x,v1.y,v2.x,v2.y); }
			else if (act == Act.LINE) { drawer.line(v1,v2); }
			else if (act == Act.POINT) { drawer.circle(v1.x,v1.y,4f); }
			else if (act == Act.NOFILL) { drawer.noFill(); }
			else if (act == Act.NOSTROK) { drawer.noStroke(); }
			else if (act == Act.FILL) { drawer.fill(r,g,b,a); }
			else if (act == Act.STROK) { drawer.stroke(r,g,b,a,f1); }
			else if (act == Act.TEXT) { drawer.text(t,v1,f1); }
			else if (act == Act.CIRCLE) { drawer.circle(v1.x,v1.y,f1); }
			else if (act == Act.TRIG) { drawer.polygon(v1,v2,v3); }
			else if (act == Act.PUSH) { drawer.push(); }
			else if (act == Act.POP) { drawer.pop(); }
			else if (act == Act.TRANSLATE) { drawer.translate(v1); }
			else if (act == Act.ROTATE) { drawer.rotate(f1); }
			else if (act == Act.SCALE) { drawer.scale(f1); }
		}
	}
	
	
}
