package models;

import java.util.List;
import java.util.ArrayList;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import utils.DaoAction;

public class Tuplet {
    public Long id;
    public List<Segment> segments = new ArrayList<Segment>();
    public Long duration;

    public static final DaoAction<Tuplet, Tuplet> insert = new DaoAction<Tuplet, Tuplet>() {
	@Override public Tuplet doSql(Connection connection, Tuplet tuplet) throws SQLException {
	    PreparedStatement stat = connection.prepareStatement("insert into tuplet (duration) " +
								 "values (?) ");
	    stat.setLong(1, tuplet.duration);
	    stat.executeUpdate();
	    ResultSet resultSet = stat.getGeneratedKeys();
	    if(!resultSet.next()) {
		throw new SQLException("Could not retrieve new tuplet id.");
	    }
	    tuplet.id = resultSet.getLong(1);
	    return tuplet;
	}
    };

}
