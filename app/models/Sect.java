package models;

import java.util.*;
import java.sql.*;

public interface Sect {
    Long id();
    Long startTime();
    List<Block> blocks();

    class Blank implements Sect {
	private final Long startTime;
	private final List<Block> blocks = new ArrayList<Block>();
	public Blank(Long startTime) {
	    this.startTime = startTime;
	    this.blocks.add(new Block.Blank(startTime));
	}
	@Override public Long id() { return null; }
	@Override public Long startTime() { return startTime; }
	@Override public List<Block> blocks() { return blocks; }
    }

    class Base {
	public static Long insert(Connection connection,
				  Long tuneId,
				  Long startTime) throws SQLException {
	    PreparedStatement stat = connection.prepareStatement("insert into sect values (null, ?, ?)");
	    stat.setLong(1, tuneId);
	    stat.setLong(2, startTime);
	    stat.executeUpdate();
	    ResultSet resultSet = stat.getGeneratedKeys();
	    if (resultSet.next()) {
		return resultSet.getLong(1);
	    } else {
		throw new SQLException("Could not retrieve new sect id");
	    }
	}

	public static List<Sect> getAllInTune(Connection connection,
					      Long tuneId) throws SQLException {
	    List<Sect> sects = new ArrayList<Sect>();
	    PreparedStatement stat = connection.prepareStatement(
								 "select id, " +
								 "start_time " +
								 "from sect " +
								 "where tune_id = ? ");
	    stat.setLong(1, tuneId);
	    ResultSet resultSet = stat.executeQuery();
		    
	    while (resultSet.next()) {
		final Long sectId = resultSet.getLong("id");
		final Long startTime = resultSet.getLong("start_time");
		final List<Block> blocks = Block.Base.getAllInSect(connection, sectId);
		sects.add(new Sect() {
			@Override public Long id() { return sectId; }
			@Override public Long startTime() { return startTime; }
			@Override public List<Block> blocks() { return blocks; }
		    });
	    }
	    return sects;
	}

	public static void deleteAllInTune(Connection connection,
					   Long tuneId) throws SQLException {
	    Block.Base.deleteAllInTune(connection, tuneId);
	    PreparedStatement stat = connection.prepareStatement("delete from sect " +
								 "where tune_id = ?");
	    stat.setLong(1, tuneId);
	    stat.executeUpdate();
	}
    }

}


