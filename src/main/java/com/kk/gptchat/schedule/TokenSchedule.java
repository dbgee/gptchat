package com.kk.gptchat.schedule;

import com.kk.gptchat.utils.TokenUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


@Component
@Slf4j
public class TokenSchedule {

    @Scheduled(cron="0 0/5 *  * * ? ")
    public void getTokenTask(){
        TokenUtils.getAccessToken();
        log.info("定时任务执行中，每5m 一次");
    }
}
