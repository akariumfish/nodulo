package zz_patch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.math.Vector2;

import data.*;
import util.Utl;
import util.nMap;
import zz_applet.Applet;
import zz_patch.pStandard.ObjDef;
import zz_plane.*;

public class pInstance extends sPoolable {
	
	public boolean isStandard(String r) { return (stand != null && stand.ref.equals(r)); }
	
	public pInstance(pSheet s) {
		sheet = s; patch = s.patch; data_used = new int[Utl.data_type_nb]; }

	public pPatch patch;	
	public pSheet sheet;	
	public pStandard stand = null;
	
	public boolean obj_is_init = false;
	
	int[] data_used;
	public Object[][] datas;
	int collec_used;
	public String[] collecs;
	int inst_used;
	public String[] insts;
	
	public ArrayList<pColl> collec_list = new ArrayList<pColl>();
	

	
	/*  pInstance init sequance :
	 * 		Created : 
	 * 			Pool.obtain(stand,args) > find free inst :
	 * 				inst.init_poolable
	 * 				inst.create_run(stand,args) > 
	 * 					inst.join_standard(stand)
	 * 					stand.createInst(inst,args)
	 * 				inst.init_run() > inst.stand.initInst(inst)
	 * 				inst.load_run() > inst.stand.loadInst(inst)
	 * 		Loaded :
	 * 			Pool.load() > for all tab row if used :
	 * 				inst.init_poolable
	 * 				inst.from_tab
	 * 					inst join loaded standard
	 * 				inst.init_run() > inst.stand.initInst(inst)
	 * 				inst.load_run() > inst.stand.loadInst(inst)
	 * 
	 */
	
	public void join_standard(pStandard p) {
		if (p != null) {
//			patch.app.log(pool_ref+" join stand "+p.ref);
			quit_standard();
			stand = p; 
//			patch.stand_insts.get(stand.ref).add(this);
			init_arrays();
			obtain_collecs();
			setAllDef();
		}
	}
	public void init_run() { 
		if (stand == null) return;
		stand.initInstance(this); 
	}
	
	public void load_run() {
		if (stand == null) return;
		stand.loadInstance(this); 
	}

	public void save_run() {
		if (stand == null) return;
		stand.saveInstance(this); 
	}
	
	public void quit_standard() {
		unpoint_this();
		if (stand != null) {
			stand.clearInstance(this);
//			patch.stand_insts.get(stand.ref).remove(this);
		}
		for (pColl c : collec_list) c.clear();
		objects.clear();
		var_vals.clear();
		obj_is_init = false;
		stand = null;
	}
	
	public void clear_action() {
		quit_standard();
		is_new = false;
		obj_is_init = false;
		point_this.clear();
	}

//	// called when adding new inst
	public pInstance init(pStandard p, Object ... args) { 
//		plane.app.log("cInstance.init(stand,args)");
		join_standard(p);
		stand.createInstance(this, args); 
		do_init(); do_load(); do_point_after_load();
		return this;
	}
	
	public void init_arrays() { 
		
		for (int i = 0 ; i < Utl.data_type_nb ; i++) {
			data_used[i] = stand.getDataUsed(Utl.data_type[i]); }

		collec_used = stand.getCollecUsed();
		collecs = new String[collec_used];
		for (int i = 0 ; i < collec_used ; i++) collecs[i] = ""; 

		inst_used = stand.getInstUsed();
		insts = new String[inst_used];
		for (int i = 0 ; i < inst_used ; i++) insts[i] = "";
		
		datas = new Object[Utl.data_type_nb][];
		
		int u = Utl.type_class_index.get(Vector2.class);
		datas[u] = new Vector2[data_used[u]];
		for (int i = 0 ; i < data_used[u] ; i++) datas[u][i] = new Vector2();
		
		u = Utl.type_class_index.get(Float.class);
		datas[u] = new Float[data_used[u]];
		for (int i = 0 ; i < data_used[u] ; i++) datas[u][i] = 0f;
		
		u = Utl.type_class_index.get(Integer.class);
		datas[u] = new Integer[data_used[u]];
		for (int i = 0 ; i < data_used[u] ; i++) datas[u][i] = (int)0;
		
		u = Utl.type_class_index.get(Boolean.class);
		datas[u] = new Boolean[data_used[u]];
		for (int i = 0 ; i < data_used[u] ; i++) datas[u][i] = false;
		
		u = Utl.type_class_index.get(String.class);
		datas[u] = new String[data_used[u]];
		for (int i = 0 ; i < data_used[u] ; i++) datas[u][i] = "";
		
		for (int i = 0 ; i < Utl.data_type_nb ; i++) {
			if (stand != null && stand.data_vals.get(Utl.data_type[i]) != null)
				for (Map.Entry<String, Integer> me : 
					stand.data_vals.get(Utl.data_type[i]).entrySet()) {
					Object o = getDef(me.getKey(), Utl.data_type[i]);
					if (o != null) setData(me.getKey(), Utl.copy(o));
				}
		}

		for (int i = 0 ; i < inst_used ; i++) insts[i] = "";
		
		for (ObjDef od : stand.objdefs) {
			Object o = od.newobj.do_get(this, od.param);
			setObject(od.ref,o);
		}
	}

