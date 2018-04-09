package priv.bajdcc.LALR1.syntax.handler;

import priv.bajdcc.util.Position;

/**
 * 文法生成过程中的异常
 *
 * @author bajdcc
 */
@SuppressWarnings("serial")
public class SyntaxException extends Exception {

	/**
	 * 文法推导式解析过程中的错误
	 */
	public enum SyntaxError {
		NULL("推导式为空"), UNDECLARED("无法识别的符号"), SYNTAX("语法错误"), INCOMPLETE(
				"推导式不完整"), EPSILON("可能产生空串"), INDIRECT_RECURSION("存在间接左递归"), FAILED(
				"不能产生字符串"), MISS_NODEPENDENCY_RULE("找不到无最左依赖的规则"), REDECLARATION(
				"重复定义"), COMPILE_ERROR("编译错误");

		private String message;

		SyntaxError(String message) {
			this.message = message;
		}

		public String getMessage() {
			return message;
		}

		public void setMessage(String message) {
			this.message = message;
		}
	}

	public SyntaxException(SyntaxError error, Position pos, Object obj) {
		super(error.getMessage());
		position = pos;
		kError = error;
		if (obj != null) {
			info = obj.toString();
		}
	}

	/**
	 * 位置
	 */
	private Position position = new Position();

	/**
	 * @return 错误位置
	 */
	public Position getPosition() {
		return position;
	}

	/**
	 * 错误类型
	 */
	private SyntaxError kError = SyntaxError.NULL;

	/**
	 * @return 错误类型
	 */
	public SyntaxError getErrorCode() {
		return kError;
	}

	/**
	 * 错误信息
	 */
	private String info = "";

	/**
	 * @return 错误信息
	 */
	public String getInfo() {
		return info;
	}

	/**
	 * 代码页
	 */
	private String pageName = "";
	/**
	 * 文件名
	 */
	private String fileName = "";

	/**
	 * @return 代码页
	 */
	public String getPageName() {
		return pageName;
	}

	/**
	 * 设置代码页
	 *
	 * @param pageName 代码页
	 */
	public void setPageName(String pageName) {
		this.pageName = pageName;
	}

	/**
	 * @return 文件名
	 */
	public String getFileName() {
		return fileName;
	}

	/**
	 * 设置文件名
	 *
	 * @param fileName 文件名
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
}
