package aa_term;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import aa_nodulo.PlaneApplet;
import data.*;
import gui.nInterface;
import gui.nWidgetGroup;
import util.*;

public class pTerm {
	
	
	public pTerm(PlaneApplet a) {
		app = a;
		tick_run = new nRun() { public void run(Object o) { tick((float)o); }};
		net_tick_run = new nRun() { public void run(Object o) { net_tick((float)o); }};
//		draw_run = new nDrawable() { public void drawing() { draw(); }}; 
		
		init();
	}
	
	public void setInputProcessor() {
		Gdx.input.setInputProcessor(console.getMultiplexer());
	}

	public void register(String r, sValueBloc b, Object o) {
		console.register(r,b,o); 
	}

//	public CommandHistory newStoredCode(String code_ref) {
//		return console.newStoredCode(code_ref);
//	}
	
	
	public PlaneApplet app;

	public sValueBloc bloc = null;

	nRun tick_run, net_tick_run;
//	nDrawable draw_run;

	public boolean use_net_frame = false;
	
	GUIConsole console;
//	CommandExecutor exec;
	
	public void init() {
		bloc = app.data.root_bloc.obtainBloc("term_bloc");
		bloc.addObject("term", this);

		bloc.addMetode("clearing", new nRun() { public void run() {
			clear(); }}); 
		
		use_net_frame = app.config.start_as_client;
		
		Skin skin = new Skin(Gdx.files.classpath("console_ui/uiskin.json"));
		console = new GUIConsole(skin, true, Keys.GRAVE);
//		exec = addExecutor(new CommandExecutor("term"));
//		console.setCommandExecutor(exec);
		
		console.setVisible(app.config.POP_TERMINAL);
		console.setConsoleStackTrace(true);
//		console.enablePrintButton(true);
		console.setTitle("Terminal - small 2 to hide");
//		console.setMaxEntries(16);
		console.setHoverColor(Color.BLACK);
		console.setNoHoverColor(Color.BLACK);
		console.setHoverAlpha(.9f);
		console.setNoHoverAlpha(.9f);
		
		console.setSizePercent(100, 50);
		console.setPositionPercent(0, 0);

//		console.enableSubmitButton(true);
//		console.setSubmitText("Fire!");
		
	}
	
	public void system_load() {
		

		app.time.addEventTick(tick_run);
		app.time.addEventNetTick(net_tick_run);
//		app.view.addDrawable(draw_run);

		
//		if (!app.config.RELEASE) tool_setup(true);
		
//		app.addDelayEvent(100, new nRun(this) { public void run() {
//			console
//			.exec("sys time")
//			.exec("pause true")
//			.exec("sys term")
//			;
//		}});
		

		app.outputs.put("trm", new nRun() { public void run() {
			if (args.length < 1) return;
			String r = arg(0,String.class); 
			console.submitCommand(r);
		}});
		

		app.addDelayEvent(80, new nRun(this) { public void run() {
			console.submitCommand("setboo time val_pause (not (getboo time val_pause))");
		}});

		app.addDelayEvent(120, new nRun(this) { public void run() {
			console.submitCommand("setboo time val_pause (not (getboo time val_pause))");
		}});

		app.addDelayEvent(160, new nRun(this) { public void run() {
			console.submitCommand("setboo time val_pause (not (getboo time val_pause))");
		}});
		
		
	}
	
	public void clear() {
		
		console.dispose();
		
		app.time.removeEventTick(tick_run);
		app.time.removeEventNetTick(net_tick_run);
//		app.view.removeDrawable(draw_run);
		
		bloc.clear();
	}
	
//	public void tool_init(nInterface interf) {
//		interf.setContext(bloc);
//
////		interf.add_row();
////		interf.add_row_watch(5, "Body : ", "val_body_nb");
////		interf.add_row_watch(5, " / ", "val_body_pool");
////		interf.add_row();
////		interf.add_row_label(10, "");
////		interf.add_row();
////		interf.add_row_label(1, "");
////		interf.add_row_trigg(8, "", new nRun() { public void run() {
////			 
////		}});
////		interf.add_row_label(1, "");
//		
//		
//		
//	}
//	public void tool_setup(boolean open) {
//		
//		app.addDelayEvent(1, new nRun(this) { public void run() {
//			nWidgetGroup sec = app.gui.toolbox
//					.addSection("Terminal", open);
//			nInterface interf = app.gui.addInterface();
//			interf.pop(sec);
//			interf.setContext(bloc);
//			tool_init(interf);
//		}});
//		
//	}
	

	public void do_frame(float delta) {
		if (use_net_frame) net_frame(delta); else frame(delta); 
	}
	
	public void frame(float delta) {
		
	}
	
	public void tick(float delta) {
		
	}

	public void net_frame(float delta) { }
	public void net_tick(float delta) { }
	

	public void draw() {

		console.actOnly();
		
		console.drawOnly();
		
	}
	
	
	
	
}
