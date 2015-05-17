package priv.bajdcc.LALR1.grammar.tree;

import priv.bajdcc.LALR1.grammar.codegen.ICodegen;
import priv.bajdcc.LALR1.grammar.runtime.RuntimeInst;
import priv.bajdcc.LALR1.grammar.semantic.ISemanticRecorder;
import priv.bajdcc.util.lexer.token.KeywordType;
import priv.bajdcc.util.lexer.token.OperatorType;
import priv.bajdcc.util.lexer.token.Token;

/**
 * 【语义分析】赋值表达式
 *
 * @author bajdcc
 */
public class ExpAssign implements IExp {

	/**
	 * 变量名
	 */
	private Token name = null;

	/**
	 * 表达式
	 */
	private IExp exp = null;

	/**
	 * 限定符
	 */
	private Token spec = null;

	/**
	 * 是否为声明
	 */
	private boolean decleared = false;

	public Token getName() {
		return name;
	}

	public void setName(Token name) {
		this.name = name;
	}

	public IExp getExp() {
		return exp;
	}

	public void setExp(IExp exp) {
		this.exp = exp;
	}

	public Token getSpec() {
		return spec;
	}

	public void setSpec(Token spec) {
		this.spec = spec;
	}

	public boolean isDecleared() {
		return decleared;
	}

	public void setDecleared(boolean decleared) {
		this.decleared = decleared;
	}

	@Override
	public boolean isConstant() {
		return false;
	}

	@Override
	public IExp simplify(ISemanticRecorder recorder) {
		return this;
	}

	@Override
	public void analysis(ISemanticRecorder recorder) {
		exp.analysis(recorder);
	}

	@Override
	public void genCode(ICodegen codegen) {
		exp.genCode(codegen);
		codegen.genCode(RuntimeInst.ipush, codegen.genDataRef(name.object));
		KeywordType keyword = (KeywordType) spec.object;
		if (keyword == KeywordType.LET) {
			codegen.genCode(RuntimeInst.istore);
		} else {
			codegen.genCode(RuntimeInst.ialloc);
		}
	}

	@Override
	public String toString() {
		return print(new StringBuilder());
	}

	@Override
	public String print(StringBuilder prefix) {
		StringBuilder sb = new StringBuilder();
		sb.append(spec.toRealString());
		sb.append(" " + name.toRealString());
		sb.append(" " + OperatorType.ASSIGN.getName() + " ");
		sb.append(exp.print(prefix));
		return sb.toString();
	}
}