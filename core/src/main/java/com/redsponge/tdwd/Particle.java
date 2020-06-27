package com.redsponge.tdwd;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool.PooledEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.DelayedRemovalArray;
import com.badlogic.gdx.utils.Disposable;

public class Particle implements Disposable {

    private ParticleEffectPool pool;
    private DelayedRemovalArray<PooledEffect> activeEffects;
    private ParticleEffect effect;

    public Particle(String path) {
        effect = new ParticleEffect();
        effect.load(Gdx.files.internal(path), Gdx.files.internal("particles"));

        pool = new ParticleEffectPool(effect, 16, 64);
        activeEffects = new DelayedRemovalArray<>();
    }

    public void tickAndRender(float delta, SpriteBatch batch) {
        for (int i = 0; i < activeEffects.size; i++) {
            activeEffects.get(i).draw(batch, delta);
            if(activeEffects.get(i).isComplete()) {
                activeEffects.get(i).free();
                activeEffects.removeIndex(i);
            }
        }
    }

    public PooledEffect spawnParticle(float x, float y) {
        PooledEffect e = pool.obtain();
        e.setPosition(x, y);
        activeEffects.add(e);
        return e;
    }

    @Override
    public void dispose() {
        effect.dispose();
    }
}
