package ee.esport.spring2019.web.core;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

@Component
public class StringHttpMessageConverterDisabler {

    @Autowired
    private RequestMappingHandlerAdapter handlerAdapter;

    @EventListener
    public void handleContextRefresh(ContextRefreshedEvent event) {
        handlerAdapter.getMessageConverters().removeIf(StringHttpMessageConverter.class::isInstance);
        handlerAdapter.getMessageConverters().add(new StringHttpMessageConverter());
    }

}
