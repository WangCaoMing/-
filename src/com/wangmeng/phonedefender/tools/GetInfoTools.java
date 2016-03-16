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
 * ��ȡϵͳ��Ϣ�Ĺ�����
 * 
 * @author Administrator
 * 
 */
public class GetInfoTools {

    /**
     * ��ȡϵͳ�а�װ������Ӧ�õ���Ϣ
     * 
     * @param context
     */
    public static List<AppInfoBean> getAppInfo(Context context) {
        // ��ȡ��������
        PackageManager packageManager = context.getPackageManager();

        // ��ȡ���豸�����а�װ�İ�
        List<PackageInfo> installedPackages = packageManager
                .getInstalledPackages(0);

        // �������а�װ�İ�
        // ���ڴ������app��Ϣ��list
        List<AppInfoBean> list = new ArrayList<AppInfoBean>();
        for (PackageInfo packageInfo : installedPackages) {

            // �������ڴ����Ϣ��bean
            AppInfoBean bean = new AppInfoBean();
            // ��ȡ����
            String packageName = packageInfo.packageName;
            // ��ȡapp��
            String applabel = packageInfo.applicationInfo.loadLabel(
                    packageManager).toString();
            // ��ȡͼ��
            Drawable icon = packageInfo.applicationInfo
                    .loadIcon(packageManager);
            // ��ȡӦ������(ϵͳӦ�û����û�Ӧ��?)
            boolean isRomApp;
            int flags = packageInfo.applicationInfo.flags;
            if ((flags & ApplicationInfo.FLAG_SYSTEM) == 1)
                isRomApp = true;
            else
                isRomApp = false;
            // ��ȡӦ�ó���Ĵ�С
            String size;
            String sourceDir = packageInfo.applicationInfo.sourceDir;
            File file = new File(sourceDir);
            size = Formatter.formatFileSize(context, file.length()); // ת�����ʵ���ʾ��ʽ
            // ��ȡӦ�ó������ڴ洢�ռ��λ��
            boolean locationSD;
            flags = packageInfo.applicationInfo.flags;
            if ((flags & ApplicationInfo.FLAG_EXTERNAL_STORAGE) == 1)
                locationSD = true;
            else
                locationSD = false;

            // ����ȡ������Ϣ����bean��
            bean.setIcon(icon);
            bean.setName(applabel);
            bean.setPackagename(packageName);
            bean.setRom(isRomApp);
            bean.setSize(size);

            // ��bean�����list��
            list.add(bean);

            // System.out.println("����:" + packageName);
            // System.out.println("Ӧ����:" + applabel);
            // System.out.println("����:" + packageName);

        }
        return list;
    }
    

    /**
     * ��ȡ�������н��̵���Ϣ
     * @param context �����Ķ���
     * @return ��Ž�����Ϣ��list 
     */
    public static List<TaskInfo> getTaskInfos(Context context)
    {
        //�������ڴ�Ż�ȡ����������Ϣ
        List<TaskInfo> list = new ArrayList<TaskInfo>();
        
        //��ȡActivivy����������
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Service.ACTIVITY_SERVICE);
        
        //��ȡ��������
        PackageManager packageManager = context.getPackageManager();
        
        //��ȡ�����������е�app�Ľ���
        List<RunningAppProcessInfo> runningAppProcesses = activityManager.getRunningAppProcesses();
        //���������������еĽ���
        for (RunningAppProcessInfo runningAppProcessInfo : runningAppProcesses) {
            //�������ڴ�Ž�����Ϣ��bean
            TaskInfo bean = new TaskInfo();
            
            //��ȡ�ý�������app�İ���
            String packagename = runningAppProcessInfo.processName;
            
            //��ȡ����������app������,ͼ��, ���û����̵ı�־
            String name = "";
            Drawable icon = null;
            boolean isusertask = true;
            try {
                PackageInfo packageinfo = packageManager.getPackageInfo(packagename, PackageManager.GET_ACTIVITIES);
                name = packageinfo.applicationInfo.loadLabel(packageManager).toString();
                icon = packageinfo.applicationInfo.loadIcon(packageManager);
                
                //�жϸý������û����̻���ϵͳ����
                int flags = packageinfo.applicationInfo.flags;
                if ((flags & ApplicationInfo.FLAG_SYSTEM) == 0)
                    isusertask = true;
                else 
                    isusertask = false;
                    
            } catch (NameNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            
            //��ȡ���̵������ڴ��С
            MemoryInfo[] meminfo = activityManager.getProcessMemoryInfo(new int[]{runningAppProcessInfo.pid});
            int memory_KB = meminfo[0].getTotalPrivateDirty();//ע�ⷵ�ص�ֵ�ĵ�λ��KB, ע��ת��
            long memory_B = memory_KB * 1024l;
            
            //����ȡ������Ϣ����bean��
            bean.setName(name);
            bean.setIcon(icon);
            bean.setPackagename(packagename);
            bean.setMemsize(memory_B);
            bean.setUsertask(isusertask);
            
            //��bean����list��
            list.add(bean);
            
        //    System.out.println(bean.toString());
        }
        return list;
    }
    
    /**
     * ��������ļ���MD5ֵ
     * @param file Ҫ����MD5���ļ�
     * @return �ַ������͵�MD5ֵ(16����)
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
