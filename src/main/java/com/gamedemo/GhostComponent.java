package com.gamedemo;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.texture.AnimatedTexture;
import com.almasb.fxgl.texture.AnimationChannel;
import com.almasb.fxgl.texture.Texture;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.util.Random;

public class GhostComponent extends Component {
    private final String name;
    private final double x;
    private final double y;

    private Texture left;
    private Texture right;

    private final AnimationChannel leftAnimation;
    private final AnimationChannel leftScaredAnimation;
    private final AnimationChannel rightAnimation;
    private final AnimationChannel rightScaredAnimation;
    private AnimatedTexture textureAnimation;
    private Color ghostColor = Color.RED;

    public GhostComponent(String name, double x, double y) {
        this.name = name;
        this.x = x;
        this.y = y;
        left = FXGL.texture("Ghost1.gif");
        right = FXGL.texture("Ghost2.gif");
        leftAnimation = new AnimationChannel(FXGL.image("Ghost1.gif"),12,24,24, Duration.seconds(1),0,0);
        leftScaredAnimation = new AnimationChannel(FXGL.image("GhostScared1.gif"),12,24,24, Duration.seconds(1),0,0);
        rightAnimation = new AnimationChannel(FXGL.image("Ghost2.gif"),12,24,24, Duration.seconds(1),0,0);
        rightScaredAnimation = new AnimationChannel(FXGL.image("GhostScared2.gif"),12,24,24, Duration.seconds(1),0,0);
        textureAnimation = new AnimatedTexture(leftAnimation);
    }

    @Override
    public void onAdded() {
        entity.getViewComponent().addChild(textureAnimation);
    }

    private static final double SPEED = 50;
    private double dx = 0;
    private double dy = -SPEED;

    @Override
    public void onUpdate(double tpf) {
        entity.translateX(dx * tpf);
        entity.translateY(dy * tpf);
    }

    private double getRandomSpeedAndDirection(){
        return RANDOM.nextBoolean()?SPEED:-SPEED;
    }


    private static final Random RANDOM = new Random();
    public void turn(){
        if(dx < 0){
            entity.translateX(2);
            dx = 0.0;
            dy = getRandomSpeedAndDirection();
        }else if(dx > 0){
            entity.translateX(-2);
            dx = 0.0;
            dy = getRandomSpeedAndDirection();
        }else if(dy < 0){
            entity.translateY(2);
            dy = 0.0;
            dx = getRandomSpeedAndDirection();
        }else {
            entity.translateY(-2);
            dy = 0.0;
            dx = getRandomSpeedAndDirection();

        }

//        if (dx < 0.0) {
//            entity.getViewComponent().removeChild(left);
//            entity.getViewComponent().addChild(left);
//        } else if (dx > 0.0) {
//            entity.getViewComponent().removeChild(left);
//            entity.getViewComponent().addChild(right);
//        } else {
//            entity.getViewComponent().removeChild(left);
//            entity.getViewComponent().removeChild(right);
//            entity.getViewComponent().addChild(left);
//        }
    }

    public void turnBlue(){
        textureAnimation.loopAnimationChannel(leftScaredAnimation);
        ghostColor = Color.BLUE;
        FXGL.runOnce(()->{
            textureAnimation.loopAnimationChannel(rightAnimation);
            ghostColor = Color.RED;
        }, Duration.seconds(3));
    }

    public Color getColor(){
        return ghostColor;
    }

//    public void turnRed(){
////        left = FXGL.texture("Ghost1.gif");
////        right = FXGL.texture("Ghost2.gif");
//        textureAnimation.loopAnimationChannel(rightAnimation);

//    }

}
