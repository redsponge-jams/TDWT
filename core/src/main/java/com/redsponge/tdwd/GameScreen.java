package com.redsponge.tdwd;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.DelayedRemovalArray;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.redsponge.redengine.assets.AssetSpecifier;
import com.redsponge.redengine.assets.Fonts;
import com.redsponge.redengine.lighting.LightSystem;
import com.redsponge.redengine.lighting.LightType;
import com.redsponge.redengine.physics.PhysicsDebugRenderer;
import com.redsponge.redengine.screen.AbstractScreen;
import com.redsponge.redengine.screen.systems.PhysicsSystem;
import com.redsponge.redengine.screen.systems.RenderSystem;
import com.redsponge.redengine.transitions.Transitions;
import com.redsponge.redengine.utils.GameAccessor;

import java.lang.reflect.Field;

public class GameScreen extends AbstractScreen {

    private RenderSystem rs;
    private PhysicsSystem ps;


    private Engine engine;

    private DelayedRemovalArray<Flyer> flyers;
    private DelayedRemovalArray<Coin> coins;

    private Particle star;

    private TextureRegion deadFlyerTex;
    private int deadFlyers;

    private final int[][] geometry = {
            {0, 0, getScreenWidth() * 2, 42},
    };

    private PhysicsDebugRenderer pdr;
    private LightSystem ls;
    private int added;
    private float time;
    private Particle starRain;


    private final int[][] coinLocations = {
//             {50 + 9, 90},
//             {75 + 9, 90},
//            {100 + 9, 90},
//            {150 + 9, 90},
//            {175 + 9, 90},
//            {200 + 9, 90},
//            {130, 180},
//            {130, 240},
//            {130, 300},
//            {130, 360},
//            {130, 420},
//            {130, 480},
//            {180, 520},
//            {210, 530},
//            {240, 535},
//            {270, 540},
//            {300, 542},
    };

    private FitViewport guiViewport;
    private float winTime;
    private Flyer killer;
    private TextureRegion rect;

    public GameScreen(GameAccessor ga) {
        super(ga);
    }

    @Override
    public void show() {
        guiViewport = new FitViewport(getScreenWidth(), getScreenHeight());
        coins = new DelayedRemovalArray<>();
        star = new Particle("particles/star_splash.p");
        starRain = new Particle("particles/star_rain.p");
        flyers = new DelayedRemovalArray<>();
        rs = getEntitySystem(RenderSystem.class);
        ps = getEntitySystem(PhysicsSystem.class);
        ls = new LightSystem(getScreenWidth(), getScreenHeight(), batch);
        ls.registerLightType(LightType.MULTIPLICATIVE);
        ls.registerLightType(LightType.ADDITIVE);
        ls.setAmbianceColor(new Color(0.5f, 0.6f, 1.0f, 0.2f), LightType.MULTIPLICATIVE);

        rect = assets.getTextureRegion("pixel");

        deadFlyerTex = assets.getTextureRegion("flyerDead");
        rs.setBackground(Color.BLACK);

        rs.getCamera().position.x = 200;
        try {
            Field f = AbstractScreen.class.getDeclaredField("engine");
            f.setAccessible(true);
            engine = (Engine) f.get(this);
        } catch (Exception e) {
            e.printStackTrace();
        }

        addEntity(new Island(batch, shapeRenderer, 000 + (100 - 50), 80));
        addEntity(new Island(batch, shapeRenderer, 000 + (200 - 50), 80));
        addEntity(new Island(batch, shapeRenderer, 640 - (200 - 50) - 80, 80));
        addEntity(new Island(batch, shapeRenderer, 640 - (100 - 50) - 80, 80));

        addEntity(new Island(batch, shapeRenderer, 100, 160));
        addEntity(new Island(batch, shapeRenderer, 640 - 100 - 80, 160));
        addEntity(new Cake(batch, shapeRenderer));
        addEntity(new Background(batch, shapeRenderer));
        addEntity(new Player(batch, shapeRenderer));
        for (int[] g : geometry) {
            addEntity(new WorldGeometry(batch, shapeRenderer, g[0], g[1], g[2], g[3]));
        }
        addEntity(new SlipperyGeometry(batch, shapeRenderer, -1, 0, 1, 700));
        addEntity(new SlipperyGeometry(batch, shapeRenderer, 641, 0, 1, 700));
        addEntity(new SlipperyGeometry(batch, shapeRenderer, -1, 700, 640, 1));
        addEntity(new TutorialBox(batch, shapeRenderer));

        for (int[] coinLocation : coinLocations) {
            addCoin(coinLocation[0], coinLocation[1]);
            addCoin(640 - 16 - coinLocation[0], coinLocation[1]);
        }

        pdr = new PhysicsDebugRenderer();

    }

    private Coin addCoin(int x, int y) {
        Coin c = new Coin(batch, shapeRenderer, x, y);
        addEntity(c);
        coins.add(c);
        return c;
    }


    @Override
    public void tick(float v) {
        spawnEnemies(v);
        tickEntities(v);
        if(isGameOver()) {
            rs.getCamera().position.lerp(new Vector3(killer.getX(), killer.getY(), 0), 0.1f);
        }
        clampCamera(v);
        updateEngine(v);
        batch.begin();
        batch.setProjectionMatrix(rs.getCamera().combined);
        star.tickAndRender(v, batch);
        starRain.tickAndRender(v, batch);
        batch.end();
        rs.getViewport().apply();
        ls.prepareMap(LightType.MULTIPLICATIVE, rs.getViewport());
        ls.prepareMap(LightType.ADDITIVE, rs.getViewport());
        ls.renderToScreen(LightType.MULTIPLICATIVE);
        ls.renderToScreen(LightType.ADDITIVE);

        if(coins.size == 0) {
            winTime += v;
            drawGUI();
        }
        if(isGameOver()) {
            drawGameOver();
            if(Gdx.input.isKeyJustPressed(Keys.SPACE)) {
                ga.transitionTo(new GameScreen(ga), Transitions.sineSlide(1, batch, shapeRenderer));
            }
        }
    }

