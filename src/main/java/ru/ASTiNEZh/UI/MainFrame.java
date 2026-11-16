package ru.ASTiNEZh.UI;

import ru.ASTiNEZh.UI.common.ListEditorPanel;
import ru.ASTiNEZh.UI.common.UiUtil;
import ru.ASTiNEZh.UI.dialogs.RoomEditorDialog;
import ru.ASTiNEZh.UI.model.RoomSpec;

import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class MainFrame extends JFrame {

    private final List<RoomSpec> rooms = new ArrayList<>();
    private final JLabel totalLabel = new JLabel("Итого по всем помещениям: —");
    private final ListEditorPanel<RoomSpec> panel;

    public MainFrame() {
        super("Расчёт теплопритоков — помещения");
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        panel = new ListEditorPanel<RoomSpec>(
                new String[]{"№", "Наименование", "Теплоприток (Вт)"},
                r -> new Object[]{
                        UiUtil.fmt(r.getNumber()),
                        r.getName(),
                        r.getCalculatedSupply() == null ? "—" : UiUtil.fmt1(r.getCalculatedSupply())
                },
                (parent, existing) -> RoomEditorDialog.edit(parent, existing,
                        number -> isNumberTaken(number, existing)),
                rooms);
        panel.setComparator(Comparator.comparing(RoomSpec::getNumber));
        panel.setDeleteMessage(r -> "Удалить помещение «" + UiUtil.fmt(r.getNumber())
                + " — " + r.getName() + "»?");
        panel.getTable().getColumnModel().getColumn(0).setMaxWidth(120);
        panel.getTable().getColumnModel().getColumn(2).setPreferredWidth(150);
        panel.getTable().getColumnModel().getColumn(2).setMaxWidth(200);
        panel.addExtraButton("Рассчитать", this::calculateAll);

        totalLabel.setFont(totalLabel.getFont().deriveFont(Font.BOLD, 14f));
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 8));
        bottom.add(totalLabel);

        setLayout(new BorderLayout());
        add(panel, BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);

        setSize(660, 440);
        setMinimumSize(new Dimension(420, 300));
        setLocationRelativeTo(null);
    }

    private void calculateAll() {
        if (rooms.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Список помещений пуст.", "Расчёт",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        float total = 0;
        int failed = 0;
        StringBuilder errors = new StringBuilder();
        for (RoomSpec room : rooms) {
            try {
                float supply = room.calculateSupply();
                room.setCalculatedSupply(supply);
                total += supply;
            } catch (RuntimeException ex) {
                room.setCalculatedSupply(null);
                failed++;
                errors.append("• ").append(UiUtil.fmt(room.getNumber())).append(' ')
                        .append(room.getName()).append(": ")
                        .append(UiUtil.errorText(ex)).append('\n');
            }
        }
        panel.refresh();

        totalLabel.setText("Итого по всем помещениям: " + UiUtil.fmt1(total) + " Вт ("
                + String.format("%.2f", total / 1000) + " кВт)"
                + (failed > 0 ? " — без помещений с ошибками: " + failed : ""));

        if (failed > 0) {
            JOptionPane.showMessageDialog(this,
                    "Не удалось рассчитать:\n" + errors,
                    "Ошибки расчёта", JOptionPane.WARNING_MESSAGE);
        }
    }

    private boolean isNumberTaken(float number, RoomSpec except) {
        for (RoomSpec r : rooms) {
            if (r != except && Float.compare(r.getNumber(), number) == 0) {
                return true;
            }
        }
        return false;
    }
}
