package com.bajdcc.LALR1.interpret.module;

import com.bajdcc.LALR1.grammar.Grammar;
import com.bajdcc.LALR1.grammar.runtime.*;
import com.bajdcc.LALR1.grammar.runtime.RuntimeException;
import com.bajdcc.LALR1.grammar.runtime.data.RuntimeArray;
import com.bajdcc.LALR1.grammar.runtime.data.RuntimeFuncObject;
import com.bajdcc.util.ResourceLoader;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * 【模块】进程模块
 *
 * @author bajdcc
 */
public class ModuleProc implements IInterpreterModule {

	private static ModuleProc instance = new ModuleProc();
	private RuntimeCodePage runtimeCodePage;

	public static ModuleProc getInstance() {
		return instance;
	}

	private static final int LOCK_TIME = 20;
	private static final int PIPE_READ_TIME = 5;

	@Override
	public String getModuleName() {
		return "sys.proc";
	}

	@Override
	public String getModuleCode() {
		return ResourceLoader.load(getClass());
	}

	@Override
	public RuntimeCodePage getCodePage() throws Exception {
		if (runtimeCodePage != null)
			return runtimeCodePage;

		String base = ResourceLoader.load(getClass());

		Grammar grammar = new Grammar(base);
		RuntimeCodePage page = grammar.getCodePage();
		IRuntimeDebugInfo info = page.getInfo();
		buildMethod(info);
		buildPipeMethod(info);
		buildShareMethod(info);

		return runtimeCodePage = page;
	}

