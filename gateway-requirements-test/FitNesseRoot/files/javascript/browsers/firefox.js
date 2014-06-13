var methods;
var firstLine;
var lines;
var className;

/*
 * This file contains functions used if the users browser is Mozilla Firefox.
 */
Experior = {

		/*
		 * Initializes the editor. Adds keylistener.
		 * Adds the first line in the text to the global variable firstline.
		 */
		initialize : function() {

	body = document.getElementsByTagName('body')[0];
	body.innerHTML = body.innerHTML.replace(/\n/g,"");	

	editor = document.getElementsByTagName('pre')[0];
	document.designMode = 'on';
	document.addEventListener('keypress', this.keyListener, false);
	window.addEventListener('scroll', function() {
		if(!Experior.scrolling)
			Experior.highlightDocument('scroll') 
	},
	false);

	firstline = Experior.getText().split("\n");
	firstline = firstline[0];
	caret = '\u2009';
},

//Keylistener for keys pressed.
keyListener : function(evt) {

	keyCode = evt.keyCode;	
	charCode = evt.charCode;

	// Space or enter pressed
	if(charCode == 32 ||keyCode==13) {
		Experior.highlightDocument();
	}
	// Pipe (|)
	else if( charCode==124) {	
		evt.preventDefault();
		evt.stopPropagation();
		Experior.align();	
	}
	// Backspace or delete
	else if(keyCode==46||keyCode==8) {		
		Experior.editEvents.lastPositions[Experior.editEvents.next()] = editor.innerHTML;
	}
	// Undo or redo
	else if((charCode==122||charCode==121||charCode==90) && evt.ctrlKey) { 
		(charCode==121||evt.shiftKey) ? Experior.editEvents.redo() :  Experior.editEvents.undo(); 
		evt.preventDefault();
	}
	// Paste
	else if(charCode==118 && evt.ctrlKey) {


		parent.window.setTimeout('experior.editor.highlightDocument()',100 );

	}
},

/*
 * Function which updates the width of the div with methods to fit the
 * longest method-name. Is called from the events onMouseOver and onMouseOut.
 */
updateDivWidth : function( width ) {

	var div = parent.document.getElementById("methodsDiv");

	if( width == "over" ) 	{
		div.style.width = "auto";
		div.style.overflow = "auto";

		div.style.overflow = "-moz-scrollbars-vertical";
		div.style.minWidth = "135px";		
	}
	else {
		div.style.width="135px";
		div.style.overflow = "hidden";
	}
},

/*
 * Creates the div on the left side with the list of available methods.
 * The methods is contained in the array methodsInDiv.
 */
createMethodsDiv : function( methodsInDiv ) {

	var newdiv = document.createElement('div'); 
	var divIdName = 'methodsDiv'; 

	var metodestring = "";

	parent.document.body.appendChild(newdiv);
	newdiv.setAttribute('id',divIdName);
	newdiv.onmouseover = new Function("Experior.updateDivWidth('over');");
	newdiv.onmouseout = new Function("Experior.updateDivWidth('out');");
	newdiv.style.width = "135px";
	newdiv.style.backgroundImage = "url(/files/javascript/divbackground.png)";
	newdiv.style.backgroundRepeat = "repeat";
	newdiv.style.whiteSpace = "nowrap";
	newdiv.style.overflow = "hidden";

	newdiv.style.left = "5px";
	newdiv.style.height = screen.height - 310 + 'px';
	newdiv.style.top = "120px"; 
	newdiv.style.position = "fixed";

	newdiv.style.textDecoration = "none";		

	if( methodsInDiv.length > 1 ) {
		newdiv.innerHTML = "<h3>Methods</h3>";
		for( var i = 0; i < methodsInDiv.length; i++ ) {	
			methodsInDiv[i].trim;

			metodestring += "<a style='margin-bottom:5px; display:block; " +
			"text-decoration: none;' href=javascript:void(0) " +
			"onclick=insertMethod(" + i + ")>" + methodsInDiv[i] + "</a>";		
		}
		newdiv.innerHTML += metodestring;
	}
	else
		newdiv.innerHTML = "";

},

/*
 * Updates the div with available methods if there has been a change to the classname
 * in the test.
 */
updateMethodsDiv : function() {
	var metodestring = "";	

	parent.document.getElementById("methodsDiv").innerHTML = "";

	if( lines.length > 0 ) 	{
		for( var i = 0; i < lines.length; i++ )	{
			lines[i].trim;

			metodestring += "<a style='margin-bottom:5px; display:block;" +
			"text-decoration: none;' href=javascript:void(0) " +
			"onclick=insertMethod(" + i + ")>" + lines[i] + "</a>";
		}
		parent.document.getElementById("methodsDiv").innerHTML = "<h3>Methods</h3>" + 
		metodestring;
	}
},

/*
 * Split large tests, if it is necessary with scrollbar. Causes that only parts of the
 * text is highlighted at the same time, which leads to better perfomance.
 */
splitLargeTests : function(code,flag) {
	if(flag=='scroll') {
		this.scrolling = true;
		return code;
	}
	else {
		this.scrolling = false;
		var cursor = code.indexOf(caret);

		if(cursor-3000<0) {
			var start=0;
			var end=4000;
		}
		else if(cursor+2000>code.length) {
			var start=code.length-4000;
			var end=code.length;
		}
		else {
			var start=cursor-2000;
			var end=cursor+2000;
		}

		code = code.substring(start,end);
		return code;
	}
},

/*
 * Highlights the correct text; methodnames (if the name is contained in methods2),
 * comments and keywords.
 */
highlightDocument : function(flag, methods2) {


	var newclassName = Experior.getText().match( "(\\!\\|\\-?)[\\w|\\.]+(\\-?\\|)");

	if(methods2 != null )		
		methods = methods2

		if( newclassName != null)
			className = newclassName[0];

	if(methods.length == 0) {
		lines = new Array();
	}
	else {
		lines = methods.split('\n');
		lines.pop();
	}

	if(flag != 'init') {
		window.getSelection().getRangeAt(0).insertNode(document.createTextNode(caret));
	}

	editor = Experior.getEditor();
	o = editor.innerHTML;

	o = o.replace(/<br>/g,'\n');
	o = o.replace(/<.*?>/g,'');
	x = z = this.splitLargeTests(o,flag);
	x = x.replace(/\n/g,'<br>');
	x = x.replace(/&nbsp;/g, '&nbsp;');

	if(arguments[1] && arguments[2])
		x = x.replace(arguments[1],arguments[2]);

	var InputToRegex;

	for(i=0;i<FitNesse.syntax.length;i++) 
		x = x.replace(FitNesse.syntax[i].input,FitNesse.syntax[i].output);

	var words1; //array with words between whitespaces
	var words2; //string joined from words1 with matching regex
	var words3; //string with highlighted method name

	for(j=0;j<lines.length; j++) {
		lines[j] = lines[j].replace(/\s+$/,"");
		words1 = lines[j].split(' ');

		words2 = words1.join( "(\\s+|(?:&nbsp;)*\\s*\\|.*?\\|\\s*)" );

		words3 = "";
		for (var i = 0; i<words1.length; i++) {

			words3 += "<s>"+words1[i]+"</s>";

			if (i != words1.length-1)
				words3 += "$"+(i+1);
		}
		eval('x = x.replace(/'+words2+'/g, \"'+ words3 + '\")');
	}

	editor.innerHTML = this.editEvents.lastPositions[this.editEvents.next()] = (flag=='scroll') ? 
			x : o.replace(z,x);
	if(flag!='init') 
		this.findString();

},

//Gets and returns the pre-element which contains the editor. 
getEditor : function() {
	if(!document.getElementsByTagName('pre')[0]) {
		body = document.getElementsByTagName('body')[0];

		if(!body.innerHTML)
			return body;
		if(body.innerHTML=="<br>")
			body.innerHTML = "<pre> </pre>";
		else 
			body.innerHTML = "<pre>"+body.innerHTML+"</pre>";
	}
	return document.getElementsByTagName('pre')[0];
},

//Gets the last word from the caret position
getLastWord : function() {
	var rangeAndCaret = Experior.getRangeAndCaret();
	words = rangeAndCaret[0].substring(rangeAndCaret[1]-40,rangeAndCaret[1]);
	words = words.replace(/[\s\n\r\);\W]/g,'\n').split('\n');
	return words[words.length-1].replace(/[\W]/gi,'').toLowerCase();
},

