package com.bajdcc.LALR1.grammar.tree;

import com.bajdcc.LALR1.grammar.codegen.ICodegen;
import com.bajdcc.LALR1.grammar.error.SemanticException.SemanticError;
import com.bajdcc.LALR1.grammar.runtime.RuntimeInst;
import com.bajdcc.LALR1.grammar.runtime.RuntimeInstUnary;
import com.bajdcc.LALR1.grammar.semantic.ISemanticRecorder;
import com.bajdcc.LALR1.grammar.tree.closure.IClosureScope;
import com.bajdcc.util.lexer.token.KeywordType;
import com.bajdcc.util.lexer.token.Token;

import java.util.ArrayList;
import java.util.List;

/**
 * 【语义分析】函数调用表达式
 *
 * @author bajdcc
 */
public class ExpInvoke implements IExp {

	/**
	 * 调用名
	 */
	private Token name = null;

	/**
	 * 调用函数
	 */
	private Function func = null;

	/**
	 * 外部函数名
	 */
	private Token extern = null;

	/**
	 * 参数
	 */
	private List<IExp> params = new ArrayList<>();

	/**
	 * 是否为函数指针调用
	 */
	private boolean invoke = false;

	/**
	 * 是否为YIELD调用
	 */
	private boolean yield = false;

	/**
	 * 函数名是否为函数表达式
	 */
	private ExpInvoke invokeExp = null;

	public Token getName() {
		return name;
	}

	public void setName(Token name) {
		this.name = name;
	}

	public Function getFunc() {
		return func;
	}

	public void setFunc(Function func) {
		this.func = func;
	}

	public Token getExtern() {
		return extern;
	}

	public void setExtern(Token extern) {
		this.extern = extern;
	}

	public List<IExp> getParams() {
		return params;
	}

	public void setParams(List<IExp> params) {
		this.params = params;
	}

	public boolean isInvoke() {
		return invoke;
	}

	public void setInvoke(boolean invoke) {
		this.invoke = invoke;
	}

	public boolean isYield() {
		return yield;
	}

	public void setYield(boolean yield) {
		this.yield = yield;
	}

	public ExpInvoke getInvokeExp() {
		return invokeExp;
	}

	public void setInvokeExp(ExpInvoke invokeExp) {
		this.invokeExp = invokeExp;
	}

	@Override
	public boolean isConstant() {
		return false;
	}

	@Override
	public boolean isEnumerable() {
		return func == null || func.isEnumerable();
	}

	@Override
	public IExp simplify(ISemanticRecorder recorder) {
		for (int i = 0; i < params.size(); i++) {
			params.set(i, params.get(i).simplify(recorder));
		}
		return this;
	}

	@Override
	public void analysis(ISemanticRecorder recorder) {
		if (func != null && !func.isExtern()) {
			checkArgsCount(recorder);
			if (func.getRealName().startsWith("~")) {
				func.analysis(recorder);
			}
			if (func.isYield() ^ yield) {
				recorder.add(SemanticError.WRONG_YIELD, name);
			}
		}
		for (IExp exp : params) {
			exp.analysis(recorder);
		}
	}

	/**
	 * 参数个数检查
	 *
	 * @param recorder 错误记录
	 */
	private void checkArgsCount(ISemanticRecorder recorder) {
		int invokeArgsCount = params.size();
		int funcArgsCount = func.getParams().size();
		if (invokeArgsCount != funcArgsCount) {
			recorder.add(SemanticError.MISMATCH_ARGS, name);
		}
	}

