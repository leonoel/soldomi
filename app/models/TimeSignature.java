package models;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;

import utils.DaoAction;

public class TimeSignature {
    public enum NoteValue {
	WHOLE("whole"),
	HALF("half"),
	QUARTER("quarter"),
	EIGHTH("eighth"),
	SIXTEENTH("sixteenth"),
	THIRTY_SECOND("thirty_second");

	public final String baseValue;
	
	private NoteValue(String baseValue) {
	    this.baseValue = baseValue;
	}

	public static NoteValue fromBaseValue(String baseValue) {
	    for (NoteValue noteValue : NoteValue.values()) {
		if (noteValue.baseValue.equals(baseValue)) {
		    return noteValue;
		}
	    }
	    return null;
	}

    }

    public Long id;
    public Symbol symbol;
    public Integer beatCount;
    public NoteValue beatValue;

    public static final DaoAction<TimeSignature, TimeSignature> insert = new DaoAction<TimeSignature, TimeSignature>() {
	@Override public TimeSignature doSql(Connection connection, TimeSignature timeSignature) throws SQLException {
	    PreparedStatement stat = connection.prepareStatement("insert into time_signature (symbol_id, " +
								                             "beat_count, " +
								                             "beat_value ) " +
								 "values (?, " +
								         "?, " +
								         "?) ");
	    stat.setLong(1, timeSignature.symbol.id);
	    stat.setInt(2, timeSignature.beatCount);
	    stat.setString(3, timeSignature.beatValue.baseValue);
	    stat.executeUpdate();
	    ResultSet resultSet = stat.getGeneratedKeys();
	    if(!resultSet.next()) {
		throw new SQLException("Could not retrieve new timeSignature id.");
	    }
	    timeSignature.id = resultSet.getLong(1);
	    return timeSignature;
	}
    };

    public static final DaoAction<Symbol, TimeSignature> getFromSymbol = new DaoAction<Symbol, TimeSignature>() {
	@Override public TimeSignature doSql(Connection connection, Symbol symbol) throws SQLException {
	    TimeSignature timeSignature = new TimeSignature();
	    PreparedStatement stat = connection.prepareStatement("select id, beat_count, beat_value " +
								 "from time_signature " +
								 "where symbol_id = ? ");
	    stat.setLong(1, symbol.id);
	    ResultSet resultSet = stat.executeQuery();
	    if(!resultSet.next()) {
		throw new SQLException("Could not retrieve timeSignature");
	    }
	    timeSignature.id = resultSet.getLong("id");
	    timeSignature.symbol = symbol;
	    timeSignature.beatCount = resultSet.getInt("beat_count");
	    timeSignature.beatValue = NoteValue.fromBaseValue(resultSet.getString("beat_value"));

	    return timeSignature;
	}
    };
    
}
