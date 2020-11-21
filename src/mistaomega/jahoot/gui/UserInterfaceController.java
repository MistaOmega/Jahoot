package mistaomega.jahoot.gui;

import javax.swing.*;

public abstract class UserInterfaceController implements IUI {
    JFrame frame;
    public UserInterfaceController(JFrame frame){
        this.frame = frame;
    }
    public abstract void run();

    public void show(){
        frame.setVisible(true);
    }

    public void hide(){
        frame.setVisible(false);
    }

    public JFrame getFrame() {
        return frame;
    }
}
