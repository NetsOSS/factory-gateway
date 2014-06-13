
// Array which defines which words to be highlighted, except for method-names.
FitNesse.syntax = [
	{ input : /\b(reject|show|check)\b/g, output : '<b>$1</b>' }, // FitNesse keywords
	{ input : /([^:]|^)\#(.*?)(<br|<\/P)/g, output : '$1<span class=disabled>#$2</span>$3' }, // !3
	{ input : /([^:]|^)\!3(.*?)(<br|<\/P)/g, output : '$1!3<span class=comment3>$2</span>$3' }, // !3
	{ input : /([^:]|^)\!2(.*?)(<br|<\/P)/g, output : '$1!2<span class=comment2>$2</span>$3' }, // !2
	{ input : /([^:]|^)\!1(.*?)(<br|<\/P)/g, output : '$1!1<span class=comment1>$2</span>$3' }, // !1
	{ input : /\{{3}(.*?)\}{3}/gim, output : '<span class=disabled>{{{$1}}}</span>' } // outcommented {{{ }}}
]