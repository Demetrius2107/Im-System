import config.BootStrapConfig;
import org.yaml.snakeyaml.Yaml;
import server.LimServer;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * @author: Elon
 * @title: Starter
 * @projectName: im-system
 * @description: TODO
 * @date: 2025/1/24 21:17
 */
public class Starter {

    public static void main(String[] args) {
        if(args.length > 0){

        }
    }


    private static void start(String path){
        try {
            Yaml yaml = new Yaml();
            InputStream inputStream  =  new FileInputStream(path);
            BootStrapConfig bootStrapConfig = yaml.loadAs(inputStream,BootStrapConfig.class);

            new LimServer(bootStrapConfig.getLim().start);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } finally {

        }
    }
}
