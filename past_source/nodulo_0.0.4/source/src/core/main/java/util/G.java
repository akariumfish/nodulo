package util;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.Mesh.VertexDataType;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;



public class G {
	
	

	public static class GLExecutor {
		
		private static int ARG = 4;
		private static int SYM = 32;
		private static int WORD = 128;
		
		//TODO twin buffer for synchronize
		private int[] comA = new int[ARG * SYM * WORD];
		private int[] comB = new int[ARG * SYM * WORD];
		private boolean flip = false, read_lock = false, write_lock = false;
		
		public void read() { read_lock = true; readcoms = flip ? comA : comB; }
		public void write() { write_lock = true; write_cnt = 0; writecoms = flip ? comB : comA; }
		public void free_read() { read_lock = false; }
		public void free_write() { write_lock = false; }
		public boolean isFree() { return !write_lock && !read_lock; }
		public void turn() { if (isFree()) flip = !flip; }

		private int[] writecoms;
		private int write_cnt = 0;
		private int wtmp = 0;
		
		public void write(int...sym) { 
			if (sym == null) return; 
			for (wtmp = 0 ; wtmp < sym.length ; wtmp++) {
				writecoms[write_cnt] = sym[wtmp]; write_cnt++; }
		}
		
		private int[] readcoms;
		private int words,syms;
		private int witer = 0, siter = 0, wcnt, ecnt;

		void exec(final int[] cm) { exec(cm, WORD, 0); }
		void exec(final int[] cm, int w, int s) {
			readcoms = cm; words = w; syms = s; witer = 0; siter = 0; 
			for (ecnt = 0 ; ecnt < words ; ecnt++) word(); 
			for (ecnt = 0 ; ecnt < syms ; ecnt++) sym(); }
		void word() {
			siter = witer; for (wcnt = 0 ; wcnt < SYM ; wcnt++) sym(); witer += SYM; }
		void sym() {
			switch (readcoms[siter]) {
			case CLEAR_SCREEN : {
				clear_screen(readcoms[siter+1], readcoms[siter+2]); break; }
			}
			siter += ARG;
		}
		

		public static final int CLEAR_SCREEN = 0;
		void clear_screen(int array_id, int val_id) {
			
		}
	}
	
	
	
	public static void clear_screen(float r, float g, float b, float a) {

		Gdx.gl20.glDisable(GL20.GL_BLEND);
		Gdx.gl.glClearColor(r,g,b,a);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		Gdx.gl.glEnable(GL20.GL_BLEND); 
		
	}
	
	
	public static class Shader {

		private nMap<Uniform> uniforms = new nMap<Uniform>();

		private abstract class Uniform {
			String ref; Uniform(String _ref) { ref = _ref; }
			abstract void bind(ShaderProgram p); 
			abstract void set(Object o); }
		private class UniformFloat extends Uniform {
			float val; UniformFloat(String _ref, float def) {
				super(_ref); val = def; }
			void bind(ShaderProgram p) { p.setUniformf(ref,val); }
			void set(Object o) { if (o instanceof Float) val = (float)o; } }
		private class UniformInt extends Uniform {
			int val; UniformInt(String _ref, int def) {
				super(_ref); val = def; }
			void bind(ShaderProgram p) { p.setUniformi(ref,val); }
			void set(Object o) { if (o instanceof Integer) val = (int)o; } }
		private class UniformVec2 extends Uniform {
			Vector2 val = new Vector2();
			UniformVec2(String _ref, Vector2 def) {
				super(_ref); val.set(def); }
			void bind(ShaderProgram p) { p.setUniformf(ref,val); }
			void set(Object o) { if (o instanceof Vector2) val.set((Vector2)o); } }
		private class UniformColor extends Uniform {
			Color val = new Color();
			UniformColor(String _ref, Color def) {
				super(_ref); val.set(def); }
			void bind(ShaderProgram p) { p.setUniformf(ref,val.r,val.g,val.b,val.a); }
			void set(Object o) { if (o instanceof Color) val.set((Color)o); } }
		private class UniformMat4 extends Uniform {
			Matrix4 val = new Matrix4();
			UniformMat4(String _ref, Matrix4 def) {
				super(_ref); val.set(def); }
			void bind(ShaderProgram p) { p.setUniformMatrix(ref,val); }
			void set(Object o) { if (o instanceof Matrix4) val.set((Matrix4)o); } }

