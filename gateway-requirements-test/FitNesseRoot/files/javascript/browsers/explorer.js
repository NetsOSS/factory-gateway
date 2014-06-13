var methods;
var firstLine;
var lines;
var className;

/*
 * This file contains functions used in Internet Explorer. For complete functionality,
 * se firefox.js
 */

Experior = {

		/*
		 * Initializes the editor. Adds keylistener.
		 * Adds the first line in the text to the global variable firstline.
		 */
		initialize : function() {

	editor = document.getElementsByTagName('pre')[0];
	alert( "This version for Internet Explorer is incomplete. Please use Mozilla Firefox.")

	editor.contentEditable = 'true';

	document.getElementsByTagName('body')[0].onfocus = function() {
		editor.focus();
	}

	document.attachEvent('onkeydown', this.metaHandler);
	document.attachEvent('onkeypress', this.keyHandler);

	window.attachEvent('onscroll', function() { 
		if(!Experior.scrolling)
			setTimeout(function(){
				Experior.highlightDocument('scroll')},1)});

	firstline = Experior.getText().split("\n");
	firstline = firstline[0];
	caret = '\u2009'; 
	setTimeout(function() { window.scroll(0,0) },50);
},

//Keylistener for keys pressed.
keyHandler : function( evt ) {
	charCode = evt.keyCode;
	fromChar = String.fromCharCode(charCode);

	// syntax highlighting
	if( charCode==13 || charCode == 32 ) { 
		Experior.highlightDocument();
	}
},

//Keylistener for keys pressed. New keys should be put here.
metaHandler : function(evt) {
	keyCode = evt.keyCode;

	if( keyCode==220) {			
		Experior.align();		
	}
	// undo and redo
	else if((keyCode==122||keyCode==121||keyCode==90) && evt.ctrlKey) {
		(keyCode==121||evt.shiftKey) ? Experior.editEvents.redo() :  Experior.editEvents.undo(); 
		evt.returnValue = false;
	}	
	// paste
	else if(keyCode==86 && evt.ctrlKey) {
		top.setTimeout( function(){ Experior.syntaxHighlight('paste');} );
	}		
	// backspace or delete
	else if(keyCode==46||keyCode==8) { 
		Experior.editEvents.lastPositions[Experior.editEvents.next()] = editor.innerHTML;
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
	newdiv.innerHTML = "<h3>Methods</h3>";
	newdiv.setAttribute('id',divIdName); 
	newdiv.style.width = "135px";
	newdiv.style.overflow = "auto";
	newdiv.style.left = "5px";
	newdiv.style.height = screen.height - 310 + 'px';
	newdiv.style.top = "120px"; 
	newdiv.style.position = "fixed";

	newdiv.style.textDecoration = "none";		

	for( var i = 0; i < methodsInDiv.length; i++ ) {	
		methodsInDiv[i].trim;

		metodestring += "<a style='margin-bottom:5px; display:block; text-decoration: none;' href=javascript:void(0) onclick=insertMethod(" + i + ")>" + methodsInDiv[i] + "</a>";		
	}
	newdiv.innerHTML += metodestring;	
},

/*
 * Updates the div with available methods if there has been a change to the classname
 * in the test.
 */
updateMethodsDiv : function() {
	var metodestring = "";	

	for( var i = 0; i < lines.length; i++ ) {
		lines[i].trim;

		metodestring += "<a style='margin-bottom:5px; display:block; " +
		"text-decoration: none;' href=javascript:void(0)" +
		"onclick=insertMethod(" + i + ")>" + lines[i] + "</a>";
	}
	parent.document.getElementById("methodsDiv").innerHTML = "";
	parent.document.getElementById("methodsDiv").innerHTML = "<h3>Methods</h3>" + metodestring;

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
		mid = code.indexOf(caret);
		if(mid-2000<0) {ini=0;end=4000;}
		else if(mid+2000>code.length) {ini=code.length-4000;end=code.length;}
		else {ini=mid-2000;end=mid+2000;}
		code = code.substring(ini,end);
		return code.substring(code.indexOf('<P>'),code.lastIndexOf('</P>')+4);
	}
},

/*
 * Highlights the correct text; methodnames (if the name is contained in methods2),
 * comments and keywords.
 */
highlightDocument : function(flag, methods2) {

	var newclassName = Experior.getText().match( "(\\!\\|\\-?)[\\w|\\.]+(\\-?\\|)");
	className = newclassName[0];

	if(methods2 != null) {		
		methods = methods2;		
	} 

	if(methods.length == 0) {
		lines = new Array();
	}
	else {
		lines = methods.split('\n');
		lines.pop();
	}

	if(flag!='init') document.selection.createRange().text = caret;
	o = editor.innerHTML;

	o = o.replace(/<P>/g,'\n');
	o = o.replace(/<\/P>/g,'\r');
	o = o.replace(/<.*?>/g,'');
	o = '<PRE><P>'+o+'</P></PRE>';
	o = o.replace(/\n\r/g,'<P></P>');
	o = o.replace(/\n/g,'<P>');
	o = o.replace(/\r/g,'<\/P>');
	o = o.replace(/<P>(<P>)+/,'<P>');
	o = o.replace(/<\/P>(<\/P>)+/,'</P>');
	o = o.replace(/<P><\/P>/g,'<P><BR/></P>');
	x = z = this.splitLargeTests(o,flag);
	x = x.replace(/\n/g,'<br>');
	x = x.replace(/&nbsp;/g, '&nbsp;');

	if(arguments[1]&&arguments[2]) x = x.replace(arguments[1],arguments[2]);
	var sRegExInput;
	for(i=0;i<FitNesse.syntax.length;i++) 
		x = x.replace(FitNesse.syntax[i].input,FitNesse.syntax[i].output);

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

	editor.innerHTML = this.editEvents.lastPositions[this.editEvents.next()] = (flag=='scroll') ? x : o.replace(z,x);
	if(flag!='init') this.findString();
},

//Finds the string at caret position
findString : function() {
	range = self.document.body.createTextRange();
	if(range.findText(caret)) {
		range.select();
		range.text = '';
	}
},

/*
 * Aligns the pipe automatically to the pipes position on the previous line. The
 * function is called when the key | is pressed.
 */
align : function() {	
	var range = document.selection.createRange();

	var caret = document.selection.createRange();
	var range = document.selection.createRange();

	caret.moveStart( 'character', -this.getText().length );

	var text = Experior.getCodeFromCaret( caret.htmlText );

	var linjearray = text.split("\n");

	var currentline = linjearray[linjearray.length-1].split("|");
	if (linjearray.length > 0) 
		var previousline = linjearray[linjearray.length-2].split("|");

	var nbspstring = "";

	for (var i = 0; i < (previousline[currentline.length-1].replace(/&nbsp;/gi,' ').length)-(currentline[currentline.length-1].length); i++)
		nbspstring += "&nbsp;";

	range.pasteHTML( nbspstring  );
},


/*
 * Inserts the method with the index as specified id in the array lines, at the
 * caret position.
 */
insertMethod : function( id ) {	
	document.selection.clear();
	var range = document.selection.createRange();

	range.pasteHTML( "!|" + lines[id] + "|"  );
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

/*
 * Checks if the class-name is changed anywhere in the test. If it is, 
 * the method loadXMLString is called.
 */
checkClassName : function( url ) {	

	var text = Experior.getText();

	var newclassName = text.match( "(\\!\\|\\-?)[\\w|\\.]+(\\-?\\|)");

	if( className != newclassName[0] ) 	{		
		className = newclassName[0];
		firstline = className;
		Experior.loadXMLString( url );
	}	

},

/*
 * Gets the hostname and portnumber, and performs a XMLHttpRequest to the server.
 * Parses the returned JSON-object and puts the content into the global array methods.
 */
loadXMLString : function( url) {

	var firstline = Experior.getText().split("\n");

	var host = window.location.hostname;
	var port = window.location.port;
	var url = "http://" + host + ":" + port + "/FrontPage?responder=Commands&var=" + className;

	httpRequest=null;
	if(window.XMLHttpRequest) {

		httpRequest=new XMLHttpRequest();
	}
	else if(window.ActiveXObject){
		// older browsers
		httpRequest=new ActiveXObject("Microsoft.XMLHTTP");
	}
	if(httpRequest!=null) {

		httpRequest.onreadystatechange= function()
		{	

			if (httpRequest.readyState==4) {				
				if (httpRequest.status == 404) {
					alert('Requestet URL is not found');
				}
				else if (httpRequest.status == 403) {
					alert('Access Denied');
				}
				else if (httpRequest.status==200) {   

					methods = httpRequest.getResponseHeader("json");

					var methodsarray = eval('('+ methods +')');

					methods = methodsarray.join("\n") + "\n";

					Experior.highlightDocument();
					Experior.updateMethodsDiv();
				}
				else {
					alert("Problem retrieving XML data:" + httpRequest.statusText);
				}
			}			
		}

		httpRequest.open("GET",url,true);

		httpRequest.send(null);
	}
	else {
		alert("Your browser does not support XMLHTTP.");
	}
},

//Insert the text in the parameter code at the caret position.
insertCode : function(code,replaceCursorBefore) {
	var start = '';
	var end = '';

	if(replaceCursorBefore) { 
		end = code; 
	}
	else { start = code;
	}

	if(typeof document.selection != 'undefined') {
		var range = document.selection.createRange();
		range.text = start + end;
		range = document.selection.createRange();
		range.move('character', -end.length);
		range.select();	
	}	
},

/*
 * Replaces all HTML-tags. Parameter code is text from charet and up,
 */
getCodeFromCaret : function( code ) {

	code = code.replace(/<br>/g,'\n');
	code = code.replace(/<p>/i,' '); 
	code = code.replace(/<p>/gi,'\n');
	code = code.replace(/\u2009/g,'');
	code = code.replace(/<.*?>/g,'');
	code = code.replace(/&lt;/g,'<');
	code = code.replace(/&gt;/g,'>');
	code = code.replace(/&amp;/gi,'&');
	return code;
},

/*
 * Gets and returns all text from the editor. Removes the specified HTML-tags from
 * the text before it is returned
 */	
getText : function() {
	var code = editor.innerHTML;
	code = code.replace(/<br>/g,'\n');
	code = code.replace(/<p>/i,'');
	code = code.replace(/<p>/gi,'\n');
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
	code = code.replace(/&nbsp;/gi, ' ');
	code = code.replace(/&/gi,'&amp;');		
	code = code.replace(/</g,'&lt;');
	code = code.replace(/>/g,'&gt;');
	editor.innerHTML = '<pre>'+code+'</pre>';
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

	for( var i=0; i < linearray.length-1; i++ ) { // Iterates through each line

		// ! on the line
		if( linearray[i][0].search(/\!/) != -1  ) {
			template = new Array();

			for( var j = 0; j < linearray[i].length-1; j++ ) {
				template[j] = linearray[i][j].length;
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
			if( i > 0 && linearray[i-1][0].search(/\!/) != -1 ) {				
				for( var k = 1; k < linearray[i].length-1; k++ ) {			 
					if( linearray[i][k].search(/\!\d/) == -1 ) {
						template[k] = linearray[i][k].length;
					}
				}
			}

			// next line is empty or next line contains a pipe
			if( linearray[i+1].length == 1  || linearray[i+1][0].search(/\!/) != -1 )
			{
				for( var l = tablestart; l <= i; l++ )
				{
					for( var m = 0; m < linearray[l].length-1; m++ )
					{		
						var nbspstring = "";
						if( linearray[l][0].search(/\!/) == -1 )
						{
							for( var n = linearray[l][m].length; n < template[m]; n++)
							{
								nbspstring += "&nbsp;";					 
							}
						}
						linearray[l][m] += nbspstring + "|";
					}
				}				
			}
		}
	}

	for( var i = 0; i < linearray.length; i++ )
	{
		for( var j = 0; j < linearray[i].length; j++ )
			output += linearray[i][j];

		if( i < linearray.length-1)
			output += "\n";
	}
	this.setText( output );
},

//undo the last user action
editEvents : {
	position : -1,
	lastPositions : [],

	undo : function() {
	if(editor.innerHTML.indexOf(caret)==-1){
		document.selection.createRange().text = caret;
		this.lastPositions[this.position] = editor.innerHTML;
	}
	this.position--;
	if(typeof(this.lastPositions[this.position])=='undefined') this.position++;
	editor.innerHTML = this.lastPositions[this.position];
	Experior.findString();
},

//redo the last user action
redo : function() {
	this.position++;
	if(typeof(this.lastPositions[this.position])=='undefined') this.position--;
	editor.innerHTML = this.lastPositions[this.position];
	Experior.findString();
},

//backspace or delete
next : function() {
	if(this.position>20) this.lastPositions[this.position-21] = undefined;
	return ++this.position;
}
}
}
FitNesse={};
window.attachEvent('onload', function() { Experior.initialize('new');});

FitNesse.syntax = [
                   { input : /\b(reject|show|check)\b/g, output : '<b>$1</b>' }, // FitNesse keywords
                   { input : /([^:]|^)\#(.*?)(<br|<\/P)/g, output : '$1<span class=disabled>#$2</span>$3' }, // !3
                   { input : /([^:]|^)\!3(.*?)(<br|<\/P)/g, output : '$1!3<span class=comment3>$2</span>$3' }, // !3
                   { input : /([^:]|^)\!2(.*?)(<br|<\/P)/g, output : '$1!2<span class=comment2>$2</span>$3' }, // !2
                   { input : /([^:]|^)\!1(.*?)(<br|<\/P)/g, output : '$1!1<span class=comment1>$2</span>$3' }, // !1
                   { input : /\{{3}(.*?)\}{3}/gim, output : '<span class=disabled>{{{$1}}}</span>' } // outcommented {{{ }}}
                   ]