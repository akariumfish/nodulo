package app;

import java.util.ArrayList;
import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import data.sBoo;
import data.sInt;
import data.sVec;
import util.nRun;

public class nInput implements InputProcessor {

	//keyboard letters
	public boolean getState(char k) { 
		return getKeyboardButton(k).state;
	}
	public boolean getClick(char k) { 
		return getKeyboardButton(k).trigClick;
	}
	public boolean getUnClick(char k) { 
		return getKeyboardButton(k).trigUClick;
	}

	//mouse n specials
	public boolean getState(String k) { 
		return getButton(k).state;
	}
	public boolean getClick(String k) { 
		return getButton(k).trigClick;
	}
	public boolean getUnClick(String k) { 
		return getButton(k).trigUClick;
	}

	public char getLastKey() { 
		return last_key;
	}
	
	public static final String[] alphabet_str = { 
//			  "0", "1", "2", "3", "4", "5", "6", "7", "8", "9",
			  "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", 
			  "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"//, 
//			  "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", 
//			  "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"
			  }; 
	public static final char[] alphabet_char = { 
//			  '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
			  'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 
			  'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'//, 
//			  'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 
//			  'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'
			  }; 

	public nInput(App a) {//PApplet app) {
		app = a;
		mouseLeft = getButton("MouseLeft");
		mouseRight = getButton("MouseRight");
		mouseCenter = getButton("MouseCenter");
		keyBackspace = getButton("Backspace"); 
		keyEnter = getButton("Enter");
		keyCtrl = getButton("Ctrl");
		keyShift = getButton("Shift");
		keyLeft = getButton("Left"); 
		keyRight = getButton("Right");
		keyUp = getButton("Up"); 
		keyDown = getButton("Down");
		keyAll = getButton("All"); //any key
		keyChar = getButton("Char"); //any key
		
		for (char c : alphabet_char) { getKeyboardButton(c); }
		for (String c : alphabet_str) { getButton(c); }

		Gdx.input.setInputProcessor(this);
		
		val_mouse_pos = app.data.system_bloc.newVec("val_mouse_pos");
		val_mouse_prev = app.data.system_bloc.newVec("val_mouse_prev");
		val_mouse_move = app.data.system_bloc.newVec("val_mouse_move");

	    val_framerate = app.data.system_bloc.newInt("val_framerate");
	    fps_stack = new int[fps_stack_size];
	    for (int i = 0 ; i < fps_stack_size ; i++) fps_stack[i] = 60;
	    
	    val_seed = app.data.root_bloc.newInt("val_seed", 123456);
		rng = new Random(val_seed.get());
	    
		val_fullscreen = app.data.root_bloc.newBoo("val_fullscreen", false);
		nRun run_fs = new nRun() { public void run() {
			if (val_fullscreen.get()) app.gdx.fullscreen();
			else app.gdx.window(); }};

		val_javaHeap = app.data.system_bloc.newInt("val_javaHeap", 0);
		val_nativeHeap = app.data.system_bloc.newInt("val_nativeHeap", 0);
			
		app.addEventNextFrame(new nRun() { public void run() {
			run_fs.run();
			val_fullscreen.addEventChangeLastFrame(run_fs); 
			app.gdx.addEventScreen(new nRun() { public void run() {
				val_fullscreen.set(app.gdx.isfullscreen()); }}); }});
	}

	public App app;
	
	public sBoo val_fullscreen;
	
	public sInt val_framerate;
	
	public sVec val_mouse_pos, val_mouse_prev, val_mouse_move;
	
	public sInt val_seed, val_javaHeap, val_nativeHeap;
	public Random rng;
	
	public void reset_rng() { rng.setSeed(val_seed.get()); }

	public boolean mouse_has_been_catched = false;
	public Vector2 mouse = new Vector2();
	public Vector2 pmouse = new Vector2(); //prev pos
	public Vector2 mmouse = new Vector2(); //mouvement
	public boolean mouseWheelUp, mouseWheelDown;
	public boolean do_shortcut = true;
	ArrayList<nInput_Button> pressed_keys = new ArrayList<nInput_Button>();
	char last_key = ' ';

