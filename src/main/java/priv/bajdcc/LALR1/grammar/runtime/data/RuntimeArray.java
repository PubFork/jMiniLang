package priv.bajdcc.LALR1.grammar.runtime.data;

import priv.bajdcc.LALR1.grammar.runtime.IRuntimeStatus;
import priv.bajdcc.LALR1.grammar.runtime.RuntimeException;
import priv.bajdcc.LALR1.grammar.runtime.RuntimeObject;

import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 【运行时】运行时数组
 *
 * @author bajdcc
 */
public class RuntimeArray implements Cloneable {

	private List<RuntimeObject> array;

	public RuntimeArray() {
		array = new ArrayList<>();
	}

	public RuntimeArray(RuntimeArray obj) {
		copyFrom(obj);
	}

	public RuntimeArray(List<RuntimeObject> array) {
		this.array = array;
	}

	public void add(RuntimeObject obj) {
		array.add(obj);
	}

	public void add(RuntimeArray arr) {
		array.addAll(arr.array);
	}

	public void insert(int index, RuntimeObject obj) {
		array.add(index, obj);
	}

	public boolean set(int index, RuntimeObject obj) {
		if (array == null) {
			return false;
		}
		if (index >= 0 && index < array.size()) {
			array.set(index, obj);
			return true;
		}
		return false;
	}

	public RuntimeObject pop() {
		if (array == null) {
			return null;
		}
		if (array.isEmpty()) {
			return null;
		}
		return array.remove(array.size() - 1);
	}

	public RuntimeObject get(int index) {
		if (index >= 0 && index < array.size()) {
			return array.get(index);
		}
		return null;
	}

	public RuntimeObject get(int index, IRuntimeStatus status) throws RuntimeException {
		if (index >= 0 && index < array.size()) {
			return array.get(index);
		}
		status.err(RuntimeException.RuntimeError.INVALID_INDEX, "array.get");
		return null;
	}

	public boolean contains(RuntimeObject obj) {
		return array.contains(obj);
	}

	public RuntimeObject size() {
		return new RuntimeObject(BigInteger.valueOf(array.size()));
	}

	public int length() {
		return array.size();
	}

	public RuntimeObject remove(int index) {
		if (index >= 0 && index < array.size()) {
			return array.remove(index);
		}
		return null;
	}

	public RuntimeObject delete(RuntimeObject obj) {
		return new RuntimeObject(array.remove(obj));
	}

	public void reverse() {
		Collections.reverse(array);
	}

	public void clear() {
		array.clear();
	}

	public boolean isEmpty() {
		return array.isEmpty();
	}

	/**
	 * 深拷贝
	 *
	 * @param obj 原对象
	 */
	public void copyFrom(RuntimeArray obj) {
		array = new ArrayList<>();
		for (RuntimeObject o : obj.array) {
			array.add(o.clone());
		}
	}

	public List<Object> toList() {
		return array.stream().map(a -> a.getObj()).collect(Collectors.toList());
	}

	public List<String> toStringList() {
		return array.stream().map(a -> String.valueOf(a.getObj())).collect(Collectors.toList());
	}

	public RuntimeArray clone() {
		try {
			return (RuntimeArray) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return new RuntimeArray();
	}

	@Override
	public String toString() {
		return String.valueOf(array.size());
	}
}
