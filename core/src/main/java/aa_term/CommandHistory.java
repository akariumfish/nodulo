package aa_term;

import java.io.IOException;
import java.io.Writer;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;

import util.Utl;

public class CommandHistory {
	private final Array<String> commands = new Array<String>(true, 20);
	private int index;
	
	public int getSize() { return commands.size; }

	public boolean printToFile (FileHandle fh) {
		if (fh.isDirectory()) {
			throw new IllegalArgumentException("File cannot be a directory!");
		}

		Writer out = null;
		try {
			out = fh.writer(false);
		} catch (Exception e) {
			return false;
		}

		String toWrite = "console\n";
		for (int i = commands.size - 1 ; i >= 0 ; i--) {
			toWrite += ".exec(\""+commands.get(i)+ "\")\n";
		}
		toWrite += ";\n";
		
		try {
			out.write(toWrite);
			out.close();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	public void print (boolean code) {
		if (code) {
			Utl.printn("console");
			for (int i = commands.size - 1 ; i >= 0 ; i--) 
				Utl.printn(".exec(\""+commands.get(i)+"\")");
			Utl.printn(";");
		} else {
			for (int i = commands.size - 1 ; i >= 0 ; i--) 
				Utl.printn(commands.get(i));
		}
	}

	public String[] get (boolean code) {
		if (code) {
			String[] ret = new String[commands.size + 2];
			int c = 0;
			ret[c++] = "console";
			for (int i = commands.size - 1 ; i >= 0 ; i--) 
				ret[c++] = ".exec(\""+commands.get(i)+"\")";
			ret[c++] = ";";
			return ret;
		} else {
			String[] ret = new String[commands.size];
			int c = 0;
			for (int i = commands.size - 1 ; i >= 0 ; i--) 
				ret[c++] = commands.get(i);
			return ret;
		}
	}

	public void read (String code) {
		String[] lines = code.split("\n");
		for (String s : lines) store(s);
	}

	public void reset () {
		commands.clear();
		indexAtBeginning();
	}

	public void append (CommandHistory ch) {
		for (String s : ch.commands) commands.insert(0, s);
		indexAtBeginning();
	}

	public void copy (CommandHistory ch) {
		commands.clear();
		for (String s : ch.commands) commands.insert(0, s);
		indexAtBeginning();
	}
	
	public void store (String command) {
		if (commands.size > 0 && isLastCommand(command)) {
			return;
		}
		commands.insert(0, command);
		indexAtBeginning();
	}

	public String getPreviousCommand () {
		index++;

		if (commands.size == 0) {
			indexAtBeginning();
			return "";
		} else if (index >= commands.size) {
			index = 0;
		}

		return commands.get(index);
	}

	public String getFirstCommand () {
		index = commands.size - 1;

		if (commands.size == 0) {
			indexAtBeginning();
			return "";
		} 

		return commands.get(index);
	}

	public String getNextCommand () {
		index--;
		if (commands.size <= 1 || index < 0) {
			indexAtBeginning();
			return "";
		}
		return commands.get(index);
	}

	private boolean isLastCommand (String command) {
		return command.equals(commands.first());
	}

	private void indexAtBeginning () {
		index = -1;
	}
	
	
}
