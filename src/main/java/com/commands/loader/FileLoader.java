package com.commands.loader;

import java.io.File;

/**
 * Created by Pablo on 3/20/2016.
 */
public class FileLoader {
    private ClassLoader classLoader;

    public FileLoader(){
        classLoader = getClass().getClassLoader();
    }

    public File loadFileFromResources(String fileName){
        return new File(classLoader.getResource(fileName).getFile());
    }
}
