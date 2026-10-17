package data;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.badlogic.gdx.math.Vector2;

import util.Utl;

public class File_Data {
	
	public sData file;
	public String ref;
	byte[] ref_byte;
	int ref_byte_nb = 0;
	byte[] ref_byte_nb_bytes;
	
	byte[] data_byte;
	int data_byte_nb = 0;
	byte[] data_byte_nb_bytes;

	File_Bloc parent = null;

	public File_Data(sData f) { file = f; }
	
	public File_Data init(String n) { 
		ref = ""; 
		ref_byte = null; 
		ref_byte_nb = 0;
		ref_byte_nb_bytes = null;
		data_byte = null;
		data_byte_nb = 0;
		data_byte_nb_bytes = null;

		if (parent != null) parent.removeData(this);
		parent = null;
		
		ref = Utl.copy(n); 
		ref_byte = ref.getBytes();
		ref_byte_nb = ref_byte.length;
		ref_byte_nb_bytes = file.getBytes(ref_byte_nb);
		return this;
	}
	
	public int size() { return 2 * sData.BYTE_SIZE_INT + ref_byte_nb + data_byte_nb; }

	public void clear() {
		ref = ""; 
		ref_byte = null; 
		ref_byte_nb = 0;
		ref_byte_nb_bytes = null;
		data_byte = null;
		data_byte_nb = 0;
		data_byte_nb_bytes = null;

		if (parent != null) parent.removeData(this);
		parent = null;
		
		file.filedata_pool.free(this);
	}
	

	public void to_stream(OutputStream stream) throws IOException {

//		file.app.log("File_Data "+ref+" to_stream");
		
		stream.write(ref_byte_nb_bytes);
		stream.write(ref_byte);
		
		stream.write(data_byte_nb_bytes);
		stream.write(data_byte);
	}

	private int readed_nb = 0;
	public void from_stream(InputStream stream) throws IOException {

//		String l = "File_Data "+ref+" from_stream";
//		if (parent != null) l += " in "+parent.ref;
//		if (parent != null && parent.parent != null) l += " in "+parent.parent.ref;
//		file.app.log(l);

		ref_byte_nb_bytes = new byte[sData.BYTE_SIZE_INT];
		readed_nb = stream.read(ref_byte_nb_bytes);
		if (readed_nb != sData.BYTE_SIZE_INT) {
			Utl.logn("ERROR: File_Data "+ref+" from_stream : stream.read(ref_byte_nb_bytes); "
					+ "not enough bytes readed: "+readed_nb+" instead of "+sData.BYTE_SIZE_INT);
			return; }
		ref_byte_nb = file.getInt(ref_byte_nb_bytes);
		ref_byte = new byte[ref_byte_nb];
		readed_nb = stream.read(ref_byte);
		if (readed_nb != ref_byte_nb) {
			Utl.logn("ERROR: File_Data "+ref+" from_stream : stream.read(ref_byte); "
					+ "not enough bytes readed: "+readed_nb+" instead of "+ref_byte_nb);
			return; }
		ref = file.getStr(ref_byte);

		data_byte_nb_bytes = new byte[sData.BYTE_SIZE_INT];
		readed_nb = stream.read(data_byte_nb_bytes);
		if (readed_nb != sData.BYTE_SIZE_INT) {
			Utl.logn("ERROR: File_Data "+ref+" from_stream : stream.read(data_byte_nb_bytes); "
					+ "not enough bytes readed: "+readed_nb+" instead of "+sData.BYTE_SIZE_INT);
			return; }
		data_byte_nb = file.getInt(data_byte_nb_bytes);
		data_byte = new byte[data_byte_nb];
		readed_nb = stream.read(data_byte);
		if (readed_nb != data_byte_nb) {
			Utl.logn("ERROR: File_Data "+ref+" from_stream : stream.read(data_byte); "
					+ "not enough bytes readed: "+readed_nb+" instead of "+data_byte_nb);
			return; }
	}
	
	
	
	
	
	public void set(Object d) { 
		if (d instanceof String) set((String)d); 
		else if (d instanceof Float) set((float)d);
		else if (d instanceof Integer) set((int)d);
		else if (d instanceof Boolean) set((boolean)d);
		else if (d instanceof Vector2) set((Vector2)d); 
		else Utl.logn("ERROR: File_Data "+ref+" set(Object) : "
				+ "unrecognized Object class : "+d.getClass().getName());}
	
	public void set(byte[] d) { 
		data_byte = d; 
		data_byte_nb = d.length; 
		data_byte_nb_bytes = file.getBytes(data_byte_nb);
	}
	
	public void set(String s) { set(file.getBytes(s)); }
	public void set(int s) { set(file.getBytes(s)); }
	public void set(float s) { set(file.getBytes(s)); }
	public void set(boolean s) { set(file.getBytes(s)); }
	public void set(Vector2 s) { set(file.getBytes(s)); }
	
	public byte[] getData() { return data_byte; }
	public String getStr() { return file.getStr(data_byte); }
	public int getInt() { return file.getInt(data_byte); }
	public float getFlt() { return file.getFlt(data_byte); }
	public boolean getBoo() { return file.getBoo(data_byte); }
	public Vector2 getVec() { return file.getVec(data_byte); }
	
}
