package box2d;

import static com.badlogic.gdx.graphics.GL20.GL_FRAMEBUFFER_BINDING;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.BufferUtils;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.ReflectionException;

/**
 * Wraps {@link FrameBuffer} and manages currently bound OpenGL FBO.
 * <p>
 * This implementation supports nested frame buffer drawing approach.
 * You can use multiple instances of this class to draw into one frame buffer while you drawing into another one,
 * the OpenGL state will be managed properly.
 * <br>
 * Here's an example:
 * <pre>
 * FboWrapper buffer0, buffer1;
 * // ...
 * void render() {
 *      // Any drawing here will be performed directly to the screen.
 *      buffer0.begin();
 *      // Any drawing here will be performed into buffer0's FBO.
 *      buffer1.begin();
 *      // Any drawing here will be performed into buffer1's FBO.
 *      buffer1.end();
 *      // Any drawing here will be performed into buffer0's FBO.
 *      buffer0.end();
 *      // Any drawing here will be performed directly to the screen.
 * }
 * </pre>
 * <p>
 * {@link VfxFrameBuffer} internally switches GL viewport between {@link #begin()} and {@link #end()}.
 * <br>
 * If you use any kind of batch renders (e.g. {@link Batch} or {@link ShapeRenderer}),
 * you should update their transform and projection matrices to setup viewport to the target frame buffer's size.
 * You can do so by registering {@link Renderer} using {@link #addRenderer(Renderer)} and {@link #removeRenderer(Renderer)}.
 * The registered renderers will automatically switch their matrices back and forth respectively upon {@link #begin()} and {@link #end()} calls.
 * They will also be flushed in the right time.
 * <p>
 * <b>NOTE:</b> Depth and stencil buffers are not supported.
 *
 * @author metaphore
 */
public class VfxFrameBuffer implements Disposable {
	
	public interface VfxGlExtension {
	    int getBoundFboHandle();
	}
	
	
	
    /** Current depth of buffer nesting rendering (keeps track of how many buffers are currently activated). */
    private static int bufferNesting = 0;
    /** @see #bufferNesting */
    public static int getBufferNesting() { return bufferNesting; }

    private static final OrthographicCamera tmpCam = new OrthographicCamera();
    private static final Matrix4 zeroTransform = new Matrix4();

    private final Matrix4 localProjection = new Matrix4();
    private final Matrix4 localTransform = new Matrix4();

    private final RendererManager renderers = new RendererManager();

    private final VfxGlViewport preservedViewport = new VfxGlViewport();
    private final Pixmap.Format pixelFormat;    //TODO Shall be non-final and become a parameter of #initialize().
    private int previousFboHandle;

    private FrameBuffer fbo = null;
    private boolean initialized;
    private boolean drawing;

    public VfxFrameBuffer(Pixmap.Format pixelFormat) {
        this.pixelFormat = pixelFormat;
    }

    @Override
    public void dispose() {
        reset();
    }

    public void initialize(int width, int height) {
        if (initialized) { dispose(); }

        initialized = true;

        int boundFboHandle = getBoundFboHandle();
        fbo = new FrameBuffer(pixelFormat, width, height, false);
        fbo.getColorBufferTexture().setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        Gdx.gl20.glBindFramebuffer(GL20.GL_FRAMEBUFFER, boundFboHandle);

        OrthographicCamera cam = tmpCam;
        cam.setToOrtho(false, width, height);
        localProjection.set(cam.combined);
        localTransform.set(zeroTransform);
    }

    public void reset() {
        if (!initialized) return;

        initialized = false;

        fbo.dispose();
        fbo = null;
    }

    public FrameBuffer getFbo() {
        return fbo;
    }

    public Texture getTexture() {
        return fbo == null ? null : fbo.getColorBufferTexture();
    }

    public Pixmap.Format getPixelFormat() {
        return pixelFormat;
    }

    public boolean isInitialized() {
        return initialized;
    }

    /** @return true means {@link VfxFrameBuffer#begin()} has been called */
    public boolean isDrawing() {
        return drawing;
    }

