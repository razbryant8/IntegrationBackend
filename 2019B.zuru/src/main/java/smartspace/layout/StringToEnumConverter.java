package smartspace.layout;

import org.springframework.core.convert.converter.Converter;

public class StringToEnumConverter implements Converter<String, Search> {
    @Override
    public Search convert(String s) {
        return Search.valueOf(s);
    }
}
