package data;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Vector2;

import app.App;
import app.nInput;
import data.sSpace.Use;
import util.Utl;
import util.nMap;
import util.nPool;
import util.nRun;




/*



*/


public class sData extends sValueBloc {
	
	
	

	
	ArrayList<nRun> eventsAllChange = new ArrayList<nRun>();
	
	public sValueBloc addEventAllChange(nRun r) { eventsAllChange.add(r); return this; }
	public sValueBloc removeEventAllChange(nRun r) { eventsAllChange.remove(r); return this; }
	public boolean as_changed = false;
	
	public void signal_change() {
		as_changed = true;
	}
	
	public void frame_end() {
		if (as_changed) {
			as_changed = false;
			nRun.runEvents(eventsAllChange);
		}
		super.frame_end();
	}
	

	public sBloc_Builder getBuilder(String r) {
		return bloc_builders.get(r); }
	
//	public boolean USE_BUILDER = true;
//
//	public void do_build() { USE_BUILDER = true; }
//	public void no_build() { USE_BUILDER = false; }
	
	
	
	
	
	
	

	public sValueBloc selected_bloc = null;
	
	nInput input;

	public static final String[] types = {"flt", "int", "boo", "str", "vec", 
			"arr", "tab"};
	public static final Object[] type_class = {sFlt.class, sInt.class, sBoo.class, 
			sStr.class, sVec.class, sArr.class, sTab.class};
	public static final Object[] type_prims = {Float.class, Integer.class, 
			Boolean.class, String.class, Vector2.class, null, null, null}; 
	
	public final nPool<sValueBloc> bloc_pool;
	public final nPool<sFlt> flt_pool;
	public final nPool<sInt> int_pool;
	public final nPool<sStr> str_pool;
	public final nPool<sVec> vec_pool;
	public final nPool<sArr> arr_pool;
	public final nPool<sTab> tab_pool;
	public final nPool<sBoo> boo_pool;

	public HashMap<String, sBloc_Builder> bloc_builders;
	
	public ArrayList<sSpace> load_delay_space = new ArrayList<sSpace>();
	
	boolean doevent = true;

	public sValueBloc system_bloc;
	
	public nAutoID autoid;
	
	public sSpace root_space;
	public sValueBloc root_bloc;

	public sData(App a) {
		super(); 
		app = a; input = app.input; ref = ""; base_ref = ""; 
		parent = this; data = this; 
		adress = ""+adress_token;
		
		autoid = new nAutoID();
		
		filebloc_pool = new nPool<File_Bloc>() {
			protected File_Bloc newObject() { return new File_Bloc(data); } };
		filedata_pool = new nPool<File_Data>() {
			protected File_Data newObject() { return new File_Data(data); } };
		
		bloc_pool = new nPool<sValueBloc>() {
			protected sValueBloc newObject() { return new sValueBloc(); } };
		flt_pool = new nPool<sFlt>() {
			protected sFlt newObject() { return new sFlt(); } };
		int_pool = new nPool<sInt>() {
			protected sInt newObject() { return new sInt(); } };
		str_pool = new nPool<sStr>() {
			protected sStr newObject() { return new sStr(); } };
		boo_pool = new nPool<sBoo>() {
			protected sBoo newObject() { return new sBoo(); } };
		vec_pool = new nPool<sVec>() {
			protected sVec newObject() { return new sVec(); } };
		arr_pool = new nPool<sArr>() {
			protected sArr newObject() { return new sArr(); } };
		tab_pool = new nPool<sTab>() {
			protected sTab newObject() { return new sTab(); } };
		
		system_bloc = newBloc("__system");
		
		bloc_builders = new HashMap<String, sBloc_Builder>();
	    root_space = newSpace("root", sSpace.Use.SETTING);
	    root_bloc = root_space.root;
	    
		def_root_savepath = "save" + file_extension;
		
		file = new sFile(this);

		val_root_savepath = obtainStr("val_root_savepath", def_root_savepath);
		
	}
	
