package juego;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

public class SistemaDeJuego {
    private Array<Colisionable> objetos;
    private Array<Rectangle> posiciones;
    private long ultimoTiempoCreacion;
    private long tiempoInicioJuego; 
    
    // Parámetros de dificultad
    private final float VELOCIDAD_BASE = 200f; 
    private final float INCREMENTO_POR_SEGUNDO = 10f;

    private Texture balonNormal;
    private Texture balonDificil;
    private Texture premioVida;
    private Texture premioPuntos;
    private Sound sonidoAtajada;
    private Sound sonidoGol;
    private Sound sonidoPremio;
    private Music musicaFondo;

    public SistemaDeJuego(Texture balonNormal, Texture balonDificil, 
                          Texture premioVida, Texture premioPuntos,
                          Sound atajadaSound, Sound golSound, Sound premioSound, 
                          Music musica) {
        this.balonNormal = balonNormal;
        this.balonDificil = balonDificil;
        this.premioVida = premioVida;
        this.premioPuntos = premioPuntos;
        this.sonidoAtajada = atajadaSound;
        this.sonidoGol = golSound;
        this.sonidoPremio = premioSound;
        this.musicaFondo = musica;
    }

    public void crear() {
        objetos = new Array<Colisionable>();
        posiciones = new Array<Rectangle>();
        
        tiempoInicioJuego = TimeUtils.nanoTime(); 
        
        crearObjeto();
        musicaFondo.setLooping(true);
        musicaFondo.setVolume(0.4f); 
        musicaFondo.play();
    }

    private void crearObjeto() {
        Rectangle posicion = new Rectangle();
        posicion.x = MathUtils.random(50, 700);
        posicion.y = 480;
        posicion.width = 64;
        posicion.height = 64;
        posiciones.add(posicion);

        // 1. Calcular la velocidad base actual
        float tiempoTranscurrido = (TimeUtils.nanoTime() - tiempoInicioJuego) / 1_000_000_000f;
        float velocidadBaseActual = VELOCIDAD_BASE + (tiempoTranscurrido * INCREMENTO_POR_SEGUNDO);
        float velocidadFinal;

        int tipoObjeto = MathUtils.random(1, 10);
        Colisionable objeto;

        // 2. Crear objeto y calcular velocidad final según el tipo
        if (tipoObjeto <= 5) { 
            velocidadFinal = velocidadBaseActual; 
            // CORRECCIÓN: Llamada al constructor de Balon con velocidad float
            objeto = new Balon(balonNormal, sonidoAtajada, 1, velocidadFinal);
        } else if (tipoObjeto <= 8) { 
            velocidadFinal = velocidadBaseActual * 1.5f; 
            // CORRECCIÓN: Llamada al constructor de Balon con velocidad float
            objeto = new Balon(balonDificil, sonidoGol, 2, velocidadFinal);
        } else if (tipoObjeto <= 9) { 
            velocidadFinal = velocidadBaseActual * 0.7f; 
            // CORRECCIÓN: Llamada al constructor de Premio con velocidad float
            objeto = new Premio(premioVida, sonidoPremio, 3, velocidadFinal);
        } else { 
            velocidadFinal = velocidadBaseActual * 0.7f; 
            // CORRECCIÓN: Llamada al constructor de Premio con velocidad float
            objeto = new Premio(premioPuntos, sonidoPremio, 4, velocidadFinal);
        }

        ((EntidadMovil)objeto).crear(posicion.x, posicion.y, 64, 64);
        objetos.add(objeto);
        ultimoTiempoCreacion = TimeUtils.nanoTime();
    }

    public boolean actualizarMovimiento(ArqueroClaudioBravo arquero) {
        if(TimeUtils.nanoTime() - ultimoTiempoCreacion > 1000000000) 
            crearObjeto();

        for (int i = objetos.size - 1; i >= 0; i--) {
            Colisionable objeto = objetos.get(i);
            EntidadMovil entidad = (EntidadMovil)objeto;

            entidad.mover(Gdx.graphics.getDeltaTime());

            if(entidad.getArea().y + 64 < 0) {
                objetos.removeIndex(i);
                posiciones.removeIndex(i);
                continue;
            }

            if(entidad.getArea().overlaps(arquero.getArea())) {
                objeto.alColisionar(arquero);
                objetos.removeIndex(i);
                posiciones.removeIndex(i);

                if (arquero.getVidas() <= 0) {
                    return false;
                }
            }
        }
        return true;
    }
    
    public void actualizarDibujo(SpriteBatch batch) {
        for (Colisionable objeto : objetos) {
            ((EntidadMovil)objeto).dibujar(batch);
        }
    }

    public void destruir() {
        for (Colisionable objeto : objetos) {
            ((EntidadMovil)objeto).dispose(); 
        }
        objetos.clear();
        posiciones.clear();

        balonNormal.dispose();
        balonDificil.dispose();
        premioVida.dispose();
        premioPuntos.dispose();
        sonidoAtajada.dispose();
        sonidoGol.dispose();
        sonidoPremio.dispose();
        musicaFondo.dispose();
    }

    public void pausar() {
        musicaFondo.stop();
    }

    public void continuar() {
        musicaFondo.play();
    }
}