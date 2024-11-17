package pl.wrss.wita.common.model.converter;

import jakarta.persistence.AttributeConverter;
import lombok.Data;
import lombok.experimental.SuperBuilder;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public abstract class ArrayToStringConverter<T> implements AttributeConverter<T[], String> {

    @Data
    @SuperBuilder
    public static class ConversionPair {
        private Function<Object, String> format;
        private Function<String, Object> parse;
    }

    private final String delimiter;
    private final String escape;
    private final Function<T, String> format;
    private final Function<String, T> parse;
    private final String doubleEscape;
    private final String delimiterEscaped;
    private final Class<T> valueType;

    protected ArrayToStringConverter(Class<T> valueType, char delimiter, char escape, Function<T, String> format, Function<String, T> parse) {
        this.delimiter = String.valueOf(delimiter);
        this.escape = String.valueOf(escape);
        this.format = format;
        this.parse = parse;
        this.doubleEscape = this.escape + this.escape;
        this.delimiterEscaped = this.escape + this.delimiter;
        this.valueType = valueType;
    }

    @Override
    public String convertToDatabaseColumn(T[] values) {
        if (values == null) {
            return null;
        }
        var builder = new StringBuilder();
        for (int i = 0; i < values.length; i++) {
            if (values[i] != null) {
                var value = format.apply(values[i]);
                value = value.replace(escape, doubleEscape);
                value = value.replace(delimiter, delimiterEscaped);
                builder.append(value);
            }
            if (i + 1 < values.length) {
                builder.append(delimiter);
            }
        }
        return builder.toString();
    }

    @Override
    public T[] convertToEntityAttribute(String joined) {
        if (joined == null) {
            return null;
        }
        List<T> values = new ArrayList<>();
        int yourAreHere = 0;
        boolean newPart = true;

        while (true) {
            int sep = joined.indexOf(delimiter, yourAreHere);
            int exc = joined.indexOf(escape, yourAreHere);
            if (sep == -1 && exc == -1) { // last part
                add(values, joined.substring(yourAreHere), parse, newPart);
                break;
            }
            if (sep == -1 && exc + 1 == joined.length()) { // ghost escape
                add(values, joined.substring(yourAreHere, exc), parse, newPart);
                break;
            }
            if (exc == -1 || (sep != -1 && sep < exc)) {
                add(values, joined.substring(yourAreHere, sep), parse, newPart);
                yourAreHere = sep + 1;
                newPart = true;
            } else {
                char next = joined.charAt(exc + 1);
                add(values, joined.substring(yourAreHere, exc) + next, parse, newPart);
                yourAreHere = exc + 2;
                newPart = false;
            }
        }
        return values.toArray((T[]) Array.newInstance(valueType, 0));
    }

    private static <V> void add(List<V> result, String part, Function<String, V> parse, boolean newPart) {
        if (newPart) {
            result.add(parse.apply(part));
        } else {
            int last = result.size() - 1;
            result.set(last, parse.apply(result.get(last) + part));
        }
    }
}
