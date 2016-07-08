package org.raisercostin.utils;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.springframework.core.io.Resource;

public class ResourceUtils {

    public static String readAsString(Resource resource, String encoding, boolean replaceWithStandardEndOfLine) {
        InputStream in = null;
        try {
            in = resource.getInputStream();
            String string = IOUtils.toString(in, encoding);
            if (replaceWithStandardEndOfLine) {
                string = toStandardEndOfLine(string);
            }
            return string;
        } catch (IOException e) {
            throw new RuntimeException("When trying to read resource [" + resource + "] using encoding=[" + encoding
                    + "] an exception occureed.", e);
        } finally {
            IOUtils.closeQuietly(in);
        }
    }

    public static String toStandardEndOfLine(String string) {
        return string.replace(IOUtils.LINE_SEPARATOR, "\n");
    }

}
