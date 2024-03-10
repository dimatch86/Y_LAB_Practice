package org.example.monitoringservice.repository;

import lombok.RequiredArgsConstructor;
import org.example.monitoringservice.exception.custom.DbException;
import org.example.monitoringservice.exception.custom.ReadingTypeAlreadyExistsException;
import org.example.monitoringservice.model.reading.ReadingType;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ReadingTypeRepositoryImpl implements ReadingTypeRepository {

    private final JdbcTemplate jdbcTemplate;
    /**
     * Saves a new reading type to the database.
     * @param readingType The reading type to be saved
     * @throws DbException if an error occurs while saving the reading type to the database
     */
    @Override
    @Transactional
    public void saveNewReadingType(ReadingType readingType) {
        String sql = "INSERT INTO monitoring_service_schema.available_reading " +
                "(id, type) " +
                "VALUES (NEXTVAL('jdbc_sequence'), ?)";
        try {
            jdbcTemplate.update(sql, readingType.getType());
        } catch (DuplicateKeyException e) {
            throw new ReadingTypeAlreadyExistsException(MessageFormat
                    .format("Тип показаний {0} уже существует в базе", readingType.getType()));
        }
    }

    /**
     * Finds the available reading by type from the database.
     * @param type The type of the reading to find
     * @return An Optional object containing the ReadingType if found
     */
    @Override
    public Optional<ReadingType> findAvailableReadingByType(String type) {
        String sql ="SELECT * FROM monitoring_service_schema.available_reading ar " +
                "WHERE ar.type = ?";
        Optional<ReadingType> availableReading = Optional.empty();
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql, type);
        if (rowSet.next()) {
            availableReading = Optional.of(new ReadingType(
                    rowSet.getString("type")));
        }
        return availableReading;
    }

    /**
     * Finds the available readings from the database.
     * @return A list of strings representing the available readings
     */
    @Override
    public List<String> findAvailableReadings() {
        String sql ="SELECT * FROM monitoring_service_schema.available_reading";
        List<String> availableReadings = new ArrayList<>();
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql);
        while (rowSet.next()) {
            availableReadings.add(rowSet.getString("type"));
        }
        return availableReadings;
    }
}