	public static final int start_data_nb = 1;
	public String get_convert_str(String s, HashMap<String,String> map) {
		if (map != null && map.get(s) != null) s = Utl.copy(map.get(s)); return s; }
	public void from_tab(sTab t, int c) { from_tab(t, c, null); }
	public void from_tab(sTab t, int c, HashMap<String,String> map) {
		
		quit_standard();
		
		int cnt = start_data_nb;
		
		String stand_ref = t.getStr(c, cnt);
		stand = pStandard.get(stand_ref); 
		if (stand == null) { 
			patch.app.logn("ERROR pInst from_tab cant find stand < "+stand_ref+" >"); 
			return; }

		int row_height = t.height(c);
		if (row_height < stand.data_size()) {
			patch.app.logn("ERROR : "+pool_ref+" from_tab : tab row too small : tab:"+row_height+" stand.datasize:"+stand.data_size()); }
		
//		patch.stand_insts.get(stand.ref).add(this);
		
		init_arrays();

		cnt++;
		
		for (int i = 0 ; i < Utl.data_type_nb ; i++) {
			for (int j = 0 ; j < data_used[i] ; j++) {
				if (i == Utl.type_class_index.get(Vector2.class)) { 
					((Vector2)datas[i][j]).x = t.getFloat(c, cnt+(j*2));
					((Vector2)datas[i][j]).y = t.getFloat(c, cnt+(j*2)+1);
				} else if (i == Utl.type_class_index.get(Float.class)) { 
					datas[i][j] = t.getFloat(c, cnt+j);
				} else if (i == Utl.type_class_index.get(Integer.class)) { 
					datas[i][j] = t.getInt(c, cnt+j);
				} else if (i == Utl.type_class_index.get(Boolean.class)) { 
					datas[i][j] = t.getBool(c, cnt+j);
				} else if (i == Utl.type_class_index.get(String.class)) { 
					datas[i][j] = get_convert_str(t.getStr(c, cnt+j), map);
				}
			}
			cnt += data_used[i] * Utl.type_data_size.get(Utl.data_type[i]);
		}
		

		for (int j = 0 ; j < collec_used ; j++) {
			collecs[j] = get_convert_str(t.getStr(c, cnt+j), map);
		}
		obtain_collecs();
		cnt += collec_used;
		
		for (int j = 0 ; j < inst_used ; j++) {
			insts[j] = get_convert_str(t.getStr(c, cnt+j), map);
		}
		cnt += inst_used;
		
		int var_nb = t.getInt(c, cnt); cnt++;
		
		if (row_height != stand.data_size()+(var_nb * 3)) {
			patch.app.logn("ERROR : "+pool_ref+" from_tab : tab row too small : "
					+ "tab row has:"+row_height+" but need "
					+ "stand.datasize + vars:"+(stand.data_size()+(var_nb * 3))); }
		
		for (int j = 0 ; j < var_nb ; j++) {
			String ref = t.getStr(c, cnt+(j*3));
			String ct = t.getStr(c, cnt+(j*3)+1);
			String so = t.getStr(c, cnt+(j*3)+2);
			if (ct.equals(Float.class.getName())) 
				var_vals.put(ref, Utl.from_string(so, Float.class));
			else if (ct.equals(Integer.class.getName())) 
				var_vals.put(ref, Utl.from_string(so, Integer.class));
			else if (ct.equals(Boolean.class.getName())) 
				var_vals.put(ref, Utl.from_string(so, Boolean.class));
			else if (ct.equals(Vector2.class.getName())) 
				var_vals.put(ref, Utl.from_string(so, Vector2.class));
			else if (ct.equals(String.class.getName())) 
				var_vals.put(ref, get_convert_str(so, map));
		}
		cnt += var_nb * 3;
		
	}
	
