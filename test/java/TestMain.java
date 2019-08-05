import files.Main;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class TestMain {
    @Before
    public void before(){
        new Prepare().clean();
    }

    @Test
    public void removeFromList(){
        new Prepare().prepareSourceFiles();
        new Prepare().prepareFreshFiles();
        List<File> list1 = new ArrayList<>(Arrays.asList(new File[]{
                new File(Prepare.pathToSource+"file2.log"),
                new File(Prepare.pathToSource+"dirA\\file1.log"),
                new File(Prepare.pathToSource+"file4.log"),
                new File(Prepare.pathToSource+"dirB\\dirBI\\file3.log"),
        }));
        List<File> list2 = new ArrayList<>(Arrays.asList(new File[]{
                new File(Prepare.pathToFresh+"file2.log"),
                new File(Prepare.pathToFresh+"file4.log"),
                new File(Prepare.pathToFresh+"file3.log"),
        }));
        new Main().removeFromList(list1,list2);
        List<File> expected = new ArrayList<>(Arrays.asList(new File[]{
                new File(Prepare.pathToSource+"dirA\\file1.log"),
        }));
        Assert.assertEquals(expected,list1);
    }

    @Test
    public void size() throws IOException {
        new Prepare().prepareSourceFiles();
        List<File> list = new ArrayList<>(Arrays.asList(new File[]{
                new File(Prepare.pathToSource+"file2.log"),
                new File(Prepare.pathToSource+"dirA\\file1.log"),
                new File(Prepare.pathToSource+"file4.log"),
                new File(Prepare.pathToSource+"dirB\\dirBI\\file3.log"),
        }));
        long size = new Main().size(list);
        Assert.assertEquals(460000,size);
    }

    @Test
    public void delete() throws IOException {
        new Prepare().prepareFreshFiles();

        List<File> list = new ArrayList<>(Arrays.asList(new File[]{
                new File(Prepare.pathToFresh+"file2.log"),
                new File(Prepare.pathToFresh+"file1.log"),
                new File(Prepare.pathToFresh+"file4.log"),
                new File(Prepare.pathToFresh+"file3.log"),
        }));
        new Main().delete(list,110000);
        Set<Path> set = Files.walk(Paths.get(Prepare.pathToFresh), FileVisitOption.FOLLOW_LINKS).collect(Collectors.toSet());
        Set<Path> expected = new HashSet<Path>(Arrays.asList(new Path[]{
                new File(Prepare.pathToFresh+"file3.log").toPath(),
                new File(Prepare.pathToFresh+"file4.log").toPath(),
                new File(Prepare.pathToFresh).toPath(),
        }));
        Assert.assertEquals(expected,set);
    }

    @Test
    public void copy() throws IOException {
        new Prepare().prepareSourceFiles();
        List<File> list = new ArrayList<>(Arrays.asList(new File[]{
                new File(Prepare.pathToSource+"file2.log"),
                new File(Prepare.pathToSource+"dirA\\file1.log"),
                new File(Prepare.pathToSource+"file4.log"),
                new File(Prepare.pathToSource+"dirB\\dirBI\\file3.log"),
        }));
        new Main().copy(list,new File(Prepare.pathToFresh), Long.MAX_VALUE);
        Set<Path> set = Files.walk(Paths.get(Prepare.pathToFresh), FileVisitOption.FOLLOW_LINKS).collect(Collectors.toSet());
        Set<Path> expected = new HashSet<Path>(Arrays.asList(new Path[]{
                new File(Prepare.pathToFresh+"file1.log").toPath(),
                new File(Prepare.pathToFresh+"file2.log").toPath(),
                new File(Prepare.pathToFresh+"file3.log").toPath(),
                new File(Prepare.pathToFresh+"file4.log").toPath(),
                new File(Prepare.pathToFresh).toPath(),
        }));
        Assert.assertEquals(expected,set);
    }


}
