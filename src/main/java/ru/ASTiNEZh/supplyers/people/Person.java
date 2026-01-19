package ru.ASTiNEZh.supplyers.people;

import lombok.Getter;
import ru.ASTiNEZh.supplyers.Supplier;

@Getter
public class Person extends Supplier {
    private final TypeActivity typeActivity;

    public Person(TypeActivity typeActivity, int degrees) {
        this.typeActivity = typeActivity;
        switch (degrees) {
            case 22:
                suppliedHeat = typeActivity.get_22degrees();
                break;
            case 25:
                suppliedHeat = typeActivity.get_25degrees();
                break;
        }
    }

    @Override
    public String toString() {
        String typeActivityStr = switch (typeActivity) {
            case REST -> "покой";
            case EASY1 -> "лёгкая I";
            case MEDIUM2A -> "средняя IIа";
            case MEDIUM2B -> "средняя IIб";
            case HARD -> "тяжёлая";
            default -> throw new RuntimeException();
        };
        return "Вид деятельности: " + typeActivityStr + "; \n" +
                "Теплопоступление: " + suppliedHeat;
    }
}