//Finds the string at caret position
findString : function() {
	if(self.find(caret))
		window.getSelection().getRangeAt(0).deleteContents();		
},

/*
 * Aligns the pipe automatically to the pipes position on the previous line. Is called
 * when the key | is pressed.
 */
align : function() {
	var range = window.getSelection().getRangeAt(0);
	var startNode = document.getElementsByTagName("pre").item(0);

	var startOffset = 0;	

	range.setStartBefore( startNode );

	var div = document.createElement('div');
	div.appendChild(range.cloneContents());

	var linearray = div.innerHTML.split("<br>");

	for( var i = 0; i < linearray.length; i++ ) {
		linearray[i] = this.removeTags( linearray[i] );
	}

	range.collapse( false );

	if( linearray.length <= 1 || linearray[linearray.length-2] == 0 || 
			linearray[linearray.length-2].search(/span/) > 0 )
	{		
		this.createTextnode();
		return;
	}

	var currentline = linearray[linearray.length-1].split("|");

	if (linearray.length > 0 && linearray.length-2 > 0 ) 
		var previousline = linearray[linearray.length-2].split("|");

	var nbspstring = "";

	if( previousline[currentline.length-1] != null)	{
		for (var i = 0; i < 
		(previousline[currentline.length-1].replace(/&nbsp;/gi,' ').length)-
		(currentline[currentline.length-1].length); i++)
		{	
			var node = window.document.createTextNode( "\u00a0" );
			var range = window.getSelection().getRangeAt(0);

			var selct = window.getSelection();
			var range2 = range.cloneRange();
			selct.removeAllRanges();
			range.deleteContents();
			range.insertNode(node);
			range2.selectNode(node);		
			range2.collapse(false);
			selct.removeAllRanges();
			selct.addRange(range2);
		}
	}
	else {
		Experior.createTextnode();
		return;
	}

	var node = window.document.createTextNode( "|" );
	var range = window.getSelection().getRangeAt(0);

	var selct = window.getSelection();
	var range2 = range.cloneRange();
	selct.removeAllRanges();
	range.deleteContents();
	range.insertNode(node);
	range2.selectNode(node);		
	range2.collapse(false);
	selct.removeAllRanges();
	selct.addRange(range2);
},

