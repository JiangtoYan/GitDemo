package com.sccp.library.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.Map;

import com.sccp.library.http.BaseHttpClient;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;

public class ImageUtil {

	private static String LOG_TAG = "ImageUtil::";
	
	@SuppressLint("DefaultLocale")
	public static String getImageFilePathByUrl(String imageUrl, String subPath, Context context) {

		String md5 = StringUtil.stringToMD5(imageUrl);
		String imageName = String.format("%s.%s", md5.substring(2), FileUtil.getFileExtension(imageUrl).toLowerCase());
		Log.d(LOG_TAG + "getImageFilePathByUrl:", imageName);
		
		String cachePath = StorageUtil.getFileCachePath(context, subPath);
		
		if(StringUtil.isEmpty(cachePath)) {
			return null;
		}
		
		cachePath = String.format("%s/%s", cachePath, md5.substring(0, 2));
		File dirFile = new File(cachePath);
		
		if(!dirFile.exists()){
			dirFile.mkdirs();
		}
		
		String filePath = String.format("%s/%s", cachePath, imageName);
		Log.d(LOG_TAG + "getImageFilePathByUrl:", filePath);
		
		if(FileUtil.isFileExist(filePath)) {
			return filePath;
		}
		
		return null;
	}
	
	@SuppressLint("DefaultLocale")
	public static String getImageFilePath(String imageUrl, String subPath, Context context) {

		String md5 = StringUtil.stringToMD5(imageUrl);
		String imageName = String.format("%s.%s", md5.substring(2), FileUtil.getFileExtension(imageUrl).toLowerCase());
		Log.d(LOG_TAG + "getImageFilePath:", imageName);
		
		String cachePath = StorageUtil.getFileCachePath(context, subPath);
		
		if(StringUtil.isEmpty(cachePath)) {
			return null;
		}
		
		cachePath = String.format("%s/%s", cachePath, md5.substring(0, 2));
		File dirFile = new File(cachePath);
		
		if(!dirFile.exists()){
			dirFile.mkdirs();
		}
		
		String filePath = String.format("%s/%s", cachePath, imageName);
		Log.d(LOG_TAG + "getImageFilePath:", filePath);
		
		if(FileUtil.isFileExist(filePath)) {
			return filePath;
		}
		
		InputStream stream = getInputStreamFromUrl(imageUrl, 10000);
		
		if(stream == null) {
			return null;
		}
		
		return inputStreamToFile(filePath, stream);
	}
	
	/**
     * get input stream from network by imageurl, you need to close inputStream yourself
     * 
     * @param imageUrl
     * @param readTimeOutMillis
     * @return
     * @see ImageUtils#getInputStreamFromUrl(String, int, boolean)
     */
    public static InputStream getInputStreamFromUrl(String imageUrl, int readTimeOutMillis) {
        return getInputStreamFromUrl(imageUrl, readTimeOutMillis, null);
    }
	
	public static InputStream getInputStreamFromUrl(String imageUrl, int readTimeOutMillis, Map<String, String> requestProperties) {
		
		InputStream stream = null;
		BaseHttpClient imageHttpClient = BaseHttpClient.getInstance();
		stream = imageHttpClient.getImageInputStream(imageUrl);
		
		return stream;
	}
	
    /**
     * get Bitmap by imageUrl
     * 
     * @param imageUrl
     * @param readTimeOut
     * @return
     * @see ImageUtils#getBitmapFromUrl(String, int, boolean)
     */
    public static Bitmap getBitmapFromUrl(String imageUrl, int readTimeOut) {
        return getBitmapFromUrl(imageUrl, readTimeOut, null);
    }

    /**
     * get Bitmap by imageUrl
     * 
     * @param imageUrl
     * @param requestProperties http request properties
     * @return
     */
    public static Bitmap getBitmapFromUrl(String imageUrl, int readTimeOut, Map<String, String> requestProperties) {
    	
        InputStream stream = getInputStreamFromUrl(imageUrl, readTimeOut, requestProperties);
        Bitmap b = BitmapFactory.decodeStream(stream);
        closeInputStream(stream);
        return b;
    }

