package medeus.finalproject.Utils;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;


public class AnimationSet {

    public final Animation<TextureRegion> walkDown;
    public final Animation<TextureRegion> walkLeft;
    public final Animation<TextureRegion> walkRight;
    public final Animation<TextureRegion> walkUp;

    public final TextureRegion idleDown;
    public final TextureRegion idleLeft;
    public final TextureRegion idleRight;
    public final TextureRegion idleUp;

    private final Texture spriteSheet;


    private AnimationSet(Texture sheet,
                         Animation<TextureRegion> walkDown,
                         Animation<TextureRegion> walkLeft,
                         Animation<TextureRegion> walkRight,
                         Animation<TextureRegion> walkUp,
                         TextureRegion idleDown,
                         TextureRegion idleLeft,
                         TextureRegion idleRight,
                         TextureRegion idleUp) {
        this.spriteSheet = sheet;
        this.walkDown  = walkDown;
        this.walkLeft  = walkLeft;
        this.walkRight = walkRight;
        this.walkUp    = walkUp;
        this.idleDown  = idleDown;
        this.idleLeft  = idleLeft;
        this.idleRight = idleRight;
        this.idleUp    = idleUp;
    }

    public static AnimationSet load(String texturePath) {
        return load(texturePath, 64, 64, 0.15f, 3);
    }

    public static AnimationSet load(String texturePath,
                                    int frameW, int frameH,
                                    float frameDuration,
                                    int walkFrames) {
        Texture sheet = new Texture(texturePath);
        TextureRegion[][] split = TextureRegion.split(sheet, frameW, frameH);

        return new AnimationSet(
            sheet,
            buildAnim(split, 0, 1, walkFrames, frameDuration),
            buildAnim(split, 1, 1, walkFrames, frameDuration),
            buildAnim(split, 2, 1, walkFrames, frameDuration),
            buildAnim(split, 3, 1, walkFrames, frameDuration),
            split[0][0], split[1][0], split[2][0], split[3][0]
        );
    }


    public TextureRegion getFrame(String direction, boolean moving, float stateTime) {
        if (moving) {
            switch (direction) {
                case "up":    return walkUp.getKeyFrame(stateTime, true);
                case "left":  return walkLeft.getKeyFrame(stateTime, true);
                case "right": return walkRight.getKeyFrame(stateTime, true);
                default:      return walkDown.getKeyFrame(stateTime, true);
            }
        } else {
            switch (direction) {
                case "up":    return idleUp;
                case "left":  return idleLeft;
                case "right": return idleRight;
                default:      return idleDown;
            }
        }
    }

    public void dispose() {
        spriteSheet.dispose();
    }

    private static Animation<TextureRegion> buildAnim(TextureRegion[][] split,
                                                       int row,
                                                       int startCol,
                                                       int count,
                                                       float frameDuration) {
        TextureRegion[] frames = new TextureRegion[count];
        for (int i = 0; i < count; i++) {
            frames[i] = split[row][startCol + i];
        }
        return new Animation<>(frameDuration, frames);
    }
}
