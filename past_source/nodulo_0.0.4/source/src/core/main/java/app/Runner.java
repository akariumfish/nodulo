package app;

import util.nRun;

public interface Runner {
	public void addRunFrame(nRun r);
	public void removeRunFrame(nRun r);
	public void addRunFrameStart(nRun r);
	public void removeRunFrameStart(nRun r);
	public void addRunFrameEnd(nRun r);
	public void removeRunFrameEnd(nRun r);
	public void addEventNextFrame(nRun r);

	public void addDelayEvent(int delay, nRun r);
}
