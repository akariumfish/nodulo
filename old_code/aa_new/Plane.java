package aa_new;

import com.badlogic.gdx.math.Vector2;

import aa_nodulo.PlaneApplet;
import aa_nodulo.pParam;
import aa_nodulo.pProperty;
import aa_term.TerminalCommand;
import aa_term.pTerm;
import data.sInt;
import data.sPool;
import data.sTab;
import data.sValueBloc;
import gui.nDrawable;
import gui.nInterface;
import gui.nWidgetGroup;
import util.Utl;
import util.nMap;
import util.nRun;
import util.nSortedArray;

public class Plane {


	private static boolean has_build_statics = false;
	public static void build_statics() {
		if (has_build_statics) return;
		has_build_statics = true;



	}



	public void build_scripts() {

		term.exe("beginComponent coord")
		.exe("endComponent");
		
	}
	

	public void build_base_property() {

		Property entity = newProperty("entity")
		;

		Property entityComponent = newProperty("entityComponent")
		;


		Property script = newProperty("script")
		.registered()
		;

		Property tick_script = newProperty("tick_script")
		.copy(entityComponent)
		.addRef("script", "script").getProp()
		.addTickRun(new nRun() {public void run() {
			//run script
		}})
		;


		Property body = newProperty("body")
		.copy(entity)
		;

		Property coord = newProperty("coord")
		.copy(entityComponent)
		.addEntry("pos", Vector2.class).getProp()
		.addDrawRun(new nRun() {public void run() {
			Component com = arg(0, Component.class);
			Vector2 pos = com.get("pos", Vector2.class);
			app.fill(255);
			app.circle(pos.x,pos.y,20);
		}})
		;

	}

	private Component edited_comp = null;

	@TerminalCommand
	public void beginComponent(String prop_ref) {
		Property prop = properties.get(prop_ref);
		edited_comp = prop.obtain();
	}

	//	@TerminalCommand
	//	public boolean compHas(String r, Class<?> ct) { return edited_comp.has(r,ct); }
	//	@TerminalCommand
	//	public void setComp(String r, Object o) { edited_comp.set(r,o); }
	//	@TerminalCommand
	//	public Object compGet(String r) { return edited_comp.get(r); }
	//	@TerminalCommand
	//	public <V> V compGet(String r, Class<V> ct) { return edited_comp.get(r,ct); }
	//	@TerminalCommand
	//	public void compClear(String r) { edited_comp.clear(r); }
	//	@TerminalCommand
	//	public void compAdd(String r, Object o) { edited_comp.add(r,o); }
	//	@TerminalCommand
	//	public void compSet(String r, int i, Object o) { edited_comp.set(r,i,o); }
	//	@TerminalCommand
	//	public void compRemove(String r, Object o) { edited_comp.remove(r,o); }
	//	@TerminalCommand
	//	public int compSize(String r) { return edited_comp.size(r); }
	//	@TerminalCommand
	//	public Object compGet(String r, int i) { return edited_comp.get(r,i); }
	//	@TerminalCommand
	//	public <V> V compGet(String r, int i, Class<V> ct) { return edited_comp.get(r,i,ct); }
	//	@TerminalCommand
	//	public Object[] compAll(String r) { return edited_comp.all(r); }
	//	@TerminalCommand
	//	public <V> V[] compAll(String r, Class<V> ct) { return edited_comp.all(r,ct); }

	@TerminalCommand
	public void endComponent() {
		edited_comp.do_finish();
		edited_comp = null;
	}



	@TerminalCommand
	public void resetPlane() {
		emptyPlane();

	}

	@TerminalCommand
	public void emptyPlane() {

		for (Property prop : properties.all()) {
			prop.empty();
		}

	}




	public nMap<Property> properties = new nMap<Property>();

