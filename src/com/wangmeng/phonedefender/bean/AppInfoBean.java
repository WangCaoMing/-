package com.wangmeng.phonedefender.bean;

import java.io.File;

import android.graphics.drawable.Drawable;



/**
 * ���ڴ��application������Ϣ��bean
 * @author Administrator
 *
 */
public class AppInfoBean {
    
    private Drawable icon;
    private String name;
    private String packagename;
    private boolean isRom; //ϵͳӦ��? �û�Ӧ��
    private String size;
    private boolean locationSD; //Ӧ�ó������ڴ洢�ռ��λ��
    
    
    public Drawable getIcon() {
        return icon;
    }
    public void setIcon(Drawable icon) {
        this.icon = icon;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getPackagename() {
        return packagename;
    }
    public void setPackagename(String packagename) {
        this.packagename = packagename;
    }
    public boolean isRom() {
        return isRom;
    }
    public void setRom(boolean isRom) {
        this.isRom = isRom;
    }
    public String getSize() {
        return size;
    }
    public void setSize(String size) {
        this.size = size;
    }
    
    public boolean isLocationSD() {
        return locationSD;
    }
    public void setLocationSD(boolean locationSD) {
        this.locationSD = locationSD;
    }
    @Override
    public String toString() {
        return "AppInfoBean [icon=" + icon + ", name=" + name
                + ", packagename=" + packagename + ", isRom=" + isRom
                + ", size=" + size + "]";
    }
    
    
    
    
}
