package com.kingston.jforgame.merge.config;


import com.kingston.jforgame.merge.config.utils.XmlUtils;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

@Root(name = "config")
public class MergeConfig {

    @Element(required = true)
    private ClearConfig clear;
    @Element(required = true)
    private MergeServer parentServer;
    @ElementList(required = true)
    private List<MergeServer> childServers;

    public static void main(String[] args) {

        MergeConfig config = XmlUtils.loadXmlConfig("merge.xml", MergeConfig.class);
        System.out.println(config);

    }

}
