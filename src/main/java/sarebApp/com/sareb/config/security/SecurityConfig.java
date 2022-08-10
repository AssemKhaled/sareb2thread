package sarebApp.com.sareb.config.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {


    private final MyUserDetailsService myUserDetailsService;
//    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JwtRequestFilter jwtRequestFilter;
//    private final JwtUtil jwtRequestFilter;
    private  final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean()throws Exception{
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//        auth.userDetailsService(myUserDetailsService).passwordEncoder(bCryptPasswordEncoder);
        super.configure(auth);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
//        http
//                .csrf()
//                .disable()
//                .sessionManagement().sessionCreationPolicy(STATELESS)
//                .and()
//                .authorizeRequests()
//                .antMatchers("/login")
//                .permitAll()
//                .and()
//                .addFilter(new CustomAuthenticationFilter(authenticationManagerBean()));

                http
                .csrf().disable()
                .exceptionHandling()
                .authenticationEntryPoint(jwtAuthenticationEntryPoint).and()
                .authorizeRequests()
                .antMatchers("/app/login")
                .permitAll()
                .anyRequest()
                .authenticated().and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
                http.addFilterBefore( jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

//                .formLogin()
//                .and()
//                .logout()
//                .logoutRequestMatcher(new AntPathRequestMatcher("/logout","GET"))
//                .clearAuthentication(true)
//                .invalidateHttpSession(true)
//                .deleteCookies()
//                .logoutUrl("www.google.com")
//                .and()
//                http.logout().logoutRequestMatcher(new AntPathRequestMatcher("/app/logout"));
//        http.logout().logoutUrl("/app/logout").invalidateHttpSession(true).deleteCookies("mohamed");
//            http.saml2Logout().logoutUrl("/app/logout");
//        http.logout().logoutRequestMatcher(new AntPathRequestMatcher("/app/logout"));
    }
    public  SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .authorizeRequests().anyRequest().authenticated()
                .and()
                .formLogin()
                .and()
                .logout()
                .logoutUrl("/app/logout") .logoutSuccessUrl("/login.html");
        return null;
    }

//    @Bean
//    public PasswordEncoder passwordEncoder()
//    {
//        return new BCryptPasswordEncoder();
//    }
        @Bean
    public PasswordEncoder passwordEncoder(){
        return (NoOpPasswordEncoder.getInstance());
        }
}

