package com.gamedemo;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.dsl.components.DraggableComponent;
import com.almasb.fxgl.dsl.components.ExpireCleanComponent;
import com.almasb.fxgl.dsl.components.KeepOnScreenComponent;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityFactory;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.Spawns;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.entity.components.IrremovableComponent;
import com.almasb.fxgl.physics.*;
import com.almasb.fxgl.physics.box2d.dynamics.BodyDef;
import com.almasb.fxgl.physics.box2d.dynamics.BodyType;
import com.almasb.fxgl.physics.box2d.dynamics.FixtureDef;
import com.almasb.fxgl.texture.AnimatedTexture;
import com.almasb.fxgl.texture.AnimationChannel;
import javafx.application.Platform;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.util.List;
import java.util.Map;

public class DemoApp extends GameApplication {


    static final String GOLD_KEY = "gold";
    static final String FPS_KEY = "fps";
    static final String PILL_KEY = "pill";
    static final String LIFE_KEY = "life";

    private ImageView background;
    private int fps;
    private Entity player;
    private int lifeLeft = 3;
    public Color ColorOfGhost = Color.RED;
    private static int startLevel = 1;



    enum GameObj{
        PLAYER,GOLD,WALL, PILL, GHOST, FIGHT_FRUIT, LIFE;
    }


    @Override
    protected void initSettings(GameSettings gameSettings) {
        gameSettings.setWidth(800);
        gameSettings.setHeight(600);
        gameSettings.setTitle("DemoApp");
        gameSettings.setVersion("0.1");
        gameSettings.setMainMenuEnabled(true);
        gameSettings.setDeveloperMenuEnabled(true);
    }

    private static Entity getPlayer() {
        return FXGL.getGameWorld().getSingleton(GameObj.PLAYER);
    }

    private static final int SPEED = 100;
    @Override
    protected void initInput() {
        System.out.println("initInput");

        FXGL.onKey(KeyCode.UP,"MoveUp",()->{
            getPlayer().getComponent(PhysicsComponent.class).setVelocityY(-SPEED);
            getPlayer().getComponent(PlayerComponent.class).moveUp();
        });
        FXGL.onKey(KeyCode.DOWN,"MoveDown",()->{
            getPlayer().getComponent(PhysicsComponent.class).setVelocityY(SPEED);
            getPlayer().getComponent(PlayerComponent.class).moveDown();
        });
        FXGL.onKey(KeyCode.LEFT,"MoveLeft",()->{
            getPlayer().getComponent(PhysicsComponent.class).setVelocityX(-SPEED);
            getPlayer().getComponent(PlayerComponent.class).moveLeft();
        });
        FXGL.onKey(KeyCode.RIGHT,"MoveRight",()->{
            getPlayer().getComponent(PhysicsComponent.class).setVelocityX(SPEED);
            getPlayer().getComponent(PlayerComponent.class).moveRight();
        });
    }

    @Override
    protected void onPreInit() {
        System.out.println("onPreInit");
    }

    @Override
    protected void initGameVars(Map<String, Object> vars) {
        System.out.println("initGameVars");
        vars.put(GOLD_KEY, 0);
        vars.put(FPS_KEY, 0);
        vars.put(PILL_KEY, 0);
        vars.put("level", startLevel);
        vars.put(LIFE_KEY, 3);
    }

    @Override
    protected void initGame() {

        System.out.println("initGame");

        //添加背景
        FXGL.getGameWorld().addEntityFactory(new CustomEntityFactory());
        FXGL.spawn("Background",new SpawnData(0,0).put("width",800).put("height",600));

        initLevel();


        //显示fps&gold
        Rectangle2D rectangle2D = new Rectangle2D(100,100,FXGL.getAppWidth()-200,FXGL.getAppHeight()-200);//舞台范围
        FXGL.getGameTimer().runAtInterval(()->{
            FXGL.set(FPS_KEY,fps);
            fps = 0;
        },Duration.seconds(1));

    }

    private void initLevel() {
        FXGL.spawn("Background",new SpawnData(0,0).put("width",800).put("height",600));
        //FXGL.setLevelFromMap("level"+startLevel+".tmx");
        setLevelFromMapOrGameOver();
        FXGL.getWorldProperties().setValue(PILL_KEY, 0);
    }

    private void setLevelFromMapOrGameOver() {
        try{
            FXGL.setLevelFromMap("level"+FXGL.geti("level")+".tmx");
            setLifeLeft();
        } catch (IllegalArgumentException e){gameOver(true);}
    }


