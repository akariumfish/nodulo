package patch;

import data.*;
import gui.*;
import patch.pTile.CT;
import util.*;
import aa_nodulo.*;
import app.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.math.Vector2;


public class pFunc {

	public static void build() {

		build_codes();

		pFuncBook.build_operators();
		
		pFuncBook.build_instructions();
		
		build_tile();

	}
	
	public static void build_tile() {
		pStandard stand_stack_start = pStandard.standards.get("tile_model_stack_start");
		pStandard stand_branch_start = pStandard.standards.get("tile_model_branch_start");
		
		
		for (Operator ope : operators.all()) {
			
			pTile.tile_models_short.put(ope.ref, ope.pic);

			pStandard stand = pTile.newTileModel(ope.ref).process()
			.openSec().param("ref", "label", "text", ope.pic, "width", (int)3)
			.commande(pTile.getCom(CT.ADD_WIDGET)).closeSec()
			.getStand();
			
			for (int i = 0 ; i < ope.priv_nb ; i++) {
				stand.process()
				.run(pTile.getRun(CT.OBTAIN_VAR), ope.privs[i].ref, ope.privs[i].def);
			}
			
			for (int i = 0 ; i < ope.arg_nb ; i++) {
				Data ma = ope.args[i];
				if (ma.arg_class == Object[].class) 
					stand.run(pTile.getRun(CT.ADD_OBTAIN_PLUG), ma.ref, "arr");
				else if (ma.arg_class != null)
					stand.run(pTile.getRun(CT.ADD_OBTAIN_PLUG), ma.ref, 
							Utl.type_class_type.get(ma.arg_class)); 
				else stand.run(pTile.getRun(CT.ADD_OBTAIN_PLUG), ma.ref, "all"); }
			
			nRun ope_run = new nRun() {public Object get() {
				return ope.run.do_get(instance, new nRun() {public void run() {
					String r = arg(0, String.class);
					Object d = arg(1, Object.class);
					pInstance tile = instance.object("tile", pInstance.class);
					for (int i = 0 ; i < ope.priv_nb ; i++) 
						if (ope.privs[i].ref.equals(r)) tile.setVar(r, d);
				}
				public Object get() {
					String r = arg(0, String.class);
					if (r.equals("exec_in_instance")) return true;
					pInstance tile = instance.object("tile", pInstance.class);
					if (r.equals("tile_node")) return tile.getInst("tile_node");

					if (!ope.data_type.hasKey(r)) {
						Utl.logn("ERROR : pFunc.build_tile.ope_run : "
								+ "ope dont have key "+r); 
						return null; } 
					if (ope.data_type.get(r) == scr_data_type[0] && tile.hasVar(r)) {
						Object rt = tile.getVar(r);
//						Utl.logn("op get var "+r+" > "+rt);
						return rt;
					} else if (ope.data_type.get(r) == scr_data_type[1]) {
						Object rt = tile.get("plug_obtain", Object.class, r);
//						Utl.logn("op get arg "+r+" > "+rt);
						return rt; 
					} else if (ope.data_type.get(r) == scr_data_type[2] && tile.hasVar(r)) {
						Object rt = tile.getVar(r);
//						Utl.logn("op get priv "+r+" > "+rt);
						return rt; 
					} 

					Utl.logn("ERROR : pFunc.build_tile.ope_run : "
							+ "ope have key "+r+" but didnt found a return"); 
					return null;
				}});
			}};
			
			String filter = ope.ret_filter;
			if (filter == null && ope.ret_class != null) 
				filter = Utl.type_class_type.get(ope.ret_class);
			if (filter != null) stand.openSec().param("offer", ope_run)
				.run(pTile.getRun(CT.ADD_OFFER_PLUG), "out", filter).closeSec();
			else stand.openSec().param("offer", ope_run)
			.run(pTile.getRun(CT.ADD_OFFER_PLUG), "out").closeSec(); 
			
			if (ope.stand_run != null) {
				stand.run(ope.stand_run); }
			
			nRun add_at_run = new nRun() {public Object get() {
				String this_plug_ref = arg(0, String.class);
				instance.get("add_at", pInstance.class, this_plug_ref, ope.ref, "out");
				for (int i = 0 ; i < ope.var_nb ; i++) 
					instance.get("set_var", pInstance.class, ope.vars[i].ref, 
							arg(i+1, Object.class));
				if (ope.arg_nb == 0) instance.get("get_last", pInstance.class);
				return instance;
			}};
			
			stand_stack_start.newRun("add_"+ope.ref+"_at", add_at_run);
			stand_branch_start.newRun("add_"+ope.ref+"_at", add_at_run);
			
			stand.newRun("get_branch_script", new nRun() {public Object get() { 
				return get_branch_script(instance); }});
			
			stand_to_ope.put(stand,ope);
		}
		

		nRun add_arr_run = new nRun() {public Object get() {
			String this_plug_ref = arg(0, String.class);
			instance.get("add_at", pInstance.class, this_plug_ref, "arr", "out");
			if (args.length >= 2) {
				Object ent = arg(1, Object.class);
				instance.get("add_at", pInstance.class, "entry", 
						Utl.type_class_type.get(ent.getClass()), "out");
				instance.get("set_var", pInstance.class, "value", ent);
				instance.get("get_last", pInstance.class);
				int cnt = 2;
				while (args.length >= cnt+1) {
					instance.get("add_at", pInstance.class, "array", "arr", "out");
					Object nent = arg(cnt, Object.class);
					instance.get("add_at", pInstance.class, "entry", 
							Utl.type_class_type.get(nent.getClass()), "out");
					instance.get("set_var", pInstance.class, "value", nent);
					instance.get("get_last", pInstance.class);
					cnt++;
				}
				while (cnt > 2) {
					instance.get("get_last", pInstance.class);
					cnt--; }
				instance.get("get_last", pInstance.class);
			}
			return instance;
		}};
		stand_stack_start.replaceRun("add_arr", add_arr_run);
		stand_branch_start.replaceRun("add_arr", add_arr_run);
		
		nRun add_arr_at_run = new nRun() {public Object get() {
			String this_plug_ref = arg(0, String.class);
			instance.get("add_at", pInstance.class, this_plug_ref, "arr", "out");
			if (args.length >= 2) {
				Object ent = arg(1, Object.class);
				instance.get("add_at", pInstance.class, "entry", 
						Utl.type_class_type.get(ent.getClass()), "out");
				instance.get("set_var", pInstance.class, "value", ent);
				instance.get("get_last", pInstance.class);
				int cnt = 2;
				while (args.length >= cnt+1) {
					instance.get("add_at", pInstance.class, "array", "arr", "out");
					Object nent = arg(cnt, Object.class);
					instance.get("add_at", pInstance.class, "entry", 
							Utl.type_class_type.get(nent.getClass()), "out");
					instance.get("set_var", pInstance.class, "value", nent);
					instance.get("get_last", pInstance.class);
					cnt++;
				}
//				while (cnt > 2) {
//					instance.get("get_last", pInstance.class);
//					cnt--; }
//				instance.get("get_last", pInstance.class);
			}
			return instance;
		}};

		stand_stack_start.replaceRun("add_arr_at", add_arr_at_run);
		stand_branch_start.replaceRun("add_arr_at", add_arr_at_run);
		
		
		stand_branch_start.newRun("get_instruction_script", new nRun() {public Object get() {
			return get_stack_branch_script(instance); }});
		
		

		for (Instruction ins : instructions.all()) {
			
			pTile.tile_models_short.put(ins.ref, ins.pic);
			
			if (ins.activated) ins.addPrivVar("active", true);
			
			pStandard stand = pTile.newTileModel(ins.ref);
			pProcess proc = stand.process();
			
			proc.openSec().param("ref", ins.ref+"_label", "text", ins.pic, "width", (int)3)
			.commande(pTile.getCom(CT.ADD_WIDGET)).closeSec();
			

			for (int i = 0 ; i < ins.priv_nb ; i++) {
				stand.process()
				.run(pTile.getRun(CT.OBTAIN_VAR), ins.privs[i].ref, ins.privs[i].def);
			}
			
			for (int i = 0 ; i < ins.arg_nb ; i++) {
				Data ma = ins.args[i];
				if (ma.arg_class == Object[].class) 
					stand.run(pTile.getRun(CT.ADD_OBTAIN_PLUG), ma.ref, "arr");
				else if (ma.arg_class != null)
				stand.run(pTile.getRun(CT.ADD_OBTAIN_PLUG), ma.ref, 
						Utl.type_class_type.get(ma.arg_class)); 
				else stand.run(pTile.getRun(CT.ADD_OBTAIN_PLUG), ma.ref, "all"); }

			if (ins.stand_run != null) {
				stand.run(ins.stand_run); }

			if (ins.watched) {
				proc.run(pTile.getRun(CT.OBTAIN_VAR), "watch", "")
				.openSec()
				.param("ref", "watch_watch", "var_link_ref", "watch", 
						"var_link_class", String.class.getName())
				.param("width", (int)10)
				.commande(pTile.getCom(CT.ADD_WATCH))
				.closeSec();
			}

//			if (ins.activated) {
//				proc.openSec().param("ref", "activate_switch", "var_link_ref", "activate", 
//						"var_link_class", Boolean.class.getName(), 
//						"width", (int)3, "text", "ON", "def", true)
//				.commande(pTile.getCom(CT.ADD_SWITCH)).closeSec();
//			}
			
			nRun ins_run = new nRun() {public Object get() {
				return ins.run.do_get(instance, C.class, new nRun() {public void run() {
					String r = arg(0, String.class);
					Object d = arg(1, Object.class);
					for (int i = 0 ; i < ins.priv_nb ; i++) 
						if (ins.privs[i].ref.equals(r)) instance.setVar(r, d);
				}
				public Object get() {
					String r = arg(0, String.class);
					if (r.equals("exec_in_instance")) return true;
					if (r.equals("tile_node")) return instance.getInst("tile_node");
					
					if (!ins.data_type.hasKey(r)) {
						Utl.logn("ERROR : pFunc.build_tile.ins_run : "
								+ "ins dont have key "+r); 
						return null; } 
					if (ins.data_type.get(r) == scr_data_type[0] && instance.hasVar(r)) {
						return instance.getVar(r);
					} else if (ins.data_type.get(r) == scr_data_type[1]) {
						return instance.get("plug_obtain", Object.class, r); 
					} else if (ins.data_type.get(r) == scr_data_type[2] && instance.hasVar(r)) {
						return instance.getVar(r); }
					
					Utl.logn("ERROR : pFunc.build_tile.ins_run : "
							+ "ins have key "+r+" but didnt found a return"); 
					return null;
				}});
			}};
			
			stand.openSec().param("run_event", ins_run)
			.run(pTile.getRun(CT.ADD_RUN_PLUGS)).closeSec();
			
			stand_stack_start
			.newRun("add_"+ins.ref, new nRun() { public Object get() {
				instance.get_this("add_at", "run_out", ins.ref, "run_in");
				for (int i = 0 ; i < ins.var_nb ; i++)  
					instance.get_this("set_var", ins.vars[i].ref, arg(i, Object.class));
				for (int i = 0 ; i < ins.priv_nb ; i++) 
					if (i+ins.var_nb < args.length) instance.get_this("set_var", 
							ins.privs[i].ref, arg(i+ins.var_nb, Object.class));
				return instance;
			}})
			;
			
			stand.newRun("get_instruction_script", new nRun() {public Object get() {
				return get_instruction_script(instance); }});

			stand_to_ins.put(stand,ins);
		}
		 
		stand_stack_start.newRun("get_instruction_script", new nRun() {public Object get() {
			return get_stack_script(instance); }});

		
		
		

	}

