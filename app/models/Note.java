package models;

import java.util.List;
import java.util.ArrayList;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;

import utils.DaoAction;
import utils.EnumBaseMapper;

public class Note {

    public static final class Pitch {
	public enum NoteName {
	    C("c"),
	    D("d"),
	    E("e"),
	    F("f"),
	    G("g"),
	    A("a"),
	    B("b");
	    public String baseValue;
	    private NoteName(String _baseValue) {
		baseValue = _baseValue;
	    }
	    public static NoteName fromBaseValue(String baseValue) {
		for (NoteName noteName : NoteName.values()) {
		    if (noteName.baseValue.equals(baseValue)) {
			return noteName;
		    }
		}
		return null;
	    }
	}

	public NoteName noteName;
	public Integer octave;

	public Pitch(NoteName _noteName,
		     Integer _octave) {
	    noteName = _noteName;
	    octave = _octave;
	}

	public Pitch addInterval(int toAdd) {
	    int ordinal = (noteName.ordinal() + toAdd) % NoteName.values().length;
	    int octave = this.octave + (noteName.ordinal() + toAdd - ordinal) / NoteName.values().length;
	    if (ordinal < 0) {
		ordinal += NoteName.values().length;
		octave--;
	    }
	    return new Pitch(NoteName.values()[ordinal], octave);
	}

	public static Integer interval(Pitch from, Pitch to) {
	    return (to.noteName.ordinal() - from.noteName.ordinal()) +
		NoteName.values().length * (to.octave - from.octave);
	}
    }

    public enum Accidental {
	AUTO,
	NATURAL,
	SHARP,
	FLAT,
	DOUBLE_SHARP,
	DOUBLE_FLAT;
    }

    public Long id;
    public Segment segment;
    public Pitch pitch;
    public Accidental accidental;

    public Note() {
    }

    private static final EnumBaseMapper<Pitch.NoteName> NOTE_NAME_MAPPER = new EnumBaseMapper<Pitch.NoteName>(Pitch.NoteName.class);
    private static final EnumBaseMapper<Accidental> ACCIDENTAL_MAPPER = new EnumBaseMapper<Accidental>(Accidental.class);

    public static final DaoAction<Note, Note> insert = new DaoAction<Note, Note>() {
	@Override public Note doSql(Connection connection, Note note) throws SQLException {
	    PreparedStatement stat = connection.prepareStatement("insert into note (segment_id, " +
								                      "note_name, " +
								                      "octave, " +
								                      "accidental ) " +
								 "values (?, " +
								         "?, " +
								         "?, " +
								         "?) ");
	    stat.setLong(1, note.segment.id);
	    stat.setString(2, NOTE_NAME_MAPPER.toBase(note.pitch.noteName));
	    stat.setInt(3, note.pitch.octave);
	    stat.setString(4, ACCIDENTAL_MAPPER.toBase(note.accidental));
	    stat.executeUpdate();
	    ResultSet resultSet = stat.getGeneratedKeys();
	    if(!resultSet.next()) {
		throw new SQLException("Could not retrieve new note id.");
	    }
	    note.id = resultSet.getLong(1);
	    return note;
	}
    };

    public static final DaoAction<Segment, Note> getFromSegment = new DaoAction<Segment, Note>() {
	@Override public Note doSql(Connection connection, final Segment segment) throws SQLException {
	    Note note = new Note();
	    PreparedStatement stat = connection.prepareStatement("select id, note_name, octave, accidental " +
								 "from note " +
								 "where segment_id = ? ");
	    stat.setLong(1, segment.id);
	    ResultSet resultSet = stat.executeQuery();
	    if(!resultSet.next()) {
		throw new SQLException("Could not retrieve note info");
	    }
	    note.id = resultSet.getLong("id");
	    note.segment = segment;
	    note.pitch = new Pitch(NOTE_NAME_MAPPER.fromBase(resultSet.getString("note_name")),
				   resultSet.getInt("octave"));
	    note.accidental = ACCIDENTAL_MAPPER.fromBase(resultSet.getString("accidental"));
	    return note;
	}
    };

}
