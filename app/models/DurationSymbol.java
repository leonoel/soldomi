package models;

public enum DurationSymbol {
  UNDEFINED           (0,0.0000f),
  SIXTY_FOURTH        (1,0.0625f), // Quadruple Croche
  THIRTY_SECOND       (2,0.1250f), // Triple Croche
  TWENTY_FOURTH       (3,0.1666f), // Triolet de Double
  DOTTED_THIRTY_SECOND(2,0.1875f), // Triple Croche Pointee
  SIXTEENTH           (4,0.2500f), // Dble Croche
  TWELFTH             (5,0.3333f), // Triolet de Croche
  DOTTED_SIXTEENTH    (6,0.3750f), // Dble Croche Pointee
  D_DOTTED_SIXTEENTH  (7,0.4375f), // Dble Croche Dble Pointee
  EIGHTH              (8,0.5000f), // Croche
  SEVENTH             (9,0.5714f), // Septolet de Noire
  SIXTH              (11,0.6666f), // Triolet de Noires
  DOTTED_EIGHTH      (12,0.7500f), // Croche Pointee
  FIFTH              (13,0.8000f), // Quintolet de Noires
  D_DOTTED_EIGHTH    (14,0.8750f), // Croche Double Pointee
  QUARTER            (16,1.0000f), // Noire
  THIRD              (21,1.3333f), // Triolet de Blanche
  DOTTED_QUARTER     (24,1.5000f), // Noire Pointee
  D_DOTTED_QUARTER   (28,1.7500f), // Noire Double Pointee
  HALF               (32,2.0000f), // Blanche
  DOTTED_HALF        (48,3.0000f), // Blanche Pointee
  D_DOTTED_HALF      (56,3.5000f), // Blanche Double Pointee
  WHOLE              (64,4.0000f); // Ronde

  private Integer m_sixtyFourths;
  private float   m_duration;

  private DurationSymbol(Integer sixtyFourths,float duration)   {
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
