package com.kk.gptchat.service;

import cn.hutool.http.HttpRequest;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.kk.gptchat.dao.QuestionDao;
import com.kk.gptchat.entity.Question;
import com.kk.gptchat.utils.GPTUtils;
import com.kk.gptchat.utils.TokenUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
@Slf4j
public class AsyncService {
    @Resource
    private QuestionDao questionDao;

    @Async
    public void saveMsg(String questionContent, String from, String uuid) {
        Question question=new Question();
        question.setOpenid(from);
        question.setQuestion(questionContent);
        question.setUuid(uuid);
        String answer=fetchGptContent(questionContent);
        question.setAnswer(answer);
        questionDao.save(question);
    }

    private static String fetchGptContent(String questionContent) {
        String responseContent= GPTUtils.createChat(questionContent);
        String tempContent="";
        if(responseContent.contains("That model is currently overloaded with other requests")){
            tempContent="GPT 服务器错误，负载过大，请稍后再试";
            log.error("gpt server error:{} ",responseContent);
        }else if (responseContent.contains("Rate limit reached for default-gpt-3.5-turbo")) {
            tempContent = "GPT 服务器错误limit，负载过大，请稍后再试";
            log.error("gpt server error:{} ", responseContent);
        }else if(responseContent.length()==0) {
            tempContent = "GPT 服务器错误，请联系管理员确认网络连接是否正常";
            log.error("gpt server connect error :{} ", responseContent);
        }else if(responseContent.contains("invalid_api_key")){
            tempContent="invalid_api_key";
            log.error("invalid_api_key :{} ", responseContent);

        }else{
            JSONObject resData=JSONObject.parseObject(responseContent);
            JSONArray temp= resData.getJSONArray("choices");
            tempContent=temp.getJSONObject(0).getJSONObject("message").getString("content");
        }

        return tempContent;
    }

    @Async
    /**
     * 需要微信认证，才能使用这个 主动发送消息的接口
     */
    public void sendMsgToUser(String questionContent, String openid){
//        调用客服接口，要使用json 数据包
        String token="";
        HashMap<String, String> temp= TokenUtils.getAccessToken();
        for(String key:temp.keySet()){
            token=key;
        }
        String url="https://api.weixin.qq.com/cgi-bin/message/custom/send?access_token={}".replace("{}",token);
        JSONObject body=new JSONObject();
        body.put("touser",openid);
        body.put("msgtype","text");
        JSONObject content=new JSONObject();
        content.put("content",fetchGptContent(questionContent));
        body.put("text",content);

        log.info("body:{}", body);
        String resBody= HttpRequest.post(url).body(body.toString()).execute().body();
        log.info("resBody:{}",resBody);

    }
}
