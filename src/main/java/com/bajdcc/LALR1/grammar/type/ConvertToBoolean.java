package com.bajdcc.LALR1.grammar.type;

import com.bajdcc.util.lexer.token.Token;
import com.bajdcc.util.lexer.token.TokenType;

/**
 * 【类型转换】转换为布尔类型
 *
 * @author bajdcc
 */
public class ConvertToBoolean implements ITokenConventer {

	@Override
	public Token convert(Token token) {
		switch (token.getType()) {
			case CHARACTER:
			case STRING:
			case DECIMAL:
			case INTEGER:
				token.setObj(getBooleanValue(token));
				token.setType(TokenType.BOOL);
				break;
			default:
				break;
		}
		return token;
	}

	/**
	 * 强制布尔转换（值）
	 *
	 * @param token 操作数
	 * @return 转换结果
	 */
	private static boolean getBooleanValue(Token token) {
		switch (token.getType()) {
			case BOOL:
				return (boolean) token.getObj();
			case CHARACTER:
				char ch = (char) token.getObj();
				return ch != 0;
			case STRING:
				return true;
			case DECIMAL:
				double decimal = (double) token.getObj();
				return decimal != 0D;
			case INTEGER:
				long integer = (long) token.getObj();
				return integer != 0L;
			default:
				break;
		}
		return false;
	}
}
