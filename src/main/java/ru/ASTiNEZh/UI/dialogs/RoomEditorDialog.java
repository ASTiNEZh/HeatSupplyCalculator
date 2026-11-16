package ru.ASTiNEZh.UI.dialogs;

import ru.ASTiNEZh.UI.common.FormDialog;
import ru.ASTiNEZh.UI.common.Labels;
import ru.ASTiNEZh.UI.common.ListEditorPanel;
import ru.ASTiNEZh.UI.common.UiUtil;
import ru.ASTiNEZh.UI.model.RoomSpec;
import ru.ASTiNEZh.UI.model.RoomSpec.ApplianceSpec;
import ru.ASTiNEZh.UI.model.RoomSpec.PersonSpec;
import ru.ASTiNEZh.UI.model.RoomSpec.RoofSpec;
import ru.ASTiNEZh.UI.model.RoomSpec.VentilationSpec;
import ru.ASTiNEZh.UI.model.RoomSpec.WindowSpec;

import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.function.Predicate;

/**
 * Диалог создания/редактирования помещения: номер, наименование и вкладки
 * «Окна», «Люди», «Оборудование», «Вентиляция и кровля».
 * Работает с копией RoomSpec — отмена ничего не меняет.
 */
public class RoomEditorDialog extends FormDialog<RoomSpec> {

    private final RoomSpec work;
    private final Predicate<Float> numberTaken;

    private final JTextField numberField = new JTextField(12);
    private final JTextField nameField = new JTextField(28);

    private final JCheckBox ventBox = new JCheckBox("Есть приточная вентиляция");
    private final JTextField airFlowField = new JTextField(10);
    private final JTextField internalTempField = new JTextField(10);

    private final JCheckBox roofBox = new JCheckBox("Есть теплоприток от кровли");
    private final JTextField roofAreaField = new JTextField(10);

    private final JLabel resultLabel = new JLabel("Теплоприток: —");

    private RoomEditorDialog(Component parent, RoomSpec existing, Predicate<Float> numberTaken) {
        super(parent, existing == null
                ? "Новое помещение"
                : "Помещение " + UiUtil.fmt(existing.getNumber()) + " — " + existing.getName());
        this.work = existing == null ? new RoomSpec() : existing.copy();
        this.numberTaken = numberTaken;

        if (existing != null) {
            numberField.setText(UiUtil.fmt(existing.getNumber()));
            nameField.setText(existing.getName());
        }
        addRow("Номер помещения:", numberField);
        addRow("Наименование:", nameField);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Окна", windowsTab());
        tabs.addTab("Люди", peopleTab());
        tabs.addTab("Оборудование", appliancesTab());
        tabs.addTab("Вентиляция и кровля", ventilationAndRoofTab());
        tabs.setPreferredSize(new Dimension(760, 340));

        JPanel content = new JPanel(new BorderLayout());
        content.add(form, BorderLayout.NORTH);
        content.add(tabs, BorderLayout.CENTER);
        content.add(calculatePanel(), BorderLayout.SOUTH);
        setContent(content);
    }

    /**
     * @param existing    null — создание нового помещения
     * @param numberTaken проверка занятости номера другим помещением
     * @return новое описание помещения или null, если пользователь отменил
     */
    public static RoomSpec edit(Component parent, RoomSpec existing, Predicate<Float> numberTaken) {
        return new RoomEditorDialog(parent, existing, numberTaken).showDialog();
    }

    // ---------------------------------------------------------------- вкладки

    private JComponent windowsTab() {
        ListEditorPanel<WindowSpec> panel = new ListEditorPanel<WindowSpec>(
                new String[]{"Сторона света", "Ширина, м", "Высота, м", "УФ-плёнка",
                        "Затенено", "Кол-во", "Теплопоступление, Вт"},
                w -> new Object[]{
                        Labels.side(w.side()),
                        UiUtil.fmt(w.width()),
                        UiUtil.fmt(w.height()),
                        w.uvFilter() ? "да" : "нет",
                        w.inShadow() ? "да" : "нет",
                        w.count(),
                        UiUtil.fmt1(w.totalHeat())
                },
                WindowDialog::edit,
                work.getWindows());
        panel.setColumnWidths(110, 70, 70, 80, 80, 60, 170);
        return panel;
    }

    private JComponent peopleTab() {
        return new ListEditorPanel<PersonSpec>(
                new String[]{"Вид деятельности", "Температура, °C", "Кол-во", "Теплопоступление, Вт"},
                p -> new Object[]{
                        Labels.activity(p.type()),
                        p.degrees(),
                        p.count(),
                        UiUtil.fmt1(p.totalHeat())
                },
                PersonDialog::edit,
                work.getPeople());
    }

