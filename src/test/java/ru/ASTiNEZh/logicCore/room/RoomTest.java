package ru.ASTiNEZh.logicCore.room;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import ru.ASTiNEZh.logicCore.supplyers.householdAppliances.AppliancesTypes;
import ru.ASTiNEZh.logicCore.supplyers.householdAppliances.HouseholdAppliance;
import ru.ASTiNEZh.logicCore.supplyers.people.Person;
import ru.ASTiNEZh.logicCore.supplyers.people.TypeActivity;
import ru.ASTiNEZh.logicCore.supplyers.roofSupply.RoofSupply;
import ru.ASTiNEZh.logicCore.supplyers.supplyVentilation.SupplyVentilation;
import ru.ASTiNEZh.logicCore.supplyers.windows.Window;
import ru.ASTiNEZh.logicCore.supplyers.windows.WindowSide;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Room: суммарный теплоприток помещения")
class RoomTest {

    private static final float DELTA = 0.01f;

    /** Окно 1x1 м без плёнки и тени: теплоприток = удельная нагрузка стороны. */
    private static Window window(WindowSide side) {
        return new Window(side, 1, 1, false, false, 0);
    }

    private static List<Window> windows(Window... ws) {
        return new ArrayList<>(List.of(ws));
    }

    private static List<Person> people(Person... ps) {
        return new ArrayList<>(List.of(ps));
    }

    private static List<HouseholdAppliance> appliances(HouseholdAppliance... as) {
        return new ArrayList<>(List.of(as));
    }

    private static Room room(List<Window> w, List<Person> p, SupplyVentilation v,
                             RoofSupply r, List<HouseholdAppliance> a) {
        return new Room(1, "Гостиная", w, p, v, r, a);
    }

    private static Room windowsOnly(Window... ws) {
        return room(windows(ws),
                people(new Person(TypeActivity.REST, 22)),
                new SupplyVentilation(0, 25),
                new RoofSupply(0),
                appliances(new HouseholdAppliance(AppliancesTypes.TV, 0)));
    }

    // ------------------------------------------------------------------ окна

    @Nested
    @DisplayName("Теплоприток через окна")
    class WindowsSupply {

        @Test
        @DisplayName("Одно окно: учитывается полностью")
        void singleWindowCountsFully() {
            Room room = windowsOnly(new Window(WindowSide.SOUTH_WINDOW, 2, 1.5f, false, false, 0));

            assertEquals(690f, room.calculateWindowsSupply(), DELTA);
        }

        @Test
        @DisplayName("Окна на самой горячей стороне суммируются со 100%")
        void sameSideWindowsSumFully() {
            Room room = windowsOnly(window(WindowSide.SOUTH_WINDOW), window(WindowSide.SOUTH_WINDOW));

            assertEquals(460f, room.calculateWindowsSupply(), DELTA);
        }

        @Test
        @DisplayName("Смежное окно учитывается с коэффициентом 0.5")
        void adjacentWindowUsesHalf() {
            // горячая сторона — юг (230); восток для юга смежный: 200 * 0.5
            Room room = windowsOnly(window(WindowSide.SOUTH_WINDOW), window(WindowSide.EAST_WINDOW));

            assertEquals(230f + 100f, room.calculateWindowsSupply(), DELTA);
        }

        @Test
        @DisplayName("Противолежащее окно учитывается с коэффициентом 0.3")
        void oppositeWindowUsesThirtyPercent() {
            // горячая сторона — юг (230); север для юга противолежащий: 100 * 0.3
            Room room = windowsOnly(window(WindowSide.SOUTH_WINDOW), window(WindowSide.NORTH_WINDOW));

            assertEquals(230f + 30f, room.calculateWindowsSupply(), DELTA);
        }

        @Test
        @DisplayName("Три окна: горячее (ЮЗ), смежное (Ю) и противолежащее (СВ)")
        void mixedOrientations() {
            Room room = windowsOnly(
                    window(WindowSide.SOUTHWEST_WINDOW),   // 310 * 1.0
                    window(WindowSide.SOUTH_WINDOW),       // 230 * 0.5
                    window(WindowSide.NORTHEAST_WINDOW));  // 180 * 0.3

            assertEquals(310f + 115f + 54f, room.calculateWindowsSupply(), DELTA);
        }

