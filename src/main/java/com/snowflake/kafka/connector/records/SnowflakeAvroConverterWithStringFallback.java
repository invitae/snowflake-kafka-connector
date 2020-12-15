/*
 * Copyright (c) 2019 Snowflake Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.snowflake.kafka.connector.records;

import com.snowflake.kafka.connector.internal.Logging;
import org.apache.kafka.connect.data.SchemaAndValue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class SnowflakeAvroConverterWithStringFallback extends SnowflakeAvroConverter
{
  @Override
  SchemaAndValue handleError(final Exception e, final byte[] bytes)
  {
    LOGGER.debug(Logging.logMessage("failed to parse record as Avro, interpreting as utf-8 string"));
    // assumes byte array is UTF-8 encoded
    Map<String, String> payload = new HashMap<>();
    payload.put("rawBytes", new String(bytes, StandardCharsets.UTF_8));
    try
    {
      return new SchemaAndValue(new SnowflakeJsonSchema(),
        new SnowflakeRecordContent(mapper.valueToTree(payload)));
    } catch (IllegalArgumentException ex)
    {
      LOGGER.error(Logging.logMessage("Failed to construct JSON record\n" + ex.toString()));
      return new SchemaAndValue(new SnowflakeJsonSchema(),
        new SnowflakeRecordContent(bytes)); // return a broken record
    }
  }
}
