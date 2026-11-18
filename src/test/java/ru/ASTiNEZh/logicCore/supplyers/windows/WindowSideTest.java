package ru.ASTiNEZh.logicCore.supplyers.windows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("WindowSide: справочник сторон света и тепловых нагрузок")
class WindowSideTest {

    @ParameterizedTest(name = "{0}: сторона {1}, {2} Вт/м²")
    @CsvSource({
            "NORTH_WINDOW,     NORTH,     100",
            "NORTHEAST_WINDOW, NORTHEAST, 180",
            "EAST_WINDOW,      EAST,      200",
            "SOUTHEAST_WINDOW, SOUTHEAST, 240",
            "SOUTH_WINDOW,     SOUTH,     230",
            "SOUTHWEST_WINDOW, SOUTHWEST, 310",
            "WEST_WINDOW,      WEST,      290",
            "NORTHWEST_WINDOW, NORTHWEST, 200"
    })
    @DisplayName("Каждая сторона привязана к своей стороне света и удельной нагрузке")
    void shouldMapSideAndHeat(WindowSide side, SideWorld world, float heat) {
        assertEquals(world, side.getSideWorld());
        assertEquals(heat, side.getLightsHeat(), 0.001f);
    }

    @ParameterizedTest
    @EnumSource(WindowSide.class)
    @DisplayName("У каждой стороны 4 смежных и 3 противолежащих направления")
    void shouldHaveFourAdjacentAndThreeOpposite(WindowSide side) {
        assertEquals(4, side.getAdjacentSids().size());
        assertEquals(3, side.getOppositeSids().size());
    }

    @ParameterizedTest
    @EnumSource(WindowSide.class)
    @DisplayName("Сама сторона + смежные + противолежащие покрывают все 8 направлений без повторов")
    void shouldCoverAllDirectionsExactlyOnce(WindowSide side) {
        Set<SideWorld> all = new HashSet<>();
        all.add(side.getSideWorld());
        all.addAll(side.getAdjacentSids());
        all.addAll(side.getOppositeSids());

        assertEquals(8, all.size(), "направления повторяются или пропущены");
        assertEquals(EnumSet.allOf(SideWorld.class), all);
    }

    @ParameterizedTest
    @EnumSource(WindowSide.class)
    @DisplayName("Сторона не входит в собственные списки смежных/противолежащих")
    void shouldNotContainItself(WindowSide side) {
        assertFalse(side.getAdjacentSids().contains(side.getSideWorld()));
        assertFalse(side.getOppositeSids().contains(side.getSideWorld()));
    }

    @Test
    @DisplayName("Отношение «смежная» и «противолежащая» симметрично")
    void shouldBeSymmetric() {
        for (WindowSide a : WindowSide.values()) {
            for (WindowSide b : WindowSide.values()) {
                if (a.getAdjacentSids().contains(b.getSideWorld())) {
                    assertTrue(b.getAdjacentSids().contains(a.getSideWorld()),
                            a + " смежна с " + b + ", но не наоборот");
                }
                if (a.getOppositeSids().contains(b.getSideWorld())) {
                    assertTrue(b.getOppositeSids().contains(a.getSideWorld()),
                            a + " противолежит " + b + ", но не наоборот");
                }
            }
        }
    }

    @Test
    @DisplayName("Самая «горячая» сторона — юго-запад")
    void southWestShouldBeHottest() {
        WindowSide hottest = WindowSide.NORTH_WINDOW;
        for (WindowSide s : WindowSide.values()) {
            if (s.getLightsHeat() > hottest.getLightsHeat()) {
                hottest = s;
            }
        }
        assertEquals(WindowSide.SOUTHWEST_WINDOW, hottest);
    }
}
