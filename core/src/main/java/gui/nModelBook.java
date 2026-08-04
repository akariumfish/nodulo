package gui;

import com.badlogic.gdx.math.Vector2;

import app.GdxApp;
import util.Utl;
import util.nMap;

public class nModelBook {
	
	public final nMap<nModel> models;
	public final nMap<nModelGroup> modelgroups;
	
	public nModelBook() {
		models = new nMap<nModel>();
		modelgroups = new nMap<nModelGroup>();
		
		build_def();
	}
	
	public nModel newModel(String ref) {
		nModel m = new nModel();
		models.put(ref,m);
		return m;
	}
	public nModel newModel(String ref, nModel m) {
		models.put(ref,m);
		return m;
	}
	
	public nModel getModel(String ref) {
		return models.get(ref);
	}
	
	public nWidget buildWidget(String ref, nGUI gui) {
		return gui.addWidget().copyFrom(getModel(ref)).asWidget()
				.runModelCustomInit(getModel(ref));
	}
	public nWidget buildWidget(String size_ref, 
			String col_ref, String func_ref, nGUI gui) {
		return gui.addWidget()
				.copySizeFrom(getModel(size_ref))
				.copyLookFrom(getModel(col_ref))
				.copyFunctionFrom(getModel(func_ref))
				.asWidget()
				.runModelCustomInit(getModel(func_ref));
	}
	
	public void newModelGroup(String ref, nModelGroup g) {
		if (modelgroups.containsKey(ref)) 
			GdxApp.loggn("ERROR: book allready contains a modelGroup with key "+ref);
		modelgroups.put(ref, g);
		g.ref = Utl.copy(ref);
	}

	public nWidgetGroup buildGroup(String ref, nGUI gui) {
		nWidgetGroup g = modelgroups.get(ref).build(gui);
		g.model_ref = Utl.copy(ref);
		return g;
	}

	public final float RS = 30; //ref size
	public final float WINDOW_STACK_STRT_X = 400, 
			WINDOW_STACK_STRT_Y = GdxApp.HEIGHT - 50, 
			WINDOW_STACK_SIZE = 10, WINDOW_STACK_INCR = 40;
	public float WINDOW_STACK_CNT = 0; 
	
	public Vector2 getNewWindowPos() {
		float px = WINDOW_STACK_STRT_X + WINDOW_STACK_CNT * WINDOW_STACK_INCR;
		float py = WINDOW_STACK_STRT_Y - WINDOW_STACK_CNT * WINDOW_STACK_INCR;
		WINDOW_STACK_CNT++;
		if (WINDOW_STACK_CNT >= WINDOW_STACK_SIZE) WINDOW_STACK_CNT = 0;
		return new Vector2(px,py);
	}
	
	public void build_def() {

		
		newModel("CL_def")
		.set_color_background(Utl.color(80,80,80,255))
		.set_color_pressed(Utl.color(20,20,255,255))
		.set_color_hovered(Utl.color(0,0,210,255))
		.set_color_standby(Utl.color(0,0,120,255))
		.set_color_sliderback(Utl.color(50,50,50,255))
		.set_color_outline(Utl.color(200,200,200,255))
		.set_color_outline_selected(Utl.color(200,200,0,255))
		.set_color_shadow(Utl.color(0,0,0,100))
		.set_color_switch_on(Utl.color(0,70,255,255))
		.set_color_switch_off(Utl.color(0,0,40,255))
		.set_color_text(Utl.color(200,255))
		.setFont(18)
		;

	}
}
