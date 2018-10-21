package wepaycontroller;


import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Map;
import java.util.Random;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;

import wepayUtil.HttpUtil;
import wepayUtil.PayCommonUtil;
import wepayUtil.XMLUtil;

@Controller
@RequestMapping("order")
public class PayController {
	private static String APPID = "";
	private static String MCH_ID = "";
	private static String secretkey = "";
	/*
	 * 购买函数
	 */
	@ResponseBody
	@RequestMapping("returnparam")
	public void doOrder(HttpServletRequest request,HttpServletResponse response) throws Exception {
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		//得到openid（微信用户唯一的openid）
				String openid = request.getParameter("openid");
				//得到价钱（自定义）
				int fee = 0;
				if (null != request.getParameter("price")) {
					fee = Integer.parseInt(request.getParameter("price").toString());
				}
				//得到商品的ID（自定义）
				String goodsid=request.getParameter("goodsid");
				//订单标题（自定义）
				String title = request.getParameter("title");
				//时间戳
				String times = System.currentTimeMillis() + "";
				
				//订单编号（自定义 这里以时间戳+随机数）
				Random random = new Random();
				String did = times+random.nextInt(1000);
		 
				SortedMap<String, Object> packageParams = (SortedMap<String, Object>) new TreeMap<String, Object>();
				packageParams.put("appid", APPID);//微信小程序ID
				packageParams.put("mch_id", MCH_ID);//商户ID
				packageParams.put("nonce_str", times);//随机字符串（32位以内） 这里使用时间戳
				packageParams.put("body", title);//支付主体名称 自定义
				packageParams.put("out_trade_no", did+goodsid);//编号 自定义以时间戳+随机数+商品ID
				packageParams.put("total_fee", fee);//价格 自定义
				//packageParams.put("spbill_create_ip", remoteAddr);
				packageParams.put("notify_url", "http://你的IP地址/order/buy.action");//支付返回地址要外网访问的到， localhost不行，调用下面buy方法。
				packageParams.put("trade_type", "JSAPI");//这个api有，固定的
				packageParams.put("openid", openid);//用户的openid 可以要 可以不要
				
				String sign = PayCommonUtil.createSign("UTF-8", packageParams, secretkey); //密匙是自己在微信商户设置的
				packageParams.put("sign", sign);
				//转换成xml
				String requestxml = PayCommonUtil.getRequestXml(packageParams);
				//得到含有prepay_id的xml
				String resxml = HttpUtil.postData("https://api.mch.weixin.qq.com/pay/unifiedorder", requestxml);
				//解析xml存入map
				Map map = XMLUtil.doXMLParse(resxml);
				String prepay_id = (String) map.get("prepay_id");
				SortedMap<String,Object> packagep = new TreeMap<String,Object>();
				packagep.put("appid",APPID);//自己手动改成小程序的appid
				packagep.put("nonceStr",times);//时间戳
				packagep.put("package", "prepay_id="+prepay_id);//必须把package写成 "prepay_id="+prepay_id这种形式
				packagep.put("signType","MD5");//签名算法
				packagep.put("timeStamp",(System.currentTimeMillis())/1000 +"");
				String paySign = PayCommonUtil.createSign("UTF-8", packagep, secretkey);
				packagep.put("sign", paySign);
				
				Gson gson = new Gson();
				String json = gson.toJson(packagep);
				PrintWriter pw = response.getWriter();
				pw.write(json);
				pw.close();
	}
	
	//上面notify_url填的就是这么方法的地址，，用来回调这个方法
	@RequestMapping("buy")
	@ResponseBody
	public void Buy(HttpServletRequest request,HttpServletResponse response) throws Exception {
		BufferedReader br = new BufferedReader(new InputStreamReader((ServletInputStream)request.getInputStream()));  
		String line = null;  
		StringBuilder sb = new StringBuilder();  
		while((line = br.readLine()) != null){  
			sb.append(line);  
		}  
		br.close(); 
		//sb为微信返回的xml
		String notifyxml = sb.toString();
		String resxml = "";
		Map map = XMLUtil.doXMLParse(notifyxml);
		String returncode = (String) map.get("return_code");
		if("SUCCESS".equals(returncode)) {// 推送支付结果  --成功
			String out_trade_no = (String) map.get("out_trade_no");
			String timestamp = (String)map.get("nonce_str");
			String goodsid = (String)out_trade_no.substring(out_trade_no.length()-3, out_trade_no.length());
			String openid = (String)map.get("openid");
			
			resxml = "<xml>" + "<return_code>![CDATA[SUCCESS]]></return_code>"+"<return_msg>![CDATA[OK]]</return_msg></xml>";
			
		}else {
			resxml = "<xml>" + "<return_code>![CDATA[FAIL]]></return_code>"+"<return_msg>![CDATA[报文为空]]</return_msg></xml>";
		}
		BufferedOutputStream out = new BufferedOutputStream(response.getOutputStream());
		out.write(resxml.getBytes());
		out.flush();
		out.close();
	}
}
