/*
 * This code is inspired and modeled on CodePress, http://sourceforge.net/projects/codepress
 * 
 * CodePress is released under LGPL. Read the full licence: 
 * http://www.opensource.org/licenses/lgpl-license.php
 * 
 * This program is free software; you can redistribute it and/or modify it under the terms of the 
 * GNU Lesser General Public License as published by the Free Software Foundation.
 * 
 *  
 */
var experior;
var hidden;

Experior = function(textarea, hiddenField) {

	experior = document.createElement('iframe');
	experior.textarea = textarea;

	hidden = hiddenField;		
	experior.textarea.disabled = true;
	experior.textarea.style.overflow = 'hidden';
	experior.textarea.style.overflow = 'auto';

	experior.style.height = screen.height - 330 + 'px';
	experior.style.width = screen.width - 170 + 'px';
	experior.style.border = '1px solid gray';
	experior.style.visibility = 'hidden';
	experior.style.position = 'absolute';

	experior.frameBorder = 0;
	experior.options = experior.textarea.className;
	
	/*
	 * Initializes the editor. Makes the textarea invisible and call relevant methods
	 * inside the editor.
	 */
	experior.initialize = function() {

		experior.editor = experior.contentWindow.Experior;
		experior.contentWindow.focus();
		experior.editor.body = experior.contentWindow.document.getElementsByTagName('body')[0];

		experior.textarea.style.display = 'none';
		experior.style.position = 'static';
		experior.style.visibility = 'visible';
		experior.style.display = 'inline';

		experior.editor.alignAllPipesOnPageLoad( experior.textarea.value);
		experior.editor.highlightDocument('init', hidden.value);				

		var methods = hidden.value.split("\n");
		experior.editor.createMethodsDiv( methods);
	}

	// Puts the editor in edit-mode.
	experior.edit = function( textarea,language ) {

		if( !experior.textarea.disabled )
			return;

		experior.src = Experior.path+'experior.html?';

		if( experior.attachEvent ) 
			experior.attachEvent( 'onload',experior.initialize );
		else
			experior.addEventListener( 'load',experior.initialize, false );
		
	}
	
	// Puts the text in the variable code inside the editor
	experior.setText = function( code ) {

		if( experior.textarea.disabled )
			experior.editor.setText( code );
		else
			experior.textarea.value = code;
	}
	
	// Gets the text from inside the editor
	experior.getText = function() {

		if( experior.textarea.disabled )
			return experior.editor.getText();
		else
			return experior.textarea.value;
	}

	// Set the text inside the editor if it is not disabled.
	experior.setUpEditor = function() {		
		if(experior.textarea.disabled) {
			experior.textarea.value = getText();
			experior.textarea.disabled = false;
			experior.style.display = 'none';
			experior.textarea.style.display = 'inline';
		}

		else {
			experior.textarea.disabled = true;
			experior.setText(experior.textarea.value);
			experior.editor.highlightDocument('init');
			experior.style.display = 'inline';
			experior.textarea.style.display = 'none';
		}
	}

	experior.edit();
	return experior;
}

// Adds necessary scriptfiles and creates a new Experior object.
Experior.run = function() {
	s = document.getElementsByTagName('script');
	
	for(var i=0,n=s.length;i<n;i++) {
		if(s[i].src.match('experior.js')) {
			Experior.path = s[i].src.replace('experior.js','');
		}
	}

	var textarea = document.getElementById('experior');
	var hidden = document.getElementById('hiddenfield');

	id = textarea.id;
	eval(id+' = new Experior(textarea, hidden)');
	textarea.parentNode.insertBefore(eval(id), textarea);	
}

if(window.attachEvent) {
	window.attachEvent('onload',Experior.run);
}
else window.addEventListener('DOMContentLoaded',Experior.run,false);

// Moves text from Experiors textarea to the hidden field.
function moveText() {
	hidden.value = experior.getText();
}

// Moves text from Experiors textarea to the hidden field.
function saveAndExit() {
	var form = document.getElementById( "hiddenfieldform" );
	hidden.value = experior.getText(form);
}

// Align all pipes in the document if the button Align is clicked.
function alignClick() {	
	experior.editor.alignAllPipesOnPageLoad( experior.getText() );
	experior.editor.highlightDocument('init', hidden.value);
}

// Moves text from the hidden field to Experiors textarea.
function moveTextUp() {	
	experior.setText( hidden.value );
}

// Gets all the methods names in the class, and insert the chosen method.
function insertMethod(method) {
	var methods = hidden.value.split("\n");
	experior.editor.insertMethod(method);
	experior.contentWindow.focus();
}

// Checks if the classname has changed
function checkClassName() {
	var url = window.location.href;

	experior.editor.checkClassName( url );
}