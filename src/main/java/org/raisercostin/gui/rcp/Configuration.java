/*
 * Created on Jun 8, 2005
 */
package org.raisercostin.gui.rcp;

import java.beans.*;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Configuration implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4198596413142969464L;

	/**
	 * Logger for this class
	 */
	private static final Logger logger = LoggerFactory.getLogger(Configuration.class);

	public Configuration() {
		setData(new HashMap<String, Object>());
	}

	private Map<String, Object> data;

	private static Configuration loadFromXML(final Configuration result,
			final InputStream in) {
		final Configuration fileConfig = loadFromXML2(in);
		if (fileConfig != null) {
			result.getData().putAll(fileConfig.getData());
		}
		return result;
	}

	private static Configuration loadFromFile(final Configuration result,
			final InputStream in) throws IOException, ClassNotFoundException {
		final Configuration fileConfig = loadFromFile2(in);
		if (fileConfig != null) {
			result.getData().putAll(fileConfig.getData());
		}
		return result;
	}

	private static Configuration loadFromXML2(final InputStream in) {
		final XMLDecoder d = new XMLDecoder(new BufferedInputStream(in));
		d.setExceptionListener(new ExceptionListener() {
			@Override
			public void exceptionThrown(final Exception e) {
				logger.error("",e);
			}
		});
		final Configuration result = (Configuration) d.readObject();
		d.close();
		return result;
	}

	private static Configuration loadFromFile2(final InputStream in)
			throws IOException, ClassNotFoundException {
		final ObjectInputStream ois = new ObjectInputStream(
				new BufferedInputStream(in));
		final Configuration result = (Configuration) ois.readObject();
		return result;
	}

	private static void saveToXML(final Configuration props,
			final OutputStream os) {
		final XMLEncoder e = new XMLEncoder(new BufferedOutputStream(os));
		e.setExceptionListener(new ExceptionListener() {
			@Override
			public void exceptionThrown(final Exception e) {
				logger.error("",e);
			}
		});
		e.writeObject(props);
		e.close();
		return;
	}

	private static void saveToFile(final Configuration props,
			final OutputStream os) throws IOException {
		final ObjectOutputStream oos = new ObjectOutputStream(
				new BufferedOutputStream(os));
		oos.writeObject(props);
		oos.close();
	}

	public void save(final OutputStream os) throws IOException {
		saveToFile(this, os);
	}

	public void load(final InputStream is) throws IOException,
			ClassNotFoundException {
		loadFromFile(this, is);
	}

	public void saveToXML(final OutputStream os) {
		saveToXML(this, os);
	}

	public void loadFromXML(final InputStream is) {
		loadFromXML(this, is);
	}

	public Object get(final String key) {
		return getData().get(key);
	}

	public void remove(final String key) {
		getData().remove(key);
	}

	public void set(final String key, final Object value) {
		getData().put(key, value);
	}

	public void setData(final Map<String, Object> data) {
		this.data = data;
	}

	public Map<String, Object> getData() {
		return data;
	}
}
