package com.algaworks.algamoney.api.config;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

import com.algaworks.algamoney.api.config.token.CustomTokenEnhancer;

@Profile("oauth-security")
@Configuration
public class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter 
{
	@Autowired
	private AuthenticationManager authenticationManager;
	@Autowired
	private UserDetailsService userDetailsService;
	
	//configuração do cliente (aplicação que está usando a API)
	public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
		clients.inMemory().
				withClient("angular").
				secret("$2a$10$1pDy.ryBCbzTQI/IhulTWuZ8uDmVQR.LpEuzm/.E4FKmCc.vpUjca").
				// "@ngul@r0 security/util/GeradorSenha.java
				scopes("read", "write").
				authorizedGrantTypes("password", "refresh_token"). 		
				// fluxo de senha e refresh token
				accessTokenValiditySeconds(1800).						
				// tempo de validade do access token em segundos.
				refreshTokenValiditySeconds(3600*24	). 					
				// tempo de validade do refresh token em segundos.
			and().
				withClient("mobile").
				secret("$2a$10$kYcjzeZzSjnnyXZUXxW5Y.MfGRvkxkz87vMps1enp59KQdPyto1Uu").
				// m0b1l30
				scopes("read").
				authorizedGrantTypes("password", "refresh_token").
				accessTokenValiditySeconds(1800). 
				refreshTokenValiditySeconds(3600*24);
	};
	
	@Override
	public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
		TokenEnhancerChain tokenEnhancerChain = new TokenEnhancerChain();
		tokenEnhancerChain.setTokenEnhancers(Arrays.asList(tokenEnhancer(), accessTokenConverter()));
		
		endpoints
			.tokenStore(tokenStore())
			.tokenEnhancer(tokenEnhancerChain)
			.reuseRefreshTokens(false)
			// sempre que pedir novo token, um novo refresh token é enviado	
			.userDetailsService(userDetailsService)
			// precisa configurar esta propriedade para refresh token
			.authenticationManager(authenticationManager);
	}

	@Bean
	public JwtAccessTokenConverter accessTokenConverter() {
		JwtAccessTokenConverter accessTokenConverter = new JwtAccessTokenConverter();
		accessTokenConverter.setSigningKey("algaworks"); 	// palavra secreta que valida token
		return accessTokenConverter;
	}

	@Bean
	public TokenStore tokenStore(){
		return new JwtTokenStore(accessTokenConverter());
	}
	
	private TokenEnhancer tokenEnhancer() {
		return new CustomTokenEnhancer();
	}
}