import { Component, OnInit } from '@angular/core';

import {SearchService} from './search.service';

@Component({
	selector: 'app-search',
	templateUrl: './search.component.html',
	styleUrls: ['./search.component.scss']
})
export class SearchComponent implements OnInit {
	public rows: Array<any> = [];
	public columns: Array<any> = [
		{ title: 'Name', name: 'name', sort: true},
		{ title: 'Size', name: 'size_bytes', sort: true},
		{ title: 'Age', name: 'age', sort: true },
		{ title: 'Seeders', name: 'seeders', sort: 'desc' },
		{ title: 'Leechers', name: 'leechers', sort: true }
	];
	public page: number = 1;
	public limit: number = 25;
	public maxPaginators: number = 5;
	public length: number = 1;
	public data: Array<any>;


	public config: any = {
		paging: true,
		sorting: { columns: this.columns },
		filtering: { filterString: '' },
		className: ['table-striped', 'table-bordered']
	};

	public constructor(private searchService: SearchService) {

	}

	public ngOnInit(): void {
		this.onChangeTable(this.config);
	}

	public changePage(page: any, data: Array<any> = this.data): Array<any> {
		// let start = (page.page - 1) * page.limit;
		// let end = page.limit > -1 ? (start + page.limit) : data.length;
		// return data.slice(start, end);
		return [];
	}

	public changeSort(data: any, config: any): any {
		// if (!config.sorting) {
		// 	return data;
		// }

		// let columns = this.config.sorting.columns || [];
		// let columnName: string = void 0;
		// let sort: string = void 0;

		// for (let i = 0; i < columns.length; i++) {
		// 	if (columns[i].sort !== '' && columns[i].sort !== false) {
		// 		columnName = columns[i].name;
		// 		sort = columns[i].sort;
		// 	}
		// }

		// if (!columnName) {
		// 	return data;
		// }

		// // simple sorting
		// return data.sort((previous: any, current: any) => {
		// 	if (previous[columnName] > current[columnName]) {
		// 		return sort === 'desc' ? -1 : 1;
		// 	} else if (previous[columnName] < current[columnName]) {
		// 		return sort === 'asc' ? -1 : 1;
		// 	}
		// 	return 0;
		// });
	}

	public changeFilter(data: any, config: any): any {
		// let filteredData: Array<any> = data;
		// this.columns.forEach((column: any) => {
		// 	if (column.filtering) {
		// 		filteredData = filteredData.filter((item: any) => {
		// 			return item[column.name].match(column.filtering.filterString);
		// 		});
		// 	}
		// });

		// if (!config.filtering) {
		// 	return filteredData;
		// }

		// if (config.filtering.columnName) {
		// 	return filteredData.filter((item: any) =>
		// 		item[config.filtering.columnName].match(this.config.filtering.filterString));
		// }

		// let tempArray: Array<any> = [];
		// filteredData.forEach((item: any) => {
		// 	let flag = false;
		// 	this.columns.forEach((column: any) => {
		// 		if (item[column.name].toString().match(this.config.filtering.filterString)) {
		// 			flag = true;
		// 		}
		// 	});
		// 	if (flag) {
		// 		tempArray.push(item);
		// 	}
		// });
		// filteredData = tempArray;

		// return filteredData;
	}

	public onChangeTable(config: any, page: any = { page: this.page, limit: this.limit }): any {
		// if (config.filtering) {
		// 	Object.assign(this.config.filtering, config.filtering);
		// }

		// if (config.sorting) {
		// 	Object.assign(this.config.sorting, config.sorting);
		// }

		// let filteredData = this.changeFilter(this.data, this.config);
		// let sortedData = this.changeSort(filteredData, this.config);
		// this.rows = page && config.paging ? this.changePage(page, sortedData) : sortedData;
		// this.length = sortedData.length;

		this.page = page.page;

		let q: string = config.filtering.filterString;

		let orderBy: Array<string> = this.buildOrderByArray(config);

		this.searchService.getSearchResults(q, this.limit, this.page, orderBy).subscribe(d => {
			this.rows = d.results;
			this.length = d.count;
		});
	}

	public onCellClick(data: any): any {
		
	}

	private buildOrderByArray(config: any): Array<string> {
		let orderByArray: Array<string> = [];

		for (let sortOption of config.sorting.columns) {
			if (typeof sortOption.sort === 'string' && sortOption.sort.length > 0) {
				orderByArray.push(sortOption.name + '-' + sortOption.sort);
			}
		}

		if (orderByArray.length == 0) {
			orderByArray = undefined;
		}

		return orderByArray;
	}
}

