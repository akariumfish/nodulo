package aa_nodulo;

import gui.nGUI;
import util.Utl;
import util.nRun;

public class nMenu {

	
	public PlaneApplet app;
	public nGUI gui;
	
	public nMenu(PlaneApplet a) {
		app = a;
		app.menu = this;
		gui = a.gui;
		
		float RS = nGUI.book.RS;
		gui.addWidget("ref")
				.setSize(4f*RS, RS)
				.setBoundParent(true)
				.setStacked(true)
				.setFont(20)
				.setTrigger()
				.setText("Exit")
				.asWidget()
				.setParent(gui.menu_right)
				.addEventTrigger(new nRun() { public void run() {
					gui.pop_exit(); }})
				;

		gui.addWidget("ref")
				.setSize(RS, RS)
				.setBoundParent(true)
				.setStacked(true)
				.setFont(20)
				.setSwitch()
				.setText("Fs")
				.asWidget()
				.setParent(gui.menu_right)
				.setLink(app.input.val_fullscreen)
				;

		gui.addWidget("ref")
				.setSize(RS, RS)
				.setBoundParent(true)
				.setStacked(true)
				.setFont(20)
				.setSwitch()
				.setText("Mn")
				.asWidget()
				.setParent(gui.menu_right)
				.setLink(gui.val_hide_bar)
				;

		gui.addWidget("ref")
				.setSize(RS, RS)
				.setBoundParent(true)
				.setStacked(true)
				.setFont(20)
				.setSwitch()
				.setText("I")
				.asWidget()
				.setParent(gui.menu_right)
				.setLink(gui.val_hide_info)
				;

		gui.addWidget("ref")
				.setSize(RS, RS)
				.setBoundParent(true)
				.setStacked(true)
				.setFont(20)
				.setSwitch()
				.setText("FX")
				.asWidget()
				.setParent(gui.menu_right)
				.setLink(gui.val_fx)
				;
		
		gui.addWidget("ref")
				.setSize(RS*8f, RS)
				.setBoundParent(true)
				.setStacked(true)
				.setPassif()
				.set_color_background(Utl.color(0,0))
				.asWidget()
				.setParent(gui.menu_right)
				.setLink(app.data.val_root_savepath)
				.setFont(20).asWidget()
				;


		gui.add_menu_trigg("Save", new nRun() { public void run() { 
			gui.data.full_save(); }});
		gui.add_menu_trigg("Save to", new nRun() { public void run() {
			gui.pop_saveas(); }});
		gui.add_menu_trigg("Shortcut", new nRun() { public void run() {
			gui.pop_shortcut(); }});


	}
	
	
	
}