	ArrayList<nInput_Button> buttons = new ArrayList<nInput_Button>();

	//  public ArrayList<sValue> shorted_values = new ArrayList<sValue>();
	public nInput_Button mouseLeft;
	public nInput_Button mouseRight;
	public nInput_Button mouseCenter;
	public nInput_Button keyBackspace;
	public nInput_Button keyEnter;
	public nInput_Button keyCtrl;
	public nInput_Button keyShift;
	public nInput_Button keyLeft;
	public nInput_Button keyRight;
	public nInput_Button keyUp;
	public nInput_Button keyDown;
	public nInput_Button keyAll;
	public nInput_Button keyChar;

	nInput_Button getButton(String r) {
		for (nInput_Button b : buttons) if (b.ref.equals(r)) return b;
		nInput_Button n = new nInput_Button(r); 
		buttons.add(n);
		return n;
	}
	public nInput_Button getKeyboardButton(char k) {
		for (nInput_Button b : buttons) if (b.ref.equals("k") && k == b.key_char) return b;
		nInput_Button n = new nInput_Button("k", k); 
		buttons.add(n);
		return n;
	}

	private static final int heap_long = 60;
	private long[] jheaps = new long[heap_long];
	private long[] nheaps = new long[heap_long];
	private int heap_cnt = 0;
	private long jheap_med = 0, nheap_med = 0;
	
	public void frame_str() {

		jheaps[heap_cnt] = app.gdx.javaHeap;
		nheaps[heap_cnt] = app.gdx.nativeHeap;
		heap_cnt++; if (heap_cnt >= heap_long) heap_cnt = 0;
		jheap_med = 0; nheap_med = 0;
		for (int i = 0 ; i < heap_long ; i++) {
			jheap_med += jheaps[i]; nheap_med += nheaps[i]; }
		jheap_med /= heap_long; nheap_med /= heap_long;
		
		val_javaHeap.set(jheap_med / 1000000);
		val_nativeHeap.set(nheap_med / 1000000);
		
		pmouse.x = mouse.x; 
		pmouse.y = mouse.y;
		
		Vector3 m = new Vector3(Gdx.input.getX(), 
				Gdx.input.getY(), 0);
		m = app.gdx.viewport.unproject(m);
		mouse.set(m.x, m.y);
		
		mmouse.x = mouse.x - pmouse.x; 
		mmouse.y = mouse.y - pmouse.y;
		
		val_mouse_pos.set(mouse); 
		val_mouse_prev.set(pmouse); 
		val_mouse_move.set(mmouse); 
		
		mouse_has_been_catched = false;

		fps_stack[fps_stack_count] = (int)(1f / Gdx.graphics.getDeltaTime());
		fps_stack_count++;
		if (fps_stack_count >= fps_stack_size) fps_stack_count = 0;
		fps_med = 0;
		for (int i = 0 ; i < fps_stack_size ; i++) fps_med += fps_stack[i];
		float f = fps_med / fps_stack_size;
		if (f > 0) val_framerate.set(f);

	}
	
	private int[] fps_stack;
	private int fps_stack_count = 0, fps_med = 0;
	private final int fps_stack_size = 60;
	
	public void frame_end() {
		mouseWheelUp = false; 
		mouseWheelDown = false;
		for (nInput_Button b : buttons) {
			b.frame();
		}
		last_key = 0;
	}



	public boolean touchDragged (int x, int y, int pointer) {
		return false;
	}

	public boolean mouseMoved (int x, int y) {
		return false;
	}

	public boolean scrolled (float amountX, float amountY) {
		float e = amountY;
		if (e<0) { 
			mouseWheelUp =true; 
			mouseWheelDown =false;
		}
		if (e>0) { 
			mouseWheelDown = true; 
			mouseWheelUp=false;
		}
		return false;
	}

	public boolean touchCancelled(int screenX, int screenY, int pointer, int button) {
		return false;
	}

