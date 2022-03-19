package thibault.dumortier;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.MissingResourceException;

public class Compiler {
    static Compiler instance;

    private Compiler(){
    }

    //Get an instance of compiler
    public static Compiler getInstance() {
        if(instance == null)
            instance = new Compiler();
        return instance;
    }

    //Compile a directory using compiler
    public void compile(File parentDirectory){
        //Check if passed file is actually a directory
        if(!parentDirectory.isDirectory())
            throw new IllegalArgumentException("Parameter needs to be a directory");

        //Tell user that the program is compiling images inside of passed directory
        System.out.println("Now compiling : "+parentDirectory.getName());

        //If the directory actually contains images
        if (containsImages(parentDirectory)) {
            //Variables
            int[] imgDimensions = getImageDimensions(parentDirectory);
            BufferedImage imageBuffer = new BufferedImage(imgDimensions[0], imgDimensions[1], BufferedImage.TYPE_INT_ARGB);

            //Compile images from directory
            compileDir(parentDirectory, imageBuffer);
            //Write image
            imageWriter(imageBuffer, parentDirectory.getName());
        }else{
            //Open directory
            for (File file : parentDirectory.listFiles()) {
                //Check for other directories and compile them
                if (file.isDirectory())
                    compile(file);
            }
        }
    }

    //Gets dimensions of directory compiled image
    private int[] getImageDimensions(File directory){
        //Check if passed file is actually a directory
        if(!directory.isDirectory())
            throw new IllegalArgumentException("Parameter needs to be a directory");

        //Variables
        int[] dim = new int[2]; dim[0] = 0; dim[1] = 0;

        //Open directory
        for (File file: directory.listFiles()) {
            //If file is an image check height and add width
            if(file.getName().matches("([^\\s]+(\\.(?i)(jpe?g|png|bmp))$)")){
                BufferedImage image = null;
                try {
                    image = ImageIO.read(file);
                    if(image.getHeight()>dim[1])
                        dim[1]=image.getHeight();
                    dim[0] += image.getWidth();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return dim;
    }

    //Checks if directory contains images
    private boolean containsImages(File directory){
        //Check if passed file is actually a directory
        if(!directory.isDirectory())
            throw new IllegalArgumentException("Parameter needs to be a directory");

        //Open directory
        for (File file: directory.listFiles()) {
            //If file is an image return true
            if(file.getName().matches("([^\\s]+(\\.(?i)(jpe?g|png|bmp))$)"))
                return true;
        }
        return false;
    }

    //Writes image to "compiledImages"
    private void imageWriter(BufferedImage imageBuffer, String name){
        if (Path.of("./compiledImages").toFile().exists())
            name = ("./compiledImages/" + name + ".png");
        try {
            ImageIO.write(
                    imageBuffer,
                    "png",
                    new File(name));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Compiles images from Directory
    private void compileDir(File parentDirectory, BufferedImage imageBuffer){
        int x=0,y=0;

        for (File file : parentDirectory.listFiles()) {
            if (file.getName().matches("([^\\s]+(\\.(?i)(jpe?g|png|bmp))$)")) {
                try {
                    BufferedImage image = ImageIO.read(file);
                    for (int i = 0; i < image.getWidth(); i++) {
                        for (int j = 0; j < image.getHeight(); j++) {
                            //Getting uncompiled image RGB at pixel(i,j)
                            int RGB = image.getRGB(i, j);
                            //Setting compiled image RGB at pixel(x,y)
                            imageBuffer.setRGB(x, y, RGB);
                            y++;
                        }
                        y -= image.getHeight();
                        x++;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            //Check for other directories and compile them
            if (file.isDirectory())
                compile(file);
        }
    }
}