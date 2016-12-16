package com.sccp.library.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

public class StorageUtil {

	private static final String LOG_TAG = new Object() {  
        public String getClassName() {  
            String clazzName = this.getClass().getName();  
            return clazzName.substring(clazzName.lastIndexOf('.') + 1, clazzName.lastIndexOf('$'));  
        }  
    }.getClassName() + ":";
	
    /**
     * Check the SD card
     *
     * @return 是否存在SDCard
     */
    public static boolean checkSDCardAvailable() {
        return android.os.Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED);
    }

    /**
     * Check if the file is exists
     *
     * @param filePath 文件路径
     * @param fileName 文件名
     * @return 是否存在文件
     */
    public static boolean isFileExistsInSDCard(String filePath, String fileName) {
        boolean flag = false;
        if (checkSDCardAvailable()) {
            File file = new File(filePath, fileName);
            if (file.exists()) {
                flag = true;
            }
        }
        return flag;
    }

    /**
     * Write file to SD card
     *
     * @param filePath 文件路径
     * @param filename 文件名
     * @param content  内容
     * @return 是否保存成功
     * @throws Exception
     */
    public static boolean saveFileToSDCard(String filePath, String filename,
                                           String content) throws Exception {
        boolean flag = false;
        if (checkSDCardAvailable()) {
            File dir = new File(filePath);
            if (!dir.exists()) {
                dir.mkdir();
            }
            File file = new File(filePath, filename);
            FileOutputStream outStream = new FileOutputStream(file);
            outStream.write(content.getBytes());
            outStream.close();
            flag = true;
        }
        return flag;
    }

    /**
     * Read file as stream from SD card
     *
     * @param fileName String PATH =
     *                 Environment.getExternalStorageDirectory().getAbsolutePath() +
     *                 "/dirName";
     * @return Byte数组
     */
    public static byte[] readFileFromSDCard(String filePath, String fileName) {
        byte[] buffer = null;
        try {
            if (checkSDCardAvailable()) {
                String filePaht = filePath + "/" + fileName;
                FileInputStream fin = new FileInputStream(filePaht);
                int length = fin.available();
                buffer = new byte[length];
                fin.read(buffer);
                fin.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return buffer;
    }

    /**
     * Delete file
     *
     * @param filePath 文件路径
     * @param fileName filePath =
     *                 android.os.Environment.getExternalStorageDirectory().getPath()
     * @return 是否删除成功
     */
    public static boolean deleteSDFile(String filePath, String fileName) {
        File file = new File(filePath + "/" + fileName);
        if (!file.exists() || file.isDirectory())
            return false;
        return file.delete();
    }
    
    /**
     * Get file cache path of application
     * @param context
     * @param subPath
     * @return
     */
    public static String getFileCachePath(Context context, String subPath) {
    	
    	if(context == null) {
    		Log.d(LOG_TAG + "getFileCachePath", "context is null");
    		return null;
    	}
    	
		if(checkSDCardAvailable()) {
			subPath = context.getExternalCacheDir().getAbsolutePath() + subPath;
		}
		else{
    		Log.d(LOG_TAG + "getFileCachePath", "sdcard is not available");
			subPath = context.getCacheDir().getAbsolutePath() + subPath;
		}

		Log.d(LOG_TAG + "getFileCachePath", "CachePath = " + subPath);
		return subPath;
	}
    
	public static String getFileStoragePath(Context context, String subPath) {

    	if(context == null) {
    		Log.d(LOG_TAG + "getFileStoragePath", "context is null");
    		return null;
    	}
    	
		if(checkSDCardAvailable()) {

			subPath = Environment.getExternalStorageDirectory() + subPath;
		}
		else{
    		Log.d(LOG_TAG + "getFileStoragePath", "sdcard is not available");
			subPath = context.getFilesDir() + subPath;
		}

		Log.i(LOG_TAG + "getFileStoragePath", "StoragePath = " + subPath);
		return subPath;
	}
}
