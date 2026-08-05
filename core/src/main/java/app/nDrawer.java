package app;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.Viewport;
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

import gui.nAlign;
import space.earlygrey.shapedrawer.JoinType;
import space.earlygrey.shapedrawer.ShapeDrawer;
import util.Utl;
import util.nTransform;

public class nDrawer {
	public boolean USE_FX = true;
	
	public boolean vfx;
	public final BitmapFont bitmapfont, bitmapfont_2y;
	public BitmapFont font;
	public PolygonSpriteBatch spritebatch;
	public Texture texture;
	public ShapeDrawer drawer;

	private VfxManager vfxManager;
	ArrayList<AbstractVfxEffect> effect = new ArrayList<AbstractVfxEffect>();
    private VfxFrameBuffer buffer;
//    public GdxApp app;
    public DrawContext context;
    
    public interface DrawContext {
		public nDrawer getDrawer();
		public Viewport getViewport();
    		public Rectangle getScreenRect();
    		public OrthographicCamera getCamera();
    }

	public void draw_begin() {
		transf.reset();
		ScreenUtils.clear(color_back);
		context.getViewport().apply(false);
//		app.viewport.apply(false);
		ready(); begin();
	}
	public void fx() { end(); vfx = USE_FX; begin(); }
	public void noFx() { end(); vfx = false; begin(); }

	public void pause_batch() { 
		end(); 
	}
	public void restart_batch() { 
		begin(); 
	}
	private BitmapFont makeFont(String s) {

		FreeTypeFontGenerator fontgenerator = new FreeTypeFontGenerator(
				Gdx.files.internal(s));
		FreeTypeFontParameter parameter = new FreeTypeFontParameter();
//		parameter.borderWidth = basetxtSize/20f;
//		parameter.borderColor = Color.BLACK; 
//		parameter.borderStraight = true;
		parameter.size = (int) basetxtSize;
		BitmapFont f = fontgenerator.generateFont(parameter);
		fontgenerator.dispose();
		//font has 15pt, but we need to scale it to our viewport by ratio of viewport height to screen height
		f.setUseIntegerPositions(false);
		//		bitmapfont.getData().setScale(viewport.getWorldHeight() / Gdx.graphics.getHeight());

		f.getRegion().getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
		return f;
	}
	