        @Test
        @DisplayName("Порядок окон в списке не влияет на результат")
        void orderShouldNotMatter() {
            Room a = windowsOnly(window(WindowSide.NORTHEAST_WINDOW),
                    window(WindowSide.SOUTH_WINDOW), window(WindowSide.SOUTHWEST_WINDOW));
            Room b = windowsOnly(window(WindowSide.SOUTHWEST_WINDOW),
                    window(WindowSide.NORTHEAST_WINDOW), window(WindowSide.SOUTH_WINDOW));

            assertEquals(a.calculateWindowsSupply(), b.calculateWindowsSupply(), DELTA);
        }

        @Test
        @DisplayName("Размеры, УФ-плёнка и тень окон учитываются в сумме")
        void windowPropertiesAreUsed() {
            Room room = windowsOnly(
                    new Window(WindowSide.SOUTH_WINDOW, 2, 1.5f, true, false, 0),  // 345
                    new Window(WindowSide.SOUTH_WINDOW, 2, 1.5f, false, true, 50)); // 150

            assertEquals(495f, room.calculateWindowsSupply(), DELTA);
        }

        @Test
        @DisplayName("Горячая сторона определяется по стороне света, а не по теплу конкретного окна")
        void hottestSideIsChosenBySideNotByWindowHeat() {
            // ЮЗ-окно маленькое и с плёнкой (155 Вт), южное — большое (690 Вт),
            // но «горячей» считается ЮЗ (310 > 230), поэтому юг идёт с коэффициентом 0.5
            Room room = windowsOnly(
                    new Window(WindowSide.SOUTHWEST_WINDOW, 1, 1, true, false, 0),
                    new Window(WindowSide.SOUTH_WINDOW, 3, 1, false, false, 0));

            assertEquals(155f + 345f, room.calculateWindowsSupply(), DELTA);
        }

        @Test
        @DisplayName("findHottestSide выбирает сторону с максимальной нагрузкой")
        void findHottestSidePicksMax() {
            Room room = windowsOnly(window(WindowSide.NORTH_WINDOW),
                    window(WindowSide.SOUTHWEST_WINDOW), window(WindowSide.EAST_WINDOW));

            room.findHottestSide();

            assertEquals(WindowSide.SOUTHWEST_WINDOW, room.getHottestSide());
            assertEquals(310f, room.getHottestSupply(), DELTA);
        }

        @Test
        @DisplayName("Пустой список окон -> RuntimeException")
        void emptyWindowsThrows() {
            Room room = windowsOnly();

            RuntimeException ex = assertThrows(RuntimeException.class, room::calculateWindowsSupply);
            assertEquals("пустой список окон", ex.getMessage());
        }

        @Test
        @Disabled("BUG-1: hottestSide кэшируется и не сбрасывается при изменении списка окон")
        @DisplayName("После изменения списка окон горячая сторона пересчитывается")
        void hottestSideShouldBeRecalculatedAfterWindowsChange() {
            List<Window> list = windows(window(WindowSide.SOUTHWEST_WINDOW));
            Room room = room(list, people(new Person(TypeActivity.REST, 22)),
                    new SupplyVentilation(0, 25), new RoofSupply(0),
                    appliances(new HouseholdAppliance(AppliancesTypes.TV, 0)));
            assertEquals(310f, room.calculateWindowsSupply(), DELTA);

            list.clear();
            list.add(window(WindowSide.NORTH_WINDOW));

            // единственное окно — северное, оно и есть самое горячее => 100 Вт
            assertEquals(100f, room.calculateWindowsSupply(), DELTA);
        }
    }

    // ------------------------------------------------------------------ люди

    @Nested
    @DisplayName("Теплоприток от людей")
    class PeopleSupply {

        @Test
        @DisplayName("Сумма по нескольким людям")
        void sumsPeople() {
            Room room = room(windows(window(WindowSide.NORTH_WINDOW)),
                    people(new Person(TypeActivity.REST, 22),
                            new Person(TypeActivity.REST, 22),
                            new Person(TypeActivity.EASY1, 25)),
                    new SupplyVentilation(0, 25), new RoofSupply(0),
                    appliances(new HouseholdAppliance(AppliancesTypes.TV, 0)));

            assertEquals(105f + 105f + 145f, room.calculatePeopleSupply(), DELTA);
        }

