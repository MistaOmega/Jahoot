package mistaomega.jahoot.gui;

public enum JahootColors {
    JAHOOTPINK ("#EC84F0"),
    JAHOOTBLUE ("#6DC3F0"),
    JAHOOTORANGE ("#F0AC54"),
    JAHOOTLIME ("#99F060");

    String hex;

    JahootColors(String s) {
        this.hex = s;
    }

    public String getHex() {
        return hex;
    }
}