    public void addRenderer(Renderer renderer) {
        renderers.addRenderer(renderer);
    }

    public void removeRenderer(Renderer renderer) {
        renderers.removeRenderer(renderer);
    }

    public void clearRenderers() {
        renderers.clearRenderers();
    }

    public void setProjectionMatrix(Matrix4 matrix) {
        localProjection.set(matrix);
    }

    public void setTransformMatrix(Matrix4 matrix) {
        localTransform.set(matrix);
    }

    public Matrix4 getProjectionMatrix() {
        return localProjection;
    }

    public Matrix4 getTransformMatrix() {
        return localTransform;
    }

    public void begin() {
        bufferNesting++;

        if (!initialized) throw new IllegalStateException("VfxFrameBuffer must be initialized first");
        if (drawing) throw new IllegalStateException("Already drawing");

        drawing = true;

        renderers.flush();
        previousFboHandle = getBoundFboHandle();
        preservedViewport.set(getViewport());
        Gdx.gl20.glBindFramebuffer(GL20.GL_FRAMEBUFFER, fbo.getFramebufferHandle());
        Gdx.gl20.glViewport(0, 0, getFbo().getWidth(), getFbo().getHeight());
        renderers.assignLocalMatrices(localProjection, localTransform);
    }

    public void end() {
        bufferNesting--;

        if (!initialized) throw new IllegalStateException("VfxFrameBuffer must be initialized first");
        if (!drawing) throw new IllegalStateException("Is not drawing");

        if (getBoundFboHandle() != fbo.getFramebufferHandle()) {
            throw new IllegalStateException("Current bound OpenGL FBO's handle doesn't match to wrapped one. It seems like begin/end order was violated.");
        }

        drawing = false;

        renderers.flush();
        Gdx.gl20.glBindFramebuffer(GL20.GL_FRAMEBUFFER, previousFboHandle);
        Gdx.gl20.glViewport(preservedViewport.x, preservedViewport.y, preservedViewport.width, preservedViewport.height);
        renderers.restoreOwnMatrices();
    }

    protected int getBoundFboHandle() {
        int boundFboHandle = VfxGLUtils.getBoundFboHandle();
        return boundFboHandle;
    }

    protected VfxGlViewport getViewport() {
        VfxGlViewport viewport = VfxGLUtils.getViewport();
        return viewport;
    }

    private static class RendererManager implements Renderer {

        private final Array<Renderer> renderers = new Array<>();

        // Closed CTOR
        RendererManager() { }

        public void addRenderer(Renderer renderer) {
            renderers.add(renderer);
        }

        public void removeRenderer(Renderer renderer) {
            renderers.removeValue(renderer, true);
        }

        public void clearRenderers() {
            renderers.clear();
        }

        @Override
        public void flush() {
            for (int i = 0; i < renderers.size; i++) {
                renderers.get(i).flush();
            }
        }
        @Override
        public void assignLocalMatrices(Matrix4 projection, Matrix4 transform) {
            for (int i = 0; i < renderers.size; i++) {
                renderers.get(i).assignLocalMatrices(projection, transform);
            }
        }
        @Override
        public void restoreOwnMatrices() {
            for (int i = 0; i < renderers.size; i++) {
                renderers.get(i).restoreOwnMatrices();
            }
        }
    }

    public interface Renderer {
        void flush();
        void assignLocalMatrices(Matrix4 projection, Matrix4 transform);
        void restoreOwnMatrices();
    }

    public static abstract class RendererAdapter implements Renderer {
        private final Matrix4 preservedProjection = new Matrix4();
        private final Matrix4 preservedTransform = new Matrix4();

        @Override
        public void assignLocalMatrices(Matrix4 projection, Matrix4 transform) {
            preservedProjection.set(getProjection());
            preservedTransform.set(getTransform());
            setProjection(projection);
//            setTransform(transform);
        }

        @Override
        public void restoreOwnMatrices() {
            setProjection(preservedProjection);
//            setTransform(preservedTransform);
        }

