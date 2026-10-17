package gui;

import java.util.ArrayList;

import data.sTab;


public class nInterfModel {
	public String ref;
	public ArrayList<nInterfCommand> build_commands  = new ArrayList<nInterfCommand>();
	
	public nInterfModel(String r) { ref = r; }
	public nInterfModel(String r, ArrayList<nInterfCommand> coms) {
		ref = r;
		for (nInterfCommand c : coms) {
			nInterfCommand n = new nInterfCommand(c);
			build_commands.add(n); }
	}
	
	public void save_to(sTab tab) {
		tab.empty();
		tab.setWidth(build_commands.size());
//		tab.resize(build_commands.size(), nInterfCommand.MAX_ARGS + 1);
		for (int i = 0 ; i < build_commands.size() ; i++) {
			nInterfCommand c = build_commands.get(i);
			tab.setRowHeight(i, nInterfCommand.MAX_ARGS + 1); 
			tab.set(i, 0, nInterface.codeToStr(c.code)); 
			if (c.args != null) for (int j = 0 ; j < c.args.length ; j++) 
				tab.set(i, j+1, c.args[j]);
		}
	}
	public void load_from(sTab tab) {
		build_commands.clear();
		for (int i = 0 ; i < tab.width() ; i++) {
			nInterface.Code c = nInterface.strToCode(tab.getStr(i, 0));
			nInterfCommand n = new nInterfCommand(c, nInterfCommand.MAX_ARGS);
			for (int j = 0 ; j < nInterfCommand.MAX_ARGS ; j++) {
				n.args[j] = tab.getStr(i, j + 1);
			}
			build_commands.add(n);
		}
	}
}
