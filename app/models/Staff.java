package models;

import java.util.*;
import javax.persistence.*;

import play.db.jpa.*;

@Entity
public class Staff extends Model {
  @ManyToOne
  public Tune tune;

  public String name;

  public Staff(Tune tune) {
    this.tune = tune;
  }

  public Staff fromNwc(nwcfile.Staff staff) {
    this.name = staff.getName();
    return this;
  }

}
