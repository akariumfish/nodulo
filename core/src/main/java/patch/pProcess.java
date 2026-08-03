package patch;

import java.util.ArrayList;

import app.Applet;
import app.nMap;
import app.nRun;

public class pProcess {

	public static nMap<pProcess> process;
	public static Applet app;
	
	public static void build(Applet a) {
		app = a; 
		process = new nMap<pProcess>(); 
	}

	public static pProcess newProcess(String r) {
		if (process.hasKey(r)) {
			app.logn("ERROR: cant create process, <"+r+"> allready exist"); return null; }
		pProcess p = new pProcess(r); return p; }
	public static pProcess get(String r) {
		return process.get(r); }

	
	
	public String ref;
	public String comment = "";
	public pStandard creator_stand = null;
	public pStandard getStand() { return creator_stand; }

	public pProcess(String r) { ref = r; process.put(r, this); }

	public pProcess exec() {
//		app.log("proc "+ref+" of stand "+standard.ref+" exec on "+cont.pool_ref+" of stand "+cont.stand.ref);
//		if (cont.stand != standard) return;
		for (Com c : getCommList()) {
			if (c.return_class == null) 
				c.com.run.do_run(c.param, c.args);
			else c.com.run.do_get(c.param, c.args);
		}
		useInit();
		return this; 
	}

	public pProcess exec(pInstance cont) {
//		app.log("proc "+ref+" of stand "+standard.ref+" exec on "+cont.pool_ref+" of stand "+cont.stand.ref);
		for (Com c : getCommList()) {
			if (c.return_class == null) 
				c.com.run.do_run(cont, c.param, c.args);
			else c.com.run.do_get(cont, c.param, c.args);
		}
		useInit();
		return this; 
	}

	public pProcess exec(pInstance cont, pPar param) {
//		app.log("proc "+ref+" of stand "+standard.ref+" exec on "+cont.pool_ref+" of stand "+cont.stand.ref);
		for (Com c : getCommList()) {
			if (c.return_class == null) 
				c.com.run.do_run(cont, c.param.mix(param), c.args);
			else c.com.run.do_get(cont, c.param.mix(param), c.args);
		}
		useInit();
		return this; 
	}

	public class Com { 
		public pCommande com; public Class<?> return_class; 
		public Object[] args = new Object[0]; 
		public pPar param = new pPar();
		Com(Com c) { com = c.com; return_class = c.return_class; param = new pPar(c.param); args = c.args; } 
		Com(pCommande c) { com = c; return_class = c.return_class; } 
		Com(pCommande c, pPar p) { this(c); param = new pPar(p); } 
		Com(pCommande c, Object ... v) { this(c); args = v;/*Applet.toArray(v);*/ } 
		Com(pCommande c, pPar p, Object ... v) { this(c); param = new pPar(p); args = v;/*Applet.toArray(v);*/ } }
	
	public ArrayList<Com> commandes = new ArrayList<Com>();
	public ArrayList<Com> load_commandes = new ArrayList<Com>();
	public ArrayList<Com> save_commandes = new ArrayList<Com>();
	public ArrayList<Com> clear_commandes = new ArrayList<Com>();
	
	private boolean def_load = false, def_save = false, def_clear = false;
	
	public pProcess useInit() { def_load = false; def_save = false; def_clear = false; return this; }
	public pProcess useLoad() { def_load = true; def_save = false; def_clear = false; return this; }
	public pProcess useSave() { def_save = true; def_load = false; def_clear = false; return this; }
	public pProcess useClear() { def_clear = true; def_load = false; def_save = false; return this; }
	
	private ArrayList<Com> getCommList() {
		if (def_load) return load_commandes;
		else if (def_save) return save_commandes;
		else if (def_clear) return clear_commandes;
		else return commandes; }
	
	int new_com = 0;

	public pProcess run(nRun p, Object ... args) {
		if (p == null) return this;
		int l = 1;
		if (args != null) l += args.length;
		Object[] a = new Object[l]; a[0] = this;
		for (int i = 0 ; i < args.length ; i++) a[i+1] = args[i];
		p.do_run(current_param, a);
		return this; }
	
