package juego;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.Rectangle;

public class Balon extends EntidadMovil implements Colisionable {
    private Sound sonido;
    private int tipo; // 1: normal, 2: difícil, 5: curvo, 6: zigzag
    
    // Constructor con velocidad como float
    public Balon(Texture textura, Sound sonido, int tipo, float velocidad) {
        super(textura, velocidad);
        this.sonido = sonido;
        this.tipo = tipo;
    }
    
    public Sound getSound() {
    	return this.sonido;
    }
    
    @Override
    public void mover(float delta) {
        area.y -= velocidad * delta;
    }
    
    @Override
    public void alColisionar(ArqueroClaudioBravo arquero, Rectangle ballArea, Rectangle arqueroHitbox) {
        
        if (tipo == 1 || tipo == 5 || tipo == 6) {
            // Balones Normales, Curvos, y ZigZag: Atajada estándar
            arquero.atajar();
            
        } else if (tipo == 2) {
             // ** CAMBIO: Lógica Balón Difícil (Tipo 2): SOLO ATACA CON LA PARTE SUPERIOR DERECHA **
            
            // 1. Define la mitad derecha de la hitbox
            float rightHalfStartX = arqueroHitbox.x + arqueroHitbox.width / 2;
            
            // 2. Define la mitad superior de la hitbox
            float topHalfStartY = arqueroHitbox.y + arqueroHitbox.height / 2;
            
            // 3. Crea la Rectangle que representa solo la mitad superior derecha
            Rectangle topRightQuarter = new Rectangle(
                rightHalfStartX,
                topHalfStartY, // Inicia en la mitad de Y hacia arriba
                arqueroHitbox.width / 2,
                arqueroHitbox.height / 2 // La altura es la mitad
            );

            // 4. Si el área del balón se superpone con la mitad superior derecha
            if (ballArea.overlaps(topRightQuarter)) {
                arquero.atajar(); // Atajada exitosa
            } else {
                arquero.recibirGol(); // Atajada fallida (pierde vida)
            }
        }
        
        // El sonido se elimina de aquí para ser gestionado en SistemaDeJuego
        // y evitar reproducción doble.
        // sonido.play(0.6f); 
    }
    
    @Override
    public int getTipo() {
        return tipo;
    }
}