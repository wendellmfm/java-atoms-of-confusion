package br.ufc.mdcc.bohr.finder;

import br.ufc.mdcc.bohr.model.AoC;
import br.ufc.mdcc.bohr.model.AoCInfo;
import br.ufc.mdcc.bohr.model.Dataset;
import br.ufc.mdcc.bohr.util.Util;
import spoon.SpoonException;
import spoon.processing.AbstractProcessor;
import spoon.reflect.code.CtAssignment;
import spoon.reflect.code.CtBinaryOperator;
import spoon.reflect.code.CtLocalVariable;
import spoon.reflect.code.CtUnaryOperator;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.visitor.filter.TypeFilter;

public class PreAndPostIncrementDecrementFinder extends AbstractProcessor<CtClass<?>> {

	public void process(CtClass<?> element) {
		if (Util.isValid(element)) {
			String qualifiedName = element.getQualifiedName();

			TypeFilter<CtUnaryOperator<?>> unaryOprFilter = new TypeFilter<CtUnaryOperator<?>>(CtUnaryOperator.class);

			for (CtUnaryOperator<?> unaryOpr : element.getElements(unaryOprFilter)) {
				
				CtElement parent = unaryOpr.getParent();
				if ((parent != null)) {
					
					if (parent instanceof CtBinaryOperator
							|| parent instanceof CtAssignment
							|| parent instanceof CtLocalVariable) {
						
						try {
							int lineNumber = parent.getPosition().getEndLine();
							String snippet = parent.prettyprint();
							
							save(qualifiedName, unaryOpr, lineNumber, snippet);
							
						} catch (SpoonException e) {
							// TODO: handle exception
						}
					}

				}
			}
		}

	}

	private void save(String qualifiedName, CtUnaryOperator<?> unaryOpr, int lineNumber, String snippet) {

		switch (unaryOpr.getKind()) {
		case POSTDEC:
			Dataset.store(qualifiedName, new AoCInfo(AoC.PostDEC, lineNumber, snippet));
			break;
			
		case POSTINC:
			Dataset.store(qualifiedName, new AoCInfo(AoC.PostINC, lineNumber, snippet));
			break;
		
		case PREDEC:
			Dataset.store(qualifiedName, new AoCInfo(AoC.PreDEC, lineNumber, snippet));
			break;
		
		case PREINC:
			Dataset.store(qualifiedName, new AoCInfo(AoC.PreINC, lineNumber, snippet));
			break;

		default:
			break;
		}
	}
}
