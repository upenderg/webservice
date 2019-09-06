package dk.gilakathula.webservice.util;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.ocpsoft.pretty.time.PrettyTime;
import org.joda.time.DateTime;

import java.io.IOException;

public class CustomDateTimeSerializer extends JsonSerializer<DateTime> {
    private static final PrettyTime prettyTime = new PrettyTime();
    @Override
    public void serialize(DateTime value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
        jgen.writeObject(prettyTime.format(value.toDate()));
    }
}
