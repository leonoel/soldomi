package controllers;

import java.util.*;
import java.io.*;
import java.sql.SQLException;

import play.*;
import play.db.*;
import play.data.*;
import play.mvc.*;
import play.mvc.Http.*;
import play.mvc.Http.MultipartFormData.*;

import views.html.index;
import views.html.tunes;
import org.soldomi.support.nwc175.*;
import org.soldomi.support.nwc175.model.*;
import org.soldomi.model.tune2.*;

public class HtmlApi extends Controller {
    
    public static Result index() {
	return ok(views.html.index.render());
    }

    public static Result tunes() {
	List<Tune> tunes = TuneDao.getAllTunes.runInTransaction(DB.getConnection(), null).value();
	return ok(views.html.tunes.render(tunes));
    }

    public static Result createNew() {
	DynamicForm requestData = Form.form().bindFromRequest();
//	Tune tune = Tune.insert.execute(Tune.makeBlank(requestData.get("name")));
//        Tune tune = Tune.createNewTune(requestData.get("name"));
	Tune tune = new Tune(requestData.get("name"));
        tune = TuneDao.insertTune.runInTransaction(DB.getConnection(), tune).value();
	return redirect(routes.HtmlApi.showTune(tune.id));
    }

    public static Result deleteTune(Long id) {
// TODO: Implement the deleteTune method in TuneDao.java
//	Tune.delete.execute(id);
	return TODO;
//	return redirect(routes.HtmlApi.tunes());
    }

    public static Result importNwc() {
	MultipartFormData body = request().body().asMultipartFormData();
	FilePart nwc = body.getFile("nwc");
	if (nwc != null) {
	    String fileName = nwc.getFilename();
	    String contentType = nwc.getContentType(); 
	    File file = nwc.getFile();
	    try {
		NwcFileReader reader = new NwcFileReader(new FileInputStream(file));
		NwcFile nwcfile = new NwcFile().unmarshall(reader);
		Tune tune = NwcFileImporter.run(nwcfile);
		tune = TuneDao.insertTuneWithSystsAndSects.runInTransaction(DB.getConnection(), tune).value();
		return redirect(routes.HtmlApi.showTune(tune.id));
	    } catch (NwcFileException e) {
		e.printStackTrace();
		flash("error", "Error parsing nwc file.");
	    } catch (FileNotFoundException e) {
		e.printStackTrace();
		flash("error", "Error opening nwc file.");
	    }
	} else {
	    flash("error", "Missing file");
	}
	return redirect(routes.HtmlApi.tunes());
    }

    public static Result showTune(Long id) {
	Tune tune = TuneDao.getTune.runInTransaction(DB.getConnection(), id).value();
	return ok(views.html.showTune.render(tune));
    }

    public static Result presets() {
//	List<Preset> presets = Preset.getAll.execute(null);
	return TODO; //ok(views.html.presets.render(presets));
    }

    public static Result deletePreset(Long id) {
//	Preset.delete.execute(id);
	return TODO; //redirect(routes.HtmlApi.presets());
    }

    public static Result sf2FileExtractor() {
	return ok(views.html.sf2FileExtractor.render());
    }
}
