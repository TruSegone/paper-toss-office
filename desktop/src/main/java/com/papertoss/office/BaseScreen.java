package com.papertoss.office;

import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Align;

abstract class BaseScreen extends ScreenAdapter {
    static final float W = 800, H = 600;
    final PaperTossOffice game;
    BaseScreen(PaperTossOffice game) { this.game = game; }
    void begin() { com.badlogic.gdx.Gdx.gl.glClearColor(.03f, .04f, .06f, 1); com.badlogic.gdx.Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT); }
    void background(boolean dim) {
        Texture bg = game.assets.get("office_background.png");
        game.batch.begin(); game.batch.setColor(Color.WHITE); game.batch.draw(bg, 0, 0, W, H);
        if (dim) { game.batch.setColor(0, 0, 0, .58f); game.batch.draw(bg, 0, 0, W, H); game.batch.setColor(Color.WHITE); }
        game.batch.end();
    }
    void text(String value, float x, float y, float scale, Color color, int align) { game.batch.begin(); game.font.getData().setScale(scale); game.font.setColor(color); game.font.draw(game.batch, value, x, y, 0, align, false); game.font.getData().setScale(1); game.batch.end(); }
    boolean clicked(Rectangle r) { return com.badlogic.gdx.Gdx.input.justTouched() && r.contains(com.badlogic.gdx.Gdx.input.getX(), H - com.badlogic.gdx.Gdx.input.getY()); }
    void button(String label, Rectangle r) {
        game.shapes.begin(com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType.Filled); game.shapes.setColor(new Color(.08f,.15f,.22f,.9f)); game.shapes.rect(r.x,r.y,r.width,r.height); game.shapes.end();
        game.shapes.begin(com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType.Line); game.shapes.setColor(new Color(.25f,.8f,1,1)); game.shapes.rect(r.x,r.y,r.width,r.height); game.shapes.end();
        text(label, r.x + r.width / 2, r.y + r.height / 2 + 7, 1.25f, Color.WHITE, Align.center);
    }
}
