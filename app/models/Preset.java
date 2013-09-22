package models;

import java.util.*;
import java.sql.*;


public class Preset {
    public Long id;
    public String name;
    public String js;

/*    
    public static final DaoAction<Object, List<Preset>> getAll = new DaoAction<Object, List<Preset>>() {
	@Override public List<Preset> doSql(Connection connection, Object object) throws SQLException {
	    List<Preset> presets = new ArrayList<Preset>();
	    Statement stat = connection.createStatement();
	    ResultSet resultSet = stat.executeQuery("select id, name from preset");
	    while(resultSet.next()) {
		Preset preset = new Preset();
		preset.id = resultSet.getLong("id");
		preset.name = resultSet.getString("name");
		presets.add(preset);
	    }
	    return presets;
	}
    };

    public static final DaoAction<Long, Preset> get = new DaoAction<Long, Preset>() {
	@Override public Preset doSql(Connection connection, Long id) throws SQLException {
	    PreparedStatement stat = connection.prepareStatement("select name, js from preset where id = ?");
	    stat.setLong(1, id);
	    ResultSet resultSet = stat.executeQuery();
	    if (!resultSet.next()) {
		throw new SQLException("Preset id not found.");
	    }
	    Preset preset = new Preset();
	    preset.id = id;
	    preset.name = resultSet.getString("name");
	    preset.js = resultSet.getString("js");
	    return preset;
	}
    };

    public static final DaoAction<Preset, Preset> insert = new DaoAction<Preset, Preset>() {
	@Override public Preset doSql(Connection connection, Preset preset) throws SQLException {
	    PreparedStatement stat = connection.prepareStatement("insert into preset " +
								 "values (null, ?, ?)");
	    stat.setString(1, preset.name);
	    stat.setString(2, preset.js);
	    stat.executeUpdate();
	    ResultSet resultSet = stat.getGeneratedKeys();
	    if (!resultSet.next()) {
		throw new SQLException("Could not retrieve new preset id.");
	    }
	    preset.id = resultSet.getLong(1);
	    return preset;
	}
    };

    public static final DaoAction<Long, Object> delete = new DaoAction<Long, Object>() {
	@Override public Object doSql(Connection connection, Long id) throws SQLException {
	    PreparedStatement stat = connection.prepareStatement("delete from preset where id = ?");
	    stat.setLong(1, id);
	    stat.executeUpdate();
	    return null;
	}
    };
*/
}
