package ru.ASTiNEZh.logicCore.supplyers.householdAppliances;

import lombok.Getter;

@Getter
public enum AppliancesTypes {
    PK          (300),
    LAPTOP      (100),
    REFRIGERATOR(100),
    TV          (50 ),
    PRINTER     (100);

    /** Количество поступаемого тепла в ваттах */
    private final float suppliedHeat;

    AppliancesTypes(float suppliedHeat) {
        this.suppliedHeat = suppliedHeat;
    }
}
