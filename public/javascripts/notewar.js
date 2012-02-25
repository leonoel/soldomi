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
    for (var i = 0; i < tune.staves.length; i++) {
	var stave = new Vex.Flow.Stave(10, i * 80, 900);
	stave.setContext(NoteWar.ctx).draw();
    }
}

