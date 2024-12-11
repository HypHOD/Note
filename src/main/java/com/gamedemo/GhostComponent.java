package com.gamedemo;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.texture.Texture;
import javafx.scene.text.Text;

import java.util.Random;

public class GhostComponent extends Component {
    private final String name;
    private final double x;
    private final double y;

    private final Texture left;
    private final Texture right;

    public GhostComponent(String name, double x, double y) {
        this.name = name;
        this.x = x;
        this.y = y;
        left = FXGL.texture("Ghost1.gif");
        right = FXGL.texture("Ghost2.gif");
    }

    @Override
    public void onAdded() {
        entity.getViewComponent().addChild(left);
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

        if (dx < 0.0) {
            entity.getViewComponent().removeChild(left);
            entity.getViewComponent().addChild(left);
        } else if (dx > 0.0) {
            entity.getViewComponent().removeChild(left);
            entity.getViewComponent().addChild(right);
        } else {
            entity.getViewComponent().removeChild(left);
            entity.getViewComponent().removeChild(right);
            entity.getViewComponent().addChild(left);
        }
    }

    public void respawn(){
        entity.removeFromWorld();
        FXGL.spawn("Ghost",new SpawnData(x,y).put("name",name));

    }
}
