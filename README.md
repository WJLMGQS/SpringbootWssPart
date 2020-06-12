# SpringbootWssPart
**基于Springboot&amp;SpringWebSocket实现websocket服务端与客户端交互**

### 功能

    * 支持WebSocket客户端与服务端双向或单项通信
    * 支持客户端心跳检测及频率限制
    * 支持自定义实现客户端服务
    * 支持分布式部署多服务端
    * 支持多客户端并发通讯

### 设计
    
#####目录结构：org/wjlmgqs/swp
    
        * resources：wss通讯依赖的资源及环境配置，含redis、spring线程池等配置
        
        * core：封装了wss通讯完整的核心内容，直接引用
        
        * bus：基于core实现的业务模块，实现了caller及his2个wss客户端对接示例
            
            caller: WssController作为触发入口，向已连接的通讯客户端发送请求，并获得结果
            
            his: 客户端在成功连接到服务端后，主动向服务端查询业务数据，并获得结果
     
### 用法 

    以新增caller客户端未例    
    
#####  步骤一：在org/wjlmgqs/swp/bus/wss/s目录下新增服务：/caller/CallerWssSessionServiceImpl.java，继承AbstractWssSessionService并实现getWssClientType()接口

    @Slf4j
    @Service
    public class CallerWssSessionServiceImpl extends AbstractWssSessionService {
    
        /**
         * 声明叫号类型
         */
        public static WssClientType WSS_CLIENT_TYPE = WssClientType.build("caller", "叫号");
    
        @Override
        public WssClientType getWssClientType() {
            return WSS_CLIENT_TYPE;
        }
     
    }

#####  步骤二：在CallerWssSessionServiceImpl中定义业务方法，并调用:super.sendBusiMsg(),发送通讯消息给客户端


    /**
     * 发送业务消息，需要暂存会话，等客户端响应后唤醒
     */
    public <T extends WssSessionMsg> T sendBusiMsg(WssCallerCallParam callerCallParam) {
        return (T) super.sendBusiMsg(callerCallParam.getClinicId() + "",//组装成已经连接客户端的标识
                dozerBeanMapper.map(callerCallParam, CallSessionCallMsgData.class),
                WssSessionMsg.class); //定时从消息池中读取对应uuid的消息
    }

#####  步骤三：如果需要实现客户端查询服务单，可以参考HisWssSessionServiceImpl.java中实现的queryCallback()方法，默认在父类中已经实现


    /**
     * 处理客户端请求服务端
     */
    @Override
    public void queryCallback(WssSession wssSession, Session session, WssSessionMsg sessionMsg) {

        //入参请求
        HisClientSocketParams params = JSON.parseObject(sessionMsg.getData(), HisClientSocketParams.class);
        //构建响应结果
        HisClientSocketResult socketResult = new HisClientSocketResult();

        try {
            /**
             * TODO：根据params查询数据，组装结果对象
             */
            //.....
            socketResult.setResult("哈哈");
        } catch (Exception e) {
            sessionMsg.setCode(WssSessionMsg.SESSION_CODE_FAIL).setMsg(e.getMessage());
            e.printStackTrace();
        } finally {
            super.sendClientSessionMsg(session, sessionMsg
                    .setSessionType(WssSessionType.CLIENT.getCode())
                    .setData(JSON.toJSONString(socketResult))
            );
      /*      log.info("医保服务 响应客户端请求消息  versionType -> {} , busiType -> {} , 耗时 -> {} 毫秒 ({} - {}) , params -> {} , results -> {}  ", versionType, busiType.getValue(),
                    (currTime - resultData.getSessionTime()), currTime, resultData.getSessionTime(), data, JSON.toJSONString(socketResult));*/
        }
    }



    
             
        