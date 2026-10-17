package aa_nodulo;

import java.util.ArrayList;

import com.badlogic.gdx.math.Vector2;

import util.Utl;
import util.nMap;
import util.nRun;
import app.App;

public class pFamily {

	public static nMap<pFamily> body_families = new nMap<pFamily>();
	
	public static pFamily newFamily(String r) {
		if (body_families.hasKey(r)) {
			Utl.logn("ERROR: cant add family, <"+r+"> allready exist");
			return null;
		}
		pFamily p = new pFamily(r);
		return p;
	}
	public static pFamily getFamily(String r) {
		return body_families.get(r);
	}
	
	
	
	
	
	
	public String ref;
	public ArrayList<pProperty> props = new ArrayList<pProperty>();
	public nRun clear_run = null;
	
	public pFamily(String r) { 
		ref = r;  
		body_families.put(ref, this);
		
	}

	public pFamily setClearRun(nRun r) {
		clear_run = r;
		return this;
	}
	public pFamily addProp(String r) {
		pProperty p = pProperty.body_propertys.get(r);
		if (p != null) props.add(p);
		return this;
	}
	public boolean hasProp(String r) {
		for (pProperty p : props) if (p.ref.equals(r)) return true;
		return false;
	}

	public boolean contains(pBody b) {
		boolean valide = true;
		for (pProperty prp : props) {
			boolean found = false;
			for (pParam pr : b.params.all()) {
				if (pr.prop == prp) found = true;
			}
			valide = valide && found;
		}
		return valide;
	}
	
}
