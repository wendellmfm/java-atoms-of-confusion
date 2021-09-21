package br.ufc.mdcc.bohr.finder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.ufc.mdcc.bohr.model.AoC;
import br.ufc.mdcc.bohr.model.AoCInfo;
import br.ufc.mdcc.bohr.model.Dataset;
import br.ufc.mdcc.bohr.util.Util;
import spoon.processing.AbstractProcessor;
import spoon.reflect.code.BinaryOperatorKind;
import spoon.reflect.code.CtBinaryOperator;
import spoon.reflect.code.CtExpression;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.visitor.filter.TypeFilter;

public class InfixOperatorPrecedenceFinder extends AbstractProcessor<CtClass<?>> {
	private List<Integer> atomLines = new ArrayList<Integer>();

	public void process(CtClass<?> element) {
		if (Util.isValid(element)) {
			String qualifiedName = element.getQualifiedName();

			TypeFilter<CtExpression<?>> expressionFilter = new TypeFilter<CtExpression<?>>(CtExpression.class);

			for (CtExpression<?> expression : element.getElements(expressionFilter)) {
				
				CtExpression<?> fullExpression = getHighLevelParent(expression);
				
				try {
					if (isCandidate(fullExpression)) {
						int lineNumber = fullExpression.getPosition().getLine();
						if(!atomLines.contains(Integer.valueOf(lineNumber))) {
							atomLines.add(lineNumber);
							String snippet = fullExpression.getOriginalSourceFragment().getSourceCode();
							Dataset.store(qualifiedName, new AoCInfo(AoC.IOP, lineNumber, snippet));
						}
					}
					
				} catch (Exception e) {
					// TODO: handle exception
				}
			}
		}
	}

	private boolean isCandidate(CtExpression<?> expression) {

		if (hasCombinationOfArithmeticalOperators(expression)) {
			if(isMissingParenthesesInArithmeticalOperations(expression)) {
				return true;
			}
		}
		
		if (hasCombinationLogicalOperators(expression)) {
			if(isMissingParenthesesInLogicalOperations(expression)) {
				return true;
			}
		}

		return false;
	}

	private boolean hasCombinationOfArithmeticalOperators(CtExpression<?> expression) {
		TypeFilter<CtBinaryOperator<?>> binaryOprFilter = null;
		binaryOprFilter = new TypeFilter<CtBinaryOperator<?>>(CtBinaryOperator.class);
		
		Map<String, Integer> differentOperatorsMap = new HashMap<String, Integer>();
		differentOperatorsMap.put("PLUS", 0);
		differentOperatorsMap.put("MINUS", 0);
		differentOperatorsMap.put("MOD", 0);
		differentOperatorsMap.put("MUL", 0);
		differentOperatorsMap.put("DIV", 0);

		for (CtBinaryOperator<?> binaryOpr : expression.getElements(binaryOprFilter)) {
			switch (binaryOpr.getKind()) {
			case PLUS:
				differentOperatorsMap.put("PLUS", differentOperatorsMap.get("PLUS") + 1);
				break;

			case MINUS:
				differentOperatorsMap.put("MINUS", differentOperatorsMap.get("MINUS") + 1);
				break;

			case MOD:
				differentOperatorsMap.put("MOD", differentOperatorsMap.get("MOD") + 1);
				break;

			case MUL:
				differentOperatorsMap.put("MUL", differentOperatorsMap.get("MUL") + 1);
				break;

			case DIV:
				differentOperatorsMap.put("DIV", differentOperatorsMap.get("DIV") + 1);
				break;

			default:
				break;
			}
		}

		int firstSetOfOperators = 0;

		if (differentOperatorsMap.get("PLUS") > 0) {
			firstSetOfOperators++;
		}

		if (differentOperatorsMap.get("MINUS") > 0) {
			firstSetOfOperators++;
		}

		int secondSetOfOperators = 0;

		if (differentOperatorsMap.get("MUL") > 0) {
			secondSetOfOperators++;
		}

		if (differentOperatorsMap.get("DIV") > 0) {
			secondSetOfOperators++;
		}

		if (differentOperatorsMap.get("MOD") > 0) {
			secondSetOfOperators++;
		}

		return (firstSetOfOperators > 0) && (secondSetOfOperators > 0);
	}
	
