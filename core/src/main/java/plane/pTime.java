package plane;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import app.Applet;
import app.Timer;
import app.nMap;
import app.nPool;
import app.nRun;

import data.*;
import gui.*;
import patch.pAnk;
import patch.pInstance;
import patch.pNode;

public class pTime extends pSystem {

	public static sBloc_Builder builder = null;
	
	public static void build(Applet app) {
		builder = builder(app, "tick", pTime.class, new nRun() { public void run(Object o) {
			sValueBloc b = (sValueBloc)o; newObject(b); }});
	}
	
	public static void build_node(Applet app) {
		
		pNode.newNodeModel("time")
		.newRun("do_step", new nRun() {public void run() { 
			if (!instance.hasVar("counter")) { instance.addVar("counter", (int)0); }
			int delay = instance.getVar("delay", Integer.class);
			int counter = instance.getVar("counter", Integer.class);
			if (instance.hasVar("state") && instance.getVar("state", Boolean.class)) {
				counter++; if (counter >= delay) {
					pInstance co = instance.get("get_co", pInstance.class, "out");
					co.run("send", true);
					counter = 0; }
				instance.setVar("counter", counter); }
		}})
		.newRun("do_tick", new nRun() {public void run() { 
			if (!(instance.hasVar("tick") && instance.getVar("tick", Boolean.class))) return;
			instance.run("do_step");
		}})
		.process()
			.commande(new nRun() {public void run() { 
				pTime time = instance.patch.plane.getSystem(pTime.class);
				time.addTickBric(instance);
			}})
			.useClear().commande(new nRun() {public void run() { 
				pTime time = instance.patch.plane.getSystem(pTime.class);
				time.removeTickBric(instance);
			}}).useInit()
			.openSec()
				.param("logic_event", new nRun() {public void run() { 
					if (!(instance.hasVar("frame") && 
							instance.getVar("frame", Boolean.class))) return;
					instance.run("do_step");
				}}) 
				.run(pNode.getRun(pNode.CT.RUNP_ADD_LABEL), "", (int)1)
			.closeSec()
			.openSec()
				.param("def", (int)1, "min", 1f, "max", 60f, "granulo", 1f)
				.run(pNode.getRun(pNode.CT.RUNP_VAR_INT_LAB_FIELD), "delay", "delay", (int)6)
			.closeSec()
			.commande(pNode.getCom(pNode.CT.COM_ADD_ROW))
			.openSec()
				.param("text", "T", "width", (int)4) 
				.run(pNode.getRun(pNode.CT.RUNP_VAR_BOO_SWITCH), "tick")
			.closeSec()
			.openSec()
				.param("text", "F", "width", (int)4) 
				.run(pNode.getRun(pNode.CT.RUNP_VAR_BOO_SWITCH), "frame")
			.closeSec()
			.openSec()
				.param("text", "ON", "width", (int)4) 
				.run(pNode.getRun(pNode.CT.RUNP_VAR_BOO_SWITCH), "state")
			.closeSec()
		.getStand()
		.param("keys", new String[] {"bang"}, "filters", new String[] {"bang"})
		.run(pNode.getRun(pNode.CT.RUNS_ADD_CO_OUT), "out")
		;

//		pNode.newNodeModel("frame")
//		.process()
//			.openSec()
//				.param("logic_event", new nRun() {public void run() { 
//					if (!instance.hasVar("counter")) { instance.addVar("counter", (int)0); }
//					int delay = instance.getVar("delay", Integer.class);
//					int counter = instance.getVar("counter", Integer.class);
//					if (instance.hasVar("state") && instance.getVar("state", Boolean.class)) {
//						counter++; if (counter >= delay) {
//							pInstance co = instance.get("get_co", pInstance.class, "out");
//							co.run("send", true);
//							counter = 0; }
//						instance.setVar("counter", counter); }
//				}}) 
//				.run(pNode.getRun(pNode.CT.RUNP_ADD_LABEL), "", (int)1)
//			.closeSec()
//			.openSec()
//				.param("def", (int)1, "min", 1f, "max", 60f, "granulo", 1f)
//				.run(pNode.getRun(pNode.CT.RUNP_VAR_INT_LAB_FIELD), "delay", "delay")
//			.closeSec()
//			.openSec()
//				.param("text", "on", "width", (int)4) 
//				.run(pNode.getRun(pNode.CT.RUNP_VAR_BOO_SWITCH), "state")
//			.closeSec()
//		.getStand()
//		.param("keys", new String[] {"bang"}, "filters", new String[] {"bang"})
//		.run(pNode.getRun(pNode.CT.RUNS_ADD_CO_OUT), "out")
//		;
		
	}
	
	
	public static void dispose(Applet app) { pool.dispose(); }
	public static final nPool<pTime> pool = new nPool<pTime>() {
		protected pTime newObject() { return new pTime(); } };
	public static pTime newObject(sValueBloc b) {
		return pool.obtain().init(b); }
	

	
	
	
	

