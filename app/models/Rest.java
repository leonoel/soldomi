package models;

import java.util.*;
import javax.persistence.*;

import play.db.jpa.*;

@Entity
public class Rest extends Symbol {

  public Rest(SymbolContainer symbolContainer) {
    super(symbolContainer);
  }

}