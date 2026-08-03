package plane;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import com.badlogic.gdx.math.Vector2;

import app.Applet;
import data.*;
import gui.nDrawable;
import gui.nGUI;
import gui.nInterfCommand;
import gui.nInterfModel;
import gui.nInterface;
import gui.nModelBook;
import gui.nModelGroup;
import gui.nWidget;
import gui.nWidgetGroup;
import net.nNetwork;
import patch.pAnk;
import patch.pStandard;
import util.*;

public class pSpace extends pSystem {
	
	public static sBloc_Builder builder = null;
	
	public static void build(Applet app) {
		builder = builder(app, "space", pSpace.class, new nRun() { public void run(Object o) {
			sValueBloc b = (sValueBloc)o; newObject(b); }});
	}

	public static void dispose(Utl app) {
		pool.dispose();
	}

	public static final nPool<pSpace> pool = new nPool<pSpace>() {
		protected pSpace newObject() { return new pSpace(); } };
	public static pSpace newObject(sValueBloc b) {
		return pool.obtain().init(b); }
	

	
	
	
	
	
	
	
	
	
	
	

	ArrayList<nRun> eventSpaceStart = new ArrayList<nRun>();
//	ArrayList<nRun> eventSpaceClear = new ArrayList<nRun>();
	
	public pSpace addEventSpaceStart(nRun r) { eventSpaceStart.add(r); return this; }
	public pSpace removeEventSpaceStart(nRun r) { eventSpaceStart.remove(r); return this; }
//	public pSpace addEventSpaceClear(nRun r) { eventSpaceClear.add(r); return this; }
//	public pSpace removeEventSpaceClear(nRun r) { eventSpaceClear.remove(r); return this; }

	private boolean space_starting = false; 
	public void start_space() {
		if (!space_starting) {
			space_starting = true;
			pTime time = plane.getSystem(pTime.class);
			clear_all_body();
			app.addDelayEvent(2, new nRun() { public void run() {
				time.val_pause.set(true);
				nRun.runEvents(eventSpaceStart);
				app.addDelayEvent(1, new nRun() { public void run() {
					time.val_tick_cnt.set(0);
					time.val_pause.set(false);
					space_starting = false; }}); 
			}}); 
		}
	}
	
	
	
	
	
	

	public void build_param_database(sTab db) {
		db.setWidth(pProperty.body_common_propertys.size());
		int i = 0;
		for (pProperty model : pProperty.body_common_propertys.all()) {
			model.def_to_tab(db,i); i++; }
	}
	

	public sPool<pBody> body_pool;
	public sPool<pCollec> collec_pool;
	
	public boolean client_space = false;

	public pBody new_body() { pBody b = body_pool.obtain(); newBodys.add(b); return b; }

//	public pBody new_body(String species) { 
//		pBody b = new_body(); if (b == null) return null; else return b.addSpecies(species); }

	public pParam new_param(String pr) { 
		pParam p = param_pools.get(pr).obtain().obtain(); 
		if (p.prop != null && !p.prop.mode_nosync) newParams.add(p);
		return p; }
	
	public pCollec new_collec(pProperty p, String ref_in_p) { 
		pCollec c = collec_pool.obtain().init(this, p, ref_in_p);
		if (c.prop != null && !c.prop.mode_nosync) newCollecs.add(c);
		return c; }

	ArrayList<pBody> newBodys = new ArrayList<pBody>();
	ArrayList<pParam> newParams = new ArrayList<pParam>();
	ArrayList<pCollec> newCollecs = new ArrayList<pCollec>();

	ArrayList<pBody> delBodys = new ArrayList<pBody>();
	ArrayList<pParam> delParams = new ArrayList<pParam>();
	ArrayList<pCollec> delCollecs = new ArrayList<pCollec>();

	ArrayList<pBody> chgBodys = new ArrayList<pBody>();
	ArrayList<pParam> chgParams = new ArrayList<pParam>();
	ArrayList<pCollec> chgCollecs = new ArrayList<pCollec>();

	ArrayList<pBody> delayChgBodys = new ArrayList<pBody>();
	ArrayList<pParam> delayChgParams = new ArrayList<pParam>();
	ArrayList<pCollec> delayChgCollecs = new ArrayList<pCollec>();

