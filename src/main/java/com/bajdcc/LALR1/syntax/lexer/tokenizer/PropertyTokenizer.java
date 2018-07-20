package com.bajdcc.LALR1.syntax.lexer.tokenizer;

import com.bajdcc.util.lexer.algorithm.TokenAlgorithm;
import com.bajdcc.util.lexer.algorithm.filter.StringPairFilter;
import com.bajdcc.util.lexer.error.RegexException;
import com.bajdcc.util.lexer.regex.IRegexStringIterator;
import com.bajdcc.util.lexer.token.MetaType;
import com.bajdcc.util.lexer.token.Token;
import com.bajdcc.util.lexer.token.TokenType;

/**
 * 终结符解析
 *
 * @author bajdcc
 */
public class PropertyTokenizer extends TokenAlgorithm {

	public PropertyTokenizer() throws RegexException {
		super(getRegexString(), new StringPairFilter(MetaType.LBRACE, MetaType.RBRACE));
	}

	public static String getRegexString() {
		return "{.*}";
	}

	/* （非 Javadoc）
	 * @see com.bajdcc.lexer.algorithm.ITokenAlgorithm#getToken(java.lang.String, com.bajdcc.lexer.token.Token)
	 */
	@Override
	public Token getToken(String string, Token token, IRegexStringIterator iterator) {
		token.setType(TokenType.MACRO);
		token.setObj(string);
		return token;
	}
}
