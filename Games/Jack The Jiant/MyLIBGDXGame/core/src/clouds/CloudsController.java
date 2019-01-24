package clouds;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

import java.util.Random;

import collectables.Collectable;
import helpers.GameInfo;
import player.Player;

public class CloudsController {

    private World world;

    private Array<cloud> clouds = new Array<cloud>();
    private Array<Collectable> collectables = new Array<Collectable>();

    private final float distanceBetweenClouds = 250f;
    private float minX, maxX;
    private float lastCloudPositionY;
    private float cameraY;

    private Random random = new Random();

    public CloudsController(World world){

        this.world = world;
        minX = GameInfo.WIDTH / 2f - 110;
        maxX = GameInfo.WIDTH / 2f + 110;
        createClouds();
        cloudPositions(true);

    }

    void createClouds(){

        for (int i = 0; i < 2; i++) {

            clouds.add(new cloud(world, "Dark Cloud"));

        }

        int index = 1;

        for (int i = 0; i < 6; i++) {

            clouds.add(new cloud(world, "Cloud " + index));
            index++;

            if (index == 4) {

                index = 1;

            }

        }

        clouds.shuffle();


    }

    public void cloudPositions(boolean firstTimePositioning) {

        while (clouds.get(0).getCloudName() == "Dark Cloud"){

            clouds.shuffle();

        }

        float positionY = 0;

        if(firstTimePositioning) {

            positionY = GameInfo.HEIGHT / 2f;

        } else {

            positionY = lastCloudPositionY;

        }

        int controlX = 0;

        for (cloud cloud : clouds) {

            if(cloud.getX() == 0 && cloud.getY() == 0) {

                float tempX = 0;

                if(controlX == 0) {

                    tempX = randomCloudPosition(maxX - 60, maxX);
                    controlX = 1;
                    cloud.setDrawLeft(false);

                } else if(controlX == 1){

                    tempX = randomCloudPosition(minX + 60, minX);
                    controlX = 0;
                    cloud.setDrawLeft(true);

                }

                cloud.setSpritePosition(tempX, positionY);
                positionY -= distanceBetweenClouds;
                lastCloudPositionY = positionY;

                if (!firstTimePositioning && cloud.getCloudName() == "Dark Cloud") {

                    int rand = random.nextInt(10);

                    if (rand > 1) {

                        int randomCollectable = random.nextInt(2);

                        if (randomCollectable == 0) {
                            // spawn a life, if the life cound is lower than 2
                            Collectable collectable = new Collectable(world, "Life");
                            collectable.setCollectablePosition(cloud.getX(),
                                    cloud.getY() + 40);
                            collectables.add(collectable);



                        } else {
                            // spawn a coin
                            Collectable collectable = new Collectable(world, "Coin");
                            collectable.setCollectablePosition(cloud.getX(),
                                    cloud.getY() + 40);
                            collectables.add(collectable);

                        }

                    }

                }


            }


        }

    }



    public void drawClouds(SpriteBatch batch){

        for (cloud cloud : clouds) {

           if (cloud.getDrawLeft()) {

               batch.draw(cloud, cloud.getX() - cloud.getWidth() /2f - 20,
                       cloud.getY() - cloud.getHeight() /2f);

           } else {

               batch.draw(cloud, cloud.getX() - cloud.getWidth() /2f + 10,
                       cloud.getY() - cloud.getHeight() /2f);

           }

        }

    }

    public void drawCollectables(SpriteBatch batch) {

                for (Collectable c : collectables) {

                    c.updateCollectable();
                    batch.draw(c, c.getX(), c.getY());

                }

    }

    public void removeCollectables() {

        for (int i = 0; i < collectables.size; i++) {
            if(collectables.get(i).getFixture().getUserData() == "Remove") {
                collectables.get(i).changeFilter();
                collectables.get(i).getTexture().dispose();
                collectables.removeIndex(i);

            }

        }

    }

    public void createAndArrangeNewClouds() {

        for (int i = 0; i < clouds.size; i++) {

            if((clouds.get(i).getY() - GameInfo.HEIGHT /2 - 20) > cameraY) {

                clouds.get(i).getTexture().dispose();
                clouds.removeIndex(i);

            }

        }

        if (clouds.size == 4) {

            createClouds();
            cloudPositions(false);

        }

    }

    public void removeOffScreenCollectables () {

        for (int i = 0; i < collectables.size; i++){

            if((collectables.get(i).getY() - GameInfo.HEIGHT / 2f - 15) > cameraY){

               collectables.get(i).getTexture().dispose();
               collectables.removeIndex(i);
               System.out.println("removed");

            }

        }

    }

    public void setCameraY(float cameraY) {

        this.cameraY = cameraY;

    }

    public Player positionThePlayer(Player player) {

        player = new Player(world, clouds.get(0).getX(),
                clouds.get(0).getY() + 78);
        return player;

    }

    public float randomCloudPosition(float min, float max) {

        return random.nextFloat() * (max - min) + min;

    }

} // cloud controller
