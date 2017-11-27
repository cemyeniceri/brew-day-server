package org.gsu.brewday.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * Created by cyeniceri on 11/02/2017.
 */
public class AuthenticationFilter extends GenericFilterBean {

    private static final Logger LOG = LoggerFactory.getLogger(AuthenticationFilter.class);

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        final HttpServletRequest request = (HttpServletRequest) req;

        final String method = ((HttpServletRequest) req).getMethod();
        if (method.equalsIgnoreCase("options")) {
            LOG.info("Jwt filter disabled for options request");
            chain.doFilter(req, res);
            return;
        }

        final String path = ((HttpServletRequest) req).getRequestURI();
        if (path.startsWith("/principals")) {
            LOG.info("Jwt filter disabled for login request");
            chain.doFilter(req, res);
            return;
        }

        final String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new ServletException("Missing or invalid Authorization header.");
        }

        final String token = authHeader.substring(7); // The part after "Bearer "

        try {
            final Claims claims = Jwts.parser().setSigningKey("secretkey").parseClaimsJws(token).getBody();
            request.setAttribute("claims", claims);
        } catch (final SignatureException e) {
            throw new ServletException("Invalid token.");
        }

        chain.doFilter(req, res);
    }
}