	public static Object[] get_stack_branch_script(pInstance inst) {
		pInstance instance = inst;
		if (instance.get("get_plug", pInstance.class, "in") != null && 
				instance.get("get_plug", pInstance.class, "in")
				.getInst("plugged") != null) {
//			instance.getInst("tile_node").run("reset_next_id");
			Object[] as = instance.get("get_plug", pInstance.class, "in")
					.getInst("plugged").getInst("tile")
					.get("get_branch_script", Object[].class);
			if (as == null) {
				Utl.logn("ERROR : Tile branch_start get_full_branch_script : "
						+ "the branch <in> did not return a script"); 
				return new ScriptBuilder().com(C.NULL).get(); 
			} else {
				return new ScriptBuilder().com(as).get();
			}
		} else {
			return new ScriptBuilder().com(C.NULL).get();
		}
	}

	public static Object[] get_stack_script(pInstance inst) {
		pInstance instance = inst;
//		instance.getInst("tile_node").run("reset_next_id");
		ScriptBuilder script_builder = new ScriptBuilder();
		script_builder.com(C.START);
		pInstance run_out = instance.get("get_plug", pInstance.class, "run_out");
		if (run_out != null && run_out.getInst("plugged") != null) {
			pInstance next = run_out.getInst("plugged").getInst("tile");
			boolean pass = true;
			int cnt = 0;
			while (pass && next != null && cnt < 500) {
				Object[] as = next.get("get_instruction_script", Object[].class);
				if (as != null) {
					script_builder.com(new ScriptBuilder().com(as).get());
				} else {
					Utl.logn("ERROR : Tile stack_start get_instruction_script : "
							+ "an instruction did not return a script"); 
					script_builder.com(new ScriptBuilder().com(C.NULL).get()); }
				cnt++; pass = false;
				run_out = next.get("get_plug", pInstance.class, "run_out");
				if (run_out != null && run_out.getInst("plugged") != null) {
					next = run_out.getInst("plugged").getInst("tile"); 
					pass = true; 
				}
			}
			if (cnt > 498) Utl.logn("WARNING : pTile stack_start.run(get_instruction_script) : "
					+ "looped throug too many instruction and stopped ");
		}
		return script_builder.get();
	}
	
