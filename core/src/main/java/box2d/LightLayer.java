package box2d;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.Array;

import util.Utl;

public class LightLayer extends nRenderer.Layer {

	RayHandler rayHandler;

	public final Array<Light> lightList = new Array<Light>(false, 16);
	
	public ArrayList<Body> transparent = new ArrayList<Body>();

	public ArrayList<Body> light_blocker = new ArrayList<Body>();
	public boolean use_blocker = false;
	
	public enum MODE { DEFAULT, LIGHT, AURA, VISION, COLOR }
	
	public MODE mode = MODE.DEFAULT;
	
	public boolean active = true;
	
	public void prepareRender() {
		use_blocker = false;
		if (mode == MODE.DEFAULT) {
//			rayHandler.setBlendDef();
			active = false;
		} else if (mode == MODE.LIGHT) {
			rayHandler.setBlendLight();
			active = rend.box.drawlight();
		} else if (mode == MODE.AURA) {
			rayHandler.setBlendAura(); 
			active = rend.box.drawaura();
		} else if (mode == MODE.VISION) {
			use_blocker = true;
			rayHandler.setBlendVision(); 
			active = rend.box.drawvision();
		} else if (mode == MODE.COLOR) {
			rayHandler.setBlendColor(); 
			active = rend.box.drawcolor();
		} 
	}

	public LightLayer(nRenderer tm) { this(tm,0); }
	public LightLayer(nRenderer tm, int p) {
		super(tm,p);
		this.rayHandler = tm.rayHandler;
		rayHandler.layerList.add(this);
	}

	public LightLayer(nRenderer tm, MODE m) { this(tm,m,0); }
	public LightLayer(nRenderer tm, MODE m, int p) {
		this(tm,p);
		mode = m;
	}

	public void loadMapObject(MapProperties prop) {
		if (Utl.getBoo(prop,"pointlight")) {
			float dist = prop.get("dist", Float.class);
			Color col = prop.get("color", Color.class);
			Vector2 pos = rend.tileLayer.mapToSpace(prop.get("x", Float.class), 
					prop.get("y", Float.class));
			PointLight pl = newPointLight(col, dist, pos.x, pos.y);
		}
		if (Utl.getBoo(prop,"conelight")) {
			float dir = prop.get("dir", Float.class); // 0 = 0deg, 0.5 = 180deg
			float cone = prop.get("cone", Float.class); // 0 = 0deg, 0.5 = 180deg
			float dist = prop.get("dist", Float.class);
			Color col = prop.get("color", Color.class);
			Vector2 pos = rend.tileLayer.mapToSpace(prop.get("x", Float.class), 
					prop.get("y", Float.class));
			ConeLight cl = newConeLight(col, dist, pos.x, pos.y, 
					dir * 360f, cone * 360f);
			cl.setSoft(false);
		}
	}
	public void loadMapLayer(MapLayer ml) {
		for (MapObject m : ml.getObjects()) {
			loadMapObject(m.getProperties());
		}
	}
	
	@Override
	public void render() {
		rayHandler.renderLayer(this);
	}

	public PointLight newPointLight(Color col, float dist, float x, float y) {
		return new PointLight(this, 
				(int)(Math.PI * 2f * dist * RayHandler.LIGHT_DEG_SIZE / 360f), 
				col, dist, x, y); }
	public PointLight newPointLight(int ray, Color col, float dist, float x, float y) {
		return new PointLight(this, ray, col, dist, x, y); }

	public ConeLight newConeLight(Color col, float dist, float x, float y, float dir, float cone) {
		return new ConeLight(this, 
				(int)(Math.PI * 2f * dist * (cone / 360f) * RayHandler.LIGHT_DEG_SIZE / 360f), 
				col, dist, x, y, dir, cone); }
	public ConeLight newConeLight(int ray, Color col, float dist, float x, float y, float dir, float cone) {
		return new ConeLight(this, ray, col, dist, x, y, dir, cone); }

	public RectLight newRectLight(Color col, float x, float y, float w, float h) {
		return new RectLight(this, (int)(w / RayHandler.LIGHT_PIX_SIZE / 2f), 
				col, x, y, w, h); } 
	public RectLight newRectLight(int ray, Color col, float x, float y, float w, float h) {
		return new RectLight(this, ray, col, x, y, w, h); } 
	public RectLight newRectLight(Color col, float x, float y, float w, float h, float d) {
		return new RectLight(this, (int)(w / RayHandler.LIGHT_PIX_SIZE / 2f), 
				col, x, y, w, h, d); } 
	public RectLight newRectLight(int ray, Color col, float x, float y, float w, float h, float d) {
		return new RectLight(this, ray, col, x, y, w, h, d); } 

	public void newCrossAmbiantLight(Color col, TileLayer tl) {
		newCrossAmbiantLight(col, tl.getWidth(), tl.getHeight(), tl.getTileWidth()); }
	public void newCrossAmbiantLight(Color col, float mapwidth, float mapheight, float wallsize) {
		
		int rayw = (int)(mapwidth / (float)RayHandler.LIGHT_AMB_DIV);
		int rayh = (int)(mapheight / (float)RayHandler.LIGHT_AMB_DIV);
		float soft = wallsize / 2f;
		
		float dr = 0f;
		Vector2 vp = new Vector2(mapwidth, mapheight);
		RectLight rl = newRectLight(rayh, new Color(1f,1f,1f,1f), 
				-vp.x / 2f, -vp.y / 2f, mapwidth, mapheight, dr);
		rl.setSoftnessLength(soft);
		dr += 90f; vp.rotateRad((float)Math.PI / 2f);
		rl = newRectLight(rayw, new Color(1f,1f,1f,1f), 
				-vp.x / 2f, -vp.y / 2f, mapwidth, mapheight, dr);
		rl.setSoftnessLength(soft);
		dr += 90f; vp.rotateRad((float)Math.PI / 2f);
		rl = newRectLight(rayh, new Color(1f,1f,1f,1f), 
				-vp.x / 2f, -vp.y / 2f, mapwidth, mapheight, dr);
		rl.setSoftnessLength(soft);
		dr += 90f; vp.rotateRad((float)Math.PI / 2f);
		rl = newRectLight(rayw, new Color(1f,1f,1f,1f), 
				-vp.x / 2f, -vp.y / 2f, mapwidth, mapheight, dr);
		rl.setSoftnessLength(soft);

	} 

	public void newVisionLight(Body body) {
		transparent.add(body);
		ConeLight cl = newConeLight(720, new Color(1f,1f,1f,1f), 
				15000f, 0f, 0f, 0f, 190f);
		cl.setSoftnessLength(500);
		cl.setIgnoreAttachedBody(true);
		rend.box.attachToBody(cl, body);
		cl = newConeLight(720, new Color(1f,1f,1f,1f), 15000f, 0f, 0f, 180f, 190f);
		cl.setSoftnessLength(500);
		cl.setIgnoreAttachedBody(true);
		rend.box.attachToBody(cl, body);
	}
	
}
