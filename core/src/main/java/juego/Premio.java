package juego;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.audio.Sound;

public class Premio extends EntidadMovil implements Colisionable {
    private Sound sonido;
    private int tipo; // 3: Premio vida extra, 4: Premio puntos extra

    public Premio(Texture textura, Sound sonido, int tipo) {
        super(textura, 150);
        this.sonido = sonido;
        this.tipo = tipo;
    }

    @Override
    public void mover(float delta) {
        area.y -= velocidad * delta;
    }

    @Override
    public void alColisionar(ArqueroClaudioBravo arquero) {
        sonido.play();
        if (tipo == 3) {
            // Vida extra: Aumenta una vida (usando el método encapsulado)
            arquero.agregarVida();
        } else if (tipo == 4) {
            // Puntos extra: Suma 10 atajadas (usando el método encapsulado)
            arquero.sumarPuntos(10);
        }
    }

    @Override
    public int getTipo() {
        return tipo;
    }
}