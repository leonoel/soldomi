package utils.nwc;

import models.*;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

public class NwcFileImporter {

  private final nwcfile.NwcFile m_nwcFile;
  private Tune m_tune;
  private StaffImporter[] m_staves;
  private Measure m_currentMeasure;
  private List<TimeSignature> m_timeSignatures = new ArrayList<TimeSignature>();
  private List<KeySignature> m_keySignatures = new ArrayList<KeySignature>();

  public NwcFileImporter(nwcfile.NwcFile nwcFile) {
    super();
    m_nwcFile = nwcFile;
  }

  public Tune toTune() {
    m_tune = new Tune();
    m_tune.title = m_nwcFile.getTitle();
    if (m_tune.title.length() == 0)
      m_tune.title = "Untitled";

    List<nwcfile.Staff> nwcStaves = m_nwcFile.getStaves();
    m_staves = new StaffImporter[nwcStaves.size()];
    for (int i = 0; i < nwcStaves.size(); i++) {
      m_staves[i] = new StaffImporter(this, nwcStaves.get(i));
      m_tune.staves.add(m_staves[i].getStaff());
    }

    addAllSymbols();
    adjustTimeSignatures();
    propagateKeySignatures();

    return m_tune;
  }

  Tune getTune() {
    return m_tune;
  }

  void addTimeSignature(TimeSignature timeSignature) {
    m_timeSignatures.add(timeSignature);
  }

  void addKeySignature(KeySignature keySignature) {
    m_keySignatures.add(keySignature);
  }

  private void addAllSymbols() {
    m_currentMeasure = new Measure(m_tune, 0);
    m_currentMeasure.absolutePosition = 0.0f;

    while(isSymbolLeft()) {
      stepLatestStaff();
      if (areStavesSynchronized()) {
	m_tune.measures.add(m_currentMeasure);
	m_currentMeasure = new Measure(m_tune, m_tune.measures.size());
	m_currentMeasure.absolutePosition = absoluteTime();
      }
    }
  }

  private void adjustTimeSignatures() {
    Collections.sort(m_timeSignatures);
    Iterator<TimeSignature> it = m_timeSignatures.iterator();
    TimeSignature currentTs = null;
    TimeSignature nextTs = it.hasNext() ? it.next() : null;
    for (Measure measure : m_tune.measures) {
      float position = measure.absolutePosition;
      float duration = 0.0f;
      for (Segment segment : measure.segments) {
	float endPosition = segment.getRelativePosition() + segment.getDuration();
	if (duration < endPosition) {
	  duration = endPosition;
	}
      }

      while(nextTs != null &&
	    nextTs.position <= position) {
	currentTs = nextTs;
	nextTs = it.hasNext() ? it.next() : null;
      }

      if (currentTs != null) {
	measure.beatCount = currentTs.beatCount;
	measure.beatValue = currentTs.beatValue;
      } else {
	DurationSymbol beatValue = DurationSymbol.SIXTY_FOURTH;
	int beatCount = (int) Math.round(duration*16); // 16 SIXTY_FOURTH in a QUARTER
	while(beatCount % 2 == 0 && beatValue.ordinal() < DurationSymbol.QUARTER.ordinal()) {
	  beatCount = beatCount >> 1;
	  beatValue = DurationSymbol.values()[beatValue.ordinal() + 1];
	}
	measure.beatCount = beatCount;
	measure.beatValue = beatValue;
      }
    }
  }

  private void propagateKeySignatures() {
    Collections.sort(m_keySignatures);
    Iterator<KeySignature> it = m_keySignatures.iterator();
    KeySignature currentKs = null;
    KeySignature nextKs = it.hasNext() ? it.next() : null;
    for (Measure measure : m_tune.measures) {
      float position = measure.absolutePosition;
      while(nextKs != null && nextKs.position <= position) {
	currentKs = nextKs;
	nextKs = it.hasNext() ? it.next() : null;
      }

      if (currentKs != null) {
	measure.keySignature = currentKs.getMajorScale();
      } else {
	measure.keySignature = "C"; //new KeySignature(position);
      }
    }
  }

  private boolean isSymbolLeft() {
    for (StaffImporter staffImporter : m_staves) {
      if (staffImporter.isSymbolLeft())
	return true;
    }
    return false;
  }

  private void stepLatestStaff() {
    float minTime = 0.0f;
    StaffImporter staffToStep = null;
    for (StaffImporter staff : m_staves) {
      if (staff.isSymbolLeft()) {
	if (staffToStep == null || minTime >= staff.getTime()) {
	  staffToStep = staff;
	  minTime = staff.getTime();
	}
      }
    }
    staffToStep.stepOneMeasure(m_currentMeasure);
  }

  private boolean areStavesSynchronized() {
    float time = absoluteTime();
    for (StaffImporter staff : m_staves) {
      if (staff.isSymbolLeft() && Math.abs(time-staff.getTime())>0.01) {
	return false;
      }
    }
    return true;
  }

  private float absoluteTime() {
    float time = 0.0f;
    for (StaffImporter staff : m_staves) {
      if (time < staff.getTime()) {
	time = staff.getTime();
      }
    }
    return time;
  }
}
