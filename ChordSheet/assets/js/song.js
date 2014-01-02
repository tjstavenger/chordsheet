$(document).ready(function () {
	var texts = $('.textWidth');
	var maxWidth = 0;
	var maxWidthText;
	
	if (texts.length > 0) {
		for (var i = 0; i < texts.length; i++) {
			var width = texts.eq(i).width();
			
			if (width > maxWidth) {
				maxWidth = width;
				maxWidthText = texts.eq(i);
			}
		}
		
		var body = $('body');
		var lastLine = body.children().last();
		var fullWidth = window.innerWidth - parseInt(body.css('margin-left')) - parseInt(body.css('margin-right'));
		var halfWidth = (fullWidth - parseInt(body.css('-webkit-column-gap'))) / 2;
		var height = window.innerHeight - parseInt(body.css('margin-top')) - parseInt(body.css('margin-bottom'));
		var htmlHeight = $(document).height();
					
		if (fullWidth > height && halfWidth >= maxWidth && htmlHeight <= (height * 2)) {
			body.css('-webkit-column-count', 2);
			body.css('column-count', 2);		
		} else {
			body.css('-webkit-column-count', '');
			body.css('column-count', '');
		}
		
		autoZoom(body, lastLine, fullWidth, height, maxWidthText);
		
		if (lastLine.position().left < halfWidth) {
			body.css('-webkit-column-count', '');
			body.css('column-count', '');
			autoZoom(body, lastLine, fullWidth, height, maxWidthText);
		}
	}
});

function autoZoom(body, lastLine, fullWidth, height, maxWidthText) {
	var previousHeight;
	var fontSize = parseInt(body.css('font-size'));
		
	do {
		previousHeight = maxWidthText.height();
		fontSize++;
		body.css('font-size', fontSize);
	} while (lastLine.position().left < fullWidth && lastLine.position().top < height && maxWidthText.height() < (previousHeight * 2));
	
	body.css('font-size', fontSize - 1);
}