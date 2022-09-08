package ar.edu.taco.simplejml;

import java.util.HashSet;
import java.util.Vector;
import ar.uba.dc.rfm.alloy.ast.expressions.ExprVariable;
import ar.uba.dc.rfm.alloy.ast.expressions.ExpressionVisitor;
import ar.uba.dc.rfm.alloy.ast.formulas.FormulaVisitor;

public class ResultVariableNamesCollectorVisitor extends ExpressionVisitor {
	
	HashSet<String> resultVariableNames = new HashSet<String>();
	
	public ResultVariableNamesCollectorVisitor(){}


	public Object visit(ExprVariable ev){
		if (ev.getVariable().getVariableId().getString().contains("ARG_result")){
			resultVariableNames.add(ev.getVariable().getVariableId().getString());
		}
		return null;
	}
	
	
	public HashSet<String> getResultVariableNames(){
		return resultVariableNames;
	}
	
	
}
