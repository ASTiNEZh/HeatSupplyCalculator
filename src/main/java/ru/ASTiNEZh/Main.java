package ru.ASTiNEZh;

import ru.ASTiNEZh.logicCore.room.Room;
import ru.ASTiNEZh.logicCore.supplyers.people.Person;
import ru.ASTiNEZh.logicCore.supplyers.people.TypeActivity;
import ru.ASTiNEZh.logicCore.supplyers.supplyVentilation.SupplyVentilation;
import ru.ASTiNEZh.logicCore.supplyers.windows.Window;
import ru.ASTiNEZh.logicCore.supplyers.windows.WindowSide;

import java.util.ArrayList;
import java.util.List;

public class Main {
    static void main() {
        // -1 этаж
        List<Window> windows001 = new ArrayList<>();
        windows001.add(new Window(WindowSide.SOUTH_WINDOW, 1.4F, 0.8F, false, false, 0));
        windows001.add(new Window(WindowSide.SOUTHEAST_WINDOW, 2.8F, 0.8F, false, false, 0));
        windows001.add(new Window(WindowSide.SOUTHEAST_WINDOW, 2.8F, 0.8F, false, false, 0));

        List<Person> people001 = new ArrayList<>();
        people001.add(new Person(TypeActivity.MEDIUM2A, 22));

        SupplyVentilation supplyVentilation001 = new SupplyVentilation(400, 22);

        Room room001 = new Room("001 Цоколь", windows001, people001, supplyVentilation001, null, new ArrayList<>());

        System.out.println(room001.toString());

        // 1 этаж

        // 101
        List<Window> windows101 = new ArrayList<>();
        windows101.add(new Window(WindowSide.SOUTH_WINDOW, 1F, 2.8F, false, false, 0));

        List<Person> people101 = new ArrayList<>();
        people101.add(new Person(TypeActivity.MEDIUM2A, 22));

        SupplyVentilation supplyVentilation101 = new SupplyVentilation(20, 22);

        Room room101 = new Room("101 Прихожая", windows101, people101, supplyVentilation101, null, new ArrayList<>());

        System.out.println(room101.toString());

        // 102
        List<Person> people102 = new ArrayList<>();
        people102.add(new Person(TypeActivity.MEDIUM2A, 22));

        SupplyVentilation supplyVentilation102 = new SupplyVentilation(10, 22);

        Room room102 = new Room("102 Гардероб", new ArrayList<>(), people102, supplyVentilation102, null, new ArrayList<>());

        System.out.println(room102.toString());

        // 103
        List<Window> windows103 = new ArrayList<>();
        windows101.add(new Window(WindowSide.WEST_WINDOW, 0.9F, 1.9F, false, false, 0));

        List<Person> people103 = new ArrayList<>();
        people103.add(new Person(TypeActivity.REST, 25));

        SupplyVentilation supplyVentilation103 = new SupplyVentilation(11, 25);

        Room room103 = new Room("103 C/y", windows103, people103, supplyVentilation103, null, new ArrayList<>());

        System.out.println(room103.toString());

        // 104
        List<Window> windows104 = new ArrayList<>();
        windows104.add(new Window(WindowSide.EAST_WINDOW, 0.9F, 1.9F, false, false, 0));

        SupplyVentilation supplyVentilation104 = new SupplyVentilation(34, 22);

        Room room104 = new Room("104 Холл", windows104, new ArrayList<>(), supplyVentilation104, null, new ArrayList<>());

        System.out.println(room104.toString());

        // 105
        List<Window> windows105 = new ArrayList<>();
        windows105.add(new Window(WindowSide.NORTH_WINDOW, 1.6F, 2.8F, false, false, 0));
        windows105.add(new Window(WindowSide.EAST_WINDOW, 2.8F, 2.8F, false, false, 0));
        windows105.add(new Window(WindowSide.EAST_WINDOW, 2.8F, 2.8F, false, false, 0));

        List<Person> people105 = new ArrayList<>();
        for (int i = 0; i < 10; i++)
            people105.add(new Person(TypeActivity.REST, 22));

        SupplyVentilation supplyVentilation105 = new SupplyVentilation(170, 22);

        Room room105 = new Room("105 Гостиная", windows105, people105, supplyVentilation105, null, new ArrayList<>());

        System.out.println(room105.toString());

        // 106
        List<Window> windows106 = new ArrayList<>();
        windows106.add(new Window(WindowSide.EAST_WINDOW, 1.4F, 1.8F, false,false, 0));

        List<Person> people106 = new ArrayList<>();
        people106.add(new Person(TypeActivity.REST, 22));
        people106.add(new Person(TypeActivity.REST, 22));

        SupplyVentilation supplyVentilation106 = new SupplyVentilation(40, 22);

        Room room106 = new Room("106 Спальня", windows106, people106, supplyVentilation106, null, new ArrayList<>());

        System.out.println(room106.toString());

        // 107
        List<Window> windows107 = new ArrayList<>();
        windows107.add(new Window(WindowSide.SOUTH_WINDOW, 1.4F, 2.8F, false, false, 0));
        windows107.add(new Window(WindowSide.EAST_WINDOW, 2.8F, 2.8F, false, false, 0));
        windows107.add(new Window(WindowSide.NORTH_WINDOW, 1, 2.8F, false, false, 0));

        List<Person> people107 = new ArrayList<>();
        people107.add(new Person(TypeActivity.MEDIUM2A, 22));

        SupplyVentilation supplyVentilation107 = new SupplyVentilation(43, 22);

        Room room107 = new Room("107 Кухня", windows107, people107, supplyVentilation107, null, new ArrayList<>());

        System.out.println(room107.toString());

        // 2й этаж

        // 201
        List<Window> windows201 = new ArrayList<>();
        windows201.add(new Window(WindowSide.WEST_WINDOW, 1.4F, 2.8F, false, false, 0));

        List<Person> people201 = new ArrayList<>();

        SupplyVentilation supplyVentilation201 = new SupplyVentilation(49, 22);

        Room room201 = new Room("201 Холл", windows201, people201, supplyVentilation201, null, new ArrayList<>());

        System.out.println(room201.toString());

        // 202
        List<Window> windows202 = new ArrayList<>();
        windows202.add(new Window(WindowSide.SOUTH_WINDOW, 1, 2, false, false, 0));

        List<Person> people202 = new ArrayList<>();
        people202.add(new Person(TypeActivity.REST, 22));

        SupplyVentilation supplyVentilation202 = new SupplyVentilation(22, 22);

        Room room202 = new Room("202 C/y", windows202, people202,supplyVentilation202, null, new ArrayList<>());

        System.out.println(room202.toString());

        // 203
        List<Window> windows203 = new ArrayList<>();
        windows203.add(new Window(WindowSide.WEST_WINDOW, 0.9F, 2, false, false, 0));

        List<Person> people203 = new ArrayList<>();
        people203.add(new Person(TypeActivity.REST, 22));

        SupplyVentilation supplyVentilation203 = new SupplyVentilation(33, 22);

        Room room203 = new Room("203", windows203, people203, supplyVentilation203, null, new ArrayList<>());

        System.out.println(room203.toString());

        // 204
        List<Window> windows204 = new ArrayList<>();
        windows204.add(new Window(WindowSide.SOUTH_WINDOW, 1, 2, false, false, 0));
        windows204.add(new Window(WindowSide.EAST_WINDOW, 2.8F, 2.8F, false, false, 0));

        List<Person> people204 = new ArrayList<>();
        people204.add(new Person(TypeActivity.REST, 22));
        people204.add(new Person(TypeActivity.REST, 22));

        SupplyVentilation supplyVentilation204 = new SupplyVentilation(83, 22);

        Room room204 = new Room("204 Спальня", windows204, people204, supplyVentilation204, null, new ArrayList<>());

        System.out.println(room204.toString());

        // 205
        List<Window> windows205 = new ArrayList<>();
        windows205.add(new Window(WindowSide.EAST_WINDOW, 2.8F, 2.8F, false, false, 0));
        windows205.add(new Window(WindowSide.EAST_WINDOW, 1.2F, 2.35F, false, false, 0));

        List<Person> people205 = new ArrayList<>();
        people205.add(new Person(TypeActivity.REST, 22));
        people205.add(new Person(TypeActivity.REST, 22));

        SupplyVentilation supplyVentilation205 = new SupplyVentilation(87, 22);

        Room room205 = new Room("205 Спальня", windows205, people205, supplyVentilation205, null, new ArrayList<>());

        System.out.println(room205.toString());

        // 206
        List<Window> windows206 = new ArrayList<>();
        windows206.add(new Window(WindowSide.EAST_WINDOW, 1.2F, 2.35F, false, false, 0));

        List<Person> people206 = new ArrayList<>();
        people206.add(new Person(TypeActivity.REST, 22));

        SupplyVentilation supplyVentilation206 = new SupplyVentilation(19, 22);

        Room room206 = new Room("206 Гардероб", windows206, people206, supplyVentilation206, null, new ArrayList<>());

        System.out.println(room206.toString());

        // 207
        List<Window> windows207 = new ArrayList<>();

        List<Person> people207 = new ArrayList<>();
        people207.add(new Person(TypeActivity.REST, 22));

        SupplyVentilation supplyVentilation207 = new SupplyVentilation(19, 22);

        Room room207 = new Room("207 Гардероб", windows207, people207, supplyVentilation207, null, new ArrayList<>());

        System.out.println(room207.toString());

        float total = room001.calculateSupply() +
                room101.calculateSupply() +
                room102.calculateSupply() +
                room103.calculateSupply() +
                room104.calculateSupply() +
                room105.calculateSupply() +
                room106.calculateSupply() +
                room107.calculateSupply() +
                room201.calculateSupply() +
                room202.calculateSupply() +
                room203.calculateSupply() +
                room204.calculateSupply() +
                room205.calculateSupply() +
                room206.calculateSupply() +
                room207.calculateSupply();

        System.out.println("\nИтого: " + total);
    }
}
