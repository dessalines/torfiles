import { Component, OnInit } from '@angular/core';

import {SearchService} from '../../services';
import {DomSanitizer, SafeHtml} from '@angular/platform-browser';
import { environment } from '../../../environments/environment';

import { Subject } from 'rxjs/Subject';
import 'rxjs/add/operator/debounceTime';
import 'rxjs/add/operator/distinctUntilChanged';

@Component({
	selector: 'app-search',
	templateUrl: './search.component.html',
	styleUrls: ['./search.component.scss']
})
export class SearchComponent implements OnInit {

	private searchTerm: string = '';
	private searchChanged: Subject<string> = new Subject<string>();

	private rows: Array<any> = [];

	private sorting: any = {
		'name': '',
		'size_bytes': '',
		'age': '',
		'seeders': '',
		'peers': ''
	};

	public page: number = 1;
	public limit: number = 25;
	public maxPaginators: number = 5;
	public length: number = 1;
	public data: Array<any>;

	constructor(private searchService: SearchService,
		private sanitizer: DomSanitizer) {
		this.setupSearch();
	}

	public ngOnInit(): void {
		this.onChangeTable();
	}

	private setupSearch() {
		this.searchChanged
            .debounceTime(300) // wait 300ms after the last event before emitting last event
            .distinctUntilChanged() // only emit if value is different from previous value
            .subscribe(st => {
            	this.searchTerm = st;
            	this.onChangeTable();
            });
	}

	private newSearch(event) {
		this.page = 1;
		this.searchChanged.next(event);
	}

	public onChangeTable(page: any = { page: this.page, limit: this.limit }): any {

		this.page = page.page;

		let orderBy: Array<string> = this.buildOrderByArray(this.sorting);

		this.searchService.getSearchResults(this.searchTerm, this.limit, this.page, orderBy).subscribe(d => {
			this.rows = d.results;
			this.length = d.count;
		});
	}


	private buildOrderByArray(sorting: any): Array<string> {
		let orderByArray: Array<string> = [];

		for (var key in sorting) {
    		let val = sorting[key];
    		if (val !== '') {
    			orderByArray.push(key + '-' + val);
    		}
		}

		return orderByArray;
	}

	private getSortingClass(column: string): string {
		let sort = this.sorting[column];
		let classes: string;
		switch (sort) {
			case 'asc': 
				classes =  'pull-right fa fa-fw fa-caret-up';
				break;
			case 'desc': 
				classes =  'pull-right fa fa-fw fa-caret-down';
				break;
			case '':
				classes =  '';
				break;
		}

		return classes;
	}

	private toggleSort(column: string) {
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

	private getDownloadLink(infoHash: string) {
		return environment.endpoint + 'torrent_download/' + infoHash + '.torrent';
	}
}

