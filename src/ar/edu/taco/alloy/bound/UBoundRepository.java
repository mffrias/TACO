package ar.edu.taco.alloy.bound;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;

import ar.edu.taco.TacoConfigurator;
import ar.edu.taco.TacoException;
import ar.edu.taco.alloy.AlloyScope;
import ar.edu.taco.jdynalloy.JDynAlloyClassDiagram;

class UBoundRepository {

	private static class UpperBoundXML implements Serializable {
		static final long serialVersionUID = 345678345;
		
		private String xmlDoc;

		public UpperBoundXML(String xmlDoc) {
			this.xmlDoc = xmlDoc;
		}
	}

	private UBoundRepository() {

	}

	private static UBoundRepository instance = null;

	public static UBoundRepository getInstance() {
		if (instance == null) {
			instance = new UBoundRepository();
		}
		return instance;
	}

	private UpperBoundXML look_up_repository(String entry_class_name, String node_class_name, AlloyScope alloy_scope) {

		String class_to_check = TacoConfigurator.getInstance().getClassToCheck();

		if (class_to_check.equals(entry_class_name)) {
			if (alloy_scope.getScopeOf(entry_class_name) == 1) {
				int scope = alloy_scope.getScopeOf(node_class_name);
				String xml_filename = "xml" + java.io.File.separator + entry_class_name + "-" + (scope < 10 ? "0" : "") + scope + ".xml";
				return new UpperBoundXML(xml_filename);
			}
		}

		return null;
	}

	private UpperBoundXML look_up_roops_repository(String class_to_check, AlloyScope alloy_scope) {

		Map<String, String> roops_case_studies = new HashMap<String, String>();
		roops_case_studies.put("roops_core_objects_AvlTree", "roops_core_objects_AvlNode");
		roops_case_studies.put("roops_core_objects_BinomialHeap", "roops_core_objects_BinomialHeapNode");
		roops_case_studies.put("roops_core_objects_BinTree", "roops_core_objects_BinTreeNode");
		roops_case_studies.put("roops_core_objects_FibHeap", "roops_core_objects_FibHeapNode");
		roops_case_studies.put("roops_core_objects_LinkedList", "roops_core_objects_LinkedListNode");
		roops_case_studies.put("roops_core_objects_NodeCachingLinkedList", "roops_core_objects_LinkedListNode");
		roops_case_studies.put("roops_core_objects_SinglyLinkedList", "roops_core_objects_SinglyLinkedListNode");
		roops_case_studies.put("roops_core_objects_TreeSet", "roops_core_objects_TreeSetEntry");

		if (roops_case_studies.containsKey(class_to_check)) {
			return look_up_repository(class_to_check, roops_case_studies.get(class_to_check), alloy_scope);
		} else
			return null;
	}

	private UpperBoundXML findUpperBoundXML(JDynAlloyClassDiagram class_diagram, AlloyScope alloy_scope) {
		String class_to_check = TacoConfigurator.getInstance().getClassToCheck();
		UpperBoundXML xml_doc = look_up_roops_repository(class_to_check, alloy_scope);
		return xml_doc;
	}

	public List<UBound> getUpperBound(JDynAlloyClassDiagram class_diagram, AlloyScope alloy_scope) {

		UpperBoundXML upperBoundXML = findUpperBoundXML(class_diagram, alloy_scope);

		if (upperBoundXML != null) {
			File file_xml = new File(upperBoundXML.xmlDoc);
			if (!file_xml.exists()) {
				return null;
			}
			List<UBound> upper_bounds = readUpperBoundXML(upperBoundXML);

			return upper_bounds;
		} else
			return null;
	}

	private List<UBound> readUpperBoundXML(UpperBoundXML upperBoundXML) {

		try {
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser saxParser = factory.newSAXParser();
			UBoundSAXHandler upper_bound_handler = new UBoundSAXHandler();
			saxParser.parse(upperBoundXML.xmlDoc, upper_bound_handler);
			return upper_bound_handler.getUpperBounds();

		} catch (ParserConfigurationException e) {
			throw new TacoException("readUpperBoundXML: " + e);
		} catch (SAXException e) {
			throw new TacoException("readUpperBoundXML: " + e);
		} catch (IOException e) {
			throw new TacoException("readUpperBoundXML: " + e);
		}
	}

}
