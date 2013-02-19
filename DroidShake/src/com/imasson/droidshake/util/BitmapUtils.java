package com.imasson.droidshake.util;

import java.io.InputStream;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

/**
 * <p>���ڽ���λͼ����{@link Bitmap}�ı���롢ͼ��Ķ�ȡ�ͱ���Ȳ����Ĺ����ࡣ</p>
 * <p>Ŀǰ֧�ֵ��������£�</p>
 * <ul>
 * <li>֧�ִ��ļ����ֽ������{@link InputStream}�ж�ȡͼ��</li>
 * <li>֧�ָ��ݿ�߻���������������λͼ�����ɴ�С</li>
 * <li>�ṩ�����ڶ���λͼ��ߵķ���</li>
 * </ul>
 * <p>�ù������ѶԸ��ֿ��ܳ��ֵ��쳣���˷�װ�ͱ����������߽��������Ϊnullʱ������</p>
 * 
 * @version 1.0 ��������Bitmap��ȡ�ͱ��淽ʽ�Ĺ��߷���
 */
public class BitmapUtils {
	private static final String TAG = "BitmapUtil";
	
	/**
	 * Ĭ�ϵ�λͼͼ������������������� (2^19)��Լ����720*720
	 */
	public static final int DEFALUT_BITMAP_MAX_PIXELS = 524288;
	
	public static final int UNCONSTRAINED = -1;
	
	
	/**
     * ��ָ�����ļ��л�ȡλͼͼ��
     * @param filePath ͼ���ļ�������·��
     * @return ��ԭͼ��С��ͬ��λͼͼ��
     */
	public static Bitmap getBitmap(String filePath) {
		if (filePath == null) {
			Log.w(TAG, "Argument 'filePath' is null at getBitmap(String)");
			return null;
		}
		
		Bitmap retBitmap = null;
		try {
			retBitmap = BitmapFactory.decodeFile(filePath);
		} catch (OutOfMemoryError e) {
			Log.w(TAG, "OutOfMemoryError at getBitmap(String)", e);
			Log.w(TAG, "    filePath: " + filePath);
		} catch (Exception e) {
			Log.w(TAG, "Exception at getBitmap(String)", e);
			Log.w(TAG, "    filePath: " + filePath);
		}
		return retBitmap;
	}
	
	/**
	 * ��ָ�����ļ��л�ȡλͼͼ��
	 * @param filePath ͼ���ļ�������·��
	 * @param maxWidth �����
	 * @param maxHeight ���߶�
	 * @return ����ָ����λͼ����Ⱥ͸߶�����λͼ
	 */
	public static Bitmap getBitmap(String filePath, int maxWidth, int maxHeight) {
		if (filePath == null) {
			Log.w(TAG, "Argument 'filePath' is null at getBitmap(String, int, int)");
			return null;
		}
		if (maxWidth <= 0) {
    		Log.w(TAG, "Argument 'maxWidth' <= 0 at getBitmap(String, int, int)");
    		return null;
    	}
    	if (maxHeight <= 0) {
    		Log.w(TAG, "Argument 'maxHeight' <= 0 at getBitmap(String, int, int)");
    		return null;
    	}
		
		Bitmap retBitmap = null;
		
		try {
			// ȡͼ��sampleSize�ĳ�ʼֵ
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			BitmapFactory.decodeFile(filePath, options);
			if (options.mCancel || options.outWidth == -1 || options.outHeight == -1) {
            	Log.w(TAG, "Error on decode bounds at getBitmap(String, int, int)");
                return null;
            }
			
			// ��ȱ�
			int ratioWidth = (int)((double)options.outWidth / maxWidth + 0.5);
			// �߶ȱ�
			int ratioHeight = (int)((double)options.outHeight / maxHeight + 0.5);
			
			int max = ratioHeight > ratioWidth ? ratioHeight : ratioWidth;
			int sampleSize = max <= 1 ? 1 : max;
			
			// ͼƬ���صĿ�͸�ȡԭ����1/sampleSize
			options.inSampleSize = sampleSize;
			options.inJustDecodeBounds = false;
		
			retBitmap = BitmapFactory.decodeFile(filePath, options);
		} catch (OutOfMemoryError e) {
			Log.w(TAG, "OutOfMemoryError at getBitmap(String, int, int)", e);
			Log.w(TAG, "    filePath: " + filePath);
		} catch (Exception e) {
			Log.w(TAG, "Exception at getBitmap(String, int, int)", e);
			Log.w(TAG, "    filePath: " + filePath);
		}
	
		return retBitmap;
	}
	
