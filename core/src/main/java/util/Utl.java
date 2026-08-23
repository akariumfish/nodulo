package util;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.noodle.nodulo.GdxApp;

import app.App;
import data.sBoo;
import data.sFlt;
import data.sInt;
import data.sStr;
import data.sValue;
import data.sVec;

public class Utl {
	
	
	
	
	public interface Priorizable { public int getSortingPriority(); }
	public interface Ordered { 
		public boolean isOrderedPre(); 
		public boolean isOrderedMid(); 
		public boolean isOrderedPost(); }


	public static <T extends Ordered & Priorizable> 
	ArrayList<T> orderPrioDuplic(ArrayList<T> l) {
		ArrayList<T> list = new ArrayList<T>();
		int max_prio = 0;
		for (T r : l) if (r.getSortingPriority() < max_prio) max_prio = r.getSortingPriority();
		for (int i = max_prio ; i >= 0 ; i--) 
			for (T r : l) if (r.getSortingPriority() == i && r.isOrderedPre()) list.add(r);
		for (int i = max_prio ; i >= 0 ; i--) 
			for (T r : l) if (r.getSortingPriority() == i && r.isOrderedMid()) list.add(r);
		for (int i = max_prio ; i >= 0 ; i--) 
			for (T r : l) if (r.getSortingPriority() == i && r.isOrderedPost()) list.add(r);
		return list; }
	public static <T extends Ordered & Priorizable> ArrayList<T> orderRevPrioDuplic(ArrayList<T> l) {
		ArrayList<T> list = new ArrayList<T>();
		int max_prio = 0;
		for (T r : l) if (r.getSortingPriority() < max_prio) max_prio = r.getSortingPriority();
		for (int i = 0 ; i <= max_prio ; i++)
			for (T r : l) if (r.getSortingPriority() == i && r.isOrderedPre()) list.add(r);
		for (int i = 0 ; i <= max_prio ; i++)
			for (T r : l) if (r.getSortingPriority() == i && r.isOrderedMid()) list.add(r);
		for (int i = 0 ; i <= max_prio ; i++)
			for (T r : l) if (r.getSortingPriority() == i && r.isOrderedPost()) list.add(r);
		return list; }
	
	public static <T extends Ordered> ArrayList<T> orderDuplic(ArrayList<T> l) {
		ArrayList<T> list = new ArrayList<T>();
		for (T r : l) if (r.isOrderedPre()) list.add(r);
		for (T r : l) if (r.isOrderedMid()) list.add(r);
		for (T r : l) if (r.isOrderedPost()) list.add(r);
		return list; }

	public static <T extends Priorizable> ArrayList<T> prioDuplic(ArrayList<T> l) {
		ArrayList<T> list = new ArrayList<T>();
		int max_prio = 0;
		for (T r : l) if (r.getSortingPriority() < max_prio) max_prio = r.getSortingPriority();
		for (int i = max_prio ; i >= 0 ; i--)
			for (T r : l) if (r.getSortingPriority() == i) list.add(r);
		return list; }
	public static <T extends Priorizable> ArrayList<T> revprioDuplic(ArrayList<T> l) {
		ArrayList<T> list = new ArrayList<T>();
		int max_prio = 0;
		for (T r : l) if (r.getSortingPriority() < max_prio) max_prio = r.getSortingPriority();
		for (int i = 0 ; i <= max_prio ; i++)
			for (T r : l) if (r.getSortingPriority() == i) list.add(r);
		return list; }
	
	
	private static final ArrayList<Object> sorting_tmp_list = new ArrayList<Object>();
	@SuppressWarnings("unchecked")
	public static <T extends Ordered> void orderSort(ArrayList<T> l) {
		sorting_tmp_list.clear();
		for (T r : l) if (r.isOrderedPre()) sorting_tmp_list.add(r);
		for (T r : l) if (r.isOrderedMid()) sorting_tmp_list.add(r);
		for (T r : l) if (r.isOrderedPost()) sorting_tmp_list.add(r);
		l.clear(); for (Object r : sorting_tmp_list) l.add((T)r);
		sorting_tmp_list.clear(); }
	@SuppressWarnings("unchecked")
	public static <T extends Priorizable> void prioSort(ArrayList<T> l) {
		sorting_tmp_list.clear();
		int max_prio = 0;
		for (T r : l) if (r.getSortingPriority() < max_prio) max_prio = r.getSortingPriority();
		for (int i = max_prio ; i >= 0 ; i--)
			for (T r : l) if (r.getSortingPriority() == i) sorting_tmp_list.add(r);
		l.clear(); for (Object r : sorting_tmp_list) l.add((T)r);
		sorting_tmp_list.clear(); }
	@SuppressWarnings("unchecked")
	public static <T extends Priorizable> void revprioSort(ArrayList<T> l) {
		sorting_tmp_list.clear();
		int max_prio = 0;
		for (T r : l) if (r.getSortingPriority() < max_prio) max_prio = r.getSortingPriority();
		for (int i = 0 ; i <= max_prio ; i++)
			for (T r : l) if (r.getSortingPriority() == i) sorting_tmp_list.add(r);
		l.clear(); for (Object r : sorting_tmp_list) l.add((T)r);
		sorting_tmp_list.clear(); }
	
	
	
	
	
