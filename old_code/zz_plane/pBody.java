package zz_plane;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.badlogic.gdx.math.Vector2;

import data.*;
import util.Utl;
import util.nMap;
import zz_applet.Applet;

public class pBody extends sPoolable {

	public static Applet app;

	public static void build(Applet a) {
		app = a;
	}
	
	
	
	
	

	public void signalChange() {
		space.signalChange(this); 
	}
	

	public static int max_param = 20;

	
	public void update_families() {
		space.families_updated = true;
	}
	
	
	private static final int start_data_nb = 2;
	
//	private float getFloat(Object[] c, int i) { return (float)c[i]; }
	private int getInt(Object[] c, int i) { return (int)c[i]; }
	private boolean getBool(Object[] c, int i) { return (boolean)c[i]; }
	private String getStr(Object[] c, int i) { return (String)c[i]; }
	public void init_from_array(Object[] c) {
		
		empty();
		
		int cnt = 0;
		
		boolean is_used = getBool(c, 0);
		if (!is_used) return;
		
		int param_nb = getInt(c, 1); 
		
		cnt += start_data_nb;
		
		for (int i = 0 ; i < param_nb ; i++) {
			String prop_ref = getStr(c, cnt+i*3);
			String param_ref = getStr(c, cnt+i*3+1);
			String ref = getStr(c, cnt+i*3+2);
			pProperty prop = pProperty.body_propertys.get(prop_ref);
			obtainParam(prop, param_ref, ref);
		}
		cnt += param_nb*3;
		
		update_families();
	}
	public void load_from_array(Object[] c) {

		int cnt = 0;
		
		boolean is_used = getBool(c, 0);
		if (!is_used) return;
		
		int param_nb = getInt(c, 1); 
		
		cnt += start_data_nb;

		nMap<pParam> old_params = new nMap<pParam>();
		nMap<pProperty> new_params = new nMap<pProperty>();
		for (Map.Entry<String,pParam> prm : params.entrySet()) 
			old_params.put(prm.getKey(), prm.getValue());
		params.clear();
		
		for (int i = 0 ; i < param_nb ; i++) {
			String prop_ref = getStr(c, cnt+i*3);
			String param_ref = getStr(c, cnt+i*3+1);
			String ref = getStr(c, cnt+i*3+2);
			pProperty prop = pProperty.body_propertys.get(prop_ref);
			
			if (prop != null && space.param_pools.get(prop.ref) != null) {
				if (space.param_pools.get(prop.ref).get(param_ref) != null) {
					pParam p = space.param_pools.get(prop.ref).get(param_ref);
					if (old_params.get(ref) == p) { 
						old_params.remove(ref,p); 
						params.put(ref,p); 
					} 
					else { 
						p.addUser(this); 
						params.put(ref, p);
					}
				} else { new_params.put(ref, prop); }
			}
		}
		cnt += param_nb*3;

		for (pParam prm : old_params.all()) { prm.removeUser(this); }
		
		for (Map.Entry<String,pProperty> me : new_params.entrySet()) 
			newParam(me.getValue(), me.getKey());
		
		update_families();
		signalChange();
	}
	private void set(Object[] c, int i, Object data) { c[i] = Utl.copy(data); }
	public void to_array(Object[] c) {
		int cnt = 0;
		
		set(c, 0, true); 
		set(c, 1, (int)params.size()); 
		
		cnt += start_data_nb;
		
		int pi = 0;
		for (Entry<String, pParam> p : params.entrySet()) {
			set(c, cnt+pi, p.getValue().prop.ref);
			set(c, cnt+pi+1, p.getValue().pool_ref);
			set(c, cnt+pi+2, p.getKey());
			pi+=3;
		}
		cnt += params.size() * 3;
		
	}
	
