function NoteWar() {}

NoteWar.init = function(canvas,
			url) {
    NoteWar.canvas = canvas;
    NoteWar.url = url;
    NoteWar.renderer = new Vex.Flow.Renderer(canvas,
					     Vex.Flow.Renderer.Backends.CANVAS);
    NoteWar.ctx = NoteWar.renderer.getContext();

    $.getJSON(url, NoteWar.renderTune);
}

NoteWar.renderTune = function(tune) {
    NoteWar.tune = tune;
    var y = 0;
    for (var staffId in tune.staves) {
	NoteWar.tune.staves[staffId].staff = new Vex.Flow.Stave(10, y * 80, 900).setContext(NoteWar.ctx).draw();
	y++;
    }
    $.getJSON(NoteWar.url + "/measures/0", NoteWar.renderMeasure);
}

NoteWar.renderMeasure = function(measure) {
    NoteWar.measure = measure;
    for (var staffId in NoteWar.tune.staves) {
	NoteWar.tune.staves[staffId].staveNotes = new Array();
    } 

    for (var i in measure.segments) {
	var segment = measure.segments[i];
	var staffId = segment.staffId;
	NoteWar.tune.staves[staffId].staveNotes.push(
	    new Vex.Flow.StaveNote(
		{ keys: [NoteWar.formatKeys(segment)],
		  duration: NoteWar.formatDuration(segment),
		  clef: NoteWar.formatClef(segment)
		}));
    }

    for (var staffId in NoteWar.tune.staves) {
	var voice = new Vex.Flow.Voice({
	    num_beats: 4,
	    beat_value: 4,
	    resolution: Vex.Flow.RESOLUTION
	});
	voice.addTickables(NoteWar.tune.staves[staffId].staveNotes);
	var formatter = new Vex.Flow.Formatter().joinVoices([voice]).format([voice], 500);
	voice.draw(NoteWar.ctx, NoteWar.tune.staves[staffId].staff);
    }
}

NoteWar.formatKeys = function(segment) {
    return segment.note.toLowerCase() + '/' + segment.octave;
}

NoteWar.formatDuration = function(segment) {
    var duration;
    switch(segment.durationSymbol) {
    case "WHOLE":
	duration = "w";
	break;
    case "HALF":
	duration = "h";
	break;
    case "QUARTER":
	duration = "q";
	break;
    default:
	break;
    }
    var rest = (segment.rest == "true") ? "r" : "";
    return duration + rest;
}

NoteWar.formatClef = function(segment) {
    return segment.clef.toLowerCase();
}