	public static Object[] get_instruction_script(pInstance inst) {
		pInstance instance = inst;
		Instruction ins = stand_to_ins.get(inst.stand);
		if (ins == null) {
			Utl.logn("ERROR : pFunc.get_instruction_script : "
					+ "cant find ins");
			return null; }
		
		ScriptBuilder script_builder = new ScriptBuilder();
		script_builder.com(ins.code);
		
//		int id = instance.getInst("tile_node").get("get_next_id", Integer.class);
//		script_builder.com(id);
		script_builder.com(instance.pool_ref);
		
		for (int i = 0 ; i < ins.var_nb ; i++) {
			script_builder.com(i);
			if (instance.hasVar(ins.vars[i].ref)) {
				script_builder.com(instance.getVar(ins.vars[i].ref)); }
			else {
				script_builder.com(C.NULL);
			}
		}

		for (int i = 0 ; i < ins.priv_nb ; i++) {
			script_builder.com(i);
			if (instance.hasVar(ins.privs[i].ref)) {
				script_builder.com(instance.getVar(ins.privs[i].ref)); }
			else {
				script_builder.com(C.NULL);
			}
		}
		
		for (int i = 0 ; i < ins.arg_nb ; i++) {
			script_builder.com(i);
			String arg_ref = Utl.copy(ins.args[i].ref);
//			Data ma = ins.args[i];
			if (instance.get("get_plug", pInstance.class, arg_ref) != null && 
					instance.get("get_plug", pInstance.class, arg_ref)
					.getInst("plugged") != null) {
				Object[] as = instance.get("get_plug", pInstance.class, arg_ref)
						.getInst("plugged").getInst("tile")
						.get("get_branch_script", Object[].class);
				if (as != null) {
					script_builder.com(new ScriptBuilder().com(as).get());  
				} else {
					Utl.logn("ERROR : Tile "+ins.ref+" get_instruction_script : "
							+ "the branch <"+arg_ref+"> did not return a script"); 
				}
			} else {
				script_builder.com(C.NULL); 
			}
		}
		script_builder.com(C.SCRIPT_END); 
		return script_builder.get();
	}
	
