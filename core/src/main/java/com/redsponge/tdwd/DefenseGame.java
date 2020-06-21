package com.redsponge.tdwd;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.redsponge.redengine.EngineGame;
import com.redsponge.redengine.utils.GeneralUtils;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
@SuppressWarnings("ALL")
public class DefenseGame extends EngineGame {

    public static ShaderProgram defaultShader;
    public static ShaderProgram onlyWhiteShader;

    @Override
    public void init() {
        ShaderProgram.pedantic = false;
        defaultShader = SpriteBatch.createDefaultShader();
        onlyWhiteShader = GeneralUtils.tryLoadShader(Gdx.files.internal("shaders/onlyWhite.vert"), Gdx.files.internal("shaders/onlyWhite.frag"));

//        batch.setShader(onlyWhiteShader);

        setScreen(new GameScreen(ga));
    }
}