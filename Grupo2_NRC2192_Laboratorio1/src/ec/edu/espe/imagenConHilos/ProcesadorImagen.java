package ec.edu.espe.imagenConHilos;

import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

/**
 *
 * @author Miranda Alison, Orrico Camilo, Quispe Bryan
 */

public class ProcesadorImagen {

    public static void main(String[] args) {
        try {
            // Cargar la imagen
            File archivo = new File("C:\\Users\\User\\Desktop\\4to\\Paralela\\Parcial1\\Paralela-2192\\Grupo2_NRC2192_Laboratorio1\\imagenes\\mujercasada.jpg");
            BufferedImage imagen = ImageIO.read(archivo);

            int altura = imagen.getHeight();
            int ancho = imagen.getWidth();

            System.out.println("Procesando imagen de " + ancho + "x" + altura);

            // Crear y asignar hilos
            int numeroHilos = 4; // Dividir en 4 partes
            Thread[] hilos = new Thread[numeroHilos];

            int filasPorHilo = altura / numeroHilos;
            int finFila;
            
            long inicio = System.nanoTime(); // Registrar tiempo inicial
            
            for (int i = 0; i < numeroHilos; i++) {
                int inicioFila = i * filasPorHilo;
                
                if (i == numeroHilos - 1) {
                    finFila = altura; // Último hilo procesa las filas restantes
                } else {
                    finFila = inicioFila + filasPorHilo;
                }

                hilos[i] = new Thread(new ConvertidorGris(imagen, inicioFila, finFila));
                hilos[i].start();
            }

            // Esperar a que todos los hilos terminen
            for (Thread hilo : hilos) {
                hilo.join();
            }

            // Guardar la nueva imagen
            File archivoSalida = new File("imagen_gris_conc.png");
            ImageIO.write(imagen, "png", archivoSalida);
            
            long fin = System.nanoTime(); // Registrar tiempo final

            System.out.println("Imagen procesada y guardada como 'imagen_gris_conc.png'");
            System.out.println("Tiempo de ejecución: " + (fin - inicio) / 1_000_000 + " ms");
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