	public static Object[] get_branch_script(pInstance inst) {
		pInstance instance = inst;
		Operator ope = stand_to_ope.get(inst.stand);
		if (ope == null) {
			Utl.logn("ERROR : pFunc.get_branch_script : "
					+ "cant find ope");
			return null; }
		
//		Utl.logn(""+instance.pool_ref+" "+ope.ref+" make script");
		
		ScriptBuilder script_builder = new ScriptBuilder();
		script_builder.com(ope.code);

//		int id = instance.getInst("tile_node").get("get_next_id", Integer.class);
//		script_builder.com(id);
		script_builder.com(instance.pool_ref);
		
		for (int i = 0 ; i < ope.var_nb ; i++) {
			script_builder.com(i);
			if (instance.hasVar(ope.vars[i].ref))
				script_builder.com(instance.getVar(ope.vars[i].ref));
			else script_builder.com(C.NULL);
		}

		for (int i = 0 ; i < ope.priv_nb ; i++) {
			script_builder.com(i);
			if (instance.hasVar(ope.privs[i].ref))
				script_builder.com(instance.getVar(ope.privs[i].ref));
			else script_builder.com(C.NULL);
		}
		
		for (int i = 0 ; i < ope.arg_nb ; i++) {
			script_builder.com(i);
			String arg_ref = Utl.copy(ope.args[i].ref);
//			Data ma = ope.args[i];
			if (instance.get("get_plug", pInstance.class, arg_ref) != null && 
					instance.get("get_plug", pInstance.class, arg_ref)
					.getInst("plugged") != null) {
				pInstance plug_tile = instance.get("get_plug", 
						pInstance.class, arg_ref)
						.getInst("plugged").getInst("tile");
				
//				Utl.logn(""+instance.pool_ref+" "+ope.ref+" do get script at "+
//						arg_ref+" for "+plug_tile.pool_ref);
				
				Object[] as = plug_tile.get("get_branch_script", Object[].class);
				if (as != null) {
					script_builder.com(new ScriptBuilder().com(as).get()); 
				} else {
					Utl.logn("ERROR : Tile "+ope.ref+" get_branch_script : "
							+ "the branch <"+arg_ref+"> did not return a script"); 
				}
			} else {
//				Utl.logn("WARNING : Tile "+ope.ref+" get_branch_script : "
//						+ "the branch <"+ma.ref+"> is unplugged"); 
				script_builder.com(C.NULL); 
			}
		}
		script_builder.com(C.SCRIPT_END); 
		return script_builder.get();
	}
	
	
	public static void func_script_run(pInstance inst, Object[] sc, Object[] passed_arg) {
		if (sc == null) return;
		ArrayList<Object> script = to_array(sc);
		if (popC(script) != C.INT) return;
		int tot_size = pop(script, Integer.class);
		if (script.size() + 2 != tot_size) { Utl.logn("ERROR : pFunc.func_script_run() : "
				+ "script.size() != tot_size"); return; }
		if (popC(script) != C.CODE) return;
		if (popC(script) != C.START) return;

		pInstance result_inst = inst;
		Object[] result_passed_arg = Utl.duplic(passed_arg);
		
		boolean pass = true; boolean run_script = true; int cnt = 0;

		while(script.size() > 0 && popC(script) == C.INT && pass && cnt < 500) {
			cnt++; pass = false;
			int ins_size = pop(script, Integer.class);
			ArrayList<Object> ins_script = new ArrayList<Object>();
			pop(script, ins_size - 2, ins_script);
			
			if (run_script) {
				C code = instruction_script_run(result_inst, ins_script, result_passed_arg);
				if (code == pFunc.C.NEXT) pass = true;
				else if (code == pFunc.C.STOP) pass = false;
				else if (code == pFunc.C.JUMP) {
					pass = true; run_script = false;
				}
			} else if (ins_script.size() > 3) {
				if (int_to_code.get((int)ins_script.get(3)) == C.CLS) {
					pass = true; run_script = true;
				} else {
					pass = true; 
				}
			} else {
				pass = true; 
			}
			
		}
		if (cnt > 498) Utl.logn("WARNING : pFunc func_script_run : "
				+ "looped throug too many instruction and stopped ");
	}
	
