package com.endava.filters;

import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.endava.bean.Redirect;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;
import com.netflix.discovery.shared.Application;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;

public class PreFilter extends ZuulFilter {

	@Autowired
	private EurekaClient eurekaClient;

	@Autowired
	private RestTemplate restTemplate;

	@Override
	public String filterType() {
		return "pre";
	}

	@Override
	public int filterOrder() {
		return 1;
	}

	public boolean shouldFilter() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public Object run() throws ZuulException {
		RequestContext ctx = RequestContext.getCurrentContext();
		HttpServletRequest request = ctx.getRequest();

		String token = request.getHeader("Authorization");

		if (token != null) {
			Application userApp = eurekaClient.getApplication("AUTH");
			InstanceInfo instanceInfo = userApp.getInstances().get(0);

			List<String> ms = Arrays.asList("http://", String.valueOf(instanceInfo.getIPAddr()), ":",
					String.valueOf(instanceInfo.getPort()), "/checkToken");

			String url = String.join("", ms);

			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);

			String URI = request.getRequestURI();

			Redirect red = new Redirect();
			JSONObject data = new JSONObject();
			data.put("token", token);
			data.put("url", URI);

			HttpEntity<JSONObject> req = new HttpEntity<JSONObject>(data, headers);

			ResponseEntity<Boolean> response = restTemplate.postForEntity(url, req, Boolean.class);
			Boolean resp = response.getBody();
			if (!resp) {
				throw new ZuulException("Access forbiden", 500, "Invalid token");
			}
		}

		System.out.println(
				"Request Method : " + request.getMethod() + " Request URL : " + request.getRequestURL().toString());
		return null;
	}

}
