package controllers;

import java.util.*;
import java.io.*;
import java.sql.SQLException;

import play.*;
import play.data.*;
import play.mvc.*;
import play.mvc.Http.*;
import play.mvc.Http.MultipartFormData.*;

import models.Tune;
import models.Preset;
import views.html.index;
import views.html.tunes;
import utils.nwc.NwcFileImporter;
import utils.DaoAction;
import utils.DaoAction.DaoException;

public class HtmlApi extends Controller {
    
    public static Result index() {
	return ok(views.html.index.render());
    }

    public static Result tunes() throws DaoException {
	List<Tune> tunes = Tune.getAll.execute(null);
	return ok(views.html.tunes.render(tunes));
    }

    public static Result createNew() throws DaoException {
	DynamicForm requestData = Form.form().bindFromRequest();
//	Tune tune = Tune.insert.execute(Tune.makeBlank(requestData.get("name")));
        Tune tune = Tune.createNewTune(requestData.get("name"));
	return redirect(routes.HtmlApi.showTune(tune.id));
    }

    public static Result deleteTune(Long id) throws DaoException {
	Tune.delete.execute(id);
	return redirect(routes.HtmlApi.tunes());
    }

    public static Result importNwc() throws DaoException {
	MultipartFormData body = request().body().asMultipartFormData();
	FilePart nwc = body.getFile("nwc");
	if (nwc != null) {
	    String fileName = nwc.getFilename();
	    String contentType = nwc.getContentType(); 
	    File file = nwc.getFile();
	    try {
		nwcfile.NwcFileReader reader = new nwcfile.NwcFileReader(new FileInputStream(file));
		nwcfile.NwcFile nwcfile = new nwcfile.NwcFile().unmarshall(reader);
		Tune tune = NwcFileImporter.run(nwcfile);
		tune = Tune.insert.execute(tune);
		return redirect(routes.HtmlApi.showTune(tune.id));
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

    public static Result showTune(Long id) throws DaoException {
	Tune tune = Tune.get.execute(id);
	return ok(views.html.showTune.render(tune));
    }

    public static Result presets() throws DaoException {
	List<Preset> presets = Preset.getAll.execute(null);
	return ok(views.html.presets.render(presets));
    }

    public static Result deletePreset(Long id) throws DaoException {
	Preset.delete.execute(id);
	return redirect(routes.HtmlApi.presets());
    }

    public static Result sf2FileExtractor() {
	return ok(views.html.sf2FileExtractor.render());
    }
}
