package models;

public enum DurationSymbol {
  UNDEFINED (0),
  SIXTY_FOURTH (1),  
  THIRTY_SECOND (2),
  SIXTEENTH (4),
  EIGHTH (8),
  QUARTER (16),
  HALF (32),
  WHOLE (64);

  private long m_sixtyFourths;

  private DurationSymbol(long sixtyFourths) {
    m_sixtyFourths = sixtyFourths;
  }

  public long toSixtyFourths() {
    return m_sixtyFourths;
  }
}