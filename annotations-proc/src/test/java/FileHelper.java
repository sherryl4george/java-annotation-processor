import com.google.common.base.Joiner;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class FileHelper {

    /**
     * Get file from the resources folder and returns a String
     * @param fileName
     * @return
     */
    static protected String getQueryFromFile(String fileName, boolean replace, String... args){
        List<String> codeLines = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        try (Stream<String> stream = Files.lines(Paths.get(FileHelper.class.getClassLoader().getResource(fileName).toURI()))) {
            stream.forEach(x -> {
                if(replace && x.trim().equals(args[0]))
                    codeLines.add(args[1]);
                else
                    codeLines.add(x);
            });
        } catch (IOException | URISyntaxException e) {
//            logger.log(Level.INFO, "Query File read error");
        }
        return Joiner.on("\n").join(codeLines);
    }
}
