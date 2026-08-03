package plane;


import java.util.ArrayList;
import java.util.Map;

import com.badlogic.gdx.math.Vector2;
import app.Applet;
import app.nPool;
import app.nRun;

import data.*;
import net.nNetwork;
import net.nNetwork.ClientPacket;
import net.nNetwork.ServerPacket;
import patch.pInstance;

public class pNet extends pSystem {
	
	
	public static boolean PRINT_LOG = false;
	
	

	public static sBloc_Builder builder = null;

	public static void build(Applet app) {
		builder = builder(app, "net", pNet.class, !app.start_solo, new nRun() { public void run(Object o) {
			sValueBloc b = (sValueBloc)o; newObject(b); }});
	}

	public static void dispose(Applet app) { pool.dispose(); }
	public static final nPool<pNet> pool = new nPool<pNet>() {
		protected pNet newObject() { return new pNet(); } };
	public static pNet newObject(sValueBloc b) {
		return pool.obtain().init(b); }







	public pNet() { super(); 
	tick_run = new nRun() { public void run(Object o) { tick((float)o); }};
	run_frame_end = new nRun() { public void run() {
		frame_end(); }};  }

	public pNet init(sValueBloc b) { return (pNet) super.init(b); }



	public nNetwork net;
	nRun run_frame_end;
	nRun tick_run;

	pSpace space;
	pTime time;

	public boolean is_server = false, is_client = false;

	public void system_init() {
		bloc.addObject("net", this);

		plane.storeSystemType(bloc.ref, this.getClass());

		net = new nNetwork(app);

		if (app.start_as_server || app.start_as_client) 
			app.addDelayEvent(2, new nRun() { public void run() { 
				space = plane.getSystem(pSpace.class);
				time = plane.getSystem(pTime.class);
				if (app.start_as_server && 
						app.getPref("AUTO_CONNECT", Boolean.class)) { init_server(); }
				else if (app.start_as_client && 
						app.getPref("AUTO_CONNECT", Boolean.class)) { init_client(); }
			}});

		app.addEventFrameEnd(run_frame_end);

	}
	public void system_load() {

	}
	public void system_clear() {
		net.dispose();
		app.removeEventFrameEnd(run_frame_end);
		if (plane.getSystem(pTime.class) != null) 
			plane.getSystem(pTime.class).removeEventTick(tick_run);
	}

	//allways
	public void frame_end() {
		net.update();
	}

	//solo or server
	public void frame(float delta) {
		if (is_client) {
//			app.log("client pNet send client speaking");
			if (tick_bang > 0 && net.sendClientSpeaking()) {
				for (int i = 0 ; i < tick_bang ; i++) {
//					app.log("client pNet bang net tick");
					time.bang_net_tick();
				}
				tick_bang = 0;
				
				for (pCollec b : Applet.duplic(chgCollecs)) send_chg_collec(b);
				for (pParam b : Applet.duplic(chgParams)) send_chg_param(b);
				chgParams.clear(); chgCollecs.clear(); 
		
//				app.log("client pNet send client listening");
				net.sendClientListening();
			} else {
				if (PRINT_LOG) app.logn("client pNet frame() could not sendClientSpeaking");
			}
		}
	}

	public int tick_bang = 0;
	public void init_client() {
		is_client = true;
		space.client_space = true;

		net.addEventGotTick(new nRun() { public void run() { 
			tick_bang++;
		}});

		net.addEventGotDataPacket(new nRun() { public void run(Object o) { 
			if (!(o instanceof ServerPacket)) return;
			ServerPacket msg = (ServerPacket)o;
			int ref = msg.ref;//, l = msg.l;
//			if (PRINT_LOG) app.log("client pNet got data pack , ref="+ref);
			byte[] arr = msg.arr;
			receiveServerPacket(ref, arr);
		}});
		

		space.addEventChgParam(new nRun() { public void run(Object o) { 
			pParam par = (pParam)o; 
			if (par.prop != null && par.prop.mode_fullsync) {
				chgParams.add(par);
				
//				for (Map.Entry<String,Object> me : par.data_changes.entrySet()) {
//					if (PRINT_LOG) app.log("client pNet send changed param data "+
//							par.pool_ref + " : " + me.getKey());
//					send_chg_param_data(par, me.getKey()); 
//				}
//				par.data_changes.clear();
//				for (Map.Entry<String,String> me : par.ref_changes.entrySet()) {
//					if (PRINT_LOG) app.log("client pNet send changed param ref "+
//							par.pool_ref + " : " + me.getKey());
//					send_chg_param_ref(par, me.getKey()); 
//				}
//				par.ref_changes.clear();
//				for (Map.Entry<String,String> me : par.body_changes.entrySet()) {
//					if (PRINT_LOG) app.log("client pNet send changed param body "+
//							par.pool_ref + " : " + me.getKey());
//					send_chg_param_body(par, me.getKey()); 
//				}
//				par.body_changes.clear();
				
//				if (PRINT_LOG) app.log("client pNet send changed param "+par.pool_ref);
//				send_chg_param(par); 
			} 
		}});
		space.addEventChgCollec(new nRun() { public void run(Object o) { 
			pCollec col = (pCollec)o; 
			if (col.prop != null && col.prop.mode_fullsync) {
				chgCollecs.add(col);
//				if (PRINT_LOG) app.log("client pNet send changed collec "+col.pool_ref);
//				send_chg_collec(col); 
			}
		}});
		

		space.cancel_change();
		
		net.start_client();
		
	}