	public void bloc_change() {
		bloc_change = true;
	}

	public void unbloc_change() {
		bloc_change = false;
	}
	
	public void cancel_change() {
		delBodys.clear(); delParams.clear(); delCollecs.clear(); 
		newBodys.clear(); newParams.clear(); newCollecs.clear(); 
		chgBodys.clear(); chgParams.clear(); chgCollecs.clear(); 
		delayChgBodys.clear(); delayChgParams.clear(); delayChgCollecs.clear(); 
	}
	
	public void signalChange(pBody b) { 
		if (bloc_change) return;
		if (!checking_change && !Utl.has(chgBodys, b)) chgBodys.add(b); 
		if (checking_change && !Utl.has(delayChgBodys, b)) delayChgBodys.add(b); }
	public void signalChange(pParam b) { 
		if (bloc_change) return;
		if (!checking_change && !Utl.has(chgParams, b)) chgParams.add(b); 
		if (checking_change && !Utl.has(delayChgParams, b)) delayChgParams.add(b); }
	public void signalChange(pCollec b) { 
		if (bloc_change) return;
		if (!checking_change && !Utl.has(chgCollecs, b)) chgCollecs.add(b); 
		if (checking_change && !Utl.has(delayChgCollecs, b)) delayChgCollecs.add(b); }

	private boolean bloc_change = false;
	private boolean checking_change = false;
	private void check_change() {

		checking_change = true;

		for (pBody b : newBodys) if (Utl.has(chgBodys, b)) chgBodys.remove(b);
		for (pParam b : newParams) if (Utl.has(chgParams, b)) chgParams.remove(b);
		for (pCollec b : newCollecs) if (Utl.has(chgCollecs, b)) chgCollecs.remove(b);
		
		for (pBody b : delBodys) if (Utl.has(chgBodys, b)) chgBodys.remove(b);
		for (pBody b : delBodys) if (Utl.has(newBodys, b)) newBodys.remove(b);
		for (pParam b : delParams) if (Utl.has(chgParams, b)) chgParams.remove(b);
		for (pParam b : delParams) if (Utl.has(newParams, b)) newParams.remove(b);
		for (pCollec b : delCollecs) if (Utl.has(chgCollecs, b)) chgCollecs.remove(b);
		for (pCollec b : delCollecs) if (Utl.has(newCollecs, b)) newCollecs.remove(b);

		for (pCollec b : delCollecs) nRun.runEvents(eventDelCollec, b);
		for (pParam b : delParams) nRun.runEvents(eventDelParam, b);
		for (pBody b : delBodys) nRun.runEvents(eventDelBody, b);
		delBodys.clear(); delParams.clear(); delCollecs.clear(); 
		for (pCollec b : newCollecs) nRun.runEvents(eventNewCollec, b);
		for (pParam b : newParams) nRun.runEvents(eventNewParam, b);
		for (pBody b : newBodys) nRun.runEvents(eventNewBody, b);
		newBodys.clear(); newParams.clear(); newCollecs.clear(); 
		for (pCollec b : chgCollecs) nRun.runEvents(eventChgCollec, b);
		for (pParam b : chgParams) nRun.runEvents(eventChgParam, b);
		for (pBody b : chgBodys) nRun.runEvents(eventChgBody, b);
		chgBodys.clear(); chgParams.clear(); chgCollecs.clear(); 

		checking_change = false;

		for (pBody b : newBodys) if (Utl.has(delayChgBodys, b)) delayChgBodys.remove(b);
		for (pParam b : newParams) if (Utl.has(delayChgParams, b)) delayChgParams.remove(b);
		for (pCollec b : newCollecs) if (Utl.has(delayChgCollecs, b)) delayChgCollecs.remove(b);
		
		for (pCollec b : delayChgCollecs) nRun.runEvents(eventChgCollec, b);
		for (pParam b : delayChgParams) nRun.runEvents(eventChgParam, b);
		for (pBody b : delayChgBodys) nRun.runEvents(eventChgBody, b);
		
//		for (pBody b : delayChgBodys) chgBodys.add(b);
//		for (pParam b : delayChgParams) chgParams.add(b);
//		for (pCollec b : delayChgCollecs) chgCollecs.add(b);
		
		delayChgBodys.clear(); delayChgParams.clear(); delayChgCollecs.clear(); 
		
//		checking_change = false;
	}
	
