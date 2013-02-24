package models;

import java.util.*;
import java.sql.*;
import utils.DaoAction;

public interface Tune {
    Long id();
    String name();
    java.util.Date lastModified();
    List<Syst> systs();
    List<Sect> sects();

    DaoAction<Object, List<Tune>> getAll = new DaoAction<Object, List<Tune>>() {
	@Override public List<Tune> doSql(Connection connection, Object object) throws SQLException {
	    return Base.getAll(connection);
	}
    };

    DaoAction<Tune, Long> create = new DaoAction<Tune, Long>() {
	@Override public Long doSql(Connection connection, Tune tune) throws SQLException {
	    return Base.insert(connection, tune);
	}
    };

    DaoAction<Long, Tune> get = new DaoAction<Long, Tune>() {
	@Override public Tune doSql(Connection connection, Long id) throws SQLException {
	    return Base.get(connection, id);
	}
    };


    DaoAction<Long, Object> delete = new DaoAction<Long, Object>() {
	@Override public Object doSql(Connection connection, Long id) throws SQLException {
	    Base.delete(connection, id);
	    return null;
	}
    };

    class Blank implements Tune {
	private final String name;
	private final java.util.Date lastModified;
	private final List<Syst> systs = new ArrayList<Syst>();
	private final List<Sect> sects = new ArrayList<Sect>();
	public Blank(String name) {
	    this.name = name;
	    this.lastModified = new java.util.Date();
	    this.sects.add(new Sect.Blank(0L));
	}
	@Override public Long id() { return null; }
	@Override public String name() { return name; }
	@Override public java.util.Date lastModified() { return lastModified; }
	@Override public List<Syst> systs() { return systs; }
	@Override public List<Sect> sects() { return sects; }
    }

    class Base {
	public static Long insert(Connection connection,
				  Tune tune) throws SQLException {
	    Long tuneId;
	    PreparedStatement stat = connection.prepareStatement("insert into tune values (null, ?, ?)");
	    stat.setString(1, tune.name());
	    stat.setDate(2, new java.sql.Date(tune.lastModified().getTime()));
	    stat.executeUpdate();
	    ResultSet resultSet = stat.getGeneratedKeys();
	    if(resultSet.next()) {
		tuneId = resultSet.getLong(1);
	    } else {
		throw new SQLException("Could not retrieve new tune id.");
	    }
	    for (Syst syst : tune.systs()) {
		Long systId = Syst.Base.insert(connection, tuneId, syst.name());
		for (Staff staff : syst.staffs()) {
		    Staff.Base.insert(connection, systId, staff.name());
		}
	    }
	    for (Sect sect : tune.sects()) {
		Long sectId = Sect.Base.insert(connection, tuneId, sect.startTime());
		for (Block block : sect.blocks()) {
		    Block.Base.insert(connection, sectId, block.startTime());
		}
	    }
	    return tuneId;
	}

	public static List<Tune> getAll(Connection connection) throws SQLException {
	    List<Tune> tunes = new ArrayList<Tune>();
	    Statement stat = connection.createStatement();
	    final ResultSet resultSet = stat.executeQuery("select id, name, last_modified from tune");
	    while(resultSet.next()) {
		final Long id = resultSet.getLong("id");
		final String name = resultSet.getString("name");
		final java.util.Date lastModified = resultSet.getDate("last_modified");
		tunes.add(new Tune() {
			@Override public Long id() { return id; }
			@Override public String name() { return name; }
			@Override public java.util.Date lastModified() { return lastModified; }
			@Override public List<Syst> systs() { return null; }
			@Override public List<Sect> sects() { return null; }
		    });
	    }
	    return tunes;
	}

	public static Tune get(final Connection connection, final Long id) throws SQLException {
	    PreparedStatement stat = connection.prepareStatement(
								 "select t.name as tune_name, " +
								 "t.last_modified as tune_last_modified " +
								 "from tune as t " +
								 "where t.id = ? ");
	    stat.setLong(1, id);
	    ResultSet resultSet = stat.executeQuery();

	    if(resultSet.next()) {
		
		final String name = resultSet.getString("tune_name");
		final java.util.Date lastModified = resultSet.getDate("tune_last_modified");
		final List<Syst> systs = Syst.Base.getAllInTune(connection, id);
		final List<Sect> sects = Sect.Base.getAllInTune(connection, id);

		return new Tune() {
		    @Override public Long id() { return id; }
		    @Override public String name() { return name; }
		    @Override public java.util.Date lastModified() { return lastModified; }
		    @Override public List<Syst> systs() { return systs; }
		    @Override public List<Sect> sects() { return sects; }
		};
	    } else {
		throw new SQLException("Tune id not found");
	    }
	}

	public static void delete(Connection connection,
				  Long id) throws SQLException {
	    Sect.Base.deleteAllInTune(connection, id);
	    Syst.Base.deleteAllInTune(connection, id);
	    PreparedStatement stat = connection.prepareStatement("delete from tune " +
								 "where id = ?");
	    stat.setLong(1, id);
	    stat.executeUpdate();
	}
    }


}
