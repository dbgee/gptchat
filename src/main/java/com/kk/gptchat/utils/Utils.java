package com.kk.gptchat.utils;

import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {
    public static String getUUID(){
        return UUID.randomUUID().toString().replace("-","").substring(0,12);
    }

    public static boolean checkUUID(String content){
        String target="[a-z0-9]{12}";
        Pattern pattern=Pattern.compile(target);
        Matcher matcher=pattern.matcher(content);
        if(matcher.find()){
            return true;
        }
        return false;
    }
}
