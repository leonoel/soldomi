package utils.nwc;

import models.*;

class TimeSignature implements Comparable<TimeSignature> {
  public final Long position;
  public final Long beatCount;
  public final DurationSymbol beatValue;
  public TimeSignature(long _position,
		       long _beatCount,
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