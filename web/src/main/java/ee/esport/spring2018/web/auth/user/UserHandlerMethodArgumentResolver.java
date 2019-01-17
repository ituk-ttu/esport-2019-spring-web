package ee.esport.spring2018.web.auth.user;

import ee.esport.spring2018.web.auth.jwt.JwtService;
import ee.esport.spring2018.web.core.ArgumentResolverComponent;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletRequest;

@RequiredArgsConstructor
@ArgumentResolverComponent
public class UserHandlerMethodArgumentResolver implements HandlerMethodArgumentResolver {

    private final JwtService jwtService;
    private final UserService userService;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return User.class.isAssignableFrom(parameter.getParameterType());
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
        HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
        Integer userId = jwtService.validateAndGetUserId(request);
        if (userId == null) {
            return null;
        }
        return userService.findByUserId(userId);
    }

}
