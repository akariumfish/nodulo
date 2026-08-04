package data;

import com.badlogic.gdx.math.Vector2;

import net.nNetwork;
import util.Utl;
import zz_applet.Applet;

public class sVec extends sValue {
	public Vector2 asVec() { return new Vector2(val.x, val.y); }
	public String getString() { 
		return Utl.trimFlt(val.x) + "," + Utl.trimFlt(val.y); }
	
	private Vector2 val = new Vector2(), def = new Vector2();
	
	public sVec() { super(); }
	
	//called when obtained from pool
	public sVec init(sValueBloc b, String n, String s) {
		super.init(b, "vec", n, s); return this; }
	public sVec init(sValueBloc b, Vector2 v, String n, String s) {
		super.init(b, "vec", n, s); set(v); return this; }
	
	public void clear() { 
		super.clear(); val.x = def.x; val.y = def.y; 
		data.vec_pool.free(this);
	}
	
	//called when freed by pool
	public void reset() {
		super.reset();
//		val.set(0,0); def.set(0,0); 
	}
	
	public float x() { return val.x; }
	public float y() { return val.y; }
	public Vector2 get() { return new Vector2(val.x, val.y); }
	public sVec setx(double v) { return setx((float)v); }
	public sVec sety(double v) { return sety((float)v); }
	public sVec setx(float v) { run_events_allset(); if (v != val.x) { val.x = v; doChange(); } return this; }
	public sVec sety(float v) { run_events_allset(); if (v != val.y) { val.y = v; doChange(); } return this; }
	public sVec set(double _x, double _y) { return set((float)_x, (float)_y); }
	public sVec set(float _x, float _y) { 
		run_events_allset(); 
		if (_x != val.x || _y != val.y) {
			val.x = _x; 
			val.y = _y; 
			doChange(); 
		} 
		return this;
	}
	public sVec set(Vector2 v) { set(v.x, v.y); return this; }
	public sVec addx(float _x) { setx(val.x+_x); return this; }
	public sVec addy(float _y) { sety(val.y+_y); return this; }
	public sVec add(float _x, float _y) { set(val.x+_x, val.y+_y); return this; }
	public  sVec add(Vector2 v) { add(v.x, v.y); return this; }
	public  sVec add(sVec v) { add(v.x(), v.y()); return this; }
	public  sVec mult(float m) { set(val.x*m, val.y*m); return this; }
//	void save_to_bloc(Save_Bloc svb) { super.save_to_bloc(svb);
//	svb.newData("x", val.x);
//	svb.newData("y", val.y); }
//	void load_from_bloc(Save_Bloc svb) { super.load_from_bloc(svb);
//	set(svb.getFloat("x"), svb.getFloat("y")); }
	void save_to_bloc(File_Bloc svb) { super.save_to_bloc(svb);
	svb.newData("x", val.x);
	svb.newData("y", val.y); }
	void load_from_bloc(File_Bloc svb) { super.load_from_bloc(svb);
	set(svb.getFloat("x"), svb.getFloat("y")); }
	public void toNetMsg(nNetwork.UpdateValue uv) {
		uv.put("valx", val.x);
		uv.put("valy", val.y);
	}
	public void fromNetMsg(nNetwork.UpdateValue uv) {
		set(uv.getInt("valx"), uv.getInt("valy"));
	}

	public Object get_val() { return val; }

//	public void populate_viewer() {
//		if (widgGroup_value_viewer != null) {
////			widgGroup_value_viewer.addWidget("watch", 
////					widgGroup_value_viewer.gui.addWidget("W_entry")
////					.setParent(widgGroup_value_viewer.getGroup("window").get("back"))
////					.setWatcher(this)
////					);
//		}
//	}
}
