package app;

import java.util.ArrayList;

import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class nTransform {
	
	class Transf {
		Vector2 translate = new Vector2();
		float scale = 1.0f;
		float rotate = 0.0f;
		public Transf() {}
		public Transf(Transf t) { 
			translate.x = t.translate.x; translate.y = t.translate.y;
			scale = t.scale; rotate = t.rotate; }
		public Transf(float x, float y) {
			translate.x = x; translate.y = y; }
		public Transf(float x, float y, float s) {
			translate.x = x; translate.y = y; scale = s; }
		public Transf(float x, float y, float s, float r) {
			translate.x = x; translate.y = y; scale = s; rotate = r; }
		public void init() { translate.x = 0f; translate.y = 0f; scale = 1f; rotate = 0.0f; }
		public void set(Transf t) { 
			translate.x = t.translate.x; translate.y = t.translate.y;
			scale = t.scale; rotate = t.rotate; }
		public String toString() { 
			return "[T:"+translate.x+","+translate.y+" S:"+scale+" R:"+rotate+"]";
		}
	}
	
	ArrayList<ArrayList<Transf>> transform = new ArrayList<ArrayList<Transf>>();
	
	ArrayList<Transf> transf = new ArrayList<Transf>();
	
	private boolean dirty = true;
	
	public nTransform() {
		reset();
	}
	
	public static String toString(nTransform t) { 
		String s = "";
		for (ArrayList<Transf> c : t.transform) {
			for (Transf nt : c) {
				s += nt.toString() + " ";
			}
		}
		for (Transf c : t.transf) {
			s += c.toString() + " ";
		}
		Vector2 v = t.getTranslation();
		s += " result: "+v.x+","+v.y+" S:"+t.getScale()+" R:"+t.getRotation();
		return s;
	}

	public void transf(nTransform t) { 
		for (ArrayList<Transf> c : t.transform) {
			for (Transf nt : c) {
				Transf n = new Transf(nt);
				transf.add(n); 
			}
		}
		for (Transf c : t.transf) {
			Transf n = new Transf(c);
			transf.add(n); 
		}
		dirty = true;
	}
	
	public void set(nTransform t) {
		reset();
		for (ArrayList<Transf> c : t.transform) {
			ArrayList<Transf> a = new ArrayList<Transf>();
			for (Transf nt : c) {
				Transf n = new Transf(nt);
				a.add(n); 
			}
			transform.add(a); 
		}
		for (Transf c : t.transf) {
			Transf n = new Transf(c);
			transf.add(n); 
		}
		dirty = true;
	}
	
	public void push() {
		ArrayList<Transf> a = new ArrayList<Transf>();
		for (Transf nt : transf) {
			Transf n = new Transf(nt);
			a.add(n); 
		}
		transform.add(a); 
		transf.clear();
		dirty = true;
	}
	public void pop() {
		ArrayList<Transf> t = transform.get(transform.size()-1);
		transf.clear();
		for (Transf nt : t) {
			Transf n = new Transf(nt);
			transf.add(n); 
		}
		transform.remove(t); 
		dirty = true;
	}
	
	public void reset() { 
		for (ArrayList<Transf> c : transform) c.clear();
		transform.clear(); 
		transf.clear();
		dirty = true;
	}

	public void translate(Vector2 v) { translate(v.x, v.y); }
	
	public void translate(float x, float y) { dirty = true; 
		Transf n = new Transf(); n.translate.set(x, y); transf.add(n); }
	public void scale(float s) { dirty = true; 
		Transf n = new Transf(); n.scale = s; transf.add(n); }
	public void rotate(float s) { dirty = true; 
		Transf n = new Transf(); n.rotate = s; transf.add(n); }
	
	private Vector2 tot_translat = new Vector2();
	private float tot_scale, tot_rot;
	private void clean() {
		if (dirty) {
			Vector2 tmp = new Vector2();
			Vector2 r = new Vector2();
			float s = 1.0f;
			float rt = 0.0f;
			for (ArrayList<Transf> tl : transform) 
				for (Transf t : tl) {
					tmp.set(t.translate.x * s, t.translate.y * s);
					tmp.rotateRad(rt);
					r.add(tmp);
					rt += t.rotate;
					s *= t.scale;
			}
			for (Transf t : transf) {
				tmp.set(t.translate.x * s, t.translate.y * s);
				tmp.rotateRad(rt);
				r.add(tmp);
				rt += t.rotate;
				s *= t.scale;
			}
			tot_translat.set(r);
			tot_scale = s;
			tot_rot = rt;
			dirty = false;
		}
	}
	public Vector2 getTranslation() { clean(); return Applet.copy(tot_translat); }
	public float getScale() { clean(); return tot_scale; }
	public float getRotation() { clean(); return tot_rot; }
	
	public Vector2 transform(float x, float y) {
		return transform(new Vector2(x,y)); }
	public Vector2 transform(Vector2 v) {
		Vector2 v2 = new Vector2(v);
		Vector2 r = getTranslation();
		float s = getScale();
		float rt = getRotation();
		v2.rotateRad(rt);
		r.x += v2.x * s; r.y += v2.y * s;
		return r;
	}
	public Rectangle transform(Rectangle o) {
		Rectangle r = new Rectangle(o);
		Vector2 cv = new Vector2(r.width/2f,r.height/2f);
		Vector2 c = new Vector2(r.x+cv.x,r.y+cv.y);
		float s = getScale();
//		Vector2 v = new Vector2(r.x,r.y);
		Vector2 v = new Vector2(c.x,c.y);
		v.set(transform(v));
		cv.scl(getScale());
//		cv.rotateRad(getRotation());
		r.width *= s; r.height *= s;
//		r.x = v.x; r.y = v.y;
		r.x = v.x-cv.x; r.y = v.y-cv.y;
		return r;
	}
	public Circle transform(Circle o) {
		Circle c = new Circle(o);
		c.radius *= getScale();
		Vector2 v = new Vector2(c.x,c.y);
		v.set(transform(v));
		c.x = v.x; c.y = v.y;
		return c;
	}

	
	public Vector2 revert(Vector2 v) {
		Vector2 r = getTranslation();
		float s = getScale();
		float rt = getRotation();
		Vector2 a = new Vector2();
		a.x = (v.x - r.x) / s;
		a.y = (v.y - r.y) / s;
		a.rotateRad(-rt);
		return a;
	}
	
	
	
	

	
}
