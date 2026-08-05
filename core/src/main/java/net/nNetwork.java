package net;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.EndPoint;

import data.*;
import gui.nAlign;
import gui.nInterface;
import gui.nWidget;
import gui.nWidgetGroup;
import util.Utl;
import util.nMap;
import util.nRun;
import app.App;
import aa_nodulo.pTime;

public class nNetwork {
	
	
	
	
	ArrayList<nRun> eventNewClient = new ArrayList<nRun>();
	ArrayList<nRun> eventGotDataPacket = new ArrayList<nRun>();
	ArrayList<nRun> eventGotTick = new ArrayList<nRun>();
	
	public nNetwork addEventNewClient(nRun r) { eventNewClient.add(r); return this; }
	public nNetwork removeEventNewClient(nRun r) { eventNewClient.remove(r); return this; }
	public nNetwork addEventGotDataPacket(nRun r) { eventGotDataPacket.add(r); return this; }
	public nNetwork removeEventGotDataPacket(nRun r) { eventGotDataPacket.remove(r); return this; }
	public nNetwork addEventGotTick(nRun r) { eventGotTick.add(r); return this; }
	public nNetwork removeEventGotTick(nRun r) { eventGotTick.remove(r); return this; }
	
	
	

	static public void registerData(Kryo kryo) {
		
		kryo.register(Tick.class);
		kryo.register(ServerPacket.class);
		kryo.register(ClientPacket.class);
		
	}

	static public class Tick { 
//		public long cnt;
	}
	
	static public class ServerPacket { 
		public int ref, l;
		public byte[] arr;
	}

	static public class ClientPacket { 
		public int ref, l;
		public byte[] arr;
	}
	

	public void sendPacket(int ref, byte[] arr) { 
		if (is_server) {
			ServerPacket pp = new ServerPacket();  
			pp.ref = ref; pp.l = arr.length; pp.arr = arr; 
			server.sendMessage(pp);
		} else if (is_client) {
			ClientPacket pp = new ClientPacket();  
			pp.ref = ref; pp.l = arr.length; pp.arr = arr; 
			client.sendMessage(pp); 
		}
	}
	
	public void sendTick() {
		Tick tk = new Tick();
		if (is_server) {
			server.sendMessage(tk); }
		else if (is_client) {
			client.sendMessage(tk); }
	}

	public boolean sendServerSpeaking() {
		if (is_server && server.isListening && !server.clientSpeaking) {
			server.isListening = false;
			ServerSpeaking tk = new ServerSpeaking();
			server.sendMessage(tk); 
			return true; }
		return false;
	}
	public void sendServerListening() {
		if (is_server && !server.isListening) {
			server.isListening = true;
			ServerListening tk = new ServerListening();
			server.sendMessage(tk); }
	}
	public boolean sendClientSpeaking() {
		if (is_client && client.serverListening && client.isListening) {
			client.isListening = false;
			ClientSpeaking tk = new ClientSpeaking();
			client.sendMessage(tk); 
			return true; }
		return false;
	}
	public void sendClientListening() {
		if (is_client && !client.isListening) {
			client.isListening = true;
			ClientListening tk = new ClientListening();
			client.sendMessage(tk); }
	}
	public boolean isServerListening() {
		if (is_server) return server.isListening;
		if (is_client) return client.serverListening;
		return false;
	}
	
	
	
	
	
	
	
	nMap<Integer> sync_data_ref_ids = new nMap<Integer>();
	nIDMap<String> sync_data_id_refs = new nIDMap<String>();
	int id_section = 0;
	int id_section_cnt = 0;

	public int getSyncedDataSection() { return id_section_cnt; }
	public int nextSyncedDataSection() {
		id_section_cnt++;
		id_section = id_section_cnt * 100000; 
		return id_section_cnt; }
	public void setSyncedDataSection(int s) {
		id_section_cnt = s;
		id_section = id_section_cnt * 100000; }

	public int newSyncedData() {
		int id = id_section + sync_data_id_refs.getFreeId();
		String ref = "___"+id;
		if (sync_data_ref_ids.hasKey(ref)) {
			Utl.logn("ERROR : nNetwork.newSyncedData(  "+ref+"  ) ref allready exist.");
			return -1; }
		sync_data_ref_ids.put(ref,id);
		return id;
	}
	
	public int newSyncedData(String ref) {
		if (sync_data_ref_ids.hasKey(ref)) {
			Utl.logn("ERROR : nNetwork.newSyncedData(  "+ref+"  ) ref allready exist.");
			return -1; }
		int id = id_section + sync_data_id_refs.getFreeId();
		sync_data_ref_ids.put(ref,id);
		return id;
	}
	
	
	
	
	static public final int port = 54555;

