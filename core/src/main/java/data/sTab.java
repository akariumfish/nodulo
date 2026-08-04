package data;

import com.badlogic.gdx.math.Vector2;

import gui.nInterface;
import gui.nWidget;
import gui.nWidgetGroup;
import net.nNetwork;
import util.Utl;
//import zz_dump.Save_Bloc;
//import zz_plane2.cPlane;
import zz_applet.Applet;

public class sTab extends sValue {
	
	public String getString() { 
		String s = "w:"+width;
//		for (int i = 0 ; i < len ; i++) s += "val"+i+":"+val[i]+" ";
		return s; 
	}
	
	public void clear() { 
		super.clear(); 
		val = new String[0][0]; width = 0;
		data.tab_pool.free(this);
	}
	
	private Object[][] val = new Object[0][0];
	private int width = 0;

	public void setWidth(int w) {
		if (w == width) return;
		Object[][] n = new Object[w][];
		for (int i = 0 ; i < width && i < w ; i++) {
			
			n[i] = new Object[val[i].length];
			for (int j = 0 ; j < val[i].length ; j++)
				n[i][j] = Utl.copy(val[i][j]);
		}
		for (int i = width ; i < w ; i++) n[i] = new Object[0];
		width = w; val = n;
	}
	public void setRowHeight(int i, int h) {
		if (h == val[i].length) return;
		Object[] n = new Object[h];
		
		for (int j = 0 ; j < n.length && j < val[i].length ; j++)
			n[j] = Utl.copy(val[i][j]);
	
		for (int j = val[i].length ; j < n.length ; j++) 
			n[j] = "";
		
		val[i] = n;
	}
	public void empty() { 
		for (int i = 0 ; i < width ; i++) val[i] = new Object[0]; 
	}

	public int width() { return width; }
	public int height(int i) { if (i >= 0 && i < width) return val[i].length; return 0; }
	
	public sTab() { super(); }
	
	//called when obtained from pool
	public sTab init(sValueBloc b, String n, String s) {
		super.init(b, "tab", n, s); return this; }
		
	//called when freed by pool
	public void reset() {
		super.reset();
		setWidth(0);
	}
	public void set(int w, int h, Object v) {
		if (w >= width) setWidth(w+1);
		if (h >= val[w].length) setRowHeight(w, h+1);
		
		if ((val[w][h] == null && v != null) || 
				(val[w][h] != null && v == null) || 
				(val[w][h] != null && v != null && !v.equals(val[w][h]))) doChange();
		val[w][h] = Utl.copy(v);
	}

	public <T> T get(int w, int h, Class<T> ct) { 
		if (w >= 0 && h >= 0 && w < width && h < val[w].length && ct != null) 
			return (T)val[w][h]; else return null; }
	public Object getObj(int w, int h) { 
		if (w >= 0 && h >= 0 && w < width && h < val[w].length) return val[w][h]; 
		else return null; }
	public String getStr(int w, int h) { return get(w,h, String.class); }
	public int getInt(int w, int h) { return get(w,h, Integer.class); }
	public float getFloat(int w, int h) { return get(w,h, Float.class); }
	public boolean getBool(int w, int h) { return get(w,h, Boolean.class); }

	public String getGenericString(int w, int h) { return get(w,h, Object.class).toString(); }
	
