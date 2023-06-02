package com.kk.gptchat.entity;

import lombok.Data;

@Data
public class Msg {
    private String xml;
    private String ToUserName;
    private String FromUserName;
    private String CreateTime;
    private String MsgType;
    private String Content;
    private String MsgId;
}