	public void do_point_after_load() {
		if (stand == null) return;
		for (String s : stand.inst_vals.allKey()) if (getInst(s) != null) 
			getInst(s).pointed_by(s, false, this);
		for (String s : stand.collec_inst_pools.allKey()) 
			for (pInstance b : collecInstAll(s)) b.pointed_by(s, true, this);
	}
	
	public void to_tab(sTab t, int c) {
		
		if (stand == null) { t.set(c, 0, false); return; }
		
		t.set(c, 0, true);
		
		int cnt = start_data_nb;
		
		t.set(c, cnt, stand.ref);

		cnt++;
		
		for (int i = 0 ; i < Utl.data_type_nb ; i++) {
			for (int j = 0 ; j < data_used[i] ; j++) {
				if (i == Utl.type_class_index.get(Vector2.class)) { 
					Vector2 v = (Vector2)datas[i][j];
					t.set(c, cnt+(j*2), v.x);
					t.set(c, cnt+(j*2)+1, v.y);
				} else if (i == Utl.type_class_index.get(Float.class)) { 
					float v = (float)datas[i][j];
					t.set(c, cnt+j, v);
				} else if (i == Utl.type_class_index.get(Integer.class)) { 
					int v = (int)datas[i][j];
					t.set(c, cnt+j, v);
				} else if (i == Utl.type_class_index.get(Boolean.class)) { 
					boolean v = (boolean)datas[i][j];
					t.set(c, cnt+j, v);
				} else if (i == Utl.type_class_index.get(String.class)) { 
					String v = (String)datas[i][j];
					t.set(c, cnt+j, v);
				}
			}
			cnt += data_used[i] * Utl.type_data_size.get(Utl.data_type[i]);
		}

		for (int j = 0 ; j < collec_used ; j++) {
			t.set(c, cnt+j, collecs[j]);
		}
		cnt += collec_used;

		for (int j = 0 ; j < inst_used ; j++) {
			t.set(c, cnt+j, insts[j]);
		}
		cnt += inst_used;
		
		t.set(c, cnt, var_vals.size()); cnt++;
		int j = 0;
		for (Map.Entry<String,Object> me : var_vals.entrySet()) {
			t.set(c, cnt+(j*3), me.getKey());
			t.set(c, cnt+(j*3)+1, me.getValue().getClass().getName());
			t.set(c, cnt+(j*3)+2, Utl.to_string(me.getValue()));
			j++;
		}
		cnt += j * 3;
		
	}
	public int data_size() {
		int cnt = start_data_nb;
		cnt++;
		for (int i = 0 ; i < Utl.data_type_nb ; i++) 
			cnt += data_used[i] * Utl.type_data_size.get(Utl.data_type[i]);
		cnt += collec_used;
		cnt += inst_used;
		cnt += 1 + var_vals.size() * 3;
		return cnt;
	}
	
	public pColl obtainCollec(int i, String r) {
		if (stand == null) return null;
		if (sheet.collec_pool.get(r) != null) {
			pColl p = sheet.collec_pool.get(r);
			collec_list.add(p);
			return p; 
		} else {
			String col_ref = stand.getCollecRefFromId(i);
			pColl p = sheet.newCollec(stand, col_ref);
			if (p != null) {
				collec_list.add(p);
			} else {
				patch.app.logn("ERROR: pInstance could not obtain a collec");
			}
			return p;
		}
	}
	
	public void obtain_collecs() {
		for (int i = 0 ; i < collec_used ; i++) {
			pColl c = obtainCollec(i, collecs[i]);
			if (c == null) continue;
			collecs[i] = c.pool_ref; 
		}
	}

