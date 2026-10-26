package ${packages.utils};

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class DateUtils {

    private static final Object LOCK_OBJ = new Object();

    private static final Map<String, ThreadLocal<SimpleDateFormat>> SD_MAP = new HashMap<>();

    private DateUtils() {
    }

    private static SimpleDateFormat getSdf(final String pattern) {
        ThreadLocal<SimpleDateFormat> tl = SD_MAP.get(pattern);
        if (tl == null) {
            synchronized (LOCK_OBJ) {
                tl = SD_MAP.get(pattern);
                if (tl == null) {
                    tl = new ThreadLocal<SimpleDateFormat>() {
                        @Override
                        protected SimpleDateFormat initialValue() {
                            SimpleDateFormat format = new SimpleDateFormat(pattern);
                            format.setLenient(false);
                            return format;
                        }
                    };
                    SD_MAP.put(pattern, tl);
                }
            }
        }
        return tl.get();
    }

    public static String format(Date date, String pattern) {
        if (date == null) {
            return null;
        }
        SimpleDateFormat sdf = getSdf(pattern);
        sdf.setTimeZone(java.util.TimeZone.getTimeZone("GMT+8"));
        return sdf.format(date);
    }

    public static Date parse(String dateStr, String pattern) {
        if (dateStr == null || dateStr.isBlank()) {
            return null;
        }
        try {
            return getSdf(pattern).parse(dateStr);
        } catch (ParseException e) {
            throw new IllegalArgumentException("日期解析失败: " + dateStr, e);
        }
    }
}
