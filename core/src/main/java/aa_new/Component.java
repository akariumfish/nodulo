package aa_new;

import java.util.ArrayList;

import data.sTab;
import util.Utl;

public class Component extends cPoolable {

	public final Property prop;
	
	public int id = 0;
	
	public final Object[][] datas;
	
	public Component(Property p) {
		prop = p; datas = prop.getDataArray(); 
	}
	
	
	
	@Override
	public void init_run() {
		prop.run_init(this);
	}

	@Override
	public void finish_run() {
		prop.run_finish(this);
	}

	@Override
	public void clear_run() {
		prop.run_clear(this);
	}

	@Override
	public void dispose_run() {
		
	}
	

	public String[] get_script() {
		
		return null;
	}

	public void build_script(String[] s) {
		
	}
	
	
	
	
	public void set(String r, Object o) { 
		prop.getEntry(r).set(datas, o); }
	
	public Object get(String r) { 
		return prop.getEntry(r).get(datas); }
	
	public <V> V get(String r, Class<V> ct) { 
		return (V)prop.getEntry(r).get(datas); }

	public void clear(String r) { 
		((ArrayList<?>)(prop.getEntry(r).get(datas))).clear(); }

	public <V> void add(String r, V o) { 
		((ArrayList<V>)(prop.getEntry(r).get(datas))).add(o); }

	public <V> void set(String r, int i, V o) {
		((ArrayList<V>)(prop.getEntry(r).get(datas))).set(i,o); }

	public <V> void remove(String r, V o) {
		((ArrayList<V>)(prop.getEntry(r).get(datas))).add(o); }

	public int size(String r) {
		return ((ArrayList<?>)(prop.getEntry(r).get(datas))).size(); }

	public Object get(String r, int i) {
		return ((ArrayList<?>)(prop.getEntry(r).get(datas))).get(i); }
	
	public <V> V get(String r, int i, Class<V> ct) { 
		return ((ArrayList<V>)(prop.getEntry(r).get(datas))).get(i); }

	public ArrayList<?> all(String r) { 
		return ((ArrayList<?>)(prop.getEntry(r).get(datas))); }
	
	public <V> ArrayList<V> all(String r, Class<V> ct) { 
		return ((ArrayList<V>)(prop.getEntry(r).get(datas))); }
	
	
	
	

	public void run(String ref, Object ... v) {
		if (prop == null) return;
		Property.RunDef rd = prop.getRunDef(ref);
		if (rd == null) {
			Utl.logn("ERROR : Component.run : runDef <"+ref+"> dont exist"
					+ " param "+id+" prop "+prop.ref);
			return; }
		rd.run.context = this;
		rd.run.do_run(v); }
	
	public Object get(String ref, Object ... v) {
		if (prop == null) return null;
		Property.RunDef rd = prop.getRunDef(ref);
		if (rd == null) {
			Utl.logn("ERROR : Component.get : runDef <"+ref+"> dont exist"
					+ " param "+id+" prop "+prop.ref);
			return null; }
		rd.run.context = this;
		return rd.run.do_get(v); }
	
	public <T> T get(String ref, Class<T> cl, Object ... v) {
		if (prop == null) return null;
		Property.RunDef rd = prop.getRunDef(ref);
		if (rd == null) {
			Utl.logn("ERROR : Component.get : runDef <"+ref+"> dont exist"
					+ " param "+id+" prop "+prop.ref);
			return null; }
		rd.run.context = this;
		return rd.run.do_get(cl,v); }
	
	public Component get_this(String ref, Object ... v) {
		if (prop == null) return null;
		Property.RunDef rd = prop.getRunDef(ref);
		if (rd == null) {
			Utl.logn("ERROR : Component.get_this : runDef <"+ref+"> dont exist"
					+ " param "+id+" prop "+prop.ref);
			return null; }
		rd.run.context = this;
		rd.run.do_get(v);
		return this; }





}
