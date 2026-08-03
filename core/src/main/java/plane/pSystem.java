package plane;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import app.Applet;
import data.sBloc_Builder;
import data.sBoo;
import data.sInt;
import data.sStr;
import data.sTab;
import data.sValue;
import data.sValueBloc;
import gui.nDrawable;
import gui.nInterfModel;
import gui.nInterface;
import gui.nWidget;
import gui.nWidgetGroup;
import patch.pStandard;
import util.nMap;
import util.nRun;

public abstract class pSystem {
	
	public static nMap<Class<? extends pSystem>> systems = 
			new nMap<Class<? extends pSystem>>();
	public static HashMap<Class<? extends pSystem>, String> system_refs = 
			new HashMap<Class<? extends pSystem>, String>();
	
	public static void storeSystemType(String r, Class<? extends pSystem> ct) {
		if (system_refs.get(ct) == null) system_refs.put(ct,r); systems.put(r,ct); }
	
//	public static void build(Applet app) {
////		pPlane.builder.addEventInit(new nRunnable() { public void run(Object o) {
////			sValueBloc b = (sValueBloc)o; }});
//	}
	
	public static ArrayList<sBloc_Builder> sys_builders = new ArrayList<sBloc_Builder>();

	public static <T extends pSystem> sBloc_Builder builder(Applet app, String ref, 
			Class<T> sc, nRun run_new) {
		return builder(app,ref,sc,true,run_new); }
	public static <T extends pSystem> sBloc_Builder builder(Applet app, String ref, 
			Class<T> sc, boolean addToPlane, nRun run_new) {
		 
		storeSystemType(ref,sc);
		
		sBloc_Builder new_builder = new sBloc_Builder(app.data, ref)
			.setSolo(true)
			.setInitRun(new nRun() { public void run(Object o) {
				sValueBloc b = (sValueBloc)o; run_new.run(b); }})
			.setLoadRun(new nRun() { public void run(Object o) {
				sValueBloc b = (sValueBloc)o; b.object("system", pSystem.class).do_system_load(); }})
			.setClearRun(new nRun() { public void run(Object o) {
				sValueBloc b = (sValueBloc)o; b.run("clearing"); }});

		if (addToPlane) {
			pPlane.builder.addEventInit(new nRun() { public void run(Object o) {
				sValueBloc b = (sValueBloc)o; b.addBlocBuilder(new_builder); 
				if (b.is_new_bloc) {
					app.gdx.exec_nothrow("pPlane.bloc.buildBloc("+ref+")", 
							new nRun() { public void run() {	
						b.buildBloc(ref, ref); }}); }
			}});
		}
		sys_builders.add(new_builder);
		
		return new_builder;
	}
	
//	public static void build_sys_brics(Applet app) {
//		for (Class<? extends pSystem> sys : systems.all()) {
//			String sys_ref = system_refs.get(sys);
//			
//			pStandard stand_sys = pBric.newUnicBricModel("sys_"+sys_ref);
//			stand_sys.process().useLoad()
//			.commande(new nRun() {public void run() {
//				 sValueBloc sys_bloc = instance.patch.plane.getSystem(sys).bloc;
//				 nInterface interf = instance.object("bric_interf", nInterface.class);
//				 interf.setContext(sys_bloc);
////				 for (sValue val : sys_bloc.values.all()) {
////					 if (val.isBoo()) {
////						 interf.add_row();
////						 interf.add_row_switch_boo(8, val.ref, val.ref);
////					 } else {
////						 interf.add_row();
////						 interf.add_row_watch(8, val.ref+" = ", val.ref);
////					 }
////				 }
//				 instance.patch.plane.getSystem(sys).tool_init(interf);
//			}})
//			;
//		}
//	}
	

//	public static pStandard build_sys_bric(Applet app, Class<? extends pSystem> sys) {
//		String sys_ref = system_refs.get(sys);
//		
//		pStandard stand_sys = pBric.newUnicBricModel("sys_"+sys_ref);
//		stand_sys.process().useLoad()
//		.commande(new nRun() {public void run() {
//			 sValueBloc sys_bloc = instance.patch.plane.getSystem(sys).bloc;
//			 nInterface interf = instance.object("bric_interf", nInterface.class);
//			 interf.setContext(sys_bloc);
////				 for (sValue val : sys_bloc.values.all()) {
////					 if (val.isBoo()) {
////						 interf.add_row();
////						 interf.add_row_switch_boo(8, val.ref, val.ref);
////					 } else {
////						 interf.add_row();
////						 interf.add_row_watch(8, val.ref+" = ", val.ref);
////					 }
////				 }
//			 instance.patch.plane.getSystem(sys).tool_init(interf);
//		}})
//		;
//		return stand_sys;
//	}