	public pProcess append(pProcess p) {
		if (p == null) return this;
		for (Com c : p.commandes) commandes.add(new Com(c));
		for (Com c : p.load_commandes) load_commandes.add(new Com(c));
		for (Com c : p.save_commandes) save_commandes.add(new Com(c));
		for (Com c : p.clear_commandes) clear_commandes.add(new Com(c));
		return this; }
	
	public pProcess commande(pCommande c) {
		if (c != null) getCommList().add(new Com(c,current_param));
		return this; }
	public pProcess commande(pCommande c, Object ... v) { 
		if (c != null) getCommList().add(new Com(c,current_param,v));
		return this; }

	public pProcess commande(String r) { 
		pCommande c = pCommande.get(r);
		if (c != null) getCommList().add(new Com(c,current_param));
		return this; }
	public pProcess commande(String r, Object ... v) { 
		pCommande c = pCommande.get(r);
		if (c != null) getCommList().add(new Com(c,current_param,v));
		return this; }

	public pProcess commande(nRun rn) { 
		pCommande c = pCommande.newCommande("process_"+ref+"_com_"+new_com,rn); new_com++;
		if (c != null) getCommList().add(new Com(c,current_param));
		return this; }
	public pProcess commande(Class<?> ct, nRun rn) { 
		pCommande c = pCommande.newCommande("process_"+ref+"_com_"+new_com,ct,rn); new_com++;
		if (c != null) getCommList().add(new Com(c,current_param));
		return this; }

	public pProcess commande(nRun rn, Object ... v) { 
		pCommande c = pCommande.newCommande("process_"+ref+"_com_"+new_com,rn); new_com++;
		if (c != null) getCommList().add(new Com(c,current_param,v));
		return this; }
	public pProcess commande(Class<?> ct, nRun rn, Object ... v) { 
		pCommande c = pCommande.newCommande("process_"+ref+"_com_"+new_com,ct,rn); new_com++;
		if (c != null) getCommList().add(new Com(c,current_param,v));
		return this; }

	
	
	
	public pPar process_param = new pPar();
	public pPar current_param = process_param;
	
	public pProcess param(pPar par) {
		current_param.set(current_param.mix(par));
		return this;
	} 
	public pProcess param(Object ... args) {
		if (args == null) return this; 
		if (args.length%2 != 0) return this; 
		String k = null;
		for (Object a : args) {
			if (k == null && (a instanceof String)) { k = (String)a; }
			else if (k != null) { current_param.set(k,a); k = null; }  }
		return this;
	} 

	
	
	

	public ArrayList<Section> all_sections = new ArrayList<Section>();
	public ArrayList<Section> sections = new ArrayList<Section>();
	public Section current_sec = null;

//	private void init_sections() {
//		def_param = new pPar();
//		current_param = def_param;
//		current_sec = null;
//	} 
	public pProcess openSec() {
		if (current_sec == null) {
			Section s = new Section(this);
			setcurrentsec(s);
		} else {
			Section s = new Section(current_sec);
			setcurrentsec(s); }
		return this;
	}
	public pProcess closeSec() {
		if (current_sec != null) {
			if (current_sec.sur_sec instanceof pProcess) setcurrentsec(null);
			else setcurrentsec((Section)current_sec.sur_sec);
		}
		return this;
	}
	private void setcurrentsec(Section s) {
		current_sec = s;
		if (s != null) current_param = s.param;
		else current_param = process_param;
	} 
	
	public class Section {
		public Object sur_sec = null;
		public pProcess pross = null;
		public ArrayList<Section> sections = new ArrayList<Section>();
		public pPar param;
		public Section(pProcess s) { 
			pross = s; sur_sec = s;
			s.sections.add(this);
			pross.all_sections.add(this);
			param = new pPar(s.current_param);
		}
		public Section(Section s) { 
			pross = s.pross; sur_sec = s;
			s.sections.add(this);
			pross.all_sections.add(this);
			param = new pPar(s.param);
		}
		public void clear() {
			for (Section s : sections) s.clear();
			sections.clear();
			param.clear(); param = null;
			sur_sec = null; pross = null;
		}
	}
}
