package box2d;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.Mesh.VertexDataType;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import util.nMap;

public class nBatch {
	
	
	private ArrayList<VertexAttributeDef> attribDefs = 
			new ArrayList<VertexAttributeDef>();
	private int vertex_flt_nb = 0;
	private class VertexAttributeDef {
		final int usage, componentNb; final String ref; 
		VertexAttributeDef(int _usage, int _componentNb, String _ref) {
			usage = _usage; componentNb = _componentNb; ref = _ref; }
	}
	void prepareAttribute() {
		attributes = new VertexAttribute[attribDefs.size()];
		for (int i = 0 ; i < attribDefs.size() ; i++) {
			VertexAttributeDef def = attribDefs.get(i);
			attributes[i] = new VertexAttribute(def.usage,def.componentNb,def.ref);
		}
	}
	
	VertexDataType type; 
	boolean isStatic; 
	int maxVertices; 
	int maxIndices;
	VertexAttribute[] attributes;
	
	public nBatch() {
		this((Gdx.gl30 != null ? 	VertexDataType.VertexBufferObjectWithVAO : 
									VertexDataType.VertexArray), 
				false, 256, 4096, 4096); }
	public nBatch(VertexDataType _type, boolean _isStatic, int unitCapacity, 
			int _maxVertices, int _maxIndices) {
		type = _type;
		isStatic = _isStatic;
		maxVertices = _maxVertices; 
		maxIndices = _maxIndices;
		
		// Array (boolean ordered, int capacity)
		/** ordered : If false, methods that remove elements may change the order of other elements in the array, which avoids a
		 *           memory copy.
		 * capacity : Any elements added beyond this will cause the backing array to be grown. */
		unitList = new Array<Unit>(false, unitCapacity);
		tmpunit = new Array<Unit>(false, unitCapacity);
		freeUnit = new Array<Unit>(false, unitCapacity);
		clearingUnit = new Array<Unit>(false, unitCapacity);

		beshs = new Array<Besh>(true, 10);
	}

	private void attrib(int usage, int componentNb, int floatNb, String ref) {
		attribDefs.add(new VertexAttributeDef(usage, componentNb, ref));
		vertex_flt_nb += floatNb; }

	public nBatch positionAttribute(String ref) {
		attrib(Usage.Position, 2, 2, ref); return this; }
	public nBatch colorAttribute(String ref) {
		attrib(Usage.ColorPacked, 4, 1, ref); return this; }
	public nBatch textureCoordAttribute(String ref) {
		attrib(Usage.TextureCoordinates, 2, 2, ref); return this; }
	public nBatch genericAttribute(String ref) {
		attrib(Usage.Generic, 1, 1, ref); return this; }

	public nBatch finish() {
		vertices = new float[maxVertices * vertex_flt_nb];
		indices = new short[maxIndices];
		new Besh();
		return this;
	}
	
	public void dispose() {
		for (Besh b : beshs) b.dispose(); beshs.clear();
		for (Model m : models.tmp_all()) m.dispose();
		tmpunit.clear(); for (Unit u : unitList) tmpunit.add(u);
		for (Unit u : tmpunit) u.clear(); tmpunit.clear(); freeUnit.clear();
		attribDefs.clear();
	}

	private float vertices[];
	private short indices[];

	private int flt_cnt = 0, ind_cnt = 0, besh_cnt = 0;
	private short vert_cnt = 0;
	
	private void reset_cnt() { flt_cnt = 0; vert_cnt = 0; ind_cnt = 0; besh_cnt = 0; }


	private final Array<Besh> beshs;

	private class Besh {

		Mesh mesh;

		private int besh_ind = 0;

		public Besh() {
			beshs.add(this);
			prepareAttribute();
			mesh = new Mesh(type, isStatic, maxVertices, maxIndices, attributes);
		}
		
		public void dispose() {
			mesh.dispose();
		}
		
		public void pushStackToMesh() {
			if (flt_cnt <= 0 || ind_cnt <= 0) { besh_ind = 0; return; }
			mesh.setVertices(vertices, 0, flt_cnt);
			mesh.setIndices(indices, 0, ind_cnt);
			besh_ind = ind_cnt; ind_cnt = 0; flt_cnt = 0;
		}
		
