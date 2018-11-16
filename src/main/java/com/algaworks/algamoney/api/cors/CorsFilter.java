package com.algaworks.algamoney.api.cors;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.algaworks.algamoney.api.config.property.AlgamoneyApiProperty;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CorsFilter implements Filter{
	
	@Autowired
	private AlgamoneyApiProperty algamoneyApiProperty;

	@Override
	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) resp;
		
		// este 02 headers precisam ser obrigatoriamente enviados para quaisquer requisições
		response.setHeader("Access-Control-Allow-Origin", this.algamoneyApiProperty.getOrigemPermitida());
		response.setHeader("Access-Control-Allow-Credentials", "true");	
		// para permitir que o Cookie do refresh Token seja enviado 	
		if ("OPTIONS".equals(request.getMethod())
				&& algamoneyApiProperty.getOrigemPermitida().equals(request.getHeader("Origin"))){
			response.setHeader("Access-Control-Allow-Methods", "POST, GET, DELETE, PUT, OPTIONS");
			response.setHeader("Access-Control-Allow-Headers", "Authorization, Content-Type, Accept");
			response.setHeader("Access-Control-Max-Age", "3600"); // 1 horas
			response.setStatus(HttpServletResponse.SC_OK);
		}else{
			chain.doFilter(req, resp);
		}
		
	}
	@Override
	public void destroy() {
		
	}
	@Override
	public void init(FilterConfig arg0) throws ServletException {
		
	}
	

}
