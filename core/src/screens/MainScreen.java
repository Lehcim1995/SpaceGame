package screens;

import classes.gameobjects.GameObject;
import classes.managers.GameManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.school.spacegame.Main;

import java.rmi.RemoteException;

//http://badlogicgames.com/forum/viewtopic.php?t=19454&p=81586
public class MainScreen implements Screen
{
    private GameManager gameManager;

    // Camera and drawing
    private Batch batch;
    private Batch textBatch;
    private ShapeRenderer shapeRenderer;
    private Camera camera;
    private float zoomLevel = 5;
    private GameObject textDrawer;

    // Text stuff
    private BitmapFont font;
    private GlyphLayout layout;

    // Background
    private Texture background;

    //Debug
    private Box2DDebugRenderer box2DDebugRenderer;

    private Main parent;
    private boolean online;
    private GameManager.playerType type;

    public MainScreen(
            Main parent,
            boolean online,
            GameManager.playerType type)
    {
        this.parent = parent;
        this.online = online;
        this.type = type;
    }

    @Override
    public void show()
    {
        // init
        try
        {
            gameManager = new GameManager(online, type);
        }
        catch (RemoteException e)
        {
            e.printStackTrace();
            // exit
            parent.sceneManager.LoadMainMenuScreen();
        }
        batch = new SpriteBatch();
        textBatch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        shapeRenderer.setAutoShapeType(true);
        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.update();
        box2DDebugRenderer = new Box2DDebugRenderer();

        font = new BitmapFont();
        layout = new GlyphLayout();

        background = new Texture(Gdx.files.local("/core/assets/textures/seamless space.png"));
        background.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
    }

    @Override
    public void render(float delta)
    {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        switch (gameManager.getPlayerType())
        {
            case Destroyer:
                camera.position.set(gameManager.getPlayer().getPosition(), 0);
                break;
            case Spawner:
                camera.position.set(gameManager.getWaveSpawnerPlayer().getPosition(), 0);
                break;
        }
        camera.update();

        batch.begin();
        backGround();
        gameManager.draw(batch);
        batch.end();
        gameManager.update(delta);

        shapeRenderer.begin();
        gameManager.draw(shapeRenderer);
        shapeRenderer.end();

//        textBatch.begin();
//        int fps = (int) (1 / delta);
//        font.setColor(fps < 30 ? Color.RED : Color.WHITE);
//        final Vector2 pos = new Vector2(100, 100);
//        gameManager.getPlayer().DrawText(textBatch, font, layout, "Fps: " + fps, pos);
//        textBatch.end();

        box2DDebugRenderer.setDrawBodies(false);
        box2DDebugRenderer.setDrawVelocities(false);
        box2DDebugRenderer.render(gameManager.getWorldManager().world, camera.combined);
        batch.setProjectionMatrix(camera.combined);
        shapeRenderer.setProjectionMatrix(camera.combined);
    }

    @Override
    public void resize(int width, int height)
    {
        camera.viewportWidth = width;
        camera.viewportHeight = height;
        camera.update();

        batch.setProjectionMatrix(camera.combined);
        textBatch = new SpriteBatch();
    }

    @Override
    public void pause()
    {

    }

    @Override
    public void resume()
    {

    }

    @Override
    public void hide()
    {

    }

    @Override
    public void dispose()
    {
        gameManager.dispose();
        batch.dispose();
    }

    private void backGround()
    {
        final float textureHeight = 1024;
        final float textureWidth = 1024;

        final float offSetHeight = textureHeight * 2;
        final float offSetWidth = textureWidth * 2;

        final int totalHeight = (int) (textureHeight * 4);
        final int totalWidth = (int) (textureWidth * 4);

        GameObject player = null;
        switch (type)
        {
            case Destroyer:
                player = gameManager.getPlayer();
                break;
            case Spawner:
                player = gameManager.getWaveSpawnerPlayer();
                break;
            case Spectator:
                // Exit
                return;
//                break;
        }

        batch.draw(background, -offSetWidth + (textureWidth * (int) (player.getPosition().x / textureWidth)), -offSetHeight + (textureHeight * (int) (player.getPosition().y / textureHeight)), 0, 0, totalWidth, totalHeight);
    }
}
