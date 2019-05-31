package com.endava.controller;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.netflix.discovery.EurekaClient;
import com.netflix.discovery.shared.Application;

@RestController
@RequestMapping
@RefreshScope
public class RefreshController {

	@Autowired
	private EurekaClient eurekaClient;

	@Autowired
	private RestTemplate restTemplate;
	
	@Value("#{'${globals.refreshServices}'.split(',')}")
	private String[] refreshServices;

	@GetMapping("/refresh")
	public void refreshServices() {
		List<Application> apps = eurekaClient.getApplications().getRegisteredApplications();
		apps.stream()
		.filter(i -> isPresent(refreshServices,i.getName()))
		.forEach(x -> {
			x.getInstances().stream().forEach(i -> {
				List<String> list_ms = Arrays.asList("http://", String.valueOf(i.getIPAddr()), ":",
						String.valueOf(i.getPort()), "/refresh");
				String url = String.join("", list_ms);
				ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
				System.out.println(response);
			});
		});

	}
	
	public static<T> boolean isPresent(T[] a, T target)
	{
		return Arrays.stream(a)
					.anyMatch(x -> target.equals(x.toString()));
	}
}
