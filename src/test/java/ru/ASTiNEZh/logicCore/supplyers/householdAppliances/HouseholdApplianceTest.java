package ru.ASTiNEZh.logicCore.supplyers.householdAppliances;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("HouseholdAppliance: тепловыделение техники")
class HouseholdApplianceTest {

    @ParameterizedTest(name = "{0} -> {1} Вт")
    @CsvSource({
            "PK,           300",
            "LAPTOP,       100",
            "REFRIGERATOR, 100",
            "TV,            50",
            "PRINTER,      100"
    })
    @DisplayName("Для типовой техники берётся значение из справочника")
    void shouldTakeHeatFromType(AppliancesTypes type, float expected) {
        HouseholdAppliance appliance = new HouseholdAppliance(type, 0);

        assertEquals(expected, appliance.getSuppliedHeat(), 0.001f);
        assertEquals(expected, type.getSuppliedHeat(), 0.001f);
    }

    @Test
    @DisplayName("Без типа используется пользовательская мощность")
    void shouldUseCustomHeatWhenTypeIsNull() {
        HouseholdAppliance appliance = new HouseholdAppliance(null, 75.5f);

        assertEquals(75.5f, appliance.getSuppliedHeat(), 0.001f);
    }

    @Test
    @DisplayName("При заданном типе пользовательская мощность игнорируется")
    void shouldIgnoreCustomHeatWhenTypeGiven() {
        HouseholdAppliance appliance = new HouseholdAppliance(AppliancesTypes.PK, 999);

        assertEquals(300f, appliance.getSuppliedHeat(), 0.001f);
    }

    @Test
    @Disabled("BUG-5: нет валидации — отрицательная мощность принимается")
    @DisplayName("Отрицательная пользовательская мощность должна отвергаться")
    void shouldRejectNegativeCustomHeat() {
        org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class,
                () -> new HouseholdAppliance(null, -10));
    }
}
