package aa_term;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.HashMap;

import com.badlogic.gdx.math.Vector2;

public class ComponentManager {

	static final Class<?>[] primitive_types = new Class<?>[] {
//		Boolean.class, Byte.class, 
		Integer.class
//		, Float.class, Long.class 
		};
	
	static final Class<?>[][] prim_pair = new Class<?>[][] {
//		{ Boolean.class, boolean.class }, 
//		{ Byte.class, byte.class }, 
		{ Integer.class, int.class }
//		, { Float.class, float.class }, 
//		{ Long.class, long.class }
	};
	
	static final Primitive primitive_list = 
			new Primitive(primitive_types);
	
	public static class Primitive {
		static Primitive static_prim_list;
		
		public Primitive(Class<?>...primitive_types) {
			static_prim_list = this;
			for (Class<?> c : primitive_types) new PrimitiveConstructor(c);
		}

		static class StaticConstructor {
			public static int new_int() { return (int)0; }
			public static Integer new_Integer() { return new Integer(0); }
			public static int[] new_int_array(int l) { return new int[l]; }
			public static Integer[] new_Integer_array(int l) { return new Integer[l]; }
		}
		
		private HashMap<Class<?>,PrimitiveConstructor<?>> constructors = 
				new HashMap<Class<?>,PrimitiveConstructor<?>>();
		
		class PrimitiveConstructor<T> implements Constructor<T> {
			
			// store instenciation methods
			
			PrimitiveConstructor(Class<T> ct) {
				constructors.put(ct,this);
				// search instenciation methods in StaticConstructor
			}
			public T alloc() {
				return null;
			}
			public T[] alloc(int l) {
				return null;
			}
		}
		
		interface Constructor <T> {
			public T alloc();
			public T[] alloc(int l);
		}

		public static <K> Constructor<K> constructor(Class<K> ct) {
			return (Constructor<K>)static_prim_list.constructors.get(ct);
		}
 	}

	
	class Memory <T> {
		
		class TrackingArray {
			Boolean[] use; 
			TrackingArray(int l) { 
				use = boolean_constructor.alloc(l);
			}
		}
		class TrackedArray extends TrackingArray { 
			T[] data; 
			TrackedArray(int l) {
				super(l);
				data = constructor.alloc(l);
			}
		}
		class ChunkArray extends TrackingArray { 
			Chunk[] data; 
			ChunkArray(int l) {
				super(l);
//				data = new ComponentManager.Memory<T>.Chunk[l];
			}
		}
		
		Primitive.Constructor<T> constructor;
		Primitive.Constructor<Boolean> boolean_constructor;
		
		private static final int CHUNK_SIZE = 50;
		private static final int LINK_SIZE = 5;
		
		Memory(Class<T> ct) {
			constructor = Primitive.constructor(ct);
		}
		
		private ChunkArray chunks;
		class Chunk { // for value
			TrackedArray data;
		}
//		private ChainArray chains;
//		private class Chain { // for arrays
//			private Link first;
//			private Link last;
//			private class Link {
//				TrackedArray data;
//			}
//		}
	}
	
	


	@Target({ElementType.FIELD}) @Retention(RetentionPolicy.RUNTIME) 
	public @interface ComponentField {
		String description () default "";
		String[] parameter () default {};
	}

	@Target({ElementType.FIELD}) @Retention(RetentionPolicy.RUNTIME) 
	public @interface ComponentReference {}

	// this field reference a component who is exclusif to the containing Component
	@Target({ElementType.FIELD}) @Retention(RetentionPolicy.RUNTIME) 
	public @interface MemberComponent {}

	// the Component containing this field require a value for it
	@Target({ElementType.FIELD}) @Retention(RetentionPolicy.RUNTIME) 
	public @interface RequiredComponent {}

	// shared by multiple component 
	@Target({ElementType.TYPE}) @Retention(RetentionPolicy.RUNTIME) 
	public @interface CommonComponent {}
	
	// member of an entity
	@Target({ElementType.TYPE}) @Retention(RetentionPolicy.RUNTIME) 
	public @interface EntityComponent {}
	
	// contain group of EntityComponent
	@Target({ElementType.TYPE}) @Retention(RetentionPolicy.RUNTIME) 
	public @interface EntityClass {}

	
	
	
	


	static class RegisteredComponent extends Component {
		String name;
		int registry_index;
		
		static <T extends RegisteredComponent> T read(String name) {
			return null;
		}
	}

	static class Component {
		int id;

		//			POOLING METHODS
		
		//		Life cycle event callbacks
		public void build() {} // called at original creation
		public void load() {} // called after being created by loading data
		public void init() {} // called after build or load
		public void finish() {} // called when all simultanely created Component have done their init()
		public void save() {} // called before being saved
		public void clear() {} // called when destroyed
		
		//access Component by id
		static <T extends Component> T access(int i) {
			return null;
		}
		// instanciate new Component
		static <T extends Component> T alloc() {
			return null;
		}
		// destroy a Component
		static <T extends Component> void destroy(T t) {
			
		}
		
		//		SCRIPTING / SAVING / STREAMING METHODS

