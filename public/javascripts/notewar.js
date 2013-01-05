function NoteWar() {}

NoteWar.init = function(canvas,url) {
    NoteWar.canvas = canvas;
    NoteWar.url = url;
    NoteWar.renderer = new Vex.Flow.Renderer(canvas,
					     Vex.Flow.Renderer.Backends.CANVAS);
    NoteWar.ctx = NoteWar.renderer.getContext();

    $.getJSON(url, NoteWar.renderTune);
}

NoteWar.renderTune = function(tune) {
    NoteWar.tune = tune;
    var xShift          = 10;
    var lineHeight      = 80;
    var measureMinWidth = 300;
    var keySettingWidth = 80;
    var xCoor = xShift, yCoor = 0;

    NoteWar.tune.measures = new Array();
    for(var measureId=0; measureId<tune.measureCount;measureId++) {
      NoteWar.tune.measures[measureId] = {};
      var lines       = new Array(); 
      for (var staffId in tune.staves) {
        lines[staffId]  = {};
        lines[staffId]["vexFlowStaff"] = new Vex.Flow.Stave(xCoor,yCoor,measureMinWidth); 
	if(measureId == tune.measureCount -1) lines[staffId].vexFlowStaff.setEndBarType(Vex.Flow.Barline.type.END);
        yCoor += lineHeight;
      }
      NoteWar.tune.measures[measureId]["lines"] = lines;
      $.getJSON(NoteWar.url + "/measures/" + measureId, NoteWar.renderMeasure);
      for(staffId in tune.staves) {
        xCoor  += NoteWar.tune.measures[measureId].lines[staffId].vexFlowStaff.width;
	break;
      }
      yCoor   = 0;
    }
}

NoteWar.renderMeasure = function(measure) {
//    NoteWar.measure = measure;
    var measureId = measure.measureId;
    var block     = NoteWar.tune.measures[measureId].this;
    for (var staffId in NoteWar.tune.staves) {
//	block.lines[staffId]["segments"] = new Array();
	NoteWar.tune.measures[measureId].lines[staffId]["segments"] = new Array();
    } 

    for (var i in measure.segments) {
	var segment  = measure.segments[i];
	var staffId  = segment.staffId;
	var inter = NoteWar.formatKeys(segment);
	NoteWar.tune.measures[measureId].lines[staffId].segments.push(
	    new Vex.Flow.StaveNote(
		{ keys: [NoteWar.formatKeys(segment)],
		  duration: NoteWar.formatDuration(segment),
		  clef: NoteWar.formatClef(segment),
//		  stem_direction : -1
		}));
	NoteWar.addOrnament(NoteWar.tune.measures[measureId].lines[staffId].segments[i],segment);
    }
     
    
    for (var staffId in NoteWar.tune.staves) {
      var vexFlowStaff = NoteWar.tune.measures[0].lines[staffId].vexFlowStaff;
      if(measureId == 0) { // First Measure, set keySignature
        vexFlowStaff.width += 0;
        vexFlowStaff.addClef(NoteWar.tune.measures[measureId].lines[staffId].segments[0].clef);
	vexFlowStaff.addKeySignature(measure.keySignature);
	vexFlowStaff.addTimeSignature(NoteWar.timeSignature(measure));

//        NoteWar.tune.measures[0].lines[staffId].vexFlowStaff.width += 0;
//        NoteWar.tune.measures[0].lines[staffId].vexFlowStaff.addClef(NoteWar.tune.measures[measureId].lines[staffId].segments[0].clef).addTimeSignature(NoteWar.timeSignature(measure));
      }
      NoteWar.tune.measures[measureId].lines[staffId].vexFlowStaff.setContext(NoteWar.ctx).draw();
      Vex.Flow.Formatter.FormatAndDraw(NoteWar.ctx,
        NoteWar.tune.measures[measureId].lines[staffId].vexFlowStaff,
        NoteWar.tune.measures[measureId].lines[staffId].segments);
    }
  /*
    for (var staffId in NoteWar.tune.staves) {
	var voice = new Vex.Flow.Voice({
	    num_beats: 4,
	    beat_value: 4,
	    resolution: Vex.Flow.RESOLUTION
	});
	voice.addTickables(NoteWar.tune.lines[staffId].measures[measureId].segments);
	var formatter = new Vex.Flow.Formatter().joinVoices([voice]).format([voice], 500);
	voice.draw(NoteWar.ctx, NoteWar.tune.lines[staffId].measures[measureId]);
    }
  */
}

NoteWar.addOrnament = function(vexFlowSegment,JSONSegment)
{
  if(JSONSegment.accidental < 5) {
	vexFlowSegment.addAccidental(0,new Vex.Flow.Accidental(NoteWar.StringFromAccidental(JSONSegment.accidental)));
  }
  var nDots = JSONSegment.dot;
  while(nDots>0) {
   vexFlowSegment.addDotToAll();
   nDots--;
  }
}

NoteWar.StringFromAccidental = function(accidental)
{
  switch(accidental) {
   case 0: return "#";
   case 1: return "b";
   case 2: return "n";
   case 3: return "##";
   case 4: return "bb";
   default: return ""; // TODO!!!! Need to define properly AUTO accidental. Pretty hard!
  }
}

NoteWar.formatKeys = function(segment) {
    return segment.note.toLowerCase() + NoteWar.StringFromAccidental(segment.accidental)+ '/' + segment.octave;
    return segment.note.toLowerCase() + '/' + segment.octave;
}

NoteWar.timeSignature = function(measure) {
  var count,value;
  count = measure.beatCount;
  switch(measure.beatValue) {
  case "QUARTER":
    value = "4";
    break;
  default: 
    document.write("Please inform what this beatValue is:"+measure.beatValue+"<br>");
    break;
  }
  return count+"/"+value;
}

NoteWar.formatDuration = function(segment) {
    var duration;
    switch(segment.durationSymbol) {
    case "WHOLE": // Ronde
	duration = "w";
	break;    
    case "HALF":          // Blanche
    case "DOTTED_HALF":   // Blanche Pointee
    case "D_DOTTED_HALF": // Blanche Dble Pointee
    case "THIRD":         // Triolet de Blanche
	duration = "h";
	break;
    case "QUARTER":          // Noire
    case "DOTTED_QUARTER":   // Noire Pointee
    case "D_DOTTED_QUARTER": // Noire Dble Pointe
    case "FIFTH":            // Quintolet de Noire
    case "SIXTH":            // Triolet de Noire
    case "SEVENTH":          // Septolet de Noire
	duration = "q";
	break;
    case "EIGHTH":           // Croche
    case "DOTTED_EIGHTH":    // Croche Pointee
    case "D_DOTTED_EIGHTH":  // Croche Dble Pointee
    case "TWELFTH":          // Triolet de Croche
	duration = "8";
	break; 
    case "SIXTEENTH":          // Double Croche
    case "DOTTED_SIXTEENTH":   // Double Croche Pointee
    case "D_DOTTED_SIXTEENTH": // Double Croche Double pointee
    case "TWENTY_FOURTH":      // Triolet de Double
	duration = "16";
	break; 
    case "THIRTY_SECOND":        // Triple Croche
    case "DOTTED_THIRTY_SECOND": // Triple Croche pointee
	duration = "32";
	break; 
    case "SIXTY_FOURTH":   // Quadruple Croche
	duration = "64";
	break; 
    default:
	break;
    }
    var rest = segment.rest ? "r" : "";
    return duration + rest;
}

NoteWar.formatClef = function(segment) {
    return segment.clef.toLowerCase();
}

