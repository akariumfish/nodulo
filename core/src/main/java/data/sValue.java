package data;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool.Poolable;

import app.App;
import app.nLauncher;
import gui.nInterface;
import gui.nWidget;
import gui.nWidgetGroup;
import net.nNetwork;
import util.Utl;
import util.nClearable;
import util.nRun;


public abstract class sValue extends nLauncher implements nClearable, Poolable {
	
	
	public final ArrayList<String> flags = new ArrayList<String>();
	public sValue addFlag(String f) {
		for (String s : flags) if (s.equals(f)) return this;
		flags.add(f); return this; }
	public boolean isFlag(String f) {
		for (String s : flags) if (s.equals(f)) { return true; } return false; }
	
	
	
	public abstract void toNetMsg(nNetwork.UpdateValue uv);
	public abstract void fromNetMsg(nNetwork.UpdateValue uv);
	
	ArrayList<nWidget> linked_widget = new ArrayList<nWidget>();
	public sValue linkWidget(nWidget v) { linked_widget.add(v); return this; }
	public sValue unlinkWidget(nWidget v) { linked_widget.remove(v); return this; }
	
	public boolean log = false;
	sValueBloc getBloc() { return bloc; }
	public abstract String getString();
	
	@Override
	public void build_lauchables() {
		eventsChangeLastFrame = newEventList("eventsChangeLastFrame", new ArrayList<nRun>());
		eventsChangeThisFrame = newEventList("eventsChangeThisFrame", new ArrayList<nRun>());
		eventsAllChange = newEventList("eventsAllChange", new ArrayList<nRun>());
		eventsAllGet = newEventList("eventsAllGet", new ArrayList<nRun>());
		eventsDelete = newEventList("eventsDelete", new ArrayList<nRun>());
	}
	
	public boolean doevent() { return doevent; }
	public sValue doEvent(boolean v) { doevent = v; return this; }
	public sValue pauseEvent() { pauseevent = true; return this; }
	public sValue addEventDelete(nRun r) { eventsDelete.add(r); return this; }
	public sValue removeEventDelete(nRun r) { eventsDelete.remove(r); return this; }
	public sValue addEventChangeLastFrame(nRun r) { eventsChangeLastFrame.add(r); return this; }
	public sValue removeEventChangeLastFrame(nRun r) { eventsChangeLastFrame.remove(r); return this; }
	public sValue addEventChangeThisFrame(nRun r) { eventsChangeThisFrame.add(r); return this; }
	public sValue removeEventChangeThisFrame(nRun r) { eventsChangeThisFrame.remove(r); return this; }
	public sValue addEventAllChange(nRun r) { eventsAllChange.add(r); return this; }
	public sValue removeEventAllChange(nRun r) { eventsAllChange.remove(r); return this; }
	public sValue addEventAllGet(nRun r) { eventsAllGet.add(r); return this; }
	public sValue removeEventAllGet(nRun r) { eventsAllGet.remove(r); return this; }
	public void doChange() { 
		if (!pauseevent) { 
			if (doevent && data.doevent) runEventList("eventsAllChange"); 
			has_changed = true; 
		} else {
			was_changed = true;
		}
	}
	public void cancelChange() { 
		has_changed = false;
		was_changed = false;
	}
	public sValueBloc bloc;
	boolean has_changed = false, doevent = true, pauseevent = false, was_changed = false;
	public String ref;
	public String type;
	public String shrt;
	public String adress;
	sData data;
	App app;
	
	sValue() { super(); }
	
	//called when obtained from pool
	public sValue init(sValueBloc b, String t, String r, String s) {
		bloc = b; data = bloc.data; app = data.app;
		while (bloc.values.get(r) != null) r = r + "'";
		type = t; ref = r; shrt = s;
		if (!sData.refIsValid(r)) Utl.logn("ERROR Invalid sValue ref");
		bloc.values.put(ref, this); 
		if (bloc == data) adress = sData.adress_token + ref;
		else adress = bloc.adress + sData.adress_token + ref;
		if (bloc.doevent) bloc.last_created_value = this; 
		if (bloc.doevent && data.doevent) bloc.runEventList("eventsAddVal"); 
		return this; 
	}
	
