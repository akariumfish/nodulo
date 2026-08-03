package data;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

import util.Utl;

public class sFile {
	
	public sData data;
	public String path = "";
	public boolean isOpen = false;

	public File_Bloc file_bloc;

	public File_Bloc getBloc() { return file_bloc; }
	
	public sFile(sData d) {
		data = d;
		file_bloc = data.newFileBloc("sfile_filebloc"); 
	}
	public void open(String p) { open(p, false); }
	public void open(String p, boolean add_absent_file) {
//		data.app.log("open "+p);
//		if (isOpen) { close(); }
		if (add_absent_file && !Utl.file_exist(p)) {
			FileHandle fl = Gdx.files.local(p);
			if (!fl.exists()) fl.writeString(" ", false);
		}
		if (Utl.file_exist(p)) {
			path = p;
			file_bloc.empty();
//			file_bloc.load_from(path);
			isOpen = true;
		}
	}
	public void save() {
		if (isOpen) {
			file_bloc.save_to(path, true);
		}
	}
	public void load() {
		if (isOpen) {
			file_bloc.empty();
			file_bloc.load_from(path);
		}
	}
	public void empty() {
		if (file_bloc != null) file_bloc.clear();
		file_bloc = data.newFileBloc("sfile_filebloc"); 
	}
	public void close() {
		empty();
		path = "";
		isOpen = false;
	}
}
