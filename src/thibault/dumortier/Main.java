package thibault.dumortier;

import java.io.File;
import java.nio.file.Path;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner sc=new Scanner(System.in);
        System.out.println("Please enter path to directory to compile");
        Path dirPath = Path.of(sc.nextLine());
        if(!dirPath.toFile().exists())
            throw new IllegalArgumentException("Directory doesn't exist or wrong path.");
        sc.close();

        createOrClearCompImgDir();

        Compiler comp = Compiler.getInstance();
        comp.compile(dirPath.toFile());
    }

    //Creates directory in which compiled images will be saved
    private static void createOrClearCompImgDir(){
        File compiledImagesDir = new File("./compiledImages");
        if(!compiledImagesDir.exists())
            compiledImagesDir.mkdirs();
        if(compiledImagesDir.exists()){
            for (File file: compiledImagesDir.listFiles()) {
                file.delete();
            }
        }
    }
}
