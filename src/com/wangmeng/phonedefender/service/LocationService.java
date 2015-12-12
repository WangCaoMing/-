package com.wangmeng.phonedefender.service;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;

/**
 * 获取手机位置信息的服务
 * @author Administrator
 *
 */
public class LocationService extends Service {
	
	//配置文件
	private static SharedPreferences sprefs;
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		//获取配置文件
		sprefs = getSharedPreferences("sprefs", MODE_PRIVATE);
		
		//获取系统的位置管理器
		LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE); 
		
		Criteria criteria = new Criteria();// 创建获取最好提供者的标准
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		criteria.setCostAllowed(true);
		String bestProvider = locationManager.getBestProvider(criteria, true); //根据标准获取最好的提供者
		locationManager.requestLocationUpdates(bestProvider, 60, 10, new MyLocationListener());
		
	}
	
	/**
	 * 自定义的LocationListener
	 * @author Administrator
	 *
	 */
	class MyLocationListener implements LocationListener
	{

		@Override
		public void onLocationChanged(Location location) {
			
			//获取位置的经度和纬度
			double latitude = location.getLatitude();
			double longitude = location.getLongitude();
			
			System.out.println("经纬度:" + latitude + ", " + longitude);
			
			//将获取到的经度和纬度写入到配置文件中
			sprefs.edit().putString("latitude", String.valueOf(latitude)).commit();
			sprefs.edit().putString("longitude", String.valueOf(longitude)).commit();
			sprefs.edit().putBoolean("location_enable", true).commit(); //发送位置信息已准备好的标识
			
			//关闭掉自己
			stopSelf();
		}

		@Override
		public void onProviderDisabled(String provider) {
			
		}

		@Override
		public void onProviderEnabled(String provider) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO Auto-generated method stub
			
		}
		
	}

}
