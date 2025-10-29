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
        // Los premios otorgan beneficios
        if (tipo == 3) {
            // Vida extra - implementar si quieres agregar este feature
            sonido.play();
        } else if (tipo == 4) {
            // Puntos extra
            sonido.play();
        }
    }
    
    @Override
    public int getTipo() {
        return tipo;
    }
}