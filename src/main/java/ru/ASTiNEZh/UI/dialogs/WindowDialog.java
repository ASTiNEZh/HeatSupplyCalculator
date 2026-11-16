package ru.ASTiNEZh.UI.dialogs;

import ru.ASTiNEZh.logicCore.supplyers.windows.WindowSide;
import ru.ASTiNEZh.UI.common.FormDialog;
import ru.ASTiNEZh.UI.common.Labels;
import ru.ASTiNEZh.UI.common.UiUtil;
import ru.ASTiNEZh.UI.model.RoomSpec.WindowSpec;

import javax.swing.*;
import java.awt.Component;

/** Диалог создания/редактирования окна. */
public class WindowDialog extends FormDialog<WindowSpec> {

    private final JComboBox<WindowSide> sideBox = UiUtil.combo(WindowSide.values(), Labels::sideWithHeat);
    private final JTextField widthField = new JTextField(10);
    private final JTextField heightField = new JTextField(10);
    private final JCheckBox uvBox = new JCheckBox("Установлена анти-УФ плёнка");
    private final JCheckBox shadowBox = new JCheckBox("Окно затенено");
    private final JTextField shadowField = new JTextField(10);
    private final JSpinner countSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 1000, 1));

    private WindowDialog(Component parent, WindowSpec existing) {
        super(parent, existing == null ? "Новое окно" : "Редактирование окна");

        sideBox.setSelectedItem(existing == null ? WindowSide.SOUTH_WINDOW : existing.side());
        widthField.setText(existing == null ? "1" : UiUtil.fmt(existing.width()));
        heightField.setText(existing == null ? "1" : UiUtil.fmt(existing.height()));
        uvBox.setSelected(existing != null && existing.uvFilter());
        shadowBox.setSelected(existing != null && existing.inShadow());
        shadowField.setText(existing == null ? "0" : UiUtil.fmt(existing.shadowSupply()));
        countSpinner.setValue(existing == null ? 1 : existing.count());

        shadowField.setEnabled(shadowBox.isSelected());
        shadowBox.addActionListener(e -> shadowField.setEnabled(shadowBox.isSelected()));

        addRow("Сторона света:", sideBox);
        addRow("Ширина, м:", widthField);
        addRow("Высота, м:", heightField);
        addWide(uvBox);
        addWide(shadowBox);
        addRow("Теплопоступление в тени, Вт/м²:", shadowField);
        addRow("Количество одинаковых окон:", countSpinner);
    }

    public static WindowSpec edit(Component parent, WindowSpec existing) {
        return new WindowDialog(parent, existing).showDialog();
    }

    @Override
    protected WindowSpec collect() {
        float width = readPositive(widthField, "Ширина");
        float height = readPositive(heightField, "Высота");
        boolean shadow = shadowBox.isSelected();
        float shadowSupply = shadow ? readNonNegative(shadowField, "Теплопоступление в тени") : 0f;

        return new WindowSpec(
                (WindowSide) sideBox.getSelectedItem(),
                width,
                height,
                uvBox.isSelected(),
                shadow,
                shadowSupply,
                (Integer) countSpinner.getValue()
        );
    }
}
