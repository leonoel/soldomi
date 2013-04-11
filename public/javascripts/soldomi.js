var SolDoMi = (function() {

  // Private
  var canvas;
  var url;
  var renderer;
  var ctx;
  var tune;

  var renderTune = function(tune) {
    tune = tune;
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
*/
    // Initialization. vexFlowStaves is a table of nStaves*nBlocks vexFlowStaves
    var staffCounter   = 0;
    var blockCounter   = 0;
    tune.vexFlowStaves = new Array();
    for(var syst in tune.systs) {
      for(var staff in tune.systs[syst].staves) {
        tune.vexFlowStaves[staffCounter] = new Array();
        staffCounter++;
      }
    }

    for(var sect in tune.sects) {
      for(var block in tune.sects[sect].blocks) {
        staffCounter = 0;
        for(var syst in tune.systs) {
          for(var staff in tune.systs[syst].staves) {
            tune.vexFlowStaves[staffCounter][blockCounter] = new Vex.Flow.Stave(xCoor,yCoor,measureMinWidth);
 //           tune.vexFlowStaves[staffCounter][blockCounter].setContext(ctx).draw();
            yCoor += lineHeight;
            staffCounter++;
          }
        }
        xCoor += tune.vexFlowStaves[0][blockCounter].width;
        yCoor  = 0;
        blockCounter++;
      }
    }

//	if(blockCounter == 0) {
    // Add First Blocks Ornaments (connectors, Clef, TimeSignature, etc)

    tune.connectors = new Vex.Flow.StaveConnector(tune.vexFlowStaves[0][0],tune.vexFlowStaves[staffCounter-1][0]);
    tune.connectors.setType(Vex.Flow.StaveConnector.type.SINGLE);
    tune.connectors.setContext(ctx).draw();
//	}

    staffCounter = 0;
    blockCounter--;
    for(var syst in tune.systs) {
      for(var staff in tune.systs[syst].staves) {
        tune.vexFlowStaves[staffCounter][blockCounter].setEndBarType(Vex.Flow.Barline.type.END);
        staffCounter++;
      }
    }

    // Render on Screen
    blockCounter = 0;
    for(var sect in tune.sects) {
      for(var block in sect.blocks) {
        staffCounter = 0;
        for(var syst in tune.systs) {
          for(var staff in syst.staves) {
            tune.vexFlowStaff[staffCounter][blockCounter].setContext(ctx).draw();
     //       Vex.Flow.Formatter.FormatAndDraw(ctx,tune.vexFlowStaff[staffCounter][blockCounter]);
	  }
	}
      }
    }

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