	public void setAllDef() {
		if (stand == null) return;
		
		for (int i = 0 ; i < Utl.data_type_nb ; i++) {
			if (stand != null && stand.data_vals.get(Utl.data_type[i]) != null)
				for (Map.Entry<String, Integer> me : 
					stand.data_vals.get(Utl.data_type[i]).entrySet()) {
					Object o = getDef(me.getKey(), Utl.data_type[i]);
					if (o != null) setData(me.getKey(), Utl.copy(o));
				}
		}

		for (pColl c : collec_list) c.empty();
		
		for (int i = 0 ; i < inst_used ; i++) insts[i] = "";
		
		for (ObjDef od : stand.objdefs) {
			Object o = od.newobj.do_get(this, od.param);
			setObject(od.ref,o);
		}
	}
	public <T> T getDef(String r, Class<T> ct) { 
		if (stand == null) return null;
		Object o = stand.getDataValDef(r, ct);
		if (o != null) return (T)o;
		return null;
	}
	
	
	
	
	public void unpoint_this() {
		for (Point p : Utl.duplic(point_this)) {
			if (p.in_collec) p.point_by.collecInstRemove(p.ref,this);
			else p.point_by.setInst(p.ref,""); }
		point_this.clear();
	}
	public class Point {
		pInstance point_by;
		String ref;
		boolean in_collec = false;
	}
	public ArrayList<Point> point_this = new ArrayList<Point>();
	public void pointed_by(String r, boolean collec, pInstance t) {
		Point p = new Point(); p.point_by = t; p.ref = r; p.in_collec = collec; 
		point_this.add(p); }
	public void unpointed_by(String r, boolean collec, pInstance t) {
		for (Point p : Utl.duplic(point_this)) 
			if (p.point_by == t && p.ref.equals(r) && p.in_collec == collec)
				point_this.remove(p); }
	
	
	

	public nMap<Object> var_vals = new nMap<Object>();

	public pInstance obtainVar(String r, Object o) { 
		if (!hasVar(r)) addVar(r,o); return this; }
	
	public pInstance addVar(String ref, Object var) {
		if (var_vals.hasKey(ref)) {
			stand.app.logn("ERROR : cInstance "+this.pool_ref+" has allready the key "+ref);
			return this; }
		var_vals.put(ref,Utl.copy(var)); return this; }
	public pInstance removeVar(String ref) { var_vals.remove(ref); return this; }
	public boolean hasVar(String ref) { return var_vals.hasKey(ref); }
	
	public pInstance setVar(String r, Object o) { 
		var_vals.remove(r); var_vals.put(r,Utl.copy(o)); return this; }
	public <T> T getVar(String r, Class<T> ct) { 
		if (var_vals.get(r) == null) return null; return Utl.copy((T)var_vals.get(r)); }
	public Object getVar(String r) { 
		if (var_vals.get(r) == null) return null; return Utl.copy(var_vals.get(r)); }

	
	public pInstance setData(String r, Object o) { 
		if (stand == null) return this;
		int val_id = stand.getDataValId(r, o.getClass());
		if (val_id == -1) return this;
		int data_id = Utl.type_class_index.get(o.getClass());
		datas[data_id][val_id] = Utl.copy(o);
		return this; 
	}
	public <T> T getData(String r, Class<T> ct) { 
		if (stand == null) return null;
		int val_id = stand.getDataValId(r, ct);
		if (val_id == -1) return null;
		int data_id = Utl.type_class_index.get(ct);
		if (datas[data_id][val_id] == null) return null;
		return Utl.copy((T)datas[data_id][val_id]); 
	}
	
//	// FOR DEBUG ONLY !!! 
//	private boolean hasData(String r, Class<?> ct) { 
//		return true; // << FOR DEBUG to make error crash
//		
////		if (stand == null) return false;
////		int val_id = stand.getDataValId(r, ct);
////		if (val_id == -1) return false;
////		int data_id = Utl.type_class_index.get(ct);
////		if (datas[data_id][val_id] == null) return false;
////		return true; 
//	}

	public pInstance setDataVec(String r, float x, float y) { 
		setData(r, new Vector2(x,y)); return this; }
	public pInstance setDataVec(String r, Vector2 v) { 
		setDataVec(r, v.x, v.y); return this; }
	public pInstance addDataVec(String r, float x, float y) { 
		setData(r, new Vector2(getDataVec(r)).add(x,y)); return this; }
	public pInstance addDataVec(String r, Vector2 v) { 
		addDataVec(r, v.x, v.y); return this; }
	
	public Vector2 getDataVec(String r) { return getData(r, Vector2.class); }
	public float getDataFlt(String r) { return getData(r, Float.class); }
	public pInstance addDataFlt(String r, float v) { setData(r, v+getData(r, Float.class)); return this; }
	public int getDataInt(String r) { return getData(r, Integer.class); }
	public pInstance addDataInt(String r, int v) { setData(r, v+getData(r, Integer.class)); return this; }
	public boolean getDataBoo(String r) { return getData(r, Boolean.class); }
	public String getDataStr(String r) { return getData(r, String.class); }

	public int getCollecSize(String r) {
		if (stand == null) return 0;
		int col_id = stand.getCollecValId(r);
		if (col_id == -1) return -1;
		return sheet.collec_pool.get(collecs[col_id]).size();
	}

