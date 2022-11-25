package ru.ipim.phonebook.phonebookroute;

import org.apache.camel.component.jackson.JacksonDataFormat;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.ipim.phonebook.phonebookroute.routes.Json;
import ru.ipim.phonebook.phonebookroute.routes.Person;
import com.fasterxml.jackson.databind.ObjectMapper;

// Класс работы с конфигурацией
@Configuration
public class PhonebookRouteConfiguration {

    @Bean
    @Qualifier("recordType")
    public Class<Person> recordType() {
        return Person.class;
    }

    // Jackson object mapper used 
    @Bean
    public ObjectMapper objectMapper() {
        return Json.createDefaultMapper();
    }

    // формат входных json-файлов маппим на Person
    @Bean
    public JacksonDataFormat inputDataFormat() {
        final JacksonDataFormat format = new JacksonDataFormat(Person.class);
        format.setUseList(true);
        format.setObjectMapper(objectMapper());
        return format;
    }

    // Jackson вых. формат
    @Bean
    public JacksonDataFormat outputDataFormat() {
        final Class<Person> type = Person.class;
        final JacksonDataFormat format = new JacksonDataFormat(Person.class, type, false);
        format.setUseList(true);
        format.setObjectMapper(objectMapper());
        return format;
    }

}

