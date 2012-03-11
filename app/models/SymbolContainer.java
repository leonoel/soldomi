package models;

import java.util.*;
import javax.persistence.*;

import play.db.jpa.*;

@Entity
public class SymbolContainer extends Model {
  public enum SymbolType {
    NOTE,
    REST;
  }

  @ManyToOne
  public Staff staff;

  @Enumerated(EnumType.STRING)
  public SymbolType type;

  @OneToOne(cascade=CascadeType.ALL)
  public Symbol symbol;

  public SymbolContainer(Staff staff) {
    this.staff = staff;
  }

}
