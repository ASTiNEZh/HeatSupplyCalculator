package ru.ASTiNEZh.UI.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.ASTiNEZh.UI.model.RoomSpec.ApplianceSpec;
import ru.ASTiNEZh.UI.model.RoomSpec.PersonSpec;
import ru.ASTiNEZh.UI.model.RoomSpec.RoofSpec;
import ru.ASTiNEZh.UI.model.RoomSpec.VentilationSpec;
import ru.ASTiNEZh.UI.model.RoomSpec.WindowSpec;
import ru.ASTiNEZh.logicCore.room.Room;
import ru.ASTiNEZh.logicCore.supplyers.householdAppliances.AppliancesTypes;
import ru.ASTiNEZh.logicCore.supplyers.people.TypeActivity;
import ru.ASTiNEZh.logicCore.supplyers.windows.WindowSide;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("RoomSpec: описание помещения, которое вводит пользователь")
class RoomSpecTest {

    private static final float DELTA = 0.01f;

    private RoomSpec spec;

    @BeforeEach
    void setUp() {
        spec = new RoomSpec();
        spec.setNumber(3);
        spec.setName("Спальня");
        spec.getWindows().add(new WindowSpec(WindowSide.SOUTH_WINDOW, 1, 1, false, false, 0, 2)); // 460
        spec.getPeople().add(new PersonSpec(TypeActivity.REST, 22, 1));                           // 105
        spec.getAppliances().add(new ApplianceSpec(AppliancesTypes.PK, 0, 1));                    // 300
    }

    @Test
    @DisplayName("WindowSpec: count задаёт число одинаковых окон")
    void windowSpecBuildsCountWindows() {
        WindowSpec w = new WindowSpec(WindowSide.SOUTH_WINDOW, 1, 1, false, false, 0, 3);

        assertEquals(3, w.build().size());
        assertEquals(690f, w.totalHeat(), DELTA);
    }

    @Test
    @DisplayName("WindowSpec: count = 0 -> пустой список и 0 Вт")
    void windowSpecWithZeroCount() {
        WindowSpec w = new WindowSpec(WindowSide.SOUTH_WINDOW, 1, 1, false, false, 0, 0);

        assertTrue(w.build().isEmpty());
        assertEquals(0f, w.totalHeat(), DELTA);
    }

    @Test
    @DisplayName("PersonSpec: считает людей и их тепловыделение")
    void personSpecTotalHeat() {
        PersonSpec p = new PersonSpec(TypeActivity.EASY1, 25, 4);

        assertEquals(4, p.build().size());
        assertEquals(4 * 145f, p.totalHeat(), DELTA);
    }

    @Test
    @DisplayName("ApplianceSpec: типовая техника и пользовательская мощность")
    void applianceSpecTotalHeat() {
        assertEquals(2 * 100f, new ApplianceSpec(AppliancesTypes.PRINTER, 0, 2).totalHeat(), DELTA);
        assertEquals(3 * 75f, new ApplianceSpec(null, 75, 3).totalHeat(), DELTA);
    }

    @Test
    @DisplayName("VentilationSpec и RoofSpec строят корректные объекты")
    void ventilationAndRoofSpecs() {
        assertEquals(500f, new VentilationSpec(100, 25).build().getSuppliedHeat(), DELTA);
        assertEquals(200f, new RoofSpec(20).build().getSuppliedHeat(), DELTA);
    }

    @Test
    @DisplayName("toRoom переносит номер, название и все списки")
    void toRoomCopiesData() {
        Room room = spec.toRoom();

        assertEquals(3f, room.getNumber(), DELTA);
        assertEquals("Спальня", room.getName());
        assertEquals(2, room.getWindows().size());
        assertEquals(1, room.getPeople().size());
        assertEquals(1, room.getHouseholdAppliances().size());
    }

    @Test
    @DisplayName("Без вентиляции и кровли подставляются нулевые объекты")
    void nullVentilationAndRoofBecomeZero() {
        Room room = spec.toRoom();

        assertNotNull(room.getVentilationSupply());
        assertNotNull(room.getRoofSupply());
        assertEquals(0f, room.getVentilationSupply().getSuppliedHeat(), DELTA);
        assertEquals(0f, room.getRoofSupply().getSuppliedHeat(), DELTA);
    }

    @Test
    @DisplayName("calculateSupply без вентиляции и кровли: окна + люди + техника")
    void calculateSupplyWithoutVentilationAndRoof() {
        assertEquals(460f + 105f + 300f, spec.calculateSupply(), DELTA);
    }

    @Test
    @DisplayName("calculateSupply с вентиляцией и кровлей")
    void calculateSupplyWithVentilationAndRoof() {
        spec.setVentilation(new VentilationSpec(100, 25)); // 500
        spec.setRoof(new RoofSpec(20));                    // 200

        assertEquals(865f + 500f + 200f, spec.calculateSupply(), DELTA);
    }

    @Test
    @DisplayName("Без окон исключение расчёта пробрасывается наружу")
    void noWindowsPropagatesException() {
        spec.getWindows().clear();

        RuntimeException ex = assertThrows(RuntimeException.class, spec::calculateSupply);
        assertEquals("пустой список окон", ex.getMessage());
    }

    @Test
    @DisplayName("Без людей исключение расчёта пробрасывается наружу")
    void noPeoplePropagatesException() {
        spec.getPeople().clear();

        assertThrows(RuntimeException.class, spec::calculateSupply);
    }

    @Test
    @DisplayName("copy: независимая копия, результат расчёта не копируется")
    void copyIsIndependent() {
        spec.setCalculatedSupply(865f);
        spec.setVentilation(new VentilationSpec(100, 25));

        RoomSpec copy = spec.copy();

        assertEquals(spec.getNumber(), copy.getNumber(), DELTA);
        assertEquals(spec.getName(), copy.getName());
        assertEquals(spec.getWindows(), copy.getWindows());
        assertEquals(spec.getPeople(), copy.getPeople());
        assertEquals(spec.getAppliances(), copy.getAppliances());
        assertEquals(spec.getVentilation(), copy.getVentilation());
        assertNull(copy.getCalculatedSupply());

        copy.getWindows().clear();
        copy.setName("Другое");

        assertEquals(1, spec.getWindows().size(), "правка копии не должна менять оригинал");
        assertEquals("Спальня", spec.getName());
        assertEquals(865f, spec.getCalculatedSupply(), DELTA);
    }

    @Test
    @DisplayName("Новая спецификация: пустое название и нет результата расчёта")
    void newSpecDefaults() {
        RoomSpec fresh = new RoomSpec();

        assertEquals("", fresh.getName());
        assertNull(fresh.getCalculatedSupply());
        assertNull(fresh.getVentilation());
        assertNull(fresh.getRoof());
        assertTrue(fresh.getWindows().isEmpty());
    }
}