	public static boolean getBoo(MapProperties prop, String r) {
		return (prop.get(r, Boolean.class) != null && 
				prop.get(r, Boolean.class));
	}
	
	
	
	public static float radToDeg(float d) {
		return d/((float)Math.PI)*360f; }
	
	
	/**
	 * Get the method name for a depth in call stack. <br />
	 * Utility function
	 * @param depth depth in the call stack (0 means current method, 1 means call method, ...)
	 * @return method name
	 */
	public static String getMethodName(final int depth)
	{
	  final StackTraceElement[] ste = Thread.currentThread().getStackTrace();
	  if (ste.length <= 2 + depth) return ""; 
	  return ste[2 + depth].getMethodName(); 
	}
	
	public static void logn() { Gdx.app.log(log_pref1+log_pref2, log_stack); log_stack = ""; }
	public static void logn(String t) { Gdx.app.log(log_pref1+log_pref2, log_stack+t); log_stack = ""; }
	public static String log_pref1 = "", log_pref2 = "";
	public static void log(String t) { log_stack += t; }
	private static String log_stack = "";

	
	public static boolean file_exist(String path) {
		FileHandle handle = Gdx.files.local(path);
		if (handle.exists()) return true;
		return false;
	}
	
	public static int clamp(int v, int min, int max) {
		if (v > max) v = max; if (v < min) v = min; return v; }
	public static int rgbToInt(Color c) {
		int r = (int)(255f*c.r); int g = (int)(255f*c.g); 
		int b = (int)(255f*c.b); int a = (int)(255f*c.a);
	    return rgbToInt(r,g,b,a);
	}
	public static int rgbToInt(int red, int green, int blue, int alpha) {
	    alpha = clamp(alpha, 0, 255);
	    red = clamp(red, 0, 255);
	    green = clamp(green, 0, 255);
	    blue = clamp(blue, 0, 255);
	    return (alpha << 24) | (red << 16) | (green << 8) | blue;
	}
	public static Color intToColor(int argb) {
		int a = (argb >> 24) & 0xFF;
		int r = (argb >> 16) & 0xFF;
		int g = (argb >> 8) & 0xFF;
		int b = argb & 0xFF;
	    return new Color(r/255.0f, g/255.0f, b/255.0f, a/255.0f);
	}
	
	public static Color color(int r, int g, int b, int a) {
		return new Color(r/255.0f, g/255.0f, b/255.0f, a/255.0f); 
	}
	
	public static Color color(int r, int g, int b) {
		return color(r, g, b, 255); }
	public static Color color(int l, int a) {
		return color(l,l,l,a); }
	public static Color color(int l) {
		return color(l,l,l,255); }


	static public float distanceSegmentPoint(Vector2 s1, Vector2 s2, Vector2 p) {
		return Intersector.distanceSegmentPoint(s1, s2, p); }
	
