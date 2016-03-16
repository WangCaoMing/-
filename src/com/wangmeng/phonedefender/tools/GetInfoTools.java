package com.wangmeng.phonedefender.tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import com.wangmeng.phonedefender.bean.AppInfoBean;
import com.wangmeng.phonedefender.bean.TaskInfo;

import android.R.integer;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.Service;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.os.Debug.MemoryInfo;
import android.text.format.Formatter;

/**
 * 获取系统信息的工具类
 * 
 * @author Administrator
 * 
 */
public class GetInfoTools {

    /**
     * 获取系统中安装的所有应用的信息
     * 
     * @param context
     */
    public static List<AppInfoBean> getAppInfo(Context context) {
        // 获取包管理器
        PackageManager packageManager = context.getPackageManager();

        // 获取该设备上所有安装的包
        List<PackageInfo> installedPackages = packageManager
                .getInstalledPackages(0);

        // 遍历所有安装的包
        // 用于存放所有app信息的list
        List<AppInfoBean> list = new ArrayList<AppInfoBean>();
        for (PackageInfo packageInfo : installedPackages) {

            // 创建用于存放信息的bean
            AppInfoBean bean = new AppInfoBean();
            // 获取包名
            String packageName = packageInfo.packageName;
            // 获取app名
            String applabel = packageInfo.applicationInfo.loadLabel(
                    packageManager).toString();
            // 获取图标
            Drawable icon = packageInfo.applicationInfo
                    .loadIcon(packageManager);
            // 获取应用类型(系统应用还是用户应用?)
            boolean isRomApp;
            int flags = packageInfo.applicationInfo.flags;
            if ((flags & ApplicationInfo.FLAG_SYSTEM) == 1)
                isRomApp = true;
            else
                isRomApp = false;
            // 获取应用程序的大小
            String size;
            String sourceDir = packageInfo.applicationInfo.sourceDir;
            File file = new File(sourceDir);
            size = Formatter.formatFileSize(context, file.length()); // 转换合适的显示格式
            // 获取应用程序所在存储空间的位置
            boolean locationSD;
            flags = packageInfo.applicationInfo.flags;
            if ((flags & ApplicationInfo.FLAG_EXTERNAL_STORAGE) == 1)
                locationSD = true;
            else
                locationSD = false;

            // 将获取到的信息存入bean中
            bean.setIcon(icon);
            bean.setName(applabel);
            bean.setPackagename(packageName);
            bean.setRom(isRomApp);
            bean.setSize(size);

            // 将bean存放入list中
            list.add(bean);

            // System.out.println("包名:" + packageName);
            // System.out.println("应用名:" + applabel);
            // System.out.println("包名:" + packageName);

        }
        return list;
    }
    

    /**
     * 获取正在运行进程的信息
     * @param context 上下文对象
     * @return 存放进程信息的list 
     */
    public static List<TaskInfo> getTaskInfos(Context context)
    {
        //创建用于存放获取到的任务信息
        List<TaskInfo> list = new ArrayList<TaskInfo>();
        
        //获取Activivy管理器对象
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Service.ACTIVITY_SERVICE);
        
        //获取包管理器
        PackageManager packageManager = context.getPackageManager();
        
        //获取所有正在运行的app的进程
        List<RunningAppProcessInfo> runningAppProcesses = activityManager.getRunningAppProcesses();
        //遍历所有正在运行的进程
        for (RunningAppProcessInfo runningAppProcessInfo : runningAppProcesses) {
            //创建用于存放进程信息的bean
            TaskInfo bean = new TaskInfo();
            
            //获取该进程所属app的包名
            String packagename = runningAppProcessInfo.processName;
            
            //获取进程所属的app的名字,图标, 和用户进程的标志
            String name = "";
            Drawable icon = null;
            boolean isusertask = true;
            try {
                PackageInfo packageinfo = packageManager.getPackageInfo(packagename, PackageManager.GET_ACTIVITIES);
                name = packageinfo.applicationInfo.loadLabel(packageManager).toString();
                icon = packageinfo.applicationInfo.loadIcon(packageManager);
                
                //判断该进程是用户进程还是系统进程
                int flags = packageinfo.applicationInfo.flags;
                if ((flags & ApplicationInfo.FLAG_SYSTEM) == 0)
                    isusertask = true;
                else 
                    isusertask = false;
                    
            } catch (NameNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            
            //获取进程的运行内存大小
            MemoryInfo[] meminfo = activityManager.getProcessMemoryInfo(new int[]{runningAppProcessInfo.pid});
            int memory_KB = meminfo[0].getTotalPrivateDirty();//注意返回的值的单位是KB, 注意转换
            long memory_B = memory_KB * 1024l;
            
            //将获取到的信息放入bean中
            bean.setName(name);
            bean.setIcon(icon);
            bean.setPackagename(packagename);
            bean.setMemsize(memory_B);
            bean.setUsertask(isusertask);
            
            //将bean放入list中
            list.add(bean);
            
        //    System.out.println(bean.toString());
        }
        return list;
    }
    
    /**
     * 计算给定文件的MD5值
     * @param file 要计算MD5的文件
     * @return 字符串类型的MD5值(16进制)
     */
    public static String getFileMd5(File file)
    {
        try {
            MessageDigest md5 = MessageDigest.getInstance("md5");
            InputStream in = new FileInputStream(file);
            byte[] b = new byte[1024];
            int length = -1;
            while ((length = in.read(b)) != -1)
            {
                md5.update(b, 0, length);
            }
            
            byte[] digest = md5.digest();
            StringBuffer buffer = new StringBuffer();
            for (int i = 0; i < digest.length; ++i)
            {
                int temp = digest[i] & 0xff;
                String hex = Integer.toHexString(temp);
                buffer.append((hex.length() == 2) ? hex : "0" + hex);
            }
            
            return buffer.toString();
            
        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }
    
}
