package juego;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.audio.Sound;

public class Premio extends EntidadMovil implements Colisionable {
    private Sound sonido;
    private int tipo; // 3: Premio vida extra, 4: Premio puntos extra

    // Constructor con velocidad como float
    public Premio(Texture textura, Sound sonido, int tipo, float velocidad) {
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
        sonido.play(0.95f); 
        if (tipo == 3) {
            arquero.agregarVida();
        } else if (tipo == 4) {
            arquero.sumarPuntos(10);
        }
    }

    @Override
    public int getTipo() {
        return tipo;
    }
}