	/**
	 * ��ָ�����ļ��л�ȡλͼͼ��
	 * @param filePath ͼ���ļ�������·��
	 * @param maxNumOfPixels ͼ������������������ԭͼ������ֵ�����Զ���С���ͼ��
     * @return ����ָ����������������ɵ�λͼ
	 */
	public static Bitmap getBitmap(String filePath, int maxNumOfPixels) {
		if (filePath == null) {
			Log.w(TAG, "Argument 'filePath' is null at getBitmap(String, int)");
			return null;
		}
    	if (maxNumOfPixels <= 0) {
    		Log.w(TAG, "Argument 'maxNumOfPixels' <= 0 at getBitmap(String, int)");
    		return null;
    	}
		
		try {
			// ȡͼ��sampleSize�ĳ�ʼֵ
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(filePath, options);
            if (options.mCancel || options.outWidth == -1 || options.outHeight == -1) {
            	Log.w(TAG, "Error on decode bounds at getBitmap(String, int)");
                return null;
            }
            
            // ͼƬ���صĿ�͸�ȡԭ����1/sampleSize
            options.inSampleSize = computeSampleSize(options, UNCONSTRAINED, maxNumOfPixels);
            
            options.inJustDecodeBounds = false;
            options.inDither = false;
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            return BitmapFactory.decodeFile(filePath, options);
            
        } catch (OutOfMemoryError e) {
			Log.w(TAG, "OutOfMemoryError at getBitmap(String, int)", e);
			Log.w(TAG, "    filePath: " + filePath);
		} catch (Exception e) {
			Log.w(TAG, "Exception at getBitmap(String, int)", e);
			Log.w(TAG, "    filePath: " + filePath);
		}
        return null;
	}
	
	
	/**
	 * ���ֽ������л�ȡλͼͼ��
	 * @param imageBytes λͼ���ֽ�����
	 * @return ��ԭͼ��С��ͬ��λͼͼ��
	 */
	public static Bitmap getBitmap(byte[] imageBytes) {
		if (imageBytes == null || imageBytes.length == 0) {
			Log.w(TAG, "Argument 'imageBytes' is null or empty at getBitmap(byte[])");
			return null;
		}
		
		Bitmap retBitmap = null;
		try {
			retBitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
		} catch (OutOfMemoryError e) {
			Log.w(TAG, "OutOfMemoryError at getBitmap(byte[])", e);
		} catch (Exception e) {
			Log.w(TAG, "Exception at getBitmap(byte[])", e);
		}
		return retBitmap;
	}
	
