package com.radcheb.sysdis.slave;

import com.radcheb.sysdis.slave.mapper.Mapper;
import com.radcheb.sysdis.slave.reduce.Reducer;
import com.radcheb.sysdis.slave.shuffle.Shuffler;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

public class Slave {

    private final static Logger logger = Logger.getLogger(Slave.class);

    public static void main(String[] args) throws ClassNotFoundException, IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException, IOException {

        if (args.length < 1) {
            logger.error("Usage: Slave mode ...");
        } else {
            String mode = args[0];
            String[] subArgs = Arrays.copyOfRange(args, 1, args.length);
            switch (mode) {
                case "0":
                    Mapper.main(subArgs);
                    break;
                case "1":
                    Shuffler.main(subArgs);
                    break;
                case "2":
                    Reducer.main(subArgs);
                    break;
                default:
                    logger.error("Unknown mode: " + mode);
                    break;
            }
        }
    }
}
