package box2dLight;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import aa_nodulo.nTileMap;

public class LightLayer {

	RayHandler rayHandler;

	public final Array<Light> lightList = new Array<Light>(false, 16);

	public MapLayer maplayer;
	
	public nTileMap map;
	
	public enum MODE { DEFAULT, GROUND, SPACE, VIEW }
	
	public MODE mode = MODE.DEFAULT;
	
	public boolean active = true;
	
	public void prepareRender() {
		if (mode == MODE.DEFAULT) {
			rayHandler.setBlendDef();
			active = false;
		} else if (mode == MODE.GROUND) {
			rayHandler.setBlendGround();
			active = true;
		} else if (mode == MODE.SPACE) {
			rayHandler.setBlendSpace(); 
			active = true;
		} else if (mode == MODE.VIEW) {
			rayHandler.setBlendView(); 
			active = map.box.drawviewfilter();
		}
	}
	
	public LightLayer(nTileMap tm, MapLayer ml) {
		super();
		map = tm;
		maplayer = ml;
		this.rayHandler = tm.rayHandler;
		rayHandler.layerList.add(this);

		maplayer.getProperties().put("lightlayer", this);
		
		if (maplayer.getProperties().get("mode", Integer.class) != null) {
			int mo = maplayer.getProperties().get("mode", Integer.class);
			int cnt = 0;
			for (MODE md : MODE.values()) { 
				if (cnt == mo) { mode = md; break; } cnt++; }
		}
		for (MapObject m : ml.getObjects()) {
			if (m.getProperties().get("pointlight", Boolean.class) != null && 
					m.getProperties().get("pointlight", Boolean.class)) {
				
				MapProperties prop = m.getProperties();
				
				int ray = prop.get("ray", Integer.class);
				float dist = prop.get("dist", Float.class);
				Color col = prop.get("color", Color.class);
				Vector2 pos = map.mapToSpace(prop.get("x", Float.class), 
						prop.get("y", Float.class));
				new PointLight(this, ray, col, dist, pos.x, pos.y);
				
//				Iterator<String> iter = m.getProperties().getKeys();
//				while (iter.hasNext()) {
//					String k = iter.next();
//					Utl.logn(k+" "+prop.get(k));
//				}
				
			}
		}
	}

	public MapObjects getObjects() {
		return maplayer.getObjects();
	}

}
