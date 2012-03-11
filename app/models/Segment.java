package models;

import java.util.*;
import javax.persistence.*;

import play.db.jpa.*;

@Entity
public class Segment extends Model {

  @ManyToOne
  public Tune tune;

  @ManyToOne
  public Staff staff;

  @ManyToOne
  public Measure measure;

  public boolean rest;

  @Enumerated(EnumType.STRING)
  public Clef clef;

  @Embedded
  public Pitch pitch;
  
  public long absolutePosition;

  @Enumerated(EnumType.STRING)
  public DurationSymbol durationSymbol;

  public Segment(Staff staff, Measure measure) {
    this.tune = staff.tune;
    this.staff = staff;
    this.measure = measure;
  }

  public long getRelativePitch() {
    return Pitch.interval(this.clef.getPitch(), this.pitch);
  }

  public long getDuration() {
    return this.durationSymbol.toSixtyFourths();
  }

  public long getRelativePosition() {
    return this.absolutePosition - this.measure.absolutePosition;
  }
}