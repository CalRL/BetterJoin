package me.calrl.betterjoin.Functions;

import me.calrl.betterjoin.BetterJoin;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.configuration.file.FileConfiguration;

public class FireworkCreator {
    
    public static FireworkEffect createFireworkEffect(String typeConfigPath, String colorConfigPath) {
        FileConfiguration config = BetterJoin.getInstance().getConfig();
        FireworkEffect.Builder builder = FireworkEffect.builder()
                .withTrail();
        try {
                    builder.with(FireworkEffect.Type.valueOf(config.getString(typeConfigPath)));
        } catch (Exception e) {
            e.printStackTrace();
        }


        String colorString = config.getString(colorConfigPath);
        Color color = getColor(colorString);
        if(color != null) {
            builder.withColor(color);
        }
        return builder.build();
    }

    private static Color getColor(String colorString) {
        try {
            return (Color) Color.class.getField(colorString.toUpperCase()).get(null);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }
    

}
