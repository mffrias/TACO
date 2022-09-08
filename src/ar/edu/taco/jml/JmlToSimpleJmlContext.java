package ar.edu.taco.jml;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class JmlToSimpleJmlContext {

	private final Map<String, String> renaming = new HashMap<String, String>();

	public void register_jml_to_simplejml_rename(String oldName, String newName) {
		this.renaming.put(oldName, newName);
	}

	public Set<String> get_old_names() {
		return this.renaming.keySet();
	}

	public boolean containsOldName(String old_name) {
		return this.renaming.containsKey(old_name);
	}

	public String getNewName(String old_name) {
		return this.renaming.get(old_name);
	}

}
