package ru.ASTiNEZh.supplyers.roofSupply;

import lombok.Getter;
import ru.ASTiNEZh.supplyers.Supplier;

@Getter
public class RoofSupply extends Supplier {
    private final float area;

    public RoofSupply(float area) {
        this.area = area;

        suppliedHeat = 10 * area;
    }

}
