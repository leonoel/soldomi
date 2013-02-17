package dao;

import java.util.*;
import java.sql.*;

public final class NewTuneDao {
    public interface Block {
	Long startTime();
    }

    public interface Syst {
	
    }
    
    public interface Tune {
	String name();
	List<Syst> systs();
	List<Block> blocks();
    }

    public static Long create(final Tune tune) {
	return new DaoAction<Long>() {
	    @Override public Long doSql(Connection connection) throws SQLException {
		Long id = null;
		PreparedStatement tuneInsert = connection.prepareStatement("insert into tune values (null, ?, ?)");
		tuneInsert.setString(1, tune.name());
		tuneInsert.setDate(2, new java.sql.Date(System.currentTimeMillis()));
		tuneInsert.executeUpdate();
		ResultSet resultSet = tuneInsert.getGeneratedKeys();
		if(resultSet.next()) {
		    id = resultSet.getLong(1);
		} else {
		    throw new SQLException("Could not retrieve new tune id.");
		}
		for (Syst syst : tune.systs()) {
		    PreparedStatement systInsert = connection.prepareStatement("insert into syst values (null, ?)");
		    systInsert.setLong(1, id);
		    systInsert.executeUpdate();
		}
		for (Block block : tune.blocks()) {
		    PreparedStatement blockInsert = connection.prepareStatement("insert into block values (null, ?, ?");
		    blockInsert.setLong(1, id);
		    blockInsert.setLong(2, block.startTime());
		    blockInsert.executeUpdate();
		}
		return id;
	    }
	}.execute();
    }

}
