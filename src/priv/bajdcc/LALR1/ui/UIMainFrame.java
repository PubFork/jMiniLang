package priv.bajdcc.LALR1.ui;

import priv.bajdcc.LALR1.grammar.Grammar;
import priv.bajdcc.LALR1.grammar.runtime.RuntimeCodePage;
import priv.bajdcc.LALR1.grammar.runtime.RuntimeException;
import priv.bajdcc.LALR1.interpret.Interpreter;
import priv.bajdcc.LALR1.interpret.module.ModuleRemote;
import priv.bajdcc.LALR1.interpret.os.IOSCodePage;
import priv.bajdcc.LALR1.interpret.os.irq.IRPrint;
import priv.bajdcc.LALR1.interpret.os.irq.IRRemote;
import priv.bajdcc.LALR1.interpret.os.irq.IRSignal;
import priv.bajdcc.LALR1.interpret.os.irq.IRTask;
import priv.bajdcc.LALR1.interpret.os.kern.OSEntry;
import priv.bajdcc.LALR1.interpret.os.kern.OSIrq;
import priv.bajdcc.LALR1.interpret.os.kern.OSTask;
import priv.bajdcc.LALR1.interpret.os.task.TKNet;
import priv.bajdcc.LALR1.interpret.os.task.TKSystem;
import priv.bajdcc.LALR1.interpret.os.task.TKUI;
import priv.bajdcc.LALR1.interpret.os.task.TKUtil;
import priv.bajdcc.LALR1.interpret.os.ui.UIClock;
import priv.bajdcc.LALR1.interpret.os.ui.UIHitokoto;
import priv.bajdcc.LALR1.interpret.os.ui.UIMain;
import priv.bajdcc.LALR1.interpret.os.ui.UIMonitor;
import priv.bajdcc.LALR1.interpret.os.user.UserMain;
import priv.bajdcc.LALR1.interpret.os.user.routine.*;
import priv.bajdcc.LALR1.interpret.os.user.routine.file.URFileAppend;
import priv.bajdcc.LALR1.interpret.os.user.routine.file.URFileLoad;
import priv.bajdcc.LALR1.interpret.os.user.routine.file.URFileSave;
import priv.bajdcc.LALR1.syntax.handler.SyntaxException;
import priv.bajdcc.LALR1.ui.drawing.UIGraphics;
import priv.bajdcc.util.lexer.error.RegexException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * 【界面】窗口
 *
 * @author bajdcc
 */
public class UIMainFrame extends JFrame {
	private UIPanel panel;

	public UIPanel getPanel() {
		return panel;
	}

	public UIMainFrame() {
		panel = new UIPanel();
		this.setTitle("jMiniLang Command Window");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setPreferredSize(new Dimension(800, 610));
		this.setContentPane(panel);
		this.pack();
		this.setLocationRelativeTo(null);
		this.setAlwaysOnTop(true);
		this.setResizable(false);
		this.setVisible(true);
	}

	public static void main(String[] args) {
		UIMainFrame frame = new UIMainFrame();
		ModuleRemote.enabled();
		frame.setTimer();
		startOS(frame.getPanel().getUIGraphics());
		frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
	}

	private void setTimer() {
		new Timer(33, e -> {
			panel.repaint();
		}).start();
	}

	private static void startOS(UIGraphics g) {
		IOSCodePage pages[] = new IOSCodePage[]{
				// OS
				new OSEntry(),
				new OSIrq(),
				new OSTask(),
				// IRQ
				new IRPrint(),
				new IRRemote(),
				new IRTask(),
				new IRSignal(),
				// TASK
				new TKSystem(),
				new TKUtil(),
				new TKUI(),
				new TKNet(),
				// UI
				new UIMain(),
				new UIClock(),
				new UIHitokoto(),
                new UIMonitor(),
                // USER
				new UserMain(),
				// USER ROUTINE
				new URShell(),
				new UREcho(),
				new URPipe(),
				new URDup(),
				new URGrep(),
				new URRange(),
				new URProc(),
				new URTask(),
				new URSleep(),
				new URTime(),
				new URCount(),
				new URTest(),
                new URMsg(),
				// USER FILE
				new URFileLoad(),
				new URFileSave(),
				new URFileAppend(),
		};

		try {
			String code = "import \"sys.base\";\n" +
					"import \"sys.proc\";\n" +
					"call g_load_sync_x(\"/kern/entry\");\n";

			Interpreter interpreter = new Interpreter();

			for (IOSCodePage page : pages) {
				interpreter.load(page.getName(), page.getCode());
			}

			Grammar grammar = new Grammar(code);
			//System.out.println(grammar.toString());
			RuntimeCodePage page = grammar.getCodePage();
			//System.out.println(page.toString());
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			RuntimeCodePage.exportFromStream(page, baos);
			ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
			interpreter.run("@main", bais);

		} catch (RegexException e) {
			System.err.println();
			System.err.println(e.getPosition() + "," + e.getMessage());
			e.printStackTrace();
		} catch (SyntaxException e) {
			System.err.println();
			System.err.println(e.getPosition() + "," + e.getMessage() + " "
					+ e.getInfo());
			e.printStackTrace();
		} catch (RuntimeException e) {
			System.err.println();
			System.err.println(e.getPosition() + ": " + e.getInfo());
			e.printStackTrace();
		} catch (Exception e) {
			System.err.println();
			System.err.println(e.getMessage());
			e.printStackTrace();
		}
	}
}
