package com.papertoss.office;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Align;

final class EndScreen extends BaseScreen {
    private final boolean victory; private final int score, baskets, throwsUsed;
    private final Rectangle retry = new Rectangle(255, 130, 290, 52), menu = new Rectangle(255, 62, 290, 52);
    EndScreen(PaperTossOffice game, boolean victory, int score, int baskets, int throwsUsed) { super(game); this.victory=victory; this.score=score; this.baskets=baskets; this.throwsUsed=throwsUsed; }
    @Override public void render(float delta) {
        begin(); background(true);
        if (victory) victory(); else gameOver();
        button(victory ? "PLAY AGAIN" : "RETRY", retry); button("MAIN MENU", menu);
        if (clicked(retry) || Gdx.input.isKeyJustPressed(Input.Keys.R)) game.setScreen(new GameplayScreen(game));
        if (clicked(menu) || Gdx.input.isKeyJustPressed(Input.Keys.M)) game.setScreen(new MenuScreen(game));
    }
    private void victory() {
        Texture basket=game.assets.get("wastebasket_scored.png"), confetti=game.assets.get("victory_confetti.png");
        game.batch.begin(); game.batch.draw(confetti, 25, 290, 230, 230); game.batch.draw(confetti, 545, 290, 230, 230); game.batch.draw(basket, 315, 190, 170, 155); game.batch.end();
        text("GREAT SHOT!",400,510,2.5f,new Color(.35f,.9f,1,1),Align.center); text("Final Score: " + score,400,390,1.35f,Color.WHITE,Align.center);
        text("Baskets: 5/5     Throws Used: " + throwsUsed + "/10",400,360,1.05f,Color.WHITE,Align.center);
    }
    private void gameOver() {
        Texture ball=game.assets.get("paper_ball.png"); game.batch.begin(); game.batch.setColor(1,1,1,.45f); game.batch.draw(ball,90,335,60,60); game.batch.draw(ball,640,305,55,55); game.batch.setColor(Color.WHITE); game.batch.end();
        text("OUT OF THROWS!",400,500,2.25f,new Color(1,.45f,.35f,1),Align.center); text("Final Score: " + score,400,400,1.35f,Color.WHITE,Align.center);
        text("Baskets: " + baskets + "/5     Throws Used: 10/10",400,367,1.05f,Color.WHITE,Align.center);
        text("You needed five baskets to win. Give the next round your best shot!",400,325,.82f,new Color(.9f,.9f,.9f,1),Align.center);
    }
}
