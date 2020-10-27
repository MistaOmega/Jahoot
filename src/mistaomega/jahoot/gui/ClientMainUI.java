package mistaomega.jahoot.gui;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class ClientMainUI extends Thread {
    private ArrayList<Color> colorList;
    private ArrayList<JPanel> panels;
    private JPanel mainPanel;
    private JPanel answerPane1;
    private JPanel answerPane2;
    private JPanel answerPane3;
    private JPanel answerPane4;
    private JFrame frame;

    public ClientMainUI() {

    }

    @Override
    public void run() {
        frame = new JFrame("Main GUI");
        frame.setContentPane(new ClientMainUI().mainPanel);
        frame.getContentPane().setBackground(Color.BLACK);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);

    }

    public void setPanelColors() {
        Random rnd = new Random();
        System.out.println("Hello");
        for (JPanel panel : panels) {
            int index = rnd.nextInt(colorList.size());
            System.out.println(index);
            panel.setBackground(colorList.get(index));
            colorList.remove(index);
        }
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
        colorList = new ArrayList<>(Arrays.asList(Color.MAGENTA, Color.CYAN, Color.ORANGE, Color.RED));
        panels = new ArrayList<>();
        answerPane1 = new JPanel();
        answerPane2 = new JPanel();
        answerPane3 = new JPanel();
        answerPane4 = new JPanel();
        panels.add(answerPane1);
        panels.add(answerPane2);
        panels.add(answerPane3);
        panels.add(answerPane4);
        setPanelColors();

    }


}
