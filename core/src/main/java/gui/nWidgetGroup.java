package gui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool.Poolable;

import app.Applet;
import data.sValueBloc;
import util.nClearable;
import util.nMap;
import util.nRun;


public class nWidgetGroup implements Poolable, nClearable {
	
	public void print_state() {
		String t = "";
		t += "-"+group_id+" ref:"+ref+" key:"+groupKey;
		if (group != null) t += " in "+group.ref;
		t += " groups:"+widgetgroups.size()+" widgets:"+widgets.size();
		app.logn(t);
	}
	
	
	public nGUI gui;
	public Applet app;
	
	public nMap<nWidget> widgets;
	public nMap<nWidgetGroup> widgetgroups;
	public nMap<nRun> metodes;
	public nMap<Object> objects;
	
	public String ref = "";

	public String model_ref = "";
	
	public nWidgetGroup group = null;
	public String groupKey = "";

	public boolean clearing = true;

	public static int WGROUP_COUNTER = 0;
	public int group_id = 0;
	
	public nWidgetGroup(nGUI g) {
		gui = g;
		app = gui.app;
		group_id = WGROUP_COUNTER;
		WGROUP_COUNTER++;
		widgets = new nMap<nWidget>();
		widgetgroups = new nMap<nWidgetGroup>();
		metodes = new nMap<nRun>();
		objects = new nMap<Object>();
	}
	
	//called when optainned from pool
	public nWidgetGroup init() {
		if (clearing) {
			empty();
			group = null; groupKey = ""; ref = "";
			model_ref = "";
			eventClearRun.clear();
			clearing = false;
	//		app.log("new wgroup : w:"+widgets.size()+" g:"+widgetgroups.size());
		}
		return this;
	}
	
	//called when freed by the pool
	@Override
	public void reset() { 
		model_ref = "";
	}
	
	//clear everything in group
	public void empty() { 
		for (nWidget w : widgets.tmp_all()) w.clear();
		for (nWidgetGroup w : widgetgroups.tmp_all()) w.clear();
		
		widgets.clear(); 
		widgetgroups.clear();
		metodes.clear(); 
		objects.clear(); 
		
	}
	
	//clear all widget in group and free the group
	public void clear() { 
		if (!clearing) {
			
//			app.log("widgetgroup "+ref+" clear");
			
			clearing = true;

			nRun.runEvents(eventClearRun);

			empty();
			
			if (group != null) group.removeGroup(this);
			group = null; groupKey = ""; ref = "";
			model_ref = "";

			eventClearRun.clear();
			
			gui.widgetgroup_pool.free(this);
		}
	}
	
	ArrayList<nRun> eventClearRun = new ArrayList<nRun>();
	
	public nWidgetGroup addEventClear(nRun r) { eventClearRun.add(r); return this; }
	public nWidgetGroup removeEventClear(nRun r) { eventClearRun.remove(r); return this; }
	public nWidgetGroup clearEventClear() { eventClearRun.clear(); return this; }
	
	public nWidget get(String ref) { return widgets.get(ref); }
	public nWidget addWidget(String model) {
		nWidget w = gui.addWidget(model);
		addWidget(model, w); return w; }
	public nWidget addWidget(String ref, String model) {
		nWidget w = gui.addWidget(model);
		addWidget(ref, w); return w; }
	public nWidget addWidget(String ref, nWidget w) {
//		String s = "";
//		for (Entry<String, nWidget> mev : widgets.entrySet()) s += " "+mev.getKey()+":"+mev.getValue().widget_id;
//		app.log("Doing addWidget("+w.widget_id+") in group "+this.ref+" with: "+s);
		
		if (widgets.containsKey(ref)) app.logn("ERROR : widgetgroup "+this.ref+" cant addWidget, key "+ref+" allready exist");
		else if (!widgets.containsValue(w)) w.setGroup(this, ref); 
		else app.logn("ERROR: widgetgroup "+this.ref+" cant addWidget "+ref+" because its allready in group as " + w.groupKey); return w; }
	public nWidgetGroup removeWidget(nWidget w) {
		w.quitGroup(); return this; }
	public nWidgetGroup removeWidget(String s) {
		nWidget w = get(s);
		if (w != null) w.quitGroup(); return this; }
	
	public boolean hasGroup(String ref) { return widgetgroups.containsKey(ref); }
	public nWidgetGroup getGroup(String ref) { return widgetgroups.get(ref); }
	public nWidgetGroup addWidgetGroup(String ref, nWidgetGroup w) {
		if (widgetgroups.containsKey(ref)) app.logn("ERROR : widgetgroup "+this.ref+" cant addGroup, key "+ref+" allready exist");
		else if (!widgetgroups.containsValue(w)) {
			if (w.group != null) w.group.removeGroup(w);
			w.groupKey = ref; w.group = this;
			widgetgroups.put(ref, w); }  
		else app.logn("ERROR : widgetgroup "+this.ref+" cant addGroup, it allready contain "+w.ref);
		return w; }
	public nWidgetGroup addWidgetGroup(String ref, String model) {
		return addWidgetGroup(ref, gui.addWidgetGroup(model)); }
	public nWidgetGroup removeGroup(nWidgetGroup w) {
		if (widgetgroups.containsValue(w)) {
			widgetgroups.remove(w.groupKey, w); 
			w.groupKey = ""; w.group = null;
		} 
		return this; }
	
