package screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.game.GameMain;

import javax.crypto.Mac;

import clouds.CloudsController;
import clouds.cloud;
import helpers.GameInfo;
import helpers.GameManager;
import huds.UIHud;
import player.Player;


public class GameplayScreen implements Screen, ContactListener {

    private GameMain game;

    private OrthographicCamera mainCamera;
    private Viewport gameViewport;

    private OrthographicCamera box2DCamera;
    private Box2DDebugRenderer debugRenderer;

    private World world;

    private Sprite[] backgrounds;
    private float lastYPosition;
    private float lastPlayerY;

    private boolean touchedForTheFirstTime;

    private UIHud hud;

    private CloudsController cloudsController;

    private Player player;

    public GameplayScreen(GameMain game) {

        this.game = game;

        mainCamera = new OrthographicCamera(GameInfo.WIDTH, GameInfo.HEIGHT);
        mainCamera.position.set(GameInfo.WIDTH / 2f, GameInfo.HEIGHT / 2f, 0);

        gameViewport = new StretchViewport(GameInfo.WIDTH, GameInfo.HEIGHT, mainCamera);

        box2DCamera = new OrthographicCamera();
        box2DCamera.setToOrtho(false, GameInfo.WIDTH / GameInfo.PPM,
                GameInfo.HEIGHT / GameInfo.PPM);
        box2DCamera.position.set(GameInfo.WIDTH / 2f, GameInfo.HEIGHT / 2f -10, 0);

        debugRenderer = new Box2DDebugRenderer();
        world = new World(new Vector2(0, -9.8f), true);
        // informing the world that the contact listener is the gameplay class
        world.setContactListener(this);

        hud = new UIHud(game);

        cloudsController = new CloudsController(world);

        player = cloudsController.positionThePlayer(player);


        createBackgrounds();

    }

    void handleInput(float dt) {

        if(Gdx.input.isKeyPressed(Input.Keys.LEFT)) {

            player.movePlayer(-2);

        } else if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)){

            player.movePlayer(2);



        } else {
            player.isWalking(false);
        }

    }

    void checkForFirstTouch() {

        if(!touchedForTheFirstTime){

            if(Gdx.input.justTouched()) {

                touchedForTheFirstTime = true;
                GameManager.getInstance().isPaused = false;
                lastPlayerY = player.getY();

            }



        }


    }

    void update(float dt){

        checkForFirstTouch();

        if(!GameManager.getInstance().isPaused) {

            handleInput(dt);
            moveCamera();
            checkBGOutOfBounds();
            cloudsController.setCameraY(mainCamera.position.y);
            cloudsController.createAndArrangeNewClouds();
            cloudsController.removeOffScreenCollectables();
            checkPlayerBounds();
            countScore();

        }
    }

    void moveCamera(){

        mainCamera.position.y -= 2;

    }

    void createBackgrounds() {

        backgrounds = new Sprite[3];

        for (int i = 0; i < backgrounds.length; i++) {

            backgrounds[i] = new Sprite(new Texture("Backgrounds/Game BG.png"));
            backgrounds[i].setPosition(0, -(i * backgrounds[i].getHeight()));
            lastYPosition = Math.abs(backgrounds[i].getY());


        }

    }

    void drawBackgrounds(){

        for (int i = 0; i < backgrounds.length; i++) {

            game.fetchBatch().draw(backgrounds[i], backgrounds[i].getX(), backgrounds[i].getY());

        }

    }

    void checkBGOutOfBounds() {

        for (int i = 0; i < backgrounds.length; i++) {

            if((backgrounds[i].getY() - backgrounds[i].getHeight() / 2f - 5) > mainCamera.position.y) {

                float newPosition = backgrounds[i].getHeight() + lastYPosition;
                backgrounds[i].setPosition(0, -newPosition);
                lastYPosition = Math.abs(newPosition);


            }

        }

    }

    void countScore(){

        if (lastPlayerY > player.getY()) {

            hud.incrementScore(1);
            lastPlayerY = player.getY();

        }

    }

    void checkPlayerBounds() {

        // checks if player is out of bounds from top of the screen

        if(player.getY() - GameInfo.HEIGHT / 2f - player.getHeight() / 2f > mainCamera.position.y) {

            GameManager.getInstance().isPaused = true;


        }

        // checks for downwards of the screen out of bounds

        if(player.getY() + GameInfo.HEIGHT / 2f + player.getHeight() / 2f < mainCamera.position.y) {

            GameManager.getInstance().isPaused = true;

        }

        if (player.getX() - 25 > GameInfo.WIDTH || player.getX() + 60 < 0) {
            // for right side out of bounds
            GameManager.getInstance().isPaused = true;

        }

    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {

        update(delta);

        Gdx.gl.glClearColor(1, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        game.fetchBatch().begin();

        drawBackgrounds();

        cloudsController.drawClouds(game.fetchBatch());
        cloudsController.drawCollectables(game.fetchBatch());

        player.drawPlayerIdle(game.fetchBatch());
        player.drawPlayerAnimation(game.fetchBatch());

        game.fetchBatch().end();

       // debugRenderer.render(world, box2DCamera.combined);

        game.fetchBatch().setProjectionMatrix(hud.getStage().getCamera().combined);
        hud.getStage().draw();

        game.fetchBatch().setProjectionMatrix(mainCamera.combined); // return projection matrix of our camera.
        mainCamera.update();

        player.updatePlayer();

        world.step(Gdx.graphics.getDeltaTime(), 6,2);

    }

    @Override
    public void resize(int width, int height) {
        gameViewport.update(width, height);

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

        world.dispose();
        for (int i = 0; i < backgrounds.length; i++) {

            backgrounds[i].getTexture().dispose();

        }
        player.getTexture().dispose();
        debugRenderer.dispose();

    }

    @Override
    public void beginContact(Contact contact) {

        Fixture body1, body2;

        if(contact.getFixtureA().getUserData() == "Player") {

            body1 = contact.getFixtureA();
            body2 = contact.getFixtureB();

        } else {

            body1 = contact.getFixtureB();
            body2 = contact.getFixtureA();

        }

        if(body1.getUserData() == "Player" && body2.getUserData() == "Coin") {
            // player has collided with the coin
            hud.incrementCoins();
            body2.setUserData("Remove");
            cloudsController.removeCollectables();


        }

        if(body1.getUserData() == "Player" && body2.getUserData() == "Life") {
            // player has collided with the life
            hud.incrementLifes();
            body2.setUserData("Remove");
            cloudsController.removeCollectables();

        }

    }

    @Override
    public void endContact(Contact contact) {

    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }
} // gameplay
