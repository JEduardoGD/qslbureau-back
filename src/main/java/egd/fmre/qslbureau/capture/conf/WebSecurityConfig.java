package egd.fmre.qslbureau.capture.conf;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;

import egd.fmre.qslbureau.capture.component.JwtAuthenticationEntryPoint;
import egd.fmre.qslbureau.capture.component.JwtRequestFilter;
import egd.fmre.qslbureau.capture.service.impl.JwtUserDetailsService;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {
    @Autowired          JwtUserDetailsService      jwtUserDetailsService;
    @Autowired private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @Bean
    JwtRequestFilter authenticationJwtTokenFilter() {
        return new JwtRequestFilter();
    }

    @Bean
    DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();

        authProvider.setUserDetailsService(jwtUserDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());

        return authProvider;
    }

    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /*
    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.cors().and().csrf().disable()
        // dont authenticate this particular request
        .authorizeRequests().antMatchers("/authenticate").permitAll()
        .and().authorizeRequests().antMatchers("/actuator/**").permitAll()
        .and().authorizeRequests().antMatchers("/portal/**").permitAll()
        //.and().authorizeRequests().antMatchers("/actuator/**").permitAll()
        // all other requests need to be authenticated
        .anyRequest().authenticated().and()
        // make sure we use stateless session; session won't be used to
        // store user's state.
        .exceptionHandling().authenticationEntryPoint(jwtAuthenticationEntryPoint).and().sessionManagement()
        .sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        http.authenticationProvider(authenticationProvider());

        http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
    */
    
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		return http
				.authorizeHttpRequests(authz -> authz
						.requestMatchers(new AntPathRequestMatcher("/authenticate/**", HttpMethod.POST.name())).permitAll()
						.requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/actuator/**", "/portal/**").permitAll()
						//.requestMatchers("/authenticate", "/actuator/**", "/portal/**", "/actuator/**", "/swagger-ui/**").permitAll()
						//.requestMatchers("/**").permitAll()
						.anyRequest().authenticated())
				// .formLogin(c -> c.loginPage("/login").permitAll())
				// .logout(LogoutConfigurer::permitAll)
				.exceptionHandling(r -> r.authenticationEntryPoint(jwtAuthenticationEntryPoint))
				.sessionManagement(r -> r.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class)
				.cors(cors -> cors.configurationSource(request -> {
					CorsConfiguration configuration = new CorsConfiguration();
					configuration.setAllowedOrigins(Arrays.asList("*"));
					configuration.setAllowedMethods(Arrays.asList("*"));
					configuration.setAllowedHeaders(Arrays.asList("*"));
					return configuration;
				})).csrf(c -> c.disable()).build();
	}
    
    
}