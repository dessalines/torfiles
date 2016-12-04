import { Component, OnInit } from '@angular/core';

import {SearchService} from './search.service';
import {DomSanitizer, SafeHtml} from '@angular/platform-browser';


@Component({
	selector: 'app-search',
	templateUrl: './search.component.html',
	styleUrls: ['./search.component.scss']
})
export class SearchComponent implements OnInit {

	private searchTerm: string = '';
	private rows: Array<any> = [];

	private sorting: any = {
		'name': '',
		'size_bytes': '',
		'age': '',
		'seeders': 'desc',
		'leechers': ''
	};

	public page: number = 1;
	public limit: number = 25;
	public maxPaginators: number = 5;
	public length: number = 1;
	public data: Array<any>;


	constructor(private searchService: SearchService,
		private sanitizer: DomSanitizer) {

	}

	public ngOnInit(): void {
		this.onChangeTable();
	}

	public onChangeTable(page: any = { page: this.page, limit: this.limit }): any {

		this.page = page.page;

		let orderBy: Array<string> = this.buildOrderByArray(this.sorting);

		this.searchService.getSearchResults(this.searchTerm, this.limit, this.page, orderBy).subscribe(d => {
			this.rows = d.results;
			this.length = d.count;
			console.log(this.rows);
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

		console.log(orderByArray);

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
}

