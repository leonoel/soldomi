package models;

import java.util.*;
import java.sql.*;

public interface Block {
    Long id();
    Long startTime();

    class Blank implements Block {
	private final Long startTime;
	public Blank(Long startTime) {
	    this.startTime = startTime;
	}
	@Override public Long id() { return null; }
	@Override public Long startTime() { return startTime; }
    }

    class Base {
	public static Long insert(Connection connection,
				  Long sectId,
				  Long startTime) throws SQLException {
	    PreparedStatement stat = connection.prepareStatement("insert into block values (null, ?, ?)");
	    stat.setLong(1, sectId);
	    stat.setLong(2, startTime);
	    stat.executeUpdate();
	    ResultSet resultSet = stat.getGeneratedKeys();
	    if (resultSet.next()) {
		return resultSet.getLong(1);
	    } else {
		throw new SQLException("Could not retrieve new block id");
	    }
	}

	public static List<Block> getAllInSect(Connection connection,
					       Long sectId) throws SQLException {
	    List<Block> blocks = new ArrayList<Block>();
	    PreparedStatement stat = connection.prepareStatement(
								 "select id, " +
								 "start_time " +
								 "from block " +
								 "where sect_id = ? ");
	    stat.setLong(1, sectId);
	    ResultSet resultSet = stat.executeQuery();
		    
	    while (resultSet.next()) {
		final Long blockId = resultSet.getLong("id");
		final Long startTime = resultSet.getLong("start_time");
		blocks.add(new Block() {
			@Override public Long id() { return blockId; }
			@Override public Long startTime() { return startTime; }
		    });
	    }
	    return blocks;
	}

	public static void deleteAllInTune(Connection connection,
					   Long tuneId) throws SQLException {
	    PreparedStatement stat = connection.prepareStatement("delete from block " +
								 "where exists ( " +
								 "select * from sect " +
								 "where block.sect_id = sect.id " +
								 "and sect.tune_id = ?)");
	    stat.setLong(1, tuneId);
	    stat.executeUpdate();
	}
    }


}


