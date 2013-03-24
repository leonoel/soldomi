package controllers;

import java.util.*;
import java.io.*;

import play.*;
import play.data.*;
import play.mvc.*;
import play.mvc.Http.*;
import play.mvc.Http.MultipartFormData.*;

import models.Tune;
import models.Preset;
import views.html.*;
import utils.nwc.*;

public class HtmlApi extends Controller {
    
    public static Result index() {
	return ok(views.html.index.render());
    }

    public static Result tunes() {
	List<Tune> tunes = Tune.getAll.execute(null);
	return ok(views.html.tunes.render(tunes));
    }

    public static Result createNew() {
	DynamicForm requestData = Form.form().bindFromRequest();
//	Tune tune = Tune.insert.execute(Tune.makeBlank(requestData.get("name")));
        Tune tune = Tune.createNewTune(requestData.get("name"));
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
		Tune tune = Tune.insert.execute(NwcFileImporter.run(nwcfile));
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

    public static Result showTune(Long id) {
	Tune tune = Tune.get.execute(id);
	return ok(views.html.showTune.render(tune));
    }

    public static Result presets() {
	List<Preset> presets = Preset.getAll.execute(null);
	return ok(views.html.presets.render(presets));
    }

    public static Result deletePreset(Long id) {
	Preset.delete.execute(id);
	return redirect(routes.HtmlApi.presets());
    }

    public static Result sf2FileExtractor() {
	return ok(views.html.sf2FileExtractor.render());
    }
}
