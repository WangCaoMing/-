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
 * ��ȡ�ֻ�λ����Ϣ�ķ���
 * @author Administrator
 *
 */
public class LocationService extends Service {
	
	//�����ļ�
	private static SharedPreferences sprefs;
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		//��ȡ�����ļ�
		sprefs = getSharedPreferences("sprefs", MODE_PRIVATE);
		
		//��ȡϵͳ��λ�ù�����
		LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE); 
		
		Criteria criteria = new Criteria();// ������ȡ����ṩ�ߵı�׼
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		criteria.setCostAllowed(true);
		String bestProvider = locationManager.getBestProvider(criteria, true); //���ݱ�׼��ȡ��õ��ṩ��
		locationManager.requestLocationUpdates(bestProvider, 60, 10, new MyLocationListener());
		
	}
	
	/**
	 * �Զ����LocationListener
	 * @author Administrator
	 *
	 */
	class MyLocationListener implements LocationListener
	{

		@Override
		public void onLocationChanged(Location location) {
			
			//��ȡλ�õľ��Ⱥ�γ��
			double latitude = location.getLatitude();
			double longitude = location.getLongitude();
			
			System.out.println("��γ��:" + latitude + ", " + longitude);
			
			//����ȡ���ľ��Ⱥ�γ��д�뵽�����ļ���
			sprefs.edit().putString("latitude", String.valueOf(latitude)).commit();
			sprefs.edit().putString("longitude", String.valueOf(longitude)).commit();
			sprefs.edit().putBoolean("location_enable", true).commit(); //����λ����Ϣ��׼���õı�ʶ
			
			//�رյ��Լ�
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
