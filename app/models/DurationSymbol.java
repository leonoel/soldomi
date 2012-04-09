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

  private Integer m_sixtyFourths;

  private DurationSymbol(Integer sixtyFourths) {
    m_sixtyFourths = sixtyFourths;
  }

  public Integer toSixtyFourths() {
    return m_sixtyFourths;
  }
}