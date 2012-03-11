package utils.nwc;

class KeySignature implements Comparable<KeySignature> {
  public final Long position;

  public KeySignature(long _position) {
    position = _position;
  }

  @Override
  public int compareTo(KeySignature ts) {
    return position.compareTo(ts.position);
  }

}