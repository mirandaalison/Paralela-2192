package Imagen.ConcurrenteConHilos;

import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class ConcurrenciaHilos {

    public static void main(String[] args) {
        try {
            // Directorio de entrada
            File carpetaEntrada = new File("C:\\ruta\\a\\tu\\carpeta\\imagenes");

            // Directorio de salida
            File carpetaSalida = new File("C:\\ruta\\a\\tu\\carpeta\\Imagenes_grises_porhilo");

            if (!carpetaEntrada.exists() || !carpetaEntrada.isDirectory()) {
                System.out.println("La carpeta de entrada especificada no existe o no es un directorio.");
                return;
            }

            // Crear la carpeta de salida si no existe
            if (!carpetaSalida.exists()) {
                if (carpetaSalida.mkdirs()) {
                    System.out.println("Carpeta de salida creada en: " + carpetaSalida.getAbsolutePath());
                } else {
                    System.out.println("No se pudo crear la carpeta de salida.");
                    return;
                }
            }

            File[] archivos = carpetaEntrada.listFiles((dir, name) -> 
                name.toLowerCase().endsWith(".jpg") || name.toLowerCase().endsWith(".png"));

            if (archivos == null || archivos.length == 0) {
                System.out.println("No se encontraron imágenes en la carpeta de entrada.");
                return;
            }

            System.out.println("Procesando " + archivos.length + " imágenes...");

            // Crear un hilo por cada imagen
            Thread[] hilos = new Thread[archivos.length];

            for (int i = 0; i < archivos.length; i++) {
                File archivo = archivos[i];

                hilos[i] = new Thread(() -> {
                    try {
                        System.out.println("Procesando imagen: " + archivo.getName());

                        BufferedImage imagen = ImageIO.read(archivo);

                        if (imagen == null) {
                            System.out.println("No se pudo leer la imagen: " + archivo.getName());
                            return;
                        }

                        int altura = imagen.getHeight();
                        int ancho = imagen.getWidth();

                        System.out.println("Dimensiones: " + ancho + "x" + altura);

                        // Convertir la imagen a escala de grises
                        for (int y = 0; y < altura; y++) {
                            for (int x = 0; x < ancho; x++) {
                                int rgb = imagen.getRGB(x, y);

                                int rojo = (rgb >> 16) & 0xFF;
                                int verde = (rgb >> 8) & 0xFF;
                                int azul = rgb & 0xFF;

                                int gris = (rojo + verde + azul) / 3;

                                int nuevoRGB = (gris << 16) | (gris << 8) | gris;
                                imagen.setRGB(x, y, nuevoRGB);
                            }
                        }

                        // Guardar la nueva imagen en la carpeta de salida
                        File archivoSalida = new File(carpetaSalida, "gris_" + archivo.getName());
                        ImageIO.write(imagen, "png", archivoSalida);

                        System.out.println("Imagen guardada como: " + archivoSalida.getName());
                    } catch (Exception e) {
                        System.out.println("Error procesando la imagen: " + archivo.getName());
                        e.printStackTrace();
                    }
                });

                hilos[i].start(); // Iniciar hilo
            }

            // Esperar a que todos los hilos terminen
            for (Thread hilo : hilos) {
                hilo.join();
            }

            System.out.println("Procesamiento completado para todas las imágenes.");
        } catch (Exception e) {
            System.out.println("Ocurrió un error durante la ejecución:");
            e.printStackTrace();
        }
    }
}
