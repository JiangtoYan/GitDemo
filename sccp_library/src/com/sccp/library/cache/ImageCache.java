package com.sccp.library.cache;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.sccp.library.entity.CacheObject;
import com.sccp.library.util.FileUtil;
import com.sccp.library.util.ImageUtil;
import com.sccp.library.util.StringUtil;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

public class ImageCache extends FileCache<String, String> {

	private final static String TAG = "ImageCache:";
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

    private OnImageCacheCallbackListener            onImageCacheCallbackListener;
	/**
     * restore threads those getting data, to avoid multi threads get the data for same key so that to save network traffic
     **/
    private transient Map<String, GetImageFileThread> getImageFileThreadMap          = new HashMap<String, GetImageFileThread>();

    private boolean                              isOpenWaitingQueue     = true;
    /** getting data thread pool **/
    private ExecutorService                 threadPool;

    private Context                         context;
    private boolean                         isCheckNetwork                = true;
    private int                             allowedNetworkTypes           = ~0;

    private transient ConnectivityManager   connectivityManager;
    /**
     * key is image url, value is the newest view which waiting for image loaded, used when {@link #isOpenWaitingQueue}
     * is false
     **/
    private transient Map<String, View>          viewMap;
    /**
     * key is image url, value is view set those waiting for image loaded, used when {@link #isOpenWaitingQueue} is true
     **/
    private transient Map<String, HashSet<View>> viewSetMap;
    
    public static final int                 NETWORK_MOBILE                = 1 << 0;
    public static final int                 NETWORK_WIFI                  = 1 << 1;
    /** message what for get image successfully **/
    private static final int                     WHAT_GET_IMAGE_SUCCESS = 1;
    /** message what for get image failed **/
    private static final int                     WHAT_GET_IMAGE_FAILED  = 2;
    private transient Handler                    handler;
    
	public ImageCache(int maxSize) {
		
		super(maxSize);

        this.viewMap = new ConcurrentHashMap<String, View>();
        this.viewSetMap = new HashMap<String, HashSet<View>>();
        this.handler = new MyHandler();
//		if (threadPoolSize <= 0) {
//            throw new IllegalArgumentException("The threadPoolSize of cache must be greater than 0.");
//        }
        this.threadPool = Executors.newFixedThreadPool(8);
	}
	
	public CacheObject<String> getFromCache(String key) {
    	Log.d(TAG + "getFromCache", "IMAGE_URL:" + key);
        return super.get(key);
    }
	
