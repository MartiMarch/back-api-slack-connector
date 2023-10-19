package com.valentiasoft.backapislackconnector.components

import com.valentiasoft.backapislackconnector.entities.UserEntity
import com.valentiasoft.backapislackconnector.services.JwtService
import com.valentiasoft.backapislackconnector.services.UserService
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import org.springframework.security.core.context.SecurityContextHolder

@Component
class JwtCustomFilter extends OncePerRequestFilter {

    @Autowired
    private UserService userService

    @Autowired
    private JwtService jwtService

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, java.io.IOException {
        String authorizationHeader = request.getHeader('Authorization')
        UserEntity userEntity
        String username = null
        String token = null

        if(isValidAuthorizationHeader(authorizationHeader)){
            token = authorizationHeader.substring(7)
            username = jwtService.getUsername(token)
        }

        if(isValidUsernameAndContext(username)){
            userEntity = userService.getUser(username)

            if(jwtService.isValidToken(token, userEntity)){
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    userService.loadUserByUsername(username),
                    null,
                    userService.loadUserByUsername(username).getAuthorities()
                )
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request))
                SecurityContextHolder.getContext().setAuthentication(authToken)
            }
        }

        filterChain.doFilter(request, response)
    }

    private static boolean isValidUsernameAndContext(String username){
        return username != null && !SecurityContextHolder.getContext().getAuthentication()
    }

    private static boolean isValidAuthorizationHeader(String authorizationHeader){
        return authorizationHeader != null && authorizationHeader.startsWith('Bearer ')
    }
}