	// This registers objects that are going to be sent over the network.
	static public void register (EndPoint endPoint) {
		Kryo kryo = endPoint.getKryo();

		kryo.register(byte.class);
		kryo.register(float.class);
		kryo.register(int.class);
		kryo.register(boolean.class);
		kryo.register(byte[].class); 
		kryo.register(float[].class);
		kryo.register(int[].class);
		kryo.register(boolean[].class);
		
		kryo.register(Byte.class);
		kryo.register(Float.class);
		kryo.register(Integer.class);
		kryo.register(Boolean.class);
		kryo.register(Byte[].class);
		kryo.register(Float[].class);
		kryo.register(Integer[].class);
		kryo.register(Boolean[].class);
		
		kryo.register(String.class);
		kryo.register(Vector2.class);
		kryo.register(String[].class);
		kryo.register(Vector2[].class);
		kryo.register(HashMap.class);

		kryo.register(Login.class);
		kryo.register(ConfirmLogin.class);

		kryo.register(ServerSpeaking.class);
		kryo.register(ServerListening.class);
		kryo.register(ClientSpeaking.class);
		kryo.register(ClientListening.class);

		registerValAndBloc(kryo);
		registerData(kryo);

	}
	static public class Login { public String name; }

	static public class ConfirmLogin { public String name; public boolean listen; }

	static public class ServerSpeaking {}
	static public class ServerListening {}
	static public class ClientSpeaking {}
	static public class ClientListening {}
	

	
	
	App app;

	nServer server;
	nClient client;
	
	public boolean is_server = false, is_client = false;
	
	sStr val_net_mode;
	sBoo val_net_started;
	sInt val_connected_player;
	sBoo val_connected_to_server;

	public nNetwork(App a) {
		app = a;

		val_net_mode = app.data.setting_bloc.newStr("val_net_mode", "network mode", "");
		val_net_started = app.data.setting_bloc.newBoo("val_net_started", "network started", false);
		val_connected_player = app.data.setting_bloc.newInt("val_connected_player", (int)0);
		val_connected_to_server = app.data.setting_bloc.newBoo("val_connected_to_server", false);

//		app.menu.add_tool_menu_trigg("Network", new nRun() { public void run() {
//			pop_net_window(); }});

//		if (app.start_as_client || app.start_as_server) {
//			
//			app.menu.add_info_text("");
//			if (app.start_as_client) app.menu.add_info_text("Connected: ", val_connected_to_server);
//			if (app.start_as_server) app.menu.add_info_text("Players: ", val_connected_player);
//			app.menu.add_info_text("");
//			
////			pop_net_window();
//			
//		}
		
		
	}
	
	public void dispose() {
		if (val_net_started.get()) {
			if (val_net_mode.equals("server") && server != null) {
				server.dispose();
			}
			else if (val_net_mode.equals("client") && client != null) {
				client.dispose();
			}
		}
	}
	
	
	public void start_server() {
		if (!val_net_started.get()) {
			try { server = new nServer(this); }
			catch(Exception e) { 
				System.out.println(e.getMessage()); 
				Utl.logn("ERROR : nNetwork.start_server() catched an Exception. "
						+ "Server could not start.");
				return; }
			val_net_mode.set("server");
			val_net_started.set(true);
			sync_vals_changed.clear();
//			sync_blocs_changed.clear();
			is_server = true;
		}
	}

	public void start_client() {
		if (!val_net_started.get()) {
			try { client = new nClient(this); }
			catch(Exception e) { 
				System.out.println(e.getMessage()); 
				Utl.logn("ERROR : nNetwork.start_client() catched an Exception. "
						+ "Client could not start.");
				return; }
			val_net_mode.set("client");
			val_net_started.set(true);
			sync_vals_changed.clear();
//			sync_blocs_changed.clear();
			is_client = true;
		}
	}

	public void update() {
		if (val_net_started.get()) {
			if (is_server) {
				server.frame_end();
				val_connected_player.set(server.loggedIn.size());
			}
			else if (is_client) {
				client.frame_end();
				val_connected_to_server.set(client.connected);
			}
			sync_vals_changed.clear();
//			sync_blocs_changed.clear();
		}
	}
	
	
	
	
//	public void pop_net_window() {
//
//		nInterface interf = app.menu.get_popWindow();
//
//		interf.setContext(app.data.setting_bloc);
//		
//		interf.add_row();
//		interf.add_row_label(11,"Network : ");
//
//		interf.add_row();
//		interf.add_row_watch(5, "mode: ", "val_net_mode");
//		interf.add_row_label(1,"");
//		interf.add_row_watch(5, "started: ", "val_net_started");
//		interf.add_row();
//		interf.add_row_watch(5, "Players: ", "val_connected_player");
//		interf.add_row_label(1,"");
//		interf.add_row_watch(5, "Connected: ", "val_connected_to_server");
//
//		interf.add_row();
//		interf.add_row_trigg(5, "start_server", new nRun() { public void run() {
//			start_server(); }});
//		interf.add_row_label(1,"");
//		interf.add_row_trigg(5, "start_client", new nRun() { public void run() {
//			start_client(); }});
//		
//		app.addEventNextFrame(new nRun() { public void run() {
//			app.menu.pop_popwindow("Network"); }});
//	}

	

	
	
	
	

