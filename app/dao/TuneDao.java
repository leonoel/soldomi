package dao;

import models.*;
import java.sql.*;
import java.util.*;

public class TuneDao {
    public interface Tune {
	String name();
	java.util.Date lastModified();
	Integer systCount();
    }

    public static Tune get(final Long id) {
	return new DaoAction<Tune>() {
	    @Override public Tune doSql(Connection connection) throws SQLException {
		PreparedStatement prep = connection.prepareStatement(
								     "select t.name as tune_name, " +
								            "t.last_modified as tune_last_modified, " +
								            "(   select count(distinct id) " +
								                "from syst " +
								                "where tune_id=t.id)" +
								            "as syst_count " +
								     "from tune as t " +
								     "where t.id = ? ");
		prep.setLong(1, id);
		final ResultSet resultSet = prep.executeQuery();
		if(resultSet.next()) {
		    return new Tune() {
			private final String name = resultSet.getString("tune_name");
			private final java.util.Date lastModified = resultSet.getDate("tune_last_modified");
			private final Integer systCount = resultSet.getInt("syst_count");
			@Override public String name() { return name; }
			@Override public java.util.Date lastModified() { return lastModified; }
			@Override public Integer systCount() { return systCount; }
		    };
		} else {
		    throw new SQLException("Tune id not found");
		}
	    }
	}.execute();
    }


    public static void delete(final Long id) {
	new DaoAction<Object>() {
	    @Override public Object doSql(Connection connection) throws SQLException {
		PreparedStatement prep = connection.prepareStatement("delete tune where id = ?");
		prep.setLong(1, id);
		prep.executeUpdate();
		return null;
	    }
	}.execute();
    }

}
