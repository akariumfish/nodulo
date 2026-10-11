package util;

import java.util.Iterator;

import patch.pInstance;
import patch.pPar;

@SuppressWarnings("serial")
public class nRuns extends nSortedArray<nRun> { // implement Iterable<nRun>
	public nRuns() { super(); }
	
	public void do_run(Object ... v) { nRun.runList(this,v); }
	public void do_run(pInstance cont, Object ... v) { nRun.runList(this,cont,v); }
	public void do_run(pPar par, Object ... v) { nRun.runList(this,par,v); }
	public void do_run(pInstance cont, pPar par, Object ... v) { nRun.runList(this,cont,par,v); }
	public void run() { nRun.runEvents(this); }
	public void run(Object v) { nRun.runEvents(this,v); }
	public void run(Object v1, Object v2) { nRun.runEvents(this,v1,v2); }
	public void run(Object v1, Object v2, Object v3) { nRun.runEvents(this,v1,v2,v3); }

	public nRuns setContext(Object p) { for (nRun n : this) n.context = p; return this; }
	
	public nRuns setOrdered() { super.setOrdered(); return this; }
	public nRuns setPrioritized() { super.setPrioritized(); return this; }
	public nRuns setReversePrioritized() { super.setReversePrioritized(); return this; }
	public nRuns setAutoSorted() { super.setAutoSorted(); return this; }

	public boolean add(nRun n) { if (sorted) n.setSorted(); return super.add(n); }
	
	
	
	
//	@Override public Iterator<nRun> iterator() { return new RunIterator(); }
//		
//	class RunIterator implements Iterator<nRun> {
//		private int cnt = 0;
//		@Override public boolean hasNext() { return cnt < size(); }
//		@Override public nRun next() { cnt++; return get(cnt-1); }
//	}
//	
	
	
}