	public nDrawer(DrawContext a, boolean fx) {
		context = a; vfx = fx;

		spritebatch = new PolygonSpriteBatch();
		
		bitmapfont = makeFont("Mx437_IBM_BIOS.ttf");
		bitmapfont_2y = makeFont("Mx437_IBM_BIOS-2y.ttf");
		font = bitmapfont_2y;
		
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
    		buffer.initialize(GdxApp.WIDTH,GdxApp.HEIGHT);
    		vfxManager.getResultBuffer().addRenderer(batchRenderer); 
	}
	public Matrix4 getTransformMatrix() { return spritebatch.getTransformMatrix(); }
	public void dispose() {
		bitmapfont.dispose(); bitmapfont_2y.dispose();
		buffer.dispose();
		vfxManager.dispose();
		for (AbstractVfxEffect e : effect) e.dispose();
		spritebatch.dispose();
		texture.dispose();
	}
	public void resize(int w, int h) {
        vfxManager.resize(w, h); buffer.reset(); buffer.initialize(w, h); }
	public void flush() { spritebatch.flush(); }
	public void ready() { vfxManager.cleanUpBuffers(Utl.color(0, 0)); }
	public void begin() {
//		spritebatch.setProjectionMatrix(app.viewport.getCamera().combined);
		spritebatch.setProjectionMatrix(context.getViewport().getCamera().combined);
		drawer.updatePixelSize(); 
		if (vfx) {
//			vfxManager.rebind();
//			vfxManager.update(1); 
	        vfxManager.beginInputCapture();
			ScreenUtils.clear(buffer_clear_color);
	        vfxManager.endInputCapture();        
	        vfxManager.renderToFbo(buffer);
	        vfxManager.cleanUpBuffers(Utl.color(0,0));
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
					(int)context.getScreenRect().width, 
					(int)context.getScreenRect().height, 
					0, 0, 1, 1);
			spritebatch.end();
		}
	}
	public void draw_end() {
		end();

		// cancel all drawing transforms
		transf.reset();
		
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
	
	
	
	public nTransform transf = new nTransform();
	public void push() { transf.push(); }
	public void pop() { transf.pop(); }
	public void transf(nTransform t) { transf.transf(t); }
	public void translate(float x, float y) { transf.translate(x, y); }
	public void translate(Vector2 v) { transf.translate(v.x, v.y); }
	public void scale(float s) { transf.scale(s); }
	public void rotate(float s) { transf.rotate(s); }
	
	
	
	
	
	public static final char[] Alphabet = {'0','1','2','3','4','5','6','7','8','9',
			'A','B','C','D','E','F','G','H','I','J','K','L','M',
			'N','O','P','Q','R','S','T','U','V','W','X','Y','Z',
			'a','b','c','d','e','f','g','h','i','j','k','l','m',
			'n','o','p','q','r','s','t','u','v','w','x','y','z'};

	private nAlign textAlignmentX = nAlign.CENTER;
	private nAlign textAlignmentY = nAlign.CENTER;
	private float txtSize = basetxtSize;
	private float txtSizeTransf = basetxtSize;
	private static final float basetxtSize = 64;
	// to redo correctly
	public float txtCharSize = txtSize/2f;
	// 	game.font.getBounds(t.subSequence(0,t.length()-1));
	public void setLargeFont() { font = bitmapfont; }
	public void setDefaultFont() { font = bitmapfont_2y; }
	public float textWidth(String t) { 
		if (font == bitmapfont_2y) return txtCharSize * t.length(); 
		else return txtCharSize * t.length() * 1.9f; }
	public float textWidth(char t) { 
		if (font == bitmapfont_2y) return txtCharSize; else return txtCharSize*1.9f; }
	public float textHeight() { return font.getLineHeight(); }
	public nDrawer textAlign(nAlign ax, nAlign ay) {
		textAlignmentX = ax;
		textAlignmentY = ay;
		return this;
	}
	public nDrawer text(String t, Vector2 v, float s) {
		return text(t,v.x,v.y,s,Color.WHITE); }
	public nDrawer text(String t, Vector2 v, float s, Color c) {
		return text(t,v.x,v.y,s,c); }
	public nDrawer text(String t, float x, float y, float s) {
		return text(t,x,y,s,Color.WHITE); }
	public nDrawer text(String t, float x, float y, float s, Color c) {
		txtSizeTransf = s * transf.getScale();
		txtSize = txtSizeTransf;
		txtCharSize = txtSizeTransf/2f;
		float f = txtSize/basetxtSize;
		font.getData().setScale(f);
		float alignmentOffsetX = 0;
		float alignmentOffsetY = 0;
		if (textAlignmentX == nAlign.CENTER) 
			alignmentOffsetX = -textWidth(t) / (2.0f * transf.getScale());
		else if (textAlignmentX == nAlign.RIGHT) 
			alignmentOffsetX = -textWidth(t) / transf.getScale();
		if (textAlignmentY == nAlign.CENTER) 
			alignmentOffsetY = font.getLineHeight() / (2.0f * transf.getScale());
		else if (textAlignmentY == nAlign.BOTTOM) 
			alignmentOffsetY = font.getLineHeight() / transf.getScale();
		x += alignmentOffsetX;
		y += alignmentOffsetY;
		Vector2 p = transf.transform(x, y);
		font.setColor(c);
		font.draw(spritebatch, t, p.x, p.y);
		return this;
	}
	
	
	

	public nDrawer rect(Rectangle n) {
		Rectangle r = transf.transform(n);
		if (do_fill) drawer.filledRectangle(r.x, r.y, r.width, r.height, color_fill);
		if (do_stroke) drawer.rectangle(r.x, r.y, r.width, r.height, color_stroke, strokeW * transf.getScale()); 
		return this; }
	public nDrawer rect(float x, float y, float w, float h) {
		rect(new Rectangle(x,y,w,h)); return this; }
	
	public nDrawer circle(Rectangle r) {
		r = transf.transform(r);
		circle(r.x+r.width/2f, r.y+r.height/2f, Math.min(r.width, r.height)/2f); return this; }
	public nDrawer circle(float x, float y, float r) {
		Circle c = transf.transform(new Circle(x,y,r));
		if (do_fill) drawer.filledEllipse(c.x, c.y, c.radius, c.radius, 0, color_fill, color_fill);
		if (do_stroke) { drawer.setColor(color_stroke); drawer.circle(c.x, c.y, c.radius, strokeW * transf.getScale()); } 
		return this; }
	
	public nDrawer line(Vector2 p1, Vector2 p2) {
		line(p1.x, p1.y, p2.x, p2.y); return this; }
	public nDrawer line(float x1, float y1, float x2, float y2) {
		Vector2 v1 = transf.transform(x1,y1);
		Vector2 v2 = transf.transform(x2,y2);
		float s = strokeW * transf.getScale();
		if (s > 1) drawer.line(v1.x, v1.y, v2.x, v2.y, color_stroke, s);
		else {
			Color c = new Color(color_stroke);
			c.a = c.a * s;
			drawer.line(v1.x, v1.y, v2.x, v2.y, c, 1); }
		return this; }
	
	public nDrawer polygon(Polygon p) {
		float[] v = p.getTransformedVertices(); polygon(v); return this; }
	public nDrawer polygon(Vector2 v0, Vector2 v1, Vector2 v2) {
		Vector2[] v = new Vector2[3]; v[0] = v0; v[1] = v1; v[2] = v2; polygon(v); return this; }
	public nDrawer polygon(Vector2 v0, Vector2 v1, Vector2 v2, Vector2 v3) {
		Vector2[] v = new Vector2[4]; v[0] = v0; v[1] = v1; v[2] = v2; v[3] = v3; polygon(v); return this; }
	public nDrawer polygon(Vector2[] v) {
		float[] f = new float[v.length * 2];
		for (int i = 0 ; i < v.length ; i++) { f[i*2] = v[i].x; f[(i*2)+1] = v[i].y; }
		polygon(f); return this; }
	public nDrawer polygon(float[] v) {
		for (int i = 0 ; i < v.length ; i += 2) {
			Vector2 p = transf.transform(v[i], v[i+1]); v[i] = p.x; v[i+1] = p.y; }
		if (do_fill) { drawer.setColor(color_fill); drawer.filledPolygon(v); }
		if (do_stroke) { 
			drawer.setColor(color_stroke); 
			drawer.polygon(v, strokeW * transf.getScale(), JoinType.SMOOTH); } 
		return this; }
	
	public nDrawer diamond(Rectangle r) {
		float[] v = new float[8];
		v[0] = r.x; v[1] = r.y + r.height / 2f;
		v[2] = r.x + r.width / 2f; v[3] = r.y + r.height; 
		v[4] = r.x + r.width; v[5] = r.y + r.height / 2f;
		v[6] = r.x + r.width / 2f; v[7] = r.y;
		polygon(v);
		return this; }
	
	public Color buffer_clear_color = Color.WHITE;
	public Color color_back = new Color(40);
	Color color_fill = new Color();
	Color color_stroke = new Color();
	private float strokeW = 2;
	private boolean do_fill = true, do_stroke = false;
	
	public nDrawer fill(Color c) {
		color_fill.set(c); do_fill = true; return this; }
	public nDrawer fill(int c) {
		color_fill.set(Utl.color(c)); do_fill = true; return this; }
	public nDrawer fill(int l, int a) {
		color_fill.set(Utl.color(l,a)); do_fill = true; return this; }
	public nDrawer fill(int r, int g, int b) {
		color_fill.set(Utl.color(r,g,b)); do_fill = true; return this; }
	public nDrawer fill(int r, int g, int b, int a) {
		color_fill.set(Utl.color(r,g,b,a)); do_fill = true; return this; }

	public nDrawer stroke(Color c) {
		color_stroke.set(c); do_stroke = true; return this; }
	public nDrawer stroke(Color c, float w) {
		color_stroke.set(c); strokeW = w; do_stroke = true; return this; }
	public nDrawer stroke(int c) {
		color_stroke.set(Utl.color(c)); do_stroke = true; return this; }
	public nDrawer stroke(int l, float w) {
		color_stroke.set(Utl.color(l,l,l)); strokeW = w; do_stroke = true; return this; }
	public nDrawer stroke(int r, int g, int b) {
		color_stroke.set(Utl.color(r,g,b)); do_stroke = true; return this; }
	public nDrawer stroke(int r, int g, int b, int a) {
		color_stroke.set(Utl.color(r,g,b, a)); do_stroke = true; return this; }
	public nDrawer stroke(int r, int g, int b, int a, float w) {
		color_stroke.set(Utl.color(r,g,b)); strokeW = w; do_stroke = true; return this; }
	
	public nDrawer noFill() { do_fill = false; return this; }
	public nDrawer noStroke() { do_stroke = false; return this; }
	public void strokeWeight(float strokeW) { this.strokeW = strokeW; }

	public static final float point_size = 1f;
	public nDrawer point(Vector2 v) { return point(v.x,v.y); }
	public nDrawer point(float x, float y) {
		fill(color_fill); noStroke(); circle(x,y,point_size); return this; }
	public nDrawer point(float x, float y, Color c) {
		fill(c); noStroke(); circle(x,y,point_size); return this; }

	
	
	public interface Drawer {


		public void flush();
		
		public void fx();
		public void noFx();

		public Matrix4 getTransformMatrix();
		
		public void push();  
		public void pop();  
		public void transf(nTransform t);  
		public void translate(float x, float y);  
		public void translate(Vector2 v);  
		public void scale(float s);  
		public void rotate(float s);  
		
		public float textWidth(String t);  
		public float textWidth(char t);  
		public float textHeight();  
		public void textAlign(nAlign ax, nAlign ay); 
		public void text(String t, Vector2 v, float s); 
		public void text(String t, Vector2 v, float s, Color c); 
		public void text(String t, float x, float y, float s); 
		public void text(String t, float x, float y, float s, Color c); 
		
		public void rect(Rectangle n); 
		public void rect(float x, float y, float w, float h); 
		
		public void circle(Rectangle r); 
		public void circle(float x, float y, float r); 
		
		public void line(Vector2 p1, Vector2 p2); 
		public void line(float x1, float y1, float x2, float y2); 
		
		public void polygon(Polygon p); 
		public void polygon(Vector2 v0, Vector2 v1, Vector2 v2); 
		public void polygon(Vector2 v0, Vector2 v1, Vector2 v2, Vector2 v3); 
		public void polygon(Vector2[] v); 
		public void polygon(float[] v); 
		
		public void diamond(Rectangle r); 
		
		public void fill(Color c); 
		public void fill(int c); 
		public void fill(int l, int a); 
		public void fill(int r, int g, int b); 
		public void fill(int r, int g, int b, int a); 

		public void stroke(Color c); 
		public void stroke(Color c, float w); 
		public void stroke(int c); 
		public void stroke(int l, float w); 
		public void stroke(int r, int g, int b); 
		public void stroke(int r, int g, int b, int a); 
		public void stroke(int r, int g, int b, int a, float w); 
		
		public void noFill();  
		public void noStroke();  
		public void strokeWeight(float strokeW);  

		public void point(Vector2 v);  
		public void point(float x, float y); 
		public void point(float x, float y, Color c);

	}
}
