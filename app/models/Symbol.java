package models;

import java.util.*;
import javax.persistence.*;

import play.db.jpa.*;

@Entity
public abstract class Symbol extends Model {
  @OneToOne(mappedBy="symbol")
  public SymbolContainer symbolContainer;

  public Symbol(SymbolContainer symbolContainer) {
    super();
    this.symbolContainer = symbolContainer;
  }

}