	private void buildMethod(IRuntimeDebugInfo info) {
		info.addExternalFunc("g_create_process", new IRuntimeDebugExec() {
			@Override
			public String getDoc() {
				return "创建进程";
			}

			@Override
			public RuntimeObjectType[] getArgsType() {
				return new RuntimeObjectType[]{RuntimeObjectType.kFunc};
			}

			@Override
			public RuntimeObject ExternalProcCall(List<RuntimeObject> args,
			                                      IRuntimeStatus status) throws Exception {
				RuntimeFuncObject func = (RuntimeFuncObject) args.get(0).getObj();
				return new RuntimeObject(BigInteger.valueOf(status.createProcess(func)));
			}
		});
		info.addExternalFunc("g_create_process_args", new IRuntimeDebugExec() {
			@Override
			public String getDoc() {
				return "创建进程带参数";
			}

			@Override
			public RuntimeObjectType[] getArgsType() {
				return new RuntimeObjectType[]{RuntimeObjectType.kFunc, RuntimeObjectType.kObject};
			}

			@Override
			public RuntimeObject ExternalProcCall(List<RuntimeObject> args,
			                                      IRuntimeStatus status) throws Exception {
				RuntimeFuncObject func = (RuntimeFuncObject) args.get(0).getObj();
				RuntimeObject obj = args.get(1);
				return new RuntimeObject(BigInteger.valueOf(status.createProcess(func, obj)));
			}
		});
		info.addExternalFunc("g_create_user_process", new IRuntimeDebugExec() {
			@Override
			public String getDoc() {
				return "创建用户态进程";
			}

			@Override
			public RuntimeObjectType[] getArgsType() {
				return new RuntimeObjectType[]{RuntimeObjectType.kFunc};
			}

			@Override
			public RuntimeObject ExternalProcCall(List<RuntimeObject> args,
			                                      IRuntimeStatus status) throws Exception {
				RuntimeFuncObject func = (RuntimeFuncObject) args.get(0).getObj();
				return new RuntimeObject(BigInteger.valueOf(status.createUsrProcess(func)));
			}
		});
		info.addExternalFunc("g_create_user_process_args", new IRuntimeDebugExec() {
			@Override
			public String getDoc() {
				return "创建用户态进程带参数";
			}

			@Override
			public RuntimeObjectType[] getArgsType() {
				return new RuntimeObjectType[]{RuntimeObjectType.kFunc, RuntimeObjectType.kObject};
			}

			@Override
			public RuntimeObject ExternalProcCall(List<RuntimeObject> args,
			                                      IRuntimeStatus status) throws Exception {
				RuntimeFuncObject func = (RuntimeFuncObject) args.get(0).getObj();
				RuntimeObject obj = args.get(1);
				return new RuntimeObject(BigInteger.valueOf(status.createUsrProcess(func, obj)));
			}
		});
		info.addExternalFunc("g_get_user_procs", new IRuntimeDebugExec() {
			@Override
			public String getDoc() {
				return "获取用户态进程ID列表";
			}

			@Override
			public RuntimeObjectType[] getArgsType() {
				return null;
			}

			@Override
			public RuntimeObject ExternalProcCall(List<RuntimeObject> args,
			                                      IRuntimeStatus status) {
				RuntimeArray arr = new RuntimeArray();
				List<Integer> list = status.getUsrProcs();
				for (Integer pid : list) {
					arr.add(new RuntimeObject(pid));
				}
				return new RuntimeObject(arr);
			}
		});
		info.addExternalFunc("g_get_pid", new IRuntimeDebugExec() {
			@Override
			public String getDoc() {
				return "获取进程ID";
			}

			@Override
			public RuntimeObjectType[] getArgsType() {
				return null;
			}

			@Override
			public RuntimeObject ExternalProcCall(List<RuntimeObject> args,
			                                      IRuntimeStatus status) {
				return new RuntimeObject(BigInteger.valueOf(status.getPid()));
			}
		});
		info.addExternalFunc("g_get_parent_pid", new IRuntimeDebugExec() {
			@Override
			public String getDoc() {
				return "获取父进程ID";
			}

			@Override
			public RuntimeObjectType[] getArgsType() {
				return null;
			}

			@Override
			public RuntimeObject ExternalProcCall(List<RuntimeObject> args,
			                                      IRuntimeStatus status) {
				return new RuntimeObject(BigInteger.valueOf(status.getParentPid()));
			}
		});
		info.addExternalFunc("g_get_process_priority", new IRuntimeDebugExec() {
			@Override
			public String getDoc() {
				return "获取进程优先级";
			}

			@Override
			public RuntimeObjectType[] getArgsType() {
				return null;
			}

			@Override
			public RuntimeObject ExternalProcCall(List<RuntimeObject> args,
			                                      IRuntimeStatus status) {
				return new RuntimeObject(BigInteger.valueOf(status.getPriority()));
			}
		});
		info.addExternalFunc("g_set_process_priority", new IRuntimeDebugExec() {
			@Override
			public String getDoc() {
				return "设置进程优先级";
			}

			@Override
			public RuntimeObjectType[] getArgsType() {
				return new RuntimeObjectType[]{RuntimeObjectType.kInt};
			}

			@Override
			public RuntimeObject ExternalProcCall(List<RuntimeObject> args,
			                                      IRuntimeStatus status) {
				BigInteger priority = (BigInteger) args.get(0).getObj();
				return new RuntimeObject(status.setPriority(priority.intValue()));
			}
		});
		info.addExternalFunc("g_join_process_once", new IRuntimeDebugExec() {
			@Override
			public String getDoc() {
				return "进程等待";
			}

			@Override
			public RuntimeObjectType[] getArgsType() {
				return new RuntimeObjectType[]{RuntimeObjectType.kInt};
			}

			@Override
			public RuntimeObject ExternalProcCall(List<RuntimeObject> args,
			                                      IRuntimeStatus status) {
				BigInteger pid = (BigInteger) args.get(0).getObj();
				return new RuntimeObject(status.getService().getProcessService().join(pid.intValue(), status.getPid()));
			}
		});
		info.addExternalFunc("g_live_process", new IRuntimeDebugExec() {
			@Override
			public String getDoc() {
				return "进程存活";
			}

			@Override
			public RuntimeObjectType[] getArgsType() {
				return new RuntimeObjectType[]{RuntimeObjectType.kInt};
			}

			@Override
			public RuntimeObject ExternalProcCall(List<RuntimeObject> args,
			                                      IRuntimeStatus status) {
				BigInteger pid = (BigInteger) args.get(0).getObj();
				return new RuntimeObject(status.getService().getProcessService().live(pid.intValue()));
			}
		});
		info.addExternalFunc("g_sleep", new IRuntimeDebugExec() {
			@Override
			public String getDoc() {
				return "进程睡眠";
			}

			@Override
			public RuntimeObjectType[] getArgsType() {
				return new RuntimeObjectType[]{RuntimeObjectType.kInt};
			}

			@Override
			public RuntimeObject ExternalProcCall(List<RuntimeObject> args,
			                                      IRuntimeStatus status) {
				BigInteger turn = (BigInteger) args.get(0).getObj();
				int time = turn.intValue();
				return new RuntimeObject(BigInteger.valueOf(
						status.getService().getProcessService().sleep(status.getPid(), time > 0 ? time : 0)));
			}
		});
		info.addExternalFunc("g_query_usr_proc", new IRuntimeDebugExec() {
			@Override
			public String getDoc() {
				return "枚举用户态进程";
			}

			@Override
			public RuntimeObjectType[] getArgsType() {
				return null;
			}

			@Override
			public RuntimeObject ExternalProcCall(List<RuntimeObject> args,
			                                      IRuntimeStatus status) {
				return new RuntimeObject(ProcInfoHelper.getProcInfo(status, status.getUsrProcs()));
			}
		});
		info.addExternalFunc("g_query_sys_proc", new IRuntimeDebugExec() {
			@Override
			public String getDoc() {
				return "枚举内核态进程";
			}

			@Override
			public RuntimeObjectType[] getArgsType() {
				return null;
			}

			@Override
			public RuntimeObject ExternalProcCall(List<RuntimeObject> args,
			                                      IRuntimeStatus status) {
				return new RuntimeObject(ProcInfoHelper.getProcInfo(status, status.getSysProcs()));
			}
		});
		info.addExternalFunc("g_query_all_proc", new IRuntimeDebugExec() {
			@Override
			public String getDoc() {
				return "枚举进程";
			}

			@Override
			public RuntimeObjectType[] getArgsType() {
				return null;
			}

			@Override
			public RuntimeObject ExternalProcCall(List<RuntimeObject> args,
			                                      IRuntimeStatus status) {
				return new RuntimeObject(ProcInfoHelper.getProcInfoAll(status));
			}
		});
		info.addExternalFunc("g_set_process_desc", new IRuntimeDebugExec() {
			@Override
			public String getDoc() {
				return "设置进程说明";
			}

			@Override
			public RuntimeObjectType[] getArgsType() {
				return new RuntimeObjectType[]{RuntimeObjectType.kString};
			}

			@Override
			public RuntimeObject ExternalProcCall(List<RuntimeObject> args,
			                                      IRuntimeStatus status) {
				status.setProcDesc(String.valueOf(args.get(0).getObj()));
				return null;
			}
		});
	}

