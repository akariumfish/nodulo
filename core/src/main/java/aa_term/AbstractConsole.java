package aa_term;

import java.util.ArrayList;
import java.util.Collections;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.reflect.Annotation;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.Method;
import com.badlogic.gdx.utils.reflect.ReflectionException;

import util.Utl;
import util.nMap;

/**
 * @author Eric
 */
public abstract class AbstractConsole implements Console, Disposable {
	protected final Log log;
	protected CommandExecutor exec;
	protected boolean logToSystem;

	protected boolean disabled;

	protected boolean executeHiddenCommands = true;
	protected boolean displayHiddenCommands = false;
	protected boolean consoleTrace = false, recordLog = true;

	private CommandHistory execHistory;
	
	private final nMap<CommandExecutor> system_execs = new nMap<CommandExecutor>();
	private final nMap<CommandHistory> historys = new nMap<CommandHistory>();
	
	public nMap<CommandExecutor> getSysMap() { return system_execs; }
	
	public void addExecutor(CommandExecutor ce) {
		if (system_execs.hasKey(ce.ref)) {
			Utl.logn("ERROR : AbstractConsole.addExecutor : the executor ref <"+ce.ref+"> is allready used.");
			return; } 
		system_execs.putOne(ce.ref,ce);
		ce.setConsole(this);
		if (exec == null) { exec = ce; }
	}
	
	public AbstractConsole () {
		log = new Log();
		execHistory = new CommandHistory();
	}

	@Override public void setRecordLog(boolean b) { recordLog = b; }
	@Override public boolean isLogRecorded() { return recordLog; }

	@Override public boolean getConsoleTrace() {
		return consoleTrace;
	}
	
	@Override public boolean isDisabled () {
		return disabled;
	}

	@Override public void setDisabled (boolean disabled) {
		this.disabled = disabled;
	}

	@Override public void setCommandExecutor (CommandExecutor commandExec) {
		exec = commandExec;
	}

	@Override public void setLoggingToSystem (Boolean log) {
		this.logToSystem = log;
	}

	@Override public void log (String msg, LogLevel level) {
		if (level == LogLevel.COMMAND)
			log.addEntry(exec.ref, msg, level);
		else if (isLogRecorded() || level == LogLevel.ERROR) 
			log.addEntry(msg, level);

		if (logToSystem) {
			switch (level) {
			case ERROR:
				System.err.println(exec.ref+"> " + msg);
				break;
			default:
				System.out.println(exec.ref+"> " + msg);
				break;
			}
		}
	}

	@Override public void log (String msg) {
		this.log(msg, LogLevel.DEFAULT);
	}

	@Override public void log (Throwable exception, LogLevel level) {
		this.log(ConsoleUtils.exceptionToString(exception), level);
	}

	@Override public void log (Throwable exception) {
		this.log(exception, LogLevel.ERROR);
	}

	@Override public void printLogToFile (String file) {
		this.printLogToFile(Gdx.files.local(file));
	}

	@Override public void printLogToFile (FileHandle fh) {
		if (log.printToFile(fh)) {
			log("Successfully wrote logs to file.", LogLevel.SUCCESS);
		} else {
			log("Unable to write logs to file.", LogLevel.ERROR);
		}
	}

	@Override public void printLog () {
		log.printLog();
	}

	@Override public void printToFile(String file) {
		execHistory.printToFile(Gdx.files.local(file));
	}
	@Override public void printCode(boolean code) {
		execHistory.print(code);
	}

	@Override public void store(boolean t) {
		storeCode = t;
	}

	@Override public void clearCode() {
		execHistory.reset();
	}

	@Override public void allStore() {
		for (String s : historys.allKey())
			log(s);
	}

	@Override public void storeCode(String ref) {
		if (historys.hasKey(ref)) {
			historys.remove(ref); }
		CommandHistory ch = new CommandHistory();
		ch.copy(execHistory);
		historys.put(ref,ch);
	}
	public CommandHistory newStoredCode(String ref) {
		if (historys.hasKey(ref)) {
			historys.remove(ref); }
		CommandHistory ch = new CommandHistory();
		historys.put(ref,ch);
		return ch;
	}

	@Override public void runCode(String ref) {
		if (historys.hasKey(ref)) {
//			execHistory.reset();
			String[] cd = historys.get(ref).get(false);
			for (String s : cd) exec(s);
		}
	}

	public int getMaxHistory() {
		return maxHistory;
	}
	public void setMaxHistory(int i) {
		maxHistory = i;
	}
	
	private int maxHistory = 2000;
	private boolean storeCode = true;
	public void storeCommand(String c) {
		if (!storeCode) return;
		if (execHistory.getSize() > getMaxHistory()) {
				log("ERROR : execution history is full, cant store command", LogLevel.ERROR);
			return; }
		execHistory.store(c);
		if (execHistory.getSize() > getMaxHistory()) {
			log("WARNING : execution history is full");
		}
	}
	
	@Override public Console exec (String command) {
		if (isDisabled()) return this;
		exec.execCommand(command); return this; }

	@Override public void execCommand (String command) {
		if (isDisabled()) return;
		exec.execCommand(command); }

	@Override public void printCommands () {
		exec.printCommands();
	}

	@Override public void printAllCommands () {
		exec.printAllCommands();
	}

	@Override public void printHelp (String command) {
		exec.printHelp(command);
	}

	@Override public void setExecuteHiddenCommands (boolean enabled) {
		executeHiddenCommands = enabled;
	}

	@Override public boolean isExecuteHiddenCommandsEnabled () {
		return executeHiddenCommands;
	}

	@Override public void setDisplayHiddenCommands (boolean enabled) {
		displayHiddenCommands = enabled;
	}

	@Override public boolean isDisplayHiddenCommandsEnabled () {
		return displayHiddenCommands;
	}

	@Override public void setConsoleStackTrace (boolean enabled) {
		this.consoleTrace = enabled;
	}

	@Override public void setMaxEntries (int numEntries) {
	}

	@Override public void clear () {
		clearCode();
	}

	@Override public void setSize (int width, int height) {
	}

	@Override public void setSizePercent (float wPct, float hPct) {
	}

	@Override public void setPosition (int x, int y) {
	}

	@Override public void setPositionPercent (float xPosPct, float yPosPct) {
	}

	@Override public void resetInputProcessing () {
	}

	@Override public InputProcessor getInputProcessor () {
		return null;
	}

	@Override public void draw () {
	}

	@Override public void refresh () {
	}

	@Override public void refresh (boolean retain) {
	}

	@Override public int getDisplayKeyID () {
		return 0;
	}

	@Override public void setDisplayKeyID (int code) {
	}

	@Override public boolean hitsConsole (float screenX, float screenY) {
		return false;
	}

	@Override public void dispose () {
		CommandExecutor.dispose();
	}

	@Override public boolean isVisible () {
		return false;
	}

	@Override public void setVisible (boolean visible) {
	}

	@Override public void select () {
	}

	@Override public void deselect () {
	}

	@Override public void setTitle (String title) {
	}

	@Override public void setHoverAlpha (float alpha) {
	}

	@Override public void setNoHoverAlpha (float alpha) {
	}

	@Override public void setHoverColor (Color color) {
	}

	@Override public void setNoHoverColor (Color color) {
	}

	@Override public void enableSubmitButton (boolean enable) {
	}

	@Override public void setSubmitText (String text) {
	}

	@Override public Window getWindow () {
		return null;
	}
}
