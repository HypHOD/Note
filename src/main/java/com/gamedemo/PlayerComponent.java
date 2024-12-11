package com.gamedemo;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.texture.AnimatedTexture;
import com.almasb.fxgl.texture.AnimationChannel;
import javafx.util.Duration;

public class PlayerComponent extends Component {
    private final AnimatedTexture texture;
    private final AnimationChannel left;
    private final AnimationChannel right;
    private final AnimationChannel up;
    private final AnimationChannel down;
    private final double x;
    private final double y;

    public PlayerComponent(double x, double y) {
        this.x = x;
        this.y = y;

        up = new AnimationChannel(FXGL.image("sprite.png"),12,24,24,Duration.seconds(0.5),0,2);
        down = new AnimationChannel(FXGL.image("sprite.png"),12,24,24, Duration.seconds(0.5),3,5);
        left = new AnimationChannel(FXGL.image("sprite.png"),12,24,24, Duration.seconds(0.5),6,8);
        right = new AnimationChannel(FXGL.image("sprite.png"),12,24,24, Duration.seconds(0.5),9,11);
        texture = new AnimatedTexture(up);
    }

    @Override
    public void onAdded() {
        entity.getViewComponent().addChild(texture);
        texture.loopAnimationChannel(up);
    }

    public void moveLeft() {
        if(texture.getAnimationChannel() != left) texture.loopAnimationChannel(left);
    }
    public void moveRight() {
        if(texture.getAnimationChannel() != right) texture.loopAnimationChannel(right);
    }
    public void moveUp() {
        if(texture.getAnimationChannel() != up) texture.loopAnimationChannel(up);
    }
    public void moveDown() {
        if(texture.getAnimationChannel() != down) texture.loopAnimationChannel(down);
    }

    public void respawn() {
        entity.removeFromWorld();
        FXGL.spawn("Player", new SpawnData(x, y));
    }

}
