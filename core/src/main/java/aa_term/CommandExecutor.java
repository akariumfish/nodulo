package aa_term;

import java.util.ArrayList;
import java.util.Collections;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.Method;
import com.badlogic.gdx.utils.reflect.ReflectionException;
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

	protected Console console;

	public CommandExecutor(String r) {
		ref = r;
	}

	protected void setConsole (Console c) {
		console = c;
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
	
	
	
	
	
	
	public void execCommand (String command) {
		if (console.isDisabled())
			return;

		console.log(command, LogLevel.COMMAND);

		String[] parts = command.split(" ");
		String methodName = parts[0];
		String[] mParts = methodName.split("/");
		
		if (mParts.length == 2 && console.getSysMap().hasKey(mParts[0])) {
			String back = "";
			if (!ref.equals(mParts[0])) {
				back = ref;
				console.execCommand("sys "+mParts[0]);
			}
			methodName = mParts[1];
			for (int i = 1 ; i < parts.length ; i++) methodName += " " + parts[i];
			console.execCommand(methodName);
			if (back.length() > 0) console.execCommand("sys "+back);
			return;
		}
		
		String[] sArgs = null;
		if (parts.length > 1) {
			sArgs = new String[parts.length - 1];
			for (int i = 1; i < parts.length; i++) {
				sArgs[i - 1] = parts[i];
			}
		}

		Array<Integer> possible = new Array<Integer>();
		for (int i = 0; i < methods.length; i++) {
			Method method = methods[i];
			if (method.getName().equalsIgnoreCase(methodName) && ConsoleUtils.canExecuteCommand(console, method)) {
				possible.add(i);
			}
		}

		if (possible.size <= 0) {
			console.log("No such method found.", LogLevel.ERROR);
			return;
		}

		int size = possible.size;
		int numArgs = sArgs == null ? 0 : sArgs.length;
		for (int i = 0; i < size; i++) {
			Method m = methods[possible.get(i)];
			Class<?>[] params = m.getParameterTypes();
			if (numArgs == params.length) {
				try {
					Object[] args = null;

					try {
						if (sArgs != null) {
							args = new Object[numArgs];

							for (int j = 0; j < params.length; j++) {
								Class<?> param = params[j];
								final String value = sArgs[j];

								if (param.equals(String.class)) {
									args[j] = value;
								} else if (param.equals(Boolean.class) || param.equals(boolean.class)) {
									args[j] = Boolean.parseBoolean(value);
								} else if (param.equals(Byte.class) || param.equals(byte.class)) {
									args[j] = Byte.parseByte(value);
								} else if (param.equals(Short.class) || param.equals(short.class)) {
									args[j] = Short.parseShort(value);
								} else if (param.equals(Integer.class) || param.equals(int.class)) {
									args[j] = Integer.parseInt(value);
								} else if (param.equals(Long.class) || param.equals(long.class)) {
									args[j] = Long.parseLong(value);
								} else if (param.equals(Float.class) || param.equals(float.class)) {
									args[j] = Float.parseFloat(value);
								} else if (param.equals(Double.class) || param.equals(double.class)) {
									args[j] = Double.parseDouble(value);
								} else if (param.equals(Vector2.class)) {
									args[j] = new Vector2().fromString(value);
								}
							}
						}
					} catch (Exception e) {
						// Error occurred trying to parse parameter, continue
						// to next function
						continue;
					}

					m.setAccessible(true);
					console.setTmpStore();
					m.invoke(this, args);
					console.validCode(command);
					return;
				} catch (ReflectionException e) {
					String msg = e.getMessage();
					if (msg == null || msg.length() <= 0) {
						msg = "Unknown Error";
						e.printStackTrace();
					}
					console.log(msg, LogLevel.ERROR);
					if (console.getConsoleTrace()) {
						console.log(e, LogLevel.ERROR);
					}
					return;
				}
			}
		}

		console.log("Bad parameters. Check your code.", LogLevel.ERROR);
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
	@ConsoleDoc(description = "List all stored codes") 
	public final void allStore () {
		console.allStore();
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
			console.setTitle("Terminal - small 2 to hide - "+r);
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
