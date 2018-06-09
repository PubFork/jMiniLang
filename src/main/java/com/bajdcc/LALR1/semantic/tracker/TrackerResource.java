package com.bajdcc.LALR1.semantic.tracker;

/**
 * 跟踪器资源（链表）
 *
 * @author bajdcc
 */
public class TrackerResource {
	/**
	 * 跟踪器链表头
	 */
	public Tracker head = null;

	/**
	 * 跟踪器链表尾
	 */
	public Tracker tail = null;

	/**
	 * 添加指令记录
	 *
	 * @param prev 前驱记录
	 * @return 新的指令记录
	 */
	public InstructionRecord addInstRecord(InstructionRecord prev) {
		return new InstructionRecord(prev);
	}

	/**
	 * 添加错误记录
	 *
	 * @param prev 前驱记录
	 * @return 新的错误记录
	 */
	public ErrorRecord addErrorRecord(ErrorRecord prev) {
		return new ErrorRecord(prev);
	}

	/**
	 * 添加跟踪器
	 *
	 * @return 新的跟踪器
	 */
	public Tracker addTracker() {
		Tracker tracker = new Tracker();
		if (head != null) {
			/* 将新的跟踪器插入表首 */
			tracker.next = head;
			head.prev = tracker;
			head = tracker;
		} else {
			head = tail = tracker;
		}
		return tracker;
	}

	/**
	 * 释放跟踪器
	 *
	 * @param tracker 不需要的跟踪器
	 */
	public void freeTracker(Tracker tracker) {
		if (tracker == head) {
			if (tracker == tail) {
				head = tail = null;// 删除链表中的唯一项
			} else {
				head = head.next;
				head.prev = null;
			}
		} else if (tracker == tail) {
			tail = tail.prev;
			tail.next = null;
		} else {
			tracker.next.prev = tracker.prev;
			tracker.prev.next = tracker.next;
		}
	}
}
