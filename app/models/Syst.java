package models;

import java.util.*;
import java.sql.*;

public interface Syst {
    Long id();
    String name();
    List<Staff> staffs();

    public static class Base {
	public static Long insert(Connection connection,
				  Long tuneId,
				  String name) throws SQLException {
	    PreparedStatement stat = connection.prepareStatement("insert into syst values (null, ?, ?)");
	    stat.setLong(1, tuneId);
	    stat.setString(2, name);
	    stat.executeUpdate();
	    ResultSet resultSet = stat.getGeneratedKeys();
	    if (resultSet.next()) {
		return resultSet.getLong(1);
	    } else {
		throw new SQLException("Could not retrieve new syst id");
	    }
	}

	public static List<Syst> getAllInTune(Connection connection,
					      Long tuneId) throws SQLException {
	    List<Syst> systs = new ArrayList<Syst>();
	    PreparedStatement stat = connection.prepareStatement(
								     "select id, " +
								     "name " +
								     "from syst " +
								     "where tune_id = ? ");
	    stat.setLong(1, tuneId);
	    ResultSet resultSet = stat.executeQuery();
		    
	    while (resultSet.next()) {
		final Long systId = resultSet.getLong("id");
		final String systName = resultSet.getString("name");
		final List<Staff> staffs = Staff.Base.getAllInSyst(connection, systId);
		systs.add(new Syst() {
			@Override public Long id() { return systId; }
			@Override public String name() { return systName; }
			@Override public List<Staff> staffs() { return staffs; }
		    });
	    }
	    return systs;
	}

	public static void deleteAllInTune(Connection connection,
					   Long tuneId) throws SQLException {
	    Staff.Base.deleteAllInTune(connection, tuneId);
	    PreparedStatement stat = connection.prepareStatement("delete from syst " +
								 "where tune_id = ?");
	    stat.setLong(1, tuneId);
	    stat.executeUpdate();
	}
    }


}


