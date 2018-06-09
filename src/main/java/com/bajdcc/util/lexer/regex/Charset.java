package com.bajdcc.util.lexer.regex;

import com.bajdcc.util.lexer.token.MetaType;

import java.util.ArrayList;

/**
 * 字符集
 *
 * @author bajdcc
 */
public class Charset implements IRegexComponent {

	/**
	 * 包含的范围（正范围）
	 */
	public ArrayList<CharacterRange> arrPositiveBounds = new ArrayList<>();

	/**
	 * 是否取反
	 */
	public boolean bReverse = false;

	@Override
	public void visit(IRegexComponentVisitor visitor) {
		visitor.visitBegin(this);
		visitor.visitEnd(this);
	}

	/**
	 * 添加范围
	 *
	 * @param begin 上限
	 * @param end   下限
	 * @return 是否添加成功
	 */
	public boolean addRange(char begin, char end) {
		if (begin > end) {
			return false;
		}
		for (CharacterRange range : arrPositiveBounds) {
			if (begin <= range.chLowerBound && end >= range.chUpperBound)
				return false;
		}
		arrPositiveBounds.add(new CharacterRange(begin, end));
		return true;
	}

	/**
	 * 添加字符
	 *
	 * @param ch 字符
	 * @return 添加是否有效
	 */
	public boolean addChar(char ch) {
		return addRange(ch, ch);
	}

	/**
	 * 当前字符集是否包含指定字符
	 *
	 * @param ch 字符
	 * @return 查找结果
	 */
	public boolean include(char ch) {
		for (CharacterRange range : arrPositiveBounds) {
			if (range.include(ch)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		boolean comma = false;
		for (CharacterRange range : arrPositiveBounds) {
			if (comma)
				sb.append(MetaType.COMMA.getChar());
			sb.append(range);
			if (!comma)
				comma = true;
		}
		return sb.toString();
	}
}