	public static C instruction_script_run(
			pInstance inst, ArrayList<Object> script, Object[] passed_arg) {
		Virtual virt = newVirtual();

		if (!virt.pop_result_from_script(inst, script, passed_arg)) { 
			Utl.logn("ERROR : pFunc.instruction_script_run : "
					+ "not valid"); virt.free(); return C.NEXT; }
		
		virt.obtain_args_from_branchs();
		
		C result = virt.execute(inst, C.class);
		
//		Utl.logg("instruc: "+code.name()+" "+ins_id);

		pInstance parent_inst = virt.result_inst.patch.sheets.get("function")
				.tile_pool.get(virt.inst_ref);
		if (parent_inst != null) { parent_inst.run("highlight"); }

		virt.free();
		
		return result;
	}
	public static Object branch_script_obtain(
			pInstance inst, ArrayList<Object> script, Object[] passed_arg) {
		Virtual virt = newVirtual();

		if (!virt.pop_result_from_script(inst, script, passed_arg)) { 
			Utl.logn("ERROR : pFunc.branch_script_obtain : "
					+ "not valid"); virt.free(); return null; }
		
		virt.obtain_args_from_branchs();

		Object result = virt.execute(inst);
		
		pInstance parent_inst = virt.result_inst.patch.sheets.get("function")
				.tile_pool.get(virt.inst_ref);
		if (parent_inst != null) parent_inst.run("highlight");
		
		if (parent_inst != null && result != null && Utl.type_is_used(result.getClass()) && 
				parent_inst.get("get_plug", pInstance.class, "out")
				.getInst("plugged") != null && 
				parent_inst.get("get_plug", pInstance.class, "out")
				.getInst("plugged").getInst("tile").hasVar("watch")) {
			parent_inst.get("get_plug", pInstance.class, "out")
			.getInst("plugged").getInst("tile").setVar("watch", Utl.to_string(result)); }

//		Utl.logg(""+result); 
		
		virt.free();
		return result;
	}

	
	
	
	
	
	
	public static void dispose() { virtual_pool.dispose(); }
	public static void freeAllVirtual() { virtual_pool.freeAll(); }
	public static final nQuickPool<Virtual> virtual_pool = new nQuickPool<Virtual>() {
		protected Virtual newObject() { return new Virtual(); } };
	public static Virtual newVirtual() { return virtual_pool.obtain(); }
	
