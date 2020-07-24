package com.snowflake.kafka.connector.internal;

import com.snowflake.kafka.connector.records.SnowflakeConverter;
import com.snowflake.kafka.connector.records.SnowflakeJsonConverter;
import org.apache.kafka.connect.data.SchemaAndValue;
import org.apache.kafka.connect.sink.SinkRecord;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class SnowflakeSinkServiceV1Test
{
    private static final String TOPIC = "test";

    @Test
    public void testInsertRecordWithInvalidKeyOrValue()
    {
        String validJson = "{\"name\":\"test\"}";
        String validArrJson = "[" + validJson + "]";
        String invalidJson = "test";
        SnowflakeConverter converter = new SnowflakeJsonConverter();
        SchemaAndValue validValue = converter.toConnectData(TOPIC, validJson.getBytes(UTF_8));
        SchemaAndValue invalidValue = converter.toConnectData(TOPIC, invalidJson.getBytes(UTF_8));

        SinkRecord record = new SinkRecord(TOPIC, 0, validValue.schema(), validValue.value(),
                validValue.schema(), validValue.value(), 0);
        SinkRecord recordWithInvalidKey = new SinkRecord(TOPIC, 0, invalidValue.schema(), invalidValue.value(),
                validValue.schema(), validValue.value(), 1);
        SinkRecord recordWithInvalidValue = new SinkRecord(TOPIC, 0, validValue.schema(), validValue.value(),
                invalidValue.schema(), invalidValue.value(), 2);

        SnowflakeConnectionService conn = spy(SnowflakeConnectionService.class);
        when(conn.getConnectorName()).thenReturn("connector");
        SnowflakeSinkServiceV1 sinkService = new SnowflakeSinkServiceV1(conn);
        sinkService.startTask("table", TOPIC, 0);

        sinkService.insert(record);
        sinkService.insert(recordWithInvalidKey);
        sinkService.insert(recordWithInvalidValue);

        ArgumentCaptor<String> file = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<byte[]> content = ArgumentCaptor.forClass(byte[].class);
        verify(conn, times(4)).putToTableStage(any(), file.capture(), content.capture());

        assert file.getAllValues().get(0).matches("connector/table/0/1_key_\\d+\\.gz");
        assert new String(content.getAllValues().get(0), UTF_8).equals(invalidJson);
        assert file.getAllValues().get(1).matches("connector/table/0/1_value_\\d+\\.gz");
        assert new String(content.getAllValues().get(1), UTF_8).equals(validArrJson);
        assert file.getAllValues().get(2).matches("connector/table/0/2_key_\\d+\\.gz");
        assert new String(content.getAllValues().get(2), UTF_8).equals(validArrJson);
        assert file.getAllValues().get(3).matches("connector/table/0/2_value_\\d+\\.gz");
        assert new String(content.getAllValues().get(3), UTF_8).equals(invalidJson);
    }
}
