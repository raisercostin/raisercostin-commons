package org.raisercostin.gui;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JApplet;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JWindow;
import javax.swing.ScrollPaneConstants;
import javax.swing.WindowConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Console {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory.getLogger(Console.class);

	class MyPanel extends JPanel {
		/**
		 * 
		 */
		private static final long serialVersionUID = 8919476322608024334L;

		private final org.raisercostin.gui.Console console;

		MyPanel(final JPanel panel) {
			setLayout(new BorderLayout());
			add(panel, BorderLayout.CENTER);
			console = Console.this;
		}

		public Console getConsole() {
			return console;
		}

		public Frame getFrame() {
			return console.frame;
		}

		@Override
		public void invalidate() {
			super.invalidate();
			pack();
		}
	}

	private static Point computeCentered(final int width, final int height) {
		final Point result = new Point();
		final Toolkit toolkit = Toolkit.getDefaultToolkit();
		result.x = (int) ((toolkit.getScreenSize().getWidth() - width) / 2);
		result.y = (int) ((toolkit.getScreenSize().getHeight() - height) / 2);
		return result;
	}

	public static Console getConsole(final JPanel panel) {
		return ((MyPanel) panel.getParent()).getConsole();
	}

	public static Frame getFrame(final Object view) {
		if (view instanceof JFrame) {
			return (Frame) view;
		} else if (view instanceof JPanel) {
			return ((MyPanel) ((JPanel) view).getParent()).getConsole()
					.getFrame();
		}
		return null;
	}

	public static void makeCentred(final JDialog dialog) {
		dialog.setLocation(computeCentered(dialog.getWidth(), dialog
				.getHeight()));
	}

	public static Thread run(final JApplet applet, final int left,
			final int top, final int width, final int height) {
		final JFrame frame = new JFrame(title(applet));
		final Thread result = setupClosing(frame);
		frame.getContentPane().add(applet);
		frame.setLocation(left, top);
		frame.setSize(width, height);
		applet.init();
		applet.start();
		frame.setVisible(true);
		return result;
	}

	public static Thread run(final JFrame frame, final int left, final int top,
			final int width, final int height) {
		final Thread result = setupClosing(frame);
		frame.setLocation(left, top);
		frame.setSize(width, height);
		frame.setVisible(true);
		return result;
	}

	public static Thread run(final JFrame frame, final int width,
			final int height) {
		final Thread result = setupClosing(frame);
		final int maxWidth = (int) frame.getGraphicsConfiguration().getBounds()
				.getWidth();
		final int maxHeight = (int) frame.getGraphicsConfiguration()
				.getBounds().getHeight();
		frame.setSize(width, height);
		frame.setLocation((maxWidth - width) / 2, (maxHeight - height) / 2);
		frame.setVisible(true);
		return result;
	}

	public static Thread run(final JPanel panel) {
		return run(panel, (int) panel.getPreferredSize().getWidth(),
				(int) panel.getPreferredSize().getHeight());
	}

	public static Thread run(final JPanel panel, final int width,
			final int height) {
		final JFrame frame = new JFrame(title(panel));
		final Thread result = setupClosing(frame);
		frame.getContentPane().add(panel);
		final int maxWidth = (int) frame.getGraphicsConfiguration().getBounds()
				.getWidth();
		final int maxHeight = (int) frame.getGraphicsConfiguration()
				.getBounds().getHeight();
		frame.setSize(width, height);
		frame.setLocation((maxWidth - width) / 2, (maxHeight - height) / 2);
		frame.setVisible(true);
		return result;
	}

	public static Thread run(final JPanel panel, final int left, final int top,
			final int width, final int height) {
		final JFrame frame = new JFrame(title(panel));
		final Thread result = setupClosing(frame);
		frame.getContentPane().add(panel);
		frame.setLocation(left, top);
		frame.setSize(width, height);
		frame.setVisible(true);
		return result;
	}

	public static Thread run(final String title, final JPanel panel) {
		final int width = (int) panel.getPreferredSize().getWidth();
		final int height = (int) panel.getPreferredSize().getHeight();
		final JFrame frame = new JFrame(title);
		final Thread result = setupClosing(frame);
		frame.getContentPane().add(panel);
		final int maxWidth = (int) frame.getGraphicsConfiguration().getBounds()
				.getWidth();
		final int maxHeight = (int) frame.getGraphicsConfiguration()
				.getBounds().getHeight();
		if (panel instanceof Menuable) {
			frame.setJMenuBar(((Menuable) panel).getJMenuBar());
		}
		frame.setSize(width, height);
		frame.setLocation((maxWidth - width) / 2, (maxHeight - height) / 2);
		frame.setVisible(true);
		return result;
	}

	private static Thread setupClosing(final JFrame frame) {
		final Thread aThread = new Thread() {
			@Override
			public void run() {
				try {
					synchronized (this) {
						wait();
					}
				} catch (InterruptedException e) {
					// e.printStackTrace();
				}
			}
		};
		aThread.start();
		frame.addWindowListener(new WindowAdapter() {
			/*
			 * (non-Javadoc)
			 * 
			 * @see java.awt.event.WindowAdapter#windowClosing(java.awt.event.WindowEvent)
			 */
			@Override
			public void windowClosing(final WindowEvent e) {
				frame.dispose();
				super.windowClosing(e);
				aThread.interrupt();
			}
		});
		return aThread;
	}

	// Create a title string from the class name:
	public static String title(final Object o) {
		String t = o.getClass().toString();
		// Remove the word "class":
		if (t.indexOf("class") != -1) {
			t = t.substring(6);
		}
		return t;
	}

	ConsoleListener consoleListener;

	private JFrame frame;

	private final int height;

	private final boolean resizable;

	private boolean running;

	private final Object synchronizer = new byte[0];

	private final String title;

	private final int width;

	private boolean confirmOnExit;

	public Console(final String title, final int width, final int height,
			final boolean resize) {
		this.title = title;
		this.width = width;
		this.height = height;
		resizable = resize;
		confirmOnExit = false;
	}

	public void exit() {
		boolean exit = true;
		if (confirmOnExit) {
			final Object[] options = { "Yes", "No" };
			final int n = JOptionPane.showOptionDialog(frame, "Exit ?",
					"Confirm Exit", JOptionPane.YES_NO_OPTION,
					JOptionPane.QUESTION_MESSAGE, null, options, options[1]);
			exit = (n == 0);
		}
		if (exit) {
			if (consoleListener != null) {
				consoleListener.fireClose();
			}
			frame.dispose();
			synchronized (synchronizer) {
				setRunning(false);
				synchronizer.notifyAll();
			}
		}
	}

	public Frame getFrame() {
		return frame;
	}

	private boolean isRunning() {
		synchronized (synchronizer) {
			return running;
		}
	}

	public void join() throws InterruptedException {
		synchronized (synchronizer) {
			while (isRunning()) {
				synchronizer.wait();
			}
		}
	}

	public void pack() {
		if (!frame.isResizable()) {
			frame.pack();
		}
	}

	public Console show(final Object view) {
		if (view instanceof JPanel) {
			return showPanel((JPanel) view);
		}
		if (view instanceof JFrame) {
			return showFrame((JFrame) view);
		}
		throw new RuntimeException("Can't show object " + view + ".");
	}

	private Console showPanel(final JPanel panel) {
		setRunning(true);
		frame = new JFrame();
		setupClosing();
		if (panel instanceof Menuable) {
			frame.setJMenuBar(((Menuable) panel).getJMenuBar());
		}
		if (panel instanceof ConsoleListener) {
			((ConsoleListener) panel).setConsole(this);
			consoleListener = (ConsoleListener) panel;
		}
		frame.getContentPane().add(new MyPanel(panel));
		frame.setLocation(computeCentered(width, height));
		frame.setTitle(title);
		frame.setSize(width, height);
		frame.setVisible(true);
		frame.setResizable(resizable);
		return this;
	}

	public void setRunning(final boolean running) {
		synchronized (synchronizer) {
			this.running = running;
		}
	}

	public void setupClosing() {
		// The JDK 1.2 Solution as an
		// anonymous inner class:
		frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(final WindowEvent e) {
				exit();
			}
		});
		// The improved solution in JDK 1.3:
		// frame.setDefaultCloseOperation(
		// EXIT_ON_CLOSE);
	}

	public static Thread run(final JComponent component) {
		final JPanel panel = new JPanel(new BorderLayout());
		panel.add(new JScrollPane(component,
				ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS),
				BorderLayout.CENTER);
		return run(panel);
	}

	public static void center(final JDialog dialog) {
		final int width = dialog.getWidth();
		final int height = dialog.getHeight();
		dialog.setLocation(computeCentered(width, height));
	}

	/**
	 * @param owner
	 * @param string
	 * @return
	 */
	public static JWindow createSplash(final Frame owner, final String title,
			final String fileName) {
		final JWindow frame = new JWindow(owner);
		final URL url = Console.class.getClassLoader().getResource(fileName);
		if (url != null) {
			frame.getContentPane().add(new JLabel(new ImageIcon(url)));
		} else {
			frame.getContentPane().add(new JLabel(title));
		}
		final int width = 640;
		final int height = 213;
		frame.setLocation(computeCentered(width, height));
		// frame.setTitle(title);
		frame.setSize(width, height);
		frame.setVisible(true);
		return frame;
	}

	private Console showFrame(final JFrame frame) {
		setRunning(true);
		this.frame = frame;
		setupClosing();
		frame.setLocation(computeCentered(width, height));
		frame.setTitle(title);
		frame.setSize(width, height);
		frame.setVisible(true);
		frame.setResizable(resizable);
		return this;
	}

	public boolean isConfirmOnExit() {
		return confirmOnExit;
	}

	public void setConfirmOnExit(final boolean confirmOnExit) {
		this.confirmOnExit = confirmOnExit;
	}

	public void runBlocked(final JPanel view) {
		try {
			run(view).join();
		} catch (final InterruptedException e) {
			logger
					.warn(
							"While waiting for panel to be closed an exception occured.",
							e);
		}
	}
}
