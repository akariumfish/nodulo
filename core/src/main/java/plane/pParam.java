package plane;

import java.util.ArrayList;
import java.util.Map;

import com.badlogic.gdx.math.Vector2;
import app.Applet;
import app.nMap;

import data.sPoolable;
import data.sTab;
import data.sValueBloc;

public class pParam extends sPoolable {
	
//	public void signalChangeFull() {
//		if (prop != null && !prop.mode_nosync && !prop.mode_localval) {
//			space.signalChange(this); 
//		}
//	}

//	nMap<Object> data_changes = new nMap<Object>();
//	nMap<String> body_changes = new nMap<String>();
//	nMap<String> ref_changes = new nMap<String>();
	public void cancelChange() {
//		data_changes.clear();
//		body_changes.clear();
//		ref_changes.clear();
	}
	public void signalChangeData(String r, Object o) {
		if (prop != null && !prop.mode_nosync && !prop.mode_localval) {
//			data_changes.replace(r, o);
			space.signalChange(this); 
		}
	}
	public void signalChangeRef(String r, String o) {
		if (prop != null && !prop.mode_nosync && !prop.mode_localval) {
//			ref_changes.replace(r, o);
			space.signalChange(this); 
		}
	}
	public void signalChangeBody(String r, String o) {
		if (prop != null && !prop.mode_nosync && !prop.mode_localval) {
//			body_changes.replace(r, o);
			space.signalChange(this); 
		}
	}
	
	public static final int start_data_nb = 1;

