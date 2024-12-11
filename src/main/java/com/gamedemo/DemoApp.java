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
    static final String PILL_KEY = "pill";

    private ImageView background;
    private int fps;
    private Entity player;


    enum GameObj{
        PLAYER,GOLD,WALL, PILL, GHOST;
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
//        FXGL.onKey(KeyCode.UP,"MoveUp",()->getPlayer().translateY(-SPEED));
//        FXGL.onKey(KeyCode.DOWN,"MoveDown",()->getPlayer().translateY(SPEED));
//        FXGL.onKey(KeyCode.LEFT,"MoveLeft",()->getPlayer().translateX(-SPEED));
//        FXGL.onKey(KeyCode.RIGHT,"MoveRight",()->getPlayer().translateX(SPEED));

//        FXGL.onKey(KeyCode.UP,"MoveUp",()->getPlayer().getComponent(PhysicsComponent.class).setVelocityY(-SPEED));
//        FXGL.onKey(KeyCode.DOWN,"MoveDown",()->getPlayer().getComponent(PhysicsComponent.class).setVelocityY(SPEED));
//        FXGL.onKey(KeyCode.LEFT,"MoveLeft",()->getPlayer().getComponent(PhysicsComponent.class).setVelocityX(-SPEED));
//        FXGL.onKey(KeyCode.RIGHT,"MoveRight",()->getPlayer().getComponent(PhysicsComponent.class).setVelocityX(SPEED));

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

    }

    @Override
    protected void initGameVars(Map<String, Object> vars) {
        System.out.println("initGameVars");
        vars.put(GOLD_KEY, 0);
        vars.put(FPS_KEY, 0);
        vars.put(PILL_KEY, 0);
        vars.put("level", startLevel);
    }

    @Override
    protected void initGame() {

        System.out.println("initGame");
        //添加背景
        FXGL.getGameWorld().addEntityFactory(new CustomEntityFactory());
        FXGL.spawn("Background",new SpawnData(0,0).put("width",800).put("height",600));
        FXGL.setLevelFromMap("level1.tmx");
        //添加玩家
//        player = FXGL.entityBuilder()
//                .viewWithBBox(new ImageView(FXGL.image("player.png")))
//                .at(FXGL.getAppCenter())    //设置初始位置
//                .type(GameObj.PLAYER)   //设置类型,不设置无法碰撞
//                .collidable()//设置碰撞
//                .with(new KeepOnScreenComponent())
//                .with(new DraggableComponent())
//                .buildAndAttach();

        //显示fps&gold
        Rectangle2D rectangle2D = new Rectangle2D(100,100,FXGL.getAppWidth()-200,FXGL.getAppHeight()-200);//舞台范围
        FXGL.getGameTimer().runAtInterval(()->{
            FXGL.set(FPS_KEY,fps);
            fps = 0;

            //List<Entity> golds = FXGL.getGameWorld().getEntitiesByType(GameObj.GOLD);
//            if(golds.size() < 10){
//                Point2D point2D = FXGLMath.randomPoint(rectangle2D);
//                while(!FXGL.getGameWorld().getEntitiesInRange(new Rectangle2D(point2D.getX(), point2D.getY(), 10,10)).isEmpty()){
//                    point2D = FXGLMath.randomPoint(rectangle2D);
//                }
//
//                FXGL.entityBuilder()
//                        .viewWithBBox(new Circle(10,Color.GOLD))
//                        .at(point2D)
//                        .type(GameObj.GOLD)
//                        .collidable()
//                        .buildAndAttach();
//            }
        },Duration.seconds(1));





    }



    @Override
    protected void initPhysics() {
        System.out.println("initPhysics");
        PhysicsWorld physics = FXGL.getPhysicsWorld();
        physics.setGravity(0, 0);
        physics.addCollisionHandler(new CollisionHandler(GameObj.PLAYER, GameObj.PILL) {
            @Override
            protected void onCollisionBegin(Entity player, Entity pill) {
                pill.removeFromWorld();
                FXGL.inc(PILL_KEY, 1);
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
            protected void onCollisionBegin(Entity player, Entity ghost) {
                FXGL.getGameWorld()
                        .getEntitiesByType(GameObj.GHOST)
                        .forEach(entity -> entity.getComponent(GhostComponent.class).respawn());
                //player.getComponent(PlayerComponent.class).die();
                player.getComponent(PlayerComponent.class).respawn();
            }
        });
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
    }

    @Override
    protected void onUpdate(double tpf) {
        fps++;


    }


    public class CustomEntityFactory implements EntityFactory {
        @Spawns("Ghost")
        public Entity newGhost(SpawnData spawnData) {
            return FXGL.entityBuilder()
                    .type(GameObj.GHOST)
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
                    .viewWithBBox(new Rectangle(40,40,Color.RED))
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
                    .type(GameObj.PLAYER)
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
                    .type(GameObj.WALL)
                    .bbox(new HitBox(BoundingShape.box(spawnData.<Integer>get("width"),spawnData.<Integer>get("height"))))
                    .with(new PhysicsComponent())
                    .collidable()
                    .build();
        }

        @Spawns("Pill")
        public Entity spawnPill(SpawnData spawnData) {
            return FXGL.entityBuilder(spawnData)
                    .type(GameObj.PILL)
                    .view("pill.png")
                    .bbox(new HitBox("PILL_HIT_BOX", new Point2D(5, 5), BoundingShape.box(9, 9)))
                    .collidable()
                    .build();

        }



    }


    private static int startLevel = 1;
    public static void main(String[] args) {
        if(args.length>0) startLevel = Integer.parseInt(args[0]);
        launch(args);
    }
}
