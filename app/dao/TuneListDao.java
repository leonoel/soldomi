package dao;

import java.util.*;
import java.sql.*;

public final class TuneListDao {

    public interface Tune {
	Long id();
	String name();
	java.util.Date lastModified();
    }

    public static List<Tune> getAll() {
	return new DaoAction<List<Tune>>() {
	    @Override public List<Tune> doSql(Connection connection) throws SQLException {
		List<Tune> tunes = new ArrayList<Tune>();
		Statement statement = connection.createStatement();
		final ResultSet resultSet = statement.executeQuery("select id, name, last_modified from tune");
		while(resultSet.next()) {
		    tunes.add(new Tune() {
			    private final Long id = resultSet.getLong("id");
			    private final String name = resultSet.getString("name");
			    private final java.util.Date lastModified = resultSet.getDate("last_modified");
			    @Override public Long id() { return id; }
			    @Override public String name() { return name; }
			    @Override public java.util.Date lastModified() { return lastModified; }
			});
		}
		return tunes;
	    }
	}.execute();
    }

}
