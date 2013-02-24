package utils;

public final class Pitch {
  public enum Note {
      C("c"),
      D("d"),
      E("e"),
      F("f"),
      G("g"),
      A("a"),
      B("b");
      public String baseValue;
      private Note(String _baseValue) {
	  baseValue = _baseValue;
      }
  }

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