	public static class Virtual implements nPool.Poolable {
		C code = null;
		int tile_id = -1;
		String inst_ref = null;
		Scriptable scr = null;
		pInstance result_inst = null;
		Object[] result_passed_arg = new Object[MAX_INFO];
		Object[] result_vars = new Object[MAX_INFO];
		Object[] result_args = new Object[MAX_INFO];
		Object[] result_privs = new Object[MAX_INFO];
		ArrayList<Object>[] script_args = new ArrayList[MAX_INFO];
		public boolean pop_result_from_script(
				pInstance inst, ArrayList<Object> script, Object[] passed_arg) { 
			if (script == null || script.size() == 0) {
				Utl.logn("ERROR : pFunc.Virtual.pop_result_from_script : "
						+ "no script"); return false; }
			if (popC(script) != C.INT) {
				Utl.logn("ERROR : pFunc.Virtual.pop_result_from_script : "
						+ "no script size"); return false; }
			int tot_size = pop(script, Integer.class);
			if (script.size() + 2 != tot_size) { Utl.logn("ERROR : "
					+ "pFunc.Virtual.pop_result_from_script : script.size() != tot_size"
					+ " " + script.size() +" "+ tot_size); 
					return false; }

			result_inst = inst;
			if (result_inst == null) {
				Utl.logn("ERROR : pFunc.Virtual.pop_result_from_script : "
						+ "result_inst is null"); return false; }
			set_result_passed_arg(passed_arg);
			
			if (popC(script) != C.CODE) {
				Utl.logn("ERROR : pFunc.Virtual.pop_result_from_script : "
						+ "no start code"); return false; }
			code = popC(script);
//			if (popC(script) != C.INT) {
//				Utl.logn("ERROR : pFunc.Virtual.pop_result_from_script : "
//						+ "no code for tile id"); return false; }
//			tile_id = pop(script, Integer.class);
			if (popC(script) != C.STR) {
				Utl.logn("ERROR : pFunc.Virtual.pop_result_from_script : "
						+ "no code for instance ref"); return false; }
			inst_ref = pop(script, String.class);
			scr = code_to_ins.get(code);
			if (scr == null) scr = code_to_ope.get(code);
			if (scr == null) {
				Utl.logn("ERROR : pFunc.Virtual.pop_result_from_script : "
						+ "cant find scriptable class"); return false; }

//			for (int i = 0 ; i < scr.priv_vars.size() ; i++) 
//			inst.run("add_temp", "ins_"+tile_id+"_"+scr.priv_vars.get(i).var_ref, 
//					scr.priv_vars.get(i).def);

			for (int i = 0 ; i < scr.var_nb ; i++) {
				if (popC(script) != C.INT || pop(script, Integer.class) != i) {
					Utl.logn("ERROR : pFunc.Virtual.pop_result_from_script : "
							+ "bad index while popping vars"); }
				C c = popC(script);
				if (c == C.CODE) {
					if (popC(script) != C.NULL) {
						Utl.logn("ERROR : pFunc.Virtual.pop_result_from_script : "
								+ "bad result while popping vars");
					} result_vars[i] = null; continue; }
				Class<?> cl = code_to_class.get(c);
				result_vars[i] = pop(script, cl); 
				if (result_vars[i] == null) {
					Utl.logn("ERROR : pFunc.Virtual.pop_result_from_script : "
							+ "null result while popping vars");
				} 
			}

			for (int i = 0 ; i < scr.priv_nb ; i++) {
				if (popC(script) != C.INT || pop(script, Integer.class) != i) {
					Utl.logn("ERROR : pFunc.Virtual.pop_result_from_script : "
							+ "bad index while popping privs"); }
				C c = popC(script);
				if (c == C.CODE) {
					if (popC(script) != C.NULL) {
						Utl.logn("ERROR : pFunc.Virtual.pop_result_from_script : "
								+ "bad result while popping privs");
					} result_privs[i] = null; continue; }
				Class<?> cl = code_to_class.get(c);
				result_privs[i] = pop(script, cl); 
				if (result_privs[i] == null) {
					Utl.logn("ERROR : pFunc.Virtual.pop_result_from_script : "
							+ "null result while popping privs");
				} 
			}
			
			int pop_cnt = 0;
			for (int i = 0 ; i < scr.arg_nb ; i++) {
				if (popC(script) != C.INT || pop(script, Integer.class) != i) {
					Utl.logn("ERROR : pFunc.Virtual.pop_result_from_script : "
							+ "bad index while popping args"); }
				if (peekC(script) == C.CODE && peekC(script, 1) == C.NULL) {

//					Utl.logn("WARNING : pFunc.Virtual.pop_result_from_script : "
//							+ "found null arg");
					
					pop_cnt++;
					pop(script); pop(script); script_args[i] = null;
				} else if (peekC(script) == C.INT) {
					popC(script);
					int arg_size = pop(script, Integer.class);
					if (arg_size > 2 && peekC(script) == C.INT) {
						ArrayList<Object> l = new ArrayList<Object>();
						pop(script, arg_size - 2, l);
						script_args[i] = Utl.duplic(l);
						pop_cnt++;
					} else {
						script_args[i] = null;
						Utl.logn("ERROR : pFunc.Virtual.pop_result_from_script : "
								+ "bad arg size while popping args"); 
					}
				} else {
					script_args[i] = null;
					Utl.logn("ERROR : pFunc.Virtual.pop_result_from_script : "
							+ "unknown code while popping args"); }
			}
			if (pop_cnt != scr.arg_nb) {
				Utl.logn("ERROR : pFunc.Virtual.pop_result_from_script : "
						+ "did not pop all args"); 
				return false;
			} 
			if (popC(script) != C.CODE || popC(script) != C.SCRIPT_END) {
				Utl.logn("ERROR : pFunc.Virtual.pop_result_from_script : "
						+ "no script end code"); 
				return false;
			} 
			return true;
		}
		public void obtain_args_from_branchs() { 
			int pop_cnt = 0;
			for (int i = 0 ; i < scr.arg_nb ; i++) { 
				if (script_args[i] != null) {
					result_args[i] = branch_script_obtain(result_inst, 
							script_args[i], result_passed_arg); 
					pop_cnt++;
//					if (result_args[i] == null) {
//						Utl.logn("WARNING : pFunc.Virtual.obtain_args_from_branchs : "
//								+ "null result when branch_script_obtain");
//					} 
				} else {
					pop_cnt++;
					result_args[i] = null; 
				}
			} 
			if (pop_cnt != scr.arg_nb) {
				Utl.logn("ERROR : pFunc.Virtual.obtain_args_from_branchs : "
						+ "did not obtain all args"); 
			} 
		}
		public nRun get_answer() { 
			return new nRun() { public void run() {
				String r = arg(0, String.class);
				Object d = arg(1, Object.class);
				if (scr == null || !scr.data_type.hasKey(r)) return;
				if (scr.data_type.get(r) == scr_data_type[2]) {
					result_privs[scr.data_index.get(r)] = d; 
//					result_inst.run("set_temp", "ope_"+tile_id+"_"+r, d);
				} 
			}
			public Object get() {
				String r = arg(0, String.class);
				if (r.equals("exec_in_instance")) return false;
				if (r.equals("tile_node")) return result_inst;
				if (r.equals("passed_arg")) return Utl.copy(result_passed_arg);
				if (scr == null || !scr.data_type.hasKey(r)) return null;
				if (scr.data_type.get(r) == scr_data_type[0]) {
					return result_vars[scr.data_index.get(r)];
				} else if (scr.data_type.get(r) == scr_data_type[1]) {
					return result_args[scr.data_index.get(r)]; 
				} else if (scr.data_type.get(r) == scr_data_type[2]) {
					return result_privs[scr.data_index.get(r)]; } 
				return null;
			}};
		}
		public Object execute(pInstance inst) { 
			result_inst = inst; 
			return scr.run.do_get(inst, get_answer()); }
		public <T> T execute(pInstance inst, Class<T> ct) { 
			result_inst = inst; 
			return scr.run.do_get(inst, ct, get_answer()); }
		
		public void set_result_passed_arg(Object[] a) { if (a == null) return;
			for (int i = 0 ; i < a.length ; i++) result_passed_arg[i] = Utl.copy(a[i]); }
		public void free() { virtual_pool.free(this); }
		public void pool_init() { reset(); }
		public void reset() {
			code = null;
			tile_id = -1;
			inst_ref = null;
			scr = null;
			result_inst = null;
			for (int i = 0 ; i < result_passed_arg.length ; i++) result_passed_arg[i] = null;
			for (int i = 0 ; i < result_vars.length ; i++) result_vars[i] = null;
			for (int i = 0 ; i < result_privs.length ; i++) result_privs[i] = null;
			for (int i = 0 ; i < result_args.length ; i++) result_args[i] = null;
			for (int i = 0 ; i < script_args.length ; i++) script_args[i] = null;
		}
	}

	
	
	
	public static ArrayList<Object> to_array(Object[] list) {
		ArrayList<Object> arr = new ArrayList<Object>();
		for (Object o : list) arr.add(o);
		return arr; }
	
