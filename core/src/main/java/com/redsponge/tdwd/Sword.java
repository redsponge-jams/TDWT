package com.redsponge.tdwd;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.redsponge.redengine.screen.components.PositionComponent;
import com.redsponge.redengine.screen.components.RenderRunnableComponent;
import com.redsponge.redengine.screen.entity.ScreenEntity;

public class Sword extends ScreenEntity {

    private float time;
    private final float maxTime = 0.5f;
    private final int dir;

    private PositionComponent attachedTo;
    private Vector2 offset;
    private FrameBuffer fbo;

    private TextureRegion tex;
    private TextureRegion fboTR;

    private FitViewport viewport;
    private TextureRegion pixel;
    private float start;

    private Rectangle attackBox;

    public Sword(SpriteBatch batch, ShapeRenderer shapeRenderer, int dir, PositionComponent attachedTo, Vector2 offset) {
        super(batch, shapeRenderer);
        this.dir = dir;
        this.attachedTo = attachedTo;
        this.offset = offset;
        fbo = new FrameBuffer(Format.RGBA8888, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
        fbo.begin();
        Gdx.gl.glClearColor(0, 0, 0, 0);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        fbo.end();

        fboTR = new TextureRegion(fbo.getColorBufferTexture());
        fboTR.flip(false, true);
        fboTR.getTexture().setFilter(TextureFilter.Nearest, TextureFilter.Nearest);

        viewport = new FitViewport(480, 270);
        viewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);

        attackBox = new Rectangle();
    }

    @Override
    public void added() {
        pos.set(100, 100);
        size.set(13, 13);
    }

    @Override
    public void loadAssets() {
        tex = assets.getTextureRegion("swordTex");
        add(new RenderRunnableComponent(this::render));
        pixel = assets.getTextureRegion("pixel");
        assets.get("swordWhooshSound", Sound.class).play(Settings.soundVolume);
    }

    private void decaySmear(float multiplier) {
        batch.setColor(1, 1, 1, multiplier);
        batch.setBlendFunctionSeparate(GL20.GL_ZERO, GL20.GL_ONE, GL20.GL_ZERO, GL20.GL_SRC_ALPHA);
        batch.draw(pixel, ((GameScreen)screen).getRenderSystem().getCamera().position.x - ((GameScreen) screen).getRenderSystem().getViewport().getWorldWidth() / 2f, 0, screen.getScreenWidth(), 643);
        batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
    }

    private void renderFadeSmear() {
        batch.end();
        fbo.begin();

        batch.begin();
        decaySmear(0.7f);

        batch.setColor(Color.WHITE);
        for (float f = start; f < time; f += 0.001f) {
            float rot = Interpolation.swingOut.apply(f / maxTime);
            if (dir > 0) {
                rot *= -90;
            } else {
                rot *= 90;
                rot += 90;
            }
            batch.draw(tex, (int) pos.getX(), (int) pos.getY(), 0, 0, 13, 13, 1, 1, rot);
        }
        batch.end();
        start = time;


        fbo.end();
        batch.begin();
        ((GameScreen)screen).getRenderSystem().getViewport().apply();
        batch.setProjectionMatrix(((GameScreen)screen).getRenderSystem().getViewport().getCamera().combined);
        batch.draw(fboTR, ((GameScreen) screen).getRenderSystem().getCamera().position.x - ((GameScreen) screen).getRenderSystem().getViewport().getWorldWidth() / 2,
                ((GameScreen) screen).getRenderSystem().getCamera().position.y - ((GameScreen) screen).getRenderSystem().getViewport().getWorldHeight() / 2, screen.getScreenWidth(), screen.getScreenHeight());
    }

