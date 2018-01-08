package ee.esport.spring2018.web.web;

import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class CorsFilter extends OncePerRequestFilter {

    public static final Pattern ORIGIN_PATTERN = Pattern.compile("(.*)e-sport\\.ee(:\\d+)?");

    @Override
    protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response,
                                    final FilterChain filterChain) throws ServletException, IOException {

        String origin = request.getHeader(HttpHeaders.ORIGIN);
        if(StringUtils.hasText(origin) && isCrossOriginRequest(request)) {
            Matcher matcher = ORIGIN_PATTERN.matcher(origin);
            if(matcher.find()) {
                response.addHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, origin);
                response.addHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS, HttpHeaders.AUTHORIZATION + ", "
                                                                             + HttpHeaders.CONTENT_TYPE);
                response.addHeader(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, HttpHeaders.AUTHORIZATION + ", "
                                                                              + HttpHeaders.CONTENT_TYPE);
                response.addHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS,
                                   "GET, HEAD, POST, PUT, DELETE, OPTIONS, PATCH");
            }
        }
        filterChain.doFilter(request, response);
    }

    private boolean isCrossOriginRequest(final HttpServletRequest request) {
        return !StringUtils.isEmpty(request.getHeader(HttpHeaders.ORIGIN));
    }

}
