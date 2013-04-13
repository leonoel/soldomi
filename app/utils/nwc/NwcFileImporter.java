package utils.nwc;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import org.apache.commons.math3.fraction.Fraction;

import models.Staff;
import models.Tune;
import models.Block;
import models.Sect;
import models.Syst;
import models.Segment;
import models.Symbol;
import models.Symbol.Position;
import models.Symbol.SymbolType;
import models.Tuplet;
import models.Note;
import models.TimeSignature;
import models.TimeSignature.NoteValue;
import models.KeySignature;
import models.KeySignature.NotePitch;

import utils.DurationSymbol;
import utils.Clef;
import utils.Accidental;


public class NwcFileImporter {

    private final class StaffImporter {
	public final Staff staff;
	private Fraction currentTime;
	private Clef currentClef;
	private Iterator<nwcfile.SymbolContainer> symbolContainerIterator;
	private Tuplet currentTuplet;
	

	public StaffImporter(nwcfile.Staff _nwcStaff, Staff _staff) {
	    staff = _staff;
	    currentTime = Fraction.ZERO;
	    currentClef = Clef.TREBLE;
	    symbolContainerIterator = _nwcStaff.getSymbols().iterator();
	    currentTuplet = null;
	}

	private void addClef(nwcfile.Clef nwcClef) {
	    currentClef = toClef(nwcClef);

	    Symbol symbol = new Symbol();
	    symbol.position = new Position(staff, currentBlock);
	    symbol.startTime = currentTime;
	    symbol.symbolType = currentClef.symbolType;

	    staff.symbols.add(symbol);
	    currentBlock.symbols.add(symbol);
	}

	private void addSegment(nwcfile.Segment nwcSegment, Boolean isRest) {
	    Symbol symbol = new Symbol();
	    Segment segment               = new Segment();
	    DurationSymbol durationSymbol = toDurationSymbol(nwcSegment.getDuration());
	    SymbolType symbolType  = isRest ? durationSymbol.restSymbolType : durationSymbol.noteSymbolType;
	    Integer dotCount              = toDotCount(nwcSegment.getDots());

	    Fraction duration = new Fraction(durationSymbol.duration(dotCount));
	    nwcfile.Segment.Triplet nwcTriplet = nwcSegment.getTriplet();
	    if (nwcfile.Segment.Triplet.NONE != nwcTriplet) {
		duration = duration.multiply(new Fraction(2, 3));
	    }

	    if (nwcfile.Segment.Triplet.FIRST == nwcTriplet) {
		currentTuplet = new Tuplet();
	    }


	    symbol.position               = new Position(staff, currentBlock);
	    symbol.startTime              = currentTime;
	    symbol.symbolType             = symbolType;
	    symbol.segment                = segment;

	    segment.symbol                = symbol;
	    segment.duration              = duration;
	    segment.tuplet                = currentTuplet;
		
	    if (!isRest) {
		Note note = new Note();
		note.pitch = currentClef.pitch.addInterval((int) nwcSegment.getRelativePitch());
		//note.accidental       = toAccidental(nwcSegment.getAccidental()); 
		note.segment = segment;

		segment.note = note;
	    }

	    segment.dotCount              = dotCount;

	    if (currentTuplet != null) {
		currentTuplet.segments.add(segment);
	    }

	    if (nwcfile.Segment.Triplet.LAST == nwcTriplet) {
		Fraction currentTupletDuration = Fraction.ZERO;
		for (Segment tupletSegment : currentTuplet.segments) {
		    currentTupletDuration.add(tupletSegment.duration);
		}
		currentTuplet.duration = currentTupletDuration.longValue();
		currentTuplet = null;
	    }


	    staff.symbols.add(symbol);
	    currentBlock.symbols.add(symbol);

	    incrementTime(duration);
	}

	private void addTimeSignature(nwcfile.TimeSignature nwcTimeSignature) {
	    Symbol symbol = new Symbol();
	    symbol.position = new Position(staff, currentBlock);
	    symbol.startTime = currentTime;
	    symbol.symbolType = toSymbolType(nwcTimeSignature.getStyle());
	    
	    if (SymbolType.STANDARD_TIME_SIGNATURE == symbol.symbolType) {
		symbol.timeSignature = new TimeSignature();
		symbol.timeSignature.symbol = symbol;
		symbol.timeSignature.beatCount = nwcTimeSignature.getBeatCount().intValue();
		symbol.timeSignature.beatValue = toNoteValue(nwcTimeSignature.getBeatValue());
	    }

	    staff.symbols.add(symbol);
	    currentBlock.symbols.add(symbol);
	}

	private void addKeySignature(nwcfile.KeySignature nwcKeySignature) {
	    Symbol symbol = new Symbol();
	    symbol.position = new Position(staff, currentBlock);
	    symbol.startTime = currentTime;
	    symbol.symbolType = SymbolType.KEY_SIGNATURE;
	    
	    symbol.keySignature = new KeySignature();
	    symbol.keySignature.symbol = symbol;
	    symbol.keySignature.a = toNotePitch(nwcKeySignature, nwcfile.KeySignature.Note.A);
	    symbol.keySignature.b = toNotePitch(nwcKeySignature, nwcfile.KeySignature.Note.B);
	    symbol.keySignature.c = toNotePitch(nwcKeySignature, nwcfile.KeySignature.Note.C);
	    symbol.keySignature.d = toNotePitch(nwcKeySignature, nwcfile.KeySignature.Note.D);
	    symbol.keySignature.e = toNotePitch(nwcKeySignature, nwcfile.KeySignature.Note.E);
	    symbol.keySignature.f = toNotePitch(nwcKeySignature, nwcfile.KeySignature.Note.F);
	    symbol.keySignature.g = toNotePitch(nwcKeySignature, nwcfile.KeySignature.Note.G);

	    staff.symbols.add(symbol);
	    currentBlock.symbols.add(symbol);
	}

