package ar.edu.taco.alloy.bound;

import java.util.LinkedList;
import java.util.List;
import java.util.TreeSet;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import ar.uba.dc.rfm.alloy.ast.expressions.AlloyExpression;
import ar.uba.dc.rfm.alloy.ast.expressions.ExprConstant;
import ar.uba.dc.rfm.alloy.ast.expressions.ExprJoin;
import ar.uba.dc.rfm.alloy.ast.expressions.ExprProduct;
import ar.uba.dc.rfm.alloy.ast.expressions.ExprUnion;

class UBoundSAXHandler extends DefaultHandler {

	private ExprJoin current_field = null;
	private int current_arity = 0;
	private TreeSet<AlloyExpression> current_tuple_set = null;
	private List<ExprConstant> current_atom_list = null;
	private List<UBound> upper_bounds;

	@Override
	public void startDocument() throws SAXException {
		upper_bounds = new LinkedList<UBound>();
		TupleComparator tuple_comparator = new TupleComparator();
		current_tuple_set = new TreeSet<AlloyExpression>(tuple_comparator);
		current_atom_list = new LinkedList<ExprConstant>();
	}

	@Override
	public void endDocument() throws SAXException {
		UBoundComparator upper_bound_comparator = new UBoundComparator();
		TreeSet<UBound> sorted_set = new TreeSet<UBound>(upper_bound_comparator);
		sorted_set.addAll(upper_bounds);
		upper_bounds.clear();
		upper_bounds.addAll(sorted_set);
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {

		if (qName.equals("field")) {
			// start new field
			String field_name = attributes.getValue("label");
			String current_arity_str = attributes.getValue("arity");
			current_arity = new Integer(current_arity_str);
			current_field = ExprJoin.join(ExprConstant.buildExprConstant("QF"), ExprConstant.buildExprConstant(field_name));
			current_tuple_set.clear();

		} else if (qName.equals("tuple")) {
			
			current_atom_list.clear();
			
		} else if (qName.equals("atom")) {

			String constant_id = attributes.getValue("label");
			ExprConstant atom_constant = ExprConstant.buildExprConstant(constant_id);
			current_atom_list.add(atom_constant);
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {

		if (qName.equals("field")) {

			UBound new_upper_bound;
			if (current_tuple_set.isEmpty()) {
				new_upper_bound = new UBound(current_field, buildExprProductNone());
			} else {
				new_upper_bound = new UBound(current_field, buildExprUnion(current_tuple_set));
			}

			upper_bounds.add(new_upper_bound);
			current_field = null;
			current_arity = 0;

		} else if (qName.equals("tuple")) {

			AlloyExpression tuple = buildExprProduct(current_atom_list);
			current_tuple_set.add(tuple);
		}

	}

	private AlloyExpression buildExprProduct(List<ExprConstant> atom_list) {
		AlloyExpression result = null;
		for (ExprConstant atom : atom_list) {
			if (result == null) {
				result = atom;
			} else {
				result = new ExprProduct(result, atom);
			}
		}
		return result;
	}

	private AlloyExpression buildExprUnion(TreeSet<AlloyExpression> tuple_set) {
		AlloyExpression result = null;
		for (AlloyExpression tuple : tuple_set) {
			if (result == null) {
				result = tuple;
			} else {
				result = ExprUnion.buildExprUnion(result, tuple);
			}
		}
		return result;
	}

	private AlloyExpression buildExprProductNone() {
		AlloyExpression result = null;

		for (int i = 0; i < current_arity; i++) {
			if (result == null) {
				result = ExprConstant.buildExprConstant("none");
			} else {
				result = new ExprProduct(result, ExprConstant.buildExprConstant("none"));
			}

		}

		return result;
	}

	public List<UBound> getUpperBounds() {
		return upper_bounds;
	}

}
