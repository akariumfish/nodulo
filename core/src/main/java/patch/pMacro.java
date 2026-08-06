package patch;

import data.*;
import gui.*;
import util.*;
import aa_nodulo.*;
import app.*;

import java.util.ArrayList;
import java.util.Map;

import com.badlogic.gdx.math.Vector2;


public class pMacro {

	
	public static void link_brics_cos(pInstance bric1, String co_ref1, 
			pInstance bric2, String co_ref2) {
		if (bric1 == null || bric2 == null) return;
		pInstance co1 = bric1.get("get_co", pInstance.class, co_ref1);
		pInstance co2 = bric2.get("get_co", pInstance.class, co_ref2);
		if (co1 == null || co2 == null) return;
		co1.run("link_to", co2);
	}

	
	
	
	
	
	public static final nMap<Macro> all_macros = new nMap<Macro>();

	public static Macro getMacro(String r) { return all_macros.get(r); }
	
	public static class Macro {
		public String ref;
		public nMap<Macro> macros = new nMap<Macro>();
		public nMap<Vector2> macros_pos = new nMap<Vector2>();
		public nMap<MacroNode> macronodes = new nMap<MacroNode>();
		public ArrayList<MacroLink> macrolinks = new ArrayList<MacroLink>();
		public nMap<MacroSet> set = new nMap<MacroSet>();
		public nMap<MacroPop> pops = new nMap<MacroPop>();
		public nMap<String> scripts = new nMap<String>();
		public ArrayList<nRun> runs = new ArrayList<nRun>();
		public Macro(String r) {
			ref = r; 
			if (all_macros.hasKey(r)) all_macros.remove(r);
			all_macros.put(r, this);
//			newMacro(r, new nRun() { public Object get() {
//				pSheet sheet = arg(0, pSheet.class);
//				boolean to_cam = true;
//				if (args.length > 1) to_cam = arg(1, Boolean.class);
//				nMap<pInstance> list = pop(sheet);
//				if (to_cam)
//					sheet.app.addDelayEvent(16, new nRun() { public void run() {
//						sheet.patch.unselect_all(); 
//						sheet.patch.move_inst_group_to_cam(list.all()); 
//						for (pInstance b : list.all()) { b.run("select"); }
//					}});
//				return list.all(); }});
		}
		
		public MacroNode addNode(String r, String m, float x, float y) {
			MacroNode mb = new MacroNode(this,r,m,x,y);
			macronodes.put(r,mb); return mb; }
		public Macro addLink(String b1, String c1, String b2, String c2) {
			macrolinks.add(new MacroLink(b1,c1,b2,c2)); return this; }
		public Macro addMacro(String ref, String mac) { return addMacro(ref, getMacro(mac)); }
		public Macro addMacro(String ref, String mac, float x, float y) { return addMacro(ref, getMacro(mac), x, y); }
		public Macro addMacro(String ref, Macro mac) { macros.put(ref,mac); return this; }
		public Macro addMacro(String ref, Macro mac, float x, float y) { 
			macros_pos.put(ref,new Vector2(x,y)); macros.put(ref,mac); return this; }
		public Macro addSetVar(String targ_ref, String var_ref, Object data) {
			set.put(targ_ref, new MacroSet(var_ref, data)); return this; }
		public Macro addRunPop(String targ_ref, String pop_ref, String run_ref, Object... args) {
			pops.put(targ_ref, new MacroPop(pop_ref, run_ref, args)); return this; }
		public Macro addTileScript(String targ_ref, String script_ref) {
			scripts.put(targ_ref, script_ref); return this; }
		public Macro addRun(nRun r) { runs.add(r); return this; }
		
