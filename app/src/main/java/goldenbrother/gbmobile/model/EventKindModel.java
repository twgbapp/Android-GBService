package goldenbrother.gbmobile.model;

public class EventKindModel {
    private String code;
    private String value;

    public EventKindModel(){

    }

    public EventKindModel(String code, String value) {
        this.code = code;
        this.value = value;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
