package com.example.demo.comm.tools;

/**
 * @ProjectName: demo
 * @Author: 王先望
 * @Description: 返回数据格式工具
 * @Date: 2021/8/19 16:10
 * @Version: 1.0
 */

import com.example.demo.enums.StatusEnums;
import com.example.demo.utils.JsonUtil;

import java.io.Serializable;

/**
 * 用于异步操作输出
 */
public class JsonResult implements Serializable {
    /**
     * 本次请求是否成功
     */
    private boolean state = true;
    /**
     * 请求的code
     */
    private Integer code;
    /**
     * 请求的数据
     */
    private Object data;
    /**
     * 前端弹出的消息  一般都会给消息  展示不展示由前端决定
     */
    private String message;
    /**
     * 防重复提交设计
     */
    private String token;

    /**
     * 错误信息
     */
    private String errorMsg;


    public static JsonResult getSuccessResponse(String msg) {
        return new JsonResult().setState(true).setCode(StatusEnums.SUCCESS.getCode()).setMessage(msg);
    }

    public static JsonResult getSuccessResponse() {
        return new JsonResult().setState(true).setCode(StatusEnums.SUCCESS.getCode()).setMessage("");
    }

    public static JsonResult getNoAuthResponse(String msg) {
        return new JsonResult().setState(false).setCode(StatusEnums.NO_AUTH.getCode()).setMessage(msg);
    }

    public static JsonResult getExceptionResponse(String msg) {
        return new JsonResult().setState(false).setCode(StatusEnums.EXCEPTION.getCode()).setMessage(msg);
    }

    public static JsonResult getFaildResponse(StatusEnums enums, String msg) {
        return new JsonResult().setState(false).setCode(enums.getCode()).setMessage(msg);
    }

    public static JsonResult getFaildResponse(Integer code, String msg) {
        return new JsonResult().setState(false).setCode(code).setMessage(msg);
    }


    @Override
    public String toString() {
        return JsonUtil.toJson(this);
    }

    public JsonResult setCode(Integer code) {
        this.code = code;
        return this;
    }

    public JsonResult setToken(String token) {
        this.token = token;
        return this;
    }

    public Integer getCode() {
        return code;
    }

    public JsonResult setState(boolean state) {
        this.state = state;
        return this;
    }

    public JsonResult setData(Object data) {
        this.data = data;
        return this;
    }


    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public JsonResult setMessage(String message) {
        this.message = message;
        return this;
    }

    public Object getData() {
        return data;
    }

    public boolean isState() {
        return state;
    }

    public String getMessage() {
        return message;
    }

    public String getToken() {
        return token;
    }
}

