export interface SearchResult {
	id: number;
	info_hash: string;
	path: string;
	index_: number;
	created: number;
	peers: number;
	size_bytes: number;
}