	public void dispose() {
		filebloc_pool.dispose(); 
		filedata_pool.dispose();
		
		bloc_pool.dispose();
		flt_pool.dispose();
		int_pool.dispose();
		str_pool.dispose();
		boo_pool.dispose();
		vec_pool.dispose();
		arr_pool.dispose();
		tab_pool.dispose();
		
		bloc_builders.clear();
	}
	
	

	public sSpace newSpace(String ref, sSpace.Use u) {
		if (data.blocs.get(ref) == null) {
			sSpace d = new sSpace(this, ref, u);
			return d; }
		return null;
	}

	public final static String file_ext_txt = "sdt";
	public final static String file_extension = "."+file_ext_txt;
	public sFile file = null;
	public String def_root_savepath;
	public sStr val_root_savepath;

	public void full_save() { 
		root_space.space_save(val_root_savepath.get(), true);
	}
	public void full_load() { 
		root_space.space_load(val_root_savepath.get());
		// load delayed, keep file 6 frame
//		app.addDelayEvent(10, new nRun() { public void run() {
//			file.close(); }});
	}
	
	
	
	
	
	
	
	
	
	
	public static final int BYTE_SIZE_INT = 4;
	public static final int BYTE_SIZE_FLOAT = 4;

	nPool<File_Bloc> filebloc_pool;
	nPool<File_Data> filedata_pool;

	public File_Bloc newFileBloc(String n) { return filebloc_pool.obtain().init(n); }
	
	
	public OutputStream getOutputStream(String path) {
		FileHandle handle = Gdx.files.local(path);
		boolean append = false;
		if (handle.exists()) return handle.write(append);
		return null;
	}
	
	public InputStream getInputStream(String path) {
		FileHandle handle = Gdx.files.local(path);
		if (handle.exists()) return handle.read();
		return null;
	}
	
	
	public static byte[] getBytes(Object d) { 
		if (d instanceof String) return getBytes((String)d); 
		else if (d instanceof Float) return getBytes((float)d);
		else if (d instanceof Integer) return getBytes((int)d);
		else if (d instanceof Boolean) return getBytes((boolean)d);
		else if (d instanceof Vector2) return getBytes((Vector2)d); 
		else return null; }
	
	public static byte[] getBytes(String s) { return s.getBytes(); }
	public static byte[] getBytes(int s) { return ByteBuffer.allocate(BYTE_SIZE_INT).putInt(s).array(); }
	public static byte[] getBytes(float s) { return ByteBuffer.allocate(BYTE_SIZE_FLOAT).putFloat(s).array(); }
	public static byte[] getBytes(boolean s) { byte[] arr = {(byte) ((s) ? 1 : 0)}; return arr; }
	public static byte[] getBytes(Vector2 s) { return getBytes(s.toString()); }

	public static <T> T getValue(byte[] data, Class<T> ct) { 
		if (ct == String.class) return (T)getStr(data); 
		else if (ct == Float.class) return (T)(Object)getFlt(data);
		else if (ct == Integer.class) return (T)(Object)getInt(data);
		else if (ct == Boolean.class) return (T)(Object)getBoo(data);
		else if (ct == Vector2.class) return (T)getVec(data);
		else return null;
	}
	
	public static String getStr(byte[] data) { return new String(data); }
	public static int getInt(byte[] data) { return ByteBuffer.wrap(data).getInt(); }
	public static float getFlt(byte[] data) { return ByteBuffer.wrap(data).getFloat(); }
	public static boolean getBoo(byte[] data) { return data[0] != 0; }
	public static Vector2 getVec(byte[] data) { return new Vector2().fromString(getStr(data)); }
	
	
	
	
	
	
	
	
	
	public static final char adress_token = '/';

