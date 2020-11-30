package mistaomega.jahoot.gui;

import javax.swing.*;

/**
 * Abstract implementation of a Controller Class for the UI
 * @author Jack Nash
 * @version 1.0
 */
public abstract class UserInterfaceControllerClass implements IUI {
    JFrame frame;

    /**
     * Constructor
     * @param frame Instance of a JFrame for the main window
     */
    public UserInterfaceControllerClass(JFrame frame) {
        this.frame = frame;
    }

    public abstract void run();

    /**
     * Shows the window
     */
    public void show() {
        frame.setVisible(true);
    }

    /**
     * Hides the window
     */
    public void hide() {
        frame.setVisible(false);
    }

}