	ArrayList<nRun> eventNewBody = new ArrayList<nRun>();
	ArrayList<nRun> eventNewParam = new ArrayList<nRun>();
	ArrayList<nRun> eventNewCollec = new ArrayList<nRun>();
	
	public pSpace addEventNewBody(nRun r) { eventNewBody.add(r); return this; }
	public pSpace removeEventNewBody(nRun r) { eventNewBody.remove(r); return this; }
	public pSpace addEventNewParam(nRun r) { eventNewParam.add(r); return this; }
	public pSpace removeEventNewParam(nRun r) { eventNewParam.remove(r); return this; }
	public pSpace addEventNewCollec(nRun r) { eventNewCollec.add(r); return this; }
	public pSpace removeEventNewCollec(nRun r) { eventNewCollec.remove(r); return this; }

	ArrayList<nRun> eventDelBody = new ArrayList<nRun>();
	ArrayList<nRun> eventDelParam = new ArrayList<nRun>();
	ArrayList<nRun> eventDelCollec = new ArrayList<nRun>();
	
	public pSpace addEventDelBody(nRun r) { eventDelBody.add(r); return this; }
	public pSpace removeEventDelBody(nRun r) { eventDelBody.remove(r); return this; }
	public pSpace addEventDelParam(nRun r) { eventDelParam.add(r); return this; }
	public pSpace removeEventDelParam(nRun r) { eventDelParam.remove(r); return this; }
	public pSpace addEventDelCollec(nRun r) { eventDelCollec.add(r); return this; }
	public pSpace removeEventDelCollec(nRun r) { eventDelCollec.remove(r); return this; }

	ArrayList<nRun> eventChgBody = new ArrayList<nRun>();
	ArrayList<nRun> eventChgParam = new ArrayList<nRun>();
	ArrayList<nRun> eventChgCollec = new ArrayList<nRun>();
	
	public pSpace addEventChgBody(nRun r) { eventChgBody.add(r); return this; }
	public pSpace removeEventChgBody(nRun r) { eventChgBody.remove(r); return this; }
	public pSpace addEventChgParam(nRun r) { eventChgParam.add(r); return this; }
	public pSpace removeEventChgParam(nRun r) { eventChgParam.remove(r); return this; }
	public pSpace addEventChgCollec(nRun r) { eventChgCollec.add(r); return this; }
	public pSpace removeEventChgCollec(nRun r) { eventChgCollec.remove(r); return this; }
	
	
	public void test_param_use(sPool<pParam> pl) {
		pProperty prop = null;
		for (Map.Entry<String, sPool<pParam>> me : param_pools.entrySet()) 
			if (me.getValue() == pl) prop = pProperty.get(me.getKey());
		if (prop != null && !prop.mode_common) {
			ArrayList<pParam> tmp = new ArrayList<pParam>();
			for (pParam p : pl.all_used.all()) 
				if (p.users.size() == 0) tmp.add(p);
			for (pParam p : tmp) p.clear();
		}
	}
	
	public void clear_all_body() {
		body_pool.freeAll(); 
		for (sPool<pParam> p : param_pools.all()) test_param_use(p);
//		for (sPool<pParam> p : param_pools.all()) p.freeAll(); 
//		collec_pool.freeAll(); 
		families_updated = true;
		update_families();
	}

	public void clear_all_obj() {
		body_pool.freeAll(); 
		for (sPool<pParam> p : param_pools.all()) p.freeAll(); 
		collec_pool.freeAll(); 
		families_updated = true;
		update_families();
//		nRun.runEvents(eventSpaceClear);
	}

	public nMap<sPool<pParam>> param_pools = new nMap<sPool<pParam>>();

	public pParam getParam(String prop, String par) {
		return param_pools.get(prop).get(par); }
	
	public HashMap<pFamily, ArrayList<pBody>> families = 
			new HashMap<pFamily, ArrayList<pBody>>();
	
	public pFamily getFamily(String r) { return pFamily.getFamily(r); }
	public boolean familyContains(String r, pBody b) { 
		if (pFamily.getFamily(r) != null) return pFamily.getFamily(r).contains(b); 
		else return false; }