	@Override
	public void genCode(ICodegen codegen) {
		if (yield) {
			int yldLine = codegen.getCodeIndex();
			RuntimeInstUnary yld = codegen.genCode(RuntimeInst.ijyld, -1);
			if (func != null) {
				codegen.genCode(RuntimeInst.ipush, 1); // call本地地址，1
				codegen.genCode(RuntimeInst.iyldi);
				for (IExp exp : params) {
					exp.genCode(codegen);
					codegen.genCode(RuntimeInst.iyldi);
				}
				codegen.genCodeWithFuncWriteBack(RuntimeInst.ipush,
						codegen.getFuncIndex(func));
				codegen.genCode(RuntimeInst.iyldi);
			} else {
				if (invoke) {
					codegen.genCode(RuntimeInst.ipush, 2); // call本地符号，2
					codegen.genCode(RuntimeInst.iyldi);
				} else {
					codegen.genCode(RuntimeInst.ipush, 3); // call外部模块，3
					codegen.genCode(RuntimeInst.iyldi);
				}
				for (IExp exp : params) {
					exp.genCode(codegen);
					codegen.genCode(RuntimeInst.iyldi);
				}
				codegen.genCode(RuntimeInst.ipush,
						codegen.genDataRef(extern.getObj()));
				codegen.genCode(RuntimeInst.iyldi);
			}
			codegen.genCode(RuntimeInst.iyldy, yldLine);
			codegen.genCode(RuntimeInst.iyldo);
			RuntimeInstUnary jmp = codegen.genCode(RuntimeInst.ijmp, -1);
			yld.op1 = codegen.getCodeIndex();
			codegen.genCode(RuntimeInst.iyldr, yldLine);
			codegen.genCode(RuntimeInst.iyldo);
			jmp.op1 = codegen.getCodeIndex();
		} else if (invokeExp != null) {
			invokeExp.genCode(codegen);
			codegen.genCode(RuntimeInst.iopena);
			for (IExp exp : params) {
				exp.genCode(codegen);
				codegen.genCode(RuntimeInst.ipusha);
			}
			codegen.genCode(RuntimeInst.ipush, -1);
			codegen.genCode(RuntimeInst.ically);
		} else {
			codegen.genCode(RuntimeInst.iopena);
			for (IExp exp : params) {
				exp.genCode(codegen);
				codegen.genCode(RuntimeInst.ipusha);
			}
			if (func != null) {
				codegen.genCodeWithFuncWriteBack(RuntimeInst.ipush,
						codegen.getFuncIndex(func));
				codegen.genCode(RuntimeInst.icall);
			} else {
				codegen.genCode(RuntimeInst.ipush,
						codegen.genDataRef(extern.getObj()));
				if (invoke) {
					codegen.genCode(RuntimeInst.ically);
				} else {
					codegen.genCode(RuntimeInst.icallx);
				}
			}
		}
	}

	@Override
	public String toString() {
		return print(new StringBuilder());
	}

	@Override
	public String print(StringBuilder prefix) {
		StringBuilder sb = new StringBuilder();
		if (yield) {
			sb.append(KeywordType.YIELD.getDesc());
			sb.append(" ");
		}
		sb.append(KeywordType.CALL.getDesc()).append(" ");
		if (func != null) {
			if (!func.getRealName().startsWith("~")) {
				sb.append(func.getRealName());
			} else {
				sb.append(func.print(prefix));
			}
		} else if (invokeExp != null) {
			sb.append("( ");
			sb.append(invokeExp.print(prefix));
			sb.append(" )");
		} else{
			sb.append(KeywordType.EXTERN.getDesc());
			sb.append(" ");
			sb.append(extern.toRealString());
		}
		if (!params.isEmpty()) {
			sb.append("( ");
			if (params.size() == 1) {
				sb.append(params.get(0).print(prefix));
			} else {
				for (int i = 0; i < params.size(); i++) {
					sb.append(params.get(i).print(prefix));
					if (i != params.size() - 1) {
						sb.append(", ");
					}
				}
			}
			sb.append(" )");
		}
		return sb.toString();
	}

	@Override
	public void addClosure(IClosureScope scope) {
		if (invoke) {
			scope.addRef(extern.getObj());
		}
		if (invokeExp != null) {
			invokeExp.addClosure(scope);
		}
		for (IExp exp : params) {
			exp.addClosure(scope);
		}
	}

	@Override
	public void setYield() {
		yield = true;
	}
}
