package aa_term;

import com.badlogic.gdx.utils.reflect.Method;

/**
 * @author Eric
 */
public final class ConsoleUtils {
	public static boolean canExecuteCommand (Console console, Method method) {
		return console.isExecuteHiddenCommandsEnabled() || !method.isAnnotationPresent(HiddenCommand.class);
	}

	public static boolean canDisplayCommand (Console console, Method method) {
		return console.isDisplayHiddenCommandsEnabled() || !method.isAnnotationPresent(HiddenCommand.class);
	}

	public static String exceptionToString (final Throwable throwable) {
		StringBuilder result = new StringBuilder();
		Throwable cause = throwable;

		while (cause != null) {
			result.append("\nCaused by: ")
					.append(cause.getClass().getCanonicalName())
					.append(": ")
					.append(cause.getMessage());

			for (final StackTraceElement traceElement : cause.getStackTrace()) {
				result.append("\n\tat ").append(traceElement.toString());
			}
			cause = cause.getCause();
		}
		return result.toString();
	}
}