	public void tick(float delta) {

//		app.log("server pNet send server speaking");
		if (net.sendServerSpeaking()) {

			if (got_new_client) {
				send_full_space();
				got_new_client = false;
				cancel_change();
			}
	
			for (pCollec b : Applet.duplic(delCollecs)) send_del_collec(b);
			for (pParam b : Applet.duplic(delParams)) send_del_param(b);
			for (pBody b : Applet.duplic(delBodys)) send_del_body(b);
			delBodys.clear(); delParams.clear(); delCollecs.clear(); 
			for (pCollec b : Applet.duplic(newCollecs)) send_new_collec(b);
			for (pParam b : Applet.duplic(newParams)) send_new_param(b);
			for (pBody b : Applet.duplic(newBodys)) send_new_body(b); 
			newBodys.clear(); newParams.clear(); newCollecs.clear(); 
			for (pCollec b : Applet.duplic(chgCollecs)) send_chg_collec(b);
			for (pParam b : Applet.duplic(chgParams)) send_chg_param(b);
			for (pBody b : Applet.duplic(chgBodys)) send_chg_body(b);
			chgBodys.clear(); chgParams.clear(); chgCollecs.clear(); 
			
//			app.log("server pNet send server listening");
			net.sendServerListening();
		} else {
			if (PRINT_LOG) app.logn("server pNet tick() could not sendServerSpeaking");
		}

//		app.log("server pNet send tick");
		net.sendTick();
				
	}

	public void cancel_change() {
		delBodys.clear(); delParams.clear(); delCollecs.clear(); 
		newBodys.clear(); newParams.clear(); newCollecs.clear(); 
		chgBodys.clear(); chgParams.clear(); chgCollecs.clear(); 
	}
	
	boolean got_new_client = false;

	ArrayList<pBody> newBodys = new ArrayList<pBody>();
	ArrayList<pParam> newParams = new ArrayList<pParam>();
	ArrayList<pCollec> newCollecs = new ArrayList<pCollec>();

	ArrayList<pBody> delBodys = new ArrayList<pBody>();
	ArrayList<pParam> delParams = new ArrayList<pParam>();
	ArrayList<pCollec> delCollecs = new ArrayList<pCollec>();

	ArrayList<pBody> chgBodys = new ArrayList<pBody>();
	ArrayList<pParam> chgParams = new ArrayList<pParam>();
	ArrayList<pCollec> chgCollecs = new ArrayList<pCollec>();