	ArrayList<nRun> eventTickRun = new ArrayList<nRun>();
	ArrayList<nRun> eventNetTickRun = new ArrayList<nRun>();
	
	public pTime addEventTick(nRun r) { eventTickRun.add(r); return this; }
	public pTime removeEventTick(nRun r) { eventTickRun.remove(r); return this; }
	public pTime addEventNetTick(nRun r) { eventNetTickRun.add(r); return this; }
	public pTime removeEventNetTick(nRun r) { eventNetTickRun.remove(r); return this; }
	public pTime clearEventTick() { eventTickRun.clear(); return this; }
	
	
	public pTime() { super(); }
	public pTime init(sValueBloc b) { return (pTime) super.init(b); }
	
	
	

	public Timer timer;
	
	nRun run_pause;
	nWidget info_tps, info_cnt;

	public sFlt val_tickrate, val_tick_by_sec;
	public sBoo val_pause;
	public sInt val_tick_cnt;

	public ArrayList<pInstance> prev_tick_bric = new ArrayList<pInstance>();
	public ArrayList<pInstance> tick_bric = new ArrayList<pInstance>();
	public void addPrevTickBric(pInstance b) { 
		if (!prev_tick_bric.contains(b)) prev_tick_bric.add(b); }
	public void removePrevTickBric(pInstance b) { prev_tick_bric.remove(b); }
	public void addTickBric(pInstance b) { 
		if (!tick_bric.contains(b)) tick_bric.add(b); }
	public void removeTickBric(pInstance b) { tick_bric.remove(b); }
	
	public void system_init() {
		
		bloc.addObject("tick", this);
		
		plane.storeSystemType(bloc.ref, this.getClass());

		useNetFrame();
		

		val_tickrate = bloc.obtainFlt("val_tickrate", 1f / 
				app.getPref("DEF_TICK_BY_SEC", Float.class)); // cible
		val_tick_by_sec = bloc.obtainFlt("val_tick_by_sec"); // result
		val_pause = bloc.obtainBoo("val_pause", false);
		val_tick_cnt = bloc.obtainInt("val_tick_cnt", 0);
		val_tick_cnt.set(0);
		
		info_tps = app.menu.add_info_text("tps:", val_tick_by_sec); 
		info_cnt = app.menu.add_info_text("tick cnt: ", val_tick_cnt);
		
		tps_stack = new int[tps_stack_size];
	    for (int i = 0 ; i < tps_stack_size ; i++) tps_stack[i] = 60;

		timer = new Timer();
		timer.start("tick");
		run_pause = new nRun() { public void run() {
			if (val_pause.get() && !plane.NET_CTRL) timer.pause("tick");
			else timer.play("tick"); }};
		val_pause.addEventChangeLastFrame(run_pause);
		run_pause.run();

		nRun run_do_tick = new nRun() { public void run(Object o) { 
			if ((boolean)o) do_tick(1); }};
		nRun run_pause = new nRun() { public void run(Object o) { 
			if ((boolean)o) val_pause.set(!val_pause.get()); }};
			
		app.menu.add_shortcut_target("Time - Next Tick", 'N', run_do_tick);
		app.menu.add_shortcut_target("Time - Pause", 'P', run_pause);

		plane.addEventToolInit(new nRun() { public void run(Object o) {
			nInterface interf = (nInterface)o;
			if (!plane.NET_CTRL) {
//				interf.setContext(bloc);
				interf.cmd_context(bloc.adress);
				interf.add_row();
				interf.set_param("entry_height","3");
				interf.add_row_switch_boo(9,"pause","val_pause");
				interf.set_param("entry_height","0.5");
				interf.add_row();
				interf.add_row_label(9,"");
				interf.set_param("entry_height","1");
				interf.add_row();
				interf.add_row_label(1,"");
				interf.add_row_trigg(7,"Next Tick >", new nRun() { public void run() {
					do_tick(1); }});
				interf.add_row_label(1,"");
				interf.set_param("entry_height","0.5");
				interf.add_row();
				interf.add_row_label(9,"");
				interf.set_param("entry_height","1");
				interf.add_row();
				interf.add_row_watch(9,"tps","val_tick_by_sec");
				interf.set_param("entry_height","0.5");
				interf.add_row();
				interf.add_row_label(9,"");
				interf.set_param("entry_height","1");
				interf.add_row();
				interf.add_row_trigg(2,"120", new nRun() { public void run() {
					val_tickrate.set(1f/120f); }});
				interf.add_row_trigg(2,"60", new nRun() { public void run() {
					val_tickrate.set(1f/60f); }});
				interf.add_row_trigg(2,"30", new nRun() { public void run() {
					val_tickrate.set(1f/30f); }});
				interf.add_row_trigg(2,"10", new nRun() { public void run() {
					val_tickrate.set(1f/10f); }});
				interf.add_row();
				interf.add_row_trigg(2,"6", new nRun() { public void run() {
					val_tickrate.set(1f/6f); }});
				interf.add_row_trigg(2,"3", new nRun() { public void run() {
					val_tickrate.set(1f/3f); }});
				interf.add_row_trigg(2,"1", new nRun() { public void run() {
					val_tickrate.set(1f/1f); }});
				interf.add_row_trigg(2,"1/2", new nRun() { public void run() {
					val_tickrate.set(2f); }});
			}
		}});
		
	}
	public void system_load() {
		app.addDelayEvent(1, new nRun() { public void run() {
			if (!app.start_solo) {
				plane.getSystem(pNet.class).net.addSyncVal(val_tick_cnt);	 }
		}});
	}
	public void system_clear() {

		for (int i = 0 ; i < tps_stack_size ; i++) tps_stack[i] = 60;
		tps_stack_count = 0; tps_med = 0;
		tick_stack = 0; //tick_count = 0;

		info_tps.clear();
		info_cnt.clear();

		eventTickRun.clear();
		
		timer.dispose();
		
		tick_force = 0; tick_call = 0;
		
	}
	public void set_tickrate_fact(float n) { tickrate_fact = n; }
	public float get_tickrate_fact() { return tickrate_fact; }
	