		private nMap<pInstance> list = new nMap<pInstance>();
		public ArrayList<pInstance> add(pSheet sheet) { return add(sheet, false); }
		public ArrayList<pInstance> add(pSheet sheet, boolean to_cam) {
			list.clear();
			list = pop(sheet);
			if (to_cam)
				sheet.app.addDelayEvent(16, new nRun() { public void run() {
					sheet.patch.unselect_all(); 
					sheet.patch.move_inst_group_to_cam(list.all()); 
					for (pInstance b : list.all()) { b.run("select"); }
				}});
			return list.all();
		}
		public nMap<pInstance> pop(pSheet p) {
			list.clear();
			for (String mr : macros.allKey()) {
				Macro mc = macros.get(mr);
				nMap<pInstance> l = mc.pop(p);
				for (String br : l.allKey()) {
					pInstance b = l.get(br);
					list.put(mr+"_"+br,b); } 
				if (macros_pos.get(mr) != null) {
					p.app.addDelayEvent(8, new nRun(l, macros_pos.get(mr)) { public void run() {
						nMap<pInstance> l = ((nMap)args[0]);
						for (pInstance inst : l.all())
							inst.run("move", ((Vector2)args[1]).x, ((Vector2)args[1]).y); }});
					
				}
			}
			for (String mbr : macronodes.allKey()) {
				MacroNode mb = macronodes.get(mbr);
				nMap<pInstance> l = mb.pop(p, mbr);
				for (String lr : l.allKey()) {
					pInstance li = l.get(lr);
					list.put(lr,li);
				}
			}
			for (Map.Entry<String,MacroSet> ms : set.entrySet()) {
				pInstance tr = list.get(ms.getKey());
				if (tr == null) {
					Utl.logn("ERROR : Macro.pop() : "
							+ "set target <"+ms.getKey()+"> dont exist");
					continue; }
				tr.setVar(ms.getValue().var_ref, 
						Utl.copy(ms.getValue().data));
			}
			for (Map.Entry<String,MacroPop> me : pops.entrySet()) {
				MacroPop ms = me.getValue();
				pInstance n = list.get(me.getKey());
				if (n == null) {
					Utl.logn("ERROR : Macro.pop() : "
							+ "pop target <"+me.getKey()+"> dont exist");
					continue; }
				String rf = me.getKey();
				if (ms.args == null || ms.args.length == 0) {
					pInstance pi = n.get(ms.run_ref, pInstance.class);
					if (pi == null) { Utl.logn("ERROR : Macro.pop : pop command <"
							+ms.run_ref+"> with 0 args returned null"); }
					list.put(rf+"_"+ms.pop_ref,pi);
				} else if (ms.args.length == 1) {
					pInstance pi = n.get(ms.run_ref, pInstance.class, ms.args[0]);
					if (pi == null) { Utl.logn("ERROR : Macro.pop : pop command <"
							+ms.run_ref+"> with 1 args returned null. args : "+ms.args[0]); }
					list.put(rf+"_"+ms.pop_ref,pi);
				} else if (ms.args.length == 2) {
					pInstance pi = n.get(ms.run_ref, pInstance.class, ms.args[0], ms.args[1]);
					if (pi == null) { Utl.logn("ERROR : Macro.pop : pop command <"
							+ms.run_ref+"> with 2 args returned null. args : "+ms.args[0]+" "+ms.args[1]); }
					list.put(rf+"_"+ms.pop_ref,pi);
				} else if (ms.args.length == 3) {
					pInstance pi = n.get(ms.run_ref, pInstance.class, ms.args[0], ms.args[1], ms.args[2]);
					if (pi == null) { Utl.logn("ERROR : Macro.pop : pop command <"
							+ms.run_ref+"> with 3 args returned null. args : "+ms.args[0]+" "+ms.args[1]+" "+ms.args[2]); }
					list.put(rf+"_"+ms.pop_ref,pi);
				} else if (ms.args.length == 4) {
					pInstance pi = n.get(ms.run_ref, pInstance.class, ms.args[0], ms.args[1], ms.args[2], ms.args[3]);
					if (pi == null) { Utl.logn("ERROR : Macro.pop : pop command <"
							+ms.run_ref+"> with 4 args returned null. args : "+ms.args[0]+" "+ms.args[1]+" "+ms.args[2]+" "+ms.args[3]); }
					list.put(rf+"_"+ms.pop_ref,pi);
				} else {
					Utl.logn("ERROR : Macro.pop() : too many args");
				}
			}
			for (Map.Entry<String,String> ms : scripts.entrySet()) {
				pInstance tr = list.get(ms.getKey());
				if (tr == null) Utl.logn("ERROR : Macro.pop() : "
						+ "script target <"+ms.getKey()+"> dont exist");
				tile_pop_script(ms.getValue(),tr); }
			for (MacroLink ml : macrolinks) {
				
				//TODO add error log
				
				link_brics_cos(list.get(ml.bric1), ml.co1, 
						list.get(ml.bric2), ml.co2); }
			for (nRun rn : runs) rn.do_run(list);
			return Utl.duplic(list);
		}
	}
	public static class MacroNode {
		public Macro macro;
		public String ref, model_ref;
		public Vector2 pos = new Vector2();
		public ArrayList<MacroSet> set = new ArrayList<MacroSet>();
		public ArrayList<MacroPop> pops = new ArrayList<MacroPop>();
		public ArrayList<String> scripts = new ArrayList<String>();
		public MacroNode(Macro mac, String r, String m, float x, float y) {
			macro = mac; ref = r; model_ref = m; pos.set(x,y); }
		public MacroNode addSetVar(String var_ref, Object data) {
			set.add(new MacroSet(var_ref, data)); return this; }
		public MacroNode addRunPop(String pop_ref, String run_ref, Object... args) {
			pops.add(new MacroPop(pop_ref, run_ref, args)); return this; }
		public MacroNode addTileScript(String script_ref) {
			scripts.add(script_ref); return this; }
		public Macro getMacro() { return macro; }
		public nMap<pInstance> pop(pSheet p, String ref) {
			nMap<pInstance> list = new nMap<pInstance>();
			pInstance n = p.newNode(model_ref);
			if (n == null) {
				Utl.logn("ERROR : MacroNode.pop() : model_ref <"+model_ref+"> dont exist ");
				return list;
			}
			list.put(ref,n);
			for (MacroSet ms : set) n.setVar(ms.var_ref, Utl.copy(ms.data));
			for (MacroPop ms : pops) {
				if (ms.args == null || ms.args.length == 0) {
					pInstance pi = n.get(ms.run_ref, pInstance.class);
					if (pi == null) { Utl.logn("ERROR : MacroNode.pop : pop command <"
							+ms.run_ref+"> with 0 args returned null"); }
					list.put(ref+"_"+ms.pop_ref,pi);
				} else if (ms.args.length == 1) {
					pInstance pi = n.get(ms.run_ref, pInstance.class, ms.args[0]);
					if (pi == null) { Utl.logn("ERROR : MacroNode.pop : pop command <"
							+ms.run_ref+"> with 1 args returned null. args : "+ms.args[0]); }
					list.put(ref+"_"+ms.pop_ref,pi);
				} else if (ms.args.length == 2) {
					pInstance pi = n.get(ms.run_ref, pInstance.class, ms.args[0], ms.args[1]);
					if (pi == null) { Utl.logn("ERROR : MacroNode.pop : pop command <"
							+ms.run_ref+"> with 2 args returned null. args : "+ms.args[0]+" "+ms.args[1]); }
					list.put(ref+"_"+ms.pop_ref,pi);
				} else if (ms.args.length == 3) {
					pInstance pi = n.get(ms.run_ref, pInstance.class, ms.args[0], ms.args[1], ms.args[2]);
					if (pi == null) { Utl.logn("ERROR : MacroNode.pop : pop command <"
							+ms.run_ref+"> with 3 args returned null. args : "+ms.args[0]+" "+ms.args[1]+" "+ms.args[2]); }
					list.put(ref+"_"+ms.pop_ref,pi);
				} else if (ms.args.length == 4) {
					pInstance pi = n.get(ms.run_ref, pInstance.class, ms.args[0], ms.args[1], ms.args[2], ms.args[3]);
					if (pi == null) { Utl.logn("ERROR : MacroNode.pop : pop command <"
							+ms.run_ref+"> with 4 args returned null. args : "+ms.args[0]+" "+ms.args[1]+" "+ms.args[2]+" "+ms.args[3]); }
					list.put(ref+"_"+ms.pop_ref,pi);
				} else {
					Utl.logn("ERROR : MacroNode.pop() : too many args");
				}
			}
			for (String ms : scripts) {
				tile_pop_script(ms,n); }
			p.app.addDelayEvent(3, new nRun(n, new Vector2(pos)) { public void run() {
				((pInstance)args[0]).run("go_to", ((Vector2)args[1]).x, ((Vector2)args[1]).y); }});
			return list;
		}
	}
	public static class MacroPop {
		public String pop_ref, run_ref; public Object[] args;
		public MacroPop(String p, String r, Object[] d) { pop_ref = p; run_ref = r; args = d; } }
	public static class MacroSet {
		public String var_ref; public Object data;
		public MacroSet(String r, Object d) { var_ref = r; data = d; } }
	public static class MacroLink {
		public String bric1, co1, bric2, co2;
		public MacroLink(String b1, String c1, String b2, String c2) {
			bric1 = b1; co1 = c1; bric2 = b2; co2 = c2; } }
	
	

