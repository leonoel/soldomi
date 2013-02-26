package models;

import java.util.*;
import java.sql.*;

import utils.DaoAction;

public class Symbol {

    public class Position {
	Long staffId;
	Long blockId;
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

    public Long id;
    public Position position;
    public Long startTime;
    public SymbolType symbolType;

    public static final DaoAction<Position, List<Symbol>> getAll = new DaoAction<Position, List<Symbol>>() {
	@Override public List<Symbol> doSql(Connection connection, final Position position) throws SQLException {
	    List<Symbol> symbols = new ArrayList<Symbol>();
	    PreparedStatement stat = connection.prepareStatement("select id, start_time, symbol_type " +
								 "from symbol " +
								 "where staff_id = ? " +
								 "and block_id = ? ");
	    stat.setLong(1, position.staffId);
	    stat.setLong(2, position.blockId);
	    ResultSet resultSet = stat.executeQuery();
	    while(resultSet.next()) {
		Symbol symbol = new Symbol();
		symbol.id = resultSet.getLong("id");
		symbol.position = position;
		symbol.startTime = resultSet.getLong("start_time");
		symbol.symbolType = SymbolType.fromBaseValue(resultSet.getString("symbol_type"));
		symbols.add(symbol);
	    }
	    return symbols;
	}
    };

    public static final DaoAction<Symbol, Symbol> insert = new DaoAction<Symbol, Symbol>() {
	@Override public Symbol doSql(Connection connection, Symbol symbol) throws SQLException {
	    PreparedStatement stat = connection.prepareStatement("insert into symbol ('id', 'block_id', 'staff_id', 'start_time', 'symbol_type') " +
								 "values (null, ?, ?, ?, ?)");
	    stat.setLong(1, symbol.position.blockId);
	    stat.setLong(2, symbol.position.staffId);
	    stat.setLong(3, symbol.startTime);
	    stat.setString(4, symbol.symbolType.baseValue);
	    stat.executeUpdate();
	    ResultSet resultSet = stat.getGeneratedKeys();
	    if(!resultSet.next()) {
		throw new SQLException("Could not retrieve new symbol id.");
	    }
	    symbol.id = resultSet.getLong(1);
	    return symbol;
	}
    };

    public static final DaoAction<Long, Object> deleteAllInTune = new DaoAction<Long, Object>() {
	@Override public Object doSql(Connection connection, Long tuneId) throws SQLException {
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
	    return null;
	}
    };
}
