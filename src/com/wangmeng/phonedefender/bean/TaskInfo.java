package com.wangmeng.phonedefender.bean;

import android.graphics.drawable.Drawable;

/**
 * 正在运行进程的信息
 * @author Administrator
 *
 */
public class TaskInfo {
    
    private String name;
    private String packagename;
    private Drawable icon;
    private long memsize;
    private boolean usertask;
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
    public Drawable getIcon() {
        return icon;
    }
    public void setIcon(Drawable icon) {
        this.icon = icon;
    }
    public long getMemsize() {
        return memsize;
    }
    public void setMemsize(long memsize) {
        this.memsize = memsize;
    }
    public boolean isUsertask() {
        return usertask;
    }
    public void setUsertask(boolean usertask) {
        this.usertask = usertask;
    }
    @Override
    public String toString() {
        return "TaskInfo [name=" + name + ", packagename=" + packagename
                + ", memsize=" + memsize + ", usertask=" + usertask + "]";
    }
    
    
}
