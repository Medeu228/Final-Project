package medeus.finalproject.Utils;

public class WarriorSprites {

    private static final String[] SPRITE_PATHS = {
        null,
        "warrior_lv1.png",
        "warrior_lv2.png",
        "warrior_lv3.png",
    };

    public static String forLevel(int gameLevel) {
        if (gameLevel < 1 || gameLevel >= SPRITE_PATHS.length) {
            throw new IllegalArgumentException("Неверный уровень: " + gameLevel);
        }
        return SPRITE_PATHS[gameLevel];
    }
}
