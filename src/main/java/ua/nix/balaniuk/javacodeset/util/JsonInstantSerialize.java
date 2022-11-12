package ua.nix.balaniuk.javacodeset.util;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;
import java.time.Instant;

public class JsonInstantSerialize extends StdSerializer<Instant> {

    public JsonInstantSerialize(Class<Instant> t) {
        super(t);
    }

    @Override
    public void serialize(Instant instant, JsonGenerator jsonGenerator,
                          SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeString(instant.toString());
    }
}
