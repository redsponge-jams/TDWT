package com.redsponge.tdwd;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.redsponge.redengine.lighting.Light;
import com.redsponge.redengine.lighting.LightTextures.Point;
import com.redsponge.redengine.lighting.LightType;
import com.redsponge.redengine.lighting.PointLight;
import com.redsponge.redengine.physics.PActor;
import com.redsponge.redengine.physics.PBodyType;
import com.redsponge.redengine.physics.PSolid;
import com.redsponge.redengine.screen.components.PhysicsComponent;
import com.redsponge.redengine.screen.components.RenderRunnableComponent;
import com.redsponge.redengine.screen.entity.ScreenEntity;

public class Arrow extends ScreenEntity {

    private float x, y;
    private float startAngle;
    private PhysicsComponent physics;
    private TextureRegion tex;
    private float time;
    private boolean onGround;
    private int power;

    private float timeOnGround;
    private Vector2 tip;
    private boolean split;
    private PointLight light;

    public Arrow(SpriteBatch batch, ShapeRenderer shapeRenderer, float x, float y, float startAngle, int power) {
        super(batch, shapeRenderer);
        this.x = x;
        this.y = y;
        this.startAngle = startAngle;
        this.power = power;

        tip = new Vector2();
        split = false;
    }

    @Override
    public void added() {
        add(physics = new PhysicsComponent(PBodyType.ACTOR));
        add(new RenderRunnableComponent(this::renderArrow));
        physics.setOnCollideX(this::hitGround).setOnCollideY(this::hitGround);
        pos.set(x, y, -1);
        vel.set((float) Math.cos(MathUtils.degRad * (startAngle)), (float) Math.sin(MathUtils.degRad * (startAngle)));
        vel.set(vel.getX() * power, vel.getY() * power);
        size.set(3, 3);

        light = new PointLight(pos.getX(), pos.getY(), 30, Point.feathered);
        light.getColor().a = 0.1f;
        ((GameScreen)screen).getLightSystem().addLight(light, LightType.ADDITIVE);
        split = ((GameScreen) screen).getCoins().size == 0;
        //        render.setUseRegW(true).setUseRegH(true);
//        render.setRotation(startAngle);
    }

    private void hitGround(PSolid pSolid){
        if(!(pSolid.getConnectedEntity() instanceof SlipperyGeometry)) {
            vel.set(0, 0);
            onGround = true;
        } else {
            vel.setX(-vel.getX() * 0.5f);
            vel.setY(vel.getY() * 0.5f);
        }
    }

    private void renderArrow() {
        batch.setColor(Color.WHITE);
        if(timeOnGround > 5) {
            batch.setColor(1, 1, 1, Math.max(1 - (timeOnGround - 5), 0));
        }
        batch.draw(tex,pos.getX(), pos.getY() - 10, 1.5f, 11.5f, 3, 13, 1, 1, startAngle - 90);
    }

    @Override
    public void loadAssets() {
        super.loadAssets();
        tex = assets.getTextureRegion("arrowTex");
//        add(new TextureComponent(assets.getTextureRegion("arrowTex")));
    }

    @Override
    public void additionalTick(float delta) {
        time += delta;
        if(!onGround) {
            if(((GameScreen)screen).isGameOver()) {
                remove();
                return;
            }
            vel.setY(vel.getY() - 300 * delta);
            startAngle = MathUtils.atan2(vel.getY(), vel.getX()) * MathUtils.radDeg;
            light.pos.set(pos.getX() + vel.getX() * delta + size.getX() / 2f, pos.getY() + vel.getY() * delta + size.getY() / 2f);
//            light.pos.set(300, 100);
        } else {
            timeOnGround += delta;
            if(timeOnGround > 5) {
                light.getColor().a = MathUtils.map(5, 6, 0.1f, 0f, timeOnGround);
            }
            if(timeOnGround >= 6) remove();
        }

        checkFlyers();
    }

    private void checkFlyers() {
        if(!onGround) {
            for (Flyer flyer : ((GameScreen) screen).getFlyers()) {
                if(flyer.isAlive() && flyer.getCollision().contains(getTip())) {
                    flyer.hit(vel.getX(), vel.getY());
                    if(split) doSplit();
                    remove();
                    return;
                }
            }
        }
    }

    private void doSplit() {
        screen.addEntity(new Arrow(batch, shapeRenderer, pos.getX(), pos.getY(), 45, 300));
        screen.addEntity(new Arrow(batch, shapeRenderer, pos.getX(), pos.getY(), 45 + 90, 300));
        screen.addEntity(new Arrow(batch, shapeRenderer, pos.getX(), pos.getY(), 45 + 180, 300));
        screen.addEntity(new Arrow(batch, shapeRenderer, pos.getX(), pos.getY(), 45 + 270, 300));
    }

    private Vector2 getTip() {
        return tip.set(pos.getX() + 1, pos.getY() + 1);
    }

    @Override
    public void removed() {
        super.removed();
        ((GameScreen)screen).getPhysicsSystem().getPhysicsWorld().getActors().removeValue((PActor) physics.getBody(), true);
        if(light != null) {
            ((GameScreen) screen).getLightSystem().removeLight(light, LightType.ADDITIVE);
        }
    }
}
