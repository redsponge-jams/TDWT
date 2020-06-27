package com.redsponge.tdwd;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.redsponge.redengine.assets.Asset;
import com.redsponge.redengine.assets.AssetSpecifier;
import com.redsponge.redengine.assets.atlas.AtlasAnimation;
import com.redsponge.redengine.assets.atlas.AtlasFrame;

public class GameAssets extends AssetSpecifier {

    public GameAssets(AssetManager am) {
        super(am);
    }

    @Asset("textures/textures.atlas")
    private TextureAtlas atlas;

    @AtlasFrame(atlas = "atlas", frameName = "player")
    private TextureRegion playerTex;

    @AtlasFrame(atlas = "atlas", frameName = "pixel")
    private TextureRegion pixel;

    @AtlasFrame(atlas = "atlas", frameName = "sword")
    private TextureRegion swordTex;

    @AtlasAnimation(atlas = "atlas", animationName = "bow", length = 6, playMode = PlayMode.NORMAL, frameDuration = 0.05f)
    private Animation<TextureRegion> bowAnimation;

    @AtlasFrame(atlas = "atlas", frameName = "arrow")
    private TextureRegion arrowTex;


    @Asset("audio/bow_armed.ogg")
    private Sound bowArmedSound;

    @Asset("audio/bow_pew.ogg")
    private Sound bowPewSound;

    @Asset("audio/sword_whoosh.ogg")
    private Sound swordWhooshSound;

    @Asset("audio/enemy_hit.ogg")
    private Sound enemyHitSound;

    @AtlasAnimation(atlas = "atlas", animationName = "flyer_body", length = 5, playMode = PlayMode.LOOP_PINGPONG, frameDuration = 0.1f)
    private Animation<TextureRegion> flyerBodyAnimation;

    @AtlasAnimation(atlas = "atlas", animationName = "flyer_wing", length = 5, playMode = PlayMode.LOOP_PINGPONG, frameDuration = 0.1f)
    private Animation<TextureRegion> flyerWingAnimation;

    @AtlasAnimation(atlas = "atlas", animationName = "flyer_eye", length = 5, playMode = PlayMode.LOOP_PINGPONG, frameDuration = 0.1f)
    private Animation<TextureRegion> flyerEyeAnimation;

    @AtlasFrame(atlas = "atlas", frameName = "background")
    private TextureRegion background;

    @AtlasFrame(atlas = "atlas", frameName = "island")
    private TextureRegion island;

    @AtlasFrame(atlas = "atlas", frameName = "flyer_dead")
    private TextureRegion flyerDead;

    @AtlasFrame(atlas = "atlas", frameName = "cake")
    private TextureRegion cake;

    @AtlasAnimation(atlas = "atlas", animationName = "coin", length = 8)
    private Animation<TextureRegion> coinAnimation;

    @Asset("audio/coin.wav")
    private Sound coinPickup;

    @AtlasFrame(atlas = "atlas", frameName = "tutorial")
    private TextureRegion tutorial;


}
