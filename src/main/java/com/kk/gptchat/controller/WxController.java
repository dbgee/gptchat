package com.kk.gptchat.controller;

import cn.hutool.core.util.StrUtil;
import com.kk.gptchat.dao.QuestionDao;
import com.kk.gptchat.entity.Msg;
import com.kk.gptchat.entity.Question;
import com.kk.gptchat.service.AsyncService;
import com.kk.gptchat.utils.Utils;
import com.kk.gptchat.utils.XmlUtils;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@Slf4j
public class WxController {
    @Resource
    private QuestionDao questionDao;
    @Resource
    private AsyncService asyncService;

    @RequestMapping("/wx")
    @ResponseBody
    public String handleWxMsg(@RequestBody(required = false) String data, HttpServletRequest request){
        if(request.getMethod().equalsIgnoreCase("get")){
            return "server is running...";
        }
        Msg receiveMsg= (Msg) XmlUtils.xmlToObject(data);
        String questionContent=receiveMsg.getContent();
        String client=receiveMsg.getFromUserName();
        String server=receiveMsg.getToUserName();
        String type=receiveMsg.getMsgType();
        String resContent ="";

        if(type.equals("event")){
            resContent=generateMsg(server,client,"欢迎您的关注：\n为您提供最专业的安全服务和解决方案！");
            return resContent;
        }

        if(!type.equals("text")){
            resContent=generateMsg(server,client,"目前除了文本消息，其它类型的消息暂不支持呢 😁");
            return resContent;
        }

        log.info("user:{} >>>>>>>>> msg content:{}",client,questionContent);

        if(questionContent.startsWith("new")){
            questionContent=questionContent.replace("new","");
        }else {
            String oldQuestion=askBefore(questionContent);
            if(oldQuestion.length()>0){
                resContent=generateMsg(server,client,oldQuestion);
                return resContent;
            }
        }

        if(Utils.checkUUID(StrUtil.trim(questionContent))){
            Question temp=questionDao.findQuestionByUuid(questionContent);
            String gptAnswer;
            try {
                gptAnswer=temp.getAnswer().replace("GPT-3","GPT-4").replace("\\\"","\"");
            }catch (Exception e){
                log.error("sql error:{}",e.toString());
                gptAnswer="数据查询异常，请确认uuid 是否正确；GPT 生成内容需要时间，请耐心等待几秒钟再重试";
            }
            resContent=generateMsg(server,client,gptAnswer);
        }else{
            String uuid= Utils.getUUID();
            String tips="🤣 由于公众号接口限制，需要发送下面的这串数字（uuid）到聊天框，获取GPT 生成的内容\n\n  ";
            resContent=generateMsg(server,client,tips+uuid);
            if(questionContent.startsWith("remove")){
                questionContent=questionContent.replace("remove","");
            }
            asyncService.saveMsg(questionContent, client, uuid);
        }
        return resContent;
    }

    private String generateMsg( String server, String client,String msgContent){
        Msg sendMsg=new Msg();
        sendMsg.setFromUserName(XmlUtils.needCdata(server));
        sendMsg.setToUserName(XmlUtils.needCdata(client));
        sendMsg.setCreateTime(String.valueOf(System.currentTimeMillis()/1000));
        sendMsg.setMsgType(XmlUtils.needCdata("text"));
        sendMsg.setContent(XmlUtils.needCdata(msgContent));
        String resContent=XmlUtils.objectToXml(sendMsg);
        return resContent;
    }

    private String askBefore(String question){
        List<Question> all = questionDao.findAll();

        if(question.startsWith("remove")){
            question=question.replace("remove","");
            for (Question item :
                    all) {
                if(item.getQuestion().equals(question)){
                    log.warn("准备删除这个问题xxxxxxx:{}",question);
                    Long id= item.getId();
                    questionDao.deleteById(id);
                    return "";
                }
            }
        }else{
            for (Question item :
                    all) {
                if(item.getQuestion().equals(question)){
                    log.warn("这个问题已经问过咯:{}",question);
                    return item.getAnswer();
                }
            }
        }
        return "";
    }
}
