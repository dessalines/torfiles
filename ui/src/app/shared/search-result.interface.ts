export interface SearchResult {
	id: number;
	info_hash: string;
	path: string;
	index_: number;
	created: number;
	seeders: number;
	leechers: number;
	size_bytes: number;
}