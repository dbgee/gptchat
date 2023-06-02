package com.kk.gptchat.utils;

import cn.hutool.core.io.IORuntimeException;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.kk.gptchat.service.EnvService;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;
import java.net.Proxy;

@Slf4j
@Component
public class GPTUtils {
    static Proxy proxy=new Proxy(Proxy.Type.HTTP,new InetSocketAddress("127.0.0.1",7890));

    private static String apiKey="";
    @PostConstruct
    private void init(){
        apiKey=new EnvService().getKey();
    }
    private static final String host="https://api.openai.com";

    public static String createCompletions(String prompt){

        String url="/v1/completions";
        JSONObject postData=new JSONObject();
        postData.put("model","text-davinci-003");
        postData.put("prompt",prompt);
        postData.put("max_tokens",2048);
        postData.put("temperature",0);
        postData.put("n",1);

        url=host+url;
        HttpResponse httpResponse =HttpRequest.post(url)
                .setProxy(proxy)
                .header("Authorization","Bearer "+apiKey)
                .header("Content-Type","application/json")
                .body(postData.toString())
                .execute();

        return httpResponse.body();
    }

    public static String createChat(String prompt){
        String url="/v1/chat/completions";
        JSONObject postData=new JSONObject();
        postData.put("model","gpt-3.5-turbo");
        JSONArray msg=new JSONArray();

        JSONObject roleContent=new JSONObject();
        roleContent.put("role","user");
        roleContent.put("content",prompt);
        msg.add(roleContent);

        postData.put("messages",msg);
        log.info("postData:{}",postData);

        url=host+url;
        HttpResponse httpResponse = null;
        try {
            httpResponse =HttpRequest.post(url)
                    .header("Authorization","Bearer "+apiKey)
                    .setProxy(proxy)
                    .header("Content-Type","application/json")
                    .body(postData.toString())
                    .execute();
        }catch (IORuntimeException exception){
            log.info("gpt server connect error:{}",exception.toString());
            return "";
        }

        log.info("body:{}",httpResponse.body());

        return httpResponse.body();
    }

    public static String createImages(String prompt){

        String url="/v1/images/generations";
        JSONObject postData=new JSONObject();
        postData.put("prompt",prompt);
        postData.put("n",3);
        postData.put("size", "512x512");

        url=host+url;
        HttpResponse httpResponse =HttpRequest.post(url)
                .header("Authorization","Bearer "+apiKey)
                .setProxy(proxy)
                .header("Content-Type","application/json")
                .body(postData.toString())
                .execute();

        return httpResponse.body();
    }

    public static String test(String prompt) {
        JSONObject result=new JSONObject();
        JSONArray jsonArray=new JSONArray();
        JSONObject url1=new JSONObject();
        JSONObject url2=new JSONObject();
        JSONObject url3=new JSONObject();
        url1.put("url","https://oaidalleapiprodscus.blob.core.windows.net/private/org-fLdgj7yMu2OglTPJWONPORzM/user-miJSk0HNu2ycpTFVGuUOlNUh/img-377daAOZJvwQjp1IUegRdEQa.png?st=2023-03-23T08%3A12%3A54Z&se=2023-03-23T10%3A12%3A54Z&sp=r&sv=2021-08-06&sr=b&rscd=inline&rsct=image/png&skoid=6aaadede-4fb3-4698-a8f6-684d7786b067&sktid=a48cca56-e6da-484e-a814-9c849652bcb3&skt=2023-03-23T05%3A54%3A27Z&ske=2023-03-24T05%3A54%3A27Z&sks=b&skv=2021-08-06&sig=zW/jyeYJfMSPfq9JgKYp94wRcYHXyrrZFEQjzss8OlY%3D");
        url2.put("url","https://oaidalleapiprodscus.blob.core.windows.net/private/org-fLdgj7yMu2OglTPJWONPORzM/user-miJSk0HNu2ycpTFVGuUOlNUh/img-3kEXDLefVsAHJBQN2EJrffN2.png?st=2023-03-23T08%3A12%3A54Z&se=2023-03-23T10%3A12%3A54Z&sp=r&sv=2021-08-06&sr=b&rscd=inline&rsct=image/png&skoid=6aaadede-4fb3-4698-a8f6-684d7786b067&sktid=a48cca56-e6da-484e-a814-9c849652bcb3&skt=2023-03-23T05%3A54%3A27Z&ske=2023-03-24T05%3A54%3A27Z&sks=b&skv=2021-08-06&sig=jP/tYcVm85TFwfnyi/EgjbF1gjG93Lwdsr6zS%2BdV8Zc%3D");
        url3.put("url","https://oaidalleapiprodscus.blob.core.windows.net/private/org-fLdgj7yMu2OglTPJWONPORzM/user-miJSk0HNu2ycpTFVGuUOlNUh/img-lY8bjMmbYq7A94eBA6nmGVU6.png?st=2023-03-23T08%3A12%3A54Z&se=2023-03-23T10%3A12%3A54Z&sp=r&sv=2021-08-06&sr=b&rscd=inline&rsct=image/png&skoid=6aaadede-4fb3-4698-a8f6-684d7786b067&sktid=a48cca56-e6da-484e-a814-9c849652bcb3&skt=2023-03-23T05%3A54%3A27Z&ske=2023-03-24T05%3A54%3A27Z&sks=b&skv=2021-08-06&sig=EgkH%2BaSMucY/bmcFH0MHgG2V6sSJYdNxBoxrosnvRVE%3D");
        jsonArray.add(url1);
        jsonArray.add(url2);
        jsonArray.add(url3);

        log.info(jsonArray.toString());
        result.put("data",jsonArray);
        return result.toString();
    }

}
