package com.redsponge.tdwd;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.redsponge.redengine.physics.PBodyType;
import com.redsponge.redengine.screen.components.PhysicsComponent;
import com.redsponge.redengine.screen.components.TextureComponent;
import com.redsponge.redengine.screen.entity.ScreenEntity;

public class Player extends ScreenEntity {

    private PhysicsComponent physics;
    private final int acceleration = 500;
    private final float maxSpeed = 400;
    private final float friction = 0.9f;
    private float jumpSpeed = 100;
    private float fallAmplifier = -200;
    private float stopJumpAmplifier = -300;

    private boolean facingRight;
    private Sword sw;

    public Player(SpriteBatch batch, ShapeRenderer shapeRenderer) {
        super(batch, shapeRenderer);
    }

    @Override
    public void added() {
        super.added();
        pos.set(100, 100);
        size.set(16, 16);
        add(physics = new PhysicsComponent(PBodyType.ACTOR));
        physics.setOnCollideY((s) -> vel.setY(0));
    }

    @Override
    public void loadAssets() {
        add(new TextureComponent(assets.get("playerTex", Texture.class)));
    }

    @Override
    public void additionalTick(float delta) {
        updateStrafe(delta);
        updateJump(delta);
        if(Gdx.input.isButtonJustPressed(Buttons.LEFT)) {
            if(facingRight) {
                screen.addEntity(sw = new Sword(batch, shapeRenderer, 1, pos, new Vector2(14, 5)));
            } else {
                screen.addEntity(sw = new Sword(batch, shapeRenderer, -1, pos, new Vector2(4, 5)));
            }
        }
        render.setFlipX(!facingRight);
        if(sw != null && !sw.canRelease()) vel.set(0, 0);
        if(sw != null && sw.isDone()) sw = null;
    }

    private void updateJump(float delta) {
        float y = vel.getY();
        if(Gdx.input.isKeyJustPressed(Controls.JUMP)) {
            y = jumpSpeed;
        }
        if(y <= 0) {
            y += fallAmplifier * delta;
        }
        if(y > 0) {
            if(!Gdx.input.isKeyPressed(Controls.JUMP)) {
                y += stopJumpAmplifier * delta;
            }
            else
            {
                y += fallAmplifier * delta;
            }
        }
        vel.setY(y);
    }

    private void updateStrafe(float delta) {
        int horiz = (Gdx.input.isKeyPressed(Controls.RIGHT) ? 1 : 0) - (Gdx.input.isKeyPressed(Controls.LEFT) ? 1 : 0);
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
}
