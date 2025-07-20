package com.example.config;

import java.io.IOException;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import com.example.atm.details.CustomUserDetailsService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Spring Security の設定クラス
 * - ログイン/ログアウトの設定
 * - 認証処理の設定
 * - パスワードエンコーダーの定義
 */

@Configuration
public class SecurityConfig {
    private final CustomUserDetailsService customUserDetailsService;
    
    /**
     * コンストラクタでカスタムユーザー詳細サービスを注入
     * customUserDetailsService ユーザー情報を取得するサービス
     */
    public SecurityConfig(CustomUserDetailsService customUserDetailsService) {
        this.customUserDetailsService = customUserDetailsService;
    }
    
    
    /**
     * セキュリティフィルタチェーンの設定
     * ・特定のURLは認証不要に設定（ログイン、登録、CSS等）
     * ・フォームログイン設定（ログインページ、成功後の遷移先）
     * ・ログアウト時の処理（ログアウト成功時のリダイレクト）
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
          .authorizeHttpRequests(authz -> authz
                  .requestMatchers("/atm/login", "/atm/register", "/register", "/style.css").permitAll()
                  .anyRequest().authenticated()
                  )
                  .formLogin(form -> form
                          .loginPage("/atm/login")
                          .loginProcessingUrl("/atm/login")
                          .defaultSuccessUrl("/atm/?loginSuccess=true", true)
                          .permitAll()
                  )
                  .logout(logout -> logout
                          .logoutSuccessHandler(new LogoutSuccessHandler() {
                              @Override
                              public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response,
                                                          Authentication authentication) throws IOException {
                                  if (authentication != null) {
                                      String username = authentication.getName();
                                      response.sendRedirect("/atm/login?logout&username=" + username);
                                  } else {
                                      response.sendRedirect("/atm/login?logout");
                                  }
                              }
                          })
                          .permitAll()
                   );
       return http.build();
    }
    
    /**
     * パスワードエンコーダーのBean定義
     * BCryptPasswordEncoderを利用してパスワードをハッシュ化
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    
    /**
     * AuthenticationManagerのBean定義
     * DaoAuthenticationProviderを用いてカスタムユーザー詳細サービスと
     * パスワードエンコーダーを設定し、認証処理を構築する
     */
    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(customUserDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        
        AuthenticationManagerBuilder builder = http.getSharedObject(AuthenticationManagerBuilder.class);
        builder.authenticationProvider(provider);
        
        return builder.build();
    }
    
}
