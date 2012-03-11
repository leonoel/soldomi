package models;

import java.util.*;
import javax.persistence.*;

import play.db.jpa.*;

@Entity
public class Measure extends Model {

  @ManyToOne
  public Tune tune;

  @OneToMany(mappedBy="measure",
	     cascade=CascadeType.ALL)
  public List<Segment> segments; 

  @Enumerated(EnumType.STRING)
  public DurationSymbol beatValue;

  public long beatCount; 

  /*
   * From beginning of tune
   * Unit : measures
   */
  public long relativePosition;

  /*
   * From beginning of tune
   * Unit : 64ths
   */
  public long absolutePosition;

  public Measure(Tune tune) {
    this.tune = tune;
    this.relativePosition = 0;
    this.absolutePosition = 0;
    this.beatCount = 0;
    this.beatValue = DurationSymbol.UNDEFINED;
    this.segments = new ArrayList<Segment>();
  }

}