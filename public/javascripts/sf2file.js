/*
 * Javascript tools to decode SF2 file format.
 * http://connect.creativelabs.com/developer/SoundFont/sfspec21.pdf
 * 
 * Author : Leo NOEL
 */
var Sf2File = (function() {
    if (!window.FileReader) {
	console.log('This browser does not have FileReader support.');
    }

    var readBytes = function(file, offset, length, cb) {
	var reader = new FileReader;
	reader.onload = function(e) {
	    cb(new Uint8Array(e.target.result));
	};
	reader.onloadend = function() {};
	reader.onerror = function(e) {
	    return console.log("Error : " + e);
	};
	reader.onprogress = function(e) {};

	var slice;
	file[slice = 'slice'] || file[slice = 'webkitSlice'] || file[slice = 'mozSlice'];
	var blob = file[slice](offset, offset + length);
	reader.readAsArrayBuffer(blob);
    }


    var ByteTools = (function() {
	var nativeEndian = new Uint16Array(new Uint8Array([0x12, 0x34]).buffer)[0] === 0x3412;
	var buffer = new ArrayBuffer(4);
	var uint8 = new Uint8Array(buffer);
	var uint16 = new Uint16Array(buffer);
	var uint32 = new Uint32Array(buffer);
	var int8 = new Int8Array(buffer);
	var int16 = new Int16Array(buffer);
	var int32 = new Int32Array(buffer);

	var fillBuffer = function(segment) {
	    if (nativeEndian) {
		for (var i = 0, end = segment.length; i < end; i++) {
		    uint8[i] = segment[i];
		}
	    } else {
		for (var i = 0, end = segment.length; i < end; i++) {
		    uint8[i] = segment[segment.length - i - 1];
		}
	    }
	};
	
	return {
	    segmentAsUInt32: function(segment) {
		fillBuffer(segment);
		return uint32[0];
	    },

	    segmentAsUInt16: function(segment) {
		fillBuffer(segment);
		return uint16[0];
	    },

	    segmentAsUInt8: function(segment) {
		fillBuffer(segment);
		return uint8[0];
	    },

	    segmentAsInt32: function(segment) {
		fillBuffer(segment);
		return int32[0];
	    },

	    segmentAsInt16: function(segment) {
		fillBuffer(segment);
		return int16[0];
	    },

	    segmentAsInt8: function(segment) {
		fillBuffer(segment);
		return int8[0];
	    }
	}
    })();


    var extractTree = function(file, cb) {
	var offset = 0;
	var rootChunk = {};
	var currentChunk = rootChunk;

	var segmentAsId = function(segment) {
	    var str = "";
	    for (var i = 0, iEnd = segment.length; i < iEnd; i++) {
		var byte = segment[i];
		str += String.fromCharCode(byte);
	    }
	    return str;
	}

	var findSubChunk = function(root, path) {
	    if (path.length === 0) {
		return root;
	    } else {
		var head = path.shift();
		var i = 0;
		while (i < root.subChunks.length) {
		    if (root.subChunks[i].name === head) {
			return findSubChunk(root.subChunks[i], path);
		    }
		    i++;
		}
		return null;
	    }
	}

	var parseChunk = function() {
	    readBytes(file, offset, 4, function(segment) {
		offset += 4;
		parseId(segment);
	    });
	}

	var parseId = function(segment) {
	    currentChunk.name = segmentAsId(segment);
	    console.log("Id : " + currentChunk.name);
	    readBytes(file, offset, 4, function(segment) {
		offset += 4;
		parseSize(segment);
	    });
	}

	var parseSize = function(segment) {
	    currentChunk.size = ByteTools.segmentAsUInt32(segment);
	    if (currentChunk.size === 0) {
		console.log("Size 0");
		return;
	    }
	    console.log("Size : " + currentChunk.size);
	    if (currentChunk.name === "RIFF" || currentChunk.name === "LIST") {
		readBytes(file, offset, 4, function(segment) {
		    offset += 4;
		    parseListId(segment);
		});
	    } else {
		currentChunk.offset = offset;
		offset += currentChunk.size;
		while (currentChunk.parent) {
		    currentChunk.parent.subChunks.push(currentChunk);
		    var parentChunkSizeRead = 4;
		    for (var i = 0, iEnd = currentChunk.parent.subChunks.length; i < iEnd; i++) {
			var subChunk = currentChunk.parent.subChunks[i];
			parentChunkSizeRead += subChunk.size + 8;
		    }
		    if (parentChunkSizeRead < currentChunk.parent.size) {
			var sibling = {};
			sibling.parent = currentChunk.parent;
			currentChunk = sibling;
			parseChunk();
			return;
		    } else {
			currentChunk = currentChunk.parent;
		    }
		}
		console.log("Finished !");
		cb({
		    findSubChunk: function(path) {
			return findSubChunk(rootChunk, path);
		    }
		});
	    }
	}

	var parseListId = function(segment) {
	    currentChunk.name = segmentAsId(segment);
	    console.log("List id : " + currentChunk.name);
	    currentChunk.subChunks = new Array();
	    var child = {};
	    child.parent = currentChunk;
	    currentChunk = child;
	    parseChunk();
	}

	parseChunk();
    } // end extractTree

    return {
	decode: function(file, cb) {
	    extractTree(file, function(riffTree) {
		console.log("Sf2 Riff tree extracted.");

		var FloatFactor = 1 / Math.pow(2, 16 - 1);

		var GeneratorType = {
		    InstrumentId: 41,
		    KeyRange: 43,
		    VelRange: 44,
		    SampleId: 53
		};

		var segmentAsVersion = function(segment) {
		    var version = ByteTools.segmentAsUInt32(segment);
		    return (version >> 16) + "." + (version & 0xFFFF);
		};

		var segmentAsString = function(segment) {
		    var str = "";
		    for (var i = 0, end = segment.length; i < end; i++) {
			var byte = segment[i];
			if (byte) {
			    str += String.fromCharCode(byte);
			}
		    }
		    return str;
		};

		var extractSegment = function(path, parse, cb) {
		    var subChunk = riffTree.findSubChunk(path);
		    if (subChunk !== null) {
			readBytes(file, subChunk.offset, subChunk.size, function(segment) {
			    parse(segment);
			    cb();
			});
		    } else {
			cb();
		    }
		};

		var info = {};
		var hydra = {
		    presetHeaders: [],
		    presetBags: [],
		    presetMods: [],
		    presetGens: [],
		    instrumentHeaders: [],
		    instrumentBags: [],
		    instrumentMods: [],
		    instrumentGens: [],
		    sampleHeaders: []
		};
		var instruments = [];
		var banks = [];

		var steps = [
		    function(cb) {
			extractSegment(["INFO", "ifil"], function(segment) {
			    info.ifil = segmentAsVersion(segment);
			}, cb);
		    },
		    function(cb) {
			extractSegment(["INFO", "isng"], function(segment) {
			    info.isng = segmentAsString(segment);
			}, cb);
		    },
		    function(cb) {
			extractSegment(["INFO", "irom"], function(segment) {
			    info.irom = segmentAsString(segment);
			}, cb);
		    },
		    function(cb) {
			extractSegment(["INFO", "iver"], function(segment) {
			    info.iver = segmentAsVersion(segment);
			}, cb);
		    },
		    function(cb) {
			extractSegment(["INFO", "INAM"], function(segment) {
			    info.inam = segmentAsString(segment);
			}, cb);
		    },
		    function(cb) {
			extractSegment(["INFO", "ICRD"], function(segment) {
			    info.icrd = segmentAsString(segment);
			}, cb);
		    },
		    function(cb) {
			extractSegment(["INFO", "IENG"], function(segment) {
			    info.ieng = segmentAsString(segment);
			}, cb);
		    },
		    function(cb) {
			extractSegment(["INFO", "IPRD"], function(segment) {
			    info.iprd = segmentAsString(segment);
			}, cb);
		    },
		    function(cb) {
			extractSegment(["INFO", "ICOP"], function(segment) {
			    info.icop = segmentAsString(segment);
			}, cb);
		    },
		    function(cb) {
			extractSegment(["INFO", "ICMT"], function(segment) {
			    info.icmt = segmentAsString(segment);
			}, cb);
		    },
		    function(cb) {
			extractSegment(["INFO", "ISTF"], function(segment) {
			    info.istf = segmentAsString(segment);
			}, cb);
		    },
		    function(cb) {
			extractSegment(["pdta", "phdr"], function(segment) {
			    var offset = 0;
			    while (offset < segment.length) {
				var header = {};
				header.name = segmentAsString(segment.subarray(offset, offset + 20));
				header.id = ByteTools.segmentAsInt16(segment.subarray(offset + 20, offset + 22));
				header.bank = ByteTools.segmentAsInt16(segment.subarray(offset + 22, offset + 24));
				header.bag = ByteTools.segmentAsInt16(segment.subarray(offset + 24, offset + 26));
				header.library = ByteTools.segmentAsUInt32(segment.subarray(offset + 26, offset + 30));
				header.genre = ByteTools.segmentAsUInt32(segment.subarray(offset + 30, offset + 34));
				header.morphology = ByteTools.segmentAsUInt32(segment.subarray(offset + 34, offset + 38));
				hydra.presetHeaders.push(header);
				offset += 38;
			    }
			}, cb);
		    },
		    function(cb) {
			extractSegment(["pdta", "pbag"], function(segment) {
			    var offset = 0;
			    while (offset < segment.length) {
				var pbag = {};
				pbag.genId = ByteTools.segmentAsInt16(segment.subarray(offset, offset + 2));
				pbag.modId = ByteTools.segmentAsInt16(segment.subarray(offset + 2, offset + 4));
				hydra.presetBags.push(pbag);
				offset += 4;
			    }
			}, cb);
		    },
		    function(cb) {
			extractSegment(["pdta", "pmod"], function(segment) {
			    var offset = 0;
			    while (offset < segment.length) {
				var pmod = {};
				pmod.src = ByteTools.segmentAsInt16(segment.subarray(offset, offset + 2));
				pmod.dest = ByteTools.segmentAsInt16(segment.subarray(offset + 2, offset + 4));
				pmod.amount = ByteTools.segmentAsInt16(segment.subarray(offset + 4, offset + 6));
				pmod.amountSrc = ByteTools.segmentAsInt16(segment.subarray(offset + 6, offset + 8));
				pmod.trans = ByteTools.segmentAsInt16(segment.subarray(offset + 8, offset + 10));
				hydra.presetMods.push(pmod);
				offset += 10;
			    }
			}, cb);
		    },
		    function(cb) {
			extractSegment(["pdta", "pgen"], function(segment) {
			    var offset = 0;
			    while (offset < segment.length) {
				var pgen = {};
				pgen.type = ByteTools.segmentAsInt16(segment.subarray(offset, offset + 2));
				if (pgen.type === GeneratorType.KeyRange || pgen.type === GeneratorType.VelRange) {
				    pgen.value = { min: ByteTools.segmentAsUInt8(segment.subarray(offset + 2, offset + 3)),
						   max: ByteTools.segmentAsUInt8(segment.subarray(offset + 3, offset + 4)) };
				} else if (pgen.type === GeneratorType.InstrumentId) {
				    pgen.value = ByteTools.segmentAsUInt16(segment.subarray(offset + 2, offset + 4));
				} else {
				    pgen.value = ByteTools.segmentAsInt16(segment.subarray(offset + 2, offset + 4));
				}
				hydra.presetGens.push(pgen);
				offset += 4;
			    }
			}, cb);
		    },
		    function(cb) {
			extractSegment(["pdta", "inst"], function(segment) {
			    var offset = 0;
			    while (offset < segment.length) {
				var inst = {};
				inst.name = segmentAsString(segment.subarray(offset, offset + 20));
				inst.bagId = ByteTools.segmentAsInt16(segment.subarray(offset + 20, offset + 22));
				hydra.instrumentHeaders.push(inst);
				offset += 22;
			    }
			}, cb);
		    },
		    function(cb) {
			extractSegment(["pdta", "ibag"], function(segment) {
			    var offset = 0;
			    while (offset < segment.length) {
				var ibag = {};
				ibag.genId = ByteTools.segmentAsInt16(segment.subarray(offset, offset + 2));
				ibag.modId = ByteTools.segmentAsInt16(segment.subarray(offset + 2, offset + 4));
				hydra.instrumentBags.push(ibag);
				offset += 4;
			    }
			}, cb);
		    },
		    function(cb) {
			extractSegment(["pdta", "imod"], function(segment) {
			    var offset = 0;
			    while (offset < segment.length) {
				var imod = {};
				imod.src = ByteTools.segmentAsInt16(segment.subarray(offset, offset + 2));
				imod.dest = ByteTools.segmentAsInt16(segment.subarray(offset + 2, offset + 4));
				imod.amount = ByteTools.segmentAsInt16(segment.subarray(offset + 4, offset + 6));
				imod.amountSrc = ByteTools.segmentAsInt16(segment.subarray(offset + 6, offset + 8));
				imod.trans = ByteTools.segmentAsInt16(segment.subarray(offset + 8, offset + 10));
				hydra.instrumentMods.push(imod);
				offset += 10;
			    }
			}, cb);
		    },
		    function(cb) {
			extractSegment(["pdta", "igen"], function(segment) {
			    var offset = 0;
			    while (offset < segment.length) {
				var igen = {};
				igen.type = ByteTools.segmentAsInt16(segment.subarray(offset, offset + 2));
				if (igen.type === GeneratorType.KeyRange || igen.type === GeneratorType.VelRange) {
				    igen.value = { min: ByteTools.segmentAsUInt8(segment.subarray(offset + 2, offset + 3)),
						   max: ByteTools.segmentAsUInt8(segment.subarray(offset + 3, offset + 4)) };
				} else if (igen.type === GeneratorType.SampleId) {
				    igen.value = ByteTools.segmentAsUInt16(segment.subarray(offset + 2, offset + 4));
				} else {
				    igen.value = ByteTools.segmentAsInt16(segment.subarray(offset + 2, offset + 4));
				}
				hydra.instrumentGens.push(igen);
				offset += 4;
			    }
			}, cb);
		    },
		    function(cb) {
			extractSegment(["pdta", "shdr"], function(segment) {
			    var offset = 0;
			    while (offset < segment.length) {
				var shdr = {};
				shdr.name = segmentAsString(segment.subarray(offset, offset + 20));
				shdr.start = ByteTools.segmentAsUInt32(segment.subarray(offset + 20, offset + 24));
				shdr.end = ByteTools.segmentAsUInt32(segment.subarray(offset + 24, offset + 28));
				shdr.startLoop = ByteTools.segmentAsUInt32(segment.subarray(offset + 28, offset + 32));
				shdr.endLoop = ByteTools.segmentAsUInt32(segment.subarray(offset + 32, offset + 36));
				shdr.sampleRate = ByteTools.segmentAsUInt32(segment.subarray(offset + 36, offset + 40));
				shdr.originalPitch = ByteTools.segmentAsUInt8(segment.subarray(offset + 40, offset + 41));
				shdr.pitchCorrection = ByteTools.segmentAsUInt8(segment.subarray(offset + 41, offset + 42));
				shdr.sampleLink = ByteTools.segmentAsInt16(segment.subarray(offset + 42, offset + 44));
				shdr.sampleType = ByteTools.segmentAsInt16(segment.subarray(offset + 44, offset + 46));
				hydra.sampleHeaders.push(shdr);
				offset += 46;
			    }
			}, cb);
		    },
		    function(cb) {
			hydra.instrumentHeaders.slice(0, hydra.instrumentHeaders.length - 1).forEach(function(iheader, i) {
			    var nextIheader = hydra.instrumentHeaders[i+1];
			    var zones = [];
			    var globalSettings = {};
			    hydra.instrumentBags.slice(iheader.bagId, nextIheader.bagId).forEach(function(ibag, i) {
				var nextIbag = hydra.instrumentBags[iheader.bagId+i+1];
				var gens = [];
				var mods = [];
				var sampleLoader = undefined;
				var keyRange = undefined;
				var velRange = undefined;
				hydra.instrumentGens.slice(ibag.genId, nextIbag.genId).forEach(function(igen) {
				    if (igen.type === GeneratorType.SampleId) {
					var sampleH = hydra.sampleHeaders[igen.value];
					sampleLoader = function(cb) {
					    var dataSubChunk = riffTree.findSubChunk(["sdta", "smpl"]);
					    readBytes(file,
						      dataSubChunk.offset + 2 * sampleH.start,
						      2 * (sampleH.end - sampleH.start),
						      function(segment) {
							  cb({name: sampleH.name,
							      loopStart: sampleH.startLoop - sampleH.start,
							      loopEnd: sampleH.endLoop - sampleH.start,
							      sampleRate: sampleH.sampleRate,
							      originalPitch: sampleH.originalPitch,
							      pitchCorrection: sampleH.pitchCorrection,
							      data: btoa(segment)});
						      });
					}
				    } else if (igen.type === GeneratorType.KeyRange) {
					keyRange = igen.value;
				    } else if (igen.type === GeneratorType.VelRange) {
					velRange = igen.value;
				    } else {
					gens.push(igen);
				    }
				});
				hydra.instrumentMods.slice(ibag.genId, nextIbag.genId).forEach(function(imod) {
				    mods.push(imod);
				});
				if (sampleLoader) {
				    zones.push({
					sampleLoader: sampleLoader,
					gens: gens,
					mods: mods,
					keyRange: keyRange,
					velRange: velRange
				    });
				} else {
				    globalSettings = {
					gens: gens,
					mods: mods
				    };
				}
			    });

			    instruments.push({
				name: iheader.name,
				globalSettings: globalSettings,
				zones: zones
			    });
			});
			cb();
		    },
		    function(cb) {
			hydra.presetHeaders.slice(0, hydra.presetHeaders.length - 1).forEach(function(pheader, i) {
			    var nextPheader = hydra.presetHeaders[i+1];
			    var zones = [];
			    var globalSettings = {};
			    hydra.presetBags.slice(pheader.bag, nextPheader.bag).forEach(function(pbag, i) {
				var nextPbag = hydra.presetBags[pheader.bag+i+1];
				var gens = [];
				var mods = [];
				var inst = undefined;
				var keyRange = undefined;
				var velRange = undefined;
				hydra.presetGens.slice(pbag.genId, nextPbag.genId).forEach(function(pgen) {
				    if (pgen.type === GeneratorType.InstrumentId) {
					inst = instruments[pgen.value];
				    } else if (pgen.type === GeneratorType.KeyRange) {
					keyRange = pgen.value;
				    } else if (pgen.type === GeneratorType.VelRange) {
					velRange = pgen.value;
				    } else {
					gens.push(pgen);
				    }
				});
				hydra.presetMods.slice(pbag.modId, nextPbag.modId).forEach(function(pmod) {
				    mods.push(pmod);
				});
				if (inst) {
				    zones.push({
					instrument: inst,
					gens: gens,
					mods: mods,
					keyRange: keyRange,
					velRange: velRange
				    });
				} else {
				    globalSettings = {
					gens: gens,
					mods: mods
				    };
				}

			    });

			    var iBank = 0;
			    for (; iBank < banks.length && banks[iBank].id !== pheader.bank; iBank++) {}
			    if (iBank === banks.length) {
				banks.push({id: pheader.bank,
					    presets: []
					   });
			    }
			    banks[iBank].presets.push({id: pheader.id,
						       name: pheader.name,
						       pack: function(cb) {
							   console.log("Packing preset.");
							   var samplesLoaded = 0;
							   var samplesToLoad = 0;
							   var samples = [];
							   zones.forEach(function(zone) {
							       zone.instrument.zones.forEach(function(instZone) {
								   samplesToLoad++;
								   instZone.sampleLoader(function(sample) {
								       samples[samplesLoaded] = sample;
								       samplesLoaded++;
								       console.log("Loaded sample " + samplesLoaded + "/" + samplesToLoad);
								       console.log(sample);
								       if (samplesLoaded === samplesToLoad) {
									   cb(JSON.stringify({name: pheader.name,
											      js: JSON.stringify({samples: samples,
														  playNote: function(sampleRate, key, velocity) {
														      //TODO
														  }.toString()
														 })
											     }));
								       }
								   });
							       });
							   });
						       }
						      });


			});
			cb();
		    },
		    function(cb) {
			// Sort Banks and Presets by Id
			banks.forEach(function(bank) {
			    bank.presets.sort(function(p1, p2) {return p1.id < p2.id ? -1 : ((p1.id > p2.id) ? 1 : 0)});
			});
			banks.sort(function(b1, b2) {return b1.id < b2.id ? -1 : ((b1.id > b2.id) ? 1 : 0)});
			cb();
		    }
		]; // end steps array
		
		var step = function(i) {
		    if (i < steps.length) {
			steps[i](function() {
			    step(i + 1);
			});
		    } else {
			cb({info: info,
			    banks: banks});
		    }
		};

		step(0);
	    }); // end extractTree

	} // end decode
    }; // end return
})(); // end Sf2File
