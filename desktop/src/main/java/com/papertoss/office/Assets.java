package com.papertoss.office;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Disposable;
import java.util.HashMap;
import java.util.Map;

public final class Assets implements Disposable {
    private final Map<String, Texture> textures = new HashMap<>();
    public Texture get(String name) { return textures.computeIfAbsent(name, n -> { Texture t = new Texture(n); t.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear); return t; }); }
    @Override public void dispose() { for (Texture texture : textures.values()) texture.dispose(); textures.clear(); }
}
