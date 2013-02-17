package controllers;

import java.util.*;
import java.io.*;

import play.*;
import play.data.*;
import play.mvc.*;
import play.mvc.Http.*;
import play.mvc.Http.MultipartFormData.*;

import models.*;
import views.html.*;
import utils.nwc.*;
import dao.*;

public class Application extends Controller {
    
    public static Result index() {
	return redirect(routes.Application.tunes());
    }

    public static Result tunes() {
	List<TuneListDao.Tune> tunes = TuneListDao.getAll();
	List<Tune> toRender = new ArrayList<Tune>();
	for (TuneListDao.Tune tune : tunes) {
	    toRender.add(new Tune(tune.id(),
				  tune.name(),
				  tune.lastModified()));
	}
	return ok(views.html.index.render(toRender));
    }

    public static Result createNew() {
	DynamicForm requestData = Form.form().bindFromRequest();
	final String name = requestData.get("name");
	final List<NewTuneDao.Syst> systs = new ArrayList<NewTuneDao.Syst>();
	final List<NewTuneDao.Block> blocks = new ArrayList<NewTuneDao.Block>();
	Long id = NewTuneDao.create(new NewTuneDao.Tune() {
		@Override public String name() { return name; }
		@Override public List<NewTuneDao.Syst> systs() { return systs; }
		@Override public List<NewTuneDao.Block> blocks() { return blocks; }
	    });
	return redirect(routes.Application.showTune(id));
    }

    public static Result deleteTune(Long id) {
	TuneDao.delete(id);
	return redirect(routes.Application.tunes());
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
		return redirect(routes.Application.showTune(id));
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
	TuneDao.Tune tune = TuneDao.get(id);
	return ok(views.html.showTune.render(new Tune(id,
						      tune.name(),
						      tune.lastModified())));
    }
}
