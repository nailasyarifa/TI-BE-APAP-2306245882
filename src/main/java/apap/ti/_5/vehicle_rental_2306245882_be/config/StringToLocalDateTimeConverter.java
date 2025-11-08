package apap.ti._5.vehicle_rental_2306245882_be.config;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class StringToLocalDateTimeConverter implements Converter<String, LocalDateTime> {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

    @Override
    public LocalDateTime convert(String source) {
        if (source == null || source.isEmpty()) return null;
        return LocalDateTime.parse(source, FORMATTER);
    }
}
