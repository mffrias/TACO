package ar.edu.taco.jdynalloy;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import ar.uba.dc.rfm.alloy.ast.formulas.AlloyFormula;
import ar.uba.dc.rfm.alloy.ast.formulas.JFormulaPrinter;

public class JDynAlloyClassDiagram implements Serializable {

	private static final long serialVersionUID = -24108282016136679L;

	private class CDSignature implements Serializable{
		static final long serialVersionUID = 998765432;
		private final String signatureId;
		private String extends_signature_id = null;
		private Set<String> in_signature_ids = new HashSet<String>();
		private Set<String> class_invariants = new HashSet<String>();
		private Set<String> object_invariants = new HashSet<String>();
		private Map<String, CDField> fields = new HashMap<String, CDField>();

		public CDSignature(String signatureId) {
			this.signatureId = signatureId;
		}

		public String getSignatureId() {
			return signatureId;
		}

		public void setExtends(String extendedSignatureId) {
			this.extends_signature_id = extendedSignatureId;
		}

		public void addSuperSetSignature(String superSetSignatureId) {
			in_signature_ids.add(superSetSignatureId);
		}

		public void addClassInvariant(AlloyFormula class_invariant) {
			JFormulaPrinter printer = new JFormulaPrinter();
			String class_invariant_str = (String) class_invariant.accept(printer);
			class_invariants.add(class_invariant_str);
		}

		public void addObjectInvariant(AlloyFormula object_invariant) {
			JFormulaPrinter printer = new JFormulaPrinter();
			String object_invariant_str = (String) object_invariant.accept(printer);
			object_invariants.add(object_invariant_str);
		}

		public void addField(String field_name, Set<String> target_signature_ids) {
			CDField cdfield = new CDField(field_name, target_signature_ids);
			fields.put(field_name, cdfield);
		}

		public CDField getField(String field_name) {
			return fields.get(field_name);
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((class_invariants == null) ? 0 : class_invariants.hashCode());
			result = prime * result + ((extends_signature_id == null) ? 0 : extends_signature_id.hashCode());
			result = prime * result + ((fields == null) ? 0 : fields.hashCode());
			result = prime * result + ((in_signature_ids == null) ? 0 : in_signature_ids.hashCode());
			result = prime * result + ((object_invariants == null) ? 0 : object_invariants.hashCode());
			result = prime * result + ((signatureId == null) ? 0 : signatureId.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			CDSignature other = (CDSignature) obj;
			if (class_invariants == null) {
				if (other.class_invariants != null)
					return false;
			} else if (!class_invariants.equals(other.class_invariants))
				return false;
			if (extends_signature_id == null) {
				if (other.extends_signature_id != null)
					return false;
			} else if (!extends_signature_id.equals(other.extends_signature_id))
				return false;
			if (fields == null) {
				if (other.fields != null)
					return false;
			} else if (!fields.equals(other.fields))
				return false;
			if (in_signature_ids == null) {
				if (other.in_signature_ids != null)
					return false;
			} else if (!in_signature_ids.equals(other.in_signature_ids))
				return false;
			if (object_invariants == null) {
				if (other.object_invariants != null)
					return false;
			} else if (!object_invariants.equals(other.object_invariants))
				return false;
			if (signatureId == null) {
				if (other.signatureId != null)
					return false;
			} else if (!signatureId.equals(other.signatureId))
				return false;
			return true;
		}


	}

	private class CDField implements Serializable {
		static final long serialVersionUID = 13243564;
		private final String fieldId;
		private final Set<String> targetSignatureIds;
		private Set<String> spec_fields = new HashSet<String>();

		public CDField(String fieldId, Set<String> targetSignatureIds) {
			this.fieldId = fieldId;
			this.targetSignatureIds = targetSignatureIds;
		}

		public void addAbstractionPred(AlloyFormula abstract_pred) {
			JFormulaPrinter printer = new JFormulaPrinter();
			String abstract_pred_str = (String) abstract_pred.accept(printer);
			spec_fields.add(abstract_pred_str);
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((fieldId == null) ? 0 : fieldId.hashCode());
			result = prime * result + ((spec_fields == null) ? 0 : spec_fields.hashCode());
			result = prime * result + ((targetSignatureIds == null) ? 0 : targetSignatureIds.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			CDField other = (CDField) obj;
			if (fieldId == null) {
				if (other.fieldId != null)
					return false;
			} else if (!fieldId.equals(other.fieldId))
				return false;
			if (spec_fields == null) {
				if (other.spec_fields != null)
					return false;
			} else if (!spec_fields.equals(other.spec_fields))
				return false;
			if (targetSignatureIds == null) {
				if (other.targetSignatureIds != null)
					return false;
			} else if (!targetSignatureIds.equals(other.targetSignatureIds))
				return false;
			return true;
		}
	}

	private Map<String, CDSignature> signatures = new HashMap<String, CDSignature>();

	private CDSignature getSignature(String signatureId) {
		if (!signatures.containsKey(signatureId)) {
			CDSignature sig = new CDSignature(signatureId);
			signatures.put(signatureId, sig);
		}
		return signatures.get(signatureId);
	}

	public void addSignatureId(String signatureId) {
		getSignature(signatureId);
	}

	public void addExtension(String extensionId, String extendedId) {
		CDSignature extensionSig = getSignature(extensionId);
		CDSignature extendedSig = getSignature(extendedId);
		extensionSig.setExtends(extendedSig.getSignatureId());
	}

	public void addSuperSignature(String subSetSignatureId, String superSetSignatureId) {
		CDSignature subSetSig = getSignature(subSetSignatureId);
		CDSignature superSetSig = getSignature(superSetSignatureId);
		subSetSig.addSuperSetSignature(superSetSig.getSignatureId());
	}

	public void addClassInvariant(String signatureId, AlloyFormula class_invariant) {
		CDSignature sig = getSignature(signatureId);
		sig.addClassInvariant(class_invariant);
	}

	public void addObjectInvariant(String signatureId, AlloyFormula object_invariant) {
		CDSignature sig = getSignature(signatureId);
		sig.addObjectInvariant(object_invariant);
	}

	public void addField(String field_name, String signatureId, Set<String> target_signatures) {
		CDSignature sig = getSignature(signatureId);
		Set<CDSignature> target_sigs = new HashSet<CDSignature>();
		for (String target_signature_id : target_signatures) {
			CDSignature target_sig = getSignature(target_signature_id);
			target_sigs.add(target_sig);
		}
		sig.addField(field_name, target_signatures);
	}

	public void addAbstractionPred(String signatureId, String field_name, AlloyFormula abstract_pred) {
		CDSignature sig = getSignature(signatureId);
		CDField cdfield = sig.getField(field_name);
		if (cdfield == null) {
			throw new IllegalArgumentException("Field " + field_name + " is not found in signature " + signatureId);
		}
		cdfield.addAbstractionPred(abstract_pred);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((signatures == null) ? 0 : signatures.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		JDynAlloyClassDiagram other = (JDynAlloyClassDiagram) obj;
		if (signatures == null) {
			if (other.signatures != null)
				return false;
		} else if (!signatures.equals(other.signatures))
			return false;
		return true;
	}

}