	public void reset() {
		bloc = null;
		type = ""; ref = ""; shrt = ""; adress = "";
		limited_min = false; limited_max = false;
		has_changed = false; doevent = true; pauseevent = false; was_changed = false;
		log = false;
		
		linked_widget.clear();
		flags.clear();

		eventsChangeLastFrame.clear();
		eventsChangeThisFrame.clear();
		eventsAllChange.clear();
		eventsAllGet.clear();
		eventsDelete.clear();
		
//		if (widgGroup_value_viewer != null) widgGroup_value_viewer.clear();
//		widgGroup_value_viewer = null;
	}

	public void clear() { 
		for (int i = linked_widget.size()-1 ; i >= 0 ; i--) linked_widget.get(i).unlink(this);
		if (doevent && data.doevent) runEventList("eventsDelete");
		if (bloc.doevent && data.doevent) bloc.runEventList("eventsDelVal");
		bloc.values.remove(ref, this); 
	}
	
	void frame_start() { 
		if (!pauseevent) { 
			if (has_changed) { 
				if (doevent && data.doevent) runEventList("eventsChangeLastFrame"); 
			} 
			has_changed = false; 
		} else {
			if (was_changed) { 
				if (doevent && data.doevent) runEventList("eventsAllChange"); 
				has_changed = true; 
			}
			pauseevent = false; 
		}
	}
	void frame_end() { 
		if (!pauseevent) { 
			if (has_changed) { 
				if (doevent && data.doevent) runEventList("eventsChangeThisFrame"); 
			} 
		}
	}
	ArrayList<nRun> eventsChangeLastFrame;
	ArrayList<nRun> eventsChangeThisFrame;
	ArrayList<nRun> eventsAllChange;
	ArrayList<nRun> eventsAllGet; 
	ArrayList<nRun> eventsDelete;
	public void run_events_change() {
		if (doevent && data.doevent) runEventList("eventsChangeLastFrame"); }
	public void run_events_allchange() {
		if (doevent && data.doevent) runEventList("eventsAllChange"); }
	public void run_events_allset() {
		if (doevent && data.doevent) runEventList("eventsAllGet"); }
	
	public boolean limited_min = false;
	public boolean limited_max = false;
	public sValue set_limit_min(boolean b) {
		if (b != limited_min) doChange(); 
		limited_min = b; return this; }
	public sValue set_limit_max(boolean b) { 
		if (b != limited_max) doChange(); 
		limited_max = b; return this; }
	public sValue set_limit(boolean b1, boolean b2) { 
		if (b1 != limited_min || b2 != limited_max) doChange(); 
		limited_min = b1; limited_max = b2; return this; }
	public sValue set_min(float mi) { return this; }
	public sValue set_max(float d) { return this; }
	public float getmin() { return 0; }
	public float getmax() { return 0; }
	public float getscale() { return 0; }
	public void setscale(float v) { ; }

	public boolean asBoo() { return false; }
	public int asInt() { return 0; }
	public float asFlt() { return 0; }
	public Vector2 asVec() { return null; }
	public Color asCol() { return Utl.color(0); }
	public String asStr() { return ""; }

	public boolean isFlt()   { return type.equals("flt"); }
	public boolean isInt()   { return type.equals("int"); }
	public boolean isBoo()   { return type.equals("boo"); }
	public boolean isVec()   { return type.equals("vec"); }
	public boolean isCol()   { return type.equals("col"); }
	public boolean isStr()   { return type.equals("str"); }
	public boolean isArr()   { return type.equals("arr"); }
	public boolean isTab()   { return type.equals("tab"); }
	public boolean isCo()   { return type.equals("co"); }
	
	public abstract Object get_val();
	
