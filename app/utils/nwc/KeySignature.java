package utils.nwc;

class KeySignature implements Comparable<KeySignature> {
  public final float position;               // Segment Position
  public final int[] accidentals = new int[7]; // A,B,C,D,E,F,G; -1=Flat,0=Neutral,1=Sharp
  public String majorScale,minorScale;         // Corresponding Scale
  
  public KeySignature(float _position) { // When no accidental vector is provided, major C is assumed
    position = _position;
    for(int i=0;i<7;i++) {accidentals[i]=0;};
    majorScale = "C"; minorScale = "A"; return;
  }
 
  public KeySignature(float _position,boolean[] _flats, boolean[] _sharps) {
    position = _position;
    int countFlats=0, countSharps=0;
    for(int i=0;i<7;i++) if(_flats[i]) {accidentals[i]--;countFlats++;};
    for(int i=0;i<7;i++) if(_sharps[i]) {accidentals[i]++;countSharps++;}; // TODO: Should be checking if accidentals[i]is positive. I don't know how to do that properly! Adam 12.12.13
    if(countFlats>0 && countSharps>0)  return; // do nothing, this is not a proper scale
    majorScale = "C"; minorScale="A";
// TODO check that scales are valid. Bb Eb and Ab is valid but not F# G#...
// Sharps 5,2,6,3,0,4,1 = ((1-i%2)*(5+i/2) + i%2*(2+i/2))%7 = (5+i/2)%7 - 3*(i%2)
    if(countFlats>0) {
      switch(countFlats) {
	case 1: {majorScale = "F";  minorScale="D"; return;}
	case 2: {majorScale = "Bb"; minorScale="G"; return;}
	case 3: {majorScale = "Eb"; minorScale="C"; return;}
	case 4: {majorScale = "Ab"; minorScale="F"; return;}
	case 5: {majorScale = "Db"; minorScale="Bb";return;}
	case 6: {majorScale = "Gb"; minorScale="Eb";return;}
	case 7: {majorScale = "Cb"; minorScale="Ab";return;}
      }
    }
    else {
      switch(countSharps){
	case 1: {majorScale = "G";  minorScale="E"; return;}
	case 2: {majorScale = "D";  minorScale="B"; return;}
	case 3: {majorScale = "A";  minorScale="F#";return;}
	case 4: {majorScale = "E";  minorScale="C#";return;}
	case 5: {majorScale = "B";  minorScale="G#";return;}
	case 6: {majorScale = "F#"; minorScale="D#";return;}
	case 7: {majorScale = "C#"; minorScale="A#";return;}
      }
    }// ABCDEFG
  }

  @Override
  public int compareTo(KeySignature ts) {
    float relativePosition = position - ts.position;
    if(Math.abs(relativePosition) < 0.01) return 0;
    if(relativePosition           < 0.0 ) return (int) Math.round(Math.ceil(relativePosition));
    else                                  return (int) Math.round(Math.floor(relativePosition));

//    return Math.abs(relativePosition)<0.01 ? 0 : (relativePosition>0.0f) ? Math.ceil(relativePosition): Math.floor(relativePosition);
//    return 0; //position.compareTo(ts.position);
  }

  public String getMajorScale() {
    return majorScale;
  }

  public String getMinorScale() {
    return minorScale;
  }
  
}
