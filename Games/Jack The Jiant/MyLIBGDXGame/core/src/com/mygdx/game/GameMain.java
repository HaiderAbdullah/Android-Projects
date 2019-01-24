package com.mygdx.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import screens.GameplayScreen;
import screens.MainMenu;

public class GameMain extends Game {

	private SpriteBatch batch;

	
	@Override
	public void create () {

		batch = new SpriteBatch();
		setScreen(new MainMenu(this));


	}

	@Override
	public void render () {

		super.render();

	}
	
	@Override
	public void dispose () {

	}

	public SpriteBatch fetchBatch(){  // getter for SpriteBatch. Can only have one sprite batch in the game.

		return this.batch;

	}
}