	public void set_from_undef(Object o) {
		if (o.getClass() == Float.class) {
			((sFlt)this).set((Float)o);
		}
		if (o.getClass() == Integer.class) {
			((sInt)this).set((Integer)o);
		}
		if (o.getClass() == Boolean.class) {
			((sBoo)this).set((Boolean)o);
		}
		if (o.getClass() == String.class) {
			((sStr)this).set((String)o);
		}
		if (o.getClass() == Vector2.class) {
			((sVec)this).set((Vector2)o);
		}
	}
	
//	protected nWidgetGroup widgGroup_value_viewer = null;
//	
	public void open_viewer() {
//		nInterface interf = data.app.menu.get_popWindow();
//		interf.add_row();
//		interf.add_row_label(6, "  "+type+"  :  "+ref+"  ");
//		String tv = getString();
//		if (tv.length() > 0) {
//			interf.add_row();
//			interf.add_row_label(6, " val : "+tv);
//		}
//		populate_viewer(interf);
//		data.app.menu.pop_popwindow(ref);
	}
//	
	public void populate_viewer(nInterface interf)  {}

	
	//		public char direct_shortcut = 0;
	//		public void set_directshortcut(char s) {
	//			if (s != 0) {
	//				direct_shortcut = s;
	//				if (!data.input.shorted_values.contains(this)) {
	//					data.input.getKeyboardButton(s);
	//					data.input.shorted_values.add(this); } 
	//			else clear_directshortcut(); } }
	//		public void clear_directshortcut() { 
	//			direct_shortcut = 0; 
	//			while (data.input.shorted_values.contains(this))
	//				data.input.shorted_values.remove(this); }
	//		public void directshortcut_action() {}
	
	
//	void save_to_bloc(Save_Bloc sb) {
//		//	    vlogln("sv save " + ref);
//		sb.newData("ref", ref);
//		sb.newData("typ", type);
//		sb.newData("shr", shrt);
//		sb.newData("flag_nb", flags.size());
//		for (int i = 0 ; i < flags.size() ; i++) {
//			sb.newData("flag_"+i, flags.get(i));
//		}
//		//	    sb.newData("cut", (int)direct_shortcut);
//	}
//	void load_from_bloc(Save_Bloc svb) {
//		//	    vlogln("sv load " + ref);
//		ref = svb.getData("ref");
//		type = svb.getData("typ");
//		shrt = svb.getData("shr");
//		if (svb.getData("flag_nb") != null) {
//			int flag_nb = Applet.toint(svb.getData("flag_nb"));
//			for (int i = 0 ; i < flag_nb ; i++) {
//				String f = svb.getData("flag_"+i);
//				addFlag(f);
//			}
//		}
//		//	    direct_shortcut = (char)svb.getInt("cut");
//		//	    if (direct_shortcut != 0) { 
//		//	    		data.input.getKeyboardButton(direct_shortcut);
//		//	    		data.input.shorted_values.add(this);
//		//	    }
//		//	    else data.input.shorted_values.remove(this);
//		has_changed = true;
//	}
	
	
	
	
	
	
	void save_to_bloc(File_Bloc sb) {
		//	    vlogln("sv save " + ref);
		sb.newData("ref", ref);
		sb.newData("typ", type);
		sb.newData("shr", shrt);
		sb.newData("flag_nb", flags.size());
		for (int i = 0 ; i < flags.size() ; i++) {
			sb.newData("flag_"+i, flags.get(i));
		}
		//	    sb.newData("cut", (int)direct_shortcut);
	}
	void load_from_bloc(File_Bloc svb) {
		//	    vlogln("sv load " + ref);
		ref = svb.getString("ref");
		type = svb.getString("typ");
		shrt = svb.getString("shr");
		if (svb.hasData("flag_nb")) {
//			int flag_nb = Applet.toint(svb.getString("flag_nb"));
			int flag_nb = svb.getInt("flag_nb");
			for (int i = 0 ; i < flag_nb ; i++) {
				String f = svb.getString("flag_"+i);
				addFlag(f);
			}
		}
		//	    direct_shortcut = (char)svb.getInt("cut");
		//	    if (direct_shortcut != 0) { 
		//	    		data.input.getKeyboardButton(direct_shortcut);
		//	    		data.input.shorted_values.add(this);
		//	    }
		//	    else data.input.shorted_values.remove(this);
		has_changed = true;
	}
}
