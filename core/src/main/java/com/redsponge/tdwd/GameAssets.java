package com.redsponge.tdwd;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.redsponge.redengine.assets.Asset;
import com.redsponge.redengine.assets.AssetSpecifier;

public class GameAssets extends AssetSpecifier {

    public GameAssets(AssetManager am) {
        super(am);
    }

    @Asset("player.png")
    private Texture playerTex;

    @Asset("pixel.png")
    private Texture pixel;

    @Asset("sword.png")
    private Texture swordTex;

}
