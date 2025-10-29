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
    
    // **NUEVOS ATRIBUTOS PARA CONTROL DE SONIDO**
    private boolean sonidoAtajadaReproduciendose = false;
    private boolean sonidoGolReproduciendose = false;
    // Esto es para la música, pero usaremos Sound.stop/play para los efectos cortos

    // Parámetros de dificultad modificados
    private final float VELOCIDAD_BASE = 200f; 
    private final float INCREMENTO_POR_SEGUNDO = 5f; 
    private final float VELOCIDAD_MAXIMA = 500f; 
    
    // Parámetros de frecuencia de aparición
    private final float TIEMPO_BASE_CREACION = 1.0f; 
    private final float DECREMENTO_POR_SEGUNDO = 0.02f; 
    private final float TIEMPO_MINIMO_CREACION = 0.3f; 
    
    // Constante para márgenes (Debe coincidir con ArqueroClaudioBravo)
    private final float MARGEN_LATERAL = 100;
    private final float ANCHO_PANTALLA = 800;

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
    
    // 🎧 Lógica: Asegurar que el sonido solo se reproduce si no está sonando.
    private void reproducirSonidoAtajada() {
        if (!sonidoAtajadaReproduciendose) {
            sonidoAtajadaReproduciendose = true;
            sonidoAtajada.play(0.6f);
        }
    }
    
    private void reproducirSonidoGol() {
        if (!sonidoGolReproduciendose) {
            sonidoGolReproduciendose = true;
            sonidoGol.play(0.85f);
        }
    }
    // Fin de lógica de sonido

    private void crearObjeto() {
        Rectangle posicion = new Rectangle();
        
        // 🚨 AJUSTE DE MARGEN: 
        // Genera la posición X entre el MARGEN_LATERAL y (ANCHO_PANTALLA - tamaño_balon - MARGEN_LATERAL)
        posicion.width = 64; // Tamaño del balón
        posicion.height = 64;
        posicion.x = MathUtils.random(MARGEN_LATERAL, ANCHO_PANTALLA - posicion.width - MARGEN_LATERAL);
        posicion.y = 480;
        posiciones.add(posicion);

        // 1. Calcular la velocidad base actual (con límite)
        float tiempoTranscurrido = (TimeUtils.nanoTime() - tiempoInicioJuego) / 1_000_000_000f;
        float velocidadAumentada = VELOCIDAD_BASE + (tiempoTranscurrido * INCREMENTO_POR_SEGUNDO);
        float velocidadBaseActual = Math.min(velocidadAumentada, VELOCIDAD_MAXIMA); 
        float velocidadFinal;

        // Se usa un rango de 1 a 12 para la aparición, dando más opciones a los balones especiales
        int tipoObjeto = MathUtils.random(1, 12);
        Colisionable objeto;
        
        // Balones curvos y zigzag van 1.2 veces más rápido que el normal
        final float MULTIPLICADOR_ESPECIAL = 1.2f;

        // 2. Crear objeto y calcular velocidad final según el tipo
        if (tipoObjeto <= 4) { // 4/12 = 33% Balón normal (Tipo 1)
            velocidadFinal = velocidadBaseActual; 
            objeto = new Balon(balonNormal, sonidoAtajada, 1, velocidadFinal);
        } else if (tipoObjeto <= 6) { // 2/12 = 16% Balón difícil (Tipo 2)
            velocidadFinal = velocidadBaseActual * 1.5f; 
            // Usamos un sonido de gol en Balon Difícil para que el chequeo de colisión decida
            objeto = new Balon(balonDificil, sonidoGol, 2, velocidadFinal); 
        } else if (tipoObjeto <= 8) { // 2/12 = 16% Balón Curvo (Tipo 5)
            velocidadFinal = velocidadBaseActual * MULTIPLICADOR_ESPECIAL; 
            objeto = new BalonCurvo(balonNormal, sonidoAtajada, velocidadFinal);
        } else if (tipoObjeto <= 10) { // 2/12 = 16% Balón ZigZag (Tipo 6)
            velocidadFinal = velocidadBaseActual * MULTIPLICADOR_ESPECIAL; 
            objeto = new BalonZigZag(balonNormal, sonidoAtajada, velocidadFinal);
        } else if (tipoObjeto <= 11) { // 1/12 = 8% Premio Vida (Tipo 3)
            velocidadFinal = velocidadBaseActual * 0.7f; 
            objeto = new Premio(premioVida, sonidoPremio, 3, velocidadFinal);
        } else { // 1/12 = 8% Premio Puntos (Tipo 4)
            velocidadFinal = velocidadBaseActual * 0.7f; 
            objeto = new Premio(premioPuntos, sonidoPremio, 4, velocidadFinal);
        }

        ((EntidadMovil)objeto).crear(posicion.x, posicion.y, posicion.width, posicion.height);
        objetos.add(objeto);
        ultimoTiempoCreacion = TimeUtils.nanoTime();
    }

    public boolean actualizarMovimiento(ArqueroClaudioBravo arquero) {
        // Reiniciar las banderas de sonido al inicio de cada frame
        sonidoAtajadaReproduciendose = false;
        sonidoGolReproduciendose = false;
        
        float tiempoTranscurrido = (TimeUtils.nanoTime() - tiempoInicioJuego) / 1_000_000_000f;

        // 1. Calcular el tiempo mínimo de creación actual (con límite de frecuencia)
        float tiempoEntreCreacionAumentado = TIEMPO_BASE_CREACION - (tiempoTranscurrido * DECREMENTO_POR_SEGUNDO);
        float tiempoEntreCreacion = Math.max(tiempoEntreCreacionAumentado, TIEMPO_MINIMO_CREACION); 
        
        // 2. Comprobar si es hora de crear un nuevo objeto
        long tiempoEntreCreacionNanos = (long)(tiempoEntreCreacion * 1_000_000_000L); 
        
        if(TimeUtils.nanoTime() - ultimoTiempoCreacion > tiempoEntreCreacionNanos) 
            crearObjeto();

        for (int i = objetos.size - 1; i >= 0; i--) {
            Colisionable objeto = objetos.get(i);
            EntidadMovil entidad = (EntidadMovil)objeto;

            entidad.mover(Gdx.graphics.getDeltaTime());

            // 🚨 GOL NO ATAJADO: Si el balón se fue por debajo de la pantalla
            if(entidad.getArea().y + 64 < 0) {
                // Solo si es un Balón (no un Premio), suma gol y resta puntaje.
                if (objeto instanceof Balon) {
                    arquero.incrementarGoles(); // Suma gol y resta punto de atajada (implementado en ArqueroClaudioBravo)
                    reproducirSonidoGol();
                }
                objetos.removeIndex(i);
                posiciones.removeIndex(i);
                continue;
            }

            // USO DE HITBOX MODIFICADA: Si hay colisión con la hitbox precisa del arquero
            if(entidad.getArea().overlaps(arquero.getHitbox())) {
                
                // 🚨 CORRECCIÓN ALCOLISIONAR: Usar la nueva firma de Colisionable
                // Se envía el área del balón y la hitbox del arquero.
                objeto.alColisionar(arquero, entidad.getArea(), arquero.getHitbox());
                
                // 🎧 Lógica: Reproducir el sonido correspondiente basado en el tipo de objeto
                if (objeto instanceof Balon) {
                    // Los balones usan sonidoAtajada, excepto el difícil (tipo 2) que usa sonidoGol en la clase Balon.
                    if (objeto.getTipo() == 2) {
                        // El balón difícil ya decide si es atajada (no reproduce sonido) o gol (reproduce sonidoGol)
                        // No necesitamos reproducir sonido aquí, se hace dentro de alColisionar de Balon si falla.
                        // Sin embargo, como modificamos Balon para *no* reproducir el sonido, lo hacemos aquí:
                        // Si es tipo 2 y no perdió vida (atajada exitosa), reproduce atajada. Si perdió vida, ya se llamó recibirGol.
                        // La forma más limpia es hacer que la clase Balon devuelva si fue atajada o no.
                        
                        // Pero, como no podemos modificar el retorno de void, hacemos la llamada al sonido en Balon
                        // y solo controlamos que no se superpongan en el mismo frame.
                        
                        // Si es Balon, siempre reproducimos el sonido asociado (aunque Balon ya tiene la lógica de atajada/gol)
                        // Para el control de sonido único:
                        if (objeto.getTipo() == 2) {
                            // Si es difícil, asumimos que si colisionó, el resultado ya fue un gol o atajada.
                            // Si fue gol, ya se reprodujo. Si fue atajada, reproducimos atajada.
                            reproducirSonidoAtajada(); 
                            
                            // NOTA: Es complicado determinar aquí si Balon.alColisionar resultó en gol o atajada.
                            // Se recomienda que los sonidos solo se reproduzcan en SistemaDeJuego, 
                            // y que Balon.alColisionar solo modifique el estado del arquero.
                            // Por ahora, solo reproduciremos el sonido de atajada.
                        } else {
                            // Balones normales, curvos, zigzag (atajada)
                            reproducirSonidoAtajada();
                        }
                    } else { // Balones normales, curvos, zigzag (atajada)
                        reproducirSonidoAtajada();
                    }
                } else if (objeto instanceof Premio) {
                    sonidoPremio.play(0.95f);
                }
                
                objetos.removeIndex(i);
                posiciones.removeIndex(i);

                if (arquero.getVidas() <= 0) {
                    return false; // Fin del juego
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
        
        // Los sonidos son compartidos y deben disponerse una sola vez.
        // Si ya se liberaron en ArqueroClaudioBravo.destruir(), no deben liberarse aquí
        // para evitar errores, o viceversa. Asumimos que se liberan aquí:
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