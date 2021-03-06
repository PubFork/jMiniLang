package com.bajdcc.LALR1.grammar

import com.bajdcc.LALR1.grammar.codegen.Codegen
import com.bajdcc.LALR1.grammar.error.LostHandler
import com.bajdcc.LALR1.grammar.runtime.RuntimeCodePage
import com.bajdcc.LALR1.grammar.semantic.ISemanticRecorder
import com.bajdcc.LALR1.grammar.semantic.SemanticHandler
import com.bajdcc.LALR1.grammar.semantic.SemanticRecorder
import com.bajdcc.LALR1.grammar.symbol.IManageSymbol
import com.bajdcc.LALR1.grammar.symbol.IQuerySymbol
import com.bajdcc.LALR1.grammar.symbol.SymbolTable
import com.bajdcc.LALR1.semantic.Semantic
import com.bajdcc.LALR1.syntax.Syntax
import com.bajdcc.LALR1.syntax.handler.SyntaxException
import com.bajdcc.LALR1.syntax.handler.SyntaxException.SyntaxError
import com.bajdcc.util.lexer.error.RegexException
import com.bajdcc.util.lexer.token.KeywordType
import com.bajdcc.util.lexer.token.OperatorType
import com.bajdcc.util.lexer.token.TokenType

/**
 * **语法分析器**
 *
 *
 * 基于文法与语义动作
 *
 *
 * @author bajdcc
 */
