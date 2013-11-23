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
    // Initialization. measuresById is a table of nStaves*nBlocks vexFlowStaves
    tune.measuresById   = {};
    tune.measuresByCoord = new Array();
    // Count Staves and initialize measuresById array
    $.each(tune.systs,function(systNb,syst) {
      $.each(syst.staves,function(staffNb,staff) {
        tune.measuresById[staff.id] = {};
	tune.measuresByCoord.push(new Array());
      });
    });
    // Count Blocks and start filling up VexFlow array
    var staffCounter = 0;
    $.each(tune.sects,function(sectNb,sect){
      $.each(sect.blocks,function(blockNb,block) {
        $.each(tune.systs,function(systNb,syst) {
          $.each(syst.staves,function(staffNb,staff) {
            tune.measuresById[staff.id][block.id] = new Vex.Flow.Stave(xCoor,yCoor,measureMinWidth);
  	    tune.measuresByCoord[staffCounter].push(tune.measuresById[staff.id][block.id]);
            yCoor += lineHeight;
	    staffCounter++;
          });
        });
        xCoor += tune.measuresByCoord[0][blockNb].width;
        yCoor  = 0;
	staffCounter = 0;
        $.getJSON("/block/"+block.id+"/symbols/json",renderBlock);
      });
    });
    // Add First Blocks Ornaments (connectors, Clef, TimeSignature, etc)
    tune.connectors = new Vex.Flow.StaveConnector(tune.measuresByCoord[0][0],tune.measuresByCoord[tune.measuresByCoord.length-1][0]);
    tune.connectors.setType(Vex.Flow.StaveConnector.type.SINGLE);
    tune.connectors.setContext(ctx).draw();
    // Put double Bars at the end of each staff
    $.each(tune.measuresByCoord,function(i,staff){
             staff[staff.length-1].setEndBarType(Vex.Flow.Barline.type.END).setContext(ctx).draw();
    });

    // Render on Screen
    $.each(tune.measuresByCoord,function(i,staff){
	$.each(staff,function(j,block) {
          block.setContext(ctx).draw();
          Vex.Flow.Formatter.FormatAndDraw(ctx,block);
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

  function renderBlock(symbols) {
   console.log('ici');
    var blockId,staffId;
    $.each(symbols,function(i,symbol) {
      staffId = symbol.staffId; 
      blockId = symbol.blockId; 
      var measure = tune.measuresById[staffId][blockId];
      renderSymbol(symbol,measure);
    });
    $.each(tune.measuresById,function(staffNb,staff) {
	staff[blockId].setContext(ctx).draw();
    });
    return;
  }

  function renderSymbol(symbol,measure) {
    return;  
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
