package com.kingston.jforgame.merge.config;


import com.kingston.jforgame.merge.config.utils.XmlUtils;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name = "config")
public class MergeConfig {

    @Element(required = true)
    private ClearConfig clear;


    public static void main(String[] args) {

        MergeConfig config = XmlUtils.loadXmlConfig("merge.xml", MergeConfig.class);
        System.out.println(config);

    }

}
