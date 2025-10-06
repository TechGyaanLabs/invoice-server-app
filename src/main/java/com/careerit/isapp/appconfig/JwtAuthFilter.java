package com.careerit.isapp.appconfig;

import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    public JwtAuthFilter(JwtService jwtService, UserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
        String token = authHeader.substring(7);
        try {
            DecodedJWT decoded = jwtService.validateToken(token);
            String username = decoded.getSubject();

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                // Option 1: trust token claims and don't load user details
                // Option 2 (safer): load user details from DB to ensure user still exists/roles are current
                UserDetails user = userDetailsService.loadUserByUsername(username);

                // Build authorities from token or from user object (prefer user object)
                List<String> rolesFromToken = decoded.getClaim("roles").asList(String.class);
                var authorities = (rolesFromToken == null)
                        ? user.getAuthorities()
                        : rolesFromToken.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(user, null, authorities);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception ex) {
            // token invalid/expired: clear context and continue (request will be rejected by security)
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }
}
