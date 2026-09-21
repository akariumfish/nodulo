package patch;

import data.*;
import gui.*;
import util.*;
import aa_nodulo.*;
import aa_nodulo.pProperty.Ctrl;
import app.*;
import patch.pMacro.Macro;
import patch.pNode.CT;

import java.util.ArrayList;
import java.util.Map;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class pNodeSpace {
	
	
	
	public static void build_sheet(sData data, boolean has_build_statics) {
		
		float RS = nGUI.book.RS;

		pSheet.SheetModel blueprint_model = pSheet.newSheet(data, "blueprint", false);
		if (!has_build_statics) {
			blueprint_model.getBuilder()
			.addEventLoad(new nRun() { public void run(Object o) {
				sValueBloc b = (sValueBloc)o; 
				pSheet sheet = b.object("sheet", pSheet.class);
				nRun run_frame = new nRun(sheet) {public void run() { 
					pSheet sheet = (pSheet)builder;
					pSpace space = PlaneApplet.app.space;
	//				for (pProperty prop : pProperty.body_propertys.all()) 
	//						if (prop.mode_common) {
	//					sPool<pParam> pool = space.param_pools.get(prop.ref);
	//					if (pool != null) for (pParam par : Utl.duplic(pool.all())) {
	//						boolean found = false;
	//						ArrayList<pInstance> toclr = new ArrayList<pInstance>();
	//						for (pInstance n : sheet.nodes) {
	//							if (pNode.node_group.get(pNode.stand_to_ref.get(n.stand))
	//									.equals("prop") && n.hasObject("param") && 
	//									 n.object("param", pParam.class) == par) {
	//								if (found) toclr.add(n);
	//								found = true;
	//							}
	//						}
	//						for (pInstance n : toclr) n.clear();
	//						if (!found) {
	//							pInstance n = sheet.newNode(prop.ref);
	//							n.run("defParam", par.pool_ref);
	//							n.run("find_place");
	//						}
	//					}
	//				}
	//				int prop_cnt = 0;
	//				float st_y = 0;
	//				for (pProperty prop : pProperty.body_propertys.all()) 
	//						if (prop.mode_common) {
	//					sPool<pParam> pool = space.param_pools.get(prop.ref);
	//					if (pool != null) {
	//						ArrayList<pInstance> member = new ArrayList<pInstance>();
	//						for (pParam par : Utl.duplic(pool.all())) {
	//							for (pInstance n : sheet.nodes) {
	//								if (pNode.node_group.get(pNode.stand_to_ref.get(n.stand))
	//										.equals("prop") && n.hasObject("param") && 
	//										 n.object("param", pParam.class) == par) {
	//									member.add(n);
	//								}
	//							}
	//						}
	//						int row_l = 3;
	//						float max_sx = 0;
	//						float max_sy = 0;
	//						for (pInstance n : member) {
	//							nWidgetGroup group = n.object("group", nWidgetGroup.class);
	//							float sx = group.get("selline").getParentRect().width;
	//							float sy = group.get("selline").getParentRect().height;
	//							if (sx > max_sx) max_sx = sx; 
	//							if (sy > max_sy) max_sy = sy; }
	//						max_sx += RS; max_sy += RS;
	//						int mem_cnt = 0;
	//						float tot_sx = max_sx * row_l;
	//						if (member.size() < row_l) tot_sx = max_sx * member.size();
	//						for (pInstance n : member) {
	//							n.run("go_to", mem_cnt * max_sx - tot_sx / 2f, st_y); 
	//							mem_cnt++; 
	//							if (mem_cnt >= row_l) {
	//								mem_cnt = 0; st_y -= max_sy; }
	//						}
	//						st_y -= max_sy;
	////						if (member.size() > 0) prop_cnt++;
	//					}
	//				}
				}};
				b.addObject("run_frame", run_frame);
				App.ap.addDelayEvent(30, new nRun() { public void run() {
					sheet.patch.addEventFrame(run_frame);
				}});
			}})
			.addEventClear(new nRun() { public void run(Object o) {
				sValueBloc b = (sValueBloc)o; 
				pSheet sheet = b.object("sheet", pSheet.class);
				sheet.patch.removeEventFrame(b.object("run_frame", nRun.class));
			}})
			;
			
			build_blueprint();
			
			
		}
		
		
	}
	

	public static void build_blueprint() {
		
		

		nRun blueprint_prop_run = new nRun() {public void run() {
			pStandard stand = arg(0,pStandard.class);
			if (stand == null) return;
			stand.openSec()
				.param("keys", new String[] {"param", "in"}) 
				.param("filters", new String[] {"param", "out"})
				.run(pNode.getRun(CT.RUNS_ADD_CO_IN), "param_in")
			.closeSec()
			;
			
			pProcess pr = stand.process();
			pr.commande(new nRun() {public void run() {
				nRun run_frame = new nRun(instance) {public void run() { 
					pInstance inst = (pInstance)builder;
					pParam par = inst.object("param", pParam.class);
					if (par == null) return;
					par.collecEmpty("prop_ref");
					par.collecEmpty("param_ref");
					pInstance co = inst.get("get_co", pInstance.class, "param_in");
					Object op = co.get("obtain_all", Object.class);
					if (op == null) return;
					ArrayList<Object> prov = (ArrayList)op;
					for (Object o : prov) if (o instanceof pParam) {
						pParam p = (pParam)o;
						if (p != null) {
							par.collecAdd("prop_ref", p.prop.ref);
							par.collecAdd("param_ref", p.pool_ref);
						}
					}
					par.setDt("name", inst.getVar("name", String.class)); 
				}};
				instance.addObject("run_frame", run_frame);
				instance.patch.addEventFrame(run_frame);
			}})
			.useClear().commande(new nRun() {public void run() { 
				instance.patch.removeEventFrame(
						instance.object("run_frame", nRun.class));
			}}).useInit()
			;
			
			pr.commande(pNode.getCom(CT.COM_ADD_ROW))
			.openSec().param("def", "blueprint").run(pNode.getRun(CT.RUNP_VAR_STR_LAB_FIELD), 
					"name", "print name:", (int)8).closeSec()
			.commande(pNode.getCom(CT.COM_ADD_ROW))
			;
		}};
		
		pProperty bp_prop = pProperty.newGeneralProperty("blueprint")
//		.setCommon()
		.addData("name", "")
		.addCollec("prop_ref", String.class)
		.addCollec("param_ref", String.class)
		.addNodeRun(blueprint_prop_run)
		;
		
	}
		
	
	

	public static pBody new_body(pParam bluep) {
		if (bluep == null) return null;
		pSpace space = bluep.space;
		
		pBody pop = space.new_body();

		ArrayList<String> prop_refs = 
				bluep.getCollecData("prop_ref", String.class);
		ArrayList<String> param_refs = 
				bluep.getCollecData("param_ref", String.class);
		if (prop_refs.size() != param_refs.size()) {
			pop.clear(); return null; }
		
		for (int i = 0 ; i < prop_refs.size() ; i++) {
			String prop_ref = prop_refs.get(i), par_ref = param_refs.get(i);
			pProperty gene_prop = pProperty.get(prop_ref);
			if (gene_prop == null || bluep.space.param_pools.get(prop_ref) == null) continue;
			pParam gene_par = bluep.space.param_pools.get(prop_ref).get(par_ref); 
			if (gene_par == null) continue;
			String gene_ref = prop_ref;
			int cnt = 1;
			while (pop.hasParam(gene_ref)) { gene_ref = prop_ref + "_" + cnt; cnt++; }
			pop.addParam(gene_ref, gene_par);
			
			for (pProperty need : gene_prop.need_props) {
				String new_ref = need.ref;
				int cnt2 = 1;
				while (pop.hasParam(new_ref)) { 
					new_ref = need.ref + "_" + cnt2; cnt2++; }
				pParam np = pop.newParam(need, new_ref);
//				new_ref = pop.getRef(np);
//				for (Map.Entry<Class<?>, nMap<Integer>> me : need.data_vals.entrySet()) {
//					if (me.getValue() != null) {
//						for (Map.Entry<String,Integer> map_me : me.getValue().entrySet()) {
//							String rf = "def_"+need.ref+"_"+map_me.getKey();
//							pop.param(new_ref).set(rf, gene_par.get(rf));
//						}		
//					}
//				}
			}
			for (pProperty opt : gene_prop.option_props) 
					if (gene_par.getDt("use_"+opt.ref, Boolean.class)) {
				String new_ref = opt.ref;
				int cnt2 = 1;
				while (pop.hasParam(new_ref)) { 
					new_ref = opt.ref + "_" + cnt2; cnt2++; }
				pParam np = pop.newParam(opt, new_ref);
//				new_ref = pop.getRef(np);
//				for (Map.Entry<Class<?>, nMap<Integer>> me : opt.data_vals.entrySet()) {
//					if (me.getValue() != null) {
//						for (Map.Entry<String,Integer> map_me : me.getValue().entrySet()) {
//							String rf = "def_"+opt.ref+"_"+map_me.getKey();
//							pop.param(new_ref).set(rf, gene_par.get(rf));
//						}		
//					}
//				}
				
			}
		}
		
		return pop;
	}

	public static pBody init_body(pBody pop, pParam bluep) {
		if (bluep == null) return null;
		pSpace space = bluep.space;
		if (space == null) return null;
		
		ArrayList<String> prop_refs = 
				bluep.getCollecData("prop_ref", String.class);
		ArrayList<String> param_refs = 
				bluep.getCollecData("param_ref", String.class);
		if (prop_refs.size() != param_refs.size()) {
			pop.clear(); return null; }
		
		for (int i = 0 ; i < prop_refs.size() ; i++) {
			String prop_ref = prop_refs.get(i);
			pProperty gene_prop = pProperty.get(prop_ref);
			if (gene_prop == null || bluep.space.param_pools.get(prop_ref) == null) continue;
			for (nRun n : gene_prop.body_init_runs) n.do_run(pop);
		}
		
		pop.update_families();
		space.update_families();
		
		return pop;
	}

	public static pBody init_body(pBody pop) {
		if (pop == null) return null;
		pSpace space = pop.space;
		if (space == null) return null;
		
		for (String prop_ref : pop.params.allKey()) {
			pProperty gene_prop = pProperty.get(prop_ref);
			if (gene_prop == null || !gene_prop.is_general) continue;
			for (nRun n : gene_prop.body_init_runs) n.do_run(pop);
		}
		
		pop.update_families();
		space.update_families();
		
		return pop;
	}
	
	

	
	
	public static void build_nodes() {

		float RS = nGUI.book.RS;

		build_param_chain_nodes();
		
		build_sel_body_node();

		build_constructor_nodes();
		
		build_actor_node();
		

		pNode.newNodeModel("space_init", false)
		.process()
		.useLoad().commande(new nRun() { public void run() {
//			nRun run_space_setup = new nRun(instance) {public void run() { 
//				pInstance inst = (pInstance)builder;
//				pInstance co = inst.get("get_co", pInstance.class, "start_run");
//				if (inst.getVar("setup", Boolean.class)) co.run("send");
//			}};
//			instance.addObject("run_space_setup", run_space_setup);
//			PlaneApplet.app.space.addEventSpaceSetup(run_space_setup);
			nRun run_space_start = new nRun(instance) {public void run() { 
				pInstance inst = (pInstance)builder;
				pInstance co = inst.get("get_co", pInstance.class, "start_run");
//				if (!inst.getVar("setup", Boolean.class)) 
					co.run("send");
			}};
			instance.addObject("run_space_start", run_space_start);
			PlaneApplet.app.space.addEventSpaceStart(run_space_start);
		}})
		.useClear().commande(new nRun() { public void run() {
			PlaneApplet.app.space.removeEventSpaceStart(
					instance.object("run_space_start", nRun.class));
//			PlaneApplet.app.space.removeEventSpaceSetup(
//					instance.object("run_space_setup", nRun.class));
		}}).useInit()
//		.openSec()
//			.param("def", false, "height", 1f) 
//			.run(pNode.getRun(pNode.CT.RUNP_VAR_BOO_SWITCH), 
//					"setup", "setup", (int)6)
//		.closeSec()
		.openSec()
		.run(pNode.getRun(CT.RUNP_ADD_LABEL), " > ", (int)10)
		.closeSec()
		.getStand()
		.openSec()
			.param("keys", new String[] {"bang"}, "filters", new String[] {}) 
			.run(pNode.getRun(CT.RUNS_ADD_CO_OUT), "start_run")
		.closeSec()
		;
		
	}

	
	public static void build_constructor_nodes() {

		float RS = nGUI.book.RS;
	
		
		
		
		
//		pStandard constructor_abstract = pStandard.newStandard("constructor_abstract", "inst");
//		constructor_abstract.newRun("new_body", new nRun() {public Object get() {
//			
//			pSpace space = PlaneApplet.app.space;
//			pParam bluep = null;
//			
//			String print_name = instance.getVar("print_name", String.class);
//			if (print_name != null) {
//				for (String br : space.param_pools.get("blueprint").allKey()) {
//					String nm = space.param_pools.get("blueprint")
//							.get(br).get("name", String.class);
//					if (nm != null && nm.equals(print_name)) {
//						instance.setVar("print_ref", br);
//						bluep = space.param_pools.get("blueprint")
//								.get(br);
//						break;
//					}
//				}
//			}
//			
//			if (bluep == null && 
//					instance.get("get_co", pInstance.class, "bluePrnt") != null) {
//				pInstance co = instance.get("get_co", pInstance.class, "bluePrnt");
//				Object op = co.get("obtain_all", Object.class);
//				if (op == null) return null;
//				ArrayList<Object> prov = (ArrayList)op;
//				for (Object o : prov) if (o instanceof pParam) {
//					pParam p = (pParam)o;
//					if (p != null && p.prop.ref.equals("blueprint")) { 
//						bluep = p;
//						instance.setVar("print_name", bluep.get("name", String.class)); 
//						break; } }
//			}
//			
//			if (bluep != null) {
//				
//				pBody pop = new_body(bluep);
//				
//				pInstance co_ank = instance.get("get_co", pInstance.class, "co_ank");
//				if (co_ank != null) {
//					Object op_ank = co_ank.get("obtain_all", Object.class);
//					if (op_ank != null) { 
//						ArrayList<Object> prov_ank = (ArrayList)op_ank;
//						for (Object o : prov_ank) if (o instanceof pInstance) {
//							pInstance ank = (pInstance)o;
//							if (ank != null) {
//								instance.setObject("ank", ank);
//							}
//						}
//					}
//					pInstance ank = instance.object("ank", pInstance.class);
//					if (ank != null && pop.hasParam("ref")) pop.setVec("ref", "pos", 
//							ank.getVar("ank_pos", Vector2.class));
//					if (ank != null && pop.hasParam("coord")) pop.setVec("coord", "pos", 
//							ank.getVar("ank_pos", Vector2.class));
//				} 
//				if (instance.hasVar("pop_pos")) {
//					if (pop.hasParam("ref")) pop.addVec("ref", "pos", 
//							instance.getVar("pop_pos", Vector2.class));
//					if (pop.hasParam("coord")) pop.addVec("coord", "pos", 
//							instance.getVar("pop_pos", Vector2.class));
//				}
//				if (instance.hasVar("pop_size")) {
//					if (pop.hasParam("ref")) pop.setFlt("ref", "scale", 
//							instance.getVar("pop_size", Float.class));
//				}
//				
//				
//				init_body(pop, bluep);
//				
//				instance.setObject("new_bod", pop);
//				pInstance co_out = instance.get("get_co", pInstance.class, "co_body");
//				if (co_out != null) co_out.run("send", pop);
//				
//				return pop;
//			}
//			return null;
//		}});
//
//		
//
//		pStandard stand_constructor = pNode.newNodeModel("constructor", "body");
//		stand_constructor.append(constructor_abstract);
//		stand_constructor.openSec()
//			.param("event_receive", new nRun() {public void run() {
//				pInstance node = instance.object("node", pInstance.class);
//				node.get("new_body"); }})
//			.param("keys", new String[] {"bang"}, "filters", new String[] {}) 
//			.run(pNode.getRun(CT.RUNS_ADD_CO_IN), "co_run")
//		.closeSec();
//
//		pProcess pr = stand_constructor.process();
//		pr.openSec()
//			.param("run", new nRun() {public void run() {
//				instance.get("new_body"); }}, "height", 2f)
//			.run(pNode.getRun(CT.RUNP_ADD_TRIGG), "new", "NEW", (int)8)
//		.closeSec()
//		.commande(pNode.getCom(CT.COM_ADD_ROW))
//		.openSec()
//			.param("text", "name: ", "width", (int)8)
//			.run(pNode.getRun(CT.RUNP_VAR_STR_LAB_FIELD), "print_name")
//		.closeSec()
//		.openSec()
//			.param("run", new nRun() {public void run() {
//				nWidget trigg_w = instance.get("get_mapped_widget", nWidget.class, 
//						"trigg_dropm_print");
//				if (trigg_w == null) return;
//				pSpace space = PlaneApplet.app.space;
//				for (String br : space.param_pools.get("blueprint").allKey()) {
//					String nm = space.param_pools.get("blueprint")
//							.get(br).get("name", String.class);
//					nGUI.add_dropmenu_entry(nm, new nRun(instance) { public void run() {
//							((pInstance)builder).setVar("print_name", nm); }}); }
//				nGUI.open_dropmenu(trigg_w);
//			}})
//			.run(pNode.getRun(CT.RUNP_ADD_TRIGG), "trigg_dropm_print", "Pk", (int)2)
//		.closeSec()
//		.commande(pNode.getCom(CT.COM_ADD_ROW))
//		;
//		
//		stand_constructor.openSec()
////			.param("text", "prop", "width", (int)4)
//			.param("keys", new String[] {"param", "in"}, 
//					"filters", new String[] {"param", "out"}) 
//			.run(pNode.getRun(CT.RUNS_ADD_CO_IN), "bluePrnt")
//		.closeSec();
//		
//		stand_constructor.openSec()
////			.param("text", "ank", "width", (int)4)
//			.param("keys", new String[] {"ank"}, 
//					"filters", new String[] {"ank"}) 
//			.run(pNode.getRun(CT.RUNS_ADD_CO_IN), "co_ank")
//		.closeSec();
//		
//		stand_constructor.openSec()
//			.param("offer", new nRun() {public Object get() {
//				pInstance node = instance.object("node", pInstance.class);
//				if (node.hasObject("new_bod"))
//					return node.object("new_bod", pBody.class);
//				else return null;
//			}})
//			.param("keys", new String[] {"body"}, "filters", new String[] {"body"}) 
//			.run(pNode.getRun(CT.RUNS_ADD_CO_OUT), "co_body")
//		.closeSec()
//		;

		
		
		
		
		
		
		

		for (Map.Entry<String,pProperty> m : pProperty.general_propertys.entrySet()) { 
			String r = m.getKey();
			pProperty prop = m.getValue();
//			if (!prop.mode_common) continue;
			pStandard stand = build_prop_node(r,prop);
			pProcess proc = stand.process();
			
			for (pProperty.Ctrl c : prop.controls.all()) {
				build_prop_ctrl(prop, proc, c.ref, c.clazz, c.args);
			}
			
			for (nRun rn : prop.node_run) {
				stand.openSec().run(rn).closeSec();
			}
		}
		

		
		
		
	}
	
	private static void build_prop_ctrl(pProperty prop, pProcess proc, 
			String ref, Class<?> clazz, Object ... args) {
		proc.commande(pNode.getCom(CT.COM_ADD_ROW));
		if (clazz == Boolean.class) {
			proc.openSec()
				.param("text", ref, "width", (int)8) 
				.run(pNode.getRun(pNode.CT.RUNP_VAR_BOO_SWITCH), ref)
			.closeSec();
		} else if (clazz == Integer.class) {
			proc.openSec()
//				.param("def", (int)1, "min", 1f, "max", 60f, "granulo", 1f)
				.run(pNode.getRun(pNode.CT.RUNP_VAR_INT_LAB_FIELD), ref, ref, (int)8)
			.closeSec();
		} else if (clazz == Float.class) {
			proc.openSec()
//				.param("def", (int)1, "min", 1f, "max", 60f, "granulo", 1f)
				.run(pNode.getRun(pNode.CT.RUNP_VAR_FLT_LAB_FIELD), ref, ref, (int)8)
			.closeSec();
		} else if (clazz == String.class) {
			proc.openSec()
//				.param("def", (int)1, "min", 1f, "max", 60f, "granulo", 1f)
				.run(pNode.getRun(pNode.CT.RUNP_VAR_STR_LAB_FIELD), ref, ref, (int)8)
			.closeSec();
		} else if (clazz == Vector2.class) {
			proc.openSec()
//				.param("def", (int)1, "min", 1f, "max", 60f, "granulo", 1f)
				.run(pNode.getRun(pNode.CT.RUNP_VAR_VEC_LAB_FIELD), ref, ref, (int)8)
			.closeSec();
		} 
	}
	
	

	private static pStandard build_prop_node(String r, pProperty prop) {

		float RS = nGUI.book.RS;
		
		pStandard stand = pNode.newNodeModel(r, "prop");
		stand.newRun("defParam", new nRun() {public void run() { 
			String par_ref = arg(0,String.class);
			if (par_ref == null) return; 
			pParam par = PlaneApplet.app.space.getParam(prop.ref, par_ref);
			if (par == null) return; 
			instance.setObject("param", par);
			instance.setVar("param_used", par.pool_ref);
			for (int i = 0 ; i < Utl.data_type_nb ; i++) {
				nMap<Integer> map = prop.data_vals.get(Utl.data_type[i]);
				if (map != null) for (Map.Entry<String, Integer> mr : map.entrySet()) {
					String dt_ref = mr.getKey();
					Object dt = par.getDt(dt_ref, Utl.data_type[i]);
					instance.setVar(dt_ref, dt);
				}
			}
		}});
		stand.process().commande(new nRun() {public void run() {
			instance.addObject("prop", prop);
			if (!instance.hasVar("param_used")) { instance.addVar("param_used", ""); } 
			pSpace space = PlaneApplet.app.space;
			pParam par = space.getParam(prop.ref, instance.getVar("param_used", String.class));
			if (par == null && !space.client_space) {
				par = space.new_param(prop.ref);
				instance.setVar("param_used", par.pool_ref); 
			} 
			if (par != null) {
				instance.addObject("param", par); }
			for (int i = 0 ; i < Utl.data_type_nb ; i++) {
				nMap<Integer> map = prop.data_vals.get(Utl.data_type[i]);
				if (map != null) for (Map.Entry<String, Integer> mr : map.entrySet()) {
					String dt_ref = mr.getKey();
					Object dt_def = prop.getDataValDef(dt_ref, Utl.data_type[i]);
					if (dt_def == null) dt_def = Utl.new_object(Utl.data_type[i]);
					if (!instance.hasVar(dt_ref)) instance.addVar(dt_ref, dt_def);
				}
			}
			nRun run_frame = new nRun(instance) {public void run() { 
				pInstance inst = (pInstance)builder;
				pParam par = inst.object("param", pParam.class);
				if (par == null) return;
				for (int i = 0 ; i < Utl.data_type_nb ; i++) {
					nMap<Integer> map = prop.data_vals.get(Utl.data_type[i]);
					if (map != null) for (Map.Entry<String, Integer> mr : map.entrySet()) {
						String dt_ref = mr.getKey();
						par.setDt(dt_ref, inst.getVar(dt_ref, Utl.data_type[i]));
					}
				}
			}};
			instance.addObject("run_frame", run_frame);
			instance.patch.addEventFrame(run_frame);
		}})
		.useLoad().commande(new nRun() {public void run() { 
			App.ap.addDelayEvent(1, new nRun(instance) { public void run() {
				pInstance inst = (pInstance)builder;
				pSpace space = PlaneApplet.app.space;
				pParam par = space.getParam(prop.ref, inst.getVar("param_used", String.class));
				if (par == null && !space.client_space) {
					par = space.new_param(prop.ref);
					inst.run("defParam", par.pool_ref);
					
					for (int i = 0 ; i < Utl.data_type_nb ; i++) {
						nMap<Integer> map = prop.data_vals.get(Utl.data_type[i]);
						if (map != null) for (Map.Entry<String, Integer> mr : map.entrySet()) {
							String dt_ref = mr.getKey();
							par.setDt(dt_ref, inst.getVar(dt_ref, Utl.data_type[i]));
						}
					}
					
//					inst.setVar("param_used", par.pool_ref); 
				} 
				if (par != null) {
					inst.run("defParam", par.pool_ref); }
				if (par != null) {
					inst.addObject("param", par); }
				for (int i = 0 ; i < Utl.data_type_nb ; i++) {
					nMap<Integer> map = prop.data_vals.get(Utl.data_type[i]);
					if (map != null) for (Map.Entry<String, Integer> mr : map.entrySet()) {
						String dt_ref = mr.getKey();
						Object dt_def = prop.getDataValDef(dt_ref, Utl.data_type[i]);
						if (dt_def == null) dt_def = Utl.new_object(Utl.data_type[i]);
						if (!inst.hasVar(dt_ref)) inst.addVar(dt_ref, dt_def);
					}
				}
				inst.patch.addEventFrame(inst.object("run_frame", nRun.class));
			}});
		}})
		.useClear().commande(new nRun() {public void run() { 
			instance.patch.removeEventFrame(
					instance.object("run_frame", nRun.class));
		}}).useInit();

		pProcess proc = stand.process();
		
		proc.openSec()
			.param("width", (int)8) 
			.run(pNode.getRun(CT.RUNP_VAR_STR_LAB_FIELD), "param_used")
		.closeSec()
		.openSec()
			.param("run", new nRun() {public void run() {
				nWidget triggP_w = instance.get("get_mapped_widget", nWidget.class, 
						"trigg_dropm_param");
				if (triggP_w == null) return;
				for (pParam param : PlaneApplet.app.space.param_pools.get(prop.ref).all()) {
					String par = param.pool_ref;
					nGUI.add_dropmenu_entry(par, new nRun(instance) { public void run() {
						((pInstance)builder).setVar("defParam", par); }}); 
				}
				nGUI.open_dropmenu(triggP_w);
			}})
			.run(pNode.getRun(CT.RUNP_ADD_TRIGG), "trigg_dropm_param", "Pk", (int)2)
		.closeSec()
		;

		int cnt = 0;
		for (pProperty opt : prop.option_props) {
			if (cnt%2 == 0) proc.commande(pNode.getCom(CT.COM_ADD_ROW));
			proc.openSec()
				.param("text", opt.ref, "width", (int)6) 
				.run(pNode.getRun(pNode.CT.RUNP_VAR_BOO_SWITCH), "use_"+opt.ref)
			.closeSec();
			cnt++;
		}
		
//		proc.commande(pNode.getCom(CT.COM_ADD_ROW))
//		.openSec()
//			.param("run", new nRun() {public void run() {
//				nWidget triggP_w = instance.get("get_mapped_widget", nWidget.class, 
//						"trigg_dropm_data");
//				if (triggP_w == null) return;
//				//instance.patch.patch_dropmenu.metode("clear_entrys");
//	
//				pParam par = instance.object("param", pParam.class);
//				if (par == null) return;
//				
//				for (int i = 0 ; i < Utl.data_type_nb ; i++) {
//					nMap<Integer> map = par.prop.data_vals.get(Utl.data_type[i]);
//					if (map != null) for (Map.Entry<String, Integer> mr : map.entrySet()) {
//						String dt_ref = mr.getKey();
//						nWidget w1 = nGUI.add_dropmenu_entry(dt_ref); 
////						nWidget w1 = (nWidget)instance.patch.patch_dropmenu
////								.metodeGet("add_entry_custom", dt_ref, RS*6f, RS*2f/3f);
//						w1.addEventTrigger(new nRun(instance) { public void run() {
//							pInstance inst = (pInstance)builder;
//							inst.run("pop_param_data", dt_ref);
//							pInstance last = inst.get("get_chain_last", pInstance.class);
//							pInstance n = last.get("pop_plug_node", pInstance.class,
//									"data_out", "param_data", "data_in");
//							n.run("set_data", dt_ref);
//							n.setVar("data_ref", dt_ref);
//						}}); 
//					}
//				}
////				instance.patch.patch_dropmenu.metode("open", triggP_w);
//				nGUI.open_dropmenu(triggP_w);
//			}})
//			.run(pNode.getRun(CT.RUNP_ADD_TRIGG), "trigg_dropm_data", "pop_data_node", (int)16)
//		.closeSec()
//		;
		
		stand
//		.newRun("pop_param_data", new nRun() {public void run() {
//			String dt_ref = arg(0,String.class);
//			if (dt_ref == null) return;
//			pInstance last = instance.get("get_chain_last", pInstance.class);
//			pInstance n = last.get("pop_plug_node", pInstance.class,
//					"data_out", "param_data", "data_in");
//			n.run("set_data", dt_ref);
//		}})
		.openSec()
			.param("offer", new nRun() {public Object get() {
				pInstance node = instance.object("node", pInstance.class);
				return node.object("param", pParam.class);
			}})
			.param("keys", new String[] {"param", "out"}) 
			.param("filters", new String[] {"param", "in"})
			.run(pNode.getRun(CT.RUNS_ADD_CO_OUT), "param")
		.closeSec()
//		.openSec()
//		.param("offer", new nRun() {public Object get() {
//			pParam par = instance.object("node", pInstance.class)
//					.object("param", pParam.class);
//			if (par == null) return "";
//			return par.pool_ref; }})
//		.param("keys", new String[] {"var", "str"}, "filters", new String[] {}) 
//		.run(pNode.getRun(CT.RUNS_ADD_CO_OUT), "ref")
//		.closeSec()
//		.openSec()
//		.param("hide", true, "keys", new String[] {"param_data"}, "filters", new String[] {"param_data"}) 
//		.run(pNode.getRun(CT.RUNS_ADD_CHAIN_START_PLUG), "data", "bottom")
//		.closeSec()
		;
		
		
		return stand;
	}
	
	
	
	
	
	
	
	
	
	


	public static void build_param_chain_nodes() {
			

		float RS = nGUI.book.RS;

		
//		
//		pNode.newChainnedNodeModel("param_data")
//		.newRun("set_data", new nRun() {public void run() {
//			String data_ref = arg(0, String.class);
//			if (data_ref == null) return;
//			instance.setVar("data_ref", data_ref);
//			nInterface interf = instance.object("interf", nInterface.class);
//			pInstance head = instance.get("get_chain_head", pInstance.class);
//			if (head == null) return;
//			pProperty prop = head.object("prop", pProperty.class);
//			Class<?> dt_class = prop.data_class.get(data_ref);
//			if (dt_class == null) return;
//			instance.addObject("got_data", true);
////			interf.add_row();
//			if (dt_class == Boolean.class) {
//				nWidget w = interf.add_row_switch(10, data_ref);
//				w.addEventSwitch(new nRun(head) {public void run() { 
//					pInstance inst = (pInstance)builder;
//					pParam par = inst.object("param", pParam.class);
//					if (par == null) return;
//					par.set(data_ref, w.isOn());
//				}});
//			} else if (dt_class == String.class) {
//				if (prop.get_setting(data_ref, "mode_list") != null && 
//						((boolean)prop.get_setting(data_ref, "mode_list"))) {
//					interf.add_row_label(3, data_ref);
//					nWidget w = interf.add_row_label(4, "");
//					w.setOutline(true);
//					nRun sl_run = new nRun(head) {public void run() {
//						pInstance target = (pInstance)builder;
//						String text = target.getVar(data_ref, String.class);
//						w.setText(text);
//					}};
//					w.addEventLogic(sl_run); sl_run.run();
//					nWidget w2 = interf.add_row_trigg(2, "Pk");
//					w2.addEventTrigger(new nRun(head) {public void run() {
//						pInstance target = (pInstance)builder;
//						for (String par : ((String[])prop.get_setting(data_ref, "list"))) {
//							nGUI.add_dropmenu_entry(par, new nRun(instance) { public void run() {
//								((pInstance)builder).setVar(data_ref, par); }}); 
//						}
//						nGUI.open_dropmenu(w2);
//					}});
//					
//				} else {
//					interf.add_row_label(4, data_ref);
//					nWidget w = interf.add_row_label(6, "");
//					w.setField(true)
//					.copyLookFrom(nGUI.book.getModel("text_field"));
//					w.addEventFieldChange(new nRun(head) {public void run() {
//						pInstance target = (pInstance)builder;
//							target.setVar(data_ref, w.getText());
//					}});
//					nRun sl_run = new nRun(head) {public void run() {
//						pInstance target = (pInstance)builder;
//						String text = target.getVar(data_ref, String.class);
//						if (
//							//!w.isSelected && 
//							!text.equals(w.getText())) w.setText(text);
//					}};
//					w.addEventLogic(sl_run); sl_run.run();
//				}
//			} else if (dt_class == Float.class) {
//				interf.add_row_label(3, data_ref);
//				nWidget w = interf.add_row_label(3, "");
//				w.setField(true)
//				.copyLookFrom(nGUI.book.getModel("text_field"));
//				int float_rez = (int)(1.2f * w.getLocalSX() / w.getFont()) - 3;
//				w.addEventFieldChange(new nRun(head) {public void run() {
//					pInstance target = (pInstance)builder;
//						target.setVar(data_ref, Utl.tofloat(w.getText()));
//				}});
//				nRun sl_run = new nRun(head, float_rez) {public void run() {
//					pInstance target = (pInstance)args[0];
//					int frez = (int)args[1];
//					String text = "" + Utl.trimFlt(target.getVar(data_ref, Float.class), frez);
//					if (
//						//!w.isSelected && 
//						!text.equals(w.getText())) w.setText(text);
//				}};
//				w.addEventLogic(sl_run); sl_run.run();
//				
//				float min = 0f, max = 1f; 
//				if (prop.get_setting(data_ref, "min") != null) 
//					min = (float)prop.get_setting(data_ref, "min");
//				if (prop.get_setting(data_ref, "max") != null) 
//					max = (float)prop.get_setting(data_ref, "max");
//				
//				nWidget w2 = interf.add_row_slide(6,min,max);
//				if (prop.get_setting(data_ref, "granulo") != null) 
//					w2.setSliderGranulo((float)prop.get_setting(data_ref, "granulo"));
//				w2.addEventSliderChange(new nRun(head) {public void run() {
//					pInstance target = (pInstance)builder;
//						target.setVar(data_ref, w2.getSliderValInMinMax()); 
//				}});
//				nRun sl_run2 = new nRun(head) {public void run() {
//					if (w2.isSliderGrabbed) return; 
//					pInstance target = (pInstance)builder;
//					float vl = w2.getSliderValInMinMax();
//					vl = target.getVar(data_ref, Float.class);
//					if (vl != w2.getSliderValInMinMax()) w2.setSliderValInRange(vl);
//				}};
//				w2.addEventLogic(sl_run2); sl_run2.run();
//				
//			} else if (dt_class == Vector2.class) {
//				interf.add_row_label(3, data_ref + " : X");
//				nWidget w = interf.add_row_label(3, "");
//				w.setField(true)
//				.copyLookFrom(nGUI.book.getModel("text_field"));
//				int float_rez = (int)(1.2f * w.getLocalSX() / w.getFont()) - 3;
//				w.addEventFieldChange(new nRun(head) {public void run() {
//					pInstance target = (pInstance)builder;
//					float x = Utl.tofloat(w.getText());
//					Vector2 v = target.getVar(data_ref, Vector2.class);
//					v.x = x;
//					target.setVar(data_ref, v);
//				}});
//				nRun sl_x_run = new nRun(head, float_rez) {public void run() {
//					pInstance target = (pInstance)args[0];
//					int frez = (int)args[1];
//					String text = "" + Utl.trimFlt(
//							target.getVar(data_ref, Vector2.class).x, frez);
//					if (
//						//!w.isSelected && 
//						!text.equals(w.getText())) w.setText(text);
//				}};
//				w.addEventLogic(sl_x_run); sl_x_run.run();
//
//				float min = 0f, max = 1f; 
//				if (prop.get_setting(data_ref, "min") != null) 
//					min = (float)prop.get_setting(data_ref, "min");
//				if (prop.get_setting(data_ref, "max") != null) 
//					max = (float)prop.get_setting(data_ref, "max");
//				
//				nWidget wb = interf.add_row_slide(6,min,max);
//				if (prop.get_setting(data_ref, "granulo") != null) 
//					wb.setSliderGranulo((float)prop.get_setting(data_ref, "granulo"));
//				wb.addEventSliderChange(new nRun(head) {public void run() {
//					pInstance target = (pInstance)builder;
//					float x = wb.getSliderValInMinMax();
//					Vector2 v = target.getVar(data_ref, Vector2.class);
//					v.x = x;
//					target.setVar(data_ref, v);
//				}});
//				nRun sl_runb = new nRun(head) {public void run() {
//					if (wb.isSliderGrabbed) return; 
//					pInstance target = (pInstance)builder;
//					float vl = wb.getSliderValInMinMax();
//					vl = target.getVar(data_ref, Vector2.class).x;
//					if (vl != wb.getSliderValInMinMax()) wb.setSliderValInRange(vl);
//				}};
//				wb.addEventLogic(sl_runb); sl_runb.run();
//				
//				interf.add_row();
//				interf.add_row_label(3, "           Y");
//				nWidget w2 = interf.add_row_label(3, "");
//				w2.setField(true)
//				.copyLookFrom(nGUI.book.getModel("text_field"));
//				w2.addEventFieldChange(new nRun(head) {public void run() {
//					pInstance target = (pInstance)builder;
//					float y = Utl.tofloat(w2.getText());
//					Vector2 v = target.getVar(data_ref, Vector2.class);
//					v.y = y;
//					target.setVar(data_ref, v);
//				}});
//				nRun sl_y_run = new nRun(head, float_rez) {public void run() {
//					pInstance target = (pInstance)args[0];
//					int frez = (int)args[1];
//					String text = "" + Utl.trimFlt(
//							target.getVar(data_ref, Vector2.class).y, frez);
//					if (
//						//!w.isSelected && 
//						!text.equals(w2.getText())) w2.setText(text);
//				}};
//				w2.addEventLogic(sl_y_run); sl_y_run.run();
//
//				nWidget wc = interf.add_row_slide(6,min,max);
//				if (prop.get_setting(data_ref, "granulo") != null) 
//					wc.setSliderGranulo((float)prop.get_setting(data_ref, "granulo"));
//				wc.addEventSliderChange(new nRun(head) {public void run() {
//					pInstance target = (pInstance)builder;
//					float y = wc.getSliderValInMinMax();
//					Vector2 v = target.getVar(data_ref, Vector2.class);
//					v.y = y;
//					target.setVar(data_ref, v);
//				}});
//				nRun sl_runc = new nRun(head) {public void run() {
//					if (wc.isSliderGrabbed) return; 
//					pInstance target = (pInstance)builder;
//					float vl = wc.getSliderValInMinMax();
//					vl = target.getVar(data_ref, Vector2.class).y;
//					if (vl != wc.getSliderValInMinMax()) wc.setSliderValInRange(vl);
//				}};
//				wc.addEventLogic(sl_runc); sl_runc.run();
//				
//			} else if (dt_class == Integer.class) {
//				interf.add_row_label(3, data_ref);
//				nWidget w = interf.add_row_label(3, "");
//				w.setField(true)
//				.copyLookFrom(nGUI.book.getModel("text_field"));
//				w.addEventFieldChange(new nRun(head) {public void run() {
//					pInstance target = (pInstance)builder;
//						target.setVar(data_ref, Utl.toint(w.getText()));
//				}});
//				nRun sl_run = new nRun(head) {public void run() {
//					pInstance target = (pInstance)builder;
//					String text = "" + target.getVar(data_ref, Integer.class);
//					if (
//						//!w.isSelected && 
//						!text.equals(w.getText())) w.setText(text);
//				}};
//				w.addEventLogic(sl_run); sl_run.run();
//
//				float min = 0f, max = 1f; 
//				if (prop.get_setting(data_ref, "min") != null) 
//					min = (float)prop.get_setting(data_ref, "min");
//				if (prop.get_setting(data_ref, "max") != null) 
//					max = (float)prop.get_setting(data_ref, "max");
//				
//				nWidget w2 = interf.add_row_slide(6,min,max);
//				if (prop.get_setting(data_ref, "granulo") != null) 
//					w2.setSliderGranulo((float)prop.get_setting(data_ref, "granulo"));
//				w2.addEventSliderChange(new nRun(head) {public void run() {
//					pInstance target = (pInstance)builder;
//						target.setVar(data_ref, (int)w2.getSliderValInMinMax()); 
//				}});
//				nRun sl_run2 = new nRun(head) {public void run() {
//					if (w2.isSliderGrabbed) return; 
//					pInstance target = (pInstance)builder;
//					float vl = w2.getSliderValInMinMax();
//					vl = target.getVar(data_ref, Integer.class);
//					if (vl != w2.getSliderValInMinMax()) w2.setSliderValInRange(vl);
//				}};
//				w2.addEventLogic(sl_run2); sl_run2.run();
//				
//			} 
//		}})
//		.process()
//			.commande(new nRun() {public void run() {
//				App.ap.addDelayEvent(1, new nRun(instance) {public void run() {
//					pInstance inst = (pInstance)builder;
//					if (inst.object("got_data") == null)
//						inst.run("set_data", inst.getVar("data_ref", String.class));
//				}});
//			}})
//			.run(pNode.getRun(CT.RUNP_OBTAIN_VAR), "data_ref", "")  
//			.getStand()
//		.openSec()
//		.param("hide", true, "keys", new String[] {"param_data"}, "filters", new String[] {"param_data"}) 
//		.run(pNode.getRun(CT.RUNS_ADD_CHAIN_PLUGS), "data", "bottom")
//		.closeSec()
//		;
//		
		
	}
	
	
	
	public static void build_sel_body_node() {
		

		float RS = nGUI.book.RS;


//		pStandard stand_sel_body = pNode.newNodeModel("sel_body", "body");
//		stand_sel_body.newRun("select_body", new nRun() {public void run() {
//			if (args.length < 1) return;
//			String bod_ref = arg(0, String.class);
//			pBody bod = PlaneApplet.app.space.body_pool.get(bod_ref);
//			if (bod == null) return;
//			
//			if (bod.hasParam("owner") && bod.getBoo("owner", "owned") && 
//					!bod.getStr("owner", "owner").equals(PlaneApplet.app.config.player_ref)) return;
//			
//			instance.run("unselect_body");
//			
//			if (bod.hasParam("highlightable")) 
//				bod.setBoo("highlightable", "lighted", true);
//			instance.setVar("owned", bod.getBoo("owner", "owned"));
//			instance.setVar("body_ref", bod.pool_ref);
//			pGeom geom = PlaneApplet.app.getSystem(pGeom.class);
//			geom.removeEventBodyClic(instance.object("clic_run", nRun.class));
//			geom.removeEventEmptyClic(instance.object("empty_clic_run", nRun.class));
//			
//			if (instance.hasObject("ank", pInstance.class)) {
//				pInstance ank = instance.object("ank", pInstance.class);
//				ank.setVar("view_ank", true);
//				instance.removeObject("ank", ank);
//			}
//			pInstance co = instance.get("get_co", pInstance.class, "co_ank");
//			Object op = co.get("obtain_all", Object.class);
//			if (op == null) return;
//			ArrayList<Object> prov = (ArrayList)op;
//			for (Object o : prov) if (o instanceof pInstance) {
//				pInstance ank = (pInstance)o;
//				if (ank != null) {
//					instance.setObject("ank", ank);
////					ank.run("metode", "set_default");
//					ank.setVar("grab", !instance.getVar("track", Boolean.class));
//					ank.setVar("title", "Selected Body");
//					ank.setVar("view_ank", true);
//					break;
//				}
//			}
//		}})
//		.newRun("unselect_body", new nRun() {public void run() {
//			pBody bod = PlaneApplet.app.space.body_pool.get(instance.getVar("body_ref", String.class));
//			if (bod == null) return;
//			if (bod.hasParam("highlightable")) 
//				bod.setBoo("highlightable", "lighted", false);
//			
//			instance.setVar("owned", false);
//			instance.setVar("body_ref", "");
//			pGeom geom = PlaneApplet.app.getSystem(pGeom.class);
//			geom.removeEventBodyClic(instance.object("clic_run", nRun.class));
//			geom.removeEventEmptyClic(instance.object("empty_clic_run", nRun.class));
//			
//			if (instance.hasObject("ank", pInstance.class)) {
//				pInstance ank = instance.object("ank", pInstance.class);
//				ank.setVar("view_ank", true);
//				instance.removeObject("ank", ank);
//			}
//			pInstance co = instance.get("get_co", pInstance.class, "co_ank");
//			Object op = co.get("obtain_all", Object.class);
//			if (op == null) return;
//			ArrayList<Object> prov = (ArrayList)op;
//			for (Object o : prov) if (o instanceof pInstance) {
//				pInstance ank = (pInstance)o;
//				if (ank != null) {
//					instance.setObject("ank", ank);
////					ank.setVar("view_ank", false);
//					ank.setVar("grab", true);
//					break;
//				}
//			}
//		}})
//		.newRun("do_prev_tick", new nRun() {public void run() { 
//			if (instance.getVar("track", Boolean.class)) {
//				pInstance ank = instance.object("ank", pInstance.class);
//				if (ank == null) return;
//				if (instance.getVar("body_ref", String.class) == null) return;
//				pBody bod = PlaneApplet.app.space.body_pool.get(instance.getVar("body_ref", String.class));
//				if (bod == null || !bod.hasParam("ref")) return;
//				ank.setVar("ank_pos", new Vector2(bod.getVec("ref", "pos")));
//				ank.run("recalc");
//			}
//		}})
//		.newRun("do_tick", new nRun() {public void run() { 
//			if (instance.getVar("track", Boolean.class)) {
//				pInstance ank = instance.object("ank", pInstance.class);
//				if (ank == null) return;
//				if (instance.getVar("body_ref", String.class) == null) return;
//				pBody bod = PlaneApplet.app.space.body_pool.get(instance.getVar("body_ref", String.class));
//				if (bod == null || !bod.hasParam("ref")) return;
//				ank.setVar("ank_pos", new Vector2(bod.getVec("ref", "pos")));
//				ank.run("recalc");
//			}
//		}})
//		.process()
//		.useInit().commande(new nRun() {public void run() {
//			PlaneApplet.app.time.addPrevTickBric(instance);
//			PlaneApplet.app.time.addTickBric(instance);
//			pGeom geom = PlaneApplet.app.getSystem(pGeom.class);
//			nRun clic_run = new nRun(instance) { public void run(Object o) {
//				pInstance inst = ((pInstance)builder);
//				inst.setVar("select", false);
//				if (o == null || !(o instanceof pBody)) return;
//				pBody bod = (pBody)o;
//				inst.run("select_body", bod.pool_ref);
//				geom.removeEventBodyClic(inst.object("clic_run", nRun.class));
//				geom.removeEventEmptyClic(inst.object("empty_clic_run", nRun.class));
//			}};
//			instance.addObject("clic_run", clic_run); 
//			nRun empty_clic_run = new nRun(instance) { public void run() {
//				pInstance inst = ((pInstance)builder);
//				inst.setVar("select", false);
//				geom.removeEventBodyClic(inst.object("clic_run", nRun.class));
//				geom.removeEventEmptyClic(inst.object("empty_clic_run", nRun.class));
//				inst.run("unselect_body");
//			}};
//			instance.addObject("empty_clic_run", empty_clic_run); 
//			nRun clear_run = new nRun(instance) { public void run(Object o) {
//				if (o == null || !(o instanceof pBody)) return;
//				pInstance inst = ((pInstance)builder);
//				pBody bod = (pBody)o;
//				if (inst.getVar("body_ref", String.class).equals(bod.pool_ref)) {
//					inst.run("unselect_body");
//				}
//			}};
//			instance.addObject("clear_run", clear_run); 
//			geom.addEventBodyClear(instance.object("clear_run", nRun.class));
//		}})
//		.useLoad().commande(new nRun() {public void run() {
//			App.ap.addDelayEvent(1,new nRun(instance) { public void run() {
//				pInstance inst = ((pInstance)builder);
//				if (inst.getVar("body_ref", String.class) == null) return;
//				pBody bod = PlaneApplet.app.space.body_pool.get(inst.getVar("body_ref", String.class));
//				if (bod != null) inst.run("select_body", bod.pool_ref); }});
//		}}).useInit()
//		.useClear().commande(new nRun() {public void run() {
//			PlaneApplet.app.time.removePrevTickBric(instance);
//			PlaneApplet.app.time.removeTickBric(instance);
//			pGeom geom = PlaneApplet.app.getSystem(pGeom.class);
//			geom.removeEventEmptyClic(instance.object("empty_clic_run", nRun.class));
//			geom.removeEventBodyClic(instance.object("clic_run", nRun.class));
//			geom.removeEventBodyClear(instance.object("clear_run", nRun.class));
//		}}).useInit()
//		.openSec()
//			.param("text", "ref: ", "width", (int)8)
//			.run(pNode.getRun(CT.RUNP_VAR_STR_LAB_FIELD), "body_ref")
//		.closeSec()
//		.openSec()
//			.param("run", new nRun() {public void run() {
//				nWidget trigg_w = instance.get("get_mapped_widget", nWidget.class, 
//						"trigg_dropm_body");
//				if (trigg_w == null) return;
//				//instance.patch.patch_dropmenu.metode("clear_entrys");
//				for (String par : PlaneApplet.app.space.body_pool.allKey()) {
//					nGUI.add_dropmenu_entry(par, new nRun(instance) { public void run() {
//						((pInstance)builder).run("select_body", par); }}); 
////					nWidget w1 = (nWidget)instance.patch.patch_dropmenu
////							.metodeGet("add_entry_custom", par, RS*6f, RS*2f/3f);
////					w1.addEventTrigger(new nRun(instance) { public void run() {
////						((pInstance)builder).run("select_body", par); 
////					}}); 
//				}
////				instance.patch.patch_dropmenu.metode("open", trigg_w); 
//				nGUI.open_dropmenu(trigg_w);
//			}})
//			.run(pNode.getRun(CT.RUNP_ADD_TRIGG), "trigg_dropm_body", "Pk", (int)2)
//		.closeSec()
//		.openSec()
//			.param("run", new nRun() {public void run() {
//				pGeom geom = PlaneApplet.app.getSystem(pGeom.class);
//				nRun clic_run = instance.object("clic_run", nRun.class); 
//				nRun empty_clic_run = instance.object("empty_clic_run", nRun.class); 
//				if (instance.getVar("select", Boolean.class)) {
//					geom.addEventBodyClic(clic_run); 
//					geom.addEventEmptyClic(empty_clic_run); } 
//				else { geom.removeEventBodyClic(clic_run); 
//					geom.removeEventEmptyClic(empty_clic_run); }
//			}})
//			.run(pNode.getRun(CT.RUNP_VAR_BOO_SWITCH), 
//					"select", "select", (int)6)
//		.closeSec()
//		.openSec()
//			.param("run", new nRun() {public void run() {
//				if (instance.getVar("body_ref", String.class) == null) return;
//				pBody bod = PlaneApplet.app.space.body_pool.get(instance.getVar("body_ref", String.class));
//				if (bod == null) return;
//				if (instance.getVar("owned", Boolean.class)) {
//					if (bod.hasParam("owner") && !bod.getBoo("owner", "owned")) {
//						bod.setBoo("owner", "owned", true);
//						bod.setStr("owner", "owner", PlaneApplet.app.config.player_ref);
//					} else if (bod.hasParam("owner") && bod.getBoo("owner", "owned") && 
//							!bod.getStr("owner", "owner").equals(PlaneApplet.app.config.player_ref)) {
//						instance.setVar("owned", false);
//					} else if (!bod.hasParam("owner")) {
//						instance.setVar("owned", false);
//					}
//				} else {
//					if (bod.hasParam("owner") && bod.getBoo("owner", "owned") && 
//							bod.getStr("owner", "owner").equals(PlaneApplet.app.config.player_ref)) {
//						bod.setBoo("owner", "owned", false);
//						bod.setStr("owner", "owner", "");
//					} 
//				}
//			}})
//			.run(pNode.getRun(CT.RUNP_VAR_BOO_SWITCH), 
//					"owned", "owned", (int)6)
//		.closeSec()
//		.commande(pNode.getCom(CT.COM_ADD_ROW))
//		.openSec()
//			.param("run", new nRun() {public void run() {
//				if (instance.getVar("track", Boolean.class)) {
//					pInstance ank = instance.object("ank", pInstance.class);
//					if (ank != null) ank.setVar("grab", false);
//				} else { 
//					pInstance ank = instance.object("ank", pInstance.class);
//					if (ank != null) ank.setVar("grab", true);
//				}
//			}})
//			.param("def", true)
//			.run(pNode.getRun(CT.RUNP_VAR_BOO_SWITCH), 
//					"track", "track", (int)6)
//		.closeSec()
//		.getStand()
//		.openSec()
//			.param("offer", new nRun() {public Object get() {
//				pInstance node = instance.object("node", pInstance.class);
//				if (node.getVar("body_ref", String.class) == null) 
//					return null;
//				return PlaneApplet.app.space.body_pool.get(node.getVar("body_ref", String.class));
//			}})
//			.param("keys", new String[] {"body"}, "filters", new String[] {"body"}) 
//			.run(pNode.getRun(CT.RUNS_ADD_CO_OUT), "co_sel_bod")
//		.closeSec()
//		.openSec()
//			.param("event_receive", new nRun() {public void run() {
//	//				app.log("sel bod received");
//				pInstance node = instance.object("node", pInstance.class);
//				if (node.getVar("body_ref", String.class) == null) return;
//				pBody bod = PlaneApplet.app.space.body_pool.get(node.getVar("body_ref", String.class));
//				if (bod == null) return;
//				String par_ref = arg(0,String.class);
//				String data_ref = arg(1,String.class);
//				Object data = arg(2,Object.class);
//				if (par_ref == null || data_ref == null || data == null) return;
//	//				app.log("sel bod received "+par_ref+" "+data_ref+" "+data);
//				if (bod.param(par_ref) == null) return;
//				bod.param(par_ref).set(data_ref,data);
//			}})
//			.param("keys", new String[] {"body"}, "filters", new String[] {}) 
//			.run(pNode.getRun(CT.RUNS_ADD_CO_IN), "set")
//		.closeSec()
//		.openSec()
//			.param("event_link", new nRun() {public void run() {
//				pInstance node = instance.object("node", pInstance.class);
//				if (node.hasObject("ank", pInstance.class)) {
//					pInstance ank = node.object("ank", pInstance.class);
//					ank.setVar("view_ank", true);
//					node.removeObject("ank", ank);
//				}
//				pInstance co = node.get("get_co", pInstance.class, "co_ank");
//				Object op = co.get("obtain_all", Object.class);
//				if (op == null) return;
//				ArrayList<Object> prov = (ArrayList)op;
//				for (Object o : prov) if (o instanceof pInstance) {
//					pInstance ank = (pInstance)o;
//					if (ank != null) {
//						node.setObject("ank", ank);
//	//						ank.run("metode", "set_default");
//						ank.setVar("grab", !node.getVar("track", Boolean.class));
//	//						ank.setVar("title", "Selected Body");
//						if (node.getVar("body_ref", String.class) == null) return;
//						pBody bod = PlaneApplet.app.space.body_pool.get(node.getVar("body_ref", String.class));
//						if (bod != null) ank.setVar("view_ank", true);
//						else ank.setVar("view_ank", false);
//						break;
//					}
//				}
//			}})
//			.param("keys", new String[] {"ank"}, "filters", new String[] {"ank"}) 
//			.run(pNode.getRun(CT.RUNS_ADD_CO_IN), "co_ank")
//		.closeSec()
//		.openSec()
//			.param("event_receive", new nRun() {public void run() {
//				if (args.length < 1) return;
//				pBody bod = arg(0,pBody.class);
//				if (bod == null) return;
//				pInstance node = instance.object("node", pInstance.class);
//				node.run("select_body", bod.pool_ref);
//			}})
//			.param("keys", new String[] {"body"}, "filters", new String[] {"body"}) 
//			.run(pNode.getRun(CT.RUNS_ADD_CO_IN), "in")
//		.closeSec()
//		.openSec()
//		.param("hide", true, "keys", new String[] {"sel_bod"}, 
//				"filters", new String[] {"sel_bod"}) 
//		.run(pNode.getRun(CT.RUNS_ADD_CHAIN_START_PLUG), "sel_bod", "bottom")
//		.closeSec()
//		;
//		
//		
//		
//		
//		
//		
//		
//		
//		
//		
//		
//		pNode.newChainnedNodeModel("bod_get_body")
//		.openSec().param("hide", true, "keys", new String[] {"sel_bod"}, 
//				"filters", new String[] {"sel_bod"}) 
//		.run(pNode.getRun(CT.RUNS_ADD_CHAIN_PLUGS), "sel_bod", "bottom").closeSec()
//		.process()
//		.openSec()
//			.param("text", "param: ", "width", (int)8)
//			.run(pNode.getRun(CT.RUNP_VAR_STR_LAB_FIELD), "param_ref")
//		.closeSec()
//		.openSec()
//			.param("run", new nRun() {public void run() {
//				nWidget trigg_w = instance.get("get_mapped_widget", nWidget.class, 
//						"trigg_dropm_par");
//				if (trigg_w == null) return;
//				pInstance head = instance.get("get_chain_head", pInstance.class);
//				if (head == null) return;
//				pBody bod = PlaneApplet.app.space.body_pool.get(head.getVar("body_ref", String.class));
//				if (bod == null) return;
//				//instance.patch.patch_dropmenu.metode("clear_entrys");
//				for (String par : bod.params.allKey()) {
//					nGUI.add_dropmenu_entry(par, new nRun(instance) { public void run() {
//						((pInstance)builder).setVar("param_ref", par); }}); 
////					nWidget w1 = (nWidget)instance.patch.patch_dropmenu
////							.metodeGet("add_entry_custom", par, RS*6f, RS*2f/3f);
////					w1.addEventTrigger(new nRun(instance) { public void run() {
////						((pInstance)builder).setVar("param_ref", par); 
////					}}); 
//				}
////				instance.patch.patch_dropmenu.metode("open", trigg_w); 
//				nGUI.open_dropmenu(trigg_w);
//			}})
//			.run(pNode.getRun(CT.RUNP_ADD_TRIGG), "trigg_dropm_par", "Pk", (int)2)
//		.closeSec()
//		.commande(pNode.getCom(CT.COM_ADD_ROW))
//		.openSec()
//			.param("text", "bod_ref: ", "width", (int)8)
//			.run(pNode.getRun(CT.RUNP_VAR_STR_LAB_FIELD), "body_ref")
//		.closeSec()
//		.openSec()
//			.param("run", new nRun() {public void run() {
//				nWidget trigg_w = instance.get("get_mapped_widget", nWidget.class, 
//						"trigg_dropm_body");
//				if (trigg_w == null) return;
//				pInstance head = instance.get("get_chain_head", pInstance.class);
//				if (head == null) return;
//				pBody bod = PlaneApplet.app.space.body_pool.get(head.getVar("body_ref", String.class));
//				if (bod == null) return;
//				String param_ref = instance.getVar("param_ref", String.class);
//				if (!bod.hasParam(param_ref)) return;
//				pParam par = bod.param(param_ref);
//				//instance.patch.patch_dropmenu.metode("clear_entrys");
//				for (String br : par.prop.body_vals.allKey()) {
//					nGUI.add_dropmenu_entry(br, new nRun(instance) { public void run() {
//						((pInstance)builder).setVar("body_ref", br); }}); 
////					nWidget w1 = (nWidget)instance.patch.patch_dropmenu
////							.metodeGet("add_entry_custom", br, RS*6f, RS*2f/3f);
////					w1.addEventTrigger(new nRun(instance) { public void run() {
////						((pInstance)builder).setVar("body_ref", br); 
////					}}); 
//				}
////				instance.patch.patch_dropmenu.metode("open", trigg_w); 
//				nGUI.open_dropmenu(trigg_w);
//			}})
//			.run(pNode.getRun(CT.RUNP_ADD_TRIGG), "trigg_dropm_body", "Pk", (int)2)
//		.closeSec()
//		.getStand()
//		.openSec()
//			.param("offer", new nRun() {public Object get() {
//				pInstance node = instance.object("node", pInstance.class);
//				pInstance head = node.get("get_chain_head", pInstance.class);
//				if (head == null) return null;
//				pBody bod = PlaneApplet.app.space.body_pool.get(head.getVar("body_ref", String.class));
//				if (bod == null) return null;
//				String param_ref = node.getVar("param_ref", String.class);
//				String body_ref = node.getVar("body_ref", String.class);
//				if (!bod.hasParam(param_ref)) return null;
//				pParam par = bod.param(param_ref);
//				pBody b = par.getBody(body_ref);
//				return b;
//			}})
//			.param("keys", new String[] {"body"}, "filters", new String[] {"body"}) 
//			.run(pNode.getRun(CT.RUNS_ADD_CO_OUT), "co_bod")
//		.closeSec()
//		;
//
//
//		pNode.newChainnedNodeModel("bod_get_data")
//		.openSec().param("hide", true, "keys", new String[] {"sel_bod"}, 
//				"filters", new String[] {"sel_bod"}) 
//		.run(pNode.getRun(CT.RUNS_ADD_CHAIN_PLUGS), "sel_bod", "bottom").closeSec()
//		.process()
//		.openSec()
//			.param("text", "param: ", "width", (int)8)
//			.run(pNode.getRun(CT.RUNP_VAR_STR_LAB_FIELD), "param_ref")
//		.closeSec()
//		.openSec()
//			.param("run", new nRun() {public void run() {
//				nWidget trigg_w = instance.get("get_mapped_widget", nWidget.class, 
//						"trigg_dropm_par");
//				if (trigg_w == null) return;
//				pInstance head = instance.get("get_chain_head", pInstance.class);
//				if (head == null) return;
//				pBody bod = PlaneApplet.app.space.body_pool.get(head.getVar("body_ref", String.class));
//				if (bod == null) return;
//				//instance.patch.patch_dropmenu.metode("clear_entrys");
//				for (String par : bod.params.allKey()) {
//					nGUI.add_dropmenu_entry(par, new nRun(instance) { public void run() {
//						((pInstance)builder).setVar("param_ref", par); }}); 
////					nWidget w1 = (nWidget)instance.patch.patch_dropmenu
////							.metodeGet("add_entry_custom", par, RS*6f, RS*2f/3f);
////					w1.addEventTrigger(new nRun(instance) { public void run() {
////						((pInstance)builder).setVar("param_ref", par); 
////					}}); 
//				}
////				instance.patch.patch_dropmenu.metode("open", trigg_w); 
//				nGUI.open_dropmenu(trigg_w);
//			}})
//			.run(pNode.getRun(CT.RUNP_ADD_TRIGG), "trigg_dropm_par", "Pk", (int)2)
//		.closeSec()
//		.commande(pNode.getCom(CT.COM_ADD_ROW))
//		.openSec()
//			.param("text", "data_ref: ", "width", (int)8)
//			.run(pNode.getRun(CT.RUNP_VAR_STR_LAB_FIELD), "data_ref")
//		.closeSec()
//		.openSec()
//			.param("run", new nRun() {public void run() {
//				nWidget trigg_w = instance.get("get_mapped_widget", nWidget.class, 
//						"trigg_dropm_data");
//				if (trigg_w == null) return;
//				pInstance head = instance.get("get_chain_head", pInstance.class);
//				if (head == null) return;
//				pBody bod = PlaneApplet.app.space.body_pool.get(head.getVar("body_ref", String.class));
//				if (bod == null) return;
//				String param_ref = instance.getVar("param_ref", String.class);
//				if (!bod.hasParam(param_ref)) return;
//				pParam par = bod.param(param_ref);
////				//instance.patch.patch_dropmenu.metode("clear_entrys");
//				for (Map.Entry<Class<?>,nMap<Integer>> ma_map : par.prop.data_vals.entrySet()) 
//					if (ma_map.getValue() != null) 
//						for (String br : ma_map.getValue().allKey()) {
//					nGUI.add_dropmenu_entry(br, new nRun(instance) { public void run() {
//						((pInstance)builder).setVar("data_ref", br); }}); 
////					nWidget w1 = (nWidget)instance.patch.patch_dropmenu
////							.metodeGet("add_entry_custom", br, RS*6f, RS*2f/3f);
////					w1.addEventTrigger(new nRun(instance) { public void run() {
////						((pInstance)builder).setVar("data_ref", br); 
////					}}); 
//				}
////				instance.patch.patch_dropmenu.metode("open", trigg_w); 
//				nGUI.open_dropmenu(trigg_w);
//			}})
//			.run(pNode.getRun(CT.RUNP_ADD_TRIGG), "trigg_dropm_data", "Pk", (int)2)
//		.closeSec()
//		.getStand()
//		.openSec()
//			.param("offer", new nRun() {public Object get() {
//				pInstance node = instance.object("node", pInstance.class);
//				pInstance head = node.get("get_chain_head", pInstance.class);
//				if (head == null) return null;
//				pBody bod = PlaneApplet.app.space.body_pool.get(head.getVar("body_ref", String.class));
//				if (bod == null) return null;
//				String param_ref = node.getVar("param_ref", String.class);
//				String data_ref = node.getVar("data_ref", String.class);
//				if (!bod.hasParam(param_ref)) return null;
//				pParam par = bod.param(param_ref);
//				return par.get(data_ref);
//			}})
//			.param("keys", new String[] {"all"}, "filters", new String[] {""}) 
//			.run(pNode.getRun(CT.RUNS_ADD_CO_OUT), "co_out")
//		.closeSec()
//		;
//
//		
		
		
	}
	
	public static void build_actor_node() {
		
		float RS = nGUI.book.RS;
		

//		pStandard stand_actor = pNode.newNodeModel("actor", "body");
//		stand_actor.append(pStandard.get("constructor_abstract"));
//		stand_actor
//		.newRun("obtain_all_reg_of_model", new nRun() {public Object get() { 
//			String model_ref = arg(0, String.class);
//			ArrayList<String> allkey = new ArrayList<String>();
//			if (model_ref == null) return allkey;
//			if (instance.getVar("body_ref", String.class) == null) return allkey;
//			pBody body = PlaneApplet.app.space.body_pool.get(
//					instance.getVar("body_ref", String.class));
//			if (body == null) return allkey;
//			if (model_ref.equals("reg_in")) {
//				allkey.add("body");
//			} else if (model_ref.equals("reg_out")) {
//				
//			}
//			return allkey;
//		}})
//		.newRun("obtain_from_reg", new nRun() {public Object get() { 
//			String reg_ref = arg(0, String.class);
//			if (reg_ref == null) return null;
//			if (instance.getVar("body_ref", String.class) == null) return null;
//			pBody body = PlaneApplet.app.space.body_pool.get(
//					instance.getVar("body_ref", String.class));
//			if (body == null) return null;
//			if (reg_ref.equals("body")) return body;
//			return null;
//		}})
//		.newRun("send_to_reg", new nRun() {public void run() { 
//			String reg_ref = arg(0, String.class);
//			Object data = arg(1, Object.class);
//			if (reg_ref == null || data == null) return;
//			
//			
//			
//		}})
//		.newRun("select_body", new nRun() {public void run() {
//			if (args.length < 1) return;
//			String bod_ref = arg(0, String.class);
//			pBody bod = PlaneApplet.app.space.body_pool.get(bod_ref);
//			if (bod == null) return;
//			
//			if (bod.hasParam("owner") && bod.getBoo("owner", "owned") && 
//					!bod.getStr("owner", "owner").equals(PlaneApplet.app.config.player_ref)) return;
//			
//			instance.run("unselect_body");
//			
//			if (bod.hasParam("highlightable")) 
//				bod.setBoo("highlightable", "lighted", true);
//			
//			instance.setVar("body_ref", bod.pool_ref);
//		}})
//		.newRun("unselect_body", new nRun() {public void run() {
//			pBody bod = PlaneApplet.app.space.body_pool.get(instance.getVar("body_ref", String.class));
//			if (bod == null) return;
//			if (bod.hasParam("highlightable")) 
//				bod.setBoo("highlightable", "lighted", false);
//			instance.setVar("body_ref", "");
//		}})
////		.newRun("do_prev_tick", new nRun() {public void run() { 
////			
////		}})
////		.newRun("do_tick", new nRun() {public void run() { 
////			
////		}})
//		.process()
//		.useInit().commande(new nRun() {public void run() {
////			PlaneApplet.app.time.addPrevTickBric(instance);
////			PlaneApplet.app.time.addTickBric(instance);
//			pGeom geom = PlaneApplet.app.getSystem(pGeom.class);
//			nRun clear_run = new nRun(instance) { public void run(Object o) {
//				if (o == null || !(o instanceof pBody)) return;
//				pInstance inst = ((pInstance)builder);
//				pBody bod = (pBody)o;
//				if (inst.getVar("body_ref", String.class).equals(bod.pool_ref)) {
//					inst.run("unselect_body");
//				}
//			}};
//			instance.addObject("clear_run", clear_run); 
//			geom.addEventBodyClear(instance.object("clear_run", nRun.class));
//		}})
//		.useLoad().commande(new nRun() {public void run() {
//			App.ap.addDelayEvent(1,new nRun(instance) { public void run() {
//				pInstance inst = ((pInstance)builder);
//				if (inst.getVar("body_ref", String.class) == null) return;
//				pBody bod = PlaneApplet.app.space.body_pool.get(inst.getVar("body_ref", String.class));
//				if (bod != null) inst.run("select_body", bod.pool_ref); }});
//			nRun run_space_start = new nRun(instance) {public void run() { 
//				pInstance inst = (pInstance)builder;
//				pBody bod = inst.get("new_body", pBody.class); 
//				if (bod != null) inst.run("select_body", bod.pool_ref); 
//			}};
//			instance.addObject("run_space_start", run_space_start);
//			PlaneApplet.app.space.addEventSpaceStart(run_space_start);
//		}}).useInit()
//		.useClear().commande(new nRun() {public void run() {
////			PlaneApplet.app.time.removePrevTickBric(instance);
////			PlaneApplet.app.time.removeTickBric(instance);
//			pGeom geom = PlaneApplet.app.getSystem(pGeom.class);
//			geom.removeEventBodyClear(instance.object("clear_run", nRun.class));
//			PlaneApplet.app.space.removeEventSpaceStart(
//					instance.object("run_space_start", nRun.class));
//		}}).useInit()
//		.openSec()
//			.param("text", "name: ", "width", (int)8)
//			.run(pNode.getRun(CT.RUNP_VAR_STR_LAB_FIELD), "print_name")
//		.closeSec()
//		.openSec()
//			.param("run", new nRun() {public void run() {
//				nWidget trigg_w = instance.get("get_mapped_widget", nWidget.class, 
//						"trigg_dropm_print");
//				if (trigg_w == null) return;
//				pSpace space = PlaneApplet.app.space;
//				for (String br : space.param_pools.get("blueprint").allKey()) {
//					String nm = space.param_pools.get("blueprint")
//							.get(br).get("name", String.class);
//					nGUI.add_dropmenu_entry(nm, new nRun(instance) { public void run() {
//							((pInstance)builder).setVar("print_name", nm); }}); }
//				nGUI.open_dropmenu(trigg_w);
//			}})
//			.run(pNode.getRun(CT.RUNP_ADD_TRIGG), "trigg_dropm_print", "Pk", (int)2)
//		.closeSec()
//		.commande(pNode.getCom(CT.COM_ADD_ROW))
//		.openSec()
//			.param("text", "ref: ", "width", (int)8)
//			.run(pNode.getRun(CT.RUNP_VAR_STR_LAB_FIELD), "body_ref")
//		.closeSec()
//		.commande(pNode.getCom(pNode.CT.COM_ADD_ROW))
//		.openSec()
//		.param("text", "pos", "width", (int)8)
//		.run(pNode.getRun(pNode.CT.RUNP_VAR_VEC_LAB_FIELD), "pop_pos")
//		.closeSec()
//		.commande(pNode.getCom(pNode.CT.COM_ADD_ROW))
//		.openSec()
//		.param("text", "scale", "width", (int)8, "def", 1f)
//		.run(pNode.getRun(pNode.CT.RUNP_VAR_FLT_LAB_FIELD), "pop_size")
//		.closeSec()
//		.getStand()
//		.openSec()
//		.param("keys", new String[]{"register", "out"}, 
//				"filters", new String[]{"register", "in"}) 
//		.run(pNode.getRun(pNode.CT.RUNS_ADD_CO_OUT), "co_register").closeSec()
//		;
		
	}
	
	
	
	
	
	
//	
//	public static void build_old_nodes(Applet app) {
//		
//
//		float RS = nGUI.book.RS;
//
//
//		pStandard stand_new_body = pNode.newNodeModel("new_body", "body");
//		stand_new_body.newRun("new_body", new nRun() {public Object get() {
////			
////			pBody bod = instance.patch.plane.getSystem(pSpace.class)
////					.new_body();
////
////			bod.newParam("ref");
////			bod.newParam("scale");
////			bod.newParam("var_move");
////			bod.newParam("info_shape");
////
////			if (instance.hasVar("ownable") && 
////					instance.getVar("ownable", Boolean.class)) {
////				bod.newParam("ownable");
////				if (instance.hasVar("acquire") && 
////						instance.getVar("acquire", Boolean.class)) {
////					bod.setBoo("ownable", "owned", true);
////					bod.setStr("ownable", "owner", app.player_ref); }
////			}
////
////			bod.newParam("highlightable");
////			
////			if (instance.hasVar("clickable") && 
////					instance.getVar("clickable", Boolean.class)) {
////				bod.newParam("clickable");
////			}
////			pInstance co_ank = instance.get("get_co", pInstance.class, "co_ank");
////			Object op_ank = co_ank.get("obtain_all", Object.class);
////			if (op_ank != null) { 
////				ArrayList<Object> prov_ank = (ArrayList)op_ank;
////				for (Object o : prov_ank) if (o instanceof pInstance) {
////					pInstance ank = (pInstance)o;
////					if (ank != null) {
////						instance.setObject("ank", ank);
////					}
////				}
////			}
////			pInstance ank = instance.object("ank", pInstance.class);
////			if (ank != null) bod.setVec("ref", "pos", 
////					ank.getVar("ank_pos", Vector2.class));
////			
////			for (String r : pProperty.addable_props.allKey()) { 
////				if (instance.hasVar("var_add_"+r) && 
////						instance.getVar("var_add_"+r, Boolean.class)) {
////					bod.newParam(r);
////				}
////			}
//			
////			pInstance co = instance.get("get_co", pInstance.class, "co_prop");
////			Object op = co.get("obtain_all", Object.class);
////			if (op == null) return bod;
////			ArrayList<Object> prov = (ArrayList)op;
////			ArrayList<pParam> pars = new ArrayList<pParam>();
////			for (Object o : prov) if (o instanceof pParam) {
////				pParam p = (pParam)o;
////				pars.add(p); 
////			}
////			int i = 0;
////			for (pParam p : pars) { 
////				String r = ""+p.prop.ref;
////				int j = 0;
////				while (bod.param(r) != null && j < 200) {
////					r = p.prop.ref+"_"+j; j++; }
////				if (j < 200) bod.addParam(r, p); 
////				else bod.addParam(p.prop.ref+"_"+i, p); 
////				i++; 
////			}
////
////			
////			bod.space.plane.getSystem(pGeom.class).init_body(bod);
//////			bod.space.plane.getSystem(pFlux.class).init_body(bod);
////			
////			bod.space.update_families();
////			
////			instance.setObject("new_bod", bod);
////			pInstance co_out = instance.get("get_co", pInstance.class, "body");
////			co_out.run("send", bod);
////			
//////			App.ap.addDelayEvent(2, new nRun() { public void run() {
//////				bod.space.plane.getSystem(pGeom.class).select_body(bod);
//////			}});
////			
////			return bod;
//			
//			pSpace space = app.space;
//			if (space == null) return null;
//			pParam bluep = null;
//			
//			String print_ref = instance.getVar("print_ref", String.class);
//			if (print_ref != null) {
//				bluep = space.param_pools.get("blueprint")
//						.get(print_ref);
//				if (bluep != null) instance.setVar("print_name", 
//						bluep.get("name", String.class));
//			}
//
//			if (bluep == null) {
//				String print_name = instance.getVar("print_name", String.class);
//				if (print_name != null) {
//					for (String br : space.param_pools.get("blueprint").allKey()) {
//						String nm = space.param_pools.get("blueprint")
//								.get(br).get("name", String.class);
//						if (nm != null && nm.equals(print_name)) {
//							instance.setVar("print_ref", br);
//							bluep = space.param_pools.get("blueprint")
//									.get(br);
//							break;
//						}
//					}
//				}
//			}
//			
//			if (bluep == null) {
//				pInstance co = instance.get("get_co", pInstance.class, "bluePrnt");
//				Object op = co.get("obtain_all", Object.class);
//				if (op == null) return null;
//				ArrayList<Object> prov = (ArrayList)op;
//				for (Object o : prov) if (o instanceof pParam) {
//					pParam p = (pParam)o;
//					if (p != null && p.prop.ref.equals("blueprint")) { 
//						bluep = p;
//						instance.setVar("print_ref", bluep.pool_ref); 
//						instance.setVar("print_name", bluep.get("name", String.class)); 
//						break; } }
//			}
//			
//			if (bluep != null) {
//
//				pBody pop = instance.patch.plane.getSystem(pSpace.class)
//						.new_body();
//				
//				pop.newParam("ref");
//				pop.newParam("scale");
//				pop.newParam("info_shape");
//				pop.newParam("var_move");
//				pop.newParam("clickable");
//				pop.newParam("highlightable");
//
//				if (instance.hasVar("ownable") && 
//						instance.getVar("ownable", Boolean.class)) {
//					pop.newParam("ownable");
//					if (instance.hasVar("acquire") && 
//							instance.getVar("acquire", Boolean.class)) {
//						pop.setBoo("ownable", "owned", true);
//						pop.setStr("ownable", "owner", app.player_ref); }
//				}
//	
////				for (String r : pProperty.addable_props.allKey()) { 
////					if (bluep.getBoo("add_"+r)) {
////						pop.newParam(r);
////					}
////				}
//				
//				ArrayList<String> prop_ref = 
//						bluep.getCollecData("prop_ref", String.class);
//				ArrayList<String> param_ref = 
//						bluep.getCollecData("param_ref", String.class);
//				if (prop_ref.size() != param_ref.size()) {
//					pop.clear(); return null; }
//				
//				for (int i = 0 ; i < prop_ref.size() ; i++) {
//					String prop = prop_ref.get(i), par = param_ref.get(i);
//					if (bluep.space.param_pools.get(prop) == null) continue;
//					pParam prm = bluep.space.param_pools.get(prop).get(par); 
//					if (prm != null && !pop.hasParam(prop)) { 
//						pop.addParam(prop, prm); } 
//				}
//				
//				pInstance co_ank = instance.get("get_co", pInstance.class, "co_ank");
//				Object op_ank = co_ank.get("obtain_all", Object.class);
//				if (op_ank != null) { 
//					ArrayList<Object> prov_ank = (ArrayList)op_ank;
//					for (Object o : prov_ank) if (o instanceof pInstance) {
//						pInstance ank = (pInstance)o;
//						if (ank != null) {
//							instance.setObject("ank", ank);
//						}
//					}
//				}
//				pInstance ank = instance.object("ank", pInstance.class);
//				if (ank != null) pop.setVec("ref", "pos", 
//						ank.getVar("ank_pos", Vector2.class));
//
//				pop.space.plane.getSystem(pGeom.class).init_body(pop);
//				
//				pop.space.update_families();
//				
//				instance.setObject("new_bod", pop);
//				pInstance co_out = instance.get("get_co", pInstance.class, "body");
//				co_out.run("send", pop);
//				
//				return pop;
//			}
//			return null;
//		}});
//
//		stand_new_body.openSec()
//			.param("event_receive", new nRun() {public void run() {
//				pInstance node = instance.object("node", pInstance.class);
//				node.get("new_body"); 
//			}})
////			.param("text", "prop", "width", (int)4)
//			.param("keys", new String[] {"bang"}, "filters", new String[] {}) 
//			.run(pNode.getRun(CT.RUNS_ADD_CO_IN), "co_new_bod")
//		.closeSec();
//
//		pProcess pr = stand_new_body.process();
//		pr.openSec()
//			.param("run", new nRun() {public void run() {
//				instance.get("new_body"); }}, "height", 2f)
//			.run(pNode.getRun(CT.RUNP_ADD_TRIGG), "new", "NEW", (int)8)
//		.closeSec()
////		.commande(pNode.getCom(CT.COM_ADD_ROW))
////		.openSec()
////			.param("text", "clickable", "width", (int)8, "def", true) 
////			.run(pNode.getRun(CT.RUNP_VAR_BOO_SWITCH), "clickable")
////		.closeSec()
//		.commande(pNode.getCom(CT.COM_ADD_ROW))
//		.openSec()
//			.param("text", "ownable", "width", (int)6, "def", true) 
//			.run(pNode.getRun(CT.RUNP_VAR_BOO_SWITCH), "ownable")
//		.closeSec()
//		.openSec()
//			.param("text", "acquire", "width", (int)6, "def", true) 
//			.run(pNode.getRun(CT.RUNP_VAR_BOO_SWITCH), "acquire")
//		.closeSec()
//		.commande(pNode.getCom(CT.COM_ADD_ROW))
//		.openSec()
//			.param("text", "print: ", "width", (int)10)
//			.run(pNode.getRun(CT.RUNP_VAR_STR_LAB_FIELD), "print_ref")
//		.closeSec()
//		.commande(pNode.getCom(CT.COM_ADD_ROW))
//		.openSec()
//			.param("text", "name: ", "width", (int)8)
//			.run(pNode.getRun(CT.RUNP_VAR_STR_LAB_FIELD), "print_name")
//		.closeSec()
//		.openSec()
//			.param("run", new nRun() {public void run() {
//				nWidget trigg_w = instance.get("get_mapped_widget", nWidget.class, 
//						"trigg_dropm_print");
//				if (trigg_w == null) return;
//				pSpace space = app.space;
//				if (space == null) return;
//				//instance.patch.patch_dropmenu.metode("clear_entrys");
//				for (String br : space.param_pools.get("blueprint").allKey()) {
//					String nm = space.param_pools.get("blueprint")
//							.get(br).get("name", String.class);
//					nWidget w1 = (nWidget)instance.patch.patch_dropmenu
//							.metodeGet("add_entry_custom", nm, RS*6f, RS*2f/3f);
//					w1.addEventTrigger(new nRun(instance) { public void run() {
//						((pInstance)builder).setVar("print_ref", br); 
//						((pInstance)builder).setVar("print_name", nm); 
//					}}); }
//				instance.patch.patch_dropmenu.metode("open", trigg_w); 
//			}})
//			.run(pNode.getRun(CT.RUNP_ADD_TRIGG), "trigg_dropm_print", "Pk", (int)2)
//		.closeSec()
//		.commande(pNode.getCom(CT.COM_ADD_ROW))
//		;
//
////		int boo_cnt = 0;
////		for (String r : pProperty.addable_props.allKey()) { 
////			pr.openSec()
////				.param("text", r, "width", (int)6, "def", true) 
////				.run(pNode.getRun(CT.RUNP_VAR_BOO_SWITCH), "var_add_"+r)
////			.closeSec();
////			boo_cnt++;
////			if (boo_cnt > 1) {
////				boo_cnt = 0;
////				pr.commande(pNode.getCom(CT.COM_ADD_ROW));
////			}
////		}
////		pr.openSec()
////			.commande(pNode.getCom(CT.COM_ADD_COL))
////			.param("ref", "preview")
////			.param("text", "", "width", (int)12, "height", 6f)
////			.param("custom_drawer", new nRun() {public void run() { 
////	
////				app.fill(40); app.rect(0,0,180,180);
////				app.push(); app.translate(90,90);
////				app.stroke(255,0,0,255,2f); app.line(0,-80,0,80);
////				app.stroke(0,255,0,255,2f); app.line(-80,0,80,0);
////				app.pop();
////				
////				pInstance co = instance.get("get_co", pInstance.class, "co_prop");
////				if (co == null) return;
////				Object op = co.get("obtain_all", Object.class);
////				if (op == null) { return; }
////				ArrayList<Object> prov = (ArrayList)op;
////				pParam graph = null;
////				pParam geom = null;
////				for (Object o : prov) if (o instanceof pParam) {
////					pParam p = (pParam)o;
////					if (p.prop.ref.equals("geom")) geom = p; 
////					if (p.prop.ref.equals("graph")) graph = p; }
////	
////				app.push(); app.translate(90,90);
////				if (geom != null) {
////					pGeom.draw_geom_graph(app, null, geom, graph); }
////				app.pop();
////				
////			}}) 
////			.commande(pNode.getCom(CT.COM_ADD_WIDGET))
////		.closeSec();
//
////		stand_new_body.openSec()
//////			.param("text", "prop", "width", (int)4)
////			.param("keys", new String[] {"param", "in"}, 
////					"filters", new String[] {"param", "out"}) 
////			.run(pNode.getRun(CT.RUNS_ADD_CO_IN), "co_prop")
////		.closeSec();
//		
//		stand_new_body.openSec()
////			.param("text", "prop", "width", (int)4)
//			.param("keys", new String[] {"param", "in"}, 
//					"filters", new String[] {"param", "out"}) 
//			.run(pNode.getRun(CT.RUNS_ADD_CO_IN), "bluePrnt")
//		.closeSec();
//		
//		stand_new_body.openSec()
////			.param("text", "ank", "width", (int)4)
//			.param("keys", new String[] {"ank"}, 
//					"filters", new String[] {"ank"}) 
//			.run(pNode.getRun(CT.RUNS_ADD_CO_IN), "co_ank")
//		.closeSec();
//		
//		stand_new_body.openSec()
//			.param("offer", new nRun() {public Object get() {
//				pInstance node = instance.object("node", pInstance.class);
//				if (node.hasObject("new_bod"))
//					return node.object("new_bod", pBody.class);
//				else return null;
//			}})
//			.param("keys", new String[] {"body"}, "filters", new String[] {"body"}) 
//			.run(pNode.getRun(CT.RUNS_ADD_CO_OUT), "body")
//		.closeSec()
//		;
//
//		
//		
//		
//	}
//	
//	
	

}