	public boolean keyTyped (char character) {
		last_key = character;
		return false;
	}

	String keycode_temp = "";
	public boolean keyDown (int keycode) {
		keycode_temp = Input.Keys.toString(keycode);
		
//		Utl.logn("<"+keycode_temp+">");
		
		char keychar = 0;
		if (keycode_temp != null && keycode_temp.length() == 1) keychar = keycode_temp.charAt(0);
		if (keycode_temp != null && keycode_temp.equals("Space")) keychar = ' ';
		
//		Utl.logn(keycode_temp);
//		Utl.logn(""+keychar);
		
		boolean found = true;
		for (nInput_Button b : buttons) 
			if (b.ref.equals("k") && b.key_char == keychar) { 
				found = true; b.eventPress(); pressed_keys.add(b); }

		if (keycode_temp != null && keycode_temp.equals("L-Shift")) { found = false; keyShift.eventPress(); }
		if (keycode == Input.Keys.LEFT) 		{ found = false; keyLeft.eventPress(); }
		if (keycode == Input.Keys.RIGHT) 	{ found = false; keyRight.eventPress(); }
		if (keycode == Input.Keys.UP) 		{ found = false; keyUp.eventPress(); }
		if (keycode == Input.Keys.DOWN) 		{ found = false; keyDown.eventPress(); }
		if (keycode == Input.Keys.BACKSPACE) { found = false; keyBackspace.eventPress(); }
		if (keycode == Input.Keys.ENTER) 	{ found = false; keyEnter.eventPress(); }
		//      if (app.keyCode == PConstants.CONTROL) keyCtrl.eventPress();
		
		if (found) {
			keyChar.eventPress();
		}
//		else app.log("nInput cant find letter "+keychar);
		
		keyAll.eventPress();

		return false;
	}

	public boolean keyUp (int keycode) {
		keycode_temp = Input.Keys.toString(keycode);
		char keychar = 0;
		if (keycode_temp != null && keycode_temp.length() == 1) keychar = keycode_temp.charAt(0);
		if (keycode_temp != null && keycode_temp.equals("Space")) keychar = ' ';
		
		boolean found = true;
		for (nInput_Button b : buttons) 
			if (b.ref.equals("k") && b.key_char == keychar) { 
				found = true; pressed_keys.remove(b); b.eventRelease(); }

		if (keycode_temp != null && keycode_temp.equals("L-Shift")) { found = false; keyShift.eventRelease(); }
		if (keycode == Input.Keys.LEFT) { found = false; keyLeft.eventRelease(); }
		if (keycode == Input.Keys.RIGHT) { found = false; keyRight.eventRelease(); }
		if (keycode == Input.Keys.UP) { found = false; keyUp.eventRelease(); }
		if (keycode == Input.Keys.DOWN) { found = false; keyDown.eventRelease(); }
		if (keycode == Input.Keys.BACKSPACE) { found = false; keyBackspace.eventRelease(); }
		if (keycode == Input.Keys.ENTER) { found = false; keyEnter.eventRelease(); }
		//      if (app.keyCode == PConstants.CONTROL) keyCtrl.eventRelease();
		
		if (found) {
			boolean ks = false;
			for (nInput_Button b : buttons) ks = ks && b.state;
			if (!ks) keyChar.eventRelease();
		}
		keyAll.eventRelease();

		return false;
	}

	public boolean touchDown (int x, int y, int pointer, int button) {
		if (button==Buttons.LEFT) mouseLeft.eventPress();
		if (button==Buttons.RIGHT) mouseRight.eventPress();
	    if (button==Buttons.MIDDLE) mouseCenter.eventPress();
		return false;
	}

	public boolean touchUp (int x, int y, int pointer, int button) {

		if (button==Buttons.LEFT) mouseLeft.eventRelease();
		if (button==Buttons.RIGHT) mouseRight.eventRelease();
		if (button==Buttons.MIDDLE) mouseCenter.eventRelease();

		return false;
	}
}
