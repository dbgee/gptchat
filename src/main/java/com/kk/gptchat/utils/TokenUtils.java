package com.kk.gptchat.utils;

import cn.hutool.http.HttpRequest;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.kk.gptchat.service.EnvService;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

@Slf4j
@Component
public class TokenUtils {
    public static String token = "";
    public static String appid = "";
    public static String secret = "";

    @PostConstruct
    private void init(){
        token=new EnvService().getToken();
        appid=new EnvService().getAppid();
        secret=new EnvService().getSecret();
        log.info("appid----->{}",appid);
    }
    private static final HashMap<String,String> access_token=new HashMap<>();
    private static int timeCount=7200;
    private static int delay=5*60;


    public static HashMap<String, String> getAccessToken(){
        String url="https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid="+appid+"&secret="+secret;

        String temp_token,expires_in;
        if(checkTokenExpires(access_token)){
            access_token.clear();
            String content=HttpRequest.get(url).execute().body();
            log.warn("重新获取 token content:{}",content);

            JSONObject data= JSON.parseObject(content);
            temp_token=data.get("access_token").toString();
            expires_in=data.getString("expires_in");

            access_token.put(temp_token,expires_in);
        }else{
            log.warn("access_token:{}",access_token);
        }

        return access_token;
    }

    private static boolean checkTokenExpires(HashMap<String, String> access_token){
        if(access_token.size()>0){
            for(String key:access_token.keySet()){
                access_token.put(key, String.valueOf(timeCount-=delay));
            }
            for(String value:access_token.values()){
                int time=Integer.parseInt(value);
                if(time<600){
                    log.info("value 将要过期:{}",time);
                    return true;
                }
            }
        }else{
            return true;
        }

        return false;
    }

    public static boolean checkSignature(String signature, String timestamp, String nonce) {
        String[] arr = new String[] { token, timestamp, nonce };
        // 将token、timestamp、nonce三个参数进行字典序排序
        // Arrays.sort(arr);
        sort(arr);
        StringBuilder content = new StringBuilder();
        for (int i = 0; i < arr.length; i++) {
            content.append(arr[i]);
        }
        MessageDigest md = null;
        String tmpStr = null;

        try {
            md = MessageDigest.getInstance("SHA-1");
            // 将三个参数字符串拼接成一个字符串进行sha1加密
            byte[] digest = md.digest(content.toString().getBytes());
            tmpStr = byteToStr(digest);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        content = null;
        // 将sha1加密后的字符串可与signature对比，标识该请求来源于微信
        return tmpStr != null ? tmpStr.equals(signature.toUpperCase()) : false;
    }

    /**
     * 将字节数组转换为十六进制字符串
     *
     * @param byteArray
     * @return
     */
    private static String byteToStr(byte[] byteArray) {
        String strDigest = "";
        for (int i = 0; i < byteArray.length; i++) {
            strDigest += byteToHexStr(byteArray[i]);
        }
        return strDigest;
    }

    /**
     * 将字节转换为十六进制字符串
     *
     * @param mByte
     * @return
     */
    private static String byteToHexStr(byte mByte) {
        char[] Digit = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
        char[] tempArr = new char[2];
        tempArr[0] = Digit[(mByte >>> 4) & 0X0F];
        tempArr[1] = Digit[mByte & 0X0F];
        String s = new String(tempArr);
        return s;
    }

    /**
     * @param a
     */
    public static void sort(String a[]) {
        for (int i = 0; i < a.length - 1; i++) {
            for (int j = i + 1; j < a.length; j++) {
                if (a[j].compareTo(a[i]) < 0) {
                    String temp = a[i];
                    a[i] = a[j];
                    a[j] = temp;
                }
            }
        }
    }
}

