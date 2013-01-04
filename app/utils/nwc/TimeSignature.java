package utils.nwc;

import models.*;

class TimeSignature implements Comparable<TimeSignature> {
  public final float position;
  public final Integer beatCount;
  public final DurationSymbol beatValue;
  public TimeSignature(float _position,
		       Integer _beatCount,
		       DurationSymbol _beatValue) {
    position = _position;
    beatCount = _beatCount;
    beatValue = _beatValue;
  }

  @Override
  public int compareTo(TimeSignature ts) {
    float relativePosition = position - ts.position;
    if(Math.abs(relativePosition) < 0.01) return 0;
    if(relativePosition           < 0.0 ) return (int) Math.round(Math.ceil(relativePosition));
    else                                  return (int) Math.round(Math.floor(relativePosition));
 //   return Math.abs(relativePosition)<0.01 ? 0 : (relativePosition>0.0f) ? Math.ceil(relativePosition): Math.floor(relativePosition);
//    return position.compareTo(ts.position);
  }

}
