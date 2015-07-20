package ru.profi1c.engine.meta;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;

import java.util.UUID;

import ru.profi1c.engine.Const;

/**
 * Базовый класс для регистров сведений
 */
public abstract class TableInfReg extends Table implements IMetadata {

    private static final long serialVersionUID = 3199842987213622731L;

    /**
     * Имя поля 'Регистратор'.
     */
    public static final String FIELD_NAME_RECORDER = "Recorder";

    /**
     * Содержит регистратор, который занес данную запись регистра сведений. Для
     * регистра, у которого в конфигураторе установлен режим записи
     * "Независимый", смысла не имеет.
     */
    @DatabaseField(columnName = TableInfReg.FIELD_NAME_RECORDER, dataType = DataType.STRING,
            index = true)
    @MetadataField(type = MetadataFieldType.GUID, name = TableInfReg.FIELD_NAME_RECORDER,
            description = Const.META_DESCRIPTION_RECORDER)
    protected String recorder;

    @Override
    public String getMetaType() {
        return MetadataObject.TYPE_INFORMATION_REGISTER;
    }

    /**
     * Возвращает регистратор, который занес данную запись регистра сведений.
     * Для независимых регистров = null
     *
     * @return
     */
    public UUID getRecorder() {
        if (recorder != null) {
            return UUID.fromString(recorder);
        }
        return null;
    }

    /**
     * Установить регистратор для данной записи, для независимых регистров
     * смысла не имеет.
     *
     * @param recorder
     */
    @Deprecated
    public void setRecorder(Document recorder) {
        this.recorder = recorder.getRef().toString();
    }

    @Override
    public String createRecordKey() {
        return recorder != null ? recorder : null;
    }

}
