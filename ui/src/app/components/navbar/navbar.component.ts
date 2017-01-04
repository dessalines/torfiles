import { Component, OnInit } from '@angular/core';
import { Router, ActivatedRoute, Params, NavigationEnd } from '@angular/router';
import { Subject } from 'rxjs/Subject';
import 'rxjs/add/operator/debounceTime';
import 'rxjs/add/operator/distinctUntilChanged';

import { Tools } from '../../shared';

@Component({
	selector: 'app-navbar',
	templateUrl: './navbar.component.html',
	styleUrls: ['./navbar.component.scss']
})
export class NavbarComponent implements OnInit {

	private collapseNavbar: boolean = false;

	private searchTerm: string = '';
	private searchChanged: Subject<string> = new Subject<string>();
	private loading: boolean = false;

	constructor(private route: ActivatedRoute,
		private router: Router) {
		this.setupSearch();
	}

	ngOnInit() {
		// This is necessary because this needs to pick up when a taxonomyUuid is set as a filter
		this.router.events.subscribe(event => {
			if (event instanceof NavigationEnd) {
				this.setValuesByUrlEvent(event);
			}
		});
	}

	private setValuesByUrlEvent(event) {
		let searchTerm = Tools.getParameterByName('searchTerm', event.url);

		if (searchTerm !== null) {
			this.searchTerm = searchTerm;
		}
	}

	private setupSearch() {
		this.searchChanged
			.debounceTime(300) // wait 300ms after the last event before emitting last event
			.distinctUntilChanged() // only emit if value is different from previous value
			.subscribe(st => {
				this.searchTerm = st;
				this.search();
				this.loading = false;
			});
	}

	private search() {
		this.router.navigate(['/search', {searchTerm: this.searchTerm}]);
	}

	private newSearch(event) {
		this.loading = true;
		this.searchChanged.next(event);
	}

	toggleCollapseNavbar() {
		this.collapseNavbar = !this.collapseNavbar;
	}

}
