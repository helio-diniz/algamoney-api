package com.algaworks.algamoney.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.expression.OAuth2MethodSecurityExpressionHandler;

@Profile("oauth-security")
@Configuration
// public class ResourceServerConfig extends WebSecurityConfigurerAdapter
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {

	@Override
	public void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests().
		// configura autenticação
				antMatchers("/categorias").
				// para requisição /categorias
				permitAll().
				// permite todas requisições sem usuário autenticado
				anyRequest().
				// para qualquer requisição
				authenticated().
				// exigindo que o usuário esteja autenticado
				and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and().csrf().disable();
		// cross site request forgery - javascript injection num serviço web
	}

	@Override
	public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
		resources.stateless(true);
	}

	@Bean
	// handler que possibilita a segurança nos métodos com OAuth2
	public MethodSecurityExpressionHandler createExpressionHandler() {
		return new OAuth2MethodSecurityExpressionHandler();
	}

}

/*
 * @Configuration
 * 
 * @EnableWebSecurity public class SecurityConfigu extends
 * WebSecurityConfigurerAdapter{
 * 
 * @Override protected void configure(AuthenticationManagerBuilder auth) throws
 * Exception { auth.inMemoryAuthentication(). withUser("admin").
 * password("admin"). roles("ROLE"); }
 * 
 * @Override protected void configure(HttpSecurity http) throws Exception {
 * http.authorizeRequests(). // configura autenticação
 * antMatchers("/categorias"). // para requisição /categorias permitAll(). //
 * permite todas requisições sem usuário autenticado anyRequest(). // para
 * qualquer requisição authenticated(). // exigindo que o usuário esteja
 * autenticado and().httpBasic().
 * and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.
 * STATELESS). and().csrf().disable(); // cross site request forgery -
 * javascript injection num serviço web } }
 */