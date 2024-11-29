package imagen.concurrenteconHilos;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import javax.imageio.ImageIO;

/**
 * Procesador de imágenes a escala de grises con multithreading.
 */
public class ConcurrenciaHilos {

    public static void main(String[] args) {
        String rutaCarpetaEntrada = "C:\\Users\\rquis_9zzy7zj\\OneDrive - UNIVERSIDAD DE LAS FUERZAS ARMADAS ESPE\\Escritorio\\Proyecto Paralela\\Grupo2_NRC2192_Laboratorio1\\imagenes";
        String rutaCarpetaSalida = "C:\\Users\\rquis_9zzy7zj\\OneDrive - UNIVERSIDAD DE LAS FUERZAS ARMADAS ESPE\\Escritorio\\Proyecto Paralela\\Grupo2_NRC2192_Laboratorio1\\Imagenes_grises_porhilo";

        File carpetaEntrada = new File(rutaCarpetaEntrada);
        File carpetaSalida = new File(rutaCarpetaSalida);

        if (!carpetaSalida.exists()) {
            carpetaSalida.mkdirs();
        }

        if (!carpetaEntrada.exists() || !carpetaEntrada.isDirectory()) {
            System.out.println("La carpeta de entrada no existe o no es un directorio: " + carpetaEntrada.getAbsolutePath());
            return;
        }

        File[] archivos = carpetaEntrada.listFiles((dir, name) -> name.toLowerCase().endsWith(".jpg"));

        if (archivos == null || archivos.length == 0) {
            System.out.println("No se encontraron imágenes .jpg en la carpeta de entrada.");
            return;
        }

        int numeroHilos = Runtime.getRuntime().availableProcessors();
        ExecutorService executor = Executors.newFixedThreadPool(numeroHilos);

        // Dividir las imágenes entre los hilos
        int tamanioBloque = (int) Math.ceil((double) archivos.length / numeroHilos);
        List<Runnable> tareas = new ArrayList<>();

        for (int i = 0; i < archivos.length; i += tamanioBloque) {
            int fin = Math.min(i + tamanioBloque, archivos.length);
            File[] bloqueArchivos = new File[fin - i];
            System.arraycopy(archivos, i, bloqueArchivos, 0, fin - i);

            // Debug: Imprimir archivos asignados al bloque
            System.out.println("Bloque asignado a un hilo:");
            for (File archivo : bloqueArchivos) {
                System.out.println(" - " + archivo.getName());
            }

            tareas.add(new ProcesadorBloque(bloqueArchivos, carpetaSalida));
        }

        // Ejecutar todas las tareas
        for (Runnable tarea : tareas) {
            executor.execute(tarea);
        }

        // Esperar a que todos los hilos terminen
        executor.shutdown();
        try {
            if (!executor.awaitTermination(1, TimeUnit.MINUTES)) {
                System.out.println("Tiempo de espera agotado. Finalizando forzadamente...");
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            System.out.println("Interrupción durante la espera de finalización.");
            executor.shutdownNow();
        }

        System.out.println("Procesamiento completado.");
    }

    /**
     * Clase que procesa un bloque de imágenes.
     */
    static class ProcesadorBloque implements Runnable {
        private final File[] archivos;
        private final File carpetaSalida;

        public ProcesadorBloque(File[] archivos, File carpetaSalida) {
            this.archivos = archivos;
            this.carpetaSalida = carpetaSalida;
        }

        @Override
        public void run() {
            try {
                for (File archivoEntrada : archivos) {
                    System.out.println(Thread.currentThread().getName() + " procesando: " + archivoEntrada.getAbsolutePath());

                    BufferedImage imagen = ImageIO.read(archivoEntrada);
                    if (imagen == null) {
                        System.out.println("No se pudo cargar la imagen: " + archivoEntrada.getName());
                        continue;
                    }

                    BufferedImage imagenGris = convertirEscalaDeGrises(imagen);

                    File archivoSalida = new File(carpetaSalida, archivoEntrada.getName());
                    ImageIO.write(imagenGris, "jpg", archivoSalida);

                    System.out.println("Imagen convertida y guardada en: " + archivoSalida.getAbsolutePath());
                }
            } catch (Exception e) {
                System.out.println("Error en el procesamiento del bloque: " + e.getMessage());
                e.printStackTrace();
            }
        }

        private BufferedImage convertirEscalaDeGrises(BufferedImage imagen) {
            int ancho = imagen.getWidth();
            int alto = imagen.getHeight();

            BufferedImage imagenGris = new BufferedImage(ancho, alto, BufferedImage.TYPE_INT_RGB);

            for (int y = 0; y < alto; y++) {
                for (int x = 0; x < ancho; x++) {
                    int pixel = imagen.getRGB(x, y);

                    int alpha = (pixel >> 24) & 0xff;
                    int red = (pixel >> 16) & 0xff;
                    int green = (pixel >> 8) & 0xff;
                    int blue = pixel & 0xff;

                    int gris = (red + green + blue) / 3;
                    int nuevoPixel = (alpha << 24) | (gris << 16) | (gris << 8) | gris;

                    imagenGris.setRGB(x, y, nuevoPixel);
                }
            }

            return imagenGris;
        }
    }
}
