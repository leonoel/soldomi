package controllers;

import play.*;
import play.mvc.*;

import java.util.*;

import models.*;
import utils.nwc.*;
import java.io.*;

import nwcfile.*;

public class Application extends Controller {

  public static void index() {
    List<Tune> tunes = Tune.find("order by lastModifAt desc").from(0).fetch(10);
    render(tunes);
  }

  public static void show(long id) {
    Tune tune = Tune.findById(id);
    render(tune);
  }

  public static void uploadNwc(File nwc) {
    if (nwc == null) {
      validation.addError("nwcfile", "Please select a file.");
      validation.keep();
    } else {
      try {
	NwcFileReader reader = new NwcFileReader(new FileInputStream(nwc));
	NwcFile nwcfile = new NwcFile().unmarshall(reader);
	Tune tune = new NwcFileImporter(nwcfile).toTune();
	tune.save();
      } catch (NwcFileException e) {
	validation.addError("nwcfile", "Error parsing nwc file.");
	validation.keep();
      } catch (FileNotFoundException e) {
	validation.addError("nwcfile", "Error opening nwc file.");
	validation.keep();
      }
    }
    index();
  }

}