	@SuppressLint("DefaultLocale")
	public static String inputStreamToFile(String filePath, InputStream is) {
		
		Bitmap bitmap = BitmapUtil.getBitmapFromStream(is);
		
		if(bitmap == null) {
			return null;
		}
		
		FileOutputStream fos = null;
		
		try {

			String imageExtension = FileUtil.getFileExtension(filePath).toLowerCase();
			CompressFormat cf = null;
			
			if(imageExtension.equals("png")) {
				cf = CompressFormat.PNG;
			}
			else if(imageExtension.equals("jpg")) {
				cf = CompressFormat.JPEG;
			}
			else{
				
				if(Build.VERSION.SDK_INT >= 14) {
					cf = CompressFormat.WEBP;
				}
				else{
					cf = CompressFormat.JPEG;
				}
			}
			
			File imageFile = new File(filePath);

			if(!imageFile.exists()){
				imageFile.createNewFile();
			}
			
			ByteArrayOutputStream byteArrayOutputStream = BitmapUtil.compressImage(bitmap, 60, cf);
	        fos = new FileOutputStream(imageFile);
            fos.write(byteArrayOutputStream.toByteArray());
            Log.d(LOG_TAG + "inputStreamToFile:", "文件保存成功");
            
            fos.flush();
            fos.close();
            
            return filePath;
		}
		catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		finally {
			
            if (fos != null) {
            	
                try {
                    fos.close();
                    is.close();
                }
                catch (IOException e) {
                    throw new RuntimeException("IOException occurred. ", e);
                }
            }
        }
	}

    /**
     * close inputStream
     * 
     * @param is
     */
    private static void closeInputStream(InputStream is) {
    	
        if (is == null) {
            return;
        }

        try {
            is.close();
        }
        catch (IOException e) {
            throw new RuntimeException("IOException occurred. ", e);
        }
    }
    
	public static File compressImage(String filePath, String newFilePath, int targetWidth, int targetHeight) {
		
//		Base64.encodeToString(b, Base64.DEFAULT);
		if(!FileUtil.isFileExist(filePath)) {
			return null;
		}
		
		FileOutputStream fos = null;
		
		try {

			String imageExtension = FileUtil.getFileExtension(filePath).toLowerCase();
			CompressFormat cf = null;
			
			if(imageExtension.equals("png")) {
				cf = CompressFormat.PNG;
			}
			else if(imageExtension.equals("jpg")) {
				cf = CompressFormat.JPEG;
			}
			else{
				
				if(Build.VERSION.SDK_INT >= 14) {
					cf = CompressFormat.WEBP;
				}
				else{
					cf = CompressFormat.JPEG;
				}
			}
			
			Bitmap bm = BitmapUtil.fileToCompressBitmap(filePath, targetWidth, targetHeight);
			
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			bm.compress(cf, 60, baos);
			byte[] b = baos.toByteArray();
			
			File newFile = new File(newFilePath);

			if(!newFile.exists()) {
				
				if(!newFile.getParentFile().exists()) {
					
		            if (!newFile.getParentFile().mkdirs()) {
		            	Log.e(LOG_TAG + "compressImage:", "cache目录生成失败");
		            	return null;
		            }
		        }
				
				newFile.createNewFile();
			}
			
	        fos = new FileOutputStream(newFile);
            fos.write(b);
            Log.d(LOG_TAG + "compressImage:", "文件保存成功");
            
            fos.flush();
            fos.close();
            
            return newFile;
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		finally {
			
            if (fos != null) {
            	
                try {
                    fos.close();
                }
                catch (IOException e) {
                    throw new RuntimeException("IOException occurred. ", e);
                }
            }
        }
		return null;
	}
    
	/**
	 * 添加到图库
	 */
	public static void galleryAddPic(Context context, String path) {
		Intent mediaScanIntent = new Intent(
				Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
		File f = new File(path);
		Uri contentUri = Uri.fromFile(f);
		mediaScanIntent.setData(contentUri);
		context.sendBroadcast(mediaScanIntent);
	}

	/**
	 * 获取保存图片的目录
	 * 
	 * @return
	 */
	public static File getAlbumDir() {
		File dir = new File(
				Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
				getAlbumName());
		if (!dir.exists()) {
			dir.mkdirs();
		}
		return dir;
	}
	
	/**
	 * 获取保存 隐患检查的图片文件夹名称
	 * 
	 * @return
	 */
	public static String getAlbumName() {
		return "sccp.frame";
	}
}