//Creates a text node with "|" at the caret position.
createTextnode : function() {
	var node = window.document.createTextNode( "|" );
	var range = window.getSelection().getRangeAt(0);

	var selct = window.getSelection();
	var range2 = range.cloneRange();
	selct.removeAllRanges();
	range.deleteContents();
	range.insertNode(node);
	range2.selectNode(node);		
	range2.collapse(false);
	selct.removeAllRanges();
	selct.addRange(range2);
},

/*
 * Inserts the method with the index as specified id in the array lines, at the
 * caret position.
 */
insertMethod : function( id ) {
	var node = window.document.createTextNode( "!|" + lines[id] + "|" );
	var range = window.getSelection().getRangeAt(0);

	var selct = window.getSelection();
	var range2 = range.cloneRange();
	selct.removeAllRanges();
	range.deleteContents();
	range.insertNode(node);
	range2.selectNode(node);		
	range2.collapse(false);
	selct.removeAllRanges();
	selct.addRange(range2);

	this.highlightDocument();
},

//Removes the tags specified in the replace-functions from the string which is parameter
removeTags : function( code ) {
	code = code.replace(/<pre>/g,'');
	code = code.replace(/<\/pre>/g,'');
	code = code.replace(/<s>/g,'');
	code = code.replace(/<\/s>/g,'');
	code = code.replace(/&lt;/g,'<');
	code = code.replace(/&gt;/g,'>');
	code = code.replace(/&amp;/gi,'&');		
	return code;
},

