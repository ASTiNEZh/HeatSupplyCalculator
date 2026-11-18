package ru.ASTiNEZh.logicCore.supplyers.supplyVentilation;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("SupplyVentilation: теплоприток от приточного воздуха Q = 0.5 * L * (35 - t)")
class SupplyVentilationTest {

    @ParameterizedTest(name = "расход {0} м³/ч, t={1}°C -> {2} Вт")
    @CsvSource({
            "100, 25,  500",
            "200, 22, 1300",
            "0,   25,    0",
            "100, 35,    0",
            "60,  20,  450",
            // внутренняя температура выше температуры притока даёт отрицательное значение
            "100, 40, -250"
    })
    void shouldCalculateHeat(float airFlow, float internalTemp, float expected) {
        SupplyVentilation vent = new SupplyVentilation(airFlow, internalTemp);

        assertEquals(expected, vent.getSuppliedHeat(), 0.001f);
    }

    @Test
    @DisplayName("Входные параметры доступны через геттеры")
    void shouldStoreParameters() {
        SupplyVentilation vent = new SupplyVentilation(150, 24);

        assertEquals(150f, vent.getAirFlowRate(), 0.001f);
        assertEquals(24f, vent.getInternalTemp(), 0.001f);
    }
}
