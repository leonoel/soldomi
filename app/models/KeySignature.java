package models;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;

import utils.DaoAction;

public class KeySignature {
    public enum NotePitch {
	NATURAL("natural"),
	SHARP("sharp"),
	FLAT("flat");

	public final String baseValue;
	
	private NotePitch(String baseValue) {
	    this.baseValue = baseValue;
	}

	public static NotePitch fromBaseValue(String baseValue) {
	    for (NotePitch notePitch : NotePitch.values()) {
		if (notePitch.baseValue.equals(baseValue)) {
		    return notePitch;
		}
	    }
	    return null;
	}

    }

    public Long id;
    public Symbol symbol;
    public NotePitch a;
    public NotePitch b;
    public NotePitch c;
    public NotePitch d;
    public NotePitch e;
    public NotePitch f;
    public NotePitch g;

    public static final DaoAction<KeySignature, KeySignature> insert = new DaoAction<KeySignature, KeySignature>() {
	@Override public KeySignature doSql(Connection connection, KeySignature keySignature) throws SQLException {
	    PreparedStatement stat = connection.prepareStatement("insert into key_signature (symbol_id, " +
								                             "a, " +
								                             "b, " +
								                             "c, " +
								                             "d, " +
								                             "e, " +
								                             "f, " +
								                             "g ) " +
								 "values (?, " +
								         "?, " +
								         "?, " +
								         "?, " +
								         "?, " +
								         "?, " +
								         "?, " +
								         "?) ");
	    stat.setLong(1, keySignature.symbol.id);
	    stat.setString(2, keySignature.a.baseValue);
	    stat.setString(3, keySignature.b.baseValue);
	    stat.setString(4, keySignature.c.baseValue);
	    stat.setString(5, keySignature.d.baseValue);
	    stat.setString(6, keySignature.e.baseValue);
	    stat.setString(7, keySignature.f.baseValue);
	    stat.setString(8, keySignature.g.baseValue);
	    stat.executeUpdate();
	    ResultSet resultSet = stat.getGeneratedKeys();
	    if(!resultSet.next()) {
		throw new SQLException("Could not retrieve new keySignature id.");
	    }
	    keySignature.id = resultSet.getLong(1);
	    return keySignature;
	}
    };

    public static final DaoAction<Symbol, KeySignature> getFromSymbol = new DaoAction<Symbol, KeySignature>() {
	@Override public KeySignature doSql(Connection connection, Symbol symbol) throws SQLException {
	    KeySignature keySignature = new KeySignature();
	    PreparedStatement stat = connection.prepareStatement("select id, " +
								 "a, " +
								 "b, " +
								 "c, " +
								 "d, " +
								 "e, " +
								 "f, " +
								 "g " +
								 "from key_signature " +
								 "where symbol_id = ? ");
	    stat.setLong(1, symbol.id);
	    ResultSet resultSet = stat.executeQuery();
	    if(!resultSet.next()) {
		throw new SQLException("Could not retrieve keySignature");
	    }
	    keySignature.id = resultSet.getLong("id");
	    keySignature.symbol = symbol;
	    keySignature.a = NotePitch.fromBaseValue(resultSet.getString("a"));
	    keySignature.b = NotePitch.fromBaseValue(resultSet.getString("b"));
	    keySignature.c = NotePitch.fromBaseValue(resultSet.getString("c"));
	    keySignature.d = NotePitch.fromBaseValue(resultSet.getString("d"));
	    keySignature.e = NotePitch.fromBaseValue(resultSet.getString("e"));
	    keySignature.f = NotePitch.fromBaseValue(resultSet.getString("f"));
	    keySignature.g = NotePitch.fromBaseValue(resultSet.getString("g"));

	    return keySignature;
	}
    };
    
}
