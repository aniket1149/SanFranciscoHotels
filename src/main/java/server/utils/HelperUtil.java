package server.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class HelperUtil {
    /**
     * Date formatter takes in date to convert to yyyy-MM-dd HH:mm
     * @param date if null return current time or converts the date passed to the format.
     * **/
    public static String dateFormatter(Optional<LocalDateTime> date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        if(date.isPresent()) {
            return date.get().format(formatter);
        }else return LocalDateTime.now().format(formatter);
    }
}
