package aa_term;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.TimeUtils;
import util.Utl;

public class LogEntry {
	private String text;
	private String tag;
	private LogLevel level;
	private long timeStamp;

	protected LogEntry (String msg, LogLevel level) {
		this.text = msg; this.tag = "";
		this.level = level;
		timeStamp = TimeUtils.millis();
	}

	protected LogEntry (String t, String msg, LogLevel level) {
		this.text = msg; this.tag = Utl.copy(t);
		this.level = level;
		timeStamp = TimeUtils.millis();
	}

	public Color getColor () {
		return level.getColor();
	}

	protected String toConsoleString () {
		String r = "";
		if (level.equals(LogLevel.COMMAND)) {
			r += tag;
			r += level.getIdentifier();
		}
		r += text;
		return r;
	}

	@Override public String toString () {
		return timeStamp + ": " + level.getIdentifier() + text;
	}
}
