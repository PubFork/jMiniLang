package com.bajdcc.OP.syntax.exp;

import com.bajdcc.OP.syntax.ISyntaxComponent;
import com.bajdcc.OP.syntax.ISyntaxComponentVisitor;
import com.bajdcc.util.VisitBag;

import java.util.ArrayList;

/**
 * 文法规则（序列）
 *
 * @author bajdcc
 */
public class SequenceExp implements ISyntaxComponent, IExpCollction {

	/**
	 * 子表达式表
	 */
	public ArrayList<ISyntaxComponent> arrExpressions = new ArrayList<>();

	@Override
	public void visit(ISyntaxComponentVisitor visitor) {
		VisitBag bag = new VisitBag();
		visitor.visitBegin(this, bag);
		if (bag.getVisitChildren()) {
			for (ISyntaxComponent exp : arrExpressions) {
				exp.visit(visitor);
			}
		}
		if (bag.getVisitEnd()) {
			visitor.visitEnd(this);
		}
	}

	@Override
	public void visitReverse(ISyntaxComponentVisitor visitor) {
		VisitBag bag = new VisitBag();
		visitor.visitBegin(this, bag);
		if (bag.getVisitChildren()) {
			for (int i = arrExpressions.size() - 1; i >= 0; i--) {
				arrExpressions.get(i).visitReverse(visitor);
			}
		}
		if (bag.getVisitEnd()) {
			visitor.visitEnd(this);
		}
	}

	@Override
	public void add(ISyntaxComponent exp) {
		arrExpressions.add(exp);
	}

	@Override
	public boolean isEmpty() {
		return arrExpressions.isEmpty();
	}
}
