package files;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

public class OrderedFiles {
    private String directory;

    public OrderedFiles(String directory){
        this.directory=directory;
    }

    public List<File> getOrderedFiles(){
        try {
            return tryGetOrderedFiles();
        } catch (IOException e) {

            e.printStackTrace();
            throw new RuntimeException("Fail access to source directory!!!");
        }
//        return null;
    }

    private List<File> tryGetOrderedFiles() throws IOException {
        List<File> list = new ArrayList<File>();
            Files.walk(Paths.get(directory), FileVisitOption.FOLLOW_LINKS)
                    .filter(p->Files.isRegularFile(p) && emptyFile(p))
                    .sorted((o1, o2) -> (int) (creationTime(o1)-creationTime(o2)))
                    .forEach(p->list.add(p.toFile()));
        return list;
    }

    private long creationTime(Path path) {
        return basicFileAttribute(path).creationTime().toMillis();
    }

    private BasicFileAttributes basicFileAttribute(Path path){
        try {
            return Files.getFileAttributeView(path, BasicFileAttributeView.class).readAttributes();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private boolean emptyFile(Path path){
        return basicFileAttribute(path).size()>0;
    }
}
