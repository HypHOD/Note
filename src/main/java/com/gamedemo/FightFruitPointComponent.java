package com.gamedemo;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.component.Component;
import javafx.util.Duration;

import java.util.Random;

public class FightFruitPointComponent extends Component {
    private static final Random RANDOM = new Random();

    @Override
    public void onUpdate(double tpf) {
        if((RANDOM.nextInt(1000)==0&&noFruitAt(entity.getX(),entity.getY())||FXGL.getGameWorld().getEntitiesByType(DemoApp.GameObj.FIGHT_FRUIT).isEmpty())) {
            Entity fightFruit = FXGL.spawn("FightFruit", new SpawnData(entity.getX(), entity.getY()));
            despawnLater(fightFruit);
        }
    }

    private boolean noFruitAt(double x, double y) {
        return FXGL.getGameWorld().getEntitiesByType(DemoApp.GameObj.FIGHT_FRUIT)
                .stream()
                .noneMatch(e->e.getX()==x&&e.getY()==y);
    }

    private void despawnLater(Entity fightFruit) {
        FXGL.getGameTimer().runOnceAfter(fightFruit::removeFromWorld, Duration.seconds(10));
    }
}
