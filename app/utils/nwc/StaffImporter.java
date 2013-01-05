package utils.nwc;

import models.*;
import javax.xml.datatype.Duration;
import java.util.Iterator;

class StaffImporter {
  private final NwcFileImporter m_nwcFileImporter;
  private final Staff m_staff;
  private final nwcfile.Staff m_nwcStaff;
  private float m_time;
  private Clef m_clef;
  private Iterator<nwcfile.SymbolContainer> m_symbolContainerIterator;

  public StaffImporter(NwcFileImporter nwcFileImporter, nwcfile.Staff nwcStaff) {
    m_nwcFileImporter = nwcFileImporter;
    m_nwcStaff = nwcStaff;
    m_staff = new Staff(nwcFileImporter.getTune());
    m_staff.name = nwcStaff.getName();
    m_time = 0.0f;
    m_clef = Clef.TREBLE;
    m_symbolContainerIterator = nwcStaff.getSymbols().iterator();
  }

  public void stepOneMeasure(Measure measure) {
    while(m_symbolContainerIterator.hasNext()) {
      nwcfile.SymbolContainer symbolContainer = m_symbolContainerIterator.next();
      switch(symbolContainer.getType()) {
      case NOTE: {
	nwcfile.Segment nwcSegment    = (nwcfile.Segment) symbolContainer.getSymbol();
	DurationSymbol durationSymbol = toDurationSymbol(nwcSegment.getDuration());
	Segment segment               = new Segment(m_nwcFileImporter.getTune(), m_staff, measure);
	m_nwcFileImporter.getTune().segments.add(segment);
	measure.segments.add(segment);
	m_staff.segments.add(segment);
	segment.rest             = false;
	segment.clef             = m_clef;
	segment.pitch            = m_clef.getPitch().addInterval((int) nwcSegment.getRelativePitch());
        segment.accidental       = nwcSegment.getAccidental().ordinal(); 
        segment.dot              = nwcSegment.getDots().ordinal();
	segment.absolutePosition = m_time;
	segment.durationSymbol   = (segment.dot>0) ? addDots(durationSymbol,segment.dot):
				     nwcSegment.isTriplet() ? toTriplet(durationSymbol):
				       durationSymbol;
	m_time                  += segment.durationSymbol.toFloat();
	roundTime();
	break;
      }
      case REST: {
	nwcfile.Segment nwcSegment = (nwcfile.Segment) symbolContainer.getSymbol();
	DurationSymbol durationSymbol = toDurationSymbol(nwcSegment.getDuration());
	Segment segment = new Segment(m_nwcFileImporter.getTune(), m_staff, measure);
	m_nwcFileImporter.getTune().segments.add(segment);
	measure.segments.add(segment);
	m_staff.segments.add(segment);
	segment.rest             = true;
	segment.clef             = m_clef;
        segment.pitch            = m_clef.getPitch();
	segment.accidental       = 5;
	segment.absolutePosition = m_time;
 	segment.dot              = nwcSegment.getDots().ordinal();
	segment.durationSymbol   = (segment.dot>0) ? addDots(durationSymbol,segment.dot):
				     nwcSegment.isTriplet() ? toTriplet(durationSymbol):
				       durationSymbol;
	m_time                  += segment.durationSymbol.toFloat();
	break;
      }
      case TIME_SIGNATURE: {
	nwcfile.TimeSignature nwcTimeSignature = (nwcfile.TimeSignature) symbolContainer.getSymbol();
	m_nwcFileImporter.addTimeSignature(new TimeSignature(m_time,
							     Integer.valueOf(nwcTimeSignature.getBeatCount()),
							     toDurationSymbol(nwcTimeSignature.getBeatValue())));
	break;
      }
      case KEY_SIGNATURE: {
	nwcfile.KeySignature nwcKeySignature = (nwcfile.KeySignature) symbolContainer.getSymbol();
	m_nwcFileImporter.addKeySignature(new KeySignature(m_time,nwcKeySignature.getFlats(),nwcKeySignature.getSharps()));
	break;
      }
      case CLEF: {
	nwcfile.Clef nwcClef = (nwcfile.Clef) symbolContainer.getSymbol();
	m_clef = toClef(nwcClef);
	break;
      }
      case BAR_LINE: {
	return;
      }
      default: {
      }
      }
    }
  }

