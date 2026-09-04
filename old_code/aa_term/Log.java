package aa_term;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;

import util.Utl;

import java.io.IOException;
import java.io.Writer;

public class Log {
	private Array<LogEntry> logEntries;
	private int numEntries = Console.UNLIMITED_ENTRIES;

	protected Log () {
		logEntries = new Array<LogEntry>();
	}

	public void clear () {
		logEntries.clear();
	}

	public void setMaxEntries (int numEntries) {
		this.numEntries = numEntries;
	}

	protected void addEntry (String msg, LogLevel level) {
		logEntries.add(new LogEntry(msg, level));
		if (logEntries.size > numEntries && numEntries != Console.UNLIMITED_ENTRIES) {
			logEntries.removeIndex(0);
		}
	}

	protected Array<LogEntry> getLogEntries () {
		return logEntries;
	}

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

		String toWrite = "";
		for (LogEntry l : logEntries) {
			toWrite += l.toString() + "\n";
		}

		try {
			out.write(toWrite);
			out.close();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	public void printLog () {
		for (LogEntry l : logEntries) {
			Utl.printn(l.toString());
		}
	}
	
}