	private boolean isMissingParenthesesInArithmeticalOperations(CtExpression<?> expression) {
		ArrayList<Boolean> results = new ArrayList<>();

		TypeFilter<CtBinaryOperator<?>> binaryOprFilter = null;
		binaryOprFilter = new TypeFilter<CtBinaryOperator<?>>(CtBinaryOperator.class);
		List<CtBinaryOperator<?>> elements = expression.getElements(binaryOprFilter);
		
		String leftHandOperand = "";
		String rightHandOperand = "";
		for (CtBinaryOperator<?> binaryOpr : elements) {
			leftHandOperand = binaryOpr.getLeftHandOperand().getOriginalSourceFragment().getSourceCode();
			rightHandOperand = binaryOpr.getRightHandOperand().getOriginalSourceFragment().getSourceCode();
			
			if(binaryOpr.getKind() == BinaryOperatorKind.MUL || (binaryOpr.getKind() == BinaryOperatorKind.DIV)) {
				String binaryExpression = binaryOpr.getOriginalSourceFragment().getSourceCode();
				if(binaryExpression.startsWith("(") && binaryExpression.endsWith(")")) {
					results.add(Boolean.FALSE);
				} else if((leftHandOperand.startsWith("(") && leftHandOperand.endsWith(")")) 
						|| (rightHandOperand.startsWith("(") && rightHandOperand.endsWith(")"))) {
					results.add(Boolean.FALSE);
				} else {
					results.add(Boolean.TRUE);
				}
			}
		}
		
		return results.contains(Boolean.TRUE);
	}
	
	private boolean hasCombinationLogicalOperators(CtExpression<?> expression) {
		TypeFilter<CtBinaryOperator<?>> binaryOprFilter = null;
		binaryOprFilter = new TypeFilter<CtBinaryOperator<?>>(CtBinaryOperator.class);
		
		Map<String, Integer> differentOperatorsMap = new HashMap<String, Integer>();
		differentOperatorsMap.put("AND", 0);
		differentOperatorsMap.put("OR", 0);

		for (CtBinaryOperator<?> binaryOpr : expression.getElements(binaryOprFilter)) {
			switch (binaryOpr.getKind()) {
			case AND:
				differentOperatorsMap.put("AND", differentOperatorsMap.get("AND") + 1);
				break;

			case OR:
				differentOperatorsMap.put("OR", differentOperatorsMap.get("OR") + 1);
				break;
				
			default:
				break;
			}
		}

		int andOperators = 0;

		if (differentOperatorsMap.get("AND") > 0) {
			andOperators++;
		}

		int orOperators = 0;

		if (differentOperatorsMap.get("OR") > 0) {
			orOperators++;
		}

		return (andOperators > 0) && (orOperators > 0);
	}
	
	private boolean isMissingParenthesesInLogicalOperations(CtExpression<?> expression) {
		TypeFilter<CtBinaryOperator<?>> binaryOprFilter = null;
		binaryOprFilter = new TypeFilter<CtBinaryOperator<?>>(CtBinaryOperator.class);
		List<CtBinaryOperator<?>> elements = expression.getElements(binaryOprFilter);
		
		for (CtBinaryOperator<?> binaryOpr : elements) {
			if(binaryOpr.getKind() == BinaryOperatorKind.AND) {
				
				String binaryExpression = binaryOpr.getOriginalSourceFragment().getSourceCode();
				
				boolean binaryOprCondition = binaryExpression.startsWith("(") && binaryExpression.endsWith(")");
				if(binaryOprCondition) {
					return false;
				}

				String leftHandOperand = binaryOpr.getLeftHandOperand().getOriginalSourceFragment().getSourceCode();
				String rightHandOperand = binaryOpr.getRightHandOperand().getOriginalSourceFragment().getSourceCode();
				
				boolean leftCondition = !(leftHandOperand.startsWith("(") && leftHandOperand.endsWith(")"));
				boolean rightCondition = !(rightHandOperand.startsWith("(") && rightHandOperand.endsWith(")"));
				
				if(leftCondition && rightCondition) {
					return true;
				}
				
			}
		}
		
		return false;
	}

	private CtExpression<?> getHighLevelParent(CtExpression<?> expression) {
		if ((expression.getParent() != null) && (expression.getParent() instanceof CtExpression<?>)) {
			return this.getHighLevelParent((CtExpression<?>) expression.getParent());
		} else {
			return expression;
		}
	}

}