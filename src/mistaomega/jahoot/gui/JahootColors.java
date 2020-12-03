package mistaomega.jahoot.gui;

/**
 * Enum reference for hex codes for the main UI
 *
 * @author Jack Nash
 * @version 1.0
 */
public enum JahootColors {
    JAHOOTPINK("#EC84F0"),
    JAHOOTBLUE("#6DC3F0"),
    JAHOOTORANGE("#F0AC54"),
    JAHOOTLIME("#99F060");

    String hex;

    JahootColors(String s) {
        this.hex = s;
    }

    /**
     * @return hex value attached to enum
     */
    public String getHex() {
        return hex;
    }
}
