package smartspace.dao.rdb;

import com.fasterxml.jackson.databind.ObjectMapper;

import javax.persistence.AttributeConverter;
import java.util.Map;

public class MapToJsonConverter implements AttributeConverter<Map<String, Object>, String> {

    private ObjectMapper jackson;

    public MapToJsonConverter() {
        this.jackson = new ObjectMapper();
    }

    @Override
    public String convertToDatabaseColumn(Map<String, Object> stringObjectMap) {
        try {
            return this.jackson.writeValueAsString(stringObjectMap);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Map<String, Object> convertToEntityAttribute(String s) {
        try {
            return this.jackson.readValue(s, Map.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
