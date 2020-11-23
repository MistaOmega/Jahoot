package mistaomega.jahoot.gui;

import javax.swing.*;

/**
 * Abstract implementation of a Controller Class for the UI
 * @author Jack Nash
 * @version 1.0
 */
public abstract class UserInterfaceControllerClass implements IUI {
    JFrame frame;

    public UserInterfaceControllerClass(JFrame frame) {
        this.frame = frame;
    }

    public abstract void run();

    public void show() {
        frame.setVisible(true);
    }

    public void hide() {
        frame.setVisible(false);
    }

    public JFrame getFrame() {
        return frame;
    }
}
