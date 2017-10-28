package goldenbrother.gbmobile.model;

public class DrawerItem {
    public static final int GROUP = 0;
    public static final int CHILD = 1;
    private int icon;
    private int name;
    private int type;

    public DrawerItem(int icon, int name, int type) {
        this.icon = icon;
        this.name = name;
        this.type = type;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public int getName() {
        return name;
    }

    public void setName(int name) {
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
