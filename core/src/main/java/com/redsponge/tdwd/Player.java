package com.redsponge.tdwd;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.redsponge.redengine.lighting.LightTextures.Point;
import com.redsponge.redengine.lighting.LightType;
import com.redsponge.redengine.lighting.PointLight;
import com.redsponge.redengine.physics.PActor;
import com.redsponge.redengine.physics.PBodyType;
import com.redsponge.redengine.screen.components.PhysicsComponent;
import com.redsponge.redengine.screen.components.TextureComponent;
import com.redsponge.redengine.screen.entity.ScreenEntity;
import com.redsponge.redengine.utils.IntVector2;
import com.redsponge.redengine.utils.MathUtilities;

public class Player extends ScreenEntity {

    private PhysicsComponent physics;
    private final int acceleration = 500;
    private final float maxSpeed = 400;
    private final float friction = 0.9f;
    private float jumpSpeed = 150;
    private float fallAmplifier = -300;
    private float stopJumpAmplifier = -600;

    private boolean facingRight;
    private Sword sword;
    private Bow bow;
    private IntVector2 tmp;
    private float disabled;

    private float facingAngle;

    private PointLight light;

    private TextureRegion deadFlyer;

    public Player(SpriteBatch batch, ShapeRenderer shapeRenderer) {
        super(batch, shapeRenderer);
    }

    @Override
    public void added() {
        super.added();
        pos.set(100, 100, 2);
        size.set(8, 16);
        render.setUseRegW(true).setUseRegH(true).setOffsetX(-4);
        add(physics = new PhysicsComponent(PBodyType.ACTOR));
        physics.setOnCollideY((s) -> {
            vel.setY(0);
            disabled = 0;
        });
        tmp = new IntVector2();
        light = new PointLight(pos.getX(), pos.getY(), 60, Point.feathered);
        ((GameScreen)screen).getLightSystem().addLight(light, LightType.MULTIPLICATIVE);
    }

    @Override
    public void loadAssets() {
        add(new TextureComponent(assets.getTextureRegion("playerTex")));
    }

    @Override
    public void additionalTick(float delta) {
        disabled -= delta;
        if(disabled <= 0) updateStrafe(delta);
        updateJump(delta);
        if(!((GameScreen)screen).isGameOver() && Gdx.input.isButtonJustPressed(Buttons.LEFT) && sword == null) {
            if(facingRight) {
                screen.addEntity(sword = new Sword(batch, shapeRenderer, 1, pos, new Vector2(10, 5)));
            } else {
                screen.addEntity(sword = new Sword(batch, shapeRenderer, -1, pos, new Vector2(0, 5)));
            }
        }

        if(!((GameScreen)screen).isGameOver() && Gdx.input.isButtonPressed(Buttons.RIGHT) && ((bow == null) || bow.isShot())) {
            if(bow != null) bow.remove();
            screen.addEntity(bow = new Bow(batch, shapeRenderer, this));
        }

        render.setFlipX(!facingRight);
        if(sword != null && !sword.canRelease()) vel.set(0, 0);
        if(sword != null && sword.isDone()) sword = null;
        if(bow != null && bow.isDone()) bow = null;

        if(!((GameScreen)screen).isGameOver()) {
            ((GameScreen) screen).getRenderSystem().getCamera().position.x = MathUtilities.lerp(((GameScreen) screen).getRenderSystem().getCamera().position.x, pos.getX() + vel.getX() * delta, 0.1f);
            ((GameScreen) screen).getRenderSystem().getCamera().position.y = MathUtilities.lerp(((GameScreen) screen).getRenderSystem().getCamera().position.y, pos.getY() + vel.getY() * delta, 0.1f);
        }
        light.pos.set(pos.getX() + vel.getX() * delta + size.getX() / 2f, pos.getY() + vel.getY() * delta + size.getY() / 2f);

        checkCoins();
    }

    private void checkCoins() {
        Rectangle self = new Rectangle(pos.getX(), pos.getY(), size.getX(), size.getY());
        for (Coin coin : ((GameScreen) screen).getCoins()) {
            if(coin.getRect().overlaps(self)) {
                coin.collect();
            }
        }
    }

    private void updateJump(float delta) {
        float y = vel.getY();
        if(Controls.isJustPressed(Controls.JUMP) && canJump()) {
            y = jumpSpeed;
        }
        if(y <= 0) {
            y += fallAmplifier * delta;
        }
        if(y > 0) {
            if(!Controls.isPressed(Controls.JUMP)) {
                y += stopJumpAmplifier * delta;
            }
            else
            {
                y += fallAmplifier * delta;
            }
        }
        vel.setY(y);
    }

    private boolean canJump() {
        return ((PActor)physics.getBody()).getFirstCollision(tmp.set((int) pos.getX(), (int) pos.getY() - 1)) != null;
    }

    private void updateStrafe(float delta) {
        int horiz = (Controls.isPressed(Controls.RIGHT) ? 1 : 0) - (Controls.isPressed(Controls.LEFT) ? 1 : 0);
        float vx = vel.getX();
        vx += horiz * acceleration * delta;
        if(Math.abs(vx) > maxSpeed) {
            vx = maxSpeed * Math.signum(vx);
        }
        vx *= friction;
        if(Math.abs(vx) < 1) vx = 0;
        vel.setX(vx);
        if(horiz != 0) {
            facingRight = horiz > 0;
        }
    }

    public void whoosh(float vx, float vy) {
        vel.set(vel.getX() + vx, vel.getY() + vy);
        disabled = 0.3f;
    }
}