    private void drawGameOver() {
        guiViewport.apply();
        batch.setProjectionMatrix(guiViewport.getCamera().combined);
        BitmapFont f = Fonts.getFont("pixelmix", 8);
        f.setColor(Color.WHITE);

        batch.begin();
        batch.setColor(0, 0, 0, 0.3f);
        batch.draw(rect, 0, guiViewport.getWorldHeight() / 2f - 15, guiViewport.getWorldWidth(), 60);
        f.draw(batch, "You failed to defend the cake :(", 0, guiViewport.getWorldHeight() / 2 + 20, guiViewport.getWorldWidth(), Align.center, false);
        f.draw(batch, "Press space to replay", 0, guiViewport.getWorldHeight() / 2, guiViewport.getWorldWidth(), Align.center, false);
        batch.end();
    }

    private void drawGUI() {
        BitmapFont f = Fonts.getFont("pixelmix", 8);
        guiViewport.apply();
        batch.setProjectionMatrix(guiViewport.getCamera().combined);
        batch.begin();
        String s = "Congratulations, You collected all coins!\nEnjoy super arrows and rainbow sword. survive as long as you can!";
        f.setColor(1, 1, 1, MathUtils.map(-1, 1, 0.8f, 1, MathUtils.sin(4 * winTime)));
        f.draw(batch, s, 0, guiViewport.getWorldHeight() - 10, getScreenWidth(), Align.center, false);

        Color c = Color.WHITE;
        c.fromHsv(10 * winTime, 0.5f, 1);
        batch.setColor(c);
        batch.draw(deadFlyerTex, 6, guiViewport.getWorldHeight() - 25);
        c.set(1, 1, 1, 1);
        batch.setColor(c);
        f.draw(batch, "x " + deadFlyers, 40, guiViewport.getWorldHeight() - 10);
        batch.end();
    }

    private void spawnEnemies(float delta) {
        int n = coins.size > 0 ? 5 : 1;
        time += delta;
        if(time > n) {
            time -= n;
            Flyer f = addFlyer(MathUtils.random(200, 440), Math.min(300 + ++added * 10, 600) - MathUtils.random(10));
            f.setSpeed(Math.min(added / 2 + 10, 50) + winTime / 4);
        }
    }

    private void clampCamera(float delta) {
        FitViewport v = rs.getViewport();
        Vector3 pos = v.getCamera().position;
        if(pos.x < v.getWorldWidth() * ((OrthographicCamera)v.getCamera()).zoom / 2) {
            pos.x = v.getWorldWidth() * ((OrthographicCamera)v.getCamera()).zoom / 2;
        }

        if(pos.x > 640 - v.getWorldWidth() * ((OrthographicCamera)v.getCamera()).zoom / 2) {
            pos.x = 640 - v.getWorldWidth() * ((OrthographicCamera)v.getCamera()).zoom / 2;
        }

        if(pos.y < v.getWorldHeight() * ((OrthographicCamera)v.getCamera()).zoom / 2) {
            pos.y = v.getWorldHeight() * ((OrthographicCamera)v.getCamera()).zoom / 2;
        }

        if(pos.y > 632 - v.getWorldHeight() * ((OrthographicCamera)v.getCamera()).zoom / 2) {
            pos.y = 632 - v.getWorldHeight() * ((OrthographicCamera)v.getCamera()).zoom / 2;
        }
    }

    private Flyer addFlyer(int x, int y) {
        Flyer f = new Flyer(batch, shapeRenderer, x, y);
        flyers.add(f);
        addEntity(f);
        return f;
    }

    @Override
    public void render() {

    }

    @Override
    public int getScreenWidth() {
        return 480;
    }

    @Override
    public int getScreenHeight() {
        return 270;
    }

    @Override
    public void reSize(int width, int height) {
        rs.resize(width, height);
        for (Entity entity : engine.getEntities()) {
            if(entity instanceof Sword) {
                ((Sword) entity).resize(width, height);
            }
        }
        ls.resize(width, height);
        guiViewport.update(width, height, true);
    }

    @Override
    public Class<? extends AssetSpecifier> getAssetSpecsType() {
        return GameAssets.class;
    }

    public RenderSystem getRenderSystem() {
        return rs;
    }

    public PhysicsSystem getPhysicsSystem() {
        return ps;
    }

    public DelayedRemovalArray<Flyer> getFlyers() {
        return flyers;
    }

    public void removeFlyer(Flyer f) {
        f.remove();
        flyers.removeValue(f, true);
    }

    public Particle getStar() {
        return star;
    }

    public LightSystem getLightSystem() {
        return ls;
    }

    public Particle getStarRain() {
        return starRain;
    }

    public DelayedRemovalArray<Coin> getCoins() {
        return coins;
    }

    public void declareDeath(Flyer flyer) {
        this.killer = flyer;
    }

    public boolean isGameOver() {
        return killer != null;
    }

    public void addFlyerDeath() {
        deadFlyers++;
    }

    @Override
    public void disposeAssets() {
        starRain.dispose();
        star.dispose();
    }
}
