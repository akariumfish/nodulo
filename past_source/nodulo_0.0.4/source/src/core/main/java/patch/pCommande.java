package patch;

import data.*;
import gui.*;
import util.*;
import aa_nodulo.*;
import app.*;


//import plane2.cStandard.RunDef;

public class pCommande {

	public static nMap<pCommande> commandes = new nMap<pCommande>(); 
//	public static PlaneApplet app;
//	
//	public static void build(PlaneApplet a) {
//		app = a; 
//		commandes = new nMap<pCommande>(); 
//	}

	public static pCommande newCommande(String r) {
		if (commandes.hasKey(r)) {
			Utl.logn("ERROR: cant create commande, <"+r+"> allready exist"); return commandes.get(r); }
		pCommande p = new pCommande(r); return p; }
	
	public static pCommande newCommande(String r, nRun rn) {
		return newCommande(r).set_run(rn); }
	public static pCommande newCommande(String r, Class<?> ct) {
		return newCommande(r).set_return(ct); }
	public static pCommande newCommande(String r, Class<?> ct, nRun rn) {
		return newCommande(r).set_run(rn).set_return(ct); }
	
	public static pCommande get(String r) {
		return commandes.get(r); }

	
	
	public String ref;
	public nRun run;
	public String comment = "";
	public Class<?> return_class;
	public int args_nb = 0;
	public String[] args_ref;
	public Class<?>[] args_class;
	
	public pCommande(String r, nRun rn) {
		this(r); set_run(rn); }
	public pCommande(String r, Class<?> ct) {
		this(r); set_return(ct); }
	public pCommande(String r, Class<?> ct, nRun rn) {
		this(r); set_return(ct); set_run(rn); }
	public pCommande(String r) {
		ref = r; return_class = null; run = null;
		args_ref = new String[0]; args_class = new Class<?>[0]; args_nb = 0;
		commandes.put(r, this); }
	
	public pCommande set_run(nRun rn) { run = rn; return this; }
	public pCommande set_return(Class<?> ct) { return_class = ct; return this; }
	
	public pCommande set_arg(String[] ar, Class<?>[] ac) {
		if ((ar != null) != (ac != null)) return this;
		if (ar != null && ac != null && ar.length != ac.length) return this;
		if (ar != null && ac != null) {
			args_ref = new String[ar.length];
			System.arraycopy( ar, 0, args_ref, 0, ar.length );
			args_class = new Class<?>[ac.length];
			System.arraycopy( ac, 0, args_class, 0, ac.length );
		} else {
			args_ref = new String[0];
			args_class = new Class<?>[0]; }
		args_nb = ar.length;
		return this;
	}
	
	
	public void run(pInstance cont, Object ... v) {
//		if (!test_args(v)) return;
		if (!test_args(v)) Utl.logn("WARNING: cCommande "+ref+" .run(..) bad arguments");
		if (run != null) run.do_run(cont,v); }
	
	public Object get(pInstance cont, Object ... v) {
//		if (!test_args(v)) return;
		if (!test_args(v)) Utl.logn("WARNING: cCommande "+ref+" .run(..) bad arguments");
		if (run != null) return run.do_get(cont,v); else return null; }
	
	public <T> T get(pInstance cont, Class<T> cl, Object ... v) {
//		if (cl != return_class) return null;
		if (cl != return_class) Utl.logn("WARNING: cCommande "+ref+" .run(..) bad return class");
//		if (!test_args(v)) return;
		if (!test_args(v)) Utl.logn("WARNING: cCommande "+ref+" .run(..) bad arguments");
		if (run != null) return run.do_get(cont,cl,v); else return null; }

	public void run(pInstance cont, pPar par, Object ... v) {
//		if (!test_args(v)) return;
		if (!test_args(v)) Utl.logn("WARNING: cCommande "+ref+" .run(..) bad arguments");
		if (run != null) run.do_run(cont,par,v); }
	
	public Object get(pInstance cont, pPar par, Object ... v) {
//		if (!test_args(v)) return;
		if (!test_args(v)) Utl.logn("WARNING: cCommande "+ref+" .run(..) bad arguments");
		if (run != null) return run.do_get(cont,par,v); else return null; }
	
	public <T> T get(pInstance cont, pPar par, Class<T> cl, Object ... v) {
//		if (cl != return_class) return null;
		if (cl != return_class) Utl.logn("WARNING: cCommande "+ref+" .run(..) bad return class");
//		if (!test_args(v)) return;
		if (!test_args(v)) Utl.logn("WARNING: cCommande "+ref+" .run(..) bad arguments");
		if (run != null) return run.do_get(cont,par,cl,v); else return null; }
	
	
	
	
	
	
	private boolean test_args(Object ... v) {
		Object[] args = v;//Applet.toArray(v);
		if (args.length != args_nb) return false;
		for (int i = 0 ; i < args_nb ; i++) {
			if (args[i].getClass() != args_class[i]) return false;
		}
		return true;
	}
}
