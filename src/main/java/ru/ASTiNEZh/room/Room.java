package ru.ASTiNEZh.room;

import lombok.Getter;
import lombok.Setter;
import ru.ASTiNEZh.supplyers.Supplier;
import ru.ASTiNEZh.supplyers.householdAppliances.HouseholdAppliance;
import ru.ASTiNEZh.supplyers.people.Person;
import ru.ASTiNEZh.supplyers.roofSupply.RoofSupply;
import ru.ASTiNEZh.supplyers.supplyVentilation.SupplyVentilation;
import ru.ASTiNEZh.supplyers.windows.SideWorld;
import ru.ASTiNEZh.supplyers.windows.Window;
import ru.ASTiNEZh.supplyers.windows.WindowSide;

import java.util.*;


@Getter
public class Room {
    @Setter
    private String name;

    /** Модификаторы для расчёта */
    private final float ADJACENT_SIDE = 0.5F;
    private final float OPPOSITE_SIDE = 0.3F;

    /** Окна */
    private List<Window> windows = new ArrayList<>();
    private float hottestSupply;
    private WindowSide hottestSide;

    /** Люди */
    private List<Person> people = new ArrayList<>();

    /** Вентиляция */
    private final SupplyVentilation ventilationSupply;

    /** Кровля */
    private final RoofSupply roofSupply;

    /** Бытовая и производственная техника */
    private final List<HouseholdAppliance> householdAppliances;

    public Room(
            String name,
            List<Window> windows,
            List<Person> people,
            SupplyVentilation ventilationSupply,
            RoofSupply roofSupply,
            List<HouseholdAppliance> householdAppliances
    ) {
        this.name = name;
        this.windows = windows;
        this.people = people;
        this.ventilationSupply = Objects.nonNull(ventilationSupply)
                ? ventilationSupply
                : new SupplyVentilation(0, 0);
        this.roofSupply = Objects.nonNull(roofSupply)
                ? roofSupply
                : new RoofSupply(0);
        this.householdAppliances = householdAppliances;
    }

    public SupplyVentilation getVentilationSupply() {
        return Objects.nonNull(ventilationSupply)? ventilationSupply : new SupplyVentilation(0, 25);
    }

    public RoofSupply getRoofSupply() {
        return Objects.nonNull(roofSupply)? roofSupply : new RoofSupply(0);

    }

    public float calculateSupply() {
        float windowsSupply = calculateWindowsSupply();
        float peopleSupply = calculatePeopleSupply();
        float supplyVentilation = this.ventilationSupply.getSuppliedHeat();
        float roofSupply =  this.roofSupply.getSuppliedHeat();
        float applianceSupply = calculateHouseholdAppliances();

        return windowsSupply + peopleSupply + supplyVentilation + roofSupply + applianceSupply;
    }

    public float calculateWindowsSupply() {
        findHottestSide();
        float sumWindowsSupply = 0;

        if (!windows.isEmpty()) {
            for (Window window : windows) {
                SideWorld windowOrientation = window.getWindowSide().getSideWorld();

                if (hottestSide.getAdjacentSids().contains(windowOrientation)) {
                    sumWindowsSupply += window.getSuppliedHeat() * ADJACENT_SIDE;
                } else if (hottestSide.getOppositeSids().contains(windowOrientation)) {
                    sumWindowsSupply += window.getSuppliedHeat() * OPPOSITE_SIDE;
                } else if (Objects.equals(hottestSide.getSideWorld(), windowOrientation)) {
                    sumWindowsSupply += window.getSuppliedHeat();
                } else {
                    throw new RuntimeException("UNEXPECTED WINDOW ORIENTATION");
                }
            }
        }

        return sumWindowsSupply;
    }

    public void findHottestSide() {
        if (!windows.isEmpty()) {
            for (Window window : windows) {
                WindowSide windowSide = window.getWindowSide();

                if (Objects.isNull(hottestSide)) {
                    hottestSupply = windowSide.getLightsHeat();
                    hottestSide = windowSide;
                } else if (hottestSupply < windowSide.getLightsHeat()) {
                    hottestSupply = windowSide.getLightsHeat();
                    hottestSide = windowSide;
                }
            }
        }
    }

    public float calculatePeopleSupply() {
        float sumPeopleSupply = 0;

        if (!people.isEmpty()) {
            for (Supplier person : people) {
                sumPeopleSupply += person.getSuppliedHeat();
            }
        }

        return sumPeopleSupply;
    }

    public float calculateHouseholdAppliances() {
        float sumHouseholdAppliancesSupply = 0;

        if (!householdAppliances.isEmpty()) {
            for (Supplier householdAppliance : householdAppliances) {
                sumHouseholdAppliancesSupply += householdAppliance.getSuppliedHeat();
            }
        }

        return sumHouseholdAppliancesSupply;
    }

    @Override
    public String toString() {
        return new StringBuilder()
                .append(name).append(": ").append(calculateSupply()).append(" Вт\n")
                .append("    Окна: ").append(calculateWindowsSupply()).append(" Вт\n")
                .append("    Люди: ").append(calculatePeopleSupply()).append(" Вт\n")
                .append("    Приточная вентиляция: ").append(ventilationSupply.getSuppliedHeat()).append(" Вт\n")
                .append("    Кровля: ").append(roofSupply.getSuppliedHeat()).append(" Вт\n")
                .append("    Оборудование: ").append(calculateHouseholdAppliances()).append(" Вт\n\n")
                .append("    Расход воздуха: ").append(ventilationSupply.getAirFlowRate()).append(" м^3/ч\n\n")
                .toString();
    }
}