        protected abstract Matrix4 getProjection();
        protected abstract Matrix4 getTransform();
        protected abstract void setProjection(Matrix4 projection);
        protected abstract void setTransform(Matrix4 transform);
    }

    public static class BatchRendererAdapter extends RendererAdapter implements Pool.Poolable {
        private Batch batch;

        public BatchRendererAdapter() {
        }

        public BatchRendererAdapter(Batch batch) {
            initialize(batch);
        }

        public BatchRendererAdapter initialize(Batch batch) {
            this.batch = batch;
            return this;
        }

        @Override
        public void reset() {
            batch = null;
        }

        public Batch getBatch() {
            return batch;
        }

        @Override
        public void flush() {
            batch.isDrawing(); {
                batch.flush();
            }
        }
        @Override
        protected Matrix4 getProjection() {
            return batch.getProjectionMatrix();
        }
        @Override
        protected Matrix4 getTransform() {
            return batch.getTransformMatrix();
        }
        @Override
        protected void setProjection(Matrix4 projection) {
            batch.setProjectionMatrix(projection);
        }
        @Override
        protected void setTransform(Matrix4 transform) {
            batch.setTransformMatrix(transform);
        }
    }

    public static class ShapeRendererAdapter extends RendererAdapter implements Pool.Poolable {
        private ShapeRenderer shapeRenderer;

        public ShapeRendererAdapter() {
        }

        public ShapeRendererAdapter(ShapeRenderer shapeRenderer) {
            initialize(shapeRenderer);
        }

        public ShapeRendererAdapter initialize(ShapeRenderer shapeRenderer) {
            this.shapeRenderer = shapeRenderer;
            return this;
        }

        @Override
        public void reset() {
            shapeRenderer = null;
        }

        public ShapeRenderer getShapeRenderer() {
            return shapeRenderer;
        }

        @Override
        public void flush() {
            if (shapeRenderer.isDrawing()) {
                shapeRenderer.flush();
            }
        }
        @Override
        protected Matrix4 getProjection() {
            return shapeRenderer.getProjectionMatrix();
        }
        @Override
        protected Matrix4 getTransform() {
            return shapeRenderer.getTransformMatrix();
        }
        @Override
        protected void setProjection(Matrix4 projection) {
            shapeRenderer.setProjectionMatrix(projection);
        }
        @Override
        protected void setTransform(Matrix4 transform) {
            shapeRenderer.setTransformMatrix(transform);
        }
    }
    
    public static class VfxGlViewport {
        public int x, y, width, height;

        public VfxGlViewport set(int x, int y, int width, int height) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            return this;
        }

        public VfxGlViewport set(VfxGlViewport viewport) {
            this.x = viewport.x;
            this.y = viewport.y;
            this.width = viewport.width;
            this.height = viewport.height;
            return this;
        }