	private float getFloat(Object[] c, int i) { return (float)c[i]; }
	private int getInt(Object[] c, int i) { return (int)c[i]; }
	private boolean getBool(Object[] c, int i) { return (boolean)c[i]; }
	private String getStr(Object[] c, int i) { return (String)c[i]; }
	public void init_from_array(Object[] c) {
		
		empty();
		
		int cnt = start_data_nb;

		for (int i = 0 ; i < Applet.data_type_nb ; i++) {
			for (int j = 0 ; j < data_used[i] ; j++) {
				if (i == Applet.type_class_index.get(Vector2.class)) { 
					((Vector2)datas[i][j]).x = getFloat(c, cnt+(j*2));
					((Vector2)datas[i][j]).y = getFloat(c, cnt+(j*2)+1);
				} else if (i == Applet.type_class_index.get(Float.class)) { 
					datas[i][j] = getFloat(c, cnt+j);
				} else if (i == Applet.type_class_index.get(Integer.class)) { 
					datas[i][j] = getInt(c, cnt+j);
				} else if (i == Applet.type_class_index.get(Boolean.class)) { 
					datas[i][j] = getBool(c, cnt+j);
				} else if (i == Applet.type_class_index.get(String.class)) { 
					datas[i][j] = getStr(c, cnt+j);
				}
			}
			cnt += data_used[i] * Applet.type_data_size.get(Applet.data_type[i]);
		}
		
		for (int j = 0 ; j < collec_used ; j++) {
			collecs[j] = getStr(c, cnt+j);
		}
		obtain_collecs();
		cnt += collec_used;
		
		for (int j = 0 ; j < ref_used ; j++) {
			refs[j] = getStr(c, cnt+j);
		}
		cnt += ref_used;

		for (int j = 0 ; j < body_used ; j++) {
			bodys[j] = getStr(c, cnt+j);
		}
		cnt += body_used;
		
	}
	public void load_from_array(Object[] c) {
		
		int cnt = start_data_nb;

		for (int i = 0 ; i < Applet.data_type_nb ; i++) {
			for (int j = 0 ; j < data_used[i] ; j++) {
				if (i == Applet.type_class_index.get(Vector2.class)) { 
					((Vector2)datas[i][j]).x = getFloat(c, cnt+(j*2));
					((Vector2)datas[i][j]).y = getFloat(c, cnt+(j*2)+1);
				} else if (i == Applet.type_class_index.get(Float.class)) { 
					datas[i][j] = getFloat(c, cnt+j);
				} else if (i == Applet.type_class_index.get(Integer.class)) { 
					datas[i][j] = getInt(c, cnt+j);
				} else if (i == Applet.type_class_index.get(Boolean.class)) { 
					datas[i][j] = getBool(c, cnt+j);
				} else if (i == Applet.type_class_index.get(String.class)) { 
					datas[i][j] = getStr(c, cnt+j);
				}
			}
			cnt += data_used[i] * Applet.type_data_size.get(Applet.data_type[i]);
		}
		
		for (int j = 0 ; j < collec_used ; j++) {
			collecs[j] = getStr(c, cnt+j);
		}
//		obtain_collecs();
		cnt += collec_used;
		
		for (int j = 0 ; j < ref_used ; j++) {
			refs[j] = getStr(c, cnt+j);
		}
		cnt += ref_used;

		for (int j = 0 ; j < body_used ; j++) {
			bodys[j] = getStr(c, cnt+j);
		}
		cnt += body_used;
		
		
//		signalChange();
		
	}
	private void setArr(Object[] c, int i, Object data) { c[i] = Applet.copy(data); }
	public void to_array(Object[] c) {
		setArr(c, 0, true);
		
		int cnt = start_data_nb;

		for (int i = 0 ; i < Applet.data_type_nb ; i++) {
			for (int j = 0 ; j < data_used[i] ; j++) {
				if (i == Applet.type_class_index.get(Vector2.class)) { 
					Vector2 v = (Vector2)datas[i][j];
					setArr(c, cnt+(j*2), v.x);
					setArr(c, cnt+(j*2)+1, v.y);
				} else if (i == Applet.type_class_index.get(Float.class)) { 
					float v = (float)datas[i][j];
					setArr(c, cnt+j, v);
				} else if (i == Applet.type_class_index.get(Integer.class)) { 
					int v = (int)datas[i][j];
					setArr(c, cnt+j, v);
				} else if (i == Applet.type_class_index.get(Boolean.class)) { 
					boolean v = (boolean)datas[i][j];
					setArr(c, cnt+j, v);
				} else if (i == Applet.type_class_index.get(String.class)) { 
					String v = (String)datas[i][j];
					setArr(c, cnt+j, v);
				}
			}
			cnt += data_used[i] * Applet.type_data_size.get(Applet.data_type[i]);
		}
		
		for (int j = 0 ; j < collec_used ; j++) {
			setArr(c, cnt+j, collecs[j]);
		}
		cnt += collec_used;

		for (int j = 0 ; j < ref_used ; j++) {
			setArr(c, cnt+j, refs[j]);
		}
		cnt += ref_used;

		for (int j = 0 ; j < body_used ; j++) {
			setArr(c, cnt+j, bodys[j]);
		}
		cnt += body_used;
		
	}
	public void from_tab(sTab t, int c) {
		
		Object[] arr = new Object[data_size()];
		for (int i = 0 ; i < data_size() ; i++) { arr[i] = t.getObj(c,i); }
		init_from_array(arr);
		
	}
	public void to_tab(sTab t, int c) {
		
		Object[] arr = new Object[data_size()];
		to_array(arr);
		for (int i = 0 ; i < data_size() ; i++) { t.set(c, i, arr[i]); }
		
	}
	public int data_size() {
		int cnt = start_data_nb;
		for (int i = 0 ; i < Applet.data_type_nb ; i++) 
			cnt += data_used[i] * Applet.type_data_size.get(Applet.data_type[i]);
		cnt += collec_used;
		cnt += ref_used;
		cnt += body_used;
		return cnt;
	}
	

	public pParam addUser(pBody b) { 
		if (!users.contains(b)) users.add(b); 
		return this; }
	public pParam removeUser(pBody b) { users.remove(b); 
		if (space != null && !space.client_space && 
				!prop.mode_common && users.size() == 0) clear(); 
		return this; }
	
	
	
	
	
	
	public pCollec obtainCollec(int i, String r) {
		if (space.collec_pool.get(r) != null) {
			pCollec p = space.collec_pool.get(r);
			if (!collec_list.contains(p)) collec_list.add(p);
			return p; 
		} else {
			String col_ref = prop.getCollecRefFromId(i);
			pCollec p = space.new_collec(prop, col_ref);
			if (p != null) {
				if (!collec_list.contains(p)) collec_list.add(p);
			} else {
				space.app.logn("ERROR: pParam could not obtain a collec");
			}
			return p;
		}
	}
	
	public void obtain_collecs() {
		for (int i = 0 ; i < collec_used ; i++) {
			pCollec c = obtainCollec(i, collecs[i]);
			collecs[i] = c.pool_ref; 
		}
	}
	
	
	
	

	public pSpace space;
	public pProperty prop;
	
