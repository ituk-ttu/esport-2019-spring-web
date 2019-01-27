package ee.esport.spring2019.web.core;

import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

public interface BaseMapper {

    default OffsetDateTime toOffsetDateTime(Timestamp timestamp) {
        return timestamp != null ?
               OffsetDateTime.ofInstant(timestamp.toInstant(), ZoneOffset.UTC) :
               null;
    }

    default Boolean toBoolean(Byte booleanAsByte) {
        return booleanAsByte != null ? booleanAsByte != 0 : null;
    }

}