	public static Object peek(ArrayList<Object> arr) { 
		if (arr == null || arr.size() == 0) return null; return arr.get(0); }
	public static <T> T peek(ArrayList<Object> arr, Class<T> cl) { 
		if (arr == null || arr.size() == 0) return null; return (T)arr.get(0); }
	public static C peekC(ArrayList<Object> arr) { 
		if (arr == null || arr.size() == 0) return null; return int_to_code.get((int)arr.get(0)); }
	public static Object peek(ArrayList<Object> arr, int n) { 
		if (arr == null) return null; return arr.get(n); }
	public static <T> T peek(ArrayList<Object> arr, int n, Class<T> cl) { 
		if (arr == null || n >= arr.size()) return null; return (T)arr.get(n); }
	public static C peekC(ArrayList<Object> arr, int n) { 
		if (arr == null) return null; return int_to_code.get((int)arr.get(n)); }
	
	public static Object pop(ArrayList<Object> arr) { 
		if (arr == null || arr.size() == 0) return null; return arr.remove(0); }
	public static <T> T pop(ArrayList<Object> arr, Class<T> cl) { 
		if (arr == null || arr.size() == 0) return null; return (T)arr.remove(0); }
	public static C popC(ArrayList<Object> arr) { 
		if (arr == null || arr.size() == 0) return null; return int_to_code.get((int)arr.remove(0)); }
	public static void pop(ArrayList<Object> arr, int n, ArrayList<Object> l) { 
		if (arr == null || arr.size() == 0) return;
		for (int i = 0 ; i < n ; i++) l.add(pop(arr)); }
	
	
	
	
	public enum C {
		//primitive
		FLT, INT, BOO, STR, VEC, // same order as GDWUtl.data_type
		
		//flag
		CODE, NULL, SCRIPT_END, 
		
		//Operator
		AND, OR, NOT, 
		ADD, SUB, MUL, DIV, NEG, ABS, 
		ADDV, ROT, MAG, VtX, VtY, DIR, LEN, 
		FtI, ItF, DMtV, XYtV, 
		
		EQ, ESUP, EINF, SUP, INF, 
		
		GETI, GETR, GETP, //SLOW, 
		
		PGET, 
		
		BRC, 
		ARR, IND, PASS, //MEM, 
		
		//Instruction
		SETO, SETR, SETP, PSET,
		
		RUNP, 
		
		NEWP, NEWB,  
		
		START, RTRN, 
		IF, CLS, 
		FUNC, //SMM, //TERM, 
		
		// iteration code
		NEXT, STOP, JUMP
	}
	
	// same order as GDWUtl.data_type
	public static final C[] types_codes = new C[] { C.FLT, C.INT, C.BOO, C.STR, C.VEC };
	
	public static final HashMap<Integer,C> int_to_code = new HashMap<Integer,C>();
	public static final HashMap<C,Integer> code_to_int = new HashMap<C,Integer>();
	public static final HashMap<C,Class<?>> code_to_class = new HashMap<C,Class<?>>();
	
	public static final HashMap<pStandard,Operator> stand_to_ope = 
			new HashMap<pStandard,Operator>();
	public static final HashMap<pStandard,Instruction> stand_to_ins = 
			new HashMap<pStandard,Instruction>();
	
	public static void build_codes() {
		int cnt = 0;
		for (C c : C.values()) { int_to_code.put(cnt,c); code_to_int.put(c,cnt); cnt++; }
		cnt = 0;
		for (Class<?> cl : Utl.data_type) { code_to_class.put(types_codes[cnt], cl); }
	}
	
	public static class ScriptBuilder {
		ArrayList<Object[]> coms = new ArrayList<Object[]>();
		public ScriptBuilder() {}
		public ScriptBuilder com(Object d) {
			if (!Utl.type_is_used(d.getClass())) return this;
			Object[] l = new Object[2];
			l[0] = code_to_int.get(types_codes[Utl.type_class_index.get(d.getClass())]);
			l[1] = Utl.copy(d); 
			coms.add(l); return this; }
		public ScriptBuilder com(C c) {
			coms.add(new Object[] { code_to_int.get(C.CODE), code_to_int.get(c) }); return this; }
		public ScriptBuilder com(Object[] d) { if (d != null) coms.add(Utl.duplic(d)); return this; }//if (d != null) 
		public Object[] get() {
			int l = 2; for (Object[] ol : coms) l += ol.length;//if (ol != null) 
			Object[] list = new Object[l];
			list[0] = code_to_int.get(types_codes[Utl.type_class_index.get(Integer.class)]);
			list[1] = l;
			l = 2; 
			for (Object[] ol : coms) {//if (ol != null) 
				int l2 = 0; for (Object o : ol) { list[l+l2] = o; l2++; }
				l += ol.length; }
			return list;
		}
	}
	
	
	public static final int MAX_INFO = 4;

	public static class Data {
		public String ref; public Class<?> arg_class; 
		public int index; public char type;
		public Object def;
		public Data(String r, int i, char t) { ref = r; index = i; type = t; }
		public Data(String r, int i, char t, Class<?> c) { 
			ref = r; index = i; type = t; arg_class = c; } 
		public Data(String r, int i, char t, Object c) { 
			ref = r; index = i; type = t; def = c; } }

