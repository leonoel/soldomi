package models;

public enum Clef {
  UNDEFINED(null),
  TREBLE(new Pitch(Pitch.Note.B, 4)),
  BASS(new Pitch(Pitch.Note.D, 3)),
  ALTO(new Pitch(Pitch.Note.C, 4)),
  TENOR(new Pitch(Pitch.Note.A, 3));

  /*
   * Pitch of the middle line
   */
  private Pitch m_pitch;

  private Clef(Pitch pitch) {
    m_pitch = pitch;
  }
  
  public Pitch getPitch() {
    return m_pitch;
  }

}