	public ArrayList<pBody> familyMember(String r) {
		if (pFamily.getFamily(r) != null)
		return families.get(pFamily.getFamily(r)); 
		else return new ArrayList<pBody>(); }
	

	public boolean families_updated = false;
	public void update_families() {
		if (families_updated) {
			for (Map.Entry<pFamily, ArrayList<pBody>> me : families.entrySet()) {
				pFamily fam = me.getKey();
				ArrayList<pBody> list = me.getValue();
				list.clear();
				for (pBody b : body_pool.all()) if (fam.contains(b)) list.add(b);
			}
			families_updated = false;
		}
	}
	
	
	
	pSpace() {
		super();
//		frame_run = new nRun() { public void run(Object o) { frame((float)o); }};
//		net_frame_run = new nRun() { public void run(Object o) { net_frame((float)o); }};
		tick_run = new nRun() { public void run(Object o) { tick((float)o); }};
		net_tick_run = new nRun() { public void run(Object o) { net_tick((float)o); }};
		draw_run = new nDrawable() { public void drawing() { draw(); }}; 
	}
	public pSpace init(sValueBloc b) { return (pSpace) super.init(b); }

//	nRun frame_run;
//	nRun net_frame_run; 
	nRun tick_run, net_tick_run;
	nDrawable draw_run;
	
	public Random seed_rng;

	sTab val_body_tab, val_collec_tab, val_param_database;
	sInt val_body_nb, val_body_pool, 
		val_param_nb, val_param_pool, 
		val_collec_nb, val_collec_pool;
	
	sValueBloc space_content_bloc;
	
	public void system_init() {
		bloc.addObject("space", this);
		
		plane.storeSystemType(bloc.ref, this.getClass());

		useNetFrame();
		
		plane.getSystem(pTime.class).addEventTick(tick_run);
		plane.getSystem(pTime.class).addEventNetTick(net_tick_run);
		plane.getSystem(pView.class).addDrawable(draw_run);

//		plane.addEventSave(new nRun() { public void run() {
//			save_contents(); 
//		}});
//
//		plane.addEventEmpty(new nRun() { public void run() {
//			clear_all_obj(); }});
//
//		plane.addEventLoad(new nRun() { public void run() {
//			app.log("space "+bloc.ref+" load_contents");
//			load_contents(); 
//		}});

		seed_rng = new Random(123456789);
		
		val_param_database = bloc.obtainTab("val_param_database");
		bloc.data.databases.put("space_param", val_param_database);
		build_param_database(val_param_database);
		
//		app.addDelayEvent(2, new nRun() { public void run() {			
//			bloc.run("add_menu"); 
//
//			sValueBloc menu_bloc = bloc.getBloc("blocmenu");
//			sInt val_tab_sel = menu_bloc.getValue("val_tab_sel", sInt.class);
//			val_tab_sel.set(1);
//			sBoo val_collapse = menu_bloc.getValue("val_collapse", sBoo.class);
//			val_collapse.set(true);
//		
//		}});

		nRun run_clear_body = new nRun() { public void run() {
			clear_all_body(); }};
		bloc.addMetode("clear_body", run_clear_body, "view_in_menu");

		nRun run_clear_all = new nRun() { public void run() {
			clear_all_obj(); }};
		bloc.addMetode("clear_all", run_clear_all, "view_in_menu");
		
		for (pFamily fam : pFamily.body_families.all()) {
			ArrayList<pBody> list = new ArrayList<pBody>();
			families.put(fam, list);
		}
		
		space_content_bloc = app.data.root_bloc.obtainBloc("space_"+bloc.ref+"_content");
		val_body_tab = space_content_bloc.obtainTab("val_body_tab");
		val_collec_tab = space_content_bloc.obtainTab("val_collec_tab");

		val_body_nb = bloc.obtainInt("val_body_nb");
		val_body_pool = bloc.obtainInt("val_body_pool");
		val_param_nb = bloc.obtainInt("val_param_nb");
		val_param_pool = bloc.obtainInt("val_param_pool");
		val_collec_nb = bloc.obtainInt("val_collec_nb");
		val_collec_pool = bloc.obtainInt("val_collec_pool");
		
		if (!app.getPref("RELEASE", Boolean.class)) app.menu.add_info_text("body nb:", val_body_nb); 
		
		pSpace space = this;
		body_pool = new sPool<pBody>(app, val_body_tab, "body") {
			public pBody newObject() { return new pBody().init(space); }
			public pBody[] newArray(int i) { return new pBody[i]; } };
		collec_pool = new sPool<pCollec>(app, val_collec_tab, "collec") {
			public pCollec newObject() { return new pCollec(); }
			public pCollec[] newArray(int i) { return new pCollec[i]; } };
		
		for (pProperty prop : pProperty.body_propertys.all()) {
			if (prop.mode_runtime) {
				sPool<pParam> pool = new sPool<pParam>(app, null, prop.ref) {
					public pParam newObject() { return new pParam().init(space, prop); }
					public pParam[] newArray(int i) { return new pParam[i]; } };
				param_pools.put(prop.ref, pool);
			} else {
				sTab val_prop_tab = space_content_bloc.obtainTab("val_prop_"+prop.ref);
				sPool<pParam> pool = new sPool<pParam>(app, val_prop_tab, prop.ref) {
					public pParam newObject() { return new pParam().init(space, prop); }
					public pParam[] newArray(int i) { return new pParam[i]; } };
				param_pools.put(prop.ref, pool);
			}
		}
		
	}
	