        @Override
        public String toString() {
            return "x=" + x +
                    ", y=" + y +
                    ", width=" + width +
                    ", height=" + height;
        }
    }
    
    public static class VfxGLUtils {
        private static final String TAG = VfxGLUtils.class.getSimpleName();
        private static final IntBuffer tmpIntBuf = ByteBuffer.allocateDirect(16 * Integer.SIZE / 8).order(ByteOrder.nativeOrder()).asIntBuffer();
        private static final ByteBuffer tmpByteBuffer = BufferUtils.newByteBuffer(32);
        private static final VfxGlViewport tmpViewport = new VfxGlViewport();

        /** The code that is always added to the vertex shader code.
         * Note that this is added as-is, you should include a newline (`\n`) if needed. */
        public static String prependVertexCode = "";

        /** The code that is always added to every fragment shader code.
         * Note that this is added as-is, you should include a newline (`\n`) if needed. */
        public static String prependFragmentCode = "";

        //TODO Remove this after https://github.com/libgdx/libgdx/issues/4688 gets resolved
        /** This field is used to provide custom GL calls implementation. */
        public static VfxGlExtension glExtension;
        static {
            if (Gdx.app.getType() == Application.ApplicationType.WebGL) {
                try {
                    glExtension = (VfxGlExtension) ClassReflection.newInstance(
                            ClassReflection.forName("com.crashinvaders.vfx.gwt.GwtVfxGlExtension"));
                    Gdx.app.log(TAG, "GWT GL Extension initialized.");
                } catch (ReflectionException e) {
                    throw new GdxRuntimeException("Cannot find GwtVfxGlExtension class." +
                            "Are you sure you connected \"gdx-vfx-gwt\" library? " +
                            "\n" +
                            "Please visit GWT setup wiki page for instructions: " +
                            "https://github.com/crashinvaders/gdx-vfx/wiki/GWT-HTML-Library-Integration", e);
                }
            } else {
                glExtension = new DefaultVfxGlExtension();
            }
        }

        public static int getBoundFboHandle() {
            return glExtension.getBoundFboHandle();
        }

        public static VfxGlViewport getViewport() {
            IntBuffer intBuf = tmpIntBuf;
            Gdx.gl.glGetIntegerv(GL20.GL_VIEWPORT, intBuf);
            return tmpViewport.set(intBuf.get(0), intBuf.get(1), intBuf.get(2), intBuf.get(3));
        }

        public static ShaderProgram compileShader(FileHandle vertexFile, FileHandle fragmentFile) {
            return compileShader(vertexFile, fragmentFile, "");
        }

        public static ShaderProgram compileShader(FileHandle vertexFile, FileHandle fragmentFile, String defines) {
            if (fragmentFile == null) {
                throw new IllegalArgumentException("Vertex shader file cannot be null.");
            }
            if (vertexFile == null) {
                throw new IllegalArgumentException("Fragment shader file cannot be null.");
            }
            if (defines == null) {
                throw new IllegalArgumentException("Defines cannot be null.");
            }

            StringBuilder sb = new StringBuilder();
            sb.append("Compiling \"").append(vertexFile.name()).append('/').append(fragmentFile.name()).append('\"');
            if (defines.length() > 0) {
                sb.append(" w/ (").append(defines.replace("\n", ", ")).append(")");
            }
            sb.append("...");
            Gdx.app.log(TAG, sb.toString());

            String prependVert = prependVertexCode + defines;
            String prependFrag = prependFragmentCode + defines;
            String srcVert = vertexFile.readString();
            String srcFrag = fragmentFile.readString();

            ShaderProgram shader = new ShaderProgram(prependVert + "\n" + srcVert, prependFrag + "\n" + srcFrag);

            if (!shader.isCompiled()) {
                throw new GdxRuntimeException("Shader compile error: " + vertexFile.name() + "/" + fragmentFile.name() + "\n" + shader.getLog());
            }
            return shader;
        }

        //region GL state queries

        /** Enable pipeline state queries: beware the pipeline can stall! */
        public static boolean enableGLQueryStates = false;

        /**
         * Provides a simple mechanism to query OpenGL pipeline states.
         * Note: state queries are costly and stall the pipeline, especially on mobile devices!
         * <br/>
         * Queries switched off by default. Update {@link #enableGLQueryStates} flag to enable them.
         */
        public static boolean isGLEnabled(int pName) {
            if (!enableGLQueryStates) return false;

            boolean result;

            switch (pName) {
                case GL20.GL_BLEND:
                    Gdx.gl20.glGetBooleanv(GL20.GL_BLEND, tmpByteBuffer);
                    result = (tmpByteBuffer.get() == 1);
                    tmpByteBuffer.clear();
                    break;
                default:
                    result = false;
            }

            return result;
        }
        //endregion
    }
    
    public static class DefaultVfxGlExtension implements VfxGlExtension {
        private static final IntBuffer tmpIntBuf = ByteBuffer.allocateDirect(16 * Integer.SIZE / 8).order(ByteOrder.nativeOrder()).asIntBuffer();

        @Override
        public int getBoundFboHandle() {
            IntBuffer intBuf = tmpIntBuf;
            Gdx.gl.glGetIntegerv(GL_FRAMEBUFFER_BINDING, intBuf);
            return intBuf.get(0);
        }
    }
}
