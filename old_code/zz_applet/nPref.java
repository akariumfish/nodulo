package zz_applet;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.math.Vector2;

import util.nMap;

public class nPref {
	
	public interface PrefAccess {
		public <T> T getPref(String r, Class<T> cl);
		public void setPref(String p, String r, Object d);
	}

	public <T> T getPref(String r, Class<T> cl) {
		if (cl == Boolean.class) {
			return (T)(Object)current_pref.getBoolean(r);
		} else if (cl == Float.class) {
			return (T)(Object)current_pref.getFloat(r);
		} else if (cl == Integer.class) {
			return (T)(Object)current_pref.getInteger(r);
		} else if (cl == String.class) {
			return (T)(Object)current_pref.getString(r);
		} else if (cl == Vector2.class) {
			return (T)(Object)(new Vector2(
					current_pref.getFloat(r+"_x"), current_pref.getFloat(r+"_y")));
		} else {
			return null;
		}
	}

	public nMap<Preferences> prefs = new nMap<Preferences>();
	public Preferences current_pref;
	
	public void setCurrentPref(String r) {
		if (prefs.hasKey(r)) current_pref = prefs.get(r);
	}
	
	public void setPref(String r, Object d) {
		if (current_pref == null) return;
		setPref(current_pref, r, d);
	}
	public void setPref(String pref_ref, String r, Object d) {
		Preferences pref = prefs.get(pref_ref);
		if (pref == null) return;
		setPref(pref, r, d);
	}
	public void setPref(Preferences pref, String r, Object d) {
		if (pref == null) return;
		Class<?> cl = d.getClass();
		if (cl == Boolean.class) {
			pref.putBoolean(r, (boolean)d);
		} else if (cl == Float.class) {
			pref.putFloat(r, (float)d);
		} else if (cl == Integer.class) {
			pref.putInteger(r, (int)d);
		} else if (cl == String.class) {
			pref.putString(r, (String)d);
		} else if (cl == Vector2.class) {
			pref.putFloat(r+"_x", ((Vector2)d).x);
			pref.putFloat(r+"_y", ((Vector2)d).y);
		} else {
			return;
		}
		pref.flush();
	}
	
}
