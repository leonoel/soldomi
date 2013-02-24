package models;

import java.util.*;
import java.sql.*;

public interface Staff {
    Long id();
    String name();

    public static class Base {
	public static Long insert(Connection connection,
				  Long systId,
				  String name) throws SQLException {
	    PreparedStatement stat = connection.prepareStatement("insert into staff values (null, ?, ?)");
	    stat.setLong(1, systId);
	    stat.setString(2, name);
	    stat.executeUpdate();
	    ResultSet resultSet = stat.getGeneratedKeys();
	    if (resultSet.next()) {
		return resultSet.getLong(1);
	    } else {
		throw new SQLException("Could not retrieve new staff id");
	    }
	}

	public static List<Staff> getAllInSyst(Connection connection,
					       Long systId) throws SQLException {
	    List<Staff> staffs = new ArrayList<Staff>();
	    PreparedStatement stat = connection.prepareStatement(
								 "select id, " +
								 "name " +
								 "from staff " +
								 "where syst_id = ? ");
	    stat.setLong(1, systId);
	    ResultSet resultSet = stat.executeQuery();

	    while (resultSet.next()) {
		final Long staffId = resultSet.getLong("id");
		final String staffName = resultSet.getString("name");
		staffs.add(new Staff() {
			@Override public Long id() { return staffId; }
			@Override public String name() { return staffName; }
		    });
	    }
	    return staffs;
	}

	public static void deleteAllInTune(Connection connection,
					   Long tuneId) throws SQLException {
	    PreparedStatement stat = connection.prepareStatement("delete from staff " +
								 "where exists ( " +
								 "select * from syst " +
								 "where staff.syst_id = syst.id " +
								 "and syst.tune_id = ?)");
	    stat.setLong(1, tuneId);
	    stat.executeUpdate();
	}
    }

}

