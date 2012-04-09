package models;

import java.util.*;
import play.db.ebean.*;
import play.data.validation.Constraints.*;
import javax.persistence.*;

@Entity
public class Staff extends Model {
  @Id
  public Long id;

  @ManyToOne
  public Tune tune;

  public String name;

  @OneToMany(mappedBy="staff")
  public List<Segment> segments = new ArrayList<Segment>();

  public Staff(Tune tune) {
    this.tune = tune;
  }

}
