package com.bajdcc.LALR1.semantic.test;

import com.bajdcc.LALR1.semantic.Semantic;
import com.bajdcc.LALR1.semantic.token.ISemanticAnalyzer;
import com.bajdcc.LALR1.syntax.handler.SyntaxException;
import com.bajdcc.util.lexer.error.RegexException;
import com.bajdcc.util.lexer.token.OperatorType;
import com.bajdcc.util.lexer.token.Token;
import com.bajdcc.util.lexer.token.TokenType;

@SuppressWarnings("unused")
public class TestOperator {

	public static void main(String[] args) {
		// System.out.println("Z -> `a`<,> | B | [`a` `b` Z B]");
		try {
			// Scanner scanner = new Scanner(System.in);
			// String expr = "( i )";
			// String expr = "((3))+4*5+66/(3-8)";
			// String expr = "(4 * 7 - 13) * (3 + 18 / 3 - 3)";
			String expr = "(5 + 5)";
			Semantic semantic = new Semantic(expr);
			semantic.addTerminal("PLUS", TokenType.OPERATOR, OperatorType.PLUS);
			semantic.addTerminal("MINUS", TokenType.OPERATOR,
					OperatorType.MINUS);
			semantic.addTerminal("TIMES", TokenType.OPERATOR,
					OperatorType.TIMES);
			semantic.addTerminal("DIVIDE", TokenType.OPERATOR,
					OperatorType.DIVIDE);
			semantic.addTerminal("LPA", TokenType.OPERATOR, OperatorType.LPARAN);
			semantic.addTerminal("RPA", TokenType.OPERATOR, OperatorType.RPARAN);
			semantic.addTerminal("INTEGER", TokenType.INTEGER, null);
			semantic.addNonTerminal("Z");
			semantic.addNonTerminal("E");
			semantic.addNonTerminal("T");
			semantic.addNonTerminal("F");
			semantic.addActionHandler("enter_paran", (indexed, manage, access, recorder) -> System.out.println("enter"));
			semantic.addActionHandler("leave_paran", (indexed, manage, access, recorder) -> System.out.println("leave"));
			semantic.addErrorHandler("lost_exp", (iterator, bag) -> {
				bag.bRead = false;
				bag.bPass = true;
				return "表达式不完整";
			});
			semantic.addErrorHandler("lost_exp_right", (iterator, bag) -> {
				bag.bRead = false;
				bag.bPass = true;
				return "缺少右括号";
			});
			ISemanticAnalyzer handleCopy = (indexed, query, recorder) -> indexed.get(0).object;
			ISemanticAnalyzer handleBinop = (indexed, query, recorder) -> {
				int lop = Integer
						.parseInt(indexed.get(0).object.toString());
				int rop = Integer
						.parseInt(indexed.get(2).object.toString());
				Token op = indexed.get(1).token;
				if (op.kToken == TokenType.OPERATOR) {
					OperatorType kop = (OperatorType) op.object;
					switch (kop) {
						case PLUS:
							return lop + rop;
						case MINUS:
							return lop - rop;
						case TIMES:
							return lop * rop;
						case DIVIDE:
							if (rop == 0) {
								return lop;
							} else {
								return lop / rop;
							}
						default:
							return 0;
					}
				} else {
					return 0;
				}
			};
			ISemanticAnalyzer handleValue = (indexed, query, recorder) -> indexed.get(0).token.object;
			// syntax.infer("E -> T `PLUS`<+> E | T `MINUS`<-> E | T");
			// syntax.infer("T -> F `TIMES`<*> T | F `DIVIDE`</> T | F");
			// syntax.infer("F -> `LPA`<(> E `RPA`<)>  | `SYMBOL`<i>");
			semantic.infer(handleCopy, "Z -> E[0]");
			semantic.infer(handleCopy, "E -> T[0]");
			semantic.infer(handleBinop,
					"E -> E[0] ( @PLUS[1]<+> | @MINUS[1]<-> ) T[2]{lost_exp}");
			semantic.infer(handleCopy, "T -> F[0]");
			semantic.infer(handleBinop,
					"T -> T[0] ( @TIMES[1]<*> | @DIVIDE[1]</> ) F[2]{lost_exp}");
			semantic.infer(handleValue, "F -> @INTEGER[0]<integer>");
			semantic.infer(handleCopy,
					"F -> @LPA#enter_paran#<(> E[0]{lost_exp} @RPA#leave_paran#{lost_exp_right}<)>");
			semantic.initialize("Z");
			// System.out.println(semantic.toString());
			// System.out.println(semantic.getNGAString());
			// System.out.println(semantic.getNPAString());
			// System.out.println(semantic.getInst());
			System.out.println(semantic.getTrackerError());
			System.out.println(semantic.getTokenList());
			System.out.println(semantic.getObject());
			// scanner.close();
		} catch (RegexException e) {
			System.err.println(e.getPosition() + "," + e.getMessage());
			e.printStackTrace();
		} catch (SyntaxException e) {
			System.err.println(e.getPosition() + "," + e.getMessage() + " "
					+ e.getInfo());
			e.printStackTrace();
		}
	}
}
