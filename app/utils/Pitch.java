package utils;

import javax.persistence.*;

@Embeddable
public final class Pitch {
  public enum Note {
    C,D,E,F,G,A,B;
  }

  @Enumerated(EnumType.STRING)
  public Note note;
  public Integer octave;

  public Pitch(Note _note,
	       Integer _octave) {
    note = _note;
    octave = _octave;
  }

  public Pitch addInterval(int toAdd) {
    int ordinal = (note.ordinal() + toAdd) % Note.values().length;
    int octave = this.octave + (note.ordinal() + toAdd - ordinal) / Note.values().length;
    if (ordinal < 0) {
      ordinal += Note.values().length;
      octave--;
    }
    return new Pitch(Note.values()[ordinal], octave);
  }

  public static Integer interval(Pitch from, Pitch to) {
    return (to.note.ordinal() - from.note.ordinal()) +
      Note.values().length * (to.octave - from.octave);
  }
}
