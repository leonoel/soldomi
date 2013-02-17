package controllers;

import java.util.*;
import java.io.*;
import java.text.*;
import play.*;
import play.mvc.*;
import models.*;
import dao.*;

import play.libs.Json;
import org.codehaus.jackson.node.*;
import org.codehaus.jackson.map.*;

public class JsonApi extends Controller {

    public static Result tuneInfo(Long id) throws IOException {
	TuneDao.Tune tune = TuneDao.get(id);

	//	Tune tune = Tune.find.select("name, measures, staffs").where().eq("id", id).findUnique();

	ObjectMapper mapper = new ObjectMapper();
	mapper.configure(SerializationConfig.Feature.INDENT_OUTPUT, true);

	ObjectNode tuneJson = Json.newObject();
	tuneJson.put("name", tune.name());
	tuneJson.put("lastModified", new SimpleDateFormat().format(tune.lastModified()));
	tuneJson.put("systCount", tune.systCount());

	//	tuneJson.put("blockCount", tune.blocks.size());

	//	ObjectNode staffsJson = tuneJson.putObject("staffs");
	//	for (Staff staff : tune.staffs) {
	//	    ObjectNode staffJson = Json.newObject();
	//	    staffJson.put("name", staff.name);
	//	    staffsJson.put(staff.id.toString(), staffJson);
	//	}

	StringWriter writer = new StringWriter();
	mapper.writeValue(writer, tuneJson); 

	return ok(writer.toString());
  }

  public static Result blockInfo(Long tuneId, Integer position) throws IOException {
      // TODO
      /*
    Block block = Block.find.fetch("tune", "id")
      .select("beatCount, beatValue, absolutePosition, segments")
      .where().eq("tune.id", tuneId).eq("position", position).findUnique();
    List<Segment> segments = Segment.find.fetch("measure", "position").fetch("tune", "id")
      .where()
      .eq("block.position", position)
      .eq("tune.id", tuneId)
      .findList();

    ObjectMapper mapper = new ObjectMapper();
    mapper.configure(SerializationConfig.Feature.INDENT_OUTPUT, true);

    ObjectNode blockJson = Json.newObject();
    blockJson.put("blockId",          position);
    blockJson.put("startTime",        block.startTime);
    blockJson.put("beatCount",        block.beatCount);
    blockJson.put("beatValue",        block.beatValue.name());
    ArrayNode segmentsJson = blockJson.putArray("segments");
    for (Segment segment : segments) {
      ObjectNode segmentJson = Json.newObject();
      segmentJson.put("staffId",          segment.symbol.staff.id);
      segmentJson.put("clef",             segment.clef.name());
      segmentJson.put("note",             segment.pitch.note.name());
      segmentJson.put("accidental",       segment.accidental.name());
      segmentJson.put("dotCount",              segment.dotCount);
      segmentJson.put("octave",           segment.pitch.octave);
      segmentJson.put("relativePosition", segment.relativePosition());
      segmentJson.put("durationSymbol",   segment.durationSymbol.name());
      segmentJson.put("rest",             segment.rest);
      segmentsJson.add(segmentJson);
    }


    StringWriter writer = new StringWriter();
    mapper.writeValue(writer, blockJson); 

    return ok(writer.toString());
      */
      return ok("");
  }

}
