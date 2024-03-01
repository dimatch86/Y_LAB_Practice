package org.example.monitoringservice.mapper.jdbc_mapper;

import lombok.RequiredArgsConstructor;
import org.example.monitoringservice.model.reading.Reading;
import org.example.monitoringservice.util.CaseConverter;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.ZoneId;
import java.util.UUID;

@RequiredArgsConstructor
public class ReadingRawMapper implements ResultSetExtractor<Reading> {
    private final String type;
    @Override
    public Reading extractData(ResultSet rs) throws SQLException, DataAccessException {
        Reading reading = null;
        if (rs.next()) {
            reading = new Reading(
                    rs.getDouble(CaseConverter.camelCaseToSnakeCase(Reading.Fields.readingValue)),
                    UUID.fromString(rs.getString(CaseConverter.camelCaseToSnakeCase(Reading.Fields.personalAccount))),
                    type,
                    rs.getDate(CaseConverter.camelCaseToSnakeCase(Reading.Fields.sendingDate)).toLocalDate()
                            .atStartOfDay(ZoneId.systemDefault()).toInstant());
        }
        return reading;
    }
}
