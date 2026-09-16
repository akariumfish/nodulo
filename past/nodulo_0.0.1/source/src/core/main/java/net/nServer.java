package net;

import java.io.IOException;
import java.util.HashSet;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

import net.nNetwork.*;
import util.nRun;


public class nServer extends nNetEntity {
	
	public void sendMessage(Object v) {
		server.sendToAllTCP(v);
	}
	
	public void dispose() {
		try {
			server.dispose();
		}
		catch(Exception e) {
			System.out.println(e.getMessage());
		}
	}
	
	Server server;
	
	HashSet<nPlayer> loggedIn = new HashSet<nPlayer>();
	
	public boolean isListening = true;
	public boolean clientSpeaking = false;
//	public String clientSpeakingRef = "";
	
	public nServer (nNetwork n) throws IOException {
		super(n);
		server = new Server() {
			protected Connection newConnection () {
				// By providing our own connection implementation, we can store per
				// connection state without a connection ID to state look up.
				return new PlayerConnection();
			}
		};

		nNetwork.register(server);

		server.addListener(new Listener() {
			public void received (Connection c, Object object) {
				PlayerConnection connection = (PlayerConnection)c;
				nPlayer player = connection.player;
				
				if (object instanceof Login) {
					// Ignore if already logged in.
					if (player != null) return;

					// Reject if the name is invalid.
					String name = ((Login)object).name;
					if (!isValid(name)) { c.close(); return; }

					// Reject if already logged in.
					for (nPlayer other : loggedIn) 
						if (other.name.equals(name)) { c.close(); return; }

					player = new nPlayer();
					player.name = name;
					connection.player = player;
					loggedIn.add(player);
					ConfirmLogin login = new ConfirmLogin();
					login.name = name;
					login.listen = isListening;
					server.sendToTCP(connection.getID(), login);
					nRun.runEvents(net.eventNewClient);
					return;
				}
				
				if (object instanceof UpdateValue) {
					// Ignore if not logged in.
					if (player == null) return;
					UpdateValue msg = (UpdateValue)object;
					applyValueUpdate(msg);
					server.sendToAllExceptTCP(connection.getID(), msg);
					return;
				}
//				if (object instanceof AddBloc) {
//					// Ignore if not logged in.
//					if (player == null) return;
//					AddBloc msg = (AddBloc)object;
//					applyAddBloc(msg);
//					server.sendToAllExceptTCP(connection.getID(), msg);
//					return;
//				}
//				if (object instanceof RemoveBloc) {
//					// Ignore if not logged in.
//					if (player == null) return;
//					RemoveBloc msg = (RemoveBloc)object;
//					applyRemoveBloc(msg);
//					server.sendToAllExceptTCP(connection.getID(), msg);
//					return;
//				}
				else if (object instanceof ClientPacket) {
					ClientPacket msg = (ClientPacket)object;
					nRun.runEvents(net.eventGotDataPacket, msg);
					return;
				}
				else if (object instanceof ClientSpeaking) {
					ClientSpeaking msg = (ClientSpeaking)object;
//					if (isListening)
						clientSpeaking = true;
					return;
				}
				else if (object instanceof ClientListening) {
					ClientListening msg = (ClientListening)object;
					clientSpeaking = false;
					return;
				}
			}

			public void disconnected (Connection c) {
				PlayerConnection connection = (PlayerConnection)c;
				if (connection.player != null) {
					loggedIn.remove(connection.player);
				}
			}
			
			private boolean isValid (String value) {
				if (value == null) return false;
				value = value.trim();
				if (value.length() == 0) return false;
				return true;
			}
		});
		server.bind(nNetwork.port);
		server.start();
	}

	static class PlayerConnection extends Connection {
		public nPlayer player;
	}

}
