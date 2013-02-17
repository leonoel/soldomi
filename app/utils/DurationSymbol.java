package utils;

public enum DurationSymbol {
  UNDEFINED           (0L),
  SIXTY_FOURTH        (1L), // Quadruple Croche
  THIRTY_SECOND       (2L), // Triple Croche
  SIXTEENTH           (4L), // Dble Croche
  EIGHTH              (8L), // Croche
  QUARTER            (16L), // Noire
  HALF               (32L), // Blanche
  WHOLE              (64L); // Ronde

  private Long m_duration;

  private DurationSymbol(Long duration)   {
    m_duration = duration;
  }

  public Long duration() {
    return m_duration;
  }
  
}
