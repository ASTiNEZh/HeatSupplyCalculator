package ru.ASTiNEZh.UI.dialogs;

import ru.ASTiNEZh.logicCore.supplyers.people.TypeActivity;
import ru.ASTiNEZh.UI.common.FormDialog;
import ru.ASTiNEZh.UI.common.Labels;
import ru.ASTiNEZh.UI.common.UiUtil;
import ru.ASTiNEZh.UI.model.RoomSpec.PersonSpec;

import javax.swing.*;
import java.awt.Component;

/** Диалог создания/редактирования группы людей. */
public class PersonDialog extends FormDialog<PersonSpec> {

    private final JComboBox<TypeActivity> typeBox = UiUtil.combo(TypeActivity.values(), Labels::activity);
    private final JComboBox<Integer> tempBox = UiUtil.combo(new Integer[]{22, 25}, t -> t + " °C");
    private final JLabel preview = new JLabel();
    private final JSpinner countSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 1000, 1));

    private PersonDialog(Component parent, PersonSpec existing) {
        super(parent, existing == null ? "Новые люди" : "Редактирование людей");

        typeBox.setSelectedItem(existing == null ? TypeActivity.REST : existing.type());
        tempBox.setSelectedItem(existing == null ? Integer.valueOf(22) : Integer.valueOf(existing.degrees()));
        countSpinner.setValue(existing == null ? 1 : existing.count());

        typeBox.addActionListener(e -> updatePreview());
        tempBox.addActionListener(e -> updatePreview());
        updatePreview();

        addRow("Вид деятельности:", typeBox);
        addRow("Температура в помещении:", tempBox);
        addRow("Тепловыделение одного человека:", preview);
        addRow("Количество человек:", countSpinner);
    }

    public static PersonSpec edit(Component parent, PersonSpec existing) {
        return new PersonDialog(parent, existing).showDialog();
    }

    private void updatePreview() {
        TypeActivity type = (TypeActivity) typeBox.getSelectedItem();
        Integer degrees = (Integer) tempBox.getSelectedItem();
        if (type == null || degrees == null) {
            preview.setText("—");
            return;
        }
        float heat = degrees == 22 ? type.get_22degrees() : type.get_25degrees();
        preview.setText(UiUtil.fmt(heat) + " Вт");
    }

    @Override
    protected PersonSpec collect() {
        return new PersonSpec(
                (TypeActivity) typeBox.getSelectedItem(),
                (Integer) tempBox.getSelectedItem(),
                (Integer) countSpinner.getValue()
        );
    }
}
