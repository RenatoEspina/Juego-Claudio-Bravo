package juego;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;

public class Balon extends EntidadMovil implements Colisionable {
    // Se elimina: private Sound sonido; ya que la gestión sonora es externa.
    
    // 1: normal, 2: difícil, 5: curvo (eliminado), 6: zigzag
    private int tipo; 
    
    // Constructor con velocidad como float
    public Balon(Texture textura, int tipo, float velocidad) {
        super(textura, velocidad);
        this.tipo = tipo;
    }
    
    // Se elimina getSound() ya que la gestión sonora es externa.
    
    @Override
    public void mover(float delta) {
        area.y -= velocidad * delta;
    }
    
    @Override
    public void alColisionar(ArqueroClaudioBravo arquero, Rectangle ballArea, Rectangle arqueroHitbox) {
        
        // Balones Normales y Balones con Trayectoria Especial (Tipos 1 y 6)
        if (tipo == 1 || tipo == 6) {
            arquero.atajar(); // Siempre es una atajada estándar
            
        } else if (tipo == 2) {
             // Lógica Balón Difícil (Tipo 2): Requiere precisión para ser atajado
            
            // 1. Definir el Cuarto Superior Derecho (Zona de Acierto de Precisión)
            float rightHalfStartX = arqueroHitbox.x + arqueroHitbox.width / 2;
            float topHalfStartY = arqueroHitbox.y + arqueroHitbox.height / 2;
            
            Rectangle topRightQuarter = new Rectangle(
                rightHalfStartX,
                topHalfStartY, 
                arqueroHitbox.width / 2,
                arqueroHitbox.height / 2
            );

            // 2. Comprobación de colisión

            // REGLA: Toca la parte superior derecha (Acierto)
            if (ballArea.overlaps(topRightQuarter)) {
                arquero.atajar(); // Atajada exitosa
            } 
            // REGLA: Si colisionó (sabemos que sí) y NO tocó el cuarto superior derecho, falla.
            else {
                 // Falla de precisión. Penaliza vida.
                 arquero.penalizarVida();
            }
        }
        
        // Se mantiene la flexibilidad para futuros tipos de balón.
    }
    
    @Override
    public int getTipo() {
        return tipo;
    }
}