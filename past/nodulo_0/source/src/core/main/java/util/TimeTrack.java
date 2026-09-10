package util;

public class TimeTrack {
	
	public String ref;
	
	public TimeTrack(String r) {
		ref = r; 
		tps_stack = new long[tps_stack_size];
	    for (int i = 0 ; i < tps_stack_size ; i++) tps_stack[i] = 1;
	}
	
	public long time = 0, tps_med = 0;
	private long[] tps_stack;
	private int tps_stack_count = 0;
	private final int tps_stack_size = 60;
	
	public void start() {
		time = System.currentTimeMillis();
	}
	public void stop() {
		long dur = System.currentTimeMillis() - time;
		tps_stack[tps_stack_count] = dur;
		tps_stack_count++;
		if (tps_stack_count >= tps_stack_size) tps_stack_count = 0;
		
		tps_med = 0;
		for (int i = 0 ; i < tps_stack_size ; i++) tps_med += tps_stack[i];
		tps_med = tps_med / tps_stack_size;
	}
	
}