	/** Determines whether the supplied rectangles intersect and, if they do,
	 *  sets the supplied {@code intersection} rectangle to the area of overlap.
	 * 
	 * @return whether the rectangles intersect
	 */
	static public boolean intersect(Rectangle rectangle1, Rectangle rectangle2) {
	    return rectangle1.overlaps(rectangle2); }
	static public boolean intersect(Rectangle rectangle1, Rectangle rectangle2, Rectangle intersection) {
	    if (rectangle1.overlaps(rectangle2)) {
	        intersection.x = Math.max(rectangle1.x, rectangle2.x);
	        intersection.width = Math.min(rectangle1.x + rectangle1.width, rectangle2.x + rectangle2.width) - intersection.x;
	        intersection.y = Math.max(rectangle1.y, rectangle2.y);
	        intersection.height = Math.min(rectangle1.y + rectangle1.height, rectangle2.y + rectangle2.height) - intersection.y;
	        return true;
	    }
	    return false;
	}

	public static float rng() { return (float)Math.random(); }
	public static float rng(float min, float max) { 
		return min + (float)Math.random() * (max - min); }
	
	public static float linear_to_log(float f, int c) {
		for (int i = 0 ; i < c ; i++) f = linear_to_log(f);
		return f; }
	public static float linear_to_log(float f) { // curve 0 > 1 line to log
		return 1f - (float)Math.log10(1f + (1f-f)*9f); }

	public static float log_to_linear(float f, int c) {
		for (int i = 0 ; i < c ; i++) f = log_to_linear(f);
		return f; }
	public static float log_to_linear(float f) { // curve 0 > 1 
		return 1f - ((float)Math.pow(10f,1f-f) - 1f) / 9f; }
	
	public static int roundDown(int value, int multiplier) {
	    if (multiplier <= 0) throw new IllegalArgumentException();
	    return value / multiplier * multiplier;
	}
	public static int roundHalfUp(int value, int multiplier) {
	    if (multiplier <= 0) throw new IllegalArgumentException();
	    return (value + (value < 0 ? multiplier / -2 : multiplier / 2)) / multiplier * multiplier;
	}
	public static int roundUp(int value, int multiplier) {
	    if (multiplier <= 0) throw new IllegalArgumentException();
	    return (value + (value < 0 ? 1 - multiplier : multiplier - 1)) / multiplier * multiplier;
	}
	
	public static int toint(String s) { 
		try {
			if (s.trim().length() > 0) return (int)Float.parseFloat(s); else return 0; 
		} catch (NumberFormatException ex) {
			ex.printStackTrace(System.out);
		}
		return 0; }
	public static float tofloat(String s) { 
		try {
			if (s.length() > 0) return Float.parseFloat(s); else return 0; 
		} catch (NumberFormatException ex) {
			ex.printStackTrace(System.out);
		}
		return 0; 
	}
	public static boolean tobool(String s) { 
		if (s.length() > 0) return (s.equals("T")); else return false; }
	public static Vector2 tovec(String s) { return new Vector2().fromString(s); }
	
	public static int toint(float s) { return (int)(s); }
	
	public static String tostr(int s) { return ""+s; }
	public static String tostr(float s) { return ""+s; }
	public static String tostr(boolean s) { if (s) return "T"; else return "F"; }
	public static String tostr(Vector2 s) { return s.toString(); }
	
	public static String trimFlt(float s) { return trimFlt(s,2); }
	public static String trimFlt(float s, int r) { 
		String f = "";
		f += "0.";
		for (int i = 0; i < r; i++) f += "0";
		if ((s > 0 && s < Math.pow(10, -r)) || (s > 0 && s > Math.pow(10, r)) || 
				(s < 0 && s > Math.pow(10, -r)) || (s < 0 && s < Math.pow(10, r)) ) f += "E0";
		DecimalFormatSymbols symb = new DecimalFormatSymbols();
		symb.setExponentSeparator("e");
		DecimalFormat frmt = new DecimalFormat(f, symb);
		return frmt.format(s);
	}
	
	
	
