package com.bajdcc.util.lexer.algorithm.impl;

import com.bajdcc.util.lexer.algorithm.TokenAlgorithm;
import com.bajdcc.util.lexer.error.RegexException;
import com.bajdcc.util.lexer.regex.IRegexStringIterator;
import com.bajdcc.util.lexer.token.Token;
import com.bajdcc.util.lexer.token.TokenType;

import java.math.BigDecimal;


/**
 * 数字解析
 *
 * @author bajdcc
 */
public class NumberTokenizer extends TokenAlgorithm {

	public NumberTokenizer() throws RegexException {
		super(getRegexString(), null);
	}

	public static String getRegexString() {
		return "[+-]?(\\d*\\.?\\d+|\\d+\\.?\\d*|0x[0-9ABCDEFabcdef]{1,4})([eE][+-]?\\d+)?";
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
		if (string.startsWith("0x")) {
			try {
				token.object = Long.parseLong(string.substring(2).toLowerCase(), 0x10);
				token.kToken = TokenType.INTEGER;
			} catch (NumberFormatException e) {
				e.printStackTrace();
				return null;
			}
		} else {
			try {
				BigDecimal decimal = new BigDecimal(string);
				token.object = decimal;
				if (string.indexOf('.') == -1) {
					token.object = decimal.toBigIntegerExact().longValue();
					token.kToken = TokenType.INTEGER;
				} else {
					token.object = decimal.doubleValue();
					token.kToken = TokenType.DECIMAL;
				}
			} catch (ArithmeticException | NumberFormatException e) {
				e.printStackTrace();
				return null;
			}
		}
		return token;
	}
}
