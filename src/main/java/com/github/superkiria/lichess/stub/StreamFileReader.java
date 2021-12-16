package com.github.superkiria.lichess.stub;

import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class StreamFileReader {

    private List<String> buffer = new ArrayList<>();

    public StreamFileReader() {
        String line = "";
        try {
            BufferedReader reader = new BufferedReader(new FileReader("stream.txt"));
            while (line != null) {
                buffer.add(line);
                line = reader.readLine();
            }
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public String readLine(int i) {
        if (buffer.size() > i) {
            return buffer.get(i);
        }
        return "";
    }

}
