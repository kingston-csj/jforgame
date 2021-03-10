package jforgame.merge.config;


import jforgame.merge.utils.XmlUtils;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

@Root(name = "config")
public class MergeConfig {

    @Element(required = true)
    private boolean backup;
    @Element(required = true)
    private ClearConfig clear;
    @Element(required = true)
    private MergeServer parentServer;
    @ElementList(required = true)
    private List<MergeServer> childServers;

    private static MergeConfig self;

    public static MergeConfig getInstance() {
        if (self != null) {
            return self;
        }
        synchronized (MergeConfig.class) {
            if (self == null) {
                self = new MergeConfig();
                self.init();
            }
        }

        return self;
    }

    private void init() {
        self = XmlUtils.loadXmlConfig("merge.xml", MergeConfig.class);
    }

    public boolean isBackup() {
        return backup;
    }

    public ClearConfig getClear() {
        return clear;
    }

    public MergeServer getParentServer() {
        return parentServer;
    }

    public List<MergeServer> getChildServers() {
        return childServers;
    }
}
