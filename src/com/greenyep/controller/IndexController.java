package com.greenyep.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.greenyep.common.*;

@Controller
public class IndexController {

	/**
	 * 订单创建，详情可参考官方接口文档：https://www.greenyep.com
	 * 
	 * @param req
	 * @return
	 */

	@RequestMapping(value = "transaction.do", method = RequestMethod.POST)
	@ResponseBody
	public void transaction(HttpServletRequest req, HttpServletResponse rep) throws IOException {

		int payType = Integer.parseInt(req.getParameter("type"));
		String payAmount = req.getParameter("amount");

		if (payType == 0 || payAmount.isEmpty()) {
			rep.getWriter().write("request param error.");
			return;
		}

		// 构建订单信息
		List<NameValuePair> payData = Helper.getPayData(payAmount, payType);

		// 调用官方接口。
		CloseableHttpClient httpclient = HttpClients.createDefault();
		HttpPost httpPost = new HttpPost(Config.API_TRANSACTION_CREATE);

		// 注意：上报数据的编码格式，请务必确保为 UTF-8
		httpPost.setEntity(new UrlEncodedFormEntity(payData, Consts.UTF_8));

		// 发起请求
		CloseableHttpResponse response = httpclient.execute(httpPost);

		try {

			if (response.getStatusLine().getStatusCode() == 200) {
				HttpEntity entity = response.getEntity();
				String responseData = EntityUtils.toString(entity, "utf-8");

				ObjectMapper objectMapper = new ObjectMapper();

				JsonNode jsonNode = objectMapper.readValue(responseData, JsonNode.class);

				int code = jsonNode.get("code").asInt();
				JsonNode content = jsonNode.get("content");

				if (code != 200) {
					rep.getWriter().write(content.asText());
					return;
				}

				String txnId = content.get("txn_id").asText();
				String amount = content.get("amount").asText();
				String amountPay = content.get("amount_pay").asText();
				int timeout = content.get("timeout").asInt();
				String cashierUrl = content.get("cashier_url").asText();
				String qrcodeUrl = content.get("qrcode_url").asText();
				String queryUrl = content.get("query_url").asText();
				String signature = content.get("signature").asText();

				String signatureData = Helper.md5(txnId + amount + amountPay + timeout + cashierUrl + qrcodeUrl
						+ queryUrl + Config.MERCHANT_TOKEN);

				if (!signatureData.equals(signature)) {
					rep.getWriter().write("signature not match.");
					return;
				}

				// 这里可以跳转至 cashier_url（官方收银台），供用户支付。
				// 也可以使用返回的数据，自定义收银台。

				rep.sendRedirect(cashierUrl);
			} else {
				rep.getWriter().write("network error.");
			}
		} finally {
			response.close();
			httpclient.close();
		}
	}

	/**
	 * 支付成功后同步跳转地址。 跳转到该地址并不表示用户一定支付成功。 需要根据订单号，查询相关订单是否已接收到通知回调成功的消息。
	 * 如果已经接收到，则可以显示支付成功的信息。
	 * 
	 * @param req
	 * @param rep
	 * @throws IOException
	 */
	@RequestMapping(value = "result.do", method = RequestMethod.GET)
	@ResponseBody
	public void resultPage(HttpServletRequest req, HttpServletResponse rep) throws IOException {
		String orderId = req.getParameter("order_id");

		// TODO: 请根据订单号，查询相关订单是否已接收到通知回调成功的消息。如果已经接收到，则可以显示支付成功的信息。。

		rep.setCharacterEncoding("UTF-8");
		rep.setContentType("text/html;charset = UTF-8");
		rep.getWriter().write("pay returned. order_id=" + orderId);
	}

	/**
	 * 支付成功后通知回调地址 可在此地址执行订单支付成功的逻辑，比如增加用户积分，延长服务时间等。
	 * 
	 * @param req
	 * @param rep
	 * @throws IOException
	 */
	@RequestMapping(value = "notify.do", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> notify(HttpServletRequest req, HttpServletResponse rep) {

		String merchToken = Config.MERCHANT_TOKEN;

		String txnId = req.getParameter("txn_id");
		String orderId = req.getParameter("order_id");
		String amount = req.getParameter("amount");
		String amountPay = req.getParameter("amount_pay");
		String signature = req.getParameter("signature");

		String signatureData = Helper.md5(txnId + orderId + amount + amountPay + merchToken);

		Log log = LogFactory.getLog(IndexController.class);

		if (signatureData.equals(signature)) {
			log.info("支付成功：执行订单支付成功的逻辑，比如增加用户积分，延长服务时间等...");
			// TODO: 支付成功：执行订单支付成功的逻辑，比如增加用户积分，延长服务时间等...
		} else {
			log.info("支付失败：加密串不匹配。");
			// TODO: 支付失败：加密串不匹配
		}

		Map<String, Object> result = new HashMap<String, Object>();
		result.put("code", 200);
		result.put("content", "ok");
		return result;
	}
}
