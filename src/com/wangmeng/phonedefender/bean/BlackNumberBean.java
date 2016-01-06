package com.wangmeng.phonedefender.bean;

/**
 * 用于存放黑名单中电话号码的bean
 * @author Administrator
 *
 */
public class BlackNumberBean {

    /**
     * 电话号码
     */
    String number;
    
    /**
     * 拦截模式
     */
    String mode;

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }
    
    
}