		public void render(final ShaderProgram shader) {
			if (besh_ind > 0) mesh.render(shader, GL20.GL_TRIANGLES, 0, besh_ind);
		}

	}
	
	public void request(int vertex, int indice) {
		if (vert_cnt + vertex >= maxVertices || 
				ind_cnt + indice >= maxIndices ) {
			if (beshs.size == 0) new Besh();
			else { beshs.get(besh_cnt).pushStackToMesh();
				if (besh_cnt++ >= beshs.size) new Besh(); }
		}
	}


	public final nMap<Model> models = new nMap<Model>();
	
	public <T extends Model> void addModel(String ref, T mod) {
		mod.addToBatch(ref,this); }

	public static abstract class Model {

		private nBatch batch;
		private String ref;
		private final int verticesUse, indicesUse, argUse;
		
		public Model(int _verticesUse, int _indicesUse, int _argUse) {
			verticesUse = _verticesUse; indicesUse = _indicesUse; argUse = _argUse;
		}
		
		void addToBatch(String _ref, nBatch _batch) {
			ref = _ref; batch = _batch; batch.models.put(ref,this); }
		
		public void dispose() {
			batch.models.remove(ref,this); }
		
		public abstract void update(Unit u);
		public void create(Unit u) {}
		public void destroy(Unit u) {}
		
		private ArrayList<LightLayer> layers = new ArrayList<LightLayer>();
		public Model useLayer(LightLayer l) {
			layers.add(l);
			return this;
		}
		
	}
	
	private void transform(float x, float y, float cos, float sin) { 
		transf.set(x,y); trcos = cos; trsin = sin; }
	private Vector2 transf = new Vector2();
	private float trsin = 0, trcos = 0;
	private static int tmpi = 0;
	private static short tmps = 0;
	private float rotX(float x, float y) { return x * trcos - y * trsin; }
	private float rotY(float x, float y) { return x * trsin + y * trcos; }
	private void pushFloat(float f) { vertices[flt_cnt++] = f; }
	private void pushPos(float x, float y) { 
		vertices[flt_cnt++] = rotX(x,y) + transf.x;
		vertices[flt_cnt++] = rotY(x,y) + transf.y; }
	private short pushVertices(float[] fs, int nb) {
		if (fs == null) return -1;
		tmps = vert_cnt++;
		for (int i = 0 ; i < nb * vertex_flt_nb ; i += vertex_flt_nb) {
			pushPos(fs[i],fs[i+1]);
			if (vertex_flt_nb > 2) 
				for (int j = 2 ; j < vertex_flt_nb ; j++) pushFloat(fs[i+j]);
		}
		return tmps;
	}
	private void pushTrigs(short[] p, int offset, int trignb) {
		if (p != null && p.length > 0 && p.length%3 == 0) 
			for (int i = 0 ; i < p.length && i < trignb * 3 ; i++) 
				indices[ind_cnt++] = (short)(offset + p[i]);
	}

	public class Unit {

		public void setTransform(float x, float y, float r) {
			pos.set(x,y); rot = r; dirty = true; }
		public void setArg(float...a) {
			if (a != null) {
				for (int i = 0 ; i < a.length ; i++) args[i] = a[i];
			}
		}

		public float a(int i) { return args[i]; }

		public float a(int i, float f) { args[i] = f; return args[i]; }

		public void beginPush() {
			f_cnt = 0; i_cnt = 0; v_cnt = 0; t_cnt = 0; }

		public short pushVert(float x, float y, float...fs) {
			verts[f_cnt++] = x; verts[f_cnt++] = y; 
			if (fs != null && vertex_flt_nb > 2) 
				for (int i = 0 ; i < fs.length && i+2 < vertex_flt_nb ; i++) 
					verts[f_cnt++] = fs[i];
			return v_cnt++; }

		public void pushTrig(int p1, int p2, int p3) {
			inds[i_cnt++] = (short)p1; inds[i_cnt++] = (short)p2; inds[i_cnt++] = (short)p3; t_cnt++; }

