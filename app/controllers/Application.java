package controllers;

import java.util.*;
import java.io.*;

import play.*;
import play.mvc.*;
import play.mvc.Http.*;
import play.mvc.Http.MultipartFormData.*;

import models.*;
import views.html.*;
import utils.nwc.*;

public class Application extends Controller {
  public static Result index() {
    return redirect(routes.Application.tunes());
  }

  public static Result tunes() {
    return ok(views.html.index.render(Tune.all()));
  }

  public static Result importNwc() {
    MultipartFormData body = request().body().asMultipartFormData();
    FilePart nwc = body.getFile("nwc");
    if (nwc != null) {
      String fileName = nwc.getFilename();
      String contentType = nwc.getContentType(); 
      File file = nwc.getFile();
      try {
	nwcfile.NwcFileReader reader = new nwcfile.NwcFileReader(new FileInputStream(file));
	nwcfile.NwcFile nwcfile = new nwcfile.NwcFile().unmarshall(reader);
	Tune tune = new NwcFileImporter(nwcfile).toTune();
	tune.save();
      } catch (nwcfile.NwcFileException e) {
	flash("error", "Error parsing nwc file.");
      } catch (FileNotFoundException e) {
	flash("error", "Error opening nwc file.");
      }
    } else {
      flash("error", "Missing file");
    }
    return redirect(routes.Application.tunes());
  }

  public static Result showTune(Long id) {
    return ok(views.html.showTune.render(Tune.find.select("title, measures, staves").where().eq("id", id).findUnique()));//Tune.find.ref(id)));
  }
}
