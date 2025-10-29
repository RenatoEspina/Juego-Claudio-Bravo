package juego;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.math.Rectangle;

public class BalonCurvo extends Balon {
    private final float RETRASO_INICIAL = 1.0f; // 1 segundo de retraso
    private final float AMPLITUD = 150f; // Desviación lateral máxima (Amplitud del arco) - Aumentada
    private final float POSICION_Y_PICO = 200f; // Altura en Y donde se alcanza el pico de la curva (puedes ajustarlo)
    private float tiempoInicio;
    private float posicionXInicial;
    private float direccionLateral; // -1 (izquierda) o 1 (derecha)
    private final float MARGEN_LATERAL = 100; // Margen de la cancha

    // Tipo 5: Balón Curvo
    public BalonCurvo(Texture textura, Sound sonido, float velocidad) {
        super(textura, sonido, 5, velocidad);
        this.tiempoInicio = TimeUtils.nanoTime();
        
        // Inicializar dirección aleatoriamente
        this.direccionLateral = MathUtils.randomBoolean() ? 1 : -1; 
    }
    
    // Sobrecarga el método crear para guardar la posición X inicial
    @Override
    public void crear(float x, float y, float ancho, float alto) {
        super.crear(x, y, ancho, alto);
        this.posicionXInicial = x;
    }

    @Override
    public void mover(float delta) {
        float tiempoTranscurrido = (TimeUtils.nanoTime() - tiempoInicio) / 1_000_000_000f;
        
        // 1. Fase de espera de 1 segundo
        if (tiempoTranscurrido < RETRASO_INICIAL) {
            return;
        }
        
        // 2. Movimiento Vertical
        area.y -= velocidad * delta;

        // --- Lógica de la Curva (Modelo Parabólico/Arco) ---
        
        // Normaliza la posición Y: 1.0 en la posición inicial (arriba), 0.0 en POSICION_Y_PICO
        float posicionVerticalRelativa = (area.y - POSICION_Y_PICO) / (480f - POSICION_Y_PICO);
        posicionVerticalRelativa = MathUtils.clamp(posicionVerticalRelativa, -1f, 1f); 
        
        // La curva se realiza sobre la distancia recorrida desde arriba hasta la posición del arquero (0).
        // Usamos una función simple que va de 0 a 1 y vuelve a 0, pero solo en la parte de arriba
        // para un arco más natural. Si la pelota está más abajo del pico, no se curva.
        
        float factorCurva = 0;
        if (area.y > POSICION_Y_PICO) {
            // Usa una parábola simple para el arco: f(x) = x * (1 - x)
            // Donde x es (480 - area.y) / (480 - POSICION_Y_PICO)
            float x = (480f - area.y) / (480f - POSICION_Y_PICO); 
            x = MathUtils.clamp(x, 0f, 1f);
            factorCurva = x * (1 - x); // El factor máximo es 0.25 en x=0.5
        } 
        
        float offsetLateral = AMPLITUD * factorCurva * 4 * direccionLateral; // Multiplicamos por 4 para que la amplitud sea AMPLITUD

        // Aplica la curva a la posición X
        area.x = posicionXInicial + offsetLateral;

        // Limites de la cancha (usando el nuevo margen)
        if(area.x < MARGEN_LATERAL) area.x = MARGEN_LATERAL;
        if(area.x > 800 - area.width - MARGEN_LATERAL) area.x = 800 - area.width - MARGEN_LATERAL; 
    }
    
    @Override
    public void alColisionar(ArqueroClaudioBravo arquero, Rectangle ballArea, Rectangle arqueroHitbox) {
        arquero.atajar(); // Es un balón difícil, pero atajable.
        // Sonido eliminado para ser gestionado en SistemaDeJuego.
    }
}