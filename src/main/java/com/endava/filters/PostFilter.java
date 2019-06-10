package com.endava.filters;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import com.endava.bean.SecurityConstants;
import com.google.common.io.CharStreams;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;
import com.netflix.discovery.shared.Application;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class PostFilter extends ZuulFilter {
	@Autowired
	private EurekaClient eurekaClient;

	@Autowired
	private RestTemplate restTemplate;

	@Value("#{'${gateway.audit_unsave_endpoints}'.split(',')}")
	private String[] audit_unsave_endpoints;

	@Override
	public String filterType() {
		return "post";
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
		auditCalls();

		return null;
	}

	private void auditCalls() {
		RequestContext ctx = RequestContext.getCurrentContext();
		HttpServletRequest request = ctx.getRequest();

		Application userApp = eurekaClient.getApplication(SecurityConstants.AUDIT_NAME);
		InstanceInfo instanceInfo = userApp.getInstances().get(0);
		String URI = request.getRequestURI();
		if (!Arrays.stream(audit_unsave_endpoints).anyMatch(x -> URI.endsWith(x.trim()))) {
			List<String> ms = Arrays.asList(SecurityConstants.PROTOCOL, String.valueOf(instanceInfo.getIPAddr()), ":",
					String.valueOf(instanceInfo.getPort()), SecurityConstants.AUDIT_ENDPOINT);
			String url = String.join("", ms);
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);

			Date now = new Date();
			Long now_epoch = now.getTime();
			JSONObject data = new JSONObject();
			data.put(SecurityConstants.AUDIT_DATE, String.valueOf(now_epoch));
			data.put(SecurityConstants.AUDIT_REQUEST_METHOD, request.getMethod());
			data.put(SecurityConstants.AUDIT_REQUEST_ENDPOINT, URI);
			String request_body = "";
			String response_body = "";
			try {
				request_body = request.getReader().lines().collect(Collectors.joining());
			} catch (Exception e) {
				e.printStackTrace();
			}
			data.put(SecurityConstants.AUDIT_REQUEST_BODY, request_body);
			try (final InputStream responseDataStream = ctx.getResponseDataStream()) {
				response_body = CharStreams.toString(new InputStreamReader(responseDataStream, SecurityConstants.CHARSET));
				ctx.setResponseBody(response_body);
			} catch (IOException e) {
				log.debug("Error reading body", e);
			}
			data.put(SecurityConstants.AUDIT_RESPONSE_BODY, response_body);
			HttpEntity<JSONObject> req = new HttpEntity<JSONObject>(data, headers);
			restTemplate.postForEntity(url, req, JSONObject.class);
		}
	}
}
