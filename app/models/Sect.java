package models;

import java.util.*;
import java.sql.*;

import utils.DaoAction;

public class Sect {
    public Long id;
    public Tune tune;
    public Long startTime;
    public final List<Block> blocks = new ArrayList<Block>();

    public static Sect makeBlank(Tune tune, Long startTime) {
	Sect sect = new Sect();
	sect.tune = tune;
	sect.startTime = startTime;
	sect.blocks.add(Block.makeBlank(sect, startTime));
//	sect.blocks.add(Block.createNewBlock(sect, startTime));
	return sect;
    }

    public static Sect createNewSect(Tune tune, Long startTime) {
	Sect sect = makeBlank(tune,startTime);
	insert.execute(sect);
	return sect;
    }

    public static final DaoAction<Sect, Sect> insert = new DaoAction<Sect, Sect>() {
	@Override public Sect doSql(Connection connection,
				    Sect sect) throws SQLException {
	    PreparedStatement stat = connection.prepareStatement("insert into sect values (null, ?, ?)");
	    stat.setLong(1, sect.tune.id);
	    stat.setLong(2, sect.startTime);
	    stat.executeUpdate();
	    ResultSet resultSet = stat.getGeneratedKeys();
	    if (!resultSet.next()) {
		throw new SQLException("Could not retrieve new sect id");
	    }
	    sect.id = resultSet.getLong(1);
	    for (Block block : sect.blocks) {
		Block.insert.doSql(connection, block);
	    }
	    return sect;
	}
    };
    
    public static final DaoAction<Tune, List<Sect>> getAllInTune = new DaoAction<Tune, List<Sect>>() {
	@Override public List<Sect> doSql(Connection connection,
					  Tune tune) throws SQLException {
	    List<Sect> sects = new ArrayList<Sect>();
	    PreparedStatement stat = connection.prepareStatement(
								 "select id, " +
								 "start_time " +
								 "from sect " +
								 "where tune_id = ? ");
	    stat.setLong(1, tune.id);
	    ResultSet resultSet = stat.executeQuery();
		    
	    while (resultSet.next()) {
		Sect sect = new Sect();
		sect.id = resultSet.getLong("id");
		sect.tune = tune;
		sect.startTime = resultSet.getLong("start_time");
		sect.blocks.addAll(Block.getAllInSect.doSql(connection, sect));
		sects.add(sect);
	    }
	    return sects;
	}
    };
    
    public static final DaoAction<Long, Object> deleteAllInTune = new DaoAction<Long, Object>() {
	@Override public Object doSql(Connection connection,
				      Long tuneId) throws SQLException {
	    Block.deleteAllInTune.doSql(connection, tuneId);
	    PreparedStatement stat = connection.prepareStatement("delete from sect " +
								 "where tune_id = ?");
	    stat.setLong(1, tuneId);
	    stat.executeUpdate();
	    return null;
	}
    };

}


