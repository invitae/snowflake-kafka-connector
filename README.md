# Snowflake-kafka-connector

[![Build Status](https://github.com/snowflakedb/snowflake-kafka-connector/workflows/Kafka%20Connector%20End2End%20Test/badge.svg)](https://github.com/snowflakedb/snowflake-kafka-connector/actions?query=workflow%3A%22Kafka+Connector+End2End+Test%22)
[![codecov](https://codecov.io/gh/snowflakedb/snowflake-kafka-connector/branch/master/graph/badge.svg)](https://codecov.io/gh/snowflakedb/snowflake-kafka-connector)
[![License](http://img.shields.io/:license-Apache%202-brightgreen.svg)](http://www.apache.org/licenses/LICENSE-2.0.txt)

Snowflake-kafka-connector is a plugin of Apache Kafka Connect.

### Tests
To run:
```
mvn test
```

#### Jenkins prerequisites:
* Ensure `JENKINS_CONNECTOR_TESTS` database and `TEST` schema exist.
* Grant `MONITOR, USAGE` for `PUBLIC` role on the database
* Grant `CREATE*, MONITOR, USAGE` for `PUBLIC` role on the schema