	static public void registerValAndBloc(Kryo kryo) {
		kryo.register(UpdateValue.class);
//		kryo.register(AddBloc.class);
//		kryo.register(RemoveBloc.class);
	}
	
	static public class UpdateValue {
		public HashMap<String,String> data;
		public void put(String k, boolean b) { data.put(k, Utl.tostr(b)); }
		public void put(String k, int b) { data.put(k, Utl.tostr(b)); }
		public void put(String k, float b) { data.put(k, Utl.tostr(b)); }
		public void put(String k, String b) { data.put(k, b); }
		public boolean getBoo(String k) { return Utl.tobool(data.get(k)); }
		public int getInt(String k) { return Utl.toint(data.get(k)); }
		public float getFlt(String k) { return Utl.tofloat(data.get(k)); }
		public String getStr(String k) { return data.get(k); }
	}
	
//	static public class AddBloc { 
//		public String adress;
//		public String ref;
//		public String[] data; 
//	}
//	static public class RemoveBloc { 
//		public String adress; 
//	}

	ArrayList<sValue> sync_vals = new ArrayList<sValue>();
	HashMap<String,nRun> sync_vals_del_runs = new HashMap<String,nRun>();
	HashMap<String,nRun> sync_vals_chg_runs = new HashMap<String,nRun>();
	ArrayList<sValue> sync_vals_changed = new ArrayList<sValue>();
	
	public void addSyncVal(sValue v) {
		if (!sync_vals.contains(v)) {
			sync_vals.add(v);
			nRun del = new nRun(v) { public void run() { removeSyncVal(((sValue)builder)); }};
			sync_vals_del_runs.put(v.adress, del);
			v.addEventDelete(del);
			nRun chg = new nRun(v) { public void run() { 
				sync_vals_changed.add(((sValue)builder)); }};
			sync_vals_chg_runs.put(v.adress, chg);
			v.addEventChangeLastFrame(chg);
		}
	}
	public void removeSyncVal(sValue v) {
		if (sync_vals.contains(v)) {
			v.removeEventDelete(sync_vals_del_runs.get(v.adress));
			v.removeEventChangeLastFrame(sync_vals_chg_runs.get(v.adress));
			sync_vals.remove(v);
			sync_vals_del_runs.remove(v.adress);
			sync_vals_chg_runs.remove(v.adress);
		}
	}
	
//	class nObjectTrio {
//		public Object obj1, obj2, obj3;
//		public nObjectTrio(Object o1, Object o2, Object o3) {
//			obj1 = o1; obj2 = o2; obj3 = o3; }
//	}
//	
//	ArrayList<sValueBloc> sync_blocs = new ArrayList<sValueBloc>();
//	HashMap<String,nRun> sync_blocs_del_runs = new HashMap<String,nRun>();
//	HashMap<String,nRun> sync_blocs_chg_runs = new HashMap<String,nRun>();
//	ArrayList<nObjectTrio> sync_blocs_changed = new ArrayList<nObjectTrio>();
//	
//	public void addSyncBloc(sValueBloc v) {
//		if (!sync_blocs.contains(v)) {
//			sync_blocs.add(v);
//			nRun del = new nRun() { public void run() { removeSyncBloc(v); }};
//			sync_blocs_del_runs.put(v.adress, del);
//			v.addEventDelete(del);
//			nRun chg = new nRun() { public void run(Object o1, Object o2) { 
//				sync_blocs_changed.add(new nObjectTrio(v, o1, o2)); }};
//			sync_blocs_chg_runs.put(v.adress, chg);
//			v.addEventChangeThisFrame(chg);
//		}
//	}
//	public void removeSyncBloc(sValueBloc v) {
//		if (sync_blocs.contains(v)) {
//			v.removeEventDelete(sync_blocs_del_runs.get(v.adress));
//			v.removeEventChangeThisFrame(sync_blocs_chg_runs.get(v.adress));
//			sync_blocs.remove(v);
//			sync_blocs_del_runs.remove(v.adress);
//			sync_blocs_chg_runs.remove(v.adress);
//		}
//	}
	
	
	
}
