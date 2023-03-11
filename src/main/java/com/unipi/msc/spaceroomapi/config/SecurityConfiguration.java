package com.unipi.msc.spaceroomapi.config;

import com.unipi.msc.spaceroomapi.Constant.ErrorMessages;
import com.unipi.msc.spaceroomapi.Model.Enum.Role;
import lombok.RequiredArgsConstructor;
import net.minidev.json.JSONObject;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.Date;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {
    private final AuthenticationProvider authenticationProvider;
    private final JwtAuthenticationFilter jwtAuthFilter;
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .csrf()
                .disable()
                .cors().and()
                .authorizeHttpRequests()
                .requestMatchers("/auth/**",
                        "/house/*",
                        "/house/*/reservation",
                        "/image/**").permitAll()
                .requestMatchers("/auth/logout",
                                 "/house/*/image/*,",
                                 "/house/*/edit,",
                                 "/house/*/image",
                                 "/user/all",
                                 "reservation/all").authenticated()
                .anyRequest()
                .authenticated()
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .exceptionHandling().authenticationEntryPoint((request, response, authException) -> {
                    JSONObject jsonObject = new JSONObject();
                    if (authException.getLocalizedMessage().equals("Bad credentials")){
                        jsonObject.put("error", ErrorMessages.USER_NOT_FOUND);
                    }else {
                        jsonObject.put("timestamp", new Date());
                        jsonObject.put("status", 403);
                        jsonObject.put("message", ErrorMessages.ACCESS_DENIED);
                    }

                    response.setContentType("application/json;charset=UTF-8");
                    response.setStatus(403);
                    response.getWriter().write(jsonObject.toJSONString());
                })
                .and()
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return httpSecurity.build();
    }
}
