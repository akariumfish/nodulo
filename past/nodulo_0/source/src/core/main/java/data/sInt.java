package data;

import net.nNetwork;

public class sInt extends sValue {
	public int asInt() { return val; }
	int min, max;
	public sInt set_limit(int mi, int ma) { 
		if (!limited_min || !limited_max || min != mi || max != ma) doChange(); 
		limited_min = true; limited_max = true; 
		min = mi; max = ma; return this; }
	public sInt set_min(int mi) { 
		if (!limited_min || min != mi) doChange(); 
		limited_min = true; 
		min = mi; return this; }
	public sInt set_max(int ma) { 
		if (!limited_max || max != ma) doChange(); 
		limited_max = true; 
		max = ma; return this; }
	public float getmin() { return min; }
	public float getmax() { return max; }
	public float getscale() { return (float)(val - min) / (float)(max - min); }
	public void setscale(float v) { set(min + (int)( v * (max - min) )); }

	public float asFloat() { return (float)(val); }

	public String getString() { return String.valueOf(val); }
	public void clear() { 
		super.clear(); val = def; 
		data.int_pool.free(this);
	}
	int val = 0, def;
	public float ctrl_factor = 2;
	
	public sInt() { super(); }
	
	//called when obtained from pool
	public sInt init(sValueBloc b, int v, String n, String s) {
		super.init(b, "int", n, s); val = v; def = val; return this; }
		
	//called when freed by pool
	@Override
	public void reset() {
		// TODO Auto-generated method stub
		super.reset();
		val = 0; def = 0; ctrl_factor = 2;
		min = 0; max = 0;
	}
	
	public int get() { return val; }
	public void set(double v) { set((int)v); }
	public void set(float v) { set((int)v); }
	public void set(int v) { 
		run_events_allset(); 
		if (limited_max && v > max) v = max; if (limited_min && v < min) v = min;
		if (v != val) { val = v; doChange(); } }
//	void save_to_bloc(Save_Bloc svb) { super.save_to_bloc(svb);
//	svb.newData("val", val);
//	svb.newData("min", min);
//	svb.newData("max", max);
//	svb.newData("lmin", limited_min);
//	svb.newData("lmax", limited_max);
//	}
//	void load_from_bloc(Save_Bloc svb) { super.load_from_bloc(svb);
//	set(svb.getInt("val"));
//	min = svb.getInt("min");
//	max = svb.getInt("max");
//	limited_min = svb.getBoolean("lmin");
//	limited_max = svb.getBoolean("lmax");
//	}
	void save_to_bloc(File_Bloc svb) { super.save_to_bloc(svb);
	svb.newData("val", val);
	svb.newData("min", min);
	svb.newData("max", max);
	svb.newData("lmin", limited_min);
	svb.newData("lmax", limited_max);
	}
	void load_from_bloc(File_Bloc svb) { super.load_from_bloc(svb);
	set(svb.getInt("val"));
	min = svb.getInt("min");
	max = svb.getInt("max");
	limited_min = svb.getBoolean("lmin");
	limited_max = svb.getBoolean("lmax");
	}
	public void toNetMsg(nNetwork.UpdateValue uv) {
		uv.put("val", val);
	}
	public void fromNetMsg(nNetwork.UpdateValue uv) {
		set(uv.getInt("val"));
	}
	public void add(int v) { set(get()+v); }
	public void mult(int v) { set(get()*v); }
	public void div(int v) { set(get()/v); }
	public void div(float v) { set((int)(get()/v)); }
	public void div(double v) { set((int)(get()/v)); }

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
