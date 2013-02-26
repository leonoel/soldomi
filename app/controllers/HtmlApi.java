package controllers;

import java.util.*;
import java.io.*;

import play.*;
import play.data.*;
import play.mvc.*;
import play.mvc.Http.*;
import play.mvc.Http.MultipartFormData.*;

import models.Tune;
import views.html.*;
import utils.nwc.*;

public class HtmlApi extends Controller {
    
    public static Result index() {
	return redirect(routes.HtmlApi.tunes());
    }

    public static Result tunes() {
	List<Tune> tunes = Tune.getAll.execute(null);
	return ok(views.html.index.render(tunes));
    }

    public static Result createNew() {
	DynamicForm requestData = Form.form().bindFromRequest();
	Tune tune = Tune.insert.execute(Tune.makeBlank(requestData.get("name")));
	return redirect(routes.HtmlApi.showTune(tune.id));
    }

    public static Result deleteTune(Long id) {
	Tune.delete.execute(id);
	return redirect(routes.HtmlApi.tunes());
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
		Long id = new NwcFileImporter(nwcfile).save();
		return redirect(routes.HtmlApi.showTune(id));
	    } catch (nwcfile.NwcFileException e) {
		flash("error", "Error parsing nwc file.");
	    } catch (FileNotFoundException e) {
		flash("error", "Error opening nwc file.");
	    }
	} else {
	    flash("error", "Missing file");
	}
	return redirect(routes.HtmlApi.tunes());
    }

    public static Result showTune(Long id) {
	Tune tune = Tune.get.execute(id);
	return ok(views.html.showTune.render(tune));
    }
}
