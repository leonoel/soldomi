package models;

import java.util.*;
import javax.persistence.*;

import play.db.jpa.*;

@Entity
public class Tune extends Model {

  public String title;

  public Date lastModifAt;

  @OneToMany(mappedBy="tune",
	     cascade=CascadeType.ALL)
  public List<Segment> segments = new ArrayList<Segment>();

  @OneToMany(mappedBy="tune",
	     cascade=CascadeType.ALL)
  public List<Staff> staves = new ArrayList<Staff>();

  @OneToMany(mappedBy="tune",
	     cascade=CascadeType.ALL)
  public List<Measure> measures = new ArrayList<Measure>();

  public Tune() {
    this.lastModifAt = new Date();
  }

}