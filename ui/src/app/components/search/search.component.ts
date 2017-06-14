import { Component, OnInit } from '@angular/core';
import { Router, ActivatedRoute, Params } from '@angular/router';

import { SearchService } from '../../services';
import { DomSanitizer, SafeHtml } from '@angular/platform-browser';
import { environment } from '../../../environments/environment';
import { Subscription } from 'rxjs/Subscription';

import { Tools, SearchResults } from '../../shared';

@Component({
	selector: 'app-search',
	templateUrl: './search.component.html',
	styleUrls: ['./search.component.scss']
})
export class SearchComponent implements OnInit {

	public searchTerm: string = '';

	public rows: Array<any> = [];

	public sorting: any = {
		'path': '',
		'size_bytes': '',
		'age': '',
		'peers': ''
	};

	public searchSub: Subscription;

	public page: number = 1;
	public limit: number = 25;
	public maxPaginators: number = 5;
	public length: number = 1;
	public numPages: number;
	public loading: boolean = false;

	constructor(private route: ActivatedRoute,
		private router: Router,
		private sanitizer: DomSanitizer,
		private searchService: SearchService) {

	}

	ngOnInit() {

		this.route.params.subscribe(params => {

			this.setSearchParams(params);
			this.page = 1;
			this.onChangeTable();
		});

	}

	public setSearchParams(params: any) {
		this.searchTerm = (params['searchTerm']) ? params['searchTerm'] : '';
	}


	public onChangeTable(page: any = { page: this.page, limit: this.limit }): any {
		this.loading = true;
		this.rows = null;
		this.page = page.page;

		let orderBy: Array<string> = this.buildOrderByArray(this.sorting);

		// Stops the last search
		if (this.searchSub) {
			this.searchSub.unsubscribe();
		}

		this.searchSub = this.searchService.getSearchResults(this.searchTerm, this.limit, this.page, orderBy).subscribe(d => {
			this.rows = d.results;
			this.length = d.count;
			this.loading = false;
		});
	}


	public buildOrderByArray(sorting: any): Array<string> {
		let orderByArray: Array<string> = [];

		for (var key in sorting) {
			let val = sorting[key];
			if (val !== '') {
				orderByArray.push(key + '-' + val);
			}
		}

		return orderByArray;
	}

	public getSortingClass(column: string): string {
		let sort = this.sorting[column];
		let classes: string;
		switch (sort) {
			case 'asc':
				classes = 'pull-right fa fa-fw fa-caret-up';
				break;
			case 'desc':
				classes = 'pull-right fa fa-fw fa-caret-down';
				break;
			case '':
				classes = '';
				break;
		}

		return classes;
	}

	public toggleSort(column: string) {
		let sort = this.sorting[column];

		switch (sort) {
			case 'asc':
				this.sorting[column] = 'desc';
				break;
			case 'desc':
				this.sorting[column] = '';
				break;
			case '':
				this.sorting[column] = 'asc';
				break;
		}

		this.page = 1;
		this.onChangeTable();
	}

	public getFileName(path: string): string {
		let lines = path.split('/');
		let out: string = lines[0];

		for (let i = 1; i < lines.length; i++) {
			let tabs = Array(i + 1).join('  ');
			out += '\n' + tabs + '└─ ' + lines[i];
		}

		return out;

	}
	public getTorrentName(path: string): string {
		return path.split('/')[0];
	}

	public generateMagnetLink(name, infoHash, index) {
		return Tools.generateMagnetLink(name, infoHash, index);
	}
}

