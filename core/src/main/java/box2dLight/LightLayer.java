package box2dLight;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.Array;

import aa_nodulo.nRenderer;

public class LightLayer extends nRenderer.Layer {

	RayHandler rayHandler;

	public final Array<Light> lightList = new Array<Light>(false, 16);

	public MapLayer maplayer;

	public ArrayList<Body> transparent = new ArrayList<Body>();

	public ArrayList<Body> light_blocker = new ArrayList<Body>();
	public boolean use_blocker = false;
	
	public enum MODE { DEFAULT, LIGHT, AURA, VISION, COLOR }
	
	public MODE mode = MODE.DEFAULT;
	
	public boolean active = true;
	
	public void prepareRender() {
		use_blocker = false;
		if (mode == MODE.DEFAULT) {
			rayHandler.setBlendDef();
			active = false;
		} else if (mode == MODE.LIGHT) {
			rayHandler.setBlendLight();
			active = true;
		} else if (mode == MODE.AURA) {
			rayHandler.setBlendAura(); 
			active = true;
		} else if (mode == MODE.VISION) {
			rayHandler.setBlendVision(); 
			active = rend.box.drawvision();
			use_blocker = true;
		} else if (mode == MODE.COLOR) {
			rayHandler.setBlendColor(); 
			active = true;
		} 
	}

	public LightLayer(nRenderer tm, MODE m) {
		super(tm);
		this.rayHandler = tm.rayHandler;
		rayHandler.layerList.add(this);
		mode = m;
	}

	public LightLayer(nRenderer tm, MapLayer ml) {
		super(tm);
		this.rayHandler = tm.rayHandler;
		rayHandler.layerList.add(this);
		loadMapLayer(ml);
	}

	public void loadMapLayer(MapLayer ml) {
		maplayer = ml;

		maplayer.getProperties().put("lightlayer", this);
		
		if (maplayer.getProperties().get("mode", Integer.class) != null) {
			int mo = maplayer.getProperties().get("mode", Integer.class);
			int cnt = 0;
			for (MODE md : MODE.values()) { 
				if (cnt == mo) { mode = md; break; } cnt++; }
		}
		if (mode == MODE.LIGHT) {
//			new DirectionalLight(this,360,new Color(1f,1f,1f,1f), 320f);
		}
		for (MapObject m : ml.getObjects()) {
			if (m.getProperties().get("pointlight", Boolean.class) != null && 
					m.getProperties().get("pointlight", Boolean.class)) {
				MapProperties prop = m.getProperties();
				int ray = prop.get("ray", Integer.class);
				float dist = prop.get("dist", Float.class);
				Color col = prop.get("color", Color.class);
				Vector2 pos = rend.mapToSpace(prop.get("x", Float.class), 
						prop.get("y", Float.class));
				PointLight pl = new PointLight(this, ray, col, dist, pos.x, pos.y);
//				pl.setSoft(true);
//				pl.setSoftnessLength(2.5f);
			}
			if (m.getProperties().get("conelight", Boolean.class) != null && 
					m.getProperties().get("conelight", Boolean.class)) {
				MapProperties prop = m.getProperties();
				int ray = prop.get("ray", Integer.class);
				float dir = prop.get("dir", Float.class); // 0 = 0deg, 0.5 = 180deg
				float cone = prop.get("cone", Float.class); // 0 = 0deg, 0.5 = 180deg
				float dist = prop.get("dist", Float.class);
				Color col = prop.get("color", Color.class);
				Vector2 pos = rend.mapToSpace(prop.get("x", Float.class), 
						prop.get("y", Float.class));
				ConeLight cl = new ConeLight(this, ray, col, dist, 
						pos.x, pos.y, dir * 360f, cone * 360f);
				cl.setSoft(false);
			}
		}
	}
	public MapObjects getObjects() {
		return maplayer.getObjects();
	}

	@Override
	public void render() {
		rayHandler.renderLayer(this);
	}
	
	public PointLight newPointLight(int ray, Color col, float dist, float x, float y) {
		return new PointLight(this, ray, col, dist, x, y);
	}

	public void newVisionLight(Body body) {
		ConeLight cl = new ConeLight(this, 720, 
				new Color(1f,1f,1f,1f), 15000f, -1f, 0f, 0f, 180f);
		cl.setSoftnessLength(500);
		rend.box.attachToBody(cl, body);
		cl = new ConeLight(this, 720, 
				new Color(1f,1f,1f,1f), 15000f, 1f, 0f, 180f, 180f);
		cl.setSoftnessLength(500);
		rend.box.attachToBody(cl, body);
	}
	
}
