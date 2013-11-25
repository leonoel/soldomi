var SolDoMi = (function() {

  // Private
  var canvas;
  var url;
  var renderer;
  var ctx;
  var tune;

  var renderTune = function(_tune) {
    tune = _tune;
    var xShift          = 30;
    var lineHeight      = 80;
    var measureMinWidth = 300;
    var keySettingWidth = 80;
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
    tune.measuresById    = {};
    tune.measuresByCoord = new Array();
/*
    // Count Staves and initialize measuresById array
    $.each(tune.systs,function(systNb,syst) {
      $.each(syst.staves,function(staffNb,staff) {
        tune.measuresById[staff.id] = {};
	tune.measuresByCoord.push(new Array());
      });
    });
*/
    // fill up VexFlow array. First indice is block.id 'cause we may usually want to render all staves but not necessarily all blocks
    $.each(tune.sects,function(sectNb,sect){
      $.each(sect.blocks,function(blockNb,block) {
        tune.measuresById[block.id] = {};
	blockNb = tune.measuresByCoord.push(new Array())-1; // push method of Array returns the length of the new Array. blockNb contains the correct indice
        $.each(tune.systs,function(systNb,syst) {
          $.each(syst.staves,function(staffNb,staff) {
            tune.measuresById[block.id][staff.id] = new Vex.Flow.Stave(xCoor,yCoor,measureMinWidth).setContext(ctx).draw();
            tune.measuresById[block.id][staff.id].segments = [];
  	    tune.measuresByCoord[blockNb].push(tune.measuresById[block.id][staff.id]); // Reference to the VexFlow object in a cartesian reference.
            yCoor += lineHeight;
          });
        });
        xCoor += tune.measuresByCoord[blockNb][0].width;
        yCoor  = 0;
        $.getJSON("/block/"+block.id+"/symbols/json",renderBlock);
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
             staff.setEndBarType(Vex.Flow.Barline.type.END).setContext(ctx).draw();
    });

    // Render on Screen
    $.each(tune.measuresByCoord,function(blockNb,block){
	$.each(block,function(staffNb,staff) {
	  console.log(staff);
     //     staff.setContext(ctx).draw();
          Vex.Flow.Formatter.FormatAndDraw(ctx,staff,staff.segments,true);
        });
    });
/*	  
    for(var block=0;block<nBlocks;block++) {
      for(var staff=0;staff<nStaves;staff++) {
        tune.vexFlowStaff[staff][block].setContext(ctx).draw();
      }
    }
*/

/*
        var lines = new Array(); 
        for (var staffId in tune.staves) {
    	lines[staffId]  = {};
    	lines[staffId]["vexFlowStaff"] = new Vex.Flow.Stave(xCoor,yCoor,measureMinWidth); 
    	if(measureId == tune.measureCount -1) lines[staffId].vexFlowStaff.setEndBarType(Vex.Flow.Barline.type.END);
    	yCoor += lineHeight;
        }
        tune.measures[measureId]["lines"] = lines;
//	    $.getJSON(url + "/measures/" + measureId, renderMeasure);
        for(staffId in tune.staves) {
    	xCoor  += tune.measures[measureId].lines[staffId].vexFlowStaff.width;
    	break;
        }
        yCoor = 0;
    }
*/
  }

  var renderBlock = function(symbols) {
  // console.log('ici');
    var blockId=symbols[0].blockId,staffId;
/*
    $.each(tune.measuresById[blockId],function(measureNb,measure) {
      measure.segments = [];
    });
*/
    $.each(symbols,function(i,symbol) {
      staffId = symbol.staffId; 
      var measure = tune.measuresById[blockId][staffId];
      renderSymbol(symbol,measure);
    });

    $.each(tune.measuresById[blockId],function(staffNb,staff) {
	staff.setContext(ctx).draw();
    });

    return;
  }

  var renderSymbol =function(symbol,measure) {
    var type = symbol.type;
    console.log(type);
    switch(type) {
      case "TREBLE_CLEF":    
	measure.addClef("treble");
	break;
      case "BASS_CLEF":      
	measure.addClef("bass");
	break;
      case "KEY_SIGNATURE":  
//	measure.addKeySignature(formatKeySignature(symbol));
	break;
      case "STANDARD_TIME_SIGNATURE": 
	measure.addTimeSignature(formatTimeSignature(symbol));
	break;
      case "WHOLE":
      case "HALF":
      case "QUARTER": // Vaudrait mieux que ce soit "Note" et que le type soit géré que par duration
      case "EIGTH":
      case "SIXTEENTH":
      case "SEGMENT":
	measure.segments.push(createNewSegment(symbol));
	break;
      default:
        console.log("Error, wrong type: ",type);
    }
    return;  
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
      consol.log("Please inform what this beatValue is:",symbol.timeSignature.beatValue);
      break;
    }
    return count+"/"+value;
  }

  var createNewSegment = function (segment) {
    return new Vex.Flow.StaveNote(
	{ keys :    [formatKeys(segment)],
	  duration: formatDuration(segment.note.duration)
	//  clef:     formatClef(segment),
          //stem_direction: -1
      	});
  }

  var formatKeys = function (segment) {
    return segment.note.pitch.name.toLowerCase()+formatAccidental(segment.note.accidental)
             +'/'+segment.note.pitch.octave;
  }

  var formatAccidental = function(accidental) {
    switch(accidental) {
    case "AUTO": return "";
    default: 
      console.log("Accidental problem: ", accidental);
      break;
    }
  }

  var formatDuration = function (duration) {
    var n = duration.n;
    var d = duration.d;

    return duration.n.toString();
  }

  var formatClef = function (segment) {
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
