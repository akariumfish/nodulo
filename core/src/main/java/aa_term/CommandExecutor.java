package aa_term;

import java.util.ArrayList;
import java.util.Collections;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.Method;
import com.noodle.nodulo.GdxApp;

/**
 * Extend this class and fill it with methods (also <code>public</code>) that you wish to have work with the {@link Console}. Then
 * call {@link Console#setCommandExecutor(CommandExecutor)}.<br>
 * <br>
 * <b>Notes</b><br>
 * <ul>
 * <li>Arguments <i><b>must</b></i> be primitive types (the only exception being {@link String}).</li>
 * <li>No two methods, of the same name, can have the same number of parameters. Make multiple methods with more specific names if
 * they must have the same number of parameters.</li>
 * <li>Methods are case-<b>insensitive</b> when invoked from the console.</li>
 * </ul>
 *
 * @author StrongJoshua
 */
public class CommandExecutor {
	
	public String ref;
	public CommandExecutor(String r) {
		ref = r;
	}
	
	Method[] methods = null;
	ArrayList<Method> methods_arr = null;
	void getMethods() {
		if (methods == null) {
			ArrayList<Method> m = getAllMethods();
			methods = new Method[m.size()];
			for (int i = 0 ; i < m.size() ; i++)
				methods[i] = m.get(i);
		}
	}
	ArrayList<Method> getAllMethods () {
		if (methods_arr == null) {
			methods_arr = new ArrayList<Method>();
			Class c = this.getClass();
			while (c != Object.class) {
				Collections.addAll(methods_arr, ClassReflection.getDeclaredMethods(c));
				c = c.getSuperclass();
			}
		}
		return methods_arr;
	}
	
	void printCommands () {
		for (Method m : getAllMethods()) {
			if (m.isPublic() && ConsoleUtils.canDisplayCommand(console, m)) {
				String s = "";
				s += m.getName();
				s += " : ";

				Class<?>[] params = m.getParameterTypes();
				for (int i = 0; i < params.length; i++) {
					s += params[i].getSimpleName();
					if (i < params.length - 1) {
						s += ", ";
					}
				}

				console.log(s);
			}
		}
	}
	
	
	protected Console console;

	protected void setConsole (Console c) {
		console = c;
	}

	/**
	 * Prints the log to a local file.
	 *
	 * @param path The relative path of the local file to print to.
	 */
	@HiddenCommand
	public final void saveLog (String path) {
		console.printLogToFile(path);
	}
	
	@HiddenCommand
	public final void printLog () {
		console.printLog();
	}
//	public final void printCode (boolean code) {
//		console.printCode(code);
//	}

	@HiddenCommand
	@ConsoleDoc(description = "Print current code to console as java instructions") 
	public final void printCode () {
		console.printCode(true);
	}

	@HiddenCommand
	@ConsoleDoc(description = "Save current code to file") 
	public final void saveCode (String path) {
		console.printToFile(path);
	}
	@HiddenCommand
	public final void store (boolean t) {
		console.store(t);
	}

	@HiddenCommand
	@ConsoleDoc(description = "Store the current code") 
	public final void storeCode (String ref) {
		console.storeCode(ref);
	}

	@HiddenCommand
	@ConsoleDoc(description = "Run a stored code") 
	public final void runCode (String ref) {
		console.runCode(ref);
	}

	/**
	 * Closes the application completely.
	 */
	@ConsoleDoc(description = "Exits the application.") 
	public final void close () {
		GdxApp.app.close_app();
	}

	/**
	 * Go to title screen
	 */
	@ConsoleDoc(description = "Exits to the title.") 
	public final void title () {
		GdxApp.app.to_title();
	}

	/**
	 * Shows all available methods, and their parameter types, in the console.
	 */
	@ConsoleDoc(description = "Shows all available methods.") 
	public final void help () {
		console.printCommands();
		for (String s : console.getSysMap().allKey()) {
			console.log("  - "+s);
		} 
	}

	/**
	 * Prints out ConsoleDoc for the given command, if it exists.
	 *
	 * @param command The command to get help for.
	 */
	@ConsoleDoc(description = "Prints console docs for the given command.") 
	public final void help (String command) {
		if (console.getSysMap().hasKey(command)) {
			console.log(command+" system commands: ");
			console.getSysMap().get(command).printCommands();
		} else {
			console.printHelp(command);
		}
	}

	/**
	 * Deselects the text field in the console. Gives keyboard control back to the application.
	 */
	@HiddenCommand
	@ConsoleDoc(description = "Deselects the console text field. Click to " + "re-select.") 
	public final void logView () {
		console.deselect();
		console.blockStore();
	}
	

	@HiddenCommand
	@ConsoleDoc(description = "Change the current system", 
			paramDescriptions = {"The system ref"}) 
	public void sys(String r) {
		if (console.getSysMap().hasKey(r)) {
			console.log("using system: "+r, LogLevel.SUCCESS);
			console.setTitle("Terminal - T to hide - "+r);
			console.setCommandExecutor(console.getSysMap().get(r));
		} else {
			console.log("didnt found system "+r, LogLevel.ERROR);
		}
		console.blockStore();
	}

	@HiddenCommand
	@ConsoleDoc(description = "List all system") 
	public void allsys() {
		for (String s : console.getSysMap().allKey())
			console.log("  - "+s, LogLevel.SUCCESS);
		console.blockStore();
	}
	

//	public void setExecuteHiddenCommands (boolean enabled) {
//		console.setExecuteHiddenCommands(enabled);
//		console.log("ExecuteHiddenCommands was set to " + enabled);
//	}

	@ConsoleDoc(description = "set if hidden commands are displayed", 
			paramDescriptions = {"bool"}) 
	public void hidecom (boolean enabled) {
		console.setDisplayHiddenCommands(enabled);
		console.log("DisplayHiddenCommands was set to " + enabled);
	}

	@ConsoleDoc(description = "Clear le terminal") 
	public void clear () {
		console.clear();
	}
}