	int[] data_used;
	public Object[][] datas;
	
	int collec_used;
	public String[] collecs;
	
	int ref_used;
	public String[] refs;

	int body_used;
	public String[] bodys;
	
	public ArrayList<pBody> users = new ArrayList<pBody>();

	public ArrayList<pCollec> collec_list = new ArrayList<pCollec>();
	
	public pParam() { 
		data_used = new int[Applet.data_type_nb]; }
	
	public pParam init(pSpace s, pProperty p) { 
		prop = p; space = s; 
		
		users.clear();
		
		for (int i = 0 ; i < Applet.data_type_nb ; i++) {
			data_used[i] = prop.getDataUsed(Applet.data_type[i]); }
		
		collec_used = prop.getCollecUsed();
		collecs = new String[collec_used];
		for (int i = 0 ; i < collec_used ; i++) collecs[i] = ""; 
//		obtain_collecs();

		ref_used = prop.getRefUsed();
		refs = new String[ref_used];
		for (int i = 0 ; i < ref_used ; i++) refs[i] = "";

		body_used = prop.getBodyUsed();
		bodys = new String[body_used];
		for (int i = 0 ; i < body_used ; i++) bodys[i] = "";
		
		datas = new Object[Applet.data_type_nb][];
		
		int u = Applet.type_class_index.get(Vector2.class);
		datas[u] = new Vector2[data_used[u]];
		for (int i = 0 ; i < data_used[u] ; i++) datas[u][i] = new Vector2();
		
		u = Applet.type_class_index.get(Float.class);
		datas[u] = new Float[data_used[u]];
		for (int i = 0 ; i < data_used[u] ; i++) datas[u][i] = 0f;
		
		u = Applet.type_class_index.get(Integer.class);
		datas[u] = new Integer[data_used[u]];
		for (int i = 0 ; i < data_used[u] ; i++) datas[u][i] = (int)0;
		
		u = Applet.type_class_index.get(Boolean.class);
		datas[u] = new Boolean[data_used[u]];
		for (int i = 0 ; i < data_used[u] ; i++) datas[u][i] = false;
		
		u = Applet.type_class_index.get(String.class);
		datas[u] = new String[data_used[u]];
		for (int i = 0 ; i < data_used[u] ; i++) datas[u][i] = "";
		
//		setAllDef();
		
		return this;
	}
	
	public pParam obtain() {
		obtain_collecs();
		setAllDef();
		
		return this;
	}
	
	public void empty() {
		
//		for (pCollec c : collec_list) c.empty();
		
		setAllDef();
		
	}
	
	private ArrayList<pBody> tmp = new ArrayList<pBody>();
	public void clear_action() {
		for (pCollec c : collec_list) c.clear();
		
		tmp.clear();
		for (pBody b : users) tmp.add(b);
		for (pBody b : tmp) b.removeParam(this);
		tmp.clear();
		users.clear();
		
		empty();
		cancelChange();

		if (space != null && !Applet.has(space.delParams, this)) space.delParams.add(this);
	}
	
	public void setAllDef() {
		for (int i = 0 ; i < Applet.data_type_nb ; i++) {
			if (prop != null && prop.data_vals.get(Applet.data_type[i]) != null)
				for (Map.Entry<String, Integer> me : 
						prop.data_vals.get(Applet.data_type[i]).entrySet()) {
					Object o = getDef(me.getKey(), Applet.data_type[i]);
					if (o != null) set(me.getKey(), space.app.copy(o));
				}
		}
		
		for (pCollec c : collec_list) c.empty();
		
		for (String r : prop.ref_vals.allKey()) setRef(r, "");
		for (String r : prop.body_vals.allKey()) setBody(r, "");
		
//		for (int i = 0 ; i < ref_used ; i++) refs[i] = ""; 
//		for (int i = 0 ; i < body_used ; i++) bodys[i] = "";
//
//		signalChangeFull();
	}
	public <T> T getDef(String r, Class<T> ct) { 
		Object o = prop.getDataValDef(r, ct);
		if (o != null) return (T)o;
		return null;
	}
	

	public int getCollecSize(String r) {
		if (prop == null) return 0;
		int col_id = prop.getCollecValId(r);
		if (col_id == -1) return -1;
		return space.collec_pool.get(collecs[col_id]).size();
	}

	public void collecEmpty(String r) {
		if (prop == null) return;
		pCollec c = getCollec(r);
		if (c != null) c.empty();
	}
	
