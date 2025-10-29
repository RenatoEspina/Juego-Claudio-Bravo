package juego;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.audio.Sound;

public class Balon extends EntidadMovil implements Colisionable {
    private Sound sonido;
    private int tipo; // 1: Balón normal, 2: Balón difícil
    
    public Balon(Texture textura, Sound sonido, int tipo) {
        super(textura, 200);
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
            arquero.atajar(); // Ataja balón normal
        } else {
            arquero.recibirGol(); // Gol con balón difícil
        }
        sonido.play();
    }
    
    @Override
    public int getTipo() {
        return tipo;
    }
}