	public void init_server() {
		is_server = true;
		

		net.addEventGotDataPacket(new nRun() { public void run(Object o) { 
			if (!(o instanceof ClientPacket)) return;
			ClientPacket msg = (ClientPacket)o;
			int ref = msg.ref;//, l = msg.l;
			byte[] arr = msg.arr;
//			if (PRINT_LOG) app.log("server pNet got data pack , ref="+ref);
			receiveClientPacket(ref, arr);
		}});
		

		time.addEventTick(tick_run);

		net.addEventNewClient(new nRun() { public void run() { 
			got_new_client = true;
			
//			if (PRINT_LOG) app.log("server pNet new client");
////			app.addDelayEvent(1, new nRun() { public void run() { 
//				send_full_space();
////			}});
		}});

		space.addEventNewBody(new nRun() { public void run(Object o) { 
			pBody bod = (pBody)o; 
			newBodys.add(bod);
//			if (PRINT_LOG) app.log("server pNet send new body "+bod.pool_ref);
//			send_new_body(bod); 
		}});
		space.addEventNewParam(new nRun() { public void run(Object o) { 
			pParam par = (pParam)o; 
			newParams.add(par);
//			if (PRINT_LOG) app.log("server pNet send new param "+par.pool_ref);
//			send_new_param(par); 
		}});
		space.addEventNewCollec(new nRun() { public void run(Object o) { 
			pCollec col = (pCollec)o; 
			newCollecs.add(col);
//			if (PRINT_LOG) app.log("server pNet send new collec "+col.pool_ref);
//			send_new_collec(col); 
		}});

		
		space.addEventDelBody(new nRun() { public void run(Object o) { 
			pBody bod = (pBody)o; 
			delBodys.add(bod); 
//			if (PRINT_LOG) app.log("server pNet send del body "+bod.pool_ref);
//			send_del_body(bod); 
		}});
		space.addEventDelParam(new nRun() { public void run(Object o) { 
			pParam par = (pParam)o; 
			delParams.add(par);
//			if (PRINT_LOG) app.log("server pNet send del param "+par.pool_ref);
//			send_del_param(par); 
		}});
		space.addEventDelCollec(new nRun() { public void run(Object o) { 
			pCollec col = (pCollec)o; 
			delCollecs.add(col);
//			if (PRINT_LOG) app.log("server pNet send del collec "+col.pool_ref);
//			send_del_collec(col); 
		}});

		space.addEventChgBody(new nRun() { public void run(Object o) { 
			pBody bod = (pBody)o;
			chgBodys.add(bod); 
//			if (PRINT_LOG) app.log("server pNet send changed body "+bod.pool_ref);
//			send_chg_body(bod); 
		}});
		space.addEventChgParam(new nRun() { public void run(Object o) { 
			pParam par = (pParam)o; 
			chgParams.add(par);

//			for (Map.Entry<String,Object> me : par.data_changes.entrySet()) {
//				if (PRINT_LOG) app.log("server pNet send changed param data "+
//						par.pool_ref + " : " + me.getKey());
//				send_chg_param_data(par, me.getKey()); 
//			}
//			par.data_changes.clear();
//			for (Map.Entry<String,String> me : par.ref_changes.entrySet()) {
//				if (PRINT_LOG) app.log("server pNet send changed param ref "+
//						par.pool_ref + " : " + me.getKey());
//				send_chg_param_ref(par, me.getKey()); 
//			}
//			par.ref_changes.clear();
//			for (Map.Entry<String,String> me : par.body_changes.entrySet()) {
//				if (PRINT_LOG) app.log("server pNet send changed param body "+
//						par.pool_ref + " : " + me.getKey());
//				send_chg_param_body(par, me.getKey()); 
//			}
//			par.body_changes.clear();

//			if (PRINT_LOG) app.log("server pNet send changed param "+par.pool_ref);
//			send_chg_param(par); 
			
		}});
		space.addEventChgCollec(new nRun() { public void run(Object o) { 
			pCollec col = (pCollec)o; 
			chgCollecs.add(col);
//			if (PRINT_LOG) app.log("server pNet send changed collec "+col.pool_ref);
//			send_chg_collec(col); 
		}});


		space.cancel_change();
		
		net.start_server();
	}

	public void send_full_space() {
		if (space == null) return;
		byte[] b = new byte[0];
		if (PRINT_LOG) app.logn("server pNet send bloc object change storing");
		net.sendPacket(BLOC_CHANGE, b);
		if (PRINT_LOG) app.logn("server pNet send clear space ");
		net.sendPacket(CLEAR_SPACE, b);
		for (pCollec col : space.collec_pool.all()) {
			if (PRINT_LOG) app.logn("server pNet send new collec "+col.pool_ref);
			send_new_collec(col); }
		for (Map.Entry<String, sPool<pParam>> me : space.param_pools.entrySet()) {
			sPool<pParam> ppool = me.getValue();
			pProperty prop = pProperty.get(me.getKey());
			if (prop != null && !prop.mode_nosync) 
				for (pParam par : ppool.all()) {
					if (PRINT_LOG) app.logn("server pNet send new param "+par.pool_ref);
					send_new_param(par);
			}
		}
		for (pBody bod : space.body_pool.all()) {
			if (PRINT_LOG) app.logn("server pNet send new body "+bod.pool_ref);
			send_new_body(bod); }

		if (PRINT_LOG) app.logn("server pNet send unbloc object change storing ");
		net.sendPacket(UNBLOC_CHANGE, b);
	}
	
	