	@Override
    public CacheObject<String> get(String key) {
		
        if (key == null) {
            return null;
        }
        
    	Log.d(TAG + "CacheObject<String> get", "IMAGE_URL:" + key);

        CacheObject<String> object = super.get(key);
        
        if (object == null) {

            GetImageFileThread getImageFileThread = getImageFile(key);
            
            // get data synchronous and wait for it
            if (getImageFileThread != null) {
            	
                try {
                	getImageFileThread.finishGetImageFileLock.await();
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            // recalculate hit rate
            object = super.get(key);
        }
        
        return object;
    }
	
    public boolean get(final String imageUrl, final View view) {
    	
    	Log.d(TAG + "boolean get", "IMAGE_URL:" + imageUrl);
    	
        if (onImageCacheCallbackListener != null) {
            onImageCacheCallbackListener.onPreGet(imageUrl, view);
        }

        if (StringUtil.isEmpty(imageUrl)) {
            if (onImageCacheCallbackListener != null) {
                onImageCacheCallbackListener.onGetNotInCache(imageUrl, view);
            }
            return false;
        }

        /**
         * if already in cache, call onImageCacheCallbackListener, else new thread to wait for it
         */
        CacheObject<String> object = getFromCache(imageUrl);
        
        if (object != null) {
        	
            String imagePath = object.getData();
        	
            if (!StringUtil.isEmpty(imagePath) && FileUtil.isFileExist(imagePath)) {
            	
                onGetSuccess(imageUrl, imagePath, view, true);
                return true;
            } else {
                remove(imageUrl);
            }
        }
        
        if (isOpenWaitingQueue) {
        	
            synchronized (viewSetMap) {
            	
                HashSet<View> viewSet = viewSetMap.get(imageUrl);
                
                if (viewSet == null) {
                    viewSet = new HashSet<View>();
                    viewSetMap.put(imageUrl, viewSet);
                }
                viewSet.add(view);
            }
        }
        else {
            viewMap.put(imageUrl, view);
        }

        if (onImageCacheCallbackListener != null) {
            onImageCacheCallbackListener.onGetNotInCache(imageUrl, view);
        }
        
        if (isExistGetImageFileThread(imageUrl)) {
            return false;
        }

        startGetImageThread(imageUrl);
        return false;
    }
	
    /**
     * Check if get data can proceed over the given network type.
     * 
     * @param networkType a constant from ConnectivityManager.TYPE_*.
     * @return one of the NETWORK_* constants
     *         <ul>
     *         <li>if {@link #getContext()} is null, return true</li>
     *         <li>if network is not avaliable, return false</li>
     *         <li>if {@link #getAllowedNetworkTypes()} is not match network, return false</li>
     *         </ul>
     */
    public boolean checkIsNetworkTypeAllowed() {
    	
        if (connectivityManager == null && context != null) {
            connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        }

        if (connectivityManager == null) {
            return true;
        }

        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && (allowedNetworkTypes == ~0 || (translateNetworkTypeToApiFlag(networkInfo.getType()) & allowedNetworkTypes) != 0);
    }

    /**
     * Translate a ConnectivityManager.TYPE_* constant to the corresponding PreloadDataCache.NETWORK_* bit flag.
     */
    private int translateNetworkTypeToApiFlag(int networkType) {
    	
        switch (networkType) {
            case ConnectivityManager.TYPE_MOBILE:
                return ImageCache.NETWORK_MOBILE;

            case ConnectivityManager.TYPE_WIFI:
                return ImageCache.NETWORK_WIFI;

            default:
                return 0;
        }
    }
    
    public void setOnImageCacheCallbackListener(OnImageCacheCallbackListener onImageCacheCallbackListener) {
        this.onImageCacheCallbackListener = onImageCacheCallbackListener;
    }
    
    /**
     * used when {@link #checkIsNetworkTypeAllowed()}
     * 
     * @param context
     */
    public void setContext(Context context) {
        this.context = context;
    }
    
    /**
     * delete file when remove
     */
    @Override
    public CacheObject<String> remove(String key) {
        CacheObject<String> o = super.remove(key);
        if (o != null) {
            //deleteFile(o.getData());
        }
        return o;
    }
    

    /**
     * start thread to wait for image get
     * 
     * @param imageUrl
     * @param urlList url list, if is null, not preload, else preload forward by
     *        {@link PreloadDataCache#preloadDataForward(Object, List, int)}, preload backward by
     *        {@link PreloadDataCache#preloadDataBackward(Object, List, int)}
     */
    private void startGetImageThread(final String imageUrl) {
    	
    	Log.d(TAG + "startGetImageThread", "IMAGE_URL:" + imageUrl);
        // wait for image be got success and send message
        threadPool.execute(new Runnable() {

            @Override
            public void run() {
            	
                try {
                    CacheObject<String> object = get(imageUrl);
                    String imagePath = (object == null ? null : object.getData());
                    
                    if (StringUtil.isEmpty(imagePath) || !FileUtil.isFileExist(imagePath)) {
                        // if image get fail, remove it
                        remove(imageUrl);
                        handler.sendMessage(handler.obtainMessage(WHAT_GET_IMAGE_FAILED, new MessageObject(imageUrl, imagePath)));
                    }
                    else {
                        handler.sendMessage(handler.obtainMessage(WHAT_GET_IMAGE_SUCCESS, new MessageObject(imageUrl, imagePath)));
                    }
                    
                } catch (OutOfMemoryError e) {
                    MessageObject msg = new MessageObject(imageUrl, null);
                    handler.sendMessage(handler.obtainMessage(WHAT_GET_IMAGE_FAILED, msg));
                }
            }
        });
    }
    
    private void onGetSuccess(String imageUrl, String imagePath, View view, boolean isInCache) {

    	Log.d(TAG + "onGetSuccess", "IMAGE_URL:" + imageUrl);
    	
        if (onImageCacheCallbackListener == null) {
            return;
        }

        try {
            onImageCacheCallbackListener.onGetSuccess(imageUrl, imagePath, view, isInCache);
        } catch (OutOfMemoryError e) {
            onImageCacheCallbackListener.onGetFailed(imageUrl, imagePath, view);
        }
    }
	
    private synchronized GetImageFileThread getImageFile(String key) {
    	
        Log.d(TAG + "GetImageFileThread", "IMAGE_URL:" + key);
    	
        if (containsKey(key) || (isCheckNetwork && !checkIsNetworkTypeAllowed())) {
            return null;
        }

        if (isExistGetImageFileThread(key)) {
            Log.d(TAG + "GetImageFileThread", "isExistGetImageFileThread:" + key);
            return getImageFileThreadMap.get(key);
        }

        GetImageFileThread getImageFileThread = new GetImageFileThread(key);
        getImageFileThreadMap.put(key, getImageFileThread);
        threadPool.execute(getImageFileThread);
        return getImageFileThread;

    }

    /**
     * whether there is a thread which is getting data for the specified key
     * 
     * @param key
     * @return
     */
    public synchronized boolean isExistGetImageFileThread(String key) {
        return getImageFileThreadMap.containsKey(key);
    }
    
    /**
     * the thread to get data
     */
    private class GetImageFileThread implements Runnable {

        private String imageUrl;

        /** get data and cache finish lock, it will be released then **/
        public CountDownLatch finishGetImageFileLock;

        /**
         * @param imageUrl
         * @param onGetImageFileListener
         */
        public GetImageFileThread(String imageUrl) {
        	
            this.imageUrl = imageUrl;
            finishGetImageFileLock = new CountDownLatch(1);
        }

        public void run() {

        	Log.d(TAG + "GetImageFileThread:run", "IMAGE_URL:" + imageUrl);
        	
            if (imageUrl != null) {
            	
            	String imagePath = ImageUtil.getImageFilePath(imageUrl, "/image", context);
                CacheObject<String> object = (StringUtil.isEmpty(imagePath) ? null : new CacheObject<String>(imagePath));
                
                if (object != null) {
                	Log.d(TAG + "run: object != null", "IMAGE_URL:" + imageUrl);
                    put(imageUrl, object);
                }
            }
            
            // get data success, release lock
            finishGetImageFileLock.countDown();

            if (getImageFileThreadMap != null && imageUrl != null) {
                getImageFileThreadMap.remove(imageUrl);
            }
        }
    }

    /**
     * callback interface when getting image
     */
    public interface OnImageCacheCallbackListener {

        /**
         * callback function before get image, run on ui thread
         * 
         * @param imageUrl imageUrl
         * @param view view need the image
         */
        void onPreGet(String imageUrl, View view);

        /**
         * callback function when get image but image not in cache, run on ui thread.<br/>
         * Will be called after {@link #onPreGet(String, View)}, before
         * {@link #onGetSuccess(String, String, View, boolean)} and
         * {@link #onGetFailed(String, String, View, FailedReason)}
         * 
         * @param imageUrl imageUrl
         * @param view view need the image
         */
        void onGetNotInCache(String imageUrl, View view);

        /**
         * callback function after get image successfully, run on ui thread
         * 
         * @param imageUrl imageUrl
         * @param imagePath image path
         * @param view view need the image
         * @param isInCache whether already in cache or got realtime
         */
        void onGetSuccess(String imageUrl, String imagePath, View view, boolean isInCache);

        /**
         * callback function after get image failed, run on ui thread
         * 
         * @param imageUrl imageUrl
         * @param imagePath image path
         * @param view view need the image
         * @param failedReason failed reason for get image
         */
        void onGetFailed(String imageUrl, String imagePath, View view);
    }
    
    private class MyHandler extends Handler {

        public void handleMessage(Message message) {
        	
            switch (message.what) {
            
                case WHAT_GET_IMAGE_SUCCESS:
                case WHAT_GET_IMAGE_FAILED:
                	
                    MessageObject object = (MessageObject)message.obj;
                    
                    if (object == null) {
                        break;
                    }

                    String imageUrl = object.imageUrl;
                    String imagePath = object.imagePath;
                    
                    Log.d(TAG + "handleMessage", "IMAGE_URL:" + imageUrl + " IMAGE_PATH:" + imagePath);
                    
                    if (onImageCacheCallbackListener != null) {
                    	
                        if (isOpenWaitingQueue) {
                        	
                            synchronized (viewSetMap) {
                            	
                                HashSet<View> viewSet = viewSetMap.get(imageUrl);
                                
                                if (viewSet != null) {
                                	
                                    for (View view : viewSet) {
                                        if (view != null) {
                                        	
                                            if (WHAT_GET_IMAGE_SUCCESS == message.what) {
                                                onGetSuccess(imageUrl, imagePath, view, false);
                                            } else {
                                                onImageCacheCallbackListener.onGetFailed(imageUrl, imagePath, view);
                                            }
                                        }
                                    }
                                }
                            }
                        } else {
                            View view = viewMap.get(imageUrl);
                            if (view != null) {
                                if (WHAT_GET_IMAGE_SUCCESS == message.what) {
                                    onGetSuccess(imageUrl, imagePath, view, false);
                                } else {
                                    onImageCacheCallbackListener.onGetFailed(imageUrl, imagePath, view);
                                }
                            }
                        }
                    }

                    if (isOpenWaitingQueue) {
                        synchronized (viewSetMap) {
                            //viewSetMap.remove(imageUrl);
                        }
                    } else {
                        //viewMap.remove(imageUrl);
                    }
                    break;
            }
        }
    }
    
    /**
     * message object
     * 
     * @author <a href="http://www.trinea.cn" target="_blank">Trinea</a> 2013-1-14
     */
    private class MessageObject {

        String       imageUrl;
        String       imagePath;

        public MessageObject(String imageUrl, String imagePath) {
            this.imageUrl = imageUrl;
            this.imagePath = imagePath;
        }
    }
    
    
    public void recycleResource() {
    	
    	if (isOpenWaitingQueue) {
        	
    		if(viewSetMap != null && viewSetMap.size() > 0) {
    			
    			Iterator<String> iterator = viewSetMap.keySet().iterator();
    			
    			while(iterator.hasNext()) {
    				
		            synchronized (viewSetMap) {
		            	
		                HashSet<View> viewSet = viewSetMap.get(iterator.next());
		                
		                if (viewSet != null) {
		                	
		                    for (View view : viewSet) {
		                    	
		                        if (view != null) {
		                        	Log.d(TAG + "recycleResource", "setImageBitmap(null)");
		                        	((ImageView)view).setImageBitmap(null);
		                        }
		                    }
		                }
		            }
    			}
    		}
    		
    		//viewSetMap = null;
        }
    	else {
        	
        	if(viewMap != null && viewMap.size() > 0) {
        		
        		Iterator<String> iterator = viewMap.keySet().iterator();
        		
        		while(iterator.hasNext()) {
        			
        			ImageView view = (ImageView) viewMap.get(iterator.next());
                    
                    if (view != null) {
                    	view.setImageBitmap(null);
                    }
        		}
        	}
        	
        	//viewMap = null;
        }
    }
}