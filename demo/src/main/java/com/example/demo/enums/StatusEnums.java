package com.example.demo.enums;

public enum StatusEnums {
    /**
     * 1 通用
     * 2 成功
     *
     */
    SUCCESS(20000),    //成功
    EXCEPTION(10002),  //服务器异常 无法预知的错误
    NO_AUTH(10001),//登录失败
    //参数错误，必填项为空
    NO_LOGIN(10009),//未登陆
    PARAMS_ERROR(30000);  //自定义msg，可以预知的错误 失败的返回



    private Integer code;

    StatusEnums(Integer code){
        this.code = code;
    }

    public Integer getCode() {
        return code;
    }

}

