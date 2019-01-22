package ee.esport.spring2019.web.core;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.util.List;


@Slf4j
@Configuration
@RequiredArgsConstructor
public class ArgumentResolverConfigurer extends WebMvcConfigurerAdapter {

    @ArgumentResolverComponent
    private final List<HandlerMethodArgumentResolver> customResolvers;

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        log.debug("Ensuring all custom handler method argument resolvers are registered");
        argumentResolvers.addAll(customResolvers);
    }

}