	public static final int CLEAR_SPACE = 0;
	public static final int NEW_COLLEC = 1;
	public static final int NEW_PARAM = 2;
	public static final int NEW_BODY = 3;
	public static final int DEL_COLLEC = 4;
	public static final int DEL_PARAM = 5;
	public static final int DEL_BODY = 6;
	public static final int CHG_COLLEC = 7;
	public static final int CHG_PARAM = 8;
	public static final int CHG_BODY = 9;
	public static final int BLOC_CHANGE = 10;
	public static final int UNBLOC_CHANGE = 11;
//	public static final int CHG_COLLEC_DATA = 12;
	public static final int CHG_PARAM_DATA = 13;
	public static final int CHG_PARAM_REF = 14;
	public static final int CHG_PARAM_BODY = 15;
//	public static final int CHG_BODY_DATA = 16;

	private void byte_arr_to_obj_stack(byte[] data, ArrayList<Object> stack) {
		int cnt = 0;
		while (cnt < data.length) {
			byte tp = data[cnt]; cnt++;
			Class<?> cl = Applet.type_id_class.get(tp);
			byte[] lon = new byte[sData.BYTE_SIZE_INT];
			for (int i = 0 ; i < sData.BYTE_SIZE_INT ; i++) {
				lon[i] = data[cnt]; cnt++; }
			int ln = sData.getInt(lon);
			byte[] dt = new byte[ln];
			for (int i = 0 ; i < ln ; i++) {
				dt[i] = data[cnt]; cnt++; }
			Object o = sData.getValue(dt, cl);
			stack.add(o);
		}
	}

	public void receiveClientPacket(int ref, byte[] data) {
		if (ref == CHG_COLLEC) {
			ArrayList<Object> stack = new ArrayList<Object>();
			byte_arr_to_obj_stack(data, stack);
			Object pi = stack.get(0);
			Object[] arr = new Object[stack.size() - 1];
			for (int i = 0 ; i < stack.size() - 1 ; i++) arr[i] = stack.get(i+1);
			int pool_id = (int)pi;
			pCollec b = space.collec_pool.getObjectAt(pool_id);
			if (b == null) { app.logn("ERROR : pNet receive client packet, CHG_COLLEC, pCollec b = null"); return; }
			if (PRINT_LOG) app.logn("server pNet received change collec "+b.pool_ref);
			b.load_from_array(arr);
		} else if (ref == CHG_PARAM) {
			ArrayList<Object> stack = new ArrayList<Object>();
			byte_arr_to_obj_stack(data, stack);
			Object pr = stack.get(0);
			Object pi = stack.get(1);
			Object[] arr = new Object[stack.size() - 2];
			for (int i = 0 ; i < stack.size() - 2 ; i++) arr[i] = stack.get(i+2);
			String prop_ref = (String)pr;
			int pool_id = (int)pi;
			sPool<pParam> pool = space.param_pools.get(prop_ref);
			if (pool == null) { app.logn("ERROR : pNet receive client packet, CHG_PARAM, pool = null"); return; }
			pParam b = pool.getObjectAt(pool_id);
			if (b == null) { app.logn("ERROR : pNet receive client packet, CHG_PARAM, pParam b = null"); return; }
			if (PRINT_LOG) app.logn("server pNet received change param "+b.pool_ref);
			b.load_from_array(arr);
		} 
//		else if (ref == CHG_PARAM_DATA) {
//			ArrayList<Object> stack = new ArrayList<Object>();
//			byte_arr_to_obj_stack(data, stack);
//			Object pr = stack.get(0);
//			Object pi = stack.get(1);
//			Object pc = stack.get(2);
//			Object pv = stack.get(3);
//			Object pd = stack.get(4);
//			String prop_ref = (String)pr;
//			int pool_id = (int)pi;
//			int clss_id = (int)pc;
//			int val_id = (int)pv;
//			
//			sPool<pParam> pool = space.param_pools.get(prop_ref);
//			if (pool == null) return;
//			pParam b = pool.getObjectAt(pool_id);
//			if (b == null) return;
//			if (PRINT_LOG) app.log("client pNet received change param "+b.pool_ref);
//			b.datas[clss_id][val_id] = pd;
//			
//		} else if (ref == CHG_PARAM_REF) {
//			ArrayList<Object> stack = new ArrayList<Object>();
//			byte_arr_to_obj_stack(data, stack);
//			Object pr = stack.get(0);
//			Object pi = stack.get(1);
//			Object pri = stack.get(2);
//			Object pnr = stack.get(3);
//			
//			String prop_ref = (String)pr;
//			int pool_id = (int)pi;
//			int ref_id = (int)pri;
//			String new_ref = (String)pnr;
//			
//			sPool<pParam> pool = space.param_pools.get(prop_ref);
//			if (pool == null) return;
//			pParam b = pool.getObjectAt(pool_id);
//			if (b == null) return;
//			if (PRINT_LOG) app.log("client pNet received change param "+b.pool_ref);
//			
//			b.refs[ref_id] = new_ref;
//			
//		} else if (ref == CHG_PARAM_BODY) {
//			ArrayList<Object> stack = new ArrayList<Object>();
//			byte_arr_to_obj_stack(data, stack);
//			Object pr = stack.get(0);
//			Object pi = stack.get(1);
//			Object pbi = stack.get(2);
//			Object pnr = stack.get(3);
//			
//			String prop_ref = (String)pr;
//			int pool_id = (int)pi;
//			int bod_id = (int)pbi;
//			String new_ref = (String)pnr;
//			
//			sPool<pParam> pool = space.param_pools.get(prop_ref);
//			if (pool == null) return;
//			pParam b = pool.getObjectAt(pool_id);
//			if (b == null) return;
//			if (PRINT_LOG) app.log("client pNet received change param "+b.pool_ref);
//
//			b.bodys[bod_id] = new_ref;
//			
//		} 
	}
	