	public void system_load() {
		
		space_starting = true;
		pTime time = plane.getSystem(pTime.class);
		app.addDelayEvent(40, new nRun() { public void run() {
			time.val_pause.set(true);
			nRun.runEvents(eventSpaceStart);
			app.addDelayEvent(1, new nRun() { public void run() {
				time.val_tick_cnt.set(0);
				time.val_pause.set(false);
				space_starting = false; }}); 
		}}); 

//		plane.getSystem(pView.class).add_toolbar_trigg("ClrBod", new nRun() { public void run() {
//			clear_all_body(); }});

//		plane.getSystem(pView.class).add_toolbar_trigg("Empt", new nRun() { public void run() {
//			clear_all_obj(); }});
		
//		load_contents();
		
		if (!app.getPref("RELEASE", Boolean.class)) tool_setup(true);
		
//		if (app.AUTO_BUILD) {
//			sValueBloc bloc2 = bloc.getBloc("bodyview");
//			if (bloc2 == null) bloc.buildBloc("bodyview", "bodyview"); 
//		}	
	}
	
	public void system_clear() {

		for (pFamily fam : pFamily.body_families.all()) {
			families.remove(fam);
		}
		families.clear();
		
		for (sPool<pParam> p : param_pools.all()) {
			p.clear();
		}
		param_pools.clear();
		collec_pool.clear();
		body_pool.clear();
		
		systems.clear();
		
//		plane.removeEventFrame(frame_run);
		if (plane.getSystem(pTime.class) != null) 
			plane.getSystem(pTime.class).removeEventTick(tick_run);
		if (plane.getSystem(pTime.class) != null) 
			plane.getSystem(pTime.class).removeEventNetTick(net_tick_run);
		if (plane.getSystem(pView.class) != null) 
			plane.getSystem(pView.class).removeDrawable(draw_run);
		
		bloc.clear();
		bloc = null;
		plane = null;
	}
	
	public void tool_init(nInterface interf) {
		interf.setContext(bloc);

		interf.add_row();
		interf.add_row_watch(5, "Body : ", "val_body_nb");
		interf.add_row_watch(5, " / ", "val_body_pool");
		interf.add_row();
		interf.add_row_watch(5, "Param : ", "val_param_nb");
		interf.add_row_watch(5, " / ", "val_param_pool");
		interf.add_row();
		interf.add_row_watch(5, "Collec : ", "val_collec_nb");
		interf.add_row_watch(5, " / ", "val_collec_pool");
		interf.add_row();
		interf.add_row_label(10, "");
		interf.add_row();
		interf.add_row_label(1, "");
		interf.add_row_trigg(8, "Restart Space", new nRun() { public void run() {
			start_space(); }});
		interf.add_row_label(1, "");
		interf.add_row();
		interf.add_row_label(10, "");
		interf.add_row();
		interf.add_row_label(1, "");
		interf.add_row_trigg(8, "Clear all bodys", new nRun() { public void run() {
			clear_all_body(); }});
		interf.add_row_label(1, "");
		
		
		
	}

