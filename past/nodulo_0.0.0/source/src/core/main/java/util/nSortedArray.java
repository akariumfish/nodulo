package util;

import java.util.ArrayList;

import patch.pInstance;
import patch.pPar;

@SuppressWarnings("serial")
public class nSortedArray <T extends Utl.Ordered & Utl.Priorizable> extends ArrayList<T> {
	public nSortedArray() { super(); }
	
	protected boolean sorted = false, ordered = false, prioritized = false, 
			reverse_prioritized = false, auto_sorted = false;
	public nSortedArray<T> setOrdered() { sorted = true; ordered = true; dirty(); return this; }
	public nSortedArray<T> setPrioritized() { sorted = true; prioritized = true; dirty(); return this; }
	public nSortedArray<T> setReversePrioritized() { sorted = true; reverse_prioritized = true; dirty(); return this; }
	public nSortedArray<T> setAutoSorted() { sorted = true; auto_sorted = true; dirty(); return this; }

	public void update() { if (dirty) clean(); }

	public boolean add(T n) { boolean r = super.add(n); if (r) dirty(); return r; }
	public void add(int i, T n) { super.add(i,n); dirty(); }
	public boolean addOne(T n) { if (Utl.has(this, n)) return false;
		boolean r = super.add(n); if (r) dirty(); return r; }
	public boolean addOne(int i, T n) { if (Utl.has(this, n)) return false;
		super.add(i,n); dirty(); return true; }
	public boolean remove(T n) { boolean r = super.remove(n); if (r) dirty(); return r; }
	public void clear() { super.clear(); dirty(); }
	
	private boolean dirty = true;
	private boolean sorting = false;
	private void dirty() { if (sorting) return; dirty = true; if (auto_sorted) clean(); }
	private void clean() {
		if (dirty) {
			sorting = true;
			if (ordered && prioritized) Utl.orderPrioSort(this);
			else if (ordered && reverse_prioritized) Utl.orderRevPrioSort(this);
			else if (ordered) Utl.orderSort(this);
			else if (prioritized) Utl.prioSort(this);
			else if (reverse_prioritized) Utl.revprioSort(this); 
			sorting = false; }
		dirty = false;
	}

}