	public pParam collecSetVec(String r, int i, float x, float y) {
		return collecSet(r, i, new Vector2(x,y)); }
	
	public pParam collecSet(String r, int i, Object o) { 
		if (prop == null) return null;
		pCollec col = getCollec(r);
		if (col == null) return this;
		if (!Applet.type_is_used(o.getClass())) return null;
		String s = Applet.to_string(o);
		col.set(i,s); 
		return this; }

	public <T> T collecGet(String r, int i, Class<T> ct) {
		if (!Applet.type_is_used(ct)) return null;
		pCollec col = getCollec(r);
		if (col == null) return null;
		String dt = prop.getCollecData(r);
		if (dt == null || !dt.equals(ct.getName())) return null;
		return Applet.from_string(col.get(i), ct);
	}
	
	public pParam collecRemove(String r, Object o) { 
		if (prop == null) return null;
		pCollec col = getCollec(r);
		if (col == null) return this;
		if (!Applet.type_is_used(o.getClass())) return null;
		String s = Applet.to_string(o);
		col.remove(s); 
		return this; }
	public pParam collecAdd(String r, Object o) { 
		if (prop == null) return null;
		pCollec col = getCollec(r);
		if (col == null) return this;
		if (!Applet.type_is_used(o.getClass())) return null;
		String s = Applet.to_string(o);
		col.add(s); 
		return this; }
	
	public <T> ArrayList<T> getCollecData(String r, Class<T> ct) {
		ArrayList<T> arr = new ArrayList<T>();
		if (prop == null) return arr;
		if (!Applet.type_is_used(ct)) return null;
		pCollec col = getCollec(r);
		if (col == null) return null;
		String dt = prop.getCollecData(r);
		if (dt == null || !dt.equals(ct.getName())) return null;
		for (String s : col.get()) arr.add(Applet.from_string(s, ct)); 
		return arr;
	}
	
	
	

	public pParam collecBodyRemove(String r, pBody o) { 
		if (prop == null) return null;
		pCollec col = getCollec(r);
		if (col == null) return this;
		col.remove(o.pool_ref); 
		return this; }
	public pParam collecBodyAdd(String r, pBody o) { 
		if (prop == null) return null;
		pCollec col = getCollec(r);
		if (col == null) return this;
		col.add(o.pool_ref); 
		return this; }
	
	public ArrayList<pBody> getCollecBody(String r) {
		ArrayList<pBody> arr = new ArrayList<pBody>();
		if (prop == null) return arr;
		
		pCollec col = getCollec(r);
		if (col == null) return null;
		
		for (String s : col.get()) {
			pBody p = space.body_pool.get(s);
			if (p != null) {
				arr.add(p); 
			}
		}
		return arr;
	}
	
	
	

	public pParam collecRefEmpty(String r) { 
		if (prop == null) return null;
		pCollec col = getCollec(r);
		if (col == null) return this;
		col.empty(); 
		return this; }
	public pParam collecRefRemove(String r, pParam o) { 
		if (prop == null) return null;
		pCollec col = getCollec(r);
		if (col == null) return this;
		col.remove(o.pool_ref); 
		return this; }
	public pParam collecRefAdd(String r, pParam o) { 
		if (prop == null) return null;
		pCollec col = getCollec(r);
		if (col == null) return this;
		col.add(o.pool_ref); 
		return this; }
	
	public ArrayList<pParam> getCollecRef(String r) {
		ArrayList<pParam> arr = new ArrayList<pParam>();
		if (prop == null) return arr;
		
		pCollec col = getCollec(r);
		if (col == null) return null;
		
		pProperty pr = prop.getCollecProp(r);
		if (pr == null) return null;
		
		for (String s : col.get()) {
			pParam p = space.param_pools.get(pr.ref).get(s);
			if (p != null) {
				arr.add(p); 
			}
		}
		return arr;
	}
	
	
	public pCollec getCollec(String r) {
		int col_id = prop.getCollecValId(r);
		if (col_id == -1) return null;
		return space.collec_pool.get(collecs[col_id]);
	}


	

