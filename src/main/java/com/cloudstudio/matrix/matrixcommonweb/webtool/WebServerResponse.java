package com.cloudstudio.matrix.matrixcommonweb.webtool;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;


/**
 * @Class WebServerResponse
 * @Author Create By Matrix·张
 * @Date 2024/11/13 下午3:39
 * 后台返回类
 */
@Data
public class WebServerResponse {
    private boolean success;//处理状态
    private int code;//处理代码
    private String msg;//处理描述
    private Object content;//处理数据

    private WebServerResponse(){};


    public static WebServerResponse success(){
        WebServerResponse resultResponse=new WebServerResponse();
        resultResponse.setSuccess(true);
        resultResponse.setCode(ResponseCode.SUCCESS.getCode());
        resultResponse.setMsg(ResponseCode.SUCCESS.getMsg());
        resultResponse.setContent(null);
        return resultResponse;
    }
    public static WebServerResponse success(Object object){
        WebServerResponse resultResponse=new WebServerResponse();
        resultResponse.setSuccess(true);
        resultResponse.setCode(ResponseCode.SUCCESS.getCode());
        resultResponse.setMsg(ResponseCode.SUCCESS.getMsg());
        resultResponse.setContent(object);
        return resultResponse;
    }
    public static WebServerResponse success(String message){
        WebServerResponse resultResponse=new WebServerResponse();
        resultResponse.setSuccess(true);
        resultResponse.setCode(ResponseCode.SUCCESS.getCode());
        resultResponse.setMsg(message);
        resultResponse.setContent(null);
        return resultResponse;
    }
    public static WebServerResponse success(String message,Object object){
        WebServerResponse resultResponse=new WebServerResponse();
        resultResponse.setSuccess(true);
        resultResponse.setCode(ResponseCode.SUCCESS.getCode());
        resultResponse.setMsg(message);
        resultResponse.setContent(object);
        return resultResponse;
    }
    public static WebServerResponse failure(){
        WebServerResponse resultResponse=new WebServerResponse();
        resultResponse.setSuccess(false);
        resultResponse.setCode(ResponseCode.FAILED.getCode());
        resultResponse.setMsg(ResponseCode.FAILED.getMsg());
        resultResponse.setContent(null);
        return resultResponse;
    }
//    public static WebServerResponse failure(AuthenticationException exception){
//        WebServerResponse resultResponse=new WebServerResponse();
//        resultResponse.setHandleType(false);
//        resultResponse.setHandleCode(404);
//        resultResponse.setHandleMessage(MessageUtil.Message(exception));
//        resultResponse.setHandleData(null);
//        return resultResponse;
//    }
    public static WebServerResponse failure(String message){
        WebServerResponse resultResponse=new WebServerResponse();
        resultResponse.setSuccess(false);
        resultResponse.setCode(ResponseCode.FAILED.getCode());
        resultResponse.setMsg(message);
        resultResponse.setContent(null);
        return resultResponse;
    }

    public static WebServerResponse winning(String message){
        WebServerResponse resultResponse=new WebServerResponse();
        resultResponse.setSuccess(false);
        resultResponse.setCode(ResponseCode.WINNING.getCode());
        resultResponse.setMsg(message);
        resultResponse.setContent(null);
        return resultResponse;
    }

    /**
     * token过期
     * @return
     */
    public static WebServerResponse tokenError(){
        WebServerResponse resultResponse=new WebServerResponse();
        resultResponse.setSuccess(false);
        resultResponse.setCode(ResponseCode.TOKEN_ERROR.getCode());
        resultResponse.setMsg(ResponseCode.TOKEN_ERROR.getMsg());
        resultResponse.setContent(null);
        return resultResponse;
    }

    /**
     * 头token过期
     * @return
     */
    public static WebServerResponse tokenExpired(){
        WebServerResponse resultResponse=new WebServerResponse();
        resultResponse.setSuccess(false);
        resultResponse.setCode(ResponseCode.TOKEN_EXPIRED.getCode());
        resultResponse.setMsg(ResponseCode.TOKEN_EXPIRED.getMsg());
        resultResponse.setContent(null);
        return resultResponse;
    }

    /**
     * 未登录
     * @return
     */
    public static WebServerResponse notLogin(){
        WebServerResponse resultResponse=new WebServerResponse();
        resultResponse.setSuccess(false);
        resultResponse.setCode(ResponseCode.NO_LOGIN.getCode());
        resultResponse.setMsg(ResponseCode.NO_LOGIN.getMsg());
        resultResponse.setContent(null);
        return resultResponse;
    }

    public static WebServerResponse paramError(){
        WebServerResponse resultResponse=new WebServerResponse();
        resultResponse.setSuccess(false);
        resultResponse.setCode(ResponseCode.PARAM_ERROR.getCode());
        resultResponse.setMsg(ResponseCode.PARAM_ERROR.getMsg());
        resultResponse.setContent(null);
        return resultResponse;
    }
}
