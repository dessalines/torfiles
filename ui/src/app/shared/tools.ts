export class Tools {

	static getParameterByName(name, url) {
		var match = RegExp('[?&;]' + name + '=([^&;]*)').exec(url);
		return match && decodeURIComponent(match[1].replace(/\+/g, ' '));
	}

	static generateMagnetLink(infoHash: string, name: string, index?: string): string {

		let link = 'magnet:?xt=urn:btih:' + infoHash +
			'&dn=' + encodeURIComponent(name) +
			'&tr=udp%3A%2F%2Ftracker.leechers-paradise.org%3A6969&tr=udp%3A%2F%2Fzer0day.ch%3A1337&tr=udp%3A%2F%2Fopen.demonii.com%3A1337&tr=udp%3A%2F%2Ftracker.coppersurfer.tk%3A6969&tr=udp%3A%2F%2Fexodus.desync.com%3A6969&tr=udp%3A%2F%2Feddie4.nl%3A6969&tr=udp%3A%2F%2Ftracker.pirateparty.gr%3A6969&tr=udp%3A%2F%2Fopentrackr.org%3A1337&tr=udp%3A%2F%2Ftracker.zer0day.to%3A1337';
		if (index) {
			link += '&so=' + index;
		}
		return link;
	}

	static getFileName(path: string): string {
		let lines = path.split('/');
		let out: string = lines[0];

		for (let i = 1; i < lines.length; i++) {
			let tabs = Array(i + 1).join('  ');
			out += '\n' + tabs + '└─ ' + lines[i];
		}

		return out;

	}
	static getTorrentName(path: string): string {
		return path.split('/')[0];
	}
}


