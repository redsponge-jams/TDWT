package com.redsponge.tdwd;

import com.badlogic.gdx.ApplicationAdapter;
import com.redsponge.redengine.EngineGame;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class DefenseGame extends EngineGame {

    @Override
    public void init() {
        setScreen(new GameScreen(ga));
    }
}