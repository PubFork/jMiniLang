package priv.bajdcc.LALR1.syntax.automata.npa;

/**
 * <p>
 * 非确定性下推自动机边类型
 * </p>
 * Move ------------ (Start,Epsilon,[Token]) ----&gt; (End,Epsilon)<br>
 * Shift ----------- (Start,Epsilon,Epsilon) ----&gt; (End,Start)<br>
 * Reduce ---------- (Start,[Status],Epsilon) ---&gt; (End,Epsilon)<br>
 * Left Recursion -- (Start,Epsilon,Epsilon) ----&gt; (End,Epsilon)<br>
 * Finish ---------- (Start,Epsilon,Epsilon) ----&gt; (Epsilon,Epsilon)<br>
 *
 * @author bajdcc
 */
public enum NPAEdgeType {
	MOVE("匹配"), SHIFT("转移"), REDUCE("归约"), LEFT_RECURSION("左递归"), FINISH("结束");

	private String name;

	NPAEdgeType(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
