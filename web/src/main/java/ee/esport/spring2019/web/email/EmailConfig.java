package ee.esport.spring2019.web.email;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("email")
@RequiredArgsConstructor
@AllArgsConstructor
public class EmailConfig {

    @Setter private String domain;
    @Setter private String apiKey;
    @Setter private String fromName;
    @Setter private String fromEmail;

    @Bean
    public MailgunConfiguration getMailgunConfig() {
        return (MailgunConfiguration) new MailgunConfiguration().domain(domain)
                                                                .apiKey(apiKey)
                                                                .from(fromName, fromEmail)
                                                                .apiUrl("https://api.eu.mailgun.net/v3");
        // TODO: should probably read the apiUrl from some config file
    }

    @Bean
    public VelocityEngine getVelocityEngine() throws Exception {
        VelocityEngine engine = new VelocityEngine();
        engine.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
        engine.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
        engine.init();
        return engine;
    }

}
