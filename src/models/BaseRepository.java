package models;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public abstract class BaseRepository<T extends Model<ID>, ID> {

    protected final Connection conn;

    // Subclasses provide table name and column mappings
    protected abstract String getTableName();
    protected abstract String getIdColumn();
    protected abstract Function<ResultSet, T> getMapper();
    protected abstract void setInsertParams(PreparedStatement ps, T entity) throws SQLException;
    protected abstract void setUpdateParams(PreparedStatement ps, T entity) throws SQLException;

    public BaseRepository(Connection conn) {
        this.conn = conn;
    }

    // CREATE
    public T insert(T entity) throws SQLException {
        String columns = getInsertColumns();
        String placeholders = getInsertPlaceholders();
        String sql = "INSERT INTO " + getTableName() + " (" + columns + ") VALUES (" + placeholders + ")";
        
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            setInsertParams(ps, entity);
            ps.executeUpdate();
            
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    @SuppressWarnings("unchecked")
                    ID id = (ID) keys.getObject(1);
                    entity.setId(id);
                }
            }
        }
        return entity;
    }

    // READ BY ID
    public Optional<T> findById(ID id) throws SQLException {
        String sql = "SELECT * FROM " + getTableName() + " WHERE " + getIdColumn() + " = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setObject(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(getMapper().apply(rs));
                }
            }
        }
        return Optional.empty();
    }

    // READ ALL
    public List<T> findAll() throws SQLException {
        List<T> results = new ArrayList<>();
        String sql = "SELECT * FROM " + getTableName();
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                results.add(getMapper().apply(rs));
            }
        }
        return results;
    }

    // UPDATE
    public void update(T entity) throws SQLException {
        String sql = "UPDATE " + getTableName() + " SET " + getUpdateColumns() + 
                     " WHERE " + getIdColumn() + " = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            setUpdateParams(ps, entity);
            ps.setObject(getUpdateParamCount() + 1, entity.getId());
            ps.executeUpdate();
        }
    }

    // DELETE
    public void delete(ID id) throws SQLException {
        String sql = "DELETE FROM " + getTableName() + " WHERE " + getIdColumn() + " = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setObject(1, id);
            ps.executeUpdate();
        }
    }

    // Helper methods (overridden in subclasses if needed)
    protected abstract String getInsertColumns();
    protected abstract String getInsertPlaceholders();
    protected abstract String getUpdateColumns();
    protected abstract int getUpdateParamCount();
}