	public void frame(float delta) {
		
//		for(pProperty p : pProperty.general_propertys.all()) {
//			
//		}
		
//		if (plane.getSystem(pView.class).mouse_is_hover_view() && app.input.mouseLeft.trigClick) 
//			bloc.select_bloc();
		
		for (sPool<pParam> p : param_pools.all()) test_param_use(p);

		val_body_nb.set(body_pool.size());
		val_body_pool.set(body_pool.capacity());
		val_collec_nb.set(collec_pool.size());
		val_collec_pool.set(collec_pool.capacity());
		int param_cnt = 0, capa_cnt = 0;
		for (sPool<pParam> prop : param_pools.all()) {
			param_cnt += prop.size();
			capa_cnt += prop.capacity();
		}
		val_param_nb.set(param_cnt);
		val_param_pool.set(capa_cnt);

		update_families();
	}
	public void tick(float delta) {
		update_families();
		
		check_change();
	}

	public void net_frame(float delta) {
		
////		if (plane.getSystem(pView.class).mouse_is_hover_view() && app.input.mouseLeft.trigClick) 
////			bloc.select_bloc();
		
//		for (sPool<pParam> p : param_pools.all()) test_param_use(p);
		
		val_body_nb.set(body_pool.size());
		val_body_pool.set(body_pool.capacity());
		val_collec_nb.set(collec_pool.size());
		val_collec_pool.set(collec_pool.capacity());
		int param_cnt = 0, capa_cnt = 0;
		for (sPool<pParam> prop : param_pools.all()) {
			param_cnt += prop.size();
			capa_cnt += prop.capacity();
		}
		val_param_nb.set(param_cnt);
		val_param_pool.set(capa_cnt);
		
		update_families();
	}
	public void net_tick(float delta) {
		update_families();

		check_change();
	}
	

	public void draw() {
		
	}
	
	public void save_contents() {

		space_content_bloc = app.data.root_bloc.obtainBloc("space_"+bloc.ref+"_content");
		val_body_tab = space_content_bloc.obtainTab("val_body_tab");
		val_collec_tab = space_content_bloc.obtainTab("val_collec_tab");
		body_pool.tab = val_body_tab;
		collec_pool.tab = val_collec_tab;
		for (pProperty prop : pProperty.body_propertys.all()) 
				if (!prop.mode_runtime) {
			sTab val_prop_tab = space_content_bloc.obtainTab("val_prop_"+prop.ref);
			param_pools.get(prop.ref).tab = val_prop_tab;
		}
		
		collec_pool.save();
		for (sPool<pParam> p : param_pools.all()) p.save(); 
		body_pool.save();
	}
	
//	public pParam tmptmp = null;
	
	public void load_contents() {
		collec_pool.freeAll();
		for (sPool<pParam> p : param_pools.all()) p.freeAll();
		body_pool.freeAll();
		
		space_content_bloc = app.data.root_bloc.obtainBloc("space_"+bloc.ref+"_content");
		val_body_tab = space_content_bloc.obtainTab("val_body_tab");
		val_collec_tab = space_content_bloc.obtainTab("val_collec_tab");
		body_pool.tab = val_body_tab;
		collec_pool.tab = val_collec_tab;
		for (pProperty prop : pProperty.body_propertys.all()) 
				if (!prop.mode_runtime) {
			sTab val_prop_tab = space_content_bloc.obtainTab("val_prop_"+prop.ref);
			param_pools.get(prop.ref).tab = val_prop_tab;
		}
		
		collec_pool.load();
		for (sPool<pParam> p : param_pools.all()) p.load();
		body_pool.load();
		
		for (pBody b : body_pool.all()) { 
			plane.getSystem(pGeom.class).init_body(b); }
		
		app.addDelayEvent(1,new nRun() { public void run() {
//			for (pBody b : body_pool.all()) {
//				plane.getSystem(pGeom.class).init_body(b);
//				plane.getSystem(pFlux.class).init_body(b);
////				if (b.hasParam("selectable") && b.getBoo("selectable","selected")) {
////					plane.getSystem(pGraph.class).select_body(b); } 
//			}
		}});
	}
	
	
}
