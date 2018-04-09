package priv.bajdcc.LALR1.interpret.os.kern;

import priv.bajdcc.LALR1.interpret.os.IOSCodePage;
import priv.bajdcc.util.ResourceLoader;

/**
 * 【内核】服务
 *
 * @author bajdcc
 */
public class OSTask implements IOSCodePage {
	@Override
	public String getName() {
		return "/kern/task";
	}

	@Override
	public String getCode() {
		return ResourceLoader.load(getClass());
	}
}
