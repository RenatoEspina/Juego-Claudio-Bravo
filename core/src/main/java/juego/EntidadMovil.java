package juego;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public abstract class EntidadMovil {
    protected Rectangle area;
    protected Texture textura;
    protected int velocidad;
    
    public EntidadMovil(Texture textura, int velocidad) {
        this.textura = textura;
        this.velocidad = velocidad;
        this.area = new Rectangle();
    }
    
    // Método abstracto que deben implementar las subclases
    public abstract void mover(float delta);
    
    public void dibujar(SpriteBatch batch) {
        batch.draw(textura, area.x, area.y);
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

        textura.dispose();

    }
}