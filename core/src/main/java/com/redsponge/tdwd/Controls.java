package com.redsponge.tdwd;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;

public class Controls {

    public static final int[] JUMP = {Keys.W, Keys.SPACE, Keys.Z};
    public static final int[] RIGHT = {Keys.D};
    public static final int[] LEFT = {Keys.A, Keys.Q};

    public static boolean isPressed(int[] key) {
        for (int i : key) {
            if(Gdx.input.isKeyPressed(i)) return true;
        }
        return false;
    }

    public static boolean isJustPressed(int[] key) {
        for (int i : key) {
            if(Gdx.input.isKeyJustPressed(i)) return true;
        }
        return false;
    }

}
