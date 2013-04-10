package models;

import java.util.List;
import java.util.ArrayList;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;

import utils.DaoAction;
import utils.DaoAction.DaoException;

public class Syst {
    public Long id;
    public Tune tune;
    public String name;
    public final List<Staff> staves = new ArrayList<Staff>();

    public Syst() {

    }

    public Syst(Tune _tune, String _name) {
	tune = _tune;
	name = _name;
    }
    
    public static final Syst createNewSyst(Tune tune, String name) throws DaoException {
      Syst syst = makeBlank(tune,name);
      insert.execute(syst);
      return syst;
    }

    public static final Syst makeBlank(Tune tune, String name) {
	Syst syst = new Syst(tune, name);
	syst.staves.add(Staff.makeBlank(syst,name));
//      syst.staves.add(Staff.createNewStaff(syst,name));
//      syst.staves.insert();
	return syst;
    }

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
	    for (Staff staff : syst.staves) {
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
		syst.staves.addAll(Staff.getAllInSyst.doSql(connection, syst));
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