class Grammar @Throws(RegexException::class, SyntaxException::class)
constructor(context: String) : Semantic(context) {

    /**
     * 语义处理
     */
    private val handler = SemanticHandler()

    /**
     * 符号表
     */
    private val symbol = SymbolTable()

    /**
     * 代码生成
     */
    private var code: Codegen? = null

    /**
     * 语义错误表
     */
    private val recorder = SemanticRecorder()

    override val querySymbolService: IQuerySymbol?
        get() = symbol

    override val manageSymbolService: IManageSymbol?
        get() = symbol

    override val semanticRecorderService: ISemanticRecorder?
        get() = recorder

    /**
     * 获得语义错误描述
     *
     * @return 语义错误描述
     */
    private val semanticError: String
        get() = recorder.toString(tokenFactory)

    /**
     * 获得中间代码描述
     *
     * @return 中间代码描述
     */
    override val inst: String
        get() = code!!.toString()

    /**
     * 生成目标代码
     *
     * @return 目标代码页
     * @throws SyntaxException 词法错误
     */
    val codePage: RuntimeCodePage
        @Throws(SyntaxException::class)
        get() {
            if (!recorder.correct) {
                System.err.println(semanticError)
                throw SyntaxException(SyntaxError.COMPILE_ERROR,
                        recorder.error[0].position.position, semanticError)
            }
            return code!!.genCodePage()
        }

    init {
        initialize()
    }

    /**
     * 初始化
     *
     * @throws SyntaxException 词法错误
     */
    @Throws(SyntaxException::class)
    private fun initialize() {
        if (Syntax.npa == null) {
            // 为避免多次构造NPA，这里采用单例模式
            declareTerminal()
            declareNonTerminal()
            declareErrorHandler()
            declareActionHandler()
            infer()
            npaDesc = npaMarkdownString
        }
        parse()
        check()
        gencode()
    }

    /**
     * 声明终结符
     *
     * @throws SyntaxException 词法错误
     */
    @Throws(SyntaxException::class)
    private fun declareTerminal() {
        addTerminal("ID", TokenType.ID, null)
        addTerminal("BOOLEAN", TokenType.BOOL, null)
        addTerminal("LITERAL", TokenType.STRING, null)
        addTerminal("CHARACTER", TokenType.CHARACTER, null)
        addTerminal("INTEGER", TokenType.INTEGER, null)
        addTerminal("DECIMAL", TokenType.DECIMAL, null)
        for (keywordType in KeywordType.values()) {
            addTerminal(keywordType.name, TokenType.KEYWORD, keywordType)
        }
        addTerminal("ELLIPSIS", TokenType.OPERATOR, OperatorType.ELLIPSIS)
        addTerminal("PTR_OP", TokenType.OPERATOR, OperatorType.POINTER)
        addTerminal("INC_OP", TokenType.OPERATOR, OperatorType.PLUS_PLUS)
        addTerminal("DEC_OP", TokenType.OPERATOR, OperatorType.MINUS_MINUS)
        addTerminal("LEFT_OP", TokenType.OPERATOR, OperatorType.LEFT_SHIFT)
        addTerminal("RIGHT_OP", TokenType.OPERATOR, OperatorType.RIGHT_SHIFT)
        addTerminal("LE_OP", TokenType.OPERATOR,
                OperatorType.LESS_THAN_OR_EQUAL)
        addTerminal("GE_OP", TokenType.OPERATOR,
                OperatorType.GREATER_THAN_OR_EQUAL)
        addTerminal("EQ_OP", TokenType.OPERATOR, OperatorType.EQUAL)
        addTerminal("NE_OP", TokenType.OPERATOR, OperatorType.NOT_EQUAL)
        addTerminal("AND_OP", TokenType.OPERATOR, OperatorType.LOGICAL_AND)
        addTerminal("OR_OP", TokenType.OPERATOR, OperatorType.LOGICAL_OR)
        addTerminal("MUL_ASSIGN", TokenType.OPERATOR, OperatorType.TIMES_ASSIGN)
        addTerminal("DIV_ASSIGN", TokenType.OPERATOR, OperatorType.DIV_ASSIGN)
        addTerminal("MOD_ASSIGN", TokenType.OPERATOR, OperatorType.MOD_ASSIGN)
        addTerminal("ADD_ASSIGN", TokenType.OPERATOR, OperatorType.PLUS_ASSIGN)
        addTerminal("SUB_ASSIGN", TokenType.OPERATOR, OperatorType.MINUS_ASSIGN)
        addTerminal("LEFT_ASSIGN", TokenType.OPERATOR,
                OperatorType.LEFT_SHIFT_ASSIGN)
        addTerminal("RIGHT_ASSIGN", TokenType.OPERATOR,
                OperatorType.RIGHT_SHIFT_ASSIGN)
        addTerminal("AND_ASSIGN", TokenType.OPERATOR, OperatorType.AND_ASSIGN)
        addTerminal("XOR_ASSIGN", TokenType.OPERATOR, OperatorType.XOR_ASSIGN)
        addTerminal("OR_ASSIGN", TokenType.OPERATOR, OperatorType.OR_ASSIGN)
        addTerminal("EQ_ASSIGN", TokenType.OPERATOR, OperatorType.EQ_ASSIGN)
        addTerminal("ADD", TokenType.OPERATOR, OperatorType.PLUS)
        addTerminal("SUB", TokenType.OPERATOR, OperatorType.MINUS)
        addTerminal("MUL", TokenType.OPERATOR, OperatorType.TIMES)
        addTerminal("DIV", TokenType.OPERATOR, OperatorType.DIVIDE)
        addTerminal("MOD", TokenType.OPERATOR, OperatorType.MOD)
        addTerminal("AND", TokenType.OPERATOR, OperatorType.BIT_AND)
        addTerminal("OR", TokenType.OPERATOR, OperatorType.BIT_OR)
        addTerminal("XOR", TokenType.OPERATOR, OperatorType.BIT_XOR)
        addTerminal("NOT", TokenType.OPERATOR, OperatorType.BIT_NOT)
        addTerminal("NOT_OP", TokenType.OPERATOR, OperatorType.LOGICAL_NOT)
        addTerminal("LT", TokenType.OPERATOR, OperatorType.LESS_THAN)
        addTerminal("GT", TokenType.OPERATOR, OperatorType.GREATER_THAN)
        addTerminal("QUERY", TokenType.OPERATOR, OperatorType.QUERY)
        addTerminal("COMMA", TokenType.OPERATOR, OperatorType.COMMA)
        addTerminal("SEMI", TokenType.OPERATOR, OperatorType.SEMI)
        addTerminal("DOT", TokenType.OPERATOR, OperatorType.DOT)
        addTerminal("ASSIGN", TokenType.OPERATOR, OperatorType.ASSIGN)
        addTerminal("COLON", TokenType.OPERATOR, OperatorType.COLON)
        addTerminal("LPA", TokenType.OPERATOR, OperatorType.LPARAN)
        addTerminal("RPA", TokenType.OPERATOR, OperatorType.RPARAN)
        addTerminal("LSQ", TokenType.OPERATOR, OperatorType.LSQUARE)
        addTerminal("RSQ", TokenType.OPERATOR, OperatorType.RSQUARE)
        addTerminal("LBR", TokenType.OPERATOR, OperatorType.LBRACE)
        addTerminal("RBR", TokenType.OPERATOR, OperatorType.RBRACE)
        addTerminal("PROPERTY", TokenType.OPERATOR, OperatorType.PROPERTY)
    }

    /**
     * 声明非终结符
     *
     * @throws SyntaxException 词法错误
     */
    @Throws(SyntaxException::class)
    private fun declareNonTerminal() {
        val nonTerminals = arrayOf("program", "stmt_list", "stmt", "stmt_stmt", "stmt_ctrl", "stmt_exp", "func", "lambda", "var", "var_list", "exp_list", "exp", "exp0", "exp1", "exp2", "exp3", "exp4", "exp5", "exp6", "exp7", "exp8", "exp9", "exp10", "type", "block", "call_exp", "call", "ret", "doc_list", "port", "if", "for", "while", "foreach", "cycle_ctrl", "block_stmt", "array", "map", "set", "invoke", "exp01", "try", "throw", "map_list", "scope")
        for (string in nonTerminals) {
            addNonTerminal(string)
        }
    }

    /**
     * 进行推导
     *
     * @throws SyntaxException 词法错误
     */
    @Throws(SyntaxException::class)
    private fun infer() {
        /* 起始符号就是main函数 */
        infer(handler.getSemanticHandler("main"),
                "program -> stmt_list[0]{lost_stmt}")
        /* Block语句 */
        infer(handler.getSemanticHandler("block"),
                "block -> @LBR#do_enter_scope# [stmt_list[0]] @RBR#do_leave_scope#{lost_rbr}")
        infer(handler.getSemanticHandler("block_stmt"),
                "block_stmt -> block[0]")
        /* 当前块（Block）全部由语句组成 */
        infer(handler.getSemanticHandler("stmt_list"),
                "stmt_list -> stmt[0]{lost_stmt} [stmt_list[1]]")
        /* 语句分为变量定义（赋值）、调用语句 */
        infer(handler.getSemanticHandler("copy"),
                "stmt_exp -> var[0] | call[0] | cycle_ctrl[0] | exp[0]")
        infer(handler.getSemanticHandler("copy"),
                "stmt -> stmt_stmt[0] | stmt_ctrl[0] | block_stmt[0]")
        infer(handler.getSemanticHandler("stmt_exp"),
                "stmt_stmt -> [stmt_exp[0]] @SEMI#clear_catch#{lost_semi}")
        infer(handler.getSemanticHandler("copy"),
                "stmt_ctrl -> ret[0] | port[0] | if[0] | for[0] | foreach[0] | while[0] | try[0] | throw[0]")
        /* 返回语句 */
        infer(handler.getSemanticHandler("return"),
                "ret -> (@YIELD[1] | @RETURN) [exp[0]] @SEMI{lost_semi}")
        /* 变量定义（赋值）语句（由于支持Lambda，函数定义皆为Lambda形式） */
        infer(handler.getSemanticHandler("var"),
                "var -> (@VARIABLE[11] | @LET[12]) @ID[0]#declear_variable#{lost_token} [@ASSIGN{lost_assign} (func[1]{lost_func} | exp[2]{lost_exp})]")
        /* 类属性赋值语句 */
        infer(handler.getSemanticHandler("set"),
                "set -> @SET exp[3]{lost_exp} @PROPERTY{lost_property} exp[4]{lost_exp} @ASSIGN{lost_assign} exp[2]{lost_exp}")
        /* 类方法调用语句 */
        infer(handler.getSemanticHandler("invoke"),
                "invoke -> @INVOKE[0] exp[1]{lost_exp} @PROPERTY{lost_property} exp[2]{lost_exp} @PROPERTY{lost_property} @LPA{lost_lpa} [exp_list[3]] @RPA{lost_rpa}")
        /* 导入与导出语句 */
        infer(handler.getSemanticHandler("port"),
                "port -> (@IMPORT[1] | @EXPORT[2]) @LITERAL[0]{lost_string} @SEMI{lost_semi}")
        /* 表达式（算符文法） */
        val exp_handler = handler.getSemanticHandler("exp")
        infer(handler.getSemanticHandler("scope"), "scope -> exp[0]")
        infer(exp_handler, "exp -> exp01[0] [@INTEGER[10] | @DECIMAL[10]]")
        infer(exp_handler,
                "exp01 -> [exp01[1] (@EQ_ASSIGN[2] | @ADD_ASSIGN[2] | @SUB_ASSIGN[2] | @MUL_ASSIGN[2] | @DIV_ASSIGN[2] | @AND_ASSIGN[2] | @OR_ASSIGN[2] | @XOR_ASSIGN[2] | @MOD_ASSIGN[2])] exp0[0]")
        infer(exp_handler,
                "exp0 -> exp1[0] [@QUERY[4] exp0[6] @COLON[5]{lost_colon} exp0[7]]")
        infer(exp_handler, "exp1 -> [exp1[1] (@AND_OP[2] | @OR_OP[2])] exp2[0]")
        infer(exp_handler,
                "exp2 -> [exp2[1] (@OR[2] | @XOR[2] | @AND[2])] exp3[0]")
        infer(exp_handler, "exp3 -> [exp3[1] (@EQ_OP[2] | @NE_OP[2])] exp4[0]")
        infer(exp_handler,
                "exp4 -> [exp4[1] (@LT[2] | @GT[2] | @LE_OP[2] | @GE_OP[2])] exp5[0]")
        infer(exp_handler,
                "exp5 -> [exp5[1] (@LEFT_OP[2] | @RIGHT_OP[2])] exp6[0]")
        infer(exp_handler, "exp6 -> [exp6[1] (@ADD[2] | @SUB[2])] exp7[0]")
        infer(exp_handler,
                "exp7 -> [exp7[1] (@MUL[2] | @DIV[2] | @MOD[2])] exp8[0]")
        infer(exp_handler, "exp8 -> (@NOT_OP[3] | @NOT[3]) exp8[1] | exp9[0]")
        infer(exp_handler,
                "exp9 -> (@INC_OP[3] | @DEC_OP[3]) exp9[1] | exp9[1] (@INC_OP[3] | @DEC_OP[3] | @LSQ exp[5]{lost_exp} @RSQ{lost_rpa}) | exp10[0]")
        infer(exp_handler, "exp10 -> [exp10[1] @DOT[2]] type[0]")
        /* 调用语句 */
        infer(handler.getSemanticHandler("call_exp"),
                "call -> @CALL[4] (@LPA{lost_lpa} (func[0]{lost_call} | exp[3]{lost_exp}) @RPA{lost_rpa} | @ID[1]{lost_call}) @LPA{lost_lpa} [exp_list[2]] @RPA{lost_rpa}")
        /* 函数定义 */
        infer(handler.getSemanticHandler("token_list"),
                "var_list -> @ID[0]#declear_param#{lost_token} [@COMMA var_list[1]{lost_token}]")
        infer(handler.getSemanticHandler("exp_list"),
                "exp_list -> exp[0] [@COMMA exp_list[1]{lost_exp}]")
        infer(handler.getSemanticHandler("token_list"),
                "doc_list -> @LITERAL[0] [@COMMA doc_list[1]]")
        /* 函数主体 */
        infer(handler.getSemanticHandler("func"),
                "func -> (@FUNCTION[10]#func_clearargs# | @YIELD#func_clearargs#) [@LSQ doc_list[0]{lost_doc} @RSQ] (@ID[1]#predeclear_funcname#{lost_func_name} | @NOT[1]#predeclear_funcname#{lost_func_name}) @LPA{lost_lpa} [var_list[2]] @RPA{lost_rpa} (@PTR_OP#do_enter_scope#{lost_func_body} scope[3]#do_leave_scope#{lost_exp} | block[4]{lost_func_body})")
        /* 匿名主体 */
        infer(handler.getSemanticHandler("lambda"),
                "lambda -> @LAMBDA[1]#lambda# @LPA{lost_lpa} [var_list[2]] @RPA{lost_rpa} (@PTR_OP#do_enter_scope#{lost_func_body} scope[3]#do_leave_scope#{lost_exp} | block[4]{lost_func_body})")
        /* 基本数据类型 */
        infer(handler.getSemanticHandler("type"),
                "type -> @ID[0] [@LPA[3] [exp_list[4]] @RPA{lost_rpa}] | @INTEGER[0] | @DECIMAL[0] | @LITERAL[0] [@LPA[3] [exp_list[4]] @RPA{lost_rpa}] | @CHARACTER[0] | @BOOLEAN[0] | @LPA exp[1]{lost_exp} @RPA{lost_rpa} | call[1] | lambda[2] | set[1] | invoke[1] | array[1] | map[1]")
        /* 条件语句 */
        infer(handler.getSemanticHandler("if"),
                "if -> @IF @LPA{lost_lpa} exp[0]{lost_exp} @RPA{lost_rpa} block[1]{lost_block} [@ELSE (block[2]{lost_block} | if[3]{lost_block})]")
        /* 循环语句 */
        infer(handler.getSemanticHandler("for"),
                "for -> @FOR#do_enter_cycle# @LPA{lost_lpa} [var[0]] @SEMI{lost_semi} [exp[1]] @SEMI{lost_semi} [exp[2] | var[2]] @RPA{lost_rpa} block[3]{lost_block}")
        infer(handler.getSemanticHandler("while"),
                "while -> @WHILE#do_enter_cycle# @LPA{lost_lpa} exp[0] @RPA{lost_rpa} block[1]{lost_block}")
        /* 循环语句 */
        infer(handler.getSemanticHandler("foreach"),
                "foreach -> @FOREACH#do_enter_cycle# @LPA{lost_lpa} @VARIABLE{lost_var} @ID[0]#declear_variable#{lost_token} @COLON{lost_colon} exp[1]{lost_exp} @RPA{lost_rpa} block[2]{lost_block}")
        /* 循环控制语句 */
        infer(handler.getSemanticHandler("cycle"),
                "cycle_ctrl -> @BREAK[0] | @CONTINUE[0]")
        /* 数组初始化 */
        infer(handler.getSemanticHandler("array"),
                "array -> @LSQ [exp_list[0]] @RSQ{lost_rsq}")
        /* 字典初始化 */
        infer(handler.getSemanticHandler("map_list"),
                "map_list -> @LITERAL[1] @COLON[3]{lost_colon} exp[2]{lost_exp} [@COMMA#clear_catch# map_list[0]]")
        infer(handler.getSemanticHandler("map"),
                "map -> @LBR [map_list[0]] @RBR{lost_rbr}")
        /* 异常处理 */
        infer(handler.getSemanticHandler("try"),
                "try -> @TRY block[1]{lost_block} @CATCH{lost_catch} [@LPA{lost_lpa} @ID[0]#declear_param#{lost_token} @RPA{lost_rpa}] block[2]{lost_block}")
        infer(handler.getSemanticHandler("throw"),
                "throw -> @THROW exp[0]{lost_exp} @SEMI{lost_semi}")
        initialize("program")
    }

    /**
     * 声明错误处理器
     *
     * @throws SyntaxException 词法错误
     */
    @Throws(SyntaxException::class)
    private fun declareErrorHandler() {
        addErrorHandler("lost_exp", LostHandler("表达式"))
        addErrorHandler("lost_func", LostHandler("函数"))
        addErrorHandler("lost_token", LostHandler("标识符"))
        addErrorHandler("lost_func_name", LostHandler("函数名"))
        addErrorHandler("lost_func_body", LostHandler("函数体"))
        addErrorHandler("lost_block", LostHandler("块"))
        addErrorHandler("lost_stmt", LostHandler("语句"))
        addErrorHandler("lost_string", LostHandler("字符串"))
        addErrorHandler("lost_assign", LostHandler("等号'='"))
        addErrorHandler("lost_call", LostHandler("调用主体"))
        addErrorHandler("lost_lpa", LostHandler("左圆括号'('"))
        addErrorHandler("lost_rpa", LostHandler("右圆括号')'"))
        addErrorHandler("lost_lsq", LostHandler("左方括号'['"))
        addErrorHandler("lost_rsq", LostHandler("右方括号']'"))
        addErrorHandler("lost_lbr", LostHandler("左花括号'{'"))
        addErrorHandler("lost_rbr", LostHandler("右花括号'}'"))
        addErrorHandler("lost_colon", LostHandler("冒号':'"))
        addErrorHandler("lost_semi", LostHandler("分号';'"))
        addErrorHandler("lost_doc", LostHandler("文档"))
        addErrorHandler("lost_var", LostHandler("赋值"))
        addErrorHandler("lost_array", LostHandler("数组'[]'"))
        addErrorHandler("lost_map", LostHandler("字典'{}'"))
        addErrorHandler("lost_dot", LostHandler("点号'.'"))
        addErrorHandler("lost_property", LostHandler("属性连接符'::'"))
        addErrorHandler("lost_try", LostHandler("属性连接符'::'"))
        addErrorHandler("lost_catch", LostHandler("'catch'"))
    }

    /**
     * 声明动作处理器
     *
     * @throws SyntaxException 词法错误
     */
    @Throws(SyntaxException::class)
    private fun declareActionHandler() {
        val actionNames = arrayOf("do_enter_scope", "do_leave_scope", "predeclear_funcname", "declear_variable", "declear_param", "func_clearargs", "do_enter_cycle", "lambda", "clear_catch")
        for (string in actionNames) {
            addActionHandler(string, handler.getActionHandler(string))
        }
    }

    /**
     * 语法树建成之后的详细语义检查
     *
     * @throws SyntaxException 词法错误
     */
    @Throws(SyntaxException::class)
    private fun check() {
        if (recorder.correct) {
            symbol.check(recorder)
        } else {
            val error = semanticError
            System.err.println(error)
            throw SyntaxException(SyntaxError.COMPILE_ERROR,
                    recorder.error[0].position.position, error)
        }
    }

    /**
     * 产生中间代码
     */
    private fun gencode() {
        if (recorder.correct) {
            code = Codegen(symbol)
            code!!.gencode()
        }
    }

    override fun toString(): String {
        val sb = StringBuilder()
        // sb.append(getNGAString());
        // sb.append(getNPAString());
        sb.append(trackerError)
        sb.append(semanticError)
        sb.append(tokenList)
        if (recorder.correct) {
            sb.append(symbol.toString())
            sb.append(inst)
        }
        return sb.toString()
    }

    companion object {

        var npaDesc: String? = null
    }
}
