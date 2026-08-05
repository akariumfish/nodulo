package app;

import java.util.ArrayList;
import java.util.HashMap;

import util.Utl;
import util.nMap;
import util.nPool;
import util.nRun;
import app.App;

public abstract class nLauncher implements nPool.Poolable {
	
	
	public static boolean LOG_CRASH = true;
	public static boolean LOG_BLOC = true;
	public static boolean LOG_STACKTRACE = true;
	public static boolean LOG_ERROR = true;
	
	
	public String launcher_ref = "?";
	
	public nLauncher() {
		build_lauchables();
	}
	
	public void reset() {
		has_crash = true;
		ref_crash.clear();
		for (String s : ref_list.allKey()) ref_crash.put(s,true);
		for (String s : ref_run.allKey()) ref_crash.put(s,true);
	}
	public void pool_init() {
		has_crash = false;
		ref_crash.clear();
		for (String s : ref_list.allKey()) ref_crash.put(s,false);
		for (String s : ref_run.allKey()) ref_crash.put(s,false);
	}
	
	public abstract void build_lauchables();

	private nMap<ArrayList<nRun>> ref_list = new nMap<ArrayList<nRun>>();
	private nMap<nRun> ref_run = new nMap<nRun>();
	public HashMap<String,Boolean> ref_crash = new HashMap<String,Boolean>();
	public boolean has_crash = false;

	public ArrayList<nRun> newEventList(String ref, ArrayList<nRun> list) {
		ref_list.put(ref,list);
		ref_crash.put(ref,false);
		return list;
	}

	public nRun newLaunchMetode(String ref, nRun run) {
		ref_run.put(ref,run);
		ref_crash.put(ref,false);
		return run;
	}

	public boolean runEventList(String ref, Object ... args) { 
		if (ref_crash.get(ref) == null || ref_list.get(ref) == null) {
			if (LOG_ERROR) Utl.logn("ERROR   - nLauncher < "+launcher_ref+" > "
					+ "runEventList  < "+ref+" >  cant be found.");
			return false;
		}
		if (!GdxApp.CATCH_THROW) { 
			if (args == null || args.length == 0) nRun.runEvents(ref_list.get(ref)); 
			else if (args.length == 1) {
				nRun.runEvents(ref_list.get(ref)); 
				nRun.runEvents(ref_list.get(ref), args[0]); }
			else if (args.length == 2) {
				nRun.runEvents(ref_list.get(ref)); 
				nRun.runEvents(ref_list.get(ref), args[0]);
				nRun.runEvents(ref_list.get(ref), args[0], args[1]); }
			else if (args.length >= 3) {
				nRun.runEvents(ref_list.get(ref)); 
				nRun.runEvents(ref_list.get(ref), args[0]);
				nRun.runEvents(ref_list.get(ref), args[0], args[1]);
				nRun.runEvents(ref_list.get(ref), args[0], args[1], args[2]); }
			return true; }
		
		if (ref_crash.get(ref)) {
			if (LOG_BLOC) Utl.logn("BLOC    - nLauncher < "+launcher_ref+" > "
					+ "runEventList  < "+ref+" >  has crashed before and is bloqued.");
			return false; }
		
		try {
			if (args == null || args.length == 0) nRun.runEvents(ref_list.get(ref)); 
			else if (args.length == 1) {
				nRun.runEvents(ref_list.get(ref)); 
				nRun.runEvents(ref_list.get(ref), args[0]); }
			else if (args.length == 2) {
				nRun.runEvents(ref_list.get(ref)); 
				nRun.runEvents(ref_list.get(ref), args[0]);
				nRun.runEvents(ref_list.get(ref), args[0], args[1]); }
			else if (args.length >= 3) {
				nRun.runEvents(ref_list.get(ref)); 
				nRun.runEvents(ref_list.get(ref), args[0]);
				nRun.runEvents(ref_list.get(ref), args[0], args[1]);
				nRun.runEvents(ref_list.get(ref), args[0], args[1], args[2]); }
		} catch (Exception ex) {
			ref_crash.remove(ref);
			ref_crash.put(ref,true);
			has_crash = true;
			GdxApp.app.interupt();
			if (LOG_STACKTRACE) ex.printStackTrace(System.out);
			if (LOG_CRASH) Utl.logn("WARNING : nLauncher < "+launcher_ref+" > "
					+ "runEventList  < "+ref+" >  catched an Exception. "
					+ "Render is paused, press space to continue");
			if (LOG_CRASH) Utl.logn("          This eventList is stored as crashing and will be ignored");
			return false;
		}
		return true;
	}
	
	public boolean runLaunchMetode(String ref, Object ... args) { 
//		Applet.app.log("runLaunchMetode"); 
		if (ref_crash.get(ref) == null || ref_run.get(ref) == null) {
			if (LOG_ERROR) Utl.logn("ERROR   - nLauncher < "+launcher_ref+" > "
					+ "runLaunchMetode  < "+ref+" >  cant be found.");
			return false;
		}
		if (!GdxApp.CATCH_THROW) { 
			if (args == null || args.length == 0) ref_run.get(ref).run(); 
			else if (args.length == 1) {
				ref_run.get(ref).run(); 
				ref_run.get(ref).run(args[0]); }
			else if (args.length == 2) {
				ref_run.get(ref).run(); 
				ref_run.get(ref).run(args[0]);
				ref_run.get(ref).run(args[0], args[1]); }
			else if (args.length >= 3) {
				ref_run.get(ref).run(); 
				ref_run.get(ref).run(args[0]);
				ref_run.get(ref).run(args[0], args[1]);
				ref_run.get(ref).run(args[0], args[1], args[2]); }
			return true; }
		if (ref_crash.get(ref)) {
			if (LOG_BLOC) Utl.logn("BLOC    - nLauncher < "+launcher_ref+" > "
					+ "runLaunchMetode  < "+ref+" >  has crashed before and is bloqued.");
			return false;
		}
		try {
			if (args == null || args.length == 0) ref_run.get(ref).run(); 
			else if (args.length == 1) {
				ref_run.get(ref).run(); 
				ref_run.get(ref).run(args[0]); }
			else if (args.length == 2) {
				ref_run.get(ref).run(); 
				ref_run.get(ref).run(args[0]);
				ref_run.get(ref).run(args[0], args[1]); }
			else if (args.length >= 3) {
				ref_run.get(ref).run(); 
				ref_run.get(ref).run(args[0]);
				ref_run.get(ref).run(args[0], args[1]);
				ref_run.get(ref).run(args[0], args[1], args[2]); }
		} catch (Exception ex) {
			ref_crash.remove(ref);
			ref_crash.put(ref,true);
			has_crash = true;
			GdxApp.app.interupt();
			if (LOG_STACKTRACE) ex.printStackTrace(System.out);
			if (LOG_CRASH) Utl.logn("WARNING : nLauncher < "+launcher_ref+" > "
					+ "runLaunchMetode  < "+ref+" >  catched an Exception. "
					+ "Render is paused, press space to continue");
			if (LOG_CRASH) Utl.logn("          This metode is stored as crashing and will be ignored");
			return false;
		}
		return true;
	}
}
