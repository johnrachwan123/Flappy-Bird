package com.flappybird.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Touchable;

import java.io.FileOutputStream;
import java.util.Random;

import sun.rmi.runtime.Log;

import static jdk.nashorn.internal.runtime.regexp.joni.Config.log;

public class FlappyBird extends ApplicationAdapter {
	SpriteBatch batch;
	Texture backround;
	Texture gameover;
	int gameState =0;
	int score =0;
	int scoringTube =0;
	int highscore =0;
	BitmapFont font;

	Circle birdCircle;
    Rectangle[] topTubeRectangle;
	Rectangle[] bottomTubeRectangle;

	Texture[] birds;
	int flappingStatus=0 ;
	float birdY = 0;
	float velocity =2;
	float gravity =2;


	Texture pipeUp;
	Texture pipeDown;
	int gap =450;
	float maxTube;
	Random randomGenerator;
	float tubeVelocity=7;

	int numberOfTubes = 4;
	float distanceBetweenTubes;
	float[] tubeX = new float[numberOfTubes];
	float[] tubeOffset = new float[numberOfTubes];

	@Override
	public void create () {
		batch = new SpriteBatch();
		gameover = new Texture("gameover.png");
		backround = new Texture("bg.png");
		font = new BitmapFont();
		font.setColor(Color.WHITE);
		font.getData().setScale(10);

		birdCircle = new Circle();
		topTubeRectangle = new Rectangle[numberOfTubes];
		bottomTubeRectangle = new Rectangle[numberOfTubes];

		birds = new Texture[2];
		birds[0]=new Texture("bird.png");
		birds[1]=new Texture("bird2.png");



		pipeUp = new Texture("toptube.png");
		pipeDown = new Texture("bottomtube.png");
		maxTube =Gdx.graphics.getHeight()/2 - gap - 100;
		randomGenerator = new Random();

		distanceBetweenTubes=Gdx.graphics.getWidth()/2;


		birdY=Gdx.graphics.getHeight()/2 - birds[flappingStatus].getHeight()/2;
		for (int i =0;i<numberOfTubes;i++){
			tubeOffset[i]=(randomGenerator.nextFloat()-0.5f)*(maxTube);
			tubeX[i] =(Gdx.graphics.getWidth() -pipeUp.getWidth()/2)+i*distanceBetweenTubes;
			topTubeRectangle[i] = new Rectangle();
			bottomTubeRectangle[i] = new Rectangle();
		}
	}

	@Override
	public void render () {
		batch.begin();
		batch.draw(backround, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		if (gameState == 1) {
			if(tubeX[scoringTube]<Gdx.graphics.getWidth()/2){
				score++;
				if(scoringTube<numberOfTubes-1)
					scoringTube++;
				else
					scoringTube=0;
			}
			if (Gdx.input.justTouched()) {
				velocity = -32;

			}
			for (int i = 0; i < numberOfTubes; i++) {
				if (tubeX[i] < -pipeUp.getWidth()) {
					tubeOffset[i] = (randomGenerator.nextFloat() - 0.5f) * (maxTube);
					tubeX[i] += numberOfTubes * distanceBetweenTubes;
				} else {
					tubeX[i] -= tubeVelocity;
				}

				batch.draw(pipeUp, tubeX[i], Gdx.graphics.getHeight() / 2 + gap / 2 + tubeOffset[i]);
				batch.draw(pipeDown, tubeX[i], Gdx.graphics.getHeight() / 2 - pipeDown.getHeight() - gap / 2 + tubeOffset[i]);

				topTubeRectangle[i] = new Rectangle(tubeX[i], Gdx.graphics.getHeight() / 2 + gap / 2 + tubeOffset[i], pipeUp.getWidth(), pipeUp.getHeight()*2);
				bottomTubeRectangle[i] = new Rectangle(tubeX[i], Gdx.graphics.getHeight() / 2 - pipeDown.getHeight() - gap / 2 + tubeOffset[i], pipeDown.getWidth(), pipeDown.getHeight());

			}

			if (birdY > 0) {
				velocity += gravity;
				birdY -= velocity;
			}
			else{
				gameState=2;
			}
		} else if (gameState==0){
			if (Gdx.input.justTouched()) {
				gameState = 1;
			}
		}
		else if (gameState==2){
			batch.draw(gameover,Gdx.graphics.getWidth()/2-gameover.getWidth()/2,Gdx.graphics.getHeight()/2-gameover.getHeight()/2);
			birdY=-10000;
			Preferences prefs = Gdx.app.getPreferences("flappybird");
			if(score>highscore) {
				highscore = score;
				prefs.putInteger("highscore",highscore);
				prefs.flush();
			}

				highscore = prefs.getInteger("highscore",0);

			font.draw(batch,"High Score : " + String.valueOf(highscore),270,900);
			if(Gdx.input.justTouched()){

				gameState = 1;
				birdY=Gdx.graphics.getHeight()/2 - birds[flappingStatus].getHeight()/2;
				for (int i =0;i<numberOfTubes;i++){
					tubeOffset[i]=(randomGenerator.nextFloat()-0.5f)*(maxTube);
					tubeX[i] =(Gdx.graphics.getWidth() -pipeUp.getWidth()/2)+i*distanceBetweenTubes;
					topTubeRectangle[i] = new Rectangle();
					bottomTubeRectangle[i] = new Rectangle();
				}
				score=0;
				scoringTube=0;
				velocity=0;
			}

		}

		if (flappingStatus == 1)
			flappingStatus = 0;
		else
			flappingStatus = 1;


		batch.draw(birds[flappingStatus], Gdx.graphics.getWidth() / 2 - birds[flappingStatus].getWidth() / 2, birdY);
		font.draw(batch,String.valueOf(score),100,200);
		batch.end();
		birdCircle.set(Gdx.graphics.getWidth() / 2, birdY + birds[flappingStatus].getHeight() / 2, birds[flappingStatus].getWidth() / 2);

		for (int i = 0; i < numberOfTubes; i++) {
			if (Intersector.overlaps(birdCircle, topTubeRectangle[i]) || Intersector.overlaps(birdCircle, bottomTubeRectangle[i])) {

				gameState=2;
			}
		}
	}
	
	@Override
	public void dispose () {
		batch.dispose();
		backround.dispose();
	}
}
