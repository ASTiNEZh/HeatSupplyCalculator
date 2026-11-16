package ru.ASTiNEZh.UI.dialogs;

import ru.ASTiNEZh.logicCore.supplyers.householdAppliances.AppliancesTypes;
import ru.ASTiNEZh.UI.common.FormDialog;
import ru.ASTiNEZh.UI.common.Labels;
import ru.ASTiNEZh.UI.common.UiUtil;
import ru.ASTiNEZh.UI.model.RoomSpec.ApplianceSpec;

import javax.swing.*;
import java.awt.Component;

/** Диалог создания/редактирования оборудования. */
public class ApplianceDialog extends FormDialog<ApplianceSpec> {

    private final JComboBox<AppliancesTypes> typeBox =
            UiUtil.combo(AppliancesTypes.values(), Labels::applianceWithHeat);
    private final JCheckBox manualBox = new JCheckBox("Указать тепловыделение вручную");
    private final JTextField powerField = new JTextField(10);
    private final JSpinner countSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 1000, 1));

    private ApplianceDialog(Component parent, ApplianceSpec existing) {
        super(parent, existing == null ? "Новое оборудование" : "Редактирование оборудования");

        boolean manual = existing != null && existing.type() == null;
        manualBox.setSelected(manual);
        if (existing != null && existing.type() != null) {
            typeBox.setSelectedItem(existing.type());
        }
        powerField.setText(manual ? UiUtil.fmt(existing.customHeat()) : "100");
        countSpinner.setValue(existing == null ? 1 : existing.count());

        updateEnabled();
        manualBox.addActionListener(e -> updateEnabled());

        addRow("Тип оборудования:", typeBox);
        addWide(manualBox);
        addRow("Тепловыделение, Вт:", powerField);
        addRow("Количество:", countSpinner);
    }

    public static ApplianceSpec edit(Component parent, ApplianceSpec existing) {
        return new ApplianceDialog(parent, existing).showDialog();
    }

    private void updateEnabled() {
        boolean manual = manualBox.isSelected();
        typeBox.setEnabled(!manual);
        powerField.setEnabled(manual);
    }

    @Override
    protected ApplianceSpec collect() {
        int count = (Integer) countSpinner.getValue();
        if (manualBox.isSelected()) {
            return new ApplianceSpec(null, readPositive(powerField, "Тепловыделение"), count);
        }
        return new ApplianceSpec((AppliancesTypes) typeBox.getSelectedItem(), 0f, count);
    }
}