		// return a script to build a copy of this
		public String[] compile() {
			return null;
		}
		// return a script with the change to this since last call to compile
		public String[] compileChange() {
			return null;
		}
		// instensiate from a script
		public static <T extends Component> T compute(String[] script) {
			return null;
		}
		
	}
	

	
	public static void register_component_class(Class<?> ct) {
		
	}
	
	static class SpaceComponent {

		public void register_component_class() {
			ComponentManager.register_component_class(Point.class);
			ComponentManager.register_component_class(Shape.class);
			ComponentManager.register_component_class(Geometry.class);
			ComponentManager.register_component_class(Coord.class);
			ComponentManager.register_component_class(Drawing.class);
			// ...
		}

		@CommonComponent class EntityDefinition extends RegisteredComponent {
			// list of EntityComponent to add to the entity
			// Entity create() > new Entity
		}
			
		@EntityClass class Entity extends Component {
			@ComponentField(description = "Coordinate of this entity", parameter = {}) 
			@ComponentReference
			@RequiredComponent
			@MemberComponent
			Coord ref;
		}

		// shape rendering
		@EntityComponent class Coord extends Component {
			@ComponentField(description = "parent", parameter = {}) 
			@ComponentReference 
			Coord parent;
			@ComponentField(description = "childs", parameter = {}) 		
			@ComponentReference 		
			Coord[] childs;
			@ComponentField(description = "position relative to parent", parameter = {"def","{0.0,0.0}v"}) 
			Vector2 local_pos;
			@ComponentField(description = "rotation relative to parent", parameter = {"def","0.0f"}) 
			float local_rot;
			@ComponentField(description = "position in world coordinate", parameter = {"def","{0.0,0.0}v"}) 
			Vector2 global_pos;
			@ComponentField(description = "total rotation", parameter = {"def","0.0f"}) 
			float global_rot;
			
			public void update() {
				// Calculate global pos/rot from parent and local, verify that parent are updated
			}
		}
		@EntityComponent class Drawing extends Component {
			@ComponentField(description = "Geometry rendered by this drawing", parameter = {}) 
			@RequiredComponent
			Geometry geometry;
			@ComponentField(description = "Coordinate of this drawing rendering", parameter = {}) 
			@ComponentReference 
			@RequiredComponent
			Coord coord;
			
			Renderable rend;

			@Override public void init() {
				Geometry geo = Component.access(0);
				rend = geo.newRenderable();
							
				// register rend with the renderer
			}
			
			// need to be called after coords updates
			public void update() {
				Coord co = Component.access(1);
				rend.update(co.global_pos.x, co.global_pos.y, co.global_rot );
			}
		}

		// Shape definition :

		@CommonComponent class Geometry extends RegisteredComponent {
			@ComponentField(description = "shape units", parameter = {}) 		
			@ComponentReference
			@MemberComponent
			ShapeUnit[] unit;
			
			public Renderable newRenderable() {
				// instensiate a renderer object configured to draw this geom
				Renderable rend = new Renderable();
				rend.setup(
						//geometry values
						);
				return rend;
			}
		}
		//used by a layered renderer
		class Renderable { 
			void setup(float...v) {} 
			void update(float...v) {} 
		}

		@CommonComponent class Point extends Component {
			@ComponentField(description = "position", parameter = {"def","{0.0,0.0}v"}) 
			Vector2 pos;
			@ComponentField(description = "color", parameter = {"def","0"}) 
			int color;
			@ComponentField(description = "multi use factor", parameter = {"def","1.0f"}) 
			float factor;
		}
		@CommonComponent class Points extends Component {
			@ComponentField(description = "point collection", parameter = {}) 		
			@ComponentReference
			@MemberComponent
			Point[] points; 
		}
		@CommonComponent class Shape extends RegisteredComponent {
			@ComponentField(description = "a geometric shape composed of points", parameter = {}) 		
			@ComponentReference
			@MemberComponent	
			Points[] points; 
		}
		
//		class Poly extends Shape {} //points are summits of a polygon
//		@CommonComponent class Line extends Shape {} //Shape is a line between its points
//		@CommonComponent class Dots extends Shape {} //Shape is a circle a each points
		
		@CommonComponent class Transform extends Component {
			@ComponentField(description = "translation", parameter = {"def","{0.0,0.0}v"}) 
			Vector2 translate;
			@ComponentField(description = "rotation", parameter = {"def","0.0f"}) 
			float rotate;
			@ComponentField(description = "scaling", parameter = {"def","1.0f"}) 
			float scale;
		}
		@CommonComponent class ShapeUnit extends Component {
			@ComponentField(description = "a shape", parameter = {})	
			@ComponentReference
			@RequiredComponent
			Shape shape; 
			@ComponentField(description = "a transform to apply to the shape", parameter = {})	
			@ComponentReference
			@RequiredComponent
			@MemberComponent
			Transform transf; 
		}
		
		// PHYSICS
		
		@CommonComponent class FieldProperty extends Component {
			// density restitution ...
		}

		@CommonComponent class FieldDefinition extends Component {
			// field property + geom
		}

		@CommonComponent class BodyProperty extends Component {
			// dinamic / kynetic / static
		}

		@CommonComponent class BodyDefinition extends RegisteredComponent {
			// body property + field definition(s)
			// Body create() > new Body
		}
		
		@EntityComponent class Body extends Component {
			
		}
	}
	
	
}
