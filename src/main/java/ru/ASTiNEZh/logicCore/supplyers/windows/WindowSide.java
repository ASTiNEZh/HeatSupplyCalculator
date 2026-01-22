package ru.ASTiNEZh.logicCore.supplyers.windows;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

import static ru.ASTiNEZh.logicCore.supplyers.windows.SideWorld.*;

/**
 * Касаемо 4х смежных и 3х противоположных сторон света.
 * Если мы рассмотрим розу ветров, то у нас всего 8 сторон света. 1на из них рассматриваемая, допустим юг, тогда у нас
 * остаётся 7. Все окна лежащие под углом до 90 градусов включительно будут считаться смежными (запад, юго-запад,
 * юго-восток, восток). Следовательно, из 7 у нас остаётся 3, чей угол относительно рассматриваемого окна более 90
 * градусов (северо-запад, север, северо-восток). Эта зависимость сторон света и присутствует в конструкторе
 */

@Getter
public enum  WindowSide {
    NORTH_WINDOW     (100,  NORTH    ,  WEST     ,NORTHWEST,NORTHEAST,EAST     ,  SOUTHEAST,SOUTH    ,SOUTHWEST),
    NORTHEAST_WINDOW (180,  NORTHEAST,  NORTHWEST,NORTH    ,EAST     ,SOUTHEAST,  SOUTH    ,SOUTHWEST,WEST     ),
    EAST_WINDOW      (200,  EAST     ,  NORTH    ,NORTHEAST,SOUTHEAST,SOUTH    ,  SOUTHWEST,WEST     ,NORTHWEST),
    SOUTHEAST_WINDOW (240,  SOUTHEAST,  NORTHEAST,EAST     ,SOUTH    ,SOUTHWEST,  WEST     ,NORTHWEST,NORTH    ),
    SOUTH_WINDOW     (230,  SOUTH    ,  EAST     ,SOUTHEAST,SOUTHWEST,WEST     ,  NORTHWEST,NORTH    ,NORTHEAST),
    SOUTHWEST_WINDOW (310,  SOUTHWEST,  SOUTHEAST,SOUTH    ,WEST     ,NORTHWEST,  NORTH    ,NORTHEAST,EAST     ),
    WEST_WINDOW      (290,  WEST     ,  SOUTH    ,SOUTHWEST,NORTHWEST,NORTH    ,  NORTHEAST,EAST     ,SOUTHEAST),
    NORTHWEST_WINDOW (200,  NORTHWEST,  SOUTHWEST,WEST     ,NORTH    ,NORTHEAST,  EAST     ,SOUTHEAST,SOUTH    );

    private final SideWorld sideWorld;
    private final List<SideWorld> adjacentSids = new ArrayList<>();
    private final List<SideWorld> oppositeSids = new ArrayList<>();
    private final float lightsHeat;

    WindowSide(
            float lightsHeat,
            SideWorld sideWorld,

            SideWorld adjacentSide1,
            SideWorld adjacentSide2,
            SideWorld adjacentSide3,
            SideWorld adjacentSide4,

            SideWorld oppositeSide1,
            SideWorld oppositeSide2,
            SideWorld oppositeSide3
    ) {
        this.lightsHeat = lightsHeat;
        this.sideWorld = sideWorld;

        adjacentSids.add(adjacentSide1);
        adjacentSids.add(adjacentSide2);
        adjacentSids.add(adjacentSide3);
        adjacentSids.add(adjacentSide4);

        oppositeSids.add(oppositeSide1);
        oppositeSids.add(oppositeSide2);
        oppositeSids.add(oppositeSide3);
    }

}
