package priv.bajdcc.LALR1.syntax.automata.npa;

import priv.bajdcc.LALR1.semantic.token.ISemanticAction;
import priv.bajdcc.LALR1.syntax.handler.IErrorHandler;

import java.util.HashSet;

/**
 * 非确定性下推自动机边数据
 *
 * @author bajdcc
 */
public class NPAEdgeData {
	/**
	 * 边类型
	 */
	public NPAEdgeType kAction = NPAEdgeType.MOVE;

	/**
	 * 指令
	 */
	public NPAInstruction inst = NPAInstruction.PASS;

	/**
	 * 指令参数
	 */
	public int iIndex = -1;

	/**
	 * 处理序号
	 */
	public int iHandler = -1;

	/**
	 * 状态参数
	 */
	public NPAStatus status = null;

	/**
	 * 记号参数
	 */
	public int iToken = -1;

	/**
	 * LookAhead表
	 */
	public HashSet<Integer> arrLookAhead = null;

	/**
	 * 错误处理器
	 */
	public IErrorHandler handler = null;

	/**
	 * 语义动作
	 */
	public ISemanticAction action = null;

	/**
	 * 出错后跳转的状态
	 */
	public NPAStatus errorJump = null;
}
