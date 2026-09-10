package data;

import gui.nAlign;
import gui.nInterface;
import gui.nWidget;
import net.nNetwork;
import util.Utl;

public class sArr extends sValue {
	
	public String getString() { 
		String s = "w:"+len;
//		for (int i = 0 ; i < len ; i++) s += "val"+i+":"+val[i]+" ";
		return s; 
	}
	
	public void clear() { 
		super.clear(); 
		val = new String[0]; len = 0;
		data.arr_pool.free(this);
	}
	
	public int size() { return len; }
	
	private String[] val = new String[0];
	private int len = 0;
	
	public void resize(int l) {
		String[] n = new String[l];
		for (int i = 0 ; i < len && i < l ; i++) n[i] = Utl.copy(val[i]);
		for (int i = len ; i < l ; i++) n[i] = "";
		len = l;
		val = n;
	}
	public void empty() { for (int i = 0 ; i < len ; i++) val[i] = ""; }
	
	public sArr() { super(); }
	
	//called when obtained from pool
	public sArr init(sValueBloc b, String n, String s) {
		super.init(b, "arr", n, s); return this; }
		
	//called when freed by pool
	@Override
	public void reset() {
		// TODO Auto-generated method stub
		super.reset();
		resize(0);
	}

	public void set(int i, String v) {
		if (i >= len) resize(i+1);
		if (!v.equals(val[i])) doChange();
		val[i] = Utl.copy(v);
	}
	public void set(int i, int v) { set(i, ""+v); }
	public void set(int i, float v) { set(i, ""+v); }
	public void set(int i, boolean v) { if (v) set(i, "true"); else set(i, "false"); }

	public void add(String v) { set(len, v); }
	public void add(int v) { set(len, ""+v); }
	public void add(float v) { set(len, ""+v); }
	public void add(boolean v) { if (v) set(len, "true"); else set(len, "false"); }
	
	public String get(int i) { if (i < len) return val[i]; else return ""; }
	public int getInt(int i) {
		if (i < len) return Utl.toint(val[i]);
		return 0; }
	public float getFloat(int i) {
		if (i < len) return Utl.tofloat(val[i]);
		return 0; }
	public boolean getBool(int i) {
		if (i < len) {
			if (val[i].equals("true")) return true;
			else if (val[i].equals("false")) return false;
		} return false; }
	
	public boolean contains(String s) {
		for (String v : val) { if (v.equals(s)) return true; } return false; }

//	void save_to_bloc(Save_Bloc svb) { 
//		super.save_to_bloc(svb);
//		svb.newData("len", len);
//		for (int i = 0 ; i < len ; i++) svb.newData("val"+i, val[i]);
//	}
//	void load_from_bloc(Save_Bloc svb) { 
//		super.load_from_bloc(svb);
//		int l = svb.getInt("len");
//		resize(l); empty();
//		for (int i = 0 ; i < l ; i++) val[i] = svb.getData("val"+i);
//	}
	void save_to_bloc(File_Bloc svb) { 
		super.save_to_bloc(svb);
		svb.newData("len", len);
		for (int i = 0 ; i < len ; i++) svb.newData("val"+i, val[i]);
	}
	void load_from_bloc(File_Bloc svb) { 
		super.load_from_bloc(svb);
		int l = svb.getInt("len");
		resize(l); empty();
		for (int i = 0 ; i < l ; i++) val[i] = svb.getString("val"+i);
	}
	public void toNetMsg(nNetwork.UpdateValue uv) {
		uv.put("len", len);
		for (int i = 0 ; i < len ; i++) uv.put("val"+i, val[i]);
	}
	public void fromNetMsg(nNetwork.UpdateValue uv) {
		set(uv.getInt("valx"), uv.getInt("valy"));
		int l = uv.getInt("len");
		resize(l); empty();
		for (int i = 0 ; i < l ; i++) val[i] = uv.getStr("val"+i);
	}

	public Object get_val() { return null; }

	public void populate_viewer(nInterface interf) {
		interf.add_row();
		interf.set_param("entry_height", "6");
		nWidget label = interf.add_row_label(10, "");
		label.setTextAutoReturn(true);
		label.setTextLineLength(40);
//		label.setFont(10);
		label.setTextAlignment(nAlign.CENTER, nAlign.BOTTOM);
		
		String txt = ""; 
		for (int i = 0; i < val.length ; i++) txt += " - "+i+":"+val[i];
		label.setText(txt);
	}
}