	public boolean blocAdressExist(String adress) {
		String[] adress_list = adress.split(""+adress_token); 
		sValueBloc current_bloc = this;
		sValueBloc next_bloc = this;
		for (int i = 0 ; i < adress_list.length - 1 ; i++) {
			if (adress_list[i].length() > 0) { 
				next_bloc = current_bloc.getBloc(adress_list[i]);
				if (next_bloc == null) {
//					app.log("ERROR doing getBlocFromAdress, cant find bloc " + 
//							adress_list[i]); 
					return false; }
				current_bloc = next_bloc;
			}
		}
		if (adress_list.length == 0) {
//			app.log("ERROR doing getBlocFromAdress: in bloc " + 
//					current_bloc.adress + " " + current_bloc.ref + "  " + 
//					"adress_list.length == 0"); 
			return false; }
		sValueBloc val = current_bloc.getBloc(adress_list[adress_list.length - 1]);
		if (val == null) {
//			app.log("ERROR doing getBlocFromAdress: in bloc " + 
//					current_bloc.adress + " " + current_bloc.ref + " cant find bloc " + 
//					adress_list[adress_list.length - 1]); 
			return false; }
		return true;
	}
	
	public sValueBloc getBlocFromAdress(String adress) {
		String[] adress_list = adress.split(""+adress_token); 
		sValueBloc current_bloc = this;
		sValueBloc next_bloc = this;
		for (int i = 0 ; i < adress_list.length - 1 ; i++) {
			if (adress_list[i].length() > 0) { 
				next_bloc = current_bloc.getBloc(adress_list[i]);
				if (next_bloc == null) {
					Utl.logn("ERROR doing getBlocFromAdress, cant find bloc " + 
							adress_list[i]); 
					return null; }
				current_bloc = next_bloc;
			}
		}
		if (adress_list.length == 0) {
//			app.log("ERROR doing getBlocFromAdress: in bloc " + 
//					current_bloc.adress + " " + current_bloc.ref + "  " + 
//					"adress_list.length == 0"); 
			return null; }
		sValueBloc val = current_bloc.getBloc(adress_list[adress_list.length - 1]);
		if (val == null) {
			Utl.logn("ERROR doing getBlocFromAdress: in bloc " + 
					current_bloc.adress + " " + current_bloc.ref + " cant find bloc " + 
					adress_list[adress_list.length - 1]); 
			return null; }
		return val;
	}
	
	public sValue getValFromAdress(String adress) {
		String[] adress_list = adress.split(""+adress_token); 
		sValueBloc current_bloc = this;
		sValueBloc next_bloc = this;
		for (int i = 0 ; i < adress_list.length - 1 ; i++) {
			if (adress_list[i].length() > 0) { 
				next_bloc = current_bloc.getBloc(adress_list[i]);
				if (next_bloc == null) {
					Utl.logn("ERROR doing getValFromAdress, cant find bloc " + 
							adress_list[i]); 
					return null; }
				current_bloc = next_bloc;
			}
		}
		sValue val = current_bloc.getValue(adress_list[adress_list.length - 1]);
		if (val == null) {
			Utl.logn("ERROR doing getValFromAdress: in bloc " + 
					current_bloc.adress + " " + current_bloc.ref + " cant find val " + 
					adress_list[adress_list.length - 1]); 
			return null; }
		return val;
	}
	
	// test if new valbloc and val ref dont contain char used somewhere as balise
	public static boolean refIsValid(String r) {
		if (r == null) return false;
		if (r.indexOf(adress_token) != -1) return false;
		return true; }
	
	
	
	
	
	

//	public sRun newRun(sValueBloc d, String n) {
//		return run_pool.obtain().init(d, n); }
	
	public sValueBloc newBloc(sValueBloc parent, String ref) {
		return bloc_pool.obtain().init(parent, ref); }

