package data;

import app.Applet;
import net.nNetwork;

public class sBoo extends sValue {
	
	public void directshortcut_action() { run_events_allset(); val = !val; doChange(); }
	public boolean asBoo() { return val; }
	public String getString() { return String.valueOf(val); }
	public void clear() { 
		super.clear(); val = def; 
		data.boo_pool.free(this);
	}
	boolean val = false, def;
	
	public sBoo() { super(); }
	
	//called when obtained from pool
	public sBoo init(sValueBloc b, boolean v, String n, String s) {
		super.init(b, "boo", n, s); val = v; def = val; return this; }
	public sBoo init(sValueBloc b, boolean v, String n, String s, char ct) {
		super.init(b, "boo", n, s); val = v; def = val; return this; } //set_directshortcut(ct);
		
	//called when freed by pool
	@Override
	public void reset() {
		// TODO Auto-generated method stub
		super.reset();
		val = false; def = false;
	}
	
	public boolean get() { return val; }
	public void set(boolean v) { run_events_allset(); if (v != val) { val = v; doChange(); } }
	public void swtch() { val = !val; doChange(); }

//	void save_to_bloc(Save_Bloc svb) { super.save_to_bloc(svb);
//		svb.newData("val", val);
//	}
//	void load_from_bloc(Save_Bloc svb) { super.load_from_bloc(svb);
//		set(svb.getBoolean("val"));
//	}
	void save_to_bloc(File_Bloc svb) { super.save_to_bloc(svb);
		svb.newData("val", val);
	}
	void load_from_bloc(File_Bloc svb) { super.load_from_bloc(svb);
		set(svb.getBoolean("val"));
	}
	public void toNetMsg(nNetwork.UpdateValue uv) {
		uv.put("val", val);
	}
	public void fromNetMsg(nNetwork.UpdateValue uv) {
		set(uv.getBoo("val"));
	}

	public Object get_val() { return val; }

//	public void populate_viewer() {
//		if (widgGroup_value_viewer != null) {
//			widgGroup_value_viewer.addWidget("switch", 
//					widgGroup_value_viewer.gui.addWidget("W_entry")
//					.setParent(widgGroup_value_viewer.getGroup("window").get("back"))
//					.setSwitch().asWidget()
//					.setLink(this)
//					.setText("switch")
//					);
//		}
//	}
	
}
