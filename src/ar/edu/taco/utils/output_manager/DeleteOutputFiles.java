package ar.edu.taco.utils.output_manager;

import java.io.File;
import java.io.IOException;

public class DeleteOutputFiles {
    /*
     * --Depth-First-Search--
     *  use array
     *  check if there is a folder
     *  if folder, recursive call on folder
     *  if no folders, check for files
     *  if files, delete files, then delete folder
     */

    public static void run() throws IOException {
        String parentDirectory = "/root/testing_TACO/TACO/output_threads";
        File parentFolder = new File(parentDirectory);
        File[] files = parentFolder.listFiles();

        for(File f: files){
            deleteFiles(f);
        }
        if(parentFolder.delete()) System.out.println("Deleted " + parentFolder.getName() + " successfully!");
        else System.out.println("Failed to delete " + parentFolder.getName() + "!");

        System.out.println();
    }


    public static void deleteFiles(File directory){
        File[] files = directory.listFiles();
        boolean isEmpty = false;

        for(File f: files){
            if(f.isDirectory()) isEmpty = f.listFiles().length == 0;

            System.out.println("File location: " + f);
            System.out.println("Is Directory?: " + f.isDirectory());
            System.out.println();

            if(f.isDirectory()) {
                if(!isEmpty) deleteFiles(f);
            }
            if(f.delete()) System.out.println("Deleted " + f.getName() + " successfully!");
            else System.out.println("Failed to delete" + f.getName() + "!");

        }
        if(directory.delete()) System.out.println("Deleted " + directory.getName() + " successfully!");
        else System.out.println("Failed to delete " + directory.getName());
    }
}
