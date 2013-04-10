package controllers;

import java.util.*;
import java.io.*;
import java.text.*;
import play.*;
import play.mvc.Result;
import play.mvc.Controller;

import models.Tune;
import models.Staff;
import models.Syst;
import models.Block;
import models.Sect;
import models.Preset;
import models.Symbol;
import models.Symbol.Position;

import utils.DaoAction;
import utils.DaoAction.DaoException;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ObjectNode;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;

public class JsonApi extends Controller {

    public static Result tuneInfo(Long id) throws IOException, DaoException {
	Tune tune = Tune.get.execute(id);

	ObjectMapper mapper = new ObjectMapper();
	mapper.configure(SerializationConfig.Feature.INDENT_OUTPUT, true);

	ObjectNode tuneJson = mapper.createObjectNode();
	tuneJson.put("name", tune.name);
	tuneJson.put("lastModified", new SimpleDateFormat().format(tune.lastModified));

	ArrayNode systsJson = tuneJson.putArray("systs");
	for (Syst syst : tune.systs) {
	    ObjectNode systJson = systsJson.addObject();
	    systJson.put("id", syst.id);
	    systJson.put("name", syst.name);
	    ArrayNode staffsJson = systJson.putArray("staffs");
	    for (Staff staff: syst.staffs) {
		ObjectNode staffJson = staffsJson.addObject();
		staffJson.put("id", staff.id);
		staffJson.put("name", staff.name);
	    }
	}

	ArrayNode sectsJson = tuneJson.putArray("sects");
	for (Sect sect : tune.sects) {
	    ObjectNode sectJson = sectsJson.addObject();
	    sectJson.put("id", sect.id);
	    sectJson.put("startTime", sect.startTime);
	    ArrayNode blocksJson = sectJson.putArray("blocks");
	    for (Block block : sect.blocks) {
		ObjectNode blockJson = blocksJson.addObject();
		blockJson.put("id", block.id);
		blockJson.put("startTime", block.startTime);
	    }
	}

	StringWriter writer = new StringWriter();
	mapper.writeValue(writer, tuneJson); 

	return ok(writer.toString());
  }

    public static Result symbols(Long tuneId, Long staffId, Long blockId) throws IOException, DaoException {
	List<Symbol> symbols = Symbol.getAll.execute(new Position(new Staff(staffId), new Block(blockId)));

	ObjectMapper mapper = new ObjectMapper();
	mapper.configure(SerializationConfig.Feature.INDENT_OUTPUT, true);

	ArrayNode symbolsJson = mapper.createArrayNode();
	for (Symbol symbol : symbols) {
	    ObjectNode symbolJson = symbolsJson.addObject();
	    symbolJson.put("id", symbol.id);
	    symbolJson.put("type", symbol.symbolType.baseValue);
	    symbolJson.put("startTimeNumerator", symbol.startTime.getNumerator());
	    symbolJson.put("startTimeDenominator", symbol.startTime.getDenominator());

	    if (symbol.segment != null) {
		ObjectNode segmentJson = symbolJson.putObject("segment");
		segmentJson.put("id", symbol.segment.id);
		segmentJson.put("durationNumerator", symbol.segment.duration.getNumerator());
		segmentJson.put("durationDenominator", symbol.segment.duration.getDenominator());
		segmentJson.put("dotCount", symbol.segment.dotCount);
		
		if (symbol.segment.note != null) {
		    ObjectNode noteJson = segmentJson.putObject("note");
		    noteJson.put("id", symbol.segment.note.id);

		    ObjectNode notePitchJson = noteJson.putObject("pitch");
		    notePitchJson.put("noteName", symbol.segment.note.pitch.noteName.baseValue);
		    notePitchJson.put("octave", symbol.segment.note.pitch.octave);

		}
	    }
	}

	StringWriter writer = new StringWriter();
	mapper.writeValue(writer, symbolsJson); 

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

    public static Result preset(Long id) throws DaoException {
	Preset preset = Preset.get.execute(id);
	return ok(preset.js);
    }

    public static Result newPreset() throws DaoException {
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
    }

}