	public static float mapToCircularValuesDist(float current, float cible, float increment, float start, float stop) {
		if (start > stop) {float i = start; start = stop; stop = i;}
		increment = Math.abs(increment);

		while (cible > stop) {cible -= (stop - start);}
		while (current > stop) {current -= (stop - start);}
		while (cible < start) {cible += (stop - start);}
		while (current < start) {current += (stop - start);}

		if (cible < current) {
			if ( (current - cible) <= (stop - current + cible - start) ) {
				if (increment >= current - cible) {return current - cible;}
				else                              {return increment;}
			} else {
				if (increment >= stop - current + cible - start) {return stop - current + cible - start;}
				else if (current + increment < stop)             {return increment;}
				else                                             {return increment;}
			}
		} else if (cible > current) {
			if ( (cible - current) <= (stop - cible + current - start) ) {
				if (increment >= cible - current) {return cible - current;}
				else                              {return increment;}
			} else { 
				if (increment >= stop - cible + current - start) {return stop - cible + current - start;}
				else if (current - increment > start)            {return increment;}
				else                                             {return increment;}
			}
		}
		return 0;
	}
	public static float mapToCircularValuesDir(float current, float cible, float increment, float start, float stop) {
		if (start > stop) {float i = start; start = stop; stop = i;}
		increment = Math.abs(increment);

		while (cible > stop) {cible -= (stop - start);}
		while (current > stop) {current -= (stop - start);}
		while (cible < start) {cible += (stop - start);}
		while (current < start) {current += (stop - start);}

		if (cible < current) {
			if ( (current - cible) <= (stop - current + cible - start) ) {
				return -1;
			} else {
				return 1;
			}
		} else if (cible > current) {
			if ( (cible - current) <= (stop - cible + current - start) ) {
				return 1;
			} else { 
				return -1;
			}
		}
		return 0;
	}
	public static float mapToCircularValues(float current, float cible, float increment, float start, float stop) {
		if (start > stop) {float i = start; start = stop; stop = i;}
		increment = Math.abs(increment);

		while (cible > stop) {cible -= (stop - start);}
		while (current > stop) {current -= (stop - start);}
		while (cible < start) {cible += (stop - start);}
		while (current < start) {current += (stop - start);}

		if (cible < current) {
			if ( (current - cible) <= (stop - current + cible - start) ) {
				if (increment >= current - cible) {return cible;}
				else                              {return current - increment;}
			} else {
				if (increment >= stop - current + cible - start) {return cible;}
				else if (current + increment < stop)             {return current + increment;}
				else                                             {return start + (increment - (stop - current));}
			}
		} else if (cible > current) {
			if ( (cible - current) <= (stop - cible + current - start) ) {
				if (increment >= cible - current) {return cible;}
				else                              {return current + increment;}
			} else { 
				if (increment >= stop - cible + current - start) {return cible;}
				else if (current - increment > start)            {return current - increment;}
				else                                             {return stop - (increment - (current - start));}
			}
		}
		return cible;
	}
	
	
	
	
	

	public static Rectangle get_bounding_rect(ArrayList<Rectangle> arr) { 
		Rectangle r = new Rectangle();
		if (arr == null || arr.size() == 0) return null;
		ArrayList<Rectangle> arr2 = new ArrayList<Rectangle>();
		for (Rectangle a : arr) if (a != null) arr2.add(a);
		if (arr2.size() == 0) return null;
		r.set(arr2.get(0));
		for (Rectangle a : arr2) {
			if (a.x < r.x) r.x = a.x; if (a.y < r.y) r.y = a.y; }
		r.width = 0; r.height = 0;
		for (Rectangle a : arr2) {
			if (a.x+a.width > r.x+r.width) r.width = a.x+a.width-r.x; 
			if (a.y+a.height > r.y+r.height) r.height = a.y+a.height-r.y; }
		return r;
	}
	
	
	
	
	