	private void buildPipeMethod(IRuntimeDebugInfo info) {
		info.addExternalFunc("g_create_pipe", new IRuntimeDebugExec() {
			@Override
			public String getDoc() {
				return "创建管道";
			}

			@Override
			public RuntimeObjectType[] getArgsType() {
				return new RuntimeObjectType[]{RuntimeObjectType.kString};
			}

			@Override
			public RuntimeObject ExternalProcCall(List<RuntimeObject> args,
			                                      IRuntimeStatus status) throws Exception {
				String name = args.get(0).getObj().toString();
				int handle = status.getService().getPipeService().create(name);
				if (handle == -1)
					status.err(RuntimeException.RuntimeError.MAX_HANDLE);
				return new RuntimeObject(handle);
			}
		});
		info.addExternalFunc("g_query_pipe", new IRuntimeDebugExec() {
			@Override
			public String getDoc() {
				return "查询管道";
			}

			@Override
			public RuntimeObjectType[] getArgsType() {
				return new RuntimeObjectType[]{RuntimeObjectType.kString};
			}

			@Override
			public RuntimeObject ExternalProcCall(List<RuntimeObject> args,
			                                      IRuntimeStatus status) {
				String name = args.get(0).getObj().toString();
				return new RuntimeObject(status.getService().getPipeService().query(name));
			}
		});
		info.addExternalFunc("g_destroy_pipe_once", new IRuntimeDebugExec() {
			@Override
			public String getDoc() {
				return "销毁管道";
			}

			@Override
			public RuntimeObjectType[] getArgsType() {
				return new RuntimeObjectType[]{RuntimeObjectType.kPtr};
			}

			@Override
			public RuntimeObject ExternalProcCall(List<RuntimeObject> args,
			                                      IRuntimeStatus status) {
				int handle = (int) args.get(0).getObj();
				return new RuntimeObject(status.getService().getPipeService().destroy(handle));
			}
		});
		info.addExternalFunc("g_destroy_pipe_by_name_once", new IRuntimeDebugExec() {
			@Override
			public String getDoc() {
				return "销毁管道";
			}

			@Override
			public RuntimeObjectType[] getArgsType() {
				return new RuntimeObjectType[]{RuntimeObjectType.kString};
			}

			@Override
			public RuntimeObject ExternalProcCall(List<RuntimeObject> args,
			                                      IRuntimeStatus status) {
				String name = String.valueOf(args.get(0).getObj());
				return new RuntimeObject(status.getService().getPipeService().destroyByName(status.getPid(), name));
			}
		});
		info.addExternalFunc("g_wait_pipe_empty", new IRuntimeDebugExec() {
			@Override
			public String getDoc() {
				return "等待管道为空";
			}

			@Override
			public RuntimeObjectType[] getArgsType() {
				return new RuntimeObjectType[]{RuntimeObjectType.kPtr};
			}

			@Override
			public RuntimeObject ExternalProcCall(List<RuntimeObject> args,
			                                      IRuntimeStatus status) {
				int handle = (int) args.get(0).getObj();
				status.getService().getProcessService().sleep(status.getPid(), PIPE_READ_TIME);
				return new RuntimeObject(status.getService().getPipeService().hasData(handle));
			}
		});
		info.addExternalFunc("g_read_pipe_char", new IRuntimeDebugExec() {
			@Override
			public String getDoc() {
				return "管道读";
			}

			@Override
			public RuntimeObjectType[] getArgsType() {
				return new RuntimeObjectType[]{RuntimeObjectType.kPtr};
			}

			@Override
			public RuntimeObject ExternalProcCall(List<RuntimeObject> args,
			                                      IRuntimeStatus status) {
				int handle = (int) args.get(0).getObj();
				char ch = status.getService().getPipeService().read(status.getPid(), handle);
				return new RuntimeObject(ch);
			}
		});
		info.addExternalFunc("g_write_pipe_char", new IRuntimeDebugExec() {
			@Override
			public String getDoc() {
				return "管道写";
			}

			@Override
			public RuntimeObjectType[] getArgsType() {
				return new RuntimeObjectType[]{RuntimeObjectType.kPtr, RuntimeObjectType.kChar};
			}

			@Override
			public RuntimeObject ExternalProcCall(List<RuntimeObject> args,
			                                      IRuntimeStatus status) {
				int handle = (int) args.get(0).getObj();
				char ch = (char) args.get(1).getObj();
				return new RuntimeObject(status.getService().getPipeService().write(handle, ch));
			}
		});
	}

