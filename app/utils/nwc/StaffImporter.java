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
	segment.durationSymbol   = durationSymbol;
	m_time                  += durationSymbol.toFloat();
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
 	segment.dot              = nwcSegment.getDots().ordinal();
	segment.absolutePosition = m_time;
	segment.durationSymbol   = durationSymbol;
	m_time                  += durationSymbol.toFloat();
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
}
