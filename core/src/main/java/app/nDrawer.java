package app;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.crashinvaders.vfx.VfxManager;
import com.crashinvaders.vfx.effects.AbstractVfxEffect;
import com.crashinvaders.vfx.effects.BloomEffect;
import com.crashinvaders.vfx.effects.GaussianBlurEffect;
import com.crashinvaders.vfx.effects.LevelsEffect;
import com.crashinvaders.vfx.effects.MotionBlurEffect;
import com.crashinvaders.vfx.effects.ShaderVfxEffect;
import com.crashinvaders.vfx.effects.VignettingEffect;
import com.crashinvaders.vfx.effects.util.MixEffect;
import com.crashinvaders.vfx.framebuffer.VfxFrameBuffer;
import com.crashinvaders.vfx.framebuffer.VfxFrameBuffer.Renderer;
import com.crashinvaders.vfx.framebuffer.VfxFrameBuffer.RendererAdapter;

import space.earlygrey.shapedrawer.ShapeDrawer;

public class nDrawer {
	public boolean vfx;
	public PolygonSpriteBatch spritebatch;
	public Texture texture;
	public ShapeDrawer drawer;

	private VfxManager vfxManager;
//    private GaussianBlurEffect vfxEffect;
//    private BloomEffect vfxEffect2;
	ArrayList<AbstractVfxEffect> effect = new ArrayList<AbstractVfxEffect>();
    private VfxFrameBuffer buffer;
    public GDXApplet app;

	public void draw_begin() {
//		ScreenUtils.clear(Color.BLACK);
		ScreenUtils.clear(app.color_back);
		app.viewport.apply(false);
		ready(); begin();
	}
	public void fx() { end(); vfx = true; begin(); }
	public void noFx() { end(); vfx = false; begin(); }

	public void pause_batch() { 
		end(); 
//		vfx = false; 
//		begin(); 
	}
	public void restart_batch() { 
//		end(); 
//		vfx = false; 
		begin(); 
	}
	
	public nDrawer(GDXApplet gdxApplet, boolean fx) {
		app = gdxApplet; vfx = fx;

		spritebatch = new PolygonSpriteBatch();
		
		Pixmap pixmap = new Pixmap(1, 1, Format.RGBA8888);
		pixmap.setColor(Color.WHITE);
		pixmap.drawPixel(0, 0);
		texture = new Texture(pixmap);
		pixmap.dispose();
		TextureRegion region = new TextureRegion(texture, 0, 0, 1, 1);
		drawer = new ShapeDrawer(spritebatch, region);
        vfxManager = new VfxManager(Pixmap.Format.RGBA8888);	
        	vfxManager.setBlendingEnabled(true);
        
//		GaussianBlurEffect e1 = new GaussianBlurEffect();
//		vfxManager.addEffect(e1); effect.add(e1);
        	
//		BloomEffect e2 = new BloomEffect();
//		vfxManager.addEffect(e2); effect.add(e2);
////		e2.setBaseIntensity(1f);
////		e2.setBaseSaturation(.85f);
//		e2.setBloomIntensity(1.1f);
//		e2.setBloomSaturation(.85f);
//		e2.setBlurPasses(10);
//		e2.setBlurAmount(1.1f);
////		e2.setThreshold(.85f);
		
//		LevelsEffect e3 = new LevelsEffect();
//		vfxManager.addEffect(e3); effect.add(e3);
//		e3.setBrightness(0.0f);
//		e3.setContrast(1.0f);
//		e3.setSaturation(1.0f);
//		e3.setHue(1.0f);
//		e3.setGamma(1.0f);
		
//		MotionBlurEffect e4 = new MotionBlurEffect(Pixmap.Format.RGBA8888, 
//				MixEffect.Method.MIX, 1.0f);
//		vfxManager.addEffect(e4); effect.add(e4);
		
//		VignettingEffect e5 = new VignettingEffect(false);
//		vfxManager.addEffect(e5); effect.add(e5);
//		e5.setSaturation(0.5f);
//		e5.setCoords(0.5f,0.5f);

    		buffer = new VfxFrameBuffer(Pixmap.Format.RGBA8888);		        
		Renderer batchRenderer = new PolygonSpriteBatchRendererAdapter(spritebatch);
        buffer.addRenderer(batchRenderer);
    		buffer.initialize(Applet.WIDTH,Applet.HEIGHT);
    		vfxManager.getResultBuffer().addRenderer(batchRenderer); 
	}
	public Matrix4 getTransformMatrix() { return spritebatch.getTransformMatrix(); }
	public void dispose() {
		buffer.dispose();
		vfxManager.dispose();
		for (AbstractVfxEffect e : effect) e.dispose();
		spritebatch.dispose();
		texture.dispose();
	}
	public void resize(int w, int h) {
        vfxManager.resize(w, h); buffer.reset(); buffer.initialize(w, h); }
	public void flush() { spritebatch.flush(); }
	public void ready() { vfxManager.cleanUpBuffers(app.color(0, 0)); }
	public void begin() {
		spritebatch.setProjectionMatrix(app.viewport.getCamera().combined);
		drawer.updatePixelSize(); 
		if (vfx) {
//			vfxManager.rebind();
//			vfxManager.update(1); 
	        vfxManager.beginInputCapture();
			ScreenUtils.clear(app.buffer_clear_color);
	        vfxManager.endInputCapture();        
	        vfxManager.renderToFbo(buffer);
	        vfxManager.cleanUpBuffers(app.color(0,0));
	        vfxManager.beginInputCapture();
		}
		
		//batch begin
		spritebatch.begin();
	}
	public void end() {
		//batch end
		spritebatch.end();
		if (vfx) {
	        vfxManager.endInputCapture();
	        vfxManager.applyEffects();		        
	        vfxManager.renderToFbo(buffer);
	        spritebatch.begin();
			spritebatch.draw(buffer.getTexture(), 0, 0, 
					(int)app.screenrect.width, (int)app.screenrect.height, 
					0, 0, 1, 1);
			spritebatch.end();
		}
	}

	public static class PolygonSpriteBatchRendererAdapter 
			extends RendererAdapter implements Poolable {
		private PolygonSpriteBatch batch;

		public PolygonSpriteBatchRendererAdapter() { }
		public PolygonSpriteBatchRendererAdapter(PolygonSpriteBatch batch) {
			initialize(batch); }

		public PolygonSpriteBatchRendererAdapter initialize(PolygonSpriteBatch batch) {
			this.batch = batch; return this; }

		@Override public void reset() { batch = null; }
		public PolygonSpriteBatch getBatch() { return batch; }
		@Override public void flush() { batch.isDrawing(); { batch.flush(); } }
		@Override protected Matrix4 getProjection() {
			return batch.getProjectionMatrix(); }
		@Override protected Matrix4 getTransform() {
			return batch.getTransformMatrix(); }
		@Override protected void setProjection(Matrix4 projection) {
			batch.setProjectionMatrix(projection); }
		@Override protected void setTransform(Matrix4 transform) {
			batch.setTransformMatrix(transform); }
	}
}
