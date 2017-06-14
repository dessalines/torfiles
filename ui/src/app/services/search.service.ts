import { Injectable } from '@angular/core';
import { Observable } from 'rxjs/Observable';
import 'rxjs/add/operator/map';
import 'rxjs/add/operator/catch';
import 'rxjs/add/observable/throw';
import { Http, Response } from '@angular/http';
import { Headers, RequestOptions } from '@angular/http';
import { environment } from '../../environments/environment';

import {SearchResults} from '../shared/search-results.interface';

@Injectable()
export class SearchService {


	private searchUrl(q: string,
		limit,
		page: number
	): string {

        let url: string = environment.endpoint + 'search?' + 
			'limit=' + limit +
			'&page=' + page;

        if (q) {
            url += '&q=' + q;
        }

		return url;

	}

	constructor(private http: Http) { }

	getSearchResults(q: string = '',
		limit: number = 25,
		page: number = 1): Observable<SearchResults> {

		return this.http.get(this.searchUrl(q, limit, page))
			.map(r => r.json())
			.catch(this.handleError);
	}

	private handleError(error: any) {
		// We'd also dig deeper into the error to get a better message
		let errMsg = error._body;

		return Observable.throw(errMsg);
	}

}
