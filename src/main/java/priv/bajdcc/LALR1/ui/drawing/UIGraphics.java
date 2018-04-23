package priv.bajdcc.LALR1.ui.drawing;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 【界面】显示屏
 *
 * @author bajdcc
 */
public class UIGraphics {

	private static final int CARET_TIME = 20;
	private static final int MAX_QUEUE_SIZE = 2000;
	private static final char FILL_FONT_SPAN = '\uffea';
	private int w;
	private int h;
	private int cols;
	private int rows;
	private int width;
	private int height;
	private int zoom;
	private int size;
	private char[] data;
	private int ptr_x, ptr_y;
	private int ptr_mx, ptr_my;
	private Queue<Character> queue;
	private UIFontImage fontImage;
	private Image image;
	private boolean caret;
	private boolean caretPrev;
	private boolean caretState;
	private int caretTime;
	private int stateColor;
	private int countColor;
	private int[] colors;
	private boolean autoFresh;

	public UIGraphics(int w, int h, int cols, int rows, int width, int height, int zoom) {
		this.w = w;
		this.h = h;
		this.cols = cols;
		this.rows = rows;
		this.size = cols * rows;
		this.width = width * zoom;
		this.height = height * zoom;
		this.zoom = zoom;
		this.data = new char[cols * rows];
		this.ptr_x = 0;
		this.ptr_y = 0;
		this.ptr_mx = 0;
		this.ptr_my = 0;
		this.queue = new LinkedBlockingQueue<>(MAX_QUEUE_SIZE);
		this.fontImage = new UIFontImage(this.width, this.height);
		this.image = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
		this.image.getGraphics().setColor(Color.white);
		this.image.getGraphics().fillRect(0, 0, w, h);
		this.stateColor = 0;
		this.countColor = 0;
		this.colors = new int[3];
		this.autoFresh = true;
	}

	public void paint(Graphics2D g) {
		int len = 0;
		for (; ; ) {
			Character c = this.queue.poll();
			if (c == null)
				break;
			if (stateColor != 0 && countColor < 3) {
				markColor(c);
				continue;
			}
			if (c == '\uffef') {
				markInput();
			} else if (c == '\f') {
				clear(g);
			} else if (c == '\uffe1' || c == '\uffe2') {
				if (c == '\uffe1')
					autoFresh = !autoFresh;
				else
					refresh(g);
			} else if (c == '\uffd2' || c == '\uffd3') {
				stateColor = c == '\uffd2' ? 1 : 2;
				countColor = 0;
			} else {
				if (c == '\t')
					c = ' ';
				draw(g, c);
				len++;
			}
		}
		if (len > 0 && caretTime > 0) {
			caretTime = 0;
		}
		if (caret != caretPrev) {
			if (caretPrev && caretState) {
				hideCaret();
				caretTime = 0;
				caretState = false;
			}
			caretPrev = caret;
		} else if (caret) {
			if (caretState) {
				showCaret(g);
			} else {
				hideCaret();
			}
			if (caretTime++ >= CARET_TIME) {
				caretState = !caretState;
				caretTime = 0;
			}
		}
		g.drawImage(image, 0, 0, null);
	}

	private void drawText(int row, int col, String text) {
		setFGColor(230, 230, 230);
		setBGColor(25, 25, 25);
		for (int i = 0; i < text.length(); i++) {
			image.getGraphics().drawImage(fontImage.getImage(text.charAt(i)),
					(col + i) * width, row * height, null);
		}
		setFGColor(0, 0, 0);
		setBGColor(255, 255, 255);
	}

