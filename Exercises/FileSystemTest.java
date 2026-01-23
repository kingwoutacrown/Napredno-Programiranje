import java.util.*;
import java.util.stream.Collectors;

interface IFile {
    String getFileName();
    long getFileSize();
    String getFileInfo(IFile file);
    void sortBySize();
    String toString(int indent);
}

class File implements IFile{
    private String name;
    private long size;
    public File(String name, long size) {
        this.name = name;
        this.size = size;
    }

    @Override
    public String getFileName() {
        return name;
    }

    @Override
    public long getFileSize() {
        return size;
    }

    @Override
    public String getFileInfo(IFile file) {
        return "";
    }

    @Override
    public void sortBySize() {
        return;
    }
    public String toString(int indent) {
        //File name [името на фајлот со 10 места порамнето на десно] File size: [големината на фајлот со 10 места пораменета на десно ]
        return "    ".repeat(indent) + String.format("File name: %10s File size: %10d\n", getFileName(), getFileSize());
    }
}
class Folder implements IFile{
    private String name;
    Map<String,IFile> fileMap;
    public Folder(String name) {
        this.name = name;
        fileMap = new TreeMap<>();
    }
    public void addFile(IFile file) throws FileNameExistsException {
        if(fileMap.containsKey(file.getFileName())) {
            throw new FileNameExistsException(String.format("There is already a file named %s in the folder %s",file.getFileName(),name));
        }
        else {
            fileMap.put(file.getFileName(),file);
        }
    }

    public Map<String,IFile> getFileMap() { return fileMap; }

    @Override
    public String getFileName() {
        return name;
    }

    @Override
    public long getFileSize() {
        return fileMap.values().stream().mapToLong(IFile::getFileSize).sum();
    }

    @Override
    public String getFileInfo(IFile file) {
        return fileMap.get(file.getFileName()).toString();
    }

    @Override
    public void sortBySize() {
        fileMap.values().forEach(IFile::sortBySize);
        fileMap = fileMap.entrySet().stream().sorted(
                Comparator.comparing(
                        v -> v.getValue().getFileSize()
                )
        ).collect( Collectors.toMap(Map.Entry::getKey,Map.Entry::getValue,
                (a,b) -> a, LinkedHashMap::new));
    }
    @Override
    public String toString(int indent) {
        //Folder name [името на директориумот со 10 места порамнето на десно] Folder size: [големината на директориумот со 10 места пораменета на десно ]
        StringBuilder sb = new StringBuilder();
        sb.append("    ".repeat(indent));
        sb.append(String.format("Folder name: %10s Folder size: %10d\n", getFileName(),getFileSize()));
        if(fileMap.isEmpty()) {
            return sb.toString();
        }
        else {
            fileMap.values().forEach(v -> sb.append(v.toString(indent+1)));
        }
        return sb.toString();
    }
}

class FileNameExistsException extends Exception {
    public FileNameExistsException(String message) {
        super(message);
    }
}

class FileSystem {
    Folder root;
    public FileSystem() {
        root = new Folder("root");
    }
    public void addFile(IFile file) throws FileNameExistsException {
        root.addFile(file);
    }
    private long findLargestFile(IFile file) {
        if(file instanceof File) {
            return file.getFileSize();
        }
        else {
            Folder folder = (Folder) file;
            return folder.getFileMap().values().stream().mapToLong(this::findLargestFile).max().orElse(0);
        }
    }
    public long findLargestFile() {
        return findLargestFile(root);
    }
    public void sortBySize() {
        root.sortBySize();
    }
    @Override
    public String toString() {
        return root.toString(0);
    }
}
public class FileSystemTest {

    public static Folder readFolder (Scanner sc)  {

        Folder folder = new Folder(sc.nextLine());
        int totalFiles = Integer.parseInt(sc.nextLine());

        for (int i=0;i<totalFiles;i++) {
            String line = sc.nextLine();

            if (line.startsWith("0")) {
                String fileInfo = sc.nextLine();
                String [] parts = fileInfo.split("\\s+");
                try {
                    folder.addFile(new File(parts[0], Long.parseLong(parts[1])));
                } catch (FileNameExistsException e) {
                    System.out.println(e.getMessage());
                }
            }
            else {
                try {
                    folder.addFile(readFolder(sc));
                } catch (FileNameExistsException e) {
                    System.out.println(e.getMessage());
                }
            }
        }

        return folder;
    }

    public static void main(String[] args)  {

        //file reading from input

        Scanner sc = new Scanner (System.in);

        System.out.println("===READING FILES FROM INPUT===");
        FileSystem fileSystem = new FileSystem();
        try {
            fileSystem.addFile(readFolder(sc));
        } catch (FileNameExistsException e) {
            System.out.println(e.getMessage());
        }

        System.out.println("===PRINTING FILE SYSTEM INFO===");
        System.out.println(fileSystem.toString());

        System.out.println("===PRINTING FILE SYSTEM INFO AFTER SORTING===");
        fileSystem.sortBySize();
        System.out.println(fileSystem.toString());

        System.out.println("===PRINTING THE SIZE OF THE LARGEST FILE IN THE FILE SYSTEM===");
        System.out.println(fileSystem.findLargestFile());




    }
}