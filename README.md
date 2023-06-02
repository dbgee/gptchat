# GPT CHAT for weixin

> 本项目主要是用来简化 chatgpt 与微信公众号的对接工作
> 继续稍作配置，即可将 chatgpt 接入自己的公众号进行使用

### Requirements
openai  gptkey  [api 申请地址](https://platform.openai.com/account/api-keys)

weixin  公众号token、appid、secret  [公众配置地址](https://mp.weixin.qq.com/advanced/advanced?action=dev&t=advanced/dev&token=&lang=zh_CN)

获取到上面内容后，直接替换配置文件中的值 [application-dev.yml](src%2Fmain%2Fresources%2Fapplication-dev.yml)

### Deploy
address:127.0.0.1

**启动server 端**
```shell
nohup java -jar gptchat-2.0.0.jar --server.address=127.0.0.1 & 
```

**配置微信公众号接口地址**  
http://your.domainOrIP/wx

