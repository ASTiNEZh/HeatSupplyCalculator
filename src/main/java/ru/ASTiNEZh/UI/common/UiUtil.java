package ru.ASTiNEZh.UI.common;

import javax.swing.*;
import java.awt.Component;
import java.util.function.Function;

/** Мелкие общие помощники для интерфейса. */
public final class UiUtil {

    private UiUtil() {
    }

    /** Число без хвоста «.0»: 12.0 -> "12", 1.4 -> "1.4". */
    public static String fmt(float value) {
        return value == Math.rint(value)
                ? String.valueOf((long) value)
                : String.valueOf(value);
    }

    /** Число с одним знаком после запятой (для теплопоступлений). */
    public static String fmt1(float value) {
        return String.format("%.1f", value);
    }

    /** Текст ошибки расчёта (у NullPointerException сообщения может не быть). */
    public static String errorText(RuntimeException ex) {
        return ex.getMessage() != null ? ex.getMessage() : ex.toString();
    }

    /** Выпадающий список, показывающий для элементов человекочитаемые подписи. */
    public static <E> JComboBox<E> combo(E[] values, Function<E, String> label) {
        JComboBox<E> box = new JComboBox<>(values);
        box.setRenderer(new DefaultListCellRenderer() {
            @Override
            @SuppressWarnings("unchecked")
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                          boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value != null) {
                    setText(label.apply((E) value));
                }
                return this;
            }
        });
        return box;
    }
}