	public void receiveServerPacket(int ref, byte[] data) {
		if (ref == CLEAR_SPACE) {
			if (PRINT_LOG) app.logn("client pNet received clear space ");
			space.clear_all_obj();
		} else if (ref == BLOC_CHANGE) {
			if (PRINT_LOG) app.logn("client pNet received bloc object change storing ");
			space.bloc_change();
		} else if (ref == UNBLOC_CHANGE) {
			if (PRINT_LOG) app.logn("client pNet received unbloc object change storing, cancel stored change ");
			space.unbloc_change();
			space.cancel_change();
		} else if (ref == NEW_COLLEC) {
			ArrayList<Object> stack = new ArrayList<Object>();
			byte_arr_to_obj_stack(data, stack);
			Object pr = stack.get(0);
			Object rp = stack.get(1);
			Object pi = stack.get(2);
			Object[] arr = new Object[stack.size() - 2];
			for (int i = 0 ; i < stack.size() - 2 ; i++) arr[i] = stack.get(i+2);
			String prop_ref = (String)pr;
			String ref_in_prop = (String)rp;
			int pool_id = (int)pi;
			
			pCollec b = space.collec_pool.obtain(pool_id)
					.init(space, pProperty.get(prop_ref), ref_in_prop);
			b.init_from_array(arr);
			if (PRINT_LOG) app.logn("client pNet received new collec "+b.pool_ref);
			
		} else if (ref == NEW_PARAM) {
			ArrayList<Object> stack = new ArrayList<Object>();
			byte_arr_to_obj_stack(data, stack);
			Object pr = stack.get(0);
			Object pi = stack.get(1);
			Object[] arr = new Object[stack.size() - 2];
			for (int i = 0 ; i < stack.size() - 2 ; i++) arr[i] = stack.get(i+2);
			String prop_ref = (String)pr;
			int pool_id = (int)pi;
			
			pParam b = space.param_pools.get(prop_ref).obtain(pool_id).obtain();
			b.init_from_array(arr);
			if (PRINT_LOG) app.logn("client pNet received new param "+b.pool_ref);
			
		} else if (ref == NEW_BODY) {
			ArrayList<Object> stack = new ArrayList<Object>();
			byte_arr_to_obj_stack(data, stack);
			Object pi = stack.get(0);
			Object[] arr = new Object[stack.size() - 1];
			for (int i = 0 ; i < stack.size() - 1 ; i++) arr[i] = stack.get(i+1);
			int pool_id = (int)pi;
			
			pBody b = space.body_pool.obtain(pool_id);
			b.init_from_array(arr);
			space.plane.getSystem(pGeom.class).init_body(b);
			if (PRINT_LOG) app.logn("client pNet received new body "+b.pool_ref);
			
		} else if (ref == DEL_BODY) {
			ArrayList<Object> stack = new ArrayList<Object>();
			byte_arr_to_obj_stack(data, stack);
			Object pr = stack.get(0);
			int bod_ind = (int)pr;
			pBody b = space.body_pool.getObjectAt(bod_ind);
			if (b == null) return;
			if (PRINT_LOG) app.logn("client pNet received del body "+b.pool_ref);
			b.clear();
		} else if (ref == DEL_PARAM) {
			ArrayList<Object> stack = new ArrayList<Object>();
			byte_arr_to_obj_stack(data, stack);
			Object pr = stack.get(0);
			Object pi = stack.get(1);
			String prop_ref = (String)pr;
			int par_ind = (int)pi;
			sPool<pParam> pool = space.param_pools.get(prop_ref);
			if (pool == null) return;
			pParam b = pool.getObjectAt(par_ind);
			if (b == null) return;
			if (PRINT_LOG) app.logn("client pNet received del param "+b.pool_ref);
			b.clear();
		} else if (ref == DEL_COLLEC) {
			ArrayList<Object> stack = new ArrayList<Object>();
			byte_arr_to_obj_stack(data, stack);
			Object pr = stack.get(0);
			int col_ind = (int)pr;
			pCollec b = space.collec_pool.getObjectAt(col_ind);
			if (b == null) return;
			if (PRINT_LOG) app.logn("client pNet received del collec "+b.pool_ref);
			b.clear();
		} else if (ref == CHG_COLLEC) {
			ArrayList<Object> stack = new ArrayList<Object>();
			byte_arr_to_obj_stack(data, stack);
			Object pi = stack.get(0);
//			Object pr = stack.get(1);
//			Object rp = stack.get(2);
			Object[] arr = new Object[stack.size() - 1];
			for (int i = 0 ; i < stack.size() - 1 ; i++) arr[i] = stack.get(i+1);
			int pool_id = (int)pi;
//			String prop_ref = (String)pr;
//			String ref_in_prop = (String)rp;
			
			pCollec b = space.collec_pool.getObjectAt(pool_id);
			if (b == null) return;
			if (PRINT_LOG) app.logn("client pNet received change collec "+b.pool_ref);
			b.load_from_array(arr);
			
		} else if (ref == CHG_PARAM) {
			ArrayList<Object> stack = new ArrayList<Object>();
			byte_arr_to_obj_stack(data, stack);
			Object pr = stack.get(0);
			Object pi = stack.get(1);
			Object[] arr = new Object[stack.size() - 2];
			for (int i = 0 ; i < stack.size() - 2 ; i++) arr[i] = stack.get(i+2);
			String prop_ref = (String)pr;
			int pool_id = (int)pi;
			
			sPool<pParam> pool = space.param_pools.get(prop_ref);
			if (pool == null) return;
			pParam b = pool.getObjectAt(pool_id);
			if (b == null) return;
			if (PRINT_LOG) app.logn("client pNet received change param "+b.pool_ref);
			b.load_from_array(arr);
			
		} 
//		else if (ref == CHG_PARAM_DATA) {
//			ArrayList<Object> stack = new ArrayList<Object>();
//			byte_arr_to_obj_stack(data, stack);
//			Object pr = stack.get(0);
//			Object pi = stack.get(1);
//			Object pc = stack.get(2);
//			Object pv = stack.get(3);
//			Object pd = stack.get(4);
//			String prop_ref = (String)pr;
//			int pool_id = (int)pi;
//			int clss_id = (int)pc;
//			int val_id = (int)pv;
//			
//			sPool<pParam> pool = space.param_pools.get(prop_ref);
//			if (pool == null) return;
//			pParam b = pool.getObjectAt(pool_id);
//			if (b == null) return;
//			if (PRINT_LOG) app.log("client pNet received change param "+b.pool_ref);
//			b.datas[clss_id][val_id] = pd;
//			
//		} else if (ref == CHG_PARAM_REF) {
//			ArrayList<Object> stack = new ArrayList<Object>();
//			byte_arr_to_obj_stack(data, stack);
//			Object pr = stack.get(0);
//			Object pi = stack.get(1);
//			Object pri = stack.get(2);
//			Object pnr = stack.get(3);
//			
//			String prop_ref = (String)pr;
//			int pool_id = (int)pi;
//			int ref_id = (int)pri;
//			String new_ref = (String)pnr;
//			
//			sPool<pParam> pool = space.param_pools.get(prop_ref);
//			if (pool == null) return;
//			pParam b = pool.getObjectAt(pool_id);
//			if (b == null) return;
//			if (PRINT_LOG) app.log("client pNet received change param "+b.pool_ref);
//			
//			b.refs[ref_id] = new_ref;
//			
//		} else if (ref == CHG_PARAM_BODY) {
//			ArrayList<Object> stack = new ArrayList<Object>();
//			byte_arr_to_obj_stack(data, stack);
//			Object pr = stack.get(0);
//			Object pi = stack.get(1);
//			Object pbi = stack.get(2);
//			Object pnr = stack.get(3);
//			
//			String prop_ref = (String)pr;
//			int pool_id = (int)pi;
//			int bod_id = (int)pbi;
//			String new_ref = (String)pnr;
//			
//			sPool<pParam> pool = space.param_pools.get(prop_ref);
//			if (pool == null) return;
//			pParam b = pool.getObjectAt(pool_id);
//			if (b == null) return;
//			if (PRINT_LOG) app.log("client pNet received change param "+b.pool_ref);
//
//			b.bodys[bod_id] = new_ref;
//			
//		} 
		else if (ref == CHG_BODY) {
			ArrayList<Object> stack = new ArrayList<Object>();
			byte_arr_to_obj_stack(data, stack);
			Object pi = stack.get(0);
			Object[] arr = new Object[stack.size() - 1];
			for (int i = 0 ; i < stack.size() - 1 ; i++) arr[i] = stack.get(i+1);
			int pool_id = (int)pi;
			
			pBody b = space.body_pool.getObjectAt(pool_id);
			if (b == null) return;
			if (PRINT_LOG) app.logn("client pNet received change body "+b.pool_ref);
			b.load_from_array(arr);
		} 
	}