  public void roundTime() {
    if(Math.abs(m_time - Math.round(m_time))<0.01) 
	m_time = Math.round(m_time);
  }

  public Staff getStaff() {
    return m_staff;
  }

  public float getTime() {
    return m_time;
  }

  public boolean isSymbolLeft() {
    return m_symbolContainerIterator.hasNext();
  }

  private DurationSymbol toDurationSymbol(nwcfile.TimeSignature.BeatValue beatValue) {
    switch(beatValue) {
    case WHOLE:
      return DurationSymbol.WHOLE;
    case HALF:
      return DurationSymbol.HALF;
    case QUARTER:
      return DurationSymbol.QUARTER;
    case EIGHTH:
      return DurationSymbol.EIGHTH;
    case SIXTEENTH:
      return DurationSymbol.SIXTEENTH;
    case THIRTY_SECOND:
      return DurationSymbol.THIRTY_SECOND;
    default:
      return DurationSymbol.UNDEFINED;
    }
  }
  
  private DurationSymbol toDurationSymbol(nwcfile.Segment.Duration duration) {
    switch(duration) {
    case WHOLE:
      return DurationSymbol.WHOLE;
    case HALF:
      return DurationSymbol.HALF;
    case QUARTER:
      return DurationSymbol.QUARTER;
    case EIGHTH:
      return DurationSymbol.EIGHTH;
    case SIXTEENTH:
      return DurationSymbol.SIXTEENTH;
    case THIRTY_SECOND:
      return DurationSymbol.THIRTY_SECOND;
    case SIXTY_FOURTH:
      return DurationSymbol.SIXTY_FOURTH;
    default:
      return DurationSymbol.UNDEFINED;
    }
  }

  private Clef toClef(nwcfile.Clef clef) {
    switch(clef.getSymbol()) {
    case TREBLE:
      return Clef.TREBLE;
    case BASS:
      return Clef.BASS;
    case ALTO:
      return Clef.ALTO;
    case TENOR:
      return Clef.TENOR;
    default:
      return Clef.UNDEFINED;
    }
  }

  private DurationSymbol addDots(DurationSymbol duration,Integer nDots) {
    switch(duration) {
      case HALF: {
	switch(nDots) {
	  case 1: return DurationSymbol.DOTTED_HALF;// Blanche Pointee
	  case 2: return DurationSymbol.D_DOTTED_HALF;// Blanche Dble Pointee
	  default://TODO Exception Error
	}
      }
      case QUARTER: {
	switch(nDots) {
	  case 1: return DurationSymbol.DOTTED_QUARTER;// Noire Pointee
	  case 2: return DurationSymbol.D_DOTTED_QUARTER;// Noire Dble Pointee
	  default://TODO Exception Error
	}
      }
      case EIGHTH: {
	switch(nDots) {
	  case 1: return DurationSymbol.DOTTED_EIGHTH;// Croche Pointee
	  case 2: return DurationSymbol.D_DOTTED_EIGHTH;// Croche Dble Pointee
	  default://TODO Exception Error
	}
      }
      case SIXTEENTH: {
	switch(nDots) {
	  case 1: return DurationSymbol.DOTTED_SIXTEENTH;// Double Croche Pointee
	  case 2: return DurationSymbol.D_DOTTED_SIXTEENTH;// Double Croche Dble Pointee
	  default://TODO Exception Error
	}
      }
      case THIRTY_SECOND: {
	switch(nDots) {
	  case 1: return DurationSymbol.DOTTED_THIRTY_SECOND;// Triple Croche Pointee
	  default://TODO Exception Error
	}
      }
      default: //TODO Exception Error
    }
    return duration;
  }
  private DurationSymbol toTriplet(DurationSymbol duration) {
    switch(duration) {
      case HALF:      return DurationSymbol.THIRD;         // Triolet de Blanche
      case QUARTER:   return DurationSymbol.SIXTH;         // Triolet de Noire
      case EIGHTH:    return DurationSymbol.TWELFTH;       // Triolet de Croche
      case SIXTEENTH: return DurationSymbol.TWENTY_FOURTH; // Triolet de Double
      default: //TODO Exception Error
    }
    return duration;
  }
}
