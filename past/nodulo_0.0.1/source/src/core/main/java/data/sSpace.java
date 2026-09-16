package data;

import java.util.ArrayList;
import java.util.Map.Entry;

import util.Utl;
import util.nRun;


public class sSpace {
	public sData data;
	public sValueBloc root;
	public String ref;
	
	public Use use;
	
	public ArrayList<sBloc_Builder> root_bloc_builders =  new ArrayList<sBloc_Builder>();
	
	public enum Use { 	SETTING,  // never delete, load value from file
						WORK, // delete all inside at loading, rebuild from file
						DATABASE // child are databases roots and kept, theire content are reloaded
					};
	
	public sSpace(sData d, String r, Use u) {
		data = d; ref = r; use = u;
		root = data.newBloc(r);
	}
	
	public void addRootBlocBuilder(sBloc_Builder b) {
		root_bloc_builders.add(b);
		root.addBlocBuilder(b); }
	public sValueBloc buildRootBloc(String builder, String ref) {
		return root.buildBloc(builder, ref); }

	public sValueBloc newRootBloc(String ref) {
		return root.newBloc(ref); }
	
	public void save_to(File_Bloc sb) {
		root.preset_to_save_bloc(sb); 
	}
	public void setup_from(File_Bloc sb) {
		if (use == Use.SETTING) {
			root.load_params_from_bloc(sb);
		} else if (use == Use.WORK) {
			root.empty();
			
			data.app.addDelayEvent(6, new nRun() { public void run() {
				for (sBloc_Builder b : root_bloc_builders) root.addBlocBuilder(b);
				root.load_from_bloc(sb);
			}});
		} else if (use == Use.DATABASE) {
			for (Entry<String, sValueBloc> mev : root.blocs.entrySet()) {
				sValueBloc base = mev.getValue();
				base.empty();
				File_Bloc base_save = sb.getBloc(base.ref);
				if (base_save != null) {
					base.load_from_bloc(base_save);
				}
			}
		}
	}
	
	

	public void space_save(String path, boolean auto_add_file) {
		if (!data.file.open(path, auto_add_file)) return; 
		data.file.empty();
		save_to(data.file.getBloc());
		data.file.save();
		data.file.close();
	}
	
	public void space_load(String path) {
		if (!data.file.open(path)) return;
		data.file.load();
		setup_from(data.file.getBloc());
	}

	
	
}
