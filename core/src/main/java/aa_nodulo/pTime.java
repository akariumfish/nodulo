package aa_nodulo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import app.App;
import data.*;
import gui.*;
import patch.pInstance;
import patch.pNode;
import util.Timer;
import util.Utl;
import util.nMap;
import util.nPool;
import util.nRun;

public class pTime {
	
	public static void build_node() {
		
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
				PlaneApplet.app.time.addTickBric(instance);
			}})
			.useClear().commande(new nRun() {public void run() { 
				PlaneApplet.app.time.removeTickBric(instance);
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

	}

	
	
	
	public void set_pause(boolean b) { val_pause.set(b); }

	ArrayList<nRun> eventTickRun = new ArrayList<nRun>();
	ArrayList<nRun> eventNetTickRun = new ArrayList<nRun>();
	
	public pTime addEventTick(nRun r) { eventTickRun.add(r); return this; }
	public pTime removeEventTick(nRun r) { eventTickRun.remove(r); return this; }
	public pTime addEventNetTick(nRun r) { eventNetTickRun.add(r); return this; }
	public pTime removeEventNetTick(nRun r) { eventNetTickRun.remove(r); return this; }
	public pTime clearEventTick() { eventTickRun.clear(); return this; }
	
	
	public pTime(PlaneApplet a) { app = a; init(); }
	
	
	

	public PlaneApplet app = null;

	public sValueBloc bloc = null;

	public boolean use_net_frame = false;
	
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
	
	public void init() {
		bloc = app.data.root_bloc.obtainBloc("time_bloc");
		bloc.addObject("tick", this);

		bloc.addMetode("clearing", new nRun() { public void run() {
			clear(); }}); 

		use_net_frame = app.config.start_as_client;
		
		val_tickrate = bloc.obtainFlt("val_tickrate", 1f / 
				app.config.DEF_TICK_BY_SEC); // cible
		val_tick_by_sec = bloc.obtainFlt("val_tick_by_sec"); // result
		val_pause = bloc.obtainBoo("val_pause", true);
		val_tick_cnt = bloc.obtainInt("val_tick_cnt", 0);
		val_tick_cnt.set(0);
		
		app.outputs.put("pause", new nRun() { public void run() {
			val_pause.set(!val_pause.get()); }});
		
		info_tps = app.gui.add_info_text("tps:", val_tick_by_sec); 
		info_cnt = app.gui.add_info_text("tick cnt: ", val_tick_cnt);
		
		tps_stack = new int[tps_stack_size];
	    for (int i = 0 ; i < tps_stack_size ; i++) tps_stack[i] = 60;

		timer = new Timer();
		timer.start("tick");
		run_pause = new nRun() { public void run() {
			if (val_pause.get() && !app.NET_CTRL) timer.pause("tick");
			else timer.play("tick"); 
		}};
		val_pause.addEventChangeLastFrame(run_pause);
		run_pause.run();

		nRun run_do_tick = new nRun() { public void run(Object o) { 
			if ((boolean)o) do_tick(1); }};
		nRun run_pause = new nRun() { public void run(Object o) { 
			if ((boolean)o) val_pause.set(!val_pause.get()); }};
			
		app.gui.add_shortcut_target("Time - Next Tick", 'N', run_do_tick);
		app.gui.add_shortcut_target("Time - Pause", 'P', run_pause);

		app.addEventToolInit(new nRun() { public void run(Object o) {
			nInterface interf = (nInterface)o;
			if (!app.NET_CTRL) {
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
			if (!app.config.start_solo) {
				app.net.net.addSyncVal(val_tick_cnt);	 }
		}});
	}
	public void clear() {

		for (int i = 0 ; i < tps_stack_size ; i++) tps_stack[i] = 60;
		tps_stack_count = 0; tps_med = 0;
		tick_stack = 0; //tick_count = 0;

		info_tps.clear();
		info_cnt.clear();

		eventTickRun.clear();
		
		timer.dispose();
		
		tick_force = 0; tick_call = 0;

		if (bloc != null) bloc.clear();
		
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
	
	public void do_frame(float delta) {
		if (use_net_frame) net_frame(delta); else frame(delta); 
	}
	
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
		Utl.log_pref2 = ":"+val_tick_cnt.get();

		for (pInstance b : prev_tick_bric) b.run("do_prev_tick");
		
		nRun.runEvents(eventTickRun);
		nRun.runEvents(eventTickRun, tick_delta);

		for (pInstance b : tick_bric) b.run("do_tick");
		
		app.tick_end_inputs();
	}
	
	
	int net_tick_bang = 0;
	public void bang_net_tick() {
		net_tick_bang++;
	}
	
	public void exec_net_tick() {

		float tick_delta = (float)timer.stop("tick");
		timer.start("tick");

		Utl.log_pref2 = ":"+val_tick_cnt.get();
		
		for (pInstance b : prev_tick_bric) b.run("do_prev_tick");
		
		nRun.runEvents(eventNetTickRun);
		nRun.runEvents(eventNetTickRun, tick_delta);

		for (pInstance b : tick_bric) b.run("do_tick");

		app.tick_end_inputs();
		
		tick_count++;
	}
	
	
}
