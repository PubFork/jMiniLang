package priv.bajdcc.LALR1.interpret.os.irq;

import priv.bajdcc.LALR1.interpret.os.IOSCodePage;
import priv.bajdcc.util.ResourceLoader;

/**
 * 【中断】远程用户界面
 *
 * @author bajdcc
 */
public class IRRemote implements IOSCodePage {
	@Override
	public String getName() {
		return "/irq/remote";
	}

	@Override
	public String getCode() {
		return ResourceLoader.load(getClass());
	}
}