//Highlight parts of code, for better performance
snippets : function(evt) {
	var snippets = FitNesse.snippets;	
	var trigger = this.getLastWord();
	for (var i=0; i<snippets.length; i++) {
		if(snippets[i].input == trigger) {
			var content = snippets[i].output.replace(/</g,'&lt;');
			content = content.replace(/>/g,'&gt;');
			if(content.indexOf('$0')<0)
				content += caret;
			else content = content.replace(/\$0/,caret);
			content = content.replace(/\n/g,'<br>');
			var pattern = new RegExp(trigger+caret,'gi');
			evt.preventDefault();
			this.highlightDocument('snippets',pattern,content);
		}
	}
},

//Gets and returns the range and caret position in an array
getRangeAndCaret : function() {	
	var range = window.getSelection().getRangeAt(0);

	var range2 = range.cloneRange();
	var node = range.endContainer;			
	var caret = range.endOffset;

	range2.selectNode(node);	
	return [range2.toString(),caret];
},

//Insert the text in the parameter code at the caret position.
insertCode : function(code,replaceCursorBefore) {
	var range = window.getSelection().getRangeAt(0);
	var node = window.document.createTextNode(code);
	var selct = window.getSelection();
	var range2 = range.cloneRange();
	selct.removeAllRanges();
	range.deleteContents();
	range.insertNode(node);
	range2.selectNode(node);		
	range2.collapse(replaceCursorBefore);
	selct.removeAllRanges();
	selct.addRange(range2);
},

/*
 * Gets and returns all text from the editor. Removes the specified HTML-tags from
 * the text before it is returned
 */
getText : function() {
	if(!document.getElementsByTagName('pre')[0] || editor.innerHTML == '') {
		editor = Experior.getEditor();	
	}
	var code = editor.innerHTML;

	code = code.replace(/<p>/g,'\n');
	code = code.replace(/<br>/g,'\n');
	code = code.replace(/&nbsp;/gi,'');
	code = code.replace(/\u2009/g,'');
	code = code.replace(/<.*?>/g,'');
	code = code.replace(/&lt;/g,'<');
	code = code.replace(/&gt;/g,'>');
	code = code.replace(/&amp;/gi,'&');		
	return code;
},

/*
 * Sets the text in arguments[0] into the editor
 */
setText : function() {
	var code = arguments[0];
	code = code.replace(/\u2009/gi,'');

	code = code.replace(/&/gi,'&amp;');
	code = code.replace(/</g,'&lt;');
	code = code.replace(/>/g,'&gt;');

	if (code == '')
		document.getElementsByTagName('body')[0].innerHTML = '';

},

/*
 * Checks if the class-name is changed anywhere in the test. If it is, 
 * the method loadXMLString is called.
 */
checkClassName : function( url ) {	

	var text = Experior.getText();

	var newclassName = text.match( "(\\!\\|\\-?)[\\w|\\.]+(\\-?\\|)");

	if( newclassName != null && className != newclassName[0] )
	{		
		className = newclassName[0];
		firstline = className;
		Experior.loadXMLString( url );
	}	
},

/*
 * Gets the hostname and portnumber, and performs a XMLHttpRequest to the server.
 * Parses the returned JSON-object and puts the content into the global array methods.
 */
loadXMLString : function( url ) {

	var firstline = Experior.getText().split("\n");
	var host = window.location.hostname;
	var port = window.location.port;

	var url = "http://" + host + ":" + port + "/FrontPage?responder=Commands&var=" + className;
	httpRequest=null;
	if(window.XMLHttpRequest) {

		httpRequest=new XMLHttpRequest();
	}
	else if(window.ActiveXObject) {
		// older browsers
		httpRequest=new ActiveXObject("Microsoft.XMLHTTP");
	}
	if(httpRequest!=null) {
		httpRequest.onreadystatechange= function() {			
			if (httpRequest.readyState==4)
			{
				if (httpRequest.status == 404) {
					alert('Requested URL is not found');
				}
				else if (httpRequest.status == 403) {
					alert('Access Denied');
				}
				else if (httpRequest.status==200) {   					
					methods = httpRequest.getResponseHeader("json");

					var methodsarray;
					if( methods.length == 4 )
						methods = new Array();
					else {
						methodsarray = eval('('+ methods +')');
						methods = methodsarray.join("\n") + "\n";
					}

					Experior.highlightDocument();
					Experior.updateMethodsDiv();
				}
				else {
					alert("Problem retrieving XML data:" + httpRequest.statusText);
				}
			}			
		}
		httpRequest.open("GET",url,true);

		httpRequest.overrideMimeType('text/xml');
		httpRequest.send(null);
	}
	else {
		alert("Your browser does not support XMLHTTP.");
	}
},

