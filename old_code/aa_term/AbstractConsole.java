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

import data.sValueBloc;
import util.Utl;
import util.nMap;

/**
 * @author Eric
 */
public abstract class AbstractConsole implements Console, Disposable {
	protected final Log log;
	protected Executor exec;
	protected boolean logToSystem;

	protected boolean disabled;

	protected boolean executeHiddenCommands = true;
	protected boolean displayHiddenCommands = false;
	protected boolean consoleTrace = false, recordLog = true;

	private CommandHistory execHistory;
	private CommandHistory scriptStack;
	protected boolean scripting = false;
	
	public void register(String r, sValueBloc b, Object o) {
		exec.register(r,b);
		exec.register(o);
	}
	
	public AbstractConsole () {
		log = new Log();
		execHistory = new CommandHistory();
		scriptStack = new CommandHistory();
		exec = new Executor(this);
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

	@Override public void setCommandExecutor (Executor commandExec) {
		exec = commandExec;
	}

	@Override public void setLoggingToSystem (Boolean log) {
		this.logToSystem = log;
	}

	@Override public void log (String msg, LogLevel level) {
		if (isLogRecorded()) {
			log.addEntry(msg, level); }

		if (logToSystem) {
			switch (level) {
			case ERROR:
				System.err.println("> " + msg);
				break;
			default:
				System.out.println("> " + msg);
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

//	@Override public void store(boolean t) {
//		storeCode = t;
//	}

	@Override public void clearCode() {
		execHistory.reset(); log.clear(); refresh();
	}

	@Override public void allScript() {
		for (String s : scripts.allKey()) log(s);
	}
	

	@Override public boolean isScripting() { return scripting; }
	private String scripting_ref = "";
	private nMap<String[]> scripts = new nMap<String[]>();
	@Override public void beginScript(String ref) {
		if (scripting || scripts.hasKey(ref)) return;
		scripting = true;
		scripting_ref = Utl.copy(ref);
		scriptStack.reset();
		log.clear(); refresh();
		log("writing script "+scripting_ref,LogLevel.SUCCESS);
	}
	@Override public void endScript() {
		if (!scripting) return;
		scripting = false;
		scripts.put(scripting_ref,scriptStack.get(false));
		log.clear(); refresh();
		for (String s : execHistory.get(false)) 
			log(s,LogLevel.COMMAND);
		log("writing script "+scripting_ref,LogLevel.SUCCESS);
		for (String s : scriptStack.get(false)) 
			log(s,LogLevel.SUCCESS);
		log("saved script "+scripting_ref,LogLevel.SUCCESS);
		scriptStack.reset();
	}
	@Override public void runScript(String ref) {
		if (!scripts.hasKey(ref)) return;
		log("running script "+ref,LogLevel.SUCCESS);
		exec.executeScript(scripts.get(ref));
//		for (String s : scripts.get(ref)) submitCommand(s);
	}
	
	

	public int getMaxHistory() {
		return maxHistory;
	}
	public void setMaxHistory(int i) {
		maxHistory = i;
	}
	
	private int maxHistory = 20000;
	
	private void storeCommand(String command) {
		if (execHistory.getSize() > getMaxHistory()) {
			log("ERROR : execution history is full, cant store command", LogLevel.ERROR); 
		} else { execHistory.store(command); }
		if (execHistory.getSize() > getMaxHistory()) { 
			log("WARNING : execution history is full"); }
	}

	@Override public Console submitCommand (String command) { 
		if (!isScripting()) {
			storeCommand(command);
			exec.executeCommands(command);
		} else {
			if (command.equals("endScript")) {
				endScript();
				storeCommand(command);
				log(command,LogLevel.COMMAND);
			} else {
				scriptStack.store(command);
				log.clear(); refresh();
				for (String s : scriptStack.get(false)) log(s,LogLevel.COMMAND);
			}
		}
		return this; 
	}
	public AbstractConsole run(String command) { return (AbstractConsole)submitCommand(command); }


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
		Executor.dispose();
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
