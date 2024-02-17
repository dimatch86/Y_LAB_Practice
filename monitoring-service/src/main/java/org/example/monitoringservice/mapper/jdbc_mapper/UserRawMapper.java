package org.example.monitoringservice.mapper.jdbc_mapper;
import org.example.monitoringservice.model.user.RoleType;
import org.example.monitoringservice.model.user.User;
import org.example.monitoringservice.util.CaseConverter;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.ZoneId;
import java.util.UUID;

public class UserRawMapper implements ResultSetExtractor<User> {
    @Override
    public User extractData(ResultSet rs) throws SQLException, DataAccessException {
        User user = null;
        if (rs.next()) {
            user = new User(
                    UUID.fromString(rs.getString(CaseConverter.camelCaseToSnakeCase(User.Fields.personalAccount))),
                    rs.getString(User.Fields.email),
                    rs.getString(User.Fields.password),
                    RoleType.valueOf(rs.getString(User.Fields.role)),
                    rs.getDate(CaseConverter.camelCaseToSnakeCase(User.Fields.registrationDate)).toLocalDate()
                            .atStartOfDay(ZoneId.systemDefault()).toInstant());
        }
        return user;
    }
}
