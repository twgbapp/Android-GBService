package goldenbrother.gbmobile.model;

import java.util.ArrayList;

/**
 * Created by asus on 2017/2/16.
 */

public class RepairKindModel {
    private int id;
    private int parentId;
    private String content;
    public RepairKindModel(){

    }

    public RepairKindModel(int id, int parentId, String content) {
        this.id = id;
        this.parentId = parentId;
        this.content = content;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getParentId() {
        return parentId;
    }

    public void setParentId(int parentId) {
        this.parentId = parentId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
