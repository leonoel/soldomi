var SolDoMi = (function() {

    // Private
    var canvas;
    var url;
    var renderer;
    var ctx;
    var tune;
    var keySettingWidth = 8;

    var StaffClefManager = function() {
	var clefs = [];

	var addClefAtTime = function(clef, time) {
	    clefs.push({
		clef: clef,
		time: time
	    });
	    clefs.sort(function(entry1, entry2) {
		return entry1.time - entry2.time;
	    });
	};

	var getClefAtTime = function(time) {
	    var clef = "treble";
	    for (var i = 0; i < clefs.length; i++) {
		var entry = clefs[i];
		if (entry.time >= time) {
		    return entry.clef;
		} else {
		    clef = entry.clef;
		}
	    }
	    return clef;
	};

	return {
	    addClefAtTime: addClefAtTime,
	    getClefAtTime: getClefAtTime
	};
    };

    var AsyncResult = function() {
	var action;

	var bind = function(value) {
	    action(value);
	};

	var then = function(value) {
	    action = value;
	};

	return {
	    bind: bind,
	    then: then
	}
    };

    var getJSON = function(url) {
	var asyncResult = AsyncResult();
	$.getJSON(url, function(result) {
	    asyncResult.bind(result);
	});
	return asyncResult;
    };

    var sequence = function(asyncResults) {
	var asyncResultSequence = AsyncResult();
	var resultSequence = [];
	$.each(asyncResults, function(i, asyncResult) {
	    asyncResult.then(function(result) {
		resultSequence.push(result);
		if (resultSequence.length === asyncResults.length) {
		    asyncResultSequence.bind(resultSequence);
		}
	    });
	});
	return asyncResultSequence;
    };

    var flatten = function(arr) {
	var reducer = function(acc, entry) { 
	    if (Array.isArray(entry)) {
		entry.reduce(reducer, acc);
	    } else {
		acc.push(entry);
	    }
            return acc;
	};
	return arr.reduce(reducer, []);
    };

    var renderTune = function(_tune) {
	tune = _tune;
	var xShift          = 30;
	var lineHeight      = 80;
	var measureMinWidth = 300;
	var xCoor = xShift, yCoor = 0;

	/*
	// Essai Pourrave
	var stave = new Array();
	stave[0]  = new Vex.Flow.Stave(xCoor,         0, measureMinWidth);
	stave[1]  = new Vex.Flow.Stave(xCoor,lineHeight, measureMinWidth);
	// Add a treble clef
	stave[0].addClef("treble");
	stave[0].setContext(ctx).draw();
	stave[1].addClef("bass");
	stave[1].setContext(ctx).draw();
	var notes = [
	// Dotted eighth E##
	new Vex.Flow.StaveNote({ keys: ["e##/5"], duration: "8d" }).
	addAccidental(0, new Vex.Flow.Accidental("##")).addDotToAll(),
	// Sixteenth Eb
	new Vex.Flow.StaveNote({ keys: ["eb/5"], duration: "16" }).
	addAccidental(0, new Vex.Flow.Accidental("#")),
	// Half D
	new Vex.Flow.StaveNote({ keys: ["d/5"], duration: "h" }),
	// Quarter Cm#5
	new Vex.Flow.StaveNote({ keys: ["c/5", "eb/5", "g#/5"], duration: "q" }).
	addAccidental(1, new Vex.Flow.Accidental("b")).
	addAccidental(2, new Vex.Flow.Accidental("#"))
	];
	// Helper function to justify and draw a 4/4 voice
	Vex.Flow.Formatter.FormatAndDraw(ctx, stave[0], notes);
	var connectors = new Vex.Flow.StaveConnector(stave[0],stave[1]);
	connectors.setType(Vex.Flow.StaveConnector.type.BRACE).setContext(ctx).draw();
	stave[0].setEndBarType(Vex.Flow.Barline.type.END);
	stave[1].setEndBarType(Vex.Flow.Barline.type.END);
	stave[0].setContext(ctx).draw();
	stave[1].setContext(ctx).draw();
	*/
	// ************* Initialization ****************************** 
	// measuresById is an object of object of nStaves*nBlocks vexFlowStaves. Its indices are the DB-ids
	// measuresByCoord is an Array of Array of vexFlowStaves. Its indices are cartesian

	var renderBlock = function(symbols) {
	    $.each(symbols,function(i,symbol) {
		var measure = tune.measuresById[symbol.blockId][symbol.staffId];
		var isSignature = renderSymbol(symbol, measure);
	//	if(isSignature) {
	//	  $.each(tune.measuresById[symbol.blockId],function(j,m) {
	//	    m.width += keySettingWidth;
   	//	  });
	//	}
	    });
	}

	var renderSymbol = function(symbol, measure) {
	    switch(symbolRole(symbol.type)) {
	    case "CLEF":    
		var clef = symbolClef(symbol.type);
		tune.staffClefManagers[symbol.staffId].addClefAtTime(clef, symbol.startTime);
		measure.addClef(clef);
		return true;
		break;
	    case "KEY_SIGNATURE":  
		//	measure.addKeySignature(formatKeySignature(symbol));
		// return true;
		break;
	    case "TIME_SIGNATURE": 
		measure.addTimeSignature(formatTimeSignature(symbol));
		return true;
		break;
	    case "NOTE":
		measure.segments.push(createNewNote(symbol));
		return false;
		break;
	    case "REST":
		measure.segments.push(createNewRest(symbol));
		return false;
		break;
	    default:
		console.log("Error, wrong type: ",type);
		return false;
	    }
	    return false;  
	}

	var formatKeySignature = function (symbol) {
	    return "";
	}

	var formatTimeSignature = function (symbol) {    
	    var count,value;
	    count = symbol.timeSignature.beatCount;
	    switch(symbol.timeSignature.beatValue) {
	    case "QUARTER":
		value = "4";
		break;
	    default: 
		console.log("Please inform what this beatValue is:",symbol.timeSignature.beatValue);
		break;
	    }
	    return count+"/"+value;
	}

	var createNewRest = (function() {
	    var durations = {
		"WHOLE_R": "1r",
		"HALF_R": "2r",
		"QUARTER_R": "4r",
		"EIGHTH_R": "8r",
		"SIXTEENTH_R": "16r",
		"THIRTY_SECOND_R": "32r",
		"SIXTY_FOURTH_R": "64r"
	    };
	    return function(symbol) {
		return new Vex.Flow.StaveNote({
		    keys: ["b/4"],
		    duration: durations[symbol.type]
		});
	    }
	})();

	var createNewNote = (function() {
	    var durations = {
		"WHOLE": "1n",
		"HALF": "2n",
		"QUARTER": "4n",
		"EIGHTH": "8n",
		"SIXTEENTH": "16n",
		"THIRTY_SECOND": "32n",
		"SIXTY_FOURTH": "64n"
	    };
	    return function (symbol) {
		var key = symbol.note.pitch.name.toLowerCase()+formatAccidental(symbol.note.accidental)
		    +'/'+symbol.note.pitch.octave;

		var note = new Vex.Flow.StaveNote(
		    { keys :    [key],
		      duration: durations[symbol.type], // Watch Out for Tuplets !!!
		      clef:     tune.staffClefManagers[symbol.staffId].getClefAtTime(symbol.startTime)
		      //stem_direction: -1
      		    });
		if(isAccidental(symbol)) { 
		  note.addAccidental(0,new Vex.Flow.Accidental(formatAccidental(symbol.note.accidental)));
		}
		return note;
	    }
	})();


	// TODO
	var formatAccidental = function(accidental) {
	    switch(accidental) {
	    case "AUTO":    return "";
	    case "SHARP":   return "#";
	    case "FLAT":    return "b";
	    case "NATURAL": return "n";
            case "DOUBLE_SHARP": return "##";
            case "DOUBLE_FLAT":  return "bb";
	    default: 
		console.log("Accidental problem: ", accidental);
		break;
	    }
	}

	// TODO, adapt for AUTO case
 	var isAccidental = function(symbol) {
	  return symbol.note.accidental != "AUTO";
	}
	var symbolClef = (function() {
	    var clefs = {
		"TREBLE_CLEF": "treble",
		"BASS_CLEF": "bass",
		"ALTO_CLEF": "TODO",
		"TENOR_CLEF": "TODO"
	    };
	    return function(symbolType) {
		return clefs[symbolType];
	    }
	})();

	var symbolOrdering = (function() {
	    var ordering = {
		"CLEF": 1,
		"KEY_SIGNATURE": 2,
		"TIME_SIGNATURE": 3,
		"REST": 4,
		"NOTE": 4
	    };
	    return function(symbolRole) {
		return ordering[symbolRole];
	    }
	})();

	var symbolRole = (function() {
	    var roles = {
		"WHOLE": "NOTE",
		"WHOLE_R": "REST",
		"HALF": "NOTE",
		"HALF_R": "REST",
		"QUARTER": "NOTE",
		"QUARTER_R": "REST",
		"EIGHTH": "NOTE",
		"EIGHTH_R": "REST",
		"SIXTEENTH": "NOTE",
		"SIXTEENTH_R": "REST",
		"THIRTY_SECOND": "NOTE",
		"THIRTY_SECOND_R": "REST",
		"SIXTY_FOURTH": "NOTE",
		"SIXTY_FOURTH_R": "REST",
		"TREBLE_CLEF": "CLEF",
		"BASS_CLEF": "CLEF",
		"ALTO_CLEF": "CLEF",
		"TENOR_CLEF": "CLEF",
		"KEY_SIGNATURE": "KEY_SIGNATURE",
		"STANDARD_TIME_SIGNATURE": "TIME_SIGNATURE",
		"ALLA_BREVE": "TIME_SIGNATURE",
		"COMMON_TIME": "TIME_SIGNATURE"// C ou CBarre... TODO
	    };
	    return function(symbolType) {
		return (symbolType in roles) ? roles[symbolType] : "UNKNOWN";
	    }
	})();

	tune.measuresById    = {};
	tune.measuresByCoord = new Array();

	// fill up VexFlow array. First indice is block.id 'cause we may usually want to render all staves but not necessarily all blocks
	$.each(tune.sects,function(sectNb,sect){
	    $.each(sect.blocks,function(blockNb,block) {
		tune.measuresById[block.id] = {};
		var measureX = tune.measuresByCoord.push(new Array())-1; // push method of Array returns the length of the new Array. measureX contains the correct indice
		$.each(tune.systs,function(systNb,syst) {
		    $.each(syst.staves,function(staffNb,staff) {
			var measure = new Vex.Flow.Stave(xCoor,yCoor,measureMinWidth);//.setContext(ctx).draw();
			measure.segments = [];
			tune.measuresById[block.id][staff.id] = measure;
  			tune.measuresByCoord[measureX].push(measure); // Reference to the VexFlow object in a cartesian reference.
			yCoor += lineHeight;
		    });
		});
		xCoor += tune.measuresByCoord[measureX][0].width;
		yCoor  = 0;
	    });
	});

	var blockIds = flatten($.map(tune.sects, function(sect) {
	    return $.map(sect.blocks, function(block) {
		return block.id;
	    });
	}));

	var staffIds = flatten($.map(tune.systs, function(syst) {
	    return $.map(syst.staves, function(staff) {
		return staff.id;
	    });
	}));

	// Build clef manager for each staff
	tune.staffClefManagers = (function() {
	    var obj = {};
	    $.each(staffIds, function(i, staffId) {
		obj[staffId] = StaffClefManager();
	    });
	    return obj;
	})();

	// Fetch and draw symbols
	sequence($.map(blockIds, function(blockId) {
	    return getJSON("/block/" + blockId + "/symbols/json");
	})).then(function(symbols) {
	    symbols = flatten(symbols).sort(function(symbol1, symbol2) {
		var dt = symbol1.startTime.n * symbol2.startTime.d - symbol2.startTime.n * symbol1.startTime.d;
		if (dt !== 0) {
		    return dt;
		} else {
		    return symbolOrdering(symbolRole(symbol1.type)) - symbolOrdering(symbolRole(symbol2.type));
		}
	    });
	    renderBlock(symbols);
	    $.each(blockIds, function(i, blockId) {
		$.each(staffIds, function(j, staffId) {
		    var measure = tune.measuresById[blockId][staffId];
		    measure.setContext(ctx).draw();
		    Vex.Flow.Formatter.FormatAndDraw(ctx, measure, measure.segments, true);
		});
	    });
	});

	
	var nbBlocks = tune.measuresByCoord.length;
	var nbStaves = tune.measuresByCoord[0].length;
	// Add First Blocks Ornaments (connectors, Clef, TimeSignature, etc)
	tune.connectors = new Vex.Flow.StaveConnector(tune.measuresByCoord[0][0],tune.measuresByCoord[0][nbStaves-1]);
	tune.connectors.setType(Vex.Flow.StaveConnector.type.SINGLE);
	tune.connectors.setContext(ctx).draw();
	// Put double Bars at the end of each staff
	$.each(tune.measuresByCoord[nbBlocks-1],function(i,staff){
            staff.setEndBarType(Vex.Flow.Barline.type.END).draw();
	});

    }


    // Public

    var SolDoMi = {};

    SolDoMi.init = function(_canvas,_url) {
	canvas   = _canvas;
	url      = _url;
	renderer = new Vex.Flow.Renderer(canvas,Vex.Flow.Renderer.Backends.CANVAS);
	ctx      = renderer.getContext();
	$.getJSON(url, renderTune);
    }

    return SolDoMi;
})();
