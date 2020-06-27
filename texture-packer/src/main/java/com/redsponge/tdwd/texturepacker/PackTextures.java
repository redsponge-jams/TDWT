package com.redsponge.tdwd.texturepacker;

import com.badlogic.gdx.tools.texturepacker.TexturePacker;

public class PackTextures {

    public static void main(String[] args) {
        TexturePacker.processIfModified("raw/textures", "../assets/textures", "textures");
    }

}
