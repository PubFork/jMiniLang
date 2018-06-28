package com.bajdcc.LALR1.grammar.error;

import com.bajdcc.util.lexer.regex.IRegexStringIterator;
import com.bajdcc.util.lexer.token.Token;

/**
 * 【语义分析】语义错误结构
 *
 * @author bajdcc
 */
@SuppressWarnings("serial")
public class SemanticException extends Exception {

	/**
	 * 操作符
	 */
	private Token token;
	/**
	 * 错误类型
	 */
	private SemanticError kError;

	public SemanticException(SemanticError error, Token token) {
		super(error.getMessage());
		this.token = token;
		kError = error;
	}

	/**
	 * @return 错误位置
	 */
	public Token getPosition() {
		return token;
	}

	/**
	 * @return 错误类型
	 */
	public SemanticError getErrorCode() {
		return kError;
	}

	public String toString(IRegexStringIterator iter) {
		String snapshot = iter.ex().getErrorSnapshot(token.position);
		if (snapshot == null) {
			return toString();
		}
		return getMessage() + ": " + token + System.lineSeparator() +
				snapshot + System.lineSeparator();
	}

	@Override
	public String toString() {
		return getMessage() + ": " + token.toString();
	}

	/**
	 * 语义分析过程中的错误
	 */
	public enum SemanticError {
		UNKNOWN("未知"),
		INVALID_OPERATOR("操作非法"),
		MISSING_FUNCNAME("过程名不存在"),
		INVALID_FUNCNAME("过程名非法"),
		DUP_ENTRY("不能设置为入口函数名"),
		DUP_FUNCNAME("重复的函数名"),
		VARIABLE_NOT_DECLARAED("变量未定义"),
		VARIABLE_REDECLARAED("变量重复定义"),
		VAR_FUN_CONFLICT("变量名与函数名冲突"),
		MISMATCH_ARGS("参数个数不匹配"),
		DUP_PARAM("参数重复定义"),
		WRONG_EXTERN_SYMBOL("导出符号不存在"),
		WRONG_CYCLE("缺少循环体"),
		WRONG_YIELD("非法调用"),
		WRONG_ENUMERABLE("要求枚举对象"),
		TOO_MANY_ARGS("参数个数太多"),
		INVALID_ASSIGNMENT("非法的赋值语句"),
		DIFFERENT_FUNCNAME("过程名不匹配");

		private String message;

		SemanticError(String message) {
			this.message = message;
		}

		public String getMessage() {
			return message;
		}

		public void setMessage(String message) {
			this.message = message;
		}
	}
}