    private void renderRainbowSmear() {
        batch.end();
        fbo.begin();

        batch.begin();
        decaySmear(0.9f);

        batch.setColor(Color.WHITE);
        for (float f = start; f < time; f += 0.001f) {
            float rot = Interpolation.swingOut.apply(f / maxTime);
            if (dir > 0) {
                rot *= -90;
            } else {
                rot *= 90;
                rot += 90;
            }
            float h = f / maxTime;
            batch.setColor(new Color(1, 1, 1, 1).fromHsv(h * 270, 0.8f, 1));
            batch.draw(tex, (int) pos.getX(), (int) pos.getY(), 0, 0, 13, 13, 1.5f, 1.5f, rot);
        }
        start = time;
        batch.end();

        fbo.end();
        batch.begin();
        ((GameScreen)screen).getRenderSystem().getViewport().apply();
        batch.setProjectionMatrix(((GameScreen)screen).getRenderSystem().getViewport().getCamera().combined);
        batch.setColor(1, 1, 1, 0.5f);
        batch.draw(fboTR, ((GameScreen) screen).getRenderSystem().getCamera().position.x - ((GameScreen) screen).getRenderSystem().getViewport().getWorldWidth() / 2,
                ((GameScreen) screen).getRenderSystem().getCamera().position.y - ((GameScreen) screen).getRenderSystem().getViewport().getWorldHeight() / 2, screen.getScreenWidth(), screen.getScreenHeight());
        float rot = Interpolation.swingOut.apply(time / maxTime);
        if(dir > 0) {
            rot *= -90;
        } else {
            rot *= 90;
            rot += 90;
        }
        float h = time / maxTime;
        batch.setColor(new Color(1, 1, 1, 1).fromHsv(h * 270, 0.8f, 1));
        batch.draw(tex, (int) pos.getX(), (int) pos.getY(), 0, 0, 13, 13, 1, 1, rot);
    }

    private void renderWhiteSmear() {
        batch.end();
        fbo.begin();
        batch.begin();

        decaySmear(0.90f);

        batch.setColor(Color.WHITE);
        batch.setShader(DefenseGame.onlyWhiteShader);
        if(time / maxTime < 0.35) {
            for (float f = start; f < time; f += 0.001f) {
                float rot = Interpolation.swingOut.apply(f / maxTime);
                if (dir > 0) {
                    rot *= -90;
                } else {
                    rot *= 90;
                    rot += 90;
                }
                batch.draw(tex, (int) pos.getX(), (int) pos.getY(), 0, 0, 13, 13, 1.5f, 1.5f, rot);
            }
        }
        start = time;
        batch.end();
        batch.setShader(DefenseGame.defaultShader);

        fbo.end();
        batch.begin();
        ((GameScreen)screen).getRenderSystem().getViewport().apply();
        batch.setProjectionMatrix(((GameScreen)screen).getRenderSystem().getViewport().getCamera().combined);
        batch.setColor(1, 1, 1, 0.5f);

        batch.draw(fboTR, ((GameScreen) screen).getRenderSystem().getCamera().position.x - ((GameScreen) screen).getRenderSystem().getViewport().getWorldWidth() / 2,
                ((GameScreen) screen).getRenderSystem().getCamera().position.y - ((GameScreen) screen).getRenderSystem().getViewport().getWorldHeight() / 2, screen.getScreenWidth(), screen.getScreenHeight());
        float rot = Interpolation.swingOut.apply(time / maxTime);
        if(dir > 0) {
            rot *= -90;
        } else {
            rot *= 90;
            rot += 90;
        }
        batch.setColor(Color.WHITE);
        batch.draw(tex, pos.getX(), pos.getY(), 0, 0, 13, 13, 1, 1, rot);
    }

    private void render() {
        if(((GameScreen)screen).getCoins().size > 0) {
            renderWhiteSmear();
        } else {
            renderRainbowSmear();
        }
    }

    @Override
    public void additionalTick(float delta) {
        if(((GameScreen)screen).isGameOver()) {
            remove();
            return;
        }
        time += delta;
        pos.set(attachedTo.getX() + offset.x, attachedTo.getY() + offset.y);
        int h = 25;
        attackBox.set(pos.getX() + (dir < 0 ? -30 : 0), pos.getY() + Interpolation.swingOut.apply(time / maxTime) * -h + 15, 30, Interpolation.swingOut.apply(time / maxTime) * h);


        checkFlyers();
        if(time >= maxTime) remove();
    }

    private void checkFlyers() {
        Array<Flyer> flyers = ((GameScreen)screen).getFlyers();
        for (int i = 0; i < flyers.size; i++) {
            Flyer f = flyers.get(i);
            if(f.isAlive() && Intersector.overlaps(f.getCollision(), attackBox)) {
                f.hit(dir * 30, 30 * (1 - Interpolation.swingOut.apply(time / maxTime)));
            }
        }
    }

    public Rectangle getAttackBox() {
        return attackBox;
    }

    public boolean canRelease() {
        return time >= maxTime - 0.1f;
    }

    public boolean isDone() {
        return time >= maxTime;
    }

    @Override
    public void removed() {
        fbo.dispose();
    }

    public void resize(int width, int height) {
        viewport.update(width, height);
        if(fbo != null) {
            fbo.dispose();
        }
        fbo = new FrameBuffer(Format.RGBA8888, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
        fboTR.setTexture(fbo.getColorBufferTexture());
    }
}