        @Test
        @DisplayName("Пустой список людей -> RuntimeException")
        void emptyPeopleThrows() {
            Room room = room(windows(window(WindowSide.NORTH_WINDOW)), people(),
                    new SupplyVentilation(0, 25), new RoofSupply(0),
                    appliances(new HouseholdAppliance(AppliancesTypes.TV, 0)));

            RuntimeException ex = assertThrows(RuntimeException.class, room::calculatePeopleSupply);
            assertEquals("пустой список людей", ex.getMessage());
        }
    }

    // ------------------------------------------------------------ оборудование

    @Nested
    @DisplayName("Теплоприток от оборудования")
    class AppliancesSupply {

        @Test
        @DisplayName("Сумма типовой и нестандартной техники")
        void sumsAppliances() {
            Room room = room(windows(window(WindowSide.NORTH_WINDOW)),
                    people(new Person(TypeActivity.REST, 22)),
                    new SupplyVentilation(0, 25), new RoofSupply(0),
                    appliances(new HouseholdAppliance(AppliancesTypes.PK, 0),
                            new HouseholdAppliance(AppliancesTypes.LAPTOP, 0),
                            new HouseholdAppliance(null, 75)));

            assertEquals(300f + 100f + 75f, room.calculateHouseholdAppliances(), DELTA);
        }

        @Test
        @DisplayName("Пустой список оборудования -> RuntimeException")
        void emptyAppliancesThrows() {
            Room room = room(windows(window(WindowSide.NORTH_WINDOW)),
                    people(new Person(TypeActivity.REST, 22)),
                    new SupplyVentilation(0, 25), new RoofSupply(0), appliances());

            RuntimeException ex = assertThrows(RuntimeException.class, room::calculateHouseholdAppliances);
            assertEquals("пустой список оборудования", ex.getMessage());
        }
    }

    // ------------------------------------------------------------ полный расчёт

    @Nested
    @DisplayName("Итоговый расчёт calculateSupply()")
    class TotalSupply {

        private Room fullRoom;

        @BeforeEach
        void setUp() {
            fullRoom = room(
                    windows(new Window(WindowSide.SOUTH_WINDOW, 2, 1.5f, false, false, 0)), // 690
                    people(new Person(TypeActivity.REST, 22), new Person(TypeActivity.REST, 22)), // 210
                    new SupplyVentilation(100, 25),                                          // 500
                    new RoofSupply(20),                                                      // 200
                    appliances(new HouseholdAppliance(AppliancesTypes.PK, 0),
                            new HouseholdAppliance(AppliancesTypes.LAPTOP, 0)));             // 400
        }

        @Test
        @DisplayName("Итог = окна + люди + вентиляция + кровля + техника")
        void totalIsSumOfAllParts() {
            assertEquals(2000f, fullRoom.calculateSupply(), DELTA);
        }

        @Test
        @DisplayName("Итог совпадает с суммой отдельных частей")
        void totalEqualsSumOfParts() {
            float parts = fullRoom.calculateWindowsSupply()
                    + fullRoom.calculatePeopleSupply()
                    + fullRoom.getVentilationSupply().getSuppliedHeat()
                    + fullRoom.getRoofSupply().getSuppliedHeat()
                    + fullRoom.calculateHouseholdAppliances();

            assertEquals(parts, fullRoom.calculateSupply(), DELTA);
        }

        @Test
        @DisplayName("Расчёт с разными ориентациями окон")
        void totalWithMixedWindows() {
            Room room = room(
                    windows(window(WindowSide.SOUTHWEST_WINDOW), window(WindowSide.SOUTH_WINDOW),
                            window(WindowSide.NORTHEAST_WINDOW)),                 // 479
                    people(new Person(TypeActivity.HARD, 25)),                    // 291
                    new SupplyVentilation(0, 25),                                 // 0
                    new RoofSupply(0),                                            // 0
                    appliances(new HouseholdAppliance(AppliancesTypes.TV, 0)));   // 50

            assertEquals(820f, room.calculateSupply(), DELTA);
        }

        @Test
        @DisplayName("Повторный вызов даёт тот же результат")
        void calculationShouldBeRepeatable() {
            assertEquals(fullRoom.calculateSupply(), fullRoom.calculateSupply(), DELTA);
        }

