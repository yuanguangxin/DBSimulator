package com.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class CMD extends JFrame implements KeyListener {
    private JTextArea jt1 = new JTextArea();
    private JScrollPane jsp1;
    private String path = new String("yuanguangxindeMacBook-Air:~ yuanguangxin");

    public CMD() {
        jt1.setForeground(Color.BLACK);
        jt1.setBackground(Color.WHITE);
        jt1.setText(path + "\n" + "$ ");
        jt1.addKeyListener(this);
        jsp1 = new JScrollPane(jt1);
        this.add(jsp1);
        this.setSize(630, 430);
        this.setLocation(400, 80);
        this.setTitle("yuanguangxin--bash - 80x24");
        this.setVisible(true);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
    }


    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == 10) {
            String s = this.jt1.getText();
            s = s + "\n" + path + "\n" + "$ ";
            jt1.setText(s);
        }
    }

    public static void main(String[] args) throws Exception {
        new CMD();
    }
}
