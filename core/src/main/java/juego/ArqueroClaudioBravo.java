package juego;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public class ArqueroClaudioBravo extends EntidadMovil {
    
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
    private final float MARGEN_LATERAL = 100;
    
    // --- CONSTANTES DE HITBOX AJUSTADAS PARA CLARIDAD ---
    private final float HITBOX_ANCHO = 200; 
    private final float HITBOX_OFFSET_X = 16.5f; // (233 - 200) / 2 = 16.5
    private final float HITBOX_ALTURA = 56f;     // 113 - 57 = 56
    private final float HITBOX_OFFSET_Y = 57f; 
    // NOTA: Se asume que el ancho de la textura es 233 y el alto es 113.

    // Constructor sin sonidos
    public ArqueroClaudioBravo(Texture textura) {
        super(textura, 500f);
    }
    
    // Sobrecarga del constructor (para compatibilidad)
    public ArqueroClaudioBravo(Texture textura, Sound atajadaSound, Sound golSound) {
        this(textura);
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
        
        if(Gdx.input.isKeyPressed(Input.Keys.A)) 
            area.x -= currentSpeed * delta;
        
        if(Gdx.input.isKeyPressed(Input.Keys.D)) 
            area.x += currentSpeed * delta;
            
        // 3. Activación de Dash (ESPACIO)
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE) && tiempoCooldownRestante <= 0) {
            super.velocidad = VELOCIDAD_DASH;
            tiempoDashRestante = DASH_DURACION;
        }

        // 4. Limites de la cancha
        if(area.x < MARGEN_LATERAL) area.x = MARGEN_LATERAL;
        
        if(area.x > ANCHO_PANTALLA - area.width - MARGEN_LATERAL) 
            area.x = ANCHO_PANTALLA - area.width - MARGEN_LATERAL; 
    }

    @Override
    public void dibujar(SpriteBatch batch) {
        batch.draw(textura, area.x, area.y, area.width, area.height);
    }
    
    // --- Hitbox con las nuevas constantes ---
    public Rectangle getHitbox() {
        Rectangle hitbox = new Rectangle();
        hitbox.width = HITBOX_ANCHO;
        hitbox.height = HITBOX_ALTURA;

        // Calcula el offset X basado en el ancho actual del área.
        // Si el ancho del área es dinámico, este cálculo es más robusto.
        float offsetX = (area.width - HITBOX_ANCHO) / 2;
        hitbox.x = area.x + offsetX; 

        hitbox.y = area.y + HITBOX_OFFSET_Y;

        return hitbox;
    }

    public void atajar() {
        atajadas++;
    }
    
    // Este método ya no es usado para goles, pero se mantiene si es llamado por BalonDificil
    public void penalizarVida() {
        vidas--;
    }
    
    public void registrarGol() {
        golesRecibidos++;
        vidas--; 
    }
    
    // Métodos para Premios
    public void agregarVida() {
        vidas++;
    }

    public void sumarPuntos(int puntos) {
        // Asumiendo que sumar puntos se suma al contador de 'atajadas', que funciona como score.
        atajadas += puntos;
    }

    // Getters
    public int getVidas() { return vidas; }
    public int getAtajadas() { return atajadas; }
    public int getGolesRecibidos() { return golesRecibidos; }

    public void destruir() {
        textura.dispose();
    }
}