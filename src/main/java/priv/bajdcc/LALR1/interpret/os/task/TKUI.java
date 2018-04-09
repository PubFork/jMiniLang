package priv.bajdcc.LALR1.interpret.os.task;

import priv.bajdcc.LALR1.interpret.os.IOSCodePage;
import priv.bajdcc.util.ResourceLoader;

/**
 * 【服务】用户界面
 *
 * @author bajdcc
 */
public class TKUI implements IOSCodePage {
	@Override
	public String getName() {
		return "/task/ui";
	}

	@Override
	public String getCode() {
		return ResourceLoader.load(getClass());
	}
}
