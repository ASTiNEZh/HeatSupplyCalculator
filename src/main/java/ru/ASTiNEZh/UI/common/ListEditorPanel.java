package ru.ASTiNEZh.UI.common;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;

/**
 * Универсальная таблица со списком и кнопками «Создать / Редактировать / Удалить».
 * Работает прямо с переданным списком (изменяет его на месте).
 * Создание и редактирование выполняет Editor — обычно это диалог:
 * получает существующий элемент (или null) и возвращает новый элемент (или null при отмене).
 */
public class ListEditorPanel<T> extends JPanel {

    @FunctionalInterface
    public interface Editor<T> {
        T edit(Component parent, T existing);
    }

    private final List<T> items;
    private final String[] columns;
    private final Function<T, Object[]> rowMapper;
    private final Editor<T> editor;

    private final AbstractTableModel model;
    private final JTable table;
    private final JButton addButton = new JButton("Создать");
    private final JButton editButton = new JButton("Редактировать");
    private final JButton deleteButton = new JButton("Удалить");
    private final JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));

    private Comparator<T> comparator;
    private Function<T, String> deleteMessage = item -> "Удалить выбранную запись?";

    public ListEditorPanel(String[] columns, Function<T, Object[]> rowMapper,
                           Editor<T> editor, List<T> items) {
        this.columns = columns;
        this.rowMapper = rowMapper;
        this.editor = editor;
        this.items = items;

        this.model = new AbstractTableModel() {
            @Override
            public int getRowCount() {
                return ListEditorPanel.this.items.size();
            }

            @Override
            public int getColumnCount() {
                return ListEditorPanel.this.columns.length;
            }

            @Override
            public String getColumnName(int column) {
                return ListEditorPanel.this.columns[column];
            }

            @Override
            public Object getValueAt(int row, int column) {
                return ListEditorPanel.this.rowMapper
                        .apply(ListEditorPanel.this.items.get(row))[column];
            }
        };
        this.table = new JTable(model);

        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(26);
        table.setFillsViewportHeight(true);
        table.getTableHeader().setReorderingAllowed(false);

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                updateButtons();
            }
        });
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && table.rowAtPoint(e.getPoint()) >= 0) {
                    onEdit();
                }
            }
        });
        table.getInputMap(JComponent.WHEN_FOCUSED)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), "deleteItem");
        table.getActionMap().put("deleteItem", new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                onDelete();
            }
        });

        addButton.addActionListener(e -> onAdd());
        editButton.addActionListener(e -> onEdit());
        deleteButton.addActionListener(e -> onDelete());

        buttons.add(addButton);
        buttons.add(editButton);
        buttons.add(deleteButton);

        setLayout(new BorderLayout());
        add(buttons, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
        updateButtons();
    }

    public JTable getTable() {
        return table;
    }

    /** Относительные ширины колонок (используются как пропорции при раскладке). */
    public void setColumnWidths(int... widths) {
        for (int i = 0; i < widths.length && i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }
    }

    public void setComparator(Comparator<T> comparator) {
        this.comparator = comparator;
        refresh();
    }

    public void setDeleteMessage(Function<T, String> deleteMessage) {
        this.deleteMessage = deleteMessage;
    }

    /** Добавляет свою кнопку справа от стандартных. */
    public JButton addExtraButton(String text, Runnable action) {
        JButton button = new JButton(text);
        button.addActionListener(e -> action.run());
        buttons.add(button);
        buttons.revalidate();
        return button;
    }

    /** Пересортировать и перерисовать таблицу (после внешних изменений данных). */
    public void refresh() {
        if (comparator != null) {
            items.sort(comparator);
        }
        model.fireTableDataChanged();
    }

    private void updateButtons() {
        boolean selected = table.getSelectedRow() >= 0;
        editButton.setEnabled(selected);
        deleteButton.setEnabled(selected);
    }

    private void select(T item) {
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i) == item) {
                table.setRowSelectionInterval(i, i);
                table.scrollRectToVisible(table.getCellRect(i, 0, true));
                return;
            }
        }
    }

    private void onAdd() {
        T created = editor.edit(this, null);
        if (created != null) {
            items.add(created);
            refresh();
            select(created);
        }
    }

    private void onEdit() {
        int row = table.getSelectedRow();
        if (row < 0) {
            return;
        }
        T updated = editor.edit(this, items.get(row));
        if (updated != null) {
            items.set(row, updated);
            refresh();
            select(updated);
        }
    }

    private void onDelete() {
        int row = table.getSelectedRow();
        if (row < 0) {
            return;
        }
        int answer = JOptionPane.showConfirmDialog(this,
                deleteMessage.apply(items.get(row)),
                "Подтверждение удаления",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);
        if (answer == JOptionPane.YES_OPTION) {
            items.remove(row);
            refresh();
        }
    }
}
