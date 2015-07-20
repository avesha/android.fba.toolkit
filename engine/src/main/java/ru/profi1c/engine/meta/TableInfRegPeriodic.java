package ru.profi1c.engine.meta;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;

import java.util.Date;

import ru.profi1c.engine.Const;

/**
 * Базовый класс для периодических регистров сведений
 */
public abstract class TableInfRegPeriodic extends TableInfReg {

    private static final long serialVersionUID = 6826037776414063411L;

    /**
     * Имя поля 'Период'.
     */
    public static final String FIELD_NAME_PERIOD = "Period";

    /**
     * Содержит период, к которому относится запись регистра. Только для
     * периодических регистров.
     */
    @DatabaseField(columnName = TableInfRegPeriodic.FIELD_NAME_PERIOD,
            dataType = DataType.DATE_LONG, index = true)
    @MetadataField(type = MetadataFieldType.DATA, name = TableInfRegPeriodic.FIELD_NAME_PERIOD,
            description = Const.META_DESCRIPTION_PERIOD)
    protected Date period;

    /**
     * Содержит дату и время записи периодического регистра сведений.
     *
     * @return
     */
    public Date getPeriod() {
        return period;
    }

    /**
     * Установить дату и время записи периодического регистра сведений.
     *
     * @param period
     */
    public void setPeriod(Date period) {
        this.period = period;
    }

    @Override
    public String getMetaName() {
        return MetadataObject.TYPE_INFORMATION_REGISTER;
    }

    @Override
    public String createRecordKey() {
        if (period == null) {
            throw new IllegalStateException(
                    "Not set 'period' field of this Periodic Information Register!");
        }

        StringBuilder sb = new StringBuilder();
        sb.append(super.createRecordKey());
        sb.append(period.getTime());
        return sb.toString();
    }

}
