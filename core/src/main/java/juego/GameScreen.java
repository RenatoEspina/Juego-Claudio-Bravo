package juego;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;

public class GameScreen implements Screen {
    final GameMenu game;
    private OrthographicCamera camera;
    private SpriteBatch batch;    
    private BitmapFont font;
    private ArqueroClaudioBravo arquero;
    private SistemaDeJuego sistema;

    public GameScreen(final GameMenu game) {
        this.game = game;
        this.batch = game.getBatch();
        this.font = game.getFont();

        // Cargar assets para Claudio Bravo
        // Se elimina la carga de atajada.wav para solucionar el error de "File not found"
        // Sound atajadaSound = Gdx.audio.newSound(Gdx.files.internal("atajada.wav")); 
        Sound golSound = Gdx.audio.newSound(Gdx.files.internal("gol.wav"));
        Sound premioSound = Gdx.audio.newSound(Gdx.files.internal("premio.wav"));

        // Se ajusta el constructor de ArqueroClaudioBravo para que no reciba sonidos
        arquero = new ArqueroClaudioBravo(
            new Texture(Gdx.files.internal("claudio_bravo.png"))
            // Se eliminan los argumentos de sonido: atajadaSound, golSound
        );

        // Cargar assets del sistema de juego
        Texture balonNormal = new Texture(Gdx.files.internal("balon_normal.png"));
        Texture balonDificil = new Texture(Gdx.files.internal("balon_dificil.png"));
        Texture premioVida = new Texture(Gdx.files.internal("vida_extra.png"));
        Texture premioPuntos = new Texture(Gdx.files.internal("puntos_extra.png"));

        Music musicaFondo = Gdx.audio.newMusic(Gdx.files.internal("hinchada.mp3"));

        // **NOTA IMPORTANTE:**
        // Asumo que ya modificaste SistemaDeJuego.java para que solo reciba golSound y premioSound,
        // ya que atajadaSound fue eliminado de ArqueroClaudioBravo.
        // Si SistemaDeJuego aún espera atajadaSound, el código de abajo causará un error.
        
        sistema = new SistemaDeJuego(
            balonNormal, balonDificil, premioVida, premioPuntos,
            golSound, premioSound, musicaFondo
            // Se elimina atajadaSound
        );

        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 480);    

        float anchoArquero = 233;
        float altoArquero = 113;
        arquero.crear(400 - (anchoArquero / 2), 20, anchoArquero, altoArquero);
        sistema.crear();
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0.2f, 0.5f, 0.2f, 1); // Color de cancha verde

        camera.update();
        batch.setProjectionMatrix(camera.combined);

        batch.begin();

        // Dibujar estadísticas
        font.draw(batch, "Atajadas: " + arquero.getAtajadas(), 5, 475);
        font.draw(batch, "Vidas: " + arquero.getVidas(), 200, 475);
        font.draw(batch, "Goles: " + arquero.getGolesRecibidos(), 400, 475);
        font.draw(batch, "HighScore: " + game.getHigherScore(), 600, 475);

        // LÓGICA DE PAUSA POR TECLA
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE) || Gdx.input.isKeyJustPressed(Input.Keys.P)) {
            this.pause();
        }

        // Lógica de juego continua (sin pausa por celebración)
        arquero.mover(delta);
        
        if (!sistema.actualizarMovimiento(arquero)) {
            if (game.getHigherScore() < arquero.getAtajadas())
                game.setHigherScore(arquero.getAtajadas());
            game.setScreen(new GameOverScreen(game));
            dispose();
        }

        arquero.dibujar(batch);
        sistema.actualizarDibujo(batch);

        batch.end();
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void show() {
        // continuar con sonido de hinchada
        sistema.continuar();
    }

    @Override
    public void hide() {
    }

    @Override
    public void pause() {
        sistema.pausar();
        game.setScreen(new PausaScreen(game, this));    
    }

    @Override
    public void resume() {
    }

    @Override
    public void dispose() {
        arquero.destruir();
        sistema.destruir();
        
        // Es esencial que los recursos de sonido que SÍ se cargaron se liberen:
        // gol.wav y premio.wav se liberarán en SistemaDeJuego.destruir()
    }
}