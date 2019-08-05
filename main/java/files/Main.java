package files;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Iterator;
import java.util.List;

/**
 * We assume:
 * All files (log) in source folder has different names, even files are in different subdirectories.
 */
public class Main {
    File source = null;
    File fresh = null;
    File archive = null;

    public static void main(String[] args) {
        new Main(args);
    }

    public Main(String[] args){
        if (args.length!=3)
            throw new RuntimeException("You need to specify: source, fresh and archive directory\n java -jar program.jar /source /fresh /archive");
        source = new File(args[0]);
        fresh = new File(args[1]);
        archive = new File(args[2]);
        init();
    }
    public Main(){}

    private void init(){
        List<File> sourceFiles = new OrderedFiles(source.getAbsolutePath()).getOrderedFiles();
        System.out.println("source files list ordered by date: \n\t"+sourceFiles+"\n");
        List<File> freshFiles = new OrderedFiles(fresh.getAbsolutePath()).getOrderedFiles();
        System.out.println("fresh files list ordered by date: \n\t"+freshFiles+"\n");
        List<File> archiveFiles = new OrderedFiles(archive.getAbsolutePath()).getOrderedFiles();
        System.out.println("archive files list ordered by date: \n\t"+archiveFiles+"\n");
        removeFromList(sourceFiles,freshFiles);
        System.out.println("source files without files already exist in fresh directory: \n\t"+sourceFiles+"\n");
        removeFromList(sourceFiles,archiveFiles);
        System.out.println("source files without files already exist in archive directory (and fresh directory): \n\t"+sourceFiles+"\n");
        long sizeSourceFiles = size(sourceFiles);
        long archiveUsableSpace = archive.getUsableSpace();
        long freshUsableSpace = fresh.getUsableSpace();
        System.out.println("size of source files: "+sizeSourceFiles);
        System.out.println("size of archive usable space: "+archiveUsableSpace);
        System.out.println("size of fresh usable space: "+freshUsableSpace);

        System.out.println("delete from archive: ");
        delete(archiveFiles,sizeSourceFiles - archiveUsableSpace);
        System.out.println("copy fresh files to archive directory: ");
        copy(freshFiles,archive,sizeSourceFiles - freshUsableSpace);
        System.out.println("delete files from fresh directory: ");
        delete(freshFiles,sizeSourceFiles - freshUsableSpace);

        //when size of files from source directory is lass then size of fresh directory
        if (sizeSourceFiles>= fresh.getUsableSpace()){
            System.out.println("copy from source directly to archive");
            copy(sourceFiles,archive, sizeSourceFiles-fresh.getUsableSpace());
            archiveFiles = new OrderedFiles(archive.getAbsolutePath()).getOrderedFiles();
            removeFromList(sourceFiles,archiveFiles);
//            System.out.println("copy left to fresh");
//            copy(sourceFiles,fresh, Long.MAX_VALUE);
        }
        System.out.println("copy source files to fresh directory: ");
        copy(sourceFiles,fresh, Long.MAX_VALUE);



//        delete(sourceFiles,sizeSourceFiles);//!!!REMOVE ORIGINAL FILES!!!
    }

    /**
     *
     * @param from list of files from witch we want remove
     * @param what what we want to be remove
     */
    public void removeFromList(List<File> from, List<File> what) {
        Iterator<File> fromIterator = from.iterator();
        while (fromIterator.hasNext()){
            File fromFile = fromIterator.next();
            Iterator<File> whatIterator = what.iterator();
            while (whatIterator.hasNext()){
                File whatFile = whatIterator.next();
                if (whatFile.getName().equals(fromFile.getName())){
//                    fromFile.delete();
                    fromIterator.remove();
                }
            }
        }
    }

    public long size(List<File> list){
        long size=0;
        for (File f: list) size+=f.length();
        return size;
    }

    /**
     * Delete files until size of deleted files will be <b>grater</b> then size;
     * @see Main#copy(List, File, long)
     * @param files files to delete
     * @param size min deleted files size
     */
    public void delete(List<File> files,long size){
        Iterator<File> iterator = files.iterator();
        while (size>0 && iterator.hasNext()){
            File file = iterator.next();
            System.out.println("\tdelete: "+file+" file size: "+file.length()+" size: "+size+" left usable space: "+file.getUsableSpace());
            size-=file.length();
            file.delete();
        }
    }

    /**
     * copy files until size of coped files will be <b>less</b> then size
     * @see Main#delete(List, long)
     * @param from files to copy
     * @param to location copy to
     * @param size max coped files size
     */
    public void copy(List<File> from, File to, long size) {
        for (File f : from){
            try {
                if (size<0) return;
                System.out.println("\tcopy: "+f+" to: "+to+" file size:"+f.length()+" size: "+size+" left usable space: "+to.getUsableSpace());
                size-=f.length();
                Files.copy(f.toPath(), Paths.get(to.getCanonicalPath() + File.separator + f.getName()), StandardCopyOption.COPY_ATTRIBUTES);

            } catch (IOException e) {
                e.printStackTrace();
            }
                    };
    }
}
