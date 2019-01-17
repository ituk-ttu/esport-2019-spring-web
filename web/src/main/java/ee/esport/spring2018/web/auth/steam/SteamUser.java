package ee.esport.spring2018.web.auth.steam;

import com.lukaspradel.steamapi.data.json.playersummaries.Player;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SteamUser {

    public SteamUser(Player player) {
        id = player.getSteamid();
        name = player.getPersonaname();
    }

    private String id;
    private String name;

}