	public void send_chg_body(pBody bod) {
		if (PRINT_LOG) app.logn("send chg body "+bod.pool_ref);
		Object[] arr = new Object[bod.data_size()];
		bod.to_array(arr);

		ArrayList<byte[]> stack = new ArrayList<byte[]>(); int cnt = 0;
		cnt += obj_to_stack(stack, bod.pool_index);
		for (Object o : arr) cnt += obj_to_stack(stack, o);
		send_stack(CHG_BODY, cnt, stack); 
	}
//	public void send_chg_param_data(pParam par, String ref) {
//		if (par.prop == null || par.prop.mode_localval) return;
//		ArrayList<byte[]> stack = new ArrayList<byte[]>(); int cnt = 0;
//		cnt += obj_to_stack(stack, par.prop.ref);
//		cnt += obj_to_stack(stack, par.pool_index);
//		Class<?> ct = par.prop.getDataValClass(ref);
//		cnt += obj_to_stack(stack, Applet.data_type_index.get(ct));
//		int val_id = par.prop.getDataValId(ref, ct);
//		cnt += obj_to_stack(stack, val_id);
//		cnt += obj_to_stack(stack, par.get(ref));
//		send_stack(CHG_PARAM_DATA, cnt, stack); 
//	}
//	public void send_chg_param_ref(pParam par, String ref) {
//		if (par.prop == null || par.prop.mode_localval) return;
//		ArrayList<byte[]> stack = new ArrayList<byte[]>(); int cnt = 0;
//		cnt += obj_to_stack(stack, par.prop.ref);
//		cnt += obj_to_stack(stack, par.pool_index);
//		cnt += obj_to_stack(stack, par.prop.getRefValId(ref));
//		cnt += obj_to_stack(stack, par.getRef(ref).pool_ref);
//		send_stack(CHG_PARAM_REF, cnt, stack); 
//	}
//	public void send_chg_param_body(pParam par, String ref) {
//		if (par.prop == null || par.prop.mode_localval) return;
//		ArrayList<byte[]> stack = new ArrayList<byte[]>(); int cnt = 0;
//		cnt += obj_to_stack(stack, par.prop.ref);
//		cnt += obj_to_stack(stack, par.pool_index);
//		cnt += obj_to_stack(stack, par.prop.getBodyValId(ref));
//		cnt += obj_to_stack(stack, par.getBody(ref).pool_ref);
//		send_stack(CHG_PARAM_BODY, cnt, stack); 
//	}
	public void send_chg_param(pParam par) {
		if (par.prop == null || par.prop.mode_localval) return;
		if (PRINT_LOG) app.logn("send chg param "+par.pool_ref);
		Object[] arr = new Object[par.data_size()];
		par.to_array(arr);

		ArrayList<byte[]> stack = new ArrayList<byte[]>(); int cnt = 0;
		cnt += obj_to_stack(stack, par.prop.ref);
		cnt += obj_to_stack(stack, par.pool_index);
		for (Object o : arr) cnt += obj_to_stack(stack, o);
		send_stack(CHG_PARAM, cnt, stack); 
	}
	public void send_chg_collec(pCollec col) {
		if (PRINT_LOG) app.logn("send chg collec "+col.pool_ref);
		if (col.prop == null || col.prop.mode_localval) return;
		Object[] arr = new Object[col.data_size()];
		col.to_array(arr);

		ArrayList<byte[]> stack = new ArrayList<byte[]>(); int cnt = 0;
		cnt += obj_to_stack(stack, col.pool_index);
//		cnt += obj_to_stack(stack, col.prop.ref);
//		cnt += obj_to_stack(stack, col.ref_in_prop);
		for (Object o : arr) cnt += obj_to_stack(stack, o);
		send_stack(CHG_COLLEC, cnt, stack); 
	}

