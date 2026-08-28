package aa_term;

import data.sBoo;
import data.sFlt;
import data.sInt;
import data.sStr;
import data.sValueBloc;
import data.sVec;

public class SystemExecutor extends CommandExecutor {

	private final sValueBloc bloc;
	public SystemExecutor(String r, sValueBloc b) {
		super(r);
		this.bloc = b;
	}

	@ConsoleDoc(description = "List all values") 
	public void allval() {
		for (String v : this.bloc.values.allKey()) 
			console.log(this.bloc.values.get(v).type+" "+v);
	}

	@ConsoleDoc(description = "set a Boolean sValue", 
			paramDescriptions = {"value ref","value"}) 
	public void setboo(String r, boolean v) {
		if (this.bloc.getValue(r,sBoo.class) != null) {
			this.bloc.getValue(r,sBoo.class).set(v);
			console.log(r+" = "+v, LogLevel.SUCCESS);
		} else {
			console.log("ERROR : cant find sBoo "+r+" in "+bloc.ref, LogLevel.ERROR);
		}
	}

	@ConsoleDoc(description = "set a Float sValue", 
			paramDescriptions = {"value ref","value"}) 
	public void setflt(String r, float v) {
		if (this.bloc.getValue(r,sFlt.class) != null) {
			this.bloc.getValue(r,sFlt.class).set(v);
			console.log(r+" = "+v, LogLevel.SUCCESS);
		} else {
			console.log("ERROR : cant find sFlt "+r+" in "+bloc.ref, LogLevel.ERROR);
		}
	}

	@ConsoleDoc(description = "set an Integer sValue", 
			paramDescriptions = {"value ref","value"}) 
	public void setint(String r, int v) {
		if (this.bloc.getValue(r,sInt.class) != null) {
			this.bloc.getValue(r,sInt.class).set(v);
			console.log(r+" = "+v, LogLevel.SUCCESS);
		} else {
			console.log("ERROR : cant find sInt "+r+" in "+bloc.ref, LogLevel.ERROR);
		}
	}

	@ConsoleDoc(description = "set a String sValue", 
			paramDescriptions = {"value ref","value"}) 
	public void setstr(String r, String v) {
		if (this.bloc.getValue(r,sStr.class) != null) {
			this.bloc.getValue(r,sStr.class).set(v);
			console.log(r+" = "+v, LogLevel.SUCCESS);
		} else {
			console.log("ERROR : cant find sStr "+r+" in "+bloc.ref, LogLevel.ERROR);
		}
	}

	@ConsoleDoc(description = "set a Vector2 sValue", 
			paramDescriptions = {"value ref","x","y"}) 
	public void setvec(String r, float x, float y) {
		if (this.bloc.getValue(r,sVec.class) != null) {
			this.bloc.getValue(r,sVec.class).set(x,y);
			console.log(r+" = "+x+","+y, LogLevel.SUCCESS);
		} else {
			console.log("ERROR : cant find sVec "+r+" in "+bloc.ref, LogLevel.ERROR);
		}
	}

}
