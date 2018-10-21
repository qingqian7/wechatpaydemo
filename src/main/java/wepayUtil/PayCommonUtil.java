package wepayUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;

public class PayCommonUtil {
	public static boolean isTenPaySign(String characterEncoding,SortedMap<String,Object> param,String API_KEY) {
		StringBuffer sb = new StringBuffer();
		Set s = param.entrySet();//将map转换成可以迭代的set
		Iterator it = s.iterator();
		while(it.hasNext()) {
			Map.Entry entry = (Map.Entry) it.next();
			String k = (String) entry.getKey();
			String v = (String) entry.getValue();
			if(!"sign".equals(k) && null != v && !"".equals(v)) {
				sb.append(k + "=" + v + "&");
			}
		}
		sb.append("key=" + API_KEY);
		String mysign = MD5.MD5Encode(sb.toString(),characterEncoding);
		String tenpaySign = ((String) param.get("sign")).toLowerCase();
		return mysign.equals(tenpaySign);
	}
	
	public static String createSign(String characterEncoding,SortedMap<String,Object> param,String API_KEY) {
		StringBuffer sb = new StringBuffer();
		Set s = param.entrySet();
		Iterator it = s.iterator();
		while(it.hasNext()) {
			Map.Entry entry = (Entry) it.next();
			String k = (String) entry.getKey();
			String v = (String) entry.getValue();
			if(!"sign".equals(k) && null != v && !"".equals(v)) {
				sb.append(k + "=" + v + "&");
			}
		}
		sb.append("key=" + API_KEY);
		String sign = MD5.MD5Encode(sb.toString(), characterEncoding);
		return sign;
	}
	/*
	 * 按照键值的ascii顺序排序
	 */
	public static String createLinkString(Map<String,Object> param) {
		List<String> keys = new ArrayList<String>(param.keySet());
		Collections.sort(keys);
		String prestr = "";
		for(int i=0;i<keys.size();i++) {
			String k = keys.get(i);
			String v = (String) param.get(k);
			if(i == keys.size() - 1)
				prestr = prestr + k + "=" + v;
			else
				prestr = prestr + k + "=" + v + "&";
		}
		return prestr;
	} 
	public static String getRequestXml(SortedMap<String, Object> parameters) {  
		StringBuffer sb = new StringBuffer();  
		sb.append("<xml>");  
		Set es = parameters.entrySet();  
		Iterator it = es.iterator();  
		while (it.hasNext()) {  
			Map.Entry entry = (Map.Entry) it.next();  
			String k = entry.getKey().toString();  
			String v = entry.getValue().toString();   
			if ("attach".equalsIgnoreCase(k) || "body".equalsIgnoreCase(k) || "sign".equalsIgnoreCase(k)) {  
				sb.append("<" + k + ">"  + v + "</" + k + ">");  
			} else {  
				sb.append("<" + k + ">" + v + "</" + k + ">");  
			}  
		}  
		sb.append("</xml>");  
		return sb.toString();  
	}  
	/** 
	 * 取出一个指定长度大小的随机正整数. 
	 *  
	 * @param length 
	 *            int 设定所取出随机数的长度。length小于11 
	 * @return int 返回生成的随机数。 
	 */  
	public static int buildRandom(int length) {  
		int num = 1;  
		double random = Math.random();  
		if (random < 0.1) {  
			random = random + 0.1;  
		}  
		for (int i = 0; i < length; i++) {  
			num = num * 10;  
		}  
		return (int) ((random * num));  
	}  
	public static String getCurrTime() {  
		Date now = new Date();  
		SimpleDateFormat outFormat = new SimpleDateFormat("yyyyMMddHHmmss");  
		String s = outFormat.format(now);  
		return s;  
	}
 
	public static boolean verify(String text, String sign, String key, String input_charset) {
		text = text + key;     
		String mysign =MD5.MD5Encode(text, input_charset).toUpperCase();  
		System.out.println(mysign);	System.out.println(mysign);	System.out.println(mysign);	System.out.println(mysign);
		if (mysign.equals(sign)) {     
			return true;     
		} else {     
			return false;     
		}     
	}  


}
