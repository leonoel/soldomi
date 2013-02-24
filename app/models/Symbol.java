package models;

import java.util.*;
import java.sql.*;

public interface Symbol {

    public interface Position {
	Long staffId();
	Long blockId();
    }

    public enum SymbolType {
	NOTE("note");
	public String baseValue;
	private SymbolType(String baseValue) {
	    this.baseValue = baseValue;
	}
	public static SymbolType fromBaseValue(String baseValue) {
	    for (SymbolType type : SymbolType.values()) {
		if (type.baseValue.equals(baseValue)) {
		    return type;
		}
	    }
	    return null;
	}
    }

    Long id();
    Position position();
    Long startTime();
    SymbolType symbolType();

    class Base {
	public static List<Symbol> getAll(Connection connection, final Position position) throws SQLException {
	    List<Symbol> symbols = new ArrayList<Symbol>();
	    PreparedStatement stat = connection.prepareStatement("select id, start_time, symbol_type " +
								 "from symbol " +
								 "where staff_id = ? " +
								 "and block_id = ? ");
	    stat.setLong(1, position.staffId());
	    stat.setLong(2, position.blockId());
	    ResultSet resultSet = stat.executeQuery();
	    while(resultSet.next()) {
		final Long id = resultSet.getLong("id");
		final Long startTime = resultSet.getLong("start_time");
		final SymbolType symbolType = SymbolType.fromBaseValue(resultSet.getString("symbol_type"));
		symbols.add(new Symbol() {
			@Override public Long id() { return id; }
			@Override public Position position() { return position; }
			@Override public Long startTime() { return startTime; }
			@Override public SymbolType symbolType() { return symbolType; }
		    });
	    }
	    return symbols;
	}

	public static Long insert(Connection connection, Symbol symbol) throws SQLException {
	    Long id;
	    PreparedStatement stat = connection.prepareStatement("insert into symbol ('id', 'block_id', 'staff_id', 'start_time', 'symbol_type') " +
								 "values (null, ?, ?, ?, ?)");
	    stat.setLong(1, symbol.position().blockId());
	    stat.setLong(2, symbol.position().staffId());
	    stat.setLong(3, symbol.startTime());
	    stat.setString(4, symbol.symbolType().baseValue);
	    stat.executeUpdate();
	    ResultSet resultSet = stat.getGeneratedKeys();
	    if(resultSet.next()) {
		id = resultSet.getLong(1);
	    } else {
		throw new SQLException("Could not retrieve new symbol id.");
	    }
	    return id;
	}

	public static void deleteAllInTune(Connection connection, Long tuneId) throws SQLException {
	    PreparedStatement stat = connection.prepareStatement("delete from symbol " +
								 "where exists ( " +
								 "select * from staff " +
								 "join syst on syst.id = staff.syst_id " +
								 "where syst.tune_id = ?)" +
								 "or exists ( " +
								 "select * from block " +
								 "join sect on sect.id = block.sect_id " +
								 "where sect.tune_id = ?)");
	    stat.setLong(1, tuneId);
	    stat.setLong(2, tuneId);
	    stat.executeUpdate();
	}
    }
}
