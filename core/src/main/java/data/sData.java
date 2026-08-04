package data;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Vector2;

import app.App;
import app.nInput;
import util.Utl;
import util.nMap;
import util.nPool;
import util.nRun;




/*



*/


public class sData extends sValueBloc {
	
	
	
	public void build_def_files() {

//		boolean stl = app.STARTUP_LOAD;
//		boolean smu = app.STARTUP_MODEL_USE;
//		boolean pb = app.PATCH_BUILD;
//		app.STARTUP_LOAD = false;
//		app.STARTUP_MODEL_USE = false;
//		app.PATCH_BUILD = false;
//
////		sValueBloc strt = app.startup_buildbloc();
////
////		app.force_nodraw_frame(40);
////		
////		space_save(setting_space, "setting_default"+setting_extension, true);
////		
////		space_save(data_space, "database_default"+data_extension, true);
////		space_save(root_space, "root_default"+file_extension, true);
////		
////		strt.clear();
////
////		app.force_nodraw_frame(10);
////		
//////		space_load(setting_space, "setting_default"+setting_extension);
//		
//		app.STARTUP_LOAD = stl;
//		app.STARTUP_MODEL_USE = smu;
//		app.PATCH_BUILD = pb;
	}
	
	
	
	public nMap<sTab> databases = new nMap<sTab>();
	
	
	
	
	

	
	
	
	
	
	

	
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
	
	public void collapse_all_in_dataview() {
		for (sValueBloc b : bloc_pool.all()) b.open_in_dataview = false; }
	

	public void addCommonBlocBuilder(sBloc_Builder b) { common_bloc_builders.add(b); }
	
	public void addRootBlocBuilder(sBloc_Builder b) {
//		nWidget ent = app.menu.add_build_menu_trigg("new "+b.ref, new nRun() { public void run() {
//			buildRootBloc(b.ref, b.ref); }});
//		b.root_interf_w = ent;
		root_space.addRootBlocBuilder(b); }
	public sValueBloc buildRootBloc(String builder, String ref) {
		return root_space.buildRootBloc(builder, ref); }

	
	public boolean USE_BUILDER = true;

	public void do_build() { USE_BUILDER = true; }
	public void no_build() { USE_BUILDER = false; }
	
	
	
	
	
	
	

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
//	public final nPool<sRun> run_pool;

	public HashMap<String, sBloc_Builder> bloc_builders;
	public ArrayList<sBloc_Builder> common_bloc_builders;
	
	public ArrayList<sSpace> load_delay_space = new ArrayList<sSpace>();
	
	public sSpace setting_space, data_space, root_space; 
	public sValueBloc setting_bloc, data_bloc, root_bloc;
	
	boolean doevent = true;

	public sValueBloc dataview_bloc = null;
	
	public nAutoID autoid;

//	public File byteFile;
	
	

	public sData(App a) {
		super(); 
		app = a; input = app.input; ref = ""; base_ref = ""; 
		parent = this; data = this; 
//		Save_Bloc.app = app; Save_Data.app = app; Save_List.app = app;
		adress = ""+adress_token;
		
		autoid = new nAutoID();
		
//		byteFile = new File(app);
		
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
//		run_pool = new nPool<sRun>() {
//			protected sRun newObject() { return new sRun(); } };
			
		bloc_builders = new HashMap<String, sBloc_Builder>();
		common_bloc_builders = new ArrayList<sBloc_Builder>();
		
		setting_space = newSpace("setting", sSpace.Use.SETTING);
	    setting_bloc = setting_space.root;

		data_space = newSpace("database", sSpace.Use.DATABASE);
		data_bloc = data_space.root;

	    root_space = newSpace("root", sSpace.Use.WORK);
	    root_bloc = root_space.root;
	    root_bloc.open_in_dataview = true;

	    // file_init()
		setting_savepath = Utl.copy(App.setting_file);
		def_root_savepath = "root_" + app.gdx.window_title + file_extension;
		def_db__savepath = "database_" + app.gdx.window_title + data_extension;
		
		file = new sFile(this);
		val_datab_savepath = setting_bloc.obtainStr("val_datab_savepath", def_db__savepath);
		val_root_savepath = setting_bloc.obtainStr("val_root_savepath", def_root_savepath);
		
	}
	