	private void buildShareMethod(IRuntimeDebugInfo info) {
		info.addExternalFunc("g_start_share", new IRuntimeDebugExec() {
			@Override
			public String getDoc() {
				return "创建共享";
			}

			@Override
			public RuntimeObjectType[] getArgsType() {
				return new RuntimeObjectType[]{RuntimeObjectType.kString, RuntimeObjectType.kObject};
			}

			@Override
			public RuntimeObject ExternalProcCall(List<RuntimeObject> args,
			                                      IRuntimeStatus status) throws Exception {
				String name = args.get(0).getObj().toString();
				int result = status.getService().getShareService().startSharing(name, args.get(1));
				if (result == -1)
					status.err(RuntimeException.RuntimeError.MAX_HANDLE, name);
				if (result == 0)
					status.err(RuntimeException.RuntimeError.DUP_SHARE_NAME, name);
				return new RuntimeObject(BigInteger.valueOf(result));
			}
		});
		info.addExternalFunc("g_create_share", new IRuntimeDebugExec() {
			@Override
			public String getDoc() {
				return "创建共享";
			}

			@Override
			public RuntimeObjectType[] getArgsType() {
				return new RuntimeObjectType[]{RuntimeObjectType.kString, RuntimeObjectType.kObject};
			}

			@Override
			public RuntimeObject ExternalProcCall(List<RuntimeObject> args,
			                                      IRuntimeStatus status) throws Exception {
				String name = args.get(0).getObj().toString();
				int result = status.getService().getShareService().createSharing(name, args.get(1));
				if (result == -1)
					status.err(RuntimeException.RuntimeError.MAX_HANDLE, name);
				return new RuntimeObject(BigInteger.valueOf(result));
			}
		});
		info.addExternalFunc("g_query_share", new IRuntimeDebugExec() {
			@Override
			public String getDoc() {
				return "查询共享";
			}

			@Override
			public RuntimeObjectType[] getArgsType() {
				return new RuntimeObjectType[]{RuntimeObjectType.kString};
			}

			@Override
			public RuntimeObject ExternalProcCall(List<RuntimeObject> args,
			                                      IRuntimeStatus status) {
				String name = args.get(0).getObj().toString();
				return status.getService().getShareService().getSharing(name, false);
			}
		});
		info.addExternalFunc("g_reference_share", new IRuntimeDebugExec() {
			@Override
			public String getDoc() {
				return "引用共享";
			}

			@Override
			public RuntimeObjectType[] getArgsType() {
				return new RuntimeObjectType[]{RuntimeObjectType.kString};
			}

			@Override
			public RuntimeObject ExternalProcCall(List<RuntimeObject> args,
			                                      IRuntimeStatus status) {
				String name = args.get(0).getObj().toString();
				return status.getService().getShareService().getSharing(name, true);
			}
		});
		info.addExternalFunc("g_stop_share", new IRuntimeDebugExec() {
			@Override
			public String getDoc() {
				return "停止共享";
			}

			@Override
			public RuntimeObjectType[] getArgsType() {
				return new RuntimeObjectType[]{RuntimeObjectType.kString};
			}

			@Override
			public RuntimeObject ExternalProcCall(List<RuntimeObject> args,
			                                      IRuntimeStatus status) throws Exception {
				String name = args.get(0).getObj().toString();
				int result = status.getService().getShareService().stopSharing(name);
				if (result == -1)
					status.err(RuntimeException.RuntimeError.INVALID_SHARE_NAME, name);
				if (result == 2)
					status.err(RuntimeException.RuntimeError.INVALID_REFERENCE, name);
				return new RuntimeObject(result == 1);
			}
		});
		info.addExternalFunc("g_try_lock_share", new IRuntimeDebugExec() {
			@Override
			public String getDoc() {
				return "尝试锁定共享变量";
			}

			@Override
			public RuntimeObjectType[] getArgsType() {
				return new RuntimeObjectType[]{RuntimeObjectType.kString};
			}

			@Override
			public RuntimeObject ExternalProcCall(List<RuntimeObject> args,
			                                      IRuntimeStatus status) {
				String name = args.get(0).getObj().toString();
				if (status.getService().getShareService().isLocked(name)) {
					status.getService().getProcessService().sleep(status.getPid(), LOCK_TIME);
					return new RuntimeObject(true);
				}
				status.getService().getShareService().setLocked(name, true);
				return new RuntimeObject(false);
			}
		});
		info.addExternalFunc("g_unlock_share", new IRuntimeDebugExec() {
			@Override
			public String getDoc() {
				return "解锁共享变量";
			}

			@Override
			public RuntimeObjectType[] getArgsType() {
				return new RuntimeObjectType[]{RuntimeObjectType.kString};
			}

			@Override
			public RuntimeObject ExternalProcCall(List<RuntimeObject> args,
			                                      IRuntimeStatus status) {
				String name = args.get(0).getObj().toString();
				if (status.getService().getShareService().isLocked(name)) {
					status.getService().getShareService().setLocked(name, false);
					return new RuntimeObject(true);
				}
				return new RuntimeObject(false);
			}
		});
		info.addExternalFunc("g_proc_exec", new IRuntimeDebugExec() {
			@Override
			public String getDoc() {
				return "运行用户态代码";
			}

			@Override
			public RuntimeObjectType[] getArgsType() {
				return new RuntimeObjectType[]{RuntimeObjectType.kString};
			}

			@Override
			public RuntimeObject ExternalProcCall(List<RuntimeObject> args,
			                                      IRuntimeStatus status) throws Exception {
				return new RuntimeObject(BigInteger.valueOf(status.ring3Exec(args.get(0).getObj().toString())));
			}
		});
		info.addExternalFunc("g_proc_kill", new IRuntimeDebugExec() {
			@Override
			public String getDoc() {
				return "强制结束用户态进程";
			}

			@Override
			public RuntimeObjectType[] getArgsType() {
				return new RuntimeObjectType[]{RuntimeObjectType.kInt};
			}

			@Override
			public RuntimeObject ExternalProcCall(List<RuntimeObject> args,
			                                      IRuntimeStatus status) {
				BigInteger pid = (BigInteger) args.get(0).getObj();
				status.getService().getFileService().addVfs("/proc/" + pid, "强制退出");
				return new RuntimeObject(BigInteger.valueOf(
						status.getService().getProcessService().ring3Kill(pid.intValue())));
			}
		});
	}