	public void do_tick(int n) { tick_force = 0; tick_call += n; }
	public void force_tick(int n) { tick_call = 0; tick_force += n; }

	private int tick_call = 0;
	private int tick_force = 0;
	private float tickrate_fact = 1f;
	private int[] tps_stack;
	private int tps_stack_count = 0, tps_med = 0;
	private final int tps_stack_size = 60;
	
	private float tick_stack = 0, tick_count = 0;
	private long time_cnt = 0;

	public void net_frame(float delta) { 
		
		for (int i = 0 ; i < net_tick_bang ; i++) {
			exec_net_tick();
		}
		net_tick_bang = 0;

		long current = System.currentTimeMillis();
		tps_stack[tps_stack_count] = (int)(1000 * tick_count / (current - time_cnt));
		time_cnt = current;
		tps_stack_count++;
		if (tps_stack_count >= tps_stack_size) tps_stack_count = 0;
		
		tps_med = 0;
		for (int i = 0 ; i < tps_stack_size ; i++) tps_med += tps_stack[i];
		float f = tps_med / tps_stack_size;
		if (f > 0) val_tick_by_sec.set(f);
		else val_tick_by_sec.set(0);
		
		tick_count = 0;
		
	}

	public void frame(float delta) { 

		tick_count = 0;
		
		if (!val_pause.get()) {
			tick_stack += delta;
			while (tick_stack >= val_tickrate.get() * tickrate_fact) {
				tick_stack -= val_tickrate.get() * tickrate_fact;
				tick_count++; exec_tick();
			}
		} else if (tick_call > 0) {
			timer.play("tick");
			tick_stack += delta;
			while (tick_stack >= val_tickrate.get() && tick_call > 0) {
				tick_stack -= val_tickrate.get();
				tick_call--; tick_count++; exec_tick();
			}
		} else if (tick_force > 0) {
			timer.play("tick");
			while (tick_force > 0) {
				tick_force--; tick_count++; exec_tick(); }
		}
		
//		for (int i = 0 ; i < tick_count ; i++) { exec_tick(); }
		
		run_pause.run();
		
		long current = System.currentTimeMillis();
		tps_stack[tps_stack_count] = (int)(1000 * tick_count / (current - time_cnt));
		time_cnt = current;
		tps_stack_count++;
		if (tps_stack_count >= tps_stack_size) tps_stack_count = 0;
		
		tps_med = 0;
		for (int i = 0 ; i < tps_stack_size ; i++) tps_med += tps_stack[i];
		float f = tps_med / tps_stack_size;
		if (f > 0) val_tick_by_sec.set(f);
		else val_tick_by_sec.set(0);
		
	}
	
	public void exec_tick() {
		tickrate_fact = 1f;
		
		float tick_delta = (float)timer.stop("tick");
		timer.start("tick");
		
		val_tick_cnt.add(1);
		app.log_pref = ":"+val_tick_cnt.get();

		for (pInstance b : prev_tick_bric) b.run("do_prev_tick");
		
		nRun.runEvents(eventTickRun);
		nRun.runEvents(eventTickRun, tick_delta);

		for (pInstance b : tick_bric) b.run("do_tick");
		
		plane.tick_end_inputs();
	}
	
	
	int net_tick_bang = 0;
	public void bang_net_tick() {
		net_tick_bang++;
	}
	
	public void exec_net_tick() {

		float tick_delta = (float)timer.stop("tick");
		timer.start("tick");

		app.log_pref = ":"+val_tick_cnt.get();
		
		for (pInstance b : prev_tick_bric) b.run("do_prev_tick");
		
		nRun.runEvents(eventNetTickRun);
		nRun.runEvents(eventNetTickRun, tick_delta);

		for (pInstance b : tick_bric) b.run("do_tick");

		plane.tick_end_inputs();
		
		tick_count++;
	}
	
	
}
