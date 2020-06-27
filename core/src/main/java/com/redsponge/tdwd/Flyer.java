package com.redsponge.tdwd;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.redsponge.redengine.lighting.LightTextures.Point;
import com.redsponge.redengine.lighting.LightType;
import com.redsponge.redengine.lighting.PointLight;
import com.redsponge.redengine.physics.PActor;
import com.redsponge.redengine.physics.PBodyType;
import com.redsponge.redengine.screen.components.PhysicsComponent;
import com.redsponge.redengine.screen.components.RenderRunnableComponent;
import com.redsponge.redengine.screen.entity.ScreenEntity;
import com.redsponge.redengine.utils.GeneralUtils;
import com.redsponge.redengine.utils.IntVector2;

public class Flyer extends ScreenEntity {

    private static final float COLLISION_RADIUS = 10;
    private int sx, sy;
    private Animation<TextureRegion> body, eye, wing;
    private float time;

    private Color color;

    private float rotation;
    private Circle collision;

    private TextureRegion dead;
    private float onGroundTime;

    private Sound hitSound;

    private PointLight light;

    private static final Color[] colors = {
            Color.RED, Color.ORANGE, Color.GOLD, Color.YELLOW, Color.TEAL, Color.LIME, Color.LIGHT_GRAY, Color.PINK,
            Color.FOREST, Color.WHITE, Color.CHARTREUSE, Color.ROYAL, Color.MAGENTA, Color.MAROON,
            Color.CORAL, Color.SKY
    };
    private float speed;
    private boolean isAlive;
    private PhysicsComponent phys;
    private PointLight mulLight;

    public Flyer(SpriteBatch batch, ShapeRenderer shapeRenderer, int sx, int sy) {
        this(batch, shapeRenderer, sx, sy, GeneralUtils.randomItem(colors).cpy().add(0.1f, 0.1f, 0.1f, 0.1f));
    }

    public Flyer(SpriteBatch batch, ShapeRenderer shapeRenderer, int sx, int sy, Color color) {
        super(batch, shapeRenderer);
        this.sx = sx;
        this.sy = sy;
        this.time = MathUtils.random();
        this.color = color;

        collision = new Circle(sx, sy, COLLISION_RADIUS);

        rotation = MathUtils.atan2(-sy, -(sx - 320)) * MathUtils.radDeg;
        speed = 10;
        isAlive = true;
    }

    @Override
    public void added() {
        pos.set(sx, sy);
        size.set(0, 0);
        add(phys = new PhysicsComponent(PBodyType.ACTOR));
        phys.setOnCollideY((s) -> {
            if(!isAlive) {
                vel.setY(-vel.getY() * 0.2f);
            }
        });
        render.setUseRegW(true).setUseRegH(true);

        light = new PointLight(pos.getX(), pos.getY(), 64, Point.feathered);
        light.getColor().r = 0;
        light.getColor().g = 0;
        light.getColor().b = 0;
        light.getColor().a = 0;
//        light.getColor().set(0.1f, 0.1f, 0.1f, 0.1f);
        light.getColor().r = color.r;
        light.getColor().g = color.g;
        light.getColor().b = color.b;
        light.getColor().a = 0.1f;

        mulLight = new PointLight(pos.getX(), pos.getY(), 64, Point.feathered);
        mulLight.getColor().set(Color.WHITE);
        ((GameScreen) screen).getLightSystem().addLight(mulLight, LightType.MULTIPLICATIVE);
        ((GameScreen)screen).getLightSystem().addLight(light, LightType.ADDITIVE);
    }

    @Override
    public void loadAssets() {
        body = assets.getAnimation("flyerBodyAnimation");
        wing = assets.getAnimation("flyerWingAnimation");
        eye = assets.getAnimation("flyerEyeAnimation");
        dead = assets.getTextureRegion("flyerDead");
        add(new RenderRunnableComponent(this::render));
        hitSound = assets.get("enemyHitSound", Sound.class);
    }

