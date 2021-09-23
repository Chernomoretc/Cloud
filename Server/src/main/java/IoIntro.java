import java.io.File;

public class IoIntro {
    private void createServerDir()
    {
        File dir = new File("server/root");
        if(!dir.exists())
        {
            dir.mkdir();
        }
    }

    public static void main(String[] args) {
        IoIntro intro = new IoIntro();
        intro.createServerDir();
    }
}
