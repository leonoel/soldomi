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
import utils.EnumBaseMapper;

import org.apache.commons.math3.fraction.Fraction;

public class Symbol {

    public static class Position {
	public final Staff staff;
	public final Block block;
	public Position(Staff staff,
			Block block) {
	    this.staff = staff;
	    this.block = block;
	}
    }

    public enum SymbolRole {
	REST(true),
	NOTE(true),
	CLEF(false),
	KEY_SIGNATURE(false),
	TIME_SIGNATURE(false);
	public final boolean isSegment;
	private SymbolRole(boolean isSegment) {
	    this.isSegment = isSegment;
	}
    }

    public enum SymbolType {
	WHOLE(SymbolRole.NOTE),
	WHOLE_R(SymbolRole.REST),
	HALF(SymbolRole.NOTE),
	HALF_R(SymbolRole.REST),
	QUARTER(SymbolRole.NOTE),
	QUARTER_R(SymbolRole.REST),
	EIGHTH(SymbolRole.NOTE),
	EIGHTH_R(SymbolRole.REST),
	SIXTEENTH(SymbolRole.NOTE),
	SIXTEENTH_R(SymbolRole.REST),
	THIRTY_SECOND(SymbolRole.NOTE),
	THIRTY_SECOND_R(SymbolRole.REST),
	SIXTY_FOURTH(SymbolRole.NOTE),
	SIXTY_FOURTH_R(SymbolRole.REST),
	TREBLE_CLEF(SymbolRole.CLEF),
	BASS_CLEF(SymbolRole.CLEF),
	ALTO_CLEF(SymbolRole.CLEF),
	TENOR_CLEF(SymbolRole.CLEF),
	KEY_SIGNATURE(SymbolRole.KEY_SIGNATURE),
	STANDARD_TIME_SIGNATURE(SymbolRole.TIME_SIGNATURE),
	ALLA_BREVE(SymbolRole.TIME_SIGNATURE),
	COMMON_TIME(SymbolRole.TIME_SIGNATURE);

	public final SymbolRole role;

	private SymbolType(SymbolRole role) {
	    this.role = role;
	}
    }

    public Long id;
    public Position position;
    public Fraction startTime;
    public SymbolType symbolType;
    public Segment segment;
    public TimeSignature timeSignature;
    public KeySignature keySignature;

    public Symbol() {
    }

    private static final EnumBaseMapper<SymbolType> TYPE_MAPPER = new EnumBaseMapper<SymbolType>(SymbolType.class);

    public static final DaoAction<Position, List<Symbol>> getAll = new DaoAction<Position, List<Symbol>>() {
	@Override public List<Symbol> doSql(Connection connection, final Position position) throws SQLException {
	    List<Symbol> symbols = new ArrayList<Symbol>();
	    PreparedStatement stat = connection.prepareStatement("select id, start_time_n, start_time_d, symbol_type " +
								 "from symbol " +
								 "where staff_id = ? " +
								 "and block_id = ? ");
	    stat.setLong(1, position.staff.id);
	    stat.setLong(2, position.block.id);
	    ResultSet resultSet = stat.executeQuery();
	    while(resultSet.next()) {
		Symbol symbol = new Symbol();
		symbol.id = resultSet.getLong("id");
		symbol.position = position;
		symbol.startTime = new Fraction(resultSet.getInt("start_time_n"), resultSet.getInt("start_time_d"));
		symbol.symbolType = TYPE_MAPPER.fromBase(resultSet.getString("symbol_type"));
		symbols.add(symbol);
	    }
	    for (Symbol symbol : symbols) {
		if (symbol.symbolType.role.isSegment) {
		    symbol.segment = Segment.getFromSymbol.doSql(connection, symbol);
		} else if (SymbolType.STANDARD_TIME_SIGNATURE == symbol.symbolType) {
		    symbol.timeSignature = TimeSignature.getFromSymbol.doSql(connection, symbol);
		} else if (SymbolType.KEY_SIGNATURE == symbol.symbolType) {
		    symbol.keySignature = KeySignature.getFromSymbol.doSql(connection, symbol);
		}
	    }
	    return symbols;
	}
    };

    public static final DaoAction<Symbol, Symbol> insert = new DaoAction<Symbol, Symbol>() {
	@Override public Symbol doSql(Connection connection, Symbol symbol) throws SQLException {
	    
	    PreparedStatement stat = connection.prepareStatement("insert into symbol (block_id, staff_id, start_time_n, start_time_d, symbol_type) " +
								 "values (?, ?, ?, ?, ?)");
	    stat.setLong(1, symbol.position.block.id);
	    stat.setLong(2, symbol.position.staff.id);
	    stat.setLong(3, symbol.startTime.getNumerator());
	    stat.setLong(4, symbol.startTime.getDenominator());
	    stat.setString(5, TYPE_MAPPER.toBase(symbol.symbolType));
	    stat.executeUpdate();
	    ResultSet resultSet = stat.getGeneratedKeys();
	    if(!resultSet.next()) {
		throw new SQLException("Could not retrieve new symbol id.");
	    }
	    symbol.id = resultSet.getLong(1);
	    if (symbol.segment != null) {
		symbol.segment = Segment.insert.doSql(connection, symbol.segment);
	    }
	    if (symbol.timeSignature != null) {
		symbol.timeSignature = TimeSignature.insert.doSql(connection, symbol.timeSignature);
	    }
	    if (symbol.keySignature != null) {
		symbol.keySignature = KeySignature.insert.doSql(connection, symbol.keySignature);
	    }
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