	nSortedArray<Property> with_frame_run = new nSortedArray<Property>().setOrdered().setPrioritized().setAutoSorted();
	nSortedArray<Property> with_tick_run = new nSortedArray<Property>().setOrdered().setPrioritized().setAutoSorted();
	nSortedArray<Property> with_draw_run = new nSortedArray<Property>().setOrdered().setPrioritized().setAutoSorted();

	public Property getProperty(String ref) {
		return properties.get(ref); }

	public Property newProperty(String ref) {
		if (properties.hasKey(ref)) {
			Utl.logn("ERROR Plane.newProperty : ref allready exist :"+ref);
			return null; }
		Property prop = new Property(this, ref);
		properties.put(ref, prop);
		return prop;
	}

	public void run_frame() 		{ 
		for (Property prop : with_frame_run) 	
			for (Component p : prop.pool.all()) prop.frame_runs.do_run(p); }
	public void run_tick() 		{ 
		for (Property prop : with_tick_run) 		
			for (Component p : prop.pool.all()) prop.tick_runs.do_run(p); }
	public void run_draw() 		{ 
		for (Property prop : with_draw_run) 		
			for (Component p : prop.pool.all()) prop.draw_runs.do_run(p); }




	public PlaneApplet app;
	public pTerm term;

	public sValueBloc bloc = null;

	nRun tick_run;
	nDrawable draw_run;

	sInt val_comp_nb, val_comp_cap;

	public Plane(PlaneApplet a) {
		app = a;
//		term = app.term;

		tick_run = new nRun() { public void run(Object o) { tick((float)o); }};
		draw_run = new nDrawable() { public void drawing() { draw(); }}; 

		bloc = app.data.root_bloc.obtainBloc("test_plane_bloc");

		val_comp_nb = bloc.obtainInt("val_comp_nb",0);
		val_comp_cap = bloc.obtainInt("val_comp_cap",0);

		term.register("plane", bloc, this);


		build_base_property();
	}

	public void finish() {
		for (Property prop : properties.all()) 
			prop.finish(); 

		app.addDelayEvent(1, new nRun(this) { public void run() {
			build_scripts();
		}});
		
		app.time.addEventTick(tick_run);
		app.view.addDrawable(25,draw_run);

		tool_setup(true);
	}

	public void dispose() {
		app.time.removeEventTick(tick_run);
		app.view.removeDrawable(draw_run);

		for (Property prop : properties.all()) { prop.dispose(); }
	}

	public void tool_init(nInterface interf) {
		interf.setContext(bloc);

		interf.add_row();
		interf.add_row_watch(6, "Component : ", "val_comp_nb");
		interf.add_row_watch(4, " / ", "val_comp_cap");
		interf.add_row();
		interf.add_row_label(10, "");
		interf.add_row();
		interf.add_row_label(1, "");
		interf.add_row_trigg(8, "Reset", new nRun() { public void run() {
			resetPlane(); }});
		interf.add_row_label(1, "");
		interf.add_row();
		interf.add_row_label(10, "");
		interf.add_row();
		interf.add_row_label(1, "");
		interf.add_row_trigg(8, "Empty Plane", new nRun() { public void run() {
			emptyPlane(); }});
		interf.add_row_label(1, "");
	}
	
	public void tool_setup(boolean open) {
		app.addDelayEvent(1, new nRun(this) { public void run() {
			nWidgetGroup sec = app.gui.toolbox
					.addSection("Plane", open);
			nInterface interf = app.gui.addInterface();
			interf.pop(sec);
			tool_init(interf);
		}});
	}


	public void frame_start(float delta) {
		int nb = 0, cap = 0;
		for (Property prop : properties.all()) {
			nb += prop.pool.size(); cap += prop.pool.capacity; }
		val_comp_nb.set(nb); val_comp_cap.set(cap);

		run_frame();
	}

	public void frame_end() {

	}

	public void tick(float delta) {
		run_tick();
	}

	public void draw() {
		run_draw();
	}

}
