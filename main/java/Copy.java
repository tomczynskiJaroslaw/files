import java.io.File;
import java.util.List;

public class Copy {
    List<File> list;
    File fresh;
    File archive;
    public Copy(List<File> list, File fresh, File archive){
        this.list=list;
        this.fresh=fresh;
        this.archive=archive;
    }

    public void copy(){
         long usableSpace = fresh.getUsableSpace();

    }

}