		public Shader addUniformFloat(String ref) {
			uniforms.put(ref, new UniformFloat(ref,0f)); return this; }
		public Shader addUniformFloat(String ref, float def) {
			uniforms.put(ref, new UniformFloat(ref,def)); return this; }
		public Shader addUniformInt(String ref) {
			uniforms.put(ref, new UniformInt(ref,(int)0)); return this; }
		public Shader addUniformInt(String ref, int def) {
			uniforms.put(ref, new UniformInt(ref,def)); return this; }
		public Shader addUniformVec2(String ref) {
			uniforms.put(ref, new UniformVec2(ref,new Vector2())); return this; }
		public Shader addUniformVec2(String ref, Vector2 def) {
			uniforms.put(ref, new UniformVec2(ref,def)); return this; }
		public Shader addUniformColor(String ref) {
			uniforms.put(ref, new UniformColor(ref,new Color())); return this; }
		public Shader addUniformColor(String ref, Color def) {
			uniforms.put(ref, new UniformColor(ref,def)); return this; }
		public Shader addUniformMatrix4(String ref) {
			uniforms.put(ref, new UniformMat4(ref,new Matrix4())); return this; }
		public Shader addUniformMatrix4(String ref, Matrix4 def) {
			uniforms.put(ref, new UniformMat4(ref,def)); return this; }

		public Shader setUniform(String ref, Object val) {
			if (uniforms.hasKey(ref)) uniforms.get(ref).set(val); return this; }
		
		public void bind() {
			shader.bind(); for (Uniform u : uniforms.all()) u.bind(shader); }
		
		private String vertexShader, fragShader;
		public Shader vertexShader(String s) { vertexShader = Utl.copy(s); return this; }
		public Shader fragShader(String s) { fragShader = Utl.copy(s); compute(); return this; }
		public ShaderProgram shader;
		private void compute() {
			ShaderProgram.pedantic = false;
			shader = new ShaderProgram(vertexShader, fragShader);
			if(!shader.isCompiled()) { Gdx.app.log("ERROR : shader not compiled", shader.getLog()); }
		}
		public void dispose() {
			shader.dispose();
		}

	}
	
	
	
	
	
	
	public static class BatchAttribute {

		private VertexDataType type; 
		private boolean isStatic; 
		private int maxVertices, maxIndices, maxInstances;
		private int vertex_flt_nb = 0;
		private int inst_flt_nb = 0;
		private boolean instantiated = false;
		
		public BatchAttribute() { this(256, 4096, 2048, 0); }
		public BatchAttribute(int _unitCapacity, int _maxVertices, int _maxTriangles, int _maxInstances) {
			this(Gdx.gl30 != null ? 
					VertexDataType.VertexBufferObjectWithVAO : VertexDataType.VertexArray, 
					false, _unitCapacity, _maxVertices, _maxTriangles, _maxInstances); }
		public BatchAttribute(VertexDataType _type, boolean _isStatic, int unitCapacity, 
				int _maxVertices, int _maxTriangles, int _maxInstances) {
			type = _type;
			isStatic = _isStatic;
			maxVertices = _maxVertices; 
			maxIndices = _maxTriangles * 3;
			maxInstances = _maxInstances;
		}

		private ArrayList<VertexAttributeDef> attribDefs = new ArrayList<VertexAttributeDef>();
		private ArrayList<VertexAttributeDef> instanceAttribDefs = new ArrayList<VertexAttributeDef>();
		private class VertexAttributeDef {
			final int usage, componentNb; final String ref; 
			VertexAttributeDef(int _usage, int _componentNb, String _ref) {
				usage = _usage; componentNb = _componentNb; ref = _ref; } }

		private VertexAttribute[] attributes;
		private void prepareAttribute() {
			attributes = new VertexAttribute[attribDefs.size()];
			for (int i = 0 ; i < attribDefs.size() ; i++) {
				VertexAttributeDef def = attribDefs.get(i);
				attributes[i] = new VertexAttribute(def.usage,def.componentNb,def.ref); } }
		private void prepareInstanceAttribute() {
			attributes = new VertexAttribute[instanceAttribDefs.size()];
			for (int i = 0 ; i < instanceAttribDefs.size() ; i++) {
				VertexAttributeDef def = instanceAttribDefs.get(i);
				attributes[i] = new VertexAttribute(def.usage,def.componentNb,def.ref); } }

