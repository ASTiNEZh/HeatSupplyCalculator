package ru.ASTiNEZh.UI.common;

import ru.ASTiNEZh.logicCore.supplyers.householdAppliances.AppliancesTypes;
import ru.ASTiNEZh.logicCore.supplyers.people.TypeActivity;
import ru.ASTiNEZh.logicCore.supplyers.windows.WindowSide;

/** Русские подписи для перечислений предметной области. */
public final class Labels {

    private Labels() {
    }

    public static String side(WindowSide side) {
        return switch (side) {
            case NORTH_WINDOW -> "Север";
            case NORTHEAST_WINDOW -> "Северо-восток";
            case EAST_WINDOW -> "Восток";
            case SOUTHEAST_WINDOW -> "Юго-восток";
            case SOUTH_WINDOW -> "Юг";
            case SOUTHWEST_WINDOW -> "Юго-запад";
            case WEST_WINDOW -> "Запад";
            case NORTHWEST_WINDOW -> "Северо-запад";
        };
    }

    public static String sideWithHeat(WindowSide side) {
        return side(side) + " — " + UiUtil.fmt(side.getLightsHeat()) + " Вт/м²";
    }

    public static String activity(TypeActivity type) {
        return switch (type) {
            case REST -> "Покой";
            case EASY1 -> "Лёгкая I";
            case MEDIUM2A -> "Средняя IIа";
            case MEDIUM2B -> "Средняя IIб";
            case HARD -> "Тяжёлая";
        };
    }

    public static String appliance(AppliancesTypes type) {
        return switch (type) {
            case PK -> "ПК";
            case LAPTOP -> "Ноутбук";
            case REFRIGERATOR -> "Холодильник";
            case TV -> "Телевизор";
            case PRINTER -> "Принтер";
        };
    }

    public static String applianceWithHeat(AppliancesTypes type) {
        return appliance(type) + " — " + UiUtil.fmt(type.getSuppliedHeat()) + " Вт";
    }
}