	private void refresh(Graphics2D g) {
		image.getGraphics().setColor(Color.white);
		image.getGraphics().fillRect(0, 0, w, h);
		g.drawImage(image, 0, 0, null);
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				char c = this.data[i * cols + j];
				if (c != FILL_FONT_SPAN) {
					image.getGraphics().drawImage(fontImage.getImage(c),
							j * width, i * height, null);
				}
			}
		}
		final String logo = "--== jMiniOS made by bajdcc ==--";
		drawText(0, (cols - logo.length()) / 2, logo);
		this.ptr_x = 0;
		this.ptr_y = 0;
		for (int i = 0; i < size; i++) {
			this.data[i] = '\0';
		}
	}

	private void markColor(Character c) {
		colors[countColor++] = c & 255;
		if (countColor >= 3) {
			if (stateColor == 1)
				setFGColor(colors[0], colors[1], colors[2]);
			else
				setBGColor(colors[0], colors[1], colors[2]);
			stateColor = 0;
		}
	}

	private void showCaret(Graphics2D g) {
		if (ptr_x == cols) {
			ptr_x = 0;
			if (ptr_y == rows) {
				clear(g);
			} else {
				ptr_y++;
			}
		}
		drawChar('_');
	}

	private void hideCaret() {
		drawChar('\0');
	}

	private void draw(Graphics2D g, char c) {
		drawIntern(g, c);
		if (ptr_x > 0 && UIFontImage.isWideChar(c)) {
			drawIntern(g, FILL_FONT_SPAN);
		}
	}

	private void drawIntern(Graphics2D g, char c) {
		if (c == '\n') {
			if (ptr_y == rows - 1) {
				newline(g);
			} else {
				ptr_x = 0;
				ptr_y++;
			}
		} else if (c == '\b') {
			if (ptr_mx + ptr_my * cols < ptr_x + ptr_y * cols) {
				if (ptr_y == 0) {
					if (ptr_x != 0) {
						drawChar('\0');
						ptr_x--;
					}
				} else {
					if (ptr_x != 0) {
						drawChar('\0');
						ptr_x--;
					} else {
						drawChar('\0');
						ptr_x = cols - 1;
						ptr_y--;
					}
				}
			}
		} else if (c == '\2') {
			for (ptr_x--; ptr_x >= 0; ptr_x--) {
				drawChar('\0');
			}
			ptr_x = 0;
		} else if (c == '\r') {
			ptr_x = 0;
		} else if (ptr_x == cols - 1) {
			if (ptr_y == rows - 1) {
				drawChar(c);
				if (autoFresh)
					newline(g);
			} else {
				drawChar(c);
				ptr_x = 0;
				ptr_y++;
			}
		} else {
			drawChar(c);
			ptr_x++;
		}
	}

	private void drawChar(char c) {
		this.data[ptr_y * cols + ptr_x] = c;
		if (autoFresh && c != FILL_FONT_SPAN) {
			image.getGraphics().drawImage(fontImage.getImage(c),
					ptr_x * width, ptr_y * height, null);
		}
	}

	public void clear(Graphics2D g) {
		this.ptr_x = 0;
		this.ptr_y = 0;
		for (int i = 0; i < size; i++) {
			this.data[i] = '\0';
		}
		image.getGraphics().setColor(Color.white);
		image.getGraphics().fillRect(0, 0, w, h);
		g.drawImage(image, 0, 0, null);
	}

	private void newline(Graphics2D g) {
		this.ptr_x = 0;
		int end = size - cols;
		System.arraycopy(data, cols, data, 0, size - cols);
		Arrays.fill(data, end, size - 1, '\0');
		image.getGraphics().copyArea(0, height, w, (rows - 1) * height, 0, -height);
		image.getGraphics().setColor(Color.white);
		image.getGraphics().fillRect(0, (rows - 1) * height, w, height);
		g.drawImage(image, 0, 0, null);
	}

	public boolean drawText(char c) {
		if (this.queue.size() == MAX_QUEUE_SIZE) { // 满了
			return true;
		}
		this.queue.add(c);
		return false;
	}

	public void setCaret(boolean caret) {
		if (this.caret != caret)
			this.caret = caret;
	}

	public boolean isHideCaret() {
		return !this.caretState && !this.caret;
	}

	private void markInput() {
		ptr_mx = ptr_x;
		ptr_my = ptr_y;
	}

	public void fallback() {
		int x = ptr_x, y = ptr_y;
		while (ptr_mx + ptr_my * cols < x + y * cols) {
			if (x == 0) {
				x = cols - 1;
				y--;
			}
			this.data[y * cols + x] = '\0';
			image.getGraphics().drawImage(fontImage.getImage('\0'),
					x * width, y * height, null);
			x--;
		}
		ptr_x = x;
		ptr_y = y;
	}

	public int getCols() {
		return cols;
	}

	public int getRows() {
		return rows;
	}

	public int calcWidth(String str) {
		return UIFontImage.calcWidth(str);
	}

	private void setFGColor(int r, int g, int b) {
		fontImage.setFGColor(new Color(r, g, b));
	}

	private void setBGColor(int r, int g, int b) {
		fontImage.setBGColor(new Color(r, g, b));
	}

	public int getWidth() {
		return w;
	}

	public int getHeight() {
		return h;
	}
}
