package priv.bajdcc.LALR1.interpret.os.irq;

import priv.bajdcc.LALR1.interpret.os.IOSCodePage;
import priv.bajdcc.util.ResourceLoader;

/**
 * 【中断】文字输出
 *
 * @author bajdcc
 */
public class IRPrint implements IOSCodePage {
	@Override
	public String getName() {
		return "/irq/print";
	}

	@Override
	public String getCode() {
		return ResourceLoader.load(getClass());
	}
}
