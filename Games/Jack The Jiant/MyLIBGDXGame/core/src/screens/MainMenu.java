package screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.game.GameMain;

import helpers.GameInfo;
import huds.MainMenuButtons;

public class MainMenu implements Screen {

    private GameMain gameMain;
    private OrthographicCamera mainCamera;
    private Viewport gameViewport;

    private Texture mainMenuBG;

    private MainMenuButtons btns;

    public MainMenu(GameMain gameMain) {

        this.gameMain = gameMain;

        mainCamera = new OrthographicCamera();
        mainCamera.setToOrtho(false, GameInfo.WIDTH, GameInfo.HEIGHT);
        mainCamera.position.set(GameInfo.WIDTH / 2f, GameInfo.HEIGHT /2f, 0);

        gameViewport = new StretchViewport(GameInfo.WIDTH, GameInfo.HEIGHT, mainCamera);

        mainMenuBG = new Texture("Backgrounds/Menu BG.png");

        btns = new MainMenuButtons(gameMain);

    }


    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {

        Gdx.gl.glClearColor(1, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        gameMain.fetchBatch().begin();

        gameMain.fetchBatch().draw(mainMenuBG, 0,0);

        gameMain.fetchBatch().end();

        gameMain.fetchBatch().setProjectionMatrix(btns.getStage().getCamera().combined);
        btns.getStage().draw();

    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

        mainMenuBG.dispose();
        btns.getStage().dispose();

    }
} // Main Menu