    @Override
    protected void initPhysics() {
        System.out.println("initPhysics");
        PhysicsWorld physics = FXGL.getPhysicsWorld();
        physics.setGravity(0, 0);
        physics.addCollisionHandler(new CollisionHandler(GameObj.PLAYER, GameObj.PILL) {
            @Override
            protected void onCollisionBegin(Entity player, Entity pill) {
                FXGL.play("pill.wav");
                pill.removeFromWorld();
                FXGL.inc(PILL_KEY, 1);
                //???
                if(FXGL.geti(PILL_KEY)>=100||FXGL.getGameWorld().getEntitiesByType(GameObj.GHOST).isEmpty()){
                    FXGL.inc("level", 1);
                    Platform.runLater(()->initLevel());
                }
            }
        });

        physics.addCollisionHandler(new CollisionHandler(GameObj.GHOST, GameObj.WALL) {
            @Override
            protected void onCollisionBegin(Entity ghost, Entity wall) {
                ghost.getComponent(GhostComponent.class).turn();
            }
        });

        physics.addCollisionHandler(new CollisionHandler(GameObj.PLAYER, GameObj.GHOST) {
            @Override
            protected void onCollision(Entity player, Entity ghost) {
                if(ColorOfGhost==Color.BLUE) {
                    ghost.removeFromWorld();
                }else{
                    FXGL.play("death.wav");
                    player.removeFromWorld();
                    lifeLeft--;
                    //减少生命数
                    List<Entity> lifes = FXGL.getGameWorld().getEntitiesByType(GameObj.LIFE);
                    for(Entity life:lifes){
                        life.removeFromWorld();
                    }
                    for(int i=0;i<lifeLeft;i++){
                        FXGL.entityBuilder()
                                .viewWithBBox(new ImageView(FXGL.image("PacMan2right.gif")))
                                .at(FXGL.getAppWidth()-100-i*24,48)
                                .type(GameObj.LIFE)
                                .buildAndAttach();
                    }

                    if(lifeLeft==0) gameOver(false);
                    else{
                        FXGL.spawn("Player",new SpawnData(0,0).put("width",800).put("height",600));
                    }

                }
            }
        });

        physics.addCollisionHandler(new CollisionHandler(GameObj.PLAYER,GameObj.FIGHT_FRUIT) {
            @Override
            protected void onCollisionBegin(Entity player, Entity fruit) {
                FXGL.play("fruit.wav");
                fruit.removeFromWorld();
                //敌人变蓝,可以击杀,10秒后变红
                FXGL.getGameWorld().getEntitiesByType(GameObj.GHOST).forEach(ghost -> {
                    ghost.getComponent(GhostComponent.class).turnBlue();
                });
                //FXGL.getGameWorld().getSingleton(GameObj.GHOST).getComponent(GhostComponent.class).turnBlue();
                ColorOfGhost = Color.BLUE;

                if(!FXGL.getGameWorld().getEntitiesByType(GameObj.GHOST).isEmpty()){
                    FXGL.getGameTimer().runOnceAfter(() -> {
                        ColorOfGhost = Color.RED;
                        FXGL.getGameWorld().getSingleton(GameObj.GHOST).getComponent(GhostComponent.class).turnRed();
                    }, Duration.seconds(10));
                }

            }
        });
    }

    private void gameOver(boolean reachEndOfGame) {
        StringBuilder builder = new StringBuilder();
        builder.append("Game Over!\n\n");
        if(reachEndOfGame) builder.append("You have reached the end of the game!\n\n");
        builder.append("Your score: ").append(FXGL.geti(PILL_KEY)).append("\n");
        builder.append("Your level: ").append(FXGL.geti("level")).append("\n");
        builder.append("Your remaining lives: ").append(lifeLeft).append("\n");
        FXGL.getDialogService().showMessageBox(builder.toString(),()->FXGL.getGameController().gotoGameMenu());
    }

    @Override
    protected void initUI() {
        System.out.println("initUI");
        Text text = FXGL.getUIFactoryService().newText("",Color.WHITE,24);
        text.textProperty().bind(FXGL.getWorldProperties().intProperty(PILL_KEY).asString("Pill: %d"));
        FXGL.addUINode(text,FXGL.getAppCenter().getX(),24);
        Text fps = FXGL.getUIFactoryService().newText("",Color.WHITE,24);
        fps.textProperty().bind(FXGL.getWorldProperties().intProperty(FPS_KEY).asString("Fps: %d"));
        FXGL.addUINode(fps,0,24);

        Label levellabel = new Label();
        levellabel.setFont(FXGL.getUIFactoryService().newFont(24));
        levellabel.setTextFill(Color.WHITE);
        levellabel.textProperty().bind(FXGL.getip("level").asString("Level: %d"));
        FXGL.addUINode(levellabel,FXGL.getAppWidth()-100,0);

        //剩余生命数
        setLifeLeft();

    }

    private void setLifeLeft() {
        for(int i=0;i<lifeLeft;i++){
            FXGL.entityBuilder()
                    .viewWithBBox(new ImageView(FXGL.image("PacMan2right.gif")))
                    .at(FXGL.getAppWidth()-100-i*20,48)
                    .type(GameObj.LIFE)
                    .buildAndAttach();
        }
    }

    @Override
    protected void onUpdate(double tpf) {
        fps++;
    }

    public static void main(String[] args) {
        if(args.length>0) startLevel = Integer.parseInt(args[0]);
        launch(args);
    }
}
