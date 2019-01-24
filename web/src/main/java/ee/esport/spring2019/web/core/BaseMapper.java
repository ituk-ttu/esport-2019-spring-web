package ee.esport.spring2019.web.core;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;

public interface BaseMapper {

    default OffsetDateTime toOffsetDateTime(Timestamp timestamp) {
        return timestamp != null ?
               OffsetDateTime.ofInstant(Instant.ofEpochMilli(timestamp.getTime()), ZoneId.systemDefault()) :
               null;
    }

    default Boolean toBoolean(Byte booleanAsByte) {
        return booleanAsByte != null ? booleanAsByte != 0 : null;
    }

}
