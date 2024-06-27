package me.calrl.betterjoin.Functions;

import java.util.HashMap;
import java.util.Map;

public class LegacyToMiniMessageConverter {

    private static final Map<Character, String> legacyToMiniMessageMap = new HashMap<>();

    static {
        legacyToMiniMessageMap.put('0', "<black>");
        legacyToMiniMessageMap.put('1', "<dark_blue>");
        legacyToMiniMessageMap.put('2', "<dark_green>");
        legacyToMiniMessageMap.put('3', "<dark_aqua>");
        legacyToMiniMessageMap.put('4', "<dark_red>");
        legacyToMiniMessageMap.put('5', "<dark_purple>");
        legacyToMiniMessageMap.put('6', "<gold>");
        legacyToMiniMessageMap.put('7', "<gray>");
        legacyToMiniMessageMap.put('8', "<dark_gray>");
        legacyToMiniMessageMap.put('9', "<blue>");
        legacyToMiniMessageMap.put('a', "<green>");
        legacyToMiniMessageMap.put('b', "<aqua>");
        legacyToMiniMessageMap.put('c', "<red>");
        legacyToMiniMessageMap.put('d', "<light_purple>");
        legacyToMiniMessageMap.put('e', "<yellow>");
        legacyToMiniMessageMap.put('f', "<white>");
        legacyToMiniMessageMap.put('k', "<obfuscated>");
        legacyToMiniMessageMap.put('l', "<bold>");
        legacyToMiniMessageMap.put('m', "<strikethrough>");
        legacyToMiniMessageMap.put('n', "<underlined>");
        legacyToMiniMessageMap.put('o', "<italic>");
        legacyToMiniMessageMap.put('r', "<reset>");
    }

    public static String convertLegacyToMiniMessage(String input) {
        StringBuilder result = new StringBuilder();
        boolean inColorCode = false;
        char previousChar = 0;

        for (char c : input.toCharArray()) {
            if (inColorCode) {
                String miniMessageTag = legacyToMiniMessageMap.get(c);
                if (miniMessageTag != null) {
                    result.append(miniMessageTag);
                } else {
                    // If the character after § or & is not recognized, keep the original format
                    result.append(previousChar).append(c);
                }
                inColorCode = false;
            } else if (c == '§' || c == '&') {
                inColorCode = true;
                previousChar = c;
            } else {
                result.append(c);
            }
        }

        return result.toString();
    }

}

