package utils.nwc;

import dao.*;
import utils.*;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

public class NwcFileImporter {

    private final class Staff {

	private final nwcfile.Staff m_nwcStaff;
	private final String m_name;
	/*
	 * Unit : sixty-fourths
	 */
	public Long time;

	private Clef m_clef;
	private Iterator<nwcfile.SymbolContainer> m_symbolContainerIterator;

	public Staff(nwcfile.Staff nwcStaff) {
	    m_nwcStaff = nwcStaff;
	    m_name = nwcStaff.getName();
	    time = 0L;
	    m_clef = Clef.TREBLE;
	    m_symbolContainerIterator = nwcStaff.getSymbols().iterator();
	}

	public void stepOneMeasure(Block block) {
	    while(m_symbolContainerIterator.hasNext()) {
		nwcfile.SymbolContainer symbolContainer = m_symbolContainerIterator.next();
		switch(symbolContainer.getType()) {
		case NOTE: {
		    /*
		    nwcfile.Segment nwcSegment    = (nwcfile.Segment) symbolContainer.getSymbol();
		    Segment segment               = new Segment();
		    segment.rest             = false;
		    segment.clef             = m_clef;
		    segment.pitch            = m_clef.getPitch().addInterval((int) nwcSegment.getRelativePitch());
		    segment.accidental       = toAccidental(nwcSegment.getAccidental()); 
		    segment.dotCount         = toDotCount(nwcSegment.getDots());
		    segment.startTime        = time;
		    segment.durationSymbol   = toDurationSymbol(nwcSegment.getDuration());
		    segment.tupletDenominator = nwcSegment.isTriplet() ? 3 : 1;
		    time                  += segment.duration(); // TODO handle tuplets
		    */
		    break;
		}
		case REST: {
		    /*
		    nwcfile.Segment nwcSegment = (nwcfile.Segment) symbolContainer.getSymbol();
		    DurationSymbol durationSymbol = toDurationSymbol(nwcSegment.getDuration());
		    Segment segment = new Segment();
		    segment.rest             = true;
		    segment.clef             = m_clef;
		    segment.pitch            = m_clef.getPitch();
		    segment.accidental       = Accidental.AUTO;
		    segment.startTime        = time;
		    segment.dotCount         = toDotCount(nwcSegment.getDots());
		    segment.durationSymbol   = toDurationSymbol(nwcSegment.getDuration());
		    time                  += segment.duration(); // TODO handle tuplets
		    */
		    break;
		}
		case TIME_SIGNATURE: {
		    /*
		    nwcfile.TimeSignature nwcTimeSignature = (nwcfile.TimeSignature) symbolContainer.getSymbol();
		    m_nwcFileImporter.addTimeSignature(new TimeSignature(time,
									 Integer.valueOf(nwcTimeSignature.getBeatCount()),
									 toDurationSymbol(nwcTimeSignature.getBeatValue())));
		    */
		    break;
		}
		case KEY_SIGNATURE: {
		    /*
		    nwcfile.KeySignature nwcKeySignature = (nwcfile.KeySignature) symbolContainer.getSymbol();
		    m_nwcFileImporter.addKeySignature(new KeySignature(time,nwcKeySignature.getFlats(),nwcKeySignature.getSharps()));
		    */
		    break;
		}
		case CLEF: {
		    /*
		    nwcfile.Clef nwcClef = (nwcfile.Clef) symbolContainer.getSymbol();
		    m_clef = toClef(nwcClef);
		    */
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

	public boolean isSymbolLeft() {
	    return m_symbolContainerIterator.hasNext();
	}
	
    }

    private final class Block implements NewTuneDao.Block {
	private final Long m_startTime;

	public Block(Long startTime) {
	    m_startTime = startTime;
	}

	@Override public Long startTime() { return m_startTime; }
    }

    private final class Syst implements NewTuneDao.Syst {
    }

    private final nwcfile.NwcFile m_nwcFile;
    private final List<Syst> m_systs = new ArrayList<Syst>();
    private final List<Staff> m_staffs = new ArrayList<Staff>();
    private final List<Block> m_blocks = new ArrayList<Block>();
    private Block m_currentBlock;
    //    private List<TimeSignature> m_timeSignatures = new ArrayList<TimeSignature>();
    //    private List<KeySignature> m_keySignatures = new ArrayList<KeySignature>();

    public NwcFileImporter(nwcfile.NwcFile nwcFile) {
	m_nwcFile = nwcFile;
	List<nwcfile.Staff> nwcStaves = m_nwcFile.getStaves();
	for (nwcfile.Staff nwcStaff : nwcStaves) {
	    m_staffs.add(new Staff(nwcStaff));
	}
    }

    public Long save() {
	addAllSymbols();
	adjustTimeSignatures();
	propagateKeySignatures();

	final String name = m_nwcFile.getTitle().length() == 0 ? "Untitled" : m_nwcFile.getTitle();
	final List<NewTuneDao.Syst> systs = new ArrayList<NewTuneDao.Syst>();
	final List<NewTuneDao.Block> blocks = new ArrayList<NewTuneDao.Block>();

	for (Syst syst : m_systs) {
	    systs.add(syst);
	}

	for (Block block : m_blocks) {
	    blocks.add(block);
	}

	return NewTuneDao.create(new NewTuneDao.Tune() {
		@Override public String name() { return name; }
		@Override public List<NewTuneDao.Syst> systs() { return systs; }
		@Override public List<NewTuneDao.Block> blocks() { return blocks; }
	    });
    }

    /*
    void addTimeSignature(TimeSignature timeSignature) {
	m_timeSignatures.add(timeSignature);
    }
    */

    /*
    void addKeySignature(KeySignature keySignature) {
	m_keySignatures.add(keySignature);
    }
    */

    private void addAllSymbols() {
	m_currentBlock = new Block(0L);

	while(isSymbolLeft()) {
	    stepLatestStaff();
	    if (areStavesSynchronized()) {
		m_blocks.add(m_currentBlock);
		m_currentBlock = new Block(absoluteTime());
	    }
	}
    }

    private void adjustTimeSignatures() {
	/*
	Collections.sort(m_timeSignatures);
	Iterator<TimeSignature> it = m_timeSignatures.iterator();
	TimeSignature currentTs = null;
	TimeSignature nextTs = it.hasNext() ? it.next() : null;
	for (Block block : m_blocks) {
	    Long position = block.startTime;
	    Long duration = 0L;
	    for (Symbol symbol : block.symbols) {
		Segment segment = symbol.segment;
		if (segment != null) {
		    Long endPosition = segment.relativePosition() + segment.duration();
		    if (duration < endPosition) {
			duration = endPosition;
		    }
		}
	    }

	    while(nextTs != null &&
		  nextTs.position <= position) {
		currentTs = nextTs;
		nextTs = it.hasNext() ? it.next() : null;
	    }

	    if (currentTs != null) {
				block.beatCount = currentTs.beatCount;
				block.beatValue = currentTs.beatValue;
	    } else {
		DurationSymbol beatValue = DurationSymbol.SIXTY_FOURTH;
		int beatCount = (int) Math.round(duration*16); // 16 SIXTY_FOURTH in a QUARTER
		while(beatCount % 2 == 0 && beatValue.ordinal() < DurationSymbol.QUARTER.ordinal()) {
		    beatCount = beatCount >> 1;
		    beatValue = DurationSymbol.values()[beatValue.ordinal() + 1];
		}
				block.beatCount = beatCount;
				block.beatValue = beatValue;
	    }
	}
	*/
    }

    private void propagateKeySignatures() {
	/*
	Collections.sort(m_keySignatures);
	Iterator<KeySignature> it = m_keySignatures.iterator();
	KeySignature currentKs = null;
	KeySignature nextKs = it.hasNext() ? it.next() : null;
	for (Block block : m_blocks) {
	    Long position = block.startTime();
	    while(nextKs != null && nextKs.position <= position) {
		currentKs = nextKs;
		nextKs = it.hasNext() ? it.next() : null;
	    }

	    if (currentKs != null) {
		//	block.keySignature = currentKs.getMajorScale();
	    } else {
		//	block.keySignature = "C"; //new KeySignature(position);
	    }
	}
	*/
    }

    private boolean isSymbolLeft() {
	for (Staff staff : m_staffs) {
	    if (staff.isSymbolLeft())
		return true;
	}
	return false;
    }

    private void stepLatestStaff() {
	Long minTime = 0L;
	Staff staffToStep = null;
	for (Staff staff : m_staffs) {
	    if (staff.isSymbolLeft()) {
		if (staffToStep == null || minTime >= staff.time) {
		    staffToStep = staff;
		    minTime = staff.time;
		}
	    }
	}
	staffToStep.stepOneMeasure(m_currentBlock);
    }

    private boolean areStavesSynchronized() {
	Long time = absoluteTime();
	for (Staff staff : m_staffs) {
	    if (staff.isSymbolLeft() && time == staff.time) {
		return false;
	    }
	}
	return true;
    }

    private Long absoluteTime() {
	Long time = 0L;
	for (Staff staff : m_staffs) {
	    if (time < staff.time) {
		time = staff.time;
	    }
	}
	return time;
    }

    private static DurationSymbol toDurationSymbol(nwcfile.TimeSignature.BeatValue beatValue) {
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
  
    private static DurationSymbol toDurationSymbol(nwcfile.Segment.Duration duration) {
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

    private static Clef toClef(nwcfile.Clef clef) {
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

    private static Integer toDotCount(nwcfile.Segment.Dots nwcDots) {
	switch(nwcDots) {
	case NONE:
	    return 0;
	case SINGLE:
	    return 1;
	case DOUBLE:
	    return 2;
	default:
	    return 0;
	}
    }

    private static Accidental toAccidental(nwcfile.Segment.Accidental nwcAccidental) {
	switch(nwcAccidental) {
	case SHARP:
	    return Accidental.SHARP;
	case FLAT:
	    return Accidental.FLAT;
	case NATURAL:
	    return Accidental.NATURAL;
	case DOUBLE_SHARP:
	    return Accidental.DOUBLE_SHARP;
	case DOUBLE_FLAT:
	    return Accidental.DOUBLE_FLAT;
	case AUTO:
	    return Accidental.AUTO;
	default:
	    return Accidental.AUTO;
	}
    }

}
