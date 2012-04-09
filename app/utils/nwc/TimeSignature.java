package utils.nwc;

import models.*;

class TimeSignature implements Comparable<TimeSignature> {
  public final Integer position;
  public final Integer beatCount;
  public final DurationSymbol beatValue;
  public TimeSignature(Integer _position,
		       Integer _beatCount,
		       DurationSymbol _beatValue) {
    position = _position;
    beatCount = _beatCount;
    beatValue = _beatValue;
  }

  @Override
  public int compareTo(TimeSignature ts) {
    return position.compareTo(ts.position);
  }

}