	public static final nMap<MacroScript> all_scripts = new nMap<MacroScript>();

	public static void tile_pop_script(String r, pInstance tile) { 
		if (all_scripts.hasKey(r)) all_scripts.get(r).tile_pop(tile); 
		else { Utl.logn("ERROR : pMacro.tile_pop_script : MacroScript <"+r+"> dont exist"); }}
	
	public static class MacroScript {
		public String ref; 
		public ArrayList<MacroScriptCom> coms = new ArrayList<MacroScriptCom>();
		public MacroScript(String r) { ref = r; all_scripts.put(r,this); }
		public MacroScriptCom com(String r, Object... a) { 
			return new MacroScriptCom(this, r, a); }
		public MacroScript script(String r) {
			if (all_scripts.hasKey(r)) for (MacroScriptCom ms : all_scripts.get(r).coms) {
				new MacroScriptCom(this, ms.com_ref, Utl.duplic(ms.args)); } 
			return this; }
		public void tile_pop(pInstance tile) {
			if (tile == null) return;
			pInstance head = tile.getInst("head_tile");
			if (head == null) return;
			for (MacroScriptCom ms : coms) {
				if (ms.args == null || ms.args.length == 0) {
					if (head.get_this(ms.com_ref) == null) 
						Utl.logn("ERROR : MacroScript.tile_pop() : "+ms.com_ref);
				} else if (ms.args.length == 1) {
					if (head.get_this(ms.com_ref, ms.args[0]) == null) 
						Utl.logn("ERROR : MacroScript.tile_pop() : "+ms.com_ref);
				} else if (ms.args.length == 2) {
					if (head.get_this(ms.com_ref, ms.args[0], ms.args[1]) == null) 
						Utl.logn("ERROR : MacroScript.tile_pop() : "+ms.com_ref);
				} else if (ms.args.length == 3) {
					if (head.get_this(ms.com_ref, ms.args[0], ms.args[1], ms.args[2]) == null) 
						Utl.logn("ERROR : MacroScript.tile_pop() : "+ms.com_ref);
				} else {
					Utl.logn("ERROR : MacroScript.tile_pop() : too many args");
				}
			}
		}
	}
	public static class MacroScriptCom {
		public MacroScript script; public String com_ref; public Object[] args;
		public MacroScriptCom(MacroScript ms, String r, Object[] d) { 
			script = ms; com_ref = r; args = d; ms.coms.add(this); } 
		public MacroScript getScript() { return script; }
		public MacroScriptCom com(String r, Object... a) { 
			return new MacroScriptCom(script, r, a); }
		public MacroScript script(String r) { return script.script(r); }
	}
		
}
