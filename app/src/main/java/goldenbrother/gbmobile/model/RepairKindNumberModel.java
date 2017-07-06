package goldenbrother.gbmobile.model;

import java.util.ArrayList;

/**
 *
 */

public class RepairKindNumberModel {
    private int number;
    private int repairKindID;
    private String parentContent;
    private ArrayList<RepairKindNumberContentModel> list_number_content;

    public RepairKindNumberModel() {
        list_number_content = new ArrayList<>();
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }
    public int getRepairKindID() {
        return repairKindID;
    }

    public void setRepairKindID(int repairKindID) {
        this.repairKindID = repairKindID;
    }

    public String getParentContent() {
        return parentContent;
    }

    public void setParentContent(String parentContent) {
        this.parentContent = parentContent;
    }

    public ArrayList<RepairKindNumberContentModel> getContent() {
        return list_number_content;
    }
}