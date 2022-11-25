package ru.ipim.phonebook.phonebookroute.routes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

// Конфигурация рабочих каталогов маршрута
@Component
public class DirectorySettings<T> {
    private static final Logger log = LoggerFactory.getLogger(DirectorySettings.class);

    // имя настройки на входной каталог
    public static final String PROCESS_DIRECTORY_PROPERTY = "phonebook" + ".workDir";
    // путь по умолчанию
    public static final String DEFAULT_DIRECTORY_PROPERTY = "phonebook" + ".defaultDir";
    // название настройки создания каталогов в случае если их нет
    public static final String CREATE_SETTINGS_PROPERTY = "phonebook" + ".makeDirs";

    // имя настройки на выходной каталог
    public static final String OUTPUT_DIRECTORY_PROPERTY = "phonebook" + ".outputDir";
    /** The name of the property pointing to a directory where to put invalid JSON data. */

    // настройки
    @Autowired
    private Environment env;

    @Autowired(required = false)
    @Qualifier("recordType")
    private Class<T> type;

    // корневой каталог маршрута
    private Path defaultDir;
    // флаг создания каталогов
    private Boolean makeDirs;

    // Входной каталог с файлами выгрузками
    private Path workDir;

    // Выходной каталог
    private Path outputDir;

    public String getInputDirectory() {
        return workDir().toString();
    }

    public String getOutputDirectory() {
        return outputDir.toString();
    }

    public boolean makeDirectories() {
        return makeDirs();
    }

    @PostConstruct
    public void configureSettings() throws IOException {
        workDir = configureDirectory(PROCESS_DIRECTORY_PROPERTY, "work");
        log.info("Входной каталог с файлами выгрузками: {}", workDir.toString());

        outputDir = configureDirectory(OUTPUT_DIRECTORY_PROPERTY, "data");
        log.info("Выходной каталог: {}", outputDir.toString());

    }

    protected Path configureDirectory(String propertyName, String defaultDirName)
            throws IOException {

        Objects.requireNonNull(propertyName, "propertyName must not be null.");
        Objects.requireNonNull(propertyName, "defaultDirName must not be null.");

        final String value = env().getProperty(propertyName);
        Path path;
        if (!StringUtils.isBlank(value)) {
            final Path tmp = Paths.get(value);
            if (tmp.isAbsolute()) {
                path = tmp;
            } else {
                path = defaultDir().resolve(tmp);
            }
        } else {
            path = defaultDir().resolve(defaultDirName);
        }

        if (!Files.exists(path) && makeDirs()) {
            Files.createDirectories(path);
        }

        return path;
    }

   // вернуть настройки
    protected final Environment env() {
        return env;
    }

    // Рабочий каталог
    protected final Path workDir() {
        return workDir;
    }

    // Каталог по умолчанию
    protected final Path defaultDir() throws IOException {
        if (defaultDir != null) {
            return defaultDir;
        }

        defaultDir = configureDefaultDir();
        log.info("Default home directory path: {}", defaultDir.toString());

        return defaultDir;
    }

    protected final boolean makeDirs() {
        if (makeDirs != null) {
            return makeDirs.booleanValue();
        }

        makeDirs = ObjectUtils.defaultIfNull(configureMakeDirs(), Boolean.TRUE);
        log.info("Флаг создание кталогов: {}.", makeDirs.booleanValue() ? "enabled" : "disabled");

        return makeDirs.booleanValue();
    }

    // Получить путь по умолчанию
    protected Path configureDefaultDir() throws IOException {
        // попытка считать настройку
        final Path defaultPath;
        final String value = env().getProperty(DEFAULT_DIRECTORY_PROPERTY);
        if (!StringUtils.isBlank(value)) {
            // готовый путь
            defaultPath = Paths.get(value).toAbsolutePath();

        } else {
            // настройки нет, сформировать путь из домашнего каталога пользователя
            final String homeDir = env().getRequiredProperty("user.home");
            final String workDirName = ".phonebook";
            //if (type != null) {
            //    final String sectionName = type.getSimpleName().toLowerCase();
            //    defaultPath = Paths.get(homeDir, workDirName, sectionName);
            //} else {
            defaultPath = Paths.get(homeDir, workDirName);
            //}
        }

        if (makeDirs()) {
            Files.createDirectories(defaultPath);
        }

        return defaultPath;
    }

    // Настройка создания каталогов
    protected Boolean configureMakeDirs() {
        return env().getProperty(CREATE_SETTINGS_PROPERTY, Boolean.class, Boolean.TRUE);
    }

}