	public static String[] split(String s, char c) { 
		int cnt = 0; for (int i = 0; i < s.length(); i++) if (s.charAt(i) == c) cnt++;
		String[] r = new String[cnt+1]; cnt = 0; int prev = 0;
		for (int i = 0; i < s.length(); i++) if (s.charAt(i) == c) {
			r[cnt] = s.substring(prev, i); cnt++; prev = i+1; }
		if (prev < s.length()) r[cnt] = s.substring(prev, s.length());
		return r; }
	public static String[] concat(String[] s1, String[] s2) { 
		String[] s = new String[s1.length + s2.length];
		for (int i = 0; i < s1.length; i++) s[i] = s1[i];
		for (int i = 0; i < s2.length; i++) s[i + s1.length] = s2[i];
		return s; }
	public static String copy(String n) { 
		if (n != null) return new String(n);
		else return ""; }
	
//	public static <T> T castTo(Object o, Class<T> cl) {
//        try { return (T) o; } catch (ClassCastException e) {
//            // log the exception or other error handling
//        } return null; }
	
	@SuppressWarnings("unchecked")
	public static <T> T copy(T n) { 
		if (n == null) return n;
		if (n instanceof String) return (T)new String((String)n);
		else if (n instanceof Vector2) return (T)new Vector2((Vector2)n);
		else if (n instanceof Integer) return (T)((Integer)n);
		else if (n instanceof Float) return (T)((Float)n);
		else if (n instanceof Boolean) return (T)((Boolean)n);
		else return n; }
	

	public static Object[] duplic(Object[] arr) {
		if (arr == null) return null;
		Object[] dup = new Object[arr.length];
		for (int i = 0 ; i < arr.length ; i++) dup[i] = copy(arr[i]);
		return dup;
	}

	public static <T> nMap<T> duplic(nMap<T> arr) {
		nMap<T> dup = new nMap<T>();
		if (arr != null) 
			for (Map.Entry<String,T> me : arr.entrySet()) 
				dup.put(me.getKey(), copy(me.getValue()));
		return dup;
	}

	public static <T> ArrayList<T> duplic(ArrayList<T> arr) {
		ArrayList<T> dup = new ArrayList<T>();
		if (arr != null) 
			for (int i = 0 ; i < arr.size() ; i++) 
				dup.add(copy(arr.get(i)));
		return dup;
	}

	public static boolean contains(String[] arr, String r) { 
		for (String s : arr) if (s.equals(r)) return true;
		return false; }

	public static boolean contains(ArrayList<String> arr, String r) { 
		for (String s : arr) if (s.equals(r)) return true;
		return false; }

	public static <T> boolean has(ArrayList<T> arr, T r) { 
		for (T s : arr) if (s == r) return true;
		return false; }
	
	
	
	

	public static final int data_type_nb = 5;
	public static final Class<?>[] data_type = new Class<?>[data_type_nb];
	public static final String[] type_names = new String[data_type_nb];
	public static final String[] type_short_names = new String[data_type_nb];
	public static final byte[] type_id = new byte[data_type_nb];
	
	public static void build() {

		new_type(Float.class, "flt", "FLT", sFlt.class, 1);
		new_type(Integer.class, "int", "INT", sInt.class, 1);
		new_type(Boolean.class, "boo", "BOO", sBoo.class, 1);
		new_type(String.class, "str", "STR", sStr.class, 1);
		new_type(Vector2.class, "vec", "VEC", sVec.class, 2);
		
		nScripted.build_codes();

		nPainting.register();
		
	}
	static class vType {
		public String name, ref, type, type_maj;
		public Class<?> _class; public Class<? extends sValue> sval;
		public int index = -1;
		public vType(Class<?> c, String t, String tm, Class<? extends sValue> cv) {
			_class = c; name = c.getSimpleName(); ref = c.getName();
			type = t; type_maj = tm; sval = cv; index = type_cnt; } }
	
	static int type_cnt = 0;
	private static void new_type(Class<?> ct, String t, String tm, Class<? extends sValue> cv, int s) {
		vType type = new vType(ct, t, tm, cv);
		type_name_class.put(type.name,type._class);
		type_ref_class.put(type.ref,type._class);
		type_type_class.put(type.type,type._class);
		type_type_maj.put(type.type,type.type_maj);
		type_class_name.put(type._class, type.name);
		type_class_type.put(type._class, type.type);
		type_class_index.put(type._class, type.index);
		type_data_size.put(type._class, s);
		data_type[type_cnt] = type._class;
		type_names[type_cnt] = type.name;
		type_short_names[type_cnt] = type.type;
		type_id[type_cnt] = (byte)type_cnt;
		type_id_class.put(type_id[type_cnt],type._class);
		type_class_id.put(type._class,type_id[type_cnt]);
		type_cnt++;
	}
	
