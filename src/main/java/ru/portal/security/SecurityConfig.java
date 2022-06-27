package ru.portal.security;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.*;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.firewall.HttpFirewall;
import org.springframework.security.web.firewall.StrictHttpFirewall;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.GenericFilterBean;

import java.util.List;

/**
 * Файл конфигурации для SpringSecurity<br>
 * Настраевает:<br>
 * {@link SecurityFilterChain}<br>
 * {@link WebSecurityCustomizer}<br>
 * {@link CorsConfigurationSource}<br>
 * {@link HttpFirewall}<br>
 * {@link AuthenticationProvider}<br>
 * {@link AuthenticationManager}
 *
 * @author Федорышин К.В.
 */
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Configuration
@EnableWebSecurity
@PropertySource(value = "classpath:security.properties", encoding = "UTF-8")
public class SecurityConfig {

    Environment environment;
    UserDetailsService userDetailsService;
    AuthenticationEntryPoint jwtAuthenticationPoint;
    GenericFilterBean jwtAuthenticationTokenFilter;

    @Autowired
    public SecurityConfig(Environment environment,
                          UserDetailsService userDetailsService,
                          @Qualifier("jwtAuthenticationEntryPoint") AuthenticationEntryPoint jwtAuthenticationPoint,
                          @Qualifier("jwtAuthenticationTokenFilter") GenericFilterBean jwtAuthenticationTokenFilter) {
        this.environment = environment;
        this.userDetailsService = userDetailsService;
        this.jwtAuthenticationPoint = jwtAuthenticationPoint;
        this.jwtAuthenticationTokenFilter = jwtAuthenticationTokenFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .exceptionHandling().authenticationEntryPoint(jwtAuthenticationPoint)
                .and()
                .addFilterBefore(jwtAuthenticationTokenFilter, UsernamePasswordAuthenticationFilter.class)
                .authenticationProvider(authenticationProvider())
                .authorizeRequests(reqConfig -> reqConfig
                        .antMatchers("/registration", "/login", "/refreshtoken","/confirmation/*").permitAll()
                        .anyRequest().authenticated())
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .cors(corsConfig -> corsConfig.configurationSource(corsConfigurationSource()))
                .headers()
                .xssProtection()
                .and()
                .contentSecurityPolicy(environment.getRequiredProperty("security.contentPolicy"));

        http.csrf().disable().httpBasic().disable();

        return http.build();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return web -> web.httpFirewall(httpFirewall());
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        var manager = new ProviderManager(authenticationProvider());
        manager.setAuthenticationEventPublisher(authenticationEventPublisher());
        return manager;
    }

    @Bean
    public AuthenticationEventPublisher authenticationEventPublisher() {
        return new DefaultAuthenticationEventPublisher();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        var provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(passwordEncoder());
        provider.setUserDetailsService(userDetailsService);
        return provider;
    }

    @Bean
    public HttpFirewall httpFirewall() {
        var strictHttpFirewall = new StrictHttpFirewall();
        strictHttpFirewall.setAllowSemicolon(false);
        strictHttpFirewall.setUnsafeAllowAnyHttpMethod(false);
        strictHttpFirewall.setAllowUrlEncodedSlash(false);
        strictHttpFirewall.setAllowBackSlash(false);
        strictHttpFirewall.setAllowNull(false);
        strictHttpFirewall.setAllowUrlEncodedPercent(false);
        return strictHttpFirewall;
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        var configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("https//"));
        configuration.setAllowedMethods(List.of(HttpMethod.GET.toString(), HttpMethod.POST.toString(),
                HttpMethod.DELETE.toString(), HttpMethod.PUT.toString(), HttpMethod.PATCH.toString(),
                HttpMethod.OPTIONS.toString()));
        var source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
