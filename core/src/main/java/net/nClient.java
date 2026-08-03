package net;

import java.io.IOException;
import java.util.HashMap;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Listener.ThreadedListener;
import app.Applet;
import app.nRun;

import data.sValue;
import net.nNetwork.*;

public class nClient extends nNetEntity {
	
	public void sendMessage(Object v) {
		client.sendTCP(v);
	}
	
	public void dispose() {
		try {
			client.dispose();
		}
		catch(Exception e) {
			System.out.println(e.getMessage());
		}
	}
	
	Client client;
	String name;
	
	boolean connected = false;

	public boolean serverListening = false;
	public boolean isListening = true;
	
	public nClient (nNetwork n) {
		super(n);
		
		client = new Client();
		client.start();
		nNetwork.register(client);

		// ThreadedListener runs the listener methods on a different thread.
		client.addListener(new Listener() {
			public void connected (Connection connection) { }
			public void disconnected (Connection connection) { 
				connected = false;
			}
			
			public void received (Connection connection, Object object) {
				if (object instanceof ConfirmLogin) {
					ConfirmLogin msg = (ConfirmLogin)object;
					connected = true;
					serverListening = msg.listen;
					isListening = true;
					return;
				}
				else if (object instanceof UpdateValue) {
					UpdateValue msg = (UpdateValue)object;
					applyValueUpdate(msg);
					return;
				}
//				else if (object instanceof AddBloc) {
//					AddBloc msg = (AddBloc)object;
//					applyAddBloc(msg);
//					return;
//				}
//				else if (object instanceof RemoveBloc) {
//					RemoveBloc msg = (RemoveBloc)object;
//					applyRemoveBloc(msg);
//					return;
//				}
				else if (object instanceof ServerSpeaking) {
					serverListening = false;
					return;
				}
				else if (object instanceof ServerListening) {
					serverListening = true;
					return;
				}
				else if (object instanceof Tick) {
					Tick msg = (Tick)object;
					nRun.runEvents(net.eventGotTick);
					return;
				}
				else if (object instanceof ServerPacket) {
					ServerPacket msg = (ServerPacket)object;
//					int ref = msg.ref, l = msg.l;
//					byte[] arr = msg.arr;
					nRun.runEvents(net.eventGotDataPacket, msg);
					return;
				}
			}
		});
		
		String host = "127.0.0.1";
		try {
			client.connect(20000, host, nNetwork.port);
			// Server communication after connection can go here, or in Listener#connected().
		} catch (IOException ex) {
			ex.printStackTrace();
		}

		name = app.player_ref;
		Login login = new Login();
		login.name = name;
		client.sendTCP(login);
	}

}