	public static boolean objectIsSavable(Object v) {
		if (v.getClass() == Integer.class) 
			return true;
		if (v.getClass() == Float.class) 
			return true;
		if (v.getClass() == Boolean.class) 
			return true;
		if (v.getClass() == String.class) 
			return true;
		if (v.getClass() == Vector2.class) 
			return true;
		return false;
	}
	public static <T> boolean classIsSavable(Class<T> v) {
		if (v == Integer.class) return true;
		if (v == Float.class) return true;
		if (v == Boolean.class) return true;
		if (v == String.class) return true;
		if (v == Vector2.class) return true;
		return false;
	}
	public sValue newVal(sValueBloc d, Object v, String n, String s) {
		if (v.getClass() == Integer.class) 
			return newInt(d, (Integer)v, n, s);
		if (v.getClass() == Float.class) 
			return newFlt(d, (Float)v, n, s);
		if (v.getClass() == Boolean.class) 
			return newBoo(d, (Boolean)v, n, s);
		if (v.getClass() == String.class) 
			return newStr(d, (String)v, n, s);
		if (v.getClass() == Vector2.class) 
			return newVec(d, (Vector2)v, n, s);
		return null; }
	
	public sInt newInt(sValueBloc d, int v, String n, String s) {
		return int_pool.obtain().init(d, v, n, s); }
	public sFlt newFlt(sValueBloc d, float v, String n, String s) {
		return flt_pool.obtain().init(d, v, n, s); }
	public sBoo newBoo(sValueBloc d, boolean v, String n, String s) {
		return boo_pool.obtain().init(d, v, n, s); }
	public sBoo newBoo(sValueBloc d, boolean v, String n, String s, char ct) {
		return boo_pool.obtain().init(d, v, n, s, ct); }
	public sVec newVec(sValueBloc d, String n, String s) {
		return vec_pool.obtain().init(d, n, s); }
	public sVec newVec(sValueBloc d, Vector2 v, String n, String s) {
		return vec_pool.obtain().init(d, v, n, s); }
	public sArr newArr(sValueBloc d, String n, String s) {
		return arr_pool.obtain().init(d, n, s); }
	public sTab newTab(sValueBloc d, String n, String s) {
		return tab_pool.obtain().init(d, n, s); }
	public sStr newStr(sValueBloc d, String v, String n, String s) {
		return str_pool.obtain().init(d, v, n, s); }
	
	
	
//	int to_save_bloc(Save_Bloc sb) { 
//		//    dlogln("DataHolder saving to savebloc");
//		int cnt = super.preset_to_save_bloc(sb); 
//		//    dlogln("saved " + cnt + " values");
//		return cnt;
//	}

	boolean values_match(sValueBloc b1, sValueBloc b2) {
		return b1.getValueHierarchy(true).equals(b2.getValueHierarchy(true)); }

	public boolean values_found(sValueBloc from, sValueBloc in) {
		boolean all_found = true;
		for (Map.Entry<String,sValue> me1 : from.values.entrySet()) { 
			sValue v1 = (sValue)me1.getValue(); 
			boolean found = false;
			for (Map.Entry<String,sValue> me2 : in.values.entrySet()) { 
				sValue v2 = (sValue)me2.getValue(); 
				found = found || v1.ref.equals(v2.ref);
			} 
			all_found = all_found && found;
		} 
		return all_found; }


	boolean full_match(sValueBloc b1, sValueBloc b2) {
		return b1.getHierarchy(true).equals(b2.getHierarchy(true)); }

	
	public sValueBloc copy_bloc_value(sValueBloc from, sValueBloc to, String n) {
		if (from != null && to != null) {
			File_Bloc b = newFileBloc("");
			from.preset_value_to_save_bloc(b); 
			return to.newBloc(b, n);
		} return null;
	}
	public void transfer_bloc_values(sValueBloc from, sValueBloc to) {
		if (from != null && to != null) {
			File_Bloc b = newFileBloc("");
			from.preset_value_to_save_bloc(b);
			to.load_values_from_bloc(b);
		} 
	}
	
	
	
	
	
	
	
	
}

