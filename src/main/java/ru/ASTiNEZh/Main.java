package ru.ASTiNEZh;

import javax.swing.*;

public class Main {
    static void main() {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
                // остаётся стандартный Look&Feel
            }
            new ru.ASTiNEZh.UI.MainFrame().setVisible(true);
        });
    }
}
