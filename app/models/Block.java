package models;

import java.util.*;
import java.sql.*;

import utils.DaoAction;

public class Block {
    public Long id;
    public Sect sect;
    public Long startTime;

    public static Block makeBlank(Sect sect, Long startTime) {
	Block block = new Block();
	block.sect = sect;
	block.startTime = startTime;
	return block;
    }

    public static final DaoAction<Block, Block> insert = new DaoAction<Block, Block> () {
	@Override public Block doSql(Connection connection,
				     Block block) throws SQLException {
	    PreparedStatement stat = connection.prepareStatement("insert into block values (null, ?, ?)");
	    stat.setLong(1, block.sect.id);
	    stat.setLong(2, block.startTime);
	    stat.executeUpdate();
	    ResultSet resultSet = stat.getGeneratedKeys();
	    if (!resultSet.next()) {
		throw new SQLException("Could not retrieve new block id");
	    }
	    block.id = resultSet.getLong(1);
	    return block;
	}
    };

    public static final DaoAction<Sect, List<Block>> getAllInSect = new DaoAction<Sect, List<Block>>() {
	@Override public List<Block> doSql(Connection connection,
					   Sect sect) throws SQLException {
	    List<Block> blocks = new ArrayList<Block>();
	    PreparedStatement stat = connection.prepareStatement(
								 "select id, " +
								 "start_time " +
								 "from block " +
								 "where sect_id = ? ");
	    stat.setLong(1, sect.id);
	    ResultSet resultSet = stat.executeQuery();
		    
	    while (resultSet.next()) {
		Block block = new Block();
		block.id = resultSet.getLong("id");
		block.sect = sect;
		block.startTime = resultSet.getLong("start_time");
		blocks.add(block);
	    }
	    return blocks;
	}
    };

    public static final DaoAction<Long, Object> deleteAllInTune = new DaoAction<Long, Object>() {
	@Override public Object doSql(Connection connection,
				      Long tuneId) throws SQLException {
	    Symbol.deleteAllInTune.doSql(connection, tuneId);
	    PreparedStatement stat = connection.prepareStatement("delete from block " +
								 "where exists ( " +
								 "select * from sect " +
								 "where block.sect_id = sect.id " +
								 "and sect.tune_id = ?)");
	    stat.setLong(1, tuneId);
	    stat.executeUpdate();
	    return null;
	}
    };


}


