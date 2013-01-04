package models;

import java.util.*;
import play.db.ebean.*;
import play.data.validation.Constraints.*;
import javax.persistence.*;

@Entity
public class Segment extends Model {
  public static Finder<Long, Segment> find = new Finder(Long.class,
							Segment.class);

  @Id
  public Long id;

  @ManyToOne
  public Tune tune;

  @ManyToOne(cascade=CascadeType.ALL)
  public Staff staff;

  @ManyToOne(cascade=CascadeType.ALL)
  public Measure measure;

  public Boolean rest;

  @Enumerated(EnumType.STRING)
  public Clef clef;

  @Embedded
  public Pitch pitch;

  public Integer accidental;

  public Integer dot;
  
  public float absolutePosition;

  @Enumerated(EnumType.STRING)
  public DurationSymbol durationSymbol;

  public Segment(Tune tune, Staff staff, Measure measure) {
    this.tune = tune;
    this.staff = staff;
    this.measure = measure;
  }

  public Integer getRelativePitch() {
    return Pitch.interval(this.clef.getPitch(), this.pitch);
  }

  public Integer getDuration() {
    return this.durationSymbol.toSixtyFourths();
  }

  public float getRelativePosition() {
    return this.absolutePosition - this.measure.absolutePosition;
  }
}
