package com.codehows.shop.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;


@Configuration
//위 어노테이션이 달린 클래스에 @Bean 어노테이션이 붙은 메서드를 등록하면 해당 메서드의 반환 값이 스프링 빈로 등록됨.
@EnableWebSecurity
//use springboot filter
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.formLogin((it)-> it
                .loginPage("/members/login")
                .defaultSuccessUrl("/")
                .usernameParameter("email")
                .failureUrl("/members/login/error")
        );
        http.logout((logout)-> logout
                .logoutUrl("/members/logout")
                .logoutSuccessUrl("/")
        );
//        http.csrf(AbstractHttpConfigurer::disable);

        //3. 인가(접근 권한) 설정
        http.authorizeHttpRequests((req)->{req
                //모두 허용(비로그인도 가능)
                .requestMatchers("/", "/members/**", "/item/**", "/images/**").permitAll()

                //로그인 후 admin 역할을 가진 사용자만 접근 가능
                .requestMatchers(("/admin/**")).hasRole("ADMIN")

                .anyRequest().authenticated();  //그 외 모든 요청 로그인한 사용자만 접근 가능
        });

        //인증이 필요한 url에 비로그인 사용자가 접근할 경우
        //spring security가 자동으로 /members/login 으로 이동시키도록 설정
        http.exceptionHandling((e) -> e
                .authenticationEntryPoint(new CustomAuthenticationEntryPoint())
                .accessDeniedHandler(new CustomAccessDeniedHandler())
        );



        return http.build();
    }

    /* 스프링 시큐리티 필터 체인 */
//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        /*
//        http.설정1
//        http.설정2
//        http.설정3
//         */
//        return http.build();
//    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
