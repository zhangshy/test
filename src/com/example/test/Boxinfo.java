package com.example.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.http.conn.util.InetAddressUtils;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Environment;
import android.os.StatFs;
import android.text.format.Formatter;
import android.util.Log;

public class Boxinfo {
	/**
	 * 通过反射机制，遍历Build变量，打印系统信息
	 */
	public void logAndroidBuild() {
		Build build = new Build();
		Field[] fields = build.getClass().getDeclaredFields();
		Log.d(Test.TAG, "Build fields");
		for (Field field : fields) {
			Log.d(Test.TAG, field.toString());
			try {
				Log.d(Test.TAG, field.get(build).toString());
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		Method[] methods = build.getClass().getMethods();
		Log.d(Test.TAG, "Build methods");
		for (Method method:methods) {
			Log.d(Test.TAG, method.toString());
		}
		VERSION version = new VERSION();
		fields = version.getClass().getDeclaredFields();
		Log.d(Test.TAG, "VERSION fields");
		for (Field field : fields) {
			Log.d(Test.TAG, field.toString());
			try {
				Log.d(Test.TAG, field.get(version).toString());
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 使用StatFs类获取存储信息
	 * @return
	 */
	public long getStorageBytes() {
		File path = Environment.getDataDirectory();
		Log.d(Test.TAG, "path: " + path);
		StatFs sdStatFs = new StatFs(path.getPath());
		long blockSize = sdStatFs.getBlockSize();
		long totalCount = sdStatFs.getBlockCount();
		long size = blockSize*totalCount;
		double gb = size/1024/1024/1024.0;
		Log.d(Test.TAG, "total size: " + String.format("%.2f", gb) + "G");
		return size;
		
//		path = Environment.getExternalStorageDirectory();
//		Log.d(Test.TAG, "external: " + Environment.getExternalStorageState());
//		Log.d(Test.TAG, "path: " + path);
//		sdStatFs = new StatFs(path.getPath());
//		blockSize = sdStatFs.getBlockSize();
//		totalCount = sdStatFs.getBlockCount();
//		size = blockSize*totalCount;
//		Log.d(Test.TAG, "total size: " + size);
	}
	
	/**
	 * 读取/proc/meminfo文件获取内存信息
	 * @return
	 */
	public int getMemSizeKB() {
		BufferedReader reader = null;
		int sizeKB = 0;
		try {
			reader = new BufferedReader(new FileReader("/proc/meminfo"));
			String line = reader.readLine();
			Log.d(Test.TAG, "mem: " + line);
			if (line.toLowerCase().startsWith("memtotal")) {
				String[] tmps = line.split(" ");
				Log.d(Test.TAG, "length: " + tmps.length);
				for (String tmp : tmps) {
					Log.d(Test.TAG, tmp);
				}
				if (tmps.length>2) {
					sizeKB = Integer.parseInt(tmps[tmps.length-2]);
				} else {
					Log.e(Test.TAG, "meminfo split error");
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (reader!=null)
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
		
		double sizeG = sizeKB/1024/1024.0;
		Log.d(Test.TAG, "memsize: " + String.format("%.2f", sizeG));
		return sizeKB;
	}
	
	/**
	 * 通过WifiManager获取wifi信息
	 * @param context
	 */
	public void logWifiInfo(Context context) {
		WifiManager wifi = (WifiManager) context.getSystemService(context.WIFI_SERVICE);
		WifiInfo info = wifi.getConnectionInfo();
		String mac = info.getMacAddress();
		int ip = info.getIpAddress();
		Log.d(Test.TAG, "wifi mac:" + mac);
		Log.d(Test.TAG, String.format("wifi ip:%x", ip));
	}
	
	/**
	 * 通过NetworkInterface获取网络信息
	 * 也可以通过/sys/class/net/eth0/address获取以太网mac地址
	 */
	public void logNetworkInfo() {
		try {
			List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
			for (NetworkInterface intf : interfaces) {
				String name = intf.getName();
				Log.d(Test.TAG, "interface:" + name);
				byte[] mac = intf.getHardwareAddress();
				if (mac!=null) {
					StringBuilder buf = new StringBuilder();
	                for (int idx=0; idx<mac.length; idx++)
	                    buf.append(String.format("%02X:", mac[idx]));       
	                if (buf.length()>0) buf.deleteCharAt(buf.length()-1);
	                Log.d(Test.TAG, name+" mac:" + buf.toString());
				}
				Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses();
				while (enumIpAddr.hasMoreElements()) {
					InetAddress inetAddress = enumIpAddr.nextElement();
					if (!inetAddress.isLoopbackAddress()) {
//						String ip = Formatter.formatIpAddress(inetAddress.hashCode());
						String ip = inetAddress.getHostAddress();
						Log.d(Test.TAG, name+" ip:" + ip + ";" + InetAddressUtils.isIPv4Address(ip));
					}
				}
			}
		} catch (SocketException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 返回MAC地址，
	 * @param interfaceName 如eth0 wlan0
	 * @return mac地址，若没有对应设备返回null
	 */
	public String getMACAddress(String interfaceName) {
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                if (interfaceName != null) {
                    if (!intf.getName().equalsIgnoreCase(interfaceName)) continue;
                }
                byte[] mac = intf.getHardwareAddress();
                if (mac==null) return "";
                StringBuilder buf = new StringBuilder();
                for (int idx=0; idx<mac.length; idx++)
                    buf.append(String.format("%02X:", mac[idx]));       
                if (buf.length()>0) buf.deleteCharAt(buf.length()-1);
                return buf.toString();
            }
        } catch (Exception ex) { } // for now eat exceptions
        return null;
	}
	
	/**
	 * 使用BluetoothAdapter获取蓝牙mac
	 * @return
	 */
	public String getBluetoothMac() {
		String mac = null;
		BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
		mac = adapter.getAddress();
		Log.d(Test.TAG, adapter.getName() + " mac:" + mac + ";state:" + adapter.getState());
		return mac;
	}
	
	class CpuFileFilter implements FileFilter {
		public boolean accept(File file) {
			//正则表达式比较
			if (Pattern.matches("cpu[0-9]", file.getName())) {
				return true;
			}
			return false;
		}
	}
	/**
	 * 通过查找/sys/devices/system/cpu/下的cpu文件个数确定CPU核心数
	 * 而/sys/devices/system/cpu/kernel_max: the maximum cpu index allowed by the kernel configuration
	 * @return cpu核心数
	 */
	public int getcpuNum() {
		int num = 0;
		File dir = new File("/sys/devices/system/cpu/");
		File[] files = dir.listFiles(new CpuFileFilter());
		num = files.length;
		Log.d(Test.TAG, "cpu nums: " + num);
		return num;
	}
	
}
