package com.endava;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.endava.bean.SecurityConstants;
import com.endava.filters.ErrorFilter;
import com.endava.filters.PostFilter;
import com.endava.filters.PreFilter;
import com.endava.filters.RouteFilter;
import com.google.common.base.Predicates;
import com.netflix.discovery.EurekaClient;

import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger.web.SwaggerResource;
import springfox.documentation.swagger.web.SwaggerResourcesProvider;
import springfox.documentation.swagger.web.UiConfiguration;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@EnableZuulProxy
@EnableDiscoveryClient
@EnableSwagger2
public class GatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(GatewayApplication.class, args);

	}

	@EnableWebSecurity
	static class WebSecurityConfig extends WebSecurityConfigurerAdapter {
		@Override
		protected void configure(HttpSecurity http) throws Exception {
			http.csrf().disable();
		}
	}

	@Bean
	public PreFilter preFilter() {
		return new PreFilter();
	}

	@Bean
	public PostFilter postFilter() {
		return new PostFilter();
	}

	@Bean
	public ErrorFilter errorFilter() {
		return new ErrorFilter();
	}

	@Bean
	public RouteFilter routeFilter() {
		return new RouteFilter();
	}

	@Configuration
	class RestTemplateConfig {

		@Bean
		public RestTemplate restTemplate(RestTemplateBuilder builder) {
			return builder.build();
		}
	}

	@Autowired
	private EurekaClient eureka;

	@Component
	@Primary
	@EnableAutoConfiguration
	public class DocumentationController implements SwaggerResourcesProvider {

		@SuppressWarnings("unchecked")
		@Override
		public List get() {
			List resources = new ArrayList<>();
			eureka.getApplications().getRegisteredApplications().forEach(i -> {
				if (!i.getName().toLowerCase().equals(SecurityConstants.GATEWAY_NAME))
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

	}

	@Bean
	UiConfiguration uiConfig() {
		return new UiConfiguration("validatorUrl", "list", "alpha", "schema",
				UiConfiguration.Constants.DEFAULT_SUBMIT_METHODS, false, true, 60000L);
	}

}
