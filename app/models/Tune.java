package models;

import java.util.*;
import java.sql.*;
import utils.DaoAction;

public class Tune {
    public Long id;
    public String name;
    public java.util.Date lastModified;
    public final List<Syst> systs = new ArrayList<Syst>();
    public final List<Sect> sects = new ArrayList<Sect>();

    public static final DaoAction<Object, List<Tune>> getAll = new DaoAction<Object, List<Tune>>() {
	@Override public List<Tune> doSql(Connection connection, Object object) throws SQLException {
	    List<Tune> tunes = new ArrayList<Tune>();
	    Statement stat = connection.createStatement();
	    final ResultSet resultSet = stat.executeQuery("select id, name, last_modified from tune");
	    while(resultSet.next()) {
		Tune tune = new Tune();
		tune.id = resultSet.getLong("id");
		tune.name = resultSet.getString("name");
		tune.lastModified = resultSet.getDate("last_modified");
		tunes.add(tune);
	    }
	    return tunes;
	}
    };

    public static final DaoAction<Tune, Tune> insert = new DaoAction<Tune, Tune>() {
	@Override public Tune doSql(Connection connection, Tune tune) throws SQLException {
	    PreparedStatement stat = connection.prepareStatement("insert into tune values (null, ?, ?)");
	    stat.setString(1, tune.name);
	    stat.setDate(2, new java.sql.Date(tune.lastModified.getTime()));

	    stat.executeUpdate();
	    ResultSet resultSet = stat.getGeneratedKeys();
	    if(!resultSet.next()) {
		throw new SQLException("Could not retrieve new tune id.");
	    }
	    tune.id = resultSet.getLong(1);
	    for (Syst syst : tune.systs) {
		Syst.insert.doSql(connection, syst);
	    }
	    for (Sect sect : tune.sects) {
		Sect.insert.doSql(connection, sect);
	    }
	    return tune;
	}
    };

    public static final DaoAction<Long, Tune> get = new DaoAction<Long, Tune>() {
	@Override public Tune doSql(Connection connection, final Long id) throws SQLException {
	    PreparedStatement stat = connection.prepareStatement(
								 "select t.name as tune_name, " +
								 "t.last_modified as tune_last_modified " +
								 "from tune as t " +
								 "where t.id = ? ");
	    stat.setLong(1, id);
	    ResultSet resultSet = stat.executeQuery();
	    if(!resultSet.next()) {
		throw new SQLException("Tune id not found");
	    }

	    Tune tune = new Tune();
	    tune.id = id;
	    tune.name = resultSet.getString("tune_name");
	    tune.lastModified = resultSet.getDate("tune_last_modified");
	    tune.systs.addAll(Syst.getAllInTune.doSql(connection, tune));
	    tune.sects.addAll(Sect.getAllInTune.doSql(connection, tune));
	    return tune;
	}
    };


    public static final DaoAction<Long, Object> delete = new DaoAction<Long, Object>() {
	@Override public Object doSql(Connection connection, Long id) throws SQLException {
	    Sect.deleteAllInTune.doSql(connection, id);
	    Syst.deleteAllInTune.doSql(connection, id);
	    PreparedStatement stat = connection.prepareStatement("delete from tune " +
								 "where id = ?");
	    stat.setLong(1, id);
	    stat.executeUpdate();
	    return null;
	}
    };

    public static final Tune makeBlank(String name) {
	Tune tune = new Tune();
	tune.name = name;
	tune.lastModified = new java.util.Date();
	tune.sects.add(Sect.makeBlank(tune, 0L));
	return tune;
    }

}