		private void attrib(int usage, int componentNb, int floatNb, String ref) {
			attribDefs.add(new VertexAttributeDef(usage, componentNb, ref));
			vertex_flt_nb += floatNb; }
		private void instAttrib(int usage, int componentNb, int floatNb, String ref) {
			instanceAttribDefs.add(new VertexAttributeDef(usage, componentNb, ref));
			inst_flt_nb += floatNb; instantiated = true; }

		public BatchAttribute positionAttribute(String ref) {
			attrib(Usage.Position, 2, 2, ref); return this; }
		public BatchAttribute colorAttribute(String ref) {
			attrib(Usage.ColorPacked, 4, 1, ref); return this; }
		public BatchAttribute textureCoordAttribute(String ref) {
			attrib(Usage.TextureCoordinates, 2, 2, ref); return this; }
		public BatchAttribute genericAttribute(String ref) {
			attrib(Usage.Generic, 1, 1, ref); return this; }

		public BatchAttribute positionInstAttribute(String ref) {
			instAttrib(Usage.Position, 2, 2, ref); return this; }
		public BatchAttribute colorInstAttribute(String ref) {
			instAttrib(Usage.ColorPacked, 4, 1, ref); return this; }
		public BatchAttribute textureInstCoordAttribute(String ref) {
			instAttrib(Usage.TextureCoordinates, 2, 2, ref); return this; }
		public BatchAttribute genericInstAttribute(String ref) {
			instAttrib(Usage.Generic, 1, 1, ref); return this; }
		
		public void dispose() {
			attribDefs.clear(); instanceAttribDefs.clear();
		}
		
		public Mesh make() { 
			prepareAttribute();
			Mesh mesh = new Mesh(type, isStatic, maxVertices, maxIndices, attributes); 
			if (instantiated && inst_flt_nb > 0 && maxInstances > 0) {
				prepareInstanceAttribute();
				mesh.enableInstancedRendering(isStatic, maxInstances, attributes); }
			return mesh;
		}

	}
	
	
	public static class Batch {
		
		private final BatchAttribute atrib;
		private int maxVertices, maxIndices, maxInstances;
		private int vertex_flt_nb = 0;
		private int inst_flt_nb = 0;
		private boolean instantiated = false;
		public Batch(BatchAttribute _atrib) { 
			atrib = _atrib;
			maxVertices = atrib.maxVertices;
			maxIndices = atrib.maxIndices; 
			maxInstances = atrib.maxInstances;
			vertex_flt_nb = atrib.vertex_flt_nb;
			inst_flt_nb = atrib.inst_flt_nb;
			instantiated = atrib.instantiated;
			
			beshs = new Array<Besh>(true, 10);
		}
		
		public Batch finish() {
			vertices = new float[maxVertices * vertex_flt_nb];
			indices = new short[maxIndices];
			instances = new float[maxInstances * inst_flt_nb];
			new Besh();
			return this;
		}
		
		public void dispose() {
			for (Besh b : beshs) b.dispose(); beshs.clear();
		}
		
		private final Array<Besh> beshs;
		private class Besh {
			Mesh mesh;
			private int besh_ind = 0;
			Besh() { beshs.add(this);
				mesh = atrib.make();
			}
			void dispose() { mesh.dispose(); }
			
			void pushStackToMesh() {
				if (instantiated) {
					if (flt_cnt <= 0 || inst_cnt <= 0) { besh_ind = 0; return; }
					mesh.setVertices(vertices, 0, flt_cnt);
					if (ind_cnt > 0) mesh.setIndices(indices, 0, ind_cnt);
					mesh.setInstanceData(instances, 0, inst_cnt);
					besh_ind = inst_cnt; inst_cnt = 0; 
				} else {
					if (flt_cnt <= 0 || ind_cnt <= 0) { besh_ind = 0; return; }
					mesh.setVertices(vertices, 0, flt_cnt);
					mesh.setIndices(indices, 0, ind_cnt);
					besh_ind = ind_cnt; ind_cnt = 0; flt_cnt = 0; }
			}
			void render(final ShaderProgram shader) {
				if (besh_ind > 0) mesh.render(shader, GL20.GL_TRIANGLES, 0, besh_ind);
				Utl.mesh_render(besh_ind); }
		}

