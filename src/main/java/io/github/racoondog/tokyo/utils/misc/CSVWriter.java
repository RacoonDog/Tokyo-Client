package io.github.racoondog.tokyo.utils.misc;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.io.Writer;

//TODO will replace with own implementation
@Environment(EnvType.CLIENT)
public class CSVWriter extends com.opencsv.CSVWriter {
    public CSVWriter(Writer writer) {
        super(writer);
    }

    /**
     * It really is THAT easy to write a good CSV writer...
     */
    public void writeNext(Object... nextLine) {
        int len = nextLine.length;
        String[] line = new String[len];
        for (int i = 0; i < len; i++) line[i] = String.valueOf(nextLine[i]);
        writeNext(line);
    }
}
