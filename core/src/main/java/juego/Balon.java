package juego;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.audio.Sound;

public class Balon extends EntidadMovil implements Colisionable {
    private Sound sonido;
    private int tipo; // 1: Balón normal, 2: Balón difícil
    
    // Constructor con velocidad como float
    public Balon(Texture textura, Sound sonido, int tipo, float velocidad) {
        super(textura, velocidad);
        this.sonido = sonido;
        this.tipo = tipo;
    }
    
    @Override
    public void mover(float delta) {
        area.y -= velocidad * delta;
    }
    
    @Override
    public void alColisionar(ArqueroClaudioBravo arquero) {
        if (tipo == 1) {
            arquero.atajar();
        } else {
            arquero.recibirGol();
        }
        sonido.play(0.6f);
    }
    
    @Override
    public int getTipo() {
        return tipo;
    }
}