	void save_to_bloc(File_Bloc svb) { 
		super.save_to_bloc(svb);
		svb.newData("wid", width);
		for (int i = 0 ; i < width ; i++) {
			svb.newData("col_"+i+"_hei", val[i].length);
			for (int j = 0 ; j < val[i].length ; j++) {
				Object v = val[i][j];
//				if (v == null) v = "";
				if (v != null && Utl.type_is_used(v.getClass())) {
					svb.newData("val"+i+"_"+j, v);
					svb.newData("val"+i+"_"+j+"_class", v.getClass().getName());
				} else {
					svb.newData("val"+i+"_"+j, "");
					svb.newData("val"+i+"_"+j+"_class", "");
				}
			}
		}
	}
	void load_from_bloc(File_Bloc svb) { 
		super.load_from_bloc(svb);
		empty();
		int w = svb.getInt("wid");
		setWidth(w);
		for (int i = 0 ; i < width ; i++) {
			int h = svb.getInt("col_"+i+"_hei");
			setRowHeight(i, h);
			for (int j = 0 ; j < h ; j++) {
				if (svb.getString("val"+i+"_"+j+"_class").equals(String.class.getName()))
					val[i][j] = svb.getString("val"+i+"_"+j);
				else if (svb.getString("val"+i+"_"+j+"_class").equals(Vector2.class.getName()))
					val[i][j] = svb.getVector2("val"+i+"_"+j);
				else if (svb.getString("val"+i+"_"+j+"_class").equals(Float.class.getName()))
					val[i][j] = svb.getFloat("val"+i+"_"+j);
				else if (svb.getString("val"+i+"_"+j+"_class").equals(Integer.class.getName()))
					val[i][j] = svb.getInt("val"+i+"_"+j);
				else if (svb.getString("val"+i+"_"+j+"_class").equals(Boolean.class.getName()))
					val[i][j] = svb.getBoolean("val"+i+"_"+j);
				else val[i][j] = null;
			}
		}
			
	}
	
	public void toNetMsg(nNetwork.UpdateValue uv) {
//		uv.put("wid", width);
//		uv.put("hei", height);
//		for (int i = 0 ; i < width ; i++) 
//			for (int j = 0 ; j < height ; j++) uv.put("val"+i+"_"+j, val[i][j]);
	}
	public void fromNetMsg(nNetwork.UpdateValue uv) {
//		int w = uv.getInt("wid");
//		int h = uv.getInt("hei");
//		resize(w, h);
//		empty();
//		for (int i = 0 ; i < width ; i++) 
//			for (int j = 0 ; j < height ; j++) val[i][j] = uv.getStr("val"+i+"_"+j);
	}

	public Object get_val() { return null; }


	public void populate_viewer(nInterface interf) {
		interf.add_row();
		interf.add_row_label(10, "viewed zone :");

		interf.add_row();
		interf.add_row_label(3, "width : ");
		nWidget fld_x_strt = interf.add_row_field(3, "0");
		nWidget fld_x_end = interf.add_row_field(3, ""+(width-1));
//		interf.add_row();
//		interf.add_row_label(3, "height : ");
//		nWidget fld_y_strt = interf.add_row_field(3, "0");
//		nWidget fld_y_end = interf.add_row_field(3, ""+(height-1));
		
		interf.add_row();
		nWidget trig_view = interf.add_row_trigg(6, "VIEW");
		
		interf.add_row();
		nWidgetGroup list = interf.add_treelist(10, 6);

//		trig_view.addEventTrigger(new nRunnable() { public void run() {
//			int x_strt = Applet.toint(fld_x_strt.getText());
//			int x_end = Applet.toint(fld_x_end.getText());
//			int y_strt = Applet.toint(fld_y_strt.getText());
//			int y_end = Applet.toint(fld_y_end.getText());
//			if (x_strt < 0 || x_end < 0 || y_strt < 0 || y_end < 0 || 
//					x_strt >= width || y_strt >= height || 
//					x_end >= width || y_end >= height || 
//					x_end - x_strt < 0 || y_end - y_strt < 0 ) return;
//
//			interf.change_current_list(list);
//			for (int i = x_strt; i <= x_end ; i++) {
//				interf.add_list_entry("column "+i);
//				for (int j = y_strt; j <= y_end ; j++) {
//					interf.add_list_entry(" "+j+" : "+val[i][j]);
//					interf.go_up_tree();
//				}
//				interf.go_up_tree();
//			}
//		}});
	}
}
