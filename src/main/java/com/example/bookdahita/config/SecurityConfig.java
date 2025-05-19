package com.example.bookdahita.config;

import com.example.bookdahita.service.CustomUserDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Autowired
    private CustomUserDetailService customUserDetailService;

    // SecurityFilterChain cho client
    @Bean
    @Order(1)
    SecurityFilterChain clientSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/client/**", "/DahitaBook", "/")
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/DahitaBook", "/client/**", "/client/register", "/client/login", "/resources/**", "/static/**").permitAll()
                        .anyRequest().hasAuthority("USER") // Chỉ cho phép vai trò USER truy cập các yêu cầu đã xác thực
                )
                .formLogin(form -> form
                        .loginPage("/client/login")
                        .loginProcessingUrl("/client/login")
                        .defaultSuccessUrl("/DahitaBook", true)
                        .failureUrl("/client/login?error=true")
                )
                .logout(logout -> logout
                        .logoutUrl("/client/logout")
                        .logoutSuccessUrl("/client/login")
                        .invalidateHttpSession(true) // Đảm bảo phiên client bị hủy khi đăng xuất
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED) // Tạo phiên nếu cần
                )
                .userDetailsService(customUserDetailService); // Sử dụng CustomUserDetailService

        return http.build();
    }

    // SecurityFilterChain cho admin
    @Bean
    @Order(2)
    SecurityFilterChain adminSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/admin/**")
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/admin/login").permitAll()
                        .requestMatchers("/admin/**").hasAuthority("ADMIN") // Chỉ cho phép vai trò ADMIN
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/admin/login")
                        .loginProcessingUrl("/admin/login")
                        .defaultSuccessUrl("/admin", true)
                        .failureUrl("/admin/login?error=true")
                )
                .logout(logout -> logout
                        .logoutUrl("/admin/logout")
                        .logoutSuccessUrl("/admin/login")
                        .invalidateHttpSession(true) // Đảm bảo phiên admin bị hủy khi đăng xuất
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED) // Tạo phiên nếu cần
                )
                .userDetailsService(customUserDetailService); // Sử dụng CustomUserDetailService

        return http.build();
    }

    @Bean
    WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().requestMatchers("/static/**", "/resources/**", "/resources/image/**", "/static/resources/uploads/**");
    }
}