import files.Main;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class ProgramTest {
    @Test
    public void test() throws IOException {
        new Prepare().clean();
        new Prepare().prepareSourceFiles();

        Main.main(new String[]{"test\\source","test\\fresh","test\\archive"});
        Main.main(new String[]{"test\\source","test\\fresh","test\\archive"});

        Set<Path> set = Files.walk(Paths.get(Prepare.pathToArchive), FileVisitOption.FOLLOW_LINKS).collect(Collectors.toSet());
        Set<Path> expected = new HashSet<Path>(Arrays.asList(new Path[]{
                new File(Prepare.pathToArchive).toPath(),
        }));
        Assert.assertEquals(expected, set);
    }
}