	public void send_new_body(pBody bod) {
		if (PRINT_LOG) app.logn("send new body "+bod.pool_ref);
		Object[] arr = new Object[bod.data_size()];
		bod.to_array(arr);

		ArrayList<byte[]> stack = new ArrayList<byte[]>(); int cnt = 0;
		cnt += obj_to_stack(stack, bod.pool_index);
		for (Object o : arr) cnt += obj_to_stack(stack, o);
		send_stack(NEW_BODY, cnt, stack); 
	}
	public void send_new_param(pParam par) {
		if (PRINT_LOG) app.logn("send new param "+par.pool_ref);
		Object[] arr = new Object[par.data_size()];
		par.to_array(arr);

		ArrayList<byte[]> stack = new ArrayList<byte[]>(); int cnt = 0;
		cnt += obj_to_stack(stack, par.prop.ref);
		cnt += obj_to_stack(stack, par.pool_index);
		for (Object o : arr) cnt += obj_to_stack(stack, o);
		send_stack(NEW_PARAM, cnt, stack); 
	}
	public void send_new_collec(pCollec col) {
		if (PRINT_LOG) app.logn("send new collec "+col.pool_ref);
		Object[] arr = new Object[col.data_size()];
		col.to_array(arr);

		ArrayList<byte[]> stack = new ArrayList<byte[]>(); int cnt = 0;
		cnt += obj_to_stack(stack, col.prop.ref);
		cnt += obj_to_stack(stack, col.ref_in_prop);
		cnt += obj_to_stack(stack, col.pool_index);
		for (Object o : arr) cnt += obj_to_stack(stack, o);
		send_stack(NEW_COLLEC, cnt, stack); 
	}