    private void render() {
        float x = pos.getX() - 16;
        float y = pos.getY() - 3;
        float ox = 20;
        float oy = 8;
        if(isAlive) {
            TextureRegion b = body.getKeyFrame(time);
            TextureRegion w = wing.getKeyFrame(time);
            TextureRegion e = eye.getKeyFrame(time);

            float rot = rotation;
            batch.setColor(color);
            batch.draw(b, x, y, ox, oy, b.getRegionWidth(), b.getRegionHeight(), 1, 1, rot);
            batch.setColor(Color.WHITE);
            batch.draw(e, x, y, ox, oy, e.getRegionWidth(), e.getRegionHeight(), 1, 1, rot);
            batch.setColor(color);
            batch.draw(w, x, y, ox, oy, w.getRegionWidth(), w.getRegionHeight(), 1, 1, rot);
        } else {
            float rot = rotation;

            batch.setColor(color);
            batch.draw(dead, x + 3, y, ox, oy, dead.getRegionWidth(), dead.getRegionHeight(), 1, 1, rotation);
        }
    }

    @Override
    public void additionalTick(float delta) {
        time += delta;

        if(isAlive) {
            vel.set(0, (float) Math.cos(5 * time) * 3); // cos is the derivative of sin
            vel.setX(vel.getX() + MathUtils.cosDeg(rotation) * speed);
            vel.setY(vel.getY() + MathUtils.sinDeg(rotation) * speed);
            collision.set(pos.getX() + vel.getX() * delta + 9, pos.getY() + vel.getY() * delta + 5, COLLISION_RADIUS);

            float x = pos.getX() + vel.getX() * delta;
            float y = pos.getY() + vel.getY() * delta;
            rotation = MathUtils.atan2(-y, -(x - 320)) * MathUtils.radDeg;
            if(!((GameScreen) screen).isGameOver() && Vector2.dst2(x, y, 320, 50) < 25 * 25) {
                ((GameScreen)screen).declareDeath(this);
            } else if(((GameScreen) screen).isGameOver()) vel.set(0, 0);
        } else {
            vel.setY(vel.getY() - 300 * delta);
            if(((PActor)phys.getBody()).getFirstCollision(new IntVector2((int) pos.getX(), (int) (pos.getY() - 1))) != null) {
                vel.setX(vel.getX() * 0.8f);
                onGroundTime += delta;
                if(onGroundTime > 2) {
                    color.a = 1 - Math.min(1, onGroundTime - 2);
                    light.getColor().a = MathUtils.map(2, 3, 0.1f, 0, onGroundTime);
                    mulLight.getColor().a = MathUtils.map(2, 3, 1, 0, onGroundTime);
                    if(onGroundTime >= 3) remove();
                }
            } else {
                onGroundTime = 0;
            }
            rotation -= vel.getX() / 60;
            checkFlyers();
        }

        light.pos.set(pos.getX() + vel.getX() * delta + 7, pos.getY() + vel.getY() * delta + 7);
        mulLight.pos.set(pos.getX() + vel.getX() * delta + 7, pos.getY() + vel.getY() * delta + 7);
    }

    private void checkFlyers() {
        if(onGroundTime != 0) return;
        Rectangle rect = new Rectangle(pos.getX() + 3, pos.getY() + 3, 9, 9);
        Array<Flyer> flyers = ((GameScreen)screen).getFlyers();
        for (int i = 0; i < flyers.size; i++) {
            if(this != flyers.get(i) && flyers.get(i).isAlive) {
                if(Intersector.overlaps(flyers.get(i).collision, rect)) {
                    flyers.get(i).hit(vel.getX() * 2, vel.getY() * 2);
                }
            }
        }
    }

    public void hit(float vx, float vy) {
        if(((GameScreen)screen).isGameOver()) return;
        isAlive = false;
        vel.set(vx / 2, vy / 2);
        size.set(15, 15);
        hitSound.play(Settings.soundVolume);
        ((GameScreen)screen).getStar().spawnParticle(pos.getX() + 7, pos.getY() + 7);
        if(((GameScreen) screen).getCoins().size == 0) {
            ((GameScreen)screen).addFlyerDeath();
        }
    }

    @Override
    public void removed() {
        ((GameScreen)screen).getPhysicsSystem().getPhysicsWorld().getActors().removeValue((PActor)phys.getBody(), true);
        ((GameScreen) screen).getLightSystem().removeLight(light, LightType.ADDITIVE);
        ((GameScreen) screen).getLightSystem().removeLight(mulLight, LightType.MULTIPLICATIVE);
    }

    public Circle getCollision() {
        return collision;
    }

    public boolean isAlive() {
        return isAlive;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public float getX() {
        return pos.getX();
    }

    public float getY() {
        return pos.getY();
    }
}
