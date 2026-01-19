package ru.ASTiNEZh.supplyers.householdAppliances;

import ru.ASTiNEZh.supplyers.Supplier;

import java.util.Objects;

public class HouseholdAppliance extends Supplier {
    public HouseholdAppliance(AppliancesTypes appliancesType, float suppliedHeat) {
        if (Objects.isNull(appliancesType)) {
            this.suppliedHeat = suppliedHeat;
        }
        else {
            this.suppliedHeat = appliancesType.getSuppliedHeat();
        }
    }
}
