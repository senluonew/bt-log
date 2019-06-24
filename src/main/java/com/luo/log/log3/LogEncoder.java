package com.luo.log.log3;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

/**
 * <P>
 *     日志格式编码器
 * </P>
 * @author pudding
 * @version 0.1.0
 * @design
 * @date 2018/12/17.14:16
 * @see
 */
public abstract class LogEncoder {

    private static LogEncoder DEFAULT_ENCODER = new DefaultLogEncoder();

    public static LogEncoder getDefaultEncoder() {
        return DEFAULT_ENCODER;
    }

    public static void setDefaultEncoder(LogEncoder logEncoder) {
        DEFAULT_ENCODER = logEncoder;
    }

    public abstract void encodeLine(LogMsg logMsg, final StringBuilder builder);

    public String[] encodeThrowable(final Writer builder) throws IOException {
        return null;
    }

    public String[] encodeThrowable(final StringBuilder builder) throws IOException {
        return null;
    }

    private static class DefaultLogEncoder extends LogEncoder {

        @Override
        public void encodeLine(LogMsg logMsg, final StringBuilder builder) {
            builder.append(logMsg.getTimeStr()).append(LogFilter.SPACE).append(logMsg.getLevelStr())
                    .append(LogFilter.NAME_LEFT).append(logMsg.getThreadName()).append(LogFilter.NAME_RIGHT)
                    .append(logMsg.getClassName())
                    .append(LogFilter.SPACE).append(LogFilter.COLON).append(LogFilter.SPACE)
                    .append(logMsg.getFormattedLine());
        }

        @Override
        public String[] encodeThrowable(final Writer builder) throws IOException {
            return encodeThrowable(builder.toString());
        }

        @Override
        public String[] encodeThrowable(final StringBuilder builder) throws IOException {
            return encodeThrowable(builder.toString());
        }

        private String[] encodeThrowable(String builder) throws IOException {
            List<String> strings = new ArrayList<>();
            BufferedReader br = new BufferedReader(new StringReader(builder));
            String line;
            while ((line = br.readLine()) != null) {
                strings.add(line);
            }
            return strings.toArray(new String[strings.size()]);
        }
    }
}
