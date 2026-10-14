package com.papertoss.office;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Align;

final class GameplayScreen extends BaseScreen {
    private static final Vector2 LAUNCH = new Vector2(138, 174);
    private static final float BALL_SIZE = 42, BALL_RADIUS = 17, GRAVITY = 650;
    private static final float BASKET_X = 640, BASKET_Y = 115, BASKET_W = 132, BASKET_H = 148;
    private static final float OPEN_LEFT = 667, OPEN_RIGHT = 745, RIM_Y = 236;
    private final Vector2 pos = new Vector2(LAUNCH), velocity = new Vector2();
    private float angle = 45, charge, rotation, fanTime, resultDelay, effectTime, rimCooldown;
    private float effectX, effectY; private boolean charging, airborne, scored, missed;
    private int score, baskets, throwsUsed, windBars; private float windAccel;
    GameplayScreen(PaperTossOffice game) { super(game); prepareThrow(); }

    private void prepareThrow() {
        pos.set(LAUNCH); velocity.setZero(); angle=45; charge=0; rotation=0; charging=false; airborne=false; scored=false; missed=false;
        resultDelay=0; effectTime=0; rimCooldown=0; windBars=MathUtils.random(1,3); windAccel=(windBars==1?50:windBars==2?90:130)*(MathUtils.randomBoolean()?1:-1);
    }
    @Override public void render(float delta) {
        delta=Math.min(delta,.05f); begin(); update(delta); drawWorld(); drawHud();
    }
    private void update(float dt) {
        fanTime += dt * (1 + windBars*.12f); if(effectTime>0) effectTime-=dt; if(rimCooldown>0) rimCooldown-=dt;
        if (!airborne && !scored && !missed) aim(dt); else if (airborne) fly(dt); else resolve(dt);
    }
    private void aim(float dt) {
        boolean up=Gdx.input.isKeyPressed(Input.Keys.W)||Gdx.input.isKeyPressed(Input.Keys.UP);
        boolean down=Gdx.input.isKeyPressed(Input.Keys.S)||Gdx.input.isKeyPressed(Input.Keys.DOWN);
        if(up != down) angle=MathUtils.clamp(angle+(up?1:-1)*60*dt,20,70);
        if(Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) charging=true;
        if(charging && Gdx.input.isKeyPressed(Input.Keys.SPACE)) charge=Math.min(1,charge+dt/1.3f);
        // libGDX exposes press events but not a just-released query; a no-longer-held
        // charging key is the release that commits the throw.
        if(charging && !Gdx.input.isKeyPressed(Input.Keys.SPACE)) launch();
    }
    private void launch() {
        float speed=MathUtils.lerp(300,700,charge), radians=angle*MathUtils.degreesToRadians;
        velocity.set(MathUtils.cos(radians)*speed,MathUtils.sin(radians)*speed); airborne=true; charging=false; throwsUsed++;
    }
    private void fly(float dt) {
        float oldY=pos.y; velocity.x+=windAccel*dt; velocity.y-=GRAVITY*dt; pos.mulAdd(velocity,dt); rotation+=velocity.len()*dt*.14f;
        checkBasket(oldY); if(!airborne) return; checkRims(); if(!airborne) return;
        if(pos.y-BALL_RADIUS<=0 || pos.x+BALL_RADIUS<0 || pos.x-BALL_RADIUS>W || pos.y-BALL_RADIUS>H) miss();
    }
    private void checkBasket(float oldY) {
        if(velocity.y<0 && oldY>RIM_Y && pos.y<=RIM_Y && pos.x>OPEN_LEFT && pos.x<OPEN_RIGHT) score();
    }
    private void checkRims() {
        if(rimCooldown>0 || pos.y < RIM_Y-18 || pos.y > RIM_Y+22) return;
        float leftDist=Vector2.dst(pos.x,pos.y,OPEN_LEFT,RIM_Y), rightDist=Vector2.dst(pos.x,pos.y,OPEN_RIGHT,RIM_Y);
        if(leftDist<BALL_RADIUS+5) { velocity.x=Math.abs(velocity.x)*.65f; velocity.y*=.65f; rimHit(OPEN_LEFT,RIM_Y); }
        else if(rightDist<BALL_RADIUS+5) { velocity.x=-Math.abs(velocity.x)*.65f; velocity.y*=.65f; rimHit(OPEN_RIGHT,RIM_Y); }
    }
    private void rimHit(float x,float y) { rimCooldown=.1f; effectTime=.15f; effectX=x; effectY=y; }
    private void score() { airborne=false; scored=true; baskets++; score+=100; resultDelay=.8f; effectTime=.6f; effectX=706; effectY=RIM_Y+12; }
    private void miss() { airborne=false; missed=true; resultDelay=.8f; effectTime=.4f; effectX=MathUtils.clamp(pos.x,20,W-20); effectY=MathUtils.clamp(pos.y,20,H-20); }
    private void resolve(float dt) {
        resultDelay-=dt; if(resultDelay>0) return;
        if(scored && baskets>=5) game.setScreen(new EndScreen(game,true,score,baskets,throwsUsed));
        else if(throwsUsed>=10) game.setScreen(new EndScreen(game,false,score,baskets,throwsUsed));
        else prepareThrow();
    }
    private void drawWorld() {
        Texture bg=game.assets.get("office_background.png"), idleFan=game.assets.get("desk_fan_idle.png"), fan=game.assets.get("desk_fan_spin_"+(1+((int)(fanTime*9)%3))+".png");
        Texture stream=game.assets.get("wind_stream.png"), back=game.assets.get("wastebasket_back.png"), front=game.assets.get("wastebasket_front.png");
        game.batch.begin(); game.batch.draw(bg,0,0,W,H);
        // fan and airflow occupy the center but remain non-solid.
        game.batch.draw(idleFan,315,145,190,126); game.batch.draw(fan,315,145,190,126);
        boolean left=windAccel<0; game.batch.draw(stream, left?365:465,190, left?90:-90,42);
        if(scored) game.batch.draw(game.assets.get("wastebasket_scored.png"),BASKET_X,BASKET_Y,BASKET_W,BASKET_H);
        else game.batch.draw(back,BASKET_X,BASKET_Y,BASKET_W,BASKET_H);
        if(airborne) drawTrail(game.batch);
        if(!scored && !missed) drawBall(game.batch);
        if(!scored) game.batch.draw(front,BASKET_X,BASKET_Y,BASKET_W,BASKET_H);
        if(effectTime>0) drawEffect(game.batch); game.batch.end();
        if(!airborne && !scored && !missed) preview();
    }
    private void drawBall(SpriteBatch batch) { Texture ball=game.assets.get("paper_ball.png"); batch.draw(ball,pos.x-BALL_SIZE/2,pos.y-BALL_SIZE/2,BALL_SIZE/2,BALL_SIZE/2,BALL_SIZE,BALL_SIZE,1,1,rotation,0,0,ball.getWidth(),ball.getHeight(),false,false); }
    private void drawTrail(SpriteBatch batch) { Texture t=game.assets.get("paper_ball_trail.png"); float a=MathUtils.atan2(velocity.y,velocity.x)*MathUtils.radiansToDegrees+180; batch.setColor(1,1,1,.5f); batch.draw(t,pos.x-58,pos.y-20,58,20,120,40,1,1,a,0,0,t.getWidth(),t.getHeight(),false,false); batch.setColor(Color.WHITE); }
    private void drawEffect(SpriteBatch batch) {
        Texture t; float size;
        if(scored) { t=game.assets.get("basket_score_effect.png"); size=95; batch.draw(t,effectX-size/2,effectY-size/2,size,size); t=game.assets.get("score_popup.png"); batch.draw(t,655,275,110,55); }
        else if(missed) { t=game.assets.get("miss_impact_effect.png"); size=76; batch.draw(t,effectX-size/2,effectY-size/2,size,size); }
        else { t=game.assets.get("rim_hit_effect.png"); size=54; batch.draw(t,effectX-size/2,effectY-size/2,size,size); }
    }
    private void preview() {
        float speed=MathUtils.lerp(300,700,charge), rad=angle*MathUtils.degreesToRadians, vx=MathUtils.cos(rad)*speed, vy=MathUtils.sin(rad)*speed;
        game.shapes.begin(ShapeRenderer.ShapeType.Filled); game.shapes.setColor(.45f,.9f,1,.55f);
        for(float t=.12f;t<1.5f;t+=.12f) { float x=LAUNCH.x+vx*t+.5f*windAccel*t*t, y=LAUNCH.y+vy*t-.5f*GRAVITY*t*t; if(x<0||x>W||y<0) break; game.shapes.circle(x,y,3); }
        game.shapes.setColor(Color.WHITE); float endX=LAUNCH.x+MathUtils.cos(rad)*55,endY=LAUNCH.y+MathUtils.sin(rad)*55; game.shapes.rectLine(LAUNCH.x,LAUNCH.y,endX,endY,2); game.shapes.end();
    }
    private void drawHud() {
        game.shapes.begin(ShapeRenderer.ShapeType.Filled); game.shapes.setColor(0.03f,.07f,.11f,.82f); game.shapes.rect(15,490,310,94); game.shapes.rect(15,20,280,56); game.shapes.end();
        game.shapes.begin(ShapeRenderer.ShapeType.Line); game.shapes.setColor(.25f,.8f,1,1); game.shapes.rect(15,490,310,94); game.shapes.rect(15,20,280,56); game.shapes.end();
        text("SCORE: "+score+"     BASKETS: "+baskets+"/5",28,557,1.05f,Color.WHITE,Align.left); text("THROWS: "+throwsUsed+"/10",28,530,1.05f,Color.WHITE,Align.left);
        String wind=(windAccel<0?"←":"→")+"  "+"▮".repeat(windBars)+"  WIND"; text(wind,28,503,1.0f,new Color(.5f,.9f,1,1),Align.left);
        text("ANGLE  "+Math.round(angle)+"°",28,55,.95f,Color.WHITE,Align.left); text("POWER",28,35,.85f,Color.WHITE,Align.left);
        game.shapes.begin(ShapeRenderer.ShapeType.Filled); game.shapes.setColor(.15f,.18f,.22f,1); game.shapes.rect(100,28,170,15); game.shapes.setColor(MathUtils.lerp(.2f,1,charge),MathUtils.lerp(.8f,.2f,charge),.18f,1); game.shapes.rect(100,28,170*charge,15); game.shapes.end();
        text("W/S or ↑/↓ aim  •  hold SPACE, release to throw",410,30,.75f,Color.WHITE,Align.center);
    }
}
