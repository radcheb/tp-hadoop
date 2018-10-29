package com.radcheb.sysdis.utils;

import java.io.File;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.nio.file.StandardOpenOption.*;

public class FilesSplitUtils {

    public static boolean splitFile(int nbrSplits, String intputFile, String outputDir) throws IOException {

        File dir = new File(outputDir);
        if (!dir.exists()) dir.mkdirs();

        cleanFolder(dir, false);

        List<FileChannel> outs = IntStream.range(0, nbrSplits).mapToObj(i -> {
            Path splitFile = Paths.get(outputDir + "/S" + i + ".txt");
            try {
                return FileChannel.open(splitFile, CREATE_NEW, WRITE);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }).filter(Objects::nonNull).collect(Collectors.toList());

        MappedByteBuffer bb;
        try (FileChannel in = FileChannel.open(Paths.get(intputFile), READ)) {
            bb = in.map(FileChannel.MapMode.READ_ONLY, 0, in.size());
        }

        int outIndex = 0;
        for (int start = 0, pos = 0, end = bb.remaining(), i = 1, lineNum = 1; pos < end; lineNum++) {
            while (pos < end && bb.get(pos++) != '\n') ;

            bb.position(start).limit(pos);
            while (bb.hasRemaining()) outs.get(outIndex).write(bb);
            bb.clear();
            start = pos;
            outIndex = (outIndex + 1) % outs.size();
        }

        outs.forEach(o -> {
            try {
                o.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        return true;
    }

    public static void cleanFolder(File folder, boolean deleteParent) {
        File[] files = folder.listFiles();
        if(files!=null) { //some JVMs return null for empty dirs
            for(File f: files) {
                if(f.isDirectory()) {
                    cleanFolder(f, true);
                } else {
                    f.delete();
                }
            }
        }
        if(deleteParent){
            folder.delete();
        }
    }
}
