package controllers;

import java.util.*;
import play.*;
import play.mvc.*;
import models.*;

import play.libs.Json;
import org.codehaus.jackson.node.*;

public class JsonApi extends Controller {

  public static Result tuneInfo(Long id) {
    Tune tune = Tune.find.select("title, measures, staves").where().eq("id", id).findUnique();
    ObjectNode tuneJson = Json.newObject();
    tuneJson.put("title", tune.title);
    tuneJson.put("measureCount", tune.measures.size());
    ObjectNode stavesJson = tuneJson.putObject("staves");
    for (Staff staff : tune.staves) {
      ObjectNode staffJson = Json.newObject();
      staffJson.put("name", staff.name);
      stavesJson.put(staff.id.toString(), staffJson);
    }
    return ok(tuneJson);
  }

  public static Result measureInfo(Long tuneId, Integer measurePosition) {
    Measure measure = Measure.find.fetch("tune", "id")
      .select("beatCount, beatValue, absolutePosition, segments")
      .where().eq("tune.id", tuneId).eq("relativePosition", measurePosition).findUnique();
    List<Segment> segments = Segment.find.fetch("measure", "relativePosition").fetch("tune", "id")
      .where()
      .eq("measure.relativePosition", measurePosition)
      .eq("tune.id", tuneId)
      .findList();

    ObjectNode measureJson = Json.newObject();
    measureJson.put("measureId",        measurePosition);
    measureJson.put("absolutePosition", measure.absolutePosition);
    measureJson.put("beatCount",        measure.beatCount);
    measureJson.put("beatValue",        measure.beatValue.name());
    measureJson.put("keySignature",     measure.keySignature); //measure.keySignature.getMajorScale());
    ArrayNode segmentsJson = measureJson.putArray("segments");
    for (Segment segment : segments) {
      ObjectNode segmentJson = Json.newObject();
      segmentJson.put("staffId",          segment.staff.id);
      segmentJson.put("clef",             segment.clef.name());
      segmentJson.put("note",             segment.pitch.note.name());
      segmentJson.put("accidental",       segment.accidental);
      segmentJson.put("dot",              segment.dot);
      segmentJson.put("octave",           segment.pitch.octave);
      segmentJson.put("relativePosition", segment.getRelativePosition());
      segmentJson.put("durationSymbol",   segment.durationSymbol.name());
      segmentJson.put("rest",             segment.rest);
      segmentsJson.add(segmentJson);
    }
    return ok(measureJson);
  }

}
