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

  public Integer relativePosition;

  @Enumerated(EnumType.STRING)
  public DurationSymbol beatValue;

  public Integer beatCount; 

//  public KeySignature keySignature;
  public String keySignature;

  /*
   * From beginning of tune
   * Unit : 64ths
   */
  public Integer absolutePosition;

  @OneToMany(mappedBy="measure")
  public List<Segment> segments = new ArrayList<Segment>();

  public Measure(Tune tune, Integer relativePosition) {
    this.tune = tune;
    this.relativePosition = relativePosition;
    this.absolutePosition = 0;
    this.beatCount = 0;
    this.beatValue = DurationSymbol.UNDEFINED;
  }

}
