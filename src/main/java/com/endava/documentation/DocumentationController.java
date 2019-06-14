package com.endava.documentation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.endava.bean.SecurityConstants;
import com.netflix.discovery.EurekaClient;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import springfox.documentation.swagger.web.SwaggerResource;
import springfox.documentation.swagger.web.SwaggerResourcesProvider;
import springfox.documentation.swagger.web.UiConfiguration;

@Component
@Primary
@EnableAutoConfiguration
@RefreshScope
public class DocumentationController implements SwaggerResourcesProvider {

	@Autowired
	private EurekaClient eureka;

	@Value("#{'${gateway.exclude-swagger-services}'.split(',')}")
	private String[] excludes;

	@SuppressWarnings("unchecked")
	@Override
	public List get() {
		List resources = new ArrayList<>();
		eureka.getApplications().getRegisteredApplications().stream().filter(i -> !isPresent(excludes, i.getName()))
				.forEach(i -> {
					resources.add(swaggerResource(i.getName().toLowerCase(), SecurityConstants.ZUUL_PREFIX
							+ i.getName().toLowerCase() + SecurityConstants.ENDPOINT_SWAGGER,
							SecurityConstants.VERSION_SWAGGER));
				});
		return resources;
	}

	public static <T> boolean isPresent(T[] a, T target) {
		return Arrays.stream(a).anyMatch(x -> target.equals(x.toString()));
	}

	private SwaggerResource swaggerResource(String name, String location, String version) {
		SwaggerResource swaggerResource = new SwaggerResource();
		swaggerResource.setName(name);
		swaggerResource.setLocation(location);
		swaggerResource.setSwaggerVersion(version);
		return swaggerResource;
	}

	@Bean
	UiConfiguration uiConfig() {
		return new UiConfiguration("validatorUrl", "list", "alpha", "schema",
				UiConfiguration.Constants.DEFAULT_SUBMIT_METHODS, false, true, 60000L);
	}

}
