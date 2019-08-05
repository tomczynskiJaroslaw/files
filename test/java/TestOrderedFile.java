import files.OrderedFiles;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TestOrderedFile {
    @Before
    public void before(){
        new Prepare().clean();
    }

    @Test
    public void getOrderedFiles(){

        new Prepare().prepareSourceFiles();
        List<File> actual = new OrderedFiles(Prepare.pathToSource).getOrderedFiles();
        List<File> expected = new ArrayList<>(Arrays.asList(new File[]{
                new File(Prepare.pathToSource+"file2.log"),
                new File(Prepare.pathToSource+"dirA\\file1.log"),
                new File(Prepare.pathToSource+"file4.log"),
                new File(Prepare.pathToSource+"dirB\\dirBI\\file3.log"),
        }));

        Assert.assertEquals(expected,actual);
    }


}
