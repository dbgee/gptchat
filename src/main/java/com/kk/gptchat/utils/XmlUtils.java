package com.kk.gptchat.utils;

import com.kk.gptchat.entity.Msg;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.security.AnyTypePermission;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class XmlUtils {

    private static final XStream xStream;
    static {
        xStream=new XStream();
        xStream.addPermission(AnyTypePermission.ANY);
        xStream.setClassLoader(Msg.class.getClassLoader());
        xStream.alias("xml",Msg.class);
        xStream.ignoreUnknownElements();
    }

    public static String  objectToXml(Object data) {
        return xStream.toXML(data).replace("&lt;","<").replace("&gt;",">");
    }

    public static Object xmlToObject(String data) {
        return xStream.fromXML(data);
    }

    public static String needCdata(String data){
        return "<![CDATA[{}]]>".replace("{}",data);
    }

}
