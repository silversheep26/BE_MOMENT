package com.back.moment.users.config;

import com.back.moment.users.jwt.JwtAuthFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true) // 버전 3으로 오면서 @EnableGlobalMethodSecurity 대신 사용
public class WebSecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    private static final String[] PERMIT_URL_ARRAY = {
/* swagger v2 */
            "/v2/api-docs",
            "/swagger-resources",
            "/swagger-resources/**",
            "/configuration/ui",
            "/configuration/security",
            "/swagger-ui.html",
            "/webjars/**",
/* swagger v3 */
            "/v3/api-docs/**",
            "/swagger-ui/**"
    };

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        // h2-console 사용 및 resources 접근 허용 설정
        return (web) -> web.ignoring()
//                .requestMatchers(PathRequest.toH2Console())
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations());
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.cors().and()
                .csrf().disable()
                .httpBasic().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
                .authorizeHttpRequests()
                .requestMatchers("/users/**").permitAll()
                .requestMatchers("/ws-edit/**").permitAll()
                .requestMatchers("/emails/**").permitAll()
                .requestMatchers("/main", "/home").permitAll()
                .requestMatchers("/feeds", "/boards").permitAll()
                .requestMatchers("/party/test-vepo").permitAll()
                .requestMatchers(PERMIT_URL_ARRAY).permitAll()
                .requestMatchers(CorsUtils::isPreFlightRequest).permitAll()

            .anyRequest().authenticated()
                // JWT 인증/인가를 사용하기 위한 설정
                .and().addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

//    @Bean
//    public CorsConfigurationSource corsConfigurationSource(){
//
//        CorsConfiguration config = new CorsConfiguration();
//        config.applyPermitDefaultValues();
//
//        // 사전에 약속된 출처를 명시
//        config.addAllowedOrigin("http://localhost:3000");
//
//
//        // 특정 헤더를 클라이언트 측에서 사용할 수 있게 지정
//        // 만약 지정하지 않는다면, Authorization 헤더 내의 토큰 값을 사용할 수 없음
//        config.addExposedHeader(JwtUtil.AUTHORIZATION_HEADER);
//
//        // 본 요청에 허용할 HTTP method(예비 요청에 대한 응답 헤더에 추가됨)
//        config.addAllowedMethod("*");
//
//        // 본 요청에 허용할 HTTP header(예비 요청에 대한 응답 헤더에 추가됨)
//        config.addAllowedHeader("*");
//
//        // 기본적으로 브라우저에서 인증 관련 정보들을 요청 헤더에 담지 않음
//        // 이 설정을 통해서 브라우저에서 인증 관련 정보들을 요청 헤더에 담을 수 있도록 해줍니다.
//        config.setAllowCredentials(true);
//
//        // allowCredentials 를 true로 하였을 때,
//        // allowedOrigin의 값이 * (즉, 모두 허용)이 설정될 수 없도록 검증합니다.
//        config.validateAllowCredentials();
//
//        // 어떤 경로에 이 설정을 적용할 지 명시합니다. (여기서는 전체 경로)
//        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        source.registerCorsConfiguration("/**", config);
//
//        return source;
//    }
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry){
                registry.addMapping("/**")
                    .allowedOrigins("*")
                    .allowedOrigins("http://localhost:8080", "http://localhost:3000",
                        "http://moment-photo.ap-northeast-2.amazonaws.com",  // s3
                        "http://moment.cadbf9mahvf5.ap-northeast-2.rds.amazonaws.com")    // db(rds)
//                        .allowedOriginPatterns("*")
                    .exposedHeaders("ACCESS_KEY", "REFRESH_KEY", "Authorization", "USER_ROLE", "USER_EMAIL")
                    .allowedMethods("GET", "POST", "PUT", "DELETE", "HEAD", "PATCH", "OPTIONS")
                    //.allowedHeaders()
                    .allowCredentials(true)
                    .maxAge(3600);
            }
        };
    }
}
