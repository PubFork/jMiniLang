package com.bajdcc.LALR1.grammar.codegen;

import com.bajdcc.LALR1.grammar.runtime.RuntimeInst;
import com.bajdcc.LALR1.grammar.runtime.RuntimeInstBinary;
import com.bajdcc.LALR1.grammar.runtime.RuntimeInstNon;
import com.bajdcc.LALR1.grammar.runtime.RuntimeInstUnary;
import com.bajdcc.LALR1.grammar.tree.Function;

/**
 * 【目标代码生成】接口
 *
 * @author bajdcc
 */
public interface ICodegen {

	void genFuncEntry(String funcName);

	RuntimeInstNon genCode(RuntimeInst inst);

	RuntimeInstUnary genCode(RuntimeInst inst, int op1);

	RuntimeInstUnary genCodeWithFuncWriteBack(RuntimeInst inst, int op1);

	RuntimeInstBinary genCode(RuntimeInst inst, int op1, int op2);

	int genDataRef(Object object);

	int getFuncIndex(Function func);

	int getCodeIndex();

	ICodegenBlock getBlockService();

	void genDebugInfo(int start, int end, Object info);
}