	public void collecEmpty(String r) {
		if (stand == null) return;
		pColl c = getCollec(r);
		if (c != null) c.empty();
	}
	
	public pInstance collecSetVec(String r, int i, float x, float y) {
		return collecSet(r, i, new Vector2(x,y)); }
	
	public pInstance collecSet(String r, int i, Object o) { 
		if (stand == null) return null;
		pColl col = getCollec(r);
		if (col == null) return this;
		if (!Utl.type_is_used(o.getClass())) return null;
		String s = Utl.to_string(o);
		col.set(i,s); 
		return this; }

	public <T> T collecGet(String r, int i, Class<T> ct) {
		if (!Utl.type_is_used(ct)) return null;
		pColl col = getCollec(r);
		if (col == null) return null;
		String dt = stand.getCollecData(r);
		if (dt == null || !dt.equals(ct.getName())) return null;
		return Utl.from_string(col.get(i), ct);
	}
	
	public pInstance collecRemove(String r, Object o) { 
		if (stand == null) return null;
		pColl col = getCollec(r);
		if (col == null) return this;
		if (!Utl.type_is_used(o.getClass())) return null;
		String s = Utl.to_string(o);
		col.remove(s); 
		return this; }
	public pInstance collecAdd(String r, Object o) { 
		if (stand == null) return null;
		pColl col = getCollec(r);
		if (col == null) return this;
		if (!Utl.type_is_used(o.getClass())) return null;
		String s = Utl.to_string(o);
		col.add(s); 
		return this; }
	
	public <T> ArrayList<T> collecAll(String r, Class<T> ct) {
		ArrayList<T> arr = new ArrayList<T>();
		if (stand == null) return arr;
		if (!Utl.type_is_used(ct)) return null;
		pColl col = getCollec(r);
		if (col == null) return null;
		String dt = stand.getCollecData(r);
		if (dt == null || !dt.equals(ct.getName())) return null;
		for (String s : col.get()) arr.add(Utl.from_string(s, ct)); 
		return arr;
	}
	
	

	public int collecInstSize(String r) { 
		if (stand == null) return 0;
		pColl col = getCollec(r);
		if (col == null) return 0;
		for (pInstance b : collecInstAll(r)) b.unpointed_by(r, true, this);
		return col.size(); }
	public pInstance collecInstClear(String r) { 
		if (stand == null) return this;
		pColl col = getCollec(r);
		if (col == null) return this;
		for (pInstance b : collecInstAll(r)) b.unpointed_by(r, true, this);
		col.empty(); 
		return this; }
	public pInstance collecInstRemove(String r, pInstance o) { 
		if (stand == null) return this;
		pColl col = getCollec(r);
		if (col == null) return this;
		o.unpointed_by(r, true, this);
		col.remove(o.pool_ref); 
		return this; }
	public pInstance collecInstAdd(String r, pInstance o) { 
		if (stand == null) return this;
		pColl col = getCollec(r);
		if (col == null) return this;
		o.pointed_by(r, true, this);
		col.add(o.pool_ref); 
		return this; }
	public pInstance collecInstGet(String r, int i) { 
		if (stand == null) return null;
		pColl col = getCollec(r);
		if (col == null) return null; 
		return sheet.pool_map.get(stand.collec_inst_pools.get(r)).get(col.get(i)); 
	}
	public boolean collecInstContains(String r, pInstance o) { 
		if (stand == null) return false;
		pColl col = getCollec(r);
		if (col == null) return false;
		return col.contains(o.pool_ref); }
	
	public ArrayList<pInstance> collecInstAll(String r) {
		ArrayList<pInstance> arr = new ArrayList<pInstance>();
		if (stand == null) return arr;
		pColl col = getCollec(r);
		if (col == null) return arr;
		if (stand.collec_inst_pools.get(r) == null || 
				sheet.pool_map.get(stand.collec_inst_pools.get(r)) == null) return arr;
		for (String s : col.get()) {
			pInstance p = sheet.pool_map.get(stand.collec_inst_pools.get(r)).get(s);
			if (p != null) { arr.add(p); } }
		return arr;
	}
	
	
	
	
	
	
	public pColl getCollec(String r) {
		if (stand == null) return null;
		int col_id = stand.getCollecValId(r);
		if (col_id == -1) return null;
		return sheet.collec_pool.get(collecs[col_id]);
	}


	

