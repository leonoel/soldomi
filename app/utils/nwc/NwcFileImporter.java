package utils.nwc;

import models.*;
import utils.*;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import org.apache.commons.math3.fraction.Fraction;

public class NwcFileImporter {

    private final class StaffImporter {
	public final Staff staff;
	public Fraction time;
	public Iterator<nwcfile.SymbolContainer> symbolContainerIterator;

	public StaffImporter(nwcfile.Staff _nwcStaff, Staff _staff) {
	    staff = _staff;
	    time = Fraction.ZERO;
	    symbolContainerIterator = _nwcStaff.getSymbols().iterator();
	}
    }

    private final nwcfile.NwcFile nwcFile;
    private final Tune tune = new Tune();

    private final List<StaffImporter> staffImporters = new ArrayList<StaffImporter>();

    private Sect currentSect;
    private Block currentBlock;

    private NwcFileImporter(nwcfile.NwcFile _nwcFile) {
	nwcFile = _nwcFile;
	tune.name = nwcFile.getTitle().length() == 0 ? "Untitled" : nwcFile.getTitle();

	currentSect = new Sect(tune, 0L);
	currentBlock = new Block(currentSect, 0L);

	currentSect.blocks.add(currentBlock);
	tune.sects.add(currentSect);

	for(nwcfile.Staff nwcStaff : nwcFile.getStaves()) {
	    Syst syst = new Syst(tune, nwcStaff.getName());
	    Staff staff = new Staff(syst, nwcStaff.getName());
	    syst.staffs.add(staff);
	    tune.systs.add(syst);
	    staffImporters.add(new StaffImporter(nwcStaff, staff));
	}


	addAllSymbols();
	//	adjustTimeSignatures();
	//	propagateKeySignatures();
    }

    public static Tune run(nwcfile.NwcFile nwcFile) {
	return new NwcFileImporter(nwcFile).tune;
    }

    private void addAllSymbols() {
	Fraction maxTime = Fraction.ZERO;
	while(true) {
	    StaffImporter minTimeStaff = null;
	    for (StaffImporter staff : staffImporters) {
		if (staff.symbolContainerIterator.hasNext() && (minTimeStaff == null || minTimeStaff.time.compareTo(staff.time) > 0)) {
		    minTimeStaff = staff;
		}
	    }
	    stepToNextBar(minTimeStaff);

	    if (maxTime.compareTo(minTimeStaff.time) < 0) {
		maxTime = minTimeStaff.time;
	    }

	    boolean sync = true;
	    for (StaffImporter staff : staffImporters) {
		if (!staff.symbolContainerIterator.hasNext()) {
		    staff.time = maxTime;
		}
		sync = sync && (staff.time.equals(maxTime));
	    }
	    if (sync) {
		if (isSymbolLeft()) {
		    currentBlock = new Block(currentSect, maxTime.longValue());
		    currentSect.blocks.add(currentBlock);
		} else {
		    return;
		}
	    }
	}
    }

	/*
    private void adjustTimeSignatures() {
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
    }
	*/

	/*
    private void propagateKeySignatures() {
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
    }
	*/

    private boolean isSymbolLeft() {
	for (StaffImporter staffImporter : staffImporters) {
	    if (staffImporter.symbolContainerIterator.hasNext())
		return true;
	}
	return false;
    }

    private void stepToNextBar(StaffImporter staff) {
	Tuplet currentTuplet = null;
	while(staff.symbolContainerIterator.hasNext()) {
	    nwcfile.SymbolContainer symbolContainer = staff.symbolContainerIterator.next();
	    switch(symbolContainer.getType()) {
	    case REST:
	    case NOTE: {
		nwcfile.Segment nwcSegment    = (nwcfile.Segment) symbolContainer.getSymbol();
		Segment segment               = new Segment();

		segment.rest                  = symbolContainer.getType() == nwcfile.SymbolContainer.SymbolType.REST;
		//segment.pitch                 = m_clef.getPitch().addInterval((int) nwcSegment.getRelativePitch());
		//segment.accidental       = toAccidental(nwcSegment.getAccidental()); 
		segment.dotCount             = toDotCount(nwcSegment.getDots());
		segment.durationSymbol       = toDurationSymbol(nwcSegment.getDuration());
		segment.startTime            = staff.time;

		nwcfile.Segment.Triplet nwcTriplet = nwcSegment.getTriplet();
		if (nwcTriplet != nwcfile.Segment.Triplet.NONE) {
		    segment.duration = new Fraction(segment.durationSymbol.duration(segment.dotCount)).multiply(new Fraction(2, 3));
		    if (nwcTriplet == nwcfile.Segment.Triplet.FIRST) {
			currentTuplet = new Tuplet();
		    }
		    segment.tuplet = currentTuplet;
		    currentTuplet.segments.add(segment);
		    if (nwcTriplet == nwcfile.Segment.Triplet.LAST) {
			Fraction duration = Fraction.ZERO;
			for (Segment tupletSegment : currentTuplet.segments) {
			    duration.add(tupletSegment.duration);
			}
			currentTuplet.duration = duration.longValue();
		    }
		} else {
		    segment.duration = new Fraction(segment.durationSymbol.duration(segment.dotCount));
		}

		// TODO add segment to tune

		staff.time = staff.time.add(segment.duration);
		break;
	    }
	    case TIME_SIGNATURE: {
		nwcfile.TimeSignature nwcTimeSignature = (nwcfile.TimeSignature) symbolContainer.getSymbol();
		// TODO
		break;
	    }
	    case KEY_SIGNATURE: {
		nwcfile.KeySignature nwcKeySignature = (nwcfile.KeySignature) symbolContainer.getSymbol();
		// TODO
		break;
	    }
	    case CLEF: {
		nwcfile.Clef nwcClef = (nwcfile.Clef) symbolContainer.getSymbol();
		// TODO
		break;
	    }
	    case BAR_LINE: {
		return;
	    }
	    default: {
	    }
	    } // end switch
	} // end while
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
