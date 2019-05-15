package com.endava.documentation;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import com.endava.bean.SecurityConstants;
import com.netflix.discovery.EurekaClient;

import springfox.documentation.swagger.web.SwaggerResource;
import springfox.documentation.swagger.web.SwaggerResourcesProvider;
import springfox.documentation.swagger.web.UiConfiguration;

@Component
@Primary
@EnableAutoConfiguration
public class DocumentationController implements SwaggerResourcesProvider {

	@Autowired
	private EurekaClient eureka;

	@SuppressWarnings("unchecked")
	@Override
	public List get() {
		List resources = new ArrayList<>();
		eureka.getApplications().getRegisteredApplications().stream()
				.filter(i -> !i.getName().toLowerCase().equals(SecurityConstants.GATEWAY_NAME))
				.forEach(i -> {
					resources.add(swaggerResource(i.getName().toLowerCase(), SecurityConstants.ZUUL_PREFIX
							+ i.getName().toLowerCase() + SecurityConstants.ENDPOINT_SWAGGER,
							SecurityConstants.VERSION_SWAGGER));
				});
		return resources;
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