        @Test
        @DisplayName("Без окон расчёт невозможен -> RuntimeException")
        void noWindowsThrows() {
            Room room = room(windows(), people(new Person(TypeActivity.REST, 22)),
                    new SupplyVentilation(0, 25), new RoofSupply(0),
                    appliances(new HouseholdAppliance(AppliancesTypes.TV, 0)));

            assertThrows(RuntimeException.class, room::calculateSupply);
        }

        @Test
        @DisplayName("Без людей расчёт невозможен -> RuntimeException")
        void noPeopleThrows() {
            Room room = room(windows(window(WindowSide.NORTH_WINDOW)), people(),
                    new SupplyVentilation(0, 25), new RoofSupply(0),
                    appliances(new HouseholdAppliance(AppliancesTypes.TV, 0)));

            assertThrows(RuntimeException.class, room::calculateSupply);
        }

        @Test
        @DisplayName("Без оборудования расчёт невозможен -> RuntimeException")
        void noAppliancesThrows() {
            Room room = room(windows(window(WindowSide.NORTH_WINDOW)),
                    people(new Person(TypeActivity.REST, 22)),
                    new SupplyVentilation(0, 25), new RoofSupply(0), appliances());

            assertThrows(RuntimeException.class, room::calculateSupply);
        }

        @Test
        @DisplayName("Вентиляция = null -> NullPointerException (текущее поведение)")
        void nullVentilationThrowsNpe() {
            Room room = room(windows(window(WindowSide.NORTH_WINDOW)),
                    people(new Person(TypeActivity.REST, 22)), null, new RoofSupply(0),
                    appliances(new HouseholdAppliance(AppliancesTypes.TV, 0)));

            assertThrows(NullPointerException.class, room::calculateSupply);
        }

        @Test
        @Disabled("BUG-2: конструктор принимает null для вентиляции и кровли, а calculateSupply() падает с NPE")
        @DisplayName("Отсутствующие вентиляция и кровля должны считаться как 0 Вт")
        void nullVentilationAndRoofShouldCountAsZero() {
            Room room = room(windows(window(WindowSide.NORTH_WINDOW)),   // 100
                    people(new Person(TypeActivity.REST, 22)),           // 105
                    null, null,
                    appliances(new HouseholdAppliance(AppliancesTypes.TV, 0))); // 50

            assertEquals(255f, room.calculateSupply(), DELTA);
        }
    }

    // ------------------------------------------------------------ прочее

    @Test
    @DisplayName("Номер и название помещения изменяются через сеттеры")
    void settersAndGetters() {
        Room room = windowsOnly(window(WindowSide.NORTH_WINDOW));

        room.setNumber(12.5f);
        room.setName("Кабинет");

        assertEquals(12.5f, room.getNumber(), DELTA);
        assertEquals("Кабинет", room.getName());
    }

    @Test
    @DisplayName("Коэффициенты суммирования окон: смежное 0.5, противолежащее 0.3")
    void modifiersHaveExpectedValues() {
        Room room = windowsOnly(window(WindowSide.NORTH_WINDOW));

        assertEquals(0.5f, room.getADJACENT_SIDE(), 0.0001f);
        assertEquals(0.3f, room.getOPPOSITE_SIDE(), 0.0001f);
    }

    @Test
    @DisplayName("toString содержит название, все слагаемые и расход воздуха")
    void toStringContainsBreakdown() {
        Room room = room(
                windows(new Window(WindowSide.SOUTH_WINDOW, 2, 1.5f, false, false, 0)),
                people(new Person(TypeActivity.REST, 22), new Person(TypeActivity.REST, 22)),
                new SupplyVentilation(100, 25),
                new RoofSupply(20),
                appliances(new HouseholdAppliance(AppliancesTypes.PK, 0),
                        new HouseholdAppliance(AppliancesTypes.LAPTOP, 0)));

        String text = room.toString();

        assertTrue(text.contains("Гостиная: 2000.0 Вт"), text);
        assertTrue(text.contains("Окна: 690.0 Вт"), text);
        assertTrue(text.contains("Люди: 210.0 Вт"), text);
        assertTrue(text.contains("Приточная вентиляция: 500.0 Вт"), text);
        assertTrue(text.contains("Кровля: 200.0 Вт"), text);
        assertTrue(text.contains("Оборудование: 400.0 Вт"), text);
        assertTrue(text.contains("Расход воздуха: 100.0 м^3/ч"), text);
    }
}
