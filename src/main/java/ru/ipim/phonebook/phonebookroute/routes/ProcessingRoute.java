package ru.ipim.phonebook.phonebookroute.routes;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.Formatter;

//  построение маршрута apache.camel
@Component
public class ProcessingRoute<T extends Serializable> extends RouteBuilder {
    private static final Logger log = LoggerFactory.getLogger(ProcessingRoute.class);

    // считать рабочие каталоги
    @Autowired
    private DirectorySettings<T> settings;

    // Регистрируется три маршрута
    @Override
    public void configure() {

        // Обработка входных файлов
        from(inputUri())
                .streamCaching()
                .routeId("Входной файл")
                .log("Найден входной файл = \"${file:name}\" в каталоге =\"${file:absolute.path}\"")
                .log("Файл \"${file:name}\" валидный ? ")
                .to("direct:splitter")
                .marshal("outputDataFormat")
                .to(outputUri())
                .log("Обработанный файл =\"${file:name}\" перемещен в каталог =\"${header.CamelFileNameProduced}\"")
                .to(loggerUri("showExchangePattern=false", "multiline=true"))
                .end();

        // разбивка массива json на отдельные записи
        from("direct:splitter")
                .routeId("Разбивка json на записи")
                .log("Старт разбивки для файла =\"${file:name}\"")
                .unmarshal("inputDataFormat")
                .split(body())
                //.log("\"${file:name}[${header.CamelSplitIndex}]\" передаем запись для insert в таблицу: \"${body}\"")
                .to("direct:insert-item")
                .end();

        // Вставка строки в таблицу
        from("direct:insert-item")
                .routeId("Вставка записи в таблицу")
                .doTry() 
                    .enrich(insertUri())
                    .log("\"${file:name}[${header.CamelSplitIndex}]\" запись была вставлена в таблицу: \"${body}\"")
                .doCatch(Throwable.class) 
                    .setBody(simple("${exception.description}")) 
                    .log("Сбой работы с БД \"${file:name}\" пишем в \"${header.CamelFileNameProduced}\".") 
                    .stop()
                .end();
    }

    //  dataSource и sql
    protected String insertUri() {
        try (Formatter f = new Formatter()) {
            f.format("sql:classpath:%s", "db/insert.sql");
            f.format("?dataSource=%s", "dataSource");
            f.format("&outputType=%s", "SelectOne");
            f.flush();
            return f.toString();
        }
    }

    // входной поток
    protected String inputUri() {
        try (Formatter f = new Formatter()) {
            f.format("file:%s", settings.getInputDirectory());
            f.format("?autoCreate=%s", settings.makeDirectories());
            f.format("&include=%s", "^integration_\\d{6}_\\d{4}\\.txt$");
            f.format("&delete=%s", true);
            f.format("&initialDelay=%d", 500);
            f.format("&delay=%d", 1500);
            f.format("&doneFileName=%s", "${file:name.noext}.ready");
            f.flush();
            return f.toString();
        }
    }

    // Сформировать выходной URI для кталога обработанных файлов
    protected String outputUri() {
        return outputUri(settings.getOutputDirectory());
    }

    // выходной URI 
    protected String outputUri(String settingsPath, String... extra) {
        try (Formatter f = new Formatter()) {
            f.format("file:%s", settingsPath);
            f.format("?charset=%s", charset());
            for (int i = 0; i < extra.length; i++) {
                f.format("&%s", extra[i]);
            }
            f.flush();
            return f.toString();
        }
    }

    // logger
    protected String loggerUri(String... parameters) {
        try (Formatter f = new Formatter()) {
            f.format("log:%s", log.getName());
            for (int i = 0; i < parameters.length; i++) {
                f.format(i == 0 ? "?%s" : "&%s", parameters[i]);
            }
            f.flush();
            return f.toString();
        }
    }

    protected String charset() {
        return StandardCharsets.UTF_8.name();
    }
}
