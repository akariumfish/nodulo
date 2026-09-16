package data;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Vector2;

import util.Utl;
import util.nMap;


public class File_Bloc {

	public int get_data_cnt() {
		int c = datas.size();
		for (File_Bloc b : blocs.all()) c += b.get_data_cnt();
		return c;
	}

	
	
	
	public sData file;
	public String ref;
	byte[] ref_byte;
	int ref_byte_nb = 0;
	byte[] ref_byte_nb_bytes;
	
	File_Bloc parent = null;

	public File_Bloc(sData f) { file = f; }
	
	public File_Bloc init(String n) { 
		if (parent != null) parent.removeBloc(this);
		empty();
		is_clearing = false;
		parent = null;
		ref = Utl.copy(n); 
		ref_byte = ref.getBytes();
		ref_byte_nb = ref_byte.length; 
		ref_byte_nb_bytes = file.getBytes(ref_byte_nb);
		return this;
	}
	
	nMap<File_Data> datas = new nMap<File_Data>();
	nMap<File_Bloc> blocs = new nMap<File_Bloc>();
	
	public ArrayList<File_Bloc> getBlocList() { return blocs.all(); }
	public ArrayList<File_Data> getDataList() { return datas.all(); }
	
	
	public File_Data newData(String n, Object d) { 
		if (hasData(n)) {
			String t = "ERROR: File_Bloc "+ref;
			t += " newData : data ref "+n+" allready exist";
			Utl.logn(t);
			return null;
		}
		File_Data sd = file.filedata_pool.obtain().init(n); sd.parent = this; sd.set(d); datas.put(n,sd); return sd; }

	public File_Bloc newBloc(String n) { 
		if (hasBloc(n)) {
			Utl.logn("ERROR: File_Bloc "+ref+" newBloc : "
					+ "bloc ref "+n+" allready exist");
			return null; }
		File_Bloc sd = file.filebloc_pool.obtain().init(n); sd.parent = this; blocs.put(n,sd); return sd; }

	public File_Bloc removeBloc(File_Bloc n) { 
		if (blocs.hasVal(n)) blocs.remove(n.ref, n); n.parent = null; return n; }
	public File_Data removeData(File_Data n) { 
		if (datas.hasVal(n)) datas.remove(n.ref, n); n.parent = null; return n; }

	public File_Bloc getBloc(String n) { return blocs.get(n); }
	public boolean hasBloc(String n) { return blocs.get(n) != null; }
	public File_Data getData(String n) { return datas.get(n); }
	public boolean hasData(String n) { return datas.get(n) != null; }
	
	public void setData(String n, String d) { if (hasData(n)) getData(n).set(d); }
	public void setData(String n, int d) { if (hasData(n)) getData(n).set(d); }
	public void setData(String n, float d) { if (hasData(n)) getData(n).set(d); }
	public void setData(String n, boolean d) { if (hasData(n)) getData(n).set(d); }
	public void setData(String n, Vector2 d) { if (hasData(n)) getData(n).set(d); }

	public String getString(String n) { if (hasData(n)) return getData(n).getStr(); else return null; }
	public int getInt(String n) { if (hasData(n)) return getData(n).getInt(); else return 0; }
	public float getFloat(String n) { if (hasData(n)) return getData(n).getFlt(); else return 0; }
	public boolean getBoolean(String n) { if (hasData(n)) return getData(n).getBoo(); else return false; }
	public Vector2 getVector2(String n) { if (hasData(n)) return getData(n).getVec(); else return null; }

	public int size() { 
		        //ref byte nb byte                 data n bloc nb bytes
		int s = sData.BYTE_SIZE_INT + ref_byte_nb + 2 * sData.BYTE_SIZE_INT;
		for (File_Data sd : datas.all()) s += sd.size();
		for (File_Bloc sb : blocs.all()) s += sb.size();
		return s; 
	}

	public void empty() {
		
//		file.app.log("FB empty "+ref);
		
		for (File_Data d : datas.tmp_all()) d.clear();
		datas.clear();
		for (File_Bloc b : blocs.tmp_all()) b.clear();
		blocs.clear();

//		file.app.log("FB empty "+ref+" done");
	}

