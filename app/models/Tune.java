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
  public List<Staff> staves = new ArrayList<Staff>();

  public Tune() {
    this.lastModifAt = new Date();
  }

  public Tune fromNwc(nwcfile.NwcFile nwcFile) {
    this.title = nwcFile.getTitle();
    if (this.title.length() == 0)
      this.title = "Untitled";
    for (nwcfile.Staff staff : nwcFile.getStaves()) {
      this.staves.add(new Staff(this).fromNwc(staff));
    }
    this.lastModifAt = new Date();
    return this;
  }

}