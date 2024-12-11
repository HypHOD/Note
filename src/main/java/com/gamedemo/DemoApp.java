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
import com.almasb.fxgl.physics.CollisionHandler;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
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

    private ImageView background;
    private int fps;
    private Entity player;

    enum GameObj{
        PLAYER,GOLD;
    }


    @Override
    protected void initSettings(GameSettings gameSettings) {
        gameSettings.setWidth(800);
        gameSettings.setHeight(600);
        gameSettings.setTitle("DemoApp");
        gameSettings.setVersion("0.1");
        gameSettings.setMainMenuEnabled(true);
    }

    @Override
    protected void initInput() {
        System.out.println("initInput");
        FXGL.onKey(KeyCode.UP,()->player.translateY(-5));
        FXGL.onKey(KeyCode.DOWN,()->player.translateY(5));
        FXGL.onKey(KeyCode.LEFT,()->player.translateX(-5));
        FXGL.onKey(KeyCode.RIGHT,()->player.translateX(5));
    }


    @Override
    protected void onPreInit() {
        System.out.println("onPreInit");
        Image image = FXGL.image("background.png");
        background = new ImageView(image);
        background.setFitWidth(800);
        background.setFitHeight(600);

    }

    @Override
    protected void initGameVars(Map<String, Object> vars) {
        System.out.println("initGameVars");
        vars.put(GOLD_KEY, 0);
        vars.put(FPS_KEY, 0);
    }

    @Override
    protected void initGame() {
        System.out.println("initGame");
        //添加背景
        FXGL.entityBuilder().view(background).buildAndAttach();
        //添加玩家
        player = FXGL.entityBuilder()
                .viewWithBBox(new ImageView(FXGL.image("player.png")))
                .at(FXGL.getAppCenter())    //设置初始位置
                .type(GameObj.PLAYER)   //设置类型,不设置无法碰撞
                .collidable()//设置碰撞
                .with(new KeepOnScreenComponent())
                .with(new DraggableComponent())
                .buildAndAttach();

        Rectangle2D rectangle2D = new Rectangle2D(100,100,FXGL.getAppWidth()-200,FXGL.getAppHeight()-200);//舞台范围
        FXGL.getGameTimer().runAtInterval(()->{
            FXGL.set(FPS_KEY,fps);
            fps = 0;

            List<Entity> golds = FXGL.getGameWorld().getEntitiesByType(GameObj.GOLD);
            if(golds.size() < 10){
                Point2D point2D = FXGLMath.randomPoint(rectangle2D);
                while(!FXGL.getGameWorld().getEntitiesInRange(new Rectangle2D(point2D.getX(), point2D.getY(), 10,10)).isEmpty()){
                    point2D = FXGLMath.randomPoint(rectangle2D);
                }

                FXGL.entityBuilder()
                        .viewWithBBox(new Circle(10,Color.GOLD))
                        .at(point2D)
                        .type(GameObj.GOLD)
                        .collidable()
                        .buildAndAttach();
            }
        },Duration.seconds(1));


        FXGL.getGameWorld().addEntityFactory(new CustomEntityFactory());
        FXGL.spawn("enemy",new SpawnData(200,200).put("time",5));

    }

    @Override
    protected void initPhysics() {
        System.out.println("initPhysics");
        FXGL.getPhysicsWorld().addCollisionHandler(new CollisionHandler(GameObj.PLAYER, GameObj.GOLD) {
            @Override
            protected void onCollisionBegin(Entity player, Entity gold) {
                gold.removeFromWorld();
                FXGL.inc(GOLD_KEY, 1);
            }
        });
    }

    @Override
    protected void initUI() {
        System.out.println("initUI");
        Text text = FXGL.getUIFactoryService().newText("",Color.BLACK,24);
        text.textProperty().bind(FXGL.getWorldProperties().intProperty(GOLD_KEY).asString("Gold: %d"));
        FXGL.addUINode(text,FXGL.getAppCenter().getX(),24);
        Text fps = FXGL.getUIFactoryService().newText("",Color.BLACK,24);
        fps.textProperty().bind(FXGL.getWorldProperties().intProperty(FPS_KEY).asString("Fps: %d"));
        FXGL.addUINode(fps,0,24);
    }

    @Override
    protected void onUpdate(double tpf) {
        fps++;
    }

    public static class CustomEntityFactory implements EntityFactory{
        @Spawns("enemy")
        public Entity newEnemy(SpawnData spawnData) {
            int time = spawnData.get("time");
            return FXGL.entityBuilder()
                    .at(FXGL.getAppCenter())
                    .with(new ExpireCleanComponent(Duration.seconds(time)))
                    .viewWithBBox(new Rectangle(40,40,Color.RED))
                    .build();
        }


    }

    public static void main(String[] args) {launch(args);}
}
