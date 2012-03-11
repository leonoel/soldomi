package controllers;

import play.*;
import play.mvc.*;
import models.*;
import com.google.gson.*;
import java.lang.reflect.Type;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class JsonApi extends Controller {
  public static void getTune(long tuneId) {
    Tune tune = Tune.findById(tuneId);
    renderJSON(tune,
	       new JsonSerializer<Tune>() {
		 public JsonElement serialize(Tune tune,
					      Type type,
					      JsonSerializationContext context) {
		   JsonObject obj = new JsonObject();
		   obj.addProperty("title", tune.title);
		   obj.addProperty("measureCount", tune.measures.size());
		   Map<Long, Staff> staves = new HashMap<Long, Staff>();
		   for (Staff staff : tune.staves) {
		     staves.put(staff.id, staff);
		   }
		   obj.add("staves", context.serialize(staves));
		   return obj;
		 }
	       },
	       new JsonSerializer<Staff>() {
		 public JsonElement serialize(Staff staff,
					      Type type,
					      JsonSerializationContext context) {
		   JsonObject obj = new JsonObject();
		   obj.addProperty("name", staff.name);
		   return obj;
		 }
	       });
  }

  /*
  public static void getSegmentsFromTimeInterval(long tuneId, long from, long to) {
    List<Segment> segments = Segment.find("select s from Segment s, Tune t "
					  + "where t.id = ? "
					  + "and s.tune = t "
					  + "and s.absolutePosition > ? "
					  + "and s.absolutePosition < ?"
					  , tuneId
					  , from
					  , to
					  ).fetch();
    renderJSON(segments,
	       new JsonSerializer<Segment>() {
		 public JsonElement serialize(Segment segment,
					      Type type,
					      JsonSerializationContext context) {
		   JsonObject obj = new JsonObject();
		   obj.addProperty("staffId", segment.staff.id);
		   obj.addProperty("relativePitch", segment.relativePitch);
		   obj.addProperty("absolutePosition", segment.absolutePosition);
		   obj.addProperty("duration", segment.duration);
		   return obj;
		 }
	       });
  }
  */

  public static void getMeasure(long tuneId, int measurePosition) {
    Tune tune = Tune.findById(tuneId);
    Measure measure = tune.measures.get(measurePosition);
    renderJSON(measure,
	       new JsonSerializer<Measure>() {
		 public JsonElement serialize(Measure measure,
					      Type type,
					      JsonSerializationContext context) {
		   JsonObject obj = new JsonObject();
		   obj.addProperty("absolutePosition", measure.absolutePosition);
		   obj.addProperty("beatCount", measure.beatCount);
		   obj.addProperty("beatValue", measure.beatValue.name());
		   obj.add("segments", context.serialize(measure.segments));
		   return obj;
		 }
	       },
	       new JsonSerializer<Segment>() {
		 public JsonElement serialize(Segment segment,
					      Type type,
					      JsonSerializationContext context) {
		   JsonObject obj = new JsonObject();
		   obj.addProperty("staffId", segment.staff.id);
		   obj.addProperty("clef", segment.clef.name());
		   obj.addProperty("note", segment.pitch.note.name());
		   obj.addProperty("octave", segment.pitch.octave);
		   obj.addProperty("relativePosition", segment.getRelativePosition());
		   obj.addProperty("durationSymbol", segment.durationSymbol.name());
		   obj.addProperty("rest", segment.rest);
		   return obj;
		 }
	       });
  }
}
