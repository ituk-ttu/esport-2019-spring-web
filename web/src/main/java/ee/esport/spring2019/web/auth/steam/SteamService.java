package ee.esport.spring2019.web.auth.steam;

import com.lukaspradel.steamapi.data.json.playersummaries.GetPlayerSummaries;
import com.lukaspradel.steamapi.data.json.playersummaries.Player;
import com.lukaspradel.steamapi.webapi.client.SteamWebApiClient;
import com.lukaspradel.steamapi.webapi.request.GetPlayerSummariesRequest;
import com.lukaspradel.steamapi.webapi.request.builders.SteamWebApiRequestFactory;
import lombok.SneakyThrows;
import org.openid4java.consumer.ConsumerManager;
import org.openid4java.consumer.VerificationResult;
import org.openid4java.discovery.DiscoveryInformation;
import org.openid4java.discovery.Identifier;
import org.openid4java.message.ParameterList;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class SteamService {

    private final Pattern STEAM_ID_PATTERN = Pattern.compile("(\\d+)$");

    @Resource
    private ConsumerManager consumerManager;

    @Resource
    private DiscoveryInformation discoveryInformation;

    @Resource
    private SteamWebApiClient steamWebApiClient;

    @SneakyThrows
    public String getLoginUrl(String returnUrl) {
        return consumerManager.authenticate(discoveryInformation, returnUrl)
                              .getDestinationUrl(true);
    }

    @SneakyThrows
    public SteamUser verifySteamLogin(String receivingUrl, Map<String, String[]> parameters) {
        VerificationResult verificationResult = consumerManager.verify(receivingUrl,
                                                           new ParameterList(parameters),
                                                           discoveryInformation);
        Identifier verifiedId = verificationResult.getVerifiedId();
        if (verifiedId == null) {
            return null;
        }
        String steamId = getSteamId(verifiedId);
        if (steamId == null) {
            return null;
        }
        GetPlayerSummariesRequest playerSummariesRequest =
                SteamWebApiRequestFactory.createGetPlayerSummariesRequest(Collections.singletonList(steamId));
        GetPlayerSummaries playerSummaries = steamWebApiClient.processRequest(playerSummariesRequest);
        Player player = playerSummaries.getResponse().getPlayers().get(0);
        return new SteamUser(player);
    }

    private String getSteamId(Identifier verifiedId) {
        Matcher matcher = STEAM_ID_PATTERN.matcher(verifiedId.getIdentifier());
        return matcher.find() ? matcher.group(1) : null;
    }

}
