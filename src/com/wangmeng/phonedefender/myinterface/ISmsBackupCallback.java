package com.wangmeng.phonedefender.myinterface;

public interface ISmsBackupCallback {

    /**
     * 备份开始前的回调函数
     * @param total 短信的总条数
     */
    public void beforeBackup(int total);
    
    /**
     * 备份进行中的回调函数
     * @param progress 已备份短信的条数
     */
    public void onBackup(int progress);
    
    /**
     * 备份完成后需要做的回调函数
     */
    public void afteronBackup();
}
