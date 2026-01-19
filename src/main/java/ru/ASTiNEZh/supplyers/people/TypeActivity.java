package ru.ASTiNEZh.supplyers.people;

import lombok.Getter;

@Getter
public enum TypeActivity {
    /** Покой (сидя с незначительными движениями) */
    REST    (105, 93),
    /** Лёгкая I (работа стоя с незначительными движениями) */
    EASY1   (148, 145),
    /** Средняя IIа (работа сидя или стоя с переносом предметов до 1кг, с постоянным движением) */
    MEDIUM2A(196, 194),
    /** Средняя IIб (работа стоя с переносом предметов до 10 кг, с постоянным движением и напряжением) */
    MEDIUM2B(244, 242),
    /** Тяжёлая (перенос груза свыше 10 кг, постоянное напряжение) */
    HARD    (292, 291);

    private final float _22degrees;
    private final float _25degrees;


    TypeActivity(float _22degrees, float _25degrees) {
        this._22degrees = _22degrees;
        this._25degrees = _25degrees;
    }

}