	/**
     * ���ֽ������л�ȡλͼͼ��
     * @param imageBytes λͼ���ֽ�����
     * @param maxWidth �����
     * @param maxHeight ���߶�
     * @return ����ָ����λͼ����Ⱥ͸߶�����λͼ
     */
    public static Bitmap getBitmap(byte[] imageBytes, int maxWidth, int maxHeight) {
    	if (imageBytes == null || imageBytes.length == 0) {
			Log.w(TAG, "Argument 'imageBytes' is null or empty at getBitmap(byte[], int, int)");
			return null;
		}
    	if (maxWidth <= 0) {
    		Log.w(TAG, "Argument 'maxWidth' <= 0 at getBitmap(byte[], int, int)");
    		return null;
    	}
    	if (maxHeight <= 0) {
    		Log.w(TAG, "Argument 'maxHeight' <= 0 at getBitmap(byte[], int, int)");
    		return null;
    	}
    	
        Bitmap retBitmap = null;
        
        try {
        	// ȡͼ��sampleSize�ĳ�ʼֵ
        	BitmapFactory.Options options = new BitmapFactory.Options();
	        options.inJustDecodeBounds = true;
	        BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length, options);
	        if (options.mCancel || options.outWidth == -1 || options.outHeight == -1) {
            	Log.w(TAG, "Error on decode bounds at getBitmap(byte[], int, int)");
                return null;
            }
	        
	        // ��ȱ�
	        int ratioWidth = (int)((double)options.outWidth / maxWidth + 0.5);
	        // �߶ȱ�
	        int ratioHeight = (int)((double)options.outHeight / maxHeight + 0.5);
	        
	        int max = ratioHeight > ratioWidth ? ratioHeight : ratioWidth;
	        
	        int sampleSize = 1;
	        if (max <= 1) {
	            sampleSize = 1;
	        } else {
	            sampleSize = max;
	        }
	        
	        // ͼƬ���صĿ�͸�ȡԭ����1/sampleSize
	        options.inSampleSize = sampleSize;
	        options.inJustDecodeBounds = false;
        
            retBitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length, options);
        } catch (OutOfMemoryError e) {
			Log.w(TAG, "OutOfMemoryError at getBitmap(byte[], int, int)", e);
		} catch (Exception e) {
			Log.w(TAG, "Exception at getBitmap(byte[], int, int)", e);
		}
    
        return retBitmap;
    }
    
    /**
     * ���ֽ������л�ȡλͼͼ��
     * @param imageBytes λͼ���ֽ�����
     * @param maxNumOfPixels ͼ������������������ԭͼ������ֵ�����Զ���С���ͼ��
     * @return ����ָ����������������ɵ�λͼ
     */
    public static Bitmap getBitmap(byte[] imageBytes, int maxNumOfPixels) {
    	if (imageBytes == null || imageBytes.length == 0) {
			Log.w(TAG, "Argument 'imageBytes' is null or empty at getBitmap(byte[], int)");
			return null;
		}
    	if (maxNumOfPixels <= 0) {
    		Log.w(TAG, "Argument 'maxNumOfPixels' <= 0 at getBitmap(byte[], int)");
    		return null;
    	}
    	
        try {
        	// ȡͼ��sampleSize�ĳ�ʼֵ
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length, options);
            if (options.mCancel || options.outWidth == -1 || options.outHeight == -1) {
            	Log.w(TAG, "Error on decode bounds at getBitmap(byte[], int)");
                return null;
            }
            
            // ͼƬ���صĿ�͸�ȡԭ����1/sampleSize
            options.inSampleSize = computeSampleSize(options, UNCONSTRAINED, maxNumOfPixels);
            
            options.inJustDecodeBounds = false;
            options.inDither = false;
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length, options);
            
        } catch (OutOfMemoryError e) {
			Log.w(TAG, "OutOfMemoryError at getBitmap(byte[], int)", e);
		} catch (Exception e) {
			Log.w(TAG, "Exception at getBitmap(byte[], int)", e);
		}
        return null;
    }
    
    
    /**
     * �������������л�ȡλͼͼ��
     * @param is ͼ���ļ�������������
     * @return ��ԭͼ��С��ͬ��λͼͼ��
     */
    public static Bitmap getBitmap(InputStream is) {
    	if (is == null) {
			Log.w(TAG, "Argument 'is' is null at getBitmap(InputStream)");
			return null;
		}
    	
    	Bitmap retBitmap = null;
		try {
			retBitmap = BitmapFactory.decodeStream(is);
		} catch (OutOfMemoryError e) {
			Log.w(TAG, "OutOfMemoryError at getBitmap(InputStream)", e);
		} catch (Exception e) {
			Log.w(TAG, "Exception at getBitmap(InputStream)", e);
		}
		return retBitmap;
    }
    
    /**
	 * �������������л�ȡλͼͼ��
	 * @param is ͼ���ļ�������������
	 * @param maxWidth �����
	 * @param maxHeight ���߶�
	 * @return ����ָ����λͼ����Ⱥ͸߶�����λͼ
	 */
    public static Bitmap getBitmap(InputStream is, int maxWidth, int maxHeight) {
    	if (is == null) {
			Log.w(TAG, "Argument 'is' is null at getBitmap(InputStream, int, int)");
			return null;
		}
    	if (maxWidth <= 0) {
    		Log.w(TAG, "Argument 'maxWidth' <= 0 at getBitmap(InputStream, int, int)");
    		return null;
    	}
    	if (maxHeight <= 0) {
    		Log.w(TAG, "Argument 'maxHeight' <= 0 at getBitmap(InputStream, int, int)");
    		return null;
    	}
    	
    	Bitmap retBitmap = null;
        
        try {
        	// ȡͼ��sampleSize�ĳ�ʼֵ
        	BitmapFactory.Options options = new BitmapFactory.Options();
	        options.inJustDecodeBounds = true;
	        BitmapFactory.decodeStream(is, null, options);
	        if (options.mCancel || options.outWidth == -1 || options.outHeight == -1) {
            	Log.w(TAG, "Error on decode bounds at getBitmap(InputStream, int, int)");
                return null;
            }
	        
	        // ��ȱ�
	        int ratioWidth = (int)((double)options.outWidth / maxWidth + 0.5);
	        // �߶ȱ�
	        int ratioHeight = (int)((double)options.outHeight / maxHeight + 0.5);
	        
	        int max = ratioHeight > ratioWidth ? ratioHeight : ratioWidth;
	        
	        int sampleSize = 1;
	        if (max <= 1) {
	            sampleSize = 1;
	        } else {
	            sampleSize = max;
	        }
	        
	        // ͼƬ���صĿ�͸�ȡԭ����1/sampleSize
	        options.inSampleSize = sampleSize;
	        options.inJustDecodeBounds = false;
	        
            retBitmap = BitmapFactory.decodeStream(is, null, options);
        } catch (OutOfMemoryError e) {
			Log.w(TAG, "OutOfMemoryError at getBitmap(InputStream, int, int)", e);
		} catch (Exception e) {
			Log.w(TAG, "Exception at getBitmap(InputStream, int, int)", e);
		}
    
        return retBitmap;
    }
    
    /**
     * �������������л�ȡλͼͼ��
     * @param is ͼ���ļ�������������
     * @param maxNumOfPixels ͼ������������������ԭͼ������ֵ�����Զ���С���ͼ��
     * @return ����ָ����������������ɵ�λͼ
     */
    public static Bitmap getBitmap(InputStream is, int maxNumOfPixels) {
    	if (is == null) {
			Log.w(TAG, "Argument 'is' is null at getBitmap(InputStream, int)");
			return null;
		}
    	if (maxNumOfPixels <= 0) {
    		Log.w(TAG, "Argument 'maxNumOfPixels' <= 0 at getBitmap(InputStream, int)");
    		return null;
    	}
    	
    	try {
    		// ȡͼ��sampleSize�ĳ�ʼֵ
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(is, null, options);
            if (options.mCancel || options.outWidth == -1 || options.outHeight == -1) {
            	Log.w(TAG, "Error on decode bounds at getBitmap(InputStream, int)");
                return null;
            }
            
            // ͼƬ���صĿ�͸�ȡԭ����1/sampleSize
            options.inSampleSize = computeSampleSize(options, UNCONSTRAINED, maxNumOfPixels);
            
            options.inJustDecodeBounds = false;
            options.inDither = false;
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            return BitmapFactory.decodeStream(is, null, options);
            
        } catch (OutOfMemoryError e) {
			Log.w(TAG, "OutOfMemoryError at getBitmap(InputStream, int)", e);
		} catch (Exception e) {
			Log.w(TAG, "Exception at getBitmap(InputStream, int)", e);
		}
        return null;
    }
    
    
    /**
     * ����ͼ��Ĵ�С��ʹ��Ĭ��ͼ�����ƣ���ָ���䳤��������ز���
     * {@link #DEFALUT_BITMAP_MAX_PIXELS}���������Ҫ���ŵ�ͼ��ı���
     * @param width ͼ��Ŀ��
     * @param height ͼ��ĸ߶�
     * @return ���ű�����N��֮һ��
     */
    public static int computeSampleSize(int width, int height) {
    	return computeSampleSize(width, height, 
    			UNCONSTRAINED, DEFALUT_BITMAP_MAX_PIXELS);
    }
    
    /**
     * ����ͼ������Ժ��������ֵ����ȷ����Ҫ���ŵ�ͼ��ı���
     * @param options ͼ����������ԣ���DecodeBounds�Ķ���
     * @param minSideLength ��̱߳���ʹ��{@link #UNCONSTRAINED}��ʾ��ָ��
     * @param maxNumOfPixels �������ֵ
     * @return ���ű�����N��֮һ��
     */
    public static int computeSampleSize(BitmapFactory.Options options,
    		int minSideLength, int maxNumOfPixels) {
    	if (options == null) {
    		Log.w(TAG, "Argument 'options' is null " +
    				"at computeSampleSize(BitmapFactory.Options, int, int)");
    		return 1;
    	}
    	return computeSampleSize(options.outWidth, options.outHeight, 
    			minSideLength, maxNumOfPixels);
    }
    
    /**
     * <p>����ͼ������Ժ��������ֵ����ȷ����Ҫ���ŵ�ͼ��ı���</p>
     * <p>˵�����öδ����ϵͳCameraԴ������ȡ</p>
     * @param width ͼ��Ŀ��
     * @param height ͼ��ĸ߶�
     * @param minSideLength ��̱߳���ʹ��{@link #UNCONSTRAINED}��ʾ��ָ��
     * @param maxNumOfPixels �������ֵ
     * @return ���ű�����N��֮һ��
     */
    public static int computeSampleSize(int width, int height,
            int minSideLength, int maxNumOfPixels) {
    	if (width <= 0) {
    		Log.w(TAG, "Argument 'width' <= 0 at computeSampleSize()");
    		return 1;
    	}
    	if (height <= 0) {
    		Log.w(TAG, "Argument 'height' <= 0 at computeSampleSize()");
    		return 1;
    	}
    	if (maxNumOfPixels <= 0) {
    		Log.w(TAG, "Argument 'maxNumOfPixels' <= 0 at computeSampleSize()");
    		return 1;
    	}
    	
        int initialSize = computeInitialSampleSize(width, height, 
        		minSideLength, maxNumOfPixels);

        int roundedSize;
        if (initialSize <= 8) {
            roundedSize = 1;
            while (roundedSize < initialSize) {
                roundedSize <<= 1;
            }
        } else {
            roundedSize = (initialSize + 7) / 8 * 8;
        }

        return roundedSize;
    }
    
    private static int computeInitialSampleSize(int width, int height, 
            int minSideLength, int maxNumOfPixels) {
        double w = width;
        double h = height;

        int lowerBound = (maxNumOfPixels == UNCONSTRAINED) ? 1 :
                (int) Math.ceil(Math.sqrt(w * h / maxNumOfPixels));
        int upperBound = (minSideLength == UNCONSTRAINED) ? 128 :
                (int) Math.min(Math.floor(w / minSideLength),
                Math.floor(h / minSideLength));

        if (upperBound < lowerBound) {
            // return the larger one when there is no overlapping zone.
            return lowerBound;
        }

        if ((maxNumOfPixels == UNCONSTRAINED) &&
                (minSideLength == UNCONSTRAINED)) {
            return 1;
        } else if (minSideLength == UNCONSTRAINED) {
            return lowerBound;
        } else {
            return upperBound;
        }
    }
    
    
    /**
     * ����ָ����ͼ���ļ�������ͼ���ļ��ĳߴ�
     * @param filePath ͼ���ļ���·��
     * @param rect ��ſ�Ⱥ͸߶����ݵĶ��󣬲���Ϊ��
     */
    public static void measureImageSize(String filePath, android.graphics.Rect rect) {
        if (filePath == null || filePath.length() == 0) {
            Log.w(TAG, "Argument 'path' is empty at measureImageSize(String, Rect)!");
            return;
        }
        if (rect == null) {
            Log.w(TAG, "Argument 'rect' is null at measureImageSize(String, Rect)!");
            return;
        }
        
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        try {
            BitmapFactory.decodeFile(filePath, options);
        } catch (Exception e) {
            Log.w(TAG, "Exception at measureImageSize(String, Rect)", e);
            Log.w(TAG, "    filePath: " + filePath);
        }

        rect.set(0, 0, options.outWidth, options.outHeight);
    }
    
    /**
     * ����ָ����ͼ�����ݣ�����ͼ��ĳߴ�
     * @param imageBytes ͼ������ݵ��ֽ�����
     * @param rect ��ſ�Ⱥ͸߶����ݵĶ��󣬲���Ϊ��
     */
    public static void measureImageSize(byte[] imageBytes, android.graphics.Rect rect) {
        if (imageBytes == null || imageBytes.length == 0) {
            Log.w(TAG, "Argument 'bytes' is empty at measureImageSize(byte[], Rect)!");
            return;
        }
        if (rect == null) {
            Log.w(TAG, "Argument 'rect' is null at measureImageSize(byte[], Rect)!");
            return;
        }
        
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        try {
            BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length, options);
        } catch (Exception e) {
        	Log.w(TAG, "Exception at measureImageSize(byte[], Rect)", e);
        }
        
        rect.set(0, 0, options.outWidth, options.outHeight);
    }
    
    /**
     * ����ָ����ͼ�����ݣ�����ͼ���ļ��ĳߴ�
     * @param is ͼ���ļ�������������
     * @param rect ��ſ�Ⱥ͸߶����ݵĶ��󣬲���Ϊ��
     */
    public static void measureImageSize(InputStream is, android.graphics.Rect rect) {
        if (is == null) {
            Log.w(TAG, "Argument 'is' is null at measureImageSize(InputStream, Rect)!");
            return;
        }
        if (rect == null) {
            Log.w(TAG, "Argument 'rect' is null at measureImageSize(InputStream, Rect)!");
            return;
        }
        
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        try {
            BitmapFactory.decodeStream(is, null, options);
        } catch (Exception e) {
        	Log.w(TAG, "Exception at measureImageSize(InputStream, Rect)", e);
        }

        rect.set(0, 0, options.outWidth, options.outHeight);
    }
}
