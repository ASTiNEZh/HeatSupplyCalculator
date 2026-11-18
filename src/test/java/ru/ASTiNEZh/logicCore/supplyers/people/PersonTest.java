package ru.ASTiNEZh.logicCore.supplyers.people;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Person: тепловыделение человека")
class PersonTest {

    @ParameterizedTest(name = "{0} при {1}°C -> {2} Вт")
    @CsvSource({
            "REST,     22, 105",
            "REST,     25,  93",
            "EASY1,    22, 148",
            "EASY1,    25, 145",
            "MEDIUM2A, 22, 196",
            "MEDIUM2A, 25, 194",
            "MEDIUM2B, 22, 244",
            "MEDIUM2B, 25, 242",
            "HARD,     22, 292",
            "HARD,     25, 291"
    })
    @DisplayName("Тепловыделение по виду деятельности и температуре")
    void shouldReturnHeatByActivityAndTemperature(TypeActivity activity, int degrees, float expected) {
        Person person = new Person(activity, degrees);

        assertEquals(expected, person.getSuppliedHeat(), 0.001f);
        assertEquals(activity, person.getTypeActivity());
    }

    @Test
    @DisplayName("При 25°C тепловыделение не больше, чем при 22°C (для любого вида деятельности)")
    void heatShouldNotGrowWithTemperature() {
        for (TypeActivity a : TypeActivity.values()) {
            assertTrue(a.get_25degrees() <= a.get_22degrees(), a.toString());
        }
    }

    @Test
    @DisplayName("Чем тяжелее работа, тем больше тепла (22°C)")
    void heatShouldGrowWithActivityLevel() {
        float prev = -1;
        for (TypeActivity a : TypeActivity.values()) {
            assertTrue(a.get_22degrees() > prev, a.toString());
            prev = a.get_22degrees();
        }
    }

    @Test
    @DisplayName("Вид деятельности = null -> NullPointerException")
    void shouldThrowOnNullActivity() {
        assertThrows(NullPointerException.class, () -> new Person(null, 22));
    }

    @Test
    @DisplayName("toString содержит вид деятельности и теплопоступление")
    void toStringShouldDescribePerson() {
        String text = new Person(TypeActivity.MEDIUM2A, 22).toString();

        assertTrue(text.contains("средняя IIа"), text);
        assertTrue(text.contains("196.0"), text);
    }

    @Test
    @Disabled("BUG-3: для температуры, отличной от 22/25, тепловыделение молча равно 0")
    @DisplayName("Неподдерживаемая температура должна отвергаться")
    void shouldRejectUnsupportedTemperature() {
        assertThrows(IllegalArgumentException.class, () -> new Person(TypeActivity.REST, 30));
    }
}
