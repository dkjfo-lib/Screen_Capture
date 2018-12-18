package Files;

import java.io.*;

public class FileWork {

    static final String FILE_LOCATION = "HeadLocalAddress.swarm";


    public static void writeToFile(String content) throws IOException {
        BufferedWriter writer = null;
        try {
            File file = new File(FILE_LOCATION);
            writer = new BufferedWriter(new FileWriter(file));
            writer.write(content);
            writer.flush();
            writer.close();
        } finally {
            if (writer != null)
                writer.close();
        }
    }

    public static String[] readFile() throws IOException {
        BufferedReader reader = null;
        String[] content = null;
        try {
            reader = new BufferedReader(new FileReader(FILE_LOCATION));

            content = reader.readLine().split(":");
            reader.close();
        } finally {
            if (reader != null)
                reader.close();
        }
        return content;
    }
}
