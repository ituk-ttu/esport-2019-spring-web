package ee.esport.spring2019.web.auth.steam;

import com.lukaspradel.steamapi.data.json.playersummaries.Player;
import lombok.NonNull;
import lombok.Value;

@Value
public class SteamUser {

    @NonNull private final String id;
    @NonNull private final String name;

    public SteamUser(Player player) {
        id = player.getSteamid();
        name = player.getPersonaname();
    }

}
