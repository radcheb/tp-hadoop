package com.radcheb.sysdis.slave.mapper;

import com.radcheb.sysdis.etapes.etape9.Etape7_44;
import com.radcheb.sysdis.utils.Pair;
import org.apache.log4j.Logger;

import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.function.Function;
import java.util.stream.Stream;

public class Mapper {

    private final static Logger logger = Logger.getLogger(Mapper.class);

    public static void main(String[] args) throws ClassNotFoundException, IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException, IOException {
        if (args.length < 1) {
            logger.error("Mapper class is missing.");
        } else {
            LineMapperFunction mapperFunction = loadMapper(args[0]);

            File outputDir = new File("/tmp/jcast/unsorted_map");
            if(!outputDir.exists()){
                logger.info("created directory " + outputDir.getName() + ": " + outputDir.mkdirs());
            }

            Arrays.stream(args).skip(1).parallel().forEach(
                    filename -> {
                        String splitIndex = Paths.get(filename).getFileName().toString().split("\\.")[0].substring(1);
                        String outputFile = "/tmp/jcast/unsorted_map/UM" + splitIndex;
                        try (BufferedReader reader = Files.newBufferedReader(Paths.get(filename));
                             BufferedWriter writer = Files.newBufferedWriter(
                                     Paths.get(outputFile))) {
                            reader.lines().parallel().flatMap((Function<String, Stream<?>>) mapperFunction::mapToLine)
                                    .forEach(l -> Mapper.savePair(l, writer));
                        } catch (IOException e) {
                            logger.error("error while reading split" + filename + "{}", e);
                        }
                    }
            );
        }
    }

    private static void savePair(Object o, BufferedWriter bw) {
        Pair p = (Pair) o;
        String key = (String) p.getKey();
        String correctedKey = key.replace(" ", "_");
        String strVal = ((Serializable) p.getValue()).toString();
        System.out.println(correctedKey);
        try {
            bw.write(correctedKey + " " + strVal + System.lineSeparator());
        } catch (IOException e) {
            logger.error("Failed to write " + correctedKey + ", {}", e);
            System.exit(1);
        }
        logger.debug(String.format("Generated pair: (%s, %s)", correctedKey, strVal));
    }

    private static Class getMapperOutputClass(String classname) {
        Class outputCls = null;
        try {
            outputCls = Class.forName(classname);
        } catch (ClassNotFoundException e) {
            logger.error("Class " + classname + " can't be loaded, {}", e);
            System.exit(1);
        }
        return outputCls;
    }

    private static LineMapperFunction loadMapper(String className) throws IllegalAccessException, InstantiationException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException {
        Class<?> mapperCls = Class.forName(className);
        logger.info("Loaded class name: " + mapperCls.getName());
        // Create a new instance from the loaded class
        Constructor constructor = mapperCls.getConstructor();
        return (LineMapperFunction) constructor.newInstance();
    }

}
