import { Component, OnInit } from '@angular/core';

@Component({
	selector: 'app-navbar',
	templateUrl: './navbar.component.html',
	styleUrls: ['./navbar.component.css']
})
export class NavbarComponent implements OnInit {

	private collapseNavbar: boolean = true;

	constructor() { }

	ngOnInit() {
	}

	toggleCollapseNavbar() {
		this.collapseNavbar = !this.collapseNavbar;
	}

}
