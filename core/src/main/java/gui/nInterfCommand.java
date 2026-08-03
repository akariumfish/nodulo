package gui;

import app.Applet;

import gui.nInterface.Code;

public class nInterfCommand {
	
	public static final int MAX_ARGS = 4;
	
	public Code code; public String[] args = null;
	
	nInterfCommand(nInterfCommand c) {
		code = c.code;
		if (c.args != null && c.args.length > 0) { 
			args = new String[c.args.length];
			for (int i = 0 ; i < c.args.length ; i++)
				args[i] = c.args[i];
		}
	}
	nInterfCommand(Code c) { code = c; }
	
	nInterfCommand(Code c, int arg_nb) {
		code = c; if (arg_nb > 0) args = new String[arg_nb]; }
	
	nInterfCommand(Code c, String arg) {
		code = c; args = new String[1];
		args[0] = arg; }
	nInterfCommand(Code c, String arg1, String arg2) {
		code = c; args = new String[2];
		args[0] = arg1; args[1] = arg2; }
	nInterfCommand(Code c, String arg1, String arg2, String arg3) {
		code = c; args = new String[3];
		args[0] = arg1; args[1] = arg2; args[2] = arg3; }
	nInterfCommand(Code c, String arg1, String arg2, String arg3, String arg4) {
		code = c; args = new String[4];
		args[0] = arg1; args[1] = arg2; args[2] = arg3; args[3] = arg4; }
	
	String arg(int i) { if (args != null && i < args.length) return args[i]; else return ""; }
	int argInt(int i) { if (args != null && i < args.length) return Applet.toint(args[i]); else return 0; }
	float argFlt(int i) { if (args != null && i < args.length) return Applet.tofloat(args[i]); else return 0; }
	boolean argBoo(int i) { if (args != null && i < args.length) return Applet.tobool(args[i]); else return false; }

}
