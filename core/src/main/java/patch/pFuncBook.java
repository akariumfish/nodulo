package patch;

import data.*;
import gui.*;
import util.*;
import aa_nodulo.*;
import app.*;
import patch.pFunc.*;
import patch.pTile.CT;

import java.util.ArrayList;
import java.util.Map;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;


public class pFuncBook {
	
	
	public static final String[] index_list = new String[] {
		"0", "1", "2", "3", "4"};

	public static void build_operators() {

		float RS = nGUI.book.RS;

		new Operator("and", "&&", C.AND, Boolean.class, new nRun() {public Object get() {
			Boolean o1 = ask("in1", Boolean.class); Boolean o2 = ask("in2", Boolean.class);
			if (o1 != null && o2 != null) { return o1 && o2; }
			return null; }})
		.addArg("in1", Boolean.class).addArg("in2", Boolean.class);

		new Operator("or", "||", C.OR, Boolean.class, new nRun() {public Object get() {
			Boolean o1 = ask("in1", Boolean.class); Boolean o2 = ask("in2", Boolean.class);
			if (o1 != null && o2 != null) { return o1 || o2; }
			return null; }})
		.addArg("in1", Boolean.class).addArg("in2", Boolean.class);

		new Operator("to_flt", "I>F", C.ItF, Float.class, new nRun() {public Object get() {
			Integer o1 = ask("in", Integer.class); if (o1 != null) { return (float)o1; } 
			return null; }}).addArg("in", Integer.class);

		new Operator("to_int", "F>I", C.FtI, Integer.class, new nRun() {public Object get() {
			Float o1 = ask("in", Float.class); if (o1 != null) { return Utl.toint(o1); } 
			return null; }}).addArg("in", Float.class);

		new Operator("not", "!", C.NOT, Boolean.class, new nRun() {public Object get() {
			Boolean o1 = ask("in", Boolean.class); if (o1 != null) { return !(boolean)o1; } 
			return null; }}).addArg("in", Boolean.class);

		new Operator("neg", "neg", C.NEG, Float.class, new nRun() {public Object get() {
			Float o1 = ask("in", Float.class); if (o1 != null) { return -(float)o1; } 
			return null; }}).addArg("in", Float.class);
		
		new Operator("abs", "abs", C.ABS, Float.class, new nRun() {public Object get() {
			Float o1 = ask("in", Float.class); if (o1 != null) { return (float)Math.abs(o1); } 
			return null; }}).addArg("in", Float.class);
		
		new Operator("len", "len", C.LEN, Float.class, new nRun() {public Object get() {
			Vector2 o1 = ask("in", Vector2.class); if (o1 != null) { return (float)o1.len(); } 
			return null; }}).addArg("in", Vector2.class);
		
		new Operator("dir", "dir", C.DIR, Float.class, new nRun() {public Object get() {
			Vector2 o1 = ask("in", Vector2.class); if (o1 != null) { return (float)o1.angleRad(); } 
			return null; }}).addArg("in", Vector2.class);
		
		new Operator("dm_vec", "dm>v", C.DMtV, Vector2.class, new nRun() {public Object get() {
			Float o1 = ask("dir", Float.class); Float o2 = ask("mag", Float.class);
			if (o1 != null && o2 != null) { return new Vector2(o2,0).rotateRad(o1); }
			return null; }})
		.addArg("dir", Float.class).addArg("mag", Float.class);

		new Operator("axe_vec", "xy>v", C.XYtV, Vector2.class, new nRun() {public Object get() {
			Float o1 = ask("x", Float.class); Float o2 = ask("y", Float.class);
			if (o1 != null && o2 != null) { return new Vector2(o1,o2); }
			return null; }})
		.addArg("x", Float.class).addArg("y", Float.class);

		new Operator("vec_x", "v>x", C.VtX, Float.class, new nRun() {public Object get() {
			Vector2 o1 = ask("in", Vector2.class); if (o1 != null) { return (float)o1.x; } 
			return null; }}).addArg("in", Vector2.class);

		new Operator("vec_y", "v>y", C.VtY, Float.class, new nRun() {public Object get() {
			Vector2 o1 = ask("in", Vector2.class); if (o1 != null) { return (float)o1.y; } 
			return null; }}).addArg("in", Vector2.class);
		
		new Operator("mag", "mag", C.MAG, Vector2.class, new nRun() {public Object get() {
			Vector2 o1 = ask("in", Vector2.class); Float o2 = ask("mag", Float.class);
			if (o1 != null && o2 != null) { return new Vector2(o1).nor().scl(o2); }
			return null; }})
		.addArg("in", Vector2.class).addArg("mag", Float.class);
		
		new Operator("rot", "rot", C.ROT, Vector2.class, new nRun() {public Object get() {
			Vector2 o1 = ask("in", Vector2.class); Float o2 = ask("rot", Float.class);
			if (o1 != null && o2 != null) { return new Vector2(o1).rotateRad(o2); }
			return null; }})
		.addArg("in", Vector2.class).addArg("rot", Float.class);

		new Operator("add_vec", "+v", C.ADDV, Vector2.class, new nRun() {public Object get() {
			Vector2 o1 = ask("in", Vector2.class); Vector2 o2 = ask("fact", Vector2.class);
			if (o1 != null && o2 != null) { return new Vector2(o1).add(o2); }
			return null; }})
		.addArg("in", Vector2.class).addArg("fact", Vector2.class);

		new Operator("add", "+", C.ADD, Float.class, new nRun() {public Object get() {
			Float o1 = ask("in", Float.class); Float o2 = ask("fact", Float.class);
			if (o1 != null && o2 != null) { return (float)(o1+o2); }
			return null; }})
		.addArg("in", Float.class).addArg("fact", Float.class);

		new Operator("sub", "-", C.SUB, Float.class, new nRun() {public Object get() {
			Float o1 = ask("in", Float.class); Float o2 = ask("fact", Float.class);
			if (o1 != null && o2 != null) { return (float)(o1-o2); }
			return null; }})
		.addArg("in", Float.class).addArg("fact", Float.class);
		
		new Operator("mult", "x", C.MUL, Float.class, new nRun() {public Object get() {
			Float o1 = ask("in", Float.class); Float o2 = ask("fact", Float.class);
			if (o1 != null && o2 != null) { return (float)(o1*o2); }
			return null; }})
		.addArg("in", Float.class).addArg("fact", Float.class);

		new Operator("div", "/", C.DIV, Float.class, new nRun() {public Object get() {
			Float o1 = ask("in", Float.class); Float o2 = ask("fact", Float.class);
			if (o1 != null && o2 != null && o2 != 0) { return (float)(o1/o2); }
			return null; }})
		.addArg("in", Float.class).addArg("fact", Float.class);

		new Operator("sup", ">", C.SUP, Boolean.class, new nRun() {public Object get() {
			Float o1 = ask("in1", Float.class); Float o2 = ask("in2", Float.class);
			if (o1 != null && o2 != null) { return (o1>o2); }
			return null; }})
		.addArg("in1", Float.class).addArg("in2", Float.class);

		new Operator("inf", "<", C.INF, Boolean.class, new nRun() {public Object get() {
			Float o1 = ask("in1", Float.class); Float o2 = ask("in2", Float.class);
			if (o1 != null && o2 != null) { return (o1<o2); }
			return null; }})
		.addArg("in1", Float.class).addArg("in2", Float.class);

		new Operator("esup", ">=", C.ESUP, Boolean.class, new nRun() {public Object get() {
			Float o1 = ask("in1", Float.class); Float o2 = ask("in2", Float.class);
			if (o1 != null && o2 != null) { return (o1>=o2); }
			return null; }})
		.addArg("in1", Float.class).addArg("in2", Float.class);

		new Operator("einf", "<=", C.EINF, Boolean.class, new nRun() {public Object get() {
			Float o1 = ask("in1", Float.class); Float o2 = ask("in2", Float.class);
			if (o1 != null && o2 != null) { return (o1<=o2); }
			return null; }})
		.addArg("in1", Float.class).addArg("in2", Float.class);

		new Operator("eq", "=", C.EQ, Boolean.class, new nRun() {public Object get() {
			Float o1 = ask("in1", Float.class); Float o2 = ask("in2", Float.class);
			if (o1 != null && o2 != null) { return (o1.equals(o2)); }
			return null; }})
		.addArg("in1", Float.class).addArg("in2", Float.class);

		
		
		
		

		int cnt = 0;
		for (String tp : Utl.type_short_names) {
			new Operator(tp, Utl.type_type_maj.get(tp), pFunc.types_codes[cnt], 
					new nRun() {public Object get() {
				return ask("value", Utl.type_type_class.get(tp));
			}})
			.addVar("value")
			.setStandRun(new nRun() {public void run() {
				pStandard stand = arg(0,pStandard.class);
				pProcess proc = stand.process()
				.run(pTile.getRun(CT.OBTAIN_VAR), "value", 
						Utl.new_object(Utl.type_type_class.get(tp)));
				if (Utl.type_type_class.get(tp) == Boolean.class) {
					proc.openSec().param("ref", "value_switch", "var_link_ref", "value", 
							"var_link_class", Boolean.class.getName(), "width", (int)8)
					.commande(pTile.getCom(CT.ADD_SWITCH)).closeSec();
				} else if (Utl.type_type_class.get(tp) == Vector2.class) {
					proc.openSec().param("ref", "value_field_x", "var_link_ref", "value", 
							"var_link_class", Vector2.class.getName(), 
							"var_link_vec_axe", "x", "width", (int)6)
					.commande(pTile.getCom(CT.ADD_FIELD)).closeSec()
					.openSec().param("ref", "value_field_y", "var_link_ref", "value", 
							"var_link_class", Vector2.class.getName(), 
							"var_link_vec_axe", "y", "width", (int)6)
					.commande(pTile.getCom(CT.ADD_FIELD)).closeSec();
				} else {
					proc.openSec().param("ref", "value_field", "var_link_ref", "value", 
							"var_link_class", Utl.type_type_class.get(tp).getName(), 
							"width", (int)8)
					.commande(pTile.getCom(CT.ADD_FIELD)).closeSec();
				}
			}});
			cnt++;
		}
		
		
		
		
		

//		new Operator("slow", "S", C.SLOW, new nRun() {public Object get() {
//			tel("cnt", 1f+ask("cnt", Float.class));
//			if (ask("cnt", Float.class) > ask("delay", Float.class)) {
//				tel("cnt", 0f); return true; }
//			return false; 
//		}})
//		.addVar("delay").addPrivVar("cnt", 0f)//.setLast()
//		.setStandRun(new nRun() {public void run() {
//			pStandard stand = arg(0,pStandard.class);
//			stand.process()
//			.run(pTile.getRun(CT.OBTAIN_VAR), "delay", 0f)
//			.openSec()
//			.param("ref", "field", "var_link_ref", "delay", 
//					"var_link_class", Float.class.getName())
//			.param("width", (int)8)
//			.commande(pTile.getCom(CT.ADD_FIELD))
//			.closeSec();
//		}});

		
		
		
		
		

		new Operator("get_input", "I", C.GETI, new nRun() {public Object get() {
			String in_ref = ask("in_ref", String.class);
				nRun in = PlaneApplet.app.inputs.get(in_ref);
				if (in != null) { return in.do_get(); } 
			return null; 
			}})
		.addVar("in_ref")
		.setStandRun(new nRun() {public void run() {
			pStandard stand = arg(0,pStandard.class);
			stand.process()
			.run(pTile.getRun(CT.OBTAIN_VAR), "in_ref", "")
			.openSec()
			.param("run_right", new nRun() {public void run() {
				nWidget triggP_w = instance.get("get_mapped_widget", nWidget.class, 
						"in_ref_watch");
				if (triggP_w == null) return;
				for (String par : PlaneApplet.app.inputs.allKey()) {
					nGUI.add_dropmenu_entry(par, new nRun(instance) { public void run() {
						((pInstance)builder).setVar("in_ref", par); }});
				}
				nGUI.open_dropmenu(triggP_w);
			}})
			.param("ref", "in_ref_watch", "var_link_ref", "in_ref", 
					"var_link_class", String.class.getName())
			.param("width", (int)8)
			.commande(pTile.getCom(CT.ADD_WATCH))
			.closeSec();
		}});

		
		new Operator("get_reg_in", "RI", C.GETR, new nRun() {public Object get() { 
			String reg_ref = ask("reg_ref", String.class);
			pInstance node = ask("tile_node", pInstance.class);
			if (node == null) return null;
			return node.get("obtain_from_reg", Object.class, reg_ref);
		}})
		.addVar("reg_ref")
		.setStandRun(new nRun() {public void run() {
			pStandard stand = arg(0,pStandard.class);
			stand.process()
			.run(pTile.getRun(CT.OBTAIN_VAR), "reg_ref", "")
			.openSec()
			.param("ref", "reg_ref_watch", "var_link_ref", "reg_ref", "var_link_class", String.class.getName())
			.param("width", (int)8)
			.commande(pTile.getCom(CT.ADD_FIELD))
			.closeSec();
		}});


		
		new Operator("get_param", "GP", C.GETP, new nRun() {public Object get() {
			Object o2 = ask("body", Object.class);
			String param_ref = ask("param_ref", String.class);
			String data_ref = ask("data_ref", String.class);
			if (o2 != null && (o2 instanceof pBody)) {
				pBody bod = (pBody)o2;
				pParam par = bod.param(param_ref);
				if (par != null && par.has(data_ref, par.prop.data_class.get(data_ref))) {
					return par.get(data_ref, par.prop.data_class.get(data_ref));
				}
			}
			return null;
		}})
		.addVar("param_ref").addVar("data_ref").addArg("body", null)
		.setStandRun(new nRun() {public void run() {
			pStandard stand = arg(0,pStandard.class);
			stand.process()
			.run(pTile.getRun(CT.OBTAIN_VAR), "param_ref", "")
			.openSec()
			.param("ref", "param_ref_watch", "var_link_ref", "param_ref", 
					"var_link_class", String.class.getName())
			.param("width", (int)8)
			.param("run_right", new nRun() {public void run() {
				nWidget triggP_w = instance.get("get_mapped_widget", nWidget.class, 
						"param_ref_watch");
				if (triggP_w == null) return;
				
				for (String par : pProperty.body_propertys.allKey()) {
					nGUI.add_dropmenu_entry(par, new nRun(instance) { public void run() {
						((pInstance)builder).setVar("param_ref", par); 
						((pInstance)builder).setVar("data_ref", ""); }});
				}
				nGUI.open_dropmenu(triggP_w);
			}})
			.commande(pTile.getCom(CT.ADD_WATCH))
			.closeSec()
			.run(pTile.getRun(CT.OBTAIN_VAR), "data_ref", "")
			.openSec()
			.param("ref", "data_ref_watch", "var_link_ref", "data_ref", 
					"var_link_class", String.class.getName())
			.param("width", (int)8)
			.param("run_right", new nRun() {public void run() {
				nWidget triggP_w = instance.get("get_mapped_widget", nWidget.class, 
						"data_ref_watch");
				if (triggP_w == null) return;
				String par_ref = instance.getVar("param_ref", String.class);
				pProperty prop = pProperty.get(par_ref);
				if (prop == null) return;
				for (Map.Entry<Class<?>, nMap<Integer>> me : prop.data_vals.entrySet()) {
//					Class<?> ct = me.getKey();
					for (Map.Entry<String,Integer> map_me : me.getValue().entrySet()) {
						String k = map_me.getKey();
						nGUI.add_dropmenu_entry(k, new nRun(instance) { public void run() {
							((pInstance)builder).setVar("data_ref", k); }});
					}
				}
				nGUI.open_dropmenu(triggP_w);
			}})
			.commande(pTile.getCom(CT.ADD_WATCH))
			.closeSec();
		}});

		

		
		
		
		
		
		new Operator("index", "IND", C.IND, new nRun() {public Object get() {
			Object o_arr = ask("array", Object.class);
			Integer index = ask("index", Integer.class);
			Object[] arr = null;
			if (o_arr != null && (o_arr instanceof Object[])) arr = (Object[])o_arr;
			if (arr != null && index != null && index < arr.length) { return arr[(int)index]; }
			return null;
			}}).addArg("array", null).addVar("index")
		.setStandRun(new nRun() {public void run() {
			pStandard stand = arg(0,pStandard.class);
			stand.process()
			.run(pTile.getRun(CT.OBTAIN_VAR), "index", (int)-1)
			.openSec()
			.param("run_right", new nRun() {public void run() {
				nWidget triggP_w = instance.get("get_mapped_widget", nWidget.class, 
						"index_watch");
				if (triggP_w == null) return;
				for (String par : index_list) {
					nGUI.add_dropmenu_entry(par, new nRun(instance) { public void run() {
						((pInstance)builder).setVar("index", Utl.toint(par)); }});
				}
				nGUI.open_dropmenu(triggP_w);
			}})
			.param("ref", "index_watch", "var_link_ref", "index", 
					"var_link_class", Integer.class.getName())
			.param("width", (int)2)
			.commande(pTile.getCom(CT.ADD_WATCH))
			.closeSec();
		}});

		
		
		
		
		new Operator("arr", "ARR", C.ARR, new nRun() {public Object get() {
			Object entry = ask("entry", Object.class);
			Object o_arr = ask("array", Object.class);
			Object[] arr = null;
			if (o_arr != null && (o_arr instanceof Object[])) arr = (Object[])o_arr;
			if (arr == null && entry == null) { return null; 
			} else if (arr != null && entry == null) { return arr; 
			} else if (arr == null && entry != null) {
				Object[] r = new Object[1]; 
				r[0] = Utl.copy(entry); 
				return r; 
			} else {
				Object[] r = new Object[arr.length + 1];
				for (int i = 0 ; i < arr.length ; i++) r[i+1] = Utl.copy(arr[i]);
				r[0] = Utl.copy(entry);
				return r;
			}
			}}).addArg("array", Object[].class).addArg("entry", null);

		
		
		
		
		new Operator("get_pass", "PASS", C.PASS, new nRun() {public Object get() {
			Integer ind = ask("ind", Integer.class);
			Object[] passed_arg = ask("passed_arg", Object[].class);
			if (passed_arg == null || ind >= passed_arg.length) return null;
			return passed_arg[ind];
			}}).addVar("ind")
		.setStandRun(new nRun() {public void run() {
			pStandard stand = arg(0,pStandard.class);
			stand.process()
			.run(pTile.getRun(CT.OBTAIN_VAR), "ind", (int)-1)
			.openSec()
			.param("run_right", new nRun() {public void run() {
				nWidget triggP_w = instance.get("get_mapped_widget", nWidget.class, 
						"index_watch");
				if (triggP_w == null) return;
				for (String par : index_list) {
					nGUI.add_dropmenu_entry(par, new nRun(instance) { public void run() {
						((pInstance)builder).setVar("ind", Utl.toint(par)); }});
				}
				nGUI.open_dropmenu(triggP_w);
			}})
			.param("ref", "index_watch", "var_link_ref", "ind", 
					"var_link_class", Integer.class.getName())
			.param("width", (int)2)
			.commande(pTile.getCom(CT.ADD_WATCH))
			.closeSec()
			;
		}})
		;
		
		
		
		

		new Operator("branch", "BR", C.BRC, new nRun() {public Object get() {
			String branch_ref = ask("branch_ref", String.class);
			Object[] arg = ask("arg", Object[].class);
			pInstance tile_node = ask("tile_node", pInstance.class);
			pInstance branch = instance.patch.common_branchs.get(branch_ref);
			if (branch == null) return null;
			Object[] script = branch.get("get_instruction_script", Object[].class);
			if (script == null) return null;
			ArrayList<Object> scr = pFunc.to_array(script);
			if (pFunc.popC(scr) != C.INT) return null;
			int tot_size = pFunc.pop(scr, Integer.class);
			ArrayList<Object> as = new ArrayList<Object>();
			pFunc.pop(scr, tot_size - 2, as);
			return pFunc.branch_script_obtain(tile_node, as, Utl.duplic(arg)); 
		}})
		.addVar("branch_ref").addArg("arg", Object[].class)
		.setStandRun(new nRun() {public void run() {
			pStandard stand = arg(0,pStandard.class);
			stand.process()
			.run(pTile.getRun(CT.OBTAIN_VAR), "branch_ref", "")
			.openSec()
			.param("run_right", new nRun() {public void run() {
				nWidget triggP_w = instance.get("get_mapped_widget", nWidget.class, 
						"branch_ref_watch");
				if (triggP_w == null) return;
				for (String par : instance.patch.common_branchs.allKey()) {
					nGUI.add_dropmenu_entry(par, new nRun(instance) { public void run() {
						((pInstance)builder).setVar("branch_ref", par); }});
				}
				nGUI.open_dropmenu(triggP_w);
			}})
			.param("ref", "branch_ref_watch", "var_link_ref", "branch_ref", 
					"var_link_class", String.class.getName())
			.param("width", (int)8)
			.commande(pTile.getCom(CT.ADD_WATCH))
			.closeSec();
		}});
		
		
		
		
		

//		pFunc.defineMemberContenant(pBody.class, new nRun() {
//			public Object get() {
//				if (args.length < 2) return null;
//				Object mem_cont = arg(0, Object.class);
//				String param_ref = arg(1, String.class);
//				if (mem_cont == null || param_ref == null || 
//						!(mem_cont instanceof pBody)) return null;
//				pBody bod = (pBody)mem_cont;
//				if (!bod.hasParam(param_ref)) return null;
//				return bod.param(param_ref);
//			}
//		});
//		pFunc.defineMemberContenant(pParam.class, new nRun() {
//			public void run() {
//				if (args.length < 3) return;
//				Object mem_cont = arg(0, Object.class);
//				String data_ref = arg(1, String.class);
//				Object data = arg(2, Object.class);
//				if (mem_cont == null || data_ref == null || data == null || 
//						!(mem_cont instanceof pParam)) return;
//				pParam par = (pParam)mem_cont;
//				par.set(data_ref, data);
//			}
//			public Object get() {
//				if (args.length < 2) return null;
//				Object mem_cont = arg(0, Object.class);
//				String data_ref = arg(1, String.class);
//				if (mem_cont == null || data_ref == null || 
//						!(mem_cont instanceof pParam)) return null;
//				pParam par = (pParam)mem_cont;
//				return par.get(data_ref);
//			}
//		});
//
//		new Operator("mem", "MEM", C.MEM, new nRun() {public Object get() {
////			Utl.logn("mem");
//			String mem = ask("mem", String.class);
//			Object obj = ask("obj", Object.class);
//			if (obj == null || mem == null) return null;
////			Utl.logn("obj:"+obj+" mem:"+mem);
//			return pFunc.obtainMember(obj, mem);
//		}})
//		.addArg("mem", String.class).addArg("obj", null);

		
		
		

		new Operator("new_param", "NEWP", C.NEWP, new nRun() {public Object get() {
			String prop_name = ask("prop", String.class);
			pSpace space = PlaneApplet.app.space;
			pParam p = space.new_param(prop_name);
			return p;
		}})
		.addVar("prop")
		.setStandRun(new nRun() {public void run() {
			pStandard stand = arg(0,pStandard.class);
			stand.process()
			.run(pTile.getRun(CT.OBTAIN_VAR), "prop", "")
			.openSec()
			.param("run_right", new nRun() {public void run() {
				nWidget triggP_w = instance.get("get_mapped_widget", nWidget.class, 
						"prop_watch");
				if (triggP_w == null) return; 
				for (String par : pProperty.general_propertys.allKey()) {
					nGUI.add_dropmenu_entry(par, new nRun(instance) { public void run() {
						((pInstance)builder).setVar("prop", par); }});
				}
				nGUI.open_dropmenu(triggP_w);
			}})
			.param("ref", "prop_watch", "var_link_ref", "prop", 
					"var_link_class", String.class.getName())
			.param("width", (int)8)
			.commande(pTile.getCom(CT.ADD_WATCH))
			.closeSec();
		}});
		

		new Operator("new_body", "NEWB", C.NEWB, new nRun() {public Object get() {
			String print_name = ask("print", String.class);
			pSpace space = PlaneApplet.app.space;
			pParam bluep = null;
			if (print_name != null) {
				for (String br : space.param_pools.get("blueprint").allKey()) {
					String nm = space.param_pools.get("blueprint")
							.get(br).get("name", String.class);
					if (nm != null && nm.equals(print_name)) {
						bluep = space.param_pools.get("blueprint")
								.get(br);
						break;
					}
				}
			}
			pBody pop = pNodeSpace.new_body(bluep);
			App.ap.addDelayEvent(1,new nRun(pop) { public void run() {
				pNodeSpace.init_body((pBody)builder); }});
			return pop;
		}})
		.addVar("print")
		.setStandRun(new nRun() {public void run() {
			pStandard stand = arg(0,pStandard.class);
			stand.process()
			.run(pTile.getRun(CT.OBTAIN_VAR), "print", "")
			.openSec()
			.param("run_right", new nRun() {public void run() {
				nWidget triggP_w = instance.get("get_mapped_widget", nWidget.class, 
						"print_watch");
				if (triggP_w == null) return; 
				pSpace space = PlaneApplet.app.space;
				for (String par : space.param_pools.get("blueprint").allKey()) {
					nGUI.add_dropmenu_entry(par, new nRun(instance) { public void run() {
						((pInstance)builder).setVar("print", par); }});
				}
				nGUI.open_dropmenu(triggP_w);
			}})
			.param("ref", "print_watch", "var_link_ref", "print", 
					"var_link_class", String.class.getName())
			.param("width", (int)8)
			.commande(pTile.getCom(CT.ADD_WATCH))
			.closeSec();
		}});
		
		


		new Operator("param_get", "PG", C.PGET, new nRun() {public Object get() {
			String run_ref = ask("run_ref", String.class);
			Object[] arg = ask("arg", Object[].class);
			Object o = ask("param", Object.class);
			if (o == null || run_ref == null || !(o instanceof pParam)) return null;
			return ((pParam)o).run_get(run_ref,arg); 
		}})
		.addVar("run_ref")
		.addArg("param", null).addArg("arg", Object[].class)
		.setStandRun(new nRun() {public void run() {
			pStandard stand = arg(0,pStandard.class);
			stand.process()
			.run(pTile.getRun(CT.OBTAIN_VAR), "run_ref", "")
			.openSec()
			.param("ref", "run_ref_watch", "var_link_ref", "run_ref", 
					"var_link_class", String.class.getName())
			.param("width", (int)8)
			.commande(pTile.getCom(CT.ADD_FIELD))
			.closeSec()
			;
		}});
		
		
		
	}
	
	
		
		
		

