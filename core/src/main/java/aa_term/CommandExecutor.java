package aa_term;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.reflect.Annotation;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.Field;
import com.badlogic.gdx.utils.reflect.Method;
import com.badlogic.gdx.utils.reflect.ReflectionException;
import com.noodle.nodulo.GdxApp;

import data.sBoo;
import data.sFlt;
import data.sInt;
import data.sStr;
import data.sValueBloc;
import data.sVec;
import util.Utl;
import util.nMap;

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
	

	public interface ScriptableConstructor <T extends Scriptable<T>> {
		public int identity(T t);
		public T get(int i);
		public boolean remove(T target);
		public boolean remove(int target_identity);
		public int scriptLength(T target);
		public int scriptLength(int target_identity);
		public int compile(T target, byte[] stack, int offset); // write state to stack
		public int compile(int target_identity, byte[] stack, int offset);
		public int build(); // > new instance indentity
		public int build(byte[] stack, int offset, int length); // > new instance indentity
		public boolean compute(T target, byte[] stack, int offset, int length); // copy state from stack
		public boolean compute(int target_identity, byte[] stack, int offset, int length); 
	}

	class Builder <K extends Scriptable<K>> implements CommandExecutor.ScriptableConstructor<K> {

		@Override public K get(int i) { return null; }
		@Override public int build() { return 0; } 
		@Override public int build(byte[] stack, int offset, int length) { return 0; } 
		@Override public boolean remove(int i) { return false; }
		@Override public int identity(K t) { return 0; }
		@Override public boolean remove(K target) { return false; }
		@Override public int scriptLength(K target) { return 0; }
		@Override public int scriptLength(int target_identity) { return 0; }
		@Override public int compile(K target, byte[] stack, int offset) { return 0; }
		@Override public int compile(int target_identity, byte[] stack, int offset) { return 0; }
		@Override public boolean compute(K target, byte[] stack, int offset, int length) { return false; }
		@Override public boolean compute(int target_identity, byte[] stack, int offset, int length) { return false; }
		
	}
	
	public static abstract class Scriptable <T extends Scriptable<T>> {

		public ScriptableConstructor<T> constructor() {
			return null;
		}
		
	}

	nMap<ScriptableClass<?,?>> scriptables = new nMap<ScriptableClass<?,?>>();
	
	private class ScriptableClass <T extends Scriptable<T>, K extends ScriptableConstructor<T>> {
		final String name;
		final Class<T> clazz;
		final K constructor;
		nMap<ScriptField<?,T,K>> fields = new nMap<ScriptField<?,T,K>>();
		ScriptableClass(Class<T> cl, K ck) {
			clazz = cl; constructor = ck;
			name = cl.getName();
			scriptables.put(name,this);
			Field[] fields = ClassReflection.getFields(cl);
			for (Field f : fields) { 
				Annotation annotation = f.getDeclaredAnnotation(ScriptedField.class);
				if (annotation != null) {
					addField(f, f.getType());
				}
			}
		}
		private <V> void addField(Field f,Class<V> cl) {
			new ScriptField<V,T,K>(this,f,cl); 
		}
	}

	private class ScriptField <V, T extends Scriptable<T>, K extends ScriptableConstructor<T>> {
		final String name;
		final Class<V> type;
		final String type_name;
		final ScriptableClass<T,K> scriptable;
		final Field field;
		final String description;
		final String[][] settings;
		final V def;
		ScriptField(ScriptableClass<T,K> sc, Field f, Class<V> cl) {
			scriptable = sc; field = f; 
			name = field.getName();
			type = cl;
			type_name = type.getName();
			scriptable.fields.put(name,this);
			Annotation annotation = f.getDeclaredAnnotation(ScriptedField.class);
			ScriptedField doc = annotation.getAnnotation(ScriptedField.class);
			description = doc.description();
			String[] sett = doc.settings();
			settings = new String[sett.length/2][2];
			V d = null;
			for (int i = 0, c = 0; i < sett.length/2 ; i++) {
				settings[i][0] = sett[c++];
				settings[i][1] = sett[c++];
				if (settings[i][0].equals("def")) {
					d = Utl.from_string(settings[i][1],type); }
			}
			def = d;
		}
	}

	@NotCommand
	public <T extends Scriptable<T>> 
	void registerScriptable(Class<T> cl) {

		new ScriptableClass<T,Builder<T>>(cl,new Builder<T>());
		
	}
	

	@HelpCommand(description = "New instance of a scripted class", 
			paramDescriptions = {"class name"}) 
	public void newObject(String class_name) {
		
	}

	
	
	
	
	
	
	
	
	
	public String ref;

	protected Console console;

	private final sValueBloc bloc;

	private static final HashMap<Byte,CommandExecutor> all_execs = 
			new HashMap<Byte,CommandExecutor>();
	private static final HashMap<Integer,Commande> all_commands = 
			new HashMap<Integer,Commande>();

	private static int commande_counter = 0;
	private static byte executor_counter = 0;
	
	private final byte executor_id;

	private nMap<Commande> commands = new nMap<Commande>();
	
	public CommandExecutor(String r, sValueBloc b, Object o) {
		ref = r; 
		if (o != null) register(o);
		this.bloc = b;
		executor_id = executor_counter; executor_counter++;
		all_execs.put(executor_id, this);
	}

	static void dispose() {
		commande_counter = 0;
		executor_counter = 0;
		all_commands.clear();
		all_execs.clear();
	}
	
	protected void setConsole (Console c) {
		console = c;
		register();
	}

	private void register() {
		ArrayList<Method> arr = new ArrayList<Method>();
		Class c = this.getClass();
		while (c != Object.class) {
			Collections.addAll(arr, ClassReflection.getDeclaredMethods(c));
			for (Method m : arr) {
				Annotation annotation = m.getDeclaredAnnotation(NotCommand.class);
				if (m.isPublic() && annotation == null) {
					Commande cm = new Commande(m,this);
					if (c == CommandExecutor.class) cm.baseMethod = true;
				}
			}
			arr.clear();
			c = c.getSuperclass();
		}
	}

	void register(Object o) {
		ArrayList<Method> arr = new ArrayList<Method>();
		Class c = o.getClass();
		while (c != Object.class) {
			Collections.addAll(arr, ClassReflection.getDeclaredMethods(c));
			c = c.getSuperclass();
		}
		for (Method m : arr) {
			Annotation annotation = m.getDeclaredAnnotation(NotCommand.class);
			if (m.isPublic() && annotation == null) register(m,o);
		}
	}

	void register(Method m, Object o) {
		if (m == null || o == null) return;
		Annotation annotation = m.getDeclaredAnnotation(TerminalCommand.class);
		if (m.isPublic() && ((o instanceof CommandExecutor) || 
				annotation != null)) new Commande(m,o);
	}

	private class Commande {

		private final int commande_id;

		Method method;
		String name, description, help;
		Object context;
		Class<?>[] params;
		boolean isHidden = false, isLogged = true, isStored = true, baseMethod = false;
		Commande(Method m, Object o) {
			method = m; context = o;
			name = method.getName();
			params = m.getParameterTypes();
			isHidden = m.isAnnotationPresent(HiddenCommand.class);
			isLogged = !m.isAnnotationPresent(NoLogCommand.class);
			isStored = !m.isAnnotationPresent(NoStoreCommand.class);
			if (commands.hasKey(name+"_"+params.length)) {
				Utl.logn("ERROR : CommandExecutor.new Commande "
						+ name + " (" + params.length + " args) :"
						+ " a commande with the same name and the same number of parameters allready exist");
			}
			commands.put(name+"_"+params.length,this);
			commande_id = commande_counter; commande_counter++;
			all_commands.put(commande_id, this);
			
			description = "";
			description += m.getName();
			description += " : ";
			Class<?>[] params = m.getParameterTypes();
			for (int i = 0; i < params.length; i++) {
				description += params[i].getSimpleName();
				if (i < params.length - 1) {
					description += ", ";
				}
			}

			StringBuilder sb = new StringBuilder();
			sb.append(m.getName()).append(": ");
			Annotation annotation = m.getDeclaredAnnotation(HelpCommand.class);
			if (annotation != null) {
				HelpCommand doc = annotation.getAnnotation(HelpCommand.class);
				sb.append(doc.description());
//				Class<?>[] params = m.getParameterTypes();
				for (int i = 0; i < params.length; i++) {
					sb.append("\n");
					for (int j = 0; j < m.getName().length() + 2; j++)
						// using spaces this way works with monotype fonts
						sb.append(" ");
					sb.append(params[i].getSimpleName()).append(": ");
					if (i < doc.paramDescriptions().length)
						sb.append(doc.paramDescriptions()[i]);
				}
			} else {
//				Class<?>[] params = m.getParameterTypes();
				for (int i = 0; i < params.length; i++) {
					sb.append(params[i].getSimpleName());
					if (i < params.length - 1) {
						sb.append(", ");
					}
				}
			}

			help = sb.toString();
			
		}

		void print() {
			if (!isHidden || console.isDisplayHiddenCommandsEnabled()) 
				console.log(description);
		}

		void printAll() {
			console.log(description);
		}

		void printHelp() {
			console.log(help);
		}
		
		boolean exec(String command, String[] sArgs) {
			if (sArgs.length != params.length) return false;
			Object[] args = args(sArgs);
			if (args == null) return false;
			boolean e = exec(args);
			if (e && isStored) 
				console.storeCommand(command);
			if (e && isLogged) 
				console.log(command, LogLevel.COMMAND);
			return e;
		}

		boolean exec(Object[] args) {
			try {
				method.setAccessible(true);
				method.invoke(context, args);
				return true;
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
				return false;
			}
		}
		Object[] args(String[] sArgs) {
			Object[] args = null;
			if (sArgs != null) {
				int numArgs = sArgs.length;
				if (numArgs == params.length) {
					try {
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
					} catch (Exception e) {
						// Error occurred trying to parse parameter, continue
						// to next function
						return null;
					}
				}
			}
			return args;
		}

		//TODO
		int compile(byte[] stack, int offset, Object[] args) {
			int step = offset, i = 0; 
			stack[step++] = executor_id;
			byte[] bytes = Utl.getBytes(commande_id);
			for (i = 0 ; i < bytes.length ; i++) stack[step++] = bytes[i];
			for (int a = 0 ; a < args.length ; a++) {
				bytes = Utl.getBytes(Utl.type_class_id.get(args[a].getClass()));
				for (i = 0 ; i < bytes.length ; i++) stack[step++] = bytes[i];
				bytes = Utl.getBytes(args[a]);
				for (i = 0 ; i < bytes.length ; i++) stack[step++] = bytes[i];
			}
			return step;
		}
		
		
	}
	
	//TODO
	int compute(final byte[] stack, final int offset) {
		int step = offset, i = 0; 
		byte exec_id = stack[step++];
		if (exec_id != executor_id) return all_execs.get(exec_id).compute(stack,offset);
		int com_id = Utl.getInt(stack,step);
		step += Utl.BYTE_SIZE_INT;
		Commande com = all_commands.get(com_id);
		
		// com.compute
		
		return step;
	}
	
	
	
	
	void execCommand (String command) {
		
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
		
		Commande com = commands.get(methodName+"_"+sArgs.length);
		
		if (com == null) {
			console.log("No such method found.", LogLevel.ERROR);
			return;
		}
		
		if (!com.exec(command,sArgs))
			console.log("Bad parameters.", LogLevel.ERROR);
		
		return;
	}
	void printCommands () {
		for (Commande c : commands.all()) if (!c.baseMethod) c.print();
	}
	void printAllCommands () {
		for (Commande c : commands.all()) if (!c.baseMethod) c.printAll();
	}
	void printHelps () {
		for (Commande c: commands.all()) if (!c.baseMethod) c.printHelp();
	}
	void printBaseCommands () {
		for (Commande c : commands.all()) if (c.baseMethod) c.print();
	}
	void printAllBaseCommands () {
		for (Commande c : commands.all()) if (c.baseMethod) c.printAll();
	}
	void printBaseHelps () {
		for (Commande c: commands.all()) if (c.baseMethod) c.printHelp();
	}
	void printHelp (String command) {
		if (!commands.hasKey(command)) {
			console.log("Commande <"+command+"> does not exist.");
			return; }
		commands.get(command).printHelp();
	}

	
	
	
	
	// COMMANDS :

	@NoStoreCommand
	@HelpCommand(description = "List all values") 
	public void allval() {
		for (String v : this.bloc.values.allKey()) 
			console.log(this.bloc.values.get(v).type+" "+v);
	}

	@HelpCommand(description = "set a Boolean sValue", 
			paramDescriptions = {"value ref","value"}) 
	public void setboo(String r, boolean v) {
		if (this.bloc.getValue(r,sBoo.class) != null) {
			this.bloc.getValue(r,sBoo.class).set(v);
			console.log(r+" = "+v, LogLevel.SUCCESS);
		} else {
			console.log("ERROR : cant find sBoo "+r+" in "+bloc.ref, LogLevel.ERROR);
		}
	}

	@HelpCommand(description = "set a Float sValue", 
			paramDescriptions = {"value ref","value"}) 
	public void setflt(String r, float v) {
		if (this.bloc.getValue(r,sFlt.class) != null) {
			this.bloc.getValue(r,sFlt.class).set(v);
			console.log(r+" = "+v, LogLevel.SUCCESS);
		} else {
			console.log("ERROR : cant find sFlt "+r+" in "+bloc.ref, LogLevel.ERROR);
		}
	}

	@HelpCommand(description = "set an Integer sValue", 
			paramDescriptions = {"value ref","value"}) 
	public void setint(String r, int v) {
		if (this.bloc.getValue(r,sInt.class) != null) {
			this.bloc.getValue(r,sInt.class).set(v);
			console.log(r+" = "+v, LogLevel.SUCCESS);
		} else {
			console.log("ERROR : cant find sInt "+r+" in "+bloc.ref, LogLevel.ERROR);
		}
	}

	@HelpCommand(description = "set a String sValue", 
			paramDescriptions = {"value ref","value"}) 
	public void setstr(String r, String v) {
		if (this.bloc.getValue(r,sStr.class) != null) {
			this.bloc.getValue(r,sStr.class).set(v);
			console.log(r+" = "+v, LogLevel.SUCCESS);
		} else {
			console.log("ERROR : cant find sStr "+r+" in "+bloc.ref, LogLevel.ERROR);
		}
	}

	@HelpCommand(description = "set a Vector2 sValue", 
			paramDescriptions = {"value ref","x","y"}) 
	public void setvec(String r, float x, float y) {
		if (this.bloc.getValue(r,sVec.class) != null) {
			this.bloc.getValue(r,sVec.class).set(x,y);
			console.log(r+" = "+x+","+y, LogLevel.SUCCESS);
		} else {
			console.log("ERROR : cant find sVec "+r+" in "+bloc.ref, LogLevel.ERROR);
		}
	}

	
	/**
	 * Prints the log to a local file.
	 *
	 * @param path The relative path of the local file to print to.
	 */
	@HiddenCommand
	@NoStoreCommand
	public final void saveLog (String path) {
		console.printLogToFile(path);
	}
	
	@HiddenCommand
	@NoStoreCommand
	public final void printLog () {
		console.printLog();
	}
	
