package com.shop.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig{
    String a ="하하";
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        http.formLogin()
                .loginPage("/members/login")    //사용자 정의 로그인 페이지
                .defaultSuccessUrl("/")         //성공적인 로그인 후 랜딩 페이지
                .usernameParameter("email")
                .failureUrl("/members/login/error") //로그인 실패 후 방문 페이지
                .and()
                .logout()
                .logoutRequestMatcher(new AntPathRequestMatcher("/members/logout")) // 로그아웃을 호출할 경우 해당 경로
                .logoutSuccessUrl("/"); //로그아웃 성공시 url

        http.authorizeRequests()   // 시큐리티 처리에 HttpServletRequest를 이용한다는 것을 의미
                .mvcMatchers("/css/**","/js/**","/img/**").permitAll()
                .mvcMatchers("/","/members/**","/item/**","/images/**").permitAll()     //permitAll() 을 통해 모든 사용자가 인증없이 접근 가능
                .mvcMatchers("/admin/**").hasRole("ADMIN")                              // Role이 ADMIN일 경우에만 접근 가능
                .anyRequest().authenticated();                                                  // 위에서 설정한 경로를 제외한 나머지 경로들은 모두 인증을 요구핟록 설정

        http.exceptionHandling()
                .authenticationEntryPoint(new CustomAuthenticationEntryPoint()); //인증되지 않은 사용자가 리소스에 접근했을 때 수행되는 핸들러
        return http.build();
    }


    //BCyPasswordEncode의 해시함수를 이용해 비밀번호 암호화하여 저장
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
}
