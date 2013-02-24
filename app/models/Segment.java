package models;

import java.util.*;
import java.sql.*;

import utils.DurationSymbol;
import utils.Pitch;

public interface Segment {

    Long id();
    Symbol symbol();
    DurationSymbol durationSymbol();
    Integer dotCount();
    Pitch pitch();

    class Base {
	public static Long insert(Connection connection, Segment segment) throws SQLException {
	    Long id;
	    Long symbolId = Symbol.Base.insert(connection, segment.symbol());
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
	    stat.setString(2, segment.durationSymbol().baseValue);
	    stat.setInt(3, segment.dotCount());
	    stat.setString(4, segment.pitch().note.baseValue);
	    stat.setInt(5, segment.pitch().octave);
	    stat.executeUpdate();
	    ResultSet resultSet = stat.getGeneratedKeys();
	    if(resultSet.next()) {
		id = resultSet.getLong(1);
	    } else {
		throw new SQLException("Could not retrieve new segment id.");
	    }

	    return id;
	}

    }
}