//	public final void printCode (boolean code) {
//		console.printCode(code);
//	}
	@HiddenCommand
	@NoStoreCommand
	@HelpCommand(description = "Print current code to console as java instructions") 
	public final void printCode () {
		console.printCode(true);
	}

	@HiddenCommand
	@NoStoreCommand
	@HelpCommand(description = "Save current code to file") 
	public final void saveCode (String path) {
		console.printToFile(path);
	}
	@HiddenCommand
	@NoStoreCommand
	public final void store (boolean t) {
		console.store(t);
	}

	@HiddenCommand
	@NoLogCommand
	@NoStoreCommand
	@HelpCommand(description = "List all stored codes") 
	public final void allStore () {
		console.allStore();
	}

	@HiddenCommand
	@NoStoreCommand
	@HelpCommand(description = "Store the current code") 
	public final void storeCode (String ref) {
		console.storeCode(ref);
	}

	@HiddenCommand
	@HelpCommand(description = "Run a stored code") 
	public final void runCode (String ref) {
		console.runCode(ref);
	}

	/**
	 * Closes the application completely.
	 */
	@HelpCommand(description = "Exits the application.") 
	public final void close () {
		GdxApp.app.close_app();
	}

	/**
	 * Go to title screen
	 */
	@HelpCommand(description = "Exits to the title.") 
	public final void title () {
		GdxApp.app.to_title();
	}

	/**
	 * Shows all available methods, and their parameter types, in the console.
	 */
	@NoLogCommand
	@NoStoreCommand
	@HelpCommand(description = "Shows all available methods.") 
	public final void help () {
		printBaseCommands();
		console.printCommands();
		for (String s : console.getSysMap().allKey()) {
			console.log("sys "+s);
		} 
	}

	/**
	 * Prints out ConsoleDoc for the given command, if it exists.
	 *
	 * @param command The command to get help for.
	 */
	@NoLogCommand
	@NoStoreCommand
	@HelpCommand(description = "Prints console docs for the given command.", 
			paramDescriptions = {"Command name, "
					+ "enter <all> to list hidden commands, "
					+ "enter <full> for a complete overview of the commands."}) 
	public final void help (String command) {
		if (command.equals("full")) {
			console.log("Basic commands:", LogLevel.TITLE);
			printBaseHelps();
			console.log("Systems commands:", LogLevel.TITLE);
			for (String s : console.getSysMap().allKey()) {
				console.log("  - sys "+s+" :");
				console.getSysMap().get(s).printHelps();
			} 
			console.log("Stored code:  (execute with runCode)", LogLevel.TITLE);
			allStore();
		} else if (command.equals("all")) {
			console.log("Basic commands:", LogLevel.TITLE);
			printAllBaseCommands();
			console.log("System commands:", LogLevel.TITLE);
			console.printAllCommands();
			console.log("All systems:", LogLevel.TITLE);
			for (String s : console.getSysMap().allKey()) {
				console.log("sys "+s);
			} 
		} else if (console.getSysMap().hasKey(command)) {
			console.log(command+" commands: ", LogLevel.TITLE);
			console.getSysMap().get(command).printCommands();
		} else {
			console.printHelp(command);
		}
	}

	/**
	 * Deselects the text field in the console. Gives keyboard control back to the application.
	 */
	@HiddenCommand
	@NoLogCommand
	@NoStoreCommand
	@HelpCommand(description = "Deselects the console text field. Click to " + "re-select.") 
	public final void logView () {
		console.deselect();
	}
	

	@HiddenCommand
	@HelpCommand(description = "Change the current system", 
			paramDescriptions = {"The system ref"}) 
	public void sys(String r) {
		if (console.getSysMap().hasKey(r)) {
			console.log("using system: "+r, LogLevel.SUCCESS);
//			console.setTitle("Terminal - small 2 to hide - "+r);
			console.setCommandExecutor(console.getSysMap().get(r));
		} else {
			console.log("didnt found system "+r, LogLevel.ERROR);
		}
	}

	@HiddenCommand
	@NoLogCommand
	@NoStoreCommand
	@HelpCommand(description = "List all system") 
	public void allsys() {
		for (String s : console.getSysMap().allKey())
			console.log("  - "+s, LogLevel.SUCCESS);
	}

	@HiddenCommand
	@NoLogCommand
	@NoStoreCommand
	@HelpCommand(description = "Choose if the logs are recorded", 
			paramDescriptions = {"value"}) 
	public void setLogRecord(boolean b) {
		console.setRecordLog(b);
	}

	@HiddenCommand
	@HelpCommand(description = "Change the terminal window position", 
			paramDescriptions = {"horizontal position in percent of screen width",
					"vertical position in percent of screen height"}) 
	public void setTerminalPos(float x, float y) {
		console.setPositionPercent(x,y);
	}

	@HiddenCommand
	@HelpCommand(description = "Change the terminal window size", 
			paramDescriptions = {"width in percent of screen width",
					"height in percent of screen height"}) 
	public void setTerminalSize(float x, float y) {
		console.setSizePercent(x,y);
	}


//	public void setExecuteHiddenCommands (boolean enabled) {
//		console.setExecuteHiddenCommands(enabled);
//		console.log("ExecuteHiddenCommands was set to " + enabled);
//	}

	@HelpCommand(description = "set if hidden commands are displayed", 
			paramDescriptions = {"bool"}) 
	@NoLogCommand
	@NoStoreCommand
	public void hiddencom (boolean enabled) {
		console.setDisplayHiddenCommands(enabled);
		console.log("DisplayHiddenCommands was set to " + enabled);
	}

	@HelpCommand(description = "Clear thee terminal") 
	public void clear () {
		console.clear();
	}
}
