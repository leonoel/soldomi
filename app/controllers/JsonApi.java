package controllers;

import java.util.*;
import java.io.*;
import java.text.*;
import play.*;
import play.db.DB;
import play.mvc.Result;
import play.mvc.Controller;

import models.Preset;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import org.soldomi.model.tune2.TuneDao;
import org.soldomi.model.tune2.TuneJson;

public class JsonApi extends Controller {

    private static String indent(JsonNode node) {
	ObjectMapper mapper = new ObjectMapper();
	mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
	StringWriter writer = new StringWriter();
	try {
	    mapper.writeValue(writer, node);
	} catch (IOException e) {
	    return e.getMessage();
	}
	return writer.toString();
    }

    public static Result tuneInfo(Long id) {
	JsonNode json = TuneJson.tuneWithSystsAndSects.write(TuneDao.getTuneWithSystsAndSects.runInTransaction(DB.getConnection(), id).value());
	return ok(indent(json));
  }

    public static Result symbols(Long blockId) {
	return ok(indent(TuneJson.symbols.write(TuneDao.getBlockSymbolsFull.runInTransaction(DB.getConnection(), blockId).value())));
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
    mapper.configure(SerializationFeature.INDENT_OUTPUT, true);

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

    public static Result preset(Long id) {
        return ok("TODO");
/*
	Preset preset = Preset.get.execute(id);
	return ok(preset.js);
*/
    }

    public static Result newPreset() {
        return ok("TODO");
/*
	JsonNode node = request().body().asJson();
	JsonNode nameNode = node.get("name");
	JsonNode jsNode = node.get("js");
	if (nameNode == null || !nameNode.isTextual()) {
	    return badRequest("Missing name");
	}
	if (jsNode == null || !jsNode.isTextual()) {
	    return badRequest("Missing js");
	}
	java.lang.System.out.println(jsNode.getTextValue());
	Preset preset = new Preset();
	preset.name = nameNode.getTextValue();
	preset.js = jsNode.getTextValue();
	Preset.insert.execute(preset);
	return ok(preset.id.toString());
*/
    }

}
