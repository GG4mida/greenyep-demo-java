package com.greenyep.common;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

public class Helper {

	/**
	 * 获取订单信息
	 * 
	 * @param amount 支付金额
	 * @param type   支付类型
	 * @return 订单信息
	 */
	public static List<NameValuePair> getPayData(String amount, int type) {
		// 商户 uid
		// 必填
		String payMerchId = Config.MERCHANT_UID;

		// 商户 token
		// 必填
		String payMerchToken = Config.MERCHANT_TOKEN;

		// 支付金额
		// 必填
		String payAmount = amount;

		// 收款账号
		// 选填
		String payAccount = "";

		// 支付类型，支付宝 = 1，微信 = 2
		// 必填
		int payType = type;

		// 支付成功后通知回调地址，必须是公网地址
		// 必填
		String payNotifyUrl = "http://127.0.0.1:8080/greenyep-demo-java/notify.do";

		// 支付成功后同步跳转地址，必须是公网地址
		// 如果是自定义收银台，可不指定
		String payRedirectUrl = "http://127.0.0.1:8080/greenyep-demo-java/result.do";

		// 自定义订单号
		// 选填
		String payOrderId = getRandomCode(12);

		// 自定义用户编号
		// 选填
		String payCustomerId = getRandomCode(12);

		// 商品名称
		// 选填
		String payProductName = "TEST PRODUCT NAME";

		// 将上述参数进行 md5-32 加密
		String signatureStr = payMerchId + payAmount + payType + payAccount + payOrderId + payCustomerId
				+ payProductName + payNotifyUrl + payRedirectUrl + payMerchToken;
		String signature = md5(signatureStr);

		List<NameValuePair> payData = new ArrayList<NameValuePair>();

		payData.add(new BasicNameValuePair("uid", payMerchId));
		payData.add(new BasicNameValuePair("amount", payAmount));
		payData.add(new BasicNameValuePair("type", String.valueOf(payType)));
		payData.add(new BasicNameValuePair("account", payAccount));
		payData.add(new BasicNameValuePair("notify_url", payNotifyUrl));
		payData.add(new BasicNameValuePair("redirect_url", payRedirectUrl));
		payData.add(new BasicNameValuePair("order_id", payOrderId));
		payData.add(new BasicNameValuePair("customer_id", payCustomerId));
		payData.add(new BasicNameValuePair("product_name", payProductName));
		payData.add(new BasicNameValuePair("signature", signature));

		return payData;
	}

	/**
	 * 获取指定长度随机字符串
	 * 
	 * @param len 字符串长度
	 * @return 随机字符串
	 */
	public static String getRandomCode(int len) {
		String str = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
		Random random = new Random();
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < len; i++) {
			int number = random.nextInt(62);
			sb.append(str.charAt(number));
		}
		return sb.toString();
	}

	/**
	 * md5
	 * 
	 * @param plainText 原始字符串
	 * @return md5 结果
	 */
	public static String md5(String plainText) {
		byte[] secretBytes = null;
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(plainText.getBytes());
			secretBytes = md.digest();
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException("no md5 module find.");
		}
		String md5code = new BigInteger(1, secretBytes).toString(16);
		for (int i = 0; i < 32 - md5code.length(); i++) {
			md5code = "0" + md5code;
		}
		return md5code;
	}
}
