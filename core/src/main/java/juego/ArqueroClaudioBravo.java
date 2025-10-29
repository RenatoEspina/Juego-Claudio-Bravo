package juego;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;

public class ArqueroClaudioBravo extends EntidadMovil {
    private Sound sonidoAtajada;
    private Sound sonidoGol;
    private int vidas = 3;
    private int atajadas = 0;
    private int golesRecibidos = 0;
    private boolean celebrando = false;
    private int tiempoCelebracionMax = 30;
    private int tiempoCelebracion;
    
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
        
        // Animación de celebración
        if (celebrando) {
            tiempoCelebracion--;
            if (tiempoCelebracion <= 0) celebrando = false;
        }
    }
    
    @Override
    public void dibujar(SpriteBatch batch) {
        if (!celebrando) {
            batch.draw(textura, area.x, area.y);
        } else {
            // Animación de celebración - pequeño salto
            float offsetY = MathUtils.random(0, 3);
            batch.draw(textura, area.x, area.y + offsetY);
        }
    }
    
    public void atajar() {
        atajadas++;
        celebrando = true;
        tiempoCelebracion = tiempoCelebracionMax;
        sonidoAtajada.play();
    }
    
    public void recibirGol() {
        golesRecibidos++;
        vidas--;
        sonidoGol.play();
    }
    
    // Getters encapsulados
    public int getVidas() { return vidas; }
    public int getAtajadas() { return atajadas; }
    public int getGolesRecibidos() { return golesRecibidos; }
    public boolean estaCelebrando() { return celebrando; }
    
    public void destruir() {
        textura.dispose();
        sonidoAtajada.dispose();
        sonidoGol.dispose();
    }
}