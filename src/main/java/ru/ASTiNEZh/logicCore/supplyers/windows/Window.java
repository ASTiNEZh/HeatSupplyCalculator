package ru.ASTiNEZh.logicCore.supplyers.windows;

import lombok.Getter;
import ru.ASTiNEZh.logicCore.supplyers.Supplier;

@Getter
public class Window extends Supplier {
    private final WindowSide windowSide;
    private final float area;
    private final boolean uvFilter;
    private final boolean inShadow;

    public Window(WindowSide windowSide, float width, float height, boolean uvFilter, boolean inShadow, float shadowSupply) {
        this.windowSide = windowSide;
        this.area = width * height;
        this.uvFilter = uvFilter;
        this.inShadow = inShadow;

        if (!inShadow) {
            this.suppliedHeat = windowSide.getLightsHeat() * area;
        }
        else {
            this.suppliedHeat = shadowSupply * area;
        }

        if (uvFilter) {
             this.suppliedHeat *= 0.5f;
        }
    }

    @Override
    public String toString() {
        String ufFilterStr = uvFilter? "установлена" : "отсутствует";
        String inShadowStr = inShadow? "да" : "нет";
        String windowSideStr = switch (windowSide) {
            case NORTH_WINDOW -> "север";
            case NORTHEAST_WINDOW -> "северо-восток";
            case EAST_WINDOW -> "восток";
            case SOUTHEAST_WINDOW -> "юго-восток";
            case SOUTH_WINDOW -> "юг";
            case SOUTHWEST_WINDOW -> "юго-запад";
            case WEST_WINDOW -> "запад";
            case NORTHWEST_WINDOW -> "северо-запад";
            default -> throw new RuntimeException();
        };
        return "Сторона света: " + windowSideStr + "; \n" +
                "Площадь: " + area + " м^2; \n" +
                "Анти УФ плёнка: " + ufFilterStr + "; \n" +
                "Затенено: " + inShadowStr + "; \n" +
                "Теплопоступление: " + suppliedHeat;
    }
}