	private void incrementTime(Fraction duration) {
	    currentTime = currentTime.add(duration);
	    if (maxTime.compareTo(currentTime) < 0) {
		maxTime = currentTime;
	    }
	}

	public void stepStaffSymbolsToBar() {
	    boolean bar = false;
	    while(!bar && symbolContainerIterator.hasNext()) {
		nwcfile.SymbolContainer symbolContainer = symbolContainerIterator.next();
		switch(symbolContainer.getType()) {
		case REST: {
		    addSegment((nwcfile.Segment) symbolContainer.getSymbol(), true);
		    break;
		}

		case NOTE: {
		    addSegment((nwcfile.Segment) symbolContainer.getSymbol(), false);
		    break;
		}
		case TIME_SIGNATURE: {
		    addTimeSignature((nwcfile.TimeSignature) symbolContainer.getSymbol());
		    break;
		}
		case KEY_SIGNATURE: {
		    addKeySignature((nwcfile.KeySignature) symbolContainer.getSymbol());
		    break;
		}
		case CLEF: {
		    addClef((nwcfile.Clef) symbolContainer.getSymbol());
		    break;
		}
		case BAR_LINE: {
		    bar = true;
		    break;
		}
		default: {
		}
		} // end switch
	    } // end while
		    
	}

    }

    private final nwcfile.NwcFile nwcFile;
    private final Tune tune = new Tune();

    private final List<StaffImporter> staffImporters = new ArrayList<StaffImporter>();

    private Sect currentSect;
    private Block currentBlock;
    private Fraction maxTime;

    private NwcFileImporter(nwcfile.NwcFile _nwcFile) {
	nwcFile = _nwcFile;
	tune.name = nwcFile.getTitle().length() == 0 ? "Untitled" : nwcFile.getTitle();

	currentSect = new Sect(tune, 0L);
	currentBlock = new Block(currentSect, 0L);
	maxTime = Fraction.ZERO;

	currentSect.blocks.add(currentBlock);
	tune.sects.add(currentSect);

	for(nwcfile.Staff nwcStaff : nwcFile.getStaves()) {
	    Syst syst = new Syst(tune, nwcStaff.getName());
	    Staff staff = new Staff(syst, nwcStaff.getName());
	    syst.staves.add(staff);
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

    private StaffImporter minTimeStaff() {
	StaffImporter result = null;
	for (StaffImporter staff : staffImporters) {
	    if (staff.symbolContainerIterator.hasNext() && (result == null || result.currentTime.compareTo(staff.currentTime) > 0)) {
		result = staff;
	    }
	}
	return result;
    }

    private boolean areStavesSynchronized() {
	//	boolean sync = true;
	for (StaffImporter staff : staffImporters) {
	    //	    if (!staff.symbolContainerIterator.hasNext()) {
	    //		staff.currentTime = maxTime;
	    //	    }
	    //	    sync = sync && (staff.currentTime.equals(maxTime));
	    if (staff.symbolContainerIterator.hasNext() && !staff.currentTime.equals(maxTime)) {
		return false;
	    }
	}
	//	return sync;
	return true;
    }

    private boolean isSymbolLeft() {
	for (StaffImporter staffImporter : staffImporters) {
	    if (staffImporter.symbolContainerIterator.hasNext())
		return true;
	}
	return false;
    }

    private void addAllSymbols() {
	while(true) {
	    minTimeStaff().stepStaffSymbolsToBar();
	    if (areStavesSynchronized()) {
		if (isSymbolLeft()) {
		    currentBlock = new Block(currentSect, maxTime.longValue());
		    currentSect.blocks.add(currentBlock);
		} else {
		    return;
		}
	    }
	}
    }

    private static SymbolType toSymbolType(nwcfile.TimeSignature.Style style) {
	switch(style) {
	case COMMON_TIME:
	    return SymbolType.COMMON_TIME;
	case ALLA_BREVE:
	    return SymbolType.ALLA_BREVE;
	case STANDARD:
	    return SymbolType.STANDARD_TIME_SIGNATURE;
	default:
	    return null;
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

    private static NoteValue toNoteValue(nwcfile.TimeSignature.BeatValue beatValue) {
	switch(beatValue) {
	case WHOLE:
	    return NoteValue.WHOLE;
	case HALF:
	    return NoteValue.HALF;
	case QUARTER:
	    return NoteValue.QUARTER;
	case EIGHTH:
	    return NoteValue.EIGHTH;
	case SIXTEENTH:
	    return NoteValue.SIXTEENTH;
	case THIRTY_SECOND:
	    return NoteValue.THIRTY_SECOND;
	default:
	    return null;
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

    private static NotePitch toNotePitch(nwcfile.KeySignature nwcKeySignature, nwcfile.KeySignature.Note note) {
	return nwcKeySignature.isSharp(note) ? NotePitch.SHARP :
	    nwcKeySignature.isFlat(note) ? NotePitch.FLAT :
	    NotePitch.NATURAL;
    }


}
