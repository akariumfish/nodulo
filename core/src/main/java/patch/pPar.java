package patch;

import java.util.Map;

import app.Applet;
import app.nMap;

public class pPar {

	public nMap<Object> params = new nMap<Object>();
	public pPar() {}
	public pPar(pPar p) { if (p == null) return;
		for (Map.Entry<String,Object> me : p.params.entrySet()) {
			params.put(me.getKey(),Applet.copy(me.getValue())); } }
	public pPar set(String k, Object v) { remove(k); params.put(k, Applet.copy(v)); return this; }
	public pPar set(pPar p) { 
		if (p == null) return this;
		params.clear(); for (Map.Entry<String,Object> me : p.params.entrySet()) {
			set(me.getKey(), me.getValue()); } return this; }
	public pPar remove(String k) {
		if (params.hasKey(k)) { params.remove(k, params.get(k)); } return this; }
	public void clear() { params.clear(); }
	public boolean has(String k) { return params.hasKey(k); }
	public Object get(String k) { return params.get(k); }
	public <T> T get(String k, Class<T> cl) { 
		if (has(k)
//				&& params.get(k).getClass() == cl
				) return (T)params.get(k); else return null; }
	public pPar mix(pPar p) {
		pPar par = new pPar(this);
		if (p != null) for (Map.Entry<String,Object> me : p.params.entrySet()) {
			par.set(me.getKey(), me.getValue()); }
		return par;
	}
}
