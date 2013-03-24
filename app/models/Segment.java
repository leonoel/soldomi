package models;

import java.util.*;
import java.sql.*;
import org.apache.commons.math3.fraction.Fraction;

import utils.DaoAction;
import utils.DurationSymbol;
import utils.Pitch;

public class Segment {

    public Long id;
    public Symbol symbol;
    public DurationSymbol durationSymbol;
    public Fraction startTime;
    public Fraction duration;
    public Integer dotCount;
    public Pitch pitch;
    public Boolean rest;
    public Tuplet tuplet;

    public static final DaoAction<Segment, Segment> insert = new DaoAction<Segment, Segment>() {
	@Override public Segment doSql(Connection connection, Segment segment) throws SQLException {
	    Long symbolId = Symbol.insert.doSql(connection, segment.symbol).id;
	    PreparedStatement stat = connection.prepareStatement("insert into segment ('id', " +
   								                      "'symbol_id', " +
								                      "'duration_type', " +
								                      "'dot_count', " +
								                      "'pitch_note', " +
								                      "'pitch_octave' ) " +
								 "values (null, " +
								         "?, " +
								         "?, " +
								         "?, " +
								         "?, " +
								         "?) ");
	    stat.setLong(1, symbolId);
	    stat.setString(2, segment.durationSymbol.baseValue);
	    stat.setInt(3, segment.dotCount);
	    stat.setString(4, segment.pitch.note.baseValue);
	    stat.setInt(5, segment.pitch.octave);
	    stat.executeUpdate();
	    ResultSet resultSet = stat.getGeneratedKeys();
	    if(!resultSet.next()) {
		throw new SQLException("Could not retrieve new segment id.");
	    }
	    segment.id = resultSet.getLong(1);
	    return segment;
	}
    };
}
