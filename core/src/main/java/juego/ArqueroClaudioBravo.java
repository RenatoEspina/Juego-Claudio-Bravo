package juego;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public class ArqueroClaudioBravo extends EntidadMovil {
    private Sound sonidoAtajada;
    private Sound sonidoGol;
    private int vidas = 3;
    private int atajadas = 0;
    private int golesRecibidos = 0;
    
    // Atributos para el Dash
    private final float VELOCIDAD_BASE = 500f; 
    private final float VELOCIDAD_DASH = 1500f; 
    private final float DASH_DURACION = 0.15f; 
    private final float DASH_COOLDOWN = 1.0f; 
    private float tiempoDashRestante = 0;
    private float tiempoCooldownRestante = 0;

    // Constantes para márgenes de la cancha (800x480)
    private final float ANCHO_PANTALLA = 800;
    private final float MARGEN_LATERAL = 100; // CAMBIO: Margen aumentado a 100
    
    // Constantes para la Hitbox (Dimensiones originales: 233x113)
    private final float HITBOX_OFFSET_Y = 57; 
    private final float HITBOX_ALTURA_REDUCIDA = 113 - HITBOX_OFFSET_Y; 
    private final float HITBOX_ANCHO_REDUCIDO = 200; 

    public ArqueroClaudioBravo(Texture textura, Sound atajadaSound, Sound golSound) {
        super(textura, 500f);
        this.sonidoAtajada = atajadaSound;
        this.sonidoGol = golSound;
    }

    @Override
    public void mover(float delta) {
        
        // 1. Actualizar temporizadores de Dash
        if (tiempoDashRestante > 0) {
            tiempoDashRestante -= delta;
            if (tiempoDashRestante <= 0) {
                super.velocidad = VELOCIDAD_BASE;
                tiempoCooldownRestante = DASH_COOLDOWN;
            }
        } else if (tiempoCooldownRestante > 0) {
            tiempoCooldownRestante -= delta;
        }
        
        // 2. Movimiento (A y D)
        float currentSpeed = super.velocidad;
        
        // Mover Izquierda (A)
        if(Gdx.input.isKeyPressed(Input.Keys.A)) 
            area.x -= currentSpeed * delta;
        
        // Mover Derecha (D)
        if(Gdx.input.isKeyPressed(Input.Keys.D)) 
            area.x += currentSpeed * delta;
            
        // 3. Activación de Dash (ESPACIO)
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE) && tiempoCooldownRestante <= 0) {
            super.velocidad = VELOCIDAD_DASH;
            tiempoDashRestante = DASH_DURACION;
        }

        // 4. Limites de la cancha (con margen aumentado)
        if(area.x < MARGEN_LATERAL) area.x = MARGEN_LATERAL;
        
        // Límite derecho: ANCHO_PANTALLA - area.width - MARGEN_LATERAL
        if(area.x > ANCHO_PANTALLA - area.width - MARGEN_LATERAL) 
            area.x = ANCHO_PANTALLA - area.width - MARGEN_LATERAL; 
    }

    @Override
    public void dibujar(SpriteBatch batch) {
        batch.draw(textura, area.x, area.y, area.width, area.height);
    }
    
    public Rectangle getHitbox() {
        Rectangle hitbox = new Rectangle();
        hitbox.width = HITBOX_ANCHO_REDUCIDO;
        hitbox.height = HITBOX_ALTURA_REDUCIDA;

        float offsetX = (area.width - HITBOX_ANCHO_REDUCIDO) / 2;
        hitbox.x = area.x + offsetX; 

        hitbox.y = area.y + HITBOX_OFFSET_Y;

        return hitbox;
    }

    public void atajar() {
        atajadas++;
        // El sonido se gestiona en SistemaDeJuego o Balon para evitar duplicados.
    }
    
    // CAMBIO: Al recibir gol, pierde vida, suma gol y emite sonido.
    public void recibirGol() {
        golesRecibidos++;
        vidas--;
        // El sonido se gestiona en SistemaDeJuego o Balon para evitar duplicados.
    }
    
    // NUEVO MÉTODO / CAMBIO: Para sumar goles cuando el balón se pasa (sin perder vida)
    // También resta 1 punto de atajadas (puntaje) por no atajarlo.
    public void incrementarGoles() {
        golesRecibidos++;
        atajadas = Math.max(0, atajadas - 1); // Resta 1 punto, mínimo 0.
        // El sonido debe manejarse en SistemaDeJuego.
    }

    // Métodos para Premios
    public void agregarVida() {
        vidas++;
    }

    public void sumarPuntos(int puntos) {
        atajadas += puntos;
    }

    // Getters
    public int getVidas() { return vidas; }
    public int getAtajadas() { return atajadas; }
    public int getGolesRecibidos() { return golesRecibidos; }

    public void destruir() {
        textura.dispose();
        sonidoAtajada.dispose();
        sonidoGol.dispose();
    }
}