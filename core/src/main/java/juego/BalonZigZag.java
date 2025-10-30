package juego;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.math.Rectangle;

public class BalonZigZag extends Balon {
    // Reducido a 0.6s
    private final float MIN_TIEMPO_ZIG = 0.2f;
    private final float MAX_TIEMPO_ZAG = 0.6f;
    // Aumentado a 500f
    private final float MIN_VELOCIDAD_LATERAL = 200f;
    private final float MAX_VELOCIDAD_LATERAL = 500f;
    
    private float tiempoInicio;
    private float direccionLateral;
    private float tiempoDesdeUltimoCambio;
    private float tiempoProximoCambio;
    private float velocidadLateralActual;
    private final float MARGEN_LATERAL = 100;
    private final float ANCHO_PANTALLA = 800; // Constante para claridad

    public BalonZigZag(Texture textura, float velocidad) {
        super(textura, 6, velocidad);
        this.tiempoInicio = TimeUtils.nanoTime();
        this.tiempoDesdeUltimoCambio = 0;
        this.direccionLateral = MathUtils.randomBoolean() ? 1 : -1;
        reiniciarTemporizador();
    }

    private void reiniciarTemporizador() {
        // Aseguramos que la velocidad es al menos 200 para que el movimiento sea visible
        this.tiempoProximoCambio = MathUtils.random(MIN_TIEMPO_ZIG, MAX_TIEMPO_ZAG);
        this.velocidadLateralActual = MathUtils.random(MIN_VELOCIDAD_LATERAL, MAX_VELOCIDAD_LATERAL);
    }

    @Override
    public void mover(float delta) {
        // Movimiento Vertical
        area.y -= velocidad * delta;
        
        // Actualizar temporizador de zigzag
        tiempoDesdeUltimoCambio += delta;
        
        // Cambiar dirección si es tiempo
        if (tiempoDesdeUltimoCambio >= tiempoProximoCambio) {
            direccionLateral *= -1; // Invierte la dirección
            cambiarDireccion();
        }

        // Movimiento Lateral
        area.x += velocidadLateralActual * direccionLateral * delta;

        // Detección de colisión con bordes (Lógica mejorada)
        boolean tocaBordeIzquierdo = area.x < MARGEN_LATERAL;
        boolean tocaBordeDerecho = area.x > ANCHO_PANTALLA - area.width - MARGEN_LATERAL;

        if (tocaBordeIzquierdo || tocaBordeDerecho) {
            // Corregir posición y forzar cambio de dirección
            if (tocaBordeIzquierdo) {
                area.x = MARGEN_LATERAL;
                // Forzamos el rebote solo si el balón iba hacia afuera
                if (direccionLateral < 0) {
                     direccionLateral = 1; 
                     cambiarDireccion();
                }
            } else { // tocaBordeDerecho
                area.x = ANCHO_PANTALLA - area.width - MARGEN_LATERAL;
                // Forzamos el rebote solo si el balón iba hacia afuera
                if (direccionLateral > 0) {
                     direccionLateral = -1;
                     cambiarDireccion();
                }
            }
        }
    }
    
    // Método auxiliar para reiniciar los parámetros aleatorios
    private void cambiarDireccion() {
        tiempoDesdeUltimoCambio = 0;
        reiniciarTemporizador();
    }
    
    @Override
    public void alColisionar(ArqueroClaudioBravo arquero, Rectangle ballArea, Rectangle arqueroHitbox) {
        arquero.atajar();
    }
}