import {SearchResult} from './search-result.interface';

export interface SearchResults {
	results: Array<SearchResult>;
	count: number;
	page: number;
}