	public abstract void system_init();
	public abstract void system_load();
	public abstract void system_clear();
	public void tool_init(nInterface interf) {}

	public abstract void frame(float delta);
	public void net_frame(float delta) {}   // to override
	

	public void do_frame(float delta) {
		if (use_net_frame) {
			if (val_active_frame.get()) net_frame(delta);
		} else {
			if (val_active_frame.get()) frame(delta);
		} 
	}
	public void useNetFrame() {
		use_net_frame = app.start_as_client;
	}
	public boolean use_net_frame = false;
	
	public sValueBloc bloc = null;
	public pPlane plane = null;
	public Applet app = null;

	public String getRef() { return bloc.ref; }
	public sValueBloc getBloc() { return bloc; }

	public sBoo val_active_frame;
	public sInt val_prio_frame;
	public static int max_frame_prio = 0;

	public pSystem() {}
	
	public pSystem init(sValueBloc b) {
		bloc = b; app = b.app;
		plane = b.parent.object("plane", pPlane.class);
		bloc.addObject("plane", plane);
		bloc.addObject("system", this);
		
		plane.systems.put(bloc.ref, this);
		
		val_active_frame = bloc.obtainBoo("val_active_frame", true);
		val_prio_frame = bloc.obtainInt("val_prio_frame", 5);
		
		max_frame_prio = Math.max(max_frame_prio, val_prio_frame.get());
		val_prio_frame.addEventChangeLastFrame(new nRun() { public void run() {
			max_frame_prio = Math.max(max_frame_prio, val_prio_frame.get()); }});

		bloc.addMetode("clearing", new nRun() { public void run() {
			clear(); }}); 

		val_seed = bloc.obtainInt("val_seed", 123456);
		rng = new Random(val_seed.get());
		
		system_init();
		
		return this;
	}

	public void do_system_load() { 
		if (plane.bloc.is_new_bloc) system_load();
		else {
			plane.bloc.addEventLoadEnd(new nRun() { public void run() {
				system_load();
			}});
		}
	}
	
	public void clear() {

		system_clear();
		
		if (plane != null) plane.systems.remove(bloc.ref, this);
		
		if (bloc != null) bloc.clear();
		
	}
	
	public sInt val_seed;
	public Random rng;

	public void reset_rng() { rng.setSeed(val_seed.get()); }

	public void rngSeed() {
		val_seed.set(plane.seed_rng.nextInt());
		reset_rng(); }

	public float rngFlt(float min, float max) {
		return min + rng.nextFloat() * (max - min); }

	void add_flt_row(nInterface interf, String text, String val, float min, float max) {
		interf.add_row();
		interf.add_row_label(5, text);
		interf.add_row_watch(5, "", val);
		interf.add_row();
		interf.add_row_slide_flt(10, min, max, val);
	}
	public void tool_setup() {
		tool_setup(false);
	}
	public void tool_setup(boolean open) {
		
		app.addDelayEvent(1, new nRun(this) { public void run() {
			nWidgetGroup sec = app.menu.toolbox
					.addSection(system_refs.get(builder.getClass()), open);
			nInterface interf = app.gui.addInterface();
			interf.pop(sec);
			interf.setContext(bloc);
			tool_init(interf);
		}});
		
//		app.addDelayEvent(1, new nRun() { public void run() {
//			nInterface interf = app.gui.addInterface();
//			nWidget w = app.gui.addWidget();
//			interf.pop(w);
//
////			interf.setContext(bloc);
//			tool_init(interf);
//			
//			nInterfModel interf_model = interf.create_model("tool");
//			sTab val_interf_model_default = 
//					bloc.obtainTab("val_quickt_interf_model_default");
//			if (val_interf_model_default != null) {
//				interf_model.save_to(val_interf_model_default);}
//			
//			bloc.run("add_menu"); 
//			
//			app.addDelayEvent(1, new nRun() { public void run() {			
//				sTab val_interf_model = 
//						bloc.getValue("val_quickt_interf_model", sTab.class);
//				if (val_interf_model != null && val_interf_model.width() <= 1) {
//					interf_model.save_to(val_interf_model);
//					val_interf_model.doChange(); }
//				w.clear();
//			}}); 
//		}});
//		
//		
	}
	
}
