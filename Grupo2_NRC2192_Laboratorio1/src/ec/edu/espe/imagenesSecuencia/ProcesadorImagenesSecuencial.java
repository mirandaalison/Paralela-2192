/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ec.edu.espe.imagenesSecuencia;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
/**
 *
 * @author Miranda Alison, Orrico Camilo, Quispe Bryan
 */
public class ProcesadorImagenesSecuencial {
    public static void main(String[] args) {
        // Ruta de la carpeta de imágenes
        String rutaCarpetaEntrada = "C:\\Users\\camilo\\4to5toSemestre2024\\Paralela\\Paralela-2192\\Grupo2_NRC2192_Laboratorio1\\imagenes";
        String rutaCarpetaSalida = "C:\\Users\\camilo\\4to5toSemestre2024\\Paralela\\Paralela-2192\\Grupo2_NRC2192_Laboratorio1\\imagenes_grises_secuencial";

        // Crear las carpetas de entrada y salida
        File carpetaEntrada = new File(rutaCarpetaEntrada);
        File carpetaSalida = new File(rutaCarpetaSalida);

        if (!carpetaSalida.exists()) {
            carpetaSalida.mkdirs();
        }

        // Comprobar si la carpeta de entrada existe
        if (!carpetaEntrada.exists() || !carpetaEntrada.isDirectory()) {
            System.out.println("La carpeta de entrada no existe o no es un directorio: " + carpetaEntrada.getAbsolutePath());
            return;
        }

        // Obtener la lista de archivos en la carpeta de entrada
        File[] archivos = carpetaEntrada.listFiles((dir, name) -> name.toLowerCase().endsWith(".jpg"));

        if (archivos == null || archivos.length == 0) {
            System.out.println("No se encontraron imágenes .jpg en la carpeta de entrada.");
            return;
        }

        // Procesar cada archivo
        for (File archivoEntrada : archivos) {
            try {
                System.out.println("Procesando archivo: " + archivoEntrada.getAbsolutePath());

                BufferedImage imagen = ImageIO.read(archivoEntrada);
                if (imagen == null) {
                    System.out.println("No se pudo cargar la imagen: " + archivoEntrada.getName());
                    continue;
                }

                // Convertir la imagen a escala de grises
                BufferedImage imagenGris = convertirEscalaDeGrises(imagen);

                // Guardar la imagen en la carpeta de salida
                File archivoSalida = new File(carpetaSalida, archivoEntrada.getName());
                ImageIO.write(imagenGris, "jpg", archivoSalida);

                System.out.println("Imagen convertida y guardada en: " + archivoSalida.getAbsolutePath());
            } catch (IOException e) {
                System.out.println("Error procesando el archivo: " + archivoEntrada.getName());
                e.printStackTrace();
            }
        }

        System.out.println("Procesamiento completado.");
    }

    /**
     * Convierte una imagen a escala de grises.
     *
     * @param imagen La imagen original
     * @return La imagen convertida a escala de grises
     */
    private static BufferedImage convertirEscalaDeGrises(BufferedImage imagen) {
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
