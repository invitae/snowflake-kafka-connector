package com.snowflake.kafka.connector.records;

import net.snowflake.client.jdbc.internal.fasterxml.jackson.databind.JsonNode;
import net.snowflake.client.jdbc.internal.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.connect.data.SchemaAndValue;
import org.apache.kafka.connect.sink.SinkRecord;
import org.junit.Test;

import java.io.IOException;

import static java.nio.charset.StandardCharsets.UTF_8;

public class KeyColumnTest
{
    private static final String META = "meta";
    private static final String KEY = "key";
    private static final String TOPIC = "test";

    private ObjectMapper mapper = new ObjectMapper();

    @Test
    public void testKeyColumn() throws IOException
    {
        SnowflakeConverter converter = new SnowflakeJsonConverter();
        String keyJson = "{\"name\":\"key\"}";
        String valueJson = "{\"name\":\"value\"}";
        SchemaAndValue key = converter.toConnectData(TOPIC, keyJson.getBytes(UTF_8));
        SchemaAndValue value = converter.toConnectData(TOPIC, valueJson.getBytes(UTF_8));
        SinkRecord record = new SinkRecord(TOPIC, 0, key.schema(), key.value(),
                value.schema(), value.value(), 0);

        RecordService service = new RecordService();
        String output = service.processRecord(record);
        JsonNode result = mapper.readTree(output);

        assert !result.get(META).has(KEY);
        assert result.get(KEY).toString().equals(keyJson);
    }

}
