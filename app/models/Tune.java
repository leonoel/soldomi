package models;

import java.util.*;
import play.db.ebean.*;
import play.data.validation.Constraints.*;
import javax.persistence.*;

@Entity
public class Tune extends Model {
  
  public static Finder<Long, Tune> find = new Finder(Long.class,
						     Tune.class);

  @Id
  public Long id;

  public String title;

  public Date lastModifAt;

  @OneToMany(mappedBy="tune",
	     cascade=CascadeType.ALL)
  public List<Segment> segments = new ArrayList<Segment>();

  @OneToMany(mappedBy="tune")
  public List<Staff> staves = new ArrayList<Staff>();

  @OneToMany(mappedBy="tune")
  public List<Measure> measures = new ArrayList<Measure>();


  public Tune() {
    this.lastModifAt = new Date();
  }

  public static List<Tune> all() {
    return find.all();
  }
}