/*
 * Method is run on page load. Receives all text in the parameter code.
 * Aligns all pipes in the document.
 */
alignAllPipesOnPageLoad : function( code ) {	
	var text = code.split("\n");	
	var linearray = new Array();
	var template = new Array();
	var output;
	firstline = text[0];

	var tablestart = 0;

	for( var i = 0; i < text.length; i++ ) {	
		linearray[i] = text[i].split("|");
	} 

	var output = "";

	for( var i=0; i < linearray.length-1; i++ ){ // Iterates through each line

		// ! on the line
		if( linearray[i][0].search(/\!/) != -1 ) { 

			template = new Array();

			for( var j = 0; j < linearray[i+1].length-1; j++ ) {
				template[j] = linearray[i+1][j].length;
			}
			tablestart = i;
		}



		// the line is not empty 	
		if( linearray[i].length > 1 ) {

			for( var k = 0; k < linearray[i].length; k++ ) {			 
				if( linearray[i][k].length > template[k] && 
						linearray[i][k].search(/\!\d/) == -1 )
				{
					template[k] = linearray[i][k].length;
				}
			}


			// not first line and previous line not empty
			if( i > 0 && linearray[i-1].length == 1 ) {
				tablestart = i;
				for( var k = 0; k < linearray[i].length; k++ ) {			 
					if( linearray[i][k].length > template[k] && 
							linearray[i][k].search(/\!\d/) == -1 )
					{
						template[k] = linearray[i][k].length;
					}
				}
			}


			// not first line and previous line contains a pipe
			if( i > 0 && linearray[i-1][0].search(/\!/) != -1 )	{

				for( var k = 1; k < linearray[i].length-1; k++ ) {			 
					if( linearray[i][k].search(/\!\d/) == -1 ) {
						template[k] = linearray[i][k].length;
					}
				}
			}

			// next line is empty or next line contains a pipe
			if( linearray[i+1].length == 1  || linearray[i+1][0].search(/\!/) != -1 ) {
				for( var l = tablestart; l <= i; l++ ) {
					for( var m = 0; m < linearray[l].length-1; m++ ) {		

						var nbspstring = "";
						if( linearray[l][0].search(/\!/) == -1 ) {
							for( var n = linearray[l][m].length; n < template[m]; n++) 	{
								nbspstring += "&nbsp;";					 
							}
						}
						linearray[l][m] += nbspstring + "|";
					}
				}				
			}
		}
	}

	for( var i = 0; i < linearray.length; i++ ) {
		for( var j = 0; j < linearray[i].length; j++ )
			output += linearray[i][j];

		if( i < linearray.length-1)
			output += "\n";
	}

	editor.innerHTML = output;
},

//Redo the last user action
redo : function() {
	this.position++;
	if(typeof(this.lastPositions[this.position])=='undefined')
		this.position--;

	editor.innerHTML = this.lastPositions[this.position];
	Experior.findString();
},

//Undo the last user action
editEvents : {
	position : -1,
	lastPositions : [],

	undo : function() {
	editor = Experior.getEditor();

	if(editor.innerHTML.indexOf(caret)==-1) {
		if(editor.innerHTML != " ")
			window.getSelection().getRangeAt(0).insertNode(document.createTextNode(caret));

		this.lastPositions[this.position] = editor.innerHTML;
	}

	this.position --;
	if(typeof(this.lastPositions[this.position])=='undefined')
		this.position ++;

	editor.innerHTML = this.lastPositions[this.position];
	if(editor.innerHTML.indexOf(caret)>-1)
		editor.innerHTML+=caret;
	Experior.findString();
},

//Backspace or delete
next : function() {
	if(this.position>20) this.lastPositions[this.position-21] = undefined;
	return ++this.position;
}
}
}

//Array for special words. Used in highlight.js
FitNesse={};

window.addEventListener('load', function() { Experior.initialize('new'); }, true);
