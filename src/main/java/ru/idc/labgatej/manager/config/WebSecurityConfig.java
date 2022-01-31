package ru.idc.labgatej.manager.config;

import org.springframework.beans.factory.annotation.Autowired;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import ru.idc.labgatej.manager.security.jwt.AuthEntryPointJwt;
import ru.idc.labgatej.manager.security.jwt.AuthTokenFilter;
import ru.idc.labgatej.manager.security.services.UserDetailsServiceImpl;

/**
 * Реализация безопасности. Предоставляет конфигурации HttpSecurity для
 * настройки cors, csrf, управления сеансами, правил для защищенных ресурсов.
 * Также можно расширить и настроить конфигурацию по умолчанию.
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(
//    securedEnabled = true,
//    jsr250Enabled = true,
    prePostEnabled = true
)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter
{
    @Autowired
    UserDetailsServiceImpl userDetailsService;

    @Autowired
    private AuthEntryPointJwt unauthorizedHandler;

    @Bean
    public AuthTokenFilter authenticationJwtTokenFilter()
    {
        return new AuthTokenFilter();
    }

    /**
     * Переопределяем билдер менеджера аутентификации. Указываем ему на сервис
     * получения детальной информации о пользователе и указывае ему
     * @param authenticationManagerBuilder
     * @throws Exception
     */
    @Override
    public void configure(
        AuthenticationManagerBuilder authenticationManagerBuilder)
    throws Exception
    {
        authenticationManagerBuilder.userDetailsService(userDetailsService)
            .passwordEncoder(passwordEncoder());
    }

    /**
     * Предоставляем бин менеджера аутентификации.
     *
     * @return бин менеджера аутентификации.
     * @throws Exception
     *         генерируемое исключение.
     */
    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean()
    throws Exception
    {
        return super.authenticationManagerBean();
    }

    /**
     * Определяем бин для кодирования паролей.
     * @return бин для кодирования паролей.
     */
    @Bean
    public PasswordEncoder passwordEncoder()
    {
        return new BCryptPasswordEncoder();
    }

    /**
     * Классическая конфигурация spring security. Выключаем cors, csrf.
     * Назначаем точки входа для авторизации JWT, отключаем сессии в cookies.
     * Переопределяем запросы для API, авторизации и разрешаем их всем.
     * Разрешаем запросы для тестовых api. Остальные запросы должны быть
     * аутентифицированы.
     *
     * @param http
     *        объект HttpSecurity определяющий политику запросов к сервисам
     *        приложения.
     * @throws Exception
     *         генерируемое исключение.
     */
    @Override
    protected void configure(
        HttpSecurity http)
    throws Exception
    {
        http
            .cors().and().csrf().disable()
            .exceptionHandling().authenticationEntryPoint(unauthorizedHandler)
            .and().sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and().authorizeRequests().antMatchers("/api/auth/**").permitAll()
            .antMatchers("/api/test/**").permitAll()
            .antMatchers("/api/driverentrypoint").permitAll()
            .antMatchers("/api/driverentrypoint/**").permitAll()
            .anyRequest().authenticated();

        http.addFilterBefore(authenticationJwtTokenFilter(),
            UsernamePasswordAuthenticationFilter.class);
    }
}
