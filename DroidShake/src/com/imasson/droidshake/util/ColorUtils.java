package com.imasson.droidshake.util;

import android.graphics.Color;

/**
 * <p>���ڽ�����ɫֵ����Ĺ�����</p>
 * 
 * @version 1.0 ����͸�����޸ĺ͵�����ɫ���ȵĹ��߷���
 */
public class ColorUtils {
	
	/**
	 * ������ɫֵ��͸����
	 * @param color ԭ������ɫֵ
	 * @param transparent ͸����<code>[0,255]</code>��
	 * 0��ʾ��ȫ͸����255��ʾ��ȫ��͸��
	 * @return ����͸���Ⱥ����ɫֵ
	 */
	public static int alterColorTransparnet(int color, int transparent) {
		int transValue = (transparent & 0x000000ff) << 24;
		int ret = color & 0x00ffffff | transValue;
		return ret;
	}
	
	/**
	 * ������ɫֵ��͸����
	 * @param color ԭ������ɫֵ
	 * @param ratio ͸���ȱ���<code>[0.0,1.0]</code>��
	 * 0��ʾ��ȫ͸����1��ʾ��ȫ��͸��
	 * @return ����͸���Ⱥ����ɫֵ
	 */
	public static int alterColorTransparnet(int color, float ratio) {
		int transValue = (int)(0x000000ff * ratio) << 24;
		int ret = color & 0x00ffffff | transValue;
		return ret;
	}
	
	/**
	 * ������ɫֵ�����ȣ�����Ϊ��ֵ��Ϊ��������
	 * @param color ԭ������ɫֵ
	 * @param delta Ҫ���ӵ�����ֵ[-255,255]
	 * @return �޸ĺ����ɫֵ
	 */
	public static int addColorBrightness(int color, int delta) {
		int a = Color.alpha(color);
		int r = Color.red(color);
		int g = Color.green(color);
		int b = Color.blue(color);
		
		if (r >= 0x000000ff - delta) r = 255;
		else if (r <= 0 - delta) r = 0;
		else r += delta;
		
		if (g >= 0x000000ff - delta) g = 255;
		else if (g <= 0 - delta) r = 0;
		else g += delta;
		
		if (b >= 0x000000ff - delta) b = 255;
		else if (b <= 0 - delta) r = 0;
		else b += delta;
		
		return Color.argb(a, r, g, b);
	}
}
