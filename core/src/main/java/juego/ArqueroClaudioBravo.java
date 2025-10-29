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
    // Eliminada: private boolean celebrando = false;
    // Eliminada: private int tiempoCelebracionMax = 30;
    // Eliminada: private int tiempoCelebracion;

    public ArqueroClaudioBravo(Texture textura, Sound atajadaSound, Sound golSound) {
        super(textura, 500); // Velocidad del movimiento
        this.sonidoAtajada = atajadaSound;
        this.sonidoGol = golSound;
    }

    @Override
    public void mover(float delta) {
        // Movimiento desde teclado
        if(Gdx.input.isKeyPressed(Input.Keys.LEFT)) 
            area.x -= velocidad * delta;
        if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)) 
            area.x += velocidad * delta;

        // Limites de la cancha
        if(area.x < 50) area.x = 50; // Límite izquierdo
        if(area.x > 650) area.x = 650; // Límite derecho

        // Eliminada: Lógica de animación de celebración
    }

    @Override
    public void dibujar(SpriteBatch batch) {
        // Simplificado para solo dibujar la textura
        batch.draw(textura, area.x, area.y);
    }

    public void atajar() {
        atajadas++;
        // Eliminada: lógica de iniciar celebración
        sonidoAtajada.play();
    }

    public void recibirGol() {
        golesRecibidos++;
        vidas--;
        sonidoGol.play();
    }

    // Métodos para implementar la lógica de Premios (Encapsulamiento)
    public void agregarVida() {
        vidas++;
    }

    public void sumarPuntos(int puntos) {
        atajadas += puntos;
    }

    // Getters encapsulados
    public int getVidas() { return vidas; }
    public int getAtajadas() { return atajadas; }
    public int getGolesRecibidos() { return golesRecibidos; }
    // Eliminada: public boolean estaCelebrando() { return celebrando; }

    public void destruir() {
        textura.dispose();
        sonidoAtajada.dispose();
        sonidoGol.dispose();
    }
}