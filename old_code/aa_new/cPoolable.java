package aa_new;

import aa_new.cPool.State;
import data.sTab;

public abstract class cPoolable { 
	public cPool.State state = State.FREE;
	public cPool<? extends cPoolable> pool = null;
	public int id = 0;

	public <T extends cPoolable> void setup(cPool<T> p, int i) {
		is_clearing = false; is_disposing = false; pool = p; id = i; 
		state = State.FREE; 
	}
	private boolean is_clearing = false;
	public boolean is_clearing() { return is_clearing; }
	
	public void clear() {
		if (!is_clearing) {
			is_clearing = true;
			clear_run();
			pool.free(this);
			is_clearing = false;
		}
	}

	private boolean is_disposing = false;
	
	public void dispose() {
		if (!is_disposing) {
			is_disposing = true;
			dispose_run();
		}
	}

	
	public void do_init() {
		is_clearing = false;
		init_run();
	}
	public void do_finish() {
		finish_run();
		state = State.USED;
	}
	
	
	public abstract void init_run();
	public abstract void finish_run();
	public abstract void clear_run();
	public abstract void dispose_run();
	
	public cPoolable() {}
}
