package com.redsponge.tdwd;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.redsponge.redengine.screen.components.AnimationComponent;
import com.redsponge.redengine.screen.components.Mappers;
import com.redsponge.redengine.screen.components.PositionComponent;
import com.redsponge.redengine.screen.components.VelocityComponent;
import com.redsponge.redengine.screen.entity.ScreenEntity;

public class Bow extends ScreenEntity {

    private float time;
    private float drawTime;

    private AnimationComponent anim;
    private ScreenEntity attachedTo;
    private PositionComponent attachedToPos;
    private VelocityComponent attachedToVel;

    private float rad = 16;
    private boolean shot;


    private Sound pewSound;
    private Sound armedSound;
    private boolean playedArmedSound;
    private boolean done;

    public Bow(SpriteBatch batch, ShapeRenderer shapeRenderer, ScreenEntity attachedTo) {
        super(batch, shapeRenderer);
        this.attachedTo = attachedTo;
        this.attachedToPos = Mappers.position.get(attachedTo);
        this.attachedToVel = Mappers.velocity.get(attachedTo);
        time = 0;
    }

    @Override
    public void added() {
        super.added();
        size.set(18, 15);
    }

    @Override
    public void loadAssets() {
        add(anim = new AnimationComponent(assets.getAnimation("bowAnimation")));
        armedSound = assets.get("bowArmedSound", Sound.class);
        pewSound = assets.get("bowPewSound", Sound.class);
    }

    @Override
    public void additionalTick(float delta) {
        if(Gdx.input.isButtonPressed(Buttons.RIGHT)) {
            if(anim.getAnimation().getKeyFrameIndex(anim.getAnimationTime()) == 2) {
            }
            if(anim.getAnimation().getKeyFrameIndex(anim.getAnimationTime()) >= 3) {
                if (!playedArmedSound) {
                    armedSound.play(Settings.soundVolume);
                    playedArmedSound = true;
                }
                anim.setAnimationTime(anim.getAnimation().getFrameDuration() * 3);
            }
        } else if(anim.getAnimation().getKeyFrameIndex(anim.getAnimationTime()) < 3) {
            remove();
            return;
        } else {
            if(!shot) {
                pewSound.play(Settings.soundVolume);
                screen.addEntity(new Arrow(batch, shapeRenderer, attachedToPos.getX(), attachedToPos.getY(), render.getRotation(), 300));
                float vx = MathUtils.cosDeg(180 + render.getRotation()) * 100;
                float vy = MathUtils.sinDeg(180 + render.getRotation()) * 100;
                ((Player)attachedTo).whoosh(vx, vy);
                shot = true;
            }
        }

        pos.set(attachedToPos.getX() + rad - 4, attachedToPos.getY());
        render.setRenderOriginX(-rad);

        if(anim.getAnimation().getKeyFrameIndex(anim.getAnimationTime()) < 4) {
            ((GameScreen) screen).getRenderSystem().getViewport().apply();
            Vector3 base = new Vector3(pos.getX(), pos.getY(), 0);//((GameScreen) screen).getRenderSystem().getCamera().position;
            Vector2 mouse = ((GameScreen) screen).getRenderSystem().getViewport().unproject(new Vector2(Gdx.input.getX(), Gdx.input.getY()));
            render.setRotation(MathUtils.radiansToDegrees * MathUtils.atan2(base.y - mouse.y, base.x - mouse.x));
        }

        if(anim.getAnimation().isAnimationFinished(anim.getAnimationTime())) remove();
    }

    @Override
    public void removed() {
        done = true;
    }

    public boolean isDone() {
        return done;
    }

    public boolean isShot() {
        return shot;
    }
}
