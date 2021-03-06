package screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.GdxBuild;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.school.spacegame.Main;

public class AbstractScreen implements Screen //TODO implement this with the other screens
{
    protected Stage stage;
    protected Main main;
    protected Skin skin;
    protected Table table;

    public AbstractScreen(Main main)
    {
        /// create stage and set it as input processor
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        this.main = main;
    }

    @Override
    public void show()
    {
        // Create a table that fills the screen. Everything else will go inside this table.
        table = new Table();
        table.setFillParent(true);
        table.setDebug(false);
        stage.addActor(table);

        // temporary until we have asset manager in

        FileHandle fh = Gdx.files.internal("/core/assets/skins/neon/skin/neon-ui.json");
        FileHandle fh2 = Gdx.files.local("/core/assets/skins/neon/skin/neon-ui.json");
        FileHandle fh3 = Gdx.files.absolute("D://Java dev//SpaceGame/core/assets/skins/neon/skin/neon-ui.json");
        FileHandle fh4 = Gdx.files.external("/core/assets/skins/neon/skin/neon-ui.json");
        String p = fh.path();
        String p2 = fh2.path();
        String p3 = fh3.path();
        String p4 = fh4.path();
        skin = new Skin(fh3);
    }

    @Override
    public void render(float delta)
    {
        // clear the screen ready for next set of images to be drawn
        Gdx.gl.glClearColor(0f, 0f, 0f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // tell our stage to do actions and draw itself
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();
    }

    @Override
    public void resize(
            int width,
            int height)
    {
        stage.getViewport().update(width, height, true);
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
        stage.dispose();
    }

    public Main getMain()
    {
        return main;
    }
}
