package com.endava.bean;

public class SecurityConstants {
	public static final String TOKEN_HEADER = "token";
	public static final String URL_HEADER = "url";
	public static final String[] UNSECURED_URLS = { "/auth/login" };
	public static final String ENDPOINT_SWAGGER = "/v2/api-docs";
	public static final String VERSION_SWAGGER = "2.0";
	public static final String ZUUL_PREFIX = "/api/";
	public static final String GATEWAY_NAME = "gateway";
	public static final String AUTHORIZATION_HEADER = "Authorization";

	public static final String AUDIT_DATE = "date";
	public static final String AUDIT_REQUEST_ENDPOINT = "endpoint";
	public static final String AUDIT_REQUEST_METHOD = "verb";
	public static final String AUDIT_RESPONSE_BODY = "response";
	public static final String AUDIT_REQUEST_BODY = "body";

	public static final String AUDIT_NAME = "AUDIT";
	public static final String AUDIT_ENDPOINT = "/audit";
	public static final String PROTOCOL = "http://";

	public static final String AUTH_NAME = "AUTH";
	public static final String CHECKTOKEN_ENDPOINT = "/checkToken";
	public static final String ACTUATOR_REFRESH_ENDPOINT = "/actuator/refresh";
	public static final String CHARSET = "UTF-8";

}
