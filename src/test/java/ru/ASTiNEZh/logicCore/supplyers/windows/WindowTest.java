package ru.ASTiNEZh.logicCore.supplyers.windows;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Window: теплопоступление через окно")
class WindowTest {

    private static final float DELTA = 0.01f;

    @ParameterizedTest(name = "{0}, {1}x{2}, УФ={3}, тень={4}, тень Вт/м²={5} -> {6} Вт")
    @CsvSource({
            // без тени, без плёнки: площадь * удельная нагрузка стороны
            "SOUTH_WINDOW,     2,   1.5, false, false, 0,  690",
            "NORTH_WINDOW,     1,   1,   false, false, 0,  100",
            "SOUTHWEST_WINDOW, 2,   2,   false, false, 0, 1240",
            // УФ-плёнка снижает приток вдвое
            "SOUTH_WINDOW,     2,   1.5, true,  false, 0,  345",
            // в тени используется shadowSupply вместо нагрузки стороны
            "SOUTH_WINDOW,     2,   1.5, false, true,  50, 150",
            // тень + плёнка: оба коэффициента
            "SOUTH_WINDOW,     2,   1.5, true,  true,  50,  75",
            // нулевая площадь
            "EAST_WINDOW,      0,   2,   false, false, 0,    0"
    })
    @DisplayName("Расчёт теплопоступления")
    void shouldCalculateSuppliedHeat(WindowSide side, float width, float height,
                                     boolean uv, boolean shadow, float shadowSupply,
                                     float expected) {
        Window window = new Window(side, width, height, uv, shadow, shadowSupply);

        assertEquals(expected, window.getSuppliedHeat(), DELTA);
    }

    @Test
    @DisplayName("Площадь = ширина * высота, поля сохраняются")
    void shouldStoreFields() {
        Window window = new Window(WindowSide.EAST_WINDOW, 1.5f, 2f, true, false, 0);

        assertEquals(3f, window.getArea(), DELTA);
        assertEquals(WindowSide.EAST_WINDOW, window.getWindowSide());
        assertTrue(window.isUvFilter());
        assertFalse(window.isInShadow());
    }

    @Test
    @DisplayName("Нагрузка в тени не зависит от стороны света")
    void shadowSupplyShouldIgnoreSide() {
        Window north = new Window(WindowSide.NORTH_WINDOW, 1, 1, false, true, 40);
        Window southwest = new Window(WindowSide.SOUTHWEST_WINDOW, 1, 1, false, true, 40);

        assertEquals(north.getSuppliedHeat(), southwest.getSuppliedHeat(), DELTA);
    }

    @Test
    @DisplayName("Без тени параметр shadowSupply игнорируется")
    void shouldIgnoreShadowSupplyWhenNotInShadow() {
        Window window = new Window(WindowSide.SOUTH_WINDOW, 1, 1, false, false, 9999);

        assertEquals(230f, window.getSuppliedHeat(), DELTA);
    }

    @Test
    @DisplayName("Сторона = null без тени -> NullPointerException")
    void shouldThrowOnNullSideWhenNotInShadow() {
        assertThrows(NullPointerException.class,
                () -> new Window(null, 1, 1, false, false, 0));
    }

    @Test
    @DisplayName("toString содержит сторону света, площадь, плёнку и тень")
    void toStringShouldDescribeWindow() {
        String text = new Window(WindowSide.NORTHEAST_WINDOW, 1, 2, true, false, 0).toString();

        assertTrue(text.contains("северо-восток"), text);
        assertTrue(text.contains("2.0"), text);
        assertTrue(text.contains("установлена"), text);
        assertTrue(text.contains("Затенено: нет"), text);
    }

    @Test
    @DisplayName("toString без плёнки и в тени")
    void toStringShouldDescribeNoFilterAndShadow() {
        String text = new Window(WindowSide.WEST_WINDOW, 1, 1, false, true, 10).toString();

        assertTrue(text.contains("запад"), text);
        assertTrue(text.contains("отсутствует"), text);
        assertTrue(text.contains("Затенено: да"), text);
    }

    @Test
    @Disabled("BUG-5: нет валидации — отрицательные размеры дают отрицательный теплоприток")
    @DisplayName("Отрицательные размеры должны отвергаться")
    void shouldRejectNegativeSize() {
        assertThrows(IllegalArgumentException.class,
                () -> new Window(WindowSide.SOUTH_WINDOW, -1, 2, false, false, 0));
    }
}
