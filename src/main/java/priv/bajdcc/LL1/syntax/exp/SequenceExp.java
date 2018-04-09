package priv.bajdcc.LL1.syntax.exp;

import priv.bajdcc.LL1.syntax.ISyntaxComponent;
import priv.bajdcc.LL1.syntax.ISyntaxComponentVisitor;
import priv.bajdcc.util.VisitBag;

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
		if (bag.bVisitChildren) {
			for (ISyntaxComponent exp : arrExpressions) {
				exp.visit(visitor);
			}
		}
		if (bag.bVisitEnd) {
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
