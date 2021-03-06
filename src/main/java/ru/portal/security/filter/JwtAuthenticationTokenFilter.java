package ru.portal.security.filter;


import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;
import ru.portal.security.services.TokenService;
import ru.portal.security.utilities.AuthenticatedUtility;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;

/**
 * Фильтр аутентифицирует пользователя для доступа к защищенным ресурсам
 * Проверяет наличие заголовка Authorization и время жизни токена.
 *
 * @author Федорышин К.В.
 */
@FieldDefaults(level = AccessLevel.PRIVATE)
@Component
@Slf4j
public class JwtAuthenticationTokenFilter extends GenericFilterBean {

    @Value("${security.token.headerAuthorizationName}")
    String headerName;

    final TokenService tokenService;

    final UserDetailsService userDetailsService;

    @Autowired
    public JwtAuthenticationTokenFilter(TokenService tokenService, UserDetailsService userDetailsService) {
        this.tokenService = tokenService;
        this.userDetailsService = userDetailsService;
    }


    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
                         FilterChain chain) throws IOException, ServletException {


        var request = (HttpServletRequest) servletRequest;
        var response = (HttpServletResponse) servletResponse;

        var header = request.getHeader(headerName);

        if (Objects.nonNull(header)) {

            var token = tokenService.getToken(header);

            if (tokenService.isValidToken(token)) {
                var user = userDetailsService.loadUserByUsername(tokenService.getUsername(token));
                AuthenticatedUtility.authentication(user);
            }
        }
        chain.doFilter(request, response);
    }
}
