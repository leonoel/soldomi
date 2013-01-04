package models;

import java.util.*;
import play.db.ebean.*;
import play.data.validation.Constraints.*;
import javax.persistence.*;

@Entity
public class Measure extends Model {

  public static Finder<Long, Measure> find = new Finder(Long.class,
							Measure.class);

  @Id
  public Long id;

  @ManyToOne
  public Tune tune;

  public Integer measureID;

  @Enumerated(EnumType.STRING)
  public DurationSymbol beatValue;

  public Integer beatCount; 

//  public KeySignature keySignature;
  public String keySignature;

  /*
   * From beginning of tune
   * Unit : 1.0f is a QUARTER. e.g.
   * In 4/4, each measure contains 4.0f Units
   * In 6/8, each measure contains 3.0f Units
   * In 7/8, each measure contains 3.5f Units
   */
  public float absolutePosition;

  @OneToMany(mappedBy="measure")
  public List<Segment> segments = new ArrayList<Segment>();

  public Measure(Tune tune, Integer measureID) {
    this.tune             = tune;
    this.measureID        = measureID;
    this.absolutePosition = 0.0f;
    this.beatCount        = 0;
    this.beatValue        = DurationSymbol.UNDEFINED;
  }

}
