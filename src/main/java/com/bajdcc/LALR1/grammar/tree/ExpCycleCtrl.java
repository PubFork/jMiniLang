package com.bajdcc.LALR1.grammar.tree;

import com.bajdcc.LALR1.grammar.codegen.ICodegen;
import com.bajdcc.LALR1.grammar.semantic.ISemanticRecorder;
import com.bajdcc.LALR1.grammar.tree.closure.IClosureScope;
import com.bajdcc.util.lexer.token.KeywordType;
import com.bajdcc.util.lexer.token.Token;

/**
 * 【语义分析】循环控制表达式
 *
 * @author bajdcc
 */
public class ExpCycleCtrl implements IExp {

	/**
	 * 变量名
	 */
	private Token name = null;

	public Token getName() {
		return name;
	}

	public void setName(Token name) {
		this.name = name;
	}

	@Override
	public boolean isConstant() {
		return false;
	}

	@Override
	public boolean isEnumerable() {
		return false;
	}

	@Override
	public IExp simplify(ISemanticRecorder recorder) {
		return this;
	}

	@Override
	public void analysis(ISemanticRecorder recorder) {

	}

	@Override
	public void genCode(ICodegen codegen) {
		KeywordType keyword = (KeywordType) name.getObj();
		if (keyword == KeywordType.BREAK) {
			codegen.getBlockService().genBreak();
		} else {
			codegen.getBlockService().genContinue();
		}
	}

	@Override
	public String toString() {
		return print(new StringBuilder());
	}

	@Override
	public String print(StringBuilder prefix) {
		return name.toRealString();
	}

	@Override
	public void addClosure(IClosureScope scope) {

	}

	@Override
	public void setYield() {

	}
}