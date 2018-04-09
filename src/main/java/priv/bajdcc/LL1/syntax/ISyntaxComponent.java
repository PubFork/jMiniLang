package priv.bajdcc.LL1.syntax;

/**
 * 文法部件接口（规则表达式基类）
 *
 * @author bajdcc
 */
public interface ISyntaxComponent {
	/**
	 * 设定遍历方式
	 *
	 * @param visitor 递归遍历算法
	 */
	void visit(ISyntaxComponentVisitor visitor);
}
