package ec.edu.espe.imagenConHilos;

import java.awt.image.BufferedImage;

/**
 *
 * @author Miranda Alison, Orrico Camilo, Quispe Bryan
 */

public class ConvertidorGris implements Runnable {
    private final BufferedImage imagen;
    private final int inicioFila;
    private final int finFila;
    
    public ConvertidorGris(BufferedImage imagen, int inicioFila, int finFila) {
        this.imagen = imagen;
        this.inicioFila = inicioFila;
        this.finFila = finFila;
    }
    
    @Override
    public void run() {
        for (int y = inicioFila; y < finFila; y++) {
            for (int x = 0; x < imagen.getWidth(); x++) {
                int pixel = imagen.getRGB(x, y);

                // Obtener componentes RGB del píxel
                int rojo = (pixel >> 16) & 0xff;
                int verde = (pixel >> 8) & 0xff;
                int azul = pixel & 0xff;

                // Calcular valor promedio para escala de grises
                int gris = (rojo + verde + azul) / 3;

                // Reconstruir el píxel en escala de grises
                int nuevoPixel = (gris << 16) | (gris << 8) | gris;

                // Actualizar el píxel en la imagen
                imagen.setRGB(x, y, nuevoPixel);
            }
        }
    }
}

