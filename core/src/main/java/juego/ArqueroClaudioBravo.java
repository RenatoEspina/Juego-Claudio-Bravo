package juego;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class ArqueroClaudioBravo extends EntidadMovil {
    private Sound sonidoAtajada;
    private Sound sonidoGol;
    private int vidas = 3;
    private int atajadas = 0;
    private int golesRecibidos = 0;
    
    // Atributos para el Dash
    private final float VELOCIDAD_BASE = 500f; // CAMBIO: float
    private final float VELOCIDAD_DASH = 1500f; 
    private final float DASH_DURACION = 0.15f; 
    private final float DASH_COOLDOWN = 1.0f; 
    private float tiempoDashRestante = 0;
    private float tiempoCooldownRestante = 0;

    public ArqueroClaudioBravo(Texture textura, Sound atajadaSound, Sound golSound) {
        super(textura, 500f); // CAMBIO: velocidad base a float
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

        // 4. Limites de la cancha
        if(area.x < 50) area.x = 50;
        // 800 (ancho pantalla) - area.width (233) - 50 (margen)
        if(area.x > 800 - area.width - 50) area.x = 800 - area.width - 50; 
    }

    @Override
    public void dibujar(SpriteBatch batch) {
        batch.draw(textura, area.x, area.y, area.width, area.height);
    }

    public void atajar() {
        atajadas++;
    }

    public void recibirGol() {
        golesRecibidos++;
        vidas--;
        sonidoGol.play(0.85f);
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