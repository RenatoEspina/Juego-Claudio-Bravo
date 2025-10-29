package juego;

import com.badlogic.gdx.math.Rectangle;

public interface Colisionable {
    // CAMBIO: Ahora recibe el área del balón y la hitbox del arquero para el chequeo de la mitad derecha.
    void alColisionar(ArqueroClaudioBravo arquero, Rectangle ballArea, Rectangle arqueroHitbox);
    int getTipo();
}