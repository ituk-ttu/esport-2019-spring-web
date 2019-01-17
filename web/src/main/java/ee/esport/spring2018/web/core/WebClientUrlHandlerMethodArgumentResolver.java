package ee.esport.spring2018.web.core;

import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletRequest;

@ArgumentResolverComponent
public class WebClientUrlHandlerMethodArgumentResolver implements HandlerMethodArgumentResolver{

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(WebClientUrl.class) &&
               String.class.isAssignableFrom(parameter.getParameterType());
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        String referer = getReferer(webRequest);
        return referer != null ? referer :
                                webRequest.getNativeRequest(HttpServletRequest.class).getRequestURL().toString();
    }

    private String getReferer(NativeWebRequest webRequest) {
        String referer = webRequest.getHeader(HttpHeaders.REFERER);
        return referer != null && CorsFilter.ORIGIN_PATTERN.matcher(referer).find() ? referer : null;
    }

}
