package com.bajdcc.LALR1.interpret.os.user.routine;

import com.bajdcc.LALR1.interpret.os.IOSCodePage;
import com.bajdcc.util.ResourceLoader;

/**
 * 【用户态】脚本解释器
 *
 * @author bajdcc
 */
public class URShell implements IOSCodePage {
	@Override
	public String getName() {
		return "/usr/p/sh";
	}

	@Override
	public String getCode() {
		return ResourceLoader.INSTANCE.load(getClass());
	}
}
