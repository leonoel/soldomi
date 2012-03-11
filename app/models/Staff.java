package models;

import java.util.*;
import javax.persistence.*;

import play.db.jpa.*;

@Entity
public class Staff extends Model {
  @ManyToOne
  public Tune tune;

  public String name;

  @OneToMany(mappedBy="staff",
	     cascade=CascadeType.ALL)
  public List<Segment> segments = new ArrayList<Segment>();

  public Staff(Tune tune) {
    this.tune = tune;
  }

}
