import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

public class IoIntro {
    private void createServerDir()
    {
        File dir = new File("server/root");
        if(!dir.exists())
        {
            dir.mkdir();
        }
    }
     private static void   dir()
     {
         Path p1 = Paths.get("Client").toAbsolutePath();
         Path p2 = Paths.get("Client","root").toAbsolutePath();
         System.out.println(p1.relativize(p2));
     }


    public static void main(String[] args) {
       dir();
    }
}
