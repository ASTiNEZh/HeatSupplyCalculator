package ru.ASTiNEZh.UI.frames;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    public MainFrame() {
        setTitle("HeSupCal v. 0.0.1");
        setSize(500, 170);
        /** Действие вызываемое нажатием на крестик окна */
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        /** Расположение окна на экране. Читай доки Swing */
        setLocationRelativeTo(null);
        // Добавить контент для окна
        add();
        setVisible(true);
    }
}
