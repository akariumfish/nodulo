package gui;

import com.badlogic.gdx.math.Vector2;
import com.noodle.nodulo.GdxApp;

import util.Utl;
import util.nMap;

public class nModelBook {
	
	public final nMap<nModel> models;
	public final nMap<nModelGroup> modelgroups;
	
	public nModelBook() {
		models = new nMap<nModel>();
		modelgroups = new nMap<nModelGroup>();
		
	}
	
	public nModel newModel(String ref) {
		if (models.get(ref) != null) { return models.get(ref); }//models.get(ref).reset(); models.remove(ref); }
		nModel m = new nModel();
		models.put(ref,m);
		return m;
	}
	public nModel newModel(String ref, nModel m) {
		if (models.get(ref) != null) { return models.get(ref); }//models.get(ref).reset(); models.remove(ref); }
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
		if (modelgroups.hasKey(ref)) {
//			Utl.logn("ERROR: book allready contains a modelGroup with key "+ref);
//			return; 
			modelgroups.remove(ref);
		}
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
	
}
