package models;

import java.util.List;
import java.util.ArrayList;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;

import utils.DaoAction;
import utils.DaoAction.DaoException;

import models.Symbol.SymbolRole;

import org.apache.commons.math3.fraction.Fraction;

public class Segment {

    public Long id;
    public Symbol symbol;
    public Fraction duration;
    public Integer dotCount;
    public Tuplet tuplet;

    public Note note;

    public Segment() {
    }

    public static final DaoAction<Segment, Segment> insert = new DaoAction<Segment, Segment>() {
	@Override public Segment doSql(Connection connection, Segment segment) throws SQLException {
	    if (segment.tuplet != null && segment.tuplet.id == null) {
		Tuplet.insert.doSql(connection, segment.tuplet);
	    }
	    PreparedStatement stat = connection.prepareStatement("insert into segment (symbol_id, " +
   								                      "duration_n, " +
   								                      "duration_d, " +
								                      "dot_count, " +
								                      "tuplet_id ) " +
								 "values (?, " +
								         "?, " +
								         "?, " +
								         "?, " +
								         "?) ");
	    stat.setLong(1, segment.symbol.id);
	    stat.setLong(2, segment.duration.getNumerator());
	    stat.setLong(3, segment.duration.getDenominator());
	    stat.setInt(4, segment.dotCount);
	    stat.setLong(5, segment.tuplet == null ? 0L : segment.tuplet.id);
	    stat.executeUpdate();
	    ResultSet resultSet = stat.getGeneratedKeys();
	    if(!resultSet.next()) {
		throw new SQLException("Could not retrieve new segment id.");
	    }
	    segment.id = resultSet.getLong(1);
	    if (segment.note != null) {
		segment.note = Note.insert.doSql(connection, segment.note);
	    }
	    return segment;
	}
    };

    public static final DaoAction<Symbol, Segment> getFromSymbol = new DaoAction<Symbol, Segment>() {
	@Override public Segment doSql(Connection connection, final Symbol symbol) throws SQLException {
	    Segment segment = new Segment();
	    PreparedStatement stat = connection.prepareStatement("select id, duration_n, duration_d, dot_count " +
								 "from segment " +
								 "where symbol_id = ? ");
	    stat.setLong(1, symbol.id);
	    ResultSet resultSet = stat.executeQuery();
	    if(!resultSet.next()) {
		throw new SQLException("Could not retrieve segment info");
	    }
	    segment.id = resultSet.getLong("id");
	    segment.symbol = symbol;
	    segment.duration = new Fraction(resultSet.getInt("duration_n"), resultSet.getInt("duration_d"));
	    segment.dotCount = resultSet.getInt("dot_count");

	    if (symbol.symbolType.role == SymbolRole.NOTE) {
		segment.note = Note.getFromSegment.doSql(connection, segment);
	    }

	    return segment;
	}
    };


}
