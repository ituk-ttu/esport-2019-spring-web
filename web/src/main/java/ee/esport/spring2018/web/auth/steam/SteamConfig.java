package ee.esport.spring2018.web.auth.steam;

import com.lukaspradel.steamapi.webapi.client.SteamWebApiClient;
import lombok.Setter;
import org.openid4java.consumer.ConsumerManager;
import org.openid4java.discovery.DiscoveryException;
import org.openid4java.discovery.DiscoveryInformation;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("steam")
public class SteamConfig {

    private static final String STEAM_OPENID = "http://steamcommunity.com/openid";

    @Setter
    private String apiKey;

    @Bean
    public ConsumerManager getConsumerManager() {
        ConsumerManager manager = new ConsumerManager();
        manager.setMaxAssocAttempts(0);
        return manager;
    }

    @Bean
    public DiscoveryInformation getDiscoveryInformation() throws DiscoveryException {
        return getConsumerManager().associate(getConsumerManager().discover(STEAM_OPENID));
    }

    @Bean
    public SteamWebApiClient getSteamWebApiClient() {
        return new SteamWebApiClient.SteamWebApiClientBuilder(apiKey).build();
    }

}
