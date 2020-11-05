package mistaomega.jahoot.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class ClientMainUI extends Thread {
    private ArrayList<String> colorList;
    private ArrayList<JPanel> panels;
    private JPanel mainPanel;
    private JPanel answerPane1;
    private JPanel answerPane2;
    private JPanel answerPane3;
    private JPanel answerPane4;
    private JTextField tfTitle;
    private JTextField tfQuestion;
    private JLabel lblAnswer1;
    private JLabel lblAnswer3;
    private JLabel lblAnswer4;
    private JButton btnAnswer2;
    private JFrame frame;

    public ClientMainUI() {

        btnAnswer2.addActionListener(e -> {
            System.out.println("your dead nan");
        });
    }

    @Override
    public void run() {
        frame = new JFrame("Main GUI");
        frame.setContentPane(new ClientMainUI().mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);

    }

    public void setPanelColors() {
        Random rnd = new Random();
        for (JPanel panel : panels) {
            int index = rnd.nextInt(colorList.size());
            panel.setBackground(Color.decode(colorList.get(index)));
            colorList.remove(index);
        }
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
        colorList = new ArrayList<>(Arrays.asList(JahootColors.JAHOOTBLUE.getHex(), JahootColors.JAHOOTLIME.getHex(), JahootColors.JAHOOTORANGE.getHex(), JahootColors.JAHOOTPINK.getHex()));
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