		public void request(int vertex, int indice) {
			if (vert_cnt + vertex >= maxVertices || ind_cnt + indice >= maxIndices ) {
				beshs.get(besh_cnt).pushStackToMesh();
				if (besh_cnt++ >= beshs.size) new Besh();  }
		}

		public void request(int instance) {
			if (inst_cnt + instance >= maxInstances ) {
				beshs.get(besh_cnt).pushStackToMesh();
				if (besh_cnt++ >= beshs.size) new Besh();  }
		}

		private float vertices[];
		private float instances[];
		private short indices[];

		private int flt_cnt = 0, ind_cnt = 0, besh_cnt = 0;
		private int vert_cnt = 0;
		private int inst_cnt = 0;
		
		public void reset_cnt() { 
			if (instantiated) { inst_cnt = 0; besh_cnt = 0; } 
			else { flt_cnt = 0; vert_cnt = 0; ind_cnt = 0; besh_cnt = 0; } }

		public void transform() { transf.set(0,0); trcos = 1f; trsin = 0f; }
		public void transform(float x, float y, float cos, float sin) { 
			transf.set(x,y); trcos = cos; trsin = sin; }
		private Vector2 transf = new Vector2();
		private float trsin = 0, trcos = 0;
		private static int tmps = 0;
		private float rotX(float x, float y) { return x * trcos - y * trsin; }
		private float rotY(float x, float y) { return x * trsin + y * trcos; }
		private void pushInstFloat(float f) { instances[inst_cnt++] = f; }
		private void pushInstPos(float x, float y) { 
			instances[inst_cnt++] = rotX(x,y) + transf.x;
			instances[inst_cnt++] = rotY(x,y) + transf.y; }
		public void pushInstances(float[] fs, int nb) {
			for (int i = 0 ; i < nb * inst_flt_nb ; i += inst_flt_nb) {
				pushInstPos(fs[i],fs[i+1]);
				for (int j = 2 ; j < inst_flt_nb ; j++) pushInstFloat(fs[i+j]); }
		}
		private void pushVertFloat(float f) { vertices[flt_cnt++] = f; }
		private void pushVertPos(float x, float y) { 
			vertices[flt_cnt++] = rotX(x,y) + transf.x;
			vertices[flt_cnt++] = rotY(x,y) + transf.y; }
		public int pushVertices(float[] fs, int nb) {
			tmps = vert_cnt++;
			for (int i = 0 ; i < nb * vertex_flt_nb ; i += vertex_flt_nb) {
				if (i != 0) vert_cnt++; pushVertPos(fs[i],fs[i+1]);
				for (int j = 2 ; j < vertex_flt_nb ; j++) pushVertFloat(fs[i+j]); }
			return tmps;
		}
		public void pushTrigs(short[] p, int offset, int trignb) {
			if (p != null && p.length > 0 && p.length%3 == 0) 
				for (int i = 0 ; i < p.length && i < trignb * 3 ; i++) 
					indices[ind_cnt++] = (short)(offset + p[i]);
		}

		public Batch pushIndice(short...fs) { 
			if (fs != null) for (short f : fs) indices[ind_cnt++] = f; return this; }
		public Batch pushVertice(float...fs) { 
			if (fs != null) for (float f : fs) vertices[flt_cnt++] = f; vert_cnt++; return this; }

		public void render(final ShaderProgram shader) {
			for (int i = 0 ; i <= besh_cnt ; i++) beshs.get(i).render(shader); }

		public void render(int g, final ShaderProgram shader) {
//			render_group = getGroup(g);
//			reset_cnt(); transform();
//			for (Unit u : render_group) { u.push(); }
			beshs.get(besh_cnt).pushStackToMesh();
			for (int i = 0 ; i <= besh_cnt ; i++) beshs.get(i).render(shader);
		}
		
	}
}
