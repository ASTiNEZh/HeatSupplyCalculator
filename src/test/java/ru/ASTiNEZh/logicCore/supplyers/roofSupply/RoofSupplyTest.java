package ru.ASTiNEZh.logicCore.supplyers.roofSupply;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("RoofSupply: теплоприток от кровли (10 Вт на м²)")
class RoofSupplyTest {

    @ParameterizedTest(name = "площадь {0} м² -> {1} Вт")
    @CsvSource({"0, 0", "1, 10", "20, 200", "12.5, 125", "100, 1000"})
    void shouldCalculateHeat(float area, float expected) {
        RoofSupply roof = new RoofSupply(area);

        assertEquals(expected, roof.getSuppliedHeat(), 0.001f);
        assertEquals(area, roof.getArea(), 0.001f);
    }

    @Test
    @Disabled("BUG-5: нет валидации — отрицательная площадь даёт отрицательный теплоприток")
    @DisplayName("Отрицательная площадь должна отвергаться")
    void shouldRejectNegativeArea() {
        assertThrows(IllegalArgumentException.class, () -> new RoofSupply(-5));
    }
}