	public pParam setBody(String r, String b) { 
		pBody bod = space.body_pool.get(b);
		setBody(r,bod);
		return this; 
	}
	public pParam setBody(String r, pBody b) { 
		int bod_id = prop.getBodyValId(r);
		if (bod_id == -1) return this;
		if (b == null) {
			bodys[bod_id] = "";
			signalChangeBody(r, "");
			return this;
		}
		boolean change = !bodys[bod_id].equals(b.pool_ref);
		bodys[bod_id] = b.pool_ref;
		if (change) signalChangeBody(r, b.pool_ref);
		return this; 
	}
	public pBody getBody(String r) {
		if (prop == null) return null;
		int bod_id = prop.getBodyValId(r);
		if (bod_id == -1) return null;
		return space.body_pool.get(bodys[bod_id]);
	}


	

	public pParam setRef(String r, String p) { 
		pProperty pr = prop.getRefProp(r);
		if (pr == null) return this;
		setRef(r,space.param_pools.get(pr.ref).get(p));
		return this; 
	}
	public pParam setRef(String r, pParam p) { 
		int ref_id = prop.getRefValId(r);
		if (ref_id == -1 || ref_id >= refs.length) return this;
		if (p == null) { refs[ref_id] = ""; signalChangeRef(r, ""); return this; }
		if (p.prop != prop.getRefProp(r)) return this;
		boolean change = !refs[ref_id].equals(p.pool_ref);
		refs[ref_id] = p.pool_ref;
		if (change) signalChangeRef(r, p.pool_ref);
		return this; 
	}
	public pParam getRef(String r) {
		if (prop == null) return null;
		int ref_id = prop.getRefValId(r);
		if (ref_id == -1) return null;
		pProperty pr = prop.getRefProp(r);
		return space.param_pools.get(pr.ref).get(refs[ref_id]);
	}

	

	public boolean has(String r, Class<?> ct) { 
		int val_id = prop.getDataValId(r, ct);
		if (val_id == -1) return false;
		return true; }
	public pParam set(String r, Object o) {
		if (r == null || o == null) return this; 
		int val_id = prop.getDataValId(r, o.getClass());
		if (val_id == -1) return this;
		int data_id = Applet.type_class_index.get(o.getClass());
		boolean change = datas[data_id][val_id] != o;
		if (o != null && datas[data_id][val_id] != null && 
				(datas[data_id][val_id] instanceof String) && (o instanceof String)) 
			change = !((String)datas[data_id][val_id]).equals((String)o);
		else if (o != null && datas[data_id][val_id] != null && 
				(datas[data_id][val_id] instanceof Vector2) && (o instanceof Vector2)) 
			change = !((Vector2)datas[data_id][val_id]).equals((Vector2)o);
		datas[data_id][val_id] = o;
		if (change) signalChangeData(r, o);
		return this; 
	}
	public Object get(String r) { 
		Class<?> ct = prop.getDataValClass(r);
		int val_id = prop.getDataValId(r, ct);
		if (val_id == -1) return null;
		int data_id = Applet.type_class_index.get(ct);
		return Applet.copy(datas[data_id][val_id]); 
	}
	public <T> T get(String r, Class<T> ct) { 
		int val_id = prop.getDataValId(r, ct);
		if (val_id == -1) return null;
		int data_id = Applet.type_class_index.get(ct);
		return Applet.copy((T)datas[data_id][val_id]); 
	}
	public String getStr(String r) { return get(r, String.class); }
	public int getInt(String r) { return get(r, Integer.class); }
	public boolean getBoo(String r) { return get(r, Boolean.class); }
	public float getFlt(String r) { return get(r, Float.class); }
	public Vector2 getVec(String r) { return get(r, Vector2.class); }
	
	public pParam setVec(String r, Vector2 v) { 
		int val_id = prop.getDataValId(r, Vector2.class);
		if (val_id == -1) return this;
		int data_id = Applet.type_class_index.get(Vector2.class);
		boolean change = !((Vector2)datas[data_id][val_id]).equals(v);
		((Vector2)datas[data_id][val_id]).set(v.x,v.y);
		if (change) signalChangeData(r, Applet.copy(v));
		return this; 
	}
	public pParam setVec(String r, float x, float y) { 
		int val_id = prop.getDataValId(r, Vector2.class);
		if (val_id == -1) return this;
		int data_id = Applet.type_class_index.get(Vector2.class);
		boolean change = !((Vector2)datas[data_id][val_id]).equals(new Vector2(x,y));
		((Vector2)datas[data_id][val_id]).set(x,y);
		if (change) signalChangeData(r, new Vector2(x,y));
		return this; 
	}
	
}
