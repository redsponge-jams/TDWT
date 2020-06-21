package com.redsponge.tdwd;

import com.badlogic.gdx.graphics.Color;
import com.redsponge.redengine.assets.AssetSpecifier;
import com.redsponge.redengine.screen.AbstractScreen;
import com.redsponge.redengine.screen.systems.PhysicsSystem;
import com.redsponge.redengine.screen.systems.RenderSystem;
import com.redsponge.redengine.utils.GameAccessor;

public class GameScreen extends AbstractScreen {

    private RenderSystem rs;
    private PhysicsSystem ps;


    private int[][] geometry = {
            {0, 0, getScreenWidth() * 3, 10},
            {100, 50, 50, 10},
            {200, 50, 50, 10},
    };

    public GameScreen(GameAccessor ga) {
        super(ga);
    }

    @Override
    public void show() {
        rs = getEntitySystem(RenderSystem.class);
        ps = getEntitySystem(PhysicsSystem.class);

        rs.setBackground(Color.GRAY);

        addEntity(new Player(batch, shapeRenderer));
        for (int[] g : geometry) {
            addEntity(new WorldGeometry(batch, shapeRenderer, g[0], g[1], g[2], g[3]));
        }
    }


    @Override
    public void tick(float v) {
        tickEntities(v);
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

    public RenderSystem getRenderSystem() {
        return rs;
    }
}
