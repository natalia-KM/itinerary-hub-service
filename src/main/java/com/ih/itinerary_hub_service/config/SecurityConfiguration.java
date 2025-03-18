package com.ih.itinerary_hub_service.config;

import com.ih.itinerary_hub_service.properties.ClientProperties;
import com.ih.itinerary_hub_service.users.auth.CustomAuthSuccessHandler;
import com.ih.itinerary_hub_service.users.auth.JwtAuthenticationFilter;
import jakarta.servlet.DispatcherType;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.oauth2.client.InMemoryOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@Slf4j
public class SecurityConfiguration {
    private final CustomAuthSuccessHandler customAuthenticationSuccessHandler;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final ClientProperties clientProperties;
    private final CookieMaker cookieMaker;

    public SecurityConfiguration(
            CustomAuthSuccessHandler customAuthenticationSuccessHandler,
            JwtAuthenticationFilter jwtAuthenticationFilter, ClientProperties clientProperties, CookieMaker cookieMaker
    ) {
        this.customAuthenticationSuccessHandler = customAuthenticationSuccessHandler;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.clientProperties = clientProperties;
        this.cookieMaker = cookieMaker;
    }

    private final String[] AUTH_WHITELIST = {
            "/v1/health",
            "/v1/users/guest",
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/oauth2/authorization/**",
            "/login/**"
    };


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        final String BASE_URL = clientProperties.getBaseUrl();

        http
                .authorizeHttpRequests((req) ->
                        req
                                .dispatcherTypeMatchers(DispatcherType.FORWARD).permitAll()
                                .requestMatchers(AUTH_WHITELIST).permitAll()
                                .anyRequest().authenticated())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .oauth2Login((oauth2Login) ->
                        oauth2Login
                                .loginPage(BASE_URL + "/login")
                                .successHandler(customAuthenticationSuccessHandler)
                                .failureHandler(((request, response, exception) -> response.sendError(HttpServletResponse.SC_UNAUTHORIZED, exception.getMessage()))))
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .deleteCookies("access_token", "user_id")
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .logoutSuccessHandler((request, response, authentication) -> {
                            cookieMaker.removeDefaultCookies(response);
                            response.setStatus(HttpServletResponse.SC_NO_CONTENT);
                        }))
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint((request, response, authException) -> {
                            log.error("Authentication failed for request : {}, message: {} ", request.getRequestURI(), authException.getMessage());
                            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, authException.getMessage());
                        }))
                .httpBasic(Customizer.withDefaults())
                .cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable);

        return http.build();
    }

    @Bean
    public OAuth2AuthorizedClientService authorizedClientService(ClientRegistrationRepository clientRegistrationRepository) {
        return new InMemoryOAuth2AuthorizedClientService(clientRegistrationRepository);
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration corsConfig = new CorsConfiguration();
        corsConfig.setAllowedOrigins(List.of(clientProperties.getBaseUrl()));
        corsConfig.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE"));
        corsConfig.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));
        corsConfig.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfig);

        return source;
    }
}