package util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

public class T {
	
	public static final String TEXT_FILE_PATH = "text.txt";

	private static FileHandle fl;
	private static String tmp = "";
	
	public static void build() {
		fl = Gdx.files.local(TEXT_FILE_PATH);
		if (!fl.exists()) { fl.writeString("void void\n", false); }
		String file = fl.readString();
		String[] line = file.split("[\\n]");
		for (String l : line) {
			String[] words = l.split("[\\s]");
			if (words.length == 0) continue;
			tmp = ""; for (int i = 1 ; i < words.length ; i++) {
				tmp += words[i]; if (i < words.length - 1) tmp += " "; }
			map.put(words[0], Utl.copy(tmp));
		}
	}
	
	private static final nMap<String> map = new nMap<String>();
	
	public static String t(String ref, String def) {
		if (!map.hasKey(ref)) {
			fl.writeString(ref+" "+def+"\n",true);
			map.put(ref,def);
			return def; }
		return map.get(ref);
	}
	

//	private static void replace(String...data) {
//		if (data == null) return; tmp = "";
//		for (String s : data) tmp += s+'\n';
//		fl.writeString(tmp,false);
//	}
//
//	private static void append(String...data) {
//		if (data == null) return; tmp = "";
//		for (String s : data) tmp += s+'\n';
//		fl.writeString(tmp,true);
//	}

}