	static class ProcInfoHelper {
		public static RuntimeArray getProcInfo(IRuntimeStatus status, List<Integer> pids) {
			return getProcInfo2(status, getProcInfo3(status, pids));
		}

		public static RuntimeArray getProcInfoAll(IRuntimeStatus status) {
			return getProcInfo2(status, getProcInfo4(status));
		}

		static RuntimeArray getProcInfo2(IRuntimeStatus status, List<Object[]> objs) {
			RuntimeArray array = new RuntimeArray();
			array.add(new RuntimeObject(String.format(" %s  %s %-5s   %-15s   %-25s   %s",
					" ", "环", "标识", "名称", "过程", "描述")));
			for (Object[] obj : objs) {
				array.add(new RuntimeObject(String.format(" %s  %s %5s   %-15s   %-25s   %s",
						obj[0], obj[1], obj[2], obj[3], obj[4], obj[5])));
			}
			return array;
		}

		static List<Object[]> getProcInfo3(IRuntimeStatus status, List<Integer> pids) {
			List<Object[]> objs = new ArrayList<>();
			for (int pid : pids) {
				objs.add(status.getProcInfoById(pid));
			}
			return objs;
		}

		static List<Object[]> getProcInfo4(IRuntimeStatus status) {
			return status.getService().getProcessService().getProcInfoCache();
		}
	}
}