	public static HashMap<Byte, Class<?>> type_id_class = new HashMap<Byte, Class<?>>();
	public static HashMap<Class<?>, Byte> type_class_id = new HashMap<Class<?>, Byte>();
	public static HashMap<String, Class<?>> type_name_class = new HashMap<String, Class<?>>();
	public static HashMap<String, Class<?>> type_ref_class = new HashMap<String, Class<?>>();
	public static HashMap<String, Class<?>> type_type_class = new HashMap<String, Class<?>>();
	public static HashMap<String, String> type_type_maj = new HashMap<String, String>();
	public static HashMap<Class<?>, String> type_class_name = new HashMap<Class<?>, String>();
	public static HashMap<Class<?>, String> type_class_type = new HashMap<Class<?>, String>();
	public static HashMap<Class<?>, Integer> type_class_index = new HashMap<Class<?>, Integer>();
	public static HashMap<Class<?>, Integer> type_data_size = new HashMap<Class<?>, Integer>();

	public static boolean type_is_used(Class<?> ct) {
		return type_class_name.get(ct) != null; }

	public static String to_string(Object o) {
		if (o == null) return "null";
		if (!type_is_used(o.getClass())) return "??";
		if (o instanceof Vector2) {
			Vector2 v = (Vector2)o;
			return v.toString();
		} else if (o instanceof Float) {
			return Float.toString((float)o);
		} else if (o instanceof Integer) {
			return Integer.toString((int)o);
		} else if (o instanceof Boolean) {
			return Boolean.toString((boolean)o);
		} else if (o instanceof String) {
			return (String)o;
		}
		return "??";
	}
	public static String to_code(Object o) {
		if (o == null) return "null";
		if (!type_is_used(o.getClass())) return "??";
		if (o instanceof Vector2) {
			Vector2 v = (Vector2)o;
			return "new Vector2("+Float.toString(v.x)+"f,"+Float.toString(v.y)+"f)";
		} else if (o instanceof Float) {
			return Float.toString((float)o)+"f";
		} else if (o instanceof Integer) {
			return "(int)"+Integer.toString((int)o);
		} else if (o instanceof Boolean) {
			return Boolean.toString((boolean)o);
		} else if (o instanceof String) {
			return "\""+((String)o)+"\"";
		}
		return "??";
	}
	public static <T> T from_string(String o, Class<?> ct) {
		if (!type_is_used(ct)) return null;
		if (o == null || o.length() == 0) return null;
		if (ct == Vector2.class) {
			try {
				Vector2 v = new Vector2();
				v.fromString(o);
				return (T)v; 
			} catch (Exception ex) {
				logn(ex.toString());
			} }
		else if (ct == Float.class) {
			try {
				Object v = Float.parseFloat(o);
				return (T)v; 
			} catch (NumberFormatException ex) {
				logn(ex.toString());
			} }
		else if (ct == Integer.class) {
			try {
				Object v = Integer.parseInt(o);
				return (T)v; 
			} catch (NumberFormatException ex) {
				logn(ex.toString());
			} }
		else if (ct == Boolean.class) {
			try {
				Object v = Boolean.parseBoolean(o);
				return (T)v; 
			} catch (NumberFormatException ex) {
				logn(ex.toString());
			} }
		else if (ct == String.class) {
			return (T)o; }
		return null;
	}

	public static <T> T new_object(Class<T> ct) {
		if (!type_is_used(ct)) return null;
		if (ct == Vector2.class) {
			Vector2 v = new Vector2();
			return (T)v; }
		else if (ct == Float.class) {
			Object v = 0.0f;
			return (T)v; }
		else if (ct == Integer.class) {
			Object v = (int)0;
			return (T)v; }
		else if (ct == Boolean.class) {
			Object v = false;
			return (T)v; }
		else if (ct == String.class) {
			return (T)""; }
		return null;
	}
	
}