	private boolean is_clearing = false;
	public void clear() {
		if (is_clearing) return;
		is_clearing = true;
//		file.app.log("FB clear "+ref);
		
		empty();
		
		if (parent != null) parent.removeBloc(this);
		parent = null;
		
		file.filebloc_pool.free(this);
		is_clearing = false;
	}
	

	public void save_to(String path, boolean add_absent_file) {

//		file.app.log("File save_to path: "+path);
		
		if (add_absent_file && !Utl.file_exist(path)) {
			FileHandle fl = Gdx.files.local(path);
			if (!fl.exists()) fl.writeString(" ", false);
		}
		OutputStream s = file.getOutputStream(path);
		if (s != null) {
			try {
				to_stream(s);
				s.flush();
				s.close();

//				file.app.log("File save_to done");
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			Utl.logn("ERROR : no output stream");
		}
	}

	public void load_from(String path) {

//		file.app.log("File load_from path: "+path);
		
		InputStream s = file.getInputStream(path);
		if (s != null) {
			empty();
			try {
				from_stream(s);
				s.close();

//				file.app.log("File load_from done");
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			Utl.logn("ERROR : no input stream");
		}
	}
	

	public void to_stream(OutputStream stream) throws IOException {
		
//		file.app.log("File_Bloc "+ref+" to_stream");
		
		stream.write(ref_byte_nb_bytes);
		stream.write(ref_byte);
		
		stream.write(file.getBytes(datas.size()));
		for (File_Data d : datas.all()) d.to_stream(stream);

		stream.write(file.getBytes(blocs.size()));
		for (File_Bloc d : blocs.all()) d.to_stream(stream);
		
	}

	private byte[] tmp_bytes;
	private int readed_nb = 0;
	public void from_stream(InputStream stream) throws IOException {

//		file.app.log("File_Bloc "+ref+" from_stream");
		
		ref_byte_nb_bytes = new byte[sData.BYTE_SIZE_INT];
		readed_nb = stream.read(ref_byte_nb_bytes);
		if (readed_nb != sData.BYTE_SIZE_INT) {
			Utl.logn("ERROR: File_Bloc "+ref+" from_stream : stream.read(ref_byte_nb_bytes); "
					+ "not enough bytes readed: "+readed_nb+" instead of "+sData.BYTE_SIZE_INT);
			return; }
		ref_byte_nb = file.getInt(ref_byte_nb_bytes);
		ref_byte = new byte[ref_byte_nb];
		readed_nb = stream.read(ref_byte);
		if (readed_nb != ref_byte_nb) {
			Utl.logn("ERROR: File_Bloc "+ref+" from_stream : stream.read(ref_byte); "
					+ "not enough bytes readed: "+readed_nb+" instead of "+ref_byte_nb);
			return; }
		ref = file.getStr(ref_byte);
		
		tmp_bytes = new byte[sData.BYTE_SIZE_INT];
		readed_nb = stream.read(tmp_bytes);
		if (readed_nb != sData.BYTE_SIZE_INT) {
			Utl.logn("ERROR: File_Bloc "+ref+" from_stream load data: stream.read(tmp_bytes); "
					+ "not enough bytes readed: "+readed_nb+" instead of "+sData.BYTE_SIZE_INT);
			return; }
		int data_nb = file.getInt(tmp_bytes);
		for (int i = 0 ; i < data_nb ; i++) {
			File_Data sd = file.filedata_pool.obtain();
			sd.init("");
			sd.from_stream(stream);
			String n = sd.ref;
//			sd.init(n); 
			datas.put(n,sd);
			sd.parent = this;
		}

		tmp_bytes = new byte[sData.BYTE_SIZE_INT];
		readed_nb = stream.read(tmp_bytes);
		if (readed_nb != sData.BYTE_SIZE_INT) {
			Utl.logn("ERROR: File_Bloc "+ref+" from_stream load bloc: stream.read(tmp_bytes); "
					+ "not enough bytes readed: "+readed_nb+" instead of "+sData.BYTE_SIZE_INT);
			return; }
		int bloc_nb = file.getInt(tmp_bytes);
		for (int i = 0 ; i < bloc_nb ; i++) {
			File_Bloc sd = file.filebloc_pool.obtain();
			sd.init("");
			sd.from_stream(stream);
//			String n = sd.ref;
//			sd.init(n); 
			blocs.put(sd.ref,sd);
			sd.parent = this;
		}
	}
	
}
