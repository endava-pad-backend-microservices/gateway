package com.endava.filters;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.exception.ZuulException;

public class PostFilter extends ZuulFilter{
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
		return false;
	}

	public Object run() throws ZuulException {
		// TODO Auto-generated method stub
		return null;
	}
	 

}