		Model model;
		private final Vector2 pos = new Vector2();
		private float rot = 0;
		private float cos = 0f, sin = 0f;
		private boolean dirty = true;

		private float args[] = null;
		private float verts[] = null;
		private short inds[] = null;

		private int f_cnt = 0, i_cnt = 0, t_cnt = 0;
		private short v_cnt = 0;
		
		Unit init(Model _model, float...a) {
			model = _model;
			clearing = false;
			unitList.add(this);
			pos.set(0,0); rot = 0f; userData = null;
			if (verts == null || verts.length < model.verticesUse * vertex_flt_nb)
				verts = new float[model.verticesUse * vertex_flt_nb];
			if (inds == null || inds.length < model.indicesUse)
				inds = new short[model.indicesUse];
			if (args == null || args.length < model.argUse)
				args = new float[model.argUse];
			if (a != null) setArg(a);
			model.create(this);
			for (LightLayer l : model.layers) l.unitList.add(this);
			return this;
		}

		private boolean clearing = false;
		public void clear() {
			if (!clearing) {
				clearing = true;
				clearingUnit.add(this);
			}
		}

		public void do_clear() {
			model.destroy(this);
			for (LightLayer l : model.layers) l.unitList.removeValue(this, true);
			unitList.removeValue(this, true);
			freeUnit.add(this);
		}
		
		private Object userData = null;
		public Unit setData(Object o) { userData = o; return this; }
		public Object getData() { return userData; }
		public <K> K getData(Class<K> cl) { if (userData == null) return null; else return (K)userData; }
		
		boolean updated = false;
		void begin() { updated = false; } 
		void update() { 
			if (!updated) { 
				model.update(this); updated = true; } }

		void pushToMesh() {
			if (!updated) update();
			if (clearing) return;
			if (v_cnt == 0 || t_cnt == 0) return;
			if (dirty) { dirty = false; 
				cos = MathUtils.cos(rot); sin = MathUtils.sin(rot); }
			transform(pos.x,pos.y,cos,sin);
			request(v_cnt, i_cnt);
			tmpi = pushVertices(verts, v_cnt);
			pushTrigs(inds, tmpi, t_cnt);
		}
		
		
		
	}

	public final Array<Unit> unitList;
	final Array<Unit> tmpunit;
	private final Array<Unit> clearingUnit;
	private final Array<Unit> freeUnit;

	public Unit newUnit(String model_ref, float...a) {
		if (models.hasKey(model_ref)) {
			if (freeUnit.size > 0) 
				return freeUnit.removeIndex(freeUnit.size - 1).init(models.get(model_ref),a);
			else return new Unit().init(models.get(model_ref),a); 
		} else return null; 
	}
	
	

	//begin frame
	private boolean mesh_pushed = false;
	public void begin() {
		mesh_pushed = false; render_group = null;
		for (Unit u : unitList) { u.begin(); }
	}

	public void end() {
		tmpunit.clear(); for (Unit u : unitList) if (u.clearing) tmpunit.add(u);
		for (Unit u : tmpunit) u.do_clear(); tmpunit.clear(); 
	}

	public void push() {
		reset_cnt();
		for (Unit u : unitList) { u.pushToMesh(); }
		beshs.get(besh_cnt).pushStackToMesh();
		mesh_pushed = true;
	}
	
	private Array<Unit> render_group = null;
	public void setRenderGroup(Array<Unit> arr) { render_group = arr; mesh_pushed = false; }

	public void updateGroup() {
		for (Unit u : render_group) { u.update(); }
	}

	public void pushGroup() {
		reset_cnt();
		for (Unit u : render_group) { u.pushToMesh(); }
		beshs.get(besh_cnt).pushStackToMesh();
		mesh_pushed = true;
	}
	
	public void render(final ShaderProgram shader) {
		if (!mesh_pushed) { if (render_group == null) push(); else pushGroup(); }
		for (int i = 0 ; i <= besh_cnt ; i++) beshs.get(i).render(shader);
	}
	
	
	
	
}
