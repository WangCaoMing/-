package com.wangmeng.phonedefender.myinterface;

public interface ISmsBackupCallback {

    /**
     * ���ݿ�ʼǰ�Ļص�����
     * @param total ���ŵ�������
     */
    public void beforeBackup(int total);
    
    /**
     * ���ݽ����еĻص�����
     * @param progress �ѱ��ݶ��ŵ�����
     */
    public void onBackup(int progress);
    
    /**
     * ������ɺ���Ҫ���Ļص�����
     */
    public void afteronBackup();
}
