package com.bajdcc.LALR1.syntax.automata.npa;

import com.bajdcc.util.VisitBag;
import com.bajdcc.util.lexer.automata.BreadthFirstSearch;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * 非确定性下推自动机状态
 *
 * @author bajdcc
 */
public class NPAStatus {
	/**
	 * 出边集合
	 */
	public ArrayList<NPAEdge> outEdges = new ArrayList<>();

	/**
	 * 入边集合
	 */
	public ArrayList<NPAEdge> inEdges = new ArrayList<>();

	/**
	 * 数据
	 */
	public NPAStatusData data = new NPAStatusData();

	/**
	 * 用于遍历包括该状态在内的所有状态（连通），结果存放在PATH中
	 *
	 * @param bfs 遍历算法
	 */
	public void visit(BreadthFirstSearch<NPAEdge, NPAStatus> bfs) {
		ArrayList<NPAStatus> stack = bfs.arrStatus;
		HashSet<NPAStatus> set = new HashSet<>();
		stack.clear();
		set.add(this);
		stack.add(this);
		for (int i = 0; i < stack.size(); i++) {// 遍历每个状态
			NPAStatus status = stack.get(i);
			VisitBag bag = new VisitBag();
			bfs.visitBegin(status, bag);
			if (bag.getVisitChildren()) {
				// 遍历状态的出边
// 边未被访问，且边类型符合要求
				status.outEdges.stream().filter(edge -> !set.contains(edge.end) && bfs.testEdge(edge)).forEach(edge -> {// 边未被访问，且边类型符合要求
					stack.add(edge.end);
					set.add(edge.end);
				});
			}
			if (bag.getVisitEnd()) {
				bfs.visitEnd(status);
			}
		}
	}
}
