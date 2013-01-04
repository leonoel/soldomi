package models;

public enum DurationSymbol {
  UNDEFINED     (0,0.0000f),
  SIXTY_FOURTH  (1,0.0625f), // Quadruple Croche
  THIRTY_SECOND (2,0.1250f), // Triple Croche
  TWENTY_FOURTH (3,0.1666f), // Triolet de Double
  SIXTEENTH     (4,0.2500f), // Double Croche
  TWELFTH       (5,0.3333f), // Triolet de Croche
  EIGHTH        (8,0.5000f), // Croche
  SEVENTH       (9,0.5714f), // Septolet de Noire
  SIXTH        (11,0.6666f), // Triolet de Noires
  FIFTH        (15,0.8000f), // Quintolet de Noires
  QUARTER      (16,1.0000f), // Noire
  THIRD        (21,1.3333f), // Triolet de Blanche
  HALF         (32,2.0000f), // Blanche
  WHOLE        (64,4.0000f); // Ronde

  private Integer m_sixtyFourths;
  private float   m_duration;

  private DurationSymbol(Integer sixtyFourths,float duration) {
    m_sixtyFourths = sixtyFourths;
    m_duration     = duration;
  }

  public Integer toSixtyFourths() {
    return m_sixtyFourths;
  }
  
  public float toFloat() {
    return m_duration;
  }
  
}
