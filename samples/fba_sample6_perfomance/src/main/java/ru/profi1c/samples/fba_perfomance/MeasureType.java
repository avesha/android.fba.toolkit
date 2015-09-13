package ru.profi1c.samples.fba_perfomance;

import ru.profi1c.engine.app.ui.IDescription;

public enum MeasureType implements IDescription {
    Variant_A("Вариант А", 100, 2, 100, 50, 10, 100),
    Variant_B("Вариант B", 1000, 6, 100, 500, 10, 500),
    Variant_C("Вариант C", 10000, 6, 200, 500, 20, 1000),
    Variant_D("Вариант D", 10000, 12, 200, 1000, 100, 1000);

    private final String mDesc;
    private final int mCountOfNumenclatura;
    private final int mCountCodes;
    private final int mCountDocPrihod;
    private final int mCountPosOfDocPrihod;
    private final int mCountDocPrice;
    private final int mCountPosOfDocPrice;

    MeasureType(String desc, int countOfNumenclatura, int countCodes, int countDocPrihod,
            int countPosOfDocPrihod, int countDocPrice, int countPosOfDocPrice) {
        mDesc = desc;
        mCountOfNumenclatura = countOfNumenclatura;
        mCountCodes = countCodes;
        mCountDocPrihod = countDocPrihod;
        mCountPosOfDocPrihod = countPosOfDocPrihod;
        mCountDocPrice = countDocPrice;
        mCountPosOfDocPrice = countPosOfDocPrice;
    }

    @Override
    public String getDescription() {
        return mDesc;
    }

    public int getCountOfNumenclatura() {
        return mCountOfNumenclatura;
    }

    public int getCountCodes() {
        return mCountCodes;
    }

    public int getCountDocPrihod() {
        return mCountDocPrihod;
    }

    public int getCountPosOfDocPrihod() {
        return mCountPosOfDocPrihod;
    }

    public int getCountDocPrice() {
        return mCountDocPrice;
    }

    public int getCountPosOfDocPrice() {
        return mCountPosOfDocPrice;
    }
}
