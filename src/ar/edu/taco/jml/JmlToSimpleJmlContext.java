package ar.edu.taco.jml;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * What the JML simplification ({@code engine.JmlStage}) tells the later stages: the field
 * renamings, old name to new name (an instance field {@code f} becomes {@code <class>_<f>};
 * static fields keep their names but are registered too). {@code FNBlockVisitor}
 * registers them in the context its {@link ASTSimplifierManager} owns; {@code JmlStage} hands it
 * to {@code TacoMain}, which gives it to {@code JfslStage}, which renames the fields of JFSL
 * annotations with it. Keyed by the bare old name: two classes with a field of the same name
 * share one entry, the last one registered. One context per run; no static state.
 */
public class JmlToSimpleJmlContext {

	private final Map<String, String> renaming = new HashMap<String, String>();

	/**
	 * Records that the field {@code oldName} is now {@code newName}, replacing an earlier entry
	 * for {@code oldName}.
	 *
	 * @param oldName the field's name in the source
	 * @param newName its name after the simplification
	 */
	public void register_jml_to_simplejml_rename(String oldName, String newName) {
		this.renaming.put(oldName, newName);
	}

	/**
	 * Returns the old names registered so far.
	 *
	 * @return a live view of the map's keys, not a copy
	 */
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
