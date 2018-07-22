package com.bajdcc.LALR1.interpret.test;

import com.bajdcc.LALR1.grammar.Grammar;
import com.bajdcc.LALR1.grammar.runtime.RuntimeCodePage;
import com.bajdcc.LALR1.grammar.runtime.RuntimeException;
import com.bajdcc.LALR1.interpret.Interpreter;
import com.bajdcc.LALR1.syntax.handler.SyntaxException;
import com.bajdcc.util.lexer.error.RegexException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

@SuppressWarnings("unused")
public class TestInterpret {

	public static void main(String[] args) {
		try {
			String[] codes = new String[]{
					"import \"sys.base\";"
							+ "call g_print(call g_is_null(g_null));",

					"import \"sys.base\";"
							+ "call g_print(\"Hello World!\\\n\");"
							+ "call g_print_err(\"Hello World!\\\n\");",

					"import \"sys.base\";\n"
							+ "call g_print(\"请输入：\");\n"
							+ "call g_print(call g_stdin_read_line() + g_endl);\n"
							+ "call g_print(\"输入两个数字：\");\n"
							+ "call g_print(\n"
							+ "  \"较大数是：\" +"
							+ "  call g_to_string(\n"
							+ "    call g_max(call g_stdin_read_int(), call g_stdin_read_int()))\n"
							+ "    + \"\\n\"\n"
							+ ");\n"
							+ "call g_print(\"输入两个数字：\");\n"
							+ "call g_print(\n"
							+ "  \"较小数是：\" +"
							+ "  call g_to_string(\n"
							+ "    call g_min(call g_stdin_read_int(), call g_stdin_read_int()))\n"
							+ "    + \"\\n\"\n"
							+ ");\n",

					"import \"sys.base\";\n"
							+ "call g_print(call g_doc(\"g_author\") + g_endl);\n"
							+ "call g_print(call g_doc(\"g_print\") + g_endl);\n"
							+ "call g_print(call g_doc(\"g_stdin_read_int\") + g_endl);\n"
							+ "call g_print(call g_doc(\"g_new\") + g_endl);\n"
							+ "call g_print(call g_to_string(call g_new(g_endl)));\n"
							+ "call g_print(call g_to_string(call g_new(5)));\n"
					,

					//"import \"sys.base\";\n"
					//+ "var f = func ~(n) ->\n"
					//+ "    n <= 2 ? 1 : call f(n-1) + call f(n-2);"
					//+ "call g_print(call f(6));\n"
					//,

					//"import \"sys.base\";\n"
					//+ "call g_print(call g_load_func(\"g_max\"));\n"
					//,
			};

			Interpreter interpreter = new Interpreter();
			Grammar grammar = new Grammar(codes[codes.length - 1]);
			System.out.println(grammar.toString());
			RuntimeCodePage page = grammar.getCodePage();
			System.out.println(page.toString());
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			RuntimeCodePage.Companion.exportFromStream(page, baos);
			ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
			interpreter.run("test_1", bais);

		} catch (RegexException e) {
			System.err.println();
			System.err.println(e.getPosition() + "," + e.getMessage());
			e.printStackTrace();
		} catch (SyntaxException e) {
			System.err.println();
			System.err.println(e.getPosition() + "," + e.getMessage() + " "
					+ e.getInfo());
			e.printStackTrace();
		} catch (RuntimeException e) {
			System.err.println();
			System.err.println(e.getPosition() + ": " + e.getInfo());
			e.printStackTrace();
		} catch (Exception e) {
			System.err.println();
			System.err.println(e.getMessage());
			e.printStackTrace();
		}
	}
}
