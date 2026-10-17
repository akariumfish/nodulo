/*******************************************************************************
 * Copyright 2014 See AUTHORS file.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package util;

import com.badlogic.gdx.utils.ObjectMap;
import com.noodle.nodulo.GdxApp;

/**
 * A simple Timer class that let's you measure multiple times and are identified via an id.
 * @author Stefan Bachmann
 */
public class Timer {
	private ObjectMap<String, Long> times;
	private ObjectMap<String, Long> pauses;


	public Timer () {
		times = new ObjectMap<String, Long>();
		pauses = new ObjectMap<String, Long>();
	}

	public long pause (String name) {
		if (times.containsKey(name)) {
			long pauseTime = stop(name);
			pauses.put(name, pauseTime);
			return pauseTime;
		} 
		return 0;
	}

	public long play (String name) {
		if (pauses.containsKey(name)) {
			long pauseTime = pauses.remove(name);
			times.put(name, System.currentTimeMillis() - pauseTime);
			return pauseTime;
		} 
		return 0;
	}

	public void start (String name) {
		times.put(name, System.currentTimeMillis());
	}

	public long stop (String name) {
		if (times.containsKey(name)) {
			long startTime = times.remove(name);
			return System.currentTimeMillis() - startTime;
		} else if (pauses.containsKey(name)) {
			return pauses.remove(name);
		} else {
//			throw new RuntimeException("Timer id doesn't exist.");
			Utl.logn("Timer id doesn't exist.");
			return 0;
		}
			
	}

	public void dispose() {
		times.clear(); 
		pauses.clear();
	}

}
