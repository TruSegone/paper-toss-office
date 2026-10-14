package com.papertoss.office;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public final class PaperTossOffice extends Game {
    public SpriteBatch batch;
    public ShapeRenderer shapes;
    public BitmapFont font;
    public Assets assets;
    @Override public void create() { batch = new SpriteBatch(); shapes = new ShapeRenderer(); font = new BitmapFont(); assets = new Assets(); setScreen(new MenuScreen(this)); }
    @Override public void dispose() { super.dispose(); batch.dispose(); shapes.dispose(); font.dispose(); assets.dispose(); }
}
