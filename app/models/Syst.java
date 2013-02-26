package models;

import java.util.*;
import java.sql.*;

import utils.DaoAction;

public class Syst {
    public Long id;
    public Tune tune;
    public String name;
    public final List<Staff> staffs = new ArrayList<Staff>();

    public static final DaoAction<Syst, Syst> insert = new DaoAction<Syst, Syst>() {
	@Override public Syst doSql(Connection connection,
				    Syst syst) throws SQLException {
	    PreparedStatement stat = connection.prepareStatement("insert into syst values (null, ?, ?)");
	    stat.setLong(1, syst.tune.id);
	    stat.setString(2, syst.name);
	    stat.executeUpdate();
	    ResultSet resultSet = stat.getGeneratedKeys();
	    if (!resultSet.next()) {
		throw new SQLException("Could not retrieve new syst id");
	    }
	    syst.id = resultSet.getLong(1);
	    for (Staff staff : syst.staffs) {
		Staff.insert.doSql(connection, staff);
	    }
	    return syst;
	}
    };

    public static final DaoAction<Tune, List<Syst>> getAllInTune = new DaoAction<Tune, List<Syst>>() {
	@Override public List<Syst> doSql(Connection connection,
					  Tune tune) throws SQLException {
	    List<Syst> systs = new ArrayList<Syst>();
	    PreparedStatement stat = connection.prepareStatement(
								 "select id, " +
								 "name " +
								 "from syst " +
								 "where tune_id = ? ");
	    stat.setLong(1, tune.id);
	    ResultSet resultSet = stat.executeQuery();
		    
	    while (resultSet.next()) {
		Syst syst = new Syst();
		syst.id = resultSet.getLong("id");
		syst.name = resultSet.getString("name");
		syst.tune = tune;
		syst.staffs.addAll(Staff.getAllInSyst.doSql(connection, syst));
		systs.add(syst);
	    }
	    return systs;
	}
    };

    public static final DaoAction<Long, Object> deleteAllInTune = new DaoAction<Long, Object>() {
	@Override public Object doSql(Connection connection,
				      Long tuneId) throws SQLException {
	    Staff.deleteAllInTune.doSql(connection, tuneId);
	    PreparedStatement stat = connection.prepareStatement("delete from syst " +
								 "where tune_id = ?");
	    stat.setLong(1, tuneId);
	    stat.executeUpdate();
	    return null;
	}
    };


}


