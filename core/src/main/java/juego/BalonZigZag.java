package juego;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.math.Rectangle;

public class BalonZigZag extends Balon {
    private final float RETRASO_INICIAL = 1.0f; // 1 segundo de retraso
    private final float CAMBIO_DIRECCION_TIEMPO = 0.5f; // Cambia de dirección cada 0.5 segundos
    private final float VELOCIDAD_LATERAL = 300f; 
    private float tiempoInicio;
    private float direccionLateral = 1; // 1 = derecha, -1 = izquierda
    private float tiempoDesdeUltimoCambio;
    private final float MARGEN_LATERAL = 100; // Margen de la cancha

    // Tipo 6: Balón ZigZag
    public BalonZigZag(Texture textura, Sound sonido, float velocidad) {
        super(textura, sonido, 6, velocidad);
        this.tiempoInicio = TimeUtils.nanoTime();
        this.tiempoDesdeUltimoCambio = 0;
    }

    @Override
    public void mover(float delta) {
        float tiempoTranscurrido = (TimeUtils.nanoTime() - tiempoInicio) / 1_000_000_000f;

        // 1. Fase de espera de 1 segundo
        if (tiempoTranscurrido < RETRASO_INICIAL) {
            return;
        }
        
        // Actualiza el tiempo desde el último cambio de dirección
        tiempoDesdeUltimoCambio += delta;

        // 2. Movimiento Vertical
        area.y -= velocidad * delta;
        
        // 3. Lógica de cambio de dirección
        if (tiempoDesdeUltimoCambio >= CAMBIO_DIRECCION_TIEMPO) {
            direccionLateral *= -1; // Invierte la dirección
            tiempoDesdeUltimoCambio = 0;
        }

        // 4. Movimiento Lateral (ZigZag)
        area.x += VELOCIDAD_LATERAL * direccionLateral * delta;

        // Limites de la cancha (Invierte dirección si toca el borde)
        if(area.x < MARGEN_LATERAL) { // CAMBIO: Usar nuevo margen
            area.x = MARGEN_LATERAL;
            direccionLateral = 1;
            tiempoDesdeUltimoCambio = 0;
        }
        if(area.x > 800 - area.width - MARGEN_LATERAL) { // CAMBIO: Usar nuevo margen
            area.x = 800 - area.width - MARGEN_LATERAL;
            direccionLateral = -1;
            tiempoDesdeUltimoCambio = 0;
        }
    }
    
    // Al colisionar, es un golpe "limpio" (tipo 1 o 2), lo definiremos como Tipo 1 (atajada)
    @Override
    public void alColisionar(ArqueroClaudioBravo arquero, Rectangle ballArea, Rectangle arqueroHitbox) {
        arquero.atajar(); // Es un balón difícil, pero atajable.
        // Sonido eliminado para ser gestionado en SistemaDeJuego.
    }
}