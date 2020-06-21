package com.redsponge.tdwd;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.redsponge.redengine.assets.AssetSpecifier;
import com.redsponge.redengine.screen.AbstractScreen;
import com.redsponge.redengine.screen.systems.PhysicsSystem;
import com.redsponge.redengine.screen.systems.RenderSystem;
import com.redsponge.redengine.utils.GameAccessor;

public class GameScreen extends AbstractScreen {

    private RenderSystem rs;
    private PhysicsSystem ps;

    public GameScreen(GameAccessor ga) {
        super(ga);
    }

    @Override
    public void show() {
        rs = getEntitySystem(RenderSystem.class);
        ps = getEntitySystem(PhysicsSystem.class);

        rs.setBackground(Color.WHITE);

        addEntity(new Player(batch, shapeRenderer));
    }


    @Override
    public void tick(float v) {
        updateEngine(v);
    }

    @Override
    public void render() {

    }

    @Override
    public int getScreenWidth() {
        return 320;
    }

    @Override
    public int getScreenHeight() {
        return 180;
    }

    @Override
    public void reSize(int width, int height) {
        rs.resize(width, height);
    }

    @Override
    public Class<? extends AssetSpecifier> getAssetSpecsType() {
        return GameAssets.class;
    }
}