	public void send_del_body(pBody bod) {
		if (PRINT_LOG) app.logn("send del body "+bod.pool_ref);
		ArrayList<byte[]> stack = new ArrayList<byte[]>(); int cnt = 0;
		cnt += obj_to_stack(stack, bod.pool_index);
		send_stack(DEL_BODY, cnt, stack); 
	}
	public void send_del_param(pParam par) {
		if (PRINT_LOG) app.logn("send del param "+par.pool_ref);
		ArrayList<byte[]> stack = new ArrayList<byte[]>(); int cnt = 0;
		cnt += obj_to_stack(stack, par.prop.ref);
		cnt += obj_to_stack(stack, par.pool_index);
		send_stack(DEL_PARAM, cnt, stack); 
	}
	public void send_del_collec(pCollec col) {
		if (PRINT_LOG) app.logn("send del collec "+col.pool_ref);
		ArrayList<byte[]> stack = new ArrayList<byte[]>(); int cnt = 0;
		cnt += obj_to_stack(stack, col.pool_index);
		send_stack(DEL_COLLEC, cnt, stack);
	}
	
	
	
	
	private int obj_to_stack(ArrayList<byte[]> stack, Object o) {
		int cnt = 0;
		byte tp = Applet.type_class_id.get(o.getClass());
		byte[] tpr = new byte[1]; tpr[0] = tp;
		byte[] bt = sData.getBytes(o);
		byte[] lon = sData.getBytes(bt.length);
		stack.add(tpr); stack.add(lon); stack.add(bt);
		cnt += 1 + lon.length + bt.length;
		return cnt;
	}
	private void send_stack(int ref, int l, ArrayList<byte[]> stack) {
		byte[] data = new byte[l]; int cnt = 0;
		for (byte[] br : stack) { 
			for (int i = 0 ; i < br.length ; i++) { data[cnt] = br[i]; cnt++; } }
		net.sendPacket(ref, data);
	}
	
}
