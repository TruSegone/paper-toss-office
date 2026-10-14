package com.papertoss.office;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Align;

final class MenuScreen extends BaseScreen {
    private final Rectangle play = new Rectangle(285, 205, 230, 54), exit = new Rectangle(285, 135, 230, 54);
    MenuScreen(PaperTossOffice game) { super(game); }
    @Override public void render(float delta) {
        begin(); background(true);
        Texture logo = game.assets.get("title_logo.png");
        game.batch.begin(); game.batch.draw(logo, 135, 355, 530, 150); game.batch.end();
        text("A desk-side arcade challenge", 400, 335, 1.15f, new Color(.85f,.93f,1,1), Align.center);
        button("PLAY", play); button("EXIT", exit);
        text("Aim: W/S or ↑/↓     Hold SPACE to charge • Release to throw", 400, 85, .88f, Color.WHITE, Align.center);
        if (clicked(play) || Gdx.input.isKeyJustPressed(Input.Keys.ENTER) || Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) game.setScreen(new GameplayScreen(game));
        if (clicked(exit) || Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) Gdx.app.exit();
    }
}
