var SolDoMi = (function() {

    // Private

    var _canvas;
    var _url;
    var _renderer;
    var _ctx;

    var _renderTune = function(tune) {
	_tune = tune;
	_tune.measures = new Array();
	var xShift          = 10;
	var lineHeight      = 80;
	var measureMinWidth = 300;
	var keySettingWidth = 80;
	var xCoor = xShift, yCoor = 0;
	for(var measureId = 0; measureId < tune.measureCount; measureId++) {
	    _tune.measures[measureId] = {};
	    var lines = new Array(); 
	    for (var staffId in _tune.staves) {
		lines[staffId]  = {};
		lines[staffId]["vexFlowStaff"] = new Vex.Flow.Stave(xCoor,yCoor,measureMinWidth); 
		if(measureId == _tune.measureCount -1) lines[staffId].vexFlowStaff.setEndBarType(Vex.Flow.Barline.type.END);
		yCoor += lineHeight;
	    }
	    _tune.measures[measureId]["lines"] = lines;
	    $.getJSON(_url + "/measures/" + measureId, _renderMeasure);
	    for(staffId in _tune.staves) {
		xCoor  += _tune.measures[measureId].lines[staffId].vexFlowStaff.width;
		break;
	    }
	    yCoor = 0;
	}
    }


    var _renderMeasure = function(measure) {
	//    NoteWar.measure = measure;
	var measureId = measure.measureId;
	var block     = _tune.measures[measureId].this;
	for (var staffId in _tune.staves) {
	    //	block.lines[staffId]["segments"] = new Array();
	    _tune.measures[measureId].lines[staffId]["segments"] = new Array();
	} 

	for (var i in measure.segments) {
	var segment  = measure.segments[i];
	var staffId  = segment.staffId;
	    var inter = _formatKeys(segment);
	    _tune.measures[measureId].lines[staffId].segments.push(
		new Vex.Flow.StaveNote(
		    { keys: [_formatKeys(segment)],
		      duration: _formatDuration(segment),
		      clef: _formatClef(segment),
		      //		  stem_direction : -1
		    }));
	    _addOrnament(_tune.measures[measureId].lines[staffId].segments[i],segment);
	}
	
    
	for (var staffId in _tune.staves) {
	    var vexFlowStaff = _tune.measures[0].lines[staffId].vexFlowStaff;
	    if(measureId == 0) { // First Measure, set keySignature
		vexFlowStaff.width += 0;
		vexFlowStaff.addClef(_tune.measures[measureId].lines[staffId].segments[0].clef);
		vexFlowStaff.addKeySignature(measure.keySignature);
		vexFlowStaff.addTimeSignature(_timeSignature(measure));

		//        NoteWar.tune.measures[0].lines[staffId].vexFlowStaff.width += 0;
		//        NoteWar.tune.measures[0].lines[staffId].vexFlowStaff.addClef(NoteWar.tune.measures[measureId].lines[staffId].segments[0].clef).addTimeSignature(NoteWar.timeSignature(measure));
	    }
	    _tune.measures[measureId].lines[staffId].vexFlowStaff.setContext(_ctx).draw();
	    Vex.Flow.Formatter.FormatAndDraw(_ctx,
					     _tune.measures[measureId].lines[staffId].vexFlowStaff,
					     _tune.measures[measureId].lines[staffId].segments);
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

    var _addOrnament = function(vexFlowSegment, JSONSegment) {
	if(JSONSegment.accidental < 5) {
	    vexFlowSegment.addAccidental(0, new Vex.Flow.Accidental(_StringFromAccidental(JSONSegment.accidental)));
	}
	var nDots = JSONSegment.dot;
	while(nDots>0) {
	    vexFlowSegment.addDotToAll();
	    nDots--;
	}
    }

    var _StringFromAccidental = function(accidental) {
	switch(accidental) {
	case 0: return "#";
	case 1: return "b";
	case 2: return "n";
	case 3: return "##";
	case 4: return "bb";
	default: return ""; // TODO!!!! Need to define properly AUTO accidental. Pretty hard!
	}
    }

    var _formatKeys = function(segment) {
	return segment.note.toLowerCase() + _StringFromAccidental(segment.accidental)+ '/' + segment.octave;
	return segment.note.toLowerCase() + '/' + segment.octave;
    }

    var _timeSignature = function(measure) {
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

    var _formatDuration = function(segment) {
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

    var _formatClef = function(segment) {
	return segment.clef.toLowerCase();
    }


    // Public

    var SolDoMi = {};

    SolDoMi.init = function(canvas, url) {
	_canvas = canvas;
	_url = url;
	_renderer = new Vex.Flow.Renderer(_canvas,
					  Vex.Flow.Renderer.Backends.CANVAS);
	_ctx = _renderer.getContext();

	$.getJSON(_url, _renderTune);
    }

    return SolDoMi;
})();
