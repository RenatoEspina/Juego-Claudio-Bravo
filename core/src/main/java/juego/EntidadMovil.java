package juego;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public abstract class EntidadMovil {
    protected Rectangle area;
    protected Texture textura;
    protected float velocidad; // CAMBIO: de int a float

    public EntidadMovil(Texture textura, float velocidad) { // CAMBIO: velocidad es float
        this.textura = textura;
        this.velocidad = velocidad;
        this.area = new Rectangle();
    }

    // Método abstracto que deben implementar las subclases
    public abstract void mover(float delta);

    public void dibujar(SpriteBatch batch) {
        // Se asegura que se dibuje con el tamaño del área (importante para Claudio Bravo)
        batch.draw(textura, area.x, area.y, area.width, area.height); 
    }

    public Rectangle getArea() {
        return area;
    }

    public void crear(float x, float y, float ancho, float alto) {
        area.x = x;
        area.y = y;
        area.width = ancho;
        area.height = alto;
    }

    public void dispose() {
        // Se puede dejar así, asumiendo que el SistemaDeJuego libera las texturas compartidas.
        // Si esta entidad tuviera una textura única, debería tener 'textura.dispose();'
    }
}