	public void from_tab(sTab t, int c) {
		
		empty();
		
		int cnt = 0;
		
		boolean is_used = t.getBool(c, 0);
		if (!is_used) return;
		
		int param_nb = t.getInt(c, 1); 
		
		cnt += start_data_nb;
		
		for (int i = 0 ; i < param_nb ; i++) {
			String prop_ref = t.getStr(c, cnt+i*3);
			String param_ref = t.getStr(c, cnt+i*3+1);
			String ref = t.getStr(c, cnt+i*3+2);
			pProperty prop = pProperty.body_propertys.get(prop_ref);
			obtainParam(prop, param_ref, ref);
		}
		cnt += param_nb*3;
		
	}
	public void to_tab(sTab t, int c) {
		int cnt = 0;
		
		t.set(c, 0, true); 
		t.set(c, 1, (int)params.size()); 
		
		cnt += start_data_nb;
		
		int pi = 0;
		for (Entry<String, pParam> p : params.entrySet()) {
			t.set(c, cnt+pi, p.getValue().prop.ref);
			t.set(c, cnt+pi+1, p.getValue().pool_ref);
			t.set(c, cnt+pi+2, p.getKey());
			pi+=3;
		}
		cnt += params.size() * 3;
		
	}
	public int data_size() {
		int cnt = 0;
		cnt += start_data_nb;
		cnt += params.size() * 3;
		return cnt;
	}

	
	public pParam obtainParam(pProperty prop, String pool_ref, String ref) {
		if (prop == null) return null;
		sPool<pParam> pool = space.param_pools.get(prop.ref);
		if (pool == null) return null;
		if (pool.get(pool_ref) != null) {
			pParam p = pool.get(pool_ref);
			p.addUser(this);
			if (!params.hasKey(ref)) params.put(ref, p);
			update_families();
			signalChange();
			return p; 
		} else {
			return newParam(prop, ref);
		}
	}
	
	
	public pParam newParam(String prop) { return newParam(prop, prop); }
	public pParam newParam(String prop, String ref) {
		return newParam(pProperty.body_propertys.get(prop), ref); }
	public pParam newParam(pProperty prop) { return newParam(prop, prop.ref); }
	public pParam newParam(pProperty prop, String ref) {
		if (prop == null) return null;
		sPool<pParam> pool = space.param_pools.get(prop.ref);
		if (params.get(ref) != null) {
			int cnt = 0;
			String base_ref = "" + ref;
			ref = base_ref + "_" + cnt;
			while (params.get(ref) != null) { cnt ++; ref = base_ref + "_" + cnt; }
		}
		pParam p = space.new_param(prop.ref);
		if (p != null) {
			p.addUser(this);
			params.put(ref, p);
			update_families();
			signalChange();
		}
		return p;
	}
	
	public pParam newCommonParam(pProperty prop, String r) {
		if (prop == null) return null;
		sPool<pParam> pool = space.param_pools.get(prop.ref);
		pParam p = pool.get_any();
		if (p != null) {
			p.addUser(this);
			params.put(r, p);
			update_families();
			signalChange();
		} else {
			p = newParam(prop, r);
		}
		return p;
	}

	public pParam addParam(String r, pParam prm) {
		if (prm == null) return null;
//		sPool<pParam> pool = space.param_pools.get(prm.prop.ref);
		pParam p = params.get(r);
		if (p != null) {
			p.removeUser(this);
			params.remove(r, p);
		}
		prm.addUser(this);
		params.put(r, prm);
		update_families();
		signalChange();
		return p;
	}
	

	public void removeParam(String r) { removeParam(params.get(r)); }
	public void removeParam(pParam p) {
		if (p != null) {
			params.remove(p);
			p.removeUser(this);
//			pParamPool pool = space.param_pools.get(p.prop.ref);
//			pool.free(p);
			update_families();
			signalChange();
		}
	}

//	public pBody addSpecies(String r) {
//		pSpecies s = pSpecies.body_species.get(r);
//		if (s != null) { 
//			for (Entry<String, pProperty> p : s.props.entrySet()) 
//				if (!p.getValue().mode_common) newParam(p.getValue(), p.getKey()); 
//				else newCommonParam(p.getValue(), p.getKey()); 
//		}
//		return this;
//	}

	
	public pSpace space = null;
	public nMap<pParam> params = new nMap<pParam>();
	
	public pBody() {}
	
	public pBody init(pSpace s) { space = s; return this; }
	
	public void empty() {
		if (space != null) 
			for (Map.Entry<pFamily, ArrayList<pBody>> me : space.families.entrySet()) {
			pFamily fam = me.getKey();
			ArrayList<pBody> list = me.getValue();
			if (fam.contains(this)) me.getValue().remove(this);
		}
		for (pParam prm : params.all()) {
			prm.removeUser(this);
		}
		params.clear();
		update_families();
		signalChange();
	}

	public void clear_action() {
		for (Map.Entry<pFamily, ArrayList<pBody>> me : space.families.entrySet()) {
			pFamily fam = me.getKey();
			if (fam.contains(this) && fam.clear_run != null) fam.clear_run.run(this); 
		}
		
		empty();
		
		if (space != null && !Utl.has(space.delBodys, this)) space.delBodys.add(this);
	}
	
	
	
	

	public boolean hasParam(String r) { 
		for (pParam p : params.all()) if (p.prop.ref.equals(r)) return true;
		return false; }
	public pParam param(String r) { return params.get(r); }

	public ArrayList<pParam> allProperty(String r) { 
		ArrayList<pParam> ar = new ArrayList<pParam>();
		for (pParam p : params.all()) if (p.prop.ref.equals(r)) ar.add(p);
		return ar; }
	
	public void addVec(String p, String r, float x, float y) { param(p).set(r,getVec(p,r).add(x,y)); }
	public void addVec(String p, String r, Vector2 v) { param(p).set(r,getVec(p,r).add(v)); }
	public void sclVec(String p, String r, float v) { param(p).set(r,getVec(p,r).scl(v)); }
	public void setVec(String p, String r, Vector2 v) { param(p).set(r,v); }
	public void setVec(String p, String r, float x, float y) { param(p).set(r,new Vector2(x,y)); }
	public void setFlt(String p, String r, float v) { param(p).set(r,v); }
	public void addFlt(String p, String r, float v) { param(p).set(r, getFlt(p,r) + v); }
	public void setInt(String p, String r, int v) { param(p).set(r,v); }
	public void addInt(String p, String r, int v) { param(p).set(r, getInt(p,r) + v); }
	public void setBoo(String p, String r, boolean v) { param(p).set(r,v); }
	public void setStr(String p, String r, String v) { param(p).set(r,v); }
	
	public Vector2 getVec(String p, String r) { return param(p).get(r,Vector2.class); }
	public float getFlt(String p, String r) { return param(p).get(r,Float.class); }
	public int getInt(String p, String r) { return param(p).get(r,Integer.class); }
	public boolean getBoo(String p, String r) { return param(p).get(r,Boolean.class); }
	public String getStr(String p, String r) { return param(p).get(r,String.class); }
	

	public void collecSetVec(String p, String r, int i, float x, float y) {
		param(p).collecSetVec(r, i, x, y); }
	public void collecSet(String p, String r, int i, Object o) {
		param(p).collecSet(r, i, o); }
	public <T> T collecGet(String p, String r, int i, Class<T> ct) {
		return param(p).collecGet(r, i, ct); }
	public int collecSize(String p, String r) {
		return param(p).getCollecSize(r); }
	public void collecEmpty(String p, String r) {
		param(p).collecEmpty(r); }
	public void collecAdd(String p, String r, Object o) {
		param(p).collecAdd(r, o); }
	public void collecRemove(String p, String r, Object o) {
		param(p).collecRemove(r, o); }
	public void collecRefAdd(String p, String r, pParam o) {
		param(p).collecRefAdd(r, o); }
	public void collecRefRemove(String p, String r, pParam o) {
		param(p).collecRefRemove(r, o); }
	public void collecBodyAdd(String p, String r, pBody o) {
		param(p).collecBodyAdd(r, o); }
	public void collecBodyRemove(String p, String r, pBody o) {
		param(p).collecBodyRemove(r, o); }
	public <T> ArrayList<T> getCollecData(String p, String r, Class<T> ct) {
		return param(p).getCollecData(r, ct); }
	public ArrayList<pParam> getCollecRef(String p, String r) {
		return param(p).getCollecRef(r); }
	public ArrayList<pBody> getCollecBody(String p, String r) {
		return param(p).getCollecBody(r); }
	
	
	
	public String getParamRef(pParam par) {
		for (Map.Entry<String,pParam> me : params.entrySet()) {
			if (me.getValue() == par) return me.getKey();
		}
		Utl.logn("pBody.getParamRef did not find param");
		return "";
	}
	
}
