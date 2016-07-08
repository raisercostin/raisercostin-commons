package org.raisercostin.util;

import java.io.File;
import java.io.IOException;

import org.springframework.core.io.FileSystemResource;

import com.google.common.base.Supplier;

public class JsonConfigurator {
	private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(JsonConfigurator.class);

	public static <T> T configure(String path, Supplier<T> supplier) {
		T defaultDao = supplier.get();
		return configureFromDao(path, defaultDao);
	}

	@SuppressWarnings("unchecked")
	public static <T> T configureFromDao(String path, T defaultDao) {
		Class<? extends Object> theClass = defaultDao.getClass();
		File defaultFile = new File(PropertyUtils.getInterpretedText(path));
		if (!defaultFile.exists()) {
			LOG.info("import " + theClass + " from [code].");
			export(defaultDao, defaultFile.getAbsolutePath());
		} else {
			T oldDao = null;
			try {
				oldDao = (T) importDao(defaultFile, theClass);
			} catch (Throwable e) {
				LOG.warn("Can't parse old " + theClass + " from [" + defaultFile.getAbsolutePath()
						+ "]. A new file will be generated.");
			}
			T newDao = defaultDao;
			if (oldDao == null || !oldDao.equals(newDao)) {
				backupConfigurationJsonFile(defaultFile.getAbsoluteFile());
				LOG.info("import " + theClass + " from [code default supplier].");
				export(defaultDao, defaultFile.getAbsolutePath());
			}
		}
		LOG.info("import " + theClass + " from [" + defaultFile.getAbsolutePath() + "].");
		return (T) importDao(defaultFile, theClass);
	}
	
	public static void backupConfigurationJsonFile(File file) {
		try {
			File renamedFile = new File(file.getAbsoluteFile() + "-" // + oldDao.version + "-"
					+ GeneratorUtils.generateUniqueTimestamp() + ".json");
			LOG.info("rename " + file + " to " + renamedFile);
			org.apache.commons.io.FileUtils.moveFile(file, renamedFile);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private static <T> T importDao(File file, Class<T> theClass) {
		try {
			String data = org.raisercostin.utils.FileUtils.readToString(new FileSystemResource(file), "UTF-8");
			return configureFromJson(data, theClass);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static <T> T configureFromJson(String data, Class<T> theClass) {
		T theObject = JsonUtil.fromJson(data, theClass);
		return theObject;
	}
	
	

	public static <T> void export(T dao, String path) {
		String res = PropertyUtils.getInterpretedText(path);
		String data = JsonUtil.toJson(dao);
		File file = new File(res);
		LOG.info("export " + dao.getClass() + " to   [" + file.getAbsolutePath() + "].");
		try {
			FileUtils.forceMkdirForParent(file);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		try {
			org.raisercostin.utils.FileUtils.writeFromString(new FileSystemResource(file), data, "UTF-8");
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
