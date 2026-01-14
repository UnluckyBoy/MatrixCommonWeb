package com.cloudstudio.matrix.matrixcommonweb.webtool;

/**
 * @ClassName：ResponseCode
 * @Author: matrix
 * @Date: 2025/2/6 22:52
 * @Description:返回代码枚举
 */
public enum ResponseCode {

    SUCCESS(200,"请求成功!"),
    REPEAT_ERROR(402,"重数据重复!"),
    SERVER_ERROR(403,"服务器异常!"),
    FAILED(404,"请求失败!"),
    NO_LOGIN(405,"未登录"),
    WINNING(409,"警告！"),
    TOKEN_ERROR(501,"令牌错误"),
    TOKEN_EXPIRED(502,"令牌过期"),
    QUERY_ERROR(504,"查询异常!"),
    PARAM_ERROR(505,"请求参数异常!");


    private int code;
    private String msg;

    ResponseCode(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
