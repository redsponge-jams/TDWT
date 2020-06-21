package com.redsponge.tdwd;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.redsponge.redengine.screen.components.PositionComponent;
import com.redsponge.redengine.screen.components.RenderRunnableComponent;
import com.redsponge.redengine.screen.components.TextureComponent;
import com.redsponge.redengine.screen.entity.ScreenEntity;

public class Sword extends ScreenEntity {

    private float time;
    private final float maxTime = .5f;
    private final int dir;

    private PositionComponent attachedTo;
    private Vector2 offset;
    private FrameBuffer fbo;

    private Texture tex;

    private FitViewport viewport;
    private Texture pixel;

    public Sword(SpriteBatch batch, ShapeRenderer shapeRenderer, int dir, PositionComponent attachedTo, Vector2 offset) {
        super(batch, shapeRenderer);
        this.dir = dir;
        this.attachedTo = attachedTo;
        this.offset = offset;
        fbo = new FrameBuffer(Format.RGBA8888, 320, 180, true);
        fbo.begin();
        Gdx.gl.glClearColor(0, 0, 0, 0);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        fbo.end();

        viewport = new FitViewport(320, 180);
        viewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
    }

    @Override
    public void added() {
        pos.set(100, 100);
        size.set(13, 13);
    }

    @Override
    public void loadAssets() {
        tex = assets.get("swordTex", Texture.class);
//        tex.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
        add(new RenderRunnableComponent(this::renderToFBO));
        pixel = assets.get("pixel", Texture.class);
    }

    private void renderToFBO() {
        batch.end();
//        viewport.apply();
//        batch.setProjectionMatrix(viewport.getCamera().combined);
        fbo.begin();
        batch.begin();
        batch.setColor(1, 1, 1, 0.9f);
        batch.setBlendFunctionSeparate(GL20.GL_ZERO, GL20.GL_ONE, GL20.GL_ZERO, GL20.GL_SRC_ALPHA);
        batch.draw(pixel, 0, 0, 320, 180);
        batch.end();

        batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        if(time / maxTime < 0.35f) {
            batch.setShader(DefenseGame.onlyWhiteShader);
            batch.begin();
            for (int i = -10; i < 10; i += 5) {
                batch.draw(tex, (int) pos.getX(), (int) pos.getY(), 0, 0, 13, 13, 1.5f, 1.5f, Interpolation.swingOut.apply(time / maxTime) * dir * -180 + 45 + i, 0, 0, 13, 13, false, false);
            }
            batch.end();
        }
        fbo.end();
        batch.setShader(DefenseGame.defaultShader);
        batch.begin();

        ((GameScreen)screen).getRenderSystem().getViewport().apply();
        batch.setProjectionMatrix(((GameScreen)screen).getRenderSystem().getViewport().getCamera().combined);
        fbo.getColorBufferTexture().setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
        TextureRegion r = new TextureRegion(fbo.getColorBufferTexture());
        r.flip(false, true);
        batch.setColor(Color.WHITE);
        batch.draw(r, 0, 0);
        batch.draw(tex, (int) pos.getX(), (int) pos.getY(), 0, 0, 13, 13, 1, 1, Interpolation.swingOut.apply(time / maxTime) * dir * -180 + 45, 0, 0, 13, 13, false, false);
    }

    @Override
    public void additionalTick(float delta) {
        super.additionalTick(delta);
        time += delta;
        pos.set(attachedTo.getX() + offset.x, attachedTo.getY() + offset.y);

        if(time >= maxTime) remove();
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
        System.out.println("hi");
        viewport.update(width, height, true);
    }
}
