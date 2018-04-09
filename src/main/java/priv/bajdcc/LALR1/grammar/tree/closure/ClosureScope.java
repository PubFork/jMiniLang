package priv.bajdcc.LALR1.grammar.tree.closure;

import java.util.HashSet;

/**
 * 闭包
 *
 * @author bajdcc
 */
public class ClosureScope implements IClosureScope {

	private HashSet<Object> ref = new HashSet<>();
	private HashSet<Object> decl = new HashSet<>();

	@Override
	public void addRef(Object obj) {
		ref.add(obj);
	}

	@Override
	public void addDecl(Object obj) {
		decl.add(obj);
	}

	protected HashSet<Object> getClosure() {
		HashSet<Object> closure = new HashSet<>();
		closure.addAll(ref);
		closure.removeAll(decl);
		return closure.isEmpty() ? null : closure;
	}
}
