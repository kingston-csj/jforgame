package jforgame.merge.config;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name = "clear")
public class ClearConfig {

    @Element(required = true)
    private int minLevel;

    @Element(required = true)
    private int offlineDays;

    public int getMinLevel() {
        return minLevel;
    }

    public void setMinLevel(int minLevel) {
        this.minLevel = minLevel;
    }

    public int getOfflineDays() {
        return offlineDays;
    }

    public void setOfflineDays(int offlineDays) {
        this.offlineDays = offlineDays;
    }
}
