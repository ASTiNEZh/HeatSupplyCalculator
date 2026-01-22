package ru.ASTiNEZh.logicCore.supplyers.supplyVentilation;

import lombok.Getter;
import ru.ASTiNEZh.logicCore.supplyers.Supplier;

@Getter
public class SupplyVentilation extends Supplier {
    /** Расход воздуха */
    private final float airFlowRate;
    /** Температура воздуха в помещении */
    private final float internalTemp;

    public SupplyVentilation(float airFlowRate, float internalTemp) {
        this.airFlowRate = airFlowRate;
        this.internalTemp = internalTemp;

        suppliedHeat = 0.5F * airFlowRate * (35 - internalTemp);
    }

}
