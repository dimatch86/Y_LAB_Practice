package org.example.monitoringservice.configuration;

import lombok.RequiredArgsConstructor;
import org.example.monitoringservice.security.CustomLogoutHandler;
import org.example.monitoringservice.security.UserDetailsServiceImpl;
import org.example.monitoringservice.security.jwt.BlackListTokenFilter;
import org.example.monitoringservice.security.jwt.JwtAuthEntryPoint;
import org.example.monitoringservice.security.jwt.JwtTokenFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig implements WebMvcConfigurer {

    private final UserDetailsServiceImpl userDetailsService;
    private final JwtAuthEntryPoint jwtAuthEntryPoint;
    private final JwtTokenFilter jwtTokenFilter;
    private final BlackListTokenFilter blacklistTokenFilter;
    private final CustomLogoutHandler logoutHandler;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService);
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**").allowedMethods("*").allowedHeaders("*");
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(auth ->
                        auth.requestMatchers("auth/login", "auth/register", "/v3/api-docs/**",
                                        "/swagger-ui/**", "/swagger").permitAll()
                        .requestMatchers("reading-type/add").hasRole("ADMIN")
                .anyRequest().authenticated())
                .exceptionHandling(configurer -> configurer.authenticationEntryPoint(jwtAuthEntryPoint)
                        .accessDeniedPage("/auth/login"))
                .logout(logout -> logout.logoutUrl("/auth/logout").addLogoutHandler(logoutHandler))
                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(Customizer.withDefaults())
                .sessionManagement(httpSecuritySessionManagementConfigurer ->
                        httpSecuritySessionManagementConfigurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterAfter(blacklistTokenFilter, JwtTokenFilter.class);
        return http.build();
    }
}
