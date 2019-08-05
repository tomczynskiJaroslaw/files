import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.FileTime;
import java.util.Comparator;

public class Prepare {
    public static final String root = "test\\";
    public static final String pathToSource = root+"source\\";
    public static final String pathToFresh = root+"fresh\\";
    public static final String pathToArchive = root+"archive\\";

    public void prepareFreshFiles(){
        try {
            createFile(pathToFresh+"file2.log",   10000,  100000);
            createFile(pathToFresh+"file1.log",   100000, 200000);
            createFile(pathToFresh+"file4.log",   110000, 150000);
            createFile(pathToFresh+"file3.log",   169000, 10000);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void prepareSourceFiles() {
        try {
            new File(pathToSource+"dirA").mkdir();
            new File(pathToSource+"dirB").mkdir();
            new File(pathToSource+"dirC").mkdir();
            new File(pathToSource+"dirB\\dirBI").mkdir();
            new File(pathToSource+"dirC\\dirCI").mkdir();
            new File(pathToSource+"dirC\\dirCII").mkdir();
            createFile(pathToSource+"file2.log",                10000,  100000);
            createFile(pathToSource+"dirA\\file1.log",          100000, 200000);
            createFile(pathToSource+"file4.log",                110000, 150000);
            createFile(pathToSource+"dirB\\dirBI\\file3.log",   169000, 10000);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createFile(String path,long time,int size) throws IOException {
        RandomAccessFile randomAccessFile = new RandomAccessFile(path,"rw");
        randomAccessFile.setLength(size);
        randomAccessFile.close();
        BasicFileAttributeView basicFileAttributeView = Files.getFileAttributeView(Paths.get(path),BasicFileAttributeView.class);
        FileTime fileTime = FileTime.fromMillis(time);
        basicFileAttributeView.setTimes(fileTime,fileTime,fileTime);
    }

    public void clean(){
        try {
            if (new File(root).exists())
                Files.walk(Paths.get(root), FileVisitOption.FOLLOW_LINKS)
                        .sorted(Comparator.reverseOrder())  // as the file tree is traversed depth-first and that deleted dirs have to be empty
                        .forEach(t -> {
                            try {
                                Files.delete(t);
                            } catch (IOException e) {
                                // LOG the exception and potentially stop the processing

                            }
                        });
        } catch (IOException e) {
            e.printStackTrace();
        }
        new File(root).mkdir();
        new File(pathToSource).mkdir();
        new File(pathToFresh).mkdir();
        new File(pathToArchive).mkdir();
    }
}
