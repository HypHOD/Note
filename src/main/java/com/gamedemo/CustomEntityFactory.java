package com.gamedemo;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.dsl.components.ExpireCleanComponent;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityFactory;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.Spawns;
import com.almasb.fxgl.entity.components.IrremovableComponent;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.HitBox;
import com.almasb.fxgl.physics.PhysicsComponent;
import com.almasb.fxgl.physics.box2d.dynamics.BodyDef;
import com.almasb.fxgl.physics.box2d.dynamics.BodyType;
import com.almasb.fxgl.physics.box2d.dynamics.FixtureDef;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

public class CustomEntityFactory implements EntityFactory {

    @Spawns("FightFruitPoint")
    public Entity newFightFruitPoint(SpawnData spawnData) {
        return FXGL.entityBuilder(spawnData)
                .with(new FightFruitPointComponent())
                .build();
    }
    @Spawns("FightFruit")
    public Entity newFightFruit(SpawnData spawnData) {
        return FXGL.entityBuilder(spawnData)
                .type(DemoApp.GameObj.FIGHT_FRUIT)
                .viewWithBBox(new Rectangle(20,20, Color.RED))
                .collidable()
                .build();
    }

    @Spawns("Ghost")
    public Entity newGhost(SpawnData spawnData) {
        return FXGL.entityBuilder()
                .type(DemoApp.GameObj.GHOST)
                .bbox(new HitBox(BoundingShape.box(20, 20)))
                .with(new GhostComponent(spawnData.get("name"), spawnData.getX(), spawnData.getY()))
                .collidable()
                .build();
    }




    @Spawns("Enemy")
    public Entity newEnemy(SpawnData spawnData) {
        int time = spawnData.get("time");
        return FXGL.entityBuilder()
                .at(FXGL.getAppCenter())
                .with(new ExpireCleanComponent(Duration.seconds(time)))
                .viewWithBBox(new Rectangle(40,40, Color.RED))
                .build();
    }

    @Spawns("Player")
    public Entity spawnPlayer(SpawnData spawnData) {
        PhysicsComponent physics = new PhysicsComponent();
        physics.setFixtureDef(new FixtureDef().friction(0).density(0));
        BodyDef bd = new BodyDef();
        bd.setFixedRotation(true);
        bd.setType(BodyType.DYNAMIC);
        physics.setBodyDef(bd);
        return FXGL.entityBuilder()
                .type(DemoApp.GameObj.PLAYER)
                .viewWithBBox("player.png")
                .with(physics)
                .with(new PlayerComponent(spawnData.getX(), spawnData.getY()))
                .collidable()
                .build();
    }

    @Spawns("Background")
    public Entity spawnBackground(SpawnData spawnData) {
        return FXGL.entityBuilder()
                .view(new Rectangle(800,600,Color.BLACK))
                .with(new IrremovableComponent())
                .zIndex(-100)
                .build();
    }

    @Spawns("Wall")
    public Entity spawnWall(SpawnData spawnData) {
        return FXGL.entityBuilder(spawnData)
                .type(DemoApp.GameObj.WALL)
                .bbox(new HitBox(BoundingShape.box(spawnData.<Integer>get("width"),spawnData.<Integer>get("height"))))
                .with(new PhysicsComponent())
                .collidable()
                .build();
    }

    @Spawns("Pill")
    public Entity spawnPill(SpawnData spawnData) {
        return FXGL.entityBuilder(spawnData)
                .type(DemoApp.GameObj.PILL)
                .view("pill.png")
                .bbox(new HitBox("PILL_HIT_BOX", new Point2D(5, 5), BoundingShape.box(9, 9)))
                .collidable()
                .build();

    }



}
