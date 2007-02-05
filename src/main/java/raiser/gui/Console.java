package raiser.gui;

import java.awt.*;
import java.awt.event.*;
import java.net.URL;
import javax.swing.*;

public class Console {
    class MyPanel extends JPanel {
        private raiser.gui.Console console;

        MyPanel(JPanel panel) {
            setLayout(new BorderLayout());
            add(panel, BorderLayout.CENTER);
            this.console = Console.this;
        }

        public Console getConsole() {
            return console;
        }

        public Frame getFrame() {
            return console.frame;
        }

        public void invalidate() {
            super.invalidate();
            pack();
        }
    }

    private static Point computeCentered(int width, int height) {
        Point result = new Point();
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        result.x = (int) ((toolkit.getScreenSize().getWidth() - width) / 2);
        result.y = (int) ((toolkit.getScreenSize().getHeight() - height) / 2);
        return result;
    }

    public static Console getConsole(JPanel panel) {
        return ((MyPanel) panel.getParent()).getConsole();
    }

    public static Frame getFrame(Object view) {
        if (view instanceof JFrame) {
            return (Frame) view;
        } else if (view instanceof JPanel) {
            return ((MyPanel) ((JPanel) view).getParent()).getConsole().getFrame();
        }
        return null;
    }

    public static void makeCentred(JDialog dialog) {
        dialog.setLocation(computeCentered(dialog.getWidth(), dialog.getHeight()));
    }

    public static Thread run(JApplet applet, int left, int top, int width, int height) {
        JFrame frame = new JFrame(title(applet));
        Thread result = setupClosing(frame);
        frame.getContentPane().add(applet);
        frame.setLocation(left, top);
        frame.setSize(width, height);
        applet.init();
        applet.start();
        frame.setVisible(true);
        return result;
    }

    public static Thread run(JFrame frame, int left, int top, int width, int height) {
        Thread result = setupClosing(frame);
        frame.setLocation(left, top);
        frame.setSize(width, height);
        frame.setVisible(true);
        return result;
    }

    public static Thread run(JFrame frame, int width, int height) {
        Thread result = setupClosing(frame);
        int maxWidth = (int) frame.getGraphicsConfiguration().getBounds().getWidth();
        int maxHeight = (int) frame.getGraphicsConfiguration().getBounds().getHeight();
        frame.setSize(width, height);
        frame.setLocation((maxWidth - width) / 2, (maxHeight - height) / 2);
        frame.setVisible(true);
        return result;
    }

    public static Thread run(JPanel panel) {
        return run(panel, (int) panel.getPreferredSize().getWidth(), (int) panel.getPreferredSize()
                .getHeight());
    }

    public static Thread run(JPanel panel, int width, int height) {
        final JFrame frame = new JFrame(title(panel));
        Thread result = setupClosing(frame);
        frame.getContentPane().add(panel);
        int maxWidth = (int) frame.getGraphicsConfiguration().getBounds().getWidth();
        int maxHeight = (int) frame.getGraphicsConfiguration().getBounds().getHeight();
        frame.setSize(width, height);
        frame.setLocation((maxWidth - width) / 2, (maxHeight - height) / 2);
        frame.setVisible(true);
        return result;
    }

    public static Thread run(JPanel panel, int left, int top, int width, int height) {
        JFrame frame = new JFrame(title(panel));
        Thread result = setupClosing(frame);
        frame.getContentPane().add(panel);
        frame.setLocation(left, top);
        frame.setSize(width, height);
        frame.setVisible(true);
        return result;
    }

    public static Thread run(String title, JPanel panel) {
        int width = (int) panel.getPreferredSize().getWidth();
        int height = (int) panel.getPreferredSize().getHeight();
        JFrame frame = new JFrame(title);
        Thread result = setupClosing(frame);
        frame.getContentPane().add(panel);
        int maxWidth = (int) frame.getGraphicsConfiguration().getBounds().getWidth();
        int maxHeight = (int) frame.getGraphicsConfiguration().getBounds().getHeight();
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
            public void windowClosing(WindowEvent e) {
                frame.dispose();
                super.windowClosing(e);
                aThread.interrupt();
            }
        });
        return aThread;
    }

    // Create a title string from the class name:
    public static String title(Object o) {
        String t = o.getClass().toString();
        // Remove the word "class":
        if (t.indexOf("class") != -1)
            t = t.substring(6);
        return t;
    }

    ConsoleListener consoleListener;
    private JFrame frame;
    private int height;
    private boolean resizable;
    private boolean running;
    private Object synchronizer = new byte[0];
    private String title;
    private int width;
    private boolean confirmOnExit;

    public Console(String title, int width, int height, boolean resize) {
        this.title = title;
        this.width = width;
        this.height = height;
        this.resizable = resize;
        this.confirmOnExit = false;
    }

    public void exit() {
        boolean exit = true;
        if (confirmOnExit) {
            Object[] options =
                {
                    "Yes",
                    "No"
                };
            int n = JOptionPane.showOptionDialog(frame, "Exit ?", "Confirm Exit",
                    JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options,
                    options[1]);
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

    public Console show(Object view) {
        if (view instanceof JPanel) {
            return showPanel((JPanel) view);
        }
        if (view instanceof JFrame) {
            return showFrame((JFrame) view);
        }
        throw new RuntimeException("Can't show object " + view + ".");
    }

    private Console showPanel(JPanel panel) {
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

    public void setRunning(boolean running) {
        synchronized (synchronizer) {
            this.running = running;
        }
    }

    public void setupClosing() {
        // The JDK 1.2 Solution as an
        // anonymous inner class:
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                exit();
            }
        });
        // The improved solution in JDK 1.3:
        // frame.setDefaultCloseOperation(
        // EXIT_ON_CLOSE);
    }

    public static Thread run(JComponent component) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JScrollPane(component, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS), BorderLayout.CENTER);
        return run(panel);
    }

    public static void center(JDialog dialog) {
        int width = dialog.getWidth();
        int height = dialog.getHeight();
        dialog.setLocation(computeCentered(width, height));
    }

    /**
     * @param owner
     * @param string
     * @return
     */
    public static JWindow createSplash(Frame owner, String title, String fileName) {
        JWindow frame = new JWindow(owner);
        URL url = Console.class.getClassLoader().getResource(fileName);
        if (url != null) {
            frame.getContentPane().add(new JLabel(new ImageIcon(url)));
        } else {
            frame.getContentPane().add(new JLabel(title));
        }
        int width = 640;
        int height = 213;
        frame.setLocation(computeCentered(width, height));
        //frame.setTitle(title);
        frame.setSize(width, height);
        frame.setVisible(true);
        return frame;
    }

    private Console showFrame(JFrame frame) {
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

    public void setConfirmOnExit(boolean confirmOnExit) {
        this.confirmOnExit = confirmOnExit;
    }
}
