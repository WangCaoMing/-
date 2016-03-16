package com.wangmeng.phonedefender.bean;

import android.graphics.drawable.Drawable;

public class TrafficInfoBean {
    private String name = null;
    private Drawable icon = null;
    private String packagename = null;
    private long tx = 0;
    private long rx = 0;
    private long totaltraffic = 0;
    
    
    public long getTotaltraffic() {
        return totaltraffic;
    }
    public void setTotaltraffic(long totaltraffic) {
        this.totaltraffic = totaltraffic;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public Drawable getIcon() {
        return icon;
    }
    public void setIcon(Drawable icon) {
        this.icon = icon;
    }
    public String getPackagename() {
        return packagename;
    }
    public void setPackagename(String packagename) {
        this.packagename = packagename;
    }
    public long getTx() {
        return tx;
    }
    public void setTx(long tx) {
        this.tx = tx;
    }
    public long getRx() {
        return rx;
    }
    public void setRx(long rx) {
        this.rx = rx;
    }
    
    
    @Override
    public String toString() {
        return "TrafficInfoBean [name=" + name + ", icon=" + icon
                + ", packagename=" + packagename + ", tx=" + tx + ", rx=" + rx
                + ", totaltraffic=" + totaltraffic + "]";
    }
    public long totalTraffic()
    {
        return rx + tx;
    }
    
}
