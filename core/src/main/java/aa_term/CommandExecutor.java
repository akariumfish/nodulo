package aa_term;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
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
	
	
	public interface Pooling <T extends Pooled<T>> {
		public T obtain();
		public T get(int i);
		public void free(T t);
	}
	
	public interface Pooled <T extends Pooled<T>> {
		public int id();
		public void create();
		public void load();
		public void init();
		public void finish();
		public void save();
		public void clear();
	}
	
	
	
	public interface ScriptableConstructor <T extends Scriptable<T> & Pooled<T>> {
		
	}
	
	class Builder <K extends Scriptable<K> & Pooled<K>> 
	implements CommandExecutor.ScriptableConstructor<K>, CommandExecutor.Pooling<K> 
	{

		@Override
		public K obtain() {
			return null;
		}

		@Override
		public K get(int i) {
			return null;
		}

		@Override
		public void free(K t) {
			
		}
	}
	
	public static abstract class Scriptable 
	<T extends Scriptable<T> & Pooled<T>> implements Pooled<T> {

		@Override
		public int id() {
			return 0;
		}

		@Override
		public void create() {
			
		}

		@Override
		public void load() {
			
		}

		@Override
		public void init() {
			
		}

		@Override
		public void finish() {
			
		}

		@Override
		public void save() {
			
		}

		@Override
		public void clear() {
			
		}
		
	}

	nMap<ScriptableClass<?,?>> scriptables = new nMap<ScriptableClass<?,?>>();
	
	private class ScriptableClass <T extends Scriptable<T> & Pooled<T>, K extends ScriptableConstructor<T>> {
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

	private class ScriptField <V, T extends Scriptable<T> & Pooled<T>, K extends ScriptableConstructor<T>> {
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
	public <T extends Scriptable<T> & Pooled<T>> 
	void registerScriptable(Class<T> cl) {

		new ScriptableClass<T,Builder<T>>(cl,new Builder<T>());
		
	}
	
	private nMap<nMap<Scriptable>> scriptable_map = 
			new nMap<nMap<Scriptable>>();
	
	private Scriptable scripting_target = null;

	@HelpCommand(description = "Make a new instance of a scripted class "
			+ "and set it as the scripting target. "
			+ "Can set a reference for the object to access it later. "
			+ "If the reference is empty the object will not be mapped "
			+ "and inaccessible after endObj.", 
			paramDescriptions = {"class name","reference of the object"}) 
	public void newObj(String class_name, String obj_ref) {
		if (scripting_target != null) return;
		ScriptableClass<?,?> scriptClass = scriptables.get(class_name);
		
	}
	
	@HelpCommand(description = "Get an instance of a scripted class mapped with"
			+ "the given reference and set it as the scripting target. ", 
			paramDescriptions = {"class name","reference of the object"}) 
	public void accessObj(String class_name, String obj_ref) {
		if (scripting_target != null) return;
		scripting_target = scriptable_map.get(class_name).get(obj_ref);
	}

	@HelpCommand(description = "Release current scripting target") 
	public void endObj() {
		if (scripting_target == null) return;
		//TODO
		scripting_target = null;
	}

	
	@HelpCommand(description = "Set a string in the current scripting target", 
			paramDescriptions = {"field name","value to apply"}) 
	public void setObjStr(String field_ref, String val) {
		if (scripting_target == null) return;
		//TODO
	}

	@HelpCommand(description = "Set an integer in the current scripting target", 
			paramDescriptions = {"field name","value to apply"}) 
	public void setObjInt(String field_ref, int val) {
		if (scripting_target == null) return;
		//TODO
	}

	@HelpCommand(description = "Set a float in the current scripting target", 
			paramDescriptions = {"field name","value to apply"}) 
	public void setObjFlt(String field_ref, float val) {
		if (scripting_target == null) return;
		//TODO
	}

	@HelpCommand(description = "Set a bool in the current scripting target", 
			paramDescriptions = {"field name","value to apply"}) 
	public void setObjBoo(String field_ref, boolean val) {
		if (scripting_target == null) return;
		//TODO
	}

	@HelpCommand(description = "Set a vector2 in the current scripting target", 
			paramDescriptions = {"field name","value to apply"}) 
	public void setObjVec(String field_ref, Vector2 val) {
		if (scripting_target == null) return;
		//TODO
	}

	@HelpCommand(description = "Set a byte in the current scripting target", 
			paramDescriptions = {"field name","value to apply"}) 
	public void setObjByt(String field_ref, byte val) {
		if (scripting_target == null) return;
		//TODO
	}

	
	
	
	
	
	
	
	
	
	protected Console console;

	private final nMap<sValueBloc> blocs = new nMap<sValueBloc>();

	private static final HashMap<Integer,Commande> all_commands = 
			new HashMap<Integer,Commande>();

	private static int commande_counter = 0;
	private nMap<Commande> commands = new nMap<Commande>();

	public CommandExecutor(Console c) {
		console = c;
		register();
		Virtual.setConsole(console);
		Virtual.setExec(this);
	}

	void register(String r, sValueBloc b) {
		blocs.put(r,b);
	}

	static void dispose() {
		commande_counter = 0;
		all_commands.clear();
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

	private class ExecutionFail {}
	
	private class Commande {

		private final int commande_id;

		Method method;
		String name, description, help;
		Object context;
		Class<?>[] params;
		Class<?> return_type;
		boolean isHidden = false, isLogged = true, isStored = true, endFlag = false;
		final int paramNb;
		
		Commande(Method m, Object o) {
			method = m; context = o;
			name = method.getName();
			params = m.getParameterTypes();
			paramNb = params.length;
			return_type = m.getReturnType();
			isHidden = m.isAnnotationPresent(HiddenCommand.class);
			endFlag = m.isAnnotationPresent(EndCommand.class);
			isLogged = !m.isAnnotationPresent(NoLogCommand.class);
			isStored = !m.isAnnotationPresent(NoStoreCommand.class);
			if (commands.hasKey(name+"_"+params.length)) {
				Utl.logn("ERROR : CommandExecutor.new Commande "
						+ name + " (" + params.length + " args) :"
						+ " a commande with the same name and the same number of parameters allready exist"); }
			commands.put(name+"_"+params.length,this);
			commande_id = commande_counter; commande_counter++;
			all_commands.put(commande_id, this);
			
			description = m.getName() + " : ";
			Class<?>[] params = m.getParameterTypes();
			for (int i = 0; i < params.length; i++) 
				description += params[i].getSimpleName() + ((i < params.length - 1) ? ", " : ""); 

			StringBuilder sb = new StringBuilder();
			sb.append(m.getName()).append(": ");
			Annotation annotation = m.getDeclaredAnnotation(HelpCommand.class);
			if (annotation != null) {
				HelpCommand doc = annotation.getAnnotation(HelpCommand.class);
				sb.append(doc.description());
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

		Object executeCommande(Object[] args) {
			try {
				method.setAccessible(true);
				return method.invoke(context, args);
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
				return new ExecutionFail();
			}
		}
		
		class Arg { 
			Arg init() { 
				for (int i = 0 ; i < paramNb ; i++) { args[i] = null; } 
				arCnt = 0; if (paramNb == arCnt) { valid = true; } else { valid = false; }
				err = false; return this; }
			private Object[] args = new Object[paramNb]; 
			private int arCnt = 0; private boolean valid = false, err = false; 
			private void increment() { arCnt++; if (paramNb == arCnt) valid = true; else valid = false; }
			private void error() { err = true; }
			boolean validate() { return !err && valid; }
			Object exec() {
				if (!validate()) return null;
				return executeCommande(args);
			}
//			boolean put(Object[] ar) {
//				if (ar.length != paramNb) return false;
//				boolean success = true;
//				for (int i = 0 ; i < paramNb ; i++) success = success && putArg(ar[i]);
//				return success;
//			}
			boolean putArg(Object value) { 
				if (params[arCnt] != value.getClass()) { error(); return false; }
				args[arCnt] = value; increment(); return true; }
			boolean parseArg(String s) { 
				try { args[arCnt] = parseArg(s,params[arCnt]); } 
				catch (Exception e) { error(); return false; } // Error occurred trying to parse parameter
				if (args[arCnt] == null) { error(); return false; }
				increment(); return true; }
			Object parseArg(String value, Class<?> param) {
				if (param.equals(String.class)) {
					return value;
				} else if (param.equals(Boolean.class) || param.equals(boolean.class)) {
					return Boolean.parseBoolean(value);
				} else if (param.equals(Byte.class) || param.equals(byte.class)) {
					return Byte.parseByte(value);
				} else if (param.equals(Short.class) || param.equals(short.class)) {
					return Short.parseShort(value);
				} else if (param.equals(Integer.class) || param.equals(int.class)) {
					return Integer.parseInt(value);
				} else if (param.equals(Long.class) || param.equals(long.class)) {
					return Long.parseLong(value);
				} else if (param.equals(Float.class) || param.equals(float.class)) {
					return Float.parseFloat(value);
				} else if (param.equals(Double.class) || param.equals(double.class)) {
					return Double.parseDouble(value);
				} else if (param.equals(Vector2.class)) {
					return new Vector2().fromString(value);
				} else return null;
			}
		}
		private final ArrayList<Commande.Arg> free_args = new ArrayList<Commande.Arg>();
		Arg obtainArg() { return (free_args.size() > 0 ? free_args.remove(0) : new Arg()).init(); }
		
	}
	
	
	
	

	private static final char SPACE = 		' ';
	private static final char OPEN = 		'(';
	private static final char CLOSE = 		')';
	private static final char IGNORE = 		'_';
	
	private static class Virtual {
		private String commandName = null;
		private final ArrayList<Object> args = new ArrayList<Object>();
		private CharBuffer inBuff;
		private StringBuffer commandBuff;
		private StringBuffer subComBuff;
		private int buffSize = 0;
		private int comSize = 0;
		private char current = 0, last = 0, next = 0;
		private boolean finish = false;
		private int wordSize = 0;
		private final ArrayList<String> comParts = new ArrayList<String>();
		private int partNb = 0;
		private boolean blocRecording = false;
		private int blocNest = 0, blocSize = 0;
		private Commande com = null;
		
		private void reset() {
			args.clear(); 
			commandName = null;
			if (inBuff != null) inBuff.position(0);
			if (commandBuff != null) commandBuff.delete(0,buffSize);
			if (subComBuff != null) subComBuff.delete(0,buffSize);
			comSize = 0;
			current = 0; last = 0; next = 0;
			finish = false;
			wordSize = 0;
			comParts.clear();
			partNb = 0;
			blocRecording = false;
			blocNest = 0; blocSize = 0;
			com = null;
		}
		
		Object error() { free(this); return exec.new ExecutionFail(); }
		
		Object execute(String command, boolean head) {
			
//			console.log("exec "+command);
			
			comSize = command.length();
			if (comSize == 0) return error();
			
			if (buffSize == 0 || buffSize < comSize) { 
				inBuff = CharBuffer.allocate(comSize); 
				commandBuff = new StringBuffer(comSize); 
				subComBuff = new StringBuffer(comSize); 
				buffSize = comSize; }
			comParts.clear();
			inBuff.position(0);
			inBuff.put(command);
			inBuff.position(0);
			commandBuff.delete(0,buffSize);
			subComBuff.delete(0,buffSize);
			blocRecording = false;
			finish = false;
			last = 0; current = 0; next = inBuff.get(); wordSize = 0; 
			blocNest = 0; blocSize = 0;
			while (!finish) {
				current = next;
				if (inBuff.hasRemaining()) next = inBuff.get(); else finish = true;
				
				if (blocRecording) { subComBuff.append(current); blocSize++; }
				
				if (current == OPEN && last == SPACE) {
					if (!blocRecording) {
						blocRecording = true;
						blocNest = 0;
						blocSize = 0;
					} else {
						blocNest++;
					}
				} else if (current == CLOSE && last != IGNORE) {
					if (blocRecording) {
						if (blocNest == 0) {
							blocRecording = false;
							subComBuff.deleteCharAt(blocSize - 1);
							blocSize = 0;
							
							Virtual virt = obtain(); virt.reset();
							Object cm = virt.execute(subComBuff.toString(), false);
							free(virt);
							if (cm instanceof ExecutionFail) {
								console.log("Cant execute <"+subComBuff.toString()+">", 
										LogLevel.ERROR); return error(); }
							comParts.add(Utl.to_string(cm));
							
							subComBuff.delete(0,buffSize);
						} else {
							blocNest--;
						}
					} 
				} else if (current != SPACE 
						&& !(current == IGNORE && (next == OPEN || next == CLOSE))
						) {
					if (!blocRecording) {
						commandBuff.append(current); wordSize++;
					}
				} else if (!blocRecording && current == SPACE && wordSize > 0) {
					comParts.add(commandBuff.toString());
					commandBuff.delete(0,buffSize);
					wordSize = 0;
				} 
				if (finish && !blocRecording && wordSize > 0) {
					comParts.add(commandBuff.toString());
					commandBuff.delete(0,buffSize);
					wordSize = 0;
				}
				last = current;
			}
			partNb = comParts.size();
			if (partNb == 0) return error();
			
			commandName = comParts.get(0) + "_" + (partNb - 1);
			com = exec.commands.get(commandName);
			if (com == null) { console.log("No such method found.", LogLevel.ERROR); return error(); }
			if (com.paramNb != partNb - 1) { console.log("Bad number of parameter.", LogLevel.ERROR); return error(); }
			
			Commande.Arg carg = com.obtainArg();
			if (!carg.validate()) 
				for (int i = 1 ; i < partNb ; i++) 
					if (!carg.parseArg(comParts.get(i))) { 
				console.log("Bad parameter "+i, LogLevel.ERROR); return error(); }
			if (!carg.validate()) { 
				console.log("Bad parameters.", LogLevel.ERROR); return error(); }
			
			if (head && com.isLogged) console.log(command, LogLevel.COMMAND);
			if (head && com.isStored) console.storeCommand(command);
			
			if (console.isScripting()) {
				if (head && com.endFlag) console.endScript();
				if (com.return_type == null) return null; 
				try { return com.return_type.newInstance(); } 
				catch (InstantiationException e) { e.printStackTrace(); } 
				catch (IllegalAccessException e) { e.printStackTrace(); } 
			}
			
			return carg.exec();
		}

		static void executeLine(String command) { 
			Virtual virt = obtain(); virt.reset(); virt.execute(command, true); free(virt); } 
		
		private static Console console;
		private static CommandExecutor exec;
		static void setConsole(Console c) { console = c; }
		static void setExec(CommandExecutor c) { exec = c; }
		
		private static final ArrayList<Virtual> free = new ArrayList<Virtual>();
		private boolean isfree = false;
		private static Virtual obtain() {
			if (free.size() == 0) return new Virtual();
			Virtual v = free.get(free.size()-1); free.remove(v); v.isfree = false; return v; }
		private static void free(Virtual v) { 
			if (!v.isfree) { v.reset(); free.add(v); } v.isfree = true; }
	}
	
	
	void executeCommands(String commands) {
		String[] parts = commands.split("\n");
		if (parts.length > 1) { for (String s : parts) Virtual.executeLine(s); }
		else Virtual.executeLine(commands);
	}
	
	
	
	
//	Object execCommand (String command) {
//		
//		String[] parts = command.split(" ");
//		String methodName = parts[0];
//		
//		String[] sArgs = null;
//		if (parts.length > 1) {
//			sArgs = new String[parts.length - 1];
//			for (int i = 1; i < parts.length; i++) { sArgs[i - 1] = parts[i]; }
//		}
//		
//		Commande com = commands.get(
//				methodName + "_" + (sArgs != null ? sArgs.length : (int)0) );
//		
//		if (com == null) { console.log("No such method found.", LogLevel.ERROR); return null; }
//		
//		if (console.isScripting()) {
//			if (com.args(sArgs) == null) {
//				console.log("Bad parameters.", LogLevel.ERROR); return null; }
//			if (com.isStored) console.storeCommand(command);
//			if (com.endFlag) console.endScript();
//			return null;
//		}
//		Object[] args = com.args(sArgs);
//		if (args == null) {
//			console.log("Bad parameters.", LogLevel.ERROR);
//			return null; }
//		if (com.isStored) console.storeCommand(command);
//		if (com.isLogged) console.log(command, LogLevel.COMMAND);
//		
//		return com.exec(args);
//	}
	
	void printCommands () { for (Commande c : commands.all()) c.print(); }
	void printAllCommands () { for (Commande c : commands.all()) c.printAll(); }
	void printHelps () { for (Commande c: commands.all()) c.printHelp(); }
	void printHelp (String command) {
		if (!commands.hasKey(command)) { console.log("Commande <"+command+"> does not exist."); return; }
		commands.get(command).printHelp(); }

	
	
	
	
	// COMMANDS :

	@NoStoreCommand
	@HelpCommand(description = "List all values") 
	public void allval() {
		for (String r : blocs.allKey())
		for (String v : blocs.get(r).values.allKey()) 
			console.log(blocs.get(r).values.get(v).type+" "+r+"/"+v+" = "+blocs.get(r).values.get(v).getString());
	}

	@HelpCommand(description = "set a Boolean sValue", 
			paramDescriptions = {"system ref","value ref","value"}) 
	public void setboo(String b, String r, boolean v) {
		if (blocs.hasKey(b) && blocs.get(b).getValue(r,sBoo.class) != null) {
			blocs.get(b).getValue(r,sBoo.class).set(v);
			console.log(r+" = "+v, LogLevel.SUCCESS);
		} else {
			console.log("ERROR : cant find sBoo "+r+" in "+b, LogLevel.ERROR);
		}
	}

	@HelpCommand(description = "get a Boolean sValue", 
			paramDescriptions = {"system ref","value ref"}) 
	public boolean getboo(String b, String r) {
		if (blocs.hasKey(b) && blocs.get(b).getValue(r,sBoo.class) != null) {
			return blocs.get(b).getValue(r,sBoo.class).get();
		} else {
			console.log("ERROR : cant find sBoo "+r+" in "+b, LogLevel.ERROR);
			return false;
		}
	}

	@HelpCommand(description = "NOT operator", paramDescriptions = {"input bool"}) 
	public boolean not(boolean b) { return !b; }

//	@HelpCommand(description = "set a Float sValue", 
//			paramDescriptions = {"value ref","value"}) 
//	public void setflt(String r, float v) {
//		if (this.bloc.getValue(r,sFlt.class) != null) {
//			this.bloc.getValue(r,sFlt.class).set(v);
//			console.log(r+" = "+v, LogLevel.SUCCESS);
//		} else {
//			console.log("ERROR : cant find sFlt "+r+" in "+bloc.ref, LogLevel.ERROR);
//		}
//	}
//
//	@HelpCommand(description = "set an Integer sValue", 
//			paramDescriptions = {"value ref","value"}) 
//	public void setint(String r, int v) {
//		if (this.bloc.getValue(r,sInt.class) != null) {
//			this.bloc.getValue(r,sInt.class).set(v);
//			console.log(r+" = "+v, LogLevel.SUCCESS);
//		} else {
//			console.log("ERROR : cant find sInt "+r+" in "+bloc.ref, LogLevel.ERROR);
//		}
//	}
//
//	@HelpCommand(description = "set a String sValue", 
//			paramDescriptions = {"value ref","value"}) 
//	public void setstr(String r, String v) {
//		if (this.bloc.getValue(r,sStr.class) != null) {
//			this.bloc.getValue(r,sStr.class).set(v);
//			console.log(r+" = "+v, LogLevel.SUCCESS);
//		} else {
//			console.log("ERROR : cant find sStr "+r+" in "+bloc.ref, LogLevel.ERROR);
//		}
//	}
//
//	@HelpCommand(description = "set a Vector2 sValue", 
//			paramDescriptions = {"value ref","x","y"}) 
//	public void setvec(String r, float x, float y) {
//		if (this.bloc.getValue(r,sVec.class) != null) {
//			this.bloc.getValue(r,sVec.class).set(x,y);
//			console.log(r+" = "+x+","+y, LogLevel.SUCCESS);
//		} else {
//			console.log("ERROR : cant find sVec "+r+" in "+bloc.ref, LogLevel.ERROR);
//		}
//	}
	

	@NoStoreCommand
	public final void beginScript(String ref) {
		console.beginScript(ref);
	}
	@NoStoreCommand
	@EndCommand
	public final void endScript() {
		console.endScript();
	}
	@NoStoreCommand
	public final void runScript(String ref) {
		console.runScript(ref);
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
	@HelpCommand(description = "List all stored scripts") 
	public final void allScript () {
		console.allScript();
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
		printCommands();
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
			console.log("Commands:", LogLevel.TITLE);
			printHelps();
		} else if (command.equals("all")) {
			console.log("Commands:", LogLevel.TITLE);
			printAllCommands(); 
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
	
	
	
	
	
	
	
	
	

//	private static class C {
//		public static final byte VOID = 		00;
//		
//		public static final byte NULL = 		01;
//		public static final byte ERROR = 	02;
//		public static final byte VALID = 	03;
//		
//		public static final byte LINE = 		10;
//		public static final byte OPEN = 		11;
//		public static final byte CLOSE = 	12;
//
//		public static final byte VALUE = 	20;
//		public static final byte ARRAY = 	21;
//		public static final byte METHOD = 	22;
//		public static final byte ARG = 		23;
//		
//		public static final byte INT = 		100;
//		public static final byte FLT = 		101;
//		public static final byte BOO = 		102;
//		public static final byte STR = 		103;
//		public static final byte VEC = 		104;
//		public static final byte BYT = 		105;
//
//		public static final byte FALSE = 	126;
//		public static final byte TRUE = 		127;
//		
//	}
//
//	private class ByteBuff {
//		byte[] data; 
//		int step, last_step, size;
//		void set(final byte[] d) { if (d == null) return; 
//			data = d; step = 0; last_step = data.length - 1; size = data.length; }
//		byte get() { return data[step]; }
//		boolean is(byte code) { return data[step] == code; }
//		byte get_next() { if (step > 0 && step <= last_step) return data[step++]; else return C.VOID; }
//		boolean is_next(byte code) { if (step > 0 && step <= last_step) return data[step++] == code; else return false; }
//		int find_after(byte code) {
//			int tmp = step; 
//			while (!is_next(code) && !isLast()) {} 
//			int result = step; step = tmp; 
//			if (result > last_step) return -1; else return result; 
//		}
//		int find_after(byte code, int stp) { 
//			int tmp = step; 
//			if (!go(stp)) return -1; 
//			while (!is_next(code) && !isLast()) {} 
//			int result = step; step = tmp; 
//			if (result > last_step) return -1; else return result; }
//		boolean next() { if (step < last_step) { step++; return true; } else return false; }
//		boolean prev() { if (step > 0) { step--; return true; } else return false; }
//		void first() { step = 0; }
//		void last() { step = last_step; }
//		boolean isFirst() { return step == 0; }
//		boolean isLast() { return step == last_step; }
//		boolean go(int i) { if (i < 0 || i > last_step) return false; step = i; return true; }
//		int size() { return size; }
//		void dispose() {
//			data = null;
//		}
//	}
////	private static Object getValue(ByteBuff data, int offset) { 
////		if (data[offset] == C.STR) return getStr(data); 
////		else if (data[offset] == C.FLT) return getFlt(data);
////		else if (data[offset] == C.INT) return getInt(data);
////		else if (data[offset] == C.BOO) return getBoo(data);
////		else if (data[offset] == C.VEC) return getVec(data);
////		else if (data[offset] == C.BYT) return getVec(data);
////		else return null;
////	}
////
////	private static String getStr(byte[] data, int offset, int length) { return new String(data, offset, length); }
////	private static int getInt(byte[] data, int offset) { return ByteBuffer.wrap(data, offset, BYTE_SIZE_INT).getInt(); }
////	private static float getFlt(byte[] data, int offset) { return ByteBuffer.wrap(data, offset, BYTE_SIZE_FLOAT).getFloat(); }
////	private static boolean getBoo(byte[] data, int offset) { return data[offset] != 0; }
////	private static Vector2 getVec(byte[] data, int offset) { return new Vector2().fromString(getStr(data, offset, 1+2*BYTE_SIZE_FLOAT)); }
//	
//
//	private static final int BYTE_SIZE_INT = 4;
//	private static final int BYTE_SIZE_FLOAT = 4;
//
//	private static byte[] getBytes(Object d) { 
//		if (d instanceof String) return getBytes((String)d); 
//		else if (d instanceof Float) return getBytes((float)d);
//		else if (d instanceof Integer) return getBytes((int)d);
//		else if (d instanceof Boolean) return getBytes((boolean)d);
//		else if (d instanceof Vector2) return getBytes((Vector2)d); 
//		else if (d instanceof Byte) return getBytes((byte)d); 
//		else return null; }
//	
//	private static byte[] getBytes(String s) { return s.getBytes(); }
//	private static byte[] getBytes(byte s) { return ByteBuffer.allocate(1).put(s).array(); }
//	private static byte[] getBytes(int s) { return ByteBuffer.allocate(BYTE_SIZE_INT).putInt(s).array(); }
//	private static byte[] getBytes(float s) { return ByteBuffer.allocate(BYTE_SIZE_FLOAT).putFloat(s).array(); }
//	private static byte[] getBytes(boolean s) { byte[] arr = {(byte) ((s) ? 1 : 0)}; return arr; }
//	private static byte[] getBytes(Vector2 s) { return getBytes(s.toString()); }
//
//	private static <T> T getValue(byte[] data, Class<T> ct) { 
//		if (ct == String.class) return (T)getStr(data); 
//		else if (ct == Float.class) return (T)(Object)getFlt(data);
//		else if (ct == Integer.class) return (T)(Object)getInt(data);
//		else if (ct == Boolean.class) return (T)(Object)getBoo(data);
//		else if (ct == Vector2.class) return (T)getVec(data);
//		else return null;
//	}
//
//	private static String getStr(byte[] data) { return new String(data); }
//	private static int getInt(byte[] data) { return ByteBuffer.wrap(data).getInt(); }
//	private static float getFlt(byte[] data) { return ByteBuffer.wrap(data).getFloat(); }
//	private static boolean getBoo(byte[] data) { return data[0] != 0; }
//	private static Vector2 getVec(byte[] data) { return new Vector2().fromString(getStr(data)); }

	
	
	
	
	
	
	
}