	public static nMap<Instruction> instructions = new nMap<Instruction>();
	public static final HashMap<Instruction,C> ins_to_code = new HashMap<Instruction,C>();
	public static final HashMap<C,Instruction> code_to_ins = new HashMap<C,Instruction>();
	public static final char[] scr_data_type = new char[] { 'V', 'A', 'P' };
	public static class Scriptable {
		public String ref, pic; public C code; public nRun run, stand_run; 
		public Data[] vars = new Data[MAX_INFO];
		public Data[] args = new Data[MAX_INFO];
		public Data[] privs = new Data[MAX_INFO];
		public nMap<Character> data_type = new nMap<Character>();
		public nMap<Integer> data_index = new nMap<Integer>();
		public int var_nb = 0, arg_nb = 0, priv_nb = 0;
		public Scriptable(String r, String p, C d, nRun n) {
			ref = r; pic = p; code = d; run = n; }
		public Scriptable setStandRun(nRun n) { stand_run = n; return this; }
		public Scriptable addArg(String r, Class<?> n) { 
			if (arg_nb < MAX_INFO) { 
				data_type.put(r, scr_data_type[1]); data_index.put(r, arg_nb);
				args[arg_nb] = new Data(r,arg_nb,scr_data_type[1],n); arg_nb++; } return this; }
		public Scriptable addVar(String r) {
			if (var_nb < MAX_INFO) { 
				data_type.put(r, scr_data_type[0]); data_index.put(r, var_nb);
				vars[var_nb] = new Data(r,var_nb,scr_data_type[0]); var_nb++; } return this; }
		public Scriptable addPrivVar(String r, Object d) { 
			if (priv_nb < MAX_INFO) { 
				data_type.put(r, scr_data_type[2]); data_index.put(r, priv_nb);
				privs[priv_nb] = new Data(r,priv_nb,scr_data_type[2], d); priv_nb++; } return this; }
	}
	
	public static class Instruction extends Scriptable {
		public boolean watched = false, activated = false; 
		public Instruction(String r, String p, C d, nRun n) { 
			super(r,p,d,n); ins_to_code.put(this,code); 
			code_to_ins.put(code,this); instructions.put(r,this); }
		public Instruction setStandRun(nRun n) { super.setStandRun(n); return this; }
		public Instruction setWatched() { watched = true; return this; }
		public Instruction setActivated() { activated = true; return this; }
		public Instruction addVar(String r) { super.addVar(r); return this; }
		public Instruction addPrivVar(String r, Object d) { super.addPrivVar(r, d); return this; }
		public Instruction addArg(String r, Class<?> n) { super.addArg(r, n); return this; }
	}
	
	public static nMap<Operator> operators = new nMap<Operator>();
	public static final HashMap<Operator,C> ope_to_code = new HashMap<Operator,C>();
	public static final HashMap<C,Operator> code_to_ope = new HashMap<C,Operator>();
	
	public static class Operator extends Scriptable {
		public String ret_filter = null; public Class<?> ret_class = null; 
		private void init() { ope_to_code.put(this,code); code_to_ope.put(code,this); 
			operators.put(ref,this); }
		public Operator(String r, String p, C d, nRun n) { super(r,p,d,n); init(); }
		public Operator(String r, String p, C d, Class<?> c, nRun n) { 
			super(r,p,d,n); init(); ret_class = c; }
		public Operator(String r, String p, C d, String c, nRun n) { 
			super(r,p,d,n); init(); ret_filter = c; }
		public Operator addVar(String r) { super.addVar(r); return this; }
		public Operator addPrivVar(String r, Object d) { super.addPrivVar(r, d); return this; }
		public Operator addArg(String r, Class<?> n) { super.addArg(r, n); return this; }
	}
	
	
	
//	public static final HashMap<Class<?>, nRun> mem_containers = new HashMap<Class<?>, nRun>();
//	
//	public static void defineMemberContenant(Class<?> cont_class, nRun run_get_mem) {
//		mem_containers.put(cont_class, run_get_mem);
//	}
//	
//	public static void setMember(Object mem_cont, Object... arg) {
//		nRun cont = mem_containers.get(mem_cont.getClass());
//		if (cont == null) return;
//		if (arg == null || arg.length == 0) {
//			Object[] a = new Object[1]; a[0] = mem_cont;
//			cont.do_run(a); }
//		Object[] a = new Object[arg.length + 1]; a[0] = mem_cont;
//		for (int i = 0 ; i < arg.length ; i++) a[i+1] = arg[i];
//		cont.do_run(a);
//	}
//
//	public static Object obtainMember(Object mem_cont, Object... arg) {
//		nRun cont = mem_containers.get(mem_cont.getClass());
//		if (cont == null) return null;
//		if (arg == null || arg.length == 0) {
//			Object[] a = new Object[1]; a[0] = mem_cont;
//			return cont.do_get(a); }
//		Object[] a = new Object[arg.length + 1]; a[0] = mem_cont;
//		for (int i = 0 ; i < arg.length ; i++) a[i+1] = arg[i];
//		return cont.do_get(a);
//	}
	
	
	

	
	
}
