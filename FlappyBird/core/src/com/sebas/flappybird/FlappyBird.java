package com.sebas.flappybird;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;

import java.util.Random;

public class FlappyBird extends ApplicationAdapter {
	SpriteBatch batch;
	Texture background;
	Texture[] birds;
	Texture tubeTop;
	Texture tubeBottom;
	Texture gameover;
	Texture ready;

	int flapState = 0;
	int tubeVelocity = 3;
	int gameState = 0;
	int numberOfTubes = 4;
	int score =0;
	int scoringTube = 0;
	float gap = 400;
	int gravity = 2;
	float[] tubeX;
	Random rand;
	float maxTubeOffSet = 400;
	float tubeOffSet[];
	float birdY = 0;
	float velocity = 0;
	float distanceBetweenTubes;
	BitmapFont bitmapFont;

	Circle birdleCircle;
	Rectangle[] bottomRectangles;
	Rectangle[] topRectangles;
	//ShapeRenderer shapeRenderer;
	@Override
	public void create () {
		batch = new SpriteBatch();
		background = new Texture("bg.png");
		birds = new Texture[2];
		birds[0] = new Texture("bird.png");
		birds[1] = new Texture("bird2.png");
		bitmapFont = new BitmapFont();
		bitmapFont.setColor(Color.WHITE);
		bitmapFont.getData().setScale(10);
		gameover = new Texture("gameover.png");
		ready = new Texture("ready.png");
		tubeBottom = new Texture("bottomtube.png");
		tubeTop = new Texture("toptube.png");

		tubeX = new float[numberOfTubes];
		maxTubeOffSet = Gdx.graphics.getHeight() / 2 - gap /2 - 100;
		tubeOffSet = new float[numberOfTubes];
		distanceBetweenTubes = Gdx.graphics.getWidth() *3/4;
		rand = new Random();
		topRectangles = new Rectangle[numberOfTubes];
		bottomRectangles = new Rectangle[numberOfTubes];

		startGame();
	}

	void startGame(){
		birdY = Gdx.graphics.getHeight()/2-birds[0].getHeight()/2;
		for(int i = 0; i<numberOfTubes; i++){
			tubeX[i]=Gdx.graphics.getWidth()/2 -  tubeTop.getWidth() / 2+ Gdx.graphics.getWidth() + i * distanceBetweenTubes;
			tubeOffSet[i] = (rand.nextFloat()-0.5f)*(Gdx.graphics.getHeight() - gap - 200);
			topRectangles[i] = new Rectangle();
			bottomRectangles[i] = new Rectangle();
		}
	}
	void checkScorePoints(){
		if(tubeX[scoringTube] < Gdx.graphics.getWidth()/2)
		{
			score++;
			Gdx.app.log("Score",String.valueOf(score));
			if(scoringTube < numberOfTubes-1){
				scoringTube++;
			}
			else{
				scoringTube = 0;
			}
		}
	}
	void detectCollisions(){
		for (int i = 0; i < numberOfTubes; i++) {
			//Collision detecion
			if(Intersector.overlaps(birdleCircle, topRectangles[i]) || Intersector.overlaps(birdleCircle,bottomRectangles[i])){
				gameState=2; //change game status
			}
		}
	}
	void moveTubesInCarousel(int i){
		if(tubeX[i] < - tubeTop.getWidth()){
			tubeX[i] += numberOfTubes * distanceBetweenTubes;
			tubeOffSet[i] = (rand.nextFloat()-0.5f)*(Gdx.graphics.getHeight() - gap - 200);
		}
		else{
			tubeX[i]-=tubeVelocity;

		}
	}
	void animateBird(){
		if (flapState == 0) {
			flapState = 1;
		} else {
			flapState = 0;
		}
	}
	@Override
	public void render () {

		batch.begin();
		batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		//-----------------------
		//Game
		//-----------------------
		if(gameState == 1)
		{
			checkScorePoints();
			for(int i = 0; i < numberOfTubes;i++)
			{
				batch.draw(tubeTop, tubeX[i], Gdx.graphics.getHeight()/2 + gap / 2 + tubeOffSet[i]);
				batch.draw(tubeBottom, tubeX[i], Gdx.graphics.getHeight()/2 - gap / 2 - tubeBottom.getHeight() + tubeOffSet[i]);
				moveTubesInCarousel(i);

				topRectangles[i] = new Rectangle(tubeX[i],Gdx.graphics.getHeight()/2 + gap / 2 + tubeOffSet[i],tubeTop.getWidth(),tubeTop.getHeight());
				bottomRectangles[i] = new Rectangle(tubeX[i],Gdx.graphics.getHeight()/2 - gap / 2 - tubeBottom.getHeight() + tubeOffSet[i],tubeTop.getWidth(),tubeTop.getHeight());
			}

			if(Gdx.input.justTouched()){
				velocity = -30;
			}
			//If the bird in the air
            if( birdY>0 ) {

                velocity = velocity + gravity;
                birdY -= velocity;
            } else{ //If the bird touch the ground
				gameState = 2;
			}
		}
		//-----------------------
		//Before start game (after running the game)
		//-----------------------
		else if(gameState == 0){
			batch.draw(ready, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
			if(Gdx.input.justTouched()){
				gameState = 1;
			}
		}
		//-----------------------
		//When the game is over
		//-----------------------
		else if(gameState==2)
		{
			batch.draw(gameover,Gdx.graphics.getWidth()/2-gameover.getWidth()/2,Gdx.graphics.getHeight()/2 - gameover.getHeight()/2);
			if(Gdx.input.justTouched()){
				gameState = 0;
				score=0;
				scoringTube=0;
				velocity=0;
				startGame();
			}
		}
		//-----------------------
		//Bird animation state
		//-----------------------
		animateBird();
		//-----------------------
		batch.draw(birds[flapState], Gdx.graphics.getWidth() / 2 - birds[flapState].getWidth() / 2, birdY);
		//-----------------------
		bitmapFont.draw(batch, String.valueOf(score), 100, 200); //Draw score
		batch.end(); //End of drawing
		//Update circle before Collision Detecion
		birdleCircle = new Circle();
		birdleCircle.set(Gdx.graphics.getWidth()/2,birdY + birds[flapState].getHeight() /2,birds[flapState].getHeight() /2);

		detectCollisions();
	}
}
