package net;

import data.sValueBloc;

public abstract class Object_Builder {
	public String type;
	public Object_Builder(String t) { type = t; }
	public abstract Object build(boolean isNew, sValueBloc bloc);
}
