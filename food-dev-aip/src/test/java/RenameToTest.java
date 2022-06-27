import java.io.File;

public class RenameToTest {
    public static void main(String[] args) {
        File oldNamed = new File("G:\\1.txt");
        File newNamed = new File("G:\\2.txt");
        System.out.println(oldNamed.renameTo(newNamed)); //true

    }
}
