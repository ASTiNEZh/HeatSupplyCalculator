package ru.ASTiNEZh.UI.common;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;

/**
 * Базовый модальный диалог с кнопками «OK» / «Отмена».
 * Наследник добавляет поля через addRow(...) и реализует collect(),
 * который читает поля и возвращает результат либо бросает InputException.
 */
public abstract class FormDialog<T> extends JDialog {

    /** Ошибка ввода: показывается пользователю, диалог остаётся открытым. */
    public static class InputException extends RuntimeException {
        private final JComponent focus;

        public InputException(String message, JComponent focus) {
            super(message);
            this.focus = focus;
        }

        public JComponent getFocus() {
            return focus;
        }
    }

    protected final JPanel form = new JPanel(new GridBagLayout());
    private JComponent content = form;
    private int row = 0;
    private T result;

    protected FormDialog(Component parent, String title) {
        super(parent == null ? null : SwingUtilities.getWindowAncestor(parent),
                title, Dialog.ModalityType.APPLICATION_MODAL);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        form.setBorder(BorderFactory.createEmptyBorder(12, 12, 4, 12));

        JButton ok = new JButton("OK");
        JButton cancel = new JButton("Отмена");
        ok.addActionListener(e -> onOk());
        cancel.addActionListener(e -> dispose());

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 8));
        buttons.add(ok);
        buttons.add(cancel);

        getRootPane().setDefaultButton(ok);
        getRootPane().registerKeyboardAction(
                e -> dispose(),
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_IN_FOCUSED_WINDOW);

        setLayout(new BorderLayout());
        add(form, BorderLayout.CENTER);
        add(buttons, BorderLayout.SOUTH);
    }

    /** Заменяет содержимое по умолчанию (простую форму) на свой компонент. */
    protected void setContent(JComponent component) {
        getContentPane().remove(content);
        content = component;
        getContentPane().add(component, BorderLayout.CENTER);
    }

    protected void addRow(String label, JComponent field) {
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(4, 4, 4, 4);
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridy = row++;

        c.gridx = 0;
        c.weightx = 0;
        form.add(new JLabel(label), c);

        c.gridx = 1;
        c.weightx = 1;
        form.add(field, c);
    }

    /** Компонент на всю ширину формы (например, флажок). */
    protected void addWide(JComponent component) {
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(4, 4, 4, 4);
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridy = row++;
        c.gridx = 0;
        c.gridwidth = 2;
        form.add(component, c);
    }

    /** Читает поля формы. При ошибке бросает InputException. */
    protected abstract T collect();

    private void onOk() {
        try {
            result = collect();
            dispose();
        } catch (InputException ex) {
            showInputError(ex);
        }
    }

    protected void showInputError(InputException ex) {
        JOptionPane.showMessageDialog(this, ex.getMessage(), "Ошибка ввода",
                JOptionPane.WARNING_MESSAGE);
        if (ex.getFocus() != null) {
            ex.getFocus().requestFocusInWindow();
        }
    }

    /** Показывает диалог; возвращает результат или null, если пользователь отменил. */
    public T showDialog() {
        pack();
        setLocationRelativeTo(getOwner());
        setVisible(true);
        return result;
    }

    // ---- чтение чисел из текстовых полей (допускается запятая) ----

    protected static float readFloat(JTextField field, String name) {
        try {
            float v = Float.parseFloat(field.getText().trim().replace(',', '.'));
            if (Float.isNaN(v) || Float.isInfinite(v)) {
                throw new NumberFormatException();
            }
            return v;
        } catch (NumberFormatException ex) {
            throw new InputException("Поле «" + name + "» должно быть числом.", field);
        }
    }

    protected static float readPositive(JTextField field, String name) {
        float v = readFloat(field, name);
        if (v <= 0) {
            throw new InputException("Поле «" + name + "» должно быть больше нуля.", field);
        }
        return v;
    }

    protected static float readNonNegative(JTextField field, String name) {
        float v = readFloat(field, name);
        if (v < 0) {
            throw new InputException("Поле «" + name + "» не может быть отрицательным.", field);
        }
        return v;
    }
}
