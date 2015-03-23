package ms.cms.service.authentication;

import ms.cms.domain.CmsUser;
import ms.cms.service.authentication.business.ReadOnlyUserManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configurers.GlobalAuthenticationConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * Application
 * Created by thebaz on 21/03/15.
 */
@Configuration
@EnableAutoConfiguration
@ComponentScan
public class Application extends WebMvcConfigurerAdapter implements CommandLineRunner {
    /**
     * Callback used to run the bean.
     *
     * @param args incoming main method arguments
     * @throws Exception on error
     */
    @Override
    public void run(String... args) throws Exception {
        SpringApplication.run(Application.class, args);
    }
    
    @Bean
    public ApplicationSecurity applicationSecurity() {
        return new ApplicationSecurity();
    }

    @Bean
    public AuthenticationSecurity authenticationSecurity() {
        return new AuthenticationSecurity();
    }
    @Order(SecurityProperties.ACCESS_OVERRIDE_ORDER)
    protected static class ApplicationSecurity extends WebSecurityConfigurerAdapter {

        @Autowired
        private SecurityProperties security;

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http
                    .authorizeRequests()
                    .antMatchers("/webjars/**", "/about", "/contact").permitAll()
                    .antMatchers("/settings/**").hasRole("ADMIN")
                    .anyRequest().authenticated();
            http
                    .formLogin()
                    .loginPage("/login")
                    .failureUrl("/login?error")
                    .permitAll()
                    .and()
                    .logout()
                    .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                    .logoutSuccessUrl("/login")
                    .and()
                    .rememberMe();
        }
    }

    @Order(Ordered.HIGHEST_PRECEDENCE + 10)
    protected static class AuthenticationSecurity extends
            GlobalAuthenticationConfigurerAdapter {

//        @Autowired
//        private DataSource dataSource;
        @Autowired
        private ReadOnlyUserManager userManager;

        @Override
        public void init(AuthenticationManagerBuilder auth) throws Exception {
            auth.userDetailsService(userManager).passwordEncoder(userManager.getEncoder());
            //auth.jdbcAuthentication().dataSource(dataSource);

//            for (CmsUser cmsUser : userManager.findAllUsers()) {
//                userManager.allocateUser(cmsUser);
//            }

//            CmsUser user = new CmsUser("user", "user", Arrays.asList(new CmsRole("ROLE_USER")));
//            user.setName("Normal user");
//            if (!userManager.userCreated("user")) {
//                userManager.createUser(user);
//            } else {
//                userManager.allocateUser(user);
//            }
//
//            CmsUser admin = new CmsUser("admin", "admin", Arrays.asList(new CmsRole("ROLE_ADMIN"), new CmsRole("ROLE_USER")));
//            admin.setName("Administrator user");
//            if (!userManager.userCreated("admin")) {
//                userManager.createUser(admin);
//            }
        }
    }
}