	public nWidgetGroup addObject(String ref, Object r) {
		if (hasObject(ref)) app.logn("ERROR: object <"+ref+"> allready exist in WidgetGroup "+this.ref); 
		if (r != null) objects.put(ref, r); return this; }
	public nWidgetGroup removeObject(String ref, Object r) {
		if (!hasObject(ref)) app.logn("ERROR: object <"+ref+"> dont exist in WidgetGroup "+this.ref); 
		if (r != null) objects.remove(ref, r); return this; }
	public nWidgetGroup removeObject(String ref) {
		if (!hasObject(ref)) app.logn("ERROR: object <"+ref+"> dont exist in WidgetGroup "+this.ref); 
		objects.remove(ref); return this; }
	public boolean hasObject(String ref) { return objects.get(ref) != null; }
	public Object object(String ref) {
		if (objects.get(ref) != null) return objects.get(ref);
		else app.logn("ERROR: object <"+ref+"> dont exist in WidgetGroup "+this.ref); 
		return null; }
	public <T> T object(String ref, Class<T> cl) { 
		if (hasObject(ref)) {
			Object o = objects.get(ref);
			if (cl.isAssignableFrom(o.getClass())) return (T)o; 
			else { app.logn("ERROR: object <"+ref+"> in WidgetGroup "+this.ref+" cant be cast to "+cl); return null; } 
		} else app.logn("ERROR: object <"+ref+"> dont exist in WidgetGroup "+this.ref); 
		return null; }
	public nWidgetGroup setObject(String ref, Object r) {
		if (r != null) {
			if (hasObject(ref)) {
				Object old = objects.get(ref);
				if (old != null) objects.remove(ref, old);
			}
			objects.put(ref, r); 
		} //else app.log("ERROR: object <"+ref+"> dont exist in WidgetGroup "+this.ref); 
		return this; 
	}
	
	public nWidgetGroup addMetode(String ref, nRun r) {
		metodes.put(ref, r); return this; }
	public nWidgetGroup metode(String ref) {
		if (metodes.get(ref) != null) metodes.get(ref).run(); 
		else app.logn("ERROR: metode <"+ref+"> dont exist in WidgetGroup "+this.ref); return this; }
	public nWidgetGroup metode(String ref, Object o) {
		if (metodes.get(ref) != null) metodes.get(ref).run(o); 
		else app.logn("ERROR: metode <"+ref+"> dont exist in WidgetGroup "+this.ref); return this; }
	public nWidgetGroup metode(String ref, Object o1, Object o2) {
		if (metodes.get(ref) != null) metodes.get(ref).run(o1, o2); 
		else app.logn("ERROR: metode <"+ref+"> dont exist in WidgetGroup "+this.ref); return this; }
	public nWidgetGroup metode(String ref, Object o1, Object o2, Object o3) {
		if (metodes.get(ref) != null) metodes.get(ref).run(o1, o2, o3); 
		else app.logn("ERROR: metode <"+ref+"> dont exist in WidgetGroup "+this.ref); return this; }
	public Object metodeGet(String ref) {
		if (metodes.get(ref) != null) return metodes.get(ref).get(); 
		else app.logn("ERROR: metode <"+ref+"> dont exist in WidgetGroup "+this.ref); return null; }
	public Object metodeGet(String ref, Object o) {
		if (metodes.get(ref) != null) return metodes.get(ref).get(o); 
		else app.logn("ERROR: metode <"+ref+"> dont exist in WidgetGroup "+this.ref); return null; }
	public Object metodeGet(String ref, Object o1, Object o2) {
		if (metodes.get(ref) != null) return metodes.get(ref).get(o1,o2); 
		else app.logn("ERROR: metode <"+ref+"> dont exist in WidgetGroup "+this.ref); return null; }
	public Object metodeGet(String ref, Object o1, Object o2, Object o3) {
		if (metodes.get(ref) != null) return metodes.get(ref).get(o1,o2,o3); 
		else app.logn("ERROR: metode <"+ref+"> dont exist in WidgetGroup "+this.ref); return null; }
	
	public int metodeGetInt(String ref) {
		return (int)metodeGet(ref); }
	public int metodeGetInt(String ref, Object o) {
		return (int)metodeGet(ref,o); }
	public float metodeGetFlt(String ref) {
		return (float)metodeGet(ref); }
	public float metodeGetFlt(String ref, Object o) {
		return (float)metodeGet(ref,o); }
	public boolean metodeGetBoo(String ref) {
		return (boolean)metodeGet(ref); }
	public boolean metodeGetBoo(String ref, Object o) {
		return (boolean)metodeGet(ref,o); }
	public String metodeGetStr(String ref) {
		return (String)metodeGet(ref); }
	public String metodeGetStr(String ref, Object o) {
		return (String)metodeGet(ref,o); }
	public Vector2 metodeGetVec(String ref) {
		return (Vector2)metodeGet(ref); }
	public Vector2 metodeGetVec(String ref, Object o) {
		return (Vector2)metodeGet(ref,o); }
}
