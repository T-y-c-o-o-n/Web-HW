package ru.itmo.wp.model.repository.impl;

import ru.itmo.wp.model.domain.Event;
import ru.itmo.wp.model.domain.User;
import ru.itmo.wp.model.exception.RepositoryException;
import ru.itmo.wp.model.repository.EventRepository;

import java.sql.*;

public class EventRepositoryImpl extends BasicRepositoryImpl<Event> implements EventRepository {
    public EventRepositoryImpl() {
        super("Event");
    }

    public void save(Event event) {
        try (Connection connection = DATA_SOURCE.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("INSERT INTO `Event` " +
                    "(`userId`, `type`, `creationTime`) VALUES (?, ?, NOW())", Statement.RETURN_GENERATED_KEYS)) {
                statement.setLong(1, event.getUserId());
                statement.setString(2, event.getType().toString());
                if (statement.executeUpdate() != 1) {
                    throw new RepositoryException("Can't save Event.");
                } else {
                    ResultSet generatedKeys = statement.getGeneratedKeys();
                    if (generatedKeys.next()) {
                        event.setId(generatedKeys.getLong(1));
                        event.setCreationTime(find(event.getId()).getCreationTime());
                    } else {
                        throw new RepositoryException("Can't save Event [no autogenerated fields].");
                    }
                }
            }
        } catch (SQLException e) {
            throw new RepositoryException("Can't save Event.", e);
        }
    }

    @Override
    protected Event getNewInstance() {
        return new Event();
    }
    @Override
    protected Event toModel(ResultSetMetaData metaData, ResultSet resultSet) throws SQLException {
        if (!resultSet.next()) {
            return null;
        }

        Event event = new Event();
        for (int i = 1; i <= metaData.getColumnCount(); i++) {
            switch (metaData.getColumnName(i)) {
                case "id":
                    event.setId(resultSet.getLong(i));
                    break;
                case "userId":
                    event.setUserId(resultSet.getLong(i));
                    break;
                case "type":
                    event.setType(Event.Type.getType(resultSet.getString(i)));
                    break;
                case "creationTime":
                    event.setCreationTime(resultSet.getTimestamp(i));
                    break;
                default:
                    // No operations.
            }
        }

        return event;
    }
}
