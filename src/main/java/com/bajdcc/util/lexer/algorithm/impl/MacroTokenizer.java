package com.bajdcc.util.lexer.algorithm.impl;

import com.bajdcc.util.lexer.algorithm.TokenAlgorithm;
import com.bajdcc.util.lexer.algorithm.filter.LineFilter;
import com.bajdcc.util.lexer.error.RegexException;
import com.bajdcc.util.lexer.regex.IRegexStringIterator;
import com.bajdcc.util.lexer.token.Token;
import com.bajdcc.util.lexer.token.TokenType;

/**
 * 宏语句解析
 *
 * @author bajdcc
 */
public class MacroTokenizer extends TokenAlgorithm {

	public MacroTokenizer() throws RegexException {
		super(getRegexString(), new LineFilter());
	}

	public static String getRegexString() {
		return "#(([^\\r\\n])*)([\\r\\n]{1,2})";
	}

	@Override
	public boolean getGreedMode() {
		return true;
	}

	/*
	 * （非 Javadoc）
	 *
	 * @see
	 * com.bajdcc.lexer.algorithm.ITokenAlgorithm#getToken(java.lang.String,
	 * com.bajdcc.lexer.token.Token)
	 */
	@Override
	public Token getToken(String string, Token token, IRegexStringIterator iterator) {
		token.setType(TokenType.MACRO);
		token.setObj(string.trim());
		return token;
	}
}
