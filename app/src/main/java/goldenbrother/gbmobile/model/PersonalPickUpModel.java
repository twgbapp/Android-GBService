package goldenbrother.gbmobile.model;

/**
 * Created by haojun on 2017/6/27.
 */

public class PersonalPickUpModel {
    private String userId;
    private String name;

    public PersonalPickUpModel(){

    }

    public PersonalPickUpModel(String userId, String name) {
        this.userId = userId;
        this.name = name;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
