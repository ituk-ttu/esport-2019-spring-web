package ee.esport.spring2018.web.web;

import ee.esport.spring2018.web.auth.ClaimsHandlerMethodArgumentResolver;
import ee.esport.spring2018.web.auth.JwtService;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import javax.annotation.Resource;
import java.util.List;


@Configuration
public class WebConfig extends WebMvcConfigurerAdapter {

    @Resource
    private JwtService jwtService;

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(new ClaimsHandlerMethodArgumentResolver(jwtService));
        argumentResolvers.add(new WebClientUrlHandlerMethodArgumentResolver());
    }

}
