package gui;


public class nDrawable {
	public Object builder = null;
	public Object[] args = null;
	public nDrawable() {}
	public nDrawable(Object o) { builder = o; }
	public nDrawable(Object ... o) { args = o; }
	public void drawing() {}
	public void drawing(Object o) {}
}

	
