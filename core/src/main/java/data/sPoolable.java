package data;

import data.sPool.State;
import util.nClearable;
import util.nPool.Poolable;

public abstract class sPoolable {
	public sPool.State state = State.FREE;
	public sPool<? extends sPoolable> pool = null;
	public int pool_index = 0;
	public String pool_ref = "";

	public boolean is_new = false;
	
	public <T extends sPoolable> void setup(sPool<T> p, int i) {
		is_clearing = false; pool = p; pool_index = i; 
		pool_ref = pool.name+"_"+i; state = State.FREE; is_new = false;
	}
	public abstract void clear_action();
	protected boolean is_clearing = false;
	public boolean is_clearing() { return is_clearing; }
	
	public void clear() {
		if (!is_clearing) {
			is_clearing = true;
			clear_action();
			pool.free(this);
			is_clearing = false;
			is_new = false;
		}
	}

	public void create_poolable() {
		is_clearing = false;
		is_new = true;
		state = State.CREATING;
	}
	public void load_poolable() {
		is_clearing = false;
		is_new = false;
		state = State.LOADING;
	}
	public void do_init() {
		state = State.INIT;
		init_run();
	}
	public void do_load() {
		state = State.LOAD;
		load_run();
		state = State.USED;
		is_new = false;
	}
	public void do_save() {
		save_run();
	}
	
	
	public void to_tab(sTab t, int i) {}
	public void from_tab(sTab t, int i) {}
	public void init_run() {}
	public void load_run() {}
	public void save_run() {}
	public int data_size() { return 0; }
	public sPoolable() {}
}
