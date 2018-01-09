package classes.managers;

import classes.CollisionMasks;
import classes.factories.ShapeHelper;
import classes.gameobjects.GameObject;
import classes.gameobjects.playable.SpaceShip;
import classes.gameobjects.playable.SpaceShipEnemy;
import classes.gameobjects.unplayble.Laser;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import interfaces.IGameObject;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static classes.Constants.*;

public class GameManager
{
    private WorldManager worldManager;
    private SpaceShipTexturesHelper spaceShipTexturesHelper;
    private ShapeHelper shapeHelper;
    private float accumulator;

    private List<IGameObject> gameObjects;

    private SpaceShip player;

    public GameManager()
    {
        this.worldManager = new WorldManager();
        this.spaceShipTexturesHelper = new SpaceShipTexturesHelper();
        shapeHelper = new ShapeHelper(worldManager.world);
        gameObjects = new CopyOnWriteArrayList<>();

        createPlayer(new Vector2(0, 0));

        for (int x = 0; x < 5; x++)
        {
            for (int y = 0; y < 5; y++)
            {
                createEnemy(new Vector2(x * 31 * 100, y * 33 * 100));
            }
        }
    }

    public void update(float deltaTime)
    {
        doPhysicsStep(deltaTime);
        for (Iterator<IGameObject> it = gameObjects.iterator(); it.hasNext(); )
        {
            IGameObject gameObject = it.next();
            gameObject.update();
        }

        // TODO can this be merged?

        for (Iterator<IGameObject> it = gameObjects.iterator(); it.hasNext(); )
        {
            GameObject gameObject = (GameObject) it.next();
            if (gameObject.isToDelete())
            {
                // Deleting this body is useful
                worldManager.world.destroyBody(gameObject.getFixture().getBody());
                gameObjects.remove(gameObject);
            }
        }
    }

    public Laser fireLaser(
            Vector2 pos,
            float speed,
            float rotation)
    {
        Laser laser = new Laser(new Vector2(pos), rotation, speed);
        Fixture fixture = shapeHelper.CreateCube(laser);
        fixture.setFilterData(CollisionMasks.PLAYER_FILTER);

        laser.setFixture(fixture); // TODO refactor this.
        laser.setSpeed(speed);

        fixture.setUserData(laser);

        gameObjects.add(laser);
        return laser;
    }

    public SpaceShip createPlayer(Vector2 pos)
    {
        SpaceShip ship = new SpaceShip(pos, 0f, spaceShipTexturesHelper.getSpaceShipSprite(1));
        ship.setGameManager(this);

        Fixture fixture = shapeHelper.CreateCube(ship);
        fixture.setFilterData(CollisionMasks.PLAYER_FILTER);
        ship.setFixture(fixture);
        fixture.setUserData(ship);


        gameObjects.add(ship);
        player = ship;
        return ship;
    }

    public SpaceShipEnemy createEnemy(Vector2 pos)
    {
        SpaceShipEnemy enemy = new SpaceShipEnemy(pos, 0, spaceShipTexturesHelper.getSpaceShipSprite(16), player);

        Fixture fixture = shapeHelper.CreateCube(enemy);
        fixture.setFilterData(CollisionMasks.ENEMY_FILTER);
        enemy.setFixture(fixture);
        fixture.setUserData(enemy);

        gameObjects.add(enemy);
        return enemy;
    }

    public void draw(Batch batch)
    {
        for (IGameObject go : gameObjects)
        {
            go.Draw(batch);
        }
    }

    public void draw(ShapeRenderer shapeRenderer)
    {
        for (IGameObject go : gameObjects)
        {
            go.Draw(shapeRenderer);
        }
    }

    private void doPhysicsStep(float deltaTime)
    {
        // fixed time step
        // max frame time to avoid spiral of death (on slow devices)
        float frameTime = Math.min(deltaTime, 0.25f);
        accumulator += frameTime;
        while (accumulator >= TIME_STEP)
        {
            worldManager.world.step(TIME_STEP, VELOCITY_ITERATIONS, POSITION_ITERATIONS);

            accumulator -= TIME_STEP;
        }
    }

    public SpaceShip getPlayer()
    {
        return player;
    }

    public WorldManager getWorldManager()
    {
        return worldManager;
    }

    public void dispose()
    {
        for (IGameObject go : gameObjects)
        {
            //TODO dispose all game objects
        }
    }
}
