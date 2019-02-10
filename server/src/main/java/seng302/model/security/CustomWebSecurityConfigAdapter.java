package seng302.model.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;
import org.springframework.security.web.util.matcher.AndRequestMatcher;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.NegatedRequestMatcher;


/**
 * Enables security and setups up the authentication filter. Endpoints that are public can be specified here.
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class CustomWebSecurityConfigAdapter extends WebSecurityConfigurerAdapter {


    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.addFilterBefore(createCustomFilter(), AnonymousAuthenticationFilter.class).csrf().disable();
    }

    protected AbstractAuthenticationProcessingFilter createCustomFilter() throws Exception {
        AuthenticationProcessingFilter filter = new AuthenticationProcessingFilter(new NegatedRequestMatcher(
                new AndRequestMatcher(
                        // Endpoints that do not require authentication!
                        new AntPathRequestMatcher("/login", "POST"),
                        new AntPathRequestMatcher("/donors", "POST")
                )
        ));
        filter.setAuthenticationManager(authenticationManagerBean());
        return filter;
    }
}


