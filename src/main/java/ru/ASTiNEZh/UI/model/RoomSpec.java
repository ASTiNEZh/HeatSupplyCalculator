package ru.ASTiNEZh.UI.model;

import ru.ASTiNEZh.logicCore.room.Room;
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

/**
 * Редактируемое «описание» помещения — данные, которые вводит пользователь.
 *
 * Классы Window, Person, SupplyVentilation и т.д. неизменяемы и не хранят параметры
 * конструктора (ширину/высоту окна, температуру для Person, тип техники), поэтому
 * редактировать по ним напрямую нельзя. Интерфейс работает с этими описаниями,
 * а настоящий Room строится методом toRoom().
 */
public class RoomSpec {

    /** Окно; count — сколько одинаковых окон. */
    public record WindowSpec(WindowSide side, float width, float height,
                             boolean uvFilter, boolean inShadow, float shadowSupply, int count) {

        public List<Window> build() {
            List<Window> result = new ArrayList<>();
            for (int i = 0; i < count; i++) {
                result.add(new Window(side, width, height, uvFilter, inShadow, shadowSupply));
            }
            return result;
        }

        public float totalHeat() {
            float sum = 0;
            for (Window w : build()) {
                sum += w.getSuppliedHeat();
            }
            return sum;
        }
    }

    /** Люди одного вида деятельности; degrees — 22 или 25. */
    public record PersonSpec(TypeActivity type, int degrees, int count) {

        public List<Person> build() {
            List<Person> result = new ArrayList<>();
            for (int i = 0; i < count; i++) {
                result.add(new Person(type, degrees));
            }
            return result;
        }

        public float totalHeat() {
            float sum = 0;
            for (Person p : build()) {
                sum += p.getSuppliedHeat();
            }
            return sum;
        }
    }

    /** Оборудование; если type == null, используется customHeat (Вт). */
    public record ApplianceSpec(AppliancesTypes type, float customHeat, int count) {

        public List<HouseholdAppliance> build() {
            List<HouseholdAppliance> result = new ArrayList<>();
            for (int i = 0; i < count; i++) {
                result.add(new HouseholdAppliance(type, customHeat));
            }
            return result;
        }

        public float totalHeat() {
            float sum = 0;
            for (HouseholdAppliance a : build()) {
                sum += a.getSuppliedHeat();
            }
            return sum;
        }
    }

    public record VentilationSpec(float airFlowRate, float internalTemp) {
        public SupplyVentilation build() {
            return new SupplyVentilation(airFlowRate, internalTemp);
        }
    }

    public record RoofSpec(float area) {
        public RoofSupply build() {
            return new RoofSupply(area);
        }
    }

    private float number;
    private String name = "";
    private final List<WindowSpec> windows = new ArrayList<>();
    private final List<PersonSpec> people = new ArrayList<>();
    private final List<ApplianceSpec> appliances = new ArrayList<>();
    private VentilationSpec ventilation; // null — приточной вентиляции нет
    private RoofSpec roof;                // null — теплопритока от кровли нет
    private Float calculatedSupply;       // результат последнего расчёта, Вт (null — не считали)

    public float getNumber() {
        return number;
    }

    public void setNumber(float number) {
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<WindowSpec> getWindows() {
        return windows;
    }

    public List<PersonSpec> getPeople() {
        return people;
    }

    public List<ApplianceSpec> getAppliances() {
        return appliances;
    }

    public VentilationSpec getVentilation() {
        return ventilation;
    }

    public void setVentilation(VentilationSpec ventilation) {
        this.ventilation = ventilation;
    }

    public RoofSpec getRoof() {
        return roof;
    }

    public void setRoof(RoofSpec roof) {
        this.roof = roof;
    }

    public Float getCalculatedSupply() {
        return calculatedSupply;
    }

    public void setCalculatedSupply(Float calculatedSupply) {
        this.calculatedSupply = calculatedSupply;
    }

    /** Копия для редактирования: отмена диалога не затрагивает оригинал. */
    public RoomSpec copy() {
        RoomSpec c = new RoomSpec();
        c.number = number;
        c.name = name;
        c.windows.addAll(windows);
        c.people.addAll(people);
        c.appliances.addAll(appliances);
        c.ventilation = ventilation;
        c.roof = roof;
        // calculatedSupply намеренно не копируем: после правки старый результат недействителен
        return c;
    }

    /**
     * Суммарный теплоприток помещения, Вт (Room.calculateSupply()).
     * Исключения расчёта (например, «пустой список окон») пробрасываются наружу.
     */
    public float calculateSupply() {
        return toRoom().calculateSupply();
    }

    /** Строит настоящий Room для расчёта. */
    public Room toRoom() {
        List<Window> w = new ArrayList<>();
        for (WindowSpec s : windows) {
            w.addAll(s.build());
        }
        List<Person> p = new ArrayList<>();
        for (PersonSpec s : people) {
            p.addAll(s.build());
        }
        List<HouseholdAppliance> a = new ArrayList<>();
        for (ApplianceSpec s : appliances) {
            a.addAll(s.build());
        }
        return new Room(
                number,
                name,
                w,
                p,
                // Room.calculateSupply() не переносит null, поэтому подставляем «нулевые» объекты
                ventilation == null ? new SupplyVentilation(0, 22) : ventilation.build(),
                roof == null ? new RoofSupply(0) : roof.build(),
                a
        );
    }
}