    private JComponent appliancesTab() {
        return new ListEditorPanel<ApplianceSpec>(
                new String[]{"Оборудование", "Кол-во", "Теплопоступление, Вт"},
                a -> new Object[]{
                        a.type() == null ? "Другое (вручную)" : Labels.appliance(a.type()),
                        a.count(),
                        UiUtil.fmt1(a.totalHeat())
                },
                ApplianceDialog::edit,
                work.getAppliances());
    }

    private JComponent ventilationAndRoofTab() {
        VentilationSpec v = work.getVentilation();
        ventBox.setSelected(v != null);
        airFlowField.setText(v == null ? "0" : UiUtil.fmt(v.airFlowRate()));
        internalTempField.setText(v == null ? "22" : UiUtil.fmt(v.internalTemp()));

        RoofSpec r = work.getRoof();
        roofBox.setSelected(r != null);
        roofAreaField.setText(r == null ? "0" : UiUtil.fmt(r.area()));

        bindEnabled(ventBox, airFlowField, internalTempField);
        bindEnabled(roofBox, roofAreaField);

        JPanel ventPanel = group("Приточная вентиляция", ventBox,
                new String[]{"Расход воздуха, м³/ч:", "Температура в помещении, °C:"},
                new JTextField[]{airFlowField, internalTempField});
        JPanel roofPanel = group("Кровля", roofBox,
                new String[]{"Площадь кровли, м²:"},
                new JTextField[]{roofAreaField});

        JPanel stack = new JPanel();
        stack.setLayout(new BoxLayout(stack, BoxLayout.Y_AXIS));
        stack.add(ventPanel);
        stack.add(Box.createVerticalStrut(10));
        stack.add(roofPanel);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        wrapper.add(stack, BorderLayout.NORTH);
        return wrapper;
    }

    /** Кнопка «Рассчитать» и поле с итоговым теплопритоком. */
    private JComponent calculatePanel() {
        JButton calcButton = new JButton("Рассчитать");
        calcButton.addActionListener(e -> onCalculate());
        resultLabel.setFont(resultLabel.getFont().deriveFont(Font.BOLD, 14f));

        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 8));
        panel.add(calcButton);
        panel.add(resultLabel);
        return panel;
    }

    /** Считает по текущему состоянию формы (без сохранения). */
    private void onCalculate() {
        try {
            RoomSpec snapshot = work.copy();
            applyVentilationAndRoof(snapshot);
            float total = snapshot.calculateSupply();
            resultLabel.setText("Теплоприток: " + UiUtil.fmt1(total) + " Вт ("
                    + String.format("%.2f", total / 1000) + " кВт)");
        } catch (InputException ex) {
            showInputError(ex);
        } catch (RuntimeException ex) {
            resultLabel.setText("Теплоприток: —");
            JOptionPane.showMessageDialog(this,
                    "Не удалось рассчитать: " + UiUtil.errorText(ex),
                    "Ошибка расчёта", JOptionPane.WARNING_MESSAGE);
        }
    }

    private static void bindEnabled(JCheckBox box, JTextField... fields) {
        Runnable update = () -> {
            for (JTextField f : fields) {
                f.setEnabled(box.isSelected());
            }
        };
        box.addActionListener(e -> update.run());
        update.run();
    }

    private static JPanel group(String title, JCheckBox box, String[] labels, JTextField[] fields) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder(title));

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(4, 6, 4, 6);
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.HORIZONTAL;

        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 2;
        panel.add(box, c);

        c.gridwidth = 1;
        for (int i = 0; i < labels.length; i++) {
            c.gridy = i + 1;
            c.gridx = 0;
            c.weightx = 0;
            panel.add(new JLabel(labels[i]), c);
            c.gridx = 1;
            c.weightx = 1;
            panel.add(fields[i], c);
        }
        return panel;
    }

    // --------------------------------------------------------------- результат

    @Override
    protected RoomSpec collect() {
        float number = readFloat(numberField, "Номер помещения");
        String name = nameField.getText().trim();
        if (name.isEmpty()) {
            throw new InputException("Введите наименование помещения.", nameField);
        }
        if (numberTaken.test(number)) {
            throw new InputException("Помещение с таким номером уже есть в списке.", numberField);
        }

        applyVentilationAndRoof(work);
        work.setNumber(number);
        work.setName(name);
        return work;
    }

    /** Читает вкладку «Вентиляция и кровля» и записывает в target. */
    private void applyVentilationAndRoof(RoomSpec target) {
        target.setVentilation(ventBox.isSelected()
                ? new VentilationSpec(
                        readNonNegative(airFlowField, "Расход воздуха"),
                        readFloat(internalTempField, "Температура в помещении"))
                : null);
        target.setRoof(roofBox.isSelected()
                ? new RoofSpec(readNonNegative(roofAreaField, "Площадь кровли"))
                : null);
    }
}
