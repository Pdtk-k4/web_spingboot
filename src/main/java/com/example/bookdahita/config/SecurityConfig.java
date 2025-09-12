package com.example.bookdahita.config;

import com.example.bookdahita.models.CustomUserDetail;
import com.example.bookdahita.models.User;
import com.example.bookdahita.service.CustomUserDetailService;
import com.example.bookdahita.service.ProductService;
import com.example.bookdahita.service.TransactionService;
import jakarta.servlet.http.HttpSession;
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

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private ProductService productService;

    @Bean
    @Order(1)
    SecurityFilterChain clientSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/client/**", "/DahitaBook", "/")
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/DahitaBook", "/client/**", "/client/register", "/client/login", "/resources/**", "/static/**").permitAll()
                        .anyRequest().hasAuthority("USER")
                )
                .formLogin(form -> form
                        .loginPage("/client/login")
                        .loginProcessingUrl("/client/login")
                        .defaultSuccessUrl("/DahitaBook", true)
                        .successHandler((request, response, authentication) -> {
                            HttpSession session = request.getSession();
                            Long productId = (Long) session.getAttribute("pendingProductId");
                            Integer quantity = (Integer) session.getAttribute("pendingQuantity");
                            if (productId != null && quantity != null) {
                                CustomUserDetail userDetail = (CustomUserDetail) authentication.getPrincipal();
                                User user = userDetail.getUser();
                                transactionService.addProductToTransaction(user, productService.findById(productId), quantity);
                                session.removeAttribute("pendingProductId");
                                session.removeAttribute("pendingQuantity");
                                response.sendRedirect("/client/cart");
                            } else {
                                response.sendRedirect("/DahitaBook");
                            }
                        })
                        .failureUrl("/client/login?error=true")
                )
                .logout(logout -> logout
                        .logoutUrl("/client/logout")
                        .logoutSuccessUrl("/client/login")
                        .invalidateHttpSession(true)
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                )
                .userDetailsService(customUserDetailService);

        return http.build();
    }

    @Bean
    @Order(2)
    SecurityFilterChain adminSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/admin/**")
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/admin/login").permitAll()
                        .requestMatchers("/admin/**").hasAuthority("ADMIN")
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
                        .invalidateHttpSession(true)
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                )
                .userDetailsService(customUserDetailService);

        return http.build();
    }

    @Bean
    WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().requestMatchers("/static/**", "/resources/**", "/resources/image/**", "/static/resources/uploads/**");
    }


}