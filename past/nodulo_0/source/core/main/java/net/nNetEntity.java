package net;

import java.util.HashMap;

import data.*;
import net.nNetwork.*;
import util.nRun;
import app.App;

public abstract class nNetEntity {
	
	App app;
	nNetwork net;
	
	
	public nNetEntity (nNetwork n) {
		net = n; app = net.app; }

	public abstract void sendMessage(Object v);
	
	public void frame_end() { 
		for (sValue v : net.sync_vals_changed) {
			sendSyncValToAll(v);
		}
//		for (nObjectTrio o : net.sync_blocs_changed) {
//			sendSyncBlocToAll(o);
//		}
	}
	
	
	
	
	
	
	//        VAL AND BLOC

	public void sendSyncValToAll(sValue v) {
//		app.log("sendSyncValToAll"); 
		UpdateValue msg = new UpdateValue();
		msg.data = new HashMap<String,String>();
		msg.data.put("adress", v.adress);
		msg.data.put("type", v.type);
		v.toNetMsg(msg);
		sendMessage(msg);
	}
	
	public void applyValueUpdate(UpdateValue msg) {
//		app.log("applyValueUpdate"); 
		String adress = msg.getStr("adress");
		if (adress != null) {
			sValue v = app.data.getValFromAdress(adress);
			if (v != null) {
				v.fromNetMsg(msg);
			}
		}
	}
	
//	public void sendSyncBlocToAll(nObjectTrio o) {
//		app.log("sendSyncBlocToAll"); 
//		sValueBloc synced_bloc = (sValueBloc)o.obj1;
//		String change = (String)o.obj2;
//		sValueBloc changed_bloc = (sValueBloc)o.obj3;
//		
//		if (change.equals("add_bloc")) {
//			app.addEventNextFrame(new nRun() { public void run() {
////				AddBloc msg = new AddBloc();
////				msg.adress = synced_bloc.adress;
////				msg.ref = changed_bloc.ref;
////				Save_Bloc sb = new Save_Bloc(msg.ref);
////				changed_bloc.preset_to_save_bloc(sb);
////				msg.data = sb.toStringList();
////				sendMessage(msg);
//			}});
//		}
//		else if (change.equals("del_bloc")) {
//			RemoveBloc msg = new RemoveBloc();
//			msg.adress = changed_bloc.adress;
//			sendMessage(msg);
//		}
//	}
//	
//	public void applyAddBloc(AddBloc msg) {
//		app.log("applyAddBloc"); 
//		String adress = msg.adress;
//		sValueBloc parent = app.data.getBlocFromAdress(adress);
//		if (parent != null && parent.getBloc(msg.ref) == null) {
////			Save_Bloc sb = new Save_Bloc(msg.ref);
////			sb.fromStringList(msg.data);
////			parent.newBloc(sb);
//		}
//		
//	}
//	public void applyRemoveBloc(RemoveBloc msg) {
//		app.log("applyRemoveBloc"); 
//		String adress = msg.adress;
//		sValueBloc bloc = app.data.getBlocFromAdress(adress);
//		if (bloc != null) {
//			bloc.clear();
//		}
//	}

}