	public pInstance setInst(String r, String b) { 
		pInstance bod = sheet.inst_pool.get(b);
//		if (bod == null) return this;
		setInst(r,bod); return this; }
	public pInstance setInst(String r, pInstance b) { 
		if (stand == null) return this;
		int bod_id = stand.getInstValId(r);
		if (bod_id == -1) return this;
		if (getInst(r) != null) getInst(r).unpointed_by(r, false, this);
		insts[bod_id] = ""; if (b == null) return this;
		insts[bod_id] = b.pool_ref; b.pointed_by(r, false, this);
		return this; 
	}
	public pInstance getInst(String r) {
		if (stand == null) return null;
		int bod_id = stand.getInstValId(r);
		if (bod_id == -1) return null;
		if (stand.inst_pools.get(r) == null || 
				sheet.pool_map.get(stand.inst_pools.get(r)) == null) return null;
		return sheet.pool_map.get(stand.inst_pools.get(r)).get(insts[bod_id]);
	}


	

	
	
	
	
	
	public nMap<Object> objects = new nMap<Object>();
	
	public pInstance addObject(String ref, Object r) {
		if (r != null) objects.put(ref, r); return this; }

	public pInstance removeObject(String ref, Object r) {
		if (r != null) objects.remove(ref, r); return this; }

	public pInstance removeObject(String ref) { 
		if (hasObject(ref)) removeObject(ref, object(ref)); return this; }
	
	public pInstance setObject(String ref, Object r) {
		Object old = object(ref); if (old != null) removeObject(ref, old);
		addObject(ref, r); return this; }
	
	public boolean hasObject(String ref) { return objects.get(ref) != null; }
	public boolean hasObject(String ref, Class<?> cl) { 
		return objects.get(ref) != null && objects.get(ref).getClass() == cl; }
	
	public Object object(String ref) { return objects.get(ref); }
	
	public <T> T object(String ref, Class<T> cl) { 
		Object o = objects.get(ref);
		if (o != null && cl.isAssignableFrom(o.getClass())) 
			return (T)o; else return null; }
	
	
	
	

	public void run(String ref, Object ... v) {
		if (stand == null) return;
		pStandard.RunDef rd = stand.getRunDef(ref);
		if (rd == null) {
			Applet.loggn("ERROR : pInstance.run : runDef <"+ref+"> dont exist"
					+ " instance "+pool_ref+" stand "+stand.ref);
			return; }
//		if (!rd.test_args(v)) return;
//		if (v != null) Utl.app.log("inst.run() : v length = "+v.length);
		rd.run.do_run(this,rd.param,v); }
	
	public void run(String ref, pPar par, Object ... v) {
		if (stand == null) return;
		pStandard.RunDef rd = stand.getRunDef(ref);
		if (rd == null) {
			Applet.loggn("ERROR : pInstance.run : runDef <"+ref+"> dont exist"
					+ " instance "+pool_ref+" stand "+stand.ref);
			return; }
//		if (!rd.test_args(v)) return;
		rd.run.do_run(this,rd.param.mix(par),v); }
	
	public Object get(String ref, Object ... v) {
		if (stand == null) return null;
		pStandard.RunDef rd = stand.getRunDef(ref);
		if (rd == null) {
			Applet.loggn("ERROR : pInstance.get : runDef <"+ref+"> dont exist"
					+ " instance "+pool_ref+" stand "+stand.ref);
			return null; }
//		if (!rd.test_args(v)) return null;
		return rd.run.do_get(this,rd.param,v); }
	
	public <T> T get(String ref, Class<T> cl, Object ... v) {
		if (stand == null) return null;
		pStandard.RunDef rd = stand.getRunDef(ref);
		if (rd == null) {
			Applet.loggn("ERROR : pInstance.get : runDef <"+ref+"> dont exist"
					+ " instance "+pool_ref+" stand "+stand.ref);
			return null; }
//		if (cl != rd.return_class) return null;
//		if (!rd.test_args(v)) return null;
		return rd.run.do_get(this,rd.param,cl,v); }
	
	public pInstance get_this(String ref, Object ... v) {
		if (stand == null) return null;
		pStandard.RunDef rd = stand.getRunDef(ref);
		if (rd == null) {
			Applet.loggn("ERROR : pInstance.get_this : runDef <"+ref+"> dont exist"
					+ " instance "+pool_ref+" stand "+stand.ref);
			return null; }
//		if (cl != rd.return_class) return null;
//		if (!rd.test_args(v)) return null;
		rd.run.do_get(this,rd.param,v);
		return this; }
	
	
	
}