	public void dispose() {
//		byteFile.dispose();
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
	public final static String data_ext_txt = "sdb";
	public final static String setting_ext_txt = "sdb";
	public final static String file_extension = "."+file_ext_txt;
	public final static String data_extension = "."+data_ext_txt;
	public final static String setting_extension = "."+setting_ext_txt;
	public sFile file = null;
	public String setting_savepath;
	public String def_root_savepath;
	public String def_db__savepath;
	public sStr val_datab_savepath;
	public sStr val_root_savepath;
	
	public void space_save(sSpace sp, String path, boolean auto_add_file) {
//		app.log("sData space_save "+sp.ref);
		file.open(path, auto_add_file); 
		file.empty();
		sp.save_to(file.getBloc());
		file.save();
		file.close();
	}
	
	public void space_load(sSpace sp, String path) {
		file.open(path);
		file.load();
		sp.setup_from(file.getBloc());
//		file.close();
	}

	public void setting_save() {
		space_save(setting_space, setting_savepath, true);
	}

	public void setting_load() { 
		space_load(setting_space, setting_savepath);
	}

	public void full_save() { 
//		app.log("sData full_save");
		setting_save();
		space_save(data_space, val_datab_savepath.get(), true);
		space_save(root_space, val_root_savepath.get(), true);
	}
	public void full_load() { 
		setting_load();
		space_load(data_space, val_datab_savepath.get());
		space_load(root_space, val_root_savepath.get()); // load delayed, keep file 6 frame
		app.addDelayEvent(7, new nRun() { public void run() {
			file.close(); }});
	}


//	public void empty_all() {
//		
//	}

	public void re_full_load() {
		app.LOADING_SCREEN_FRAME = 30;
		data.app.addDelayEvent(1, new nRun() { public void run() {
//			empty_all();
			full_load(); 
		}});
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
					app.logn("ERROR doing getBlocFromAdress, cant find bloc " + 
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
			app.logn("ERROR doing getBlocFromAdress: in bloc " + 
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
					app.logn("ERROR doing getValFromAdress, cant find bloc " + 
							adress_list[i]); 
					return null; }
				current_bloc = next_bloc;
			}
		}
		sValue val = current_bloc.getValue(adress_list[adress_list.length - 1]);
		if (val == null) {
			app.logn("ERROR doing getValFromAdress: in bloc " + 
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












//void mysetup() {
//  Save_List sl = new Save_List();
//  Save_Bloc sb = new Save_Bloc("save data");

//  int a = 0, b = 1, c = 2;
//  println("start: a " + a + " b " + b + " c " + c);

//  //gather datas
//  sb.newData("a",str(a));
//  sb.newData("b",str(b));
//  sb.newData("c",str(c));

//  //change data
//  sb.setData("b",str(5));

//  //save
//  sb.save_to("savetest.txt");

//  //load
//  sb.load_from("savetest.txt");

//  //retrieve data
//  a = int(sb.getData("a"));
//  b = int(sb.getData("b"));
//  c = int(sb.getData("c"));

//  println("end: a " + a + " b " + b + " c " + c);
//}

/*
 //* Listing files in directories and subdirectories
 //* by Daniel Shiffman.  
 //* 
 //* This example has three functions:<br />
 //* 1) List the names of files in a directory<br />
 //* 2) List the names along with metadata (size, lastModified)<br /> 
 //*    of files in a directory<br />
 //* 3) List the names along with metadata (size, lastModified)<br />
 //*    of files in a directory and all subdirectories (using recursion) 



import java.util.Date;

void setup() {

  // Using just the path of this sketch to demonstrate,
  // but you can list any directory you like.
  String path = sketchPath();

  println("Listing all filenames in a directory: ");
  String[] filenames = listFileNames(path);
  printArray(filenames);

  println("\nListing info about all files in a directory: ");
  File[] files = listFiles(path);
  for (int i = 0; i < files.length; i++) {
    File f = files[i];    
    println("Name: " + f.getName());
    println("Is directory: " + f.isDirectory());
    println("Size: " + f.length());
    String lastModified = new Date(f.lastModified()).toString();
    println("Last Modified: " + lastModified);
    println("-----------------------");
  }

  println("\nListing info about all files in a directory and all subdirectories: ");
  ArrayList<File> allFiles = listFilesRecursive(path);

  for (File f : allFiles) {
    println("Name: " + f.getName());
    println("Full path: " + f.getAbsolutePath());
    println("Is directory: " + f.isDirectory());
    println("Size: " + f.length());
    String lastModified = new Date(f.lastModified()).toString();
    println("Last Modified: " + lastModified);
    println("-----------------------");
  }

  noLoop();
}

// Nothing is drawn in this program and the draw() doesn't loop because
// of the noLoop() in setup()
void draw() {
}

// This function returns all the files in a directory as an array of Strings  
String[] listFileNames(String dir) {
  File file = new File(dir);
  if (file.isDirectory()) {
    String names[] = file.list();
    return names;
  } else {
    // If it's not a directory
    return null;
  }
}

// This function returns all the files in a directory as an array of File objects
// This is useful if you want more info about the file
File[] listFiles(String dir) {
  File file = new File(dir);
  if (file.isDirectory()) {
    File[] files = file.listFiles();
    return files;
  } else {
    // If it's not a directory
    return null;
  }
}

// Function to get a list of all files in a directory and all subdirectories
ArrayList<File> listFilesRecursive(String dir) {
  ArrayList<File> fileList = new ArrayList<File>(); 
  recurseDir(fileList, dir);
  return fileList;
}

// Recursive function to traverse subdirectories
void recurseDir(ArrayList<File> a, String dir) {
  File file = new File(dir);
  if (file.isDirectory()) {
    // If you want to include directories in the list
    a.add(file);  
    File[] subfiles = file.listFiles();
    for (int i = 0; i < subfiles.length; i++) {
      // Call this function on all files in this directory
      recurseDir(a, subfiles[i].getAbsolutePath());
    }
  } else {
    a.add(file);
  }
}
 */






