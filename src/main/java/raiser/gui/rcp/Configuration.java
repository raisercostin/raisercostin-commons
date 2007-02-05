/*
 * Created on Jun 8, 2005
 */
package raiser.gui.rcp;

import java.beans.*;
import java.io.*;
import java.util.*;
import javax.swing.*;
import org.apache.log4j.Logger;

public class Configuration implements Serializable {
    /**
     * Logger for this class
     */
    private static final Logger logger = Logger.getLogger(Configuration.class);

    public Configuration() {
        setData(new HashMap<String, Object>());
    }

    private Map<String, Object> data;

    private static Configuration loadFromXML(Configuration result, InputStream in) {
        Configuration fileConfig = loadFromXML2(in);
        if (fileConfig != null) {
            result.getData().putAll(fileConfig.getData());
        }
        return result;
    }
    private static Configuration loadFromFile(Configuration result, InputStream in) throws IOException, ClassNotFoundException {
        Configuration fileConfig = loadFromFile2(in);
        if (fileConfig != null) {
            result.getData().putAll(fileConfig.getData());
        }
        return result;
    }

    private static Configuration loadFromXML2(InputStream in) {
        XMLDecoder d = new XMLDecoder(new BufferedInputStream(in));
        d.setExceptionListener(new ExceptionListener() {
            public void exceptionThrown(Exception e) {
                logger.error(e);
            }
        });
        Configuration result = (Configuration) d.readObject();
        d.close();
        return result;
    }
    private static Configuration loadFromFile2(InputStream in) throws IOException, ClassNotFoundException {
        ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(in));
        Configuration result = (Configuration) ois.readObject();
        return result;
    }

    private static void saveToXML(Configuration props, OutputStream os) {
        XMLEncoder e = new XMLEncoder(new BufferedOutputStream(os));
        e.setExceptionListener(new ExceptionListener() {
            public void exceptionThrown(Exception e) {
                logger.error(e);
            }
        });
        e.writeObject(props);
        e.close();
        return;
    }
    private static void saveToFile(Configuration props, OutputStream os) throws IOException {
        ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(os));
        oos.writeObject(props);
        oos.close();
    }

    public void save(OutputStream os) throws IOException {
        saveToFile(this, os);
    }

    public void load(InputStream is) throws IOException, ClassNotFoundException {
        loadFromFile(this, is);
    }
    public void saveToXML(OutputStream os) {
        saveToXML(this, os);
    }

    public void loadFromXML(InputStream is) {
        loadFromXML(this, is);
    }

    public Object get(String key) {
        return getData().get(key);
    }

    public void remove(String key) {
        getData().remove(key);
    }

    public void set(String key, Object value) {
        getData().put(key, value);
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }

    public Map<String, Object> getData() {
        return data;
    }
}
