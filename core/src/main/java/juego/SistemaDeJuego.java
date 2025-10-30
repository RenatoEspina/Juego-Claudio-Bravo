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
    
    // CONTROL DE SONIDO LIMPIO
    private long idSonidoGolActual = 0; 
    private long idSonidoPremioActual = 0; 
    
    // Parámetros de dificultad
    private final float VELOCIDAD_BASE = 200f; 
    private final float INCREMENTO_POR_SEGUNDO = 5f; 
    private final float VELOCIDAD_MAXIMA = 500f; 
    
    // PARÁMETROS DE FRECUENCIA
    private final float TIEMPO_BASE_CREACION = 0.8f; 
    private final float DECREMENTO_POR_SEGUNDO = 0.03f; 
    private final float TIEMPO_MINIMO_CREACION = 0.2f; 
    
    // PARÁMETROS DE PROBABILIDAD DE PREMIOS
    private final float AUMENTO_PROB_PREMIO_POR_SEGUNDO = 0.5f; 
    private final float PROB_PREMIO_BASE = 16f; 
    private final float MAX_PROB_PREMIO = 40f; 
    
    // NUEVAS PROBABILIDADES PARA BALONES (Total 100%)
    private final float PROB_BALON_NORMAL = 0.45f;    // 30% original + 15% de Curvo = 45%
    private final float PROB_BALON_DIFICIL = 0.25f;   // Mantenido
    private final float PROB_BALON_ZIGZAG = 0.30f;    // 20% original + 10% de Curvo = 30%
    // NOTA: PROB_BALON_NORMAL (0.45) + PROB_BALON_DIFICIL (0.25) + PROB_BALON_ZIGZAG (0.30) = 1.00 (100%)
    
    // Constante para márgenes
    private final float MARGEN_LATERAL = 100;
    private final float ANCHO_PANTALLA = 800;

    private Texture balonNormal;
    private Texture balonDificil;
    private Texture premioVida;
    private Texture premioPuntos;
    private Sound sonidoGol;
    private Sound sonidoPremio;
    private Music musicaFondo;

    public SistemaDeJuego(Texture balonNormal, Texture balonDificil, 
                          Texture premioVida, Texture premioPuntos,
                          Sound golSound, Sound premioSound, 
                          Music musica) {
        this.balonNormal = balonNormal;
        this.balonDificil = balonDificil;
        this.premioVida = premioVida;
        this.premioPuntos = premioPuntos;
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
    
    private void reproducirSonidoGol() {
        if (idSonidoGolActual != 0) {
            sonidoGol.stop(idSonidoGolActual);
        }
        idSonidoGolActual = sonidoGol.play(0.85f);
    }
    
    private void reproducirSonidoPremio() {
        if (idSonidoPremioActual != 0) {
            sonidoPremio.stop(idSonidoPremioActual);
        }
        idSonidoPremioActual = sonidoPremio.play(0.95f);
    }
    
    private void crearObjeto() {
        Rectangle posicion = new Rectangle();
        
        posicion.width = 64;
        posicion.height = 64;
        posicion.x = MathUtils.random(MARGEN_LATERAL, ANCHO_PANTALLA - posicion.width - MARGEN_LATERAL);
        posicion.y = 480;
        posiciones.add(posicion);

        // 1. Calcular la velocidad base actual
        float tiempoTranscurrido = (TimeUtils.nanoTime() - tiempoInicioJuego) / 1_000_000_000f;
        float velocidadAumentada = VELOCIDAD_BASE + (tiempoTranscurrido * INCREMENTO_POR_SEGUNDO);
        float velocidadBaseActual = Math.min(velocidadAumentada, VELOCIDAD_MAXIMA);  
        float velocidadFinal;

        // 2. Calcular probabilidad de premios
        float aumentoPremio = tiempoTranscurrido * AUMENTO_PROB_PREMIO_POR_SEGUNDO;
        float probTotalPremios = Math.min(PROB_PREMIO_BASE + aumentoPremio, MAX_PROB_PREMIO);

        float randomRange = MathUtils.random(0f, 100f);
        Colisionable objeto;
        
        final float MULTIPLICADOR_ESPECIAL = 1.2f;

        // 3. Crear objeto según el tipo
        if (randomRange <= probTotalPremios) {
            // Premio
            float probVida = probTotalPremios / 2;
            velocidadFinal = velocidadBaseActual * 0.7f;
            
            if (randomRange <= probVida) {
                objeto = new Premio(premioVida, sonidoPremio, 3, velocidadFinal);
            } else {
                objeto = new Premio(premioPuntos, sonidoPremio, 4, velocidadFinal);
            }
            
        } else {
            // Balón - Usando los porcentajes corregidos (Normal: 45%, Difícil: 25%, ZigZag: 30%)
            float probBalon = (100f - probTotalPremios);
            
            // Definir rangos acumulativos (porcentajes del restante 'probBalon')
            float acumuladoNormal = probBalon * PROB_BALON_NORMAL;
            float acumuladoDificil = acumuladoNormal + (probBalon * PROB_BALON_DIFICIL);
            // El resto es ZigZag
            
            float balonRange = MathUtils.random(0f, probBalon);
            
            if (balonRange <= acumuladoNormal) {
                // Balón normal
                velocidadFinal = velocidadBaseActual; 
                objeto = new Balon(balonNormal, 1, velocidadFinal);
            } else if (balonRange <= acumuladoDificil) {
                // Balón difícil
                velocidadFinal = velocidadBaseActual * 1.5f; 
                objeto = new Balon(balonDificil, 2, velocidadFinal); 
            } else {
                // Balón ZigZag (el 30% restante)
                velocidadFinal = velocidadBaseActual * MULTIPLICADOR_ESPECIAL; 
                objeto = new BalonZigZag(balonNormal, velocidadFinal);
            }
        }

        ((EntidadMovil)objeto).crear(posicion.x, posicion.y, posicion.width, posicion.height);
        objetos.add(objeto);
        ultimoTiempoCreacion = TimeUtils.nanoTime();
    }

    public boolean actualizarMovimiento(ArqueroClaudioBravo arquero) {
        float tiempoTranscurrido = (TimeUtils.nanoTime() - tiempoInicioJuego) / 1_000_000_000f;

        // 1. Lógica de creación de objetos
        float tiempoEntreCreacionAumentado = TIEMPO_BASE_CREACION - (tiempoTranscurrido * DECREMENTO_POR_SEGUNDO);
        float tiempoEntreCreacion = Math.max(tiempoEntreCreacionAumentado, TIEMPO_MINIMO_CREACION);  
        long tiempoEntreCreacionNanos = (long)(tiempoEntreCreacion * 1_000_000_000L);  
        
        if(TimeUtils.nanoTime() - ultimoTiempoCreacion > tiempoEntreCreacionNanos)  
            crearObjeto();

        // 2. Lógica de movimiento y colisiones
        for (int i = objetos.size - 1; i >= 0; i--) {
            Colisionable objeto = objetos.get(i);
            EntidadMovil entidad = (EntidadMovil)objeto;

            entidad.mover(Gdx.graphics.getDeltaTime());

            // GOL NO ATAJADO (Balón sale por debajo)
            if(entidad.getArea().y + 64 < 0) {
                if (objeto instanceof Balon) {
                    arquero.registrarGol(); // Suma gol y resta vida
                    reproducirSonidoGol(); 
                }
                objetos.removeIndex(i);
                posiciones.removeIndex(i);
                
                // VERIFICACIÓN DE GAME OVER: Si las vidas llegan a 0 por gol
                if (arquero.getVidas() <= 0) {
                    return false; 
                }
                continue;
            }

            // Colisión con el arquero
            if(entidad.getArea().overlaps(arquero.getHitbox())) {
                
                objeto.alColisionar(arquero, entidad.getArea(), arquero.getHitbox());
                
                if (objeto instanceof Premio) {
                    reproducirSonidoPremio(); 
                }
                
                objetos.removeIndex(i);
                posiciones.removeIndex(i);

                // VERIFICACIÓN DE GAME OVER: Si las vidas llegan a 0 por colisión con Balón Difícil
                // Aunque alColisionar() es la lógica de colisión, si el arquero pierde vida aquí, 
                // debemos verificar y salir. La lógica de Balón/Premio debe actualizar la vida.
                if (arquero.getVidas() <= 0) {
                    return false; // Fin del juego
                }
            }
        }
        
        // 3. SEGUNDA VERIFICACIÓN DE GAME OVER (Garantía)
        // Aunque la verificación dentro del bucle es clave, esta asegura el estado final.
        if (arquero.getVidas() <= 0) {
            return false;
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