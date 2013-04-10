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

public class Staff {
    public Long id;
    public Syst syst;
    public String name;
    public List<Symbol> symbols = new ArrayList<Symbol>();

    public Staff() {
    }

    public Staff(Long _id) {
	id = _id;
    }

    public Staff(Syst _syst, String _name) {
	syst = _syst;
	name = _name;
    }

    public static final Staff makeBlank(Syst syst, String name) {
	return new Staff(syst, name);
    }

    public static final Staff createNewStaff(Syst syst, String name) throws DaoException {
      Staff staff = new Staff(syst,name);
      insert.execute(staff);
      return staff;
    }

    public static final DaoAction<Staff, Staff> insert = new DaoAction<Staff, Staff>() {
	@Override public Staff doSql(Connection connection,
				     Staff staff) throws SQLException {
	    PreparedStatement stat = connection.prepareStatement("insert into staff values (null, ?, ?)");
	    stat.setLong(1, staff.syst.id);
	    stat.setString(2, staff.name);
	    stat.executeUpdate();
	    ResultSet resultSet = stat.getGeneratedKeys();
	    if (!resultSet.next()) {
		throw new SQLException("Could not retrieve new staff id");
	    }
	    staff.id = resultSet.getLong(1);
	    for (Symbol symbol : staff.symbols) {
		if (symbol.position.block.id != null) {
		    Symbol.insert.doSql(connection, symbol);
		}
	    }
	    return staff;
	}
    };

    public static final DaoAction<Syst, List<Staff>> getAllInSyst = new DaoAction<Syst, List<Staff>>() {
	@Override public List<Staff> doSql(Connection connection,
					   Syst syst) throws SQLException {
	    List<Staff> staffs = new ArrayList<Staff>();
	    PreparedStatement stat = connection.prepareStatement(
								 "select id, " +
								 "name " +
								 "from staff " +
								 "where syst_id = ? ");
	    stat.setLong(1, syst.id);
	    ResultSet resultSet = stat.executeQuery();

	    while (resultSet.next()) {
		Staff staff = new Staff();
		staff.id = resultSet.getLong("id");
		staff.name = resultSet.getString("name");
		staffs.add(staff);
	    }
	    return staffs;
	}
    };

    public static final DaoAction<Long, Object> deleteAllInTune = new DaoAction<Long, Object>() {
	@Override public Object doSql(Connection connection,
				      Long tuneId) throws SQLException {
	    Symbol.deleteAllInTune.doSql(connection, tuneId);
	    PreparedStatement stat = connection.prepareStatement("delete from staff " +
								 "where exists ( " +
								 "select * from syst " +
								 "where staff.syst_id = syst.id " +
								 "and syst.tune_id = ?)");
	    stat.setLong(1, tuneId);
	    stat.executeUpdate();
	    return null;
	}
    };

}

