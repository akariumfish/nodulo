package data;

import net.nNetwork;

public class sStr extends sValue {
	public String asStr() { return app.copy(val); }
	boolean limited; int max;
	sStr set_limit(int ma) { limited = true; max = ma; return this; }
	sStr clear_limit() { limited = false; return this; }
	public String getString() { return app.copy(val); }
	public void clear() { 
		super.clear(); val = app.copy(def); 
		data.str_pool.free(this);
	}
	String val = null, def = null;
	
	public sStr() { super(); }
	
	//called when obtained from pool
	public sStr init(sValueBloc b, String v, String n, String s) {
		super.init(b, "str", n, s); 
		val = app.copy(v); def = app.copy(val); return this; }
		
	//called when freed by pool
	@Override
	public void reset() {
		// TODO Auto-generated method stub
		super.reset();
		val = null; def = null;
		limited = false; max = 0;
	}

	public String get() { return app.copy(val); }
	public boolean equals(String v) { return val.equals(v); }
	public void set(String v) { 
		run_events_allset(); 
		if (!v.equals(val)) { 
			if (limited && v.length() > max) val = v.substring(0, max); else val = app.copy(v); 

			//filter line return
			for (int i = val.length() - 1 ; i >= 0  ; i--)
				if (val.charAt(i) == '\n' || val.charAt(i) == '\r') {
					val = val.substring(0, i);
					if (i+1 < val.length()) val += val.substring(i + 1, val.length());
				}

			doChange(); 
		} 
	}
	public void add(String v) { set(val + v); }
//	void save_to_bloc(Save_Bloc svb) { super.save_to_bloc(svb);
//		svb.newData("val", val);
//	}
//	void load_from_bloc(Save_Bloc svb) { super.load_from_bloc(svb);
//		set(svb.getData("val"));
//	}
	void save_to_bloc(File_Bloc svb) { super.save_to_bloc(svb);
		svb.newData("val", val);
	}
	void load_from_bloc(File_Bloc svb) { super.load_from_bloc(svb);
		set(svb.getString("val"));
	}
	public void toNetMsg(nNetwork.UpdateValue uv) {
		uv.put("val", val);
	}
	public void fromNetMsg(nNetwork.UpdateValue uv) {
		set(uv.getStr("val"));
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