	public static void build_instructions() {	

		float RS = nGUI.book.RS;
		
//		new Instruction("set_mem", "SMM", C.SMM, new nRun() {public Object get() {
//			Boolean active = ask("active", Boolean.class);
//			if (active == null || !active) return pFunc.C.NEXT;
//			String mem = ask("mem", String.class);
//			Object obj = ask("obj", Object.class);
//			Object data = ask("data", Object.class);
//			if (obj == null || mem == null || data == null) return pFunc.C.NEXT;
//			pFunc.setMember(obj, mem, data);
//			return pFunc.C.NEXT;
//		}})
//		.addArg("mem", String.class).addArg("obj", null).addArg("data", null)
//		.setWatched().setActivated()
//		;
		
		
		
		
		

//		new Instruction("term", "TRM", C.TERM, new nRun() {public Object get() {
//			Boolean active = ask("active", Boolean.class);
//			if (active == null || !active) return pFunc.C.NEXT;
//			String com = ask("com", String.class);
//			Object[] arg = ask("arg", Object[].class);
//			for (Object o : arg) { com += " " + Utl.to_string(o); }
//			nRun out = PlaneApplet.app.outputs.get("trm");
//			out.do_run(com);
//			return pFunc.C.NEXT;
//		}})
//		.addVar("com").addArg("arg", Object[].class)
//		.setActivated()
//		.setStandRun(new nRun() {public void run() {
//			pStandard stand = arg(0,pStandard.class);
//			stand.process()
//			.run(pTile.getRun(CT.OBTAIN_VAR), "com", "")
//			.openSec()
//			.param("var_link_ref", "com", 
//					"var_link_class", String.class.getName())
//			.param("width", (int)8)
//			.commande(pTile.getCom(CT.ADD_FIELD))
//			.closeSec()
//			;
//		}})
//		;
		
		
		
		
		
		
		new Instruction("func", "F", C.FUNC, new nRun() {public Object get() {
			Boolean active = ask("active", Boolean.class);
			if (active == null || !active) return pFunc.C.NEXT;
			String func_ref = ask("func_ref", String.class);
			Object[] arg = ask("arg", Object[].class);
			pInstance tile_node = ask("tile_node", pInstance.class);
			pInstance func = instance.patch.common_functions.get(func_ref);
			if (func == null) return null;
			Object[] script = func.get("get_instruction_script", Object[].class);
			if (script == null) return null;
			pFunc.func_script_run(tile_node, script, Utl.duplic(arg)); 
			return pFunc.C.NEXT;
		}})
		.addVar("func_ref").addArg("arg", Object[].class) 
		.setActivated()
		.setStandRun(new nRun() {public void run() {
			pStandard stand = arg(0,pStandard.class);
			stand.process()
			.run(pTile.getRun(CT.OBTAIN_VAR), "func_ref", "")
			.openSec()
			.param("run_right", new nRun() {public void run() {
				nWidget triggP_w = instance.get("get_mapped_widget", nWidget.class, 
						"func_ref_watch");
				if (triggP_w == null) return; 
				for (String par : instance.patch.common_functions.allKey()) {
					nGUI.add_dropmenu_entry(par, new nRun(instance) { public void run() {
						((pInstance)builder).setVar("func_ref", par); }});
				}
				nGUI.open_dropmenu(triggP_w);
			}})
			.param("ref", "func_ref_watch", "var_link_ref", "func_ref", 
					"var_link_class", String.class.getName())
			.param("width", (int)8)
			.commande(pTile.getCom(CT.ADD_WATCH))
			.closeSec()
			;
		}})
		;
		
		
		
		
		
		new Instruction("set_output", "O", C.SETO, new nRun() {public Object get() {
			Boolean active = ask("active", Boolean.class);
			if (active == null || !active) return pFunc.C.NEXT;
			String out_ref = ask("out_ref", String.class);
			nRun out = PlaneApplet.app.outputs.get(out_ref);
			Object r = ask("data");
			if (r != null && out != null) { 
				if (ask("exec_in_instance", Boolean.class)) 
					instance.setVar("watch", Utl.to_string(r)); 
				out.do_run(r); } 
			return pFunc.C.NEXT;
		}})
		.addVar("out_ref")
		.addArg("data", null)
		.setWatched().setActivated()
		.setStandRun(new nRun() {public void run() {
			pStandard stand = arg(0,pStandard.class);
			stand.process()
			.run(pTile.getRun(CT.OBTAIN_VAR), "out_ref", "")
			.openSec()
			.param("ref", "out_ref_watcher", "var_link_ref", "out_ref", 
					"var_link_class", String.class.getName())
			.param("width", (int)8)
			.param("run_right", new nRun() {public void run() {
				nWidget triggP_w = instance.get("get_mapped_widget", nWidget.class, 
						"out_ref_watcher");
				if (triggP_w == null) return;
				for (String par : PlaneApplet.app.outputs.allKey()) {
					nGUI.add_dropmenu_entry(par, new nRun(instance) { public void run() {
						((pInstance)builder).setVar("out_ref", par); }});
				}
				nGUI.open_dropmenu(triggP_w);
			}})
			.commande(pTile.getCom(CT.ADD_WATCH))
			.closeSec();
		}})
		;
		
		
		
		new Instruction("set_reg_out", "RO", C.SETR, new nRun() {public Object get() { 
			Boolean active = ask("active", Boolean.class);
			if (active == null || !active) return pFunc.C.NEXT;
			String reg_ref = ask("reg_ref", String.class);
			Object r = ask("data");
			if (r == null) return pFunc.C.NEXT;
			if (ask("exec_in_instance", Boolean.class)) 
				instance.setVar("watch", Utl.to_string(r));
			pInstance node = ask("tile_node", pInstance.class);
			if (node != null) node.run("send_to_reg", reg_ref, r);
			return pFunc.C.NEXT;
		}})
		.addVar("reg_ref")
		.addArg("data", null)
		.setWatched().setActivated()
		.setStandRun(new nRun() {public void run() {
			pStandard stand = arg(0,pStandard.class);
			stand.process()
			.run(pTile.getRun(CT.OBTAIN_VAR), "reg_ref", "")
			.openSec()
			.param("ref", "reg_ref_watch", "var_link_ref", "reg_ref", "var_link_class", String.class.getName())
			.param("width", (int)8)
			.commande(pTile.getCom(CT.ADD_FIELD))
			.closeSec();
		}})
		;
		
		
		
		
		
		

		new Instruction("close", "CLS", C.CLS, new nRun() {public Object get() {
			return pFunc.C.NEXT; }})
		.setStandRun(new nRun() {public void run() {
			pStandard stand = arg(0,pStandard.class);
			stand.process()
			.openSec().param("ref", "label", "text", "close", "width", (int)13)
			.commande(pTile.getCom(CT.ADD_WIDGET)).closeSec();
		}});
		
		new Instruction("return", "->", C.RTRN, new nRun() {public Object get() {
			return pFunc.C.STOP; }})
		.setStandRun(new nRun() {public void run() {
			pStandard stand = arg(0,pStandard.class);
			stand.process()
			.openSec().param("ref", "label", "text", "return", "width", (int)13)
			.commande(pTile.getCom(CT.ADD_WIDGET)).closeSec();
		}});
		

		new Instruction("if", "IF", C.IF, new nRun() {public Object get() {
			boolean test = false;
			Boolean r = ask("test", Boolean.class);
			if (r != null) test = r;
			Boolean active = ask("active", Boolean.class);
			if (active == null || !active) test = false;
			if (ask("exec_in_instance", Boolean.class)) 
				instance.setVar("watch", Utl.to_string(test));
			if (test) return pFunc.C.NEXT;
			else return pFunc.C.JUMP;
		}})
		.addArg("test", Boolean.class)
		.setWatched().setActivated()
		;
		
		
		
		
		
		
		
		
		
		
		new Instruction("set_param", "SP", C.SETP, new nRun() {public Object get() {
			Object o1 = ask("data", Object.class);
			Object o2 = ask("body", Object.class);
			Boolean active = ask("active", Boolean.class);
			if (active == null || !active) return pFunc.C.NEXT;
			String param_ref = ask("param_ref", String.class);
			String data_ref = ask("data_ref", String.class);
			if (o1 != null && o2 != null && (o2 instanceof pBody)) {
				pBody bod = (pBody)o2;
				pParam par = bod.param(param_ref);
				if (par != null && par.has(data_ref, o1.getClass())) {
					if (ask("exec_in_instance", Boolean.class)) 
						instance.setVar("watch", Utl.to_string(o1));
					par.set(data_ref, o1);
				}
			}
			return pFunc.C.NEXT;
		}})
		.addVar("param_ref").addVar("data_ref")
		.addArg("data", null).addArg("body", null)
		.setWatched().setActivated()
		.setStandRun(new nRun() {public void run() {
			pStandard stand = arg(0,pStandard.class);
			stand.process()
			.run(pTile.getRun(CT.OBTAIN_VAR), "param_ref", "")
			.openSec()
			.param("ref", "param_ref_watch", "var_link_ref", "param_ref", 
					"var_link_class", String.class.getName())
			.param("width", (int)8)
			.param("run_right", new nRun() {public void run() {
				nWidget triggP_w = instance.get("get_mapped_widget", nWidget.class, 
						"param_ref_watch");
				if (triggP_w == null) return;
				for (String par : pProperty.body_propertys.allKey()) {
					nGUI.add_dropmenu_entry(par, new nRun(instance) { public void run() {
						((pInstance)builder).setVar("param_ref", par); 
						((pInstance)builder).setVar("data_ref", ""); }});
				}
				nGUI.open_dropmenu(triggP_w);
			}})
			.commande(pTile.getCom(CT.ADD_WATCH))
			.closeSec()
			.run(pTile.getRun(CT.OBTAIN_VAR), "data_ref", "")
			.openSec()
			.param("ref", "data_ref_watch", "var_link_ref", "data_ref", 
					"var_link_class", String.class.getName())
			.param("width", (int)8)
			.param("run_right", new nRun() {public void run() {
				nWidget triggP_w = instance.get("get_mapped_widget", nWidget.class, 
						"data_ref_watch");
				if (triggP_w == null) return;
				String par_ref = instance.getVar("param_ref", String.class);
				pProperty prop = pProperty.get(par_ref);
				if (prop == null) return;
				for (Map.Entry<Class<?>, nMap<Integer>> me : prop.data_vals.entrySet()) {
//					Class<?> ct = me.getKey();
					for (Map.Entry<String,Integer> map_me : me.getValue().entrySet()) {
						String k = map_me.getKey();
						nGUI.add_dropmenu_entry(k, new nRun(instance) { public void run() {
							((pInstance)builder).setVar("data_ref", k); }});
					}
				}
				nGUI.open_dropmenu(triggP_w);
			}})
			.commande(pTile.getCom(CT.ADD_WATCH))
			.closeSec()
			;
		}});
		

		new Instruction("param_set", "PS", C.PSET, new nRun() {public Object get() {
			Object o1 = ask("data", Object.class);
			Object o2 = ask("param", Object.class);
			Boolean active = ask("active", Boolean.class);
			if (active == null || !active) return pFunc.C.NEXT;
			String data_ref = ask("data_ref", String.class);
			if (o1 != null && o2 != null && (o2 instanceof pParam)) {
				pParam par = (pParam)o2;
				if (par != null && par.has(data_ref, o1.getClass())) {
					if (ask("exec_in_instance", Boolean.class)) 
						instance.setVar("watch", Utl.to_string(o1));
					par.set(data_ref, o1);
				}
			}
			return pFunc.C.NEXT;
		}})
		.addVar("data_ref")
		.addArg("data", null).addArg("param", null)
		.setWatched().setActivated()
		.setStandRun(new nRun() {public void run() {
			pStandard stand = arg(0,pStandard.class);
			stand.process()
			.run(pTile.getRun(CT.OBTAIN_VAR), "data_ref", "")
			.openSec()
			.param("ref", "data_ref_watch", "var_link_ref", "data_ref", 
					"var_link_class", String.class.getName())
			.param("width", (int)8)
//			.param("run_right", new nRun() {public void run() {
//				nWidget triggP_w = instance.get("get_mapped_widget", nWidget.class, 
//						"data_ref_watch");
//				if (triggP_w == null) return;
//				String par_ref = instance.getVar("param_ref", String.class);
//				pProperty prop = pProperty.get(par_ref);
//				if (prop == null) return;
//				for (Map.Entry<Class<?>, nMap<Integer>> me : prop.data_vals.entrySet()) {
////					Class<?> ct = me.getKey();
//					for (Map.Entry<String,Integer> map_me : me.getValue().entrySet()) {
//						String k = map_me.getKey();
//						nGUI.add_dropmenu_entry(k, new nRun(instance) { public void run() {
//							((pInstance)builder).setVar("data_ref", k); }});
//					}
//				}
//				nGUI.open_dropmenu(triggP_w);
//			}})
			.commande(pTile.getCom(CT.ADD_WATCH))
			.closeSec()
			;
		}});
		

		new Instruction("run_param", "RP", C.RUNP, new nRun() {public Object get() {
			String run_ref = ask("run_ref", String.class);
			Object[] arg = ask("arg", Object[].class);
			Object o = ask("param", Object.class);
			if (o == null || run_ref == null || !(o instanceof pParam)) return pFunc.C.NEXT;
			pParam par = (pParam)o;
			if (par != null) { par.run(run_ref,arg); }
			return pFunc.C.NEXT;
		}})
		.addVar("run_ref")
		.addArg("param", null).addArg("arg", Object[].class)
		.setWatched().setActivated()
		.setStandRun(new nRun() {public void run() {
			pStandard stand = arg(0,pStandard.class);
			stand.process()
			.run(pTile.getRun(CT.OBTAIN_VAR), "run_ref", "")
			.openSec()
			.param("ref", "run_ref_watch", "var_link_ref", "run_ref", 
					"var_link_class", String.class.getName())
			.param("width", (int)8)
			.commande(pTile.getCom(CT.ADD_FIELD))
			.closeSec()
			;
		}});
		
		
		
		
	}
	
	
}
