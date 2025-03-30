package uj.wmii.pwj.delegations;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class Calc {
    BigDecimal calculate(String name, String start, String end, BigDecimal dailyRate) throws IllegalArgumentException {
        ZonedDateTime startTime = ZonedDateTime.parse(start, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm z"));
        ZonedDateTime endTime = ZonedDateTime.parse(end, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm z"));

        if (!endTime.isAfter(startTime)) return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);

        long totalMinutes = Duration.between(startTime, endTime).toMinutes();
        long totalDays = totalMinutes / (24 * 60);
        long remainingMinutes = totalMinutes % (24 * 60);
        BigDecimal salary = dailyRate.multiply(BigDecimal.valueOf(totalDays));

        if (remainingMinutes > 0) {
            double fraction = remainingMinutes < 8 * 60 ? 1.0 / 3 : remainingMinutes < 12 * 60 ? 0.5 : 1.0;
            salary = salary.add(dailyRate.multiply(BigDecimal.valueOf(fraction)));
        }
        return salary.setScale(2, RoundingMode.HALF_UP);
    }
}
