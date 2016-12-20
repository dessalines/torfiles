export class Tools { 

	static getParameterByName(name, url) {
		var match = RegExp('[?&;]' + name + '=([^&;]*)').exec(url);
		return match && decodeURIComponent(match[1].replace(/\+